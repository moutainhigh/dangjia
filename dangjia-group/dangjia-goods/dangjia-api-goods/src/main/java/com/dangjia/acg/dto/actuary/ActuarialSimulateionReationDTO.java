package com.dangjia.acg.dto.actuary;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Date: 2019/9/20
 * Time: 16:26
 */
@Data
public class ActuarialSimulateionReationDTO {


    @ApiModelProperty("组合ID")
    private String id;
    @ApiModelProperty("excel名称")
    private String excelName;

    @ApiModelProperty("excel地址")
    private String excelAddress;

    @ApiModelProperty("excel详情地址")
    private String excelAddressUrl;

    @ApiModelProperty("组合编码")
    private String simulationCodeGroup;

    @ApiModelProperty("组合名称")
    private String simulationNameGroup;


}
