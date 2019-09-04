package com.dangjia.acg.dto.sale.achievement;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 *员工业绩 返回参数
 */
@Data
public class UserAchievementDataDTO implements Serializable {

    @ApiModelProperty("成交量")
    private Integer dealNumber;

    @ApiModelProperty("当月总提成")
    private Integer arrMonthRoyalty;

    List<UserAchievementInfoDTO> userAchievementInfoDTOS;
}
