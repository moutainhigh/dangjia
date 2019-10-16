package com.dangjia.acg.service.delivery;

import com.dangjia.acg.api.BasicsStorefrontAPI;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.delivery.DjDeliveryReturnSlipDTO;
import com.dangjia.acg.dto.delivery.SupplierSettlementManagementDTO;
import com.dangjia.acg.mapper.delivery.DjDeliveryReturnSlipMapper;
import com.dangjia.acg.modle.delivery.DjDeliveryReturnSlip;
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


    /**
     * 供应商结算管理
     * @param supId
     * @param applyState
     * @return
     */
    public ServerResponse querySupplierSettlementManagement(String supId, PageDTO pageDTO, Integer applyState) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<SupplierSettlementManagementDTO> supplierSettlementManagementDTOS = djDeliveryReturnSlipMapper.querySupplierSettlementManagement(supId, applyState);
            PageInfo pageResult = new PageInfo(supplierSettlementManagementDTOS);
            supplierSettlementManagementDTOS.forEach(supplierSettlementManagementDTO -> {
                Storefront storefront = basicsStorefrontAPI.querySingleStorefrontById(supplierSettlementManagementDTO.getShopId());
                supplierSettlementManagementDTO.setShopId(storefront.getId());
                supplierSettlementManagementDTO.setStorefrontName(storefront.getStorefrontName());
                supplierSettlementManagementDTO.setContact(storefront.getContact());
                supplierSettlementManagementDTO.setStorekeeperName(storefront.getStorekeeperName());
            });
            if(supplierSettlementManagementDTOS.size()<=0)
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(),ServerCode.NO_DATA.getDesc());
            return ServerResponse.createBySuccess("查询成功",pageResult);
        } catch (Exception e) {
            logger.error("查询失败",e);
            return ServerResponse.createByErrorMessage("查询失败: "+e);
        }
    }


    /**
     * 供应商结算管理
     * @param supId
     * @param shopId
     * @param applyState
     * @return
     */
    public ServerResponse querySupplierSettlementList(String supId, String shopId, Integer applyState) {
        try {
            List<DjDeliveryReturnSlip> djDeliveryReturnSlips = djDeliveryReturnSlipMapper.querySupplierSettlementList(supId, shopId, applyState);
            if(djDeliveryReturnSlips.size()>0)
                return ServerResponse.createBySuccess("查询成功",djDeliveryReturnSlips);
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(),ServerCode.NO_DATA.getDesc());
        } catch (Exception e) {
            logger.error("查询失败",e);
            return ServerResponse.createByErrorMessage("查询失败: "+e);
        }
    }
}
