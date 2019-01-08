package com.dangjia.acg.dto.member;

import com.dangjia.acg.modle.member.Customer;
import com.dangjia.acg.modle.member.CustomerRecord;
import com.dangjia.acg.modle.member.Member;
import lombok.Data;

/**
 * author: ysl
 * Date: 2019/1/7
 * Time: 16:00
 * 业主和客服的沟通记录进度
 */
@Data
public class MemberCustomerDTO {
    private Member member;
    private Customer customer;
    private CustomerRecord currCustomerRecord;//最新沟通记录
    private CustomerRecord remindCustomerRecord;//最近的提醒沟通记录id
}
