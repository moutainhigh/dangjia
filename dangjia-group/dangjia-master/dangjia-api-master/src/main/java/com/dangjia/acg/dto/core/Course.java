package com.dangjia.acg.dto.core;

import lombok.Data;

/**
 * author: Ronalcheng
 * Date: 2018/11/6 0006
 * Time: 17:56
 */
@Data
public class Course {
    private String nameA;//工序名
    private String nameB;//节点名
    private String nameC;//工序详情
    private String url;//详情url
    private int total;//总共节点
    private int rank;//第几
    private String color;//颜色
    private int state;//列表状态 0代表不显示红点，1显示红点
}
