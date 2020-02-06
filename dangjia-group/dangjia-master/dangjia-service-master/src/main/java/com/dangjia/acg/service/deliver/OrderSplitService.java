package com.dangjia.acg.service.deliver;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.BasicsStorefrontAPI;
import com.dangjia.acg.api.data.ForMasterAPI;
import com.dangjia.acg.api.supplier.DjSupApplicationProductAPI;
import com.dangjia.acg.api.supplier.DjSupplierAPI;
import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.enums.AppType;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.JsmsUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.deliver.*;
import com.dangjia.acg.mapper.complain.IComplainMapper;
import com.dangjia.acg.mapper.delivery.IOrderItemMapper;
import com.dangjia.acg.mapper.delivery.IOrderSplitItemMapper;
import com.dangjia.acg.mapper.delivery.IOrderSplitMapper;
import com.dangjia.acg.mapper.delivery.ISplitDeliverMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.house.IWarehouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.product.IMasterStorefrontProductMapper;
import com.dangjia.acg.mapper.repair.IMendOrderMapper;
import com.dangjia.acg.modle.complain.Complain;
import com.dangjia.acg.modle.deliver.OrderItem;
import com.dangjia.acg.modle.deliver.OrderSplit;
import com.dangjia.acg.modle.deliver.OrderSplitItem;
import com.dangjia.acg.modle.deliver.SplitDeliver;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.house.Warehouse;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.product.DjBasicsProductTemplate;
import com.dangjia.acg.modle.repair.MendOrder;
import com.dangjia.acg.modle.storefront.Storefront;
import com.dangjia.acg.modle.supplier.DjSupApplicationProduct;
import com.dangjia.acg.modle.supplier.DjSupplier;
import com.dangjia.acg.service.config.ConfigMessageService;
import com.dangjia.acg.service.product.MasterStorefrontService;
import com.dangjia.acg.util.StringTool;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.*;

/**
 * author: Ronalcheng
 * Date: 2018/12/5 0005
 * Time: 14:30
 * <p>
 * 生成发货单 发货操作类
 */
@Service
public class OrderSplitService {
    private static Logger logger = LoggerFactory.getLogger(OrderSplitService.class);
    @Autowired
    private ISplitDeliverMapper splitDeliverMapper;
    @Autowired
    private IOrderSplitMapper orderSplitMapper;
    @Autowired
    private IOrderSplitItemMapper orderSplitItemMapper;
    @Autowired
    private IHouseMapper houseMapper;
    @Autowired
    private IMemberMapper memberMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IWarehouseMapper warehouseMapper;
    @Autowired
    private ForMasterAPI forMasterAPI;
    @Autowired
    private IMendOrderMapper mendOrderMapper;
    @Autowired
    private IComplainMapper complainMapper;
    @Autowired
    private ConfigMessageService configMessageService;
    @Autowired
    private DjSupplierAPI djSupplierAPI ;
    @Autowired
    private MasterStorefrontService masterStorefrontService;
    @Autowired
    private DjSupApplicationProductAPI djSupApplicationProductAPI;
    @Autowired
    private IOrderItemMapper iOrderItemMapper;


