package com.dangjia.acg.dto.actuary;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2018/11/15 0015
 * Time: 19:55
 */
@Data
public class ActuaryDetailsDTO {
    private String houseId;
    @ApiModelProperty("精算状态:-1已精算没有发给业主,默认0未开始,1已开始精算,2已发给业主,3审核通过,4审核不通过")
    private int budgetOk;//
    private List<FlowDetailsDTO> flowDetailsDTOList;
}
