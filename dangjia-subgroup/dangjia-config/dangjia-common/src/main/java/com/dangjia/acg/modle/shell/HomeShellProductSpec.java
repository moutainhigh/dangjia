package com.dangjia.acg.modle.shell;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import com.dangjia.acg.modle.GoodsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created with IntelliJ IDEA.
 * author: LJL
 * Date: 2019/9/11
 * Time: 13:56
 */
@Data
@Entity
@Table(name = "dj_home_shell_product_spec")
@ApiModel(description = "当家贝商品")
@FieldNameConstants(prefix = "")
public class HomeShellProductSpec extends BaseEntity {

    @Column(name = "name")
    @Desc(value = "商品名称")
    @ApiModelProperty("商品名称")
    private String name;

    @Column(name = "product_id")
    @Desc(value = "商品ID")
    @ApiModelProperty("商品ID")
    private String productId;

    @Column(name = "integral")
    @Desc(value = "所需贝币")
    @ApiModelProperty("所需贝币")
    private Double integral;

    @Column(name = "money")
    @Desc(value = "所需金额")
    @ApiModelProperty("金额")
    private Double money;

    @Column(name = "stock_num")
    @Desc(value = "库存数量")
    @ApiModelProperty("库存数量")
    private Double stockNum;

    @Column(name = "converted_number")
    @Desc(value = "已兑换数")
    @ApiModelProperty("已兑换数")
    private Double convertedNumber;


}
