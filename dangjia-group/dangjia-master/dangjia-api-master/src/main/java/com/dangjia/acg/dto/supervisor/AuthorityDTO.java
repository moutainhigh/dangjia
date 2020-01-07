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
    private String houseId;//房子ID
    private String houseName;//房子名称
    private String memberName;//业主名称
    private String memberId;//业主ID
    private String memberPhone;//业主手机号
    private Integer visitState;//0待确认开工,1装修中,2休眠中,3已完工,4提前结束装修 5提前结束装修申请中
    private Date constructionDate;//开工时间
    private Boolean selection;//是否选中
}
