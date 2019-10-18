package com.dangjia.acg.dto.delivery;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 17/10/2019
 * Time: 下午 5:20
 */
@Data
public class SupplierStoreDimensionDTO {

    private String shopId;
    private String storefrontName;//店铺名称
    private String supId;
    private String contact;//店铺联系电话
    private Integer totalNumberSupply;//总供应数量
    private String storekeeperName;//店铺联系人
    private Double income;//收入
}
