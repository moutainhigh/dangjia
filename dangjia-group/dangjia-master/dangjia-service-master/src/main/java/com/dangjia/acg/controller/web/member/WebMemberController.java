package com.dangjia.acg.controller.web.member;

import com.dangjia.acg.api.web.member.WebMemberAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.member.Customer;
import com.dangjia.acg.modle.member.CustomerRecord;
import com.dangjia.acg.modle.member.MemberLabel;
import com.dangjia.acg.service.member.CustomerRecordService;
import com.dangjia.acg.service.member.CustomerService;
import com.dangjia.acg.service.member.MemberLabelService;
import com.dangjia.acg.service.member.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * author: Ronalcheng
 * Date: 2018/11/3 0003
 * Time: 16:35
 */
@RestController
public class WebMemberController implements WebMemberAPI {

    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberLabelService memberLabelService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private CustomerRecordService customerRecordService;

    @Override
    @ApiMethod
    public ServerResponse getMemberList(HttpServletRequest request, PageDTO pageDTO) {
        return memberService.getMemberList(request, pageDTO);
    }

    @Override
    public ServerResponse getMemberLabelList(HttpServletRequest request, PageDTO pageDTO) {
        return memberLabelService.getMemberLabelList(pageDTO);
    }

    @Override
    public ServerResponse setMemberLabel(HttpServletRequest request, MemberLabel memberLabel) {
        return memberLabelService.setMemberLabel(memberLabel);
    }

    @Override
    public ServerResponse getCustomerRecordList(HttpServletRequest request, PageDTO pageDTO, String memberId) {
        return customerRecordService.getCustomerRecordList(pageDTO, memberId);
    }

    @Override
    public ServerResponse addCustomerRecord(HttpServletRequest request, CustomerRecord customerRecord) {
        return customerRecordService.addCustomerRecord(customerRecord);
    }

    @Override
    public ServerResponse setMemberCustomer(HttpServletRequest request, Customer customer) {
        return customerService.setMemberCustomer(customer);
    }
}
