package com.dangjia.acg.service.pay;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.api.StorefrontConfigAPI;
import com.dangjia.acg.api.actuary.BudgetMaterialAPI;
import com.dangjia.acg.api.actuary.BudgetWorkerAPI;
import com.dangjia.acg.api.data.ForMasterAPI;
import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.enums.AppType;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.common.util.MathUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.activity.ActivityRedPackRecordDTO;
import com.dangjia.acg.dto.actuary.BudgetLabelDTO;
import com.dangjia.acg.dto.actuary.BudgetLabelGoodsDTO;
import com.dangjia.acg.dto.actuary.ShopGoodsDTO;
import com.dangjia.acg.dto.basics.ProductDTO;
import com.dangjia.acg.dto.house.HouseOrderDetailDTO;
import com.dangjia.acg.dto.pay.PaymentDTO;
import com.dangjia.acg.dto.pay.SafeTypeDTO;
import com.dangjia.acg.dto.pay.UpgradeSafeDTO;
import com.dangjia.acg.dto.product.ShoppingCartDTO;
import com.dangjia.acg.dto.product.ShoppingCartListDTO;
import com.dangjia.acg.mapper.account.IMasterAccountFlowRecordMapper;
import com.dangjia.acg.mapper.activity.IActivityRedPackRecordMapper;
import com.dangjia.acg.mapper.core.*;
import com.dangjia.acg.mapper.delivery.*;
import com.dangjia.acg.mapper.house.*;
import com.dangjia.acg.mapper.member.ICustomerRecordMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.pay.IBusinessOrderMapper;
import com.dangjia.acg.mapper.pay.IMasterSupplierPayOrderMapper;
import com.dangjia.acg.mapper.pay.IPayOrderMapper;
import com.dangjia.acg.mapper.product.IMasterStorefrontMapper;
import com.dangjia.acg.mapper.product.IMasterStorefrontProductMapper;
import com.dangjia.acg.mapper.product.IShoppingCartMapper;
import com.dangjia.acg.mapper.repair.IChangeOrderMapper;
import com.dangjia.acg.mapper.repair.IMendMaterialMapper;
import com.dangjia.acg.mapper.repair.IMendOrderMapper;
import com.dangjia.acg.mapper.repair.IMendWorkerMapper;
import com.dangjia.acg.mapper.safe.IWorkerTypeSafeMapper;
import com.dangjia.acg.mapper.safe.IWorkerTypeSafeOrderMapper;
import com.dangjia.acg.mapper.supplier.IMasterSupplierMapper;
import com.dangjia.acg.mapper.worker.IInsuranceMapper;
import com.dangjia.acg.modle.account.AccountFlowRecord;
import com.dangjia.acg.modle.activity.ActivityRedPackRecord;
import com.dangjia.acg.modle.actuary.BudgetMaterial;
import com.dangjia.acg.modle.brand.Brand;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.core.HouseWorkerOrder;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.deliver.Order;
import com.dangjia.acg.modle.deliver.OrderItem;
import com.dangjia.acg.modle.deliver.OrderSplit;
import com.dangjia.acg.modle.deliver.OrderSplitItem;
import com.dangjia.acg.modle.house.*;
import com.dangjia.acg.modle.member.CustomerRecord;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.order.DeliverOrderAddedProduct;
import com.dangjia.acg.modle.pay.BusinessOrder;
import com.dangjia.acg.modle.pay.PayOrder;
import com.dangjia.acg.modle.product.BasicsGoods;
import com.dangjia.acg.modle.product.ShoppingCart;
import com.dangjia.acg.modle.repair.ChangeOrder;
import com.dangjia.acg.modle.repair.MendOrder;
import com.dangjia.acg.modle.safe.WorkerTypeSafe;
import com.dangjia.acg.modle.safe.WorkerTypeSafeOrder;
import com.dangjia.acg.modle.storefront.Storefront;
import com.dangjia.acg.modle.storefront.StorefrontProduct;
import com.dangjia.acg.modle.supplier.DjSupplier;
import com.dangjia.acg.modle.supplier.DjSupplierPayOrder;
import com.dangjia.acg.modle.worker.Insurance;
import com.dangjia.acg.service.account.MasterAccountFlowRecordService;
import com.dangjia.acg.service.acquisition.MasterCostAcquisitionService;
import com.dangjia.acg.service.config.ConfigMessageService;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.dangjia.acg.service.design.HouseDesignPayService;
import com.dangjia.acg.service.repair.MendOrderCheckService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import sun.swing.StringUIClientPropertyKey;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * author: Ronalcheng
 * Date: 2018/11/7 0007
 * Time: 14:38
 */
@Service
public class PaymentService {
    @Autowired
    private IMasterSupplierPayOrderMapper masterSupplierPayOrderMapper;
    @Autowired
    private IActivityRedPackRecordMapper activityRedPackRecordMapper;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private IInsuranceMapper insuranceMapper;
    @Autowired
    private IHouseFlowMapper houseFlowMapper;
    @Autowired
    private IWorkerTypeMapper workerTypeMapper;

    @Autowired
    private IMemberMapper memberMapper;
    @Autowired
    private IHouseMapper houseMapper;
    @Autowired
    private IWorkerTypeSafeMapper workerTypeSafeMapper;
    @Autowired
    private IWorkerTypeSafeOrderMapper workerTypeSafeOrderMapper;

    @Autowired
    private MasterAccountFlowRecordService masterAccountFlowRecordService;

    @Autowired
    private IMasterBasicsGoodsMapper iMasterBasicsGoodsMapper;
    @Autowired
    private IMasterBrandMapper iMasterBrandMapper;
    @Autowired
    private IBusinessOrderMapper businessOrderMapper;
    @Autowired
    private IPayOrderMapper payOrderMapper;
    @Autowired
    private IHouseWorkerOrderMapper houseWorkerOrderMapper;
    @Autowired
    private IHouseAccountsMapper houseAccountsMapper;
    @Autowired
    private ForMasterAPI forMasterAPI;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IMendOrderMapper mendOrderMapper;
    @Autowired
    private IMendMaterialMapper mendMaterialMapper;
    @Autowired
    private IMendWorkerMapper mendWorkerMapper;
    @Autowired
    private IOrderMapper orderMapper;
    @Autowired
    private IOrderItemMapper orderItemMapper;
    @Autowired
    private IHouseExpendMapper houseExpendMapper;
    @Autowired
    private ConfigMessageService configMessageService;
    @Autowired
    private IWarehouseMapper warehouseMapper;//仓库
    @Autowired
    private IWarehouseDetailMapper warehouseDetailMapper;//流水
    @Autowired
    private BudgetMaterialAPI budgetMaterialAPI;
    @Autowired
    private BudgetWorkerAPI budgetWorkerAPI;
    @Autowired
    private IHouseDistributionMapper iHouseDistributionMapper;
    @Autowired
    private IChangeOrderMapper changeOrderMapper;
    @Autowired
    private IOrderSplitMapper orderSplitMapper;
    @Autowired
    private IOrderSplitItemMapper orderSplitItemMapper;
    @Autowired
    private MendOrderCheckService mendOrderCheckService;
    @Autowired
    private StorefrontConfigAPI storefrontConfigAPI;
    @Autowired
    private IShoppingCartMapper iShoppingCartMapper;
    @Autowired
    private IMasterDeliverOrderAddedProductMapper masterDeliverOrderAddedProductMapper;
    @Autowired
    private IProductChangeOrderMapper productChangeOrderMapper;
    @Autowired
    private HouseDesignPayService houseDesignPayService;
    @Autowired
    private CraftsmanConstructionService constructionService;
    @Autowired
    private ICustomerRecordMapper customerRecordMapper;

    @Autowired
    private IMasterBudgetMapper iMasterBudgetMapper;
    @Autowired
    private MasterCostAcquisitionService masterCostAcquisitionService;
    @Autowired
    private PayService payService;
    @Autowired
    private IMasterSupplierPayOrderMapper iMasterSupplierPayOrderMapper;
    @Autowired
    private IMasterSupplierMapper iMaterSupplierMapper;
    @Autowired
    private IMasterStorefrontMapper iMasterStorefrontMapper;

    @Autowired
    private IMasterStorefrontProductMapper iMasterStorefrontProductMapper;
    @Autowired
    private IMasterAccountFlowRecordMapper iMasterAccountFlowRecordMapper;
    private Logger logger = LoggerFactory.getLogger(PaymentService.class);

    @Transactional(rollbackFor = Exception.class)
    public ServerResponse setServersSuccess(String businessOrderId,BigDecimal money,String image ) {
        try {
            BusinessOrder businessOrder = businessOrderMapper.selectByPrimaryKey(businessOrderId);
            businessOrder.setPayPrice(money);
            businessOrder.setImage(image);
            businessOrderMapper.updateByPrimaryKeySelective(businessOrder);

            ServerResponse serverResponse=payService.getPOSSign(businessOrder.getNumber());
            if(!serverResponse.isSuccess()){
                return serverResponse;
            }

            return setServersSuccess((String)serverResponse.getResultObj());
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ServerResponse.createByErrorMessage("支付回调异常");
        }
    }

