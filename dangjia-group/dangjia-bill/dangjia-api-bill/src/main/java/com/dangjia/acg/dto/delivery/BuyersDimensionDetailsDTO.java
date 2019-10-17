package com.dangjia.acg.dto.delivery;

import lombok.Data;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * 买家维度详情DTO
 * Date: 17/10/2019
 * Time: 上午 11:27
 */
@Data
public class BuyersDimensionDetailsDTO {
    private String orderSplitId;
    private String number;
    private Date createDate;
    private String shopCount;
    private String totalAmount;
}
