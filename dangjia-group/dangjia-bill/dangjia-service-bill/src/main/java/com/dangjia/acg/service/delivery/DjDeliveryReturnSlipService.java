package com.dangjia.acg.service.delivery;

import com.dangjia.acg.api.BasicsStorefrontAPI;
import com.dangjia.acg.api.supplier.DjSupApplicationProductAPI;
import com.dangjia.acg.api.supplier.DjSupplierAPI;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.delivery.*;
import com.dangjia.acg.dto.supplier.DjSupplierDTO;
import com.dangjia.acg.dto.supplier.SupplierLikeDTO;
import com.dangjia.acg.mapper.delivery.DjDeliveryReturnSlipMapper;
import com.dangjia.acg.modle.complain.Complain;
import com.dangjia.acg.modle.storefront.Storefront;
import com.dangjia.acg.modle.supplier.DjSupplier;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


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

    @Autowired
    private ConfigUtil configUtil;


    /**
     * 供货任务列表
     *
     * @param pageDTO
     * @param searchKey
     * @param invoiceStatus 0:全部  0,0:代发货  0,1:已发货待收货  0,2:已收货  0,4:部分收货  1,0:待退货  1,1:已确认  1,2:已结算(已退货)  1,3:拒绝退货
     * @return
     */
    public ServerResponse querySupplyTaskList(PageDTO pageDTO, String userId, String cityId, String searchKey, String invoiceStatus) {
        try {
            DjSupplier djSupplier = djSupplierAPI.querySingleDjSupplier(userId, cityId);
            if(null==djSupplier)
                return ServerResponse.createByErrorMessage("暂无店铺信息");
            PageInfo pageResult=null;
            List<DjDeliveryReturnSlipDTO> djDeliveryReturnSlipDTOS = null;
            if(!CommonUtil.isEmpty(invoiceStatus)&&invoiceStatus.equals("0")){
                PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
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
                    PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
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
                    PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
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
    public ServerResponse querySupplierSettlementManagement(String userId, String cityId, PageDTO pageDTO, Integer applyState, String searchKey) {
        try {
            DjSupplier djSupplier = djSupplierAPI.querySingleDjSupplier(userId, cityId);
            if(null==djSupplier)
                return ServerResponse.createByErrorMessage("暂无店铺信息");
            List<Storefront> storefronts = basicsStorefrontAPI.queryLikeSingleStorefront(searchKey);
            if(storefronts.size()>0) {
                PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
                List<SupplierSettlementManagementDTO> supplierSettlementManagementDTOS = djDeliveryReturnSlipMapper.querySupplierSettlementManagement(djSupplier.getId(), applyState, cityId, storefronts);
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
            }else{
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            }
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
            Storefront storefront = basicsStorefrontAPI.querySingleStorefrontById(shopId);
            djDeliveryReturnSlipDTOS.forEach(djDeliveryReturnSlipDTO -> {
                djDeliveryReturnSlipDTO.setShopName(storefront.getStorefrontName());
                djDeliveryReturnSlipDTO.setStorekeeperName(storefront.getStorekeeperName());
                djDeliveryReturnSlipDTO.setMobile(storefront.getMobile());
            });
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
                return ServerResponse.createByErrorMessage("暂无供应商信息");
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
            String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            DjSupplier djSupplier = djSupplierAPI.querySingleDjSupplier(userId, cityId);
            if(null==djSupplier)
                return ServerResponse.createByErrorMessage("暂无店铺信息");
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<SupplyDimensionDTO> supplyDimensionDTOS = djSupApplicationProductAPI.queryDjSupSupplierProductList(djSupplier.getId(), searchKey);
            PageInfo pageResult = new PageInfo(supplyDimensionDTOS);
            supplyDimensionDTOS.forEach(supplyDimensionDTO -> {
                List<BuyersDimensionDTO> buyersDimensionDTOS = djDeliveryReturnSlipMapper.querySupplyDimensionList(djSupplier.getId(), supplyDimensionDTO.getProductId(), cityId);
                supplyDimensionDTO.setBuyersDimensionDTOS(buyersDimensionDTOS);
                supplyDimensionDTO.setImage(imageAddress+supplyDimensionDTO.getImage());
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
            List<SupplierStoreDimensionDTO> supplierStoreDimensionDTOS=new ArrayList<>();
            for (Storefront storefront : storefronts) {
                List<SupplierStoreDimensionDTO> supplierStoreDimensionDTOS1 = djDeliveryReturnSlipMapper.querySupplierStoreDimensionList(djSupplier.getId(), storefront.getId(), cityId);
                supplierStoreDimensionDTOS1.forEach(supplierStoreDimensionDTO -> {
                    supplierStoreDimensionDTO.setStorefrontName(storefront.getStorefrontName());
                    supplierStoreDimensionDTO.setStorekeeperName(storefront.getStorekeeperName());
                    supplierStoreDimensionDTO.setMobile(storefront.getMobile());
                });
                supplierStoreDimensionDTOS.addAll(supplierStoreDimensionDTOS1);
            }
            if (supplierStoreDimensionDTOS.size() <= 0)
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            PageInfo pageResult = new PageInfo(supplierStoreDimensionDTOS);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            logger.error("查询失败", e);
            return ServerResponse.createByErrorMessage("查询失败: " + e);
        }
    }


    /**
     *供应商店铺维度详情列表
     * @param supId
     * @param shopId
     * @param searchKey
     * @param cityId
     * @return
     */
    public ServerResponse querySupplierStoreDimensionDetailList(PageDTO pageDTO,String supId, String shopId, String searchKey, String cityId) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<BuyersDimensionDetailsDTO> buyersDimensionDetailsDTOS = djDeliveryReturnSlipMapper.querySupplierStoreDimensionDetailList(supId, shopId,cityId, searchKey);
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
     **********************************店铺统计模块**********************************************************
     * 店铺利润统计-供应商维度
     * @param pageDTO
     * @param userId
     * @param cityId
     * @param searchKey
     * @return
     */
    public ServerResponse supplierDimension(PageDTO pageDTO, String userId, String cityId, String searchKey) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            Storefront storefront=basicsStorefrontAPI.queryStorefrontByUserID(userId,cityId);
            if(storefront==null)
            {
                return ServerResponse.createByErrorMessage("不存在店铺信息，请先维护店铺信息");
            }
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<SupplierLikeDTO > supplierList=djSupplierAPI.queryLikeSupplier(searchKey);

            List<StoreSupplierDimensionDTO> StoreSupplierDimensionDTOList=new  ArrayList<StoreSupplierDimensionDTO>();
            if(supplierList==null)
            {
                return ServerResponse.createByErrorMessage("模糊查询没有检索到数据");
            }
            for (SupplierLikeDTO supplierLikeDTO  : supplierList) {
                List<StoreSupplierDimensionDTO> list=djDeliveryReturnSlipMapper.supplierDimension(supplierLikeDTO .getId(), storefront.getId(),cityId);
                list.forEach(StoreSupplierDimensionDTO -> {
                    StoreSupplierDimensionDTO.setName(supplierLikeDTO.getName()); //供应商名称
                    StoreSupplierDimensionDTO.setCheckPeople(supplierLikeDTO.getCheckPeople());  //联系人
                    StoreSupplierDimensionDTO.setTelephone(supplierLikeDTO.getTelephone());//联系号码
                });
                StoreSupplierDimensionDTOList.addAll(list);
            }
            if (StoreSupplierDimensionDTOList.size() <= 0)
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            PageInfo pageResult = new PageInfo(StoreSupplierDimensionDTOList);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            logger.error("店铺利润统计供应商维度异常", e);
            return ServerResponse.createByErrorMessage("店铺利润统计供应商维度异常: " + e);
        }
    }

    /**
     * 店铺利润统计-商品维度
     * @param pageDTO
     * @param userId
     * @param cityId
     * @param searchKey
     * @return
     */
    public ServerResponse storefrontProductDimension(PageDTO pageDTO,String userId, String cityId, String searchKey) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            Storefront storefront = basicsStorefrontAPI.queryStorefrontByUserID(userId, cityId);
            if(storefront==null)
            {
                return ServerResponse.createByErrorMessage("不存在店铺信息，请先维护店铺信息");
            }
            List<Map> bListMap =new ArrayList<>();
            List<StoreSupplyDimensionDTO> list=djDeliveryReturnSlipMapper.storefrontProductDimension(storefront.getId(),searchKey);
            if (list.size() <= 0)
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            for (StoreSupplyDimensionDTO storeSupplyDimensionDTO:list ) {
                Map brandMap= BeanUtils.beanToMap(storeSupplyDimensionDTO);
                String prodTemplateId= storeSupplyDimensionDTO.getProdTemplateId();
                List<StoreSupplyDimensionDetailDTO> listDetail=djDeliveryReturnSlipMapper.storefrontProductDimensionDetail(storefront.getId(),prodTemplateId,cityId);
                bListMap.add(brandMap);
                brandMap.put("StoreSupplyDimensionDetailDTO",listDetail);
            }
            PageInfo pageResult = new PageInfo(bListMap);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            logger.error("店铺利润统计-商品维度异常", e);
            return ServerResponse.createByErrorMessage("店铺利润统计-商品维度异常: " + e);
        }
    }

    /**
     * 店铺利润统计-买家维度
     * @param pageDTO
     * @param userId
     * @param cityId
     * @param searchKey
     * @return
     */
    public ServerResponse sellerDimension(PageDTO pageDTO, String userId, String cityId, String searchKey) {
        try {

            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            Storefront storefront = basicsStorefrontAPI.queryStorefrontByUserID(userId, cityId);
            if(storefront==null)
            {
                return ServerResponse.createByErrorMessage("不存在店铺信息，请先维护店铺信息");
            }
            List<StoreBuyersDimensionDTO> list=djDeliveryReturnSlipMapper.sellerDimension(storefront.getId(),cityId,searchKey);
            PageInfo pageResult = new PageInfo(list);
            if (list.size() <= 0)
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            logger.error("店铺利润统计-卖家维度异常", e);
            return ServerResponse.createByErrorMessage("店铺利润统计-卖家维度异常: " + e);
        }
    }

    /**
     *店铺利润统计-查看供应详情
     * @param pageDTO
     * @param userId
     * @param houseId
     * @param searchKey
     * @param cityId
     * @return
     */
    public ServerResponse supplyDetails(PageDTO pageDTO, String userId, String houseId, String searchKey, String cityId) {
        try {

            Storefront storefront = basicsStorefrontAPI.queryStorefrontByUserID(userId, cityId);
            if(storefront==null)
            {
                return ServerResponse.createByErrorMessage("不存在店铺信息，请先维护店铺信息");
            }

            return null;
        } catch (Exception e) {
            logger.error("店铺利润统计-查看供应详情异常", e);
            return ServerResponse.createByErrorMessage("店铺利润统计-查看供应详情异常: " + e);
        }
    }

    /**
     *店铺利润统计-查看货单详情
     * @param pageDTO
     * @param userId
     * @param houseId
     * @param searchKey
     * @param cityId
     * @return
     */
    public ServerResponse shippingDetails(PageDTO pageDTO, String userId, String houseId, String searchKey, String cityId) {
        try {
            Storefront storefront = basicsStorefrontAPI.queryStorefrontByUserID(userId, cityId);
            if(storefront==null)
            {
                return ServerResponse.createByErrorMessage("不存在店铺信息，请先维护店铺信息");
            }

            return null;
        } catch (Exception e) {
            logger.error("店铺利润统计-查看货单详情异常", e);
            return ServerResponse.createByErrorMessage("店铺利润统计-查看货单详情异常: " + e);
        }
    }
}
