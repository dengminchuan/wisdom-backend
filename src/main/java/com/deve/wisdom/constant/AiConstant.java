//-*- coding =utf-8 -*-
//@Time : 2023/8/18
//@Author: 邓闽川
//@File  AiConstarnt.java
//@software:IntelliJ IDEA
package com.deve.wisdom.constant;

public interface AiConstant {
    String PRIMARY_CODE_PROMOTE ="接下来我会按照以下格式给你提供内容:\n" +
            "分析需求:{数据的分析需求}\n" +
            "原始数据:{csv格式的原始数据，用 , 作为分隔符}\n" +
            "请按照这两部分内容，在不生成其它内容如注释的情况下生成以下内容:\n" +
            "{Echarts v5的option配置对象js代码}\n";
    String PRIMARY_ANALYSE_PROMOTE="接下来我会按照以下格式给你提供内容:\n" +
            "分析需求:{数据的分析需求}\n" +
            "原始数据:{csv格式的原始数据，用 , 作为分隔符}\n" +
            "请按照这两部分内容，在不生成其它内容如注释的情况下生成以下内容:\n" +
            "{明确的的数据分析结论,越详细越好}\n";
    Long ECHARTS_MODEL_ID =1651468696054513666L;

    Long ANALYSE_MODEL_ID=1659171950288818178L;
}
