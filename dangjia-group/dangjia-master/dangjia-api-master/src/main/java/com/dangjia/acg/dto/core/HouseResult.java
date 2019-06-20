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
    private String  buildStage;//装修状态
    private int task;//其它房产待处理任务
    private String state;//列表状态
    private List<NodeDTO> courseList;//其他工序节点
    private NodeDTO designList;//设计节点
    private NodeDTO actuaryList;//精算节点
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
