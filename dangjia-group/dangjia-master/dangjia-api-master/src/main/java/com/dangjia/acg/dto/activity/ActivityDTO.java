package com.dangjia.acg.dto.activity;

import com.dangjia.acg.modle.activity.ActivityDiscount;
import com.dangjia.acg.modle.activity.ActivityRedPack;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * author: qiyuxiang
 * Date: 2018/11/6 0006
 * Time: 17:56
 */
@Data
public class ActivityDTO {

    protected String id;

    @ApiModelProperty("创建时间")
    protected Date createDate;// 创建日期

    @ApiModelProperty("修改时间")
    protected Date modifyDate;// 修改日期

    @ApiModelProperty("活动名")
    private String name;

    @ApiModelProperty("优惠类型 0为直推 1为注册 2为邀请")
    private int redPacketType;//

    @ApiModelProperty("活动开始时间")
    private Date startDate;//

    @ApiModelProperty("活动结束时间")
    private Date endDate;//


    @ApiModelProperty("备注说明适用范围")
    private String remake;//

    @ApiModelProperty("活动图片")
    private String vimage;//

    @ApiModelProperty("活动专链")
    private String vurl;//


    @ApiModelProperty("地区专享")
    private String cityId;//


    @ApiModelProperty("状态，0正常，1停用")
    private int deleteState;

    @ApiModelProperty("活动优惠券")
    private List<ActivityRedPackDTO> discounts;


}
