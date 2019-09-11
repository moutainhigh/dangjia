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
@ApiModel(description = "商品关联词维护表")
@FieldNameConstants(prefix = "")
public class DjBasicsMaintain extends BaseEntity {
    @Column(name = "keyword_name")
    @Desc(value = "关键词名称")
    @ApiModelProperty("关键词名称")
    private String keywordName;

    @Column(name = "search_item")
    @Desc(value = "搜索词")
    @ApiModelProperty("搜索词")
    private String searchItem;

    @Column(name = "label_ids")
    @Desc(value = "商品标签表IDS")
    @ApiModelProperty("商品标签表IDS")
    private String labelIds;
}
