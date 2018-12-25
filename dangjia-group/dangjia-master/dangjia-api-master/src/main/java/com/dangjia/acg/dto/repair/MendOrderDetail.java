package com.dangjia.acg.dto.repair;

import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * author: Ronalcheng
 * Date: 2018/12/24 0024
 * Time: 16:45
 */
@Data
public class MendOrderDetail {
    private String number;//单号
    private Integer type;
    private Integer materialOrderState;
    private Integer workerOrderState;
    private Integer materialBackState;
    private Integer workerBackState;
    private Double totalAmount;

    private Date createDate;
    private Date modifyDate;

    private List<Map<String,Object>> mapList;
}
