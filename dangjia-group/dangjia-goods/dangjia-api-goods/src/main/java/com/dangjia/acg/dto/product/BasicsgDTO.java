package com.dangjia.acg.dto.product;

import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Data
@FieldNameConstants(prefix = "")
public class BasicsgDTO {

    private String name;
    private String abwId;
    private String sName;
    private Double price;
    private String image;
    private String unitName;
    private String totalPrice;
    private String houseId;
    private Integer steta;
    private String workerGoodsId;
    private String workerGoodsSn;


    private String houseFlowId;
    private String workerTypeId;//工种ID   3: 大管家 ，4：拆除 ，5：  ，6：水电 ，7：泥工 ，8：木工 ，9：油漆
    private String templateId; // 模板信息Id
    private Integer deleteState;//用户删除状态·,0表示未支付，1表示已删除,2表示业主取消,3表示已经支付,4再次/更换购买,5 被更换
    private String productId;//货号ID
    private String productSn;// 货号编号
    private String productName;//货号名称
    private String productNickName;//货品昵称
    private String goodsId;//商品Id
    private String goodsName;// 商品名称
    private Double cost;// 成本价
    private String description; //页面描述 备注
    private Double shopCount;//购买总数 (精算的时候，用户手动填写的购买数量， 该单位是 product 的convertUnit换算单位 )
    private Double convertCount;
    private String groupType; //null：单品；有值：关联组合  （如果 null 并且 goods_group_id 不为null， 说明是关联组的单品则不参与关联组切换）
    private Integer productType; //0：材料；1：包工包料
    private String goodsGroupId; //所属关联组
    private String categoryId;//分类id


}
