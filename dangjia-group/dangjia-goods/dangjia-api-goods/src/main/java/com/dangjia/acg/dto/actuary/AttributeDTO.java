package com.dangjia.acg.dto.actuary;

import lombok.Data;

import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2018/11/19 0019
 * Time: 20:12
 */
@Data
public class AttributeDTO {

    private String name;//名称
    private List<AttributeValueDTO> valueDTOList;//属性选项对应多个属性值
}
