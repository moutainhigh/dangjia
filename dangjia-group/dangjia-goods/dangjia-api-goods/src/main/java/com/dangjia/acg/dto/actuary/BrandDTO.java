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
public class BrandDTO {
    private String brandId;//品牌id
    private String name;//品牌名称
    private int state;//1选择,2未选,3不能选
//    private int isSwitch;//可切换性0:可切换；1不可切换
//    private String targetGroupId;//可切换的目标关联组id
    private List<BrandSeriesDTO> brandSeriesDTOList;//品牌系列
}
