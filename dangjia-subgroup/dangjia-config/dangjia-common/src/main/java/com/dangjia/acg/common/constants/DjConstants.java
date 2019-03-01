package com.dangjia.acg.common.constants;

import java.math.BigDecimal;

/**
 * 公共应用常量类
 */
public class DjConstants {
    /**
     * PageAddress 工匠端页面跳转地址
     */
    public static class GJPageAddress {


        public final static String SERVICEMANAGE ="serviceManage?userToken=%s&cityId=%s&title=%s";// 服务管理
        public final static String ADDARTIFICIAL ="addArtificial?userToken=%s&cityId=%s&title=%s";// 补人工
        public final static String CHANGEARTIFICIAL ="changeArtificial?userToken=%s&cityId=%s&title=%s";// 人工变更
        public final static String MATERIALCONSUMPTION ="materialConsumption?userToken=%s&cityId=%s&title=%s";// 材料用量
        public final static String HPMANAGE ="hpManage?userToken=%s&cityId=%s&title=%s";//货品管理
        public final static String MYINVITECODE ="myInviteCode?userToken=%s&cityId=%s&title=%s";//我的邀请码
        public final static String HPDETAIL ="hpDetail?userToken=%s&cityId=%s&title=%s";//货品详情
        public final static String REQUIREGOODSLIST ="requireGoodsList?userToken=%s&cityId=%s&title=%s";//要货(补货、退货)
        public final static String COMFIRMGOODSLIST ="comfirmGoodsList?userToken=%s&cityId=%s&title=%s";//要货确认
        public final static String CONFIRMBTWORKER ="confirmBTworker?userToken=%s&cityId=%s&title=%s";//补退人工
        public final static String BTRECORDLIST ="btRecordList?userToken=%s&cityId=%s&title=%s";//补货退货记录
        public final static String BTRECORDDETAIL ="btRecordDetail?userToken=%s&cityId=%s&title=%s";//补货退货记录详情
        public final static String GJGYLIST ="gjgyList?userToken=%s&cityId=%s&title=%s";//工价工艺
        public final static String GJGYDETAIL ="gjgyDetail?userToken=%s&cityId=%s&title=%s";//工价工艺详情
        public final static String GJGYGOODSDETAIL ="gjgyGoodsDetail?userToken=%s&cityId=%s&title=%s";//商品详情(工价工艺)
        public final static String RECIVEGOODSLIST ="reciveGoodsList?userToken=%s&cityId=%s&title=%s";//收货
        public final static String RECIVEGOODSDETAIL ="reciveGoodsDetail?userToken=%s&cityId=%s&title=%s";//收货清单
        public final static String PARTGOODSRECIVE ="partGoodsRecive?userToken=%s&cityId=%s&title=%s";//部分收货
        public final static String INTEGRATIONLIST ="integrationList?userToken=%s&cityId=%s&title=%s";//积分记录
        public final static String JUGLELIST ="jugleList?userToken=%s&cityId=%s&title=%s";//评价记录
        public final static String GJREGISTERPROTOCOL ="gjRegisterProtocol?userToken=%s&cityId=%s&title=%s";//注册协议
        public final static String PROJECTDRAWINGLIST ="projectDrawingList?userToken=%s&cityId=%s&title=%s";//施工图
        public final static String QRCODE ="qrCode?userToken=%s&cityId=%s&title=%s";//二维码
        public final static String PROJECTRECORD ="projectRecord?userToken=%s&cityId=%s&title=%s";//工地记录
        public final static String PROJECTADRESSLIST ="projectAdressList?userToken=%s&cityId=%s&title=%s";//工地通讯录
        public final static String GJJINGSUANLIST ="jsCraftsman?userToken=%s&cityId=%s&title=%s";//精算
        public final static String CONFIRMQIANGDAN ="confirmQiangdan?userToken=%s&cityId=%s&title=%s";//抢单确认
        public final static String GJMANAGERJINGSUAN ="gjManagerJingSuan?userToken=%s&cityId=%s&title=%s";//精算
        public final static String GJMANAGERISOK ="gjManagerIsOk?userToken=%s&cityId=%s&title=%s";//已抢单
        public final static String COMFIRMAPPLY ="comfirmApply?userToken=%s&cityId=%s&title=%s";//审核申请
        public final static String COMFIRMSTOPPROJECT ="comfirmStopProject?userToken=%s&cityId=%s&title=%s";//审核停工
        public final static String COMFIRMFAIL ="comfirmFail?userToken=%s&cityId=%s&title=%s";//审核不通过
        public final static String READPROJECTINFO ="readProjectInfo?userToken=%s&cityId=%s&title=%s";//阅读交底项目
        public final static String ABOUTUS ="aboutUs?userToken=%s&cityId=%s&title=%s";//关于我们
        public final static String JIANGFALIST ="jiangFaList?userToken=%s&cityId=%s&title=%s";//奖罚记录
        public final static String JIANGFADETAIL ="jiangFaDetail?userToken=%s&cityId=%s&title=%s";//奖罚详情
        public final static String COMPALIN ="compalin?userToken=%s&cityId=%s&title=%s";//申诉
        public final static String HELPCENTER ="helpCenter?userToken=%s&cityId=%s&title=%s";//帮助中心
        public final static String ORDERRECORD ="orderRecord?userToken=%s&cityId=%s&title=%s";//接单记录
        public final static String AFFIRMGRAB ="affirmGrab?userToken=%s&cityId=%s&title=%s";//抢单确认页
        public final static String PROCESSREQUIRE ="processRequire?userToken=%s&cityId=%s&title=%s";//工艺要求
        public final static String PROCESSDETAIL ="processDetail?userToken=%s&cityId=%s&title=%s";//工艺详情
        public final static String GJPRICE ="gjPrice?userToken=%s&cityId=%s&title=%s";//工匠报价
        public final static String CASHRECORD ="cashRecord?userToken=%s&cityId=%s&title=%s";//提现记录
        public final static String MYTASK ="myTask?userToken=%s&cityId=%s&title=%s";//我的任务
        public final static String MYBANKCARD ="myBankCard?userToken=%s&cityId=%s&title=%s";//银行卡（添加）
        public final static String MYBANKCARDYES ="myBankCardYes?userToken=%s&cityId=%s&title=%s";//银行卡（已添加）
        public final static String GJBJDETAILS ="gjbjDetails?userToken=%s&cityId=%s&title=%s";//工匠报价详情
        public final static String JFREGULATIONS ="jfRegulations?userToken=%s&cityId=%s&title=%s";//选择奖罚条例
        public final static String JFREASON ="jfReason?userToken=%s&cityId=%s&title=%s";//奖罚原因
        public final static String JSCRAFTSMAN ="jsCraftsman?userToken=%s&cityId=%s&title=%s";//精算（工匠）
        public final static String JSDETAILS ="jsDetails?userToken=%s&cityId=%s&title=%s";//精算（工匠）详情
    }

