package com.dangjia.acg.controller.app.deliver;

import com.dangjia.acg.api.app.deliver.SplitDeliverAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.service.deliver.SplitDeliverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * author: Ronalcheng
 * Date: 2018/12/5 0005
 * Time: 20:14
 */
@RestController
public class SplitDeliverController implements SplitDeliverAPI {

    @Autowired
    private SplitDeliverService splitDeliverService;


    /**
     * 部分收货
     */
    @Override
    @ApiMethod
    public ServerResponse partSplitDeliver(String userToken, String splitDeliverId, String image, String splitItemList) {
        return splitDeliverService.partSplitDeliver(userToken, splitDeliverId, image, splitItemList);
    }

    /**
     * 确认收货
     */
    @Override
    @ApiMethod
    public ServerResponse affirmSplitDeliver(String userToken, String splitDeliverId, String image, String splitItemList) {
        if (CommonUtil.isEmpty(splitItemList)) {
            return splitDeliverService.affirmSplitDeliver(userToken, splitDeliverId, image);
        } else {//IOS接口调错  此方法为补救
            return splitDeliverService.partSplitDeliver(userToken, splitDeliverId, image, splitItemList);
        }
    }

    /**
     * 委托大管家收货
     */
    @Override
    @ApiMethod
    public ServerResponse supState(String splitDeliverId) {
        return splitDeliverService.supState(splitDeliverId);
    }

    /**
     * 发货单明细
     */
    @Override
    @ApiMethod
    public ServerResponse splitDeliverDetail(String splitDeliverId) {
        return splitDeliverService.splitDeliverDetail(splitDeliverId);
    }

    /**
     * 收货列表
     * shipState  0待发货,1已发待收货,2已收货,3取消,4部分收
     */
    @Override
    @ApiMethod
    public ServerResponse splitDeliverList(String houseId, int shipState) {
        return splitDeliverService.splitDeliverList(houseId, shipState);
    }
}
