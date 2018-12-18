package com.dangjia.acg.common.constants;

/**
 * 公共应用常量类
 */
public class DjConstants {
    public final static String PUBLIC_DANGJIA_APP_ADDRESS="http://172.16.30.95:7001/#/";
    /**
     * PageAddress 工匠端页面跳转地址
     */
    public static class GJPageAddress {
        public final static String HPMANAGE =PUBLIC_DANGJIA_APP_ADDRESS+"hpManage?userToken=%s&cityId=%s&title=%s";//货品管理
        public final static String MYINVITECODE =PUBLIC_DANGJIA_APP_ADDRESS+"myInviteCode?userToken=%s&cityId=%s&title=%s";//我的邀请码
        public final static String HPDETAIL =PUBLIC_DANGJIA_APP_ADDRESS+"hpDetail?userToken=%s&cityId=%s&title=%s";//货品详情
        public final static String REQUIREGOODSLIST =PUBLIC_DANGJIA_APP_ADDRESS+"requireGoodsList?userToken=%s&cityId=%s&title=%s";//要货(补货、退货)
        public final static String COMFIRMGOODSLIST =PUBLIC_DANGJIA_APP_ADDRESS+"comfirmGoodsList?userToken=%s&cityId=%s&title=%s";//要货确认
        public final static String BTRECORDLIST =PUBLIC_DANGJIA_APP_ADDRESS+"btRecordList?userToken=%s&cityId=%s&title=%s";//补货退货记录
        public final static String BTRECORDDETAIL =PUBLIC_DANGJIA_APP_ADDRESS+"btRecordDetail?userToken=%s&cityId=%s&title=%s";//补货退货记录详情
        public final static String GJGYLIST =PUBLIC_DANGJIA_APP_ADDRESS+"gjgyList?userToken=%s&cityId=%s&title=%s";//工价工艺
        public final static String GJGYDETAIL =PUBLIC_DANGJIA_APP_ADDRESS+"gjgyDetail?userToken=%s&cityId=%s&title=%s";//工价工艺详情
        public final static String GJGYGOODSDETAIL =PUBLIC_DANGJIA_APP_ADDRESS+"gjgyGoodsDetail?userToken=%s&cityId=%s&title=%s";//商品详情(工价工艺)
        public final static String BTPEOPLE =PUBLIC_DANGJIA_APP_ADDRESS+"btPeople?userToken=%s&cityId=%s&title=%s";//补退人工
        public final static String RECIVEGOODSLIST =PUBLIC_DANGJIA_APP_ADDRESS+"reciveGoodsList?userToken=%s&cityId=%s&title=%s";//收货
        public final static String RECIVEGOODSDETAIL =PUBLIC_DANGJIA_APP_ADDRESS+"reciveGoodsDetail?userToken=%s&cityId=%s&title=%s";//收货清单
        public final static String PARTGOODSRECIVE =PUBLIC_DANGJIA_APP_ADDRESS+"partGoodsRecive?userToken=%s&cityId=%s&title=%s";//部分收货
        public final static String INTEGRATIONLIST =PUBLIC_DANGJIA_APP_ADDRESS+"integrationList?userToken=%s&cityId=%s&title=%s";//积分记录
        public final static String JUGLELIST =PUBLIC_DANGJIA_APP_ADDRESS+"jugleList?userToken=%s&cityId=%s&title=%s";//评价记录
        public final static String GJREGISTERPROTOCOL =PUBLIC_DANGJIA_APP_ADDRESS+"gjRegisterProtocol?userToken=%s&cityId=%s&title=%s";//注册协议
        public final static String PROJECTDRAWINGLIST =PUBLIC_DANGJIA_APP_ADDRESS+"projectDrawingList?userToken=%s&cityId=%s&title=%s";//施工图
        public final static String QRCODE =PUBLIC_DANGJIA_APP_ADDRESS+"qrCode?userToken=%s&cityId=%s&title=%s";//二维码
        public final static String PROJECTRECORD =PUBLIC_DANGJIA_APP_ADDRESS+"projectRecord?userToken=%s&cityId=%s&title=%s";//工地记录
        public final static String PROJECTADRESSLIST =PUBLIC_DANGJIA_APP_ADDRESS+"projectAdressList?userToken=%s&cityId=%s&title=%s";//工地通讯录
        public final static String GJJINGSUANLIST =PUBLIC_DANGJIA_APP_ADDRESS+"gjJingSuanList?userToken=%s&cityId=%s&title=%s";//精算
        public final static String CONFIRMQIANGDAN =PUBLIC_DANGJIA_APP_ADDRESS+"confirmQiangdan?userToken=%s&cityId=%s&title=%s";//抢单确认
        public final static String GJMANAGERJINGSUAN =PUBLIC_DANGJIA_APP_ADDRESS+"gjManagerJingSuan?userToken=%s&cityId=%s&title=%s";//精算
        public final static String GJMANAGERISOK =PUBLIC_DANGJIA_APP_ADDRESS+"gjManagerIsOk?userToken=%s&cityId=%s&title=%s";//已抢单
        public final static String COMFIRMAPPLY =PUBLIC_DANGJIA_APP_ADDRESS+"comfirmApply?userToken=%s&cityId=%s&title=%s";//审核申请
        public final static String COMFIRMSTOPPROJECT =PUBLIC_DANGJIA_APP_ADDRESS+"comfirmStopProject?userToken=%s&cityId=%s&title=%s";//审核停工
        public final static String COMFIRMFAIL =PUBLIC_DANGJIA_APP_ADDRESS+"comfirmFail?userToken=%s&cityId=%s&title=%s";//审核不通过
        public final static String READPROJECTINFO =PUBLIC_DANGJIA_APP_ADDRESS+"readProjectInfo?userToken=%s&cityId=%s&title=%s";//阅读交底项目
        public final static String ABOUTUS =PUBLIC_DANGJIA_APP_ADDRESS+"aboutUs?userToken=%s&cityId=%s&title=%s";//关于我们
        public final static String JIANGFALIST =PUBLIC_DANGJIA_APP_ADDRESS+"jiangFaList?userToken=%s&cityId=%s&title=%s";//奖罚记录
        public final static String JIANGFADETAIL =PUBLIC_DANGJIA_APP_ADDRESS+"jiangFaDetail?userToken=%s&cityId=%s&title=%s";//奖罚详情
        public final static String COMPALIN =PUBLIC_DANGJIA_APP_ADDRESS+"compalin?userToken=%s&cityId=%s&title=%s";//申诉
        public final static String HELPCENTER =PUBLIC_DANGJIA_APP_ADDRESS+"helpCenter?userToken=%s&cityId=%s&title=%s";//帮助中心
        public final static String ORDERRECORD =PUBLIC_DANGJIA_APP_ADDRESS+"orderRecord?userToken=%s&cityId=%s&title=%s";//接单记录
        public final static String AFFIRMGRAB =PUBLIC_DANGJIA_APP_ADDRESS+"affirmGrab?userToken=%s&cityId=%s&title=%s";//抢单确认页
        public final static String PROCESSREQUIRE =PUBLIC_DANGJIA_APP_ADDRESS+"processRequire?userToken=%s&cityId=%s&title=%s";//工艺要求
        public final static String PROCESSDETAIL =PUBLIC_DANGJIA_APP_ADDRESS+"processDetail?userToken=%s&cityId=%s&title=%s";//工艺详情
        public final static String GJPRICE =PUBLIC_DANGJIA_APP_ADDRESS+"gjPrice?userToken=%s&cityId=%s&title=%s";//工匠报价
        public final static String CASHRECORD =PUBLIC_DANGJIA_APP_ADDRESS+"cashRecord?userToken=%s&cityId=%s&title=%s";//提现记录
        public final static String MYTASK =PUBLIC_DANGJIA_APP_ADDRESS+"myTask?userToken=%s&cityId=%s&title=%s";//我的任务
        public final static String MYBANKCARD =PUBLIC_DANGJIA_APP_ADDRESS+"myBankCard?userToken=%s&cityId=%s&title=%s";//银行卡（添加）
        public final static String MYBANKCARDYES =PUBLIC_DANGJIA_APP_ADDRESS+"myBankCardYes?userToken=%s&cityId=%s&title=%s";//银行卡（已添加）
        public final static String GJBJDETAILS =PUBLIC_DANGJIA_APP_ADDRESS+"gjbjDetails?userToken=%s&cityId=%s&title=%s";//工匠报价详情
        public final static String JFREGULATIONS =PUBLIC_DANGJIA_APP_ADDRESS+"jfRegulations?userToken=%s&cityId=%s&title=%s";//选择奖罚条例
        public final static String JFREASON =PUBLIC_DANGJIA_APP_ADDRESS+"jfReason?userToken=%s&cityId=%s&title=%s";//奖罚原因
        public final static String JSCRAFTSMAN =PUBLIC_DANGJIA_APP_ADDRESS+"jsCraftsman?userToken=%s&cityId=%s&title=%s";//精算（工匠）
        public final static String JSDETAILS =PUBLIC_DANGJIA_APP_ADDRESS+"jsDetails?userToken=%s&cityId=%s&title=%s";//精算（工匠）详情
    }

