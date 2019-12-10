package com.dangjia.acg.dto.order;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 10/12/2019
 * Time: 下午 5:18
 */
@Data
public class AcceptanceEvaluationListDTO {
    private String splitDeliverId;
    private String productId;
    private String splitItemId;
    private Double shopCount;
    private  String valueIdArr;//商品规格ID
    private String valueNameArr;//商品规格名称
    private String image;
}
