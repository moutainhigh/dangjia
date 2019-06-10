package com.dangjia.acg.dto.core;

import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 实体类 - 工种排期表
 */
@Data
public class HouseFlowDTO extends BaseEntity {
    protected String id;

    protected Date createDate;// 创建日期

    protected Date modifyDate;// 修改日期
    @ApiModelProperty("城市id")
    private String cityId;//cityid

    @ApiModelProperty("房间ID")
    private String houseId;//houseid

    @ApiModelProperty("工种类型ID")
	private String workerTypeId;//workertyid

    @ApiModelProperty("工种类型")
    private Integer workerType;//workertype

    @ApiModelProperty("工种类型名称")
    private String workerTypeName;//workertype

	@ApiModelProperty("抢单的工人Id")
	private String workerId;//workerid

	@ApiModelProperty("0可用排期，2禁用，3删除")
	private Integer state;

	@ApiModelProperty("抢单锁·0可抢，1指定工人")
	private Integer grabLock;

    @ApiModelProperty("管家排期的开工开始时间")
    private Date startDate;

    @ApiModelProperty("管家排期的阶段结束/整体（仅拆除）结束时间")
    private Date endDate;

    @ApiModelProperty("指定的工人id")
    private String nominator;

	@ApiModelProperty("实际排期顺序")
	private Integer sort;

	@ApiModelProperty("被拒人数")
	private Integer refuseNumber;//refusemumber

    @ApiModelProperty("抢过单人数")
	private Integer grabNumber;//grabmumber

    @ApiModelProperty("默认0, 抢单状态，1还没有发布，只是默认房产,2等待被抢，3有工匠抢单,4已采纳已支付")
	private Integer workType;  //worktype

    @ApiModelProperty("发布时间")
	private Date releaseTime;//releaseTime

    @ApiModelProperty("施工状态，0未开始 ，1阶段完工通过，2整体完工通过，3待交底，4施工中，5收尾施工，6提前竣工")
	private Integer workSteta;//worksteta

    @ApiModelProperty("施工状态0正常,1暂停")
	private Integer pause;

    @ApiModelProperty("材料钱 支付后更新")
	private BigDecimal materialPrice; //goodstotal

    @ApiModelProperty("工钱 支付后更新")
	private BigDecimal workPrice;//workertotal

    @ApiModelProperty("总钱 工钱+材料 支付后更新")
	private BigDecimal totalPrice;//goodstotalprice
    @ApiModelProperty("自购材料钱 支付后才会保存")
	private BigDecimal selfPrice;//selfprice

    @ApiModelProperty("工种为大管家时：0未开工；1已开工")
	private Integer supervisorStart;

    @ApiModelProperty("二维码生成时间")
	private Date past;//

    @ApiModelProperty("工匠经纬度")
	private String latitude;

    @ApiModelProperty("工匠经纬度")
	private String longitude;

    @ApiModelProperty("巡查次数")
	private Integer patrol;//

    @ApiModelProperty("每次巡查拿钱 这里只针对大管家")
	private BigDecimal patrolMoney;//

    @ApiModelProperty("每次验收该拿的钱 这里只针对大管家")
	private BigDecimal checkMoney;//

}