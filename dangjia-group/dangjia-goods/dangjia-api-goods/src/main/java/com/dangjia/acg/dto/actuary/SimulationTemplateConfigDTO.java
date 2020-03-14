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
public class SimulationTemplateConfigDTO {



    //阶段产品ID
    private  String id;

    @ApiModelProperty("阶段名称")
    private String configName;


    @ApiModelProperty("模板类型：A图片和文字，B仅图片，C仅文字")
    private String configType;

    @ApiModelProperty("服务类型ID")
    private String serviceTypeId;

    private String addressUrl;

    @ApiModelProperty("标题详情列表")
    private List<SimulationTemplateConfigDetailDTO>  simulationDetailList;//
}