    /**
     * 修改 供应商结算状态
     * id 供应商结算id
     * deliveryFee 配送费用
     * applyMoney   供应商申请结算的价格
     */
    public ServerResponse setSplitDeliver(SplitDeliver splitDeliver) {
        try {
            if (!StringUtils.isNoneBlank(splitDeliver.getId()))
                return ServerResponse.createByErrorMessage("id 不能为null");
            SplitDeliver srcSplitDeliver = splitDeliverMapper.selectByPrimaryKey(splitDeliver.getId());
            if (srcSplitDeliver == null)
                return ServerResponse.createByErrorMessage("无供应商结算单");
            //配送状态（0待发货,1已发待收货,2已收货,3取消,4部分收）
            if (!(srcSplitDeliver.getShippingState() == 2 || srcSplitDeliver.getShippingState() == 4 || srcSplitDeliver.getShippingState() == 6))
                return ServerResponse.createByErrorMessage("当前为未收货状态，不能申请结算");
            Example example = new Example(Complain.class);
            example.createCriteria()
                    .andEqualTo(Complain.COMPLAIN_TYPE, 4)
                    .andEqualTo(Complain.BUSINESS_ID, splitDeliver.getId())
                    .andEqualTo(Complain.STATUS, 0);
            List list = complainMapper.selectByExample(example);
            if (list.size() > 0) {
                return ServerResponse.createByErrorMessage("请勿重复提交申请！");
            }
            srcSplitDeliver.setDeliveryFee(splitDeliver.getDeliveryFee());
//            srcSplitDeliver.setApplyMoney(splitDeliver.getApplyMoney());
            srcSplitDeliver.setApplyState(0);//供应商申请结算的状态0申请中；1不通过；2通过
            splitDeliverMapper.updateByPrimaryKeySelective(srcSplitDeliver);
            return ServerResponse.createBySuccessMessage("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }


    /**
     * 供应商发货
     */
    public ServerResponse sentSplitDeliver(String splitDeliverId) {
        try {
            SplitDeliver splitDeliver = splitDeliverMapper.selectByPrimaryKey(splitDeliverId);
            System.out.println(splitDeliver);
            if (splitDeliver.getShippingState() == 6) {
                return ServerResponse.createBySuccessMessage("材料员已撤回！");
            }
            splitDeliver.setSendTime(new Date());
            splitDeliver.setShippingState(1);//已发待收
            splitDeliverMapper.updateByPrimaryKeySelective(splitDeliver);
            House house = houseMapper.selectByPrimaryKey(splitDeliver.getHouseId());
            //业主
            configMessageService.addConfigMessage(null, AppType.ZHUANGXIU, house.getMemberId(), "0", "供应商发货", String.format
                    (DjConstants.PushMessage.YZ_F_001, house.getHouseName()), "");
            return ServerResponse.createBySuccessMessage("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }

    /**
     * 工匠拒绝收货，打回供应商待发货
     */
    public ServerResponse rejectionSplitDeliver(String splitDeliverId) {
        try {
            SplitDeliver splitDeliver = splitDeliverMapper.selectByPrimaryKey(splitDeliverId);
            if (splitDeliver.getShippingState() == 0) {
                return ServerResponse.createBySuccessMessage("您已经拒收！");
            }
            splitDeliver.setSendTime(null);
            splitDeliver.setShippingState(0);//已发待收
            splitDeliverMapper.updateByPrimaryKeySelective(splitDeliver);
            return ServerResponse.createBySuccessMessage("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }

    /**
     * 发货单明细
     */
    public ServerResponse splitDeliverDetail(String splitDeliverId) {
        try {
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            SplitDeliver splitDeliver = splitDeliverMapper.selectByPrimaryKey(splitDeliverId);
            SplitDeliverDetailDTO detailDTO = new SplitDeliverDetailDTO();
            detailDTO.setHouseId(splitDeliver.getHouseId());
            detailDTO.setNumber(splitDeliver.getNumber());
            detailDTO.setShipName(splitDeliver.getShipName());
            detailDTO.setShipAddress(splitDeliver.getShipAddress());
            detailDTO.setShippingState(splitDeliver.getShippingState());
            detailDTO.setApplyState(splitDeliver.getApplyState());
            detailDTO.setShipMobile(splitDeliver.getShipMobile());
            Member sup = memberMapper.selectByPrimaryKey(splitDeliver.getSupervisorId());//管家
            detailDTO.setSupMobile(sup.getMobile());
            detailDTO.setSupName(sup.getName());
            detailDTO.setMemo(splitDeliver.getMemo());
            detailDTO.setReason(splitDeliver.getReason());
            detailDTO.setTotalAmount(0.0);
            detailDTO.setApplyMoney(0.0);

            Example example = new Example(OrderSplitItem.class);
            example.createCriteria().andEqualTo(OrderSplitItem.SPLIT_DELIVER_ID, splitDeliverId);
            example.orderBy(OrderSplitItem.CATEGORY_ID).desc();
            List<OrderSplitItem> orderSplitItemList = orderSplitItemMapper.selectByExample(example);
            List<OrderSplitItemDTO> orderSplitItemDTOS = new ArrayList<>();
            House house = houseMapper.selectByPrimaryKey(splitDeliver.getHouseId());
            for (OrderSplitItem orderSplitItem : orderSplitItemList) {
                if (orderSplitItem.getReceive() == null) {
                    orderSplitItem.setReceive(0D);
                }
                DjBasicsProductTemplate product=forMasterAPI.getProduct(house.getCityId(), orderSplitItem.getProductId());
                OrderSplitItemDTO orderSplitItemDTO = new OrderSplitItemDTO();
                orderSplitItemDTO.setProductName(product.getName());
                orderSplitItemDTO.setNum(orderSplitItem.getNum());
                orderSplitItemDTO.setCost(product.getCost());
                orderSplitItemDTO.setSupCost(orderSplitItem.getSupCost());
                orderSplitItemDTO.setUnitName(orderSplitItem.getUnitName());
                orderSplitItemDTO.setAskCount(orderSplitItem.getAskCount());
                orderSplitItemDTO.setShopCount(orderSplitItem.getShopCount());
                orderSplitItemDTO.setImage(address + product.getImage());
                orderSplitItemDTO.setReceive(orderSplitItem.getReceive());
                //orderSplitItemDTO.setBrandSeriesName(forMasterAPI.brandSeriesName(house.getCityId(), orderSplitItem.getProductId()));
                orderSplitItemDTO.setBrandName(forMasterAPI.brandName(house.getCityId(), orderSplitItem.getProductId()));
                if (splitDeliver.getShippingState() == 2 || splitDeliver.getShippingState() == 4 || splitDeliver.getShippingState() == 5) {
                    orderSplitItemDTO.setTotalPrice(new BigDecimal(orderSplitItem.getSupCost() * orderSplitItem.getReceive()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                } else {
                    orderSplitItemDTO.setTotalPrice(new BigDecimal(orderSplitItem.getSupCost() * orderSplitItem.getNum()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                }
                orderSplitItemDTOS.add(orderSplitItemDTO);
                detailDTO.setApplyMoney(new BigDecimal(detailDTO.getApplyMoney() + orderSplitItemDTO.getTotalPrice()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                detailDTO.setTotalAmount(new BigDecimal(detailDTO.getTotalAmount() + (orderSplitItem.getSupCost() * orderSplitItem.getNum())).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
            }
            detailDTO.setSize(orderSplitItemList.size());
            detailDTO.setOrderSplitItemDTOS(orderSplitItemDTOS);
            return ServerResponse.createBySuccess("查询成功", detailDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 供应商端发货列表
     * shipState  0待发货,1已发待收货,2已收货,3取消,4部分收,5已结算
     */
    public ServerResponse splitDeliverList(String supplierId, int shipState) {
        Example example = new Example(SplitDeliver.class);
        if (shipState == 2) {
            example.createCriteria().andEqualTo(SplitDeliver.SUPPLIER_ID, supplierId)
                    .andCondition(" shipping_state in(2,4) ");
            example.orderBy(SplitDeliver.CREATE_DATE).desc();
            example.orderBy(SplitDeliver.APPLY_STATE).asc();
        } else {
            example.createCriteria().andEqualTo(SplitDeliver.SUPPLIER_ID, supplierId)
                    .andEqualTo(SplitDeliver.SHIPPING_STATE, shipState);
            example.orderBy(SplitDeliver.CREATE_DATE).desc();
            example.orderBy(SplitDeliver.APPLY_STATE).asc();
        }
        List<SplitDeliver> splitDeliverList = splitDeliverMapper.selectByExample(example);
        for (SplitDeliver splitDeliver : splitDeliverList) {
            System.out.println(splitDeliver);
        }
        return ServerResponse.createBySuccess("查询成功", splitDeliverList);
    }


    /**
     * 撤回供应商待发货的订单（整单撤回）
     */
    public ServerResponse withdrawSupplier(String orderSplitId) {
        try {
            //将发货单设置为撤回状态
            SplitDeliver splitDeliver=splitDeliverMapper.selectByPrimaryKey(orderSplitId);
            if (splitDeliver.getShippingState()==1) {
                return ServerResponse.createBySuccessMessage("供应商已发货！");
            }
            splitDeliver.setShippingState(6);
            splitDeliverMapper.updateByPrimaryKeySelective(splitDeliver);
            return ServerResponse.createBySuccessMessage("撤回成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("撤回失败");
        }
    }

    /**
     * 发送供应商
     * 分发不同供应商
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse sentSupplier(String orderSplitId, String splitItemList,String cityId,String userId,String installName,
                                       String installMobile, String deliveryName, String deliveryMobile) {
        try {
            //判断店铺是否存在
            Storefront storefront = masterStorefrontService.getStorefrontByUserId(userId, cityId);
            if (storefront == null) {
                return ServerResponse.createByErrorMessage("不存在店铺信息，请先维护店铺信息");
            }

            String address = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class);
            OrderSplit orderSplit = orderSplitMapper.selectByPrimaryKey(orderSplitId);
            House house = houseMapper.selectByPrimaryKey(orderSplit.getHouseId());
            Member supervisor = memberMapper.getSupervisor(house.getId());//管家
            Member member = memberMapper.selectByPrimaryKey(house.getMemberId());//工匠
            Map<String,String > list=new HashMap();
            JSONArray arr = JSONArray.parseArray(splitItemList);
            for (int i = 0; i < arr.size(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                String id = obj.getString("id");
                String supplierId = obj.getString("supplierId");
                DjSupplier djSupplier = djSupplierAPI.queryDjSupplierByPass(supplierId);
                if (djSupplier.getIsNonPlatformSupperlier() == 1) {
                    continue;  //非平台供应商
                } else {
                    list.put(djSupplier.getTelephone(),"1");
                    //配送状态（0待发货,1已发待收货,2已收货,3取消,4部分收,5已结算,6材料员撤回(只待发货才能撤回)）
                    OrderSplitItem orderSplitItem = orderSplitItemMapper.selectByPrimaryKey(id);
                    Example example = new Example(SplitDeliver.class);
                    example.createCriteria().andEqualTo(SplitDeliver.HOUSE_ID, orderSplit.getHouseId()).andEqualTo(SplitDeliver.SUPPLIER_ID, supplierId)
                            .andEqualTo(SplitDeliver.SHIPPING_STATE, 0).andEqualTo(SplitDeliver.ORDER_SPLIT_ID, orderSplitId);
                    List<SplitDeliver> splitDeliverList = splitDeliverMapper.selectByExample(example);
                    SplitDeliver splitDeliver;
                    if (splitDeliverList.size() > 0) {
                        splitDeliver = splitDeliverList.get(0);
                        //配送状态（0待发货,1已发待收货,2已收货,3取消,4部分收,5已结算,6材料员撤回(只待发货才能撤回)）
                        // 是非平台
                        if (splitDeliver.getShippingState() == 2 && djSupplier.getIsNonPlatformSupperlier() == 1) {
                            splitDeliver.setInstallMobile(installMobile);// 安装人号码
                            splitDeliver.setInstallName(installName);//安装人姓名
                        }
                    } else {
                        example = new Example(SplitDeliver.class);
                        splitDeliver = new SplitDeliver();
                        splitDeliver.setNumber(orderSplit.getNumber() + "00" + splitDeliverMapper.selectCountByExample(example));//发货单号
                        splitDeliver.setHouseId(house.getId());
                        splitDeliver.setOrderSplitId(orderSplitId);
                        splitDeliver.setTotalAmount(0.0);
                        splitDeliver.setDeliveryFee(0.0);
                        splitDeliver.setApplyMoney(0.0);
                        splitDeliver.setShipName(member.getNickName() == null ? member.getName() : member.getNickName());
                        splitDeliver.setShipMobile(member.getMobile());
                        splitDeliver.setShipAddress(house.getHouseName());
                        splitDeliver.setSupplierId(supplierId);//供应商id
                        splitDeliver.setSupplierTelephone(djSupplier.getTelephone());//供应商联系电话
                        splitDeliver.setSupplierName(djSupplier.getName());//供应商供应商名称
                        splitDeliver.setSupervisorId(supervisor.getId());//管家id
                        splitDeliver.setSubmitTime(new Date());
                        splitDeliver.setSupState(0);
                        splitDeliver.setShippingState(0);//待发货状态
                        splitDeliver.setApplyState(null);
                        splitDeliver.setCityId(cityId);//城市id
                        splitDeliver.setStorefrontId(storefront.getId());//店铺id
                        //判断是非平台供应商
                        if (djSupplier.getIsNonPlatformSupperlier() != null) {
                            if (djSupplier.getIsNonPlatformSupperlier() == 1) {
                                splitDeliver.setDeliveryName(deliveryName);//送货人姓名
                                splitDeliver.setDeliveryMobile(deliveryMobile);//送货人号码
                            }
                        }
                        splitDeliverMapper.insert(splitDeliver);
                    }
                    DjSupApplicationProduct djSupApplicationProduct = djSupApplicationProductAPI.getDjSupApplicationProduct(house.getCityId(), supplierId, orderSplitItem.getProductId());
                    orderSplitItem.setSupCost(djSupApplicationProduct.getPrice());//供应价
                    orderSplitItem.setSplitDeliverId(splitDeliver.getId());//发货单id
                    orderSplitItemMapper.updateByPrimaryKeySelective(orderSplitItem);
                    //发货单总额
                    splitDeliver.setTotalAmount(djSupApplicationProduct.getPrice() * orderSplitItem.getNum() + splitDeliver.getTotalAmount());//累计供应商价总价
                    splitDeliverMapper.updateByPrimaryKeySelective(splitDeliver);
                }
                orderSplit.setApplyStatus(2);//2通过(发给供应商)
                orderSplitMapper.updateByPrimaryKeySelective(orderSplit);//修改要货单信息

                //给供应商发送短信
                for (String key : list.keySet()) {
                    JsmsUtil.sendSupplier(key, address + "submitNumber?cityId=" + house.getCityId());
                }
                return ServerResponse.createBySuccessMessage("操作成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
        return null;
    }



    /**
     * 取消(打回)
     * 返回数量
     */
    public ServerResponse cancelOrderSplit(String orderSplitId) {
        try {
            OrderSplit orderSplit = orderSplitMapper.selectByPrimaryKey(orderSplitId);
            Example example = new Example(OrderSplitItem.class);
            example.createCriteria().andEqualTo(OrderSplitItem.ORDER_SPLIT_ID, orderSplit.getId());
            List<OrderSplitItem> orderSplitItemList = orderSplitItemMapper.selectByExample(example);
            for (OrderSplitItem orderSplitItem : orderSplitItemList) {
                Warehouse warehouse = warehouseMapper.getByProductId(orderSplitItem.getProductId(), orderSplit.getHouseId());
                if (warehouse != null) {
                    warehouse.setAskCount(warehouse.getAskCount() - orderSplitItem.getNum());
                    warehouseMapper.updateByPrimaryKeySelective(warehouse);
                }
            }

            orderSplit.setApplyStatus(3);
            orderSplitMapper.updateByPrimaryKey(orderSplit);

            if (!CommonUtil.isEmpty(orderSplit.getMendNumber())) {
                MendOrder mendOrder = mendOrderMapper.selectByPrimaryKey(orderSplit.getMendNumber());
                mendOrder.setState(2);//不通过取消
                mendOrderMapper.updateByPrimaryKeySelective(mendOrder);
            }
            return ServerResponse.createBySuccessMessage("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }

    /**
     * 发货单打回
     */
    public ServerResponse cancelSplitDeliver(String splitDeliverId) {
        try {
            //将发货单设置为撤回状态
            SplitDeliver splitDeliver=splitDeliverMapper.selectByPrimaryKey(splitDeliverId);
            if (splitDeliver.getShippingState()==6) {
                Example example = new Example(OrderSplitItem.class);
                example.createCriteria().andEqualTo(OrderSplitItem.SPLIT_DELIVER_ID, splitDeliver.getId());
                List<OrderSplitItem> orderSplitItemList = orderSplitItemMapper.selectByExample(example);
                for (OrderSplitItem orderSplitItem : orderSplitItemList) {
                    Warehouse warehouse = warehouseMapper.getByProductId(orderSplitItem.getProductId(), splitDeliver.getHouseId());
                    if (warehouse != null) {
                        warehouse.setAskCount(warehouse.getAskCount() - orderSplitItem.getNum());
                        warehouseMapper.updateByPrimaryKeySelective(warehouse);
                    }
                }
                splitDeliver.setShippingState(3);//取消发货单
                splitDeliverMapper.updateByPrimaryKeySelective(splitDeliver);
            }
            return ServerResponse.createBySuccessMessage("打回成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("打回成功");
        }
    }

    /**
     * 发货任务--货单列表--分发任务页面
     */
    public ServerResponse orderSplitItemList(String orderSplitId) {
        try {
            Map<String,Object> resultMap=new HashMap();
            //1.查询对应的要货单号，判断是否已分发过供应商，若已分发，则返回给提示
            OrderSplit orderSplit=orderSplitMapper.selectByPrimaryKey(orderSplitId);
            if(orderSplit!=null&&orderSplit.getApplyStatus()==2){
                return ServerResponse.createByErrorMessage("此要货单已分发过供应商，请勿重复操作");
            }
            resultMap.put("memberId",orderSplit.getMemberId());//要货人ID
            resultMap.put("memberName",orderSplit.getMemberName());//要货人姓名
            resultMap.put("mobile",orderSplit.getMobile());//要货人联系方式
            //2.查询对应的需要分发的要货单明细
            List<OrderSplitItemDTO> orderSplitItemList=orderSplitItemMapper.getSplitOrderItemBySplitOrderId(orderSplitId);
            if(orderSplitItemList!=null&&orderSplitItemList.size()>0){
                String address = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class);
                for (OrderSplitItemDTO sd:orderSplitItemList){
                  //2.1查询当前订单对应的购买总量，已要货量
                    Map<String,Object> countItemMap=iOrderItemMapper.searchCountItemByInfo(orderSplit.getStorefrontId(),orderSplit.getAddressId(),orderSplit.getHouseId(),sd.getProductId());
                    sd.setShopCount((Double)countItemMap.get("shopCount"));//购买总数
                    sd.setAskCount((Double)countItemMap.get("askCount"));//已要货总数
                    sd.setImageUrl(StringTool.getImageSingle(sd.getImage(),address));
                    //2.2查询当前商品对应的供应商，及销售总价(对此店铺供过货的供应商)
                    List<Map<String,Object>> supplierIdlist = splitDeliverMapper.getsupplierByProduct(orderSplit.getStorefrontId(),sd.getProductId());
                    if(supplierIdlist==null||supplierIdlist.size()<=0){
                        //若未查到线上可发货的供应商，则查询非平台供应商给到页面选择
                        supplierIdlist=splitDeliverMapper.queryNonPlatformSupplier();
                    }
                    sd.setSupplierIdlist(supplierIdlist);
                }
            }
            resultMap.put("orderSplitItemList",orderSplitItemList);//要货单明细表
            return ServerResponse.createBySuccess("查询成功", resultMap);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 材料员看房子列表
     */
    public ServerResponse getHouseList(String userId,String cityId,PageDTO pageDTO, String likeAddress,String startDate, String endDate) {
        try {
            //通过缓存查询店铺信息
           Storefront storefront= masterStorefrontService.getStorefrontByUserId(userId,cityId);
            if(storefront==null)
            {
                return ServerResponse.createByErrorMessage("不存在店铺信息，请先维护店铺信息");
            }
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
//        List<House> houseList = houseMapper.selectAll();
            List<DeliverHouseDTO> deliverHouseDTOList = houseMapper.getHouseAddrssByAddress(storefront.getId(),cityId,likeAddress,startDate,endDate);
            PageInfo pageResult = new PageInfo(deliverHouseDTOList);
            pageResult.setList(deliverHouseDTOList);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            logger.error("查询失败：",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 根据收货地址，查询对应的要货单列表
     */
    public ServerResponse getOrderSplitList(String userId,String cityId,PageDTO pageDTO,String addressId,String houseId,String storefrontId) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<OrderSplitDTO> orderSplistList=orderSplitMapper.searchOrderSplistByAddressId(addressId,houseId,storefrontId);
            PageInfo pageResult = new PageInfo(orderSplistList);
            pageResult.setList(orderSplistList);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            logger.error("查询失败",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }
}
