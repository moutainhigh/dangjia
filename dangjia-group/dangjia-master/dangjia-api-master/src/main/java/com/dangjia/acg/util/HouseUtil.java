package com.dangjia.acg.util;


import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dto.core.NodeDTO;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.house.House;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HouseUtil {


    public static NodeDTO getWorkerDatas(House house, HouseFlow houseFlow,WorkerType workerType,String address ) {
        NodeDTO nodeDTO = new NodeDTO();
        String[] nameBs;
        String[] iconsN;
        String[] iconsY;
        nodeDTO.setNameA(workerType.getName());
        nodeDTO.setColor(workerType.getColor());
        if(!CommonUtil.isEmpty(workerType.getImage())) {
            nodeDTO.setImage(address + workerType.getImage());
        }
        if (workerType.getType() == 1) {//设计
            if (house.getDecorationType() == 2) {//自带设计流程
                nameBs= new String[]{"设计抢单", "设计平面图", "设计施工图", "设计完成"};
                iconsN= new String[]{"zx_icon_1_1.png", "zx_icon_5_1.png", "zx_icon_6_1.png", "zx_icon_7_1.png"};
                iconsY= new String[]{"zx_icon_1_2.png", "zx_icon_5_2.png", "zx_icon_6_2.png", "zx_icon_7_2.png"};
                nodeDTO.setTotal(nameBs.length);
            }else{
                nameBs = new String[]{"设计抢单", "业主支付", "量房阶段", "设计平面图", "设计施工图", "设计完成"};
                iconsN= new String[]{"zx_icon_1_1.png", "zx_icon_2_1.png","zx_icon_3_1.png","zx_icon_5_1.png", "zx_icon_6_1.png", "zx_icon_7_1.png"};
                iconsY= new String[]{"zx_icon_1_2.png", "zx_icon_2_2.png","zx_icon_3_2.png","zx_icon_5_2.png", "zx_icon_6_2.png", "zx_icon_7_2.png"};
                nodeDTO.setTotal(nameBs.length);
            }
            nodeDTO=getDesignDatas(house,nodeDTO);
        }else if (workerType.getType() == 2) {//精算
            nameBs = new String[]{"精算抢单", "业主支付", "制作精算", "精算完成"};
            iconsN= new String[]{"zx_icon_1_1.png", "zx_icon_2_1.png","zx_icon_4_1.png", "zx_icon_7_1.png"};
            iconsY= new String[]{"zx_icon_1_2.png", "zx_icon_2_2.png","zx_icon_4_2.png", "zx_icon_7_2.png"};
            nodeDTO=getBudgetDatas(house,nodeDTO);
        }else if (workerType.getType() == 3) {//大管家
            iconsN=null;
            iconsY=null;
            nameBs = new String[]{"未开始","大管家抢单", "业主支付", "工程排期", "确认开工", "监管工地", "整体竣工"};
            if (houseFlow.getWorkType() == 1) {
                nodeDTO.setRank(0);
                nodeDTO.setNameB("未开始");
            } else if (houseFlow.getWorkType() == 2) {
                nodeDTO.setRank(1);
                nodeDTO.setNameB("待抢单");
            } else if (houseFlow.getWorkType() == 3) {
                nodeDTO.setRank(2);
                nodeDTO.setNameB("待支付");
            } else if (houseFlow.getSupervisorStart() == 0 && houseFlow.getWorkType() == 4) {
                nodeDTO.setRank(3);
                nodeDTO.setNameB("待开工");
            } else if (houseFlow.getSupervisorStart() == 1 && houseFlow.getWorkType() == 4) {
                nodeDTO.setRank(4);
                nodeDTO.setNameB("监工中");
            } else if (houseFlow.getWorkSteta() == 2 || houseFlow.getWorkSteta() == 6) {
                if (houseFlow.getWorkSteta() == 2) {
                    nodeDTO.setNameB("整体完工");
                } else {
                    nodeDTO.setNameB("提前结束装修");
                }
                nodeDTO.setRank(5);
            }
        }else if (workerType.getType() == 4) {//拆除
            iconsN=null;
            iconsY=null;
            nameBs = new String[]{"未开始","拆除抢单", "业主支付", "施工交底","施工中", "整体完工"};
        }else{//其他
            iconsN=null;
            iconsY=null;
            nameBs = new String[]{"未开始",workerType.getName()+"抢单", "业主支付", "施工交底","施工中", "阶段完工", "整体完工"};
        }
        if (workerType.getType() >3) {
            nodeDTO = getWorkerDatas(houseFlow, nodeDTO);
        }
        nodeDTO.setTotal(nameBs.length);
        Map<String, Object> dataMap = new HashMap<>();
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (int i = 0; i < nameBs.length; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", nameBs[i]);
            map.put("begin", i +1);
            if(houseFlow.getWorkerType()<3){
                if(iconsY.length==nameBs.length) {
                    if (nodeDTO.getRank() <= (i + 1)) {
                        map.put("icon",address + "icon/"+iconsY[i]);
                    } else {
                        map.put("icon",address + "icon/"+iconsN[i]);
                    }
                }

            }

            dataList.add(map);
        }
        dataMap.put("dataList", dataList);
        nodeDTO.setProgress(dataMap);
        return nodeDTO;
    }
    public static NodeDTO getWorkerDatas(HouseFlow houseFlow,NodeDTO nodeDTO) {
        if (houseFlow.getWorkType() == 1) {
            nodeDTO.setRank(0);
            nodeDTO.setNameB("未发布");
        } else if (houseFlow.getWorkType() == 2) {
            nodeDTO.setRank(1);
            nodeDTO.setNameB("待抢单");
        } else if (houseFlow.getWorkType() == 3) {
            nodeDTO.setRank(2);
            nodeDTO.setNameB("待支付");
        } else if (houseFlow.getWorkType() == 4) {//已支付
            if (houseFlow.getWorkSteta() == 3) {
                nodeDTO.setRank(3);
                nodeDTO.setNameB("待交底");
            } else if (houseFlow.getWorkSteta() == 4) {
                nodeDTO.setRank(4);
                nodeDTO.setNameB("施工中");
            } else {
                if (houseFlow.getWorkSteta() == 1) {
                    nodeDTO.setRank(5);
                    nodeDTO.setNameB("阶段完工");
                }
                if (houseFlow.getWorkSteta() == 2 || houseFlow.getWorkSteta() == 6) {
                    if (houseFlow.getWorkerType() == 4) {//拆除
                        nodeDTO.setRank(5);
                    }else{
                        nodeDTO.setRank(6);
                    }
                    if (houseFlow.getWorkSteta() == 2) {
                        nodeDTO.setNameB("整体完工");
                    } else {
                        nodeDTO.setNameB("提前结束装修");
                    }
                }
            }
        }
        return nodeDTO;
    }

    public static NodeDTO getBudgetDatas(House house,NodeDTO nodeDTO) {
        switch (house.getBudgetOk()) {
            case 0:
                nodeDTO.setRank(1);
                nodeDTO.setNameB( "待抢单");
                break;
            case 5:
                nodeDTO.setRank(2);
                nodeDTO.setNameB( "待业主支付");
                break;
            case 1:
                if (house.getDecorationType() == 2 && house.getDesignerOk() != 3) {
                    nodeDTO.setRank(3);
                    nodeDTO.setNameB( "待上传设计图");
                } else {
                    nodeDTO.setRank(3);
                    nodeDTO.setNameB( "精算中");
                    nodeDTO.setNameC("5天内提交,需业主确认");
                }
                break;
            case -1:
                nodeDTO.setRank(3);
                nodeDTO.setNameB( "未发送精算");
                nodeDTO.setNameC("5天内提交,需业主确认");
                break;
            case 2:
                nodeDTO.setRank(3);
                nodeDTO.setNameB( "待审核精算");
                nodeDTO.setNameC("5天内提交,需业主确认");
                break;
            case 4:
                nodeDTO.setRank(3);
                nodeDTO.setNameB( "修改精算");
                nodeDTO.setNameC("5天内提交,需业主确认");
                break;
            case 3:
                nodeDTO.setRank(4);
                nodeDTO.setNameB( "完成");
                break;
        }
        return nodeDTO;
    }
    public static NodeDTO getDesignDatas(House house,NodeDTO nodeDTO) {
        if (house.getDecorationType() == 2) {//自带设计流程
            switch (house.getDesignerOk()) {
                case 0://0未确定设计师
                    nodeDTO.setRank(1);
                    nodeDTO.setNameB("待抢单");
                    break;
                case 4://4设计待抢单
                case 1://1已支付-设计师待量房
                case 9://9量房图发给业主
                    nodeDTO.setRank(2);
                    nodeDTO.setNameB("待上传平面图");
                    nodeDTO.setNameC("5天内提交,需业主确认");
                    break;
                case 5://5平面图发给业主 （发给了业主）
                    nodeDTO.setRank(2);
                    nodeDTO.setNameB("待审核平面图");
                    nodeDTO.setNameC("5天内提交,需业主确认");
                    break;
                case 6://6平面图审核不通过（NG，可编辑平面图）
                    nodeDTO.setRank(2);
                    nodeDTO.setNameB("待修改平面图");
                    nodeDTO.setNameC("5天内提交,需业主确认");
                    break;
                case 7://7通过平面图待发施工图（OK，可编辑施工图）
                    nodeDTO.setRank(3);
                    nodeDTO.setNameB("待上传施工图");
                    nodeDTO.setNameC("5天内提交,需业主确认");
                    break;
                case 2://2已发给业主施工图 （发给了业主）
                    nodeDTO.setRank(3);
                    nodeDTO.setNameB("待审核施工图");
                    nodeDTO.setNameC("5天内提交,需业主确认");
                    break;
                case 8://8施工图片审核不通过（NG，可编辑施工图）
                    nodeDTO.setRank(3);
                    nodeDTO.setNameB("待修改施工图");
                    nodeDTO.setNameC("5天内提交,需业主确认");
                    break;
                case 3://施工图(全部图)审核通过（OK，完成）
                    nodeDTO.setRank(4);
                    nodeDTO.setNameB("完成");
                    break;
            }
        } else {
            switch (house.getDesignerOk()) {
                case 0://0未确定设计师
                    nodeDTO.setRank(1);
                    nodeDTO.setNameB("待抢单");
                    break;
                case 4://4设计待抢单
                    nodeDTO.setRank(2);
                    nodeDTO.setNameB("待业主支付");
                    break;
                case 1://1已支付-设计师待量房
                    nodeDTO.setRank(3);
                    nodeDTO.setNameB("待量房");
                    break;
                case 9://9量房图发给业主
                    nodeDTO.setRank(4);
                    nodeDTO.setNameB("待上传平面图");
                    nodeDTO.setNameC("5天内提交,需业主确认");
                    break;
                case 5://5平面图发给业主 （发给了业主）
                    nodeDTO.setRank(4);
                    nodeDTO.setNameB("待审核平面图");
                    nodeDTO.setNameC("5天内提交,需业主确认");
                    break;
                case 6://6平面图审核不通过（NG，可编辑平面图）
                    nodeDTO.setRank(4);
                    nodeDTO.setNameB("待修改平面图");
                    nodeDTO.setNameC("5天内提交,需业主确认");
                    break;
                case 7://7通过平面图待发施工图（OK，可编辑施工图）
                    nodeDTO.setRank(5);
                    nodeDTO.setNameB("待上传施工图");
                    nodeDTO.setNameC("5天内提交,需业主确认");
                    break;
                case 2://2已发给业主施工图 （发给了业主）
                    nodeDTO.setRank(5);
                    nodeDTO.setNameB("待审核施工图");
                    nodeDTO.setNameC("5天内提交,需业主确认");
                    break;
                case 8://8施工图片审核不通过（NG，可编辑施工图）
                    nodeDTO.setRank(5);
                    nodeDTO.setNameB("待修改施工图");
                    nodeDTO.setNameC("5天内提交,需业主确认");
                    break;
                case 3://施工图(全部图)审核通过（OK，完成）
                    nodeDTO.setRank(6);
                    nodeDTO.setNameB("完成");
                    break;
            }
        }
        return nodeDTO;
    }



    public static Map getWorkerDatas(House house,String address ) {
         Map progress=new HashMap();
        String[] nameBs;
        String[] iconsY;
        Integer[] workerTypes=new Integer[]{1,2,3};
        for (Integer workerType : workerTypes) {
            if (workerType == 1) {//设计
                if (house!=null&&house.getDecorationType() == 2) {//自带设计流程
                    nameBs= new String[]{"设计抢单", "设计平面图", "设计施工图", "设计完成"};
                    iconsY= new String[]{"zx_icon_1_2.png", "zx_icon_5_2.png", "zx_icon_6_2.png", "zx_icon_7_2.png"};
                }else{
                    nameBs = new String[]{"设计抢单", "业主支付", "量房阶段", "设计平面图", "设计施工图", "设计完成"};
                    iconsY= new String[]{"zx_icon_1_2.png", "zx_icon_2_2.png","zx_icon_3_2.png","zx_icon_5_2.png", "zx_icon_6_2.png", "zx_icon_7_2.png"};
                }
            }else if (workerType == 2) {//精算
                nameBs = new String[]{"精算抢单", "业主支付", "制作精算", "精算完成"};
                iconsY= new String[]{"zx_icon_1_2.png", "zx_icon_2_2.png","zx_icon_4_2.png", "zx_icon_7_2.png"};
            }else{//其他
                nameBs = new String[]{"工匠抢单", "支付费用", "正常施工", "阶段完工", "整体完工", "施工完成"};
                iconsY= new String[]{"zx_icon_1_2.png", "zx_icon_2_2.png","zx_icon_sg_default@2x.png", "zx_icon_ztwg_default@2x.png", "zx_icon_wg_default@2x.png", "zx_icon_7_2.png"};
            }
            Map<String, Object> dataMap = new HashMap<>();
            List<Map<String, Object>> dataList = new ArrayList<>();
            for (int i = 0; i < nameBs.length; i++) {
                Map<String, Object> map = new HashMap<>();
                map.put("name", nameBs[i]);
                map.put("begin", i +1);
                map.put("icon",address + "icon/"+iconsY[i]);
                dataList.add(map);
            }
            dataMap.put("dataList", dataList);
            progress.put("workerType"+workerType,dataMap);
        }
        return progress;
    }



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