    /**
     * PageAddress 业主端页面跳转地址
     */
    public static class YZPageAddress {
        //        public final static String *-=PUBLIC_DANGJIA_APP_ADDRESS+"*-?userToken=%s&cityId=%s&title=%s";//施工现场
        //        public final static String *-=PUBLIC_DANGJIA_APP_ADDRESS+"*-?userToken=%s&cityId=%s&title=%s";//参考报价
        //        public final static String **-=PUBLIC_DANGJIA_APP_ADDRESS+"**-?userToken=%s&cityId=%s&title=%s";//联系客服
        public final static String INDEX =PUBLIC_DANGJIA_APP_ADDRESS+"index?userToken=%s&cityId=%s&title=%s";//首页
        public final static String GJDETAIL =PUBLIC_DANGJIA_APP_ADDRESS+"gjDetail?userToken=%s&cityId=%s&title=%s";//直连工匠
        public final static String TMGJDETAIL =PUBLIC_DANGJIA_APP_ADDRESS+"tmgjDetail?userToken=%s&cityId=%s&title=%s";//透明工价
        public final static String BGGYDETAIL =PUBLIC_DANGJIA_APP_ADDRESS+"bggyDetail?userToken=%s&cityId=%s&title=%s";//标杆工艺
        public final static String BGGYMARKDETAIL =PUBLIC_DANGJIA_APP_ADDRESS+"bggyMarkDetail?userToken=%s&cityId=%s&title=%s";//标杆工艺详情
        public final static String YXCLDETAIL =PUBLIC_DANGJIA_APP_ADDRESS+"yxclDetail?userToken=%s&cityId=%s&title=%s";//严选材料
        public final static String NEWDECORATEDETAIL =PUBLIC_DANGJIA_APP_ADDRESS+"newDecorateDetail?userToken=%s&cityId=%s&title=%s";//新装修
        public final static String JGGJDETAIL =PUBLIC_DANGJIA_APP_ADDRESS+"jggjDetail?userToken=%s&cityId=%s&title=%s";//精干工匠
        public final static String QCJGDETAIL =PUBLIC_DANGJIA_APP_ADDRESS+"qcjgDetail?userToken=%s&cityId=%s&title=%s";//全程监管
        public final static String REFERCEPRICEDETAIL =PUBLIC_DANGJIA_APP_ADDRESS+"refercePriceDetail?userToken=%s&cityId=%s&title=%s";//参考估算报价
        public final static String HOUSEINSPECTION =PUBLIC_DANGJIA_APP_ADDRESS+"houseInspection?userToken=%s&cityId=%s&title=%s";//我要验房
        public final static String ZHINAN =PUBLIC_DANGJIA_APP_ADDRESS+"zhinan?userToken=%s&cityId=%s&title=%s";//装修指南
        public final static String CONFIRMAPPLY =PUBLIC_DANGJIA_APP_ADDRESS+"confirmApply?userToken=%s&cityId=%s&title=%s";//审核申请
        public final static String JOBLOCATIONDETAIL =PUBLIC_DANGJIA_APP_ADDRESS+"jobLocationDetails?userToken=%s&cityId=%s&title=%s";//施工现场详情
        public final static String SHIGONG =PUBLIC_DANGJIA_APP_ADDRESS+"shigong?userToken=%s&cityId=%s&title=%s";//施工流程
        public final static String BANKCARDALREADYADD =PUBLIC_DANGJIA_APP_ADDRESS+"bankCardAlreadyAdd?userToken=%s&cityId=%s&title=%s";//我的-银行卡
        public final static String BANDCARD =PUBLIC_DANGJIA_APP_ADDRESS+"bandCard?userToken=%s&cityId=%s&title=%s";//我的-银行卡（add）
        public final static String ORDERLIST =PUBLIC_DANGJIA_APP_ADDRESS+"orderList?userToken=%s&cityId=%s&title=%s";//我的-我的订单
        public final static String ORDERDETAIL =PUBLIC_DANGJIA_APP_ADDRESS+"orderDetail?userToken=%s&cityId=%s&title=%s";//我的-订单详情
        public final static String MYQUALITYASSURANCECARD =PUBLIC_DANGJIA_APP_ADDRESS+"myQualityAssuranceCard?userToken=%s&cityId=%s&title=%s";//我的-我的质保卡
        public final static String MYQUALITYASSURANCECARDDETAILS =PUBLIC_DANGJIA_APP_ADDRESS+"myQualityAssuranceCardDetails?userToken=%s&cityId=%s&title=%s";//我的-我的质保卡详情
        public final static String WORKINGDETAILS =PUBLIC_DANGJIA_APP_ADDRESS+"workingDetails?userToken=%s&cityId=%s&title=%s";//工序记录及工匠详情
        public final static String MYGOODS =PUBLIC_DANGJIA_APP_ADDRESS+"myGoods?userToken=%s&cityId=%s&title=%s";//我购买的
        public final static String MYWAREDETAILS =PUBLIC_DANGJIA_APP_ADDRESS+"myWareDetails?userToken=%s&cityId=%s&title=%s";//我购买的-商品详情
        public final static String MYGOODSAPPLYRETURN =PUBLIC_DANGJIA_APP_ADDRESS+"myGoodsApplyReturn?userToken=%s&cityId=%s&title=%s";//我的商品-申请退款
        public final static String COMFIRMAPPLYRETURN =PUBLIC_DANGJIA_APP_ADDRESS+"comfirmApplyReturn?userToken=%s&cityId=%s&title=%s";//我的商品-确认退款
        public final static String REFUNDLIST =PUBLIC_DANGJIA_APP_ADDRESS+"refundList?userToken=%s&cityId=%s&title=%s";//我的商品-退款列表
        public final static String APPLYRETURNDETAIL =PUBLIC_DANGJIA_APP_ADDRESS+"applyReturnDetail?userToken=%s&cityId=%s&title=%s";//我的商品-退款记录详情
        public final static String RECEIVING =PUBLIC_DANGJIA_APP_ADDRESS+"receiving?userToken=%s&cityId=%s&title=%s";//收货
        public final static String CONSIGNMENTGOODS =PUBLIC_DANGJIA_APP_ADDRESS+"consignmentGoods?userToken=%s&cityId=%s&title=%s";//收货清单
        public final static String CONSIGNMENTSHEET =PUBLIC_DANGJIA_APP_ADDRESS+"consignmentSheet?userToken=%s&cityId=%s&title=%s";//部分收货
        public final static String CONFIRMACTUARY =PUBLIC_DANGJIA_APP_ADDRESS+"confirmActuary?userToken=%s&cityId=%s&title=%s";//确认精算
        public final static String CONFIRMACTUARYDETAIL =PUBLIC_DANGJIA_APP_ADDRESS+"confirmActuaryDetail?userToken=%s&cityId=%s&title=%s";//精算明细(材料明细)
        public final static String COMMO =PUBLIC_DANGJIA_APP_ADDRESS+"commo?userToken=%s&cityId=%s&title=%s";//精算-商品详情
        public final static String GOODSDETAIL =PUBLIC_DANGJIA_APP_ADDRESS+"goodsDetail?userToken=%s&cityId=%s&title=%s";//精算-商品详情
        public final static String COMMODITY =PUBLIC_DANGJIA_APP_ADDRESS+"commodity?userToken=%s&cityId=%s&title=%s";//精算-商品更换
        public final static String WAITINGPAYDETAIL =PUBLIC_DANGJIA_APP_ADDRESS+"waitingPayDetail?userToken=%s&cityId=%s&title=%s";//待付款明细
        public final static String MYSHOPLIST =PUBLIC_DANGJIA_APP_ADDRESS+"myShopList?userToken=%s&cityId=%s&title=%s";//自购清单
        public final static String DESIGNLIST =PUBLIC_DANGJIA_APP_ADDRESS+"designList?userToken=%s&cityId=%s&title=%s";//设计图
        public final static String JINGSUANLIST =PUBLIC_DANGJIA_APP_ADDRESS+"jingsuanList?userToken=%s&cityId=%s&title=%s";//精算
        public final static String CONSTUCTIONRECORD =PUBLIC_DANGJIA_APP_ADDRESS+"constuctionRecord?userToken=%s&cityId=%s&title=%s";//施工记录
        public final static String YZREGISTERPROTOCOL =PUBLIC_DANGJIA_APP_ADDRESS+"yzRegisterProtocol?userToken=%s&cityId=%s&title=%s";//注册协议

    }

