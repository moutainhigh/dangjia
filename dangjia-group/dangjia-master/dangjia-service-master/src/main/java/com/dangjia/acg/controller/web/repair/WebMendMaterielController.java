package com.dangjia.acg.controller.web.repair;

import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.api.web.repair.WebMendMaterielAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
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
    public ServerResponse landlordState(HttpServletRequest request,String userId,String cityId, PageDTO pageDTO, String state,String likeAddress) {
        //通过缓存查询店铺信息
        return mendMaterielService.landlordState(userId,cityId, pageDTO,state, likeAddress);
    }

    @Override
    @ApiMethod
    public ServerResponse landlordStateHandle(HttpServletRequest request, String cityId,  PageDTO pageDTO, String state, String likeAddress) {
        return mendMaterielService.landlordStateHandle(request,cityId,pageDTO,state,likeAddress);
    }

    /**
     * 房子id查询退货单列表
     */
    @Override
    @ApiMethod
    public ServerResponse materialBackState(HttpServletRequest request,String cityId,String userId, PageDTO pageDTO,String state, String likeAddress) {

        return mendMaterielService.materialBackState(userId,cityId, pageDTO, state,likeAddress);
    }

    @Override
    @ApiMethod
    public ServerResponse applyPlatformAccess(HttpServletRequest request, String cityId, String houseId, PageDTO pageDTO) {
        return mendMaterielService.applyPlatformAccess(request,cityId,houseId,pageDTO);
    }

    @Override
    @ApiMethod
    public ServerResponse acceptPartialReturn(HttpServletRequest request, String cityId, String houseId, PageDTO pageDTO) {
        return mendMaterielService.acceptPartialReturn(request,cityId,houseId,pageDTO);
    }



    @Override
    @ApiMethod
    public ServerResponse storeReturnDistributionSupplier(HttpServletRequest request, String cityId, String userId, PageDTO pageDTO, String likeAddress) {
        return mendMaterielService.storeReturnDistributionSupplier(request,cityId,userId,pageDTO,likeAddress);
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

    @Override
    @ApiMethod
    public ServerResponse confirmReturnMendMaterial(String mendOrderId, String userId,Integer type,String actualCountList,String returnReason,String supplierId) {
        return mendMaterielService.confirmReturnMendMaterial(mendOrderId,userId,type,actualCountList,returnReason,supplierId);
    }

    @Override
    @ApiMethod
    public ServerResponse returnProductDistributionSupplier(String mendOrderId, String userId, String actualCountList) {
        return mendMaterielService.returnProductDistributionSupplier(mendOrderId,userId,actualCountList);
    }

    @Override
    @ApiMethod
    public ServerResponse queryMendMaterialList(String mendOrderId, String userId) {
        return mendMaterielService.queryMendMaterialList(mendOrderId,userId);
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
    public ServerResponse materialOrderState(HttpServletRequest request, String houseId, String userId,String cityId,PageDTO pageDTO, String beginDate, String endDate, String state,String likeAddress) {

        return mendMaterielService.materialOrderState(userId,cityId,houseId, pageDTO, beginDate, endDate,state, likeAddress);
    }
}
