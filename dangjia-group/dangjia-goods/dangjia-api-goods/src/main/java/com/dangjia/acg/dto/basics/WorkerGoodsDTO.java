package com.dangjia.acg.dto.basics;


import com.dangjia.acg.common.annotation.ExcelField;
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
public class WorkerGoodsDTO {
    @ApiModelProperty("id")
    private String id;
    @ApiModelProperty("name")
    private String name;

    @ApiModelProperty("storefrontId")
    private String storefrontId;//

    private Integer sales;//退货性质0：可退；1：不可退
    private String irreversibleReasons;//不可退原因
    @ExcelField(titile = "商品编号", offset = 2)
    private String workerGoodsSn;
    @ApiModelProperty("image")
    private String image;
    @ApiModelProperty("imageUrl")
    private String imageUrl;
    private String unitId;
    private String unitName;
    @ApiModelProperty("price")
    private Double price;
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

    private int productType;//2=人工商品
    @ApiModelProperty(" 是否置顶 0=正常  1=置顶")
    private String istop;

    private Double lastPrice;
    private Date lastTime;
    private String technologyIds;
    private String considerations;
    private String calculateContent;
    private String buildContent;

    @ExcelField(titile = "精算数", offset = 6)
    private String shopCount;//精算数
    private String msg;//异常说明

    private String purchaseRestrictions;//购买限制（0自由购房；1有房无精算；2有房有精算）
    @ApiModelProperty("technologies")
    private List<TechnologyDTO> technologies;


}
