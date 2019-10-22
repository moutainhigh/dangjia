package com.dangjia.acg.dto.supplier;

import com.dangjia.acg.common.model.BaseEntity;
import lombok.Data;

@Data
public class DjSupplierDTO  {

    private String id;
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
    /**
     *   是否非平台供应商（1是，0否）
     */
    private Integer isNonPlatformSupperlier;

    /**
     *   失败原因
     */
    private String failReason;
    /**
     *   合同地址
     */
    private String contract;

}