    /**
     * PageAddress 业主端页面跳转地址
     */
    public static class YZPageAddress {
        //        public final static String *-="*-?userToken=%s&cityId=%s&title=%s";//施工现场
        //        public final static String *-="*-?userToken=%s&cityId=%s&title=%s";//参考报价
        //        public final static String **-="**-?userToken=%s&cityId=%s&title=%s";//联系客服
        public final static String INDEX ="index?userToken=%s&cityId=%s&title=%s";//首页
        public final static String GJDETAIL ="gjDetail?userToken=%s&cityId=%s&title=%s";//直连工匠
        public final static String TMGJDETAIL ="tmgjDetail?userToken=%s&cityId=%s&title=%s";//透明工价
        public final static String BGGYDETAIL ="bggyDetail?userToken=%s&cityId=%s&title=%s";//标杆工艺
        public final static String BGGYMARKDETAIL ="bggyMarkDetail?userToken=%s&cityId=%s&title=%s";//标杆工艺详情
        public final static String YXCLDETAIL ="yxclDetail?userToken=%s&cityId=%s&title=%s";//严选材料
        public final static String NEWDECORATEDETAIL ="newDecorateDetail?userToken=%s&cityId=%s&title=%s";//新装修
        public final static String JGGJDETAIL ="jggjDetail?userToken=%s&cityId=%s&title=%s";//精干工匠
        public final static String QCJGDETAIL ="qcjgDetail?userToken=%s&cityId=%s&title=%s";//全程监管
        public final static String REFERCEPRICEDETAIL ="refercePriceDetail?userToken=%s&cityId=%s&title=%s";//参考估算报价
        public final static String HOUSEINSPECTION ="houseInspection?userToken=%s&cityId=%s&title=%s";//我要验房
        public final static String ZHINAN ="zhinan?userToken=%s&cityId=%s&title=%s";//装修指南
        public final static String CONFIRMAPPLY ="confirmApply?userToken=%s&cityId=%s&title=%s";//审核申请
        public final static String JOBLOCATIONDETAIL ="jobLocationDetails?userToken=%s&cityId=%s&title=%s";//施工现场详情
        public final static String SHIGONG ="shigong?userToken=%s&cityId=%s&title=%s";//施工流程
        public final static String BANKCARDALREADYADD ="bankCardAlreadyAdd?userToken=%s&cityId=%s&title=%s";//我的-银行卡
        public final static String BANDCARD ="bandCard?userToken=%s&cityId=%s&title=%s";//我的-银行卡（add）
        public final static String ORDERLIST ="orderList?userToken=%s&cityId=%s&title=%s";//我的-我的订单
        public final static String ORDERDETAIL ="orderDetail?userToken=%s&cityId=%s&title=%s";//我的-订单详情
        public final static String MYQUALITYASSURANCECARD ="myQualityAssuranceCard?userToken=%s&cityId=%s&title=%s";//我的-我的质保卡
        public final static String MYQUALITYASSURANCECARDDETAILS ="myQualityAssuranceCardDetails?userToken=%s&cityId=%s&title=%s";//我的-我的质保卡详情
        public final static String WORKINGDETAILS ="workingDetails?userToken=%s&cityId=%s&title=%s";//工序记录及工匠详情
        public final static String MYGOODS ="myGoods?userToken=%s&cityId=%s&title=%s";//我购买的
        public final static String MYWAREDETAILS ="myWareDetails?userToken=%s&cityId=%s&title=%s";//我购买的-商品详情
        public final static String MYGOODSAPPLYRETURN ="myGoodsApplyReturn?userToken=%s&cityId=%s&title=%s";//我的商品-申请退款
        public final static String COMFIRMAPPLYRETURN ="comfirmApplyReturn?userToken=%s&cityId=%s&title=%s";//我的商品-确认退款
        public final static String REFUNDLIST ="refundList?userToken=%s&cityId=%s&title=%s";//要补退记录
        public final static String APPLYRETURNDETAIL ="applyReturnDetail?userToken=%s&cityId=%s&title=%s";//我的商品-退款记录详情
        public final static String RECEIVING ="receiving?userToken=%s&cityId=%s&title=%s";//收货
        public final static String CONSIGNMENTGOODS ="consignmentGoods?userToken=%s&cityId=%s&title=%s";//收货清单
        public final static String CONSIGNMENTSHEET ="consignmentSheet?userToken=%s&cityId=%s&title=%s";//部分收货
        public final static String CONFIRMACTUARY ="confirmActuary?userToken=%s&cityId=%s&title=%s";//确认精算
        public final static String CONFIRMACTUARYDETAIL ="confirmActuaryDetail?userToken=%s&cityId=%s&title=%s";//精算明细(材料明细)
        public final static String COMMO ="commo?userToken=%s&cityId=%s&title=%s";//精算-商品详情
        public final static String GOODSDETAIL ="goodsDetail?userToken=%s&cityId=%s&title=%s";//精算-商品详情
        public final static String COMMODITY ="commodity?userToken=%s&cityId=%s&title=%s";//精算-商品更换
        public final static String WAITINGPAYDETAIL ="waitingPayDetail?userToken=%s&cityId=%s&title=%s";//待付款明细
        public final static String MYSHOPLIST ="myShopList?userToken=%s&cityId=%s&title=%s";//自购清单
        public final static String DESIGNLIST ="designList?userToken=%s&cityId=%s&title=%s";//设计图
        public final static String JINGSUANLIST ="jingsuanList?userToken=%s&cityId=%s&title=%s";//精算
        public final static String CONSTUCTIONRECORD ="constuctionRecord?userToken=%s&cityId=%s&title=%s";//施工记录
        public final static String YZREGISTERPROTOCOL ="yzRegisterProtocol?userToken=%s&cityId=%s&title=%s";//注册协议

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
        public final static Integer BU_RENGGONG = 4;// 补人工
        public final static Integer BU_CAILIAO = 5;// 补材料
    }
    /**
     * Type 验房分销定金
     */
    public static class distribution {
        public final static BigDecimal PRICE = new BigDecimal(50.00);
    }
    public static class PushMessage{
        public final static String START_FITTING_UP ="业主您好！您的美宅【%s】开始装修啦！";
        public final static String SNAP_UP_ORDER ="您有一个新的装修订单，马上去抢！";
        public final static String DESIGNER_GRABS_THE_BILL ="业主您好！您的美宅【%s】已有设计师抢单，赶快去看看吧~";
        public final static String PAYMENT_OF_DESIGN_FEE ="设计师您好！业主【%s】已支付【%s】设计费，请及时查看";
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
        public final static String STEWARD_CRAFTSMAN_FINISHED ="业主，您好！【%s】大管家已经与工匠完成开工交底，【%s】工序施工正式开始";
        public final static String CRAFTSMAN_NOT_START ="工匠您好！【%s】今日未开工，请在12点前开工，如有不便需要请假请申请停工。";
        public final static String STEWARD_CRAFTSMEN_APPLY_FOR_STOPPAGE ="大管家您好！【%s】有工匠申请停工，请查看";
        public final static String CRAFTSMAN_NEW_REPLACE ="业主您好！经多方协调，当家装修已为您更换了新的工匠，立即查看";
        public final static String STEWARD_APPLY_FINISHED ="大管家您好！【%s】【%s】申请阶段/整体完工，请审核！";
        public final static String STEWARD_APPLY_FINISHED_NOT_PASS ="工匠您好！您申请的【%s】阶段/整体完工未通过大管家的审核，请及时整改！";
        public final static String STEWARD_APPLY_FINISHED_PASS ="工匠您好！您申请的【%s】阶段/整体完工已通过大管家的审核，请查看。";
        public final static String CRAFTSMAN_EVALUATE ="工匠您好！【%s】业主已对您进行评价，立即查看";
        public final static String CRAFTSMAN_ALL_FINISHED ="业主您好！【%s】所有工匠都已完工，大管家申请竣工验收，请查看。";
        public final static String STEWARD_EVALUATE ="大管家您好！【%s】业主已对您进行评价，立即查看";
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




