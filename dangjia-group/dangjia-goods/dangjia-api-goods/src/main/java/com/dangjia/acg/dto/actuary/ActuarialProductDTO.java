package com.dangjia.acg.dto.actuary;

import com.dangjia.acg.common.annotation.ExcelField;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Date: 2019/9/20
 * Time: 16:26
 */
@Data
public class ActuarialProductDTO  {


    @ApiModelProperty("阶段ID")
    private String id;

    private String actuarialTemplateId;//阶段模板ID

    @ApiModelProperty("货品ID")
    private String goodsId;

    @ApiModelProperty("商品ID")
    private String productId;

    @ApiModelProperty("商品编码")
    @ExcelField(titile = "商品编码", offset = 1)
    private String productSn;

    @ApiModelProperty("商品编码")
    @ExcelField(titile = "商品编码", offset = 3)
    private String purchaseQuantity;

    @ApiModelProperty("商品编码")
    @ExcelField(titile = "商品编码", offset = 4)
    private String prodType;

    @ApiModelProperty("工种类型（1设计师，2精算师，3大管家,4拆除，6水电，8泥工,9木工，10油漆工）")
    private String workerTypeId;



}
