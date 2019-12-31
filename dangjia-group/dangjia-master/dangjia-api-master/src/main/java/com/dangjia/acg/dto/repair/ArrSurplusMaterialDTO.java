package com.dangjia.acg.dto.repair;

import lombok.Data;

import java.util.Date;
import java.util.List;


@Data
public class ArrSurplusMaterialDTO {
    private String name;//工匠名称
    private String mobile;//工匠手机
    private Date createDate;//申请时间
    private String workerId;//工匠id
    private String houseId;//房子id
    List<SurplusMaterialDTO> list;

    private String returnReason;//备注
    private String storefrontName;//店铺名称
    private String storefrontMobile;//店铺电话
    private String businessOrderNumber;//申请订单号
    private Integer type;//0生成中,1处理中,2不通过取消,3已通过,4已全部结算,5已撤回,6已关闭7，已审核待处理 8，部分退货
    private String mendOrderId;//补退订单表id
}
