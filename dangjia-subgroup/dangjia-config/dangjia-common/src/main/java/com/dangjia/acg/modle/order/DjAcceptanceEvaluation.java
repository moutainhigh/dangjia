package com.dangjia.acg.modle.order;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 9/12/2019
 * Time: 下午 3:55
 */

@Data
@Entity
@Table(name = "dj_acceptance_evaluation")
@ApiModel(description = "验收评价记录")
@FieldNameConstants(prefix = "")
public class DjAcceptanceEvaluation extends BaseEntity {

    @Column(name = "split_item_id")
    @Desc(value = "要货单明细id")
    @ApiModelProperty("要货单明细id")
    private String splitItemId;

    @Column(name = "product_id")
    @Desc(value = "店铺商品id")
    @ApiModelProperty("店铺商品id")
    private String productId;

    @Column(name = "star")
    @Desc(value = "星级")
    @ApiModelProperty("星级")
    private Integer star;

    @Column(name = "content")
    @Desc(value = "内容")
    @ApiModelProperty("内容")
    private String content;

    @Column(name = "state")
    @Desc(value = "1为商品评论,2其他")
    @ApiModelProperty("1为商品评论,2其他")
    private Integer state;

    @Column(name = "image")
    @Desc(value = "图片")
    @ApiModelProperty("图片")
    private String image;

    @Column(name = "member_id")
    @Desc(value = "用户ID")
    @ApiModelProperty("用户ID")
    private String memberId;



}
