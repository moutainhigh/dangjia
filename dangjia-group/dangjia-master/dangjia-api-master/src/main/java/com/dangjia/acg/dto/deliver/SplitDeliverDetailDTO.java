package com.dangjia.acg.dto.deliver;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2018/12/21 0021
 * Time: 19:32
 */
@Data
public class SplitDeliverDetailDTO {
    private String splitDeliverId;//发货单ID
    private String number;//发货单号
    private String shipName;//收货人姓名
    private String shipAddress;//地址地址
    private String shipMobile;//收货人电话
    private String supMobile;//供应商电话
    private String supName;//供应商名称
    private String memo;//须知
    private String reason;//备注
    private String totalSplitPrice;//销售总价
    private Double totalAmount;//成本总价（包含运费、搬运费)
    private Double totalPrice;//商品总价
    private Double deliveryFee;//运费
    private Double stevedorageCost;//搬运费
    private Double applyMoney;//结算总价（成本总价）
    private int size;//件
    private Integer shippingState;//0待发货,1已发待收货,2已收货,3取消,4部分收,5已结算,6店铺撤回(只待发货才能撤回)7-待安装 8-已完成）9-拒绝收货 10 -待评价
    private Integer applyState;
    private String houseId;
    private Date createDate;//分发时间
    private Integer complainStatus;// 部分收货申诉状态，0未处理，1已认可部分收货，2已申请平台申诉，3平台申诉已通过，4平台申诉被驳回
    private String floor;//楼层
    private Integer elevator;//是否电梯房：0:否，1：是
    private String image;//收货图片显示
    private String imageUrl;//收货地址显示
    private String isNonPlatformSupplier;//是否非平台供应商 1是，0否
    private List<OrderSplitItemDTO> orderSplitItemList;
}
