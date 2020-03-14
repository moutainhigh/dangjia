package com.dangjia.acg.controller.app.deliver;

import com.dangjia.acg.api.app.deliver.SplitDeliverAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.service.deliver.SplitDeliverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

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
    public ServerResponse partSplitDeliver(String userToken, String splitDeliverId, String image, String splitItemList,Integer userRole) {
        return splitDeliverService.partSplitDeliver(userToken, splitDeliverId, image, splitItemList,userRole);
    }

    /**
     * 确认收货
     */
    @Override
    @ApiMethod
    public ServerResponse affirmSplitDeliver(String userToken, String splitDeliverId, String image, String splitItemList,Integer userRole) {
        if (CommonUtil.isEmpty(splitItemList)) {
            return splitDeliverService.affirmSplitDeliver(userToken, splitDeliverId, image,userRole);
        } else {//IOS接口调错  此方法为补救
            return splitDeliverService.partSplitDeliver(userToken, splitDeliverId, image, splitItemList,userRole);
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
     * 发货单明细（导出）
     */
    @Override
    @ApiMethod
    public ServerResponse exportDeliverDetail(HttpServletResponse response, Integer deliverType, String splitDeliverId){
        return splitDeliverService.exportDeliverDetail(response,deliverType,splitDeliverId);
    }
    /**
     * 收货列表
     * shipState  0待发货,1已发待收货,2已收货,3取消,4部分收
     */
    @Override
    @ApiMethod
    public ServerResponse splitDeliverList(PageDTO pageDTO, String houseId, Integer shipState) {
        return splitDeliverService.splitDeliverList( pageDTO,houseId, shipState);
    }

    /**
     * 确认安装
     * @param userToken
     * @param splitDeliverId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse confirmInstallation( String userToken,String splitDeliverId) {
        return splitDeliverService.confirmInstallation(userToken,splitDeliverId);
    }

}
