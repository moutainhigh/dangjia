package com.dangjia.acg.dto.refund;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 14/10/2019
 * Time: 上午 10:00
 */

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class OrderProgressDTO {


    @ApiModelProperty("订单ID（各种订单）")
    private String progressOrderId;


    @ApiModelProperty("订单类型（1订单，2退货单，3补货单，4其它）")
    private String progressType;


    @ApiModelProperty("节点类型")
    private String nodeType;


    @ApiModelProperty("节点编码")
    private String nodeCode;

    @ApiModelProperty("节点名称")
    private String nodeName;

    @ApiModelProperty("节点显示状态(1黑勾，2黑叉，3红叉，4灰掉）")
    private int nodeStatus;

    @ApiModelProperty("节点描述")
    private String nodeDescribe;

    @ApiModelProperty("可操作编码")
    private String associatedOperation;

    @ApiModelProperty("可操作编码描述")
    private String associatedOperationName;

    @ApiModelProperty("创建人")
    private String createBy;


    @ApiModelProperty("修改人")
    private String updateBy;

    @ApiModelProperty("创建时间")
    private Date createDate;

    @ApiModelProperty("修改时间")
    private Date modifyDate;
}
