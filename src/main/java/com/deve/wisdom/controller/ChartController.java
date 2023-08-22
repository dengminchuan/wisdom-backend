package com.deve.wisdom.controller;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.deve.wisdom.constant.AiConstant;
import com.deve.wisdom.constant.CommonConstant;
import com.deve.wisdom.manager.AiManager;
import com.deve.wisdom.manager.RedisLimiterManager;
import com.deve.wisdom.model.dto.chart.*;
import com.deve.wisdom.model.entity.User;
import com.deve.wisdom.model.enums.ExecMessage;
import com.deve.wisdom.model.vo.BiResponse;
import com.deve.wisdom.service.UserService;
import com.deve.wisdom.utils.SqlUtils;
import com.google.gson.Gson;
import com.deve.wisdom.annotation.AuthCheck;
import com.deve.wisdom.common.BaseResponse;
import com.deve.wisdom.common.DeleteRequest;
import com.deve.wisdom.common.ErrorCode;
import com.deve.wisdom.common.ResultUtils;
import com.deve.wisdom.constant.UserConstant;
import com.deve.wisdom.exception.BusinessException;
import com.deve.wisdom.exception.ThrowUtils;
import com.deve.wisdom.model.entity.Chart;

import com.deve.wisdom.service.ChartService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

import static com.deve.wisdom.utils.ExcelUtils.excel2Csv;

