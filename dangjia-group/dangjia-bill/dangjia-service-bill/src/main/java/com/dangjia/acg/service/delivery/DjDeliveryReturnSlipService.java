package com.dangjia.acg.service.delivery;

import com.dangjia.acg.api.BasicsStorefrontAPI;
import com.dangjia.acg.api.supplier.DjSupApplicationProductAPI;
import com.dangjia.acg.api.supplier.DjSupplierAPI;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dto.delivery.*;
import com.dangjia.acg.mapper.delivery.DjDeliveryReturnSlipMapper;
import com.dangjia.acg.modle.storefront.Storefront;
import com.dangjia.acg.modle.supplier.DjSupplier;
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

    @Autowired
    private DjSupApplicationProductAPI djSupApplicationProductAPI;

    @Autowired
    private DjSupplierAPI djSupplierAPI;

    /**
     * 供货任务列表
     *
     * @param pageDTO
     * @param searchKey
     * @param invoiceStatus 0:全部  0,0:代发货  0,1:待收货  0,2:已收货  1,0:待退货  1,1:已确认  1,2:已结算  1,3:拒绝退货
     * @return
     */
    public ServerResponse querySupplyTaskList(PageDTO pageDTO, String userId, String cityId, String searchKey, String invoiceStatus) {
        try {
            DjSupplier djSupplier = djSupplierAPI.querySingleDjSupplier(userId, cityId);
            if(null==djSupplier)
                return ServerResponse.createByErrorMessage("暂无店铺信息");
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            PageInfo pageResult=null;
            List<DjDeliveryReturnSlipDTO> djDeliveryReturnSlipDTOS = null;
            if(!CommonUtil.isEmpty(invoiceStatus)&&invoiceStatus.equals("0")){
                djDeliveryReturnSlipDTOS = djDeliveryReturnSlipMapper.querySupplyTaskList(djSupplier.getId(), searchKey, cityId);
                pageResult = new PageInfo(djDeliveryReturnSlipDTOS);
                djDeliveryReturnSlipDTOS.forEach(djDeliveryReturnSlipDTO -> {
                    Storefront storefront = basicsStorefrontAPI.querySingleStorefrontById(djDeliveryReturnSlipDTO.getShopId());
                    if(null!=storefront) {
                        djDeliveryReturnSlipDTO.setShopName(storefront.getStorefrontName());
                        djDeliveryReturnSlipDTO.setStorekeeperName(storefront.getStorekeeperName());
                    }
                });
            }else if(!CommonUtil.isEmpty(invoiceStatus)){
                String[] split = invoiceStatus.split(",");
                if(split[0].equals("0")){
                    djDeliveryReturnSlipDTOS = djDeliveryReturnSlipMapper.querySupplyDeliverTaskList(djSupplier.getId(), searchKey, split[1], cityId);
                    pageResult = new PageInfo(djDeliveryReturnSlipDTOS);
                    djDeliveryReturnSlipDTOS.forEach(djDeliveryReturnSlipDTO -> {
                        Storefront storefront = basicsStorefrontAPI.querySingleStorefrontById(djDeliveryReturnSlipDTO.getShopId());
                        if(null!=storefront) {
                            djDeliveryReturnSlipDTO.setShopName(storefront.getStorefrontName());
                            djDeliveryReturnSlipDTO.setStorekeeperName(storefront.getStorekeeperName());
                        }
                    });
                }else if(split[0].equals("1")){
                    djDeliveryReturnSlipDTOS = djDeliveryReturnSlipMapper.querySupplyRepairTaskList(djSupplier.getId(), searchKey, split[1], cityId);
                    pageResult = new PageInfo(djDeliveryReturnSlipDTOS);
                    djDeliveryReturnSlipDTOS.forEach(djDeliveryReturnSlipDTO -> {
                        Storefront storefront = basicsStorefrontAPI.querySingleStorefrontById(djDeliveryReturnSlipDTO.getShopId());
                        if(storefront!=null) {
                            djDeliveryReturnSlipDTO.setShopName(storefront.getStorefrontName());
                            djDeliveryReturnSlipDTO.setStorekeeperName(storefront.getStorekeeperName());
                        }
                    });
                }
            }else{
                return ServerResponse.createByErrorMessage("查询失败: invoiceStatus不能为空");
            }
            if(djDeliveryReturnSlipDTOS.size() <= 0)
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            logger.error("查询失败", e);
            return ServerResponse.createByErrorMessage("查询失败: " + e);
        }
    }


    /**
     * 处理供货任务
     *
     * @param id
     * @return
     */
    public ServerResponse setDeliveryTask(String id, Integer invoiceType, Integer shippingState) {
        try {
            if (djDeliveryReturnSlipMapper.setDeliveryTask(id, invoiceType,shippingState) > 0)
                return ServerResponse.createBySuccessMessage("操作成功");
            return ServerResponse.createByErrorMessage("操作失败");
        } catch (Exception e) {
            logger.error("操作失败", e);
            return ServerResponse.createByErrorMessage("操作失败: " + e);
        }
    }


    /**
     * 供应商结算管理
     * @param userId
     * @param cityId
     * @param pageDTO
     * @param applyState
     * @return
     */
    public ServerResponse querySupplierSettlementManagement(String userId, String cityId, PageDTO pageDTO, Integer applyState) {
        try {
            DjSupplier djSupplier = djSupplierAPI.querySingleDjSupplier(userId, cityId);
            if(null==djSupplier)
                return ServerResponse.createByErrorMessage("暂无店铺信息");
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<SupplierSettlementManagementDTO> supplierSettlementManagementDTOS = djDeliveryReturnSlipMapper.querySupplierSettlementManagement(djSupplier.getId(), applyState, cityId);
            PageInfo pageResult = new PageInfo(supplierSettlementManagementDTOS);
            supplierSettlementManagementDTOS.forEach(supplierSettlementManagementDTO -> {
                Storefront storefront = basicsStorefrontAPI.querySingleStorefrontById(supplierSettlementManagementDTO.getShopId());
                supplierSettlementManagementDTO.setShopId(storefront.getId());
                supplierSettlementManagementDTO.setStorefrontName(storefront.getStorefrontName());
                supplierSettlementManagementDTO.setMobile(storefront.getMobile());
                supplierSettlementManagementDTO.setStorekeeperName(storefront.getStorekeeperName());
            });
            if (supplierSettlementManagementDTOS.size() <= 0)
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            logger.error("查询失败", e);
            return ServerResponse.createByErrorMessage("查询失败: " + e);
        }
    }


    /**
     * 供应商结算列表
     *
     * @param supId
     * @param shopId
     * @param applyState
     * @return
     */
    public ServerResponse querySupplierSettlementList(String supId, String shopId, Integer applyState) {
        try {
            List<DjDeliveryReturnSlipDTO> djDeliveryReturnSlipDTOS = djDeliveryReturnSlipMapper.querySupplierSettlementList(supId, shopId, applyState);
            if (djDeliveryReturnSlipDTOS.size() > 0)
                return ServerResponse.createBySuccess("查询成功", djDeliveryReturnSlipDTOS);
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        } catch (Exception e) {
            logger.error("查询失败", e);
            return ServerResponse.createByErrorMessage("查询失败: " + e);
        }
    }


    /**
     * 供应商结算买家维度列表
     * @param pageDTO
     * @param userId
     * @param cityId
     * @param searchKey
     * @return
     */
    public ServerResponse queryBuyersDimensionList(PageDTO pageDTO, String userId, String cityId, String searchKey) {
        try {
            DjSupplier djSupplier = djSupplierAPI.querySingleDjSupplier(userId, cityId);
            if(null==djSupplier)
                return ServerResponse.createByErrorMessage("暂无店铺信息");
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<BuyersDimensionDTO> buyersDimensionDTOS = djDeliveryReturnSlipMapper.queryBuyersDimensionList(djSupplier.getId(), searchKey,cityId);
            PageInfo pageResult = new PageInfo(buyersDimensionDTOS);
            if (buyersDimensionDTOS.size() <= 0)
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            logger.error("查询失败", e);
            return ServerResponse.createByErrorMessage("查询失败: " + e);
        }
    }


    /**
     * 供应商查看买家维度详情列表
     * @param pageDTO
     * @param supId
     * @param houseId
     * @return
     */
    public ServerResponse queryBuyersDimensionDetailList(PageDTO pageDTO, String supId, String houseId, String searchKey, String cityId) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<BuyersDimensionDetailsDTO> buyersDimensionDetailsDTOS = djDeliveryReturnSlipMapper.queryBuyersDimensionDetailList(supId, houseId, searchKey, cityId);
            PageInfo pageResult = new PageInfo(buyersDimensionDetailsDTOS);
            if (buyersDimensionDetailsDTOS.size() <= 0)
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            logger.error("查询失败", e);
            return ServerResponse.createByErrorMessage("查询失败: " + e);
        }
    }


    /**
     * 供应商商品维度列表
     * @param pageDTO
     * @param userId
     * @param cityId
     * @param searchKey
     * @return
     */
    public ServerResponse querySupplyDimensionList(PageDTO pageDTO, String userId, String cityId, String searchKey) {
        try {
            DjSupplier djSupplier = djSupplierAPI.querySingleDjSupplier(userId, cityId);
            if(null==djSupplier)
                return ServerResponse.createByErrorMessage("暂无店铺信息");
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<SupplyDimensionDTO> supplyDimensionDTOS = djSupApplicationProductAPI.queryDjSupSupplierProductList(djSupplier.getId(), searchKey);
            PageInfo pageResult = new PageInfo(supplyDimensionDTOS);
            supplyDimensionDTOS.forEach(supplyDimensionDTO -> {
                List<BuyersDimensionDTO> buyersDimensionDTOS = djDeliveryReturnSlipMapper.querySupplyDimensionList(djSupplier.getId(), supplyDimensionDTO.getProductId(), cityId);
                supplyDimensionDTO.setBuyersDimensionDTOS(buyersDimensionDTOS);
            });
            if (supplyDimensionDTOS.size() <= 0)
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            logger.error("查询失败", e);
            return ServerResponse.createByErrorMessage("查询失败: " + e);
        }
    }


    /**
     * 供应商店铺维度列表
     * @param pageDTO
     * @param userId
     * @param cityId
     * @param searchKey
     * @return
     */
    public ServerResponse querySupplierStoreDimensionList(PageDTO pageDTO, String userId, String cityId, String searchKey) {
        try {
            DjSupplier djSupplier = djSupplierAPI.querySingleDjSupplier(userId, cityId);
            if(null==djSupplier)
                return ServerResponse.createByErrorMessage("暂无店铺信息");
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<Storefront> storefronts = basicsStorefrontAPI.queryLikeSingleStorefront(searchKey);
            PageInfo pageResult = new PageInfo(storefronts);
            List<SupplierStoreDimensionDTO> supplierStoreDimensionDTOS=null;
            for (Storefront storefront : storefronts) {
                supplierStoreDimensionDTOS = djDeliveryReturnSlipMapper.querySupplierStoreDimensionList(djSupplier.getId(), storefront.getId(),cityId);
                supplierStoreDimensionDTOS.forEach(supplierStoreDimensionDTO -> {
                    supplierStoreDimensionDTO.setStorefrontName(storefront.getStorefrontName());
                    supplierStoreDimensionDTO.setStorekeeperName(storefront.getStorekeeperName());
                });
            }
            if (supplierStoreDimensionDTOS.size() <= 0)
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            return ServerResponse.createBySuccess("查询成功", supplierStoreDimensionDTOS);
        } catch (Exception e) {
            logger.error("查询失败", e);
            return ServerResponse.createByErrorMessage("查询失败: " + e);
        }
    }
}
