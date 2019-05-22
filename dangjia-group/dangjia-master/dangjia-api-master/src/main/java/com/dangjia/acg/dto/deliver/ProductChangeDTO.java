package com.dangjia.acg.dto.deliver;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * author: Yinjianbo
 * Date: 2019-5-14
 */
@Data
public class ProductChangeDTO {

    private String id;
    private String houseId;
    private String memberId;
    private String categoryId;
    private String srcProductId;
    private String srcProductSn;
    private String srcProductName;
    private Double srcPrice;
    // 剩余数
    private Double srcSurCount;
    private String srcUnitName;
    private String srcImage;
    // 新
    private String destProductId;
    private String destProductSn;
    private String destProductName;
    private Double destPrice;
    // 更换数
    private Double destSurCount;
    private String destUnitName;
    private String destImage;
    // 差额
    private BigDecimal differencePrice;
    //0未处理 1已处理
    private Integer type;
    // 创建日期
    private Date createDate;
    // 修改日期
    private Date modifyDate;
}
