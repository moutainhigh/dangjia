package com.dangjia.acg.dto.storefront;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 14/10/2019
 * Time: 下午 4:58
 */
@Data
public class StorefrontProductListDTO {
    private String productSn;
    private String productId;
    private String image;
    private String productName;
    /**
     * 城市ID
     */
    private String cityId;

}
