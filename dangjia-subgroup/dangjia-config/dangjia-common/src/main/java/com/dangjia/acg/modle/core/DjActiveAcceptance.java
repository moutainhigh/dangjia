package com.dangjia.acg.modle.core;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 26/12/2019
 * Time: 上午 10:33
 */
@Data
@Entity
@Table(name = "dj_active_acceptance")
@ApiModel(description = "主动验收进程表")
@FieldNameConstants(prefix = "")
public class DjActiveAcceptance extends BaseEntity {

    @Column(name = "start_date")
    @Desc(value = "开始时间")
    @ApiModelProperty("管家自动审核倒计时时间")
    private Date startDate;

    @Column(name = "end_date")
    @Desc(value = "结束时间")
    @ApiModelProperty("业主自动审核倒计时时间")
    private Date endDate;

    @Column(name = "apply_dec")
    @Desc(value = "每日描述 审核停工的原因")
    @ApiModelProperty("每日描述 审核停工的原因")
    private String applyDec;//applydec

    @Column(name = "member_check")
    @Desc(value = "用户审核结果,0未审核，1审核通过，2审核不通过，3自动审核，4申述中")
    @ApiModelProperty("用户审核结果,0未审核，1审核通过，2审核不通过，3自动审核，4申述中")
    private Integer memberCheck;//membercheck

    @Column(name = "supervisor_check")
    @Desc(value = "大管家审核结果,0未审核，1审核通过，2审核不通过")
    @ApiModelProperty("大管家审核结果,0未审核，1审核通过，2审核不通过")
    private Integer supervisorCheck;//supervisorcheck

    @Column(name = "house_id")
    @Desc(value = "房子/项目ID")
    @ApiModelProperty("房子/项目ID")
    private String houseId;//houseid

    @Column(name = "steward_id")
    @Desc(value = "管家id")
    @ApiModelProperty("管家id")
    private String stewardId;//stewardId

    @Column(name = "business_id")
    @Desc(value = "业务id")
    @ApiModelProperty("业务id")
    private String businessId;//business_id
}
