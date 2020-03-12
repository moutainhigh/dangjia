package com.dangjia.acg.common.constants;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 公共应用常量类
 */
public class DjConstants {

    public static  HashMap<Integer, String> applyTypeMap = new HashMap<Integer, String>(){
        {
            put(ApplyType.MEIRI_WANGGONG, "每日完工");
            put(ApplyType.JIEDUAN_WANGONG, "阶段完工");
            put(ApplyType.ZHENGTI_WANGONG, "整体完工");
            put(ApplyType.TINGGONG, "停工");
            put(ApplyType.MEIRI_KAIGONG, "每日开工");
            put(ApplyType.YOUXIAO_XUNCHA, "巡查");
            put(ApplyType.WUREN_XUNCHA, "巡查");
            put(ApplyType.ZUIJIA_XUNCHA, "巡查");

            put(ApplyType.TIQIAN_JIESU, "提前结束装修");
            put(ApplyType.TIQIAN_JIESU_SHENQ, "提前结束装修申请");
            put(ApplyType.ZHUDONGYANSHOU, "主动验收申请");
        }
    };
    /**
     * PageAddress 工匠端页面跳转地址
     */
    public static class GJPageAddress {


        public final static String JUGLELIST = "jugleList?userToken=%s&cityId=%s&title=%s";//评价记录
        public final static String COMFIRMAPPLY = "comfirmApply?userToken=%s&cityId=%s&title=%s";//审核申请
        public final static String READPROJECTINFO = "readProjectInfo?userToken=%s&cityId=%s&title=%s";//阅读交底项目
        public final static String JIANGFALIST = "jiangFaList?userToken=%s&cityId=%s&title=%s";//奖罚记录
        public final static String AFFIRMGRAB = "affirmGrab?userToken=%s&cityId=%s&title=%s";//抢单确认页
        public final static String JFREGULATIONS = "jfRegulations?userToken=%s&cityId=%s&title=%s";//选择奖罚条例
    }

    /**
     * PageAddress 业主端页面跳转地址
     */
    public static class YZPageAddress {
        public final static String CONFIRMACTUARYDETAIL = "confirmActuaryDetail?userToken=%s&cityId=%s&title=%s";//精算明细(材料明细)
        public final static String COMMO = "commo?userToken=%s&cityId=%s&title=%s";//精算-商品详情
        public final static String GOODSDETAIL = "goodsDetail?userToken=%s&cityId=%s&title=%s";//精算-商品详情
        public final static String COMMODITY = "commodity?userToken=%s&cityId=%s&title=%s";//精算-商品更换

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
        public final static Integer TIQIAN_JIESU = 8;// 提前结束
        public final static Integer TIQIAN_JIESU_SHENQ = 9;// 9提前结束申请
        public final static Integer ZHUDONGYANSHOU = 10;// 主动验收
        public final static Integer JIEDUAN_WANGONG_SUCCESS = 101;// 阶段完工审核
        public final static Integer ZHENGTI_WANGONG_SUCCESS = 102;// 整体完工审核
        public final static Integer NO_PASS = 666;// 整体完工
    }
    /**
     * ApplyType 任务进程-申请类型
     */
    public static class RecordType {
        public final static Integer MEIRI_WANGGONG = 0;//0每日完工申请
        public final static Integer JIEDUAN_WANGONG = 1;// 1阶段完工申请
        public final static Integer ZHENGTI_WANGONG = 2;// 2整体完工申请
        public final static Integer TINGGONG = 3;// 3停工申请
        public final static Integer MEIRI_KAIGONG = 4;// 4：每日开工
        public final static Integer YOUXIAO_XUNCHA = 5;// 有效巡查
        public final static Integer JJ_TINGGONG = 6;// 无人巡查
        public final static Integer TY_TINGGONG = 7;// 追加巡查
        public final static Integer BURENGONG = 8;// 8补人工
        public final static Integer TUIRENGONG = 9;// 9退人工
        public final static Integer BUCAILIAO = 10;// 10补材料
        public final static Integer TUICAILIAO = 11;// 11退材料
        public final static Integer YZ_TUICAILIAO = 12;// 12业主退材料
        public final static Integer TIQIANJIESHU = 13;// 13提前结束装修
        public final static Integer SCSJT = 14;// 上传平面图
        public final static Integer SCSGT = 15;// 上传施工图
        public final static Integer SJWC = 16;// 精算完成

