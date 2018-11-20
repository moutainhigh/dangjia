package com.dangjia.acg.dto.pay;

import lombok.Data;

/**
 * author: Ronalcheng
 * Date: 2018/11/7 0007
 * Time: 18:06
 */
@Data
public class DesignImageDTO {
    private String designImageTypeId;//设计图类型id
    private String name;//图名字
    private String price;//价格
    private int selected;//1勾选,0未勾选
}
