package com.dangjia.acg.dto.supervisor;

import lombok.Data;

import java.util.Date;

/**
 * @author Ruking.Cheng
 * @descrilbe 督导工地配置返回实体
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2020/1/7 4:17 PM
 */
@Data
public class AuthorityDTO {
    //------------公共-----//
    private String houseId;//房子ID
    private String houseName;//房子名称
    //------------中台-----//
    private String memberName;//业主名称
    private String memberId;//业主ID
    private String memberPhone;//业主手机号
    private Integer visitState;//0待确认开工,1装修中,2休眠中,3已完工,4提前结束装修 5提前结束装修申请中
    private Date constructionDate;//开工时间
    private Boolean selection;//是否选中
    //------------APP-----//
    private String personnel;//今日施工/参与人员
    private String constructionPeriod;//工期
    private String address;//地址
    private String price;//价格
    private Integer type;//0:施工,1:维保
    private Date startDate;//排期开始时间
    private Date endDate;//排期结束时间
}
