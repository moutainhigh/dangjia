package com.dangjia.acg.api.actuary.app;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * author: qiyuxiang
 * Date: 2019/09/18
 */
@Api(description = "APP工序商品操作类")
@FeignClient("dangjia-service-goods")
public interface AppActuaryOperationAPI {

    /**
     * 选择取消精算
     */
    @PostMapping("/app/actuary/actuaryOperation/choiceGoods")
    @ApiOperation(value = "选择取消精算", notes = "选择取消精算")
    ServerResponse choiceGoods(@RequestParam("request") HttpServletRequest request,
                               @RequestParam("houseId") String houseId, @RequestParam("productId") String productId);

    /**
     * 更换货品
     */
    @PostMapping("/app/actuary/actuaryOperation/changeProduct")
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
    @PostMapping("/app/actuary/actuaryOperation/recoveryProduct")
    @ApiOperation(value = "恢复精算货品", notes = "恢复精算货品")
    ServerResponse recoveryProduct(@RequestParam("request") HttpServletRequest request,
                                   @RequestParam("houseId") String houseId,
                                   @RequestParam("workerTypeId") String workerTypeId);
    /**
     * 选择货品
     */
    @PostMapping("/app/actuary/actuaryOperation/selectProduct")
    @ApiOperation(value = "选择货品", notes = "选择货品")
    ServerResponse selectProduct(@RequestParam("request") HttpServletRequest request,
                                 @RequestParam("goodsId") String goodsId,
                                 @RequestParam("selectVal") String selectVal,
                                 @RequestParam("budgetMaterialId") String budgetMaterialId);

    @PostMapping("/app/actuary/actuaryOperation/getCommo")
    @ApiOperation(value = "商品详情", notes = "商品详情")
    ServerResponse getCommo(@RequestParam("request") HttpServletRequest request,
                            @RequestParam("gId") String gId,
                            @RequestParam("cityId") String cityId);



}
