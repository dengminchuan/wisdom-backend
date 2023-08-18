package com.deve.wisdom.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.deve.wisdom.model.entity.Chart;
import com.deve.wisdom.service.ChartService;
import com.deve.wisdom.mapper.ChartMapper;
import org.springframework.stereotype.Service;

/**
* @author lv jiang er hao
* @description 针对表【chart(图表)】的数据库操作Service实现
* @createDate 2023-08-18 12:51:58
*/
@Service
public class ChartServiceImpl extends ServiceImpl<ChartMapper, Chart>
    implements ChartService{

}




