package com.dangjia.acg.dto.delivery;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class OrderCollectInFoDTO {
    private String id;//订单id

    @ApiModelProperty("安装人姓名")
    private String installName;

    @ApiModelProperty("安装人号码")
    private String installMobile;

    @ApiModelProperty("送货人姓名")
    private String deliveryName;

    @ApiModelProperty("送货人号码")
    private String deliveryMobile;

    private String houseName;//房子名称

    @ApiModelProperty("订单号")
    private String number;

    private Date createDate;//创建时间

    @ApiModelProperty("下单时间")
    private Date submitTime;

    @ApiModelProperty("发货时间")
    private Date sendTime; //

    @ApiModelProperty("收货时间")
    private Date recTime;

    private Double totalAmount;//总价格

    private String storefrontName;//店铺名称
    private String storefrontIcon;//店铺图标

    private List<Map<String,Object>> list;//商品详情list

    private String total;//件数
    private Integer itemListSize;
    private String image;
    private String houseId;

}
