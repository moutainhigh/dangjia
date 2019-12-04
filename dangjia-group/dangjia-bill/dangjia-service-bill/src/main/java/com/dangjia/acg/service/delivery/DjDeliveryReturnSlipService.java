package com.dangjia.acg.service.delivery;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
import com.dangjia.acg.dto.supplier.SupplierLikeDTO;
import com.dangjia.acg.mapper.delivery.*;
import com.dangjia.acg.mapper.refund.IBillMendMaterialMapper;
import com.dangjia.acg.modle.deliver.Order;
import com.dangjia.acg.modle.deliver.OrderItem;
import com.dangjia.acg.modle.deliver.OrderSplitItem;
import com.dangjia.acg.modle.deliver.SplitDeliver;
import com.dangjia.acg.modle.repair.MendDeliver;
import com.dangjia.acg.modle.repair.MendMateriel;
import com.dangjia.acg.modle.storefront.Storefront;
import com.dangjia.acg.modle.supplier.DjSupplier;
import com.dangjia.acg.mapper.repair.IBillMendDeliverMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
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
    @Autowired
    private BillDjDeliverSplitDeliverMapper billDjDeliverSplitDeliverMapper;
    @Autowired
    private BillDjDeliverOrderSplitItemMapper billDjDeliverOrderSplitItemMapper;
    @Autowired
    private IBillDjDeliverOrderItemMapper ibillDjDeliverOrderItemMapper;
    @Autowired
    private IBillDjDeliverOrderMapper ibillDjDeliverOrderMapper;
    @Autowired
    private IBillMendDeliverMapper iBillMendDeliverMapper;
    @Autowired
    private IBillMendMaterialMapper iBillMendMaterialMapper;

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
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse setDeliveryTask(String id, Integer invoiceType, Integer shippingState, String jsonStr, String reasons) {
        try {
            if(shippingState==1){
                SplitDeliver splitDeliver = billDjDeliverSplitDeliverMapper.selectByPrimaryKey(id);
                Example example=new Example(OrderSplitItem.class);
                example.createCriteria().andEqualTo(OrderSplitItem.ORDER_SPLIT_ID,splitDeliver.getOrderSplitId());
                List<OrderSplitItem> orderSplitItems = billDjDeliverOrderSplitItemMapper.selectByExample(example);
                for (OrderSplitItem orderSplitItem : orderSplitItems) {
                    example=new Example(OrderItem.class);
                    example.createCriteria().andEqualTo(OrderItem.PRODUCT_ID,orderSplitItem.getProductId())
                            .andEqualTo(OrderItem.HOUSE_ID,splitDeliver.getHouseId())
                            .andEqualTo(OrderItem.ORDER_STATUS,2);
                    List<OrderItem> orderItems = ibillDjDeliverOrderItemMapper.selectByExample(example);
                    for (OrderItem orderItem : orderItems) {
                        if(orderItem.getShopCount()-orderItem.getAskCount()-orderItem.getReturnCount()>0){
                            continue;
                        }else {
                            orderItem.setOrderStatus("3");
                            ibillDjDeliverOrderItemMapper.updateByPrimaryKeySelective(orderItem);
                            Order order=new Order();
                            order.setId(orderItem.getId());
                            order.setOrderStatus("3");
                            ibillDjDeliverOrderMapper.updateByPrimaryKeySelective(order);
                        }
                    }
                }
            }
            if (djDeliveryReturnSlipMapper.setDeliveryTask(id, invoiceType,shippingState) > 0) {
                JSONArray jsonArr = JSONArray.parseArray(jsonStr);
                jsonArr.forEach(o ->{
                    JSONObject obj = (JSONObject) o;
                    Double supActualCount = obj.getDouble("supActualCount");
                    String repairMendMaterielId = obj.getString("repairMendMaterielId");
                    MendMateriel mendMateriel=new MendMateriel();
                    mendMateriel.setId(repairMendMaterielId);
                    mendMateriel.setSupActualCount(supActualCount);
                    iBillMendMaterialMapper.updateByPrimaryKeySelective(mendMateriel);
                });
                if(!CommonUtil.isEmpty(reasons)){
                    MendDeliver mendDeliver=new MendDeliver();
                    mendDeliver.setId(id);
                    mendDeliver.setReason(reasons);
                    iBillMendDeliverMapper.updateByPrimaryKeySelective(mendDeliver);
                }
                return ServerResponse.createBySuccessMessage("操作成功");
            }
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
    public ServerResponse supplierDimension(PageDTO pageDTO, Date startTime, Date endTime, String userId, String cityId, String searchKey) {
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
                list.forEach(storeSupplierDimensionDTO -> {
                    storeSupplierDimensionDTO.setName(supplierLikeDTO.getName()); //供应商名称
                    storeSupplierDimensionDTO.setCheckPeople(supplierLikeDTO.getCheckPeople());  //联系人
                    storeSupplierDimensionDTO.setTelephone(supplierLikeDTO.getTelephone());//联系号码
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
                String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
                storeSupplyDimensionDTO.setImageDetail(imageAddress+storeSupplyDimensionDTO.getImage());
                Map brandMap= BeanUtils.beanToMap(storeSupplyDimensionDTO);
                String prodTemplateId= storeSupplyDimensionDTO.getProdTemplateId();
                List<StoreSupplyDimensionDetailDTO> listDetail=djDeliveryReturnSlipMapper.storefrontProductDimensionDetail(storefront.getId(),prodTemplateId,cityId);
                bListMap.add(brandMap);
                brandMap.put("listDetail",listDetail);
            }
            PageInfo pageResult = new PageInfo(bListMap);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            logger.error("店铺利润统计-商品维度异常", e);
            return ServerResponse.createByErrorMessage("店铺利润统计-商品维度异常: " + e);
        }
    }

    /**
     * 店铺利润统计-买家维度列表
     * @param pageDTO
     * @param userId
     * @param cityId
     * @param searchKey
     * @return
     */
    public ServerResponse sellerDimension(PageDTO pageDTO, String userId, String cityId, String searchKey) {
        try {
            Storefront storefront = basicsStorefrontAPI.queryStorefrontByUserID(userId, cityId);
            if(storefront==null)
            {
                return ServerResponse.createByErrorMessage("不存在店铺信息，请先维护店铺信息");
            }
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<StoreBuyersDimensionDTO> StoreBuyersDimensionDTOlist=djDeliveryReturnSlipMapper.sellerDimension(storefront.getId(),cityId,searchKey);
            if (StoreBuyersDimensionDTOlist.size() <= 0)
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            for (StoreBuyersDimensionDTO storeBuyersDimensionDTO :StoreBuyersDimensionDTOlist ) {
                List<StoreBuyersDimensionDetailDTO> detaillist= djDeliveryReturnSlipMapper.sellerDimensionDetail(storeBuyersDimensionDTO.getHouseId());
                storeBuyersDimensionDTO.setDetaillist(detaillist);
            }
            PageInfo pageResult = new PageInfo(StoreBuyersDimensionDTOlist);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            logger.error("店铺利润统计-卖家维度异常", e);
            return ServerResponse.createByErrorMessage("店铺利润统计-卖家维度异常: " + e);
        }
    }




    /**
     *店铺利润统计-查看买家订单详情
     * @param pageDTO
     * @return
     */
    public ServerResponse shippingDetails(PageDTO pageDTO,String userId,String cityId, String orderSplitId) {
        try {
            String address = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<StoreBuyersDimensionOrderDetailDTO> list=djDeliveryReturnSlipMapper.shippingDetails(orderSplitId);
            PageInfo pageResult = new PageInfo(list);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            logger.error("店铺利润统计-查看买家订单详情异常", e);
            return ServerResponse.createByErrorMessage("店铺利润统计-查看买家订单详情异常: " + e);
        }
    }

    /**
     * 店铺利润统计-查看买家-发货单详情
     * @param request
     * @param pageDTO
     * @param splitDeliverId
     * @return
     */
    public ServerResponse sellerSplitDeliverDetails(HttpServletRequest request, PageDTO pageDTO,String userId, String cityId, String splitDeliverId) {
        try {
            Storefront storefront = basicsStorefrontAPI.queryStorefrontByUserID(userId, cityId);
            if(storefront==null)
            {
                return ServerResponse.createByErrorMessage("不存在店铺信息，请先维护店铺信息");
            }
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            String address = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            List<StoreSellerSplitDeliverDetailsDTO> list=djDeliveryReturnSlipMapper.sellerSplitDeliverDetails(splitDeliverId,storefront.getId());
            list.forEach(storeSellerSplitDeliverDetailsDTO->{
                storeSellerSplitDeliverDetailsDTO.setImage(address+storeSellerSplitDeliverDetailsDTO.getImage());
                    }
            );
            PageInfo pageResult = new PageInfo(list);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            logger.error("店铺利润统计-查看买家-发货单详情异常", e);
            return ServerResponse.createByErrorMessage("店铺利润统计-查看买家-发货单详情异常: " + e);
        }
    }


    /**
     *店铺利润统计-供应商供应详情
     * @param request
     * @param pageDTO
     * @param userId
     * @param cityId
     * @param searchKey
     * @return
     */
    public ServerResponse supplierDimensionSupplyDetails(HttpServletRequest request, PageDTO pageDTO, String userId, String cityId, String searchKey) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            Storefront storefront = basicsStorefrontAPI.queryStorefrontByUserID(userId, cityId);
            if(storefront==null)
            {
                return ServerResponse.createByErrorMessage("不存在店铺信息，请先维护店铺信息");
            }
            List<SupplierDimensionSupplyDTO> list  = djDeliveryReturnSlipMapper.supplierDimensionSupplyDetails(storefront.getId(),cityId,searchKey);
            PageInfo pageResult = new PageInfo(list);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            logger.error("店铺利润统计-供应商供应详情异常", e);
            return ServerResponse.createByErrorMessage("店铺利润统计-供应商供应详情异常: " + e);
        }
    }

    /**
     *店铺利润统计-供应商货单详情
     * @param request
     * @param pageDTO
     * @param userId
     * @param cityId
     * @return
     */
    public ServerResponse supplierDimensionOrderDetails(HttpServletRequest request, PageDTO pageDTO, String userId, String cityId,String houseId) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            Storefront storefront = basicsStorefrontAPI.queryStorefrontByUserID(userId, cityId);
            if(storefront==null)
            {
                return ServerResponse.createByErrorMessage("不存在店铺信息，请先维护店铺信息");
            }
            List<SupplierDimensionOrderDetailDTO> list=djDeliveryReturnSlipMapper.supplierDimensionOrderDetails(houseId,storefront.getId(),cityId);
            PageInfo pageResult = new PageInfo(list);
            if (list.size() <= 0)
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            logger.error("店铺利润统计-供应商货单详情异常", e);
            return ServerResponse.createByErrorMessage("店铺利润统计-供应商货单详情异常: " + e);
        }
    }

    /**
     *店铺利润统计-供应商商品详情
     * @param request
     * @param pageDTO
     * @param userId
     * @param cityId
     * @param orderSplitId
     * @return
     */
    public ServerResponse supplierDimensionGoodsDetails(HttpServletRequest request, PageDTO pageDTO, String userId, String cityId, String orderSplitId) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            Storefront storefront = basicsStorefrontAPI.queryStorefrontByUserID(userId, cityId);
            if(storefront==null)
            {
                return ServerResponse.createByErrorMessage("不存在店铺信息，请先维护店铺信息");
            }
            String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            List<SupplierDimensionGoodsDetailDTO> list=djDeliveryReturnSlipMapper.supplierDimensionGoodsDetails(orderSplitId,storefront.getId());
            for (SupplierDimensionGoodsDetailDTO supplierDimensionGoodsDetailDTO :list ) {
                supplierDimensionGoodsDetailDTO.setImageDetail(imageAddress+supplierDimensionGoodsDetailDTO.getImage());
            }
            PageInfo pageResult = new PageInfo(list);
            if (list.size() <= 0)
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            logger.error("店铺利润统计-供应商商品详情异常", e);
            return ServerResponse.createByErrorMessage("店铺利润统计-供应商商品详情异常: " + e);
        }
    }
}