    /**
     * 服务器回调
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse setServersSuccess(String payOrderId) {
        try {
            PayOrder payOrder = payOrderMapper.selectByPrimaryKey(payOrderId);
            if (payOrder.getState() == 2) {
                return ServerResponse.createBySuccessMessage("支付成功");
            }
            Example example = new Example(BusinessOrder.class);
            example.createCriteria().andEqualTo(BusinessOrder.NUMBER, payOrder.getBusinessOrderNumber());
            List<BusinessOrder> businessOrderList = businessOrderMapper.selectByExample(example);
            if (businessOrderList.size() == 0) {
                return ServerResponse.createByErrorMessage("业务订单不存在");
            }
            BusinessOrder businessOrder = businessOrderList.get(0);
            businessOrder.setPayOrderNumber(payOrder.getNumber());
            businessOrder.setState(3);//已支付
            businessOrderMapper.updateByPrimaryKeySelective(businessOrder);
            payOrder.setState(2);//已支付
            payOrderMapper.updateByPrimaryKeySelective(payOrder);
            String payState = payOrder.getPayState();
            if (businessOrder.getType() == 1) {
                //精算购
                this.payWorkerType(businessOrder, payState);
            }else if (businessOrder.getType() == 2) {
                //业主购
                this.mendOrder(businessOrder, payState);
            } else if (businessOrder.getType() == 3) {
                //充值
                this.recharge(businessOrder, payState);
            }else if (businessOrder.getType() == 9) {//工人保险
                Insurance insurance = insuranceMapper.selectByPrimaryKey(businessOrder.getTaskId());
                if(insurance.getStartDate()==null){
                    insurance.setStartDate(new Date());
                }
                if(insurance.getEndDate()==null){
                    insurance.setEndDate(new Date());
                }
                insurance.setNumber(businessOrder.getNumber());
                insurance.setEndDate(DateUtil.addDateYear(insurance.getEndDate(), 1));
                insuranceMapper.updateByPrimaryKeySelective(insurance);
                return ServerResponse.createBySuccessMessage("支付成功");
            } else if (businessOrder.getType() == 5) {//验房分销
                HouseDistribution houseDistribution = iHouseDistributionMapper.selectByPrimaryKey(businessOrder.getTaskId());
                houseDistribution.setNumber(businessOrder.getNumber());//业务订单号
                houseDistribution.setState(1);//已支付Example example = new Example(CustomerRecord.class);
                iHouseDistributionMapper.updateByPrimaryKeySelective(houseDistribution);
                example =new Example(CustomerRecord.class);
                example.createCriteria().andEqualTo(CustomerRecord.MEMBER_ID, houseDistribution.getOpenid());
                example.orderBy(CustomerRecord.CREATE_DATE).desc();
                CustomerRecord customerRecord=customerRecordMapper.selectByExample(example).get(0);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(houseDistribution.getCreateDate());
                calendar.add(Calendar.DAY_OF_MONTH, +1);//+1今天的时间加一天
                customerRecord.setDescribes((houseDistribution.getType() == 1 ? "验房分销，" : "验房预约，")
                        + houseDistribution.getInfo()
                        + (houseDistribution.getState() == 1 ? "，已支付" : houseDistribution.getState() == 0 ? "，未支付" : "，预约"));
                customerRecord.setRemindTime(calendar.getTime());
                customerRecordMapper.updateByPrimaryKeySelective(customerRecord);
                return ServerResponse.createBySuccessMessage("支付成功");
            } else if (businessOrder.getType() == 7) {
                houseDesignPayService.setPaySuccess(businessOrder);
            }
            if(!CommonUtil.isEmpty(businessOrder.getHouseId())) {
                HouseExpend houseExpend = houseExpendMapper.getByHouseId(businessOrder.getHouseId());
                houseExpend.setTolMoney(houseExpend.getTolMoney() + businessOrder.getTotalPrice().doubleValue());//总金额
                houseExpend.setPayMoney(houseExpend.getPayMoney() + businessOrder.getPayPrice().doubleValue());//总支付
                houseExpend.setDisMoney(houseExpend.getDisMoney() + businessOrder.getDiscountsPrice().doubleValue());//总优惠
                example = new Example(Warehouse.class);
                example.createCriteria().andEqualTo(Warehouse.HOUSE_ID, businessOrder.getHouseId());
                List<Warehouse> warehouseList = warehouseMapper.selectByExample(example);
                houseExpend.setMaterialKind(warehouseList.size());//材料种类
                houseExpendMapper.updateByPrimaryKeySelective(houseExpend);
            }
            return ServerResponse.createBySuccessMessage("支付成功");
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ServerResponse.createByErrorMessage("支付回调异常");
        }
    }
    /**
     * web支付成功回调
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse setWebPaySuccess( String businessOrderNumber) {

        Map<String, Object> returnMap = new HashMap<>();
        try {
            Example examplePayOrder = new Example(PayOrder.class);
            examplePayOrder.createCriteria().andEqualTo(PayOrder.BUSINESS_ORDER_NUMBER, businessOrderNumber);
            List<PayOrder> payOrderList = payOrderMapper.selectByExample(examplePayOrder);
            if (payOrderList.size() == 0) {
                return ServerResponse.createByErrorMessage("支付订单不存在");
            }
            PayOrder payOrder = payOrderList.get(0);
            if (payOrder.getState() == 2) {//已支付
                returnMap.put("name", "当家装修担保平台");
                returnMap.put("businessOrderNumber", businessOrderNumber);
                returnMap.put("price", payOrder.getPrice());
                return ServerResponse.createBySuccess("支付成功", returnMap);
            }else{
                returnMap.put("name", "当家装修担保平台");
                returnMap.put("businessOrderNumber", businessOrderNumber);
                returnMap.put("price", payOrder.getPrice());
                return ServerResponse.createByErrorMessage("未支付成功");
            }
        } catch (Exception e) {
            returnMap.put("name", "当家装修担保平台");
            returnMap.put("businessOrderNumber", businessOrderNumber);
            returnMap.put("price", 0);
            return ServerResponse.createBySuccess("支付回调异常", returnMap);
        }
    }
    /**
     * 移动端支付成功回调
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse setPaySuccess(String userToken, String businessOrderNumber) {

        Map<String, Object> returnMap = new HashMap<>();
        try {
            Example examplePayOrder = new Example(PayOrder.class);
            examplePayOrder.createCriteria().andEqualTo(PayOrder.BUSINESS_ORDER_NUMBER, businessOrderNumber);
            List<PayOrder> payOrderList = payOrderMapper.selectByExample(examplePayOrder);
            if (payOrderList.size() == 0) {
                return ServerResponse.createByErrorMessage("支付订单不存在");
            }
            PayOrder payOrder = payOrderList.get(0);
            //开发回调
            setServersSuccess(payOrder.getId());
            if (payOrder.getState() == 2) {//已支付
                returnMap.put("name", "当家装修担保平台");
                returnMap.put("businessOrderNumber", businessOrderNumber);
                returnMap.put("price", payOrder.getPrice());
                return ServerResponse.createBySuccess("支付成功", returnMap);
            }
            Example example = new Example(BusinessOrder.class);
            example.createCriteria().andEqualTo(BusinessOrder.NUMBER, businessOrderNumber);
            List<BusinessOrder> businessOrderList = businessOrderMapper.selectByExample(example);
            if (businessOrderList.size() == 0) {
                return ServerResponse.createByErrorMessage("业务订单不存在");
            }
            BusinessOrder businessOrder = businessOrderList.get(0);
            if (businessOrder.getType() == 5) {//验房分销
                HouseDistribution houseDistribution = iHouseDistributionMapper.selectByPrimaryKey(businessOrder.getTaskId());
                returnMap.put("name", "当家装修担保平台");
                returnMap.put("businessOrderNumber", businessOrderNumber);
                returnMap.put("price", houseDistribution.getPrice());
                return ServerResponse.createBySuccess("支付成功", returnMap);
            }
            returnMap.put("name", "当家装修担保平台");
            returnMap.put("businessOrderNumber", businessOrderNumber);
            returnMap.put("price", businessOrder.getPayPrice());
            return ServerResponse.createBySuccess("支付成功", returnMap);
        } catch (Exception e) {
            returnMap.put("name", "当家装修担保平台");
            returnMap.put("businessOrderNumber", businessOrderNumber);
            returnMap.put("price", 0);
            return ServerResponse.createBySuccess("支付回调异常", returnMap);
        }
    }

    private void recharge(BusinessOrder businessOrder, String payState){
        DjSupplierPayOrder djSupplierPayOrder = masterSupplierPayOrderMapper.selectByPrimaryKey(businessOrder.getTaskId());
        djSupplierPayOrder.setState(1);
        masterSupplierPayOrderMapper.updateByPrimaryKeySelective(djSupplierPayOrder);
        this.setSurplusMoney(djSupplierPayOrder);
    }
    /**
     * 处理补货补人工
     */
    private void mendOrder(BusinessOrder businessOrder, String payState) {
        try {
            Order order= orderMapper.selectByPrimaryKey(businessOrder.getTaskId());
            HouseExpend houseExpend = houseExpendMapper.getByHouseId(businessOrder.getHouseId());
            House house = houseMapper.selectByPrimaryKey(businessOrder.getHouseId());

            Example example = new Example(MendOrder.class);
            example.createCriteria().andEqualTo(MendOrder.BUSINESS_ORDER_NUMBER, businessOrder.getNumber());
            List<MendOrder> mendOrders = mendOrderMapper.selectByExample(example);
            if(mendOrders.size()>0) {
                MendOrder mendOrder=mendOrders.get(0);
                order.setWorkerTypeId(mendOrder.getWorkerTypeId());
                //处理审核通过
                if (mendOrder.getType() == 0 || mendOrder.getType() == 1) {
                    //获取业主当前最新的userToken
                    String userRole = "role1:" + house.getMemberId();
                    String userToken = redisClient.getCache(userRole, String.class);
                    mendOrderCheckService.checkMendWorkerOrder(userToken, mendOrder, "1", 2);
                }
                if (mendOrder.getType() == 0) {//补货
                    WorkerType workerType = workerTypeMapper.selectByPrimaryKey(mendOrder.getWorkerTypeId());
                    houseExpend.setMaterialMoney(houseExpend.getMaterialMoney() + businessOrder.getTotalPrice().doubleValue());//材料
                    houseExpendMapper.updateByPrimaryKeySelective(houseExpend);
                    mendOrder.setState(4);//业主已支付补材料

                    mendOrderMapper.updateByPrimaryKeySelective(mendOrder);
                    order.setType(2);//材料

                    WarehouseDetail warehouseDetail = new WarehouseDetail();
                    warehouseDetail.setHouseId(businessOrder.getHouseId());
                    warehouseDetail.setRecordType(2);//补材料
                    warehouseDetail.setRelationId(order.getId());
                    warehouseDetailMapper.insert(warehouseDetail);


                    orderSplit(mendOrder.getHouseId(), mendOrder.getId(), workerType.getType());
                }
                if (mendOrder.getType() == 1) {//补人工
                    order.setType(1);//人工

                    houseExpend.setWorkerMoney(houseExpend.getWorkerMoney() + businessOrder.getTotalPrice().doubleValue());//人工
                    houseExpendMapper.updateByPrimaryKeySelective(houseExpend);
                    mendOrder.setState(4);//业主已支付补人工
                    mendOrderMapper.updateByPrimaryKeySelective(mendOrder);
                    ChangeOrder changeOrder = changeOrderMapper.selectByPrimaryKey(mendOrder.getChangeOrderId());
                    changeOrder.setState(4);//已支付
                    changeOrderMapper.updateByPrimaryKeySelective(changeOrder);
                    //若工序发生补人工，则所有未完工工序顺延XX天
                    example = new Example(HouseFlow.class);
                    example.createCriteria().andEqualTo(HouseFlow.HOUSE_ID, changeOrder.getHouseId())
                            .andGreaterThan(HouseFlow.WORKER_TYPE, 3)
                            .andCondition(" work_steta not in (1,2,6)  ");
                    List<HouseFlow> houseFlowList = houseFlowMapper.selectByExample(example);
                    for (HouseFlow houseFlow : houseFlowList) {
                        if (houseFlow.getStartDate() != null) {
                            houseFlow.setEndDate(DateUtil.addDateDays(houseFlow.getEndDate(), changeOrder.getScheduleDay()));
                            houseFlowMapper.updateByPrimaryKeySelective(houseFlow);
                        }
                    }
                    HouseWorkerOrder houseWorkerOrder = houseWorkerOrderMapper.getByHouseIdAndWorkerTypeId(mendOrder.getHouseId(), mendOrder.getWorkerTypeId());
                    HouseWorkerOrder houseWorkerOrdernew = new HouseWorkerOrder();
                    houseWorkerOrdernew.setId(houseWorkerOrder.getId());
                    //还可得的补人工钱，分别在阶段或者整体申请时拿钱
                    BigDecimal repairPrice = houseWorkerOrder.getRepairPrice().add(new BigDecimal(mendOrder.getTotalAmount()));
                    houseWorkerOrdernew.setRepairPrice(repairPrice);
                    //记录总补人工钱
                    if (houseWorkerOrdernew.getRepairTotalPrice() == null) {
                        houseWorkerOrdernew.setRepairTotalPrice(new BigDecimal(0));
                    }
                    BigDecimal repairTotalPrice = houseWorkerOrdernew.getRepairTotalPrice().add(houseWorkerOrdernew.getRepairPrice());
                    houseWorkerOrdernew.setRepairTotalPrice(repairTotalPrice);
                    houseWorkerOrderMapper.updateByPrimaryKeySelective(houseWorkerOrdernew);

                }
            }
            order.setOrderPayTime(new Date());
            order.setPayment(payState);// 支付方式
            orderMapper.updateByPrimaryKeySelective(order);
            budgetCorrect(order,  payState,  order.getWorkerTypeId());

        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
    }

    /**
     * 业主支付完补货单后处理要货单
     */
    private void orderSplit(String houseId, String mendOrderId, Integer workerType) {
        Example example = new Example(OrderSplit.class);
        example.createCriteria().andEqualTo(OrderSplit.HOUSE_ID, houseId).andEqualTo(OrderSplit.APPLY_STATUS, 4)
                .andEqualTo(OrderSplit.MEND_NUMBER, mendOrderId);
        List<OrderSplit> orderSplitList = orderSplitMapper.selectByExample(example);
        //判断是否存在要货
        if (orderSplitList.size() > 0) {
            for (OrderSplit orderSplit : orderSplitList) {
                example = new Example(OrderSplitItem.class);
                example.createCriteria().andEqualTo(OrderSplitItem.ORDER_SPLIT_ID, orderSplit.getId());
                List<OrderSplitItem> orderSplitItemList = orderSplitItemMapper.selectByExample(example);
                for (OrderSplitItem orderSplitItem : orderSplitItemList) {
                    Warehouse warehouse = warehouseMapper.getByProductId(orderSplitItem.getProductId(), houseId);
                    warehouse.setAskCount(warehouse.getAskCount() + orderSplitItem.getNum());//更新仓库已要总数
                    warehouse.setAskTime(warehouse.getAskTime() + 1);//更新该货品被要次数
                    warehouseMapper.updateByPrimaryKeySelective(warehouse);
                    orderSplitItem.setShopCount(warehouse.getShopCount());
                    orderSplitItemMapper.updateByPrimaryKeySelective(orderSplitItem);
                }
                orderSplit.setApplyStatus(1);//提交到后台
                orderSplitMapper.updateByPrimaryKeySelective(orderSplit);

                //记录仓库流水
                WarehouseDetail warehouseDetail = new WarehouseDetail();
                warehouseDetail.setHouseId(houseId);
                warehouseDetail.setRelationId(orderSplit.getId());//要货单
                warehouseDetail.setRecordType(1);//要
                warehouseDetailMapper.insert(warehouseDetail);

                House house = houseMapper.selectByPrimaryKey(houseId);
                if (workerType == 3) {
                    configMessageService.addConfigMessage(null, AppType.ZHUANGXIU, house.getMemberId(), "0", "大管家要包工包料",
                            String.format(DjConstants.PushMessage.STEWARD_Y_SERVER, house.getHouseName()), "");
                } else {
                    configMessageService.addConfigMessage(null, AppType.ZHUANGXIU, house.getMemberId(), "0", "工匠要材料", String.format
                            (DjConstants.PushMessage.CRAFTSMAN_Y_MATERIAL, house.getHouseName()), "");
                }
            }
        }
    }


    /**
     * 支付工序
     */
    private void payWorkerType(BusinessOrder businessOrder, String payState) {
        try {
            Order order= orderMapper.selectByPrimaryKey(businessOrder.getTaskId());
            String workerTypeId=order.getWorkerTypeId();

            //判断是否为工序订单( 设计、精算订单)
            if((workerTypeId==null|| StringUtils.isBlank(workerTypeId))){
                boolean desginStatus=false;
                //1.如果是工序订单，但总单上没有工序ID，则需要查子单上的工序ID
                List<HouseOrderDetailDTO> orderDetailList=houseMapper.getBudgetOrderDetailByHouseId(order.getHouseId(),"1");//设计订单
                if(orderDetailList!=null&&orderDetailList.size()>0){
                    BigDecimal diffMoney=new BigDecimal(0);
                    if(order.getOrderSource()==4){//补差价订单(设计师）
                        diffMoney=getDiffTotalPrice(orderDetailList);
                    }
                    desginStatus=true;
                    setHouseFlowInfo(order,"1",payState,1,diffMoney,2);
                }
                //精算师的判断
                orderDetailList=houseMapper.getBudgetOrderDetailByHouseId(order.getHouseId(),"2");//精算订单
                if(orderDetailList!=null&&orderDetailList.size()>0){
                    BigDecimal diffMoney=new BigDecimal(0);
                    if(order.getOrderSource()==4){//补差价订单（精算师）
                        diffMoney=getDiffTotalPrice(orderDetailList);
                    }
                    //判断精算师是否可抢单
                    if(desginStatus){
                        setHouseFlowInfo(order,"2",payState,1,diffMoney,2);
                    }else{
                        setHouseFlowInfo(order,"2",payState,1,diffMoney,1);
                    }

                }
            }else{
                setHouseFlowInfo(order,workerTypeId,payState,1,new BigDecimal(0),2);
            }
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
    }

    /**
     * 获取差价金钱
     * @param orderDetailList
     * @return
     */
    private BigDecimal getDiffTotalPrice(List<HouseOrderDetailDTO> orderDetailList){
        Double totalPrice=0d;
        if(orderDetailList!=null&&orderDetailList.size()>0){
            for(HouseOrderDetailDTO orderDetailDTO:orderDetailList){
                totalPrice= MathUtil.add(totalPrice,orderDetailDTO.getPrice());
            }
        }
        return  BigDecimal.valueOf(totalPrice);
    }

    /**
     * 订单支付后工钱的处理，
     * @param order
     * @param workerTypeId
     * @param orderSource 数据来源（1工序订单，4补差价订单）
     * @param diffTotalPrice 差价金额
     */
    private  void setHouseFlowInfo(Order order,String workerTypeId,String payState,Integer orderSource,BigDecimal diffTotalPrice,Integer workType){
        HouseFlow houseFlow = houseFlowMapper.getByWorkerTypeId(order.getHouseId(),workerTypeId);
        House house = houseMapper.selectByPrimaryKey(houseFlow.getHouseId());
        if (house.getMoney() == null) {
            house.setMoney(new BigDecimal(0));
        }
        HouseWorkerOrder hwo = houseWorkerOrderMapper.getByHouseIdAndWorkerTypeId(houseFlow.getHouseId(), houseFlow.getWorkerTypeId());
       // HouseWorkerOrder houseWorkerOrdernew = new HouseWorkerOrder();
        hwo.setId(hwo.getId());
        hwo.setPayState(1);
        if(orderSource==4){//如果是补差价订单，则需要在当前工序上把钱补上去
            hwo.setWorkPrice(hwo.getWorkPrice().add(diffTotalPrice));//人工金钱
            hwo.setTotalPrice(hwo.getTotalPrice().add(diffTotalPrice));//总金钱
        }
        houseWorkerOrderMapper.updateByPrimaryKeySelective(hwo);
        //为兼容老数据，工序到了已被抢单的，说明已经支付完成，无需在做下一步操作
        if(houseFlow.getWorkType()!=3){
            houseFlow.setWorkType(workType);
            houseFlow.setReleaseTime(new Date());//set发布时间
        }
        houseFlow.setPayStatus(1);//已支付
        houseFlow.setMaterialPrice(hwo.getMaterialPrice());
        houseFlow.setWorkPrice(hwo.getWorkPrice());
        houseFlow.setTotalPrice(hwo.getTotalPrice());
        houseFlow.setModifyDate(new Date());
        houseFlowMapper.updateByPrimaryKeySelective(houseFlow);

        if(houseFlow.getWorkType()>2) {
            /*不统计设计精算人工*/
            HouseExpend houseExpend = houseExpendMapper.getByHouseId(hwo.getHouseId());
            houseExpend.setMaterialMoney(houseExpend.getMaterialMoney() + hwo.getMaterialPrice().doubleValue());//材料
            houseExpend.setWorkerMoney(houseExpend.getWorkerMoney() + hwo.getWorkPrice().doubleValue());//人工
            houseExpendMapper.updateByPrimaryKeySelective(houseExpend);
        }

        WarehouseDetail warehouseDetail = new WarehouseDetail();
        warehouseDetail.setHouseId(order.getHouseId());
        warehouseDetail.setRecordType(0);//支付精算
        warehouseDetail.setRelationId(order.getId());
        warehouseDetailMapper.insert(warehouseDetail);
        /*处理人工和取消的材料改到自购精算*/
        budgetCorrect(order, payState, houseFlow.getId());

        /*处理保险订单*/
        this.insurance(hwo, payState);
    }




    /**
     * 保险订单
     */
    private void insurance(HouseWorkerOrder hwo, String payState) {
        try {
            WorkerTypeSafeOrder wtso = workerTypeSafeOrderMapper.getByNotPay(hwo.getWorkerTypeId(), hwo.getHouseId());
            if (wtso != null) {
                wtso.setState(1);  //已支付
                wtso.setShopDate(new Date());  //设置购买时间
                workerTypeSafeOrderMapper.updateByPrimaryKeySelective(wtso);

                hwo.setSafePrice(wtso.getPrice());
                houseWorkerOrderMapper.updateByPrimaryKeySelective(hwo);//记录保险费

                House house = houseMapper.selectByPrimaryKey(hwo.getHouseId());
                WorkerTypeSafe wts = workerTypeSafeMapper.selectByPrimaryKey(wtso.getWorkerTypeSafeId());
                if (house.getMoney() == null) {
                    house.setMoney(new BigDecimal(0));
                }
                //记录项目流水 保险
                HouseAccounts ha = new HouseAccounts();
                ha.setReason("收入" + wts.getName() + "费用");
                ha.setMoney(house.getMoney().add(hwo.getSafePrice()));//项目总钱
                ha.setState(0);//进
                ha.setPayMoney(hwo.getSafePrice());//本次数额
                ha.setHouseId(house.getId());
                ha.setHouseName(house.getHouseName());
                ha.setMemberId(house.getMemberId());
                ha.setName("业主支付");
                ha.setPayment(payState);//统计支付方式
                houseAccountsMapper.insert(ha);
                house.setMoney(house.getMoney().add(hwo.getSafePrice()));//累计项目钱
                houseMapper.updateByPrimaryKeySelective(house);
            }
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
    }




    public void budgetCorrect(Order order, String payState, String houseFlowId) {
        List<ShopGoodsDTO> queryShopGoods = queryShopGoods(order.getId());
        //如果存在多个订单则拆分子订单
        for (ShopGoodsDTO queryShopGood : queryShopGoods) {
            BigDecimal paymentPrice = new BigDecimal(0);//总共钱
            BigDecimal freightPrice = new BigDecimal(0);//总运费
            BigDecimal totalMoveDost = new BigDecimal(0);//搬运费

            Order orderNew = orderMapper.getStorefontOrder(queryShopGood.getShopId(),order.getId());
            if(queryShopGoods.size()>1){
                if(orderNew==null) {
                    orderNew = new Order();
                }
                orderNew.setHouseId(order.getHouseId());
                orderNew.setCityId(order.getCityId());
                orderNew.setStorefontId(queryShopGood.getShopId());
                orderNew.setMemberId(order.getMemberId());
                orderNew.setBusinessOrderNumber(order.getBusinessOrderNumber());
                orderNew.setType(order.getType());
                orderNew.setOrderNumber(System.currentTimeMillis() + "-" + (int) (Math.random() * 9000 + 1000));
                orderNew.setTotalDiscountPrice(new BigDecimal(0));
                orderNew.setParentOrderId(order.getId());
                orderNew.setTotalStevedorageCost(new BigDecimal(0));
                orderNew.setTotalTransportationCost(new BigDecimal(0));
                orderNew.setActualPaymentPrice(new BigDecimal(0));
                orderNew.setOrderGenerationTime(order.getOrderGenerationTime());
                orderNew.setOrderSource(order.getOrderSource());//精算制作
                orderNew.setWorkerId(order.getWorkerId());
                orderNew.setAddressId(order.getAddressId());
                orderNew.setCreateBy(order.getCreateBy());
                orderMapper.insert(orderNew);
            }else {
                orderNew=order;
                orderNew.setStorefontId(queryShopGood.getShopId());
                orderNew.setParentOrderId(order.getId());
            }

            WorkerType workerType = workerTypeMapper.selectByPrimaryKey(order.getWorkerTypeId());
            String workerTypeName="";
            if(workerType!=null){
                workerTypeName=workerType.getName();
            }
            orderNew.setWorkerTypeName(workerTypeName+orderNew.getWorkerTypeName());
            for (BudgetLabelDTO labelDTO : queryShopGood.getLabelDTOS()) {
                for (BudgetLabelGoodsDTO good : labelDTO.getGoods()) {
                    OrderItem orderItem = orderItemMapper.selectByPrimaryKey(good.getId());
                    paymentPrice = paymentPrice.add(new BigDecimal(orderItem.getTotalPrice()));
                    freightPrice = freightPrice.add(new BigDecimal(orderItem.getTransportationCost()));
                    totalMoveDost = totalMoveDost.add(new BigDecimal(orderItem.getStevedorageCost()));
                    if(!CommonUtil.isEmpty(payState)) {
                        orderItem.setOrderStatus("2");
                    }else{
                        orderItem.setOrderStatus("1");
                    }
                    orderItem.setOrderId(orderNew.getId());
                    orderItemMapper.updateByPrimaryKeySelective(orderItem);

                    if(!CommonUtil.isEmpty(payState)) {
                        if (good.getProductType() < 3) {
                            if (houseFlowId != null) {
                                Example example = new Example(BudgetMaterial.class);
                                example.createCriteria()
                                        .andEqualTo(BudgetMaterial.HOUSE_FLOW_ID, houseFlowId)
                                        .andEqualTo(BudgetMaterial.PRODUCT_ID, good.getProductId())
                                        .andEqualTo(BudgetMaterial.STETA, 1);
                                List<BudgetMaterial> budgetMaterialList = iMasterBudgetMapper.selectByExample(example);
                                for (BudgetMaterial budgetMaterial : budgetMaterialList) {
                                    budgetMaterial.setTotalPrice(budgetMaterial.getConvertCount() * budgetMaterial.getPrice());//已支付 记录总价
                                    budgetMaterial.setDeleteState(3);//已支付
                                    budgetMaterial.setModifyDate(new Date());
                                    iMasterBudgetMapper.updateByPrimaryKeySelective(budgetMaterial);
                                }
                            }
                            if (!CommonUtil.isEmpty(order.getHouseId())) {
                                addWarehouse(orderItem, houseFlowId, order.getHouseId());
                            }
                        }
                    }
                }
            }
            if(queryShopGoods.size()>1) {
                orderNew.setTotalTransportationCost(freightPrice);//总运费
                orderNew.setTotalStevedorageCost(totalMoveDost);//总搬运费
                orderNew.setTotalAmount(paymentPrice);// 订单总额(工钱)

                BigDecimal payPrice = orderNew.getTotalAmount().subtract(orderNew.getTotalDiscountPrice());
                payPrice = payPrice.add(orderNew.getTotalStevedorageCost());
                payPrice = payPrice.add(orderNew.getTotalTransportationCost());
                orderNew.setActualPaymentPrice(payPrice);
                if(!CommonUtil.isEmpty(payState)) {
                    orderNew.setOrderStatus("2");
                    orderNew.setOrderPayTime(new Date());
                    orderNew.setPayment(payState);// 支付方式
                }else{
                    orderNew.setOrderStatus("1");
                }
                orderMapper.updateByPrimaryKeySelective(orderNew);
            }
        }
        BigDecimal payPrice = order.getTotalAmount().subtract(order.getTotalDiscountPrice());
        payPrice = payPrice.add(order.getTotalStevedorageCost());
        payPrice = payPrice.add(order.getTotalTransportationCost());
        order.setActualPaymentPrice(payPrice);
        if(!CommonUtil.isEmpty(payState)) {
            order.setOrderStatus("2");
            order.setOrderPayTime(new Date());
            order.setPayment(payState);// 支付方式
        }else{
            order.setOrderStatus("1");
        }

        orderMapper.updateByPrimaryKeySelective(order);

    }

    /**
     * 生成仓库
     * type 1 工序抢单任务进来的
     * 2 未购买待付款进来的
     */
    private void addWarehouse(OrderItem budgetMaterial,String houseFlowId, String houseId) {
        try {

            Example example = new Example(Warehouse.class);
            example.createCriteria().andEqualTo(Warehouse.HOUSE_ID, houseId).andEqualTo(Warehouse.PRODUCT_ID, budgetMaterial.getProductId());
            int sum = warehouseMapper.selectCountByExample(example);
            if (sum > 0) {//累计数量
                Warehouse warehouse = warehouseMapper.getByProductId(budgetMaterial.getProductId(), houseId);
                warehouse.setShopCount(warehouse.getShopCount() + budgetMaterial.getShopCount());//数量
                if(CommonUtil.isEmpty(houseFlowId)){
                    warehouse.setRepairCount(warehouse.getRepairCount() + budgetMaterial.getShopCount());
                    warehouse.setRepTime(warehouse.getRepTime() + 1);//补次数
                }else{
                    warehouse.setRobCount(warehouse.getRobCount() + budgetMaterial.getShopCount());
                }
                warehouse.setPrice(budgetMaterial.getPrice());
                warehouse.setCost(budgetMaterial.getCost());
                warehouse.setImage(budgetMaterial.getImage());
                warehouse.setProductName(budgetMaterial.getProductName());
                warehouse.setPayTime(warehouse.getPayTime() + 1);//买次数
                warehouseMapper.updateByPrimaryKeySelective(warehouse);
            } else {//增加一条
                Warehouse warehouse = new Warehouse();
                warehouse.setHouseId(houseId);
                warehouse.setShopCount(budgetMaterial.getShopCount());
                warehouse.setRepTime(0);
                if(CommonUtil.isEmpty(houseFlowId)){
                    warehouse.setRepairCount(budgetMaterial.getShopCount());
                }else{
                    warehouse.setBudgetCount(budgetMaterial.getShopCount());
                    warehouse.setRobCount(budgetMaterial.getShopCount());
                }
                warehouse.setStayCount(0.0);
                warehouse.setAskCount(0.0);//已要数量
                warehouse.setBackCount(0.0);//退总数
                warehouse.setReceive(0.0);
                warehouse.setProductId(budgetMaterial.getProductId());
                warehouse.setProductSn(budgetMaterial.getProductSn());
                warehouse.setProductName(budgetMaterial.getProductName());
                warehouse.setPrice(budgetMaterial.getPrice());
                warehouse.setCost(budgetMaterial.getCost());
                warehouse.setUnitName(budgetMaterial.getUnitName());
                warehouse.setProductType(budgetMaterial.getProductType());
                warehouse.setCategoryId(budgetMaterial.getCategoryId());
                warehouse.setImage(budgetMaterial.getImage());
                warehouse.setPayTime(1);//买次数
                warehouse.setAskTime(0);
                warehouse.setBackTime(0);
                warehouse.setCityId(budgetMaterial.getCityId());
                warehouseMapper.insert(warehouse);
            }
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
    }


    /**
     * 支付页面
     * type   1工序支付任务,2其他
     */
    public ServerResponse getPaymentOrder(String userToken,  String orderId) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Order order= orderMapper.selectByPrimaryKey(orderId);
            Example example = new Example(BusinessOrder.class);
            example.createCriteria().andEqualTo(BusinessOrder.NUMBER, order.getBusinessOrderNumber()).andNotEqualTo(BusinessOrder.STATE,4);
            List<BusinessOrder> businessOrderList = businessOrderMapper.selectByExample(example);
            BusinessOrder businessOrder = null;
            if(businessOrderList.size()>0){
                businessOrder = businessOrderList.get(0);
                if(businessOrder.getState()==3){
                    return ServerResponse.createByErrorMessage("该订单已支付，请勿重复支付！");
                }
            }


            businessOrder.setState(2);//刚生成

            PaymentDTO paymentDTO = new PaymentDTO();
            BigDecimal paymentPrice = new BigDecimal(0);//总共钱
            List<ShopGoodsDTO> budgetLabelDTOS = queryShopGoods(orderId);//精算工钱
            paymentDTO.setDatas(budgetLabelDTOS);
            for (ShopGoodsDTO budgetLabelDTO : budgetLabelDTOS) {
                for (BudgetLabelDTO labelDTO : budgetLabelDTO.getLabelDTOS()) {
                    paymentPrice = paymentPrice.add(labelDTO.getTotalPrice());
                }
            }
            if (order.getOrderSource() == 1) {//支付
                House house = houseMapper.selectByPrimaryKey(order.getHouseId());
                WorkerType workerType = workerTypeMapper.selectByPrimaryKey(order.getWorkerTypeId());
                paymentDTO.setWorkerTypeName(workerType.getName());
                /*
                 * 生成工匠订单
                 */
                HouseWorkerOrder hwo = houseWorkerOrderMapper.getByHouseIdAndWorkerTypeId(house.getId(), order.getWorkerTypeId());
                if (hwo == null) {
                    hwo = new HouseWorkerOrder(true);
                    hwo.setHouseId(order.getHouseId());
                    hwo.setWorkerTypeId(order.getWorkerTypeId());
                    hwo.setWorkerType(workerType.getType());
                    hwo.setBusinessOrderNumber(businessOrder.getNumber());//业务订单号
                    houseWorkerOrderMapper.insert(hwo);
                } else {
                    hwo.setHouseId(order.getHouseId());
                    hwo.setWorkerTypeId(order.getWorkerTypeId());
                    hwo.setWorkerType(workerType.getType());
                    hwo.setBusinessOrderNumber(businessOrder.getNumber());//业务订单号
                    houseWorkerOrderMapper.updateByPrimaryKey(hwo);
                }
                Double workerPrice = 0d;//精算工钱
                Double caiPrice =0d;//精算材料钱
                Double serPrice = 0d;//精算包工包料钱

                for (ShopGoodsDTO budgetLabelDTO : budgetLabelDTOS) {
                    for (BudgetLabelDTO labelDTO : budgetLabelDTO.getLabelDTOS()) {
                        for (BudgetLabelGoodsDTO good : labelDTO.getGoods()) {
                            if(good.getProductType()==0){//材料
                                caiPrice+=good.getTotalPrice().doubleValue();
                            }
                            if(good.getProductType()==1){//包工包料
                                serPrice+=good.getTotalPrice().doubleValue();
                            }
                            if(good.getProductType()==2){//人工
                                workerPrice+=good.getTotalPrice().doubleValue();
                            }
                        }
                    }
                }
                hwo.setWorkPrice(new BigDecimal(workerPrice));//工钱
                hwo.setMaterialPrice(new BigDecimal(caiPrice + serPrice));//材料钱
                hwo.setTotalPrice(paymentPrice);//工钱+拆料
                houseWorkerOrderMapper.updateByPrimaryKey(hwo);
                if (order.getOrderSource() == 1 && workerType.getType() > 2) {//精算师和设计师不存在保险订单
                    //查出有没有生成保险订单
                    example = new Example(WorkerTypeSafeOrder.class);
                    example.createCriteria().andEqualTo(WorkerTypeSafeOrder.HOUSE_ID, house.getId()).andEqualTo(WorkerTypeSafeOrder.WORKER_TYPE_ID, workerType.getId()).andEqualTo(WorkerTypeSafeOrder.DATA_STATUS, 0);
                    List<WorkerTypeSafeOrder> wtsoList = workerTypeSafeOrderMapper.selectByExample(example);
                    if (wtsoList.size() == 1) {
                        WorkerTypeSafeOrder workerTypeSafeOrder = wtsoList.get(0);
                        workerTypeSafeOrder.setBusinessOrderNumber(businessOrder.getNumber());
                        //保存业务订单号
                        workerTypeSafeOrderMapper.updateByPrimaryKey(workerTypeSafeOrder);
                        UpgradeSafeDTO upgradeSafeDTO = new UpgradeSafeDTO();//保险服务
                        upgradeSafeDTO.setTitle("保险服务");
                        upgradeSafeDTO.setType(0);//单选
                        WorkerTypeSafe wts = workerTypeSafeMapper.selectByPrimaryKey(workerTypeSafeOrder.getWorkerTypeSafeId());
                        List<SafeTypeDTO> safeTypeDTOList = new ArrayList<>();
                        SafeTypeDTO safeTypeDTO = new SafeTypeDTO();
                        safeTypeDTO.setName(wts.getName());
                        BigDecimal price = wts.getPrice().multiply(house.getSquare());
                        safeTypeDTO.setPrice("¥" + String.format("%.2f", price.doubleValue()));
                        safeTypeDTO.setSelected(1);//勾
                        safeTypeDTOList.add(safeTypeDTO);
                        upgradeSafeDTO.setSafeTypeDTOList(safeTypeDTOList);
                        paymentDTO.setUpgradeSafeDTO(upgradeSafeDTO);//保险
                        paymentPrice = paymentPrice.add(wts.getPrice().multiply(house.getSquare()));//钱加上
                    } else if (wtsoList.size() > 1) {
                        return ServerResponse.createByErrorMessage("保险订单错误,联系平台部");
                    }
                }
            }
            paymentDTO.setFreight(order.getTotalTransportationCost());//运费
            paymentDTO.setMoveDost(order.getTotalStevedorageCost());//搬运费
            //保存总价
            businessOrder.setTotalPrice(paymentPrice);
            BigDecimal payPrice = businessOrder.getTotalPrice().subtract(businessOrder.getDiscountsPrice());
            payPrice = payPrice.add(paymentDTO.getFreight());
            payPrice = payPrice.add(paymentDTO.getMoveDost());
            businessOrder.setPayPrice(payPrice);
            businessOrderMapper.updateByPrimaryKeySelective(businessOrder);

            //查看优惠
            List<ActivityRedPackRecordDTO> rprList = discountPage(businessOrder.getNumber());
            if (rprList != null && rprList.size() > 0) {
                paymentDTO.setDiscounts(1);//有优惠
            } else {
                paymentDTO.setDiscounts(0);//
            }
            paymentDTO.setTotalPrice(paymentPrice);
            paymentDTO.setDiscountsPrice(businessOrder.getDiscountsPrice());
            paymentDTO.setPayPrice(payPrice);//实付
            paymentDTO.setType(order.getType());//1人工订单 2其他订单 3精算订单 4体验订单
            paymentDTO.setBusinessOrderNumber(businessOrder.getNumber());
            return ServerResponse.createBySuccess("查询成功", paymentDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    public List<ShopGoodsDTO> queryShopGoods(String orderId){
        List<ShopGoodsDTO> budgetLabelDTOS =  orderItemMapper.queryShopGoods(orderId);
        for (ShopGoodsDTO budgetLabelDTO : budgetLabelDTOS) {
            BigDecimal totalMaterialPrice = new BigDecimal(0);//组总价
            budgetLabelDTO.setLabelDTOS(queryBudgetLabel(orderId,budgetLabelDTO.getShopId()));
            for (BudgetLabelDTO labelDTO : budgetLabelDTO.getLabelDTOS()) {
                for (BudgetLabelGoodsDTO good : labelDTO.getGoods()) {
                    if(good.getProductType()==0) {
                        totalMaterialPrice = totalMaterialPrice.add(good.getTotalPrice());
                    }
                }
            }
            budgetLabelDTO.setTotalMaterialPrice(totalMaterialPrice);
        }
        return budgetLabelDTOS;
    }
    public List<BudgetLabelDTO> queryBudgetLabel(String orderId,String storefontId){
        String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
        List<BudgetLabelDTO> budgetLabelDTOS =  orderItemMapper.queryBudgetLabel(orderId,storefontId);//精算工钱

        List<BudgetLabelGoodsDTO> budgetLabelGoodsDTOS = orderItemMapper.queryBudgetLabelGoods(orderId,storefontId);//精算工钱
        for (BudgetLabelDTO budgetLabelDTO : budgetLabelDTOS) {
            BigDecimal totalZPrice = new BigDecimal(0);//组总价
            String[] array = budgetLabelDTO.getCategoryIds().split(",");
            List<BudgetLabelGoodsDTO> budgetLabelGoodss= new ArrayList<>();
            for (BudgetLabelGoodsDTO budgetLabelGoodsDTO : budgetLabelGoodsDTOS) {
                boolean flag = Arrays.asList(array).contains(budgetLabelGoodsDTO.getCategoryId());
                if(flag){
                    totalZPrice = totalZPrice.add(budgetLabelGoodsDTO.getTotalPrice());
                    if(!CommonUtil.isEmpty(budgetLabelGoodsDTO.getGoodsId())){
                        Brand brand =null;
                        BasicsGoods goods=iMasterBasicsGoodsMapper.selectByPrimaryKey(budgetLabelGoodsDTO.getGoodsId());
                        budgetLabelGoodsDTO.setIsReservationDeliver(goods.getIsReservationDeliver());
                        if (!CommonUtil.isEmpty(goods.getBrandId())) {
                            brand = iMasterBrandMapper.selectByPrimaryKey(goods.getBrandId());
                        }
                        if (!CommonUtil.isEmpty(budgetLabelGoodsDTO.getAttributeName())) {
                            budgetLabelGoodsDTO.setAttributeName(budgetLabelGoodsDTO.getAttributeName().replaceAll(",", " "));
                        }
                        if (brand!=null) {
                            budgetLabelGoodsDTO.setAttributeName(brand.getName()+" "+budgetLabelGoodsDTO.getAttributeName());
                        }
                    }
                    budgetLabelGoodsDTO.setImage(CommonUtil.isEmpty(budgetLabelGoodsDTO.getImage())?"":imageAddress+budgetLabelGoodsDTO.getImage());
                    budgetLabelGoodss.add(budgetLabelGoodsDTO);
                }
            }
            budgetLabelDTO.setTotalPrice(totalZPrice);
            budgetLabelDTO.setGoods(budgetLabelGoodss);
        }
        return budgetLabelDTOS;
    }
    /**
     * 提交订单
     * @return
     */
    public ServerResponse generateOrder(String userToken,String cityId, String productJsons,String workerId, String addressId) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = (Member) object;
            String houseId="";
            House house=getHouseId(member.getId());
            if(house!= null) {
                houseId=house.getId();
            }
            return generateOrderCommon(member,houseId, cityId, productJsons, workerId,  addressId,2);
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ServerResponse.createByErrorMessage("提交失败：原因："+e.getMessage());
        }
    }

    /**
     *
     * @param member
     * @param houseId
     * @param cityId
     * @param productJsons
     * @param workerId
     * @param addressId
     * @param orderSource (1设计精算订单提交，2购物车提交，4设计精算补差价订单提交）
     * @return
     */
    public ServerResponse generateOrderCommon(Member member,String houseId,String cityId, String productJsons,String workerId, String addressId,Integer orderSource){
        JSONArray productArray= JSON.parseArray(productJsons);
        if(productArray.size()==0){
            return ServerResponse.createByErrorMessage("参数错误");
        }
        Map<String,ShoppingCartListDTO> productMap = new HashMap<>();
        String[] productIds=new String[productArray.size()];
        for (int i = 0; i < productArray.size(); i++) {
            JSONObject productObj = productArray.getJSONObject(i);
            productMap.put(productObj.getString(ProductDTO.PRODUCT_ID),BeanUtils.mapToBean(ShoppingCartListDTO.class,productObj));
            productIds[i]=productObj.getString(ProductDTO.PRODUCT_ID);

        }
        List<ShoppingCartDTO> shoppingCartDTOS=iShoppingCartMapper.queryShoppingCartDTOS(productIds);
        BigDecimal paymentPrice = new BigDecimal(0);//总共钱
        BigDecimal freightPrice = new BigDecimal(0);//总运费
        BigDecimal totalMoveDost = new BigDecimal(0);//搬运费
        for (ShoppingCartDTO shoppingCartDTO : shoppingCartDTOS) {
            BigDecimal totalSellPrice = new BigDecimal(0);//总价
            BigDecimal totalMaterialPrice = new BigDecimal(0);//组总价
            for (ShoppingCartListDTO shoppingCartListDTO : shoppingCartDTO.getShoppingCartListDTOS()) {
                ShoppingCartListDTO parmDTO=productMap.get(shoppingCartListDTO.getProductId());
                shoppingCartListDTO.setShopCount(parmDTO.getShopCount());
                shoppingCartListDTO.setWorkerTypeId(parmDTO.getWorkerTypeId());//工种类型 （1设计,2精算，3其它）
                BigDecimal totalPrice = new BigDecimal(shoppingCartListDTO.getPrice()*parmDTO.getShopCount());
                if(shoppingCartListDTO.getProductType()==0) {
                    totalMaterialPrice = totalMaterialPrice.add(totalPrice);
                }
                totalSellPrice = totalSellPrice.add(totalPrice);
                paymentPrice = paymentPrice.add(totalPrice);
            }
            shoppingCartDTO.setTotalMaterialPrice(totalMaterialPrice);
            shoppingCartDTO.setTotalPrice(totalSellPrice);
        }

        if (shoppingCartDTOS!=null) {
            Order order = new Order();
            String workerTypeName="购物车订单";
            if(orderSource==1||orderSource==4){
                workerTypeName="设计、精算订单";
            }
            order.setWorkerTypeName(workerTypeName);
            order.setCityId(cityId);
            order.setMemberId(member.getId());
            order.setWorkerId(workerId);
            order.setAddressId(addressId);
            order.setHouseId(houseId);
            order.setOrderNumber(System.currentTimeMillis() + "-" + (int) (Math.random() * 9000 + 1000));
            order.setTotalDiscountPrice(new BigDecimal(0));
            order.setTotalStevedorageCost(new BigDecimal(0));
            order.setTotalTransportationCost(new BigDecimal(0));
            order.setActualPaymentPrice(new BigDecimal(0));
            order.setOrderStatus("1");
            order.setOrderGenerationTime(new Date());
            order.setOrderSource(orderSource);//来源购物车
            order.setCreateBy(member.getId());
            orderMapper.insert(order);
            for (ShoppingCartDTO shoppingCartDTO : shoppingCartDTOS) {
                Double freight=storefrontConfigAPI.getFreightPrice(shoppingCartDTO.getStorefrontId(),shoppingCartDTO.getTotalMaterialPrice().doubleValue());
                freightPrice=freightPrice.add(new BigDecimal(freight));
                for (ShoppingCartListDTO good : shoppingCartDTO.getShoppingCartListDTOS()) {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setIsReservationDeliver(good.getIsReservationDeliver());
                    orderItem.setOrderId(order.getId());
                    orderItem.setPrice(good.getPrice().doubleValue());//销售价
                    orderItem.setShopCount(good.getShopCount());//购买总数
                    orderItem.setUnitName(good.getUnitName());//单位
                    orderItem.setTotalPrice(good.getPrice()*good.getShopCount());//总价
                    orderItem.setProductName(good.getProductName());
                    orderItem.setProductSn(good.getProductSn());
                    orderItem.setCategoryId(good.getCategoryId());
                    orderItem.setProductId(good.getProductId());
                    orderItem.setImage(good.getImage());
                    orderItem.setCityId(cityId);
                    orderItem.setProductType(good.getProductType());
                    orderItem.setStorefontId(shoppingCartDTO.getStorefrontId());
                    orderItem.setAskCount(0d);
                    orderItem.setDiscountPrice(0d);
                    orderItem.setActualPaymentPrice(0d);
                    orderItem.setStevedorageCost(0d);
                    orderItem.setTransportationCost(0d);
                    if(orderSource==1||orderSource==4){
                        orderItem.setWorkerTypeId(good.getWorkerTypeId());
                    }
                    if(!CommonUtil.isEmpty(order.getHouseId())) {
                        //搬运费运算
                        Double moveDost = masterCostAcquisitionService.getStevedorageCost(order.getHouseId(), orderItem.getProductId(), orderItem.getShopCount());
                        totalMoveDost = totalMoveDost.add(new BigDecimal(moveDost));
                        if (moveDost > 0) {
                            //均摊运费
                            orderItem.setStevedorageCost(moveDost);
                        }
                    }
                    if(good.getProductType()==0&&freight>0){
                        //均摊运费
                        Double transportationCost=(orderItem.getTotalPrice()/shoppingCartDTO.getTotalMaterialPrice().doubleValue())*freight;
                        orderItem.setTransportationCost(transportationCost);
                    }
                    if(good.getProductType()==0||good.getProductType()==1){
                        order.setWorkerTypeName("实物订单");
                        order.setType(2);
                    }else if(good.getProductType()==2){
                        order.setWorkerTypeName("人工订单");
                        order.setType(1);
                    }else {
                        order.setWorkerTypeName("体验订单");
                        order.setType(4);
                    }
                    orderItem.setOrderStatus("1");//1待付款，2已付款，3待收货，4已完成，5已取消，6已退货，7已关闭
                    orderItem.setCreateBy(member.getId());
                    orderItemMapper.insert(orderItem);

                    ShoppingCartListDTO parmDTO=productMap.get(orderItem.getProductId());
                    if(!CommonUtil.isEmpty(parmDTO.getAddedProductIds())) {
                        setAddedProduct(orderItem.getId(), parmDTO.getAddedProductIds(), "1");
                    }

                    //获取购物车增值商品信息
                    Example example1=new Example(DeliverOrderAddedProduct.class);
                    example1.createCriteria().andEqualTo(DeliverOrderAddedProduct.ANY_ORDER_ID,orderItem.getId())
                            .andEqualTo(DeliverOrderAddedProduct.SOURCE,1);
                    List<DeliverOrderAddedProduct> deliverOrderAddedProducts = masterDeliverOrderAddedProductMapper.selectByExample(example1);
                    for (DeliverOrderAddedProduct deliverOrderAddedProduct : deliverOrderAddedProducts) {
                        BigDecimal totalPrice = new BigDecimal(deliverOrderAddedProduct.getPrice()*parmDTO.getShopCount());
                        paymentPrice = paymentPrice.add(totalPrice);
                    }
                }
            }


            // 生成支付业务单
            Example example = new Example(BusinessOrder.class);
            example.createCriteria().andEqualTo(BusinessOrder.TASK_ID, order.getId()).andNotEqualTo(BusinessOrder.STATE, 4).andNotEqualTo(BusinessOrder.STATE, 3);
            List<BusinessOrder> businessOrderList = businessOrderMapper.selectByExample(example);
            BusinessOrder businessOrder = null;
            if (businessOrderList.size() > 0) {
                businessOrder = businessOrderList.get(0);
            }
            if (businessOrderList.size() == 0) {
                businessOrder = new BusinessOrder();
                businessOrder.setMemberId(member.getId());
                businessOrder.setHouseId(houseId);
                businessOrder.setNumber(System.currentTimeMillis() + "-" + (int) (Math.random() * 9000 + 1000));
                businessOrder.setState(1);//刚生成
                businessOrder.setTotalPrice(paymentPrice);
                businessOrder.setDiscountsPrice(new BigDecimal(0));
                businessOrder.setPayPrice(paymentPrice);
                businessOrder.setType(2);//记录支付类型任务类型
                businessOrder.setTaskId(order.getId());//保存任务ID
                businessOrderMapper.insert(businessOrder);
            }

            order.setTotalTransportationCost(freightPrice);//总运费
            order.setTotalStevedorageCost(totalMoveDost);//总搬运费
            order.setBusinessOrderNumber(businessOrder.getNumber());
            order.setTotalAmount(paymentPrice);// 订单总额(工钱)
            orderMapper.updateByPrimaryKeySelective(order);

            if(orderSource==1){//若为支付类型的订单，需增加订单支付工序阶段（设计，精算类的订单)
                List<HouseOrderDetailDTO> orderDetailList=houseMapper.getBudgetOrderDetailByHouseId(houseId,"1");//设计订单
                setTotalPrice(orderDetailList,houseId,"1",order,businessOrder);
                orderDetailList=houseMapper.getBudgetOrderDetailByHouseId(houseId,"2");//精算订单
                setTotalPrice(orderDetailList,houseId,"2",order,businessOrder);//
            }

            budgetCorrect(order,null,null);

            //清空增值商品
            example = new Example(ShoppingCart.class);
            example.createCriteria().andEqualTo(ShoppingCart.MEMBER_ID, member.getId())
                    .andIn(ShoppingCart.PRODUCT_ID,Arrays.asList(productIds));
            List<ShoppingCart> shoppingCarts = iShoppingCartMapper.selectByExample(example);
            if(shoppingCarts.size()>0) {
                for (ShoppingCart shoppingCart : shoppingCarts) {
                    Example example1 = new Example(DeliverOrderAddedProduct.class);
                    example1.createCriteria().andEqualTo(DeliverOrderAddedProduct.ANY_ORDER_ID, shoppingCart.getId()).andEqualTo(DeliverOrderAddedProduct.SOURCE, 4);
                    masterDeliverOrderAddedProductMapper.deleteByExample(example1);
                }
            }

            //清空购物车指定商品
            example = new Example(ShoppingCart.class);
            example.createCriteria().andEqualTo(ShoppingCart.MEMBER_ID, member.getId())
                    .andIn(ShoppingCart.PRODUCT_ID,Arrays.asList(productIds));
            iShoppingCartMapper.deleteByExample(example);
            return ServerResponse.createBySuccess("提交成功", businessOrder.getNumber());
        }
        return ServerResponse.createBySuccess("提交成功");
    }

    /**
     * 获取设计，精算订单的总价钱
     * @param orderDetailList
     * @return
     */
    private void setTotalPrice(List<HouseOrderDetailDTO> orderDetailList,String houseId,String workerTypeId,Order order,BusinessOrder businessOrder){
        Double totalPrice=0d;
        if(orderDetailList!=null&&orderDetailList.size()>0){
            for(HouseOrderDetailDTO orderDetailDTO:orderDetailList){
                totalPrice= MathUtil.add(totalPrice,orderDetailDTO.getPrice());
            }
            House house=houseMapper.selectByPrimaryKey(houseId);
            setHouseWorkerOrderInfo(house,workerTypeId,order,businessOrder,new BigDecimal(totalPrice));
        }
    }

    /***
     * 设计、精算生成工匠订单
     * @param house
     * @param workerTypeId
     * @param order
     */
    public void setHouseWorkerOrderInfo(House house,String workerTypeId,Order order,BusinessOrder businessOrder,BigDecimal paymentPrice){
        WorkerType wt = workerTypeMapper.selectByPrimaryKey(workerTypeId);
        /*
         * 生成工匠订单
         */
        HouseWorkerOrder hwo = houseWorkerOrderMapper.getByHouseIdAndWorkerTypeId(house.getId(),workerTypeId);
        if (hwo == null) {
            hwo = new HouseWorkerOrder(true);
            hwo.setHouseId(order.getHouseId());
            hwo.setWorkerTypeId(order.getWorkerTypeId());
            hwo.setWorkerType(wt.getType());
            hwo.setBusinessOrderNumber(businessOrder.getNumber());//业务订单号
            houseWorkerOrderMapper.insert(hwo);
        } else {
            hwo.setHouseId(order.getHouseId());
            hwo.setWorkerTypeId(order.getWorkerTypeId());
            hwo.setWorkerType(wt.getType());
            hwo.setBusinessOrderNumber(businessOrder.getNumber());//业务订单号
            houseWorkerOrderMapper.updateByPrimaryKey(hwo);
        }
        /*Double workerPrice = 0d;//精算工钱
        Double caiPrice =0d;//精算材料钱
        Double serPrice = 0d;//精算包工包料钱

        for (ShopGoodsDTO budgetLabelDTO : budgetLabelDTOS) {
            for (BudgetLabelDTO labelDTO : budgetLabelDTO.getLabelDTOS()) {
                for (BudgetLabelGoodsDTO good : labelDTO.getGoods()) {
                    if(good.getProductType()==0){//材料
                        caiPrice+=good.getTotalPrice().doubleValue();
                    }
                    if(good.getProductType()==1){//包工包料
                        serPrice+=good.getTotalPrice().doubleValue();
                    }
                    if(good.getProductType()==2){//人工
                        workerPrice+=good.getTotalPrice().doubleValue();
                    }
                }
            }
        }*/
        hwo.setWorkPrice(paymentPrice);//工钱
        hwo.setMaterialPrice(new BigDecimal(0d));//材料钱
        hwo.setTotalPrice(paymentPrice);//工钱+拆料
        houseWorkerOrderMapper.updateByPrimaryKey(hwo);

    }

    /**
     * 提交订单(精算)
     * @param taskId 工序ID
     * @return
     */
    public ServerResponse generateBudgetOrder(String userToken,String cityId,String taskId) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = (Member) object;

            HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(taskId);
            Example exampleOrder = new Example(Order.class);
            exampleOrder.createCriteria().andEqualTo(Order.HOUSE_ID, houseFlow.getHouseId())
                    .andEqualTo(Order.WORKER_TYPE_ID, houseFlow.getWorkerTypeId())
                    .andEqualTo(Order.ORDER_SOURCE,"1")
                    .andEqualTo(Order.ORDER_STATUS, "1")
                    .andEqualTo(Order.MEMBER_ID, member.getId());
            List<Order> list=orderMapper.selectByExample(exampleOrder);
            if(list.size()>0){
                return ServerResponse.createBySuccess("提交成功", list.get(0).getId());
            }
            //处理人工
            House house = houseMapper.selectByPrimaryKey(houseFlow.getHouseId());
            if(house!=null){
                cityId=house.getCityId();
            }
            WorkerType wt = workerTypeMapper.selectByPrimaryKey(houseFlow.getWorkerTypeId());
            List<ShopGoodsDTO> budgetLabelDTOS;
            BigDecimal paymentPrice = new BigDecimal(0);//总共钱
            BigDecimal freightPrice = new BigDecimal(0);//总运费
            BigDecimal totalMoveDost = new BigDecimal(0);//搬运费
            budgetLabelDTOS = forMasterAPI.queryShopGoods(houseFlow.getHouseId(), houseFlow.getWorkerTypeId(), house.getCityId());//精算工钱
            for (ShopGoodsDTO budgetLabelDTO : budgetLabelDTOS) {
                for (BudgetLabelDTO labelDTO : budgetLabelDTO.getLabelDTOS()) {
                    paymentPrice = paymentPrice.add(labelDTO.getTotalPrice());
                }
            }

            if (budgetLabelDTOS!=null) {
                Order order = new Order();
                if(house!=null) {
                    order.setHouseId(house.getId());
                }
                if(wt!=null) {
                    order.setWorkerTypeName(wt.getName() + "精算订单");
                }
                order.setCityId(cityId);
                order.setWorkerTypeId(houseFlow.getWorkerTypeId());
                order.setMemberId(member.getId());
//                order.setBusinessOrderNumber(businessOrderNumber);
                order.setType(3);
                order.setOrderNumber(System.currentTimeMillis() + "-" + (int) (Math.random() * 9000 + 1000));
                order.setTotalDiscountPrice(new BigDecimal(0));
                order.setTotalStevedorageCost(new BigDecimal(0));
                order.setTotalTransportationCost(new BigDecimal(0));
                order.setActualPaymentPrice(new BigDecimal(0));
                order.setOrderStatus("1");
                order.setOrderGenerationTime(new Date());
                order.setOrderSource(1);//精算制作
                order.setWorkerId(houseFlow.getWorkerId());
                order.setCreateBy(member.getId());
                orderMapper.insert(order);
                for (ShopGoodsDTO budgetLabelDTO : budgetLabelDTOS) {
                    Double freight=storefrontConfigAPI.getFreightPrice(budgetLabelDTO.getShopId(),budgetLabelDTO.getTotalMaterialPrice().doubleValue());
                    freightPrice=freightPrice.add(new BigDecimal(freight));
                    for (BudgetLabelDTO labelDTO : budgetLabelDTO.getLabelDTOS()) {
                        for (BudgetLabelGoodsDTO good : labelDTO.getGoods()) {
                            OrderItem orderItem = new OrderItem();
                            orderItem.setIsReservationDeliver(good.getIsReservationDeliver());
                            orderItem.setOrderId(order.getId());
                            orderItem.setHouseId(house.getId());
                            orderItem.setPrice(good.getPrice().doubleValue());//销售价
                            orderItem.setShopCount(good.getShopCount().doubleValue());//购买总数
                            orderItem.setUnitName(good.getUnitName());//单位
                            orderItem.setTotalPrice(good.getTotalPrice().doubleValue());//总价
                            orderItem.setProductName(good.getProductName());
                            orderItem.setProductSn(good.getProductSn());
                            orderItem.setCategoryId(good.getCategoryId());
                            orderItem.setProductId(good.getProductId());
                            orderItem.setImage(good.getImage());
                            orderItem.setCityId(cityId);
                            orderItem.setProductType(good.getProductType());
                            orderItem.setStorefontId(good.getStorefontId());
                            orderItem.setAskCount(0d);
                            orderItem.setDiscountPrice(0d);
                            orderItem.setActualPaymentPrice(0d);
                            orderItem.setStevedorageCost(0d);
                            orderItem.setTransportationCost(0d);
                            if(good.getProductType()==0&&freight>0){
                                //均摊运费
                                Double transportationCost=(orderItem.getTotalPrice()/budgetLabelDTO.getTotalMaterialPrice().doubleValue())*freight;
                                orderItem.setTransportationCost(transportationCost);
                            }
                            //搬运费运算
                            Double moveDost=masterCostAcquisitionService.getStevedorageCost(house.getId(),orderItem.getProductId(),orderItem.getShopCount());
                            totalMoveDost=totalMoveDost.add(new BigDecimal(moveDost));
                            if(moveDost>0){
                                //均摊运费
                                orderItem.setStevedorageCost(moveDost);
                            }
                            orderItem.setOrderStatus("1");//1待付款，2已付款，3待收货，4已完成，5已取消，6已退货，7已关闭
                            orderItem.setCreateBy(member.getId());
                            orderItemMapper.insert(orderItem);

                            //添加增殖类商品
                            Example example=new Example(DeliverOrderAddedProduct.class);
                            example.createCriteria().andEqualTo(DeliverOrderAddedProduct.ANY_ORDER_ID,good.getId())
                                    .andEqualTo(DeliverOrderAddedProduct.DATA_STATUS,0);
                            List<DeliverOrderAddedProduct> deliverOrderAddedProducts = masterDeliverOrderAddedProductMapper.selectByExample(example);
                            List<String> addedProductIds=deliverOrderAddedProducts
                                    .stream()
                                    .map(DeliverOrderAddedProduct::getAddedProductId)
                                    .collect(Collectors.toList());
                            if(addedProductIds.size()>0) {
                                setAddedProduct(orderItem.getId(),addedProductIds.toString(),"1");
                            }

                            for (DeliverOrderAddedProduct deliverOrderAddedProduct : deliverOrderAddedProducts) {
                                BigDecimal totalPrice = new BigDecimal(deliverOrderAddedProduct.getPrice()*orderItem.getShopCount());
                                paymentPrice = paymentPrice.add(totalPrice);
                            }
                        }
                    }
                }

                paymentPrice=paymentPrice.add(order.getTotalTransportationCost());
                paymentPrice=paymentPrice.add(order.getTotalStevedorageCost());

                // 生成支付业务单
                Example example = new Example(BusinessOrder.class);
                example.createCriteria().andEqualTo(BusinessOrder.TASK_ID, order.getId()).andNotEqualTo(BusinessOrder.STATE, 4).andNotEqualTo(BusinessOrder.STATE, 3);
                List<BusinessOrder> businessOrderList = businessOrderMapper.selectByExample(example);
                BusinessOrder businessOrder = null;
                if (businessOrderList.size() > 0) {
                    businessOrder = businessOrderList.get(0);
                }
                if (businessOrderList.size() == 0) {
                    businessOrder = new BusinessOrder();
                    businessOrder.setMemberId(member.getId());
                    businessOrder.setHouseId(house.getId());
                    businessOrder.setNumber(System.currentTimeMillis() + "-" + (int) (Math.random() * 9000 + 1000));
                    businessOrder.setState(1);//刚生成
                    businessOrder.setTotalPrice(paymentPrice);
                    businessOrder.setDiscountsPrice(new BigDecimal(0));
                    businessOrder.setPayPrice(paymentPrice);
                    businessOrder.setType(1);//记录支付类型任务类型
                    businessOrder.setTaskId(order.getId());//保存任务ID
                    businessOrderMapper.insert(businessOrder);
                }
                order.setTotalTransportationCost(freightPrice);//总运费
                order.setTotalStevedorageCost(totalMoveDost);//总搬运费
                order.setBusinessOrderNumber(businessOrder.getNumber());
                order.setTotalAmount(paymentPrice);// 订单总额(工钱)
                orderMapper.updateByPrimaryKeySelective(order);

                budgetCorrect(order,null,null);
                //该工钟所有保险
                example = new Example(WorkerTypeSafe.class);
                example.createCriteria().andEqualTo(WorkerTypeSafe.WORKER_TYPE_ID, wt.getId());
                List<WorkerTypeSafe> wtsList = workerTypeSafeMapper.selectByExample(example);
                WorkerTypeSafeOrder workerTypeSafeOrder = workerTypeSafeOrderMapper.getByNotPay(wt.getId(), house.getId());
                if (workerTypeSafeOrder == null) {//默认生成一条
                    if (wtsList.size() > 0) {
                        workerTypeSafeOrder = new WorkerTypeSafeOrder();
                        workerTypeSafeOrder.setWorkerTypeSafeId(wtsList.get(0).getId()); // 向保险订单中存入保险服务类型的id
                        workerTypeSafeOrder.setHouseId(house.getId()); // 存入房子id
                        workerTypeSafeOrder.setWorkerTypeId(wt.getId()); // 工种id
                        workerTypeSafeOrder.setWorkerType(wt.getType());
                        workerTypeSafeOrder.setPrice(wtsList.get(0).getPrice().multiply(house.getSquare()));
                        workerTypeSafeOrder.setState(0);  //未支付
                        workerTypeSafeOrderMapper.insert(workerTypeSafeOrder);
                    }
                }
                if (order.getOrderSource() == 1) {//支付
                    /*
                     * 生成工匠订单
                     */
                    HouseWorkerOrder hwo = houseWorkerOrderMapper.getByHouseIdAndWorkerTypeId(house.getId(), order.getWorkerTypeId());
                    if (hwo == null) {
                        hwo = new HouseWorkerOrder(true);
                        hwo.setHouseId(order.getHouseId());
                        hwo.setWorkerTypeId(order.getWorkerTypeId());
                        hwo.setWorkerType(wt.getType());
                        hwo.setBusinessOrderNumber(businessOrder.getNumber());//业务订单号
                        houseWorkerOrderMapper.insert(hwo);
                    } else {
                        hwo.setHouseId(order.getHouseId());
                        hwo.setWorkerTypeId(order.getWorkerTypeId());
                        hwo.setWorkerType(wt.getType());
                        hwo.setBusinessOrderNumber(businessOrder.getNumber());//业务订单号
                        houseWorkerOrderMapper.updateByPrimaryKey(hwo);
                    }
                    Double workerPrice = 0d;//精算工钱
                    Double caiPrice =0d;//精算材料钱
                    Double serPrice = 0d;//精算包工包料钱

                    for (ShopGoodsDTO budgetLabelDTO : budgetLabelDTOS) {
                        for (BudgetLabelDTO labelDTO : budgetLabelDTO.getLabelDTOS()) {
                            for (BudgetLabelGoodsDTO good : labelDTO.getGoods()) {
                                if(good.getProductType()==0){//材料
                                    caiPrice+=good.getTotalPrice().doubleValue();
                                }
                                if(good.getProductType()==1){//包工包料
                                    serPrice+=good.getTotalPrice().doubleValue();
                                }
                                if(good.getProductType()==2){//人工
                                    workerPrice+=good.getTotalPrice().doubleValue();
                                }
                            }
                        }
                    }
                    hwo.setWorkPrice(new BigDecimal(workerPrice));//工钱
                    hwo.setMaterialPrice(new BigDecimal(caiPrice + serPrice));//材料钱
                    hwo.setTotalPrice(paymentPrice);//工钱+拆料
                    houseWorkerOrderMapper.updateByPrimaryKey(hwo);
                    if (order.getOrderSource() == 1 && wt.getType() > 2) {//精算师和设计师不存在保险订单
                        //查出有没有生成保险订单
                        example = new Example(WorkerTypeSafeOrder.class);
                        example.createCriteria().andEqualTo(WorkerTypeSafeOrder.HOUSE_ID, house.getId()).andEqualTo(WorkerTypeSafeOrder.WORKER_TYPE_ID, wt.getId()).andEqualTo(WorkerTypeSafeOrder.DATA_STATUS, 0);
                        List<WorkerTypeSafeOrder> wtsoList = workerTypeSafeOrderMapper.selectByExample(example);
                        if (wtsoList.size() == 1) {
                            workerTypeSafeOrder = wtsoList.get(0);
                            workerTypeSafeOrder.setBusinessOrderNumber(businessOrder.getNumber());
                            //保存业务订单号
                            workerTypeSafeOrderMapper.updateByPrimaryKey(workerTypeSafeOrder);
                            UpgradeSafeDTO upgradeSafeDTO = new UpgradeSafeDTO();//保险服务
                            upgradeSafeDTO.setTitle("保险服务");
                            upgradeSafeDTO.setType(0);//单选
                            WorkerTypeSafe wts = workerTypeSafeMapper.selectByPrimaryKey(workerTypeSafeOrder.getWorkerTypeSafeId());
                            List<SafeTypeDTO> safeTypeDTOList = new ArrayList<>();
                            SafeTypeDTO safeTypeDTO = new SafeTypeDTO();
                            safeTypeDTO.setName(wts.getName());
                            BigDecimal price = wts.getPrice().multiply(house.getSquare());
                            safeTypeDTO.setPrice("¥" + String.format("%.2f", price.doubleValue()));
                            safeTypeDTO.setSelected(1);//勾
                            safeTypeDTOList.add(safeTypeDTO);
                            upgradeSafeDTO.setSafeTypeDTOList(safeTypeDTOList);
                            paymentPrice = paymentPrice.add(wts.getPrice().multiply(house.getSquare()));//钱加上
                        } else if (wtsoList.size() > 1) {
                            return ServerResponse.createByErrorMessage("保险订单错误,联系平台部");
                        }
                    }
                }
                return ServerResponse.createBySuccess("提交成功", businessOrder.getNumber());
            }
            return ServerResponse.createBySuccess("提交成功");
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ServerResponse.createByErrorMessage("提交失败：原因："+e.getMessage());
        }
    }
    /**
     * 更新订单
     * @return
     */
    public ServerResponse editOrder(String userToken, String orderId, String workerId, String addressId, String houseId) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = (Member) object;
            Order order= orderMapper.selectByPrimaryKey(orderId);
            order.setMemberId(member.getId());
            order.setWorkerId(workerId);
            order.setAddressId(addressId);
            order.setHouseId(houseId);
            BigDecimal paymentPrice = new BigDecimal(0);//总共钱
            BigDecimal totalMoveDost = new BigDecimal(0);//搬运费

            Example  example = new Example(OrderItem.class);
            example.createCriteria().andEqualTo(OrderItem.ORDER_ID, order.getId());
            List<OrderItem> orderItems = orderItemMapper.selectByExample(example);//精算工钱
            for (OrderItem orderItem : orderItems) {
                paymentPrice = paymentPrice.add(new BigDecimal(orderItem.getTotalPrice()));
                if(!CommonUtil.isEmpty(order.getHouseId())) {
                    //搬运费运算
                    Double moveDost = masterCostAcquisitionService.getStevedorageCost(order.getHouseId(), orderItem.getProductId(), orderItem.getShopCount());
                    totalMoveDost = totalMoveDost.add(new BigDecimal(moveDost));
                    if (moveDost > 0) {
                        //均摊运费
                        orderItem.setStevedorageCost(moveDost);
                    }
                }
                orderItem.setUpdateBy(member.getId());
                orderItemMapper.insert(orderItem);
            }

            paymentPrice=paymentPrice.add(order.getTotalTransportationCost());
            paymentPrice=paymentPrice.add(order.getTotalStevedorageCost());
            // 支付业务单
            example = new Example(BusinessOrder.class);
            example.createCriteria().andEqualTo(BusinessOrder.TASK_ID, order.getId()).andNotEqualTo(BusinessOrder.STATE, 4).andNotEqualTo(BusinessOrder.STATE, 3);
            List<BusinessOrder> businessOrderList = businessOrderMapper.selectByExample(example);
            BusinessOrder businessOrder = null;
            if (businessOrderList.size() > 0) {
                businessOrder = businessOrderList.get(0);
                businessOrder.setHouseId(order.getHouseId());
                businessOrder.setTotalPrice(paymentPrice);
                businessOrder.setPayPrice(paymentPrice);
                businessOrderMapper.updateByPrimaryKeySelective(businessOrder);
            }
            order.setTotalStevedorageCost(totalMoveDost);//总搬运费
            order.setBusinessOrderNumber(businessOrder.getNumber());
            order.setTotalAmount(paymentPrice);// 订单总额(工钱)
            orderMapper.updateByPrimaryKeySelective(order);

            return ServerResponse.createBySuccess("更新成功", order.getId());
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ServerResponse.createByErrorMessage("更新成功：原因："+e.getMessage());
        }
    }
    /**
     * 可用优惠券数据
     *
     * @param businessOrderNumber 订单号
     * @return
     */
    public List<ActivityRedPackRecordDTO> discountPage(String businessOrderNumber) {
        try {
            Example example = new Example(BusinessOrder.class);
            example.createCriteria().andEqualTo("number", businessOrderNumber).andEqualTo("state", 1);
            List<BusinessOrder> businessOrderList = businessOrderMapper.selectByExample(example);
            if (businessOrderList.size() == 0) {
                return null;
            }
            //满足条件的优惠券记录
            List<ActivityRedPackRecordDTO> redPacetResultList = new ArrayList<>();
            BusinessOrder businessOrder = businessOrderList.get(0);
            House house = houseMapper.selectByPrimaryKey(businessOrder.getHouseId());
            ActivityRedPackRecord activityRedPackRecord = new ActivityRedPackRecord();
            activityRedPackRecord.setEndDate(new Date());
            activityRedPackRecord.setMemberId(businessOrder.getMemberId());
            activityRedPackRecord.setCityId(house.getCityId());
            activityRedPackRecord.setHaveReceive(0);
            List<ActivityRedPackRecordDTO> redPacketRecordList = activityRedPackRecordMapper.queryActivityRedPackRecords(activityRedPackRecord);

            activityRedPackRecord.setHaveReceive(1);
            activityRedPackRecord.setBusinessOrderNumber(businessOrder.getNumber());
            List<ActivityRedPackRecordDTO> redPacketRecordSelectList = activityRedPackRecordMapper.queryActivityRedPackRecords(activityRedPackRecord);
            if (redPacketRecordSelectList != null && redPacketRecordSelectList.size() > 0) {
                redPacketRecordList.addAll(redPacketRecordSelectList);
            }
            if (redPacketRecordList.size() == 0) {
                return null;
            }
            String houseFlowId = businessOrder.getTaskId();
            ServerResponse retMaterial = budgetMaterialAPI.queryBudgetMaterialByHouseFlowId(house.getCityId(), houseFlowId);
            ServerResponse retWorker = budgetWorkerAPI.queryBudgetWorkerByHouseFlowId(house.getCityId(), houseFlowId);

            if (retMaterial.getResultObj() != null || retWorker.getResultObj() != null) {
                List<BudgetMaterial> budgetMaterialList = JSONObject.parseArray(retMaterial.getResultObj().toString(), BudgetMaterial.class);
                List<BudgetMaterial> budgetWorkerList = JSONObject.parseArray(retWorker.getResultObj().toString(), BudgetMaterial.class);

                for (ActivityRedPackRecordDTO redPacketRecord : redPacketRecordList) {
                    BigDecimal workerTotal = new BigDecimal(0);
                    BigDecimal goodsTotal = new BigDecimal(0);
                    BigDecimal productTotal = new BigDecimal(0);

                    if (budgetWorkerList.size() > 0) {
                        for (BudgetMaterial budgetWorker : budgetWorkerList) {
                            //判断工种的优惠券是否匹配
                            if (budgetWorker.getWorkerTypeId().equals(redPacketRecord.getRedPack().getFromObject()) && redPacketRecord.getRedPack().getFromObjectType() == 0) {
                                workerTotal = workerTotal.add(new BigDecimal(budgetWorker.getTotalPrice()));
                            }
                        }
                    }
                    if (budgetMaterialList.size() > 0) {
                        for (BudgetMaterial budgetMaterial : budgetMaterialList) {
                            //判断材料优惠券是否匹配
                            if (budgetMaterial.getGoodsId().equals(redPacketRecord.getRedPack().getFromObject()) && redPacketRecord.getRedPack().getFromObjectType() == 1) {
                                goodsTotal = goodsTotal.add(new BigDecimal(budgetMaterial.getTotalPrice()));
                            }
                            //判断货品的优惠券是否匹配
                            if (budgetMaterial.getProductId().equals(redPacketRecord.getRedPack().getFromObject()) && redPacketRecord.getRedPack().getFromObjectType() == 2) {
                                productTotal = productTotal.add(new BigDecimal(budgetMaterial.getTotalPrice()));
                            }
                        }
                    }
                    //判断优惠券类型是否为满减券
                    if (redPacketRecord.getRedPack().getType() == 0) {
                        ///判断人工金额是否满足优惠上限金额
                        if (redPacketRecord.getRedPack().getFromObjectType() == 0 && workerTotal.compareTo(redPacketRecord.getRedPackRule().getSatisfyMoney()) >= 0) {
                            redPacetResultList.add(redPacketRecord);
                        } else
                            //判断材料金额是否满足优惠上限金额
                            if (redPacketRecord.getRedPack().getFromObjectType() == 1 && goodsTotal.compareTo(redPacketRecord.getRedPackRule().getSatisfyMoney()) >= 0) {
                                redPacetResultList.add(redPacketRecord);
                            } else
                                //判断货品金额是否满足优惠上限金额
                                if (redPacketRecord.getRedPack().getFromObjectType() == 2 && productTotal.compareTo(redPacketRecord.getRedPackRule().getSatisfyMoney()) >= 0) {
                                    redPacetResultList.add(redPacketRecord);
                                }
                    }
                    //判断优惠券类型是否为折扣券或代金券
                    if ((redPacketRecord.getRedPack().getType() == 1 || redPacketRecord.getRedPack().getType() == 2) &&
                            (workerTotal.doubleValue() > 0 || goodsTotal.doubleValue() > 0 || productTotal.doubleValue() > 0)) {
                        redPacetResultList.add(redPacketRecord);
                    }
                }

            }
            if (redPacetResultList.size() == 0) {
                return null;
            }
            return redPacetResultList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 购物车
     *
     * @param taskId houseFlowId,mendOrderId,houseFlowApplyId
     * @param type   1精算商品,2购物车商品,3立即购商品
     */
    public ServerResponse getPaymentPage(String userToken,  String taskId, String cityId,String houseId,String productJsons,int type) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = (Member) object;

            PaymentDTO paymentDTO = new PaymentDTO();
            paymentDTO.setType(4);
            paymentDTO.setDiscountsPrice(new BigDecimal(0));
            BigDecimal totalPrice = new BigDecimal(0);//总价
            BigDecimal freightPrice = new BigDecimal(0);//总运费
            BigDecimal totalMoveDost = new BigDecimal(0);//搬运费
            if (type == 1) {//精算商品
                HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(taskId);
                House house = houseMapper.selectByPrimaryKey(houseFlow.getHouseId());
                if (houseFlow.getWorkType() == 2) {
                    return ServerResponse.createByErrorMessage("等待工匠抢单");
                }
                if (houseFlow.getWorkType() == 4) {
                    return ServerResponse.createByErrorMessage("该订单已支付");
                }
                WorkerType workerType = workerTypeMapper.selectByPrimaryKey(houseFlow.getWorkerTypeId());
                paymentDTO.setWorkerTypeName(workerType.getName());
                List<ShopGoodsDTO> budgetLabelDTOS = forMasterAPI.queryShopGoods(houseFlow.getHouseId(), houseFlow.getWorkerTypeId(), house.getCityId());//精算工钱
                for (ShopGoodsDTO budgetLabelDTO : budgetLabelDTOS) {
                    BigDecimal moveDostTotal=new BigDecimal(0);
                    Double freight=storefrontConfigAPI.getFreightPrice(budgetLabelDTO.getShopId(),budgetLabelDTO.getTotalMaterialPrice().doubleValue());
                    budgetLabelDTO.setFreight(new BigDecimal(freight));
                    freightPrice=freightPrice.add(new BigDecimal(freight));
                    for (BudgetLabelDTO labelDTO : budgetLabelDTO.getLabelDTOS()) {
                        totalPrice = totalPrice.add(labelDTO.getTotalPrice());
                        if(!CommonUtil.isEmpty(houseFlow.getHouseId())) {
                            for (BudgetLabelGoodsDTO good : labelDTO.getGoods()) {
                                //搬运费运算
                                Double moveDost = masterCostAcquisitionService.getStevedorageCost(houseFlow.getHouseId(), good.getProductId(), good.getShopCount());
                                totalMoveDost = totalMoveDost.add(new BigDecimal(moveDost));
                                moveDostTotal = moveDostTotal.add(new BigDecimal(moveDost));

                                //添加增殖类商品
                                Example example=new Example(DeliverOrderAddedProduct.class);
                                example.createCriteria().andEqualTo(DeliverOrderAddedProduct.ANY_ORDER_ID,good.getId())
                                        .andEqualTo(DeliverOrderAddedProduct.DATA_STATUS,0);
                                List<DeliverOrderAddedProduct> deliverOrderAddedProducts = masterDeliverOrderAddedProductMapper.selectByExample(example);
                                good.setAddedProducts(deliverOrderAddedProducts);

                                for (DeliverOrderAddedProduct deliverOrderAddedProduct : deliverOrderAddedProducts) {
                                    BigDecimal totalAddedProduct = new BigDecimal(deliverOrderAddedProduct.getPrice()*good.getShopCount());
                                    totalPrice = totalPrice.add(totalAddedProduct);
                                }
                            }
                        }
                    }
                    budgetLabelDTO.setMoveDost(moveDostTotal);
                }
                paymentDTO.setDatas(budgetLabelDTOS);
                paymentDTO.setType(3);
                //该工钟所有保险
                Example example = new Example(WorkerTypeSafe.class);
                example.createCriteria().andEqualTo(WorkerTypeSafe.WORKER_TYPE_ID, houseFlow.getWorkerTypeId());
                List<WorkerTypeSafe> wtsList = workerTypeSafeMapper.selectByExample(example);


                //有保险服务
                if (wtsList.size() > 0) {
                    //查出有没有生成保险订单
                    example = new Example(WorkerTypeSafeOrder.class);
                    example.createCriteria().andEqualTo(WorkerTypeSafeOrder.HOUSE_ID, houseFlow.getHouseId()).andEqualTo(WorkerTypeSafeOrder.DATA_STATUS, 0)
                            .andEqualTo(WorkerTypeSafeOrder.WORKER_TYPE_ID, houseFlow.getWorkerTypeId());
                    List<WorkerTypeSafeOrder> wtsoList = workerTypeSafeOrderMapper.selectByExample(example);
                    UpgradeSafeDTO upgradeSafeDTO = new UpgradeSafeDTO();//保险服务
                    upgradeSafeDTO.setTitle("保险服务");
                    upgradeSafeDTO.setType(0);//单选
                    List<SafeTypeDTO> safeTypeDTOList = new ArrayList<>();
                    for (WorkerTypeSafe wts : wtsList) {
                        SafeTypeDTO safeTypeDTO = new SafeTypeDTO();
                        safeTypeDTO.setName(wts.getName());
                        BigDecimal price = wts.getPrice().multiply(house.getSquare());
                        safeTypeDTO.setPrice("¥" + String.format("%.2f", price.doubleValue()));
                        safeTypeDTO.setSelected(0);//未勾
                        safeTypeDTO.setWorkerTypeSafeId(wts.getId());//保险类型id
                        safeTypeDTO.setHouseFlowId(houseFlow.getId());
                        for (WorkerTypeSafeOrder wtso : wtsoList) {
                            if (wts.getId().equals(wtso.getWorkerTypeSafeId())) {
                                safeTypeDTO.setSelected(1);//勾上
                                totalPrice = totalPrice.add(wts.getPrice().multiply(house.getSquare()));
                                break;
                            }
                        }
                        safeTypeDTOList.add(safeTypeDTO);
                    }
                    upgradeSafeDTO.setSafeTypeDTOList(safeTypeDTOList);
                    paymentDTO.setUpgradeSafeDTO(upgradeSafeDTO);//保险
                }
            } else if (type == 2 || type == 3) {//购物车商品
                JSONArray productArray= JSON.parseArray(productJsons);
                if(productArray.size()==0){
                    return ServerResponse.createByErrorMessage("参数错误");
                }
                Map<String,ShoppingCartListDTO> productMap = new HashMap<>();
                String[] productIds=new String[productArray.size()];
                for (int i = 0; i < productArray.size(); i++) {
                    JSONObject productObj = productArray.getJSONObject(i);
                    productMap.put(productObj.getString(ProductDTO.PRODUCT_ID),BeanUtils.mapToBean(ShoppingCartListDTO.class,productObj));
                    productIds[i]=productObj.getString(ProductDTO.PRODUCT_ID);

                    //更新购物车数量
                    if(type==2){
                        Example  example = new Example(ShoppingCart.class);
                        example.createCriteria().andEqualTo(ShoppingCart.PRODUCT_ID, productObj.getString(ProductDTO.PRODUCT_ID))
                                .andEqualTo(ShoppingCart.MEMBER_ID, member.getId());
                        ShoppingCart shoppingCart = new ShoppingCart();
                        shoppingCart.setId(null);
                        shoppingCart.setShopCount(Double.parseDouble(productObj.getString(ProductDTO.SHOP_COUNT)));
                        iShoppingCartMapper.updateByExampleSelective(shoppingCart,example);
                    }
                }
                if(CommonUtil.isEmpty(houseId)) {
                    House house=getHouseId(member.getId());
                    if(house!= null) {
                        houseId=house.getId();
                    }
                }
                List<ShoppingCartDTO> shoppingCartDTOS=iShoppingCartMapper.queryShoppingCartDTOS(productIds);
                for (ShoppingCartDTO shoppingCartDTO : shoppingCartDTOS) {
                    BigDecimal totalSellPrice = new BigDecimal(0);//总价
                    for (ShoppingCartListDTO shoppingCartListDTO : shoppingCartDTO.getShoppingCartListDTOS()) {
                        ShoppingCartListDTO parmDTO=productMap.get(shoppingCartListDTO.getProductId());
                        shoppingCartListDTO.setShopCount(parmDTO.getShopCount());
                        totalSellPrice = totalSellPrice.add(new BigDecimal(shoppingCartListDTO.getPrice()*parmDTO.getShopCount()));
                        totalPrice = totalPrice.add(new BigDecimal(shoppingCartListDTO.getPrice()*parmDTO.getShopCount()));
                        if(!CommonUtil.isEmpty(houseId)) {
                            //搬运费运算
                            Double moveDost = masterCostAcquisitionService.getStevedorageCost(houseId, shoppingCartListDTO.getProductId(), parmDTO.getShopCount());
                            totalMoveDost = totalMoveDost.add(new BigDecimal(moveDost));
                        }
                        if(paymentDTO.getType()!=1 && (shoppingCartListDTO.getProductType()==0||shoppingCartListDTO.getProductType()==1)){
                            paymentDTO.setType(2);
                        }
                        if(shoppingCartListDTO.getProductType()==2){
                            paymentDTO.setType(1);
                        }
                        if(!CommonUtil.isEmpty(parmDTO.getAddedProductIds())) {
                            String[] addedProductIdList=parmDTO.getAddedProductIds().split(",");
                            List<DeliverOrderAddedProduct> deliverOrderAddedProducts =new ArrayList<>();
                            for (String addedProductId : addedProductIdList) {
                                StorefrontProduct product = iMasterStorefrontProductMapper.selectByPrimaryKey(addedProductId);
                                DeliverOrderAddedProduct deliverOrderAddedProduct1 = new DeliverOrderAddedProduct();
                                deliverOrderAddedProduct1.setAnyOrderId(shoppingCartListDTO.getId());
                                deliverOrderAddedProduct1.setAddedProductId(addedProductId);
                                deliverOrderAddedProduct1.setPrice(product.getSellPrice());
                                deliverOrderAddedProduct1.setProductName(product.getProductName());
                                deliverOrderAddedProduct1.setSource("4");
                                deliverOrderAddedProducts.add(deliverOrderAddedProduct1);
                                totalPrice = totalPrice.add(new BigDecimal(product.getSellPrice()*parmDTO.getShopCount()));
                            }
                            //获取购物车增值商品信息
                            shoppingCartListDTO.setAddedProducts(deliverOrderAddedProducts);
                        }
                    }
                    Double freight=storefrontConfigAPI.getFreightPrice(shoppingCartDTO.getStorefrontId(),totalSellPrice.doubleValue());
                    freightPrice=freightPrice.add(new BigDecimal(freight));
                    shoppingCartDTO.setTotalPrice(totalSellPrice);
                }
                paymentDTO.setDatas(shoppingCartDTOS);

            } else {
                return ServerResponse.createByErrorMessage("参数错误");
            }
            paymentDTO.setDiscounts(0);
            paymentDTO.setFreight(freightPrice);//运费
            paymentDTO.setMoveDost(totalMoveDost);//搬运费
            BigDecimal payPrice = totalPrice.subtract(paymentDTO.getDiscountsPrice());
            payPrice = payPrice.add(paymentDTO.getFreight());
            payPrice = payPrice.add(paymentDTO.getMoveDost());
            paymentDTO.setTotalPrice(totalPrice);
            paymentDTO.setPayPrice(payPrice);
            paymentDTO.setTaskId(taskId);
            return ServerResponse.createBySuccess("查询成功", paymentDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 未付款 管家后
     */
    public ServerResponse setPaying(String houseId) {
        String address = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
        Example example = new Example(HouseFlow.class);
        example.createCriteria().andEqualTo(HouseFlow.HOUSE_ID, houseId).andEqualTo(HouseFlow.STATE, 0)
                .andGreaterThan(HouseFlow.WORKER_TYPE, 2);
        example.orderBy(HouseFlow.SORT).asc();
        List<HouseFlow> houseFlowList = houseFlowMapper.selectByExample(example);
        List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
        House house = houseMapper.selectByPrimaryKey(houseId);
        for (HouseFlow hf : houseFlowList) {
            Double caiPrice = forMasterAPI.nonPaymentCai(houseId, hf.getWorkerTypeId(), house.getCityId());//精算未付款材料钱
            Double serPrice = forMasterAPI.nonPaymentSer(houseId, hf.getWorkerTypeId(), house.getCityId());//精算未付款服务钱
            if (caiPrice == null) {
                caiPrice = 0.0;
            }
            if (serPrice == null) {
                serPrice = 0.0;
            }
            if (caiPrice > 0 || serPrice > 0) {
                WorkerType workerType = workerTypeMapper.selectByPrimaryKey(hf.getWorkerTypeId());
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("name", workerType.getName());
                map.put("image", address + workerType.getImage());
                map.put("houseId", houseId);
                map.put("taskId", hf.getId());
                map.put("type", 4);
                mapList.add(map);
            }
        }
        return ServerResponse.createBySuccess("查询成功", mapList);
    }


    /**
     * 更新(供应商/店铺)(余额/滞纳金)
     * @param djSupplierPayOrder
     * @return
     */
    public ServerResponse setSurplusMoney(DjSupplierPayOrder djSupplierPayOrder) {
        try {
            if(djSupplierPayOrder.getState()==1) {
                AccountFlowRecord accountFlowRecord = new AccountFlowRecord();
                accountFlowRecord.setState(2);
                accountFlowRecord.setDefinedAccountId(djSupplierPayOrder.getSupplierId());
                accountFlowRecord.setCreateBy(djSupplierPayOrder.getUserId());
                accountFlowRecord.setHouseOrderId(djSupplierPayOrder.getId());
                if (djSupplierPayOrder.getState() == 1 && djSupplierPayOrder.getSourceType() == 1) {
                    DjSupplier djSupplier = iMaterSupplierMapper.selectByPrimaryKey(djSupplierPayOrder.getSupplierId());
                    accountFlowRecord.setAmountBeforeMoney(djSupplier.getTotalAccount());//入账前金额
                    if (djSupplierPayOrder.getBusinessOrderType().equals("1")) {
                        djSupplier.setTotalAccount(djSupplier.getTotalAccount() + djSupplierPayOrder.getPrice());
                        djSupplier.setSurplusMoney(djSupplier.getSurplusMoney() + djSupplierPayOrder.getPrice());
                        accountFlowRecord.setDefinedName("供应商充值：" + djSupplierPayOrder.getPrice());
                    } else if (djSupplierPayOrder.getBusinessOrderType().equals("2")) {
                        djSupplier.setRetentionMoney(djSupplier.getRetentionMoney() + djSupplierPayOrder.getPrice());
                        accountFlowRecord.setDefinedName("供应商交纳滞留金：" + djSupplierPayOrder.getPrice());
                    }
                    iMaterSupplierMapper.updateByPrimaryKeySelective(djSupplier);
                    accountFlowRecord.setFlowType("2");
                    accountFlowRecord.setMoney(djSupplierPayOrder.getPrice());
                    accountFlowRecord.setAmountAfterMoney(djSupplier.getTotalAccount());//入账后金额

                } else if (djSupplierPayOrder.getState() == 1 && djSupplierPayOrder.getSourceType() == 2) {
                    Storefront storefront = iMasterStorefrontMapper.selectByPrimaryKey(djSupplierPayOrder.getSupplierId());
                    accountFlowRecord.setAmountBeforeMoney(storefront.getTotalAccount());//入账前金额
                    if (djSupplierPayOrder.getBusinessOrderType().equals("1")) {
                        storefront.setTotalAccount(storefront.getTotalAccount() + djSupplierPayOrder.getPrice());
                        storefront.setSurplusMoney(storefront.getSurplusMoney()+ djSupplierPayOrder.getPrice());
                        accountFlowRecord.setDefinedName("店铺充值：" + djSupplierPayOrder.getPrice());
                    } else if (djSupplierPayOrder.getBusinessOrderType().equals("2")) {
                        storefront.setRetentionMoney(storefront.getRetentionMoney() + djSupplierPayOrder.getPrice());
                        accountFlowRecord.setDefinedName("店铺交纳滞留金：" + djSupplierPayOrder.getPrice());
                    }
                    iMasterStorefrontMapper.updateByPrimaryKeySelective(storefront);
                    accountFlowRecord.setFlowType("1");
                    accountFlowRecord.setMoney(storefront.getTotalAccount());//入账后金额
                    accountFlowRecord.setAmountAfterMoney(storefront.getTotalAccount());//入账后金额
                }
                //生成流水
                iMasterAccountFlowRecordMapper.insert(accountFlowRecord);
                return ServerResponse.createBySuccessMessage("充值成功");
            }
            return ServerResponse.createByErrorMessage("充值失败");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("充值失败",e);
            return ServerResponse.createByErrorMessage("充值失败");
        }
    }

    /**
     *查询保险信息
     * @param userToken
     * @param workerId
     * @return
     */
    public ServerResponse queryInsuranceInfo(String userToken, String workerId) {


        //获取图片url
        String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);

        Example example = new Example(Insurance.class);
        example.createCriteria().andEqualTo(Insurance.WORKER_ID, workerId);
        List<Insurance> houseFlowList = insuranceMapper.selectByExample(example);

        for (Insurance insurance: houseFlowList) {
            insurance.setHead(imageAddress + insurance.getHead());
        }

        return ServerResponse.createBySuccess("查询成功", houseFlowList);
    }

    /**
     * 获取业主当前默认选中的房子ID(不包含休眠，结束的房子)
     */
    public House getHouseId(String memberId) {
        House houseId = null;
        Example example = new Example(House.class);
        example.createCriteria()
                .andEqualTo(House.MEMBER_ID, memberId)
                .andNotEqualTo(House.VISIT_STATE, 2)
                .andNotEqualTo(House.VISIT_STATE, 3)
                .andNotEqualTo(House.VISIT_STATE, 4);
        List<House> houseList = houseMapper.selectByExample(example);
        //业主待处理任务
        if (houseList.size() > 0) {
            for (House house : houseList) {
                if (house.getIsSelect() == 1) {//当前选中且开工
                    houseId = house;
                }
            }
            if (houseId == null) {//有很多房子但是没有isSelect为1的
                houseId = houseList.get(0);
            }
        }
        return houseId;
    }

    /**
     *  更新/设置增值商品
     * @param orderId 来源订单ID
     * @param addedProductIds 增值商品 多个以逗号分隔
     * @param source 来源类型
     */
    private void setAddedProduct(String orderId,String addedProductIds,String source){
        if(!CommonUtil.isEmpty(orderId)) {
            Example example=new Example(DeliverOrderAddedProduct.class);
            example.createCriteria().andEqualTo(DeliverOrderAddedProduct.ANY_ORDER_ID,orderId).andEqualTo(DeliverOrderAddedProduct.SOURCE,source);
            masterDeliverOrderAddedProductMapper.deleteByExample(example);
            if(!CommonUtil.isEmpty(addedProductIds)) {
                String[] addedProductIdList=addedProductIds.split(",");
                for (String addedProductId : addedProductIdList) {
                    StorefrontProduct product = iMasterStorefrontProductMapper.selectByPrimaryKey(addedProductId);
                    DeliverOrderAddedProduct deliverOrderAddedProduct1 = new DeliverOrderAddedProduct();
                    deliverOrderAddedProduct1.setAnyOrderId(orderId);
                    deliverOrderAddedProduct1.setAddedProductId(addedProductId);
                    deliverOrderAddedProduct1.setPrice(product.getSellPrice());
                    deliverOrderAddedProduct1.setProductName(product.getProductName());
                    deliverOrderAddedProduct1.setSource(source);
                    masterDeliverOrderAddedProductMapper.insert(deliverOrderAddedProduct1);
                }
            }
        }
    }
}
