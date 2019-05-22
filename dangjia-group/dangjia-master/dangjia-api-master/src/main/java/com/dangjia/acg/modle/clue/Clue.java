package com.dangjia.acg.modle.clue;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.annotation.ExcelField;
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
@Table(name = "dj_clue")
@ApiModel(description = "线索表")
@FieldNameConstants(prefix = "")
public class Clue extends BaseEntity {


    @Column(name = "owername")
    @Desc(value = "业主名")
    @ApiModelProperty("业主名")
    @ExcelField(titile = "业主名", offset = 1)
    private String owername;

    @Column(name = "phone")
    @Desc(value = "手机号码")
    @ApiModelProperty("手机号码")
    @ExcelField(titile = "手机号码", offset = 2)
    private String phone;

    @Column(name = "wechat")
    @Desc(value = "微信")
    @ApiModelProperty("微信")
    @ExcelField(titile = "微信", offset = 3)
    private String wechat;

    @Column(name = "address")
    @Desc(value = "地址")
    @ApiModelProperty("地址")
    @ExcelField(titile = "地址", offset = 4)
    private String address;

    @Column(name = "stage")
    @Desc(value = "阶段 0:新线索 1：继续跟进 2:已放弃 3:黑名单")
    @ApiModelProperty("阶段 0:新线索 1：继续跟进 2:已放弃 3:黑名单")
    private int stage;

    @Column(name = "cus_service")
    @Desc(value = "客服ID")
    @ApiModelProperty("客服ID")
    private String cusService;

    @Column(name = "label_id")
    @Desc(value = "标签Id")
    @ApiModelProperty("标签Id")
    private String labelId;


}
