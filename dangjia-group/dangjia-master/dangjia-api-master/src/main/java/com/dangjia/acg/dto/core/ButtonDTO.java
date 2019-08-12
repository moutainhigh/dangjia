package com.dangjia.acg.dto.core;

import lombok.Data;

import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2018/11/6 0006
 * Time: 16:19
 */
@Data
public class ButtonDTO {
    private int state;//0无房产,1回访阶段未开工,2已开工,3已开工+有待回访阶段房子
    private int houseType;//装修的房子类型0：新房；1：老房
    private int drawings;//有无图纸0：无图纸；1：有图纸
    private Integer insuranceDay;//工人保险剩余天数
    private String houseId;
    private List<Task> taskList;
}
