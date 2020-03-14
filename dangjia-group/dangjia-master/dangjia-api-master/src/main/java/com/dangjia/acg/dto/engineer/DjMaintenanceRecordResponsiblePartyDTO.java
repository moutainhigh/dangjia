package com.dangjia.acg.dto.engineer;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 13/12/2019
 * Time: 上午 11:13
 */
@Data
public class DjMaintenanceRecordResponsiblePartyDTO {

    private String id;
    private String responsiblePartyId;//责任方ID(店铺/供应商/工匠 id)
    private String maintenanceRecordId;//维保记录表id
    private Double proportion;//维保责任方占比
    private Integer responsiblePartyType;//维保责任方类型 1:店铺 2:工匠
    private String responsiblePartyName;//责任方名称
    private String image;//责任方图片
    private String complainId;//申诉单ID
}
