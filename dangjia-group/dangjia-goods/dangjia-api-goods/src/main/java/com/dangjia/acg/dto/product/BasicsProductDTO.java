package com.dangjia.acg.dto.product;

import com.dangjia.acg.modle.GoodsBaseEntity;
import com.dangjia.acg.modle.product.BasicsProductTemplateRatio;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.util.Date;
import java.util.List;

/**
 * 产品实体类
 * @author Ronalcheng
 */
@Data
@FieldNameConstants(prefix = "")
public class BasicsProductDTO extends GoodsBaseEntity {

    private String id;

    private String name;

    private String goodsId;//货品id

    private String productSn;//商品编号

    private String image;//图片

    private String unitName;//单位名称

    private String unitId;//单位ID

    private String categoryId;//分类id

    private String labelId;//标签id

    private Integer type;//是否禁用0：禁用；1不禁用

    private Integer maket;//是否上架0：不上架（不展示）；1：上架（展示）

    private Double price;//销售价

    private String otherName;//商品别名

    private Integer istop;//是否置顶 0=正常，1=置顶

    private String remark;//备注

    private String workerTypeId;//关联工序ID（材料和人工都有）

    //材料商品对应的扩展信息
    private Double weight;//重量

    private Double cost;//平均成本价

    private Double profit;//利润率

    private String valueNameArr;//属性选项选中值name集合

    private String valueIdArr;//属性选项选中值id集合

    private Double convertQuality;//换算量

    private String convertUnit;//换算单位

    private String isInflueWarrantyPeriod;//是否影响质保年限（1是，0否）


    private Double maxWarrantyPeriodYear;//最高质保年限

    private Double minWarrantyPeriodYear;//最低质保年限

    private String marketingName;//营销名称

    private Double cartagePrice;//搬运费(元/层)

    private String detailImage;//上传详情图

    private String guaranteedPolicy;//保修政策

    private String refundPolicy;//退款政策

    // 人工商品扩展字段
    private String workExplain; //工作说明

    private String workerDec; //商品介绍

    private String workerStandard;//工艺标准


    private Double adjustedPrice; //调后单价

    private Date modityPriceTime; //调价时间

    private String technologyIds; //关联的工艺ID，多个逗号分割

    private String considerations;//注意事项

    private String calculateContent;//计价说明内容，json串

    private String buildContent;//施工说明内容，json串

    private String isAgencyPurchase;//是否为代买（1是，0否）

    private String isRelateionProduct;//是否关联商品（1是，0否）

    private String relationProductIds;//关联商品编码（多个用逗号分隔）

    private String cityId;//

    private List<BasicsProductTemplateRatioDTO> productTemplateRatioList;//责任占比列表

    private Integer stewardExploration;//是否需要管家勘查（1是，0否）

    private Integer maintenanceInvestigation;//是否为维保勘查商品（1是，0否）
}
