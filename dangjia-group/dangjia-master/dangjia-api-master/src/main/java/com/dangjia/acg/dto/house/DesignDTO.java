package com.dangjia.acg.dto.house;

import lombok.Data;

/**
 * author: Ronalcheng
 * Date: 2018/11/10 0010
 * Time: 16:55
 */
@Data
public class DesignDTO {
    private String houseId;
    private int designerOk;
    private String residential;//小区名
    private String building;
    private String unit;
    private String number;
    private String schedule;//进度
    private String name;//业主姓名
    private String nickName;//昵称
    private String mobile;// 电话
    private String image;// 平面图
    private String imageUrl;// 平面图URL
    private Integer decorationType;//haveimagestate
    private String operatorId;// 操作人ID
    private String operatorName;// 操作人名字
    private String operatorMobile;// 操作人电话

}
