package com.dangjia.acg.dto.member;

import com.dangjia.acg.modle.member.Customer;
import com.dangjia.acg.modle.member.CustomerRecord;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.member.MemberLabel;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * author: ysl
 * Date: 2019/1/7
 * Time: 16:00
 * 业主和客服的沟通记录进度
 */
@Data
public class MemberCustomerDTO {
    private String memberId;//业主id
    private String memberName;//业主姓名
    private String memberNickName;//业主昵称
    private String mobile;//手机
    private String referrals;//邀请人
    private String source;//来源
    private Integer stage;//阶段
    private String userId;//当前客服id
    private String userName;//当前客服名字
    private String remindContent;//提醒内容
    private Date remindTime;//提醒时间
    private Date lastRecord;//上次跟进时间
    private List<MemberLabel> memberLabelList;//多个标签对象
    //    private Member member;
//    private Customer customer;
//    private CustomerRecord currCustomerRecord;//最新沟通记录
//    private CustomerRecord remindCustomerRecord;//最近的提醒沟通记录id
}
