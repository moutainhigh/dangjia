package com.dangjia.acg.dto.sale.achievement;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 *门店业绩 返回参数
 */
@Data
public class AchievementDataDTO implements Serializable {

    @ApiModelProperty("成交量")
    private Integer dealNumber;

    @ApiModelProperty("门店总提成")
    private Integer storeRoyalty;

    List<AchievementInfoDTO> achievementDataDTOS;
}
