package com.dangjia.acg.controller.web.repair;

import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.api.web.repair.WebMendMaterielAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.storefront.StorefrontDTO;
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
    public ServerResponse landlordState(HttpServletRequest request,String cityId,String houseId, PageDTO pageDTO, String beginDate, String endDate, String state,String likeAddress) {
        String userId = request.getParameter("userId");
        //通过缓存查询店铺信息
        return mendMaterielService.landlordState(userId,cityId,houseId, pageDTO, beginDate, endDate,state, likeAddress);
    }



    /**
     * 房子id查询退货单列表
     */
    @Override
    @ApiMethod
    public ServerResponse materialBackState(HttpServletRequest request,String cityId,String houseId, PageDTO pageDTO, String beginDate, String endDate,String state, String likeAddress) {
        String userId = request.getParameter("userId");
        return mendMaterielService.materialBackState(userId,cityId,houseId, pageDTO, beginDate, endDate, state,likeAddress);
    }

    @Override
    @ApiMethod
    public ServerResponse materialBackStateProcessing(HttpServletRequest request,String userId, String cityId, PageDTO pageDTO, String state, String likeAddress) {
        return mendMaterielService.materialBackStateProcessing(userId,cityId, pageDTO, state,likeAddress);
    }



    /**
     *
     * @param request
     * @param cityId
     * @param pageDTO
     * @param state 状态：（0生成中,1处理中,2不通过取消,3已通过,4已全部结算,5已撤回,5已关闭）
     * @param likeAddress 模糊查询参数
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse materialBackStateHandle(HttpServletRequest request,String userId, String cityId, PageDTO pageDTO, String state, String likeAddress) {
        return mendMaterielService.materialBackStateHandle(request,userId,cityId,pageDTO,state,likeAddress);
    }

    @Override
    @ApiMethod
    public ServerResponse landlordStateHandle(HttpServletRequest request, String cityId, String houseId, PageDTO pageDTO, String beginDate, String endDate, String state, String likeAddress) {
        return mendMaterielService.landlordStateHandle(request,cityId,houseId,pageDTO,state,beginDate,endDate,likeAddress);
    }

    @Override
    @ApiMethod
    public ServerResponse ownerReturnHandleIng(HttpServletRequest request, String cityId, String userId, PageDTO pageDTO, String state, String likeAddress) {
        return mendMaterielService.ownerReturnHandleIng(request,cityId,userId,pageDTO,state,likeAddress);
    }

    @Override
    @ApiMethod
    public ServerResponse ownerReturnProssing(HttpServletRequest request, String cityId, String userId, PageDTO pageDTO, String state, String likeAddress) {
        return mendMaterielService.ownerReturnProssing(request,cityId,userId,pageDTO,state,likeAddress);
    }

    @Override
    @ApiMethod
    public ServerResponse ownerReturnHandle(HttpServletRequest request, String cityId, String userId, PageDTO pageDTO, String state, String likeAddress) {
        return mendMaterielService.ownerReturnHandle(request,cityId,userId,pageDTO,state,likeAddress);
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
    public ServerResponse materialOrderState(HttpServletRequest request, String houseId, PageDTO pageDTO, String beginDate, String endDate, String state,String likeAddress) {
        String userID = request.getParameter(Constants.USERID);
        //通过缓存查询店铺信息
        StorefrontDTO storefront =redisClient.getCache(Constants.FENGJIAN_STOREFRONT+userID, StorefrontDTO.class);
        return mendMaterielService.materialOrderState(storefront.getId(),houseId, pageDTO, beginDate, endDate,state, likeAddress);
    }

    @Override
    @ApiMethod
    public ServerResponse landlordStateRefundList(HttpServletRequest request, String cityId, String houseId, PageDTO pageDTO, String beginDate, String endDate, String state, String likeAddress) {
        return null;
    }

    @Override
    @ApiMethod
    public ServerResponse landlordStateRefundAudit(HttpServletRequest request, String cityId, String houseId, PageDTO pageDTO, String beginDate, String endDate, String state, String likeAddress) {
        return null;
    }
}
