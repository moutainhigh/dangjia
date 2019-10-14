package com.dangjia.acg.dto.delivery;

import com.dangjia.acg.common.model.BaseEntity;
import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 14/10/2019
 * Time: 上午 11:48
 */
@Data
public class DjDeliveryReturnSlipDTO extends BaseEntity {

    private String shopId;
    private String shopName;
    private String storekeeperName;
    private String address;
    private String shipName;
    private String shipMobile;
    private String invoiceStatus;
    private Double totalPrice;
}
