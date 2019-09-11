package com.dangjia.acg.modle.product;

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
 * Date: 2019/7/25
 * Time: 13:56
 */
@Data
@Entity
@Table(name = "dj_basics_maintain")
@ApiModel(description = "商品标签表")
@FieldNameConstants(prefix = "")
public class DjBasicsLabel extends BaseEntity {
    @Column(name = "name")
    @Desc(value = "标签名称")
    @ApiModelProperty("标签名称")
    private String name;


}
