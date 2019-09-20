package com.dangjia.acg.api.app.product;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.product.BrowseRecord;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author ChenYufeng
 * @Description 用户浏览商品记录表
 * @Date 2019/9/15
 * @Time 上午9:51
 * @Version V2.0.0
 */
@FeignClient("dangjia-service-master")
@Api(value = "购物车-猜你喜欢（用户浏览商品记录表）", description = "购物车-猜你喜欢（用户浏览商品记录表）")
public interface BrowseRecordAPI {

    @RequestMapping(value = "app/product/queryBrowseRecord", method = RequestMethod.POST)
    @ApiOperation(value = "查询用户浏览商品记录表(固定取12条)", notes = "查询用户浏览商品记录表(固定取12条)")
    ServerResponse queryBrowseRecord(@RequestParam("request") HttpServletRequest request,
                                     @RequestParam("userToken") String userToken);

    @RequestMapping(value = "app/product/addBrowseRecord", method = RequestMethod.POST)
    @ApiOperation(value = "新增用户浏览商品记录表", notes = "新增用户浏览商品记录表")
    ServerResponse addBrowseRecord(@RequestParam("request") HttpServletRequest request,
                                   @RequestParam("userToken") String userToken,
                                   @RequestParam("productId") String productId, @RequestParam("productId") String visitsNum);

    /**
     * 我的收藏：商品调用新版方案，工地用以前老的方法
     * 购物车猜你喜欢：调用新方案
     * 商品详情页调用同级别货品下面的商品的随机12个
     */

}
