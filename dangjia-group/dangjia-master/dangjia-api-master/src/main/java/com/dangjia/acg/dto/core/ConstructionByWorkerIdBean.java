package com.dangjia.acg.dto.core;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Ruking.Cheng
 * @descrilbe 施工
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2018/12/3 11:46 AM
 */
@Data
public class ConstructionByWorkerIdBean {
    private String userId;//大管家查询时为业主ID,工匠查询是为大管家ID
    private int taskNumber;//任务数量
    private String allPatrol;//总巡查次数
    private BigDecimal alreadyMoney;//已得钱
    private BigDecimal alsoMoney;//还可得钱
    private String houseFlowId;//进程id
    private String houseIsStart;//今日未开工
    private String houseMemberName;//业主名称
    private String houseMemberPhone;//业主电话
    private String houseName;//房子名称
    private int ifBackOut;//大管家是否可放弃，true:可放弃；false:不可放弃,0：可放弃；1：申请停工；2：已停工
    private List<String> promptList;//消息提示
    private int workerType;//0:大管家；1：工匠
    private List<BigListBean> bigList;//菜单
    private List<ButtonListBean> buttonList;//按钮
    private List<WokerFlowListBean> wokerFlowList;//施工进程列表

    private String everyDay;//每日完工天数
    private int ifDisclose;//0:未支付；1：未交底；2:已交底
    private String supervisorCountOrder;//大管家总单数
    private String supervisorEvation;//大管家积分
    private String supervisorName;//大管家名字
    private String supervisorPhone;//大管家电话
    private String supervisorPraiseRate;//大管家好评率
    private String suspendDay;//暂停天数
    private String totalDay;//总开工天数
    private String footMessageTitle;// 今日开工任务
    private String footMessageDescribe;//（每日十二点前今日开工）
    private List<BigListBean.ListMapBean> workerEverydayList;//	每日开工事项

    @Data
    public static class BigListBean {
        private String name;
        private List<ListMapBean> listMap;

        @Data
        public static class ListMapBean {
            private String image;
            private String name;
            private String url;
            private int type;
        }
    }

    @Data
    public static class ButtonListBean {
        private int buttonType;//主按钮提示1：巡查工地2：申请业主验收；3:确认开工--主按钮提示 1:找大管家交底2:今日开工;3：今日完工;4阶段完工；5整体完工
        private String buttonTypeName;//主按钮提示 巡查工地;申请业主验收;确认开工--主按钮提示 1:找大管家交底2:今日开工;3：今日完工;4阶段完工；5整体完工
    }

    @Data
    public static class WokerFlowListBean {
        private String buttonTitle;//审核按钮提示
        private String detailUrl;//进程详情链接
        private String houseFlowId;//进程id
        private String houseFlowName;//进程名称
        private int houseFlowtype;//进程类型4拆除，5打孔，6水电工，7防水，8泥工,9木工，10油漆工，11安装
        private int isStart;//是否开工0:今日未开工；1：今日已开工；
        private String patrolSecond;//巡查次数
        private String patrolStandard;//巡查标准
        private int state;//装修进度0：未进场；1：待业主支付；2：待交底；3：施工中；4：阶段完工；5：收尾施工；6：整体完工
        private String workerId;//工匠id
        private String workerName;//工匠名字
        private String workerPhone;//工匠电话
    }
}
