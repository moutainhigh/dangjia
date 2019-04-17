package com.dangjia.acg.modle.config;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.apache.commons.lang.StringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * 实体类 - 广告表
 */
@Data
@Entity
@Table(name = "dj_config_advert")
@ApiModel(description = "广告表")
@FieldNameConstants(prefix = "")
public class ConfigAdvert extends BaseEntity {

    @Column(name = "app_type")
    @Desc(value = "来源应用（1:业主端，2:工匠端）")
    @ApiModelProperty("来源应用（1:业主端，2:工匠端）")
    private String appType;

    @Column(name = "to_show")
    @Desc(value = "展示方式 0: 不展示 1：展示 2: 定时展示")
    @ApiModelProperty("展示方式 0: 不展示 1：展示 2: 定时展示")
    private Integer toShow;

    @Column(name = "name")
    @Desc(value = "广告名称")
    @ApiModelProperty("广告名称")
    private String name;

    @Column(name = "city_id")
    @Desc(value = "城市ID")
    @ApiModelProperty("城市ID")
    private String cityId;

    @Column(name = "advert_type")
    @Desc(value = "广告类型（0：启屏广告，1:弹屏广告，2:轮播图广告）")
    @ApiModelProperty("广告类型（0：启屏广告，1:弹屏广告，2:轮播图广告）")
    private String advertType;

    @Column(name = "type")
    @Desc(value = "动作类型（0:直接跳转URL，1:跳转支付，2:只显示，3:登录，4:工匠端抢单界面，5:工匠端施工界面）")
    @ApiModelProperty("动作类型（0:直接跳转URL，1:跳转支付，2:只显示，3:登录，4:工匠端抢单界面，5:工匠端施工界面）")
    private Integer type;

    @Column(name = "data")
    @Desc(value = "动作内容（type==0为跳转地址，type==1为房子id，type==2无）")
    @ApiModelProperty("动作内容（type==0为跳转地址，type==1为房子id，type==2无）")
    private String data;

    @Column(name = "image")
    @Desc(value = "广告图片")
    @ApiModelProperty("广告图片")
    private String image;

    @Column(name = "show_time_start")
    @Desc(value = "定时展示开始时间")
    @ApiModelProperty("定时展示开始时间")
    private Date showTimeStart;

    @Column(name = "show_time_end")
    @Desc(value = "定时展示结束时间")
    @ApiModelProperty("定时展示结束时间")
    private Date showTimeEnd;

    //所有图片字段加入域名和端口，形成全路径
    public void initPath(String address) {
        this.image = StringUtils.isEmpty(this.image) ? null : address + this.image;
        this.data = StringUtils.isEmpty(this.data) ? null : this.data + "&title=" + this.name;
    }

    ;

}