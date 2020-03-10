package com.dangjia.acg.modle.recommend;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "dj_recommend_item_sub")
@ApiModel(description = "推荐参考项子表")
@FieldNameConstants(prefix = "")
public class RecommendItemSub extends BaseEntity {

    /** 主参考项id */
    @Column(name = "item_id")
    @Desc(value = "主参考项id")
    @ApiModelProperty("主参考项id")
    private String itemId;

    /** 子项名称 */
    @Column(name = "item_sub_name")
    @Desc(value = "子项名称")
    @ApiModelProperty("子项名称")
    private String itemSubName;
}
