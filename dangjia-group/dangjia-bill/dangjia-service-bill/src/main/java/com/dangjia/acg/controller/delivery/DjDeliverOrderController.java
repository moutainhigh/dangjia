package com.dangjia.acg.controller.delivery;

import com.dangjia.acg.api.delivery.DjDeliverOrderAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.house.HouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


/**
 *
 * Created with IntelliJ IDEA.
 * author: chenyufeng
 * Date: 25/10/2019
 * Time: 上午 9:30
 */
@RestController
public class DjDeliverOrderController implements DjDeliverOrderAPI {

    @Autowired
    private HouseService houseService;

    @Override
    @ApiMethod
    public ServerResponse calcelOrder(HttpServletRequest request, String houseId, String userToken, String user_id) {
        return houseService.calcelOrder(houseId,user_id);
    }

    @Override
    @ApiMethod
    public ServerResponse queryAllDeliverOrder(HttpServletRequest request, PageDTO pageDTO, String userId, String cityId) {
        return null;
    }

    @Override
    @ApiMethod
    public ServerResponse queryAllDeliverOrderItem(HttpServletRequest request, PageDTO pageDTO, String userId, String cityId) {
        return null;
    }

}
