package com.dangjia.acg.modle.order;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 14/10/2019
 * Time: 上午 10:00
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

@Data
@Entity
@Table(name = "dj_deliver_order_node")
@ApiModel(description = "订单节点信息")
@FieldNameConstants(prefix = "")
public class OrderNode extends BaseEntity {

    @Column(name = "type")
    @Desc(value = "类型")
    @ApiModelProperty("类型")
    private String type;

    @Column(name = "code")
    @Desc(value = "编码")
    @ApiModelProperty("编码")
    private String code;

    @Column(name = "name")
    @Desc(value = "名称")
    @ApiModelProperty("名称")
    private String name;

    @Column(name = "node_describe")
    @Desc(value = "描述")
    @ApiModelProperty("描述")
    private String nodeDescribe;

    @Column(name = "associated_operation")
    @Desc(value = "关联操作编码")
    @ApiModelProperty("关联操作编码")
    private String associatedOperation;

    @Column(name = "associated_operation_name")
    @Desc(value = "关联操作名称")
    @ApiModelProperty("关联操作名称")
    private String associatedOperationName;


    @Column(name = "remark")
    @Desc(value = "备注")
    @ApiModelProperty("备注")
    private String remark;

    @Column(name = "sort")
    @Desc(value = "排序")
    @ApiModelProperty("排序")
    private Integer sort;
}
