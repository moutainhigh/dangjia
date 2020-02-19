package com.dangjia.acg.dto.supplier;

import lombok.Data;

import java.util.Date;

/**
 * 供应商结算货单
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/5/25
 * Time: 16:29
 */
@Data
public class DjSupplierDeliverDTO {
    private String id;//货单ID
    private String splitId;//货单ID
    private String number;//货单号
    private String shipAddress;//地址
    private String memberName;//地址
    private String memberMobile;//地址
    private Double totalAmount;//货单金额
    private Double applyMoney;//结算金额
    private Integer applyState;//供应商申请结算的状态 0申请中(待处理)；1不通过(驳回)；2通过(同意)
    private Integer deliverType;//货单类型 1发货单；2退货单

}
