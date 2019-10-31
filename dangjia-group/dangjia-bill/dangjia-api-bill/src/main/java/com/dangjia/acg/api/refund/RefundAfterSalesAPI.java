package com.dangjia.acg.api.refund;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created with IntelliJ IDEA.
 * author: fzh
 * Date: 25/10/2019
 * Time: 下午 4:53
 */
@Api(description = "退款/售后")
@FeignClient("dangjia-service-bill")
public interface RefundAfterSalesAPI {

    @PostMapping("/app/refund/refundOrder/queryRefundOnlyOrderList")
    @ApiOperation(value = "查询需仅退款的订单", notes = "查询需仅退款的订单")
    ServerResponse<PageInfo> queryRefundOnlyOrderList(@RequestParam("pageDTO") PageDTO pageDTO,
                                                      @RequestParam("userToken") String userToken,
                                                      @RequestParam("cityId") String cityId,
                                                      @RequestParam("houseId") String houseId,
                                                      @RequestParam("searchKey") String searchKey);

    /**
     * 申请退款列表显示
     *
     * @param userToken        用户token
     * @param cityId           城市ID
     * @param houseId          房屋ID
     * @param orderProductAttr 需退款商品列表
     * @return
     */
    @PostMapping("/app/refund/refundOrder/queryRefundonlyInfoList")
    @ApiOperation(value = "申请退款列表显示", notes = "申请退款列表显示")
    ServerResponse queryRefundonlyInfoList(@RequestParam("userToken") String userToken,
                                      @RequestParam("cityId") String cityId,
                                      @RequestParam("houseId") String houseId,
                                      @RequestParam("orderProductAttr") String orderProductAttr);

    /**
     * 仅退款提交
     *
     * @param userToken        用户token
     * @param cityId           城市ID
     * @param houseId          房屋ID
     * @param orderProductAttr 需退款商品列表
     * @return
     */
    @PostMapping("/app/refund/refundOrder/saveRefundonlyInfo")
    @ApiOperation(value = "仅退款提交", notes = "仅退款提交")
    ServerResponse saveRefundonlyInfo(@RequestParam("userToken") String userToken,
                                      @RequestParam("cityId") String cityId,
                                      @RequestParam("houseId") String houseId,
                                      @RequestParam("orderProductAttr") String orderProductAttr);

    /**
     * 查询仅退款订单的历史记录
     *
     * @param pageDTO
     * @param userToken
     * @param cityId
     * @param houseId
     * @param searchKey
     * @return
     */
    @PostMapping("/app/refund/refundOrder/queryRefundOnlyHistoryOrderList")
    @ApiOperation(value = "查询仅退款的历史退款记录", notes = "查询仅退款的历史退款记录")
    ServerResponse<PageInfo> queryRefundOnlyHistoryOrderList(@RequestParam("pageDTO") PageDTO pageDTO,
                                                             @RequestParam("userToken") String userToken,
                                                             @RequestParam("cityId") String cityId,
                                                             @RequestParam("houseId") String houseId,
                                                             @RequestParam("searchKey") String searchKey);

    /**
     * 查询退款详情
     *
     * @param cityId
     * @param repairMendOrderId
     * @return
     */
    @PostMapping("/app/refund/refundOrder/queryRefundOnlyHistoryOrderInfo")
    @ApiOperation(value = "查询仅退款的退款详情根据退货单", notes = "查询仅退款的退款详情根据退货单")
    ServerResponse queryRefundOnlyHistoryOrderInfo(@RequestParam("cityId") String cityId,
                                                   @RequestParam("repairMendOrderId") String repairMendOrderId);

    @PostMapping("/app/refund/refundOrder/cancelRepairApplication")
    @ApiOperation(value = "撤销退款申请", notes = "撤销退款申请")
    ServerResponse cancelRepairApplication(@RequestParam("cityId") String cityId,
                                           @RequestParam("repairMendOrderId") String repairMendOrderId);

    @PostMapping("/app/refund/refundOrder/addRepairComplain")
    @ApiOperation(value = "业主申诉退货", notes = "业主申诉退货")
    ServerResponse addRepairComplain(@RequestParam("userToken") String userToken,
                                     @RequestParam("content") String content,
                                     @RequestParam("houseId") String houseId,
                                     @RequestParam("repairMendOrderId") String repairMendOrderId);
}
