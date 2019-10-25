package com.dangjia.acg.dto.actuary.app;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Date: 2019/9/20
 * Time: 16:26
 */
@Data
public class ActuarialTemplateConfigAppDTO {



    //阶段产品ID
    private  String id;

    @ApiModelProperty("阶段名称")
    private String configName;


    @ApiModelProperty("配置类型1：设计阶段 2：精算阶段 3：施工阶段")
    private String configType;

    @ApiModelProperty("描述")
    private String configDetail;


    @ApiModelProperty("阶段下面的商品列表")
    private List<ActuarialProductAppDTO>  productList;//阶段下面的商品列表
}