        public final static String STEWARD_Y_SERVER ="业主您好！【%s】大管家已为您安排了服务发货，请知晓。";
        public final static String STEWARD_T_SERVER ="业主您好！根据【%s】装修施工需要，现场退部分服务商品，请查看。";
        public final static String STEWARD_B_SERVER ="业主您好！工匠根据【%s】装修施工需要，补部分服务商品，请查看并支付";
        public final static String CRAFTSMAN_Y_MATERIAL ="业主您好！【%s】工匠已为您安排了材料发货，请及时收货。";
        public final static String CRAFTSMAN_T_MATERIAL ="业主您好！根据【%s】装修施工需要，现场盘点部分材料退货，具体退货数量以卖家验货为准，请知晓。";
        public final static String CRAFTSMAN_B_MATERIAL ="业主您好！工匠根据【%s】装修施工需要，补部分材料，请查看并支付";

        public final static String CRAFTSMAN_B_WORK ="业主您好！工匠根据【%s】装修施工需要，补部分人工，请查看并支付";
        public final static String CRAFTSMAN_T_WORK ="工匠您好！业主根据【%s】装修施工需要，退部分人工，请审核";

        public final static String STEWARD_B_CHECK_WORK ="大管家您好！【%s】【%s】提出补人工变更，请审核并填写变更数量";
        public final static String STEWARD_T_CHECK_WORK ="大管家您好！【%s】业主提出退人工变更，请审核并填写变更数量";


    }
}
