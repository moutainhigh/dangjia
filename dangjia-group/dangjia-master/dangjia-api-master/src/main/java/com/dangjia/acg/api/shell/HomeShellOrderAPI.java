package com.dangjia.acg.api.shell;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * author: fzh
 * Date: 25/02/2020
 * Time: 下午 3:29
 */
@Api(description = "当家贝订单")
@FeignClient("dangjia-service-master")
public interface HomeShellOrderAPI {


    /**
     * 查询兑换记录列表
     * @param request
     * @param pageDTO 分页
     * @param exchangeClient 客户端：-1全部，1业主端，2工匠端
     * @param status 查询状态：-1全部，1待发货，2待收货，3已收货，4待退款，5已退款
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param searchKey 兑换人姓名/电话/单号
     * @return
     */
    @PostMapping("/app/shellOrder/queryOrderInfoList")
    @ApiOperation(value = "查询当家贝兑换记录", notes = "查询当家贝兑换记录")
    ServerResponse queryOrderInfoList(@RequestParam("request") HttpServletRequest request,
                                    @RequestParam("pageDTO") PageDTO pageDTO,
                                    @RequestParam("exchangeClient") Integer exchangeClient,
                                    @RequestParam("status") Integer status,
                                    @RequestParam("startTime") Date startTime,
                                    @RequestParam("endTime") Date endTime,
                                    @RequestParam("searchKey") String searchKey);

    /**
     * 查询兑换详情
     * @param request
     * @param homeOrderId 兑换记录ID
     * @return
     */
    @PostMapping("/app/shellOrder/queryOrderInfoDetail")
    @ApiOperation(value = "查询当家贝兑换详情", notes = "查询当家贝兑换详情")
    ServerResponse queryOrderInfoDetail(@RequestParam("request") HttpServletRequest request,
                                      @RequestParam("homeOrderId") String homeOrderId);

    /**
     * 修改订单状态
     * @param request
     * @param homeOrderId 兑换记录ID
     * @param status 2发货，5退货
     * @return
     */
    @PostMapping("/app/shellOrder/updateOrderInfo")
    @ApiOperation(value = "修改订单状态", notes = "修改订单状态")
    ServerResponse updateOrderInfo(@RequestParam("request") HttpServletRequest request,
                                   @RequestParam("homeOrderId") String homeOrderId,
                                   @RequestParam("status") Integer status);

}
