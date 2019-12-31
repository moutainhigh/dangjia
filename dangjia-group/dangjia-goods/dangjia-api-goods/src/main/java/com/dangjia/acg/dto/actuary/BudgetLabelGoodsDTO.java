package com.dangjia.acg.dto.actuary;

import com.dangjia.acg.modle.order.DeliverOrderAddedProduct;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 * author: qiyuxiang
 * Date: 2019-09-21
 */
@Data
public class BudgetLabelGoodsDTO {

    private String id;//精算ID
    private String houseFlowId;//工序ID
    private String houseId;//房子ID
    private String workerTypeId;//工种ID
    private Integer steta;//1代表我们购,2代表自购,3代表模板
    private Integer buy;//购买性质0：必买；1：可选；2：自购
    private Integer sales;//退货性质0：可退；1：不可退
    private Integer deleteState;//用户删除状态·,0表示未支付，1表示已删除,2表示业主取消,3表示已经支付,4再次/更换购买,5 被更换
    private String productId;//商品ID
    private String productSn;//商品编号
    private String productName;//商品名称
    private String attributeName;//规格名称
    private String goodsId;//货品ID
    private String storefontId;//店铺ID

    private BigDecimal price;//销售单价
    private Double shopCount;//精算数
    private String unitName;//单位名称
    private BigDecimal totalPrice;//销售总价
    private String isInflueDecorationProgress;//是否影响装修进度(1是，0否)

    private String isReservationDeliver;//是否业主预约发货(1是，0否)
    private Date createDate;//新建时间
    private Date modifyDate;//修改时间
    private Integer productType;// 0：材料；1：服务；2：人工；3：体验；4：增值；5：维保
    private String categoryId;//分类ID
    private String image;//商品图片
    private String originalProductId;//更换前最初始的商品ID
    private String goodsGroupId;//商品关联组ID


    //增值商品集合
    private List<DeliverOrderAddedProduct> addedProducts;

}
