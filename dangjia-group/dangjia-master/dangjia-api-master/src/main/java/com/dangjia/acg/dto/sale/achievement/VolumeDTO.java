package com.dangjia.acg.dto.sale.achievement;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/7/25
 * Time: 13:56
 */
@Data
public class VolumeDTO {

    @ApiModelProperty("成交量")
    private Integer dealNumber;

    List<UserAchievementInfoDTO> userAchievementInfoDTOS;
}
