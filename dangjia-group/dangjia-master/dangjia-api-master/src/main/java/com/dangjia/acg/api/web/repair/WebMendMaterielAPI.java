package com.dangjia.acg.api.web.repair;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * author: Ronalcheng
 * Date: 2018/12/11 0011
 * Time: 9:38
 * web端包括普通工匠端 补退货
 */
@FeignClient("dangjia-service-master")
@Api(value = "Web端补退货", description = "Web端补退货")
public interface WebMendMaterielAPI {

    /**
     *店铺--售后处理--待处理列表(退货退款/仅退款）
     * @param request
     * @param cityId 城市ID
     * @param userId 用户ID
     * @param pageDTO
     * @param state 状态默认：1待处理，2已处理
     * @param likeAddress
     * @param type 查询类型：1退货退款，2仅退款
     * @return
     */
    @PostMapping(value = "web/repair/webMendMateriel/searchReturnRrefundList")
    @ApiOperation(value = "店铺--售后处理--待处理列表", notes = "店铺--售后处理--待处理列表")
    ServerResponse searchReturnRrefundList(
            @RequestParam("request") HttpServletRequest request,
            @RequestParam("cityId") String cityId,
            @RequestParam("userId") String userId,
            @RequestParam("pageDTO") PageDTO pageDTO,
            @RequestParam("state") Integer state,
            @RequestParam("likeAddress") String likeAddress,
            @RequestParam("type") Integer type);



    /**
     *店铺--售后处理--分配供应商、已结束列表
     * @param request
     * @param cityId 城市ID
     * @param userId 用户ID
     * @param pageDTO
     * @param state 状态默认：1.已分发供应商 2.已结束
     * @param likeAddress
     * @return
     */
    @PostMapping(value = "web/repair/webMendMateriel/searchReturnRefundSplitList")
    @ApiOperation(value = "店铺--售后处理--分配供应商、已结束列表", notes = "店铺--售后处理--分配供应商、已结束列表")
    ServerResponse searchReturnRefundSplitList(
            @RequestParam("request") HttpServletRequest request,
            @RequestParam("cityId") String cityId,
            @RequestParam("userId") String userId,
            @RequestParam("pageDTO") PageDTO pageDTO,
            @RequestParam("state") Integer state,
            @RequestParam("likeAddress") String likeAddress);

    /**
     * 售后管理--退货退款--分发供应商列表
     * @param request
     * @param cityId
     * @param userId
     * @param mendOrderId 退货申请单ID
     * @return
     */
    @PostMapping(value = "web/repair/webMendMateriel/searchReturnRefundMaterielList")
    @ApiOperation(value = "售后管理--退货退款--分发供应商列表", notes = "售后管理--退货退款--分发供应商列表")
    ServerResponse searchReturnRefundMaterielList(
            @RequestParam("request") HttpServletRequest request,
            @RequestParam("cityId") String cityId,
            @RequestParam("userId") String userId,
            @RequestParam("mendOrderId") String mendOrderId);

    /**
     * 退货退款--分发供应商--保存分发
     * @param mendOrderId
     * @param userId
     * @return
     */
    @PostMapping(value = "web/repair/webMendMateriel/saveReturnRefundMaterielSup")
    @ApiOperation(value = "退货退款--分发供应商--保存分发", notes = "退货退款--分发供应商--保存分发")
    ServerResponse saveReturnRefundMaterielSup(@RequestParam("mendOrderId") String mendOrderId,
                                                 @RequestParam("userId") String userId,
                                                 @RequestParam("cityId") String cityId,
                                                 @RequestParam("materielSupList") String materielSupList);

    /**
     * 退货退款--分发供应商--生成退货单
     * @param mendOrderId 退货申请单ID
     * @param userId 用户ID
     * @param cityId 城市ID
     * @return
     */
    @PostMapping(value = "web/repair/webMendMateriel/generateMendDeliverorder")
    @ApiOperation(value = "退货退款--分发供应商--生成退货单", notes = "退货退款--分发供应商--生成退货单")
    ServerResponse generateMendDeliverorder(@RequestParam("mendOrderId") String mendOrderId,
                                               @RequestParam("userId") String userId,
                                               @RequestParam("cityId") String cityId);


    /**
     *  退货退款—退货详情列表
     * @param mendDeliverId 退货单ID
     * @return
     */
    @PostMapping(value = "web/repair/webMendMateriel/queryMendMaterialList")
    @ApiOperation(value = "退货退款—退货详情列表", notes = "退货退款—退货详情列表")
    ServerResponse queryMendMaterialList(@RequestParam("mendDeliverId") String mendDeliverId);


