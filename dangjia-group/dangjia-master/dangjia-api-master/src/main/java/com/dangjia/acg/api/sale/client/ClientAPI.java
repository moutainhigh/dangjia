package com.dangjia.acg.api.sale.client;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.clue.Clue;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/7/20
 * Time: 9:45
 */
@FeignClient("dangjia-service-master")
@Api(value = "销售端客户页接口", description = "销售端客户页接口")
public interface ClientAPI {

    @PostMapping(value = "sale/client/enterCustomer")
    @ApiOperation(value = "录入客户", notes = "录入客户")
    ServerResponse enterCustomer(@RequestParam("request") HttpServletRequest request,
                                 @RequestParam("clue") Clue clue,
                                 @RequestParam("userToken") String userToken);

    @PostMapping(value = "sale/client/updateCustomer")
    @ApiOperation(value = "编辑客户", notes = "编辑客户")
    ServerResponse updateCustomer(@RequestParam("request") HttpServletRequest request,
                                  @RequestParam("clue") Clue clue);


    @PostMapping(value = "sale/client/clientPage")
    @ApiOperation(value = "客户页", notes = "客户页")
    ServerResponse  clientPage(@RequestParam("request") HttpServletRequest request,
                               @RequestParam("userToken") String userToken);

    /**
     * 跟进列表
     * @param request
     * @param label
     * @param time 排序 "desc":降序  不传升序
     * @param stage 0:新线索（线索阶段） 1：继续跟进（线索阶段）  4：转客服 （客户阶段）
     * @return
     */
    @PostMapping(value = "sale/client/followList")
    @ApiOperation(value = "跟进列表", notes = "跟进列表")
    ServerResponse followList(@RequestParam("request") HttpServletRequest request,
                              @RequestParam("userToken") String userToken,
                              @RequestParam("pageDTO") PageDTO pageDTO,
                              @RequestParam("label") String label,
                              @RequestParam("time") String time,
                              @RequestParam("stage") Integer stage,
                              @RequestParam("searchKey") String searchKey);


    /**
     *
     * @param request
     * @param userToken
     * @param visitState 1:已下单  3：已竣工
     * @param pageDTO
     * @param time
     * @param searchKey
     * @return
     */
    @PostMapping(value = "sale/client/ordersCustomer")
    @ApiOperation(value = "已下单竣工列表", notes = "已下单竣工列表")
    ServerResponse ordersCustomer(@RequestParam("request") HttpServletRequest request,
                                  @RequestParam("userToken") String userToken,
                                  @RequestParam("visitState") String visitState,
                                  @RequestParam("pageDTO") PageDTO pageDTO,
                                  @RequestParam("time") String time,
                                  @RequestParam("searchKey") String searchKey);
}
