package com.dangjia.acg.modle.core;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;
/**
 * 实体类 - 工种排期表
 */
@Data
@Entity
@Table(name = "dj_core_house_flow")
@ApiModel(description = "工序")
public class HouseFlow extends BaseEntity {

    @Column(name = "city_id")
    @Desc(value = "城市ID")
    @ApiModelProperty("城市ID")
    private String cityId;

	@Column(name = "worker_type_id")
    @Desc(value = "工种类型ID")
    @ApiModelProperty("工种类型ID")
	private String workerTypeId;//workertyid

    @Column(name = "worker_type")
    @Desc(value = "工种类型")
    @ApiModelProperty("工种类型")
    private int workerType;//workertype

	@Column(name = "member_id")
	@Desc(value = "用户ID")
	@ApiModelProperty("用户ID")
	private String memberId;//memberid

	@Column(name = "house_id")
	@Desc(value = "房间ID")
	@ApiModelProperty("房间ID")
	private String houseId;//houseid

	@Column(name = "house_worker_id")
	@Desc(value = "houseWorkerId")
	@ApiModelProperty("houseWorkerId")
	private String houseWorkerId;//housewokerid

	@Column(name = "house_worker_order_id")
	@Desc(value = "houseWorkerOrderId")
	@ApiModelProperty("houseWorkerOrderId")
	private String houseWorkerOrderId;  //housewokerorderid

	@Column(name = "worker_id")
	@Desc(value = "工人Id")
	@ApiModelProperty("工人Id")
	private String workerId;//workerid

	@Column(name = "state")
	@Desc(value = "0可用排期，2禁用，3删除")
	@ApiModelProperty("0可用排期，2禁用，3删除")
	private Integer state;

	@Column(name = "grab_lock")
	@Desc(value = "抢单锁·0可抢，2不可以抢")
	@ApiModelProperty("抢单锁·0可抢，2不可以抢")
	private Integer grablock;

	@Column(name = "sort")
	@Desc(value = "实际排期顺序")
	@ApiModelProperty("实际排期顺序")
	private int sort;

	@Column(name = "refuse_number")
	@Desc(value = "被拒人数")
	@ApiModelProperty("被拒人数")
	private Integer refuseNumber;//refusemumber

    @Column(name = "grab_number")
    @Desc(value = "抢单人数")
    @ApiModelProperty("抢单人数")
	private Integer grabNumber;//grabmumber

    @Column(name = "work_type")
    @Desc(value = "默认0, 抢单状态，1还没有发布，只是默认房产,2等待被抢，3有工匠抢单,4已采纳已支付")
    @ApiModelProperty("默认0, 抢单状态，1还没有发布，只是默认房产,2等待被抢，3有工匠抢单,4已采纳已支付")
	private Integer workType;  //worktype

    @Column(name = "release_time")
    @Desc(value = "发布时间")
    @ApiModelProperty("发布时间")
	private Date releaseTime;//releaseTime

    @Column(name = "work_steta")
    @Desc(value = "施工状态，0未开始 ，1阶段完工通过，2整体完工通过，3待交底，4施工中，5收尾施工")
    @ApiModelProperty("施工状态，0未开始 ，1阶段完工通过，2整体完工通过，3待交底，4施工中，5收尾施工")
	private Integer workSteta;//worksteta

    @Column(name = "evaluate_steta")
    @Desc(value = "评价状态,0未开始，1已评价，没有评价")
    @ApiModelProperty("评价状态,0未开始，1已评价，没有评价")
	private Integer evaluateSteta;//evaluatesteta

    @Column(name = "repair_state")
    @Desc(value = "0代表没有补货要审核，1代表有有补货要审核，2代表补货已经处理")
    @ApiModelProperty("0代表没有补货要审核，1代表有有补货要审核，2代表补货已经处理")
	private Integer repairState;//replenishmentstate

    @Column(name = "worker_repair_state")
    @Desc(value = "0代表没有补人工要审核，1代表有补人工要审核，2代表补人工已经处理")
    @ApiModelProperty("0代表没有补人工要审核，1代表有补人工要审核，2代表补人工已经处理")
	private Integer workerRepairState;//workerreplenishmentstate

