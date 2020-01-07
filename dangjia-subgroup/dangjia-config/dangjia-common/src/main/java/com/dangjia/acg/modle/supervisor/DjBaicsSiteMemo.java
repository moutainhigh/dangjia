package com.dangjia.acg.modle.supervisor;

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
@Table(name = "dj_basics_site_memo")
@ApiModel(description = "工地备忘录表")
@FieldNameConstants(prefix = "")
public class DjBaicsSiteMemo extends BaseEntity {

    @Column(name = "remark")
    @Desc(value = "备注")
    @ApiModelProperty("备注")
    private String remark;

    @Column(name = "house_id")
    @Desc(value = "房子id")
    @ApiModelProperty("房子id")
    private String houseId;


    @Column(name = "type")
    @Desc(value = "0=普通  1=周计划")
    @ApiModelProperty("0=普通  1=周计划")
    private Integer type;

    @Column(name = "member_id")
    @Desc(value = "备忘录创建人")
    @ApiModelProperty("备忘录创建人")
    private String memberId;

    @Column(name = "worker_type_id")
    @Desc(value = "工种类别0业主,1设计师,2精算师,3大管家,4拆除,6水电工,7防水,8泥工,9木工,10油漆工")
    @ApiModelProperty("工种类别0业主,1设计师,2精算师,3大管家,4拆除,6水电工,7防水,8泥工,9木工,10油漆工")
    private String workerTypeId;

    @Column(name = "reminder_time")
    @Desc(value = "指定时间提醒我")
    @ApiModelProperty("指定时间提醒我")
    private String reminderTime;

}
