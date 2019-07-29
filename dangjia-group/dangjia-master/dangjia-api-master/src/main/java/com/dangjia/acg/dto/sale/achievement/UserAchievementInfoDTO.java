package com.dangjia.acg.dto.sale.achievement;

import com.dangjia.acg.common.annotation.Desc;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.apache.http.util.TextUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.io.Serializable;

/**
 *员工业绩 返回参数
 */
@Data
@Entity
@ApiModel(description = "员工业绩信息")
@FieldNameConstants(prefix = "")
public class UserAchievementInfoDTO implements Serializable {

    @Column(name = "id")
    protected String id;

    @Column(name = "userId")
    protected String userId;

    @Column(name = "residential")
    @Desc(value = "小区名")
    @ApiModelProperty("小区名")
    private String residential;

    @Column(name = "building")
    @Desc(value = "楼栋，后台客服填写")
    @ApiModelProperty("楼栋，后台客服填写")
    private String building;

    @Column(name = "unit")
    @Desc(value = "单元号，后台客服填写")
    @ApiModelProperty("单元号，后台客服填写")
    private String unit;

    @Column(name = "number")
    @Desc(value = "房间号，后台客服填写")
    @ApiModelProperty("房间号，后台客服填写")
    private String number;

    @Column(name = "visit_state")
    @Desc(value = "0待确认开工,1装修中,2休眠中,3已完工,4提前结束装修 5提前结束装修申请中")
    @ApiModelProperty("0待确认开工,1装修中,2休眠中,3已完工,4提前结束装修 5提前结束装修申请中")
    private Integer visitState;

    @ApiModelProperty("当月提成")
    private Integer monthRoyalty;

    @ApiModelProperty("累计提成")
    private Integer meterRoyalty;

    @ApiModelProperty("全部提成")
    private Integer arrRoyalty;

    public String getHouseName() {
        return (TextUtils.isEmpty(getResidential()) ? "*" : getResidential())
                + (TextUtils.isEmpty(getBuilding()) ? "*" : getBuilding()) + "栋"
                + (TextUtils.isEmpty(getUnit()) ? "*" : getUnit()) + "单元"
                + (TextUtils.isEmpty(getNumber()) ? "*" : getNumber()) + "号";
    }
}
