package com.dangjia.acg.modle.receipt;

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
 * Date: 2019/5/27
 * Time: 16:06
 */
@Data
@Entity
@Table(name = "dj_receipt")
@ApiModel(description = "供应商结算回执")
@FieldNameConstants(prefix = "")
public class Receipt extends BaseEntity {

    @Column(name = "merge")
    @Desc(value = "合并结算的发货退货表id、类型的json")
    @ApiModelProperty("合并结算的发货退货表id、类型的json")
    private String merge;

    @Column(name = "image")
    @Desc(value = "回执单图片")
    @ApiModelProperty("回执单图片")
    private String image;

    @Column(name = "supplier_id")
    @Desc(value = "供应商id")
    @ApiModelProperty("供应商id")
    private String supplierId;


}
