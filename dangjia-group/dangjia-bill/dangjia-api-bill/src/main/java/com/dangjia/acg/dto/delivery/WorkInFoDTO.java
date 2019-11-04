package com.dangjia.acg.dto.delivery;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * author: ljl
 * 我要装修首页DTO
 * Date: 2019.10-30
 * Time: 下午 4:34
 */
@Data
public class WorkInFoDTO {

    //当前房子装修状态
    private Integer type;

    //订单状态
    private List<Map<String,Object>> mapList;

    //客服明细
    private Map<String,Object> map;

    //工序明细
//    private WorkNodeListDTO workList;

    private List<Object> workList;

    //今日播报
    private HouseFlowInfoDTO houseFlowInfoDTO;

    //订单状态
    private Map<String,Object> orderMap;

    //房子名称
    private String houseName;

    //房子状态
    private Integer houseType;

}
