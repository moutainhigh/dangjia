package com.dangjia.acg.dto.supplier;

import com.dangjia.acg.common.model.BaseEntity;
import lombok.Data;

@Data
public class DjSupplierDTO extends BaseEntity {
    /**
     * 供应商名称
     */
    private String name;
    /**
     * 供应商地址
     */
    private String address;
    /**
     * 联系人
     */
    private String checkPeople;
    /**
     * 联系电话
     */
    private String telephone;
    /**
     * 审核状态
     */
    private String applicationStatus;
    /**
     * 供应商ID
     */
    private String supId;
    /**
     * 店铺ID
     */
    private String shopId;

}
