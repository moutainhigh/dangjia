package com.dangjia.acg.dto.delivery;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 15/10/2019
 * Time: 下午 3:35
 */
@Data
public class SupplierSettlementManagementDTO {
    private String shopId;
    private String storefrontName;//店铺名称
    private String supId;
    private String contact;//店铺联系电话
    private Integer count;
    private String storekeeperName;//店铺联系人

}
