package com.dangjia.acg.dto.shell;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * author: fzh
 * Date: 2020-02-25
 * Time: 下午 5:18
 */
@Data
public class HomeShellProductSpecDTO {

    @ApiModelProperty("规格ID")
    private String productSpecId;//规格ID

    @ApiModelProperty("商品ID")
    private String productId;//商品ID

    @ApiModelProperty("规格名称")
    private String name;//规格名称

    @ApiModelProperty("贝币")
    private Double integral;

    @ApiModelProperty("金额")
    private Double money;

    @ApiModelProperty("库存数量")
    private Double stockNum;//

    @ApiModelProperty("已兑换数")
    private Double convertedNumber;//

}
