package com.dangjia.acg.modle.sale.royalty;

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
@Table(name = "dj_already_rob_single")
@ApiModel(description = "抢单列表")
@FieldNameConstants(prefix = "")
public class DjAlreadyRobSingle extends BaseEntity {

    @Column(name = "clue_id")
    @Desc(value = "线索Id")
    @ApiModelProperty("线索Id")
    private String clueId;

    @Column(name = "user_Id")
    @Desc(value = "销售Id")
    @ApiModelProperty("销售Id")
    private String userId;

    @Column(name = "mc_id")
    @Desc(value = "客户基础表Id")
    @ApiModelProperty("客户基础表Id")
    private String mcId;

    @Column(name = "member_id")
    @Desc(value = "客户Id")
    @ApiModelProperty("客户Id")
    private String memberId;

    @Column(name = "house_id")
    @Desc(value = "房子Id")
    @ApiModelProperty("房子Id")
    private String houseId;

    @Column(name = "is_rob_stats")
    @Desc(value = "0-未抢单 1-已抢单")
    @ApiModelProperty("0-未抢单 1-已抢单")
    private Integer isRobStats;

    @Column(name = "abroad_stats")
    @Desc(value = "0-一个销售人员下单 1-两个销售人员同时下单")
    @ApiModelProperty("0-一个销售人员下单 1-两个销售人员同时下单")
    private Integer abroadStats;


}
