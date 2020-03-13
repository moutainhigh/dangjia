package com.dangjia.acg.api.web.engineer;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * maintenanceRecordId
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 13/12/2019
 * Time: 上午 9:52
 */
@FeignClient("dangjia-service-master")
@Api(value = "维保接口", description = "维保接口")
public interface DjMaintenanceRecordAPI {


    /**
     * 保存质保记录（申请质保）
     *
     * @param userToken             用户token
     * @param houseId               房子ID
     * @param workerTypeSafeOrderId 保险订单ID
     * @param remark                备注
     * @param images                图片，多张用逗号分隔
     * @param productId             商品ID
     * @return
     */
    @PostMapping(value = "app/engineer/saveMaintenanceRecord")
    @ApiOperation(value = "保存质保记录（申请质保）", notes = "保存质保记录（申请质保）")
    ServerResponse saveMaintenanceRecord(@RequestParam("userToken") String userToken,
                                         @RequestParam("houseId") String houseId,
                                         @RequestParam("workerTypeSafeOrderId") String workerTypeSafeOrderId,
                                         @RequestParam("remark") String remark,
                                         @RequestParam("images") String images,
                                         @RequestParam("productId") String productId);

    @PostMapping(value = "app/engineer/searchMaintenanceProduct")
    @ApiOperation(value = "消息弹窗--维保商品订单", notes = "消息弹窗--维保商品订单")
    ServerResponse searchMaintenanceProduct(@RequestParam("userToken") String userToken,
                                            @RequestParam("houseId") String houseId,
                                            @RequestParam("taskId") String taskId);

    @PostMapping(value = "app/engineer/searchExpenseMaintenanceProduct")
    @ApiOperation(value = "消息弹窗--报销商品订单", notes = "消息弹窗--报销商品订单")
    ServerResponse searchExpenseMaintenanceProduct(@RequestParam("userToken") String userToken,
                                            @RequestParam("houseId") String houseId,
                                            @RequestParam("taskId") String taskId);

    @PostMapping(value = "app/engineer/searchAcceptanceApplication")
    @ApiOperation(value = "消息弹窗--验收申请单", notes = "消息弹窗--验收申请单")
    ServerResponse searchAcceptanceApplication(@RequestParam("userToken") String userToken,
                                            @RequestParam("houseId") String houseId,
                                            @RequestParam("taskId") String taskId);

    @PostMapping(value = "app/engineer/saveMaintenanceProduct")
    @ApiOperation(value = "消息弹窗提交--提交维保商品订单", notes = "消息弹窗--维保商品订单")
    ServerResponse saveMaintenanceProduct(@RequestParam("userToken") String userToken,
                                          @RequestParam("houseId") String houseId,
                                          @RequestParam("taskId") String taskId,
                                          @RequestParam("cityId") String cityId);

    @PostMapping(value = "app/engineer/saveExpenseMaintenanceProduct")
    @ApiOperation(value = "消息弹窗提交--提交报销商品订单", notes = "消息弹窗--报销商品订单")
    ServerResponse saveExpenseMaintenanceProduct(@RequestParam("userToken") String userToken,
                                                 @RequestParam("houseId") String houseId,
                                                 @RequestParam("taskId") String taskId,
                                                 @RequestParam("cityId") String cityId);

    @PostMapping(value = "app/engineer/saveAcceptanceApplication")
    @ApiOperation(value = "消息弹窗提交--提交验收申请单", notes = "消息弹窗--验收申请单")
    ServerResponse saveAcceptanceApplication(@RequestParam("userToken") String userToken,
                                             @RequestParam("houseId") String houseId,
                                             @RequestParam("taskId") String taskId,
                                             @RequestParam("auditResult") Integer auditResult);

    @PostMapping(value = "app/engineer/saveAcceptanceApplicationJob")
    @ApiOperation(value = "定时任务--工匠维保申请验收，业主到期自动处理任务", notes = "定时任务--工匠维保申请验收，业主到期自动处理任务")
    void saveAcceptanceApplicationJob();

    @PostMapping(value = "app/engineer/searchMaintenaceRecordInfo")
    @ApiOperation(value = "查询质保详情", notes = "查询质保详情（按提交人类型查询）")
    ServerResponse searchMaintenaceRecordInfo(@RequestParam("maintenanceRecordId") String maintenanceRecordId,
                                              @RequestParam("type")Integer type);

