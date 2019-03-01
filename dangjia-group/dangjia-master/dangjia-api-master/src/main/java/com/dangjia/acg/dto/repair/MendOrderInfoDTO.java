package com.dangjia.acg.dto.repair;

import com.dangjia.acg.modle.repair.MendMateriel;
import com.dangjia.acg.modle.repair.MendWorker;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2018/12/18 0018
 * Time: 11:09
 */
@Data
public class MendOrderInfoDTO {
    protected String id;

    @ApiModelProperty("创建时间")
    protected Date createDate;// 创建日期

    @ApiModelProperty("修改时间")
    protected Date modifyDate;// 修改日期

    @ApiModelProperty("数据状态 0=正常，1=删除")
    protected int dataStatus;

    @ApiModelProperty("订单号")
    private String number;

    @ApiModelProperty("变更单id")
    private String changeOrderId;//新增字段 补退人工限制时需要使用

    @ApiModelProperty("照片")
    private String imageArr;

    @ApiModelProperty("业务订单号")
    private String businessOrderNumber;

    @ApiModelProperty("订单描述")
    private String orderName;

    @ApiModelProperty("房间ID")
    private String houseId;

    @ApiModelProperty("工种ID")
    private String workerTypeId;

    @ApiModelProperty("申请人id")
    private String applyMemberId;

    @ApiModelProperty("0:补材料;1:补人工;2:退材料(剩余材料登记);3:退人工,4:业主退材料")
    private Integer type;

    @ApiModelProperty("0生成中,1处理中,2不通过取消,3已通过,4已结算")
    private Integer state;

    @ApiModelProperty("订单总额")
    private Double totalAmount;

    @ApiModelProperty("运费")
    private Double carriage;

    private List<MendWorker> mendWorkers;
    private List<MendMateriel> mendMateriels;
}
