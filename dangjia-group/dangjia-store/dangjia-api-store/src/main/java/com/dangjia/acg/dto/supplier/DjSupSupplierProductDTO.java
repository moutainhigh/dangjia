package com.dangjia.acg.dto.supplier;

import com.dangjia.acg.common.model.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 11/10/2019
 * Time: 下午 2:17
 */
@Data
public class DjSupSupplierProductDTO extends BaseEntity {

    private String goodsId;//商品id
    private String goodsName;//商品名称
    private String productId;//货品id
    private String productName;//货品名称
    private String image;//货品模板图片
    private String attributeIdArr;//标签id逗号分隔
    private Double price;//价格
    private Integer stock;//库存
    private Double supplyPrice;//供应价
    private Double salesPrice;//销售价
    private String isCartagePrice;//是否收取上楼费
    private String supplyRelationship;//供应关系 0:供应 1:停供
    private Double adjustPrice;//调后价格
    private Date adjustTime;//调价时间
    private String valueNameArr;
    private String valueIdArr;
    private Double porterage;//搬运费
    private String supId;
    private String shopId;
    /**
     * 城市ID
     */
    private String cityId;

}
