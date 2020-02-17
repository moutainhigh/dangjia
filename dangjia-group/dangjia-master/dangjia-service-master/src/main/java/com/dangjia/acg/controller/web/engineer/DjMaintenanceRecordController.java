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
     * 消息弹窗--维保商品订单
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
     * 消息弹窗--报销商品订单
     * @param userToken
     * @param houseId
     * @param taskId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse searchExpenseMaintenanceProduct(String userToken,String houseId,String taskId){
         return djMaintenanceRecordService.searchExpenseMaintenanceProduct(userToken,houseId,taskId);
    }

    /**
     * 消息弹窗--验收申请单
     * @param userToken
     * @param houseId
     * @param taskId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse searchAcceptanceApplication(String userToken,String houseId,String taskId){
          return djMaintenanceRecordService.searchAcceptanceApplication(userToken,houseId,taskId);
    }

    /**
     * 消息弹窗--提交维保商品
     * @param userToken
     * @param houseId
     * @param taskId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse saveMaintenanceProduct(String userToken,String houseId,String taskId,String cityId){
        try{
            return djMaintenanceRecordService.saveMaintenanceProduct(userToken,houseId,taskId,cityId);
        }catch (Exception e){
            logger.error("提交失败",e);
            return ServerResponse.createByErrorMessage("提交失败");
        }
    }

    /**
     * 消息弹窗--提交报销商品
     * @param userToken
     * @param houseId
     * @param taskId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse saveExpenseMaintenanceProduct(String userToken,String houseId,String taskId,String cityId){
        try{

            return djMaintenanceRecordService.saveExpenseMaintenanceProduct(userToken,houseId,taskId,cityId);
        }catch (Exception e){
            logger.error("提交失败",e);
            return ServerResponse.createByErrorMessage("提交失败");
        }
    }

    /**
     * 消息弹窗--提交验收申请结果
     * @param userToken 用户token
     * @param houseId 房子ID
     * @param taskId 任务ID
     * @param auditResult 审核结果1通过，2不通过
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse saveAcceptanceApplication(String userToken,String houseId,String taskId,Integer auditResult){
        try{
            return djMaintenanceRecordService.saveAcceptanceApplication(userToken,houseId,taskId,auditResult);
        }catch (Exception e){
            logger.error("提交失败",e);
            return ServerResponse.createByErrorMessage("提交失败");
        }
    }
    @Override
    @ApiMethod
    public void saveAcceptanceApplicationJob(){
         djMaintenanceRecordService.saveAcceptanceApplicationJob();
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
    public ServerResponse  workerEndMaintenanceRecord(String userToken,String maintenanceRecordId,String image,String remark,String cityId){
        return djMaintenanceRecordService.workerEndMaintenanceRecord(userToken,maintenanceRecordId,image,remark,cityId);
    }


    /**
     * 提前结束--勘查费用商品页面
     * @param userToken
     * @param houseId
     * @param maintenanceRecordId
     * @param cityId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse endMaintenanceSearchProduct(String userToken,String houseId,String maintenanceRecordId,String cityId){
        return djMaintenanceRecordService.endMaintenanceSearchProduct(userToken,houseId,maintenanceRecordId,cityId);
    }

    /**
     * 提前结束，结束维保
     * @param userToken
     * @param houseId
     * @param maintenanceRecordId
     * @param cityId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse endMaintenanceRecord(String userToken,String houseId,String maintenanceRecordId,String cityId){
        try{
            return djMaintenanceRecordService.endMaintenanceRecord(userToken,houseId,maintenanceRecordId,cityId,3);
        }catch (Exception e){
            logger.error("提交失败",e);
            return ServerResponse.createByErrorMessage("提交失败");
        }
    }

    /**
     * 质保管理--发表评价
     * @param userToken
     * @param houseId 房子ID
     * @param maintenanceRecordId 质保ID
     * @param workerId 工匠ID
     * @param start 星级
     * @param content 评价内容
     * @param image 评价图片
     * @param cityId 城市ID
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse evaluationMaintenanceRecord(String userToken,String houseId,String maintenanceRecordId,String workerId,
                                               Integer start,String content,String image,String cityId){
        try{
            return djMaintenanceRecordService.evaluationMaintenanceRecord( userToken, houseId, maintenanceRecordId, workerId,
                     start, content, image, cityId);
        }catch (Exception e){
            logger.error("评价失败",e);
            return ServerResponse.createByErrorMessage("评价失败");
        }
    }

    /**
     * 查询质保提交信息
     * @param maintenanceRecordId 质保ID
     * @param type 查询类型： 1:工匠 2:大管家 3：业主
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse searchMaintenaceRecordInfo(String maintenanceRecordId,Integer type){
        return djMaintenanceRecordService.searchMaintenaceRecordInfo(maintenanceRecordId,type);
    }

    /**
     * 查询质保详情记录
     * @param userToken
     * @param maintenanceRecordId
     * @return
     */
    @Override
    @ApiMethod
    public  ServerResponse queryMaintenanceRecordDetail(String userToken,String maintenanceRecordId){
        return djMaintenanceRecordService.queryMaintenanceRecordDetail(userToken,maintenanceRecordId);
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
    @Override
    @ApiMethod
    public ServerResponse queryGuaranteeMoneyList(PageDTO pageDTO,String userId,String cityId){
        return djMaintenanceRecordService.queryGuaranteeMoneyList(pageDTO,userId, cityId);
    }
    @Override
    @ApiMethod
    public ServerResponse queryGuaranteeMoneyDetail(String userId,String cityId,String accountflowRecordId) {
       return djMaintenanceRecordService.queryGuaranteeMoneyDetail( userId, cityId,accountflowRecordId);
    }

    @Override
    @ApiMethod
    public ServerResponse addApplyNewspaper(Double money,
                                             String description,
                                             String image,
                                            String businessId) {
        return djMaintenanceRecordService.addApplyNewspaper( money, description, image,businessId);
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
    public ServerResponse workerApplyCollect(String id,String remark,String image){
        return djMaintenanceRecordService.workerApplyCollect(id,remark,image);
    }

    @Override
    @ApiMethod
    public ServerResponse insertMaintenanceRecordProduct(String userToken, String houseId, String maintenanceRecordId,String productId,Double shopCount) {
        try {
            return djMaintenanceRecordService.insertMaintenanceRecordProduct(userToken,houseId,maintenanceRecordId,productId,shopCount);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("操作失败", e);
            return ServerResponse.createByErrorMessage("操作失败");
        }
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

    @Override
    @ApiMethod
    public ServerResponse setWorkerMaintenanceGoods(String userToken, String maintenanceRecordId, String houseId) {
        try {
            return djMaintenanceRecordService.setWorkerMaintenanceGoods(userToken,maintenanceRecordId,houseId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("操作失败",e);
            return ServerResponse.createByErrorMessage("操作成功");
        }
    }


    @Override
    @ApiMethod
    public ServerResponse setMaintenanceHandlesSubmissions(String userToken,String maintenanceRecordId, String remark, String image) {
        try {
            return djMaintenanceRecordService.setMaintenanceHandlesSubmissions(userToken,maintenanceRecordId,remark,image);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("操作失败",e);
            return ServerResponse.createByErrorMessage("操作成功");
        }
    }

}

