package com.dangjia.acg.dto.shell;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.modle.GoodsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: fzh
 * Date: 2020-02-25
 * Time: 下午 5:18
 */
@Data
public class HomeShellProductDTO {

    @ApiModelProperty("商品ID")
    private String shellProductId;//商品名称

    @ApiModelProperty("商品名称")
    private String name;//商品名称

    @ApiModelProperty("商品编码")
    private String productSn;

    @ApiModelProperty("商品分类：1实物商品，2虚拟商品")
    private String productType;

    @ApiModelProperty("图片")
    private String image;

    private String imageUrl;//图片地址

    @ApiModelProperty("上传详情图")
    private String detailImage;//上传详情图

    private String detailImageUrl;//详情图地址

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("支付类型：1积分，2积分+金钱")
    private Integer payType;

    @ApiModelProperty("是否开启积分限时 1是，0否")
    private Integer openingTimeLimit;

    @ApiModelProperty("限制时间（小时）")
    private Double limithours;

    @ApiModelProperty("已兑换数")
    private Double convertedNumber;//

    @ApiModelProperty("是否限制兑换量 1是，0否")
    private Integer limitExchangeVolume;//

    @ApiModelProperty("单人限数(兑换量）")
    private Integer exchangeVolumeNum;//

    @ApiModelProperty(" 上下架状态 1：上架  0:下架")
    private String shelfStatus;

    private Date createDate;//创建时间

    private List<HomeShellProductSpecDTO> productSpecList;//规格列表

}
