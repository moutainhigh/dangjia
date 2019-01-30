package com.dangjia.acg.dto.finance;

import lombok.Data;

import java.util.Date;

/**
 * author: ysl
 * Date: 2019/1/25 0018
 * Time: 14:41
 * 供应商发货信息详情
 */
@Data
public class WebSplitDeliverItemDTO {
    private String splitDeliverId;//发货表 id
    //    private Date submitTime;//下单时间
//    private Integer tol;//多少种商品
//    private List<SplitDeliverItemDTO> splitDeliverItemDTOList;
    private String supMobile;//供货商电话
    private String supName;//供应商名字
    /************************************************/
    private String number;//发货单号
    //    private String houseId;//房子ID
    private Double totalAmount;//发货单总额
    private Double applyMoney;//供应商申请结算的价格
    private Integer applyState;//供应商申请结算的状态 0申请中(待处理)；1不通过(驳回)；2通过(同意)
    //    private String shipName;//收货人姓名
//    private String shipMobile;//收货手机
    private String shipAddress;//收货地址
    private String supplierId;//供应商id
    private Date sendTime; //发货时间
    //    private Integer supState;//大管家可收货状态(0:大管家不可收货;1:大管家可收货)
//    private Integer shipState;//配送状态（0待发货,1已发待收货,2已收货,3取消,4部分收）
    private Date modifyDate;//收货时间
    private Date createDate;// 创建日期

    private Integer curWeekAddNum;//本周新增
    private Integer curWeekSuccessNum;//本周 成功处理的
    private Integer curWeekNoHandleNum;//本周 待处理的
    private Integer allNoHandleNum;//所有待处理的
}
