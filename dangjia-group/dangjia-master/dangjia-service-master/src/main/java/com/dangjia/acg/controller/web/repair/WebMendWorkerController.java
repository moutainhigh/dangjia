package com.dangjia.acg.controller.web.repair;

import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.api.web.repair.WebMendWorkerAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.storefront.StorefrontDTO;
import com.dangjia.acg.modle.storefront.Storefront;
import com.dangjia.acg.service.repair.MendWorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * author: Ronalcheng
 * Date: 2018/12/11 0011
 * Time: 11:41
 */
@RestController
public class WebMendWorkerController implements WebMendWorkerAPI {

    @Autowired
    private MendWorkerService mendWorkerService;

    @Autowired
    private RedisClient redisClient;
    /**
     * 房子id查询退人工
     */
    @Override
    @ApiMethod
    public ServerResponse workerBackState(HttpServletRequest request,String houseId, PageDTO pageDTO, String beginDate, String endDate,String state, String likeAddress) {
        String userID = request.getParameter(Constants.USERID);
        //通过缓存查询店铺信息
        StorefrontDTO storefront =redisClient.getCache(Constants.FENGJIAN_STOREFRONT+userID, StorefrontDTO.class);
        return mendWorkerService.workerBackState(storefront.getId(),houseId, pageDTO, beginDate, endDate, state,likeAddress);
    }

    @Override
    @ApiMethod
    public ServerResponse mendWorkerList(String mendOrderId) {
        return mendWorkerService.mendWorkerList(mendOrderId);
    }

    @Override
    @ApiMethod
    public ServerResponse workerOrderState(HttpServletRequest request, String houseId, PageDTO pageDTO, String beginDate, String endDate, String state,String likeAddress) {
        String userID = request.getParameter(Constants.USERID);
        //通过缓存查询店铺信息
        StorefrontDTO storefront =redisClient.getCache(Constants.FENGJIAN_STOREFRONT+userID,StorefrontDTO.class);
        return mendWorkerService.workerOrderState(storefront.getId(),houseId, pageDTO, beginDate, endDate,state, likeAddress);
    }
}
