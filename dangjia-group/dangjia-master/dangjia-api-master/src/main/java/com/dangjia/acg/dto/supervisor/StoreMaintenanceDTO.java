package com.dangjia.acg.dto.supervisor;

import lombok.Data;

/**
 * 店铺详情
 */
@Data
public class StoreMaintenanceDTO {
    private String systemLogo;//头像
    private String storefrontName;//名称
    private String storefrontDesc;//描述
    private String proportion;//占比
}
