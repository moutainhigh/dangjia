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
    @PostMapping("/web/shellOrder/queryOrderInfoList")
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
    @PostMapping("/web/shellOrder/queryOrderInfoDetail")
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
    @PostMapping("/web/shellOrder/updateOrderInfo")
    @ApiOperation(value = "修改订单状态", notes = "修改订单状态")
    ServerResponse updateOrderInfo(@RequestParam("request") HttpServletRequest request,
                                   @RequestParam("homeOrderId") String homeOrderId,
                                   @RequestParam("status") Integer status);


    /**
     * 当家贝商城--商品兑换提交
     * @param userToken 用户token
     * @param addressId 地址ID
     * @param productSpecId 商品规格ID
     * @param exchangeNum 商品数量
     * @param userRole 提交服务端：1为业主应用，2为工匠应用，3为销售应用
     * @return
     */
    @PostMapping("/app/shellOrder/saveConvertedCommodities")
    @ApiOperation(value = "商品兑换提交", notes = "商品兑换提交")
    ServerResponse saveConvertedCommodities(@RequestParam("userToken") String userToken,
                                   @RequestParam("addressId") String addressId,
                                   @RequestParam("productSpecId") String productSpecId,
                                   @RequestParam("exchangeNum") Integer exchangeNum,
                                   @RequestParam("userRole") Integer userRole,
                                   @RequestParam("cityId") String cityId);
    /**
     * 当家贝商城--兑换记录
     * @param userToken
     * @return
     */
    @PostMapping("/app/homeShell/searchConvertedProductList")
    @ApiOperation(value = "当家贝商城--兑换记录", notes = "当家贝商城--兑换记录")
    ServerResponse searchShellProductInfo(@RequestParam("userToken") String userToken,
                                          @RequestParam("pageDTO") PageDTO pageDTO);

    /**
     * 当家贝商城--当家贝明细
     * @param userToken
     * @return
     */
    @PostMapping("/app/homeShell/searchShellMoneyList")
    @ApiOperation(value = "当家贝商城--当家贝明细", notes = "当家贝商城--当家贝明细")
    ServerResponse searchShellMoneyList(@RequestParam("userToken") String userToken,
                                        @RequestParam("pageDTO") PageDTO pageDTO);

    /**
     * 当家贝商城--兑换详情
     * @param userToken
     * @return
     */
    @PostMapping("/app/homeShell/searchConvertedProductInfo")
    @ApiOperation(value = "当家贝商城--兑换详情", notes = "当家贝商城--兑换详情")
    ServerResponse searchConvertedProductInfo(@RequestParam("userToken") String userToken,
                                          @RequestParam("shellOrderId") String shellOrderId);

    /**
     * 当家贝商城--确认收货/撤销退款
     * @param userToken
     * @param shellOrderId 兑换记录ID
     * @param type 类型：1确认收货，2撤销退款
     * @return
     */
    @PostMapping("/app/shellOrder/updateConvertedProductInfo")
    @ApiOperation(value = "当家贝商城--确认收货/撤销退款", notes = "当家贝商城--确认收货/撤销退款")
    ServerResponse updateConvertedProductInfo(@RequestParam("userToken") String userToken,
                                              @RequestParam("shellOrderId") String shellOrderId,
                                              @RequestParam("type") Integer type);

    /**
     * 当家贝商城--取消订单/申请退款
     * @param userToken
     * @param shellOrderId 兑换记录ID
     * @param image 相关凭证
     * @param type 申请类型：1取消订单，2申请退款
     * @return
     */
    @PostMapping("/app/shellOrder/refundConvertedProductInfo")
    @ApiOperation(value = "当家贝商城--取消订单/申请退款", notes = "当家贝商城--取消订单/申请退款")
    ServerResponse refundConvertedProductInfo(@RequestParam("userToken") String userToken,
                                              @RequestParam("shellOrderId") String shellOrderId,
                                              @RequestParam("image") String image,
                                              @RequestParam("type") Integer type);



}
