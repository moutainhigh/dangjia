package com.dangjia.acg.dto.core;

import lombok.Data;

import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2018/11/6 0006
 * Time: 18:05
 */
@Data
public class HouseResult {
    private String houseId;
    private String houseName;
    private int again;//几套房
    private int task;//其它房产待处理任务
    private String state;//列表状态
    private List<Course> courseList;
}
