package com.dangjia.acg.dto.core;

import com.dangjia.acg.util.Utils;
import lombok.Data;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Ruking.Cheng
 * @descrilbe 施工
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2018/12/3 11:46 AM
 */
@Data
public class ConstructionByWorkerIdBean {
    private String userId = "";//大管家查询时为业主ID,工匠查询是为大管家ID
    private int taskNumber;//任务数量
    private String allPatrol = "标准巡查次数:0";//总巡查次数
    private String actualPatrol = "实际巡查次数:0";//总巡查次数
    private BigDecimal alreadyMoney;//已得钱
    private BigDecimal alsoMoney;//还可得钱
    private String houseFlowId;//进程id
    private String houseIsStart;//今日未开工
    private String houseMemberName = "";//业主名称
    private String houseMemberPhone = "";//业主电话
    private String houseName = "";//房子名称
    private Double houseSquare;//房子面积
    private int ifBackOut = 1;//0可放弃；1：申请停工；2：已停工 3 审核中
    private List<String> promptList;//消息提示
    private int workerType;//0:大管家；1：工匠；2：设计师；3：精算师
    private List<BigListBean> bigList;//菜单
    private List<ButtonListBean> buttonList;//按钮
    private List<WokerFlowListBean> wokerFlowList;//施工进程列表
    private String everyDay;//每日完工天数
    private int ifDisclose;//0:未支付；1：未交底；2:已交底
    private String supervisorCountOrder = "总单数:0";//大管家总单数
    private String supervisorPraiseRate = "总单数:0";//大管家好评率
    private String supervisorEvation = "好评率:0%";//大管家积分
    private String supervisorName = "无";//大管家名字
    private String supervisorPhone = "无";//大管家电话
    private String suspendDay;//暂停天数
    private String totalDay;//总开工天数
    private String footMessageTitle;// 今日开工任务
    private String footMessageDescribe;//（每日十二点前今日开工）
    private List<BigListBean.ListMapBean> workerEverydayList;//	每日开工事项
    private List<Map<String, Object>> dataList;//设计师和精算流程返回体
    private Integer decorationType;//"装修类型: 0表示没有开始，1远程设计，2自带设计，3共享装修")
    private String houseId;
    private String apartmentName;//户型名称
    private Integer designerOk;

    private Integer totalNodeNumber;//总结点数
    private Integer completedNodeNumber;//已完成节点


    private String nodeTitle;//节点标题
    private Integer trialNumber;//审核次数
    private Integer node;//工序申请节点  1-工匠发起 2-大管家审核通过 3-业主审核通过

    private Integer houseWorkerType;//0:装修单，1:体验单，2，维修单
    private String houseWorkerId;//装修单ID
    private Integer retentionType;//0-需缴纳质保金 1-不缴纳质保金

