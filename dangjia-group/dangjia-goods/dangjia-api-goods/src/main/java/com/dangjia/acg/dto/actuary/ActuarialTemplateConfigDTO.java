package com.dangjia.acg.dto.actuary;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Date: 2019/9/20
 * Time: 16:26
 */
@Data
public class ActuarialTemplateConfigDTO  {



    //阶段产品ID
    private  String id;

    @ApiModelProperty("阶段名称")
    private String configName;


    @ApiModelProperty("配置类型1：设计阶段 2：精算阶段 3：施工阶段")
    private String configType;

    @ApiModelProperty("精算Excel地址")
    private String excelAddress;

    @ApiModelProperty("描述")
    private String configDetail;



    @ApiModelProperty("阶段下面的商品列表")
    private List  productList;//阶段下面的商品列表
}
