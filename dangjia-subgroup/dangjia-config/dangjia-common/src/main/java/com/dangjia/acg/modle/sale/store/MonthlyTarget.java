package com.dangjia.acg.modle.sale.store;

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
 * Date: 2019/7/22
 * Time: 14:26
 */
@Data
@Entity
@Table(name = "dj_sale_monthly_target")
@ApiModel(description = "销售每月目标")
@FieldNameConstants(prefix = "")
public class MonthlyTarget extends BaseEntity {

    @Column(name = "target_number")
    @Desc(value = "目标数")
    @ApiModelProperty("目标数")
    private Integer targetNumber;

    @Column(name = "user_Id")
    @Desc(value = "销售Id")
    @ApiModelProperty("销售Id")
    private String userId;

    @Column(name = "target_date")
    @Desc(value = "目标月份")
    @ApiModelProperty("目标月份")
    protected Date targetDate;
}