    @PostMapping(value = "app/engineer/saveMaintenanceRecordOrder")
    @ApiOperation(value = "质保申请--提交订单", notes = "质保申请--提交订单")
    ServerResponse saveMaintenanceRecordOrder(@RequestParam("userToken") String userToken,
                                              @RequestParam("houseId") String houseId,
                                              @RequestParam("maintenanceRecordId") String maintenanceRecordId,
                                              @RequestParam("maintenanceRecordType") Integer maintenanceRecordType,
                                              @RequestParam("cityId") String cityId);


    @PostMapping(value = "app/engineer/workerEndMaintenanceRecord")
    @ApiOperation(value = "工匠结束维保", notes = "工匠结束维保")
    ServerResponse workerEndMaintenanceRecord(@RequestParam("userToken") String userToken,
                                              @RequestParam("maintenanceRecordId") String maintenanceRecordId,
                                               @RequestParam("image") String image,
                                               @RequestParam("remark") String remark,
                                              @RequestParam("cityId") String cityId);



    @PostMapping(value = "app/engineer/endMaintenanceSearchProduct")
    @ApiOperation(value = "提前结束--勘查费用商品页面", notes = "提前结束--勘查费用商品页面")
    ServerResponse endMaintenanceSearchProduct(@RequestParam("userToken") String userToken,
                                              @RequestParam("houseId") String houseId,
                                              @RequestParam("maintenanceRecordId") String maintenanceRecordId,
                                              @RequestParam("cityId") String cityId);

    @PostMapping(value = "app/engineer/endMaintenanceRecord")
    @ApiOperation(value = "提前结束--结束维保", notes = "提前结束--结束维保")
    ServerResponse endMaintenanceRecord(@RequestParam("userToken") String userToken,
                                       @RequestParam("houseId") String houseId,
                                       @RequestParam("maintenanceRecordId") String maintenanceRecordId,
                                       @RequestParam("cityId") String cityId);

    @PostMapping(value = "app/engineer/evaluationMaintenanceRecord")
    @ApiOperation(value = "质保管理--发表评价", notes = "质保管理--发表评价")
    ServerResponse evaluationMaintenanceRecord(@RequestParam("userToken") String userToken,
                                        @RequestParam("houseId") String houseId,
                                        @RequestParam("maintenanceRecordId") String maintenanceRecordId,
                                        @RequestParam("workerId") String workerId,
                                        @RequestParam("start") Integer start,
                                        @RequestParam("content") String content,
                                        @RequestParam("image") String image,
                                        @RequestParam("cityId") String cityId);

    @PostMapping(value = "web/engineer/queryMaintenanceRecordDetail")
    @ApiOperation(value = "查询质保详情信息", notes = "查询质保详情信息")
    ServerResponse queryMaintenanceRecordDetail(@RequestParam("userToken") String userToken,
                                                @RequestParam("maintenanceRecordId") String maintenanceRecordId);


    @PostMapping(value = "web/engineer/queryDjMaintenanceRecordList")
    @ApiOperation(value = "查询质保审核列表", notes = "查询质保审核列表")
    ServerResponse queryDjMaintenanceRecordList(@RequestParam("pageDTO") PageDTO pageDTO,
                                                @RequestParam("searchKey") String searchKey,
                                                @RequestParam("state") Integer state);


    @PostMapping(value = "web/engineer/queryDjMaintenanceRecordDetail")
    @ApiOperation(value = "查询质保审核详情", notes = "查询质保审核详情")
    ServerResponse queryDjMaintenanceRecordDetail(@RequestParam("id") String id);

    @PostMapping(value = "web/engineer/setDjMaintenanceRecord")
    @ApiOperation(value = "处理质保审核", notes = "处理质保审核")
    ServerResponse setDjMaintenanceRecord(@RequestParam("id") String id,
                                          @RequestParam("state") Integer state,
                                          @RequestParam("userId") String userId);

    @PostMapping(value = "web/engineer/queryMemberList")
    @ApiOperation(value = "查询督导列表", notes = "查询督导列表")
    ServerResponse queryMemberList(@RequestParam("pageDTO") PageDTO pageDTO,
                                   @RequestParam("name") String name);




    @PostMapping(value = "app/engineer/updateTaskStackData")
    @ApiOperation(value = "修改消息状态", notes = "修改消息状态")
    ServerResponse updateTaskStackData(@RequestParam("id") String id);

    @PostMapping(value = "app/engineer/queryMaintenanceRecordList")
    @ApiOperation(value = "工匠--查询维保记录(当前房子的所有维保记录)", notes = "工匠--查询维保记录(当前房子的所有维保记录)")
    ServerResponse queryMaintenanceRecordList(@RequestParam("userToken") String userToken,@RequestParam("pageDTO") PageDTO pageDTO,@RequestParam("houseId") String houseId);

