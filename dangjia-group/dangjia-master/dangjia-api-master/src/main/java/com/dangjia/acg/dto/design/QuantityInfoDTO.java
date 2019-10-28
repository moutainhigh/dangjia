package com.dangjia.acg.dto.design;

import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dto.house.UserInfoDateDTO;
import com.github.pagehelper.PageInfo;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class QuantityInfoDTO {

    private UserInfoDateDTO rowList;//房子信息list

    private List<Map<String,Object>> listFour;//抢单List
    private PageInfo typeOneList;//量房
    private PageInfo typeTwoList;//平面图
    private PageInfo typeThreeList;//施工图

    private String username;// 销售名称
    private String userMobile;// 销售手机号码
    private String name;// 业主名称
    private String mobile;// 业主手机号码

    private String residential;//小区名
    private String building;//楼栋，后台客服填写
    private String unit;//单元号，后台客服填写
    private String number;//房间号，后台客服填写

    private Integer numberType;//流程状态1-抢单 2-量房 3-平面图 4施工图

    private String arrHouseName;//房间号，后台客服填写



    public String getHouseName() {
        return (CommonUtil.isEmpty(getResidential()) ? "*" : getResidential())
                + (CommonUtil.isEmpty(getBuilding()) ? "*" : getBuilding()) + "栋"
                + (CommonUtil.isEmpty(getUnit()) ? "*" : getUnit()) + "单元"
                + (CommonUtil.isEmpty(getNumber()) ? "*" : getNumber()) + "号";
    }

}