    /**
     * 退货退款—确认退货/部分退货
     * @param mendDeliverId 退货单ID
     * @param userId 用户id
     * @param type 类型：1确认退货，2部分退货
     * @param mendMaterialList 退货详情列表 [{“mendMaterielId”:”退货明细ID”,”actualCount”:9（实退货量）}]
     * @param partialReturnReason 部分退货原因
     * @return
     */
    @PostMapping(value = "web/repair/webMendMateriel/confirmReturnMendMaterial")
    @ApiOperation(value = "退货退款—确认退货/部分退货", notes = "退货退款—确认退货/部分退货")
    ServerResponse confirmReturnMendMaterial(@RequestParam("mendDeliverId") String mendDeliverId,
                                             @RequestParam("userId") String userId,
                                             @RequestParam("type") Integer type,
                                             @RequestParam("mendMaterialList") String mendMaterialList,
                                             @RequestParam("partialReturnReason") String partialReturnReason);


    /**
     * 售后管理--仅退款--退货单详情列表
     * @param cityId
     * @param userId
     * @param mendOrderId 退货申请单ID
     * @return
     */
    @PostMapping(value = "web/repair/webMendMateriel/searchRefundMaterielList")
    @ApiOperation(value = "售后管理--仅退款--退货单详情列表", notes = "售后管理--仅退款--退货单详情列表")
    ServerResponse searchRefundMaterielList(
            @RequestParam("cityId") String cityId,
            @RequestParam("userId") String userId,
            @RequestParam("mendOrderId") String mendOrderId);



    /**
     * 售后管理--仅退款--确认退款
     * @param cityId
     * @param userId
     * @param mendOrderId 退货申请单ID
     * @return
     */
    @PostMapping(value = "web/repair/webMendMateriel/saveRefundMaterielInfo")
    @ApiOperation(value = "售后管理--仅退款--确认退款", notes = "售后管理--仅退款--确认退款")
    ServerResponse saveRefundMaterielInfo(
            @RequestParam("cityId") String cityId,
            @RequestParam("userId") String userId,
            @RequestParam("mendOrderId") String mendOrderId);












    /**
     * 查看补退单明细
     * @param mendOrderId
     * @param userId
     * @return
     */
    @PostMapping(value = "web/repair/webMendMateriel/mendMaterialList")
    @ApiOperation(value = "补退单查明细", notes = "补退单查明细")
    ServerResponse mendMaterialList(@RequestParam("mendOrderId") String mendOrderId, @RequestParam("userId") String userId);

    @PostMapping(value = "web/repair/webMendMateriel/materialOrderState")
    @ApiOperation(value = "房子id查询补货单列表", notes = "房子id查询补货单列表")
    ServerResponse materialOrderState(
            @RequestParam("request") HttpServletRequest request,
            @RequestParam("houseId") String houseId,
            @RequestParam("userId") String userId,
            @RequestParam("cityId") String cityId,
            @RequestParam("pageDTO") PageDTO pageDTO,
            @RequestParam("beginDate") String beginDate,
            @RequestParam("endDate") String endDate,
            @RequestParam("state") String state,
            @RequestParam("likeAddress") String likeAddress);

    /**
     *
     * @param request
     * @param pageDTO
     * @param state 状态：（0生成中,1处理中,2不通过取消,3已通过,4已全部结算,5已撤回,5已关闭）
     * @param likeAddress 模糊查询参数
     * @return
     */
    @PostMapping(value = "web/repair/webMendMateriel/materialBackState")
    @ApiOperation(value = "房子id查询退货单列表", notes = "房子id查询退货单列表")
    ServerResponse materialBackState(
            @RequestParam("request") HttpServletRequest request,
            @RequestParam("cityId") String cityId,
            @RequestParam("userId") String userId,
            @RequestParam("pageDTO") PageDTO pageDTO,
            @RequestParam("state") String state,
            @RequestParam("likeAddress") String likeAddress);









    @PostMapping(value = "app/repair/webMendMateriel/querySurplusMaterial")
    @ApiOperation(value = "业主清点剩余材料", notes = "业主清点剩余材料")
    ServerResponse querySurplusMaterial(@RequestParam("data") String data);

    /**
     * 消息弹窗--业主审核部分退货列表
     * @param taskId 任务ID
     * @return
     */
    @PostMapping(value = "app/repair/webMendMateriel/queryTrialRetreatMaterial")
    @ApiOperation(value = "消息弹窗--业主审核部分退货列表", notes = "消息弹窗--业主审核部分退货列表")
    ServerResponse queryTrialRetreatMaterial(@RequestParam("taskId") String taskId);

    @PostMapping(value = "app/repair/webMendMateriel/addPlatformComplain")
    @ApiOperation(value = "业主申请平台介入", notes = "业主申请平台介入")
    ServerResponse addPlatformComplain(@RequestParam("userToken") String userToken,
                                       @RequestParam("mendOrderId") String mendOrderId,
                                       @RequestParam("description") String description);

}
