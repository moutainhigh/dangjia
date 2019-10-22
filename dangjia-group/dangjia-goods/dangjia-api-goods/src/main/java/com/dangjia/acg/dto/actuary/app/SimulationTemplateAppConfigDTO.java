package com.dangjia.acg.dto.actuary.app;

import com.dangjia.acg.dto.actuary.SimulationTemplateConfigDetailDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Date: 2019/9/20
 * Time: 16:26
 */
@Data
public class SimulationTemplateAppConfigDTO {



    @ApiModelProperty("标题ID")
    private  String titleId;

    @ApiModelProperty("标题名称")
    private String titleName;


    @ApiModelProperty("模板类型：A图片和文字，B仅图片，C仅文字")
    private String titleType;

}
