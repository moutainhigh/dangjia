package com.dangjia.acg.service.delivery;

import com.dangjia.acg.api.BasicsStorefrontAPI;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.delivery.DjDeliveryReturnSlipDTO;
import com.dangjia.acg.mapper.delivery.DjDeliveryReturnSlipMapper;
import com.dangjia.acg.modle.storefront.Storefront;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 14/10/2019
 * Time: 上午 10:40
 */
@Service
public class DjDeliveryReturnSlipService {

    @Autowired
    private DjDeliveryReturnSlipMapper djDeliveryReturnSlipMapper;
    @Autowired
    private BasicsStorefrontAPI basicsStorefrontAPI;
    private static Logger logger = LoggerFactory.getLogger(DjDeliveryReturnSlipService.class);

    /**
     * 供货任务列表
     * @param pageDTO
     * @param searchKey
     * @param invoiceStatus
     * @return
     */
    public ServerResponse querySupplyTaskList( PageDTO pageDTO, String supId, String searchKey, String invoiceStatus) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<DjDeliveryReturnSlipDTO> djDeliveryReturnSlipDTOS = djDeliveryReturnSlipMapper.querySupplyTaskList(supId, searchKey, invoiceStatus);
            PageInfo pageResult = new PageInfo(djDeliveryReturnSlipDTOS);
            djDeliveryReturnSlipDTOS.forEach(djDeliveryReturnSlipDTO -> {
                Storefront storefront = basicsStorefrontAPI.querySingleStorefrontById(djDeliveryReturnSlipDTO.getShopId());
                djDeliveryReturnSlipDTO.setShopName(storefront.getStorefrontName());
                djDeliveryReturnSlipDTO.setStorekeeperName(storefront.getStorekeeperName());
            });
            return ServerResponse.createBySuccess("查询成功",pageResult);
        } catch (Exception e) {
            logger.error("查询失败",e);
            return ServerResponse.createByErrorMessage("查询失败: "+e);
        }
    }


    /**
     * 处理供货任务
     * @param id
     * @return
     */
    public ServerResponse setDeliveryTask(String id, String invoiceStatus) {
        try {
            if(djDeliveryReturnSlipMapper.setDeliveryTask(id,invoiceStatus)>0)
                return ServerResponse.createBySuccessMessage("操作成功");
            return ServerResponse.createByErrorMessage("操作失败");
        } catch (Exception e) {
            logger.error("操作失败",e);
            return ServerResponse.createByErrorMessage("操作失败: "+e);
        }
    }
}
