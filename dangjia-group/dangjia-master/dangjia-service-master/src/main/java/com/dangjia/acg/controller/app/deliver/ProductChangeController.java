package com.dangjia.acg.controller.app.deliver;

import com.dangjia.acg.api.app.deliver.ProductChangeAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.deliver.ProductChangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * author: Yinjianbo
 * Date: 2019-5-11
 * 商品换货Controller
 */
@RestController
public class ProductChangeController implements ProductChangeAPI {

    @Autowired
    private ProductChangeService productChangeService;


    /**
     * 添加更换商品
     * @param request
     * @param userToken
     * @param houseId
     * @param srcProductId
     * @param destProductId
     * @param srcSurCount
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse insertProductChange(HttpServletRequest request, String userToken, String houseId, String srcProductId, String destProductId, Double srcSurCount) {
        return productChangeService.insertProductChange(request, userToken, houseId, srcProductId, destProductId, srcSurCount);
    }

    /**
     * 根据houseId查询更换商品列表
     * @param request
     * @param userToken
     * @param houseId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse queryChangeByHouseId(HttpServletRequest request, String userToken, String houseId) {
        return productChangeService.queryChangeByHouseId(request, userToken, houseId);
    }

    /**
     * 申请换货
     * @param request
     * @param request
     * @param houseId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse applyProductChange(HttpServletRequest request,String houseId) {
        return productChangeService.applyProductChange(request, houseId);
    }

    /**
     * 设置商品更换数
     * @param request
     * @param id
     * @param destSurCount
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse setDestSurCount(HttpServletRequest request, String id, Double destSurCount) {
        return productChangeService.setDestSurCount(request, id, destSurCount);
    }


    /**
     * 根据houseId查询更换商品订单
     * @param request
     * @param houseId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse queryOrderByHouseId(HttpServletRequest request, String houseId) {
        return productChangeService.queryOrderByHouseId(houseId);
    }

    /**
     * 补退差价回调
     * @param request
     * @param id
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse orderBackFun(HttpServletRequest request, String id) {
        return productChangeService.orderBackFun(request, id);
    }
}
