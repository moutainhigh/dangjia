package com.dangjia.acg.modle.house;

import com.dangjia.acg.common.annotation.Desc;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * author: Ronalcheng
 * Date: 2019/4/9 0009
 * Time: 17:19
 */
@Data
@Entity
@Table(name = "dj_house_material_record")
@ApiModel(description = "管家材料登记")
@FieldNameConstants(prefix = "")
public class MaterialRecord {
    @Column(name = "house_id")
    @Desc(value = "房子ID")
    @ApiModelProperty("房子ID")
    private String houseId;

    @Column(name = "worker_type_id")
    @Desc(value = "工种id")
    @ApiModelProperty("工种id")
    private String workerTypeId;

    @Column(name = "apply_type")
    @Desc(value = "0每日完工申请，1阶段完工申请，2整体完工申请,3停工申请，4：每日开工,5有效巡查,6无人巡查,7追加巡查")
    @ApiModelProperty("0每日完工申请，1阶段完工申请，2整体完工申请,3停工申请，4：每日开工,5有效巡查,6无人巡查,7追加巡查")
    private Integer applyType;

    @Column(name = "num")
    @Desc(value = "数量")
    @ApiModelProperty("数量")
    private Double num;

    @Column(name = "product_id")
    @Desc(value = "货号ID")
    @ApiModelProperty("货号ID")
    private String productId;

    @Column(name = "product_sn")
    @Desc(value = "货号编号")
    @ApiModelProperty("货号编号")
    private String productSn;

    @Column(name = "product_name")
    @Desc(value = "货号名称")
    @ApiModelProperty("货号名称")
    private String productName;
}
