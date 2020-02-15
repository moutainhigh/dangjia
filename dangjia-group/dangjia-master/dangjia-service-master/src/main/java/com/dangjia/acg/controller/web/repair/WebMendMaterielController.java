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
import org.springframework.web.bind.annotation.RequestParam;
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




    /**
     * 退货退款—确认退货/部分退货
     * @param mendDeliverId 退货单ID
     * @param userId 用户id
     * @param type 类型：1确认退货，2部分退货
     * @param mendMaterialList 退货详情列表 [{“mendMaterielId”:”退货明细ID”,”actualCount”:9（实退货量）}]
     * @param partialReturnReason 部分退货原因
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse confirmReturnMendMaterial(String mendDeliverId, String userId,Integer type,String mendMaterialList,String partialReturnReason) {
       try{
           return mendMaterielService.confirmReturnMendMaterial(mendDeliverId,userId,type,mendMaterialList,partialReturnReason);
       }catch (Exception e){
           logger.error("确认退货失败",e);
           return ServerResponse.createByErrorMessage("确认退货失败");
       }

    }

    /**
     * 售后管理--仅退款--退货单详情列表
     * @param cityId
     * @param userId
     * @param mendOrderId 退货申请单ID
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse searchRefundMaterielList(String cityId,String userId,String mendOrderId){
        return mendMaterielService.searchRefundMaterielList(cityId,userId,mendOrderId);
    }

    /**
     * 售后管理--仅退款--确认退款
     * @param cityId
     * @param userId
     * @param mendOrderId 退货申请单ID
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse saveRefundMaterielInfo(String cityId,String userId,String mendOrderId){
        try{
            return mendMaterielService.saveRefundMaterielInfo(cityId,userId,mendOrderId);
        }catch (Exception e){
            logger.error("确认失败",e);
            return ServerResponse.createByErrorMessage("确认失败");
        }
    }



    /**
     * 退货退款--分发供应商--保存分发
     * @param mendOrderId 退货申请单ID
     * @param userId
     * @param cityId
     * @param materielSupList [{“mendMaterielId”:”退货单明细ID”,supplierId：“供应商ID”}]
     * @return
     */
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

    /**
     * 退货退款--分发供应商--生成退货单
     * @param mendOrderId 退货申请单ID
     * @param userId 用户ID
     * @param cityId 城市ID
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse generateMendDeliverorder(String mendOrderId,String userId,String cityId){
        try{
            return mendMaterielService.generateMendDeliverorder(mendOrderId,userId,cityId);
        }catch (Exception e){
            logger.error("保存失败：",e);
            return ServerResponse.createByErrorMessage("保存失败");
        }
    }

    /**
     * 退货退款—退货详情列表
     * @param mendDeliverId 退货单ID
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse queryMendMaterialList(String mendDeliverId) {
        return mendMaterielService.queryMendMaterialList(mendDeliverId);
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
    public ServerResponse queryTrialRetreatMaterial(String taskId) {
        return mendMaterielService.queryTrialRetreatMaterial(taskId);
    }

    /**
     * 业主审核部分退货--申请平台介入/接受商家退货
     * @param userToken
     * @param taskId 任务ID
     * @param description 平台介入原因
     * @param type 审核结果：1申请平台介入 2接受商家退货数
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse addPlatformComplain(String userToken,String taskId,String description,Integer type) {
        return mendMaterielService.addPlatformComplain(userToken,taskId,description,type);
    }

}
