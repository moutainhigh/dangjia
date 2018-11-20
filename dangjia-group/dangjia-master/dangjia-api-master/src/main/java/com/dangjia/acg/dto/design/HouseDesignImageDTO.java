package com.dangjia.acg.dto.design;

import lombok.Data;

import java.math.BigDecimal;

/**
 * author: Ronalcheng
 * Date: 2018/11/15 0015
 * Time: 14:51
 * 上传设计图
 */
@Data
public class HouseDesignImageDTO {

    private String houseId;
    private String designImageTypeId;
    private String imageurl;//路径

    private String name;//图名字
    private int sell;//额外付费 1是, 0不是
    private BigDecimal price;//价格

}
