package com.dangjia.acg.modle.member;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.apache.commons.lang.StringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "dj_member")
@FieldNameConstants(prefix = "")
@ApiModel(description = "当家用户表")
public class Member extends BaseEntity {

    @Column(name = "user_name")
    @Desc(value = "用户名")
    @ApiModelProperty("用户名")
    private String userName;// 用户名

    @Column(name = "password")
    @Desc(value = "密码")
    @ApiModelProperty("密码,MD5加密")
    private String password;// 密码

    @Column(name = "nick_name")
    @Desc(value = "昵称")
    @ApiModelProperty("昵称")
    private String nickName;//昵称

    @Column(name = "name")
    @Desc(value = "姓名")
    @ApiModelProperty("姓名")
    private String name;// 姓名

    @Column(name = "mobile")
    @Desc(value = "手机")
    @ApiModelProperty("手机")
    private String mobile;// 手机

    @Column(name = "qrcode")
    @Desc(value = "二维码")
    @ApiModelProperty("二维码")
    private String qrcode;//二维码

    @Column(name = "superiorId")
    @Desc(value = "上级用户id")
    @ApiModelProperty("上级用户id")
    private String superiorId;//上级用户id

    @Column(name = "invite_num")
    @Desc(value = "存放邀约人数")
    @ApiModelProperty("存放邀约人数")
    private Integer inviteNum;//存放邀约人数

    @Column(name = "visit_state")
    @Desc(value = "阶段0未回访，1已开工已下单，2有意向继续跟进，3无装修需求，4恶意操作")
    @ApiModelProperty("阶段0未回访，1已开工已下单，2有意向继续跟进，3无装修需求，4恶意操作")
    private Integer visitState;//visitstate

    @Column(name = "gift")
    @Desc(value = "礼品领取")
    @ApiModelProperty("礼品领取   1为已领取  2为领奖时间过期")
    private Integer gift;// 礼品领取   1为已领取  2为领奖时间过期

    @Column(name = "invitation_code")
    @Desc(value = "自己的邀请码")
    @ApiModelProperty("自己的邀请码")
    private String invitationCode;//自己的邀请码

    @Column(name = "others_invitation_code")
    @Desc(value = "他人邀请码")
    @ApiModelProperty("他人邀请码")
    private String othersInvitationCode;//他人邀请码

    //工匠字段
    @Column(name = "head")
    @Desc(value = "头像")
    @ApiModelProperty("头像")
    private String head; //头像

    @Column(name = "idcaoda")
    @Desc(value = "身份证正面")
    @ApiModelProperty("身份证正面")
    private String idcaoda;//身份证正面

    @Column(name = "idcaodb")
    @Desc(value = "身份证反面")
    @ApiModelProperty("身份证反面")
    private String idcaodb;//身份证反面

    @Column(name = "idcaodall")
    @Desc(value = "半身照")
    @ApiModelProperty("半身照")
    private String idcaodall;//半身照

    @Column(name = "worker_type_id")
    @Desc(value = "工种类型的id")
    @ApiModelProperty("工种类型的id")
    private String workerTypeId;//工种类型的id

    @Column(name = "worker_type")
    @Desc(value = "工种类型")
    @ApiModelProperty("工种类型：【1设计师，2精算师，3大管家,4拆除，5打孔，6水电工，7防水，8泥工,9木工，10油漆工，11安装】")
    private Integer workerType;//工种类型1设计师，2精算师，3大管家,4拆除，5打孔，6水电工，7防水，8泥工,9木工，10油漆工，11安装


    @Column(name = "idnumber")
    @Desc(value = "身份证号")
    @ApiModelProperty("身份证号")
    private String idnumber;//身份证号


    @Column(name = "worker_price")
    @Desc(value = "总工钱")
    @ApiModelProperty("//总工钱(所有订单工钱整体完工相加)")
    private BigDecimal workerPrice;//总工钱(所有订单工钱整体完工相加)

    @Column(name = "have_money")
    @Desc(value = "已获钱")
    @ApiModelProperty("已获钱,可取余额加上押金")
    private BigDecimal haveMoney;//已获钱,可取余额加上押金

    @Column(name = "surplus_money")
    @Desc(value = "可取余额")
    @ApiModelProperty("可取余额,已获钱减押金")
    private BigDecimal surplusMoney;//可取余额,已获钱减押金

    @Column(name = "retention_money")
    @Desc(value = "实际滞留金")
    @ApiModelProperty("实际滞留金")
    private BigDecimal retentionMoney;//实际滞留金

