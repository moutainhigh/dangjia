package com.dangjia.acg.dto.actuary;

import com.dangjia.acg.modle.storefront.Storefront;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2018/11/19 0019
 * Time: 19:52
 * 材料类商品
 */
@Data
public class GoodsDTO {

    private String budgetMaterialId;
    private String srcGroupId;//该product所在的原关联组id
    private int isSwitch;//可切换性0:可切换；1不可切换
    private String productId;
    private Integer sales;//退货性质0：可退；1：不可退
    private String irreversibleReasons;//不可退原因
    private String marketingName;//营销名称
    private String isInflueWarrantyPeriod;//是否影响质保年限（1是，0否）
    private String refundPolicy;//退款政策
    private String guaranteedPolicy;//保修政策
    private String goodsId;
    private String image;//product图 1张
    private String price;//价格加单位
    private String name;
    private String unitName;//单位
    private int productType;//0:材料；1：包工包料；2：人工
    private List<String> imageList;//长图片 多图组合
    private List<BrandDTO> brandDTOList;//品牌系列
    private Integer maket;//是否上架  0:未上架；1已上架
    private List<AttributeDTO> attrList;//品牌，系列，价格属性,
    private String purchaseRestrictions;//购买限制（0自由购房；1有房无精算；2有房有精算）

    private Storefront storefront;//店铺信息
}
