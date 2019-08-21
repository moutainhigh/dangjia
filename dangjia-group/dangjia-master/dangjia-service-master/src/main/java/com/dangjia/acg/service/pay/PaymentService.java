package com.dangjia.acg.service.pay;

import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.api.actuary.BudgetMaterialAPI;
import com.dangjia.acg.api.actuary.BudgetWorkerAPI;
import com.dangjia.acg.api.data.ForMasterAPI;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.enums.AppType;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.common.util.JsmsUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.activity.ActivityRedPackRecordDTO;
import com.dangjia.acg.dto.actuary.FlowActuaryDTO;
import com.dangjia.acg.dto.group.GroupDTO;
import com.dangjia.acg.dto.pay.*;
import com.dangjia.acg.mapper.activity.IActivityRedPackRecordMapper;
import com.dangjia.acg.mapper.core.IHouseFlowMapper;
import com.dangjia.acg.mapper.core.IHouseWorkerMapper;
import com.dangjia.acg.mapper.core.IHouseWorkerOrderMapper;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.deliver.*;
import com.dangjia.acg.mapper.design.IHouseStyleTypeMapper;
import com.dangjia.acg.mapper.house.*;
import com.dangjia.acg.mapper.member.ICustomerRecordMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.other.IWorkDepositMapper;
import com.dangjia.acg.mapper.pay.IBusinessOrderMapper;
import com.dangjia.acg.mapper.pay.IPayOrderMapper;
import com.dangjia.acg.mapper.repair.IChangeOrderMapper;
import com.dangjia.acg.mapper.repair.IMendMaterialMapper;
import com.dangjia.acg.mapper.repair.IMendOrderMapper;
import com.dangjia.acg.mapper.repair.IMendWorkerMapper;
import com.dangjia.acg.mapper.safe.IWorkerTypeSafeMapper;
import com.dangjia.acg.mapper.safe.IWorkerTypeSafeOrderMapper;
import com.dangjia.acg.mapper.worker.IInsuranceMapper;
import com.dangjia.acg.modle.activity.ActivityRedPackRecord;
import com.dangjia.acg.modle.actuary.BudgetMaterial;
import com.dangjia.acg.modle.actuary.BudgetWorker;
import com.dangjia.acg.modle.basics.Product;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.core.HouseWorker;
import com.dangjia.acg.modle.core.HouseWorkerOrder;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.deliver.*;
import com.dangjia.acg.modle.design.HouseStyleType;
import com.dangjia.acg.modle.group.Group;
import com.dangjia.acg.modle.house.*;
import com.dangjia.acg.modle.member.CustomerRecord;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.other.WorkDeposit;
import com.dangjia.acg.modle.pay.BusinessOrder;
import com.dangjia.acg.modle.pay.PayOrder;
import com.dangjia.acg.modle.pay.PurchaseOrder;
import com.dangjia.acg.modle.repair.ChangeOrder;
import com.dangjia.acg.modle.repair.MendMateriel;
import com.dangjia.acg.modle.repair.MendOrder;
import com.dangjia.acg.modle.repair.MendWorker;
import com.dangjia.acg.modle.safe.WorkerTypeSafe;
import com.dangjia.acg.modle.safe.WorkerTypeSafeOrder;
import com.dangjia.acg.modle.worker.Insurance;
import com.dangjia.acg.service.config.ConfigMessageService;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.dangjia.acg.service.deliver.ProductChangeService;
import com.dangjia.acg.service.design.HouseDesignPayService;
import com.dangjia.acg.service.member.GroupInfoService;
import com.dangjia.acg.service.repair.MendOrderCheckService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
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
    private IHouseWorkerMapper houseWorkerMapper;
    @Autowired
    private IHouseMapper houseMapper;
    @Autowired
    private IHouseStyleTypeMapper houseStyleTypeMapper;
    @Autowired
    private IWorkerTypeSafeMapper workerTypeSafeMapper;
    @Autowired
    private IWorkerTypeSafeOrderMapper workerTypeSafeOrderMapper;
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
    private GroupInfoService groupInfoService;
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
    private IWorkDepositMapper workDepositMapper;
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
    private ProductChangeService productChangeService;
    @Autowired
    private IProductChangeOrderMapper productChangeOrderMapper;
    @Autowired
    private HouseDesignPayService houseDesignPayService;
    @Autowired
    private CraftsmanConstructionService constructionService;
    @Autowired
    private PurchaseOrderService purchaseOrderService;
    @Autowired
    private ICustomerRecordMapper customerRecordMapper;

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
                //工序支付
                this.payWorkerType(businessOrder.getNumber(), businessOrder.getTaskId(), payState);
            } else if (businessOrder.getType() == 2) {
                //处理补货补人工
                this.mendOrder(businessOrder, payState);
            } else if (businessOrder.getType() == 4) {//待付款 支付时业主包括取消的
                //待付款 提前付材料
                this.awaitPay(businessOrder, payState);
            } else if (businessOrder.getType() == 8) {//待付款 支付时业主包括取消的
                this.awaitPayPurchaseOrder(businessOrder, payState);
            } else if (businessOrder.getType() == 9) {//工人保险
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
            } else if (businessOrder.getType() == 6) {//待付款 更换结算
                HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                        .getRequest();
                House house = houseMapper.selectByPrimaryKey(businessOrder.getHouseId());
                request.setAttribute(Constants.CITY_ID, house.getCityId());
                //待付款 提前付材料
                productChangeService.orderBackFun(request, businessOrder.getTaskId());
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

    /**
     * 处理补货补人工
     */
    private void mendOrder(BusinessOrder businessOrder, String payState) {
        try {
            MendOrder mendOrder = mendOrderMapper.selectByPrimaryKey(businessOrder.getTaskId());
            HouseExpend houseExpend = houseExpendMapper.getByHouseId(businessOrder.getHouseId());
            House house = houseMapper.selectByPrimaryKey(businessOrder.getHouseId());
            WorkerType workerType = workerTypeMapper.selectByPrimaryKey(mendOrder.getWorkerTypeId());
            //处理审核通过
            if (mendOrder.getType() == 0 || mendOrder.getType() == 1) {
                //获取业主当前最新的userToken
                String userRole = "role1:" + house.getMemberId();
                String userToken = redisClient.getCache(userRole, String.class);
                mendOrderCheckService.checkMendWorkerOrder(userToken, mendOrder, "1", 2);
            }
            if (mendOrder.getType() == 0) {//补货
                houseExpend.setMaterialMoney(houseExpend.getMaterialMoney() + businessOrder.getTotalPrice().doubleValue());//材料
                houseExpendMapper.updateByPrimaryKeySelective(houseExpend);

                mendOrder.setState(4);//业主已支付补材料
                mendOrderMapper.updateByPrimaryKeySelective(mendOrder);

                Example example = new Example(MendMateriel.class);
                example.createCriteria().andEqualTo(MendMateriel.MEND_ORDER_ID, mendOrder.getId());
                List<MendMateriel> mendMaterielList = mendMaterialMapper.selectByExample(example);
                Order order = new Order();
                order.setHouseId(businessOrder.getHouseId());
                order.setBusinessOrderNumber(businessOrder.getNumber());//业务订单号
                order.setTotalAmount(businessOrder.getTotalPrice());// 订单总额(补材料总钱)

                example = new Example(MendOrder.class);
                example.createCriteria().andEqualTo(MendOrder.HOUSE_ID, businessOrder.getHouseId()).andEqualTo(MendOrder.TYPE, 0)
                        .andEqualTo(MendOrder.STATE, 4);
                order.setWorkerTypeName(workerType.getName() + "补货" + "00" + (mendOrderMapper.selectCountByExample(example) + 1));
                order.setWorkerTypeId(mendOrder.getWorkerTypeId());
                order.setPayment(payState);// 支付方式
                order.setType(2);//材料
                orderMapper.insert(order);

                WarehouseDetail warehouseDetail = new WarehouseDetail();
                warehouseDetail.setHouseId(businessOrder.getHouseId());
                warehouseDetail.setRecordType(2);//补材料
                warehouseDetail.setRelationId(order.getId());
                warehouseDetailMapper.insert(warehouseDetail);

                //处理补材料
                for (MendMateriel mendMateriel : mendMaterielList) {
                    Product product=forMasterAPI.getProduct(house.getCityId(),mendMateriel.getProductId());
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrderId(order.getId());
                    orderItem.setHouseId(businessOrder.getHouseId());
                    orderItem.setProductId(mendMateriel.getProductId());//货品id
                    orderItem.setProductSn(product.getProductSn());//货品编号
                    orderItem.setProductName(product.getName());//货品名称
                    orderItem.setProductNickName(mendMateriel.getProductNickName() + "");//货品昵称
                    orderItem.setPrice(mendMateriel.getPrice());//销售价
                    orderItem.setCost(mendMateriel.getCost());//成本价
                    orderItem.setShopCount(mendMateriel.getShopCount());//购买总数
                    orderItem.setUnitName(mendMateriel.getUnitName());//单位
                    orderItem.setTotalPrice(mendMateriel.getTotalPrice());//总价
                    orderItem.setProductType(mendMateriel.getProductType());//0：材料；1：包工包料
                    orderItem.setCategoryId(mendMateriel.getCategoryId());
                    orderItem.setImage(product.getImage());
                    orderItem.setCityId(house.getCityId());
                    orderItemMapper.insert(orderItem);
                    example = new Example(Warehouse.class);
                    example.createCriteria().andEqualTo(Warehouse.HOUSE_ID, businessOrder.getHouseId()).andEqualTo(Warehouse.PRODUCT_ID, mendMateriel.getProductId());
                    int sum = warehouseMapper.selectCountByExample(example);
                    if (sum > 0) {//仓库购买过
                        Warehouse warehouse = warehouseMapper.getByProductId(mendMateriel.getProductId(), businessOrder.getHouseId());
                        warehouse.setShopCount(warehouse.getShopCount() + mendMateriel.getShopCount());//数量
                        warehouse.setRepairCount(warehouse.getRepairCount() + mendMateriel.getShopCount());
                        warehouse.setPrice(mendMateriel.getPrice());
                        warehouse.setCost(mendMateriel.getCost());
                        warehouse.setProductName(product.getName());
                        warehouse.setImage(mendMateriel.getImage());
                        warehouse.setRepTime(warehouse.getRepTime() + 1);//补次数
                        warehouseMapper.updateByPrimaryKeySelective(warehouse);
                    } else {
                        Warehouse warehouse = new Warehouse();
                        warehouse.setHouseId(businessOrder.getHouseId());
                        warehouse.setShopCount(mendMateriel.getShopCount());
                        warehouse.setRepairCount(mendMateriel.getShopCount());
                        warehouse.setStayCount(0.0);
                        warehouse.setBudgetCount(0.0);
                        warehouse.setRobCount(0.0);
                        warehouse.setAskCount(0.0);//已要数量
                        warehouse.setBackCount(0.0);//退总数
                        warehouse.setReceive(0.0);
                        warehouse.setProductId(mendMateriel.getProductId());
                        warehouse.setProductSn(product.getProductSn());
                        warehouse.setProductName(product.getName());
                        warehouse.setPrice(mendMateriel.getPrice());
                        warehouse.setCost(mendMateriel.getCost());
                        warehouse.setUnitName(mendMateriel.getUnitName());
                        warehouse.setProductType(mendMateriel.getProductType());
                        warehouse.setCategoryId(product.getCategoryId());
                        warehouse.setImage(mendMateriel.getImage());
                        warehouse.setPayTime(0);
                        warehouse.setAskTime(0);
                        warehouse.setRepTime(1);//补次数
                        warehouse.setBackTime(0);
                        warehouse.setCityId(house.getCityId());
                        warehouseMapper.insert(warehouse);
                    }
                }
                orderSplit(mendOrder.getHouseId(), mendOrder.getId(), workerType.getType());
            } else if (mendOrder.getType() == 1) {//补人工
                houseExpend.setWorkerMoney(houseExpend.getWorkerMoney() + businessOrder.getTotalPrice().doubleValue());//人工
                houseExpendMapper.updateByPrimaryKeySelective(houseExpend);
                mendOrder.setState(4);//业主已支付补人工
                mendOrderMapper.updateByPrimaryKeySelective(mendOrder);
                ChangeOrder changeOrder = changeOrderMapper.selectByPrimaryKey(mendOrder.getChangeOrderId());
                changeOrder.setState(4);//已支付
                changeOrderMapper.updateByPrimaryKeySelective(changeOrder);
                //若工序发生补人工，则所有未完工工序顺延XX天
                Example example = new Example(HouseFlow.class);
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
//                houseFlowScheduleService.updateFlowSchedule(changeOrder.getHouseId(),changeOrder.getWorkerTypeId(),changeOrder.getScheduleDay(),null);

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

                example = new Example(MendWorker.class);
                example.createCriteria().andEqualTo(MendWorker.MEND_ORDER_ID, mendOrder.getId());
                List<MendWorker> mendWorkerList = mendWorkerMapper.selectByExample(example);

                Order order = new Order();
                order.setHouseId(businessOrder.getHouseId());
                order.setBusinessOrderNumber(businessOrder.getNumber());//业务订单号
                order.setTotalAmount(businessOrder.getTotalPrice());// 订单总额(补人工总钱)

                example = new Example(MendOrder.class);
                example.createCriteria().andEqualTo(MendOrder.HOUSE_ID, businessOrder.getHouseId()).andEqualTo(MendOrder.TYPE, 1)
                        .andEqualTo(MendOrder.STATE, 4);
                order.setWorkerTypeName(workerType.getName() + "补人工" + "00" + (mendOrderMapper.selectCountByExample(example) + 1));
                order.setWorkerTypeId(mendOrder.getWorkerTypeId());//补人工记录工种
                order.setPayment(payState);// 支付方式
                order.setType(1);//人工
                orderMapper.insert(order);
                for (MendWorker mendWorker : mendWorkerList) {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrderId(order.getId());
                    orderItem.setHouseId(businessOrder.getHouseId());
                    orderItem.setPrice(mendWorker.getPrice());//销售价
                    orderItem.setShopCount(mendWorker.getShopCount());//购买总数
                    orderItem.setUnitName(mendWorker.getUnitName());//单位
                    orderItem.setTotalPrice(mendWorker.getTotalPrice());//总价
                    orderItem.setWorkerGoodsName(mendWorker.getWorkerGoodsName());
                    orderItem.setWorkerGoodsSn(mendWorker.getWorkerGoodsSn());
                    orderItem.setWorkerGoodsId(mendWorker.getWorkerGoodsId());
                    orderItem.setImage(mendWorker.getImage());
                    orderItem.setCityId(house.getCityId());
                    orderItemMapper.insert(orderItem);
                    /*记录补数量*/
                    forMasterAPI.repairCount(house.getCityId(), mendOrder.getHouseId(), mendWorker.getWorkerGoodsId(), mendWorker.getShopCount());
                }
            }
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
    private void payWorkerType(String businessOrderNumber, String houseFlowId, String payState) {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                    .getRequest();
            HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(houseFlowId);
            House house = houseMapper.selectByPrimaryKey(houseFlow.getHouseId());
            if (house.getMoney() == null) {
                house.setMoney(new BigDecimal(0));
            }
            HouseWorkerOrder hwo = houseWorkerOrderMapper.getByHouseIdAndWorkerTypeId(houseFlow.getHouseId(), houseFlow.getWorkerTypeId());
            HouseWorkerOrder houseWorkerOrdernew = new HouseWorkerOrder();
            houseWorkerOrdernew.setId(hwo.getId());
            houseWorkerOrdernew.setPayState(1);
            houseWorkerOrderMapper.updateByPrimaryKeySelective(houseWorkerOrdernew);

            HouseWorker houseWorker = houseWorkerMapper.getByWorkerTypeId(houseFlow.getHouseId(), houseFlow.getWorkerTypeId(), 1);
            houseWorker.setWorkType(6);
            hwo.setModifyDate(new Date());
            houseWorkerMapper.updateByPrimaryKeySelective(houseWorker);

            houseFlow.setWorkerId(hwo.getWorkerId());
            houseFlow.setWorkType(4);
            houseFlow.setMaterialPrice(hwo.getMaterialPrice());
            houseFlow.setWorkPrice(hwo.getWorkPrice());
            houseFlow.setTotalPrice(hwo.getTotalPrice());
            houseFlow.setWorkSteta(3);//待交底
            houseFlow.setModifyDate(new Date());
            houseFlowMapper.updateByPrimaryKeySelective(houseFlow);
            if (hwo.getWorkerType() == 1) { //设计费用处理
                Order order = new Order();
                order.setHouseId(hwo.getHouseId());
                order.setBusinessOrderNumber(businessOrderNumber);
                order.setTotalAmount(hwo.getWorkPrice());//设计费
                order.setWorkerTypeName("设计订单");
                order.setWorkerTypeId(hwo.getWorkerTypeId());
                HouseStyleType houseStyleType = houseStyleTypeMapper.selectByPrimaryKey(house.getStyleId());
                order.setStyleName(house.getStyle());
                order.setStylePrice(houseStyleType.getPrice());//风格价格
                order.setType(1);//人工订单
                order.setPayment(payState);
                orderMapper.insert(order);
                house.setDesignerOk(1);
                houseMapper.updateByPrimaryKeySelective(house);
            } else if (hwo.getWorkerType() == 2) {//精算费用处理
                Order order = new Order();
                WorkDeposit workDeposit = workDepositMapper.selectByPrimaryKey(house.getWorkDepositId());//结算比例表
                order.setBudgetCost(workDeposit.getBudgetCost());//精算价格
                order.setHouseId(hwo.getHouseId());
                order.setBusinessOrderNumber(businessOrderNumber);
                order.setTotalAmount(hwo.getWorkPrice());//精算费
                order.setWorkerTypeName("精算订单");
                order.setWorkerTypeId(hwo.getWorkerTypeId());
                order.setType(1);//人工订单
                order.setPayment(payState);
                orderMapper.insert(order);
                house.setBudgetOk(1);//房间工种表里标记开始精算
                houseMapper.updateByPrimaryKeySelective(house);
                //推送消息给精算师业主已付款
                configMessageService.addConfigMessage(null, AppType.GONGJIANG, house.getMemberId(),
                        "0", "业主支付精算费", String.format(DjConstants.PushMessage.JINGSUANFEIZHIFUWANC,
                                house.getHouseName()), "");
            } else {//其它工种
                /*不统计设计精算人工*/
                HouseExpend houseExpend = houseExpendMapper.getByHouseId(hwo.getHouseId());
                houseExpend.setMaterialMoney(houseExpend.getMaterialMoney() + hwo.getMaterialPrice().doubleValue());//材料
                houseExpend.setWorkerMoney(houseExpend.getWorkerMoney() + hwo.getWorkPrice().doubleValue());//人工
                houseExpendMapper.updateByPrimaryKeySelective(houseExpend);

                /*处理人工和取消的材料改到自购精算*/
                budgetCorrect(businessOrderNumber, payState, houseFlowId);

                /*处理保险订单*/
                this.insurance(hwo, payState);
            }
            /*记录项目流水工钱+材料钱*/
            //liuShui(businessOrderNumber,house,hwo,paystate);
            //业主信息
            Member member = memberMapper.selectByPrimaryKey(house.getMemberId());
            //工人信息
            Member memberGr = memberMapper.selectByPrimaryKey(houseWorker.getWorkerId());
            //app推送和发送短信给工匠
            if (houseWorker.getWorkerType() == 1) {//设计师

                configMessageService.addConfigMessage(null, AppType.GONGJIANG, houseWorker.getWorkerId(), "0", "业主支付提醒",
                        String.format(DjConstants.PushMessage.PAYMENT_OF_DESIGN_FEE, member.getMobile(), house.getHouseName()), "5");
                //短信通知
                JsmsUtil.sendDesigner(memberGr.getMobile(), member.getMobile(), house.getHouseName());

            }
            if (houseWorker.getWorkerType() == 3) {//大管家
                configMessageService.addConfigMessage(null, AppType.GONGJIANG, houseWorker.getWorkerId(), "0", "业主支付提醒",
                        String.format(DjConstants.PushMessage.STEWARD_PAYMENT, house.getHouseName()), "5");
            }
            if (houseWorker.getWorkerType() > 3) {//其它工种
                configMessageService.addConfigMessage(null, AppType.GONGJIANG, houseWorker.getWorkerId(), "0", "业主支付提醒",
                        String.format(DjConstants.PushMessage.CRAFTSMAN_PAYMENT, house.getHouseName()), "5");

                HouseFlow houseFlowDgj = houseFlowMapper.getHouseFlowByHidAndWty(houseFlow.getHouseId(), 3);
                configMessageService.addConfigMessage(null, AppType.GONGJIANG, houseFlowDgj.getWorkerId(), "0", "业主支付提醒",
                        String.format(DjConstants.PushMessage.STEWARD_CRAFTSMAN_TWO_PAYMENT, house.getHouseName()), "5");

            }
            /*支付完成后将工人拉入激光群组内，方便交流*/
            //售中客服，设计师，精算师无需进群
            if (!("1".equals(houseFlow.getWorkerTypeId()) || "2".equals(houseFlow.getWorkerTypeId()))) {
                addGroupMember(request, houseFlow.getHouseId(), houseFlow.getWorkerId());
            }
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
    /**
     * 处理待付款提前付生成订单
     */
    private void awaitPay(BusinessOrder businessOrder, String payState) {
        try {
            House house = houseMapper.selectByPrimaryKey(businessOrder.getHouseId());
            List<BudgetMaterial> budgetMaterialList = forMasterAPI.caiLiao(house.getCityId(), businessOrder.getTaskId());
            if (budgetMaterialList.size() > 0) {
                HouseExpend houseExpend = houseExpendMapper.getByHouseId(businessOrder.getHouseId());
                houseExpend.setMaterialMoney(houseExpend.getMaterialMoney() + businessOrder.getTotalPrice().doubleValue());//材料钱
                houseExpendMapper.updateByPrimaryKeySelective(houseExpend);

                HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(businessOrder.getTaskId());
                WorkerType wt = workerTypeMapper.selectByPrimaryKey(houseFlow.getWorkerTypeId());
                Order order = new Order();
                order.setHouseId(house.getId());
                order.setBusinessOrderNumber(businessOrder.getNumber());//业务订单
                order.setTotalAmount(businessOrder.getTotalPrice());// 订单总额(材料总钱)
                order.setWorkerTypeName(wt.getName() + "先付材料订单");
                order.setWorkerTypeId(wt.getId());
                order.setType(2);//材料
                order.setPayment(payState);// 支付方式
                orderMapper.insert(order);

                this.addWarehouse(budgetMaterialList, businessOrder.getHouseId(), order.getId(), 2);
            }
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
    }

    /**
     * 未购买支付成功
     */
    private void awaitPayPurchaseOrder(BusinessOrder businessOrder, String payState) {
        try {
            House house = houseMapper.selectByPrimaryKey(businessOrder.getHouseId());
            List<BudgetMaterial> budgetMaterialList = purchaseOrderService.payPurchaseOrder(businessOrder.getTaskId());
            if (budgetMaterialList.size() > 0) {
                HouseExpend houseExpend = houseExpendMapper.getByHouseId(businessOrder.getHouseId());
                houseExpend.setMaterialMoney(houseExpend.getMaterialMoney() + businessOrder.getTotalPrice().doubleValue());//材料钱
                houseExpendMapper.updateByPrimaryKeySelective(houseExpend);
                Order order = new Order();
                order.setHouseId(house.getId());
                order.setBusinessOrderNumber(businessOrder.getNumber());//业务订单
                order.setTotalAmount(businessOrder.getTotalPrice());// 订单总额(材料总钱)
                order.setWorkerTypeName("材料订单");
                order.setWorkerTypeId("");
                order.setType(2);//材料
                order.setPayment(payState);// 支付方式
                orderMapper.insert(order);
                this.addWarehouse(budgetMaterialList, businessOrder.getHouseId(), order.getId(), 2);
            }
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
    }

    /**
     * 处理精算人工生成人工订单
     */
    private boolean renGong(String businessOrderNumber, HouseWorkerOrder hwo, String payState, String houseFlowId) {
        try {
            //处理人工
            House house = houseMapper.selectByPrimaryKey(hwo.getHouseId());
            List<BudgetWorker> budgetWorkerList = forMasterAPI.renGong(house.getCityId(), houseFlowId);
            if (budgetWorkerList.size() > 0) {
                WorkerType wt = workerTypeMapper.selectByPrimaryKey(hwo.getWorkerTypeId());
                Order order = new Order();
                order.setHouseId(house.getId());
                order.setBusinessOrderNumber(businessOrderNumber);//业务订单
                order.setTotalAmount(hwo.getWorkPrice());// 订单总额(工钱)
                order.setWorkerTypeName(wt.getName() + "订单");
                order.setWorkerTypeId(hwo.getWorkerTypeId());
                order.setType(1);//人工
                order.setPayment(payState);// 支付方式
                orderMapper.insert(order);

                for (BudgetWorker budgetWorker : budgetWorkerList) {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrderId(order.getId());
                    orderItem.setHouseId(hwo.getHouseId());
                    orderItem.setPrice(budgetWorker.getPrice());//销售价
                    orderItem.setShopCount(budgetWorker.getShopCount().doubleValue());//购买总数
                    orderItem.setUnitName(budgetWorker.getUnitName());//单位
                    orderItem.setTotalPrice(budgetWorker.getTotalPrice());//总价
                    orderItem.setWorkerGoodsName(budgetWorker.getName());
                    orderItem.setWorkerGoodsSn(budgetWorker.getWorkerGoodsSn());
                    orderItem.setWorkerGoodsId(budgetWorker.getWorkerGoodsId());
                    orderItem.setImage(budgetWorker.getImage());
                    orderItem.setCityId(house.getCityId());
                    orderItemMapper.insert(orderItem);
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
    }

    /**
     * 处理精算材料生成订单
     */
    private void caiLiao(String businessOrderNumber, HouseWorkerOrder hwo, String payState, String houseFlowId) {
        try {
            //处理材料
            House house = houseMapper.selectByPrimaryKey(hwo.getHouseId());
            List<BudgetMaterial> budgetMaterialList = forMasterAPI.caiLiao(house.getCityId(), houseFlowId);
            if (budgetMaterialList.size() > 0) {
                WorkerType wt = workerTypeMapper.selectByPrimaryKey(hwo.getWorkerTypeId());
                Order order = new Order();
                order.setHouseId(hwo.getHouseId());
                order.setBusinessOrderNumber(businessOrderNumber);//业务订单
                order.setTotalAmount(hwo.getMaterialPrice());// 订单总额(材料总钱)
                order.setWorkerTypeName(wt.getName() + "订单");
                order.setWorkerTypeId(hwo.getWorkerTypeId());
                order.setType(2);//材料
                order.setPayment(payState);//支付方式
                orderMapper.insert(order);

                this.addWarehouse(budgetMaterialList, hwo.getHouseId(), order.getId(), 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
    }

    public void budgetCorrect(String businessOrderNumber, String payState, String houseFlowId) {
        HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(houseFlowId);
        HouseWorkerOrder hwo = houseWorkerOrderMapper.getByHouseIdAndWorkerTypeId(houseFlow.getHouseId(), houseFlow.getWorkerTypeId());
        /*处理人工和取消的材料改到自购精算*/
        if (this.renGong(businessOrderNumber, hwo, payState, houseFlowId)) {
            /*处理材料*/
            Double caiPrice = forMasterAPI.getBudgetCaiPrice(hwo.getHouseId(), houseFlow.getWorkerTypeId(), houseFlow.getCityId());//精算材料钱
            Double serPrice = forMasterAPI.getBudgetSerPrice(hwo.getHouseId(), houseFlow.getWorkerTypeId(), houseFlow.getCityId());//精算包工包料钱
            if (caiPrice > 0 || serPrice > 0) {
                this.caiLiao(businessOrderNumber, hwo, payState, houseFlowId);
            }
        }
    }

    /**
     * 生成仓库
     * type 1 工序抢单任务进来的
     * 2 未购买待付款进来的
     */
    private void addWarehouse(List<BudgetMaterial> budgetMaterialList, String houseId, String orderId, int type) {
        try {
            WarehouseDetail warehouseDetail = new WarehouseDetail();
            warehouseDetail.setHouseId(houseId);
            warehouseDetail.setRecordType(0);//支付精算
            warehouseDetail.setRelationId(orderId);
            warehouseDetailMapper.insert(warehouseDetail);
            House house = houseMapper.selectByPrimaryKey(houseId);
            for (BudgetMaterial budgetMaterial : budgetMaterialList) {
                OrderItem orderItem = new OrderItem();
                Product product=forMasterAPI.getProduct(house.getCityId(),budgetMaterial.getProductId());
                orderItem.setOrderId(orderId);
                orderItem.setHouseId(houseId);
                orderItem.setProductId(budgetMaterial.getProductId());//货品id
                orderItem.setProductSn(product.getProductSn());//货品编号
                orderItem.setProductName(product.getName());//货品名称
                orderItem.setProductNickName(budgetMaterial.getProductNickName());//货品昵称
                orderItem.setPrice(budgetMaterial.getPrice());//销售价
                orderItem.setCost(budgetMaterial.getCost());//成本价
                orderItem.setShopCount(budgetMaterial.getConvertCount());//购买总数
                orderItem.setUnitName(budgetMaterial.getUnitName());//单位
                orderItem.setTotalPrice(budgetMaterial.getTotalPrice());//总价
                orderItem.setProductType(budgetMaterial.getProductType());//0：材料；1：包工包料
                orderItem.setCategoryId(budgetMaterial.getCategoryId());
                orderItem.setImage(product.getImage());
                orderItem.setCityId(house.getCityId());
                orderItemMapper.insert(orderItem);

                Example example = new Example(Warehouse.class);
                example.createCriteria().andEqualTo(Warehouse.HOUSE_ID, houseId).andEqualTo(Warehouse.PRODUCT_ID, budgetMaterial.getProductId());
                int sum = warehouseMapper.selectCountByExample(example);
                if (sum > 0) {//累计数量
                    Warehouse warehouse = warehouseMapper.getByProductId(budgetMaterial.getProductId(), houseId);
                    warehouse.setShopCount(warehouse.getShopCount() + budgetMaterial.getConvertCount());//数量
                    if (type == 1) {//抢
                        warehouse.setRobCount(warehouse.getRobCount() + budgetMaterial.getConvertCount());
                    } else if (type == 2) {//未 待
                        warehouse.setStayCount(warehouse.getStayCount() + budgetMaterial.getConvertCount());
                    }
                    warehouse.setPrice(budgetMaterial.getPrice());
                    warehouse.setCost(budgetMaterial.getCost());
                    warehouse.setImage(product.getImage());
                    warehouse.setProductName(product.getName());
                    warehouse.setPayTime(warehouse.getPayTime() + 1);//买次数
                    warehouseMapper.updateByPrimaryKeySelective(warehouse);
                } else {//增加一条
                    Warehouse warehouse = new Warehouse();
                    warehouse.setHouseId(houseId);
                    warehouse.setBudgetCount(budgetMaterial.getConvertCount());
                    warehouse.setShopCount(budgetMaterial.getConvertCount());
                    if (type == 1) {
                        warehouse.setRobCount(budgetMaterial.getConvertCount());//抢单任务进来总数
                        warehouse.setStayCount(0.0);
                    } else {
                        warehouse.setRobCount(0.0);
                        warehouse.setStayCount(budgetMaterial.getConvertCount());
                    }
                    warehouse.setRepairCount(0.0);
                    warehouse.setAskCount(0.0);//已要数量
                    warehouse.setBackCount(0.0);//退总数
                    warehouse.setReceive(0.0);
                    warehouse.setProductId(budgetMaterial.getProductId());
                    warehouse.setProductSn(budgetMaterial.getProductSn());
                    warehouse.setProductName(product.getName());
                    warehouse.setPrice(budgetMaterial.getPrice());
                    warehouse.setCost(budgetMaterial.getCost());
                    warehouse.setUnitName(budgetMaterial.getUnitName());
                    warehouse.setProductType(budgetMaterial.getProductType());
                    warehouse.setCategoryId(product.getCategoryId());
                    warehouse.setImage(product.getImage());
                    warehouse.setPayTime(1);//买次数
                    warehouse.setAskTime(0);
                    warehouse.setRepTime(0);
                    warehouse.setBackTime(0);
                    warehouse.setCityId(house.getCityId());
                    warehouseMapper.insert(warehouse);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
    }

    /**
     * 拉工人进群
     */
    public void addGroupMember(HttpServletRequest request, String houseId, String memberid) {
        try {
            PageDTO pageDTO = new PageDTO();
            pageDTO.setPageNum(1);
            pageDTO.setPageSize(1);
            Group group = new Group();
            group.setHouseId(houseId);
            //获取房子群组
            ServerResponse groups = groupInfoService.getGroups(request, pageDTO, group);
            if (groups.isSuccess()) {
                PageInfo pageInfo = (PageInfo) groups.getResultObj();
                List<GroupDTO> listdto = pageInfo.getList();
                if (listdto != null && listdto.size() > 0) {
                    groupInfoService.editManageGroup(Integer.parseInt(listdto.get(0).getGroupId()), memberid, "");
                }
            }
        } catch (Exception e) {
            System.out.println("建群失败，异常：" + e.getMessage());
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
            if (type == 9) {
                Insurance insurance = insuranceMapper.selectByPrimaryKey(houseDistributionId);
                if (insurance == null) {
                    return ServerResponse.createByErrorMessage("保险记录不存在");
                }

                Example example = new Example(BusinessOrder.class);
                example.createCriteria().andEqualTo(BusinessOrder.TASK_ID, houseDistributionId).andEqualTo(BusinessOrder.STATE, 1).andEqualTo(BusinessOrder.TYPE, 9);
                List<BusinessOrder> businessOrderList = businessOrderMapper.selectByExample(example);
                BusinessOrder businessOrder;
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
                } else {
                    businessOrder = businessOrderList.get(0);
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
                Example example = new Example(BusinessOrder.class);
                example.createCriteria().andEqualTo(BusinessOrder.TASK_ID, houseDistributionId).andEqualTo(BusinessOrder.STATE, 1);
                List<BusinessOrder> businessOrderList = businessOrderMapper.selectByExample(example);
                BusinessOrder businessOrder;
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
                } else {
                    businessOrder = businessOrderList.get(0);
                }
                paymentDTO.setTotalPrice(new BigDecimal(houseDistribution.getPrice()));
                paymentDTO.setBusinessOrderNumber(businessOrder.getNumber());
                paymentDTO.setPayPrice(new BigDecimal(houseDistribution.getPrice()));//实付
                paymentDTO.setInfo("温馨提示: 您将支付" + houseDistribution.getPrice() + "元验房定金，实际费用将根据表格为准，线下补齐差额即可！");
                ActuaryDTO actuaryDTO = new ActuaryDTO();
                actuaryDTO.setImage(imageAddress + "icon/rmb.png");
                actuaryDTO.setKind("验房定金");
                actuaryDTO.setName("当家装修验房定金");
                actuaryDTO.setPrice("¥" + String.format("%.2f", houseDistribution.getPrice()));
                actuaryDTO.setType(6);
                actuaryDTOList.add(actuaryDTO);
            } else if (type == 6) {
                ProductChangeOrder productChangeOrder = productChangeOrderMapper.selectByPrimaryKey(houseDistributionId);
                if (productChangeOrder == null) {
                    return ServerResponse.createByErrorMessage("订单记录不存在");
                }
                House house = houseMapper.selectByPrimaryKey(productChangeOrder.getHouseId());
                Example example = new Example(BusinessOrder.class);
                example.createCriteria().andEqualTo(BusinessOrder.TASK_ID, houseDistributionId).andEqualTo(BusinessOrder.STATE, 1);
                List<BusinessOrder> businessOrderList = businessOrderMapper.selectByExample(example);
                BusinessOrder businessOrder;
                if (businessOrderList.size() == 0) {
                    businessOrder = new BusinessOrder();
                    businessOrder.setMemberId(house.getMemberId()); //公众号唯一标识
                    businessOrder.setHouseId(productChangeOrder.getHouseId());
                    businessOrder.setNumber(System.currentTimeMillis() + "-" + (int) (Math.random() * 9000 + 1000));
                    businessOrder.setState(1);//刚生成
                    businessOrder.setTotalPrice(productChangeOrder.getDifferencePrice());
                    businessOrder.setDiscountsPrice(new BigDecimal(0));
                    businessOrder.setPayPrice(productChangeOrder.getDifferencePrice());
                    businessOrder.setType(6);//记录支付类型任务类型
                    businessOrder.setTaskId(houseDistributionId);//保存任务ID
                    businessOrderMapper.insert(businessOrder);
                } else {
                    businessOrder = businessOrderList.get(0);
                }
                paymentDTO.setTotalPrice(productChangeOrder.getDifferencePrice());
                paymentDTO.setBusinessOrderNumber(businessOrder.getNumber());
                paymentDTO.setPayPrice(productChangeOrder.getDifferencePrice());//实付
                ActuaryDTO actuaryDTO = new ActuaryDTO();
                actuaryDTO.setImage(imageAddress + "icon/bucailiao.png");
                actuaryDTO.setKind("更换结算");
                actuaryDTO.setName("当家装修更换结算");
                actuaryDTO.setPrice("¥" + String.format("%.2f", productChangeOrder.getDifferencePrice().doubleValue()));
                actuaryDTO.setType(6);
                actuaryDTOList.add(actuaryDTO);
            } else if (type == 8) {//未购买过来的
                Map<String, Object> datas = purchaseOrderService.getPurchaseOrder(houseDistributionId);
                if (datas == null) {
                    return ServerResponse.createByErrorMessage("订单记录不存在");
                }
                PurchaseOrder purchaseOrder = (PurchaseOrder) datas.get("purchaseOrder");
                List<FlowActuaryDTO> flowActuaryDTOList = (List<FlowActuaryDTO>) datas.get("list");
                House house = houseMapper.selectByPrimaryKey(purchaseOrder.getHouseId());
                Example example = new Example(BusinessOrder.class);
                example.createCriteria().andEqualTo(BusinessOrder.TASK_ID, houseDistributionId)
                        .andEqualTo(BusinessOrder.STATE, 1);
                List<BusinessOrder> businessOrderList = businessOrderMapper.selectByExample(example);
                BusinessOrder businessOrder;
                if (businessOrderList.size() == 0) {
                    businessOrder = new BusinessOrder();
                    businessOrder.setMemberId(house.getMemberId()); //公众号唯一标识
                    businessOrder.setHouseId(purchaseOrder.getHouseId());
                    businessOrder.setNumber(System.currentTimeMillis() + "-" + (int) (Math.random() * 9000 + 1000));
                    businessOrder.setState(1);//刚生成
                    businessOrder.setTotalPrice(new BigDecimal(purchaseOrder.getPrice()));
                    businessOrder.setDiscountsPrice(new BigDecimal(0));
                    businessOrder.setPayPrice(new BigDecimal(purchaseOrder.getPrice()));
                    businessOrder.setType(8);//记录支付类型任务类型
                    businessOrder.setTaskId(houseDistributionId);//保存任务ID
                    businessOrderMapper.insert(businessOrder);
                } else {
                    businessOrder = businessOrderList.get(0);
                }
                paymentDTO.setTotalPrice(new BigDecimal(purchaseOrder.getPrice()));
                paymentDTO.setBusinessOrderNumber(businessOrder.getNumber());
                paymentDTO.setPayPrice(new BigDecimal(purchaseOrder.getPrice()));//实付
                paymentDTO.setDatas(flowActuaryDTOList);
            } else if (type == 2) {
                MendOrder mendOrder = mendOrderMapper.selectByPrimaryKey(houseDistributionId);
                if (mendOrder == null) {
                    return ServerResponse.createByErrorMessage("订单记录不存在");
                }
                House house = houseMapper.selectByPrimaryKey(mendOrder.getHouseId());
                WorkerType workerType = workerTypeMapper.selectByPrimaryKey(mendOrder.getWorkerTypeId());
                Example example = new Example(BusinessOrder.class);
                example.createCriteria().andEqualTo(BusinessOrder.TASK_ID, houseDistributionId).andEqualTo(BusinessOrder.STATE, 1);
                List<BusinessOrder> businessOrderList = businessOrderMapper.selectByExample(example);
                BusinessOrder businessOrder;
                if (businessOrderList.size() == 0) {
                    businessOrder = new BusinessOrder();
                    businessOrder.setMemberId(house.getMemberId()); //公众号唯一标识
                    businessOrder.setHouseId(mendOrder.getHouseId());
                    businessOrder.setNumber(System.currentTimeMillis() + "-" + (int) (Math.random() * 9000 + 1000));
                    businessOrder.setState(1);//刚生成
                    businessOrder.setTotalPrice(new BigDecimal(mendOrder.getTotalAmount()));
                    businessOrder.setDiscountsPrice(new BigDecimal(0));
                    businessOrder.setPayPrice(new BigDecimal(mendOrder.getTotalAmount()));
                    businessOrder.setType(2);//记录支付类型任务类型
                    businessOrder.setTaskId(houseDistributionId);//保存任务ID
                    businessOrderMapper.insert(businessOrder);
                } else {
                    businessOrder = businessOrderList.get(0);
                }
                paymentDTO.setTotalPrice(new BigDecimal(mendOrder.getTotalAmount()));
                paymentDTO.setBusinessOrderNumber(businessOrder.getNumber());
                paymentDTO.setPayPrice(new BigDecimal(mendOrder.getTotalAmount()));//实付
                ActuaryDTO actuaryDTO = new ActuaryDTO();
                if (mendOrder.getType() == 1) {
                    actuaryDTO.setImage(imageAddress + "icon/burengong.png");
                    actuaryDTO.setKind(workerType.getName());
                    actuaryDTO.setName("补人工花费");
                    actuaryDTO.setPrice("¥" + String.format("%.2f", mendOrder.getTotalAmount()));
                    actuaryDTO.setType(4);

                } else if (mendOrder.getType() == 0) {
                    actuaryDTO.setImage(imageAddress + "icon/bucailiao.png");
                    actuaryDTO.setKind(workerType.getName());
                    actuaryDTO.setPrice("¥" + String.format("%.2f", mendOrder.getTotalAmount()));
                    actuaryDTO.setName("补材料花费");
                    if (workerType.getType() == 3) {
                        actuaryDTO.setName("补包工包料花费");
                    }
                    actuaryDTO.setType(5);
                }
                actuaryDTOList.add(actuaryDTO);
            } else {
                Example example = new Example(BusinessOrder.class);
                example.createCriteria().andEqualTo(BusinessOrder.TASK_ID, houseDistributionId).andEqualTo(BusinessOrder.STATE, 1);
                List<BusinessOrder> businessOrderList = businessOrderMapper.selectByExample(example);
                if (businessOrderList == null || businessOrderList.size() <= 0) {
                    return ServerResponse.createByErrorMessage("支付订单不存在");
                }
                BusinessOrder businessOrder = businessOrderList.get(0);
                paymentDTO.setDiscountsPrice(businessOrder.getDiscountsPrice());
                paymentDTO.setTotalPrice(businessOrder.getTotalPrice());
                paymentDTO.setPayPrice(businessOrder.getPayPrice());//实付

                ActuaryDTO actuaryDTO = new ActuaryDTO();
                actuaryDTO.setImage(imageAddress + "icon/rmb.png");
                actuaryDTO.setKind("订单付款");
                actuaryDTO.setName("订单金额");
                actuaryDTO.setPrice("¥" + String.format("%.2f", businessOrder.getPayPrice().doubleValue()));
                actuaryDTO.setType(businessOrder.getType());
                actuaryDTOList.add(actuaryDTO);
            }
            paymentDTO.setActuaryDTOList(actuaryDTOList);
            return ServerResponse.createBySuccess("查询成功", paymentDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 支付页面
     * type   1工序支付任务,2补货补人工,3审核任务,4待付款进来只付材料 5
     */
    public ServerResponse getPaymentOrder(String userToken, String houseId, String taskId, int type) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            String webAddress = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class);
            if (type != 4) {
                BusinessOrder busOrder = businessOrderMapper.byTaskId(taskId, type);
                if (busOrder != null) {
                    if (busOrder.getState() == 3) {
                        return ServerResponse.createBySuccessMessage("该任务已支付");
                    }
                }
            }
            House house = houseMapper.selectByPrimaryKey(houseId);
            Example example = new Example(BusinessOrder.class);
            example.createCriteria().andEqualTo(BusinessOrder.HOUSE_ID, houseId).andEqualTo(BusinessOrder.STATE, 1);
            List<BusinessOrder> businessOrderList = businessOrderMapper.selectByExample(example);
            BusinessOrder businessOrder;
            if (businessOrderList.size() == 0) {
                businessOrder = new BusinessOrder();
                businessOrder.setMemberId(house.getMemberId());
                businessOrder.setHouseId(houseId);
                businessOrder.setNumber(System.currentTimeMillis() + "-" + (int) (Math.random() * 9000 + 1000));
                businessOrder.setState(1);//刚生成
                businessOrder.setTotalPrice(new BigDecimal(0.0));
                businessOrder.setDiscountsPrice(new BigDecimal(0));
                businessOrder.setPayPrice(new BigDecimal(0.0));
                businessOrder.setType(type);
                businessOrderMapper.insert(businessOrder);
            } else {
                businessOrder = businessOrderList.get(0);
            }
            businessOrder.setType(type);//记录支付类型任务类型
            businessOrder.setTaskId(taskId);//保存任务ID

            PaymentDTO paymentDTO = new PaymentDTO();
            List<ActuaryDTO> actuaryDTOList = new ArrayList<>();//商品
            BigDecimal paymentPrice = new BigDecimal(0);//总共钱

            if (type == 1) {//支付工序
                HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(taskId);
                if (houseFlow.getWorkType() != 3) {
                    return ServerResponse.createByErrorMessage("该工序订单异常");
                }
                HouseWorker houseWorker = houseWorkerMapper.getByWorkerTypeId(houseId, houseFlow.getWorkerTypeId(), 1);
                Member worker = memberMapper.selectByPrimaryKey(houseWorker.getWorkerId()); //查工匠
                WorkerType workerType = workerTypeMapper.selectByPrimaryKey(worker.getWorkerTypeId());
                WorkerDTO workerDTO = new WorkerDTO();
                workerDTO.setHouseWorkerId(houseWorker.getId());//换人参数
                workerDTO.setHead(imageAddress + worker.getHead());
                workerDTO.setWorkerTypeName(workerType.getName());
                workerDTO.setName(worker.getName());
                workerDTO.setWorkerId(houseWorker.getWorkerId());
                workerDTO.setMobile(worker.getMobile());
                workerDTO.setChange(0);//不能换人
                paymentDTO.setWorkerDTO(workerDTO);//工匠信息
                /*
                 * 生成工匠订单
                 */
                HouseWorkerOrder hwo = houseWorkerOrderMapper.getByHouseIdAndWorkerTypeId(houseId, houseFlow.getWorkerTypeId());
                if (hwo == null) {
                    hwo = new HouseWorkerOrder(true);
                    hwo.setHouseId(houseFlow.getHouseId());
                    hwo.setWorkerId(worker.getId());
                    hwo.setWorkerTypeId(worker.getWorkerTypeId());
                    hwo.setWorkerType(worker.getWorkerType());
                    hwo.setBusinessOrderNumber(businessOrder.getNumber());//业务订单号
                    houseWorkerOrderMapper.insert(hwo);
                } else {
                    hwo.setHouseId(houseFlow.getHouseId());
                    hwo.setWorkerId(worker.getId());
                    hwo.setWorkerTypeId(worker.getWorkerTypeId());
                    hwo.setWorkerType(worker.getWorkerType());
                    hwo.setBusinessOrderNumber(businessOrder.getNumber());//业务订单号
                    houseWorkerOrderMapper.updateByPrimaryKey(hwo);
                }

                if (houseFlow.getWorkerType() == 1) {//设计师
                    HouseStyleType houseStyleType = houseStyleTypeMapper.selectByPrimaryKey(house.getStyleId());
                    BigDecimal workPrice = house.getSquare().multiply(houseStyleType.getPrice());//设计工钱
                    hwo.setWorkPrice(workPrice);
                    hwo.setTotalPrice(workPrice);
                    houseWorkerOrderMapper.updateByPrimaryKey(hwo);
                    paymentPrice = paymentPrice.add(workPrice);
                    // protocolTpye==1 精算商品定义
                    // protocolTpye=2 精算修改费用明细
                    // protocolTpye=3 设计改图费用
                    // protocolTpye=4 9.9设计商品定义
                    // protocolTpye=5 18.8设计商品定义
                    // protocolTpye=6 28.8设计商品定义
                    if (house.getStyle() != null) {
                        switch (house.getStyle()) {
                            case "极简需求":
                                paymentDTO.setAgreementName("《设计服务须知》");
                                paymentDTO.setAgreementUrl(webAddress + "paymentAgreement?title=设计服务须知&protocolTpye=4");
                                break;
                            case "简单装修":
                                paymentDTO.setAgreementName("《设计服务须知》");
                                paymentDTO.setAgreementUrl(webAddress + "paymentAgreement?title=设计服务须知&protocolTpye=5");
                                break;
                            case "个性化":
                                paymentDTO.setAgreementName("《设计服务须知》");
                                paymentDTO.setAgreementUrl(webAddress + "paymentAgreement?title=设计服务须知&protocolTpye=6");
                                break;
                        }
                    }
                } else if (houseFlow.getWorkerType() == 2) {
                    paymentDTO.setAgreementName("《精算服务须知》");
                    paymentDTO.setAgreementUrl(webAddress + "paymentAgreement?title=精算服务须知&protocolTpye=1");
                    /*记录精算费*/
                    BigDecimal workPrice = houseFlow.getWorkPrice();
                    hwo.setWorkPrice(workPrice);
                    hwo.setTotalPrice(workPrice);
                    houseWorkerOrderMapper.updateByPrimaryKey(hwo);
                    paymentPrice = paymentPrice.add(workPrice);
                } else {//其它工序
                    Double workerPrice = forMasterAPI.getBudgetWorkerPrice(houseId, houseFlow.getWorkerTypeId(), house.getCityId());//精算工钱
                    Double caiPrice = forMasterAPI.getBudgetCaiPrice(houseId, houseFlow.getWorkerTypeId(), house.getCityId());//精算材料钱
                    Double serPrice = forMasterAPI.getBudgetSerPrice(houseId, houseFlow.getWorkerTypeId(), house.getCityId());//精算包工包料钱
                    hwo.setWorkPrice(new BigDecimal(workerPrice));//工钱
                    hwo.setMaterialPrice(new BigDecimal(caiPrice + serPrice));//材料钱
                    hwo.setTotalPrice(hwo.getWorkPrice().add(hwo.getMaterialPrice()));//工钱+拆料
                    houseWorkerOrderMapper.updateByPrimaryKey(hwo);
                    paymentPrice = paymentPrice.add(new BigDecimal(workerPrice));
                    paymentPrice = paymentPrice.add(new BigDecimal(caiPrice));
                    paymentPrice = paymentPrice.add(new BigDecimal(serPrice));
                    if (workerPrice > 0) {
                        ActuaryDTO actuaryDTO = new ActuaryDTO();
                        actuaryDTO.setImage(imageAddress + "icon/Arengong.png");
                        actuaryDTO.setKind("人工");
                        actuaryDTO.setName(workerType.getName() + "阶段人工花费");
                        actuaryDTO.setPrice("¥" + String.format("%.2f", workerPrice));
                        actuaryDTO.setType(1);
                        actuaryDTOList.add(actuaryDTO);
                    }
                    if (caiPrice > 0) {
                        ActuaryDTO actuaryDTO = new ActuaryDTO();
                        actuaryDTO.setImage(imageAddress + "icon/Acailiao.png");
                        actuaryDTO.setKind("材料");
                        actuaryDTO.setName(workerType.getName() + "阶段材料花费");
                        actuaryDTO.setPrice("¥" + String.format("%.2f", caiPrice));
                        actuaryDTO.setType(2);
                        actuaryDTOList.add(actuaryDTO);
                    }
                    if (serPrice > 0) {
                        ActuaryDTO actuaryDTO = new ActuaryDTO();
                        actuaryDTO.setImage(imageAddress + "icon/Afuwu.png");
                        actuaryDTO.setKind("包工包料");
                        actuaryDTO.setName(workerType.getName() + "阶段包工包料花费");
                        actuaryDTO.setPrice("¥" + String.format("%.2f", serPrice));
                        actuaryDTO.setType(3);
                        actuaryDTOList.add(actuaryDTO);
                    }
                    paymentDTO.setActuaryDTOList(actuaryDTOList);

                    //查出有没有生成保险订单
                    example = new Example(WorkerTypeSafeOrder.class);
                    example.createCriteria().andEqualTo(WorkerTypeSafeOrder.HOUSE_ID, houseId).andEqualTo(WorkerTypeSafeOrder.WORKER_TYPE_ID, houseFlow.getWorkerTypeId()).andEqualTo(WorkerTypeSafeOrder.DATA_STATUS, 0);
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
                        List<SafeTypeDTO> safeTypeDTOList = new ArrayList<SafeTypeDTO>();
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
            } else if (type == 2) {
                //补人工补材料
                MendOrder mendOrder = mendOrderMapper.selectByPrimaryKey(taskId);
                mendOrder.setBusinessOrderNumber(businessOrder.getNumber());
                mendOrderMapper.updateByPrimaryKeySelective(mendOrder);
                WorkerType workerType = workerTypeMapper.selectByPrimaryKey(mendOrder.getWorkerTypeId());
                ActuaryDTO actuaryDTO = new ActuaryDTO();
                if (mendOrder.getType() == 1) {
                    actuaryDTO.setImage(imageAddress + "icon/burengong.png");
                    actuaryDTO.setKind(workerType.getName());
                    actuaryDTO.setName("补人工花费");
                    actuaryDTO.setPrice("¥" + String.format("%.2f", mendOrder.getTotalAmount()));
                    actuaryDTO.setType(4);

                } else if (mendOrder.getType() == 0) {
                    actuaryDTO.setImage(imageAddress + "icon/bucailiao.png");
                    actuaryDTO.setKind(workerType.getName());
                    actuaryDTO.setPrice("¥" + String.format("%.2f", mendOrder.getTotalAmount()));
                    actuaryDTO.setName("补材料花费");
                    if (workerType.getType() == 3) {
                        actuaryDTO.setName("补包工包料花费");
                    }
                    actuaryDTO.setType(5);
                }

                actuaryDTOList.add(actuaryDTO);
                paymentDTO.setActuaryDTOList(actuaryDTOList);
                paymentPrice = paymentPrice.add(new BigDecimal(mendOrder.getTotalAmount()));
            } else if (type == 4) {
                //待付款只付材料费
                HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(taskId);
                WorkerType workerType = workerTypeMapper.selectByPrimaryKey(houseFlow.getWorkerTypeId());
                Double caiPrice = forMasterAPI.getBudgetCaiPrice(houseId, houseFlow.getWorkerTypeId(), house.getCityId());//精算材料钱
                Double serPrice = forMasterAPI.getBudgetSerPrice(houseId, houseFlow.getWorkerTypeId(), house.getCityId());//精算包工包料钱
                paymentPrice = paymentPrice.add(new BigDecimal(caiPrice));
                paymentPrice = paymentPrice.add(new BigDecimal(serPrice));
                if (caiPrice > 0) {
                    ActuaryDTO actuaryDTO = new ActuaryDTO();
                    actuaryDTO.setImage(imageAddress + "icon/Acailiao.png");
                    actuaryDTO.setKind("材料");
                    actuaryDTO.setName(workerType.getName() + "阶段材料花费");
                    actuaryDTO.setPrice("¥" + String.format("%.2f", caiPrice));
                    actuaryDTO.setType(2);
                    actuaryDTOList.add(actuaryDTO);
                }
                if (serPrice > 0) {
                    ActuaryDTO actuaryDTO = new ActuaryDTO();
                    actuaryDTO.setImage(imageAddress + "icon/Afuwu.png");
                    actuaryDTO.setKind("包工包料");
                    actuaryDTO.setName(workerType.getName() + "阶段包工包料花费");
                    actuaryDTO.setPrice("¥" + String.format("%.2f", serPrice));
                    actuaryDTO.setType(3);
                    actuaryDTOList.add(actuaryDTO);
                }
                paymentDTO.setActuaryDTOList(actuaryDTOList);
            } else {
                return ServerResponse.createByErrorMessage("参数错误");
            }
            //保存总价
            businessOrder.setTotalPrice(paymentPrice);
            BigDecimal payPrice = businessOrder.getTotalPrice().subtract(businessOrder.getDiscountsPrice());
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

    /**
     * 可用优惠券数据
     *
     * @param businessOrderNumber 订单号
     * @return
     */
    public List<ActivityRedPackRecordDTO> discountPage(String businessOrderNumber) {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
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
                List<BudgetWorker> budgetWorkerList = JSONObject.parseArray(retWorker.getResultObj().toString(), BudgetWorker.class);

                for (ActivityRedPackRecordDTO redPacketRecord : redPacketRecordList) {
                    BigDecimal workerTotal = new BigDecimal(0);
                    BigDecimal goodsTotal = new BigDecimal(0);
                    BigDecimal productTotal = new BigDecimal(0);

                    if (budgetWorkerList.size() > 0) {
                        for (BudgetWorker budgetWorker : budgetWorkerList) {
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
     * @param type   1工序支付任务,2补货补人工,3审核任务,4待付款进来只付材料
     */
    public ServerResponse getPaymentPage(String userToken, String houseId, String taskId, int type) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            String webAddress = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class);
            House house = houseMapper.selectByPrimaryKey(houseId);
            PaymentDTO paymentDTO = new PaymentDTO();
            List<ActuaryDTO> actuaryDTOList = new ArrayList<ActuaryDTO>();//商品

            BigDecimal totalPrice = new BigDecimal(0);//总价
            BigDecimal workPrice;//工钱
            Example example;
            if (type == 1) {//支付工序
                HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(taskId);
                if (houseFlow.getWorkType() == 2) {
                    return ServerResponse.createByErrorMessage("等待工匠抢单");
                }
                if (houseFlow.getWorkType() == 4) {
                    return ServerResponse.createByErrorMessage("该订单已支付");
                }
                example = new Example(HouseWorker.class);
                example.createCriteria().andEqualTo(HouseWorker.WORKER_TYPE_ID, houseFlow.getWorkerTypeId())
                        .andEqualTo(HouseWorker.HOUSE_ID, houseId).andEqualTo(HouseWorker.WORK_TYPE, 1);
                List<HouseWorker> houseWorkerList = houseWorkerMapper.selectByExample(example);
                if (houseWorkerList.size() != 1) {
                    return ServerResponse.createByErrorMessage("抢单异常,联系平台部");
                }
                Member worker = memberMapper.selectByPrimaryKey(houseWorkerList.get(0).getWorkerId()); //查工匠
                WorkerType workerType = workerTypeMapper.selectByPrimaryKey(worker.getWorkerTypeId());
                WorkerDTO workerDTO = new WorkerDTO();
                workerDTO.setHouseWorkerId(houseWorkerList.get(0).getId());//换人参数
                workerDTO.setHead(imageAddress + worker.getHead());
                if(workerType.getType()==3){
                    paymentDTO.setWorkerTypeName("开工准备");
                }else {
                    paymentDTO.setWorkerTypeName(workerType.getName());
                }
                workerDTO.setWorkerTypeName(workerType.getName());
                workerDTO.setWorkerId(houseWorkerList.get(0).getWorkerId());
                workerDTO.setName(worker.getName());
                workerDTO.setMobile(worker.getMobile());
                if (houseFlow.getWorkerType() > 2) {//精算之后才能换人
                    workerDTO.setChange(1);
                } else {
                    workerDTO.setChange(0);
                }
                paymentDTO.setWorkerDTO(workerDTO);//工匠信息
                if (houseFlow.getWorkerType() == 1) {//设计师
                    HouseStyleType houseStyleType = houseStyleTypeMapper.selectByPrimaryKey(house.getStyleId());
                    workPrice = house.getSquare().multiply(houseStyleType.getPrice());//设计工钱
                    totalPrice = totalPrice.add(workPrice);
                } else if (houseFlow.getWorkerType() == 2) {//精算
                    workPrice = houseFlow.getWorkPrice();
                    totalPrice = totalPrice.add(workPrice);
                } else {//其它工序
                    Double workerPrice = forMasterAPI.getBudgetWorkerPrice(houseId, houseFlow.getWorkerTypeId(), house.getCityId());//精算工钱
                    Double caiPrice = forMasterAPI.getBudgetCaiPrice(houseId, houseFlow.getWorkerTypeId(), house.getCityId());//精算材料钱
                    Double serPrice = forMasterAPI.getBudgetSerPrice(houseId, houseFlow.getWorkerTypeId(), house.getCityId());//精算服务钱
                    totalPrice = totalPrice.add(new BigDecimal(workerPrice));
                    totalPrice = totalPrice.add(new BigDecimal(caiPrice));
                    totalPrice = totalPrice.add(new BigDecimal(serPrice));
                    if (workerPrice > 0) {
                        ActuaryDTO actuaryDTO = new ActuaryDTO();
                        actuaryDTO.setImage(imageAddress + "icon/Arengong.png");
                        actuaryDTO.setKind("人工");
                        actuaryDTO.setName(workerType.getName() + "阶段人工花费");
                        actuaryDTO.setPrice("¥" + String.format("%.2f", workerPrice));
                        actuaryDTO.setButton("明细");
                        String url = webAddress + String.format(DjConstants.YZPageAddress.WAITINGPAYDETAIL, userToken, house.getCityId(), "待付款明细")
                                + "&houseId=" + houseId + "&workerTypeId=" + houseFlow.getWorkerTypeId() + "&type=1";
                        actuaryDTO.setUrl(url);
                        actuaryDTO.setType(1);
                        actuaryDTOList.add(actuaryDTO);
                    }
                    if (caiPrice > 0) {
                        ActuaryDTO actuaryDTO = new ActuaryDTO();
                        actuaryDTO.setImage(imageAddress + "icon/Acailiao.png");
                        actuaryDTO.setKind("材料");
                        actuaryDTO.setName(workerType.getName() + "阶段材料花费");
                        actuaryDTO.setPrice("¥" + String.format("%.2f", caiPrice));
                        actuaryDTO.setButton("明细");
                        String url = webAddress + String.format(DjConstants.YZPageAddress.WAITINGPAYDETAIL, userToken, house.getCityId(), "待付款明细")
                                + "&houseId=" + houseId + "&workerTypeId=" + houseFlow.getWorkerTypeId() + "&type=2";
                        actuaryDTO.setUrl(url);
                        actuaryDTO.setType(2);
                        actuaryDTOList.add(actuaryDTO);
                    }
                    if (serPrice > 0) {
                        ActuaryDTO actuaryDTO = new ActuaryDTO();
                        actuaryDTO.setImage(imageAddress + "icon/Afuwu.png");
                        actuaryDTO.setKind("包工包料");
                        actuaryDTO.setName(workerType.getName() + "阶段包工包料花费");
                        actuaryDTO.setPrice("¥" + String.format("%.2f", serPrice));
                        actuaryDTO.setButton("明细");
                        String url = webAddress + String.format(DjConstants.YZPageAddress.WAITINGPAYDETAIL, userToken, house.getCityId(), "待付款明细")
                                + "&houseId=" + houseId + "&workerTypeId=" + houseFlow.getWorkerTypeId() + "&type=" + 3;
                        actuaryDTO.setUrl(url);
                        actuaryDTO.setType(3);
                        actuaryDTOList.add(actuaryDTO);
                    }
                    paymentDTO.setActuaryDTOList(actuaryDTOList);
                    //该工钟所有保险
                    example = new Example(WorkerTypeSafe.class);
                    example.createCriteria().andEqualTo(WorkerTypeSafe.WORKER_TYPE_ID, houseFlow.getWorkerTypeId());
                    List<WorkerTypeSafe> wtsList = workerTypeSafeMapper.selectByExample(example);

                    WorkerTypeSafeOrder workerTypeSafeOrder = workerTypeSafeOrderMapper.getByNotPay(houseFlow.getWorkerTypeId(), houseFlow.getHouseId());
                    if (workerTypeSafeOrder == null) {//默认生成一条
                        if (wtsList.size() > 0) {
                            workerTypeSafeOrder = new WorkerTypeSafeOrder();
                            workerTypeSafeOrder.setWorkerTypeSafeId(wtsList.get(0).getId()); // 向保险订单中存入保险服务类型的id
                            workerTypeSafeOrder.setHouseId(houseFlow.getHouseId()); // 存入房子id
                            workerTypeSafeOrder.setWorkerTypeId(houseFlow.getWorkerTypeId()); // 工种id
                            workerTypeSafeOrder.setWorkerType(houseFlow.getWorkerType());
                            workerTypeSafeOrder.setPrice(wtsList.get(0).getPrice().multiply(house.getSquare()));
                            workerTypeSafeOrder.setState(0);  //未支付
                            workerTypeSafeOrderMapper.insert(workerTypeSafeOrder);
                        }
                    }

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
                        List<SafeTypeDTO> safeTypeDTOList = new ArrayList<SafeTypeDTO>();
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
                }
            } else if (type == 2) {//补人工补材料
                MendOrder mendOrder = mendOrderMapper.selectByPrimaryKey(taskId);
                WorkerType workerType = workerTypeMapper.selectByPrimaryKey(mendOrder.getWorkerTypeId());
                ActuaryDTO actuaryDTO = new ActuaryDTO();
                if (mendOrder.getType() == 1) {
                    actuaryDTO.setImage(imageAddress + "icon/burengong.png");
                    actuaryDTO.setKind(workerType.getName());
                    actuaryDTO.setName("补人工花费");
                    actuaryDTO.setPrice("¥" + String.format("%.2f", mendOrder.getTotalAmount()));
                    actuaryDTO.setButton("明细");
                    String url = webAddress +
                            String.format(DjConstants.YZPageAddress.WAITINGPAYDETAIL, userToken, house.getCityId(), "待付款明细")
                            + "&houseId=" + houseId + "&workerTypeId=" + mendOrder.getId() + "&type=4";
                    actuaryDTO.setUrl(url);
                    actuaryDTO.setType(4);

                } else if (mendOrder.getType() == 0) {
                    actuaryDTO.setImage(imageAddress + "icon/bucailiao.png");
                    actuaryDTO.setKind(workerType.getName());
                    actuaryDTO.setName("补材料花费");
                    actuaryDTO.setPrice("¥" + String.format("%.2f", mendOrder.getTotalAmount()));
                    actuaryDTO.setButton("明细");
                    String url = webAddress +
                            String.format(DjConstants.YZPageAddress.WAITINGPAYDETAIL, userToken, house.getCityId(), "待付款明细")
                            + "&houseId=" + houseId + "&workerTypeId=" + mendOrder.getId() + "&type=5";

                    if (workerType.getType() == 3) {
                        actuaryDTO.setName("补包工包料花费");
                        actuaryDTO.setButton("明细");
                    }
                    actuaryDTO.setUrl(url);
                    actuaryDTO.setType(5);
                }


                actuaryDTOList.add(actuaryDTO);
                paymentDTO.setActuaryDTOList(actuaryDTOList);
                paymentDTO.setWorkerTypeName(workerType.getName());
                totalPrice = new BigDecimal(mendOrder.getTotalAmount());
            } else if (type == 4) {
                //待付款只付材料费
                HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(taskId);
                WorkerType workerType = workerTypeMapper.selectByPrimaryKey(houseFlow.getWorkerTypeId());
                /*
                 * 在这里取消的材料都改成未付款
                 */
                Double caiPrice = forMasterAPI.getBudgetCaiPrice(houseId, houseFlow.getWorkerTypeId(), house.getCityId());//精算材料钱
                Double serPrice = forMasterAPI.getBudgetSerPrice(houseId, houseFlow.getWorkerTypeId(), house.getCityId());//精算服务钱


                Double notCaiPrice = forMasterAPI.getNotCaiPrice(houseId, houseFlow.getWorkerTypeId(), house.getCityId());//未选择材料钱
                Double notSerPrice = forMasterAPI.getNotSerPrice(houseId, houseFlow.getWorkerTypeId(), house.getCityId());//未选择服务钱

                totalPrice = totalPrice.add(new BigDecimal(caiPrice));
                totalPrice = totalPrice.add(new BigDecimal(serPrice));

                if (caiPrice > 0 || notCaiPrice > 0) {
                    ActuaryDTO actuaryDTO = new ActuaryDTO();
                    actuaryDTO.setImage(imageAddress + "icon/Acailiao.png");
                    actuaryDTO.setKind("支付材料");
                    actuaryDTO.setName(workerType.getName() + "阶段材料花费");
                    actuaryDTO.setPrice("¥" + String.format("%.2f", caiPrice));
                    actuaryDTO.setButton("明细");
                    String url = webAddress + String.format(DjConstants.YZPageAddress.WAITINGPAYDETAIL, userToken, house.getCityId(), "待付款明细")
                            + "&houseId=" + houseId + "&workerTypeId=" + houseFlow.getWorkerTypeId() + "&type=" + 2;
                    actuaryDTO.setUrl(url);
                    actuaryDTO.setType(2);
                    actuaryDTOList.add(actuaryDTO);
                }
                if (serPrice > 0 || notSerPrice > 0) {
                    ActuaryDTO actuaryDTO = new ActuaryDTO();
                    actuaryDTO.setImage(imageAddress + "icon/Afuwu.png");
                    actuaryDTO.setKind("支付包工包料");
                    actuaryDTO.setName(workerType.getName() + "阶段包工包料花费");
                    actuaryDTO.setPrice("¥" + String.format("%.2f", serPrice));
                    actuaryDTO.setButton("明细");
                    String url = webAddress + String.format(DjConstants.YZPageAddress.WAITINGPAYDETAIL, userToken, house.getCityId(), "待付款明细")
                            + "&houseId=" + houseId + "&workerTypeId=" + houseFlow.getWorkerTypeId() + "&type=" + 3;
                    actuaryDTO.setUrl(url);
                    actuaryDTO.setType(3);
                    actuaryDTOList.add(actuaryDTO);
                }
                paymentDTO.setActuaryDTOList(actuaryDTOList);
                paymentDTO.setWorkerTypeName(workerType.getName());
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
