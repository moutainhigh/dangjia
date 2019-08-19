package com.dangjia.acg.api.app.worker;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.worker.Evaluate;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * author: Ronalcheng
 * Date: 2018/11/27 0027
 * Time: 14:25
 * 评价积分
 */
@FeignClient("dangjia-service-master")
@Api(value = "评价积分", description = "评价积分")
public interface EvaluateAPI {

    @PostMapping("app/worker/integral/list")
    @ApiOperation(value = "积分记录", notes = "积分记录")
    ServerResponse queryWorkIntegral(@RequestParam("request") HttpServletRequest request,
                                     @RequestParam("pageDTO") PageDTO pageDTO,
                                     @RequestParam("userToken") String userToken);

    @PostMapping("app/worker/evaluate/list")
    @ApiOperation(value = "评分记录", notes = "评分记录")
    ServerResponse queryEvaluates(@RequestParam("request") HttpServletRequest request,
                                  @RequestParam("userToken") String userToken,
                                  @RequestParam("evaluate") Evaluate evaluate);

    @PostMapping("app/worker/evaluate/checkNo")
    @ApiOperation(value = "管家不通过工匠完工申请", notes = "管家不通过工匠完工申请")
    ServerResponse checkNo(@RequestParam("userToken") String userToken,
                           @RequestParam("houseFlowApplyId") String houseFlowApplyId,
                           @RequestParam("content") String content);


    @PostMapping("app/worker/evaluate/materialRecord")
    @ApiOperation(value = "剩余材料登记", notes = "剩余材料登记")
    ServerResponse materialRecord(@RequestParam("userToken") String userToken,
                                  @RequestParam("houseFlowApplyId") String houseFlowApplyId,
                                  @RequestParam("content") String content,
                                  @RequestParam("star") int star,
                                  @RequestParam("productArr") String productArr,
                                  @RequestParam("imageList") String imageList,
                                  @RequestParam("latitude") String latitude,
                                  @RequestParam("longitude") String longitude);

    @PostMapping("app/worker/evaluate/checkOk")
    @ApiOperation(value = "管家审核通过工匠完工申请", notes = "管家审核通过工匠完工申请")
    ServerResponse checkOk(@RequestParam("userToken") String userToken,
                           @RequestParam("houseFlowApplyId") String houseFlowApplyId,
                           @RequestParam("content") String content,
                           @RequestParam("star") int star,
                           @RequestParam("imageList") String imageList,
                           @RequestParam("latitude") String latitude,
                           @RequestParam("longitude") String longitude);

    @PostMapping("app/worker/evaluate/saveEvaluateSupervisor")
    @ApiOperation(value = "业主评价管家完工", notes = "业主评价管家完工")
    ServerResponse saveEvaluateSupervisor(@RequestParam("userToken") String userToken,
                                          @RequestParam("houseFlowApplyId") String houseFlowApplyId,
                                          @RequestParam("content") String content,
                                          @RequestParam("star") int star,
                                          @RequestParam("onekey") String onekey);

    @PostMapping("app/worker/evaluate/saveEvaluate")
    @ApiOperation(value = "业主端评价工匠", notes = "业主端评价工匠")
    ServerResponse saveEvaluate(@RequestParam("userToken") String userToken,
                                @RequestParam("houseFlowApplyId") String houseFlowApplyId,
                                @RequestParam("wContent") String wContent,
                                @RequestParam("wStar") int wStar,
                                @RequestParam("sContent") String sContent,
                                @RequestParam("sStar") int sStar);
}
