package com.dangjia.acg.dto.delivery;

import lombok.Data;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * author: LJL
 * Date: 2020-1-10
 * Time: 上午 10:43
 */
@Data
public class WorkerGoodsInFoDTO {
    private String name;//工匠名称
    private String workerId;//工匠id
    private Date createDate;//创建时间
    private String image;//图片
    private String workerGoodsName;//商品名称
    private Double price;//单价
    private Double totalAmount;//订单总额
    private Integer orderStatus;//'订单状态（1待付款，2已付款，3待收货，4已完成，5已取消，6已退货，7已关闭）',
    private String doiId;//订单详情id
    private String orderNumber;//订单号
    private String ddoId;//订单id
    private String houseId;//房子id
    private String styleName;//规格
    private String unitName;//单位

    private String productTemplateId;//商品模板ID
    private Integer totalNodeNumber;//总节点数
    private Integer completedNodeNumber;//已完成节点
}
