package com.dangjia.acg.dto.deliver;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2018/12/27 0027
 * Time: 14:06
 */
@Data
public class BusinessOrderDTO {
    private String businessOrderId;//业务订单id
    private String houseName;//
    private Date createDate;// 创建日期
    private String number;//订单号
    private List<OrderDTO> orderDTOList;
    private BigDecimal totalPrice;//应付
    private BigDecimal discountsPrice;//优惠
    private BigDecimal payPrice;//实付
    private Double carriage;//运费
}
