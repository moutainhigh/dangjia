package com.dangjia.acg.dto.repair;

import com.dangjia.acg.modle.repair.ChangeOrder;
import com.dangjia.acg.modle.repair.MendMateriel;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/5/24
 * Time: 17:11
 */
@Data
public class MendDeliverDTO {
    private String mendDeliverId;
    private String number;//单号


    private String houseId;//房ID
    private String houseName;//房名

    private String memberId;//业主ID
    private String memberName;//业主名称
    private String memberMobile;//业主手机号


    private String applicantName;//申请人名称
    private String applicantMobile;//申请人手机号

    private List<MendMateriel> list;//退货的商品

    private Integer count;//退货商品件数

    private Double sumprice;//退货商品价格

}
