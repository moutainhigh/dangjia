package com.dangjia.acg.modle.repair;

/**
 * author: Ronalcheng
 * Date: 2019/1/16 0016
 * Time: 14:43
 */

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
 * author: Ronalcheng
 * Date: 2019/1/16 0016
 * Time: 14:39
 * 补退单类型关联角色
 */
@Data
@Entity
@Table(name = "dj_repair_mend_type_role")
@ApiModel(description = "类型关联角色")
@FieldNameConstants(prefix = "")
public class MendTypeRole extends BaseEntity {

    @Column(name = "type")
    @Desc(value = "0:补材料;1:补人工;2:退材料(剩余材料登记);3:退人工,4:业主退材料")
    @ApiModelProperty("0:补材料;1:补人工;2:退材料(剩余材料登记);3:退人工,4:业主退材料")
    private Integer type;

    @Column(name = "role_arr")
    @Desc(value = "角色集合")
    @ApiModelProperty("角色集合")
    private String roleArr; //1业主,2管家,3工匠,4材料员,5供应商
}
