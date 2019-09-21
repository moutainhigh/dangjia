package com.dangjia.acg.dto.actuary;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;


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
    private Integer deleteState;//用户删除状态·,0表示未支付，1表示已删除,2表示业主取消,3表示已经支付,4再次/更换购买,5 被更换
    private String productId;//商品ID
    private String productSn;//商品编号
    private String productName;//商品名称
    private BigDecimal price;//销售单价
    private Double shopCount;//精算数
    private String unitName;//单位名称
    private BigDecimal totalPrice;//销售总价
    private Date createDate;//新建时间
    private Date modifyDate;//修改时间
    private Integer productYype;// 0：材料；1：包工包料；2：人工
    private String categoryId;//分类ID
    private String image;//商品图片
}
