package com.dangjia.acg.dto.repair;

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


    private String applicantName;//申请人名称
    private String applicantMobile;//申请人手机号

    private List<MendMateriel> list;//退货的商品

    private Integer count;//退货商品件数

    private Double sumprice;//退货商品价格

}
