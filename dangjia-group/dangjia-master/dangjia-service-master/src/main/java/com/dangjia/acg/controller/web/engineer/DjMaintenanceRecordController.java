package com.dangjia.acg.controller.web.engineer;

import com.dangjia.acg.api.web.engineer.DjMaintenanceRecordAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.engineer.DjMaintenanceRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
                                         String remark,String images){
        try{
            return djMaintenanceRecordService.saveMaintenanceRecord(userToken,houseId,workerTypeSafeOrderId,remark,images);
        }catch (Exception e){
            logger.error("申请异常",e);
            return ServerResponse.createByErrorMessage("申请异常");
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
        return djMaintenanceRecordService.auditMaintenance(userToken,remark,houseId,image,id,state,workerTypeSafeOrderId);
    }

    @Override
    @ApiMethod
    public ServerResponse submitQualityAssurance(String userToken, String remark, String houseId, String image, String id, Integer state, String workerTypeSafeOrderId) {
        return djMaintenanceRecordService.submitQualityAssurance(userToken,remark,houseId,image,id,state,workerTypeSafeOrderId);
    }

}
