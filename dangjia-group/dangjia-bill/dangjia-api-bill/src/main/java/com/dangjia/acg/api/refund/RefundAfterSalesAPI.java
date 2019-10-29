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
    @ApiOperation(value = "查询需退款的订单", notes = "查询需退款的订单")
    ServerResponse<PageInfo> queryRefundOnlyOrderList(@RequestParam("pageDTO") PageDTO pageDTO,
                                                      @RequestParam("userToken") String userToken,
                                                      @RequestParam("cityId") String cityId,
                                                      @RequestParam("houseId") String houseId,
                                                      @RequestParam("searchKey") String searchKey);

    /**
     * 仅退款提交
     * @param userToken  用户token
     * @param cityId 城市ID
     * @param houseId 房屋ID
     * @param orderProductAttr  需退款商品列表
     * @return
     */
    @PostMapping("/app/refund/refundOrder/saveRefundonlyInfo")
    @ApiOperation(value = "查询需退款的订单", notes = "查询需退款的订单")
    ServerResponse saveRefundonlyInfo(@RequestParam("userToken") String userToken,
                                        @RequestParam("cityId") String cityId,
                                        @RequestParam("houseId") String houseId,
                                        @RequestParam("orderProductAttr") String orderProductAttr);


}
