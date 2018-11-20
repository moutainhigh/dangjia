package com.dangjia.acg.dto.actuary;

import com.dangjia.acg.modle.basics.Technology;
import lombok.Data;

import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2018/11/19 0019
 * Time: 17:32
 * 人工商品详情
 */
@Data
public class WGoodsDTO {

    private String image;//人工商品图 1张
    private String price;//价格加单位
    private String name;
    private String workerDec;//人工商品详情图片
    private List<Technology> technologyList;//工艺

}
