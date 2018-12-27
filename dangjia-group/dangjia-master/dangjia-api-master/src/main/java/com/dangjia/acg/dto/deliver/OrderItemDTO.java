package com.dangjia.acg.dto.deliver;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2018/12/20 0020
 * Time: 16:38
 */
@Data
public class OrderItemDTO {
    private String orderId;//流水号
    private BigDecimal totalAmount;//该订单总价
    private List<ItemDTO> itemDTOList;
}
