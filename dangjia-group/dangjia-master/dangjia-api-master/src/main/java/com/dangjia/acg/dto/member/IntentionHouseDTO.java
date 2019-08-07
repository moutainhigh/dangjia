package com.dangjia.acg.dto.member;

import com.dangjia.acg.common.util.CommonUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

/**
 * Created with IntelliJ IDEA.
 * author: LJL
 * Date: 2019/8/3
 * Time: 16:02
 */
@Data
@FieldNameConstants(prefix = "")
public class IntentionHouseDTO {

    private String id;

    @ApiModelProperty("小区")
    private String residentialName;

    @ApiModelProperty("楼栋")
    private String buildingName;

    @ApiModelProperty("房号")
    private String numberName;

    @ApiModelProperty("线索id")
    private String clueId;

    private String houseNameArr;

    public String getHouseNameArr() {
        return (CommonUtil.isEmpty(getResidentialName()) ? "*" : getResidentialName())+ "栋"
                + (CommonUtil.isEmpty(getBuildingName()) ? "*" : getBuildingName()) + "单元"
                + (CommonUtil.isEmpty(getNumberName()) ? "*" : getNumberName()) + "号";
    }

}
