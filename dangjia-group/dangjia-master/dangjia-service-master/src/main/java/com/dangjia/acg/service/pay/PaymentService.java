package com.dangjia.acg.service.pay;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.api.StorefrontConfigAPI;
import com.dangjia.acg.api.data.ForMasterAPI;
import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.enums.AppType;
import com.dangjia.acg.common.exception.ServerCode;
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
import com.dangjia.acg.dto.pay.ActivityProductDTO;
import com.dangjia.acg.dto.pay.PaymentDTO;
import com.dangjia.acg.dto.pay.SafeTypeDTO;
import com.dangjia.acg.dto.pay.UpgradeSafeDTO;
import com.dangjia.acg.dto.product.ShoppingCartDTO;
import com.dangjia.acg.dto.product.ShoppingCartListDTO;
import com.dangjia.acg.dto.product.StorefrontProductDTO;
import com.dangjia.acg.mapper.account.IMasterAccountFlowRecordMapper;
import com.dangjia.acg.mapper.activity.DjStoreActivityProductMapper;
import com.dangjia.acg.mapper.activity.DjStoreParticipateActivitiesMapper;
import com.dangjia.acg.mapper.activity.IActivityRedPackRecordMapper;
import com.dangjia.acg.mapper.core.*;
import com.dangjia.acg.mapper.delivery.*;
import com.dangjia.acg.mapper.engineer.DjMaintenanceRecordMapper;
import com.dangjia.acg.mapper.engineer.DjMaintenanceRecordProductMapper;
import com.dangjia.acg.mapper.house.*;
import com.dangjia.acg.mapper.member.ICustomerRecordMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.pay.IBusinessOrderMapper;
import com.dangjia.acg.mapper.pay.IMasterSupplierPayOrderMapper;
import com.dangjia.acg.mapper.pay.IPayOrderMapper;
import com.dangjia.acg.mapper.product.IMasterProductTemplateMapper;
import com.dangjia.acg.mapper.product.IMasterStorefrontMapper;
import com.dangjia.acg.mapper.product.IMasterStorefrontProductMapper;
import com.dangjia.acg.mapper.product.IShoppingCartMapper;
import com.dangjia.acg.mapper.repair.IChangeOrderMapper;
import com.dangjia.acg.mapper.repair.IMendOrderMapper;
import com.dangjia.acg.mapper.safe.IWorkerTypeSafeMapper;
import com.dangjia.acg.mapper.safe.IWorkerTypeSafeOrderMapper;
import com.dangjia.acg.mapper.supplier.IMasterSupplierMapper;
import com.dangjia.acg.mapper.worker.IInsuranceMapper;
import com.dangjia.acg.modle.account.AccountFlowRecord;
import com.dangjia.acg.modle.activity.ActivityRedPackRecord;
import com.dangjia.acg.modle.activity.DjStoreActivityProduct;
import com.dangjia.acg.modle.activity.DjStoreParticipateActivities;
import com.dangjia.acg.modle.actuary.BudgetMaterial;
import com.dangjia.acg.modle.attribute.AttributeValue;
import com.dangjia.acg.modle.brand.Brand;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.core.HouseWorker;
import com.dangjia.acg.modle.core.HouseWorkerOrder;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.deliver.Order;
import com.dangjia.acg.modle.deliver.OrderItem;
import com.dangjia.acg.modle.deliver.OrderSplit;
import com.dangjia.acg.modle.deliver.OrderSplitItem;
import com.dangjia.acg.modle.engineer.DjMaintenanceRecord;
import com.dangjia.acg.modle.engineer.DjMaintenanceRecordProduct;
import com.dangjia.acg.modle.house.*;
import com.dangjia.acg.modle.member.CustomerRecord;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.order.DeliverOrderAddedProduct;
import com.dangjia.acg.modle.pay.BusinessOrder;
import com.dangjia.acg.modle.pay.PayOrder;
import com.dangjia.acg.modle.product.BasicsGoods;
import com.dangjia.acg.modle.product.BasicsGoodsCategory;
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
import com.dangjia.acg.service.acquisition.MasterCostAcquisitionService;
import com.dangjia.acg.service.config.ConfigMessageService;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.dangjia.acg.service.core.HouseWorkerService;
import com.dangjia.acg.service.core.TaskStackService;
import com.dangjia.acg.service.deliver.OrderService;
import com.dangjia.acg.service.design.HouseDesignPayService;
import com.dangjia.acg.service.repair.MendOrderCheckService;
import com.dangjia.acg.service.shell.HomeShellOrderService;
import com.dangjia.acg.util.StringTool;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import tk.mybatis.mapper.entity.Example;

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
    private IMasterProductTemplateMapper iMasterProductTemplateMapper;
    @Autowired
    private IHouseMapper houseMapper;
    @Autowired
    private IWorkerTypeSafeMapper workerTypeSafeMapper;
    @Autowired
    private IWorkerTypeSafeOrderMapper workerTypeSafeOrderMapper;

    @Autowired
    private IHouseWorkerMapper houseWorkerMapper;

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
    private DjMaintenanceRecordMapper djMaintenanceRecordMapper;
    @Autowired
    private DjMaintenanceRecordProductMapper djMaintenanceRecordProductMapper;
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
    private IMasterBasicsGoodsCategoryMapper iMasterBasicsGoodsCategoryMapper;
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
    private IMasterSupplierMapper iMaterSupplierMapper;
    @Autowired
    private IMasterStorefrontMapper iMasterStorefrontMapper;

    @Autowired
    private IMasterStorefrontProductMapper iMasterStorefrontProductMapper;
    @Autowired
    private IMasterAccountFlowRecordMapper iMasterAccountFlowRecordMapper;
    @Autowired
    private IMasterAttributeValueMapper iMasterAttributeValueMapper;
    @Autowired
    private TaskStackService taskStackService;
    @Autowired
    private IMemberMapper iMemberMapper;
    @Autowired
    private HomeShellOrderService homeShellOrderService;

    @Autowired
    private DjStoreActivityProductMapper djStoreActivityProductMapper;

    @Autowired
    private DjStoreParticipateActivitiesMapper djStoreParticipateActivitiesMapper;

    @Autowired
    private OrderService orderService;


    @Autowired
    private HouseWorkerService houseWorkerService;

    @Value("${spring.profiles.active}")
    private String active;

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
            } else if(businessOrder.getType()==10){//维保订单
                example = new Example(DjMaintenanceRecordProduct.class);
                example.createCriteria().andEqualTo(DjMaintenanceRecordProduct.MAINTENANCE_RECORD_ID,businessOrder.getMaintenanceRecordId())
                        .andEqualTo(DjMaintenanceRecordProduct.BUSINESS_ORDER_NUMBER,businessOrder.getNumber());
                List<DjMaintenanceRecordProduct> recordProductList=djMaintenanceRecordProductMapper.selectByExample(example);
                if(recordProductList!=null&&recordProductList.size()>0){
                    DjMaintenanceRecordProduct djMaintenanceRecordProduct=recordProductList.get(0);
                    DjMaintenanceRecord djMaintenanceRecord=djMaintenanceRecordMapper.selectByPrimaryKey(businessOrder.getMaintenanceRecordId());
                    if(djMaintenanceRecordProduct.getMaintenanceProductType()==1||djMaintenanceRecordProduct.getMaintenanceProductType()==2){//质保和勘查费用订单
                       setHouseWorker(djMaintenanceRecord,djMaintenanceRecordProduct.getMaintenanceProductType(),businessOrder.getTotalPrice());
                    }else if(djMaintenanceRecordProduct.getMaintenanceProductType()==3){//维保材料商品付款后，走要货流程
                        insertDeliverSplitOrderInfo(businessOrder.getId());
                    }
                    //修改维保订单的支付状态为已支付
                    djMaintenanceRecordProductMapper.updateRecordProductInfoByBusinessNumber(businessOrder.getMaintenanceRecordId(),businessOrder.getNumber());
                }
            }else if(businessOrder.getType()==11){//当家贝商品兑换
                homeShellOrderService.updateShellOrderInfo(businessOrder.getNumber());
            }else if(businessOrder.getType()==12){//当家贝充值
                homeShellOrderService.saveShellMoney(businessOrder);
            }else if(businessOrder.getType()==13){//拼团购
                return orderService.spellDeals(businessOrder);
            }else if(businessOrder.getType()==14){//限时购
                orderService.timeToBuy(businessOrder);
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
            logger.error("支付回调异常",e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ServerResponse.createByErrorMessage("支付回调异常");
        }
    }
    /**
     * web支付成功回调
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse setWebPaySuccess( String businessOrderNumber) {
        if(CommonUtil.isEmpty(businessOrderNumber)){
            return ServerResponse.createByErrorMessage("支付订单不存在");
        }
        Map<String, Object> returnMap = new HashMap<>();
        try {
            Example examplePayOrder = new Example(PayOrder.class);
            examplePayOrder.createCriteria().andEqualTo(PayOrder.BUSINESS_ORDER_NUMBER, businessOrderNumber);
            List<PayOrder> payOrderList = payOrderMapper.selectByExample(examplePayOrder);
            if (payOrderList.size() == 0) {
                return ServerResponse.createByErrorMessage("支付订单不存在");
            }
            PayOrder payOrder = payOrderList.get(0);
            if(active!=null&&(active.equals("dev"))) {
                //开发回调
                setServersSuccess(payOrder.getId());
                payOrder = payOrderMapper.selectByPrimaryKey(payOrder.getId());
            }
            Example example = new Example(BusinessOrder.class);
            example.createCriteria().andEqualTo(BusinessOrder.NUMBER, payOrder.getBusinessOrderNumber());
            List<BusinessOrder> businessOrderList = businessOrderMapper.selectByExample(example);
            if (businessOrderList.size() > 0) {
                BusinessOrder businessOrder = businessOrderList.get(0);
                Order order= orderMapper.selectByPrimaryKey(businessOrder.getTaskId());
                if(order!=null) {
                    List<OrderItem> orderItems = orderItemMapper.getReservationDeliverState(order.getId());
                    returnMap.put("shippingState", orderItems.size() > 0 ? 5 : 1004);//5=存在预约商品  1004=无
                    returnMap.put("orderId", order.getId());
                    returnMap.put("shippingType", 2);
                    returnMap.put("goodsList", orderItems);
                    returnMap.put("goodsTotalNum", orderItems.size());
                }
                returnMap.put("houseId", businessOrder.getHouseId());
            }
            if (payOrder.getState() == 2) {//已支付
                returnMap.put("name", "当家装修担保平台");
                returnMap.put("businessOrderNumber", businessOrderNumber);
                returnMap.put("price", payOrder.getPrice());
                return ServerResponse.createBySuccess("支付成功", returnMap);
            }else{
                returnMap.put("name", "当家装修担保平台");
                returnMap.put("businessOrderNumber", businessOrderNumber);
                returnMap.put("price", payOrder.getPrice());
                return ServerResponse.createByErrorMessage("未支付成功",returnMap);
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
            if(active!=null&&(active.equals("dev"))) {
                //开发回调
                setServersSuccess(payOrder.getId());
            }
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
            budgetCorrect(order,  payState, null);

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
                //修改补差价任务为已处理（查贸易符合条件补差价订单信息)
                TaskStack taskStack=taskStackService.selectTaskStackByData(order.getHouseId(),7,order.getBusinessOrderNumber());
                if(taskStack!=null&& !CommonUtil.isEmpty(taskStack.getId())){
                    taskStack.setState(1);
                    taskStack.setModifyDate(new Date());
                    taskStackService.updateTaskStackInfo(taskStack);
                }
            }else{
                if("1".equals(workerTypeId)){//如果工序ID为设计师，支付完成后，将房子表 的设计类型字段改为远程设计
                    House house=houseMapper.selectByPrimaryKey(order.getHouseId());
                    house.setDesignerOk(1);
                    house.setModifyDate(new Date());
                    houseMapper.updateByPrimaryKeySelective(house);

                }
                setHouseFlowInfo(order,workerTypeId,payState,1,new BigDecimal(0),2);

                //修改待处理的任务为已处理
                TaskStack taskStack=taskStackService.selectTaskStackByHouseIdData(order.getHouseId(),order.getBusinessOrderNumber());
                if(taskStack!=null&& !CommonUtil.isEmpty(taskStack.getId())){
                    taskStack.setState(1);
                    taskStack.setModifyDate(new Date());
                    taskStackService.updateTaskStackInfo(taskStack);
                }
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
        houseFlow.setWorkSteta(2);
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
        if(houseFlow.getWorkType()==3) {//大管家自动派单
            houseWorkerService.autoDistributeHandle(houseFlow);
        }
        /*处理保险订单*/
        this.insurance(hwo, payState);

        //修改待处理的任务为已处理
        TaskStack taskStack=taskStackService.selectTaskStackByHouseIdData(order.getHouseId(),houseFlow.getId());
        if(taskStack!=null&& !CommonUtil.isEmpty(taskStack.getId())){
            taskStack.setState(1);
            taskStack.setModifyDate(new Date());
            taskStackService.updateTaskStackInfo(taskStack);
        }
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
            BigDecimal totalDiscountPrice = new BigDecimal(0);//优惠总额
            Order orderNew = orderMapper.getStorefontOrder(queryShopGood.getShopId(),order.getId(),queryShopGood.getProductType());
            if(queryShopGoods.size()>1){
                if(orderNew==null) {
                    orderNew = new Order();
                }
                orderNew.setHouseId(order.getHouseId());
                orderNew.setWorkerTypeId(order.getWorkerTypeId());
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
                orderNew.setWorkerTypeName(order.getWorkerTypeName());
                orderMapper.insert(orderNew);
            }else {
                orderNew=order;
                orderNew.setStorefontId(queryShopGood.getShopId());
                orderNew.setParentOrderId(order.getId());
            }
            if(CommonUtil.isEmpty(order.getWorkerTypeName())) {
                if (queryShopGood.getProductType() == 0 || queryShopGood.getProductType() == 1) {
                    order.setWorkerTypeName(order.getWorkerTypeName() + "-实物");
                }
                if (queryShopGood.getProductType() == 2) {
                    order.setWorkerTypeName(order.getWorkerTypeName() + "-人工");
                }
                if (queryShopGood.getProductType() == 3) {
                    order.setWorkerTypeName(order.getWorkerTypeName() + "-体验");
                }
                if (queryShopGood.getProductType() == 5) {
                    order.setWorkerTypeName(order.getWorkerTypeName() + "-维保");
                }
            }
            for (BudgetLabelDTO labelDTO : queryShopGood.getLabelDTOS()) {
                for (BudgetLabelGoodsDTO good : labelDTO.getGoods()) {
                    OrderItem orderItem = orderItemMapper.selectByPrimaryKey(good.getId());
                    paymentPrice = paymentPrice.add(new BigDecimal(orderItem.getTotalPrice()));
                    freightPrice = freightPrice.add(new BigDecimal(orderItem.getTransportationCost()));
                    totalMoveDost = totalMoveDost.add(new BigDecimal(orderItem.getStevedorageCost()));
                    if(orderItem.getDiscountPrice()!=null&&orderItem.getDiscountPrice()>0){
                        totalDiscountPrice=totalDiscountPrice.add(new BigDecimal(orderItem.getDiscountPrice()));
                    }

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
                orderNew.setTotalDiscountPrice(totalDiscountPrice);//优惠总额
                BigDecimal payPrice = orderNew.getTotalAmount().subtract(orderNew.getTotalDiscountPrice());
                payPrice = payPrice.add(orderNew.getTotalStevedorageCost());
                payPrice = payPrice.add(orderNew.getTotalTransportationCost());
                payPrice = payPrice.subtract(totalDiscountPrice);
                orderNew.setActualPaymentPrice(payPrice);//优惠总额
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
           /* List<ActivityRedPackRecordDTO> rprList = discountPage(businessOrder.getNumber());
            if (rprList != null && rprList.size() > 0) {
                paymentDTO.setDiscounts(1);//有优惠
            } else {
                paymentDTO.setDiscounts(0);//
            }*/
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
            budgetLabelDTO.setLabelDTOS(queryBudgetLabel(orderId,budgetLabelDTO.getShopId(),budgetLabelDTO.getProductType()));
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
    public List<BudgetLabelDTO> queryBudgetLabel(String orderId,String storefontId,Integer productType){
        String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
        List<BudgetLabelDTO> budgetLabelDTOS =  orderItemMapper.queryBudgetLabel(orderId,storefontId,productType);//精算工钱

        List<BudgetLabelGoodsDTO> budgetLabelGoodsDTOS = orderItemMapper.queryBudgetLabelGoods(orderId,storefontId,productType);//精算工钱
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
    public ServerResponse generateOrder(String userToken,String cityId, String productJsons,String workerId, String addressId,String activityRedPackId) {
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
            return generateOrderCommon(member,houseId, cityId, productJsons, workerId,  addressId,2,null,activityRedPackId,null);
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
     * @param orderSource 订单来源(1,工序订单，2购物车，3补货单，4补差价订单，5维修订单 6拼团订单 7限时购订单)
     * @param activityRedPackId 优惠券ID
     * @return
     */
    public ServerResponse generateOrderCommon(Member member,String houseId,String cityId,
                                              String productJsons,String workerId, String addressId,
                                              Integer orderSource,String workerTypeId,String activityRedPackId,
                                              String storeActivityProductId){
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
        List<ShoppingCartDTO> shoppingCartDTOS;
        if(StringUtils.isNotBlank(storeActivityProductId)){
            shoppingCartDTOS=iShoppingCartMapper.queryShoppingCartDTOS1(storeActivityProductId);
        }else{
            shoppingCartDTOS=iShoppingCartMapper.queryShoppingCartDTOS(productIds);
        }
        BigDecimal paymentPrice = new BigDecimal(0);//总共钱
        BigDecimal freightPrice = new BigDecimal(0);//总运费
        BigDecimal totalMoveDost = new BigDecimal(0);//搬运费
        Double totalMoney=0d;//优惠商品总额
        Double concessionMoney=0d;//优惠总额
        String concessionProducts="";//可优惠商品编码
        ActivityProductDTO activityProductDTO;
        List<ActivityProductDTO> activityProductDTOList=new ArrayList<>();
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

                activityProductDTO=new ActivityProductDTO();
                activityProductDTO.setProductId(parmDTO.getProductId());
                activityProductDTO.setPrice(parmDTO.getPrice());
                activityProductDTO.setShopCount(parmDTO.getShopCount());
                activityProductDTOList.add(activityProductDTO);
            }
            shoppingCartDTO.setTotalMaterialPrice(totalMaterialPrice);
            shoppingCartDTO.setTotalPrice(totalSellPrice);
        }
        if(StringUtils.isNotBlank(activityRedPackId)){
            //查询对应的优惠信息
            Map map=discountPage(member.getId(),activityProductDTOList,activityRedPackId);
            if(map!=null){
                ActivityRedPackRecordDTO recommendCuponsPack=(ActivityRedPackRecordDTO)map.get("recommendCuponsPack");
                totalMoney=recommendCuponsPack.getTotalMoney();
                concessionMoney=recommendCuponsPack.getConcessionMoney();
                concessionProducts=recommendCuponsPack.getProducts()+",";

            }
        }

        if (shoppingCartDTOS!=null) {
            Order order = new Order();
            String workerTypeName="购物车订单";
            if(orderSource==1||orderSource==4){
                workerTypeName="设计、精算订单";
            }
            if(orderSource==6){
                workerTypeName="拼团购订单";
            }
            if(orderSource==7){
                workerTypeName="限时购订单";
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
            order.setWorkerTypeId(workerTypeId);
            order.setTotalDiscountPrice(BigDecimal.valueOf(concessionMoney));
            if(StringUtils.isNotBlank(storeActivityProductId)){
                DjStoreActivityProduct djStoreActivityProduct =
                        djStoreActivityProductMapper.selectByPrimaryKey(storeActivityProductId);
                DjStoreParticipateActivities djStoreParticipateActivities =
                        djStoreParticipateActivitiesMapper.selectByPrimaryKey(djStoreActivityProduct.getStoreParticipateActivitiesId());
                order.setStoreActivityId(djStoreParticipateActivities.getStoreActivityId());
                order.setStoreActivityProductId(storeActivityProductId);
            }
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
                    //优惠价钱
                    if(totalMoney>0&&concessionProducts.contains(good.getProductId()+",")){
                        orderItem.setDiscountPrice(MathUtil.div(MathUtil.mul(orderItem.getTotalPrice(),totalMoney),concessionMoney));//每个商品的优惠总额
                    }else{
                        orderItem.setDiscountPrice(0d);
                    }

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
                        order.setType(2);
                    }
                    if(good.getProductType()==2){
                        order.setType(1);
                    }
                    if(good.getProductType()==3){
                        order.setType(4);
                    }
                    if(good.getProductType()==5){
                        order.setType(5);
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
                businessOrder.setTotalPrice(paymentPrice);//订单总额
                businessOrder.setPayPrice(paymentPrice.add(totalMoveDost).add(freightPrice).subtract(order.getTotalDiscountPrice()));//实付总额
                businessOrder.setDiscountsPrice(BigDecimal.valueOf(concessionMoney));
                businessOrder.setRedPacketPayMoneyId(activityRedPackId);//优惠券ID
                if(orderSource==1||orderSource==4){
                    businessOrder.setType(1);//记录支付类型任务类型(精算支付任务，走工序的）
                }else if(orderSource==6) {
                    businessOrder.setType(13);//拼团购
                }else if(orderSource==7){
                    businessOrder.setType(14);//限时购
                }else{
                    businessOrder.setType(2);//记录支付类型任务类型
                }

                businessOrder.setTaskId(order.getId());//保存任务ID
                businessOrderMapper.insert(businessOrder);
            }

            order.setTotalTransportationCost(freightPrice);//总运费
            order.setTotalStevedorageCost(totalMoveDost);//总搬运费
            order.setBusinessOrderNumber(businessOrder.getNumber());
            order.setTotalAmount(paymentPrice);// 订单总额(工钱)
            order.setActualPaymentPrice(paymentPrice.add(totalMoveDost).add(freightPrice).subtract(order.getTotalDiscountPrice()));
            if(orderSource==4){
                order.setWorkerTypeName("补差价订单");
            }
            if(workerTypeId!=null&&"1".equals(workerTypeId)){
                order.setWorkerTypeName("设计师人工订单");
            }
            orderMapper.updateByPrimaryKeySelective(order);

            if(orderSource==1&&StringUtils.isBlank(workerTypeId)){//若为支付类型的订单，需增加订单支付工序阶段（设计，精算类的订单)
                List<HouseOrderDetailDTO> orderDetailList=houseMapper.getOrderDetailByHouseId(houseId,"1");//设计订单
                setTotalPrice(orderDetailList,houseId,"1",order,businessOrder);
                orderDetailList=houseMapper.getOrderDetailByHouseId(houseId,"2");//精算订单
                setTotalPrice(orderDetailList,houseId,"2",order,businessOrder);//
            }
            //如果工序不为空，则添加工序对应的信息
            if(StringUtils.isNotBlank(workerTypeId)){
                House house=houseMapper.selectByPrimaryKey(houseId);
                setHouseWorkerOrderInfo(house,workerTypeId,order,businessOrder,paymentPrice);
            }
            if(orderSource!=6||orderSource!=7) {
                budgetCorrect(order, null, null);
            }

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

            if(totalMoney>0){
                //优惠卷状态改为已使用
                ActivityRedPackRecord activityRedPackRecord=activityRedPackRecordMapper.selectByPrimaryKey(activityRedPackId);
                activityRedPackRecord.setHaveReceive(1);
                activityRedPackRecord.setBusinessOrderNumber(businessOrder.getNumber());
                activityRedPackRecord.setModifyDate(new Date());
                activityRedPackRecordMapper.updateByPrimaryKeySelective(activityRedPackRecord);
            }

            //清空购物车指定商品
            example = new Example(ShoppingCart.class);
            example.createCriteria().andEqualTo(ShoppingCart.MEMBER_ID, member.getId())
                    .andIn(ShoppingCart.PRODUCT_ID,Arrays.asList(productIds));
            iShoppingCartMapper.deleteByExample(example);
            return ServerResponse.createBySuccess("提交成功", businessOrder);
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
            hwo.setWorkerTypeId(workerTypeId);
            hwo.setWorkerType(wt.getType());
            hwo.setTotalPrice(paymentPrice);
            hwo.setWorkPrice(paymentPrice);
            hwo.setMaterialPrice(BigDecimal.valueOf(0));
            hwo.setBusinessOrderNumber(businessOrder.getNumber());//业务订单号
            houseWorkerOrderMapper.insert(hwo);
        } else {
            hwo.setHouseId(order.getHouseId());
            hwo.setWorkerTypeId(workerTypeId);
            hwo.setWorkerType(wt.getType());
            hwo.setTotalPrice(paymentPrice);
            hwo.setWorkPrice(paymentPrice);
            hwo.setMaterialPrice(BigDecimal.valueOf(0));
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
     * 维保订单
     * @param userToken
     * @param maintenanceRecordId
     * @param maintenanceRecordType
     * @return
     */
    public ServerResponse generateMaintenanceRecordOrder(String userToken,String maintenanceRecordId,Integer maintenanceRecordType,String cityId,String maintenanceRecordProductId) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = (Member) object;
            //查询质保信息
            DjMaintenanceRecord djMaintenanceRecord = djMaintenanceRecordMapper.selectByPrimaryKey(maintenanceRecordId);

            WorkerType wt = workerTypeMapper.selectByPrimaryKey(djMaintenanceRecord.getWorkerTypeId());

            Integer payState=1;//未支付
            if(djMaintenanceRecord.getOverProtection()==0){
                payState=2;//已支付
            }
            BigDecimal paymentPrice = new BigDecimal(0);//总共钱
            BigDecimal totalPrice = new BigDecimal(0);//总共钱
            BigDecimal freightPrice = new BigDecimal(0);//总运费
            BigDecimal totalMoveDost = new BigDecimal(0);//搬运费
            List<DjMaintenanceRecordProduct> storeProductList=djMaintenanceRecordProductMapper.selectStorefrontIdByTypeId(maintenanceRecordId,maintenanceRecordType,1);
           if(maintenanceRecordType!=4&&(djMaintenanceRecord==null||storeProductList==null)){//报销维保没有商品信息
              return ServerResponse.createByErrorMessage("未找到符合条件的维修记录，请核实");
           }

            if (storeProductList!=null||maintenanceRecordType==4) {
                Order order = new Order();
                order.setHouseId(djMaintenanceRecord.getHouseId());
                order.setWorkerTypeName(wt.getName() + "维修订单");
                order.setCityId(cityId);
                order.setWorkerTypeId(djMaintenanceRecord.getWorkerTypeId());
                order.setMemberId(member.getId());
                order.setType(5);
                order.setOrderNumber(System.currentTimeMillis() + "-" + (int) (Math.random() * 9000 + 1000));
                order.setTotalDiscountPrice(new BigDecimal(0));
                order.setTotalStevedorageCost(new BigDecimal(0));
                order.setTotalTransportationCost(new BigDecimal(0));
                order.setActualPaymentPrice(new BigDecimal(0));
                order.setOrderStatus("1");
                order.setOrderGenerationTime(new Date());
                order.setOrderSource(5);//精算制作
                order.setWorkerId(String.valueOf(wt.getType()));
                order.setCreateBy(member.getId());
                orderMapper.insert(order);
                if(maintenanceRecordType!=4){//报销费用的商品，不走详情单信息
                    for(DjMaintenanceRecordProduct storeProduct:storeProductList){
                        List<DjMaintenanceRecordProduct> recordProductList=djMaintenanceRecordProductMapper.queryPayMaintenanceRecordProduct(maintenanceRecordId,maintenanceRecordType,1,null,storeProduct.getStorefrontId());
                        Double freight=storefrontConfigAPI.getFreightPrice(storeProduct.getStorefrontId(),storeProduct.getTotalPrice());
                        freightPrice=freightPrice.add(new BigDecimal(freight));
                        //查询有几个店铺的商品,按店铺划分
                        for (DjMaintenanceRecordProduct recordProduct : recordProductList) {
                            StorefrontProduct storefrontProduct=iMasterStorefrontProductMapper.selectByPrimaryKey(recordProduct.getProductId());
                            StorefrontProductDTO storefrontProductDTO=iMasterProductTemplateMapper.getStorefrontProductByTemplateId(storefrontProduct.getProdTemplateId());
                            BasicsGoods basicsGoods=iMasterBasicsGoodsMapper.selectByPrimaryKey(storefrontProductDTO.getGoodsId());

                            OrderItem orderItem = new OrderItem();
                            orderItem.setIsReservationDeliver("0");
                            orderItem.setOrderId(order.getId());
                            orderItem.setHouseId(djMaintenanceRecord.getHouseId());
                            orderItem.setPrice(recordProduct.getPrice().doubleValue());//销售价
                            orderItem.setShopCount(recordProduct.getShopCount().doubleValue());//购买总数
                            orderItem.setUnitName(storefrontProductDTO.getUnitName());//单位
                            orderItem.setTotalPrice(recordProduct.getTotalPrice().doubleValue());//总价
                            orderItem.setProductName(storefrontProduct.getProductName());
                            orderItem.setProductSn(storefrontProductDTO.getProductSn());
                            orderItem.setCategoryId(storefrontProductDTO.getCategoryId());
                            orderItem.setProductId(storefrontProductDTO.getStorefrontProductId());
                            orderItem.setImage(storefrontProductDTO.getImage());
                            orderItem.setCityId(cityId);
                            orderItem.setProductType(basicsGoods.getType());
                            orderItem.setStorefontId(storefrontProduct.getStorefrontId());
                            orderItem.setAskCount(0d);
                            orderItem.setDiscountPrice(0d);
                            orderItem.setActualPaymentPrice(0d);
                            orderItem.setStevedorageCost(0d);
                            orderItem.setTransportationCost(0d);
                            orderItem.setOrderStatus("1");//1待付款，2已付款，3待收货，4已完成，5已取消，6已退货，7已关闭
                            if(payState==2){
                                orderItem.setOrderStatus("2");
                            }
                            orderItem.setCreateBy(member.getId());
                            if(basicsGoods.getType()==0&&freight>0){
                                //均摊运费
                                Double transportationCost=(orderItem.getTotalPrice()/storeProduct.getTotalPrice().doubleValue())*freight;
                                orderItem.setTransportationCost(transportationCost);
                                order.setTotalTransportationCost(order.getTotalStevedorageCost().add(BigDecimal.valueOf(transportationCost)));
                            }
                            if(StringUtils.isNotBlank(recordProduct.getProductId())){
                                //搬运费运算
                                Double moveDost=masterCostAcquisitionService.getStevedorageCost(djMaintenanceRecord.getHouseId(),orderItem.getProductId(),orderItem.getShopCount());
                                totalMoveDost=totalMoveDost.add(new BigDecimal(moveDost));
                                if(moveDost>0){
                                    //均摊运费
                                    orderItem.setStevedorageCost(moveDost);
                                    order.setTotalStevedorageCost(order.getTotalStevedorageCost().add(BigDecimal.valueOf(moveDost)));
                                }
                            }
                            orderItemMapper.insert(orderItem);
                            paymentPrice=paymentPrice.add(BigDecimal.valueOf(recordProduct.getPayPrice()));
                            totalPrice=totalPrice.add(BigDecimal.valueOf(recordProduct.getTotalPrice()));
                        }

                    }
                }

                if(djMaintenanceRecord.getOverProtection()==1){
                    paymentPrice=paymentPrice.add(order.getTotalTransportationCost());
                    paymentPrice=paymentPrice.add(order.getTotalStevedorageCost());
                }
                if(maintenanceRecordType==4){
                    DjMaintenanceRecordProduct djMaintenanceRecordProduct=djMaintenanceRecordProductMapper.selectByPrimaryKey(maintenanceRecordProductId);//报销费用商品的总报销费用
                    totalPrice=BigDecimal.valueOf(djMaintenanceRecordProduct.getTotalPrice());
                    paymentPrice=BigDecimal.valueOf(djMaintenanceRecordProduct.getPrice());
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
                    businessOrder.setHouseId(djMaintenanceRecord.getHouseId());
                    businessOrder.setNumber(System.currentTimeMillis() + "-" + (int) (Math.random() * 9000 + 1000));
                    businessOrder.setState(1);//刚生成
                    if(payState==2){
                        businessOrder.setState(3);//已支付
                    }
                    businessOrder.setTotalPrice(totalPrice);//订单总价
                    businessOrder.setDiscountsPrice(new BigDecimal(0));
                    businessOrder.setPayPrice(paymentPrice);//实付总价
                    businessOrder.setType(10);//记录支付类型任务类型
                    businessOrder.setTaskId(order.getId());//保存任务ID
                    businessOrder.setMaintenanceRecordId(maintenanceRecordId);
                    businessOrderMapper.insert(businessOrder);
                }
              //  order.setTotalTransportationCost(freightPrice);//总运费
               // order.setTotalStevedorageCost(totalMoveDost);//总搬运费
                order.setBusinessOrderNumber(businessOrder.getNumber());
                order.setTotalAmount(totalPrice);// 订单总额(工钱)
                order.setActualPaymentPrice(paymentPrice);//实付总价
                orderMapper.updateByPrimaryKeySelective(order);
                if(maintenanceRecordType==3){// 货材料商品才会有拆分单
                     budgetCorrect(order,null,null);// 拆分单，只有材料商品才会有拆分单
                }

                //将业务支付订单号维护到维保订单中去
                djMaintenanceRecordProductMapper.updateRecordProductInfo(maintenanceRecordId,maintenanceRecordType,businessOrder.getNumber(),payState);
                if(djMaintenanceRecord.getOverProtection()==0){
                    if(maintenanceRecordType==1||maintenanceRecordType==2){//维保期内的订单(只有维保订单或勘查订单，才会生成待抢单信息)
                        setHouseWorker(djMaintenanceRecord, maintenanceRecordType, order.getTotalAmount());
                    }else if(maintenanceRecordType==3){// 质保期内的商品
                        //如果是维保生成的要货材料，则生成要货单到对应的店铺
                        insertDeliverSplitOrderInfo(businessOrder.getId());
                    }
                }else if(djMaintenanceRecord.getOverProtection()==1){//过保需业主支付时，则返回业务订单号
                    return ServerResponse.createBySuccess("提交成功", businessOrder.getNumber());
                }
            }
            return ServerResponse.createBySuccess("提交成功","");
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ServerResponse.createByErrorMessage("提交失败：原因："+e.getMessage());
        }
    }

    /**
     * 维修订单，生成对应的要货单给到店铺
     * @param businessOrderNumber
     */
    public void insertDeliverSplitOrderInfo(String businessOrderNumber){
        BusinessOrder businessOrder=businessOrderMapper.selectByPrimaryKey(businessOrderNumber);
        List<Order> orderList=orderMapper.byBusinessOrderNumber(businessOrder.getNumber());
        if(orderList!=null&&orderList.size()>0){//查订单表
            for(Order order:orderList){//查订单详情表
                List<OrderItem> orderItemList=orderItemMapper.byOrderIdList(order.getId());
                if(orderItemList!=null&&orderItemList.size()>0){
                    DjMaintenanceRecord djMaintenanceRecord=djMaintenanceRecordMapper.selectByPrimaryKey(businessOrder.getMaintenanceRecordId());
                    Member worker=iMemberMapper.selectByPrimaryKey(djMaintenanceRecord.getWorkerMemberId());
                    //1.生成要货单
                    Example example = new Example(OrderSplit.class);
                    OrderSplit orderSplit=new OrderSplit();
                    orderSplit.setNumber("DJ" + 200000 + orderSplitMapper.selectCountByExample(example));//要货单号
                    orderSplit.setHouseId(order.getHouseId());
                    orderSplit.setApplyStatus(0);//后台审核状态：0生成中, 1申请中, 2通过, 3不通过, 4业主待支付补货材料 后台(材料员)
                    orderSplit.setMemberId(worker.getId());
                    orderSplit.setMemberName(worker.getName());
                    orderSplit.setMobile(worker.getMobile());
                    orderSplit.setWorkerTypeId(worker.getWorkerTypeId());
                    orderSplit.setTotalAmount(order.getTotalAmount());
                    orderSplit.setStorefrontId(order.getStorefontId());
                    orderSplitMapper.insert(orderSplit);
                    //2.生成要货单明细
                    for(OrderItem orderItem:orderItemList){
                        OrderSplitItem orderSplitItem=new OrderSplitItem();
                        orderSplitItem.setOrderSplitId(orderSplit.getId());
                        orderSplitItem.setProductId(orderItem.getProductId());
                        orderSplitItem.setProductSn(orderItem.getProductSn());
                        orderSplitItem.setProductName(orderItem.getProductName());
                        orderSplitItem.setPrice(orderItem.getPrice());
                        orderSplitItem.setAskCount(orderItem.getShopCount());
                        orderSplitItem.setCost(orderItem.getCost());
                        orderSplitItem.setShopCount(orderItem.getShopCount());
                        orderSplitItem.setNum(orderItem.getShopCount());
                        orderSplitItem.setUnitName(orderItem.getUnitName());
                        orderSplitItem.setTotalPrice(order.getTotalAmount().doubleValue());//单项总价 销售价
                        orderSplitItem.setProductType(orderItem.getProductType());
                        orderSplitItem.setCategoryId(orderItem.getCategoryId());
                        orderSplitItem.setImage(orderItem.getImage());//货品图片
                        orderSplitItem.setHouseId(order.getHouseId());
                        orderSplitItem.setStorefrontId(order.getStorefontId());
                        orderSplitItemMapper.insert(orderSplitItem);
                        //修改订单详情的要货量
                        orderItem.setAskCount(orderItem.getShopCount());
                        orderItem.setOrderStatus("3");
                        orderItem.setModifyDate(new Date());
                        orderItemMapper.updateByPrimaryKeySelective(orderItem);
                    }
                    //修改订的状态为待收货
                    order.setOrderStatus("3");
                    order.setModifyDate(new Date());
                    orderMapper.updateByPrimaryKeySelective(order);
                }
            }
        }
    }


    /**
     * 增加待抢单列表记录
     */
    private void setHouseWorker(DjMaintenanceRecord djMaintenanceRecord,Integer maintenanceRecordType,BigDecimal totalAmount){
        //直接找到原工匠或原管家，将订单分配给对应的人员
        HouseFlow houseFlow=houseFlowMapper.getByWorkerTypeId(djMaintenanceRecord.getHouseId(),djMaintenanceRecord.getWorkerTypeId()); //找原工匠
        if(maintenanceRecordType==2){
            //找原管家
            houseFlow=houseFlowMapper.getByWorkerTypeId(djMaintenanceRecord.getHouseId(),"3");
        }
        //生成抢单记录
        Member worker=iMemberMapper.selectByPrimaryKey(houseFlow.getWorkerId());
        HouseWorker houseWorker = new HouseWorker();
        houseWorker.setWorkerId(worker.getId());
        houseWorker.setWorkerTypeId(worker.getWorkerTypeId());
        houseWorker.setWorkerType(worker.getWorkerType());
        houseWorker.setWorkType(1);//已抢单
        houseWorker.setIsSelect(1);
        houseWorker.setPrice(totalAmount);
        houseWorker.setType(2);
        houseWorker.setBusinessId(djMaintenanceRecord.getId());
        houseWorkerMapper.insert(houseWorker);
        //将单给到对应的工匠或管家
        if(maintenanceRecordType==1){
            //给到工匠
            djMaintenanceRecord.setWorkerMemberId(worker.getId());

        }else if(maintenanceRecordType==2){
            //给到管家
            djMaintenanceRecord.setStewardId(worker.getId());
        }
        djMaintenanceRecord.setModifyDate(new Date());
        djMaintenanceRecordMapper.updateByPrimaryKeySelective(djMaintenanceRecord);
    }

    /**
     * 修改工匠抢单状态为已放弃
     * @param maintenanceRecordId
     * @param workerId
     */
    public void updateHouseWorker(String maintenanceRecordId,String workerId){
        Example example=new Example(HouseWorker.class);
        example.createCriteria().andEqualTo(HouseWorker.BUSINESS_ID,maintenanceRecordId)
                .andEqualTo(HouseWorker.WORKER_ID,workerId)
                .andEqualTo(HouseWorker.TYPE,2);
        HouseWorker houseWorker=houseWorkerMapper.selectOneByExample(example);
        if(houseWorker!=null){
            houseWorker.setWorkType(8);
            houseWorker.setIsSelect(0);
            houseWorker.setModifyDate(new Date());
            houseWorkerMapper.updateByPrimaryKeySelective(houseWorker);
        }
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

//                budgetCorrect(order,null,null);
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
                //修改待处理的任务为已处理
                TaskStack taskStack=taskStackService.selectTaskStackByHouseIdData(order.getHouseId(),houseFlow.getId());
                if(taskStack!=null&& !CommonUtil.isEmpty(taskStack.getId())){
                    taskStack.setState(1);
                    taskStack.setModifyDate(new Date());
                    taskStackService.updateTaskStackInfo(taskStack);
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
     * 查询符合条件的优惠券
     * @param userToken
     * @param productJsons
     * @return
     */
    public ServerResponse queryActivityRedPackInfo(String userToken,String productJsons){
        try{
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = (Member) object;
            JSONArray productArray= JSON.parseArray(productJsons);
            List<ActivityProductDTO> activityProductDTOList=new ArrayList<>();
            ActivityProductDTO activityProductDTO;
            if(productArray.size()==0){
                return ServerResponse.createByErrorMessage("参数错误");
            }
            Map<String,ShoppingCartListDTO> productMap = new HashMap<>();
            String[] productIds=new String[productArray.size()];
            for (int i = 0; i < productArray.size(); i++) {
                JSONObject productObj = productArray.getJSONObject(i);
                activityProductDTO=new ActivityProductDTO();
                activityProductDTO.setProductId(productObj.getString("productId"));
                activityProductDTO.setPrice(productObj.getDouble("price"));
                activityProductDTO.setShopCount(productObj.getDouble("shopCount"));
                activityProductDTOList.add(activityProductDTO);
            }
            Map<String,Object> map=discountPage(member.getId(),activityProductDTOList,null);
            if(map==null){
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            }
            return ServerResponse.createBySuccess("查询成功",map);
        }catch (Exception e){
            logger.error("查询失败",e);
            return ServerResponse.createByErrorMessage("查询失败",e);
        }

    }
    /**
     * 可用优惠券数据
     *
     * @param activityProductDTOList 需支付商品列表
     * @return
     */
    public Map<String,Object> discountPage(String membreId,List<ActivityProductDTO> activityProductDTOList,String activityRedPackId) {
        try {
            Map<String,Object> redPackMap=new HashMap<>();
            if (activityProductDTOList.size() == 0) {
                return null;
            }
            //满足条件的优惠券记录
            List<ActivityRedPackRecordDTO> redPacetResultList = new ArrayList<>();
            ActivityRedPackRecordDTO recommendCuponsPack=new ActivityRedPackRecordDTO();//推荐优惠券
            List<ActivityRedPackRecordDTO> redPacketRecordSelectList = activityRedPackRecordMapper.queryMyAticvityList(membreId,null,3,activityRedPackId);
            if (redPacketRecordSelectList.size() == 0) {
                return null;
            }
            String prodTemplateId;//商品模板ID
            String goodsId;//货品ID
            String categoryId;//类别ID
            String categoryTopId;//顶级类别id
            String storefrontId;//店铺ID
            Integer fromObjectType;
            List productlist;
            for (ActivityRedPackRecordDTO redPacketRecord : redPacketRecordSelectList) {
                Double totalMoney=0d;//符合条件的商品总额
                Double concessionMoney=0d;//可优惠金额
                productlist=new ArrayList();
                for (ActivityProductDTO activityProductDTO : activityProductDTOList) {
                    //查询当前商品的商品模板ID，获取ID，类别ID，顶级类别Id，店铺ID
                    Map<String,Object> product= iMasterProductTemplateMapper.queryPrductByStorefrontId(activityProductDTO.getProductId());
                    prodTemplateId=(String)product.get("prodTemplateId");//商品模板ID
                    goodsId=(String)product.get("goodsId");//货品ID
                    categoryId=(String)product.get("categoryId");//类别ID
                    categoryTopId=(String)product.get("categoryTopId");//顶级类别id
                    storefrontId=(String)product.get("storefrontId");//店铺ID
                    fromObjectType=redPacketRecord.getFromObjectType();//3类别，4货品，5商品，6城市，7店铺
                    //判断优惠是城市券还是店铺券
                    if(redPacketRecord.getSourceType()==1){//城市券
                        if(fromObjectType==6){
                            totalMoney=MathUtil.add(totalMoney,MathUtil.mul(activityProductDTO.getPrice(),activityProductDTO.getShopCount()));
                            productlist.add(activityProductDTO.getProductId());
                        }else if(fromObjectType==3){//类别
                            BasicsGoodsCategory basicsGoodsCategory=iMasterBasicsGoodsCategoryMapper.selectByPrimaryKey(redPacketRecord.getFromObject());
                            if(basicsGoodsCategory!=null&&(categoryId.equals(basicsGoodsCategory.getId())||categoryTopId.equals(basicsGoodsCategory.getParentTop())||categoryTopId.equals(basicsGoodsCategory.getId()))){
                                totalMoney=MathUtil.add(totalMoney,MathUtil.mul(activityProductDTO.getPrice(),activityProductDTO.getShopCount()));
                                productlist.add(activityProductDTO.getProductId());
                            }
                        }else if(fromObjectType==4&&goodsId.equals(redPacketRecord.getFromObject())){//货品
                            totalMoney=MathUtil.add(totalMoney,MathUtil.mul(activityProductDTO.getPrice(),activityProductDTO.getShopCount()));
                            productlist.add(activityProductDTO.getProductId());
                        }else if(fromObjectType==5&&prodTemplateId.equals(redPacketRecord.getFromObject())){//商品
                            totalMoney=MathUtil.add(totalMoney,MathUtil.mul(activityProductDTO.getPrice(),activityProductDTO.getShopCount()));
                            productlist.add(activityProductDTO.getProductId());
                        }
                    }else if(redPacketRecord.getSourceType()==2&&redPacketRecord.getStorefrontId().equals(storefrontId)){//店铺券
                        if(fromObjectType==7){
                            totalMoney=MathUtil.add(totalMoney,MathUtil.mul(activityProductDTO.getPrice(),activityProductDTO.getShopCount()));
                            productlist.add(activityProductDTO.getProductId());
                        }else if(fromObjectType==3){//类别
                            BasicsGoodsCategory basicsGoodsCategory=iMasterBasicsGoodsCategoryMapper.selectByPrimaryKey(redPacketRecord.getFromObject());
                            if(basicsGoodsCategory!=null&&(categoryId.equals(basicsGoodsCategory.getId())||categoryTopId.equals(basicsGoodsCategory.getParentTop())||categoryTopId.equals(basicsGoodsCategory.getId()))){
                                totalMoney=MathUtil.add(totalMoney,MathUtil.mul(activityProductDTO.getPrice(),activityProductDTO.getShopCount()));
                                productlist.add(activityProductDTO.getProductId());
                            }
                        }else if(fromObjectType==4&&goodsId.equals(redPacketRecord.getFromObject())){//货品
                            totalMoney=MathUtil.add(totalMoney,MathUtil.mul(activityProductDTO.getPrice(),activityProductDTO.getShopCount()));
                            productlist.add(activityProductDTO.getProductId());
                        }else if(fromObjectType==5&&activityProductDTO.getProductId().equals(redPacketRecord.getFromObject())){//商品
                            totalMoney=MathUtil.add(totalMoney,MathUtil.mul(activityProductDTO.getPrice(),activityProductDTO.getShopCount()));
                            productlist.add(activityProductDTO.getProductId());
                        }
                    }
                }

                //优惠券类型 0为减免金额券 1 为折扣券 2代金券
                if ("0".equals(redPacketRecord.getType())||("1".equals(redPacketRecord.getType())&&redPacketRecord.getSatisfyMoney()>0)) {
                    //判断符合条件商品总额是否满足条件
                    if(MathUtil.sub(totalMoney,redPacketRecord.getSatisfyMoney())>0){
                        //计算可优惠金额
                        if("0".equals(redPacketRecord.getType())){
                            concessionMoney=redPacketRecord.getMoney();
                        }else if("1".equals(redPacketRecord.getType())){
                            concessionMoney=MathUtil.mul(MathUtil.mul(totalMoney,redPacketRecord.getSatisfyMoney()),10);
                        }
                        redPacketRecord.setTotalMoney(totalMoney);
                        redPacketRecord.setConcessionMoney(concessionMoney);
                        redPacketRecord.setProducts(StringUtils.strip(productlist.toString(),"[]"));
                        redPacetResultList.add(redPacketRecord);
                        if(recommendCuponsPack==null||StringUtils.isBlank(recommendCuponsPack.getId())){
                            recommendCuponsPack=redPacketRecord;
                        }else if(MathUtil.sub(redPacketRecord.getConcessionMoney(),recommendCuponsPack.getConcessionMoney())>0){
                            recommendCuponsPack=redPacketRecord;
                        }

                    }

                }else{
                    if("2".equals(redPacketRecord.getType())){
                        concessionMoney=redPacketRecord.getMoney();
                    }else if("1".equals(redPacketRecord.getType())){
                        concessionMoney=MathUtil.mul(MathUtil.mul(totalMoney,redPacketRecord.getSatisfyMoney()),10);
                    }
                    redPacketRecord.setTotalMoney(totalMoney);
                    redPacketRecord.setConcessionMoney(concessionMoney);
                    redPacketRecord.setProducts(StringUtils.strip(productlist.toString(),"[]"));
                    redPacetResultList.add(redPacketRecord);
                    if(recommendCuponsPack==null||StringUtils.isBlank(recommendCuponsPack.getId())){
                        recommendCuponsPack=redPacketRecord;
                    }else if(MathUtil.sub(redPacketRecord.getConcessionMoney(),recommendCuponsPack.getConcessionMoney())>0){
                        recommendCuponsPack=redPacketRecord;
                    }

                }

            }
            if (redPacetResultList.size() == 0) {
                return null;
            }
            redPackMap.put("recommendCuponsPack",recommendCuponsPack);//推荐优惠券
            redPackMap.put("redPacetResultList",redPacetResultList);//符合条件的优惠券
            return redPackMap;
        } catch (Exception e) {
            logger.error("查询失败",e);
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

            String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            Member member = (Member) object;
            List<ActivityProductDTO> activityProductDTOList=new ArrayList<>();//汇总所有购买商品
            ActivityProductDTO activityProductDTO;
            PaymentDTO paymentDTO = new PaymentDTO();
            paymentDTO.setType(4);
            paymentDTO.setDiscountsPrice(new BigDecimal(0));
            BigDecimal totalPrice = new BigDecimal(0);//总价
            BigDecimal freightPrice = new BigDecimal(0);//总运费
            BigDecimal totalMoveDost = new BigDecimal(0);//搬运费
            if (type == 1) {//精算商品
                HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(taskId);
                House house = houseMapper.selectByPrimaryKey(houseFlow.getHouseId());
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
                                activityProductDTO=new ActivityProductDTO();
                                activityProductDTO.setProductId(good.getProductId());
                                activityProductDTO.setPrice(good.getPrice().doubleValue());
                                activityProductDTO.setShopCount(good.getShopCount());
                                activityProductDTOList.add(activityProductDTO);
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
                        shoppingCartListDTO.setImageUrl(StringTool.getImage(shoppingCartListDTO.getImage(), imageAddress));//图多张
                        shoppingCartListDTO.setImageSingle(StringTool.getImageSingle(shoppingCartListDTO.getImage(), imageAddress));//图一张
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

                            if (!CommonUtil.isEmpty(shoppingCartListDTO.getValueIdArr())) {
                                String strNewValueNameArr = " ";
                                String[] ValueNameArrs = shoppingCartListDTO.getValueIdArr().split(",");
                                for (int i = 0; i < ValueNameArrs.length; i++) {
                                    String valueId = ValueNameArrs[i];
                                    if (StringUtils.isNotBlank(valueId)) {
                                        AttributeValue attributeValue = iMasterAttributeValueMapper.selectByPrimaryKey(valueId);
                                        if (attributeValue != null && StringUtils.isNotBlank(attributeValue.getName())) {
                                            if (StringUtils.isBlank(strNewValueNameArr)) {
                                                strNewValueNameArr = attributeValue.getName();
                                            } else {
                                                strNewValueNameArr = strNewValueNameArr + ", " + attributeValue.getName();
                                            }
                                        }
                                    }
                                }
                                shoppingCartListDTO.setValueNameArr(strNewValueNameArr);
                            }
                        }
                        activityProductDTO=new ActivityProductDTO();
                        activityProductDTO.setProductId(parmDTO.getProductId());
                        activityProductDTO.setPrice(parmDTO.getPrice());
                        activityProductDTO.setShopCount(parmDTO.getShopCount());
                        activityProductDTOList.add(activityProductDTO);
                    }
                    Double freight=storefrontConfigAPI.getFreightPrice(shoppingCartDTO.getStorefrontId(),totalSellPrice.doubleValue());
                    freightPrice=freightPrice.add(new BigDecimal(freight));
                    shoppingCartDTO.setTotalPrice(totalSellPrice);
                }
                paymentDTO.setDatas(shoppingCartDTOS);

            } else {
                return ServerResponse.createByErrorMessage("参数错误");
            }
            //获取符合条件的有效的优惠券
            Map<String,Object> redPackMap=discountPage(member.getId(),activityProductDTOList,null);
            if(redPackMap!=null){
                //推荐优惠券
                ActivityRedPackRecordDTO recommendCuponsPack=(ActivityRedPackRecordDTO)redPackMap.get("recommendCuponsPack");
                paymentDTO.setDiscountsPrice(BigDecimal.valueOf(recommendCuponsPack.getConcessionMoney()));
                paymentDTO.setRedPackRecordDTO(recommendCuponsPack);
                //可选优惠券列表
                paymentDTO.setRedPacetResultList((List)redPackMap.get("redPacetResultList"));
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
            logger.error("查询失败",e);
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
                accountFlowRecord.setDefinedAccountId(djSupplierPayOrder.getSupplierId());
                accountFlowRecord.setCreateBy(djSupplierPayOrder.getUserId());
                accountFlowRecord.setHouseOrderId(djSupplierPayOrder.getId());
                if (djSupplierPayOrder.getState() == 1 && djSupplierPayOrder.getSourceType() == 1) {
                    DjSupplier djSupplier = iMaterSupplierMapper.selectByPrimaryKey(djSupplierPayOrder.getSupplierId());
                    accountFlowRecord.setAmountBeforeMoney(djSupplier.getTotalAccount());//入账前金额
                    if (djSupplierPayOrder.getBusinessOrderType().equals("1")) {
                        djSupplier.setTotalAccount(djSupplier.getTotalAccount() + djSupplierPayOrder.getPrice());
//                        djSupplier.setSurplusMoney(djSupplier.getSurplusMoney() + djSupplierPayOrder.getPrice());
                        accountFlowRecord.setState(4);
                        accountFlowRecord.setDefinedName("供应商充值：" + djSupplierPayOrder.getPrice());
                    } else if (djSupplierPayOrder.getBusinessOrderType().equals("2")) {
                        djSupplier.setRetentionMoney(djSupplier.getRetentionMoney() + djSupplierPayOrder.getPrice());
                        accountFlowRecord.setState(5);
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
//                        storefront.setSurplusMoney(storefront.getSurplusMoney()+ djSupplierPayOrder.getPrice());
                        accountFlowRecord.setState(4);
                        accountFlowRecord.setDefinedName("店铺充值：" + djSupplierPayOrder.getPrice());
                    } else if (djSupplierPayOrder.getBusinessOrderType().equals("2")) {
                        storefront.setRetentionMoney(storefront.getRetentionMoney() + djSupplierPayOrder.getPrice());
                        accountFlowRecord.setState(5);
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