    /**
     * ApplyType 任务进程-申请类型
     */
    public static class ApplyType {
        public final static Integer MEIRI_WANGGONG = 0;//0每日完工申请
        public final static Integer JIEDUAN_WANGONG = 1;// 1阶段完工申请
        public final static Integer ZHENGTI_WANGONG = 2;// 2整体完工申请
        public final static Integer TINGGONG = 3;// 3停工申请
        public final static Integer MEIRI_KAIGONG = 4;// 4：每日开工
        public final static Integer YOUXIAO_XUNCHA = 5;// 5有效巡查
        public final static Integer WUREN_XUNCHA = 6;// 6无人巡查
        public final static Integer ZUIJIA_XUNCHA = 7;// 7追加巡查
    }


    /**
     * Type 工序类型 人工1 材料2 服务3
     */
    public static class GXType {
        public final static Integer RENGGONG = 1;// 人工
        public final static Integer CAILIAO = 2;// 材料
        public final static Integer FUWU = 3;// 服务
    }

    public static class PushMessage{
        public final static String START_FITTING_UP ="业主您好！您的美宅【%s】开始装修啦！";
        public final static String SNAP_UP_ORDER ="您有一个新的装修订单，马上去抢！";
        public final static String DESIGNER_GRABS_THE_BILL ="业主您好！您的美宅【%s】已有设计师抢单，赶快去看看吧~";
        public final static String PAYMENT_OF_DESIGN_FEE ="设计师您好！业主已支付【%s】设计费，请及时查看";
        public final static String PLANE_UPLOADING ="业主您好！您的美宅【%s】平面图已经上传，请确认。";
        public final static String PLANE_ERROR ="抱歉，您设计的【%s】平面图未通过，请联系业主修改后再上传。";
        public final static String PLANE_OK ="恭喜！您设计的【%s】平面图已通过，请联系业主设计施工图。";
        public final static String CONSTRUCTION_UPLOADING ="业主您好！您的美宅【%s】施工图已经上传，请确认。";
        public final static String CONSTRUCTION_ERROR ="抱歉，您设计的【%s】施工图未通过，请联系业主修改后再上传。";
        public final static String CONSTRUCTION_OK ="恭喜！您设计的【%s】施工图已通过，请查看。";
        public final static String ACTUARY_UPLOADING ="业主您好！您的美宅【%s】精算已经上传，请确认。";
        public final static String ACTUARY_ERROR ="抱歉，您设计的【%s】精算未通过，请联系业主修改后再上传。";
        public final static String ACTUARY_OK ="恭喜！您设计的【%s】施工图已通过，请查看。";
        public final static String STEWARD_RUSH_TO_PURCHASE ="业主您好！您的美宅【%s】已有大管家抢单，赶快去看看吧~";
        public final static String STEWARD_REPLACE ="您好！【%s】业主已将您更换，请重新抢单，再接再厉！";
        public final static String STEWARD_ABANDON ="业主您好！您的美宅【%s】大管家已经放弃，请等待其他大管家抢单。";
        public final static String STEWARD_PAYMENT ="大管家您好！业主已支付【%s】大管家费，请及时查看";
        public final static String STEWARD_CONSTRUCTION ="业主您好，大管家已经开始了装修施工，施工工匠会逐步进场，请您关注装修过程!";
        public final static String RECORD_OF_REWARDS_AND_PENALTIES ="工匠您好！【%s】有一条您的奖罚记录，请查看。";
        public final static String STEWARD_NEW_REPLACE  ="业主您好！经多方协调，当家装修已为您更换了新的大管家，立即查看";
        public final static String CRAFTSMAN_RUSH_TO_PURCHASE ="业主您好！您的美宅【%s】已有工匠抢单，赶快去看看吧~";
        public final static String CRAFTSMAN_ABANDON ="业主您好！您的美宅【%s】【%s】已放弃，请等待新工匠抢单。";
        public final static String CRAFTSMAN_PAYMENT ="工匠您好！业主已支付【%s】工匠费用，请及时查看";
        public final static String STEWARD_CRAFTSMAN_FINISHED ="业主，您好！【%s】大管家已经与工匠完成开工交底，此工序施工正式开始";
        public final static String CRAFTSMAN_NOT_START ="工匠您好！【%s】今日未开工，请在12点前开工，如有不便需要请假请申请停工。";
        public final static String STEWARD_CRAFTSMEN_APPLY_FOR_STOPPAGE ="大管家您好！【%s】有工匠申请停工，请查看";
        public final static String CRAFTSMAN_NEW_REPLACE ="业主您好！经多方协调，当家装修已为您更换了新的工匠，立即查看";
        public final static String STEWARD_APPLY_FINISHED ="大管家您好！【%s】【%s】申请阶段/整体完工，请审核！";
        public final static String STEWARD_APPLY_FINISHED_NOT_PASS ="工匠您好！您申请的【%s】阶段/整体完工未通过大管家的审核，请及时整改！";
        public final static String STEWARD_APPLY_FINISHED_PASS ="工匠您好！您申请的【%s】阶段/整体完工已通过大管家的审核，请查看。";
        public final static String CRAFTSMAN_EVALUATE ="工匠您好！【%s】业主已对您进行评价，立即查看";
        public final static String CRAFTSMAN_ALL_FINISHED ="业主您好！【%s】所有工匠都已完工，大管家申请竣工验收，请查看。";
        public final static String STEWARD_EVALUATE ="大管家您好！【%s】业主已对您进行评价，立即查看";
        public final static String MATERIAL_DELIVERY ="业主您好！【%s】大管家已为您安排了材料发货，请及时收货。";
        public final static String MATERIAL_SUPPLEMENTARY ="业主您好！大管家根据【%s】装修施工情况判断现场需要补部分材料，请查看并支付";
        public final static String MATERIAL_WITHDRAWAL ="业主您好！大管家根据【%s】装修施工情况判断现场需要退部分材料，请查看。（退材料成功后，您可在\"我购买的\"发起退款）";
        public final static String REFUND_SUCCESS ="业主您好！您发起的退款操作成功，请查看";
        public final static String REFUND_ERROR ="业主您好！您发起的退款操作未成功，请重新提交";
        public final static String WITHDRAW_CASH_SUCCESS ="您好！您发起的提现操作成功，请注意查收";
        public final static String WITHDRAW_CASH_ERROR ="您好！您发起的提现操作未成功，请重新提交";
        public final static String REGISTER_SUCCESS ="您好！您邀请的朋友已注册成功，为感谢您对当家装修的支持，已将优惠券放在您的券包，请查收。";
        public final static String RED_ABOUT_TO_EXPIRE ="您好！您有一张优惠券即将过期，请查看。";





