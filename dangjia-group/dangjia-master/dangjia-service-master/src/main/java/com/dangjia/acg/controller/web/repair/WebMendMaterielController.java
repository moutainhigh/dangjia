package com.dangjia.acg.controller.web.repair;

import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.api.web.repair.WebMendMaterielAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.repair.MendMaterielService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    protected static final Logger logger = LoggerFactory.getLogger(WebMendMaterielController.class);
    @Autowired
    private MendMaterielService mendMaterielService;


    @Autowired
    private RedisClient redisClient;
    /**
     * 房子id查询业主退货单列表
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
    }*/

    /**
     * 房子id查询退货单列表
     */
    @Override
    @ApiMethod
    public ServerResponse materialBackState(HttpServletRequest request,String cityId,String userId, PageDTO pageDTO,String state, String likeAddress) {

        return mendMaterielService.materialBackState(userId,cityId, pageDTO, state,likeAddress);
    }

    /**
     * 售后管理--退货退款--分发供应商列表
     * @param request
     * @param cityId
     * @param userId
     * @param mendOrderId 退货申请单ID
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse searchReturnRefundMaterielList(HttpServletRequest request, String cityId, String userId,  String mendOrderId) {
        return mendMaterielService.searchReturnRefundMaterielList(request,cityId,userId,mendOrderId);
    }

   /* @Override
    @ApiMethod
    public ServerResponse materialBackStateProcessing(HttpServletRequest request,String userId, String cityId, PageDTO pageDTO, String state, String likeAddress) {
        return mendMaterielService.materialBackStateProcessing(userId,cityId, pageDTO, state,likeAddress);
    }
*/


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


    /**
     *店铺--售后处理--待处理列表
     * @param request
     * @param cityId 城市ID
     * @param userId 用户ID
     * @param pageDTO
     * @param state 状态默认：1待处理，2已处理
     * @param likeAddress
     * @param type 查询类型：1退货退款，2仅退款
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse searchReturnRrefundList(HttpServletRequest request, String cityId, String userId, PageDTO pageDTO, Integer state, String likeAddress,Integer type) {
        return mendMaterielService.searchReturnRrefundList(request,cityId,userId,pageDTO,state,likeAddress,type);
    }

    /**
     *店铺--售后处理--待处理列表
     * @param request
     * @param cityId 城市ID
     * @param userId 用户ID
     * @param pageDTO
     * @param state 状态默认：1.已分发供应商 2.已结束
     * @param likeAddress
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse searchReturnRefundSplitList(HttpServletRequest request, String cityId, String userId, PageDTO pageDTO, Integer state, String likeAddress) {
        return mendMaterielService.searchReturnRefundSplitList(request,cityId,userId,pageDTO,state,likeAddress);
    }


   /* @Override
    @ApiMethod
    public ServerResponse ownerReturnProssing(HttpServletRequest request, String cityId, String userId, PageDTO pageDTO, String state, String likeAddress) {
        return mendMaterielService.ownerReturnProssing(request,cityId,userId,pageDTO,state,likeAddress);
    }*/

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
    public ServerResponse saveReturnRefundMaterielSup(String mendOrderId, String userId,String cityId, String materielSupList) {
        try{
            return mendMaterielService.saveReturnRefundMaterielSup(mendOrderId,userId,cityId,materielSupList);
        }catch (Exception e){
            logger.error("分发异常：",e);
            return ServerResponse.createByErrorMessage("分发失败");
        }

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

    @Override
    @ApiMethod
    public ServerResponse querySurplusMaterial(String data) {
        return mendMaterielService.querySurplusMaterial(data);
    }

    @Override
    @ApiMethod
    public ServerResponse queryTrialRetreatMaterial(String data) {
        return mendMaterielService.queryTrialRetreatMaterial(data);
    }

    @Override
    @ApiMethod
    public ServerResponse addPlatformComplain(String userToken,String mendOrderId,String description) {
        return mendMaterielService.addPlatformComplain(userToken,mendOrderId,description);
    }

}
