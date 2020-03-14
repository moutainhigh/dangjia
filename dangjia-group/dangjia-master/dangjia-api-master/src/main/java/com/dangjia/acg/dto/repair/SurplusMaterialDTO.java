package com.dangjia.acg.dto.repair;

import lombok.Data;

import java.util.Date;


@Data
public class SurplusMaterialDTO {
    private String name;//工匠名称
    private String mobile;//工匠手机
    private Date createDate;//申请时间
    private String image;//商品图片
    private String productName;//商品名称
    private String productId;//商品id
    private Double price;//单价
    private Double receive;//收货数量
    private Double shopCount;//购买总数（退货数）
    private String taskStackId;//任务id
    private String mendOrderId;//补退订单表id
    private String mendMaterielId;//补退材料表id
    private String orderSplitItemId;//要货单详情id
    private String workerId;//工匠id
    private String houseId;//房子id
    private String orderSplitId;//要货单id
    private String storefrontId;//店铺id

    private String returnReason;//备注
    private String actualCount;//实际总数(商家退货数)
    private String unitName;//单位
    private String storefrontName;//店铺名称
    private String storefrontMobile;//店铺电话
    private String businessOrderNumber;//申请订单号
    private Integer state;
    private Integer type;//0生成中,1处理中,2不通过取消,3已通过,4已全部结算,5已撤回,6已关闭7，已审核待处理 8，部分退货
}
