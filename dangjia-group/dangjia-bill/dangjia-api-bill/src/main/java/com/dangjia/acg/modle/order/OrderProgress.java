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
@Table(name = "dj_deliver_order_progress")
@ApiModel(description = "订单进度信息")
@FieldNameConstants(prefix = "")
public class OrderProgress extends BaseEntity {

    @Column(name = "progress_order_id")
    @Desc(value = "订单ID（各种订单）")
    @ApiModelProperty("订单ID（各种订单）")
    private String progressOrderId;

    @Column(name = "progress_type")
    @Desc(value = "订单类型（1订单，2退货单，3补货单，4其它）")
    @ApiModelProperty("订单类型（1订单，2退货单，3补货单，4其它）")
    private String progressType;

    @Column(name = "node_type")
    @Desc(value = "节点类型")
    @ApiModelProperty("节点类型")
    private String nodeType;

    @Column(name = "node_code")
    @Desc(value = "节点编码")
    @ApiModelProperty("节点编码")
    private String nodeCode;

    @Column(name = "create_by")
    @Desc(value = "创建人")
    @ApiModelProperty("创建人")
    private String createBy;

    @Column(name = "update_by")
    @Desc(value = "修改人")
    @ApiModelProperty("修改人")
    private String updateBy;
}
