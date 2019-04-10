package com.dangjia.acg.dto.deliver;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * author: qiyuxiang
 * Date: 2019/04/09
 * Time: 17:00
 */
@Data
public class SplitReportDeliverOrderDTO {


    private String number;//要货单号
    private Date createDate;//要货时间
    private String supervisorId;
    private String supervisorName;
    private String supervisorTel;

    private BigDecimal totalPrice;//总售价
    private BigDecimal totalProfit;//总利润
    private List<SplitReportDeliverOrderItemDTO> splitReportDeliverOrderItemDTOS;

}
