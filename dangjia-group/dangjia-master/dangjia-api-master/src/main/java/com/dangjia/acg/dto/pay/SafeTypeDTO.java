package com.dangjia.acg.dto.pay;

import lombok.Data;

/**
 * author: Ronalcheng
 * Date: 2018/11/7 0007
 * Time: 18:10
 */
@Data
public class SafeTypeDTO {
    private String workerTypeSafeId;//保险类型id
    private String name;//保险名字
    private String price;//价格
    private int selected;//1勾选,0未勾选
    private String houseFlowId;

}
