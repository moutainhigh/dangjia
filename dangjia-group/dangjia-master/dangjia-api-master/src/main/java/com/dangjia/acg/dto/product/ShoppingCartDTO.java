package com.dangjia.acg.dto.product;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class ShoppingCartDTO  implements Serializable {

    private String cityId;//城市编号

    private String memberId;//用户编号

    private String productId;//商品编号

    private String productSn;//商品编号

    private String productName;//货品名称

    private BigDecimal price;//销售单价

    private Integer shopCount;//购买数量

    private String unitName;//单位(个、条、箱、桶、米)

    private String categoryId;//分类编号

    private Integer productType;//0：材料；1：包工包料 2:人工

    private String isCheck;//是否勾选  扩展字段： 是否勾选

    private String seller;//店铺

}
