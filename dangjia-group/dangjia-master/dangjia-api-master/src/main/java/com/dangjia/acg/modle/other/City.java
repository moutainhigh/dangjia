package com.dangjia.acg.modle.other;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created with IntelliJ IDEA.
 * author: Ronalcheng
 * Date: 2018/10/29 0029
 * Time: 17:11
 */
@Data
@Entity
@Table(name = "dj_other_city")
@ApiModel(description = "城市地区")
public class City extends BaseEntity {

    @Column(name = "name")
    @Desc(value = "名称")
    @ApiModelProperty("名称")
    private String name;

    @Column(name = "state")
    @Desc(value = "状态0启用")
    @ApiModelProperty("状态0启用")
    private String state;

    @Column(name = "initials")
    @Desc(value = "拼音首字母")
    @ApiModelProperty("拼音首字母")
    private String initials;

}