        public final static Map getRecordTypeMap(){
            Map<Integer, String> applyTypeMap = new HashMap<>();
            applyTypeMap.put(RecordType.MEIRI_WANGGONG, "每日完工申请");
            applyTypeMap.put(RecordType.JIEDUAN_WANGONG, "阶段完工申请");
            applyTypeMap.put(RecordType.ZHENGTI_WANGONG, "整体完工申请");
            applyTypeMap.put(RecordType.TINGGONG, "停工申请");
            applyTypeMap.put(RecordType.MEIRI_KAIGONG, "每日开工");
            applyTypeMap.put(RecordType.YOUXIAO_XUNCHA, "有效巡查");
            applyTypeMap.put(RecordType.JJ_TINGGONG, "无人巡查");
            applyTypeMap.put(RecordType.TY_TINGGONG, "追加巡查");
            applyTypeMap.put(RecordType.BURENGONG, "补人工");
            applyTypeMap.put(RecordType.TUIRENGONG, "退人工");
            applyTypeMap.put(RecordType.BUCAILIAO, "补材料");
            applyTypeMap.put(RecordType.TUICAILIAO, "退材料");
            applyTypeMap.put(RecordType.YZ_TUICAILIAO, "业主退材料");
            applyTypeMap.put(RecordType.TIQIANJIESHU, "提前结束装修");
            applyTypeMap.put(RecordType.SCSJT, "上传平面图");
            applyTypeMap.put(RecordType.SCSGT, "上传施工图");
            applyTypeMap.put(RecordType.SJWC, "精算完成");
            return applyTypeMap;
        }
    }

    /**
     * ApplyType 任务进程-申请类型
     */
    public static class VisitState {
        //        0待确认开工,1装修中,2休眠中,3已完工,4提前结束装修 5提前结束装修申请中
        public final static Integer DAIKAIGONG = 0;//0待确认开工
        public final static Integer ZHAUNGXIUZHONG = 1;// 1装修中
        public final static Integer XIUMIANZHONG = 2;// 2休眠中
        public final static Integer WANGONG = 3;// 3已完工
        public final static Integer TIQIANJIESHU = 4;//4提前结束装修
        public final static Integer TIQIANSHENHEZHONG = 5;//5提前结束装修申请中

        public final static Map getVisitStateMap(){
            Map<Integer, String> visitStateMap = new HashMap<>();
            visitStateMap.put(VisitState.DAIKAIGONG, "待确认开工");
            visitStateMap.put(VisitState.ZHAUNGXIUZHONG, "装修中");
            visitStateMap.put(VisitState.XIUMIANZHONG, "休眠中");
            visitStateMap.put(VisitState.WANGONG, "已完工");
            visitStateMap.put(VisitState.TIQIANJIESHU, "提前结束装修");
            visitStateMap.put(VisitState.TIQIANSHENHEZHONG, "提前结束装修申请中");
            return visitStateMap;
        }
    }
    /**
     * Type 工序类型 人工1 材料2 包工包料3
     */
    public static class GXType {
        public final static Integer RENGGONG = 1;// 人工
        public final static Integer CAILIAO = 2;// 材料
        public final static Integer FUWU = 3;// 包工包料
        public final static Integer BU_RENGGONG = 4;// 补人工
        public final static Integer BU_CAILIAO = 5;// 补材料
    }

