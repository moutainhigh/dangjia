package com.dangjia.acg.controller.shell;

import com.dangjia.acg.api.shell.HomeShellOrderAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.shell.HomeShellOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    private HomeShellOrderService billHomeShellOrderService;
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
        return billHomeShellOrderService.queryOrderInfoList(pageDTO,exchangeClient,status,startTime,endTime,searchKey);
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
        return billHomeShellOrderService.queryOrderInfoDetail(homeOrderId);
    }

    /**
     * 修改订单状态
     * @param request
     * @param homeOrderId 兑换记录ID
     * @param status 2发货，5退货
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse updateOrderInfo(HttpServletRequest request,String homeOrderId,Integer status){
        try{
            return billHomeShellOrderService.updateOrderInfo(homeOrderId,status);
        }catch (Exception e){
            logger.error("查询失败",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }
}
