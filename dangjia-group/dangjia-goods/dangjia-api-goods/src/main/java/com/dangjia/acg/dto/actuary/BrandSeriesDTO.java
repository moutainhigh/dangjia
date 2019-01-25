package com.dangjia.acg.dto.actuary;

import lombok.Data;

import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2018/11/20 0020
 * Time: 14:45
 * 品牌系列
 */
@Data
public class BrandSeriesDTO {

    private String brandSeriesId;//品牌系列id
    private String name;//名称
    private int state;//1选择,2未选,3不能选
//    private int isSwitch;//可切换性0:可切换；1不可切换
//    private String targetGroupId;//可切换的目标关联组id
    private List<AttributeDTO> attributeDTOList;//属性
//    private List<AttributeValueDTO> valueDTOList;//属性选项对应多个属性值
}
