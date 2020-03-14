package com.dangjia.acg.dto.actuary.app;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.dto.actuary.AttributeDTO;
import com.dangjia.acg.dto.basics.TechnologyDTO;
import com.dangjia.acg.modle.sup.Shop;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Date: 2019/9/20
 * Time: 16:26
 */
@Data
public class ActuarialProductAppDTO {


    @ApiModelProperty("业务ID")
    private String id;

    @ApiModelProperty("货品ID")
    private String goodsId;

    @ApiModelProperty("商品ID")
    private String productId;

    @ApiModelProperty("商品模板ID")
    private String productTemplateId;

    @ApiModelProperty("店铺ID")
    private String storefrontId;

    @ApiModelProperty("店铺名称")
    private String storefrontName;

    @ApiModelProperty("店铺图标")
    private String storefrontIcon;

    @ApiModelProperty("类别ID")
    private String categoryId;

    @ApiModelProperty("商品名称")
    private String productName;

    @ApiModelProperty("商品图片")
    private String image;

    @ApiModelProperty("商品图片(单张)")
    private String imageSingle;

    @ApiModelProperty("商品图片详细地址")
    private String imageUrl;

    @ApiModelProperty("上传商品详情图详细地址")
    private String detailImage;

    @ApiModelProperty("商品编码")
    private String productSn;

    @ApiModelProperty("商品单位")
    private String unit;

    @ApiModelProperty("商品单价")
    private BigDecimal price;

    @ApiModelProperty("商品总价")
    private BigDecimal totalPrice;

    @ApiModelProperty("是否按面积计算参考价格(1是，0否)")
    private String isCalculatedArea;

    @ApiModelProperty("购买总数")
    private Double shopCount;//购买总数 (精算的时候，用户手动填写的购买数量， 该单位是 product 的convertUnit换算单位 )

    //单位换算成 goods 表里的unit_name 后的购买总数
    // （相当于 小单位 转成 大单位后的购买数量  公式：budgetMaterial.setConvertCount(Math.ceil(shopCount / pro.getConvertQuality()));）
    @ApiModelProperty("换算后购买总数")
    private Double convertCount;

    @ApiModelProperty("商品单位名称")
    private String unitName;

    @ApiModelProperty("商品属性规格ID")
    private String valueIdArr;

    @ApiModelProperty("商品属性规格名称")
    private String valueNameArr;

    @ApiModelProperty("品牌ID")
    private String brandId;

    @ApiModelProperty("品牌名称")
    private String brandName;

    @ApiModelProperty("品牌图片")
    private String brandImage;

    @ApiModelProperty("商品类型（类型0：材料；1：服务；2：人工；3：体验；4：增值；5：维保）")
    private Integer productType;

    @ApiModelProperty("购买性质（购买性质0：必买；1：可选；2：自购；3：不可单独购买）")
    private Integer goodsBuy;

    @ApiModelProperty("换算量")
    private Double convertQuality;

    @ApiModelProperty("换算单位")
    private String convertUnit;

    @ApiModelProperty("是否有优惠卷(1：有，0：否)")
    private Integer isActivityRedPack = 0;

    private String unitId;

    private Integer unitType;//单位数值类型 1=整数单位，2=小数单位
    @ApiModelProperty("是否勾选商品标识")
    private boolean flag;

    /***********************商品明细相关内容**************************/


    @ApiModelProperty("购买限制（0自由购房；1有房无精算；2有房有精算")
    private String purchaseRestrictions;

    @ApiModelProperty("退货性质0：可退；1：不可退")
    private Integer sales;

    @ApiModelProperty("不可退原因")
    private String irreversibleReasons;

    @ApiModelProperty("是否上架  0:未上架；1已上架")
    private String isShelfStatus;

    @ApiModelProperty("营销名称")
    private String marketingName;

    @ApiModelProperty("是否影响质保年限（1是，0否）")
    private String isInflueWarrantyPeriod;

    @ApiModelProperty("退款政策")
    private String refundPolicy;

    @ApiModelProperty("保修政策")
    private String guaranteedPolicy;

    @ApiModelProperty("可切换性0:可切换；1不可切换")
    private int isSwitch;

    @ApiModelProperty("商品介绍图片")
    private String workerDec;

    @ApiModelProperty("商品介绍图片(全路径)")
    private String workerDecUrl;


    @ApiModelProperty("工种ID")
    private String workerTypeId;

    @ApiModelProperty("工种名称")
    private String workerTypeName;

    @ApiModelProperty("工作说明")
    private String workExplain;

    @ApiModelProperty("工艺标准")
    private String workerStandard;

    @ApiModelProperty("别名")
    private String otherName;

    @ApiModelProperty("调后单价")
    private Double adjustedPrice;

    @ApiModelProperty("调价时间")
    private Date modityPriceTime;

    @ApiModelProperty("注意事项")
    private String considerations;

    @ApiModelProperty("计价说明，json串")
    private String calculateContent;

    @ApiModelProperty("施工说明，json串")
    private String buildContent;

    @ApiModelProperty("该product所在的原关联组id")
    private String srcGroupId;

    @ApiModelProperty("抢购价格")
    private BigDecimal rushPurchasePrice;

    @Desc(value = "场次开始时间")
    @ApiModelProperty("场次开始时间")
    private Date sessionStartTime;

    @Desc(value = "场次结束时间")
    @ApiModelProperty("场次结束时间")
    private Date endSession;

    @Desc(value = "拼团人数")
    @ApiModelProperty("拼团人数")
    private Integer spellGroup;

    @ApiModelProperty("关联的工艺ID，多个逗号分割")
    private String technologyIds;
    @ApiModelProperty("店铺信息")
    private Shop shop;
    @ApiModelProperty("属性组")
    private List<AttributeDTO> attrList;
    @ApiModelProperty("工艺集合")
    private List<TechnologyDTO> technologies;
    /***********************商品明细相关内容**************************/

    //拼团
    private Map<String,Object> map;//拼团

    private Integer activityType;//1:限时购 2：拼团购

    private long merchandiseCountdown;//活动商品倒计时时间戳


}
