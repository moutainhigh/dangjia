package com.dangjia.acg.controller.refund;

import com.dangjia.acg.api.refund.RefundAfterSalesAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.refund.RefundAfterSalesService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * Created with IntelliJ IDEA.
 * author: fzh
 * Date: 25/10/2019
 * Time: 下午 3:56
 */
@RestController
public class RefundAfterSalesController implements RefundAfterSalesAPI {
    protected static final Logger logger = LoggerFactory.getLogger(RefundAfterSalesController.class);

    @Autowired
    private RefundAfterSalesService refundAfterSalesService;


    /**
     * 查询可退款的商品
     * @param userToken
     * @param cityId 城市ID
     * @param houseId 房屋ID
     * @param searchKey 订单号或商品名称
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse<PageInfo> queryRefundOnlyOrderList(PageDTO pageDTO, String userToken, String cityId, String houseId, String searchKey){
        return refundAfterSalesService.queryRefundOnlyOrderList(pageDTO,cityId,houseId,searchKey);
    }

    /**
     * 申请退款页面，列表展示
     * @param userToken        用户token
     * @param cityId           城市ID
     * @param houseId          房屋ID
     * @param orderProductAttr 需退款商品列表
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse queryRefundonlyInfoList(String userToken,String cityId,String houseId,String orderProductAttr){
        return refundAfterSalesService.queryRefundonlyInfoList(userToken,cityId,houseId,orderProductAttr);
    }
    /**
     *
     * @param userToken  用户token
     * @param cityId 城市ID
     * @param houseId 房屋ID
     * @param orderProductAttr  需退款商品列表
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse saveRefundonlyInfo(String userToken,String cityId, String houseId,String orderProductAttr){
        try{
            return  refundAfterSalesService.saveRefundonlyInfo(userToken,cityId,houseId,orderProductAttr);
        }catch (Exception e){
            logger.error("saveRefundonlyInfo 提交失败：",e);
        }
        return ServerResponse.createByErrorMessage("提交失败");
    }

    /**
     * 仅退款详情列表查询
     * @param pageDTO 分页
     * @param userToken  用户token
     * @param cityId 城市ID
     * @param houseId 房子ID
     * @param searchKey
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse<PageInfo> queryRefundOnlyHistoryOrderList(PageDTO pageDTO, String userToken,
                                                             String cityId,String houseId,String searchKey){
        return refundAfterSalesService.queryRefundOnlyHistoryOrderList(pageDTO,cityId,houseId,searchKey,4);
    }

    /**
     * 根据退货单查询退款详情
     * @param cityId
     * @param repairMendOrderId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse queryRefundOnlyHistoryOrderInfo(String cityId,String repairMendOrderId){
        return refundAfterSalesService.queryRefundOnlyHistoryOrderInfo(cityId,repairMendOrderId);
    }

    /**
     * 撤销退款申请
     * @param cityId
     * @param repairMendOrderId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse cancelRepairApplication(String cityId,String repairMendOrderId){
        try{
            return refundAfterSalesService.cancelRepairApplication(cityId,repairMendOrderId);
        }catch (Exception e){
            logger.error("撤销失败",e);
            return ServerResponse.createByErrorMessage("撤销失败");
        }
    }

    /**
     * 业主申诉退货
     * @param userToken
     * @param content  申诉内容
     * @param houseId  房子ID
     * @param repairMendOrderId  申诉单号
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse addRepairComplain(String userToken,String content,String houseId,String repairMendOrderId){
        try{
            return refundAfterSalesService.addRepairComplain(userToken,content,houseId,repairMendOrderId);
        }catch (Exception e){
            logger.error("申诉失败",e);
            return ServerResponse.createByErrorMessage("申诉失败");
        }
    }

    /**
     * 驳回退款申诉
     * @param repairMendOrderId 退款申请单ID
     */
    @Override
    @ApiMethod
    public ServerResponse rejectRepairApplication(String repairMendOrderId,String userId){
        try{
           return  refundAfterSalesService.rejectRepairApplication(repairMendOrderId,userId);
        }catch (Exception e){
            logger.error("驳回申诉失败",e);
            return ServerResponse.createByErrorMessage("驳回申诉失败");
        }
    }

