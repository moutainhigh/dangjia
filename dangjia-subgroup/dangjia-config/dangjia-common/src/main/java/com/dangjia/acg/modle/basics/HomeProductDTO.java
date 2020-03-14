package com.dangjia.acg.modle.basics;

import lombok.Data;

/**
 * @author Ruking.Cheng
 * @descrilbe 首页返回货品和人工商品实体
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/6/18 5:03 PM
 */
@Data
public class HomeProductDTO {
    private String id;//id
    private String image;//图片
    private Double price;//销售价
    private String unitName;//单位
    private Integer type;//0:货品，1：人工商品
    private Integer productType;//0：材料；1：服务；2：人工；3：体验；4：增值；5：维保
    private String name;//名称
}
