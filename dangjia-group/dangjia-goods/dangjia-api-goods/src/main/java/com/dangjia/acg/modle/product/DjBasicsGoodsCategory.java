package com.dangjia.acg.modle.product;

import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/9/15
 * Time: 9:20
 */
@Data
@Entity
@Table(name = "dj_basics_goods_category")
@ApiModel(description = "商品材料类别")
@FieldNameConstants(prefix = "")
public class DjBasicsGoodsCategory extends BaseEntity {

}
