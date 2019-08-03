package com.dangjia.acg.dto.member;

import com.dangjia.acg.common.util.CommonUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * author: LJL
 * Date: 2019/8/3
 * Time: 16:02
 */
@Data
public class IntentionHouseDTO {

    private String id;

    @ApiModelProperty("小区名称")
    private String residentialName;

    @ApiModelProperty("楼栋名称")
    private String buildingName;

    @ApiModelProperty("房号名称")
    private String numberName;

    @ApiModelProperty("线索id")
    private String clueId;


    public String getHouseNameArr() {
        return (CommonUtil.isEmpty(getResidentialName()) ? "*" : getResidentialName())+ "栋"
                + (CommonUtil.isEmpty(getBuildingName()) ? "*" : getBuildingName()) + "单元"
                + (CommonUtil.isEmpty(getNumberName()) ? "*" : getNumberName()) + "号";
    }

    /*public String getHouserName(){
        String rName = "";
        String bName = "";
        String nName = "";
        if(!CommonUtil.isEmpty(getResidentialName())){
             rName = getResidentialName();
        }
        if(!CommonUtil.isEmpty(getBuildingName())){
             bName = getBuildingName();
        }
        if(!CommonUtil.isEmpty(getNumberName())){
             nName = getNumberName();
        }
        return (rName+ bName+ nName);
    }*/
}
