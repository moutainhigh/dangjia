package com.dangjia.acg.dto.sale.rob;

import com.dangjia.acg.common.model.BaseEntity;
import com.dangjia.acg.dto.member.SaleMemberLabelDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 *抢单 返回参数
 */
@Data
public class RobDTO extends BaseEntity {

    @ApiModelProperty("业主名称")
    private String owerName;

    @ApiModelProperty("业主电话")
    private String phone;

    @ApiModelProperty("0待确认开工,1装修中,2休眠中,3已完工")
    private Integer visitState;

    @ApiModelProperty("标签id")
    private String labelIdArr;

    @ApiModelProperty("客戶id")
    private String memberId;

    @ApiModelProperty("城市ID")
    private String cityId;

    @ApiModelProperty("小区id")
    private String villageId;

    @ApiModelProperty("标签名称")
    private List<SaleMemberLabelDTO> list=new ArrayList<>();//标签


    public String getVisitStateName() {
        if(null != getVisitState() && 0 == getVisitState()){
            return "待确认开工";
        }
        return null;
    }

}
