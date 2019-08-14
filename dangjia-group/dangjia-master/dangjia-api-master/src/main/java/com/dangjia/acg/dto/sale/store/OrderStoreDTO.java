package com.dangjia.acg.dto.sale.store;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/7/25
 * Time: 13:56
 */
@Data
public class OrderStoreDTO {
    private String storeId;
    private String storeName;
    private String cityId;
    private String cityName;
    private String storeAddress;
    private String reservationNumber;
    private String departmentId;
    private String departmentName;
    private String latitude;
    private String longitude;
    private String scopeItude;
    private String villages;
    private String userId;
    private Integer juli;//门店距离
    private String robDate;//抢单时间
}
