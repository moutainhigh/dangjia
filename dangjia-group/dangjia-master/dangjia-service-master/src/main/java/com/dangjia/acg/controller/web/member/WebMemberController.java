package com.dangjia.acg.controller.web.member;

import com.dangjia.acg.api.web.member.WebMemberAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.member.Customer;
import com.dangjia.acg.modle.member.CustomerRecord;
import com.dangjia.acg.modle.member.Member;
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
    public ServerResponse getMemberList(HttpServletRequest request, PageDTO pageDTO, Integer stage, String userRole,String searchKey, String parentId, String childId,String orderBy,String type, String userId,String beginDate,String endDate) {
        String cityId = request.getParameter(Constants.CITY_ID);
        String userID = request.getParameter(Constants.USERID);
        return memberService.getMemberList(pageDTO,  cityId, userID , stage,  userRole,searchKey, parentId, childId, orderBy,type,userId,beginDate,endDate);
    }

    @Override
    @ApiMethod
    public ServerResponse setMember(HttpServletRequest request, Member member) {
        return memberService.setMember(member);
    }

    @Override
    @ApiMethod
    public ServerResponse getMemberLabelList(HttpServletRequest request, PageDTO pageDTO) {
        return memberLabelService.getMemberLabelList(pageDTO);
    }

    @Override
    @ApiMethod
    public ServerResponse setMemberLabel(HttpServletRequest request, String jsonStr) {
        return memberLabelService.setMemberLabel(jsonStr);
    }

    @Override
    @ApiMethod
    public ServerResponse getCustomerRecordList(HttpServletRequest request, PageDTO pageDTO, String memberId) {
        return customerRecordService.getCustomerRecordList(pageDTO, memberId);
    }

    @Override
    @ApiMethod
    public ServerResponse addCustomerRecord(HttpServletRequest request, CustomerRecord customerRecord) {
        return customerRecordService.addCustomerRecord(customerRecord);
    }

    @Override
    @ApiMethod
    public ServerResponse setMemberCustomer(HttpServletRequest request, Customer customer) {
        return customerService.setMemberCustomer(customer);
    }

    @Override
    @ApiMethod
    public ServerResponse certificationList(HttpServletRequest request, PageDTO pageDTO, String searchKey,String cityId, String policyId,Integer realNameState) {
        return memberService.certificationList(pageDTO, searchKey,  cityId,  policyId,realNameState);
    }

    @Override
    @ApiMethod
    public ServerResponse certificationDetails(HttpServletRequest request, String userId) {
        return memberService.certificationDetails(userId);
    }

    @Override
    @ApiMethod
    public ServerResponse certificationAuditing(HttpServletRequest request, String userId, Integer realNameState, String realNameDescribe) {
        return memberService.certificationAuditing(userId, realNameState, realNameDescribe);
    }

    @Override
    @ApiMethod
    public ServerResponse  queryInsurances(HttpServletRequest request,String type, String searchKey,PageDTO pageDTO){
        return memberService.queryInsurances(type, searchKey, pageDTO);
    }
}
