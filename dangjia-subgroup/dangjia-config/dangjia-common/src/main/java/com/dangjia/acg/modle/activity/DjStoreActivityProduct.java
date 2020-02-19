package com.dangjia.acg.modle.activity;

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
 * Date: 2020/2/19
 * Time: 10:54
 */
@Data
@Entity
@FieldNameConstants(prefix = "")
@Table(name = "dj_store_participate_activities")
@ApiModel(description = "dj_store_activity_product")
public class DjStoreActivityProduct extends BaseEntity {

    @Column(name = "inventory")
    @Desc(value = "库存")
    @ApiModelProperty("库存")
    private Double inventory;

    @Column(name = "activity_type")
    @Desc(value = "数据类型 1:限时购，2:拼团购")
    @ApiModelProperty("数据类型 1:限时购，2:拼团购")
    private Integer activityType;

    @Column(name = "product_id")
    @Desc(value = "店铺商品表id")
    @ApiModelProperty("店铺商品表id")
    private String productId;

    @Column(name = "store_participate_activities_id")
    @Desc(value = "店铺参加活动表id")
    @ApiModelProperty("店铺参加活动表id")
    private String storeParticipateActivitiesId;

    @Column(name = "rush_purchase_price")
    @Desc(value = "抢购价格")
    @ApiModelProperty("抢购价格")
    private Double rushPurchasePrice;


}