    /**
     * Type 验房分销定金
     */
    public static class distribution {
        public final static BigDecimal PRICE = new BigDecimal(158.8);
    }
    public static class PushMessage {
        public final static String START_FITTING_UP = "业主您好！您的美宅【%s】开始装修啦！";
        public final static String SNAP_UP_ORDER = "您有一个新的装修订单，马上去抢！";
        public final static String DESIGNER_GRABS_THE_BILL = "业主您好！您的美宅【%s】已有设计师抢单，赶快去看看吧~";
        public final static String BUDGET_GRABS_THE_BILL = "业主您好！您的美宅【%s】已有精算师抢单，赶快去看看吧~";
        public final static String PAYMENT_OF_DESIGN_FEE = "设计师您好！业主【%s】已支付【%s】设计费，请及时查看";
        public final static String PLANE_UPLOADING = "业主您好！您的美宅【%s】平面图已经上传，请确认。";
        public final static String PLANE_ERROR = "抱歉，您设计的【%s】平面图未通过，请联系业主修改后再上传。";
        public final static String PLANE_OK = "恭喜！您设计的【%s】平面图已通过，请联系业主设计施工图。";
        public final static String CONSTRUCTION_UPLOADING = "业主您好！您的美宅【%s】施工图已经上传，请确认。";
        public final static String CONSTRUCTION_ERROR = "抱歉，您设计的【%s】施工图未通过，请联系业主修改后再上传。";
        public final static String CONSTRUCTION_OK = "恭喜！您设计的【%s】施工图已通过，请查看。";
        public final static String ACTUARY_UPLOADING = "业主您好！您的美宅【%s】精算已经上传，请确认。";
        public final static String ACTUARY_ERROR = "抱歉，您设计的【%s】精算未通过，请联系业主修改后再上传。";
        public final static String ACTUARY_OK = "恭喜！您设计的【%s】精算已通过，请查看。";
        public final static String STEWARD_RUSH_TO_PURCHASE = "业主您好！您的美宅【%s】已有大管家抢单，赶快去看看吧~";
        public final static String STEWARD_REPLACE = "您好【%s】！【%s】业主已申请将您更换，请知晓。！";
        public final static String STEWARD_ABANDON = "业主您好！您的美宅【%s】大管家已经放弃，请等待其他大管家抢单。";
        public final static String STEWARD_PAYMENT = "大管家您好！业主已支付【%s】大管家费，请及时查看";
        public final static String STEWARD_CONSTRUCTION = "业主您好！您的美宅【%s】大管家已经做好排期并开始了装修，施工工匠会逐步进场，请即时支付相关费用。";
        public final static String RECORD_OF_REWARDS_AND_PENALTIES = "工匠您好！【%s】有一条您的奖罚记录，请查看。";
        public final static String STEWARD_NEW_REPLACE = "业主您好！经多方协调，当家装修已为您更换了新的大管家，立即查看";
        public final static String CRAFTSMAN_RUSH_TO_PURCHASE = "业主您好！您的美宅【%s】已有工匠抢单，赶快去看看吧~";
        public final static String CRAFTSMAN_ABANDON = "业主您好！您的美宅【%s】【%s】已放弃，请等待新工匠抢单。";
        public final static String CRAFTSMAN_PAYMENT = "工匠您好！业主已支付【%s】工匠费用，请及时查看";
        public final static String STEWARD_CRAFTSMAN_FINISHED = "业主，您好！【%s】大管家已经与工匠完成开工交底，【%s】工序施工正式开始";
        public final static String CRAFTSMAN_NOT_START = "工匠您好！【%s】今日未开工，请在12点前开工，如有不便需要请假请申请停工。";
        public final static String STEWARD_CRAFTSMEN_APPLY_FOR_STOPPAGE = "业主您好！【%s】有工匠停工，请查看";
        public final static String CRAFTSMAN_NEW_REPLACE = "业主您好！经多方协调，当家为您更换新的【%s】，请等待新【%s】抢单。";
        public final static String STEWARD_EXCESS_MATERIAL = "业主您好！工匠根据【%s】装修施工情况，帮您清点了部分可退材料，请查看。";
        public final static String STEWARD_APPLY_FINISHED = "大管家您好！【%s】【%s】申请阶段/整体完工，请审核！";
        public final static String STEWARD_APPLY_FINISHED_NOT_PASS = "工匠您好！您申请的【%s】阶段/整体完工未通过审核，请及时整改！";
        public final static String STEWARD_APPLY_FINISHED_PASS = "工匠您好！您申请的【%s】阶段/整体完工已通过审核，请查看。";
        public final static String CRAFTSMAN_EVALUATE = "工匠您好！【%s】业主已对您进行评价，立即查看";
        public final static String CRAFTSMAN_ALL_FINISHED = "业主您好！您的美宅【%s】大管家已申请竣工验收，请确认。";
        public final static String STEWARD_EVALUATE = "大管家您好！【%s】业主已对您进行评价，立即查看";
        public final static String REFUND_SUCCESS = "业主您好！您发起的退款操作成功，请查看";
        public final static String REFUND_ERROR = "业主您好！您发起的退款操作未成功，请重新提交";
        public final static String WITHDRAW_CASH_SUCCESS = "您好！您发起的提现操作成功，请注意查收";
        public final static String WITHDRAW_CASH_ERROR = "您好！您发起的提现操作未成功，请查看原因，重新提交";
        public final static String REGISTER_SUCCESS = "您好！您邀请的朋友已注册成功，为感谢您对当家装修的支持，已将优惠券放在您的券包，请查收。";
        public final static String RED_ABOUT_TO_EXPIRE = "您好！您有一张优惠券即将过期，请查看。";


