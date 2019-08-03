package com.dangjia.acg.dto.sale.rob;

import com.dangjia.acg.dto.member.CustomerRecordInFoDTO;
import com.dangjia.acg.dto.member.IntentionHouseDTO;
import com.dangjia.acg.dto.member.SaleMemberLabelDTO;
import com.dangjia.acg.dto.sale.achievement.UserAchievementDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 *抢单详情 返回参数
 */
@ApiModel(description = "抢单详情list")
@Data
public class RobArrInFoDTO {

    @ApiModelProperty("标签名称")
    private List<SaleMemberLabelDTO> list;

    @ApiModelProperty("沟通记录")
    private List<CustomerRecordInFoDTO> data;

    @ApiModelProperty("客户房屋信息")
    private List<RobInfoDTO> customerList;

    @ApiModelProperty("销售业绩")
    UserAchievementDTO userInFo;

    @ApiModelProperty("意向房屋信息")
    private List<IntentionHouseDTO> intentionHouseList;


}