    /**
     * 同意退款申诉
     * @param repairMendOrderId 退款申请单ID
     */
    @Override
    @ApiMethod
    public ServerResponse agreeRepairApplication(String repairMendOrderId,String userId){
        try{
          return  refundAfterSalesService.agreeRepairApplication(repairMendOrderId,userId);
        }catch (Exception e){
            logger.error("同意申诉失败",e);
            return ServerResponse.createByErrorMessage("同意申诉失败");
        }
    }

    /**
     * 查询可退货退款的商品
     * @param userToken
     * @param cityId 城市ID
     * @param houseId 房屋ID
     * @param searchKey 订单号或商品名称
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse<PageInfo> queryReturnRefundOrderList(PageDTO pageDTO,String userToken,String cityId,String houseId,String searchKey){
        return refundAfterSalesService.queryReturnRefundOrderList(pageDTO,cityId,houseId,searchKey);
    }

    /**
     * 申请退货退款列表展示
     * @param userToken        用户token
     * @param cityId           城市ID
     * @param houseId          房屋ID
     * @param orderProductAttr 需退款商品列表
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse queryReturnRefundInfoList(String userToken,String cityId,String houseId,String orderProductAttr){
        return refundAfterSalesService.queryReturnRefundInfoList(userToken,cityId,houseId,orderProductAttr);
    }
    /**
     * 退货退款提交
     * @param userToken        用户token
     * @param cityId           城市ID
     * @param houseId          房屋ID
     * @param orderProductAttr 需退款商品列表
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse saveReturnRefundInfo(String userToken,String cityId,String houseId,String orderProductAttr){
        try{
            return  refundAfterSalesService.saveReturnRefundInfo(userToken,cityId,houseId,orderProductAttr);
        }catch (Exception e){
            logger.error("saveReturnRefundInfo 提交失败：",e);
        }
        return ServerResponse.createByErrorMessage("提交失败");
    }
    /**
     * 退货退款历史记录查询
     * @param userToken        用户token
     * @param cityId           城市ID
     * @param houseId          房屋ID
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse<PageInfo> queryReturnRefundHistoryOrderList(PageDTO pageDTO,String userToken,String cityId,String houseId,
                                                               String searchKey){
        return refundAfterSalesService.queryRefundOnlyHistoryOrderList(pageDTO,cityId,houseId,searchKey,5);
    }

    /**
     * 查询退人工历史记录列表
     * @param pageDTO
     * @param userToken
     * @param cityId
     * @param houseId
     * @param searchType 查询类型 ：1工匠补人工历史记录查询，2业主补退人工历史记录查询
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse<PageInfo> queryRetrunWorkerHistoryList(PageDTO pageDTO,String userToken,String cityId,String houseId,String searchType){
        return refundAfterSalesService.queryRetrunWorkerHistoryList(pageDTO,userToken,cityId,houseId,searchType);
    }

    /**
     * 退人工详情页面
     * @param cityId
     * @param repairWorkOrderId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse queryRetrunWorkerHistoryDetail(String cityId,String repairWorkOrderId){
        return refundAfterSalesService.queryRetrunWorkerHistoryDetail(cityId,repairWorkOrderId);
    }

    /**
     * 撤销退人工申请
     * @param cityId
     * @param repairWorkOrderId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse cancelWorkerApplication(String cityId,String repairWorkOrderId){
        try{
            return refundAfterSalesService.cancelWorkerApplication(cityId,repairWorkOrderId);
        }catch (Exception e){
            logger.error("撤销失败",e);
            return ServerResponse.createByErrorMessage("撤销失败");
        }

    }

    /**
     * 查询待审核的补人工订单
     * @param cityId
     * @param mendOrderId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse searchAuditInfoByTaskId(String cityId,String mendOrderId){
        return refundAfterSalesService.searchAuditInfoByTaskId(cityId,mendOrderId);
    }



    /**
     * 退人工--查询符合条件的可退人工商品
     * @param userToken 用户token
     * @param cityId  城市ID
     * @param houseId 房子ID
     * @param workerTypeId 工种ID
     * @param searchKey 商品名称
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse queryWorkerProductList(String userToken,String cityId,String houseId,
                                          String workerTypeId,String searchKey){
        return refundAfterSalesService.queryWorkerProductList(userToken,cityId,houseId,workerTypeId,searchKey);
    }


}
