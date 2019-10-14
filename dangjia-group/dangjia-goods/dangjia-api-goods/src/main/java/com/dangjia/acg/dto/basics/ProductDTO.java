package com.dangjia.acg.dto.basics;

import com.dangjia.acg.common.annotation.ExcelField;
import com.dangjia.acg.common.model.BaseEntity;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

/**
 * 货品下的商品实体类
 * @author Ronalcheng
 */
@Data
@FieldNameConstants(prefix = "")
public class ProductDTO extends BaseEntity {

    private String goodsId;
    private String goodsName;
    private String productId;
    private String productName;
    private String unitName;
    private String productType;

    private String storefrontId;//店铺ID

    private String labelId;
    private String labelName;
    private String buy;
    @ExcelField(titile = "精算数", offset = 8)
    private String shopCount;
    @ExcelField(titile = "货号编号", offset = 2)
    private String productSn;//商品编号
    private String msg;

    
}