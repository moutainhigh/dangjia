package com.dangjia.acg.dto.house;

import lombok.Data;
import org.apache.http.util.TextUtils;

import java.math.BigDecimal;

/**
 * @author Ruking.Cheng
 * @descrilbe 房产列表
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/2/22 5:36 PM
 */
@Data
public class HouseListDTO {
    private String houseId;
    private String cityName;
    private String address;
    private String memberId;
    private String memberName;
    private String mobile;
    private Integer visitState;//0待确认开工,1装修中,2休眠中,3已完工
    private Integer showHouse;//0不是，1是 是否精选
    private String style;//设计风格
    private BigDecimal square;//外框面积
    private BigDecimal buildSquare;//建筑面积
    private Integer decorationType;//装修类型  0表示没有开始，1远程设计，2自带设计，3共享装修
    private Integer houseType;//0：新房；1：老房
    private String residential;//小区名
    private String building;//楼栋，后台客服填写
    private String unit;//单元号，后台客服填写
    private String number;//房间号，后台客服填写
    private String workDepositId;

    public String getHouseName() {
        return (TextUtils.isEmpty(getResidential()) ? "*" : getResidential())
                + (TextUtils.isEmpty(getBuilding()) ? "*" : getBuilding()) + "栋"
                + (TextUtils.isEmpty(getUnit()) ? "*" : getUnit()) + "单元"
                + (TextUtils.isEmpty(getNumber()) ? "*" : getNumber()) + "号";
    }
}
