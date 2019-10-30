package com.dangjia.acg.controller.web.repair;

import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.api.web.repair.WebMendMaterielAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.storefront.Storefront;
import com.dangjia.acg.service.repair.MendMaterielService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * author: Ronalcheng
 * Date: 2018/12/11 0011
 * Time: 9:40
 */
@RestController
public class WebMendMaterielController implements WebMendMaterielAPI {
    @Autowired
    private MendMaterielService mendMaterielService;

    @Autowired
    private RedisClient redisClient;
    /**
     * 房子id查询业主退货单列表
     */
    @Override
    @ApiMethod
    public ServerResponse landlordState(HttpServletRequest request,String houseId, PageDTO pageDTO, String beginDate, String endDate, String likeAddress) {
        String userId = request.getParameter("userId");
        //通过缓存查询店铺信息
        return mendMaterielService.landlordState(userId,houseId, pageDTO, beginDate, endDate, likeAddress);
    }

    /**
     * 房子id查询退货单列表
     */
    @Override
    @ApiMethod
    public ServerResponse materialBackState(HttpServletRequest request,String houseId, PageDTO pageDTO, String beginDate, String endDate, String likeAddress) {
        String userId = request.getParameter("userId");
        return mendMaterielService.materialBackState(userId,houseId, pageDTO, beginDate, endDate, likeAddress);
    }

    /**
     * 根据mendOrderId查明细
     */
    @Override
    @ApiMethod
    public ServerResponse mendMaterialList(String mendOrderId,String userId) {
        return mendMaterielService.mendMaterialList(mendOrderId,userId);
    }

    /**
     * 房子id查询补货单列表
     */
    @Override
    @ApiMethod
    public ServerResponse materialOrderState(HttpServletRequest request, String houseId, PageDTO pageDTO, String beginDate, String endDate, String likeAddress) {
        String userID = request.getParameter(Constants.USERID);
        //通过缓存查询店铺信息
        Storefront storefront =redisClient.getCache(Constants.FENGJIAN_STOREFRONT+userID,Storefront.class);
        return mendMaterielService.materialOrderState(storefront.getId(),houseId, pageDTO, beginDate, endDate, likeAddress);
    }
}