        public final static String STEWARD_TWO_REPLACE = "大管家您好！经多方协调，【%s】已将您更换，请重新抢单，再接再厉！";
        public final static String STEWARD_TWO_RUSH_TO_PURCHASE = "大管家您好！【%s】已有工匠抢单，赶快去看看吧~";
        public final static String STEWARD_CRAFTSMAN_TWO_REPLACE = "大管家您好！【%s】业主已更换工匠，请查看";
        public final static String STEWARD_CRAFTSMAN_TWO_ABANDON = "大管家您好！【%s】工匠已放弃，请查看";
        public final static String STEWARD_CRAFTSMAN_TWO_PAYMENT = "大管家您好！业主已支付【%s】工匠费用，请及时查看";
        public final static String CRAFTSMAN_TWO_REPLACE = "工匠您好！经多方协调，【%s】已将您更换，请重新抢单，再接再厉！";
        public final static String OWNER_TWO_FINISHED = "业主您好！【%s】【%s】阶段/整体完工已通过大管家的审核，请查看并对工匠和大管家分别评分，谢谢！";

        public final static String STEWARD_SHENGHECHAOSHI = "大管家您好！【%s】【%s】阶段/整体完工申请，审核倒计时结束，自动扣钱100元，每超出一天再多扣100元！";
        public final static String CRAFTSMAN_ABSENTEEISM = "工匠您好！系统检测到您的工地【%s】今日未开工，将扣除您100元，请知晓！";


        public final static String ACTUARIAL_COMPLETION = "业主您好！您的美宅【%s】精算已完成，请查看并支付。";
        public final static String REPLACEMENT_OF_CRAFTSMEN = "工匠您好！经多方协调，【%s】已更换工匠，请查看!";

        public final static String LIANGFANGWANCHENG = "业主您好！您的美宅【%s】已完成量房，请查看。";
        public final static String JINGSUANFEIZHIFUWANC = "精算师您好！业主已支付【%s】精算费，请及时查看。";
        public final static String DAGUANGJIAXUNCHAWANGCHENG = "业主您好！大管家已经巡查了您的【%s】，请查看。";
        public final static String GONGJIANGTUICAILIAOQINGDIAN = "大管家您好！【%s】【%s】申请了退材料，请联系供应商与您一起到现场清点可退材料。";
        public final static String TUIKWANGCHENG = "业主您好！大管家根据【%s】装修施工情况，帮您退部分多余材料，请查看。（退材料成功后，将退钱到您的钱包）。";
        public final static String YEZHUTUIHUO = "业主您好！您发起的退材料操作成功，退款即将进入您的钱包，请注意查收";

        public final static String YEZHUTUIKUAN = "业主您好！您发起的补人工申请工匠未同意，退款即将进入您的钱包，请注意查收";

        public final static String YEZHUENDMAINTENANCE = "业主您好！您发起的提前结束维保操作成功，退款即将进入您的钱包，请注意查收";


        public final static String STEWARD_Y_SERVER = "业主您好！【%s】大管家已为您安排了包工包料，请知晓。";
        public final static String STEWARD_T_SERVER = "业主您好！根据【%s】装修施工需要，现场退部分包工包料，请查看。";
        public final static String STEWARD_B_SERVER = "业主您好！工匠根据【%s】装修施工需要，补部分包工包料，请查看并支付";
        public final static String CRAFTSMAN_Y_MATERIAL = "业主您好！【%s】工匠已为您安排了材料发货，请及时收货。";
        public final static String CRAFTSMAN_T_MATERIAL = "业主您好！根据【%s】装修施工需要，现场盘点部分材料退货，具体退货数量以卖家验货为准，请知晓。";
        public final static String CRAFTSMAN_B_MATERIAL = "业主您好！根据【%s】装修施工情况，现场需要补部分材料，请查看并支付。";

        public final static String CRAFTSMAN_B_WORK = "业主您好！工匠根据【%s】装修施工需要，补部分人工，请查看并支付";
        public final static String CRAFTSMAN_T_WORK = "工匠您好！业主根据【%s】装修施工需要，退部分人工，请审核";

        public final static String STEWARD_B_CHECK_WORK = "大管家您好！【%s】【%s】提出补人工变更，请审核并填写变更数量";
        public final static String STEWARD_T_CHECK_WORK = "大管家您好！【%s】业主提出退人工变更，请审核并填写变更数量";


        public final static String DGJ_B_001 = "大管家您好！【%s】【%s】申请补人工，请审核并填写数量！";
        //public final static String YZ_Y_001 = "业主您好！【%s】【%s】申请补人工，大管家将审核并填写补人工数量，请知晓！";
        public final static String YZ_Y_001 = "业主您好！【%s】【%s】申请补人工，请审核。";

