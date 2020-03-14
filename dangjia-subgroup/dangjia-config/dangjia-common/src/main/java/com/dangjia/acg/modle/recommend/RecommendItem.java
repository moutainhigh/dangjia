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
@Table(name = "dj_recommend_item")
@ApiModel(description = "推荐参考项主表")
@FieldNameConstants(prefix = "")
public class RecommendItem extends BaseEntity {

    /** 主项名称 */
    @Column(name = "item_name")
    @Desc(value = "主项名称")
    @ApiModelProperty("主项名称")
    private String itemName;

}
