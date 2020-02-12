package com.dangjia.acg.controller.web.deliver;

import com.dangjia.acg.api.web.deliver.WebOrderSplitAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.deliver.SplitDeliver;
import com.dangjia.acg.service.deliver.OrderSplitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


@RestController
public class WebOrderSplitController implements WebOrderSplitAPI {
    private static Logger logger = LoggerFactory.getLogger(WebOrderSplitController.class);

    @Autowired
    private OrderSplitService orderSplitService;//生成发货单


    /**
     * 供应商发货
     */
    @Override
    @ApiMethod
    public ServerResponse sentSplitDeliver(String splitDeliverId) {
        return orderSplitService.sentSplitDeliver(splitDeliverId);
    }

    /**
     * 供应商发货
     */
    @Override
    @ApiMethod
    public ServerResponse rejectionSplitDeliver(String splitDeliverId) {
        return orderSplitService.rejectionSplitDeliver(splitDeliverId);
    }

    /**
     * 发货任务--货单详情--清单
     */
    @Override
    @ApiMethod
    public ServerResponse splitDeliverDetail(String splitDeliverId) {
        return orderSplitService.splitDeliverDetail(splitDeliverId);
    }

    @Override
    @ApiMethod
    public ServerResponse splitDeliverList(String supplierId, Integer shipState) {
        return orderSplitService.splitDeliverList(supplierId, shipState);
    }

    /**
     * 撤回供应商待发货的订单（整单撤回）
     */
    @Override
    @ApiMethod
    public ServerResponse withdrawSupplier(String orderSplitId) {
        return orderSplitService.withdrawSupplier(orderSplitId);
    }

    /**
     *
     * @param orderSplitId 要货单ID
     * @param splitDeliverId 发货单ID(重新发货时为必填）
     * @param splitItemList [{id:”aa”,supplierId:”xx”},{id:”bb”,supplierId:”xx”}] 分发明细 id要货单明细ID，supplierId 供应商ID
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse sentSupplier(String orderSplitId,String splitDeliverId, String splitItemList) {
        try{
            return orderSplitService.sentSupplier(orderSplitId,splitDeliverId, splitItemList);
        }catch (Exception e){
            logger.error("发送供应商失败：",e);
            return ServerResponse.createByErrorMessage("发送给供应商失败");
        }

    }

    /**
     * 分发供应商--生成发货单
     * @param orderSplitId 要货单ID
     * @param splitDeliverId 发货单ID(重新发货时为必填）
     * @param cityId 城市ID
     * @param userId 用户ID
     * @param installName 安装人姓名
     * @param installMobile 安装人电话
     * @param deliveryName
     * @param deliveryMobile 送货人电话
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse saveSentSupplier(String orderSplitId,String splitDeliverId,
                                       String cityId, String userId, String installName
            , String installMobile, String deliveryName, String deliveryMobile) {
        try{
            return orderSplitService.saveSentSupplier(orderSplitId,splitDeliverId, cityId, userId, installName, installMobile, deliveryName, deliveryMobile);
        }catch (Exception e){
            logger.error("发送供应商失败：",e);
            return ServerResponse.createByErrorMessage("发送给供应商失败");
        }

    }


    /**
     * 部分收货申诉接口
     * @param splitDeliverId 发货单ID
     * @param splitItemList 发货单明细列表
     * @param type 类型：1.认可部分收货，2申请平台申诉
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse platformComplaint(String splitDeliverId,String splitItemList,Integer type,String  userId){
        try{
            return orderSplitService.platformComplaint(splitDeliverId, splitItemList,type,userId);
        }catch (Exception e){
            logger.error("保存失败：",e);
            return ServerResponse.createByErrorMessage("保存失败");
        }
    }



    @Override
    @ApiMethod
    public ServerResponse cancelOrderSplit(String orderSplitId) {
        return orderSplitService.cancelOrderSplit(orderSplitId);
    }
    @Override
    @ApiMethod
    public ServerResponse cancelSplitDeliver(String splitDeliverId) {
        return orderSplitService.cancelSplitDeliver(splitDeliverId);
    }


    /**
     * 货单列表--分发任务列表
     * @param orderSplitId 要货单号
     * @param splitDeliverId 发货单号
     */
    @Override
    @ApiMethod
    public ServerResponse orderSplitItemList(String orderSplitId,String splitDeliverId) {
        return orderSplitService.orderSplitItemList(orderSplitId,splitDeliverId);
    }

    /**
     * 货单列表--货单详情列表
     * @param orderSplitId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getOrderSplitDeliverList(String orderSplitId){
        return orderSplitService.getOrderSplitDeliverList(orderSplitId);
    }




    @Override
    @ApiMethod
    public ServerResponse getHouseList(HttpServletRequest request, String cityId, PageDTO pageDTO, String likeAddress, String startDate, String endDate) {
        String userId = request.getParameter("userId");
        //通过缓存查询店铺信息
        return orderSplitService.getHouseList(userId,cityId,pageDTO, likeAddress, startDate,  endDate);
    }

    @Override
    @ApiMethod
    public ServerResponse getOrderSplitList(HttpServletRequest request,String userId,String cityId,PageDTO pageDTO,String addressId,String houseId,String storefrontId) {
        return orderSplitService.getOrderSplitList(userId,cityId,pageDTO,addressId,houseId,storefrontId);
    }

    @Override
    @ApiMethod
    public ServerResponse setSplitDeliver(SplitDeliver splitDeliver) {
        return orderSplitService.setSplitDeliver(splitDeliver);
    }
}
