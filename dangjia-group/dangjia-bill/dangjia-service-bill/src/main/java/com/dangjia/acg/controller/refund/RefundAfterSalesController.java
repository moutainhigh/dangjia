package com.dangjia.acg.controller.refund;

import com.dangjia.acg.api.refund.RefundAfterSalesAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.refund.RefundAfterSalesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse queryRefundOnlyOrderList(String userToken,String cityId,String houseId,String searchKey){
        return refundAfterSalesService.queryRefundOnlyOrderList(cityId,houseId,searchKey);
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
}
