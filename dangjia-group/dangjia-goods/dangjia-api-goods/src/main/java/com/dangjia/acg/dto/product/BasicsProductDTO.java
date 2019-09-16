package com.dangjia.acg.dto.product;

import com.dangjia.acg.common.model.BaseEntity;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.util.Date;

/**
 * 产品实体类
 * @author Ronalcheng
 */
@Data
@FieldNameConstants(prefix = "")
public class BasicsProductDTO extends BaseEntity {

    private String name;

    private String goodsId;//货品id

    private String productSn;//商品编号

    private String image;//图片

    private String unitName;//单位名称

    private String unitId;//单位ID

    private String categoryId;//分类id

    private String labelId;//标签id

    private Integer type;//是否禁用0：禁用；1不禁用

    private Integer maket;//是否上架0：不上架；1：上架

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

    private String attributeIdArr;//属性选中值Id集合

    private Double convertQuality;//换算量

    private String convertUnit;//换算单位

    private String isInflueWarrantyPeriod;//是否影响质保年限（1是，0否）


    private Integer maxWarrantyPeriodYear;//最高质保年限

    private Integer minWarrantyPeriodYear;//最低质保年限

    private String marketingName;//营销名称

    private Double cartagePrice;//搬运费(元/层)

    private String detailImage;//上传详情图

    private String guaranteedPolicy;//保修政策

    private String refundPolicy;//退款政策

    // 人工商品扩展字段
    private String workExplain; //工作说明

    private String workerDec; //商品介绍

    private String workerStandard;//工艺标准

    private Integer showGoods;//是否展示

    private Double lastPrice; //调后单价

    private Date lastTime; //调价时间

    private String technologyIds; //关联的工艺ID，多个逗号分割

    private String considerations;//注意事项

    private String calculateContent;//计价说明内容，json串

    private String buildContent;//施工说明内容，json串

    private String isAgencyPurchase;//是否为代买（1是，0否）





}
