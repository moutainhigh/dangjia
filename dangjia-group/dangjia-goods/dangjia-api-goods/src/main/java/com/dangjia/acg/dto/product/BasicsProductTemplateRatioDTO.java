package com.dangjia.acg.dto.product;

import lombok.Data;

@Data
public class BasicsProductTemplateRatioDTO {

    private String id;//责任占比表ID

    private String productTemplateId;//商品模板ID

    private String productResponsibleId;//商品责任方ID(0店铺，其它为工种ID)

    private Double productRatio;//商品责任方占比（%）

    private String remark;//备注

}
