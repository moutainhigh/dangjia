package com.dangjia.acg.dto.delivery;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 30/10/2019
 * Time: 下午 3:22
 */
@Data
public class MaterialNumberDTO {

    private String houseId;
    private Integer type;

    private Integer shopCount;//购买总数
    private Integer askCount;//要货数
    private Integer returnCount;//退货数


}
