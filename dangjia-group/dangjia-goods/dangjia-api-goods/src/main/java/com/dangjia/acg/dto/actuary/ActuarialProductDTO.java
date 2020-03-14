package com.dangjia.acg.dto.actuary;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.annotation.ExcelField;
import com.dangjia.acg.modle.GoodsBaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
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

    @ApiModelProperty("类别ID")
    private String categoryId;

    @ApiModelProperty("商品编码")
    @ExcelField(titile = "商品编码", offset = 1)
    private String productSn;

    @ApiModelProperty("商品数量")
    @ExcelField(titile = "商品数量", offset = 3)
    private String purchaseQuantity;

    @ApiModelProperty("商品类型（0材料商品，2服务商品，3人工商品）")
    @ExcelField(titile = "商品类型", offset = 4)
    private String prodType;

    @ApiModelProperty("工种类型（1设计师，2精算师，3大管家,4拆除，6水电，8泥工,9木工，10油漆工）")
    private String workerTypeId;

    @ApiModelProperty("是否按面积计算参考价格(1是，0否)")
    private String isCalculatedArea;

    @ApiModelProperty("是否默认推荐(1是，0否)")
    private String defaultRecommend;

    @ApiModelProperty("是否隐藏(1是，0否)")
    private String isHide;



}
