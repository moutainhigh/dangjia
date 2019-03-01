package com.dangjia.acg.controller.app.repair;

import com.dangjia.acg.api.app.repair.MendOrderAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.repair.MendOrderInfoDTO;
import com.dangjia.acg.service.repair.MendOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * author: zmj
 * Date: 2018/11/2 0002
 * Time: 19:52
 */
@RestController
public class MendOrderController implements MendOrderAPI {

    @Autowired
    private MendOrderService mendOrderService;

    /**
     * 业主确认退货
     */
    @Override
    @ApiMethod
    public ServerResponse confirmLandlordState(String houseId){
        return mendOrderService.confirmLandlordState(houseId);
    }
    /**
     *   业主已添加退货单明细
     */
    @Override
    @ApiMethod
    public ServerResponse landlordBackDetail(String houseId){
        return mendOrderService.landlordBackDetail(houseId);
    }
    /**
     * 业主退材料
     */
    @Override
    @ApiMethod
    public ServerResponse landlordBack(String userToken,String houseId,String productArr){
        return mendOrderService.landlordBack(userToken,houseId,productArr);
    }

    /**
     * 确认退人工
     */
    @Override
    @ApiMethod
    public ServerResponse confirmBackMendWorker(String houseId,String workerTypeId){
        return mendOrderService.confirmBackMendWorker(houseId,workerTypeId);
    }
    /**
     * 已添加退人工单明细
     */
    @Override
    @ApiMethod
    public ServerResponse backMendWorkerList(String houseId,String workerTypeId){
        return mendOrderService.backMendWorkerList(houseId,workerTypeId);
    }
    /**
     * 提交退人工
     */
    @Override
    @ApiMethod
    public ServerResponse backMendWorker(String userToken,String houseId, String workerGoodsArr,String workerTypeId,String changeOrderId){
        return mendOrderService.backMendWorker(userToken,houseId,workerGoodsArr,workerTypeId,changeOrderId);
    }
    /**
     * 确认补人工
     */
    @Override
    @ApiMethod
    public ServerResponse confirmMendWorker(String houseId,String workerTypeId){
        return mendOrderService.confirmMendWorker(houseId,workerTypeId);
    }
    /**
     * 已添加补人工单明细
     */
    @Override
    @ApiMethod
    public ServerResponse getMendWorkerList(String houseId,String workerTypeId){
        return mendOrderService.getMendWorkerList(houseId,workerTypeId);
    }
    @Override
    public MendOrderInfoDTO getMendMendOrderInfo(String houseId, String workerTypeId, String type, String state){
        return mendOrderService.getMendMendOrderInfo(houseId,workerTypeId,type,state);
    }
    /**
     * 提交补人工
     */
    @Override
    @ApiMethod
    public ServerResponse saveMendWorker(String userToken,String houseId, String workerGoodsArr,String workerTypeId,String changeOrderId){
        return mendOrderService.saveMendWorker(userToken,houseId,workerGoodsArr,workerTypeId,changeOrderId);
    }

    /**
     * 确认退货
     */
    @Override
    @ApiMethod
    public ServerResponse confirmBackMendMaterial(String userToken,String houseId,String imageArr){
        return mendOrderService.confirmBackMendMaterial(userToken,houseId,imageArr);
    }
    /**
     * 已添加退货单明细
     */
    @Override
    @ApiMethod
    public ServerResponse backMendMaterialList(String userToken,String houseId){
        return mendOrderService.backMendMaterialList(userToken,houseId);
    }
    /**
     * 提交退货(退材料)
     */
    @Override
    @ApiMethod
    public ServerResponse backMendMaterial(String userToken,String houseId,String productArr){
        return mendOrderService.backMendMaterial(userToken,houseId,productArr);
    }

    /**
     * 确认补货
     */
    @Override
    @ApiMethod
    public ServerResponse confirmMendMaterial(String userToken,String houseId){
        return mendOrderService.confirmMendMaterial(userToken,houseId);
    }
    /**
     * 返回已添加补材料单明细
     */
    @Override
    @ApiMethod
    public ServerResponse getMendMaterialList(String userToken,String houseId){
        return mendOrderService.getMendMaterialList(userToken,houseId);
    }
    /**
     * 保存补材料
     */
    @Override
    @ApiMethod
    public ServerResponse saveMendMaterial(String userToken,String houseId,String productArr){
        return mendOrderService.saveMendMaterial(userToken,houseId,productArr);
    }

}
