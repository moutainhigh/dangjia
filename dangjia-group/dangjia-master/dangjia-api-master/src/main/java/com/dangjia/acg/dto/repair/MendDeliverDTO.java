package com.dangjia.acg.dto.repair;

import com.dangjia.acg.dto.deliver.OrderSplitItemDTO;
import com.dangjia.acg.modle.repair.MendMateriel;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/5/24
 * Time: 17:11
 */
@Data
public class MendDeliverDTO {
    private String mendDeliverId;//退货单ID
    private String mendDeliverNumber;//退货单号
    private String number;//退货单号
    private String mendOrderId;//申请退货单ID
    private String mendOrderNumber;//申请退货单号
    private String houseId;//房ID
    private String houseName;//房名

    private Date createDate;//申请时间

    private String address;//收货地址

    private String memberId;//业主ID
    private String memberName;//业主名称
    private String memberMobile;//业主手机号

    private String supId;//供应商ID
    private String supName;//供应商名称
    private Integer state;//状态

    private String reasons;//退货原因

    private String storefrontId;//店铺ID
    private String storefrontName;//店铺名称
    private String storefrontMobile;//店铺电话

    private String applicantName;//申请人名称
    private String applicantMobile;//申请人手机号

    private List<MendMateriel> list;//退货的商品

    private Integer count;//退货商品件数

    private Double sumprice;//退货商品价格

    private Double totalAmount;//退业主（包含运费、搬运费)
    private Double totalPrice;//供应商总价
    private Double deliveryFee;//运费
    private Double stevedorageCost;//供应商品搬运费
    private Double applyMoney;//退供应商（成本总价）
    private String isNonPlatformSupplier;//是否非平台供应商 1是，0否

    private List<OrderSplitItemDTO> mendMaterielList;//退货商品列表

}
