package com.dangjia.acg.dto.activity;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.dto.product.StorefrontProductDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2020/2/25
 * Time: 16:34
 */
@Data
public class HomeLimitedPurchaseActivitieDTO {

    private String id;

    @Desc(value = "场次开始时间")
    @ApiModelProperty("场次开始时间")
    private Date sessionStartTime;

    @Desc(value = "场次结束时间")
    @ApiModelProperty("场次结束时间")
    private Date endSession;

    List<StorefrontProductDTO> storefrontProductDTOS;
}
