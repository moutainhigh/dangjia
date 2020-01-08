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
     * @return
     */
    @PostMapping(value = "app/engineer/saveMaintenanceRecord")
    @ApiOperation(value = "保存质保记录（申请质保）", notes = "保存质保记录（申请质保）")
    ServerResponse saveMaintenanceRecord(@RequestParam("userToken") String userToken,
                                         @RequestParam("houseId") String houseId,
                                         @RequestParam("workerTypeSafeOrderId") String workerTypeSafeOrderId,
                                         @RequestParam("remark") String remark,
                                         @RequestParam("images") String images);

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

    @PostMapping(value = "web/engineer/upDateMaintenanceInFo")
    @ApiOperation(value = "处理申诉", notes = "处理申诉")
    ServerResponse upDateMaintenanceInFo(@RequestParam("supervisorId") String supervisorId,
                                         @RequestParam("stewardSubsidy") Integer stewardSubsidy,
                                         @RequestParam("serviceRemark") String serviceRemark,
                                         @RequestParam("userId") String userId,
                                         @RequestParam("id") String id,
                                         @RequestParam("handleType") Integer handleType);


    @PostMapping(value = "app/engineer/updateTaskStackData")
    @ApiOperation(value = "修改消息状态", notes = "修改消息状态")
    ServerResponse updateTaskStackData(@RequestParam("id") String id);


    @PostMapping(value = "app/engineer/queryDimensionRecord")
    @ApiOperation(value = "查询维保责任记录", notes = "查询维保责任记录")
    ServerResponse queryDimensionRecord(@RequestParam("memberId") String memberId);

    @PostMapping(value = "app/engineer/queryDimensionRecordInFo")
    @ApiOperation(value = "查询维保详情", notes = "查询维保详情")
    ServerResponse queryDimensionRecordInFo(@RequestParam("mrId") String mrId);


    @PostMapping(value = "app/engineer/insertResponsibleParty")
    @ApiOperation(value = "新增工匠申诉", notes = "新增工匠申诉")
    ServerResponse insertResponsibleParty(@RequestParam("responsiblePartyId") String responsiblePartyId,
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


//    @PostMapping(value = "app/engineer/applicationAcceptance")
//    @ApiOperation(value = "确认申请验收", notes = "确认申请验收")
//    ServerResponse applicationAcceptance( @RequestParam("houseId") String houseId);


    @PostMapping("/web/queryGuaranteeMoneyList")
    @ApiOperation(value = "店铺-缴纳质保金列表", notes = "质保金缴纳列表")
    ServerResponse queryGuaranteeMoneyList(@RequestParam("pageDTO") PageDTO pageDTO, @RequestParam("userId") String userId, @RequestParam("cityId") String cityId);

    @PostMapping("/web/queryGuaranteeMoneyDetail")
    @ApiOperation(value = "店铺-缴纳质保金详情", notes = "缴纳质保金详情")
    ServerResponse queryGuaranteeMoneyDetail(@RequestParam("userId") String userId, @RequestParam("cityId") String cityId, @RequestParam("id") String id);


    @PostMapping(value = "app/engineer/resolved")
    @ApiOperation(value = "已解决", notes = "已解决")
    ServerResponse resolved(@RequestParam("userToken") String userToken,
                            @RequestParam("remark") String remark,
                            @RequestParam("houseId") String houseId,
                            @RequestParam("image") String image,
                            @RequestParam("id") String id,
                            @RequestParam("workerTypeSafeOrderId") String workerTypeSafeOrderId
    );

    @PostMapping(value = "app/engineer/sendingOwners")
    @ApiOperation(value = "(自购金额确认)发送给业主", notes = "(自购金额确认)发送给业主")
    ServerResponse sendingOwners(@RequestParam("userToken") String userToken,
                                 @RequestParam("houseId") String houseId,
                                 @RequestParam("remark") String remark,
                                 @RequestParam("enoughAmount") String enoughAmount
    );

    //确定维保工序（已有）
    //选择责任方（已有）
    //确定责任占比（已有）
    @PostMapping(value = "app/auditMaintenance")
    @ApiOperation(value = "管家审核维修", notes = "管家审核维修")
    ServerResponse auditMaintenance(@RequestParam("userToken") String userToken,
                                    @RequestParam("remark") String remark,
                                    @RequestParam("houseId") String houseId,
                                    @RequestParam("image") String image,
                                    @RequestParam("id") String id,
                                    @RequestParam("state") Integer state,
                                    @RequestParam("workerTypeSafeOrderId") String workerTypeSafeOrderId
    );

    @PostMapping(value = "app/submitQualityAssurance")
    @ApiOperation(value = "提交质保处理", notes = "提交质保处理")
    ServerResponse submitQualityAssurance(@RequestParam("userToken") String userToken,
                                          @RequestParam("houseId") String houseId,
                                          @RequestParam("remark") String remark,
                                          @RequestParam("image") String image,
                                          @RequestParam("id") String id,
                                          @RequestParam("state") Integer state,
                                          @RequestParam("productId") String productId,
                                          @RequestParam("price") Double price,
                                          @RequestParam("shopCount") Double shopCount,
                                          @RequestParam("workerTypeSafeOrderId") String workerTypeSafeOrderId);

    @PostMapping(value = "app/engineer/insertMaintenanceRecordProduct")
    @ApiOperation(value = "添加维保商品到购物篮", notes = "添加维保商品到购物篮")
    ServerResponse insertMaintenanceRecordProduct(@RequestParam("userToken") String userToken,
                                                  @RequestParam("houseId") String houseId,
                                                  @RequestParam("maintenanceRecordId") String maintenanceRecordId,
                                                  @RequestParam("productId") String productId);

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
}
