package com.dangjia.acg.dto.delivery;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class DjDeliverOrderDTO {

    private String id;

    private String houseId;//houseid


    private String businessOrderNumber;


    private BigDecimal totalAmount;


    private String memberId;//memberid


    private String addressId;


    private String workerId;

    private String workerTypeName;//workertypeName

    private String workerTypeId;


    private String styleName;


    private BigDecimal stylePrice;


    private BigDecimal budgetCost;


    private Integer type;

    private String payment;


    private String orderNumber;//订单编号


    private String parentOrderId;//父订单ID


    private String storefontId;//店铺ID


    private String cityId;//城市ID

    private BigDecimal totalDiscountPrice;//优惠总价钱


    private BigDecimal totalStevedorageCost;//总搬运费


    private BigDecimal totalTransportationCost;//总运费


    private String orderType;//订单类型（1设计，精算，2其它）


    private BigDecimal actualPaymentPrice;//实付总价


    private String isPayMoney;//是否可付款（1不可付款，2可付款）



    private String isShowOrder;//是否可付款（1不可付款，2可付款）


    private String orderStatus;//订单状态（1待付款，2已付款，3待收货，4已完成，5已取消，6已退货，7已关闭8待安装）


    private Date orderGenerationTime;//订单生成时间

    private Date orderPayTime;//订单支付时间


    private int orderSource;//订单来源(1,精算制作，2业主自购，3购物车）


    private String createBy;//创建人


    private String updateBy;//修改人


    private String storefrontName;

    private Integer totalSize;

    private List<DjDeliverOrderItemDTO> orderItemlist;

    private List<Map<String, Object>> buttonList;

    private Date createDate ;

    private Date modifyDate ;

    private String dataStatus;

}
