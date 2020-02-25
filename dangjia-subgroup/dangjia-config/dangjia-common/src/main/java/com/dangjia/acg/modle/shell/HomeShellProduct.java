package com.dangjia.acg.modle.shell;

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
@Table(name = "dj_home_shell_product")
@ApiModel(description = "当家贝商品")
@FieldNameConstants(prefix = "")
public class HomeShellProduct extends GoodsBaseEntity {

    @Column(name = "name")
    @Desc(value = "商品名称")
    @ApiModelProperty("商品名称")
    private String name;

    @Column(name = "product_sn")
    @Desc(value = "商品编码")
    @ApiModelProperty("商品编码")
    private String productSn;

    @Column(name = "product_type")
    @Desc(value = "商品分类：1实物商品，2虚拟商品")
    @ApiModelProperty("商品分类：1实物商品，2虚拟商品")
    private String productType;

    @Column(name = "image")
    @Desc(value = "图片")
    @ApiModelProperty("图片")
    private String image;

    @Column(name = "detail_image")
    @Desc(value = "上传详情图")
    @ApiModelProperty("上传详情图")
    private String detailImage;//上传详情图

    @Column(name = "sort")
    @Desc(value = "排序")
    @ApiModelProperty("排序")
    private Integer sort;

    @Column(name = "pay_type")
    @Desc(value = "支付类型：1积分，2积分+金钱")
    @ApiModelProperty("支付类型：1积分，2积分+金钱")
    private Integer payType;


    @Column(name = "opening_time_limit")
    @Desc(value = "是否开启积分限时 1是，0否")
    @ApiModelProperty("是否开启积分限时 1是，0否")
    private Integer openingTimeLimit;

    @Column(name = "limit_hours")
    @Desc(value = "限制时间（小时）")
    @ApiModelProperty("限制时间（小时）")
    private Double limithours;


    @Column(name = "converted_number")
    @Desc(value = "已兑换数")
    @ApiModelProperty("已兑换数")
    private Double convertedNumber;


    @Column(name = "limit_exchange_volume")
    @Desc(value = "是否限制兑换量 1是，0否")
    @ApiModelProperty("是否限制兑换量 1是，0否")
    private Integer limitExchangeVolume;

    @Column(name = "exchange_volume_num")
    @Desc(value = "单人限数（兑换量）")
    @ApiModelProperty("单人限数(兑换量）")
    private Integer exchangeVolumeNum;


    @Column(name = "shelf_status")
    @Desc(value = " 上下架状态 1：上架  0:下架")
    @ApiModelProperty(" 上下架状态 1：上架  0:下架")
    private String shelfStatus;

}
