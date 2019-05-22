package com.dangjia.acg.dto.deliver;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * author: Yinjianbo
 * Date: 2019-5-14
 */
@Data
public class ProductChangeOrderDTO {
    private String id;
    private String houseId;
    // 订单号
    private String number;
    // 总价差额
    private BigDecimal differencePrice;
    //0未支付 1已支付 2已退款
    private Integer type;
    // 创建日期
    private Date createDate;
    // 修改日期
    private Date modifyDate;
    private List<ProductChangeDTO> productChangeDTOList;
}
