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

    //房子id
    private String houseId;

    //1-下单后（销售阶段） 2-下单后（销售接单） 3-下单后（设计阶段）4-下单后（精算阶段）5-下单后(施工阶段)
    private Integer houseType;


    private List<ListMapBean> bigList;//菜单


    @Data
    public static class ListMapBean {
        private String image;
        private String name;
        private String url;
        private String apiUrl;//异步加载图标状态
        private int type;//0:跳转URL，1:获取定位后跳转URL，2:量房，3：传平面图，4：传施工图
        private int state;//0无 1有点
    }

}