    @Column(name = "deposit")
    @Desc(value = "保证金")
    @ApiModelProperty("保证金，初始默认最多押金100000元,以后根据工匠等级设计")
    private BigDecimal deposit;//初始默认最多押金100000元,以后根据工匠等级设计

    @Column(name = "check_type")//type
    @Desc(value = "审核状态")
    @ApiModelProperty("审核状态:  0审核中，1审核未通过不能抢单不能发申请,  2审核已通过 可抢单可发申请, 3账户已禁用 不能抢单不能发申请,  4账户冻结可发申请 不能抢单,5未提交资料")
    private Integer checkType;

    @Column(name = "praise_rate")//favorable
    @Desc(value = "好评率")
    @ApiModelProperty("好评率")
    private BigDecimal praiseRate;//好评率

    @Column(name = "volume")
    @Desc(value = "成交量")
    @ApiModelProperty("成交量")
    private BigDecimal volume;//成交量

    @Column(name = "evaluation_score")//evaluation
    @Desc(value = "评价积分")
    @ApiModelProperty("评价积分,70分以下押金按每单总额百分之五收取,铜牌工匠70.1-80上限为2000元,银牌工匠80.1-90上限为1500元,金牌工匠90.1~上限为500元")
    private BigDecimal evaluationScore;//评价积分,70分以下押金按每单总额百分之五收取,铜牌工匠70.1-80上限为2000元,银牌工匠80.1-90上限为1500元,金牌工匠90.1~上限为500元


    @Column(name = "is_crowned")//crowned
    @Desc(value = "是否是皇冠")
    @ApiModelProperty("是否是皇冠，0不是，1是")
    private Integer isCrowned;//是否是皇冠，0不是，1是

    @Column(name = "smscode")
    @Desc(value = "验证码")
    @ApiModelProperty("验证码")
    private Integer smscode;//验证码

    @Column(name = "paycode")
    @Desc(value = "提现验证码")
    @ApiModelProperty("提现验证码")
    private Integer paycode;//提现验证码

    @Column(name = "referrals")
    @Desc(value = "推荐人ID")
    @ApiModelProperty("推荐人ID")
    private String referrals;//推荐人ID superior

    @Column(name = "remarks")
    @Desc(value = "备注")
    @ApiModelProperty("备注")
    private String remarks;

    @Column(name = "real_name_state")
    @Desc(value = "实名认证状态")
    @ApiModelProperty("实名认证状态:0:未提交，1:认证中，2:认证被驳回，3:认证通过")
    private Integer realNameState;

    @Column(name = "real_name_describe")
    @Desc(value = "实名认证描述")
    @ApiModelProperty("实名认证描述")
    private String realNameDescribe;

    @Column(name = "check_describe")
    @Desc(value = "工匠审核描述")
    @ApiModelProperty("工匠审核描述")
    private String checkDescribe;

    /*@Column(name = "user_role")
    @Desc(value = "用户角色")
    @ApiModelProperty("用户角色 1为业主角色，2为工匠角色，0为业主和工匠双重身份角色")
    private Integer userRole;//用户角色*/


    /*@Column(name = "workyears")
    @Desc(value = "工作年限")
    @ApiModelProperty("工作年限")
    private String workyears;//工作年限

    @Column(name = "nativeplace")
    @Desc(value = "籍贯")
    @ApiModelProperty("籍贯")
    private String nativeplace;//籍贯

    @Column(name = "address")
    @Desc(value = "现居地址")
    @ApiModelProperty("现居地址")
    private String address;//现居地址

    @Column(name = "self_assessment")//selfassessment
    @Desc(value = "自我评价")
    @ApiModelProperty("自我评价")
    private String selfAssessment;//自我评价

    @Column(name = "specialty")//goodwork
    @Desc(value = "擅长工作")
    @ApiModelProperty("擅长工作")
    private String specialty;//擅长工作*/


    //所有图片字段加入域名和端口，形成全路径
    public void initPath(String address) {
        this.qrcode = StringUtils.isEmpty(this.qrcode) ? null : address + this.qrcode;//二维码
        this.head = StringUtils.isEmpty(this.head) ? null : address + this.head; //头像
        this.idcaoda = StringUtils.isEmpty(this.idcaoda) ? null : address + this.idcaoda;//身份证正面
        this.idcaodb = StringUtils.isEmpty(this.idcaodb) ? null : address + this.idcaodb;//身份证反面
        this.idcaodall = StringUtils.isEmpty(this.idcaodall) ? null : address + this.idcaodall;//半身照
    }

    ;
}
