package com.dangjia.acg.dto.house;

import lombok.Data;

import java.util.Date;

/**
 * author: QYX
 * Date: 2019/08/01 0015
 * Time: 10:55
 */
@Data
public class WarehouseGoodsDTO {

    private String orderId;//订单ID
    private int type;//订单类型：0:补货单;1:补人工单;2:退货单;3:退人工单,4:业主退,5:发货单
    private String number;//订单编号
    private Date createDate;//时间
    private Integer state;// 订单类型为0-4时：1处理中,2不通过取消,3已通过,4已全部结算,5已撤回
                          //订单类型为5时：配送状态（0待发货,1已发待收货,2已收货,3取消,4部分收,5已结算,6材料员撤回
}