        public final static String GJ_B_001 = "工匠您好！【%s】补人工申请未通过";
        public final static String GJ_B_002 = "工匠您好！【%s】补人工申请大管家已同意，请查看数量";

        public final static String GJ_B_003 = "工匠您好！【%s】补人工业主审核未通过";
        // public final static String GJ_B_003 = "工匠您好！【%s】补人工申请未通过";
        // public final static String YZ_B_001 = "业主您好！【%s】【%s】申请补人工，大管家审核补人工未通过";
        //public final static String GJ_B_004 = "工匠您好！【%s】补人工成功";
        //工匠您好！补人工成功，请查看。
        public final static String GJ_B_004 = "工匠您好！【%s】补人工业主审核通过";

        public final static String DGJ_T_002 = "大管家您好！【%s】业主申请退人工，请审核并填写数量！";
        //工匠您好！业主申请退人工，请查看并即时处理。
        public final static String GJ_T_003 = "工匠您好！【%s】业主申请退人工，请审核";
        // public final static String GJ_T_003 = "工匠您好！【%s】业主申请退人工，大管家将审核并填写退人工数量，请知晓！";
        public final static String YZ_B_002 = "业主您好！【%s】退人工申请未通过";
        public final static String YZ_T_003 = "业主您好！【%s】退人工申请大管家已同意，请查看数量";
        // public final static String YZ_T_004 = "业主您好！【%s】退人工申请未通过";
        public final static String YZ_T_004 = "业主您好！【%s】退【%s】人工申请未通过";
        //public final static String YZ_T_005 = "业主您好！【%s】退【%s】人工成功";
        //业主您好！XX退人工成功，请查看。
        public final static String YZ_T_005 = "业主您好！【%s】退【%s】人工审核通过";

        public final static String YZ_FN_001 = "业主您好！【%s】装修材料【%s】已发货，请注意查收";
        public final static String YZ_F_001 = "业主您好！【%s】装修材料已发货，请注意查收";
        public final static String YZ_S_001 = "业主您好！【%s】装修材料【%s】已收货";//业主您好！【%s】装修材料已收货
        public final static String YZ_DS_001 = "业主您好！【%s】装修材料已由他人代收货，请在48小时内验货";

        //public final static String YZ_B_010 = "业主您好！【%s】补【%s】人工申请大管家已同意，请查看数量";
        public final static String DGJ_B_010 = "大管家您好！【%s】补【%s】人工申请未通过";
        public final static String DGJ_B_011 = "大管家您好！【%s】补【%s】人工申请未通过";
        public final static String DGJ_B_012 = "大管家您好！【%s】补【%s】人工成功";
        public final static String GJ_T_010 = "工匠您好！【%s】退人工申请大管家已同意，请查看数量";
        public final static String DGJ_T_011 = "大管家您好！【%s】退【%s】人工申请未通过";
        public final static String DGJ_T_012 = "大管家您好！【%s】退【%s】人工申请未通过";
        public final static String DGJ_T_013 = "大管家您好！【%s】退【%s】人工成功";

        public final static String YZ_B_100 = "业主您好！【%s】补【%s】人工成功";
        public final static String GJ_T_100 = "工匠您好！【%s】退人工成功";

        public final static String YZ_B_200 = "工匠您好！业主已主动购买人工请您施工，请确认是否接受。";
        public final static String YZ_B_201 = "业主您好！【%s】工匠已接受您安排的施工项目，请查看。";
        public final static String YZ_B_202 = "业主您好！【%s】工匠没有接受您安排的施工项目，请查看。";


        public final static String GZ_T_WORK = "工匠您好！【%s】图纸已设计完成，请查看";
        public final static String GZ_G_WORK_N = "大管家您好！【%s】竣工验收未通过，请整改后再次申请。";
        public final static String GZ_G_WORK_Y = "大管家您好！【%s】竣工验收已通过，恭喜！";

        public final static String GZ_G_YANSHOU_Y = "业主您好！您的美宅【%s】大管家已申请验收%s，请确认。";
        public final static String YZ_G_YANSHOU_Y = "大管家您好！【%s】主动验收%s已通过，恭喜！";
        public final static String YZ_G_YANSHOU_N = "大管家您好！【%s】主动验收%s未通过，请现场达到标准后再次申请。";


    }

    public static class CommonMessage {
        public final static String YEZHU_ACCEPT = "业主【%s】审核通过了质保验收，请注意查看。";
        public final static String YEZHU_REFUSE = "业主【%s】拒绝了当前申请的质保验收，请注意查看。";

    }
}
