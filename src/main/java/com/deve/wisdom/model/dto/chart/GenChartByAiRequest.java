//-*- coding =utf-8 -*-
//@Time : 2023/8/18
//@Author: 邓闽川
//@File  GenChartByAiRequest.java
//@software:IntelliJ IDEA
package com.deve.wisdom.model.dto.chart;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
@Data
public class GenChartByAiRequest implements Serializable {
    /**
     * 名称
     */
    private  String name;

    private  String goal;


    private String chartType;


    private static final long serialVersionUID = 1L;
}
