package com.dangjia.acg.dto.actuary;

import lombok.Data;

import javax.persistence.Column;

/**
 * author: Ronalcheng
 * Date: 2018/11/19 0019
 * Time: 20:09
 */
@Data
public class AttributeValueDTO {

    private String attributeValueId;//属性值id
    private String name;//名称
    private int state;//0未选,1选择,2不能选中
//    private int isSwitch;//可切换性0:可切换；1不可切换
//    private String targetGroupId;//可切换的目标关联组id
}
