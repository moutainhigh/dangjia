package com.dangjia.acg.dto.product;

import lombok.Data;

@Data
public class BasicsProductTemplateRatioDTO {

    private String id;//责任占比表ID

    private String productTemplateId;//商品模板ID

    private Integer productResponsibleType;//责任方类型（1商家，2工种）

    private String productResponsibleId;//商品责任方ID(末级分类ID或工种ID)

    private String productResponsibleName;//商品责任方名称(末级分类名称或工种名称)

    private Double productRatio;//商品责任方占比（%）

    private String remark;//备注

}
