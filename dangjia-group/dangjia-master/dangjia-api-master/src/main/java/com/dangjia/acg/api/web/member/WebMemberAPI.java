package com.dangjia.acg.api.web.member;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.member.Customer;
import com.dangjia.acg.modle.member.CustomerRecord;
import com.dangjia.acg.modle.member.MemberLabel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * author: Ronalcheng
 * Date: 2018/11/3 0003
 * Time: 16:30
 * web端用户接口
 */
@FeignClient("dangjia-service-master")
@Api(value = "用户接口", description = "用户接口")
public interface WebMemberAPI {

    @PostMapping("web/member/getMemberList")
    @ApiOperation(value = "获取业主列表", notes = "获取业主列表")
    ServerResponse getMemberList(@RequestParam("request") HttpServletRequest request,@RequestParam("pageDTO")  PageDTO pageDTO);

    @PostMapping("web/member/getMemberLabelList")
    @ApiOperation(value = "获取标签列表", notes = "获取标签列表")
    ServerResponse getMemberLabelList(@RequestParam("request") HttpServletRequest request,@RequestParam("pageDTO")  PageDTO pageDTO);

    @PostMapping("web/member/setMemberLabel")
    @ApiOperation(value = "增加/修改标签", notes = "增加/修改标签")
    ServerResponse setMemberLabel(@RequestParam("request") HttpServletRequest request,@RequestParam("memberLabel") MemberLabel memberLabel);

    @PostMapping("web/member/getCustomerRecordList")
    @ApiOperation(value = "查询业主沟通记录，指定业主id的所有记录 （null：查所有记录）", notes = "查询业主沟通记录，指定业主id的所有记录 （null：查所有记录）")
    ServerResponse getCustomerRecordList(@RequestParam("request") HttpServletRequest request,@RequestParam("pageDTO")  PageDTO pageDTO,@RequestParam("memberId")  String memberId);

    @PostMapping("web/member/addCustomerRecord")
    @ApiOperation(value = "添加业主沟通记录", notes = "添加业主沟通记录")
    ServerResponse addCustomerRecord(@RequestParam("request") HttpServletRequest request,@RequestParam("customerRecord") CustomerRecord customerRecord);

    @PostMapping("web/member/setMemberByUserId")
    @ApiOperation(value = "修改业主的客服跟进", notes = "修改业主的客服跟进")
    ServerResponse setMemberCustomer(@RequestParam("request") HttpServletRequest request,@RequestParam("customer") Customer customer);


}
