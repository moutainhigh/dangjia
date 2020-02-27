package com.dangjia.acg.controller.shell;

import com.dangjia.acg.api.shell.HomeShellOrderAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.shell.HomeShellOrderService;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * fzh
 * 2020-02-25
 */
@RestController
public class HomeShellOrderController implements HomeShellOrderAPI {
    protected static final Logger logger = LoggerFactory.getLogger(HomeShellOrderController.class);

    @Autowired
    private HomeShellOrderService homeShellOrderService;
    /**
     * 查询兑换记录列表
     * @param request
     * @param pageDTO 分页
     * @param exchangeClient 客户端：-1全部，1业主端，2工匠端
     * @param status 查询状态：-1全部，1待发货，2待收货，3已收货，4待退款，5已退款
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param searchKey 兑换人姓名/电话/单号
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse queryOrderInfoList(HttpServletRequest request, PageDTO pageDTO,Integer exchangeClient,Integer status, Date startTime,Date endTime, String searchKey){
        return homeShellOrderService.queryOrderInfoList(pageDTO,exchangeClient,status,startTime,endTime,searchKey);
    }
    /**
     * 查询兑换详情
     * @param request
     * @param homeOrderId 兑换记录ID
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse queryOrderInfoDetail(HttpServletRequest request,String homeOrderId){
        return homeShellOrderService.queryOrderInfoDetail(homeOrderId);
    }

    /**
     * 修改订单状态
     * @param request
     * @param homeOrderId 兑换记录ID
     * @param status 类型：2发货，5退货，8确认发放
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse updateOrderInfo(HttpServletRequest request,String homeOrderId,Integer status){
        try{
            return homeShellOrderService.updateOrderInfo(homeOrderId,status);
        }catch (Exception e){
            logger.error("查询失败",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 当家贝商城--商品兑换提交
     * @param userToken 用户token
     * @param addressId 地址ID
     * @param productSpecId 商品规格ID
     * @param exchangeNum 商品数量
     * @param userRole 提交服务端：1为业主应用，2为工匠应用，3为销售应用
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse saveConvertedCommodities(String userToken,String addressId,String productSpecId,Integer exchangeNum,Integer userRole,String cityId){
        try{
            return homeShellOrderService.saveConvertedCommodities(userToken,addressId,productSpecId,exchangeNum,userRole,cityId);
        }catch (Exception e){
            logger.error("兑换失败",e);
            return ServerResponse.createByErrorMessage("兑换失败");
        }
    }
    /**
     * 当家贝商城--兑换记录
     * @param userToken 用户token
     * @param pageDTO 分页
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse searchShellProductInfo(String userToken,PageDTO pageDTO){
        return homeShellOrderService.searchShellProductInfo(userToken,pageDTO);
    }
    /**
     * 当家贝商城--当家贝明细
     * @param userToken
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse searchShellMoneyList(String userToken,PageDTO pageDTO){
        return homeShellOrderService.searchShellMoneyList(userToken,pageDTO);
    }

    /**
     * 当家贝商城--兑换详情
     * @param userToken
     * @param  shellOrderId 兑换记录ID
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse searchConvertedProductInfo(String userToken,String shellOrderId){
        return homeShellOrderService.searchConvertedProductInfo(userToken,shellOrderId);
    }

    /**
     * 当家贝商城--确认收货/撤销退款
     * @param userToken
     * @param shellOrderId 兑换记录ID
     * @param type 类型：1确认收货，2撤销退款
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse updateConvertedProductInfo(String userToken,String shellOrderId,Integer type){
        try{
            return homeShellOrderService.updateConvertedProductInfo(userToken,shellOrderId,type);
        }catch(Exception e){
            logger.error("确认收货失败",e);
            return ServerResponse.createByErrorMessage("确认收货失败");
        }
    }

    /**
     * 当家贝商城--取消订单/申请退款
     * @param userToken
     * @param shellOrderId 兑换记录ID
     * @param image 相关凭证
     * @param type 申请类型：1取消订单，2申请退款
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse refundConvertedProductInfo(String userToken,String shellOrderId,String image,Integer type){
        try{
            return homeShellOrderService.refundConvertedProductInfo(userToken,shellOrderId,image,type);
        }catch(Exception e){
            logger.error("申请退款失败",e);
            return ServerResponse.createByErrorMessage("申请退款失败");
        }
    }

    /**
     * 当家贝商城，充值页面
     * @param userToken
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse searchShellRechargeInfo(String userToken){
         return homeShellOrderService.searchShellRechargeInfo(userToken);
    }

    /**
     * 当家贝商城--支付充值（生成支付单)
     * @param userToken
     * @param id 充值记录ID
     * @param cityId 城市ID
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse saveShellRechargeInfo(String userToken,String id,String cityId){
        try{
            return homeShellOrderService.saveShellRechargeInfo(userToken,id,cityId);
        }catch (Exception e){
            logger.error("生成失败",e);
            return ServerResponse.createByErrorMessage("充值失败");
        }
    }
}