    @Column(name = "safe")
    @Desc(value = "0没有，1有")
    @ApiModelProperty("0没有，1有")
	private Integer safe;

    @Column(name = "safe_shop_state")
    @Desc(value = "是否购买保险，0没有，1有")
    @ApiModelProperty("是否购买保险，0没有，1有")
	private Integer safeShopState;//safeshopstate

    @Column(name = "safe_id")
    @Desc(value = "保险类型")
    @ApiModelProperty("保险类型")
	private String safeId;//safeid

    @Column(name = "choose")
    @Desc(value = "选择保险")
    @ApiModelProperty("选择保险")
	private int choose;//choose

    @Column(name = "pause")
    @Desc(value = "施工状态0正常,1暂停")
    @ApiModelProperty("施工状态0正常,1暂停")
	private int pause;

    @Column(name = "lock_worker")
    @Desc(value = "指定工匠 1已指定")
    @ApiModelProperty("指定工匠 1已指定")
	private Integer lockWorker;//

    @Column(name = "material_price")
    @Desc(value = "材料钱 支付后才会保存")
    @ApiModelProperty("材料钱 支付后才会保存")
	private BigDecimal materialPrice; //goodstotal

    @Column(name = "work_price")
    @Desc(value = "工钱 支付后才会保存")
    @ApiModelProperty("工钱 支付后才会保存")
	private BigDecimal workPrice;//workertotal

    @Column(name = "total_price")
    @Desc(value = "总钱 工钱+材料 支付后才会保存")
    @ApiModelProperty("总钱 工钱+材料 支付后才会保存")
	private BigDecimal totalPrice;//goodstotalprice

    @Column(name = "self_price")
    @Desc(value = "自购材料钱 支付后才会保存")
    @ApiModelProperty("自购材料钱 支付后才会保存")
	private BigDecimal selfPrice;//selfprice

    @Column(name = "supervisor_start")
    @Desc(value = "工种为大管家时：0未开工；1已开工")
    @ApiModelProperty("工种为大管家时：0未开工；1已开工")
	private int supervisorStart;

    @Column(name = "scan_code")
    @Desc(value = "扫码图片地址")
    @ApiModelProperty("扫码图片地址")
	private String scanCode;//

    @Column(name = "past")
    @Desc(value = "二维码生成时间")
    @ApiModelProperty("二维码生成时间")
	private Date past;//

    @Column(name = "latitude")
    @Desc(value = "工匠经纬度")
    @ApiModelProperty("工匠经纬度")
	private String latitude;

    @Column(name = "longitude")
    @Desc(value = "工匠经纬度")
    @ApiModelProperty("工匠经纬度")
	private String longitude;

    @Column(name = "patrol")
    @Desc(value = "巡查次数")
    @ApiModelProperty("巡查次数")
	private int patrol;//

    @Column(name = "patrol_money")
    @Desc(value = "每次巡查拿钱 这里只针对大管家")
    @ApiModelProperty("每次巡查拿钱 这里只针对大管家")
	private BigDecimal patrolMoney;//

    @Column(name = "check_money")
    @Desc(value = "每次验收该拿的钱 这里只针对大管家")
    @ApiModelProperty("每次验收该拿的钱 这里只针对大管家")
	private BigDecimal checkMoney;//

    public HouseFlow(){
        this.grablock = 0;
        this.refuseNumber = 0;
        this.grabNumber = 0;
        this.workSteta = 0;//0未开始
        this.evaluateSteta = 0;
        this.repairState = 0;
        this.workerRepairState = 0;
        this.safeShopState = 0;
        this.pause = 0;
        this.lockWorker = 0;//0未指定工匠
        this.materialPrice = new BigDecimal(0.0);//材料钱
        this.workPrice = new BigDecimal(0.0);//工钱总数
        this.totalPrice = new BigDecimal(0.0);// 工钱加材料
        this.selfPrice = new BigDecimal(0.0);//自购材料钱
        this.supervisorStart = 0;
    }
}