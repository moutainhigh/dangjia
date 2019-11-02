package com.dangjia.acg.modle.product;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.modle.GoodsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * author: LJL
 * Date: 2019/9/11
 * Time: 13:56
 */
@Data
@Entity
@Table(name = "dj_basics_product_template")
@ApiModel(description = "商品表实体")
@FieldNameConstants(prefix = "")
public class DjBasicsProductTemplate extends GoodsBaseEntity {

    @Column(name = "name")
    @Desc(value = "名称")
    @ApiModelProperty("名称")
    private String name;

    @Column(name = "goods_id")
    @Desc(value = "货品ID")
    @ApiModelProperty("货品Id")
    private String goodsId;

    @Column(name = "category_id")
    @Desc(value = "类别ID")
    @ApiModelProperty("类别ID")
    private String categoryId;

    @Column(name = "product_sn")
    @Desc(value = "货号编号")
    @ApiModelProperty("货号编号")
    private String productSn;

    @Column(name = "image")
    @Desc(value = "图片")
    @ApiModelProperty("图片")
    private String image;

    @Column(name = "unit_name")
    @Desc(value = "单位换算")
    @ApiModelProperty("单位换算")
    private String unitName;

    @Column(name = "unit_id")
    @Desc(value = "单位ID")
    @ApiModelProperty("单位ID")
    private String unitId;


    @Column(name = "label_id")
    @Desc(value = "标签id")
    @ApiModelProperty("标签id")
    private String labelId;

    @Column(name = "type")
    @Desc(value = "是否禁用0：禁用；1不禁用")
    @ApiModelProperty("是否禁用0：禁用；1不禁用")
    private Integer type;

    @Column(name = "maket")
    @Desc(value = "是否上架0：不上架；1：上架")
    @ApiModelProperty("是否上架0：不上架；1：上架")
    private Integer maket;

    @Column(name = "price")
    @Desc(value = "销售价")
    @ApiModelProperty("销售价")
    private Double price;

    @Column(name = "other_name")
    @Desc(value = "标签id")
    @ApiModelProperty("标签id")
    private String otherName;

    @Column(name = "istop")
    @Desc(value = "是否置顶 0=正常，1=置顶")
    @ApiModelProperty("是否置顶 0=正常，1=置顶")
    private Integer istop;

    @Column(name = "remark")
    @Desc(value = "备注")
    @ApiModelProperty("备注")
    private String remark;

    @Column(name = "value_name_arr")
    @Desc(value = "属性选项选中值name集合")
    @ApiModelProperty("属性选项选中值name集合")
    private String valueNameArr;//属性选项选中值name集合

    @Column(name = "value_id_arr")
    @Desc(value = "属性选项选中值id集合")
    @ApiModelProperty("属性选项选中值id集合")
    private String valueIdArr;//属性选项选中值id集合
    @Column(name = "weight")
    @Desc(value = "重量")
    @ApiModelProperty("重量")
    private Double weight;//重量

    @Column(name = "cost")
    @Desc(value = "平均成本价")
    @ApiModelProperty("平均成本价")
    private Double cost;//平均成本价

    @Column(name = "profit")
    @Desc(value = "利润率")
    @ApiModelProperty("利润率")
    private Double profit;//利润率


    @Column(name = "convert_quality")
    @Desc(value = "换算量")
    @ApiModelProperty("换算量")
    private Double convertQuality;//换算量

    @Column(name = "convert_unit")
    @Desc(value = "换算单位")
    @ApiModelProperty("换算单位")
    private String convertUnit;//换算单位

    @Column(name = "is_influe_warranty_period")
    @Desc(value = "是否影响质保年限（1是，0否）")
    @ApiModelProperty("是否影响质保年限（1是，0否）")
    private String isInflueWarrantyPeriod;//是否影响质保年限（1是，0否）

    @Column(name = "worker_type_id")
    @Desc(value = "关联工序ID")
    @ApiModelProperty("关联工序ID")
    private String workerTypeId;//关联工序ID

    @Column(name = "max_warranty_period_year")
    @Desc(value = "最高质保年限")
    @ApiModelProperty("最高质保年限")
    private Integer maxWarrantyPeriodYear;//最高质保年限

    @Column(name = "min_warranty_period_year")
    @Desc(value = "最低质保年限")
    @ApiModelProperty("最低质保年限")
    private Integer minWarrantyPeriodYear;//最低质保年限

    @Column(name = "marketing_name")
    @Desc(value = "营销名称")
    @ApiModelProperty("营销名称")
    private String marketingName;//营销名称

    @Column(name = "cartage_price")
    @Desc(value = "搬运费(元/层)")
    @ApiModelProperty("搬运费(元/层)")
    private Double cartagePrice;//搬运费(元/层)

    @Column(name = "detail_image")
    @Desc(value = "上传详情图")
    @ApiModelProperty("上传详情图")
    private String detailImage;//上传详情图

    @Column(name = "guaranteed_policy")
    @Desc(value = "保修政策")
    @ApiModelProperty("保修政策")
    private String guaranteedPolicy;//保修政策

    @Column(name = "refund_policy")
    @Desc(value = "退款政策")
    @ApiModelProperty("退款政策")
    private String refundPolicy;//退款政策

    @Column(name = "work_explain")
    @Desc(value = "工作说明")
    @ApiModelProperty("工作说明")
    private String workExplain;//工作说明

    @Column(name = "worker_dec")
    @Desc(value = "商品介绍图片")
    @ApiModelProperty("商品介绍图片")
    private String workerDec;//商品介绍图片

    @Column(name = "worker_standard")
    @Desc(value = "工艺标准")
    @ApiModelProperty("工艺标准")
    private String workerStandard;//工艺标准    暂不用(预留)



    @Column(name = "last_price")
    @Desc(value = "调后单价")
    @ApiModelProperty("调后单价")
    private Double LastPrice;//调后单价

    @Column(name = "last_time")
    @Desc(value = "调价时间")
    @ApiModelProperty("调价时间")
    private Date LastTime;//调价时间

    @Column(name = "technology_ids")
    @Desc(value = "关联的工艺ID")
    @ApiModelProperty("关联的工艺ID，多个逗号分割")
    private String technologyIds;//关联的工艺ID，多个逗号分割

    @Column(name = "considerations")
    @Desc(value = "注意事项")
    @ApiModelProperty("注意事项")
    private String considerations;//注意事项

    @Column(name = "calculate_content")
    @Desc(value = "计价说明")
    @ApiModelProperty("计价说明，json串")
    private String calculateContent;

    @Column(name = "build_content")
    @Desc(value = "施工说明")
    @ApiModelProperty("施工说明，json串")
    private String buildContent;

    @Column(name = "is_agency_purchase")
    @Desc(value = " 是否为代买（1是，0否）")
    @ApiModelProperty(" 是否为代买（1是，0否）")
    private String isAgencyPurchase;

    @Transient
    private String storefrontName;

}
