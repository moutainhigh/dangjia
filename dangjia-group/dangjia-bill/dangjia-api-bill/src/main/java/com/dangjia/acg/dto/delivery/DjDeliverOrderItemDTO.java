package com.dangjia.acg.dto.delivery;

import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.util.Date;
@Data
public class DjDeliverOrderItemDTO {


    private String cityId;

    private String orderId;

    private String houseId;

    private String productId;


    private String productSn;


    private String productName;

    private String productNickName;//货品昵称


    private Double price;// 销售价

    private Double cost;// 成本价


    private Double shopCount;//购买总数

    private String unitName;//单位


    private Double totalPrice; //总价


    private Integer productType; //0：材料；1：包工包料


    private String categoryId;//分类id


    private String image;//图片



    private String storefontId;//店铺ID


    private String activityRedPackId;//优惠卷ID


    private Double discountPrice;//优惠价钱


    private Double actualPaymentPrice;//实付价钱


    private Double stevedorageCost;//搬运费


    private Double transportationCost;//运费


    private Double askCount;


    private Double returnCount;


    private Double receiveCount;


    private String isReservationDeliver;//是否预约发货(1是，0否）


    private Date reservationDeliverTime;//预约发货时间

    private String orderStatus;//订单状态（1待付款，2已付款，3待收货，4已完成，5已取消，6已退货，7已关闭）


    private String createBy;//创建人


    private String updateBy;//修改人

    public void initPath(String address){
        this.image = StringUtils.isEmpty(this.image)?null:address+this.image;
    }
}
