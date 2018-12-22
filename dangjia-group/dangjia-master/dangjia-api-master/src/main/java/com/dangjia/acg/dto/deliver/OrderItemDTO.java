package com.dangjia.acg.dto.deliver;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2018/12/20 0020
 * Time: 16:38
 */
@Data
public class OrderItemDTO {
    private String orderId;//订单id  订单号
    private String houseName;//
    private Date createDate;// 创建日期

    private BigDecimal totalPrice;//该订单总价
    private BigDecimal discountsPrice;//优惠钱
    private BigDecimal payPrice;//实付
    private Double carriage;//运费

    private List<ItemDTO> itemDTOList;
}
