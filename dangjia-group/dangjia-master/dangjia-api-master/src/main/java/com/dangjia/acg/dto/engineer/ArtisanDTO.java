package com.dangjia.acg.dto.engineer;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * author: Ronalcheng
 * Date: 2019/1/24 0024
 * Time: 16:53
 */
@Data
public class ArtisanDTO {
    private String id;//工匠id
    private String name;// 姓名
    private String mobile;// 手机
    private String workerTypeName;//工种
    private Date createDate;//创建时间
    private Integer inviteNum;//存放邀约人数
    private Integer checkType;//状态:0审核中，1审核未通过,2审核已通过, 3账户已禁用,  5未提交资料
    private BigDecimal evaluationScore;//评价积分
    private Integer volume;//成交量
    private String superior;//邀请人

    private Integer realNameState;
    private String realNameDescribe;
    private String checkDescribe;

}
