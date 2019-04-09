package com.dangjia.acg.dto.deliver;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 *
 *  指定房子所有发货的供应商
 * author: qiyuxiang
 * Date: 2019/04/09
 * Time: 17:00
 *
 */
@Data
public class SplitReportSupplierDTO {

    private String supplierId;//供应商ID
    private String supplierName;//供应商名称
    private String supplierTelephone;//供应商电话
    private String houseId;//房子ID
    private BigDecimal totalPrice;//总售价
    private BigDecimal totalProfit;//总利润

    private List<SplitReportDeliverOrderDTO> splitReportDeliverOrderDTOS;
}
