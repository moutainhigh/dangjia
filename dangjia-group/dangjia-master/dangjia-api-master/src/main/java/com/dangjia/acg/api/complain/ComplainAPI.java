package com.dangjia.acg.api.complain;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 *
 */
@FeignClient("dangjia-service-master")
@Api(value = "申诉接口", description = "申诉接口")
public interface ComplainAPI {

    @PostMapping("/complain/addComplain")
    @ApiOperation(value = "添加申诉", notes = "添加申诉")
    ServerResponse addComplain(@RequestParam("request") HttpServletRequest request,
                               @RequestParam("userToken") String userToken,
                               @RequestParam("memberId") String memberId,
                               @RequestParam("complainType") Integer complainType,
                               @RequestParam("businessId") String businessId,
                               @RequestParam("houseId") String houseId,
                               @RequestParam("files") String files);

    @PostMapping("/complain/getComplainList")
    @ApiOperation(value = "查询申诉", notes = "查询申诉")
    ServerResponse getComplainList(@RequestParam("request") HttpServletRequest request,
                                   @RequestParam("pageDTO") PageDTO pageDTO,
                                   @RequestParam("complainType") Integer complainType,
                                   @RequestParam("state") Integer state,
                                   @RequestParam("searchKey") String searchKey);


    @PostMapping("/complain/updataComplain")
    @ApiOperation(value = "修改申诉", notes = "修改申诉")
    ServerResponse updataComplain(@RequestParam("request") HttpServletRequest request,
                                  @RequestParam("userId") String userId,
                                  @RequestParam("complainId") String complainId,
                                  @RequestParam("state") Integer state,
                                  @RequestParam("description") String description,
                                  @RequestParam("operateId") String operateId,  //操作人id
                                  @RequestParam("operateName") String operateName,  //操作人姓名
                                  @RequestParam("files") String files);

    @PostMapping("/complain/getComplain")
    @ApiOperation(value = "获取申诉详情", notes = "获取申诉详情")
    ServerResponse getComplain(@RequestParam("request") HttpServletRequest request,
                               @RequestParam("complainId") String complainId);

}
