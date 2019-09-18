package com.dangjia.acg.dto.product;


import com.dangjia.acg.common.annotation.ExcelField;
import com.dangjia.acg.dto.basics.TechnologyDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author Ruking.Cheng
 * @descrilbe 工价商品用户展示类
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2018/9/13 下午6:05
 */
@Data
@ApiModel
public class ProductWorkerDTO {
    @ApiModelProperty("id")
    private String id;
    @ApiModelProperty("name")
    private String name;

    @ExcelField(titile = "商品编号", offset = 2)
    private String productSn;
    @ApiModelProperty("image")
    private String image;
    @ApiModelProperty("imageUrl")
    private String imageUrl;
    private String unitId;
    private String unitName;
    @ApiModelProperty("price")
    private Double price;
    @ApiModelProperty("sales")
    private Integer sales;
    @ApiModelProperty("workExplain")
    private String workExplain;
    @ApiModelProperty("workerDec")
    private String workerDec;
    @ApiModelProperty("workerDecUrl")
    private String workerDecUrl;
    @ApiModelProperty(" 是否置顶 0=正常  1=置顶")
    private String istops;
    @ApiModelProperty("workerStandard")
    private String workerStandard;
    @ApiModelProperty("workerTypeId")
    private String workerTypeId;
    @ApiModelProperty("workerTypeName")
    private String workerTypeName;
    @ApiModelProperty("showGoods")
    private Integer showGoods;
    @ApiModelProperty("createDate")
    private String createDate;
    @ApiModelProperty("modifyDate")
    private String modifyDate;
    @ApiModelProperty("otherName")
    private String otherName;




}
