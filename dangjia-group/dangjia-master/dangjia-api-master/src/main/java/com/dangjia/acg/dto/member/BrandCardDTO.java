package com.dangjia.acg.dto.member;

import lombok.Data;

/**
 * author: Ronalcheng
 * Date: 2018/12/19 0019
 * Time: 10:31
 */
@Data
public class BrandCardDTO {
    private String brandName;//银行名
    private String workerBankCardId;//银行关联工人
    private String bkMaxAmt;//最大取现金额

    private String bkMinAmt;//最小取现金额

    private String bankCardImage;//图片路径
}