/**
 * 帖子接口
 *
 * @author <a href="https://github.com/liyupi">devedmc</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@RestController
@RequestMapping("/chart")
@Slf4j
public class ChartController {

    @Resource
    private ChartService chartService;

    @Resource
    private UserService userService;

    @Resource
    private AiManager aiManager;

    @Resource
    private RedisLimiterManager redisLimiterManager;

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    private final static Gson GSON = new Gson();

    // region 增删改查

    /**
     * 创建
     *
     * @param chartAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addChart(@RequestBody ChartAddRequest chartAddRequest, HttpServletRequest request) {
        if (chartAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = new Chart();
        BeanUtils.copyProperties(chartAddRequest, chart);
        User loginUser = userService.getLoginUser(request);
        chart.setUserId(loginUser.getId());
        boolean result = chartService.save(chart);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newChartId = chart.getId();
        return ResultUtils.success(newChartId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteChart(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Chart oldChart = chartService.getById(id);
        ThrowUtils.throwIf(oldChart == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldChart.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = chartService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param chartUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateChart(@RequestBody ChartUpdateRequest chartUpdateRequest) {
        if (chartUpdateRequest == null || chartUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = new Chart();
        BeanUtils.copyProperties(chartUpdateRequest, chart);
        long id = chartUpdateRequest.getId();
        // 判断是否存在
        Chart oldChart = chartService.getById(id);
        ThrowUtils.throwIf(oldChart == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = chartService.updateById(chart);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<Chart> getChartById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = chartService.getById(id);
        if (chart == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(chart);
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param chartQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<Chart>> listChartByPage(@RequestBody ChartQueryRequest chartQueryRequest,
            HttpServletRequest request) {
        long current = chartQueryRequest.getCurrent();
        long size = chartQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Chart> chartPage = chartService.page(new Page<>(current, size),
                getQueryWrapper(chartQueryRequest));
        return ResultUtils.success(chartPage);
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param chartQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page")
    public BaseResponse<Page<Chart>> listMyChartByPage(@RequestBody ChartQueryRequest chartQueryRequest,
            HttpServletRequest request) {
        if (chartQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        chartQueryRequest.setUserId(loginUser.getId());
        long current = chartQueryRequest.getCurrent();
        long size = chartQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Chart> chartPage = chartService.page(new Page<>(current, size),
                getQueryWrapper(chartQueryRequest));
        return ResultUtils.success(chartPage);
    }



    /**
     * 编辑（用户）
     *
     * @param chartEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editChart(@RequestBody ChartEditRequest chartEditRequest, HttpServletRequest request) {
        if (chartEditRequest == null || chartEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = new Chart();
        BeanUtils.copyProperties(chartEditRequest, chart);
        User loginUser = userService.getLoginUser(request);
        long id = chartEditRequest.getId();
        // 判断是否存在
        Chart oldChart = chartService.getById(id);
        ThrowUtils.throwIf(oldChart == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldChart.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = chartService.updateById(chart);
        return ResultUtils.success(result);
    }


    private QueryWrapper<Chart> getQueryWrapper(ChartQueryRequest chartQueryRequest) {
        QueryWrapper<Chart> queryWrapper = new QueryWrapper<>();
        if (chartQueryRequest == null) {
            return queryWrapper;
        }
        String chartType = chartQueryRequest.getChartType();
        String name = chartQueryRequest.getName();
        String goal = chartQueryRequest.getGoal();
        Long id = chartQueryRequest.getId();
        Long userId = chartQueryRequest.getUserId();
        String sortOrder = chartQueryRequest.getSortOrder();
        String sortField = chartQueryRequest.getSortField();
        // 拼接查询条件
        queryWrapper.eq(id!=null&&id>0,"id",id);
        queryWrapper.like(ObjectUtils.isNotEmpty(name),"name",name);
        queryWrapper.ne(ObjectUtils.isNotEmpty(chartType), "chartType", chartType);
        queryWrapper.eq(ObjectUtils.isNotEmpty(goal), "goal", goal);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @PostMapping("/gen")
    public BaseResponse<BiResponse> genChartByAi(@RequestPart("file") MultipartFile multipartFile,
                                                 GenChartByAiRequest genChartByAiRequest, HttpServletRequest request) {
        String name = genChartByAiRequest.getName();
        String goal = genChartByAiRequest.getGoal();
        String chartType = genChartByAiRequest.getChartType();
        User user = userService.getLoginUser(request);
        redisLimiterManager.doRateLimite("genChartByAi_"+user.getId());
        boolean safe = checkFile(multipartFile);
        ThrowUtils.throwIf(!safe&&StringUtils.isBlank(goal),ErrorCode.PARAMS_ERROR,"目标为空");
        String goal0=goal;
        if(StringUtils.isNotBlank(chartType)){
            goal0+=",请使用"+chartType;
        }
        // 文件目录：根据业务、用户来划分
        String result = excel2Csv(multipartFile);
        StringBuilder userInputForAnalyse = new StringBuilder();
        userInputForAnalyse.append("分析目标:").append(goal0).append("\n");
        userInputForAnalyse.append("数据:").append("\n");
        userInputForAnalyse.append(result);
        String analyseResult=aiManager.doChat(AiConstant.ANALYSE_MODEL_ID,userInputForAnalyse.toString());
        String[] split = analyseResult.split("【【【【【");
        if(split.length>3){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"ai数据生成错误");
        }
        String genChart=split[1];
        String genResult=split[2].trim();
        Chart chart = new Chart();
        chart.setName(name);
        chart.setGoal(goal);
        chart.setChartData(result);
        chart.setChartType(chartType);
        chart.setGenChart(genChart);
        chart.setGenResult(analyseResult);
        chart.setUserId(user.getId());
        chart.setStatus("succeed");
        boolean saveResult = chartService.save(chart);
        ThrowUtils.throwIf(!saveResult,ErrorCode.SYSTEM_ERROR);
        BiResponse biResponse = new BiResponse();
        biResponse.setGenChart(genChart);
        biResponse.setGenResult(genResult);
        biResponse.setChartId(chart.getId());
        return ResultUtils.success(biResponse);
    }
    @PostMapping("/gen/async")
    public BaseResponse<BiResponse> genChartByAiAsync(@RequestPart("file") MultipartFile multipartFile,
                                             GenChartByAiRequest genChartByAiRequest, HttpServletRequest request) {
        String name = genChartByAiRequest.getName();
        String goal = genChartByAiRequest.getGoal();
        String chartType = genChartByAiRequest.getChartType();
        User user = userService.getLoginUser(request);
        redisLimiterManager.doRateLimite("genChartByAi_"+user.getId());
        //todo:校验文件:大小 内容 名称 后缀
        boolean safe = checkFile(multipartFile);
        ThrowUtils.throwIf(!safe&&StringUtils.isBlank(goal),ErrorCode.PARAMS_ERROR,"目标为空");
        String result = excel2Csv(multipartFile);
        Chart chart = new Chart();
        chart.setName(name);
        chart.setGoal(goal);
        chart.setChartType(chartType);
        chart.setChartData(result);
        chart.setStatus("wait");
        chart.setUserId(user.getId());
        log.info("<====insert chart id="+chart.getId()+" ===>");
        boolean saveResult1 = chartService.save(chart);
        log.info("<====insert chart id="+chart.getId()+" ===>");
        if(!saveResult1){
            handleChartUpdateError(chart.getId(), "保存图表失败");
        }
        CompletableFuture.runAsync(() -> {
            Chart updateChart = new Chart();
            updateChart.setId(chart.getId());
            updateChart.setStatus("running");
            boolean save = chartService.updateById(updateChart);
            if(!save){
                handleChartUpdateError(updateChart.getId(),"更新为运行状态失败");
            }
            //调用ai
            String goal0=goal;
            if(StringUtils.isNotBlank(chartType)){
                goal0+=",请使用"+chartType;
            }
            StringBuilder userInputForAnalyse = new StringBuilder();
            userInputForAnalyse.append("分析目标:").append(goal0).append("\n");
            userInputForAnalyse.append("数据:").append("\n");
            userInputForAnalyse.append(result);
            String analyseResult=aiManager.doChat(AiConstant.ANALYSE_MODEL_ID,userInputForAnalyse.toString());
            String[] split = analyseResult.split("【【【【【");
            if(split.length>3){
                throw new BusinessException(ErrorCode.SYSTEM_ERROR,"ai数据生成错误");
            }
            String genChart=split[1].trim();
            String genResult=split[2].trim();
            Chart updateChartAfterAi = new Chart();
            updateChartAfterAi.setGenChart(genChart);
            updateChartAfterAi.setGenResult(genResult);
            updateChartAfterAi.setId(chart.getId());
            updateChartAfterAi.setStatus("succeed");
            boolean saveResult = chartService.updateById(updateChartAfterAi);
            ThrowUtils.throwIf(!saveResult,ErrorCode.SYSTEM_ERROR);
        },threadPoolExecutor);
        BiResponse biResponse = new BiResponse();
        biResponse.setChartId(chart.getId());
        return ResultUtils.success(biResponse);
    }
    private void handleChartUpdateError(Long id,String message){
        Chart chart = new Chart();
        chart.setExecMessage(message);
        chart.setStatus("failed");
        chart.setId(id);
        boolean updateResult = chartService.updateById(chart);
        if(!updateResult){
            log.error("更新图表状态失败,图表id:"+id+message);
        }
    }
    private static final Long ONE_MB=10*1024*1024L;
    private boolean checkFile(MultipartFile multipartFile) {
        long size = multipartFile.getSize();
        if(size>ONE_MB){
            return false;
        }
        String originalFilename = multipartFile.getOriginalFilename();
        String suffix = FileUtil.getSuffix(originalFilename);
        if(suffix!="xlsx"||suffix!="xls"){
            return false;
        }
        return true;
    }

}