    private String businessId;//业务id
    @Data
    public static class BigListBean {
        private String name;
        private List<ListMapBean> listMap;
        public static  String[] sheJi=new String[]{"MT0009","MT0001","MT0003"};//设计师菜单
        public static  String[] jingSuanYc=new String[]{"MT0009","MT0001","MT0002","MT0003"};//精算师菜单(远程设计)
        public static  String[] jingSuanZd=new String[]{"MT0001","MT0002","MT0003"};//精算师菜单(自带设计)
        public static  String[] daGuanJiaN=new String[]{"MT0001","MT0002","MT0003","MT0004","MT0005","MT0006","MT0007","MT0008"};//大管家菜单(未周计划)
        public static  String[] daGuanJiaY=new String[]{"MT0001","MT0002","MT0003","MT0004","MT0005","MT0006","MT0007"};//大管家菜单(已周计划)
        public static  String[] daGuanJiaG=new String[]{"MH0001","MH0002","MH0003"};//大管家菜单(工地记录)
        public static  String[] gongJiang=new String[]{"MT0001","MT0002","MT0003","MT0004","MT0005","MT0006","MT0007","MH0003"};//工匠菜单
        private HashMap<String, ListMapBean> beanBut = new HashMap<String, ListMapBean>(){
            {
                put("MT0001",  new ListMapBean("施工图","iconWork/menus/home_icon_shigongtu@2x.png","","MT0001"));
                put("MT0002",  new ListMapBean("精算","iconWork/menus/home_icon_jingsuan@2x.png","","MT0002"));
                put("MT0003",  new ListMapBean("通讯录","iconWork/menus/home_icon_txl@2x.png","","MT0003"));
                put("MT0004",  new ListMapBean("备忘录","iconWork/menus/home_icon_memo@2x.png","","MT0004"));
                put("MT0005",  new ListMapBean("装修日历","iconWork/menus/home_icon_zxrili@2x.png","","MT0005"));
                put("MT0006",  new ListMapBean("要货","iconWork/menus/home_icon_yaohuo@2x.png","","MT0006"));
                put("MT0007",  new ListMapBean("收货","iconWork/menus/home_icon_shth@2x.png","","MT0007"));
                put("MT0008",  new ListMapBean("周计划","iconWork/menus/zxy_icon_weekplan@2x.png","","MT0008"));
                put("MT0009",  new ListMapBean("量房","iconWork/menus/zxy_icon_liangfang@2x.png","","MT0009"));

                put("MH0001",  new ListMapBean("业主仓库","iconWork/menus/zxy_icon_cangku@2x.png","","MH0001"));
                put("MH0002",  new ListMapBean("要补退记录","iconWork/menus/zxy_icon_record@2x.png","","MH0002"));
                put("MH0003",  new ListMapBean("审核记录","iconWork/menus/zxy_icon_audit@2x.png","","MH0003"));
            }
        };
        public  List<ListMapBean> getMenus(String imageAddress,String[] menusCodes){
            this.listMap=new LinkedList<>();
            for (String menusCode : menusCodes) {
                ListMapBean listMapBean=beanBut.get(menusCode);
                listMapBean.setImage(Utils.getImageAddress(imageAddress, listMapBean.getImage()));
                this.listMap.add(listMapBean);
            }
            return this.listMap;
        }
        @Data
        public static class ListMapBean {
            public ListMapBean() {
            }
            public ListMapBean(String name,String image,String url,String type) {
                this.name =name;
                this.image = image;
                this.url= url;
                this.type= type;
            }
            private String image;//按钮图标
            private String name;//按钮名称
            private String url;
            /**
             *  施工首页-菜单按钮类型说明
             *      其他：1000：URL跳转
             *  我的工具：
             *      MT0001:施工图
             *      MT0002:精算
             *      MT0003:通讯录
             *      MT0004:备忘录
             *      MT0005:装修日历
             *      MT0006:要货
             *      MT0007:收获
             *      MT0008:周计划
             *      MT0009:量房
             *
             *  工地记录：
             *      MH0001:业主仓库
             *      MH0002:要补退记录
             *      MH0003:审核记录
             */
            private String type;//0:跳转URL，1:获取定位后跳转URL，2:量房，3：传平面图，4：传施工图
            private int state;//0无 1有点
            private int number;//点数量
        }
    }

    @Data
    public static class WokerFlowListBean {
        private String buttonTitle;//审核按钮提示
        private String detailUrl;//进程详情链接
        private String houseFlowId;//进程id
        private Integer workerType;//工种类型
        private String workerTypeName;//进程名称
        private String workerTypeColor;//颜色
        private Integer isStart;//是否开工0:今日未开工；1：今日已开工；
        private Long finishedDay;//每日完工天数
        private Long startDay;//开工天数
        private Long suspendDay;//暂停天数；
        private String patrolSecond;//巡查次数
        private String patrolStandard;//巡查标准
        private Integer state;//工程状态  0：未进场；1：待审核工匠；2：待交底；3：施工中；4：阶段完工；5：收尾施工；6：整体完工
        private String workerId;//工匠id
        private String workerName;//工匠名字
        private String workerPhone;//工匠电话
        private String workerHead;//头像
        private String raiseRate;//好评率
        private BigDecimal overall;//综合分
        private Integer orderTakingNum;//接单数量
        private Integer totalNodeNumber;//总节点数
        private Integer completedNodeNumber;//当前节点

        List<ButtonListBean> topButton;//头部按钮
        List<ButtonListBean> footButton;//底部按钮
    }
}
