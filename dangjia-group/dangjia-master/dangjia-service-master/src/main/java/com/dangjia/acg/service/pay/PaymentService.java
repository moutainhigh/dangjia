package com.dangjia.acg.service.pay;

import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.BasicsStorefrontAPI;
import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.api.StorefrontConfigAPI;
import com.dangjia.acg.api.actuary.BudgetMaterialAPI;
import com.dangjia.acg.api.actuary.BudgetWorkerAPI;
import com.dangjia.acg.api.data.ForMasterAPI;
import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.enums.AppType;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.activity.ActivityRedPackRecordDTO;
import com.dangjia.acg.dto.actuary.BudgetLabelDTO;
import com.dangjia.acg.dto.actuary.BudgetLabelGoodsDTO;
import com.dangjia.acg.dto.actuary.ShopGoodsDTO;
import com.dangjia.acg.dto.pay.ActuaryDTO;
import com.dangjia.acg.dto.pay.PaymentDTO;
import com.dangjia.acg.dto.pay.SafeTypeDTO;
import com.dangjia.acg.dto.pay.UpgradeSafeDTO;
import com.dangjia.acg.dto.product.ShoppingCartDTO;
import com.dangjia.acg.dto.product.ShoppingCartListDTO;
import com.dangjia.acg.mapper.activity.IActivityRedPackRecordMapper;
import com.dangjia.acg.mapper.core.*;
import com.dangjia.acg.mapper.delivery.*;
import com.dangjia.acg.mapper.house.*;
import com.dangjia.acg.mapper.member.ICustomerRecordMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.pay.IBusinessOrderMapper;
import com.dangjia.acg.mapper.pay.IMasterSupplierPayOrderMapper;
import com.dangjia.acg.mapper.pay.IPayOrderMapper;
import com.dangjia.acg.mapper.product.IShoppingCartMapper;
import com.dangjia.acg.mapper.repair.IChangeOrderMapper;
import com.dangjia.acg.mapper.repair.IMendMaterialMapper;
import com.dangjia.acg.mapper.repair.IMendOrderMapper;
import com.dangjia.acg.mapper.repair.IMendWorkerMapper;
import com.dangjia.acg.mapper.safe.IWorkerTypeSafeMapper;
import com.dangjia.acg.mapper.safe.IWorkerTypeSafeOrderMapper;
import com.dangjia.acg.mapper.worker.IInsuranceMapper;
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
import com.dangjia.acg.modle.pay.BusinessOrder;
import com.dangjia.acg.modle.pay.PayOrder;
import com.dangjia.acg.modle.product.DjBasicsGoods;
import com.dangjia.acg.modle.product.ShoppingCart;
import com.dangjia.acg.modle.repair.ChangeOrder;
import com.dangjia.acg.modle.repair.MendOrder;
import com.dangjia.acg.modle.safe.WorkerTypeSafe;
import com.dangjia.acg.modle.safe.WorkerTypeSafeOrder;
import com.dangjia.acg.modle.storefront.Storefront;
import com.dangjia.acg.modle.supplier.DjSupplierPayOrder;
import com.dangjia.acg.modle.worker.Insurance;
import com.dangjia.acg.service.account.MasterAccountFlowRecordService;
import com.dangjia.acg.service.acquisition.MasterCostAcquisitionService;
import com.dangjia.acg.service.config.ConfigMessageService;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.dangjia.acg.service.design.HouseDesignPayService;
import com.dangjia.acg.service.repair.MendOrderCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.*;

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
    private BasicsStorefrontAPI basicsStorefrontAPI;
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
            HouseExpend houseExpend = houseExpendMapper.getByHouseId(businessOrder.getHouseId());
            houseExpend.setTolMoney(houseExpend.getTolMoney() + businessOrder.getTotalPrice().doubleValue());//总金额
            houseExpend.setPayMoney(houseExpend.getPayMoney() + businessOrder.getPayPrice().doubleValue());//总支付
            houseExpend.setDisMoney(houseExpend.getDisMoney() + businessOrder.getDiscountsPrice().doubleValue());//总优惠
            example = new Example(Warehouse.class);
            example.createCriteria().andEqualTo(Warehouse.HOUSE_ID, businessOrder.getHouseId());
            List<Warehouse> warehouseList = warehouseMapper.selectByExample(example);
            houseExpend.setMaterialKind(warehouseList.size());//材料种类
            houseExpendMapper.updateByPrimaryKeySelective(houseExpend);
            return ServerResponse.createBySuccessMessage("支付成功");
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ServerResponse.createByErrorMessage("支付回调异常");
        }
    }

    /**
     * 移动端支付成功回调
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse setPaySuccess(String userToken, String businessOrderNumber) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
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
            HouseFlow houseFlow = houseFlowMapper.getByWorkerTypeId(order.getHouseId(),order.getWorkerTypeId());
            House house = houseMapper.selectByPrimaryKey(houseFlow.getHouseId());
            if (house.getMoney() == null) {
                house.setMoney(new BigDecimal(0));
            }
            HouseWorkerOrder hwo = houseWorkerOrderMapper.getByHouseIdAndWorkerTypeId(houseFlow.getHouseId(), houseFlow.getWorkerTypeId());
            HouseWorkerOrder houseWorkerOrdernew = new HouseWorkerOrder();
            houseWorkerOrdernew.setId(hwo.getId());
            houseWorkerOrdernew.setPayState(1);
            houseWorkerOrderMapper.updateByPrimaryKeySelective(houseWorkerOrdernew);
            //为兼容老数据，工序到了已被抢单的，说明已经支付完成，无需在做下一步操作
            if(houseFlow.getWorkType()!=3){
                houseFlow.setWorkType(2);
                houseFlow.setReleaseTime(new Date());//set发布时间
            }
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
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
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
            Order orderNew = new Order();
            BigDecimal paymentPrice = new BigDecimal(0);//总共钱
            BigDecimal freightPrice = new BigDecimal(0);//总运费
            BigDecimal totalMoveDost = new BigDecimal(0);//搬运费
            if(queryShopGoods.size()>1){
                orderNew.setHouseId(order.getHouseId());
                orderNew.setCityId(order.getCityId());
                orderNew.setMemberId(order.getMemberId());
                orderNew.setBusinessOrderNumber(order.getBusinessOrderNumber());
                orderNew.setType(order.getType());
                orderNew.setOrderNumber(System.currentTimeMillis() + "-" + (int) (Math.random() * 9000 + 1000));
                orderNew.setTotalDiscountPrice(new BigDecimal(0));
                orderNew.setTotalStevedorageCost(new BigDecimal(0));
                orderNew.setTotalTransportationCost(new BigDecimal(0));
                orderNew.setActualPaymentPrice(new BigDecimal(0));
                orderNew.setOrderStatus(payState);
                orderNew.setOrderGenerationTime(order.getOrderGenerationTime());
                orderNew.setOrderSource(order.getOrderSource());//精算制作
                orderNew.setWorkerId(order.getWorkerId());
                orderNew.setAddressId(order.getAddressId());
                orderNew.setCreateBy(order.getCreateBy());
                orderNew.setParentOrderId(order.getId());
                orderNew.setOrderPayTime(new Date());
                orderMapper.insert(orderNew);
            }else {
                orderNew = order;
            }
            if(order.getType()==0){
                orderNew.setWorkerTypeName("自购订单");
            }
            if(order.getType()==1){
                orderNew.setWorkerTypeName("人工订单");
            }
            if(order.getType()==2){
                orderNew.setWorkerTypeName("材料订单");
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
                    orderItem.setOrderStatus(payState);
                    orderItem.setOrderId(orderNew.getId());
                    orderItemMapper.updateByPrimaryKeySelective(orderItem);

                    if(good.getProductType()<3) {
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
            if(queryShopGoods.size()>1) {
                orderNew.setTotalTransportationCost(freightPrice);//总运费
                orderNew.setTotalStevedorageCost(totalMoveDost);//总搬运费
                orderNew.setTotalAmount(paymentPrice);// 订单总额(工钱)

                BigDecimal payPrice = orderNew.getTotalAmount().subtract(orderNew.getTotalDiscountPrice());
                payPrice = payPrice.add(orderNew.getTotalStevedorageCost());
                payPrice = payPrice.add(orderNew.getTotalTransportationCost());
                orderNew.setActualPaymentPrice(payPrice);
                orderNew.setPayment(payState);// 支付方式
                orderMapper.updateByPrimaryKeySelective(orderNew);
            }
        }
        BigDecimal payPrice = order.getTotalAmount().subtract(order.getTotalDiscountPrice());
        payPrice = payPrice.add(order.getTotalStevedorageCost());
        payPrice = payPrice.add(order.getTotalTransportationCost());
        order.setActualPaymentPrice(payPrice);
        order.setOrderPayTime(new Date());
        order.setPayment(payState);// 支付方式

        orderMapper.updateByPrimaryKeySelective(order);
        /**
         * 订单钱存入店铺账号余额，记录对应的流水信息
         */
        if(!CommonUtil.isEmpty(order.getStorefontId())) {
            masterAccountFlowRecordService.updateStoreAccountMoney(order.getStorefontId(),order.getHouseId(),0,order.getId(),order.getTotalAmount().doubleValue(),order.getWorkerTypeName(),"SYSTEM");
        }
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
     * 支付页面(通用)
     */
    public ServerResponse getPaymentAllOrder(String userToken, String houseDistributionId, int type) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        PaymentDTO paymentDTO = new PaymentDTO();
        List<ActuaryDTO> actuaryDTOList = new ArrayList<>();//商品
        try {
            String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            Example example = new Example(BusinessOrder.class);
            example.createCriteria().andEqualTo(BusinessOrder.TASK_ID, houseDistributionId).andNotEqualTo(BusinessOrder.STATE,4);
            List<BusinessOrder> businessOrderList = businessOrderMapper.selectByExample(example);
            BusinessOrder businessOrder = null;
            if(businessOrderList.size()>0){
                businessOrder = businessOrderList.get(0);
                if(businessOrder.getState()==3){
                    return ServerResponse.createByErrorMessage("该订单已支付，请勿重复支付！");
                }
            }

            if (type == 9) {
                Insurance insurance = insuranceMapper.selectByPrimaryKey(houseDistributionId);
                if (insurance == null) {
                    return ServerResponse.createByErrorMessage("保险记录不存在");
                }
                if (businessOrderList.size() == 0) {
                    businessOrder = new BusinessOrder();
                    businessOrder.setMemberId(insurance.getWorkerId()); //公众号唯一标识
                    businessOrder.setHouseId(null);
                    businessOrder.setNumber(System.currentTimeMillis() + "-" + (int) (Math.random() * 9000 + 1000));
                    businessOrder.setState(1);//刚生成
                    businessOrder.setTotalPrice(insurance.getMoney());
                    businessOrder.setDiscountsPrice(new BigDecimal(0));
                    businessOrder.setPayPrice(insurance.getMoney());
                    businessOrder.setType(9);//记录支付类型任务类型
                    businessOrder.setTaskId(houseDistributionId);//保存任务ID
                    businessOrderMapper.insert(businessOrder);
                }
                paymentDTO.setTotalPrice(insurance.getMoney());
                paymentDTO.setBusinessOrderNumber(businessOrder.getNumber());
                paymentDTO.setPayPrice(insurance.getMoney());//实付
                ActuaryDTO actuaryDTO = new ActuaryDTO();
                actuaryDTO.setImage(imageAddress + "icon/rmb.png");
                actuaryDTO.setKind("保险费");
                actuaryDTO.setName("保险服务（一年意外保险）");
                actuaryDTO.setPrice("¥" + String.format("%.2f", insurance.getMoney().doubleValue()));
                actuaryDTO.setType(7);
                actuaryDTOList.add(actuaryDTO);
            }else if (type == 1) {
                HouseDistribution houseDistribution = iHouseDistributionMapper.selectByPrimaryKey(houseDistributionId);
                if (houseDistribution == null) {
                    return ServerResponse.createByErrorMessage("验房分销记录不存在");
                }
                if (businessOrderList.size() == 0) {
                    businessOrder = new BusinessOrder();
                    businessOrder.setMemberId(houseDistribution.getOpenid()); //公众号唯一标识
                    businessOrder.setHouseId(houseDistribution.getOpenid());
                    businessOrder.setNumber(System.currentTimeMillis() + "-" + (int) (Math.random() * 9000 + 1000));
                    businessOrder.setState(1);//刚生成
                    businessOrder.setTotalPrice(new BigDecimal(houseDistribution.getPrice()));
                    businessOrder.setDiscountsPrice(new BigDecimal(0));
                    businessOrder.setPayPrice(new BigDecimal(houseDistribution.getPrice()));
                    businessOrder.setType(5);//记录支付类型任务类型
                    businessOrder.setTaskId(houseDistributionId);//保存任务ID
                    businessOrderMapper.insert(businessOrder);
                }
                paymentDTO.setTotalPrice(new BigDecimal(houseDistribution.getPrice()));
                paymentDTO.setBusinessOrderNumber(businessOrder.getNumber());
                paymentDTO.setPayPrice(new BigDecimal(houseDistribution.getPrice()));//实付
//                paymentDTO.setInfo("温馨提示: 您将支付" + houseDistribution.getPrice() + "元验房定金，实际费用将根据表格为准，线下补齐差额即可！");
                ActuaryDTO actuaryDTO = new ActuaryDTO();
                actuaryDTO.setImage(imageAddress + "icon/rmb.png");
                actuaryDTO.setKind("验房定金");
                actuaryDTO.setName("当家装修验房定金");
                actuaryDTO.setPrice("¥" + String.format("%.2f", houseDistribution.getPrice()));
                actuaryDTO.setType(6);
                actuaryDTOList.add(actuaryDTO);
            }
            return ServerResponse.createBySuccess("查询成功", paymentDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 支付页面
     * type   1工序支付任务,2其他
     */
    public ServerResponse getPaymentOrder(String userToken, String houseId, String orderId,int type) {
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

            House house = houseMapper.selectByPrimaryKey(houseId);
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
            if (type == 1) {//支付
                WorkerType workerType = workerTypeMapper.selectByPrimaryKey(order.getWorkerTypeId());
                paymentDTO.setWorkerTypeName(workerType.getName());
                /*
                 * 生成工匠订单
                 */
                HouseWorkerOrder hwo = houseWorkerOrderMapper.getByHouseIdAndWorkerTypeId(houseId, order.getWorkerTypeId());
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
                if (type == 1 && workerType.getType() > 2) {//精算师和设计师不存在保险订单
                    //查出有没有生成保险订单
                    example = new Example(WorkerTypeSafeOrder.class);
                    example.createCriteria().andEqualTo(WorkerTypeSafeOrder.HOUSE_ID, houseId).andEqualTo(WorkerTypeSafeOrder.WORKER_TYPE_ID, workerType.getId()).andEqualTo(WorkerTypeSafeOrder.DATA_STATUS, 0);
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
                    if(budgetLabelGoodsDTO.getDeleteState()!=2) {
                        totalZPrice = totalZPrice.add(budgetLabelGoodsDTO.getTotalPrice());
                    }
                    if(!CommonUtil.isEmpty(budgetLabelGoodsDTO.getGoodsId())){
                        Brand brand =null;
                        DjBasicsGoods goods=iMasterBasicsGoodsMapper.selectByPrimaryKey(budgetLabelGoodsDTO.getGoodsId());
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
     * @param houseId 房子ID
     * @param workerId 工人ID
     * @param addressId 地址ID
     * @return
     */
    public ServerResponse generateOrder(String userToken,String cityId,String houseId, String workerId, String addressId, String productIds) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = (Member) object;
            //处理人工
            House house = houseMapper.selectByPrimaryKey(houseId);
            if(house!=null){
                cityId=house.getCityId();
            }
            String[] productIdlist=null;
            if(!CommonUtil.isEmpty(productIds)){
                productIdlist=productIds.split(",");
            }
            List<ShoppingCartDTO> shoppingCartDTOS=new ArrayList<>();
            BigDecimal paymentPrice = new BigDecimal(0);//总共钱
            BigDecimal freightPrice = new BigDecimal(0);//总运费
            BigDecimal totalMoveDost = new BigDecimal(0);//搬运费
            List<String> strings = iShoppingCartMapper.queryStorefrontIds(member.getId(),cityId);
            for (String str : strings) {
                BigDecimal totalSellPrice = new BigDecimal(0);//总价
                BigDecimal totalMaterialPrice = new BigDecimal(0);//组总价
                ShoppingCartDTO shoppingCartDTO=new ShoppingCartDTO();
                shoppingCartDTO.setStorefrontId(str);
                List<ShoppingCartListDTO> shoppingCartListDTOS = iShoppingCartMapper.queryCartList(member.getId(),cityId, str,productIdlist);
                for (ShoppingCartListDTO shoppingCartListDTO : shoppingCartListDTOS) {
                    BigDecimal totalPrice = new BigDecimal(shoppingCartListDTO.getPrice()*shoppingCartListDTO.getSellPrice());
                    if(shoppingCartListDTO.getProductType()==0) {
                        totalMaterialPrice = totalMaterialPrice.add(totalPrice);
                    }
                    totalSellPrice = totalSellPrice.add(totalPrice);
                    paymentPrice = paymentPrice.add(totalPrice);
                }
                shoppingCartDTO.setTotalMaterialPrice(totalMaterialPrice);
                shoppingCartDTO.setTotalPrice(totalSellPrice);
                shoppingCartDTO.setShoppingCartListDTOS(shoppingCartListDTOS);
                if(shoppingCartListDTOS.size()>0) {
                    shoppingCartDTOS.add(shoppingCartDTO);
                }
            }

            if (shoppingCartDTOS!=null) {
                Order order = new Order();
                if(house!=null) {
                    order.setHouseId(house.getId());
                }
                order.setWorkerTypeName("购物车订单");
                order.setCityId(cityId);
                order.setMemberId(member.getId());
//                order.setBusinessOrderNumber(businessOrderNumber);
                order.setType(0);
                order.setOrderNumber(System.currentTimeMillis() + "-" + (int) (Math.random() * 9000 + 1000));
                order.setTotalDiscountPrice(new BigDecimal(0));
                order.setTotalStevedorageCost(new BigDecimal(0));
                order.setTotalTransportationCost(new BigDecimal(0));
                order.setActualPaymentPrice(new BigDecimal(0));
                order.setOrderStatus("1");
                order.setOrderGenerationTime(new Date());
                order.setOrderSource(2);//来源购物车
                order.setWorkerId(workerId);
                order.setAddressId(addressId);
                order.setCreateBy(member.getId());
                orderMapper.insert(order);
                for (ShoppingCartDTO shoppingCartDTO : shoppingCartDTOS) {
                    Double freight=storefrontConfigAPI.getFreightPrice(shoppingCartDTO.getStorefrontId(),shoppingCartDTO.getTotalMaterialPrice().doubleValue());
                    freightPrice=freightPrice.add(new BigDecimal(freight));
                    for (ShoppingCartListDTO good : shoppingCartDTO.getShoppingCartListDTOS()) {
                        OrderItem orderItem = new OrderItem();
                        orderItem.setIsReservationDeliver(good.getIsReservationDeliver());
                        orderItem.setOrderId(order.getId());
                        orderItem.setHouseId(houseId);
                        orderItem.setPrice(good.getPrice().doubleValue());//销售价
                        orderItem.setShopCount(good.getShopCount().doubleValue());//购买总数
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
                        if(good.getProductType()==0&&freight>0){
                            //均摊运费
                            Double transportationCost=(orderItem.getTotalPrice()/shoppingCartDTO.getTotalMaterialPrice().doubleValue())*freight;
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
                    }
                }


                // 生成支付业务单
                Example example = new Example(BusinessOrder.class);
                example.createCriteria().andEqualTo(BusinessOrder.TASK_ID, order.getId()).andNotEqualTo(BusinessOrder.STATE, 4);
                List<BusinessOrder> businessOrderList = businessOrderMapper.selectByExample(example);
                BusinessOrder businessOrder = null;
                if (businessOrderList.size() > 0) {
                    businessOrder = businessOrderList.get(0);
                    if (businessOrder.getState() == 3) {
                        return ServerResponse.createByErrorMessage("该订单已支付，请勿重复支付！");
                    }
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

                example = new Example(ShoppingCart.class);
                example.createCriteria().andEqualTo(ShoppingCart.MEMBER_ID, member.getId())
                        .andIn(ShoppingCart.PRODUCT_ID,Arrays.asList(productIdlist));
                iShoppingCartMapper.deleteByExample(example);
                return ServerResponse.createBySuccess("提交成功", order.getId());
            }
            return ServerResponse.createBySuccess("提交成功");
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ServerResponse.createByErrorMessage("提交失败：原因："+e.getMessage());
        }
    }

    /**
     * 提交订单(精算)
     * @param taskId 工序ID
     * @param addressId 地址ID
     * @return
     */
    public ServerResponse generateBudgetOrder(String userToken,String cityId,String taskId, String addressId) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = (Member) object;

            HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(taskId);
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
                    order.setWorkerTypeName(wt.getName() + "订单");
                }
                order.setCityId(cityId);
                order.setWorkerTypeId(houseFlow.getWorkerTypeId());
                order.setMemberId(member.getId());
//                order.setBusinessOrderNumber(businessOrderNumber);
                order.setType(0);
                order.setOrderNumber(System.currentTimeMillis() + "-" + (int) (Math.random() * 9000 + 1000));
                order.setTotalDiscountPrice(new BigDecimal(0));
                order.setTotalStevedorageCost(new BigDecimal(0));
                order.setTotalTransportationCost(new BigDecimal(0));
                order.setActualPaymentPrice(new BigDecimal(0));
                order.setOrderStatus("1");
                order.setOrderGenerationTime(new Date());
                order.setOrderSource(1);//精算制作
                order.setWorkerId(houseFlow.getWorkerId());
                order.setAddressId(addressId);
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
                        }
                    }
                }


                // 生成支付业务单
                Example example = new Example(BusinessOrder.class);
                example.createCriteria().andEqualTo(BusinessOrder.TASK_ID, order.getId()).andNotEqualTo(BusinessOrder.STATE, 4);
                List<BusinessOrder> businessOrderList = businessOrderMapper.selectByExample(example);
                BusinessOrder businessOrder = null;
                if (businessOrderList.size() > 0) {
                    businessOrder = businessOrderList.get(0);
                    if (businessOrder.getState() == 3) {
                        return ServerResponse.createByErrorMessage("该订单已支付，请勿重复支付！");
                    }
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
                return ServerResponse.createBySuccess("提交成功", order.getId());
            }
            return ServerResponse.createBySuccess("提交成功");
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ServerResponse.createByErrorMessage("提交失败：原因："+e.getMessage());
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
     * @param type   1精算商品,2购物车商品
     */
    public ServerResponse getPaymentPage(String userToken, String houseId, String taskId, String cityId,int type) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = (Member) object;
            House house = houseMapper.selectByPrimaryKey(houseId);
            PaymentDTO paymentDTO = new PaymentDTO();
            BigDecimal totalPrice = new BigDecimal(0);//总价
            if (type == 1) {//精算商品
                HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(taskId);
                if (houseFlow.getWorkType() == 2) {
                    return ServerResponse.createByErrorMessage("等待工匠抢单");
                }
                if (houseFlow.getWorkType() == 4) {
                    return ServerResponse.createByErrorMessage("该订单已支付");
                }
                WorkerType workerType = workerTypeMapper.selectByPrimaryKey(houseFlow.getWorkerTypeId());
                paymentDTO.setWorkerTypeName(workerType.getName());
                List<ShopGoodsDTO> budgetLabelDTOS = forMasterAPI.queryShopGoods(houseId, houseFlow.getWorkerTypeId(), house.getCityId());//精算工钱
                for (ShopGoodsDTO budgetLabelDTO : budgetLabelDTOS) {
                    for (BudgetLabelDTO labelDTO : budgetLabelDTO.getLabelDTOS()) {
                        totalPrice = totalPrice.add(labelDTO.getTotalPrice());
                    }
                }
                paymentDTO.setDatas(budgetLabelDTOS);

                //该工钟所有保险
                Example example = new Example(WorkerTypeSafe.class);
                example.createCriteria().andEqualTo(WorkerTypeSafe.WORKER_TYPE_ID, houseFlow.getWorkerTypeId());
                List<WorkerTypeSafe> wtsList = workerTypeSafeMapper.selectByExample(example);


                //有保险服务
                if (wtsList.size() > 0) {
                    //查出有没有生成保险订单
                    example = new Example(WorkerTypeSafeOrder.class);
                    example.createCriteria().andEqualTo(WorkerTypeSafeOrder.HOUSE_ID, houseId).andEqualTo(WorkerTypeSafeOrder.DATA_STATUS, 0)
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
            } else if (type == 2) {//购物车商品
                List<String> strings = iShoppingCartMapper.queryStorefrontIds(member.getId(),cityId);
                List<ShoppingCartDTO> shoppingCartDTOS=new ArrayList<>();
                for (String str : strings) {
                    BigDecimal totalSellPrice = new BigDecimal(0);//总价
                    ShoppingCartDTO shoppingCartDTO=new ShoppingCartDTO();
                    Storefront storefront = basicsStorefrontAPI.querySingleStorefrontById(str);
                    shoppingCartDTO.setStorefrontName(storefront.getStorefrontName());
                    shoppingCartDTO.setStorefrontId(storefront.getId());
                    List<ShoppingCartListDTO> shoppingCartListDTOS = iShoppingCartMapper.queryCartList(member.getId(),cityId, str,null);
                    for (ShoppingCartListDTO shoppingCartListDTO : shoppingCartListDTOS) {
                        totalSellPrice = totalSellPrice.add(new BigDecimal(shoppingCartListDTO.getPrice()*shoppingCartListDTO.getSellPrice()));
                        totalPrice = totalPrice.add(new BigDecimal(shoppingCartListDTO.getPrice()*shoppingCartListDTO.getSellPrice()));
                    }
                    shoppingCartDTO.setTotalPrice(totalSellPrice);
                    shoppingCartDTO.setShoppingCartListDTOS(shoppingCartListDTOS);
                    shoppingCartDTOS.add(shoppingCartDTO);
                }
                paymentDTO.setDatas(shoppingCartDTOS);

            } else {
                return ServerResponse.createByErrorMessage("参数错误");
            }
            paymentDTO.setTotalPrice(totalPrice);
            paymentDTO.setDiscounts(0);
            paymentDTO.setHouseId(houseId);
            paymentDTO.setTaskId(taskId);
            paymentDTO.setType(type);
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
}
