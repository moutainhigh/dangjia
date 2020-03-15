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
import java.util.List;

@Data
@Entity
@Table(name = "dj_lattice_content")
@ApiModel(description = "方格内容表")
@FieldNameConstants(prefix = "")
public class LatticeContent extends BaseEntity {

    /** '区域名称 */
    @Column(name = "area_name")
    @Desc(value = "'区域名称")
    @ApiModelProperty("'区域名称")
    private String areaName;

    /** '区域范围值 */
    @Column(name = "area_scope")
    @Desc(value = "'区域范围值")
    @ApiModelProperty("'区域范围值")
    private Integer areaScope;

    /** '样式id */
    @Column(name = "style_id")
    @Desc(value = "'样式id")
    @ApiModelProperty("'样式id")
    private String styleId;

    /** '内容类型 */
    @Column(name = "type_id")
    @Desc(value = "'内容类型")
    @ApiModelProperty("'内容类型")
    private String typeId;

    /** '内容id */
    @Column(name = "content_id")
    @Desc(value = "'内容id")
    @ApiModelProperty("'内容id")
    private String contentId;

    /** '图片 */
    @Column(name = "image")
    @Desc(value = "'图片")
    @ApiModelProperty("'图片")
    private String image;

    /** '超链接URL */
    @Column(name = "url")
    @Desc(value = "'超链接URL")
    @ApiModelProperty("'超链接URL")
    private String url;

    private List<String> codingNameList;
    private String contentName;
}
