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
     * state:0待处理
     * @param request
     * @param cityId
     * @param pageDTO
     * @param state
     * @param likeAddress
     * @return
     */
    @PostMapping(value = "web/repair/webMendMateriel/landlordState")
    @ApiOperation(value = "业主仅退款(待处理)", notes = "业主仅退款(待处理)")
    ServerResponse landlordState(@RequestParam("request") HttpServletRequest request,
                                 @RequestParam("userId") String userId,
                                 @RequestParam("cityId") String cityId,
                                 @RequestParam("pageDTO") PageDTO pageDTO,
                                 @RequestParam("state") String state,
                                 @RequestParam("likeAddress") String likeAddress);
    /**
     * state:2 已经处理
     * @param request
     * @param cityId
     * @param pageDTO
     * @param state
     * @param likeAddress
     * @return
     */
    @PostMapping(value = "web/repair/webMendMateriel/landlordStateHandle")
    @ApiOperation(value = "业主仅退款(已经处理)", notes = "业主仅退款(已经处理)")
    ServerResponse landlordStateHandle(@RequestParam("request") HttpServletRequest request,
                                 @RequestParam("cityId") String cityId,
                                 @RequestParam("pageDTO") PageDTO pageDTO,
                                 @RequestParam("state") String state,
                                 @RequestParam("likeAddress") String likeAddress);




    /**
     * auther:chenyufeng
     * date:2019.11.01
     * @param request
     * @param pageDTO
     * @param state 状态：（0生成中,1处理中,2不通过取消,3已通过,4已全部结算,5已撤回,5已关闭）
     * @param likeAddress 模糊查询参数
     * @return
     */
    @PostMapping(value = "web/repair/webMendMateriel/materialBackStateProcessing")
    @ApiOperation(value = "工匠申请退货（待审核和已经审核待处理）", notes = "工匠申请退货（待审核和已经审核待处理）")
    ServerResponse materialBackStateProcessing(
            @RequestParam("request") HttpServletRequest request,
            @RequestParam("userId") String userId,
            @RequestParam("cityId") String cityId,
            @RequestParam("pageDTO") PageDTO pageDTO,
            @RequestParam("state") String state,
            @RequestParam("likeAddress") String likeAddress);

    /**
     * auther:chenyufeng
     * date:2019.11.01
     * @param request
     * @param pageDTO
     * @param state 状态：（0生成中,1处理中,2不通过取消,3已通过,4已全部结算,5已撤回,5已关闭 7已审核待处理）
     * @param likeAddress 模糊查询参数
     * @return
     */
    @PostMapping(value = "web/repair/webMendMateriel/materialBackStateHandle")
    @ApiOperation(value = "店铺管理—售后管理—工匠退货(已结束)", notes = "店铺管理—售后管理—工匠退货(已结束)")
    ServerResponse materialBackStateHandle(
            @RequestParam("request") HttpServletRequest request,
            @RequestParam("userId") String userId,
            @RequestParam("cityId") String cityId,
            @RequestParam("pageDTO") PageDTO pageDTO,
            @RequestParam("state") String state,
            @RequestParam("likeAddress") String likeAddress);


    /**
     *
     * @param request
     * @param cityId
     * @param userId
     * @param pageDTO
     * @param state
     * @param likeAddress
     * @return
     */
    @PostMapping(value = "web/repair/webMendMateriel/ownerReturnHandleIng")
    @ApiOperation(value = "店铺管理—售后管理—业主退货退款(待审核)", notes = "店铺管理—售后管理—工匠退货(待审核)")
    ServerResponse ownerReturnHandleIng(
            @RequestParam("request") HttpServletRequest request,
            @RequestParam("cityId") String cityId,
            @RequestParam("userId") String userId,
            @RequestParam("pageDTO") PageDTO pageDTO,
            @RequestParam("state") String state,
            @RequestParam("likeAddress") String likeAddress);
    /**
     *
     * @param request
     * @param cityId
     * @param userId
     * @param pageDTO
     * @param state
     * @param likeAddress
     * @return
     */
    @PostMapping(value = "web/repair/webMendMateriel/ownerReturnProssing")
    @ApiOperation(value = "店铺管理—售后管理—业主退货退款(已经审核待处理)", notes = "店铺管理—售后管理—工匠退货(已经审核待处理)")
    ServerResponse ownerReturnProssing(
            @RequestParam("request") HttpServletRequest request,
            @RequestParam("cityId") String cityId,
            @RequestParam("userId") String userId,
            @RequestParam("pageDTO") PageDTO pageDTO,
            @RequestParam("state") String state,
            @RequestParam("likeAddress") String likeAddress);
    /**
     *
     * @param request
     * @param cityId
     * @param userId
     * @param pageDTO
     * @param state
     * @param likeAddress
     * @return
     */
    @PostMapping(value = "web/repair/webMendMateriel/ownerReturnHandle")
    @ApiOperation(value = "店铺管理—售后管理—业主退货退款(已结束)", notes = "店铺管理—售后管理—工匠退货(已结束)")
    ServerResponse ownerReturnHandle(
            @RequestParam("request") HttpServletRequest request,
            @RequestParam("cityId") String cityId,
            @RequestParam("userId") String userId,
            @RequestParam("pageDTO") PageDTO pageDTO,
            @RequestParam("state") String state,
            @RequestParam("likeAddress") String likeAddress);

    /**
     * 确定退货
     * @param mendOrderId
     * @param userId
     * @return
     */
    @PostMapping(value = "web/repair/webMendMateriel/confirmReturnMendMaterial")
    @ApiOperation(value = "确定退货", notes = "确定退货")
    ServerResponse confirmReturnMendMaterial(@RequestParam("mendOrderId") String mendOrderId,
                                             @RequestParam("userId") String userId,
                                             @RequestParam("type") Integer type,
                                             @RequestParam("actualCountList") String actualCountList,
                                             @RequestParam("returnReason") String returnReason,
                                             @RequestParam("supplierId") String supplierId);



    /**
     *  date:2019.11.23
     * author:chenyufeng
     * 新版查看补退单明细
     * @param mendOrderId
     * @param userId
     * @return
     */
    @PostMapping(value = "web/repair/webMendMateriel/queryMendMaterialList")
    @ApiOperation(value = "售后管理-补退单查明细(退货详情)", notes = "售后管理-补退单查明细(退货详情)")
    ServerResponse queryMendMaterialList(@RequestParam("mendOrderId") String mendOrderId, @RequestParam("userId") String userId);

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


    /**
     * 業主審核部分退貨-申请平台介入
     * @param request
     * @param cityId
     * @param houseId
     * @param pageDTO
     * @return
     */
    @PostMapping(value = "web/repair/webMendMateriel/applyPlatformAccess")
    @ApiOperation(value = "業主審核部分退貨-申请平台介入", notes = "業主審核部分退貨-申请平台介入")
    ServerResponse applyPlatformAccess(
            @RequestParam("request") HttpServletRequest request,
            @RequestParam("cityId") String cityId,
            @RequestParam("houseId") String houseId,
            @RequestParam("pageDTO") PageDTO pageDTO);


    /**
     * 业主申请部分退货-接受
     * @param request
     * @param cityId
     * @param houseId
     * @param pageDTO
     * @return
     */
    @PostMapping(value = "web/repair/webMendMateriel/acceptPartialReturn")
    @ApiOperation(value = "业主申请部分退货-接受", notes = "业主申请部分退货-接受")
    ServerResponse acceptPartialReturn(
            @RequestParam("request") HttpServletRequest request,
            @RequestParam("cityId") String cityId,
            @RequestParam("houseId") String houseId,
            @RequestParam("pageDTO") PageDTO pageDTO);




    @PostMapping(value = "web/repair/webMendMateriel/storeReturnDistributionSupplier")
    @ApiOperation(value = "店铺退货分发供应商列表", notes = "店铺退货分发供应商列表")
    ServerResponse storeReturnDistributionSupplier(
            @RequestParam("request") HttpServletRequest request,
            @RequestParam("cityId") String cityId,
            @RequestParam("userId") String userId,
            @RequestParam("pageDTO") PageDTO pageDTO,
            @RequestParam("likeAddress") String likeAddress);

    /**
     * 店铺退货分发供应商
     * @param mendOrderId
     * @param userId
     * @return
     */
    @PostMapping(value = "web/repair/webMendMateriel/returnProductDistributionSupplier")
    @ApiOperation(value = "店铺退货分发供应商", notes = "店铺退货分发供应商")
    ServerResponse returnProductDistributionSupplier(@RequestParam("mendOrderId") String mendOrderId,
                                                     @RequestParam("userId") String userId,
                                                     @RequestParam("actualCountList") String actualCountList);


}
