package com.dangjia.acg.dto.actuary;

import lombok.Data;

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
}
