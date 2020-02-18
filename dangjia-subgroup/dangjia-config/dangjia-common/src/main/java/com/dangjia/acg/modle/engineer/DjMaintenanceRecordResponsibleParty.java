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
    @Desc(value = "责任方ID(店铺/工匠 id)")
    @ApiModelProperty("责任方ID(店铺/工匠 id)")
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
    @Desc(value = "维保责任方类型 1:店铺 2:工匠")
    @ApiModelProperty("维保责任方类型 1:店铺 2:工匠")
    private Integer responsiblePartyType;

    @Column(name = "total_price")
    @Desc(value = "维保商品总额(包含运费，搬运费）")
    @ApiModelProperty("维保商品总额(包含运费，搬运费）")
    private Double totalPrice;

    @Column(name = "stevedorage_cost")
    @Desc(value = "搬运费")
    @ApiModelProperty("搬运费")
    private Double stevedorageCost;

    @Column(name = "transportation_cost")
    @Desc(value = "运费")
    @ApiModelProperty("运费")
    private Double transportationCost;

    @Column(name = "maintenance_total_price")
    @Desc(value = "维保分摊总额")
    @ApiModelProperty("维保分摊总额")
    private Double maintenanceTotalPrice;

    @Column(name = "appeal_proportion")
    @Desc(value = "申诉后维保责任方占比")
    @ApiModelProperty("申诉后维保责任方占比")
    private Double appealProportion;

    @Column(name = "appeal_maintenance_total_price")
    @Desc(value = "申诉后的维保分摊总额")
    @ApiModelProperty("申诉后的维保分摊总额")
    private Double appealMaintenanceTotalPrice;

    @Column(name = "complain_id")
    @Desc(value = "申诉单ID")
    @ApiModelProperty("申诉单ID")
    private String complainId;

}