        public final static String STEWARD_TWO_REPLACE ="大管家您好！经多方协调，【%s】已将您更换，请重新抢单，再接再厉！";
        public final static String STEWARD_TWO_RUSH_TO_PURCHASE ="大管家您好！【%s】已有工匠抢单，赶快去看看吧~";
        public final static String STEWARD_CRAFTSMAN_TWO_REPLACE ="大管家您好！【%s】业主已更换工匠，请查看";
        public final static String STEWARD_CRAFTSMAN_TWO_ABANDON ="大管家您好！【%s】工匠已放弃，请查看";
        public final static String STEWARD_CRAFTSMAN_TWO_PAYMENT ="大管家您好！业主已支付【%s】工匠费用，请及时查看";
        public final static String CRAFTSMAN_TWO_REPLACE ="工匠您好！经多方协调，【%s】已将您更换，请重新抢单，再接再厉！";
        public final static String OWNER_TWO_FINISHED ="业主您好！【%s】【%s】阶段/整体完工已通过大管家的审核，请查看并对工匠和大管家分别评分，谢谢！";





        public final static String ACTUARIAL_COMPLETION ="业主您好！您的美宅【%s】精算已完成，请等待大管家抢单。";
        public final static String REPLACEMENT_OF_CRAFTSMEN ="工匠您好！经多方协调，【%s】已更换工匠，请查看!";


    }
}
