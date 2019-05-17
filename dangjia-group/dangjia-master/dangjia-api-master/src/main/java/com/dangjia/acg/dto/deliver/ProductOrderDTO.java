package com.dangjia.acg.dto.deliver;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * author: Yinjianbo
 * Date: 2019-5-16
 * 中台-补退换货流程
 */
@Data
public class ProductOrderDTO {

    private String id;
    private String houseId;
    // 换货单号
    private String number;
    // 换货时间
    private Date createDate;
    //收货地址
    private String address;
    // 业主ID
    private String memberId;
    //业主姓名
    private String memberName;
    //业主电话
    private String memberMobile;
    //类型
    private String type;
    //合计退差价
    private BigDecimal totalDifferPrice;

    private List<ProductChangeItemDTO> productChangeItemDTOList;
}
