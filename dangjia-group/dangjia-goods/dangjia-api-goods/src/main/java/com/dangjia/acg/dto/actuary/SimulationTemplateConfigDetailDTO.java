package com.dangjia.acg.dto.actuary;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Date: 2019/9/20
 * Time: 16:26
 */
@Data
public class SimulationTemplateConfigDetailDTO {



    //选项值ID
    private  String id;

    @ApiModelProperty("花费模板ID")
    private String simulationTemplateId;

    @ApiModelProperty("编码")
    private String code;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("图片")
    private String image;

    @ApiModelProperty("标签名称，多个用逗号分隔")
    private String labelName;
}