    @PostMapping(value = "app/engineer/queryDimensionRecord")
    @ApiOperation(value = "查询维保责任记录", notes = "查询维保责任记录")
    ServerResponse queryDimensionRecord(@RequestParam("userToken") String userToken,@RequestParam("houseId") String houseId);

    @PostMapping(value = "app/engineer/queryDimensionRecordInFo")
    @ApiOperation(value = "查询维保详情", notes = "查询维保详情")
    ServerResponse queryDimensionRecordInFo(@RequestParam("mrrpId") String mrrpId);

    @PostMapping(value = "app/engineer/queryDimensionInfoByTaskId")
    @ApiOperation(value = "消息弹窗--查看维保定责", notes = "消息弹窗--查看维保定责")
    ServerResponse queryDimensionInfoByTaskId(@RequestParam("taskId") String taskId);

    @PostMapping(value = "app/engineer/insertResponsibleParty")
    @ApiOperation(value = "新增工匠申诉", notes = "新增工匠申诉")
    ServerResponse insertResponsibleParty(@RequestParam("mrrpId") String mrrpId,
                                          @RequestParam("responsiblePartyId") String responsiblePartyId,
                                          @RequestParam("houseId") String houseId,
                                          @RequestParam("description") String description,
                                          @RequestParam("image") String image);

    @PostMapping(value = "app/engineer/queryResponsibleParty")
    @ApiOperation(value = "查询工匠申诉", notes = "查询工匠申诉")
    ServerResponse queryResponsibleParty(@RequestParam("responsiblePartyId") String responsiblePartyId,
                                         @RequestParam("houseId") String houseId);

    @PostMapping(value = "app/engineer/toQualityMoney")
    @ApiOperation(value = "查询工匠缴纳质保金", notes = "查询工匠缴纳质保金")
    ServerResponse toQualityMoney(@RequestParam("data") String data);

    @PostMapping(value = "app/engineer/queryRobOrderInFo")
    @ApiOperation(value = "查询工匠抢单详情", notes = "查询工匠抢单详情")
    ServerResponse queryRobOrderInFo(@RequestParam("userToken") String userToken,
                                     @RequestParam("workerId") String workerId,
                                     @RequestParam("houseId") String houseId,
                                     @RequestParam("data") String data);



    @PostMapping("/web/queryGuaranteeMoneyList")
    @ApiOperation(value = "店铺-质保金变动记录查询", notes = "店铺-质保金变动记录查询")
    ServerResponse queryGuaranteeMoneyList(@RequestParam("pageDTO") PageDTO pageDTO,
                                           @RequestParam("userId") String userId,
                                           @RequestParam("cityId") String cityId);


    @PostMapping("/web/queryGuaranteeMoneyDetail")
    @ApiOperation(value = "店铺-缴纳质保金详情", notes = "缴纳质保金详情")
    ServerResponse queryGuaranteeMoneyDetail(@RequestParam("userId") String userId,
                                             @RequestParam("cityId") String cityId,
                                             @RequestParam("accountflowRecordId") String accountflowRecordId);







    @PostMapping(value = "app/engineer/insertMaintenanceRecordProduct")
    @ApiOperation(value = "添加维保商品到购物篮", notes = "添加维保商品到购物篮")
    ServerResponse insertMaintenanceRecordProduct(@RequestParam("userToken") String userToken,
                                                  @RequestParam("houseId") String houseId,
                                                  @RequestParam("maintenanceRecordId") String maintenanceRecordId,
                                                  @RequestParam("productId") String productId,
                                                  @RequestParam("shopCount") Double shopCount);

    @PostMapping(value = "app/engineer/setMaintenanceRecordProduct")
    @ApiOperation(value = "管家/工匠维保购物篮处理", notes = "管家/工匠维保购物篮处理")
    ServerResponse setMaintenanceRecordProduct(@RequestParam("userToken") String userToken,
                                               @RequestParam("houseId") String houseId,
                                               @RequestParam("maintenanceRecordId") String maintenanceRecordId);


    @PostMapping(value = "app/engineer/submitQualityAssurance")
    @ApiOperation(value = "维保购物篮", notes = "维保购物篮")
    ServerResponse queryMaintenanceShoppingBasket(@RequestParam("userToken") String userToken,
                                                  @RequestParam("houseId") String houseId,
                                                  @RequestParam("maintenanceRecordId") String maintenanceRecordId);

