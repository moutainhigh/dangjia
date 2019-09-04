package com.dangjia.acg.api.actuary;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * author: Ronalcheng
 * Date: 2018/11/15 0015
 * Time: 19:18
 * APP直接调用
 */
@Api(description = "精算操作")
@FeignClient("dangjia-service-goods")
public interface ActuaryOperationAPI {

    /**
     * 选择取消精算
     */
    @PostMapping("/actuary/actuaryOperation/choiceGoods")
    @ApiOperation(value = "选择取消精算", notes = "选择取消精算")
    ServerResponse choiceGoods(@RequestParam("request") HttpServletRequest request,
                               @RequestParam("budgetIdList") String budgetIdList);

    /**
     * 更换货品
     */
    @PostMapping("/actuary/actuaryOperation/changeProduct")
    @ApiOperation(value = "更换货品", notes = "更换货品")
    ServerResponse changeProduct(@RequestParam("request") HttpServletRequest request,
                                 @RequestParam("productId") String productId,
                                 @RequestParam("budgetMaterialId") String budgetMaterialId,
                                 @RequestParam("srcGroupId") String srcGroupId,
                                 @RequestParam("targetGroupId") String targetGroupId,
                                 @RequestParam("houseId") String houseId,
                                 @RequestParam("workerTypeId") String workerTypeId);
    /**
     * 恢复精算货品
     */
    @PostMapping("/actuary/actuaryOperation/recoveryProduct")
    @ApiOperation(value = "恢复精算货品", notes = "恢复精算货品")
    ServerResponse recoveryProduct(@RequestParam("request") HttpServletRequest request,
                                 @RequestParam("houseId") String houseId,
                                 @RequestParam("workerTypeId") String workerTypeId);
    /**
     * 选择货品
     */
    @PostMapping("/actuary/actuaryOperation/selectProduct")
    @ApiOperation(value = "选择货品", notes = "选择货品")
    ServerResponse selectProduct(@RequestParam("request") HttpServletRequest request,
                                 @RequestParam("goodsId") String goodsId,
                                 @RequestParam("selectVal") String selectVal,
                                 @RequestParam("attributeIdArr") String attributeIdArr,
                                 @RequestParam("budgetMaterialId") String budgetMaterialId);

    /**
     * 精算商品详情
     * gId="+bm.getGoodsId()+"&cityId="+cityId+"&type="+3+"&title=服务商品详情"
     */
    @PostMapping("/actuary/actuaryOperation/getCommo")
    @ApiOperation(value = "精算商品详情", notes = "精算商品详情")
    ServerResponse getCommo(@RequestParam("request") HttpServletRequest request,
                            @RequestParam("gId") String gId,
                            @RequestParam("cityId") String cityId,
                            @RequestParam("type") int type);

    /**
     * 商品详情
     * gId:  WorkerGoodsId   ProductId
     */
    @PostMapping("/actuary/actuaryOperation/goodsDetail")
    @ApiOperation(value = "普通-商品详情", notes = "普通-商品详情")
    ServerResponse getGoodsDetail(@RequestParam("request") HttpServletRequest request,
                                  @RequestParam("gId") String gId,
                                  @RequestParam("type") int type);

    /**
     * 工序明细
     * 支付时精算goods详情 waitingPayDetail 共用此方法
     */
    @PostMapping("/actuary/actuaryOperation/confirmActuaryDetail")
    @ApiOperation(value = "工序明细", notes = "工序明细")
    ServerResponse confirmActuaryDetail(@RequestParam("request") HttpServletRequest request,
                                        @RequestParam("userToken") String userToken,
                                        @RequestParam("houseId") String houseId,
                                        @RequestParam("workerTypeId") String workerTypeId,
                                        @RequestParam("type") int type,
                                        @RequestParam("cityId") String cityId);

    /**
     * 精算详情
     */
    @PostMapping("/actuary/actuaryOperation/confirmActuary")
    @ApiOperation(value = "精算详情", notes = "精算详情")
    ServerResponse confirmActuary(@RequestParam("request") HttpServletRequest request,
                                  @RequestParam("userToken") String userToken,
                                  @RequestParam("houseId") String houseId,
                                  @RequestParam("cityId") String cityId);

}
