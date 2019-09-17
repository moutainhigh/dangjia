package com.dangjia.acg.dto.product;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.annotation.ExcelField;
import com.dangjia.acg.common.model.BaseEntity;
import com.dangjia.acg.dto.basics.TechnologyDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import java.util.Date;
import java.util.List;

/**
 * 产品实体类
 * @author Ronalcheng
 */
@Data
@FieldNameConstants(prefix = "")
public class AppBasicsProductDTO extends BaseEntity {
    private String id;
    private String name;
    private String goodsId;
    private String categoryId;
    private String productSn;
    private String image;
    private String unitName;
    private String unitId;
    private String labelId;
    private Integer type;
    private Integer maket;
    private Double price;
    private String otherName;
    private Integer istop;
    private String remark;
    /**
     * 材料商品对应的扩展信息--营销名称
     */
    private String marketingName;
    /**
     * 人工商品扩展字段--选择规格
     */
    private String attributeIdArr;
    /**
     * 商品类型
     */
    private String goodType;


}
