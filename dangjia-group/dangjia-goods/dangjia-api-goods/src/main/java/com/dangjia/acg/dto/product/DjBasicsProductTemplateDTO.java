package com.dangjia.acg.dto.product;

import lombok.Data;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * author: LJL
 * Date: 2019/9/11
 * Time: 13:56
 */
@Data
public class DjBasicsProductTemplateDTO {
   private String id;

    private String createDate;

    private String  modifyDate;

    private String name;

    private String goodsId;

    private String categoryId;

    private String productSn;

    private String image;

    private String unitName;

    private String unitId;

    private String labelId;

    private Integer type;

    private Integer maket;

    private Double price;

    private String otherName;

    private Integer istop;

    private String remark;

    private String valueNameArr;//属性选项选中值name集合

    private String valueIdArr;//属性选项选中值id集合

    private Double weight;//重量

    private Double cost;//平均成本价

    private Double profit;//利润率

    private Double convertQuality;//换算量

    private String convertUnit;//换算单位

    private String isInflueWarrantyPeriod;//是否影响质保年限（1是，0否）

    private String workerTypeId;//关联工序ID

    private Integer maxWarrantyPeriodYear;//最高质保年限

    private Integer minWarrantyPeriodYear;//最低质保年限

    private String marketingName;//营销名称

    private Double cartagePrice;//搬运费(元/层)

    private String detailImage;//上传详情图

    private String guaranteedPolicy;//保修政策

    private String refundPolicy;//退款政策

    private String workExplain;//工作说明

    private String workerDec;//商品介绍图片

    private String workerStandard;//工艺标准    暂不用(预留)

    private Double LastPrice;//调后单价

    private Date LastTime;//调价时间

    private String technologyIds;//关联的工艺ID，多个逗号分割

    private String considerations;//注意事项

    private String calculateContent;

    private String buildContent;

    private String isAgencyPurchase;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 图片地址
     */
    private String imageUrl;

    /**
     * 换算单位
     */
    private String convertUnitName;

    /**
     * 标签名称
     */
    private String labelName;

    /**
     * 销售价格
     */
    private String sellPrice;

    /**
     * 供货数
     */
    private String suppliedNum;

    /**
     * 供应状态
     */
    private String isShelfStatus;

    /**
     * 属性名称
     */
    private String newValueNameArr;
}
