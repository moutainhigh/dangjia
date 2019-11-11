package com.dangjia.acg.dto.supplier;

import com.dangjia.acg.common.annotation.Desc;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;

@Data
public class SupplierLikeDTO {

    private String id;
    private String userId;//用户ID


    private String cityId;//城市ID


    private String name;//供应商名称


    private String address;//供应商地址


    private String checkPeople;//联系人姓名

    private String telephone;//联系电话


    private String email;//邮箱


    private String createBy;//创建人


    private String updateBy;//修改人


    private Integer isNonPlatformSupperlier;


    private Double totalAccount;


    private Double surplusMoney;

    private Double retentionMoney;
}
