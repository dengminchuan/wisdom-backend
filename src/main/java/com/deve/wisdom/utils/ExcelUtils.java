//-*- coding =utf-8 -*-
//@Time : 2023/8/18
//@Author: 邓闽川
//@File  ExcelUtils.java
//@software:IntelliJ IDEA
package com.deve.wisdom.utils;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * excel 工具类
 */
@Slf4j
public class ExcelUtils {
       public static  String excel2Csv(MultipartFile multipartFile)  {

           List<Map<Integer, String>> list = null;
           try {
               list = EasyExcel.read(multipartFile.getInputStream())
                       .excelType(ExcelTypeEnum.XLSX)
                       .sheet()
                       .headRowNumber(0)
                       .doReadSync();
           } catch (IOException e) {
               log.error("表格处理错误");
           }
           //转为csv
           StringBuilder stringBuilder = new StringBuilder();
           if (CollUtil.isEmpty(list)) {
               return "";
           }
           LinkedHashMap<Integer, String> headerMap = (LinkedHashMap<Integer, String>) list.get(0);
           List<String> headerList = headerMap.values().stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
           stringBuilder.append(StringUtils.join(headerList, ","));
           stringBuilder.append("\n");
           for (int i = 1; i < list.size(); i++) {
               LinkedHashMap<Integer, String> dataMap = (LinkedHashMap<Integer, String>) list.get(i);
               List<String> dataList = dataMap.values().stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
               stringBuilder.append(StringUtils.join(dataList, ","));
               stringBuilder.append("\n");
           }

           return stringBuilder.toString();
       }

    public static void main(String[] args) {
        System.out.println(excel2Csv(null));

    }
}