    @PostMapping(value = "app/engineer/setMaintenanceSolve")
    @ApiOperation(value = "管家质保已解决", notes = "管家质保已解决")
    ServerResponse setMaintenanceSolve(@RequestParam("userToken") String userToken,
                                       @RequestParam("maintenanceRecordId") String maintenanceRecordId,
                                       @RequestParam("remark") String remark,
                                       @RequestParam("image") String image);

    @PostMapping(value = "app/engineer/deleteMaintenanceRecordProduct")
    @ApiOperation(value = "删除购物篮商品", notes = "删除购物篮商品")
    ServerResponse deleteMaintenanceRecordProduct(@RequestParam("id") String id);

    @PostMapping(value = "app/engineer/setMaintenanceHandlesSubmissions")
    @ApiOperation(value = "管家质保处理提交", notes = "管家质保处理提交")
    ServerResponse setMaintenanceHandlesSubmissions(@RequestParam("userToken") String userToken,
                                                    @RequestParam("maintenanceRecordId") String maintenanceRecordId,
                                                    @RequestParam("remark") String remark,
                                                    @RequestParam("image") String image);

    @PostMapping(value = "app/engineer/addApplyNewspaper")
    @ApiOperation(value = "工匠申请报销", notes = "工匠申请报销")
    ServerResponse addApplyNewspaper(@RequestParam("money") Double money,
                                     @RequestParam("description") String description,
                                     @RequestParam("image") String image,
                                     @RequestParam("businessId") String businessId);


    @PostMapping(value = "app/engineer/queryComplain")
    @ApiOperation(value = "查询报销记录", notes = "查询报销记录")
    ServerResponse queryComplain(@RequestParam("userToken") String userToken,@RequestParam("pageDTO") PageDTO pageDTO,@RequestParam("maintenanceRecordId") String maintenanceRecordId);

    @PostMapping(value = "app/engineer/queryComplainInFo")
    @ApiOperation(value = "查询报销记录详情", notes = "查询报销记录详情")
    ServerResponse queryComplainInFo(@RequestParam("id") String id);

    @PostMapping(value = "web/engineer/handleAppeal")
    @ApiOperation(value = "处理工匠报销申诉", notes = "处理工匠报销申诉")
    ServerResponse handleAppeal(@RequestParam("id") String id,
                                @RequestParam("type") Integer type,
                                @RequestParam("actualMoney") Double actualMoney,
                                @RequestParam("operateId") String operateId,
                                @RequestParam("rejectReason") String rejectReason);


    @PostMapping(value = "web/engineer/workerApplyCollect")
    @ApiOperation(value = "工匠申请维保验收", notes = "工匠申请维保验收")
    ServerResponse workerApplyCollect(@RequestParam("id") String id,
                                      @RequestParam("remark") String remark,
                                      @RequestParam("image") String image);


    @PostMapping(value = "web/engineer/confirmStart")
    @ApiOperation(value = "已确认可开工", notes = "已确认可开工")
    ServerResponse confirmStart(@RequestParam("businessId") String businessId);

    @PostMapping(value = "web/engineer/setWorkerMaintenanceGoods")
    @ApiOperation(value = "工匠维保要货", notes = "工匠维保要货")
    ServerResponse setWorkerMaintenanceGoods(@RequestParam("userToken") String userToken,
                                             @RequestParam("maintenanceRecordId") String maintenanceRecordId,
                                             @RequestParam("houseId") String houseId);

    /**
     * 维保申诉成功后--人工定责列表查询
     * @param status 查询状态：-1全部，1待处理，2处理
     * @param searchKey 查询条：业主名称/电话
     * @return
     */
    @PostMapping(value = "web/engineer/searchManualAllocation")
    @ApiOperation(value = "申诉成功后平台人工定责--查询列表", notes = "申诉成功后平台人工定责--查询列表")
    ServerResponse searchManualAllocation(@RequestParam("pageDTO") PageDTO pageDTO,@RequestParam("status") Integer status,
                                          @RequestParam("searchKey") String searchKey,@RequestParam("cityId") String cityId);

    @PostMapping(value = "web/engineer/searchManualAllocationDetail")
    @ApiOperation(value = "申诉成功后平台人工定责--查询详情", notes = "申诉成功后平台人工定责--查询详情")
    ServerResponse searchManualAllocationDetail(@RequestParam("manuaId") String manuaId);

    @PostMapping(value = "web/engineer/saveNewPartyInfo")
    @ApiOperation(value = "申诉成功后平台人工定责--责任分配", notes = "申诉成功后平台人工定责--责任分配")
    ServerResponse saveNewPartyInfo(@RequestParam("manuaId") String manuaId,@RequestParam("newPartyInfo") String newPartyInfo);

}
