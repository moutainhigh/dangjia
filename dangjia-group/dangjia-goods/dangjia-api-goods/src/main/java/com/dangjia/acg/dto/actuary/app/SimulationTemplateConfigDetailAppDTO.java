package com.dangjia.acg.dto.actuary.app;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Date: 2019/9/20
 * Time: 16:26
 */
@Data
public class SimulationTemplateConfigDetailAppDTO {



    @ApiModelProperty("列表详情Id")
    private  String titleDetailId;

    @ApiModelProperty("编码")
    private String code;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("图片")
    private String image;

    @ApiModelProperty("图片地址")
    private String imageUrl;

    @ApiModelProperty("标签名称，多个用逗号分隔")
    private String labelName;
}
