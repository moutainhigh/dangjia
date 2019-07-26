package com.dangjia.acg.dto.house;

import com.dangjia.acg.common.annotation.ExcelField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class WareDTO {

    protected String id;

    @ApiModelProperty("创建时间")
    protected Date createDate;// 创建日期

    @ApiModelProperty("修改时间")
    protected Date modifyDate;// 修改日期

    @ApiModelProperty("数据状态 0=正常，1=删除")
    protected int dataStatus;

    @ApiModelProperty("房子ID")
    private String houseId;

    @ExcelField(titile = "业主名字", offset = 1)
    @ApiModelProperty("业主名字")
    private String userName;

    @ExcelField(titile = "工地地址", offset = 2)
    @ApiModelProperty("工地地址")
    private String address;

    @ExcelField(titile = "货号名称", offset = 3)
    @ApiModelProperty("货号名称")
    private String productName;

    @ExcelField(titile = "货号编号", offset = 4)
    @ApiModelProperty("货号编号")
    private String productSn;

    @ExcelField(titile = "购买总数", offset = 5)
    @ApiModelProperty("买总数")
    private Double shopCount; //买总数 = repairCount + stayCount + robCount


    @ExcelField(titile = "精算总数", offset = 6)
    @ApiModelProperty("精算总数")
    private Double budgetCount;

    @ExcelField(titile = "已要总数", offset = 7)
    @ApiModelProperty("已要总数")
    private Double askCount;

    @ExcelField(titile = "收货总数", offset = 8)
    @ApiModelProperty("收货总数")
    private Double receive;//收货总数

    @ExcelField(titile = "未发货数", offset = 9)
    @ApiModelProperty("未发")
    private Double noSend;//未发

    @ExcelField(titile = "剩余可要货数", offset = 10)
    @ApiModelProperty("剩余可要货数")
    private Double leftAskCount;//剩余可要货数

    @ExcelField(titile = "补总数", offset = 11)
    @ApiModelProperty("补总数")
    private Double repairCount;

    @ExcelField(titile = "工人退货", offset = 12)
    @ApiModelProperty("工人退")
    private Double workBack;//工人退

    @ExcelField(titile = "业主退货", offset = 13)
    @ApiModelProperty("业主退")
    private Double ownerBack;//业主退

    @ExcelField(titile = "已使用的数", offset = 14)
    @ApiModelProperty("已使用的数")
    private Double useCount;//已使用

    @ApiModelProperty("待付款进来总数")
    private Double stayCount;

    @ApiModelProperty("抢单任务进来总数")
    private Double robCount;

    @ApiModelProperty("退总数")
    private Double backCount;

    @ApiModelProperty("货品id")
    private String productId;




    @ApiModelProperty("销售价")
    private Double price;

    @ApiModelProperty("成本价")
    private Double cost;

    @ExcelField(titile = "单位", offset = 14)
    @ApiModelProperty("单位")
    private String unitName;

    @ApiModelProperty("0：材料；1：服务")
    private Integer productType; //0：材料；1：服务

    @ApiModelProperty("分类id")
    private String categoryId;

    @ApiModelProperty("货品图片")
    private String image;

    @ApiModelProperty("支付次数")
    private Integer payTime;//支付次数

    @ApiModelProperty("要货次数")
    private Integer askTime;//要货次数

    @ApiModelProperty("补次数")
    private Integer repTime;

    @ApiModelProperty("退次数")
    private Integer backTime;//退次数






}
