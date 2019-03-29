package com.dangjia.acg.dto.complain;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 申述返回体
 */
@Data
public class ComplainDTO {
    protected String id;
    private String memberId;
    private String memberName;
    private String memberNickName;
    private String memberMobile;
    private Integer complainType;
    private String userId;
    private String userName;
    private Integer status;
    private String description;
    private String businessId;
    private String houseId;
    private String houseName;	//houseid
    private String files;
    private List<String> fileList;
    protected Date createDate;// 创建日期
    protected Date modifyDate;// 修改日期
    private Object data;//公用产生体
}
