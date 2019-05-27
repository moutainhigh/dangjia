package com.dangjia.acg.util;


import com.dangjia.acg.modle.house.House;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HouseUtil {

    /**
     * 。。。。。。。。。。。。。。。。⦧--4
     * 精算状态：0---5---1--- -1 ---2---3
     *
     * @return
     */
    public static Map<String, Object> getBudgetDatas(House house) {
        Map<String, Object> dataMap = new HashMap<>();
        List<Map<String, Object>> dataList = new ArrayList<>();
        dataMap.put("total", 4);
        String[] nameBs = {"开始精算", "业主支付", "精算阶段", "完成"};
        switch (house.getBudgetOk()) {
            case 0:
                dataMap.put("rank", 1);
                dataMap.put("nameB", "待抢单");
                break;
            case 5:
                dataMap.put("rank", 2);
                dataMap.put("nameB", "待业主支付");
                break;
            case 1:
                if (house.getDecorationType() == 2 && house.getDesignerOk() != 3) {
                    dataMap.put("rank", 3);
                    dataMap.put("nameB", "待上传设计设计图");
                } else {
                    dataMap.put("rank", 3);
                    dataMap.put("nameB", "精算中");
                }
                break;
            case -1:
                dataMap.put("rank", 3);
                dataMap.put("nameB", "未发送精算");
                break;
            case 2:
                dataMap.put("rank", 3);
                dataMap.put("nameB", "待审核精算");
                break;
            case 4:
                dataMap.put("rank", 3);
                dataMap.put("nameB", "修改精算");
                break;
            case 3:
                dataMap.put("rank", 4);
                dataMap.put("nameB", "完成");
                break;
        }
        setMapDatas(dataMap, dataList, nameBs);
        return dataMap;
    }

    private static void setMapDatas(Map<String, Object> dataMap, List<Map<String, Object>> dataList, String[] nameBs) {
        int rank = (Integer) dataMap.get("rank");
        int total = (Integer) dataMap.get("total");
        for (int i = 0; i < nameBs.length; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", nameBs[i]);
            map.put("begin", i < rank);
            String describe = "";
            if (rank != total && i + 1 == rank) {
                describe = (String) dataMap.get("nameB");
            }
            map.put("describe", describe);
            dataList.add(map);
        }
        dataMap.put("dataList", dataList);
    }


    /**
     * 获取设计进度节点
     * 发送设计图业主
     * 设计状态:
     * 0=未确定设计师
     * 4=设计待抢单
     * 1=已支付-设计师待量房
     * 9=量房图确认，设计师待发平面图
     * 5=平面图发给业主
     * 6=平面图审核不通过
     * 7=通过平面图待发施工图
     * 2=已发给业主施工图
     * 8=施工图片审核不通过
     * 3=施工图(全部图)审核通过
     * 。。。。。。。。。。。。。。。。。⦧--6。。⦧--8
     * 远程设计流程：0---4---1---9---5---7---2---3
     *
     * 。。。。。。。。。。。。⦧--6。。⦧--8
     * 自带设计流程：0---1---5---7---2---3
     *
     * @return
     */
    public static Map<String, Object> getDesignDatas(House house) {
        Map<String, Object> dataMap = new HashMap<>();
        List<Map<String, Object>> dataList = new ArrayList<>();
        String[] nameBs;
        if (house.getDecorationType() == 2) {//自带设计流程
            dataMap.put("total", 4);
            nameBs = new String[]{"开始自带设计", "平面图阶段", "施工图阶段", "完成"};
            switch (house.getDesignerOk()) {
                case 0://0未确定设计师
                    dataMap.put("rank", 1);
                    dataMap.put("nameB", "待抢单");
                    break;
                case 4://4设计待抢单
                case 1://1已支付-设计师待量房
                case 9://9量房图发给业主
                    dataMap.put("rank", 2);
                    dataMap.put("nameB", "待上传平面图");
                    break;
                case 5://5平面图发给业主 （发给了业主）
                    dataMap.put("rank", 2);
                    dataMap.put("nameB", "待审核平面图");
                    break;
                case 6://6平面图审核不通过（NG，可编辑平面图）
                    dataMap.put("rank", 2);
                    dataMap.put("nameB", "待修改平面图");
                    break;
                case 7://7通过平面图待发施工图（OK，可编辑施工图）
                    dataMap.put("rank", 3);
                    dataMap.put("nameB", "待上传施工图");
                    break;
                case 2://2已发给业主施工图 （发给了业主）
                    dataMap.put("rank", 3);
                    dataMap.put("nameB", "待审核施工图");
                    break;
                case 8://8施工图片审核不通过（NG，可编辑施工图）
                    dataMap.put("rank", 3);
                    dataMap.put("nameB", "待修改施工图");
                    break;
                case 3://施工图(全部图)审核通过（OK，完成）
                    dataMap.put("rank", 4);
                    dataMap.put("nameB", "完成");
                    break;
            }
        } else {
            dataMap.put("total", 6);
            nameBs = new String[]{"开始远程设计", "业主支付", "量房阶段", "平面图阶段", "施工图阶段", "完成"};
            switch (house.getDesignerOk()) {
                case 0://0未确定设计师
                    dataMap.put("rank", 1);
                    dataMap.put("nameB", "待抢单");
                    break;
                case 4://4设计待抢单
                    dataMap.put("rank", 2);
                    dataMap.put("nameB", "待业主支付");
                    break;
                case 1://1已支付-设计师待量房
                    dataMap.put("rank", 3);
                    dataMap.put("nameB", "待量房");
                    break;
                case 9://9量房图发给业主
                    dataMap.put("rank", 4);
                    dataMap.put("nameB", "待上传平面图");
                    break;
                case 5://5平面图发给业主 （发给了业主）
                    dataMap.put("rank", 4);
                    dataMap.put("nameB", "待审核平面图");
                    break;
                case 6://6平面图审核不通过（NG，可编辑平面图）
                    dataMap.put("rank", 4);
                    dataMap.put("nameB", "待修改平面图");
                    break;
                case 7://7通过平面图待发施工图（OK，可编辑施工图）
                    dataMap.put("rank", 5);
                    dataMap.put("nameB", "待上传施工图");
                    break;
                case 2://2已发给业主施工图 （发给了业主）
                    dataMap.put("rank", 5);
                    dataMap.put("nameB", "待审核施工图");
                    break;
                case 8://8施工图片审核不通过（NG，可编辑施工图）
                    dataMap.put("rank", 5);
                    dataMap.put("nameB", "待修改施工图");
                    break;
                case 3://施工图(全部图)审核通过（OK，完成）
                    dataMap.put("rank", 6);
                    dataMap.put("nameB", "完成");
                    break;
            }
        }
        setMapDatas(dataMap, dataList, nameBs);
        return dataMap;
    }
}
