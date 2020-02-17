package com.dangjia.acg.controller.app.deliver;

import com.dangjia.acg.api.app.deliver.OrderAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.deliver.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * author: zmj
 * Date: 2018/11/9 0009
 * Time: 10:59
 */
@RestController
public class OrderController implements OrderAPI {

    private static Logger logger = LoggerFactory.getLogger(OrderController.class);
    @Autowired
    private OrderService orderService;

    /**
     * 订单详情
     */
    @Override
    @ApiMethod
    public ServerResponse orderDetail(String orderId) {
        return orderService.orderDetail(orderId);
    }

    @Override
    @ApiMethod
    public ServerResponse orderList(String businessOrderId) {
        return orderService.orderList(businessOrderId);
    }


    /**
     * 业务订单列表
     */
    @Override
    @ApiMethod
    public ServerResponse businessOrderList(PageDTO pageDTO, String userToken, String houseId, String queryId) {
        return orderService.businessOrderList(pageDTO, userToken, houseId, queryId);
    }



    /**
     * 删除订单
     *
     * @param userToken
     * @param orderId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse delBusinessOrderById(String userToken, String orderId) {
        return orderService.delBusinessOrderById(userToken, orderId);
    }

    /**
     * 取消订单
     * @param userToken
     * @param orderId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse cancleBusinessOrderById(String userToken, String orderId) {
        return orderService.cancleBusinessOrderById(userToken,orderId);
    }


    /**
     * 管家确认要货
     * 提交到后台材料员审核
     */
    @Override
    @ApiMethod
    public ServerResponse confirmOrderSplit(String houseId, String userToken) {
        return orderService.confirmOrderSplit(userToken, houseId);
    }

    /**
     * 补货提交订单接口
     */
    @Override
    @ApiMethod
    public ServerResponse abrufbildungSubmitOrder(String userToken, String cityId, String houseId,
                                                  String mendOrderId, String addressId) {
        return orderService.abrufbildungSubmitOrder(userToken, cityId, houseId, mendOrderId, addressId);
    }

    /**
     * 返回已添加要货单明细
     */
    @Override
    @ApiMethod
    public ServerResponse getOrderItemList(String userToken, String houseId) {
        return orderService.getOrderItemList(userToken, houseId);
    }

    /**
     * 提交到要货
     */
    @Override
    @ApiMethod
    public ServerResponse saveOrderSplit(String productArr, String houseId, String userToken) {
        return orderService.saveOrderSplit(productArr, houseId, userToken);
    }

    /**
     * 补差价订单（补差价订单详情）
     * @param userToken
     * @param houseId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getDiffOrderById(String userToken,String houseId){
        return orderService.getDiffOrderById(userToken, houseId);
    }
    /**
     * 设计精算--原订单详情
     * @param userToken
     * @param houseId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getBudgetOrderById(String userToken,
                                       String houseId){
        return orderService.getBudgetOrderById(userToken, houseId);
    }

    /**
     * 设计精算，退原订单ID,取消补差价订单ID
     * @param userToken 用户TOKEN
     * @param houseId 房子ID
     * @param orderId 订单ID
     * @param diffOrderId 补差价订单ID
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse refundBudgetOrderInfo(String userToken,String houseId,String orderId,String diffOrderId){
        try{
            return orderService.refundBudgetOrderInfo(userToken, houseId, orderId, diffOrderId);
        }catch (Exception e){
            logger.error("退款异常",e);
            return ServerResponse.createByErrorMessage("退款异常");
        }

    }

    /**
     * 设计图纸不合格--审核设计图提交
     * @param userToken 用户token
     * @param houseId 房子ID
     * @param taskId 任务ID
     * @param type 类型：1当家平台设计，2平台外设计，3结束精算
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse saveDesignDrawingReview(String userToken,String houseId,String taskId,Integer type){
        try{
            return orderService.saveDesignDrawingReview(userToken, houseId, taskId, type);
        }catch (Exception e){
            logger.error("提交异常",e);
            return ServerResponse.createByErrorMessage("提交失败");
        }
    }
    /**
     * 设计图纸不合格--提交当家平台设计师订单
     * @param userToken 用户token
     * @param houseId 房子ID
     * @param taskId 任务ID
     * @param productArr 类型：1当家平台设计，2平台外设计，3结束精算
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse saveDesignOrderInfo(String userToken,String houseId,String taskId,String productArr){
        try{
            return orderService.saveDesignOrderInfo(userToken, houseId, taskId, productArr);
        }catch (Exception e){
            logger.error("提交异常",e);
            return ServerResponse.createByErrorMessage("提交失败");
        }
    }

    /**
     * 体验单验收
     * @param userToken 当前token
     * @param orderItemId 子单ID
     * @param remark 备注
     * @param images 图片，多个逗号分隔
     * @param orderStatus 状态 4=已上传验收  5=结束
     * @return
     */
    public ServerResponse checkExperience(String userToken, String orderItemId,String remark,String images,String orderStatus){
        return orderService.checkExperience(userToken, orderItemId, remark, images,orderStatus);
    }
}
