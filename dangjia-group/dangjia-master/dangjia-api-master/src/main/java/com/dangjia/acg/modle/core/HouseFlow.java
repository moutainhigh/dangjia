package com.dangjia.acg.modle.core;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

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
@FieldNameConstants(prefix = "")
public class HouseFlow extends BaseEntity {
    @Column(name = "city_id")
    @Desc(value = "城市id")
    @ApiModelProperty("城市id")
    private String cityId;//cityid

    @Column(name = "house_id")
    @Desc(value = "房间ID")
    @ApiModelProperty("房间ID")
    private String houseId;//houseid

	@Column(name = "worker_type_id")
    @Desc(value = "工种类型ID")
    @ApiModelProperty("工种类型ID")
	private String workerTypeId;//workertyid

    @Column(name = "worker_type")
    @Desc(value = "工种类型")
    @ApiModelProperty("工种类型")
    private Integer workerType;//workertype

	@Column(name = "worker_id")
	@Desc(value = "抢单的工人Id")
	@ApiModelProperty("抢单的工人Id")
	private String workerId;//workerid

	@Column(name = "state")
	@Desc(value = "0可用排期，2禁用，3删除")
	@ApiModelProperty("0可用排期，2禁用，3删除")
	private Integer state;

	@Column(name = "grab_lock")
	@Desc(value = "抢单锁·0可抢，1已指定工人")
	@ApiModelProperty("抢单锁·0可抢，1指定工人")
	private Integer grabLock;

    @Column(name = "start_date")
    @Desc(value = "开始时间")
    @ApiModelProperty("管家排期的开工开始时间")
    private Date startDate;

    @Column(name = "end_date")
    @Desc(value = "结束时间")
    @ApiModelProperty("管家排期的阶段结束/整体（仅拆除）结束时间")
    private Date endDate;

    @Column(name = "nominator")
    @Desc(value = "指定的工人id")
    @ApiModelProperty("指定的工人id")
    private String nominator;

    @Column(name = "sort")
	@Desc(value = "实际排期顺序")
	@ApiModelProperty("实际排期顺序")
	private Integer sort;

	@Column(name = "refuse_number")
	@Desc(value = "被拒人数")
	@ApiModelProperty("被拒人数")
	private Integer refuseNumber;//refusemumber

    @Column(name = "grab_number")
    @Desc(value = "抢过单人数")
    @ApiModelProperty("抢过单人数")
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
    @Desc(value = "施工状态，0未开始 ，1阶段完工通过，2整体完工通过，3待交底，4施工中，5收尾施工，6提前竣工")
    @ApiModelProperty("施工状态，0未开始 ，1阶段完工通过，2整体完工通过，3待交底，4施工中，5收尾施工，6提前竣工")
	private Integer workSteta;//worksteta

    @Column(name = "pause")
    @Desc(value = "施工状态0正常,1暂停")
    @ApiModelProperty("施工状态0正常,1暂停")
	private Integer pause;

    @Column(name = "material_price")
    @Desc(value = "材料钱 支付后更新")
    @ApiModelProperty("材料钱 支付后更新")
	private BigDecimal materialPrice; //goodstotal

    @Column(name = "work_price")
    @Desc(value = "工钱 支付后更新")
    @ApiModelProperty("工钱 支付后更新")
	private BigDecimal workPrice;//workertotal

    @Column(name = "total_price")
    @Desc(value = "总钱 工钱+材料 支付后更新")
    @ApiModelProperty("总钱 工钱+材料 支付后更新")
	private BigDecimal totalPrice;//goodstotalprice

    @Column(name = "self_price")
    @Desc(value = "自购材料钱 支付后才会保存")
    @ApiModelProperty("自购材料钱 支付后才会保存")
	private BigDecimal selfPrice;//selfprice

    @Column(name = "supervisor_start")
    @Desc(value = "工种为大管家时：0未开工；1已开工")
    @ApiModelProperty("工种为大管家时：0未开工；1已开工")
	private Integer supervisorStart;

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
	private Integer patrol;//

    @Column(name = "patrol_money")
    @Desc(value = "每次巡查拿钱 这里只针对大管家")
    @ApiModelProperty("每次巡查拿钱 这里只针对大管家")
	private BigDecimal patrolMoney;//

    @Column(name = "check_money")
    @Desc(value = "每次验收该拿的钱 这里只针对大管家")
    @ApiModelProperty("每次验收该拿的钱 这里只针对大管家")
	private BigDecimal checkMoney;//

    public HouseFlow(){

    }
    public HouseFlow(Boolean isInit){
        if(isInit){
            this.grabLock = 0;
            this.refuseNumber = 0;
            this.grabNumber = 0;
            this.workSteta = 0;//0未开始
            this.pause = 0;
            this.patrol = 0;
            this.materialPrice = new BigDecimal(0.0);//材料钱
            this.workPrice = new BigDecimal(0.0);//工钱总数
            this.totalPrice = new BigDecimal(0.0);// 工钱加材料
            this.selfPrice = new BigDecimal(0.0);//自购材料钱
            this.supervisorStart = 0;
        }
    }
}