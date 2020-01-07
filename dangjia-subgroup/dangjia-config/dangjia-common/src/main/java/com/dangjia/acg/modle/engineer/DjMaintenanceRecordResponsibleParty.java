package com.dangjia.acg.modle.engineer;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 13/12/2019
 * Time: 上午 9:39
 */
@Data
@Entity
@Table(name = "dj_maintenance_record_responsible_party")
@FieldNameConstants(prefix = "")
@ApiModel(description = "维保责任方记录")
public class DjMaintenanceRecordResponsibleParty extends BaseEntity {

    @Column(name = "responsible_party_id")
    @Desc(value = "责任方ID(店铺/供应商/工匠 id)")
    @ApiModelProperty("责任方ID(店铺/供应商/工匠 id)")
    private String responsiblePartyId;

    @Column(name = "maintenance_record_id")
    @Desc(value = "维保记录表id")
    @ApiModelProperty("维保记录表id")
    private String maintenanceRecordId;

    @Column(name = "proportion")
    @Desc(value = "维保责任方占比")
    @ApiModelProperty("维保责任方占比")
    private Double proportion;

    @Column(name = "responsible_party_type")
    @Desc(value = "维保责任方类型 1:店铺 2:供应商 3：工匠")
    @ApiModelProperty("维保责任方类型 1:店铺 2:供应商 3：工匠")
    private Integer responsiblePartyType;

    @Column(name = "product_id")
    @Desc(value = "商品ID")
    @ApiModelProperty("商品ID")
    private String productId;

    @Column(name = "responsible_party_type")
    @Desc(value = "维保分摊总额")
    @ApiModelProperty("维保分摊总额")
    private Double maintenanceTotalPrice;

}
