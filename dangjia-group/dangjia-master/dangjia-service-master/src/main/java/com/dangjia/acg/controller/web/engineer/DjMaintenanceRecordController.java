package com.dangjia.acg.controller.web.engineer;

import com.dangjia.acg.api.web.engineer.DjMaintenanceRecordAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.engineer.DjMaintenanceRecordService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 13/12/2019
 * Time: 上午 9:57
 */
@RestController
public class DjMaintenanceRecordController implements DjMaintenanceRecordAPI {

    private static Logger logger = LoggerFactory.getLogger(DjMaintenanceRecordController.class);

    @Autowired
    private DjMaintenanceRecordService djMaintenanceRecordService;

    /**
     * 申请质保记录
     * @param userToken 用户token
     * @param houseId 房子ID
     * @param workerTypeSafeOrderId 保险订单ID
     * @param remark 备注
     * @param images 图片，多张用逗号分隔
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse saveMaintenanceRecord(String userToken,String houseId, String workerTypeSafeOrderId,
                                         String remark,String images,String productId){
        try{
            return djMaintenanceRecordService.saveMaintenanceRecord(userToken,houseId,workerTypeSafeOrderId,remark,images,productId);
        }catch (Exception e){
            logger.error("申请异常",e);
            return ServerResponse.createByErrorMessage("申请异常");
        }

    }

    /**
     * 消息弹窗--需勘查维保商品
     * @param userToken
     * @param houseId
     * @param taskId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse searchMaintenanceProduct(String userToken,String houseId,String taskId){
        try{
            return djMaintenanceRecordService.searchMaintenanceProduct(userToken,houseId,taskId);
        }catch (Exception e){
            logger.error("查询失败",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 质保申请，提交订单
     * @param userToken
     * @param houseId
     * @param maintenanceRecordId
     * @param maintenanceRecordType
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse saveMaintenanceRecordOrder(String userToken,String houseId,String maintenanceRecordId,Integer maintenanceRecordType,String cityId){
        try{
            return djMaintenanceRecordService.saveMaintenanceRecordOrder(userToken,houseId,maintenanceRecordId,maintenanceRecordType,cityId);
        }catch (Exception e){
            logger.error("提交失败",e);
            return ServerResponse.createByErrorMessage("提交失败");
        }
    }

    @Override
    @ApiMethod
    public ServerResponse queryDjMaintenanceRecordList(PageDTO pageDTO, String searchKey, Integer state) {
        return djMaintenanceRecordService.queryDjMaintenanceRecordList(pageDTO,searchKey,state);
    }

    @Override
    @ApiMethod
    public ServerResponse queryDjMaintenanceRecordDetail(String id) {
        return djMaintenanceRecordService.queryDjMaintenanceRecordDetail(id);
    }

    @Override
    @ApiMethod
    public ServerResponse setDjMaintenanceRecord(String id,Integer state,String userId) {
        return djMaintenanceRecordService.setDjMaintenanceRecord(id,state,userId);
    }

    @Override
    @ApiMethod
    public ServerResponse queryMemberList(PageDTO pageDTO,String name) {
        return djMaintenanceRecordService.queryMemberList(pageDTO,name);
    }


    @Override
    @ApiMethod
    public ServerResponse upDateMaintenanceInFo(String supervisorId,
                                                  Integer stewardSubsidy,
                                                  String serviceRemark,
                                                  String userId,
                                                  String id,
                                                  Integer handleType) {
        return djMaintenanceRecordService.upDateMaintenanceInFo(supervisorId,stewardSubsidy
                ,serviceRemark,userId,id,handleType);
    }

    @Override
    @ApiMethod
    public ServerResponse updateTaskStackData(String id) {
        return djMaintenanceRecordService.updateTaskStackData(id);
    }

    @Override
    @ApiMethod
    public ServerResponse queryDimensionRecord(String memberId) {
        return djMaintenanceRecordService.queryDimensionRecord(memberId);
    }

    @Override
    @ApiMethod
    public ServerResponse queryDimensionRecordInFo(String mrId) {
        return djMaintenanceRecordService.queryDimensionRecordInFo(mrId);
    }

    @Override
    @ApiMethod
    public ServerResponse insertResponsibleParty(String responsiblePartyId,String houseId,
                                                 String description, String image) {
        return djMaintenanceRecordService.insertResponsibleParty(responsiblePartyId,houseId,description,image);
    }

    @Override
    @ApiMethod
    public ServerResponse queryResponsibleParty(String responsiblePartyId,String houseId) {
        return djMaintenanceRecordService.queryResponsibleParty(responsiblePartyId,houseId);
    }

    @Override
    @ApiMethod
    public ServerResponse toQualityMoney(String data) {
        return djMaintenanceRecordService.toQualityMoney(data);
    }

    @Override
    @ApiMethod
    public ServerResponse queryRobOrderInFo(String userToken,String workerId,String houseId,String data) {
        return djMaintenanceRecordService.queryRobOrderInFo(userToken,workerId,houseId,data);
    }


//    @Override
//    @ApiMethod
//    public ServerResponse applicationAcceptance(String houseId) {
//        return djMaintenanceRecordService.applicationAcceptance(houseId);
//    }

    @Override
    @ApiMethod
    public ServerResponse queryGuaranteeMoneyList(PageDTO pageDTO,String userId,String cityId) {
        return  djMaintenanceRecordService.queryGuaranteeMoneyList(pageDTO,userId,cityId);
    }

    @Override
    @ApiMethod
    public ServerResponse queryGuaranteeMoneyDetail(String userId,String cityId,String id) {
       return djMaintenanceRecordService.queryGuaranteeMoneyDetail( userId, cityId,id);
    }

    @Override
    @ApiMethod
    public ServerResponse resolved(String userToken, String remark,String houseId,String image,String id,String workerTypeSafeOrderId ) {
        return djMaintenanceRecordService.resolved(userToken, remark,houseId,image, id, workerTypeSafeOrderId);
    }

    @Override
    @ApiMethod
    public ServerResponse sendingOwners(String userToken,String houseId,String remark ,String enoughAmount) {
        return djMaintenanceRecordService.sendingOwners(userToken,houseId,remark,enoughAmount);
    }

    @Override
    @ApiMethod
    public ServerResponse auditMaintenance(String userToken, String remark, String houseId, String image, String id, Integer state, String workerTypeSafeOrderId) {
        return djMaintenanceRecordService.auditMaintenance(userToken, remark, houseId, image, id, state, workerTypeSafeOrderId);
    }

    @Override
    @ApiMethod
    public ServerResponse submitQualityAssurance(String userToken, String houseId,
                                                 String remark,String image,
                                                 String id, Integer state,
                                                 String productId,
                                                 Double price,
                                                 Double shopCount,
                                                 String workerTypeSafeOrderId) {
        return djMaintenanceRecordService.submitQualityAssurance(userToken, houseId, remark, image, id, state, productId, price,
                shopCount, workerTypeSafeOrderId);
    }

    @Override
    @ApiMethod
    public ServerResponse addApplyNewspaper(String userToken,
                                             String memberId,
                                             Double money,
                                             String description,
                                             String image,
                                             String houseId,
                                            String businessId) {
        return djMaintenanceRecordService.addApplyNewspaper(userToken, memberId, money, description, image, houseId,businessId);
    }

    @Override
    @ApiMethod
    public ServerResponse queryComplain(String userToken,String memberId){
        return djMaintenanceRecordService.queryComplain(userToken, memberId);
    }

    @Override
    @ApiMethod
    public ServerResponse queryComplainInFo(String id){
        return djMaintenanceRecordService.queryComplainInFo(id);
    }

    @Override
    @ApiMethod
    public ServerResponse handleAppeal(String id,
                                       Integer type,
                                       Double actualMoney,
                                       String operateId,
                                       String rejectReason){
        return djMaintenanceRecordService.handleAppeal(id,type,actualMoney,operateId,rejectReason);
    }

    @Override
    @ApiMethod
    public ServerResponse workerApplyCollect(String id,String remarks,String image){
        return djMaintenanceRecordService.workerApplyCollect(id,remarks,image);
    }

    @Override
    @ApiMethod
    public ServerResponse insertMaintenanceRecordProduct(String userToken, String houseId, String maintenanceRecordId,String productId) {
        return djMaintenanceRecordService.insertMaintenanceRecordProduct(userToken,houseId,maintenanceRecordId,productId);
    }

    @Override
    @ApiMethod
    public ServerResponse setMaintenanceRecordProduct(String userToken, String houseId, String maintenanceRecordId) {
        return djMaintenanceRecordService.setMaintenanceRecordProduct(userToken,houseId,maintenanceRecordId);
    }

    @Override
    @ApiMethod
    public ServerResponse queryMaintenanceShoppingBasket(String userToken, String houseId, String maintenanceRecordId) {
        return djMaintenanceRecordService.queryMaintenanceShoppingBasket(userToken,houseId, maintenanceRecordId);
    }




    @Override
    @ApiMethod
    public ServerResponse setMaintenanceSolve(String userToken, String maintenanceRecordId, String remark, String image) {
        try {
            return djMaintenanceRecordService.setMaintenanceSolve(userToken,maintenanceRecordId,remark,image);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }

    @Override
    @ApiMethod
    public ServerResponse deleteMaintenanceRecordProduct(String id) {
        return djMaintenanceRecordService.deleteMaintenanceRecordProduct(id);
    }

    @Override
    @ApiMethod
    public ServerResponse confirmStart(String businessId) {
        return djMaintenanceRecordService.confirmStart(businessId);
    }


//    @Override
//    public ServerResponse setMaintenanceHandlesSubmissions(String maintenanceRecordId, String remark, String image) {
//        return null;
//    }

}

