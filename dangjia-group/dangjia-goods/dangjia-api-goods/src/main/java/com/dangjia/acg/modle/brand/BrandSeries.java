package com.dangjia.acg.modle.brand;

import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
/**
   * @功能描述：  品牌系列
   * @作者信息： hb
   * @创建时间： 2018-9-13下午7:07:37
 */
@Data
@Entity
@Table(name = "dj_basics_brand_series")
@ApiModel(description = "品牌")
public class BrandSeries extends BaseEntity{

	@Column(name = "name")
    private String name;//名称

	@Column(name = "brand_id")
    private String brandId;//品牌id

	@Column(name = "content")
    private String content;//内容

    @Column(name = "image")
    private String image;//系列图片

}