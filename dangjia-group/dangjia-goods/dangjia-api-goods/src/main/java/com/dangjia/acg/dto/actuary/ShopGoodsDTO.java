package com.dangjia.acg.dto.actuary;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * author: qiyuxiang
 * Date: 2019-09-21
 */
@Data
public class ShopGoodsDTO {

    private String shopId;//店铺ID
    private String shopName;//店铺名称
    private String shopLogo;//店铺LOGO
    private String systemLogo;//店铺系统LOGO
    private BigDecimal totalMaterialPrice;//实物总价

    private Integer productType;// 0：材料；1：服务；2：人工；3：体验；4：增值；5：维保
    private BigDecimal freight;//运费
    private BigDecimal moveDost;//搬运费
    private List<BudgetLabelDTO> labelDTOS;//分类标签下对应的商品集
}
