package com.dangjia.acg.controller.web.member;

import com.dangjia.acg.api.web.member.WebMemberAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
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
    @Override
    @ApiMethod
    public ServerResponse getMemberList(HttpServletRequest request,  PageDTO pageDTO){
        return memberService.getMemberList(request,pageDTO);
    }
}
