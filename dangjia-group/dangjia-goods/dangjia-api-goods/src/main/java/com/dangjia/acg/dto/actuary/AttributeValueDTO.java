package com.dangjia.acg.dto.actuary;

import lombok.Data;

/**
 * author: Ronalcheng
 * Date: 2018/11/19 0019
 * Time: 20:09
 */
@Data
public class AttributeValueDTO {

    private String attributeValueId;//属性值id
    private String attributeId;//属性选项id
    private String name;//名称
    private int state;//1选择,2未选,3不能选
}
