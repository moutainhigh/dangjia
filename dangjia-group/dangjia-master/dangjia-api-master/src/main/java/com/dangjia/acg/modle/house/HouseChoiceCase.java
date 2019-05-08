package com.dangjia.acg.modle.house;

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
import java.math.BigDecimal;
import java.util.Date;

/**
 * 实体类 - 房间
 */
@Data
@Entity
@Table(name = "dj_core_house_choice_case")
@ApiModel(description = "房屋精选案例")
@FieldNameConstants(prefix = "")
public class HouseChoiceCase extends BaseEntity {

    @Column(name = "title")
    @Desc(value = "标题（如房屋名称）")
    @ApiModelProperty("标题（如房屋名称）")
    private String title;

    @Column(name = "house_id")
    @Desc(value = "房子ID")
    @ApiModelProperty("房子ID")
    private String houseId;//houseid

    @Column(name = "city_id")
    @Desc(value = "城市id")
    @ApiModelProperty("城市id")
    private String cityId;//cityid

    @Column(name = "label")
    @Desc(value = "标签（多个以逗号分隔）")
    @ApiModelProperty("标签（多个以逗号分隔）")
    private String label;//

    @Column(name = "image")
    @Desc(value = "案例主图")
    @ApiModelProperty("案例主图")
    private String image;

    @Column(name = "address")
    @Desc(value = "跳转地址")
    @ApiModelProperty("跳转地址")
    private String address;//

    @Column(name = "source")
    @Desc(value = "来源说明")
    @ApiModelProperty("来源说明")
    private String source;//

    @Column(name = "money")
    @Desc(value = "金额")
    @ApiModelProperty("金额")
    private BigDecimal money;//

    @Column(name = "show_time_start")
    @Desc(value = "定时展示开始时间")
    @ApiModelProperty("定时展示开始时间")
    private Date showTimeStart;

    @Column(name = "show_time_end")
    @Desc(value = "定时展示结束时间")
    @ApiModelProperty("定时展示结束时间")
    private Date showTimeEnd;

    @Column(name = "is_show")
    @Desc(value = "展示方式 0: 展示 1：不展示 2: 定时展示")
    @ApiModelProperty("展示方式 0: 展示 1：不展示 2: 定时展示")
    private int isShow;

    //所有图片字段加入域名和端口，形成全路径
    public void initPath(String address) {
        this.image = StringUtils.isEmpty(this.image) ? null : address + this.image;
    }

}