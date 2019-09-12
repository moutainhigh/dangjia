package com.dangjia.acg.dto.product;

import com.dangjia.acg.common.annotation.ExcelField;
import com.dangjia.acg.common.model.BaseEntity;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

/**
 * 货品实体类
 * @author Ronalcheng
 */
@Data
@FieldNameConstants(prefix = "")
public class BasicsGoodsDTO extends BaseEntity {

    private String name;

    private String categoryId;//分类id

    private Integer type;//0:材料；1：包工包料2：人工；3：体验；4：增值

    private Integer buy;//购买性质0：必买；1可选；2自购

    private Integer sales;//退货性质0：可退；1不可退

    private String unitId;//单位

    private String otherName;//货品别名

    private String isInflueDecorationProgress;//是否影响装修进度(1是，0否)

    private String irreversibleReasons;//不可退原因

    private String istop;//是否置顶 0=正常，1=置顶

    private String brandId;//品牌id

    private String isElevatorFee;//电梯房是否按1层收取上楼费(1是，0否)

    private Double indicativePrice;//参考价格

    private String labelIds;//标签id，多个用逗号分隔

    
}