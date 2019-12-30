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
    @ApiOperation(value = "查询退款/退货退款的退款详情根据退货单", notes = "查询仅退款的退款详情根据退货单")
    ServerResponse queryRefundOnlyHistoryOrderInfo(@RequestParam("cityId") String cityId,
                                                   @RequestParam("repairMendOrderId") String repairMendOrderId);

    @PostMapping("/app/refund/refundOrder/cancelRepairApplication")
    @ApiOperation(value = "撤销退款/退货退款申请", notes = "撤销退款申请")
    ServerResponse cancelRepairApplication(@RequestParam("cityId") String cityId,
                                           @RequestParam("repairMendOrderId") String repairMendOrderId);

    @PostMapping("/app/refund/refundOrder/addRepairComplain")
    @ApiOperation(value = "退款/退货退款业主申诉退货", notes = "业主申诉退货")
    ServerResponse addRepairComplain(@RequestParam("userToken") String userToken,
                                     @RequestParam("content") String content,
                                     @RequestParam("houseId") String houseId,
                                     @RequestParam("repairMendOrderId") String repairMendOrderId);

    @PostMapping("/app/refund/refundOrder/rejectRepairApplication")
    @ApiOperation(value = "退款/退货退款驳回退款申诉", notes = "驳回退款申诉")
    ServerResponse rejectRepairApplication(@RequestParam("repairMendOrderId") String repairMendOrderId,
                                 @RequestParam("userId") String userId);

    @PostMapping("/app/refund/refundOrder/agreeRepairApplication")
    @ApiOperation(value = "退款/退货退款同意退款申诉", notes = "同意退款申诉")
    ServerResponse agreeRepairApplication(@RequestParam("repairMendOrderId") String repairMendOrderId,
                                 @RequestParam("userId") String userId);

    @PostMapping("/app/refund/refundOrder/queryReturnRefundOrderList")
    @ApiOperation(value = "查询需退货退款的订单", notes = "查询需退货退款的订单")
    ServerResponse<PageInfo> queryReturnRefundOrderList(@RequestParam("pageDTO") PageDTO pageDTO,
                                                      @RequestParam("userToken") String userToken,
                                                      @RequestParam("cityId") String cityId,
                                                      @RequestParam("houseId") String houseId,
                                                      @RequestParam("searchKey") String searchKey);

    /**
     * 申请退货退款列表显示
     *
     * @param userToken        用户token
     * @param cityId           城市ID
     * @param houseId          房屋ID
     * @param orderProductAttr 需退款商品列表
     * @return
     */
    @PostMapping("/app/refund/refundOrder/queryReturnRefundInfoList")
    @ApiOperation(value = "申请退货退款列表显示", notes = "申请退货退款列表显示")
    ServerResponse queryReturnRefundInfoList(@RequestParam("userToken") String userToken,
                                           @RequestParam("cityId") String cityId,
                                           @RequestParam("houseId") String houseId,
                                           @RequestParam("orderProductAttr") String orderProductAttr);

    /**
     * 退货退款提交
     *
     * @param userToken        用户token
     * @param cityId           城市ID
     * @param houseId          房屋ID
     * @param orderProductAttr 需退款商品列表
     * @return
     */
    @PostMapping("/app/refund/refundOrder/saveReturnRefundInfo")
    @ApiOperation(value = "退货退款提交", notes = "退货退款提交")
    ServerResponse saveReturnRefundInfo(@RequestParam("userToken") String userToken,
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
    @PostMapping("/app/refund/refundOrder/queryReturnRefundHistoryOrderList")
    @ApiOperation(value = "查询退货退款的历史退款记录", notes = "查询退货退款的历史退款记录")
    ServerResponse<PageInfo> queryReturnRefundHistoryOrderList(@RequestParam("pageDTO") PageDTO pageDTO,
                                                             @RequestParam("userToken") String userToken,
                                                             @RequestParam("cityId") String cityId,
                                                             @RequestParam("houseId") String houseId,
                                                             @RequestParam("searchKey") String searchKey);

    @PostMapping("/app/refund/refundOrder/queryRetrunWorkerHistoryList")
    @ApiOperation(value = "查询退人工历史记录列表", notes = "查询退人工历史记录列表")
    ServerResponse<PageInfo> queryRetrunWorkerHistoryList(@RequestParam("pageDTO") PageDTO pageDTO,
                                                          @RequestParam("userToken") String userToken,
                                                          @RequestParam("cityId") String cityId,
                                                          @RequestParam("houseId") String houseId,
                                                          @RequestParam("searchType") String searchType);

    @PostMapping("/app/refund/refundOrder/queryRetrunWorkerHistoryDetail")
    @ApiOperation(value = "查询退人工历史记录详情", notes = "查询退人工历史记录列表")
    ServerResponse queryRetrunWorkerHistoryDetail(@RequestParam("cityId") String cityId,
                                                          @RequestParam("repairWorkOrderId") String repairWorkOrderId);

    @PostMapping("/app/refund/refundOrder/cancelWorkerApplication")
    @ApiOperation(value = "撤销退人工申请", notes = "撤销退人工申请")
    ServerResponse cancelWorkerApplication(@RequestParam("cityId") String cityId,
                                           @RequestParam("repairWorkOrderId") String repairWorkOrderId);

    @PostMapping("/app/refund/ownerAudit/searchAuditInfoByTaskId")
    @ApiOperation(value = "查询待审核的补人工订单", notes = "查询待审核的补人工订单")
    ServerResponse searchAuditInfoByTaskId(@RequestParam("cityId") String cityId,
                                           @RequestParam("taskId") String taskId);

    @PostMapping("/app/refund/ownerAudit/passAuditInfoByTaskId")
    @ApiOperation(value = "待审核的订单--审核通过", notes = "待审核的订单--审核通过")
    ServerResponse passAuditInfoByTaskId(@RequestParam("cityId") String cityId,
                                         @RequestParam("taskId") String taskId);

    @PostMapping("/app/refund/ownerAudit/failAuditInfoByTaskId")
    @ApiOperation(value = "待审核的订单--审核不通过", notes = "待审核的订单--审核不通过")
    ServerResponse failAuditInfoByTaskId(@RequestParam("cityId") String cityId,
                                         @RequestParam("taskId") String taskId);

    /**
     * 查询符合条件的可退人工商品
     * @param userToken 用户token
     * @param cityId  城市ID
     * @param houseId 房子ID
     * @param workerTypeId 工种ID
     * @param searchKey 商品名称
     * @return
     */
    @PostMapping("/app/refund/refundOrder/queryWorkerProductList")
    @ApiOperation(value = "退人工--查询符合条件的人工商品", notes = "退人工--查询符合条件的人工商品")
    ServerResponse queryWorkerProductList(@RequestParam("userToken") String userToken,
                                          @RequestParam("cityId") String cityId,
                                          @RequestParam("houseId") String houseId,
                                          @RequestParam("workerTypeId") String workerTypeId,
                                          @RequestParam("searchKey") String searchKey);



}
