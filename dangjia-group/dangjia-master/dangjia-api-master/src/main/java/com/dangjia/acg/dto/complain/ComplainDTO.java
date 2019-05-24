package com.dangjia.acg.dto.complain;

import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 申诉返回体
 */
@Data
public class ComplainDTO {
    protected String id;
    private String memberId;  //对象Id
    private String memberName;
    private String memberNickName;
    private String memberMobile;
    private Integer complainType;
    private String userId;   //发起人ID
    private String operateId; //操作人Id
    private String operateName;  //操作人姓名
    private String userName;
    private Integer status;
    private String description;//处理描述
    private String businessId;//对应业务ID
    private String houseId;//房子ID
    private String houseName;	//房子名称
    private String files;//文件集
    private List<String> fileList;
    protected Date createDate;// 创建日期
    protected Date modifyDate;// 修改日期
    private Object data;//公用产生体
    private String content;//描述
    private String workerId;//描述
}
