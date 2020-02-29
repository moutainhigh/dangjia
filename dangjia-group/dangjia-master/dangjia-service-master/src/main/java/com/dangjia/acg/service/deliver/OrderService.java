package com.dangjia.acg.service.deliver;

import cn.jiguang.common.utils.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.StorefrontConfigAPI;
import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.enums.AppType;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.MathUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.deliver.*;
import com.dangjia.acg.dto.product.StorefrontProductDTO;
import com.dangjia.acg.mapper.IConfigMapper;
import com.dangjia.acg.mapper.config.IMasterActuarialProductConfigMapper;
import com.dangjia.acg.mapper.config.IMasterActuarialTemplateConfigMapper;
import com.dangjia.acg.mapper.core.*;
import com.dangjia.acg.mapper.delivery.*;
import com.dangjia.acg.mapper.design.IMasterQuantityRoomProductMapper;
import com.dangjia.acg.mapper.house.IHouseDistributionMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.house.IWarehouseDetailMapper;
import com.dangjia.acg.mapper.house.IWarehouseMapper;
import com.dangjia.acg.mapper.member.IMasterMemberAddressMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.pay.IBusinessOrderMapper;
import com.dangjia.acg.mapper.product.IMasterProductTemplateMapper;
import com.dangjia.acg.mapper.product.IMasterStorefrontProductMapper;
import com.dangjia.acg.mapper.repair.IMendMaterialMapper;
import com.dangjia.acg.mapper.repair.IMendOrderMapper;
import com.dangjia.acg.model.Config;
import com.dangjia.acg.modle.actuary.DjActuarialProductConfig;
import com.dangjia.acg.modle.actuary.DjActuarialTemplateConfig;
import com.dangjia.acg.modle.brand.Unit;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.core.HouseWorker;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.deliver.*;
import com.dangjia.acg.modle.house.*;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.member.MemberAddress;
import com.dangjia.acg.modle.pay.BusinessOrder;
import com.dangjia.acg.modle.product.BasicsGoods;
import com.dangjia.acg.modle.product.DjBasicsProductTemplate;
import com.dangjia.acg.modle.repair.MendMateriel;
import com.dangjia.acg.modle.repair.MendOrder;
import com.dangjia.acg.modle.storefront.Storefront;
import com.dangjia.acg.modle.storefront.StorefrontProduct;
import com.dangjia.acg.service.acquisition.MasterCostAcquisitionService;
import com.dangjia.acg.service.config.ConfigMessageService;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.dangjia.acg.service.core.TaskStackService;
import com.dangjia.acg.service.pay.PaymentService;
import com.dangjia.acg.service.product.MasterProductTemplateService;
import com.dangjia.acg.service.repair.MendOrderService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 要货
 * Date: 2018/11/8 0008
 * Time: 11:48
 */
@Service
public class OrderService {
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IOrderSplitMapper orderSplitMapper;
    @Autowired
    private IOrderSplitItemMapper orderSplitItemMapper;
    @Autowired
    private IMendOrderMapper mendOrderMapper;
    @Autowired
    private IMendMaterialMapper mendMaterialMapper;
    @Autowired
    private IWarehouseMapper warehouseMapper;
    @Autowired
    private IWarehouseDetailMapper warehouseDetailMapper;
    @Autowired
    private IOrderMapper orderMapper;
    @Autowired
    private IOrderItemMapper orderItemMapper;
    @Autowired
    private IHouseWorkerMapper houseWorkerMapper;
    @Autowired
    private IHouseMapper houseMapper;
    @Autowired
    private IHouseFlowMapper houseFlowMapper;
    @Autowired
    private IBusinessOrderMapper businessOrderMapper;
    @Autowired
    private IHouseDistributionMapper iHouseDistributionMapper;
    @Autowired
    private CraftsmanConstructionService constructionService;
    @Autowired
    private ConfigMessageService configMessageService;
    @Autowired
    private IWorkerTypeMapper workerTypeMapper;
    @Autowired
    private ISplitDeliverMapper splitDeliverMapper;
    @Autowired
    private IMasterBasicsGoodsMapper masterBasicsGoodsMapper;
    @Autowired
    private MendOrderService mendOrderService;
    @Autowired
    private ICartMapper cartMapper;
    @Autowired
    private IMasterStorefrontProductMapper iMasterStorefrontProductMapper;
    @Autowired
    private StorefrontConfigAPI storefrontConfigAPI;
    @Autowired
    private MasterCostAcquisitionService masterCostAcquisitionService;
    @Autowired
    private IMemberMapper iMemberMapper;
    @Autowired
    private IWarehouseMapper iWarehouseMapper;
    @Autowired
    private IMasterMemberAddressMapper iMasterMemberAddressMapper;
    @Autowired
    private IMasterProductTemplateMapper iMasterProductTemplateMapper;

    @Autowired
    private IMasterUnitMapper iMasterUnitMapper;
    @Autowired
    private MasterProductTemplateService masterProductTemplateService;

    private static Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private IMasterDeliverOrderAddedProductMapper iMasterDeliverOrderAddedProductMapper;
    @Autowired
    private RepairMendOrderService repairMendOrderService;
    @Autowired
    private IConfigMapper iConfigMapper;
    @Autowired
    private TaskStackService taskStackService;
    @Autowired
    private IMasterQuantityRoomProductMapper iMasterQuantityRoomProductMapper;
    @Autowired
    private IMasterActuarialProductConfigMapper iMasterActuarialProductConfigMapper;
    @Autowired
    private IMasterActuarialTemplateConfigMapper iMasterActuarialTemplateConfigMapper;
    @Autowired
    private PaymentService paymentService;
    /**
     * 删除订单
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse delBusinessOrderById(String userToken, String orderId) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = (Member) object;
            Order order = orderMapper.selectByPrimaryKey(orderId);
            if (order == null) {
                return ServerResponse.createByErrorMessage("该订单不存在");
            }
            House house = houseMapper.selectByPrimaryKey(order.getHouseId());
            if (house == null) {
                return ServerResponse.createByErrorMessage("该房产不存在");
            }
            //主订单删除
            Example orderexample = new Example(Order.class);
            orderexample.createCriteria().andEqualTo(Order.ID, orderId).andEqualTo(Order.MEMBER_ID, member.getId());
            Order order1 = new Order();
            order1.setDataStatus(1);
            orderMapper.updateByExampleSelective(order1, orderexample);
            //订单详情删除

            Example orderItemexample = new Example(OrderItem.class);
            orderItemexample.createCriteria().andEqualTo(Order.ID, orderId);
            OrderItem orderItem = new OrderItem();
            orderItem.setDataStatus(1);
            orderItemMapper.updateByExampleSelective(orderItem, orderItemexample);

            List<OrderItem> OrderItemList = orderItemMapper.selectByExample(orderItemexample);
            for (OrderItem OrderItem : OrderItemList) {
                //要货单以及要货单详情表删除 orderSplitMapper  orderSplitItemMapper
                String id = OrderItem.getId();
                Example OrderSplitItemexample = new Example(OrderSplitItem.class);
                OrderSplitItemexample.createCriteria().andEqualTo(OrderSplitItem.ORDER_ITEM_ID, id);
                OrderSplitItem orderSplitItem = new OrderSplitItem();
                orderSplitItem.setDataStatus(1);
                orderSplitItemMapper.updateByExampleSelective(orderSplitItem, OrderSplitItemexample);
                orderSplitItem = orderSplitItemMapper.selectByPrimaryKey(id);
                //发货单删除
                String splitItemId = orderSplitItem.getId();
                Example splitDeliverExample = new Example(SplitDeliver.class);
                splitDeliverExample.createCriteria().andEqualTo(SplitDeliver.NUMBER, splitItemId);
                SplitDeliver splitDeliver = new SplitDeliver();
                splitDeliver.setDataStatus(1);
                splitDeliverMapper.updateByExampleSelective(splitDeliver, splitDeliverExample);
            }
            return ServerResponse.createBySuccess("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("删除订单异常");
        }
    }


    /**
     * 订单详情
     */
    public ServerResponse orderDetail(String orderId) {
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            return ServerResponse.createByErrorMessage("该订单不存在");
        }
        House house = houseMapper.selectByPrimaryKey(order.getHouseId());
        if (house == null) {
            return ServerResponse.createByErrorMessage("该房产不存在");
        }
        String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        OrderItemDTO orderItemDTO = new OrderItemDTO();
        orderItemDTO.setOrderId(order.getId());
        orderItemDTO.setTotalAmount(order.getTotalAmount());
        List<ItemDTO> itemDTOList = new ArrayList<>();
        switch (order.getWorkerTypeId()) {
            case "1": {//设计
                ItemDTO itemDTO = new ItemDTO();
//                itemDTO.setName(house.getStyle());
                itemDTO.setName("当家设计");
                itemDTO.setImage(address + "icon/shejiF.png");
                itemDTO.setPrice("¥" + String.format("%.2f", order.getStylePrice().doubleValue()) + "/㎡");
                itemDTO.setShopCount(house.getSquare().doubleValue());
                itemDTO.setProductType(3);
                itemDTOList.add(itemDTO);
                break;
            }
            case "2": {
                ItemDTO itemDTO = new ItemDTO();
                itemDTO.setName("当家精算");
                itemDTO.setImage(address + "icon/jingsuanF.png");
                itemDTO.setPrice("¥" + String.format("%.2f", order.getBudgetCost().doubleValue()) + "/㎡");
                itemDTO.setShopCount(house.getSquare().doubleValue());
                itemDTO.setProductType(3);
                itemDTOList.add(itemDTO);
                break;
            }
            default:
                List<OrderItem> orderItemList = orderItemMapper.byOrderIdList(orderId);
                for (OrderItem orderItem : orderItemList) {
                    ItemDTO itemDTO = new ItemDTO();
                    itemDTO.setImage(address + orderItem.getImage());
                    itemDTO.setPrice("¥" + String.format("%.2f", orderItem.getPrice()));
                    itemDTO.setShopCount(orderItem.getShopCount());
                    if (order.getType() == 1) {//人工
                        itemDTO.setName(orderItem.getProductName());
                        itemDTO.setProductType(2);//人工
                    } else if (order.getType() == 2) {//材料
                        itemDTO.setName(orderItem.getProductName());
                        itemDTO.setProductType(orderItem.getProductType());
                    }
                    itemDTOList.add(itemDTO);
                }
                break;
        }
        orderItemDTO.setItemDTOList(itemDTOList);
        return ServerResponse.createBySuccess("查询成功", orderItemDTO);
    }


    /**
     * 订单详情
     */
    public ServerResponse orderList(String businessOrderId) {
        BusinessOrder businessOrder = businessOrderMapper.selectByPrimaryKey(businessOrderId);
        if (businessOrder == null) {
            return ServerResponse.createByErrorMessage("该订单不存在");
        }
        BusinessOrderDTO businessOrderDTO = new BusinessOrderDTO();
        if (!CommonUtil.isEmpty(businessOrder.getHouseId())) {
            House house = houseMapper.selectByPrimaryKey(businessOrder.getHouseId());
            if (house != null) {
                businessOrderDTO.setHouseName(house.getHouseName());
                List<OrderDTO> orderDTOList = this.orderDTOList(businessOrder.getNumber(), ""/*house.getStyle()*/);
                businessOrderDTO.setOrderDTOList(orderDTOList);
            }
        }
        if (businessOrder.getType() == 5) {//验房分销
            HouseDistribution houseDistribution = iHouseDistributionMapper.selectByPrimaryKey(businessOrder.getTaskId());
            if (houseDistribution != null) {
                businessOrderDTO.setHouseName(houseDistribution.getInfo());
            }
        }
        businessOrderDTO.setCreateDate(businessOrder.getCreateDate());
        businessOrderDTO.setNumber(businessOrder.getNumber());
        businessOrderDTO.setTotalPrice(businessOrder.getTotalPrice());
        businessOrderDTO.setDiscountsPrice(businessOrder.getDiscountsPrice());
        businessOrderDTO.setPayPrice(businessOrder.getPayPrice());
        businessOrderDTO.setType(businessOrder.getType());
        businessOrderDTO.setState(businessOrder.getState());
        businessOrderDTO.setCarriage(0.0);//运费
        return ServerResponse.createBySuccess("查询成功", businessOrderDTO);
    }


    /**
     * 业务订单列表
     */
    public ServerResponse businessOrderList(PageDTO pageDTO, String userToken, String houseId, String queryId) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member member = (Member) object;
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<BusinessOrder> businessOrderList = businessOrderMapper.byMemberId(member.getId(), houseId, queryId);
        PageInfo pageResult = new PageInfo(businessOrderList);
        List<BusinessOrderDTO> businessOrderDTOS = new ArrayList<>();
        for (BusinessOrder businessOrder : businessOrderList) {
            BusinessOrderDTO businessOrderDTO = new BusinessOrderDTO();
            House house = houseMapper.selectByPrimaryKey(businessOrder.getHouseId());
            String info = "";//1工序支付任务,2补货补人工 ,4待付款进来只付材料, 5验房分销, 6换货单,7:设计精算补单
            switch (businessOrder.getType()) {
                case 1:
                    info = "(工序订单)";
                    break;
                case 2:
                    info = "(补货/补人工单)";
                    break;
                case 4:
                    info = "(材料订单)";
                    break;
                case 5:
                    info = "(验房分销单)";
                    break;
                case 6:
                    info = "(换货单)";
                    break;
                case 7:
                    info = "(设计/精算单)";
                    break;
                case 8:
                    info = "(未购买单)";
                    break;
            }
            if (businessOrder.getType() == 5) {//验房分销
                HouseDistribution houseDistribution = iHouseDistributionMapper.selectByPrimaryKey(businessOrder.getTaskId());
                businessOrderDTO.setHouseName(houseDistribution.getInfo());
            } else {
                businessOrderDTO.setHouseName(house == null ? "" : house.getHouseName());
            }
            businessOrderDTO.setHouseName(businessOrderDTO.getHouseName() + info);
            List<OrderDTO> orderDTOList = this.orderDTOList(businessOrder.getNumber(), ""
//                    house == null ? "" : house.getStyle()
            );
            if (orderDTOList.size() > 0) {
                BigDecimal payPrice = new BigDecimal(0);
                for (OrderDTO orderDTO : orderDTOList) {
                    payPrice = payPrice.add(orderDTO.getTotalAmount());
                }
                businessOrderDTO.setPayPrice(payPrice);
            } else {
                businessOrderDTO.setPayPrice(businessOrder.getPayPrice());
            }
            businessOrderDTO.setOrderDTOList(orderDTOList);
            businessOrderDTO.setBusinessOrderId(businessOrder.getId());
            businessOrderDTO.setCreateDate(businessOrder.getCreateDate());
            businessOrderDTO.setNumber(businessOrder.getNumber());
            businessOrderDTO.setType(businessOrder.getType());
            businessOrderDTO.setState(businessOrder.getState());
            businessOrderDTOS.add(businessOrderDTO);
        }
        pageResult.setList(businessOrderDTOS);
        return ServerResponse.createBySuccess("查询成功", pageResult);
    }

    /*根据订单状态查询订单流水*/
    private List<OrderDTO> orderDTOList(String businessOrderNumber, String style, String orderStatus) {
        String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        List<OrderDTO> orderDTOList = new ArrayList<>();
        List<Order> orderList = orderMapper.byBusinessOrderNumberAndOrderStatus(businessOrderNumber, orderStatus);
        for (Order order : orderList) {
            OrderDTO orderDTO = new OrderDTO();
            orderDTO.setOrderId(order.getId());
            orderDTO.setTotalAmount(order.getTotalAmount());
            orderDTO.setWorkerTypeName(order.getWorkerTypeName());
            if (StringUtil.isEmpty(order.getWorkerTypeId())) {
                if (order.getType() == 2) {//材料
                    orderDTO.setImage(address + "icon/bucailiao.png");
                    orderDTO.setName("补材料商品");
                } else {
                    orderDTO.setImage(address + "icon/burengong.png");
                    orderDTO.setName("人工商品");
                }
            } else if (order.getWorkerTypeId().equals("1")) {//设计
                orderDTO.setName(style);
                orderDTO.setImage(address + "icon/shejiF.png");
            } else if (order.getWorkerTypeId().equals("2")) {
                orderDTO.setName("当家精算");
                orderDTO.setImage(address + "icon/jingsuanF.png");
            } else {
                List<OrderItem> orderItemList = orderItemMapper.byOrderIdList(order.getId());
                if (orderItemList.size() > 0) {
                    if (order.getType() == 1) {//人工
                        orderDTO.setImage(address + "icon/Arengong.png");
                        orderDTO.setName("人工类商品");
                    } else if (order.getType() == 2) {//材料
                        orderDTO.setImage(address + "icon/Acailiao.png");
                        orderDTO.setName("材料类商品");
                    }
                }
            }
            orderDTOList.add(orderDTO);
        }
        return orderDTOList;
    }

    /*订单流水*/
    private List<OrderDTO> orderDTOList(String businessOrderNumber, String style) {
        String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        List<OrderDTO> orderDTOList = new ArrayList<>();
        List<Order> orderList = orderMapper.byBusinessOrderNumber(businessOrderNumber);
        for (Order order : orderList) {
            OrderDTO orderDTO = new OrderDTO();
            orderDTO.setOrderId(order.getId());
            orderDTO.setTotalAmount(order.getTotalAmount());
            orderDTO.setWorkerTypeName(order.getWorkerTypeName());
            if (StringUtil.isEmpty(order.getWorkerTypeId())) {
                if (order.getType() == 2) {//材料
                    orderDTO.setImage(address + "icon/bucailiao.png");
                    orderDTO.setName("补材料商品");
                } else {
                    orderDTO.setImage(address + "icon/burengong.png");
                    orderDTO.setName("人工商品");
                }
            } else if (order.getWorkerTypeId().equals("1")) {//设计
                orderDTO.setName(style);
                orderDTO.setImage(address + "icon/shejiF.png");
            } else if (order.getWorkerTypeId().equals("2")) {
                orderDTO.setName("当家精算");
                orderDTO.setImage(address + "icon/jingsuanF.png");
            } else {
                List<OrderItem> orderItemList = orderItemMapper.byOrderIdList(order.getId());
                if (orderItemList.size() > 0) {
                    if (order.getType() == 1) {//人工
                        orderDTO.setImage(address + "icon/Arengong.png");
                        orderDTO.setName("人工类商品");
                    } else if (order.getType() == 2) {//材料
                        orderDTO.setImage(address + "icon/Acailiao.png");
                        orderDTO.setName("材料类商品");
                    }
                }
            }
            orderDTOList.add(orderDTO);
        }
        return orderDTOList;
    }

    /**
     * 管家要服务
     * 工匠要工序材料
     * 提交到后台材料员审核
     * //1.拆分要货单，生成子单到对应的店铺
     * //2.优化子单明细信息（将购买时的订单单价，运费等信息带过来）
     * //3.重新汇总计算要货单的总价
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse confirmOrderSplit(String userToken, String houseId) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member worker = (Member) object;
        Example example = new Example(OrderSplit.class);
        example.createCriteria().andEqualTo(OrderSplit.HOUSE_ID, houseId).andEqualTo(OrderSplit.APPLY_STATUS, 0)
                .andEqualTo(OrderSplit.WORKER_TYPE_ID, worker.getWorkerTypeId());
        List<OrderSplit> orderSplitList = orderSplitMapper.selectByExample(example);
        if (orderSplitList.size() == 0) {
            return ServerResponse.createByErrorMessage("没有生成要货单");
        } else {
            //将其他多余的单取消
            if (orderSplitList.size() > 1) {
                for (int i = 1; i < orderSplitList.size(); i++) {
                    OrderSplit orderSplit = orderSplitList.get(i);
                    orderSplit.setApplyStatus(5);
                    orderSplitMapper.updateByPrimaryKey(orderSplit);
                }
            }
            OrderSplit orderSplit = orderSplitList.get(0);
            //如果存在补货单，则告知业主补货支付
            if (!CommonUtil.isEmpty(orderSplit.getMendNumber())) {
                orderSplit.setApplyStatus(4);
                orderSplitMapper.updateByPrimaryKeySelective(orderSplit);
                return mendOrderService.confirmMendMaterial(userToken, houseId);
            }
            //setSplitOrderInfo
            setSplitOrderInfo(orderSplit.getId());

            /*example = new Example(OrderSplitItem.class);
            example.createCriteria().andEqualTo(OrderSplitItem.ORDER_SPLIT_ID, orderSplit.getId());
            List<OrderSplitItem> orderSplitItemList = orderSplitItemMapper.selectByExample(example);
            for (OrderSplitItem orderSplitItem : orderSplitItemList) {
                Warehouse warehouse = warehouseMapper.getByProductId(orderSplitItem.getProductId(), orderSplit.getHouseId());
                warehouse.setAskCount(warehouse.getAskCount() + orderSplitItem.getNum());//更新仓库已要总数
                warehouse.setAskTime(warehouse.getAskTime() + 1);//更新该货品被要次数
                warehouseMapper.updateByPrimaryKeySelective(warehouse);
            }*/
            orderSplit.setApplyStatus(1);//提交到后台
            orderSplitMapper.updateByPrimaryKeySelective(orderSplit);

            //记录仓库流水
            WarehouseDetail warehouseDetail = new WarehouseDetail();
            warehouseDetail.setHouseId(houseId);
            warehouseDetail.setRelationId(orderSplit.getId());//要货单
            warehouseDetail.setRecordType(1);//要
            warehouseDetailMapper.insert(warehouseDetail);

            House house = houseMapper.selectByPrimaryKey(houseId);
            if (worker.getWorkerType() == 3) {
                configMessageService.addConfigMessage(null, AppType.ZHUANGXIU, house.getMemberId(), "0", "大管家要服务",
                        String.format(DjConstants.PushMessage.STEWARD_Y_SERVER, house.getHouseName()), "");
            } else {
                configMessageService.addConfigMessage(null, AppType.ZHUANGXIU, house.getMemberId(), "0", "工匠要材料", String.format
                        (DjConstants.PushMessage.CRAFTSMAN_Y_MATERIAL, house.getHouseName()), "");
            }
            return ServerResponse.createBySuccessMessage("操作成功");
        }
    }


    /**
     * 补货提交订单接口
     *
     * @param userToken
     * @param houseId
     * @return
     */
    public ServerResponse abrufbildungSubmitOrder(String userToken, String cityId, String houseId,
                                                  String mendOrderId, String addressId) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = (Member) object;
            MendOrder mendOrder = mendOrderMapper.selectByPrimaryKey(mendOrderId);
            if (null == mendOrder)
                return ServerResponse.createByErrorMessage("货单不存在");
            Member worker = iMemberMapper.selectByPrimaryKey(mendOrder.getApplyMemberId());
            Order order = new Order();
            order.setHouseId(houseId);
            order.setTotalAmount(new BigDecimal(mendOrder.getTotalAmount()));
            order.setType(mendOrder.getType());
            order.setDataStatus(0);
            order.setOrderNumber(System.currentTimeMillis() + "-" + (int) (Math.random() * 9000 + 1000));
            order.setMemberId(member.getId());
            if (null != worker) {
                order.setWorkerId(worker.getId());
                WorkerType workerType = workerTypeMapper.selectByPrimaryKey(worker.getWorkerTypeId());
                if (null != workerType) {
                    order.setWorkerTypeName(workerType.getName());
                    order.setWorkerTypeId(workerType.getId());
                }
            }
            order.setCityId(cityId);
            order.setTotalDiscountPrice(new BigDecimal(0));
            order.setTotalStevedorageCost(new BigDecimal(0));
            order.setTotalTransportationCost(new BigDecimal(0));
            order.setActualPaymentPrice(new BigDecimal(0));
            order.setOrderStatus("1");
            order.setOrderGenerationTime(new Date());
            order.setOrderSource(2);//来源补货
            order.setWorkerId(member.getId());
            order.setAddressId(addressId);
            order.setCreateBy(member.getId());
            orderMapper.insert(order);


            BigDecimal paymentPrice = new BigDecimal(0);//总共钱
            BigDecimal freightPrice = new BigDecimal(0);//总运费
            BigDecimal totalMoveDost = new BigDecimal(0);//搬运费
            Example example = new Example(MendMateriel.class);
            example.createCriteria().andEqualTo(MendMateriel.MEND_ORDER_ID, mendOrderId);
            List<MendMateriel> mendMateriels = mendMaterialMapper.selectByExample(example);
            for (MendMateriel mendMateriel : mendMateriels) {
                BigDecimal totalMaterialPrice = new BigDecimal(0);//组总价
                BigDecimal totalPrice = new BigDecimal(mendMateriel.getPrice() * mendMateriel.getShopCount());
                if (mendMateriel.getProductType() == 0) {
                    totalMaterialPrice = totalMaterialPrice.add(totalPrice);
                }
                paymentPrice = paymentPrice.add(totalPrice);
                Double freight = storefrontConfigAPI.getFreightPrice(mendMateriel.getStorefrontId(), totalMaterialPrice.doubleValue());
                freightPrice = freightPrice.add(new BigDecimal(freight));
                //搬运费运算
                Double moveDost = masterCostAcquisitionService.getStevedorageCost(houseId, mendMateriel.getProductId(), mendMateriel.getShopCount());
                totalMoveDost = totalMoveDost.add(new BigDecimal(moveDost));
                //生成订单明细
                OrderItem orderItem = new OrderItem();
                orderItem.setOrderId(order.getId());
                orderItem.setCityId(cityId);
                orderItem.setHouseId(houseId);
                orderItem.setProductId(mendMateriel.getProductId());
                orderItem.setProductSn(mendMateriel.getProductSn());
                orderItem.setProductName(mendMateriel.getProductName());
                orderItem.setProductNickName(mendMateriel.getProductNickName());
                orderItem.setUnitName(mendMateriel.getUnitName());
                orderItem.setCategoryId(mendMateriel.getCategoryId());
                orderItem.setImage(mendMateriel.getImage());
                orderItem.setProductType(mendMateriel.getProductType());
                orderItem.setPrice(mendMateriel.getPrice());
                orderItem.setShopCount(mendMateriel.getShopCount());//购买总数
                orderItem.setAskCount(mendMateriel.getShopCount());//要货数
                orderItem.setTotalPrice(mendMateriel.getShopCount() * mendMateriel.getPrice());//总价
                orderItem.setStorefontId(mendMateriel.getStorefrontId());
                orderItem.setDiscountPrice(0d);
                orderItem.setActualPaymentPrice(0d);
                orderItem.setStevedorageCost(0d);
                orderItem.setTransportationCost(0d);
                if (mendMateriel.getProductType() == 0 && freight > 0) {
                    //均摊运费
                    Double transportationCost = (orderItem.getTotalPrice() / totalMaterialPrice.doubleValue()) * freight;
                    orderItem.setTransportationCost(transportationCost);
                }
                //搬运费运算
                if (moveDost > 0) {
                    //均摊运费
                    orderItem.setStevedorageCost(moveDost);
                }
                orderItem.setOrderStatus("1");//1待付款，2已付款，3待收货，4已完成，5已取消，6已退货，7已关闭
                orderItem.setCreateBy(member.getId());
                orderItemMapper.insert(orderItem);
            }

            // 生成支付业务单
            example = new Example(BusinessOrder.class);
            example.createCriteria().andEqualTo(BusinessOrder.TASK_ID, order.getId())
                    .andNotEqualTo(BusinessOrder.STATE, 4);
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
            //补货单对应订单
            mendOrder.setOrderId(order.getId());
            mendOrder.setBusinessOrderNumber(businessOrder.getNumber());
            mendOrderMapper.updateByPrimaryKeySelective(mendOrder);
            return ServerResponse.createBySuccess("提交成功", order.getId());
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ServerResponse.createByErrorMessage("提交失败：原因：" + e.getMessage());
        }
    }
    /**
     * 补货提交订单回调接口根据businessOrderNumber
     *
     * @param businessOrderNumber
     * @return
     */
    public ServerResponse setOrderQuantityBybusinessByOrderNumber(String businessOrderNumber) {
        Example example = new Example(MendOrder.class);
        example.createCriteria().andEqualTo(MendOrder.BUSINESS_ORDER_NUMBER, businessOrderNumber);
        List<MendOrder> mendOrders = mendOrderMapper.selectByExample(example);
        List<String> mendOrderNumbers = mendOrders.stream()
                .map(MendOrder::getNumber)
                .collect(Collectors.toList());
        //根据补货单number查找要货单
        example = new Example(OrderSplit.class);
        example.createCriteria().andIn(OrderSplit.MEMBER_NAME, mendOrderNumbers)
                .andEqualTo(OrderSplit.DATA_STATUS, 0);
        List<OrderSplit> orderSplits = orderSplitMapper.selectByExample(example);
        for(OrderSplit orderSplit:orderSplits){
            this.setSplitOrderInfo(orderSplit.getId());
        }
        //根据要货单查询要货单明细
        /*List<String> orderSplitIds = orderSplits.stream()
                .map(OrderSplit::getId)
                .collect(Collectors.toList());
        example = new Example(OrderSplitItem.class);
        example.createCriteria().andEqualTo(OrderSplitItem.DATA_STATUS, 0)
                .andIn(OrderSplitItem.ORDER_SPLIT_ID, orderSplitIds);
        List<OrderSplitItem> orderSplitItems = orderSplitItemMapper.selectByExample(example);*/
        return ServerResponse.createBySuccessMessage("提交成功");
    }
    /**
     * 补货提交订单回调接口根据orderSplitId
     *
     * @param orderSplitId
     * @return
     */
    public ServerResponse setOrderQuantityBybusinessByOrderSplitId(String orderSplitId) {
        /*Example example = new Example(OrderSplitItem.class);
        example.createCriteria().andEqualTo(OrderSplitItem.DATA_STATUS, 0)
                .andEqualTo(OrderSplitItem.ORDER_SPLIT_ID, orderSplitId);
        List<OrderSplitItem> orderSplitItems = orderSplitItemMapper.selectByExample(example);*/
        return this.setSplitOrderInfo(orderSplitId);
    }

    /**
     * 补货提交订单回调接口
     * 根据店铺，拆成子要货单
     * @param orderSplitId 要货单ID
     * @return
     */
    public ServerResponse setSplitOrderInfo(String orderSplitId) {
        //1.根据要货单，查询要货单明细接口，按店铺划分
        OrderSplit split=orderSplitMapper.selectByPrimaryKey(orderSplitId);
        List<Storefront> storefrontList=orderSplitItemMapper.selectStorefrontIdByOrderSplitId(orderSplitId);//根据父要货单号查询有多少个店铺
        if(storefrontList!=null){
            Example example;
            OrderSplit orderSplit;
            for(Storefront storefront:storefrontList){
                //2.1生成发货单
                example = new Example(OrderSplit.class);
                orderSplit = new OrderSplit();
                orderSplit.setNumber("DJ" + 200000 + orderSplitMapper.selectCountByExample(example));//要货单号
                orderSplit.setHouseId(split.getHouseId());
                orderSplit.setApplyStatus(1);//后台审核状态：0生成中, 1申请中, 2通过, 3不通过, 4业主待支付补货材料 后台(材料员)
                orderSplit.setMemberId(split.getMemberId());
                orderSplit.setMemberName(split.getMemberName());
                orderSplit.setMobile(split.getMobile());
                orderSplit.setWorkerTypeId(split.getWorkerTypeId());
                orderSplit.setTotalAmount(new BigDecimal(0));
                orderSplit.setAddressId(split.getAddressId());
                orderSplit.setStorefrontId(storefront.getId());
                orderSplit.setSplitParentId(orderSplitId);
                Double totalAmount=0d;

                //2.2查询当前店铺下，当前要货单下的所有商品明细
                List<OrderSplitItem> orderItemList=orderSplitItemMapper.selectOrderSplitItemList(orderSplitId,storefront.getId());
                for(OrderSplitItem splitItem:orderItemList){
                    //扣除业主仓库数据
                    Warehouse warehouse = warehouseMapper.getByProductId(splitItem.getProductId(), orderSplit.getHouseId());
                    warehouse.setAskCount(warehouse.getAskCount() + splitItem.getNum());//更新仓库已要总数
                    warehouse.setAskTime(warehouse.getAskTime() + 1);//更新该货品被要次数
                    warehouseMapper.updateByPrimaryKeySelective(warehouse);
                    //2.3生成要货单,找到对应的待扣除的订单明细，若从一个订单中扣，则只生成一个明细，从多个订单中扣，则生成多个明细
                    String productId=splitItem.getProductId();
                    //要货量
                    Double askCount = splitItem.getNum();//当前此次要货量
                    //查询订单信息
                    example = new Example(OrderItem.class);
                    example.createCriteria().andEqualTo(OrderItem.HOUSE_ID, splitItem.getHouseId())
                            .andEqualTo(OrderItem.PRODUCT_ID, productId);
                    List<OrderItem> orderItems = orderItemMapper.selectByExample(example);
                    for (OrderItem orderItem : orderItems) {
                        //剩余量(可扣减量)
                        Double surplus = MathUtil.sub(MathUtil.sub(orderItem.getShopCount()!=null?orderItem.getShopCount():0,
                                orderItem.getAskCount()!=null?orderItem.getAskCount():0), orderItem.getReturnCount()!=null?orderItem.getReturnCount():0);
                        //判断订单剩余量是否大于要货量
                        if (surplus >= askCount) {
                            if(orderItem.getAskCount()==null){
                                orderItem.setAskCount(0d);
                            }
                            splitItem.setOrderItemId(orderItem.getId());
                            splitItem.setOrderSplitId(orderSplit.getId());
                            splitItem.setPrice(orderItem.getPrice());
                            splitItem.setNum(askCount);
                            splitItem.setTotalPrice(splitItem.getPrice()*splitItem.getNum());
                            //计算运费，搬运费
                            Double transportationCost=orderItem.getTransportationCost()!=null?orderItem.getTransportationCost():0;//运费
                            Double stevedorageCost=orderItem.getStevedorageCost()!=null?orderItem.getStevedorageCost():0;//搬运费
                            //计算运费
                            if(transportationCost>0.0) {//（运费/总数量）*收货量
                                splitItem.setTransportationCost(MathUtil.mul(MathUtil.div(transportationCost,orderItem.getShopCount()!=null?orderItem.getShopCount():0),askCount));
                            }else{
                                splitItem.setTransportationCost(0d);
                            }
                            //计算搬运费
                            if(stevedorageCost>0.0){//（搬运费/总数量）*收货量
                                splitItem.setStevedorageCost(MathUtil.mul(MathUtil.div(stevedorageCost,orderItem.getShopCount()!=null?orderItem.getShopCount():0),askCount));
                            }else{
                                splitItem.setStevedorageCost(0d);
                            }
                            splitItem.setModifyDate(new Date());
                            orderSplitItemMapper.updateByPrimaryKeySelective(splitItem);
                            //修改订单中的要货量
                            orderItem.setAskCount(MathUtil.add(orderItem.getAskCount()!=null?orderItem.getAskCount():0,askCount));
                            orderItem.setModifyDate(new Date());
                            orderItemMapper.updateByPrimaryKeySelective(orderItem);
                            totalAmount = MathUtil.add(totalAmount,MathUtil.add(MathUtil.add(splitItem.getPrice()*splitItem.getNum(),splitItem.getStevedorageCost()),splitItem.getTransportationCost()));

                        }else if(surplus > 0 &&surplus < askCount){
                            //生成新的要货单明细
                            OrderSplitItem orderSplitItem=new OrderSplitItem();
                            orderSplitItem.setOrderSplitId(orderSplit.getId());
                            orderSplitItem.setProductId(splitItem.getId());
                            orderSplitItem.setProductSn(splitItem.getProductSn());
                            orderSplitItem.setProductName(splitItem.getProductName());
                            orderSplitItem.setPrice(orderItem.getPrice());
                            orderSplitItem.setAskCount(orderItem.getAskCount());
                            orderSplitItem.setCost(splitItem.getCost());
                            orderSplitItem.setShopCount(orderItem.getShopCount());
                            orderSplitItem.setNum(surplus);
                            orderSplitItem.setUnitName(splitItem.getUnitName());
                            orderSplitItem.setTotalPrice(splitItem.getPrice()*splitItem.getNum());
                            orderSplitItem.setProductType(splitItem.getProductType());
                            orderSplitItem.setCategoryId(splitItem.getCategoryId());
                            orderSplitItem.setImage(splitItem.getImage());//货品图片
                            orderSplitItem.setHouseId(splitItem.getHouseId());
                            //计算运费，搬运费
                            Double transportationCost=orderItem.getTransportationCost()!=null?orderItem.getTransportationCost():0;//运费
                            Double stevedorageCost=orderItem.getStevedorageCost()!=null?orderItem.getStevedorageCost():0;//搬运费
                            //计算运费
                            if(transportationCost>0.0) {//（运费/总数量）*收货量
                                orderSplitItem.setTransportationCost(MathUtil.mul(MathUtil.div(transportationCost,orderItem.getShopCount()!=null?orderItem.getShopCount():0),askCount));
                            }else{
                                orderSplitItem.setTransportationCost(0d);
                            }
                            //计算搬运费
                            if(stevedorageCost>0.0){//（搬运费/总数量）*收货量
                                orderSplitItem.setStevedorageCost(MathUtil.mul(MathUtil.div(stevedorageCost,orderItem.getShopCount()!=null?orderItem.getShopCount():0),askCount));
                            }else{
                                orderSplitItem.setStevedorageCost(0d);
                            }
                            orderSplitItemMapper.insert(orderSplitItem);
                            //修改订单中的要货量
                            orderItem.setAskCount(MathUtil.add(orderItem.getAskCount()!=null?orderItem.getAskCount():0,surplus));
                            orderItem.setModifyDate(new Date());
                            orderItemMapper.updateByPrimaryKeySelective(orderItem);
                            //重置要货量
                            askCount=MathUtil.sub(askCount,surplus);
                            totalAmount = MathUtil.add(totalAmount,MathUtil.add(MathUtil.add(orderSplitItem.getPrice()*(orderSplitItem.getNum()!=null?orderSplitItem.getNum():0)
                                    ,orderSplitItem.getStevedorageCost()!=null?orderSplitItem.getStevedorageCost():0),orderSplitItem.getTransportationCost()!=null?orderSplitItem.getTransportationCost():0));
                        }
                    }
                    orderSplit.setTotalAmount(BigDecimal.valueOf(totalAmount));
                    orderSplitMapper.insert(orderSplit);
                }
            }
        }
        return ServerResponse.createBySuccess("提交成功");
    }
    /**
     * 补货提交订单回调接口
     *
     * @param orderSplitItems
     * @return
     */
 /*   public ServerResponse setOrderQuantity(List<OrderSplitItem> orderSplitItems) {
        try {
            Example example = new Example(OrderItem.class);
            orderSplitItems.forEach(orderSplitItem -> {
                example.createCriteria().andEqualTo(OrderItem.HOUSE_ID, orderSplitItem.getHouseId())
                        .andEqualTo(OrderItem.PRODUCT_ID, orderSplitItem.getProductId());
                List<OrderItem> orderItems = orderItemMapper.selectByExample(example);
                //要货量
                Double askCount = orderSplitItem.getAskCount();
                //订单明细ID
                List<String> orderItemIds = new ArrayList<>();
                //订单扣减描述
                StringBuilder stringBuilder = new StringBuilder();
                Example example1 = new Example(Warehouse.class);
                for (OrderItem orderItem : orderItems) {
                    //剩余量
                    Double surplus = MathUtil.sub(MathUtil.sub(orderItem.getShopCount(), orderItem.getAskCount()), orderItem.getReturnCount());
                    //判断订单剩余量是否大于要货量
                    if (surplus >= askCount) {
                        //订单明细剩余量大于要货量,订单明细要货量=本订单要货量+原来要货量
                        orderItem.setAskCount(MathUtil.add(orderItem.getAskCount(), askCount));
                        //修改订单
                        orderItemMapper.updateByPrimaryKeySelective(orderItem);
                        orderItemIds.add(orderItem.getId());
                        stringBuilder.append("订单：" + orderItem.getId() + "，要货数：" + orderItem.getAskCount() + ";");
                        break;
                        //判断订单明细剩余量>0并且订单剩余量小于要货量
                    } else if (surplus > 0) {
                        //订单明细剩余量小于要货量,订单明细要货量=本订单要货量+原来要货量
                        orderItem.setAskCount(MathUtil.add(orderItem.getAskCount(), surplus));
                        //剩余要货量继续从下一个订单里扣除
                        askCount = MathUtil.sub(askCount, surplus);
                        //修改订单
                        orderItemMapper.updateByPrimaryKeySelective(orderItem);
                        orderItemIds.add(orderItem.getId());
                        stringBuilder.append("订单：" + orderItem.getId() + "，要货数：" + orderItem.getAskCount() + ";");
                    }
                    //业主仓库数量加减
                    example1.createCriteria()
                            .andEqualTo(Warehouse.PRODUCT_ID, orderSplitItem.getProductId())
                            .andEqualTo(Warehouse.HOUSE_ID, orderSplitItem.getHouseId());
                    Warehouse warehouse = iWarehouseMapper.selectOneByExample(example1);
                    warehouse.setAskCount(orderItem.getAskCount());
                    warehouse.setAskTime(warehouse.getAskTime() + 1);//更新要货次数
                    iWarehouseMapper.updateByPrimaryKeySelective(warehouse);
                }
                //添加订单明细ID到要货单明细
                orderSplitItem.setOrderItemId(orderItemIds.stream().collect(Collectors.joining(",")));
                orderSplitItem.setOrderItemStr(stringBuilder.toString());
                orderSplitItemMapper.updateByPrimaryKeySelective(orderSplitItem);
                //添加增殖类商品
                example1 = new Example(DeliverOrderAddedProduct.class);
                example1.createCriteria().andEqualTo(DeliverOrderAddedProduct.ANY_ORDER_ID, orderSplitItem.getId())
                        .andEqualTo(DeliverOrderAddedProduct.DATA_STATUS, 0);
                List<DeliverOrderAddedProduct> deliverOrderAddedProducts = iMasterDeliverOrderAddedProductMapper.selectByExample(example1);
                deliverOrderAddedProducts.forEach(deliverOrderAddedProduct -> {
                    DeliverOrderAddedProduct deliverOrderAddedProduct1 = new DeliverOrderAddedProduct();
                    deliverOrderAddedProduct1.setAnyOrderId(orderSplitItem.getId());
                    deliverOrderAddedProduct1.setAddedProductId(deliverOrderAddedProduct.getAddedProductId());
                    deliverOrderAddedProduct1.setPrice(deliverOrderAddedProduct.getPrice());
                    deliverOrderAddedProduct1.setProductName(deliverOrderAddedProduct.getProductName());
                    deliverOrderAddedProduct1.setSource("2");
                    iMasterDeliverOrderAddedProductMapper.insert(deliverOrderAddedProduct1);
                });
            });
            return ServerResponse.createBySuccessMessage("提交成功");
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ServerResponse.createByErrorMessage("提交失败：原因：" + e.getMessage());
        }
    }*/


    /**
     * 返回已添加要货单明细
     */
    public ServerResponse getOrderItemList(String userToken, String houseId) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member worker = (Member) object;
            Map<String, Object> map = new HashMap<>();
            WorkerType workerType = workerTypeMapper.selectByPrimaryKey(worker.getWorkerTypeId());
            if (workerType == null) {
                return ServerResponse.createByErrorMessage("未找到该工种身份");
            }
            map.put("times", workerType.getSafeState());//要货次数
            Example example = new Example(OrderSplit.class);
            example.createCriteria().andEqualTo(OrderSplit.HOUSE_ID, houseId).andEqualTo(OrderSplit.APPLY_STATUS, 2).andEqualTo(OrderSplit.WORKER_TYPE_ID, worker.getWorkerTypeId());
            int surplus = orderSplitMapper.selectCountByExample(example);
            map.put("surplus", workerType.getSafeState() - surplus);

            example = new Example(OrderSplit.class);
            example.createCriteria().andEqualTo(OrderSplit.HOUSE_ID, houseId).andEqualTo(OrderSplit.APPLY_STATUS, 0)
                    .andEqualTo(OrderSplit.WORKER_TYPE_ID, worker.getWorkerTypeId());
            List<OrderSplit> orderSplitList = orderSplitMapper.selectByExample(example);
            if (orderSplitList.size() == 0) {
                return ServerResponse.createByErrorMessage("没有生成要货单");
            } else {
                //将其他多余的单取消
                if (orderSplitList.size() > 1) {
                    for (int i = 1; i < orderSplitList.size(); i++) {
                        OrderSplit orderSplit = orderSplitList.get(i);
                        orderSplit.setApplyStatus(5);
                        orderSplitMapper.updateByPrimaryKey(orderSplit);
                    }
                }
                OrderSplit orderSplit = orderSplitList.get(0);
                example = new Example(OrderSplitItem.class);
                example.createCriteria().andEqualTo(OrderSplitItem.ORDER_SPLIT_ID, orderSplit.getId());
                List<OrderSplitItem> orderSplitItemList = orderSplitItemMapper.selectByExample(example);
                List<Map<String, Object>> resMapList = new ArrayList<>();
                for (OrderSplitItem v : orderSplitItemList) {
                    v.initPath(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class));
                    //增加当前仓库商品的购买量和剩余量字段的显示
                    example = new Example(Warehouse.class);
                    example.createCriteria().andEqualTo(Warehouse.HOUSE_ID, houseId).andEqualTo(Warehouse.PRODUCT_ID, v.getProductId());
                    List<Warehouse> warehouseList = warehouseMapper.selectByExample(example);
                    Map orderSplitMap = BeanUtils.beanToMap(v);
                    if (warehouseList.size() > 0) {
                        Warehouse warehouse = warehouseList.get(0);
                        orderSplitMap.put("shopCount", warehouse.getShopCount());//购买量
                        orderSplitMap.put("surCount", warehouse.getShopCount() -
                                (warehouse.getOwnerBack() == null ? 0D : warehouse.getOwnerBack()) - warehouse.getAskCount());
                    } else {
                        orderSplitMap.put("shopCount", 0D);//购买量
                        orderSplitMap.put("surCount", 0D);//剩余量
                    }
                    orderSplitMap.put("replenishment",0);
                    if(MathUtil.sub(v.getNum(),Double.parseDouble(orderSplitMap.get("surCount").toString()))>0){
                        orderSplitMap.put("replenishment",1);
                    }
                    resMapList.add(orderSplitMap);
                }
                map.put("orderSplitItemList", resMapList);
                return ServerResponse.createBySuccess("查询成功", map);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 管家要包工包料
     * 工匠要工序材料
     * 提交到要货
     */
    public ServerResponse saveOrderSplit(String productArr, String houseId, String userToken) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member worker = (Member) object;
            Example example = new Example(OrderSplit.class);
            example.createCriteria().andEqualTo(OrderSplit.HOUSE_ID, houseId).andEqualTo(OrderSplit.APPLY_STATUS, 4)
                    .andEqualTo(OrderSplit.WORKER_TYPE_ID, worker.getWorkerTypeId());
            int orderSplitnum = orderSplitMapper.selectCountByExample(example);
            if (orderSplitnum > 0) {
                return ServerResponse.createByErrorMessage("存在业主未处理的补货单，无法提交要货！");
            }

            example = new Example(SplitDeliver.class);
            example.createCriteria().andEqualTo(SplitDeliver.HOUSE_ID, houseId)
                    .andEqualTo(SplitDeliver.SHIPPING_STATE, 1).andCondition(" DATE_SUB(CURDATE(), INTERVAL 7 DAY) > date(send_time) ");
            int list = splitDeliverMapper.selectCountByExample(example);
            if (list > 0) {
                return ServerResponse.createByErrorMessage("存在供应商发货后7天还未签收,无法提交要货！");
            }
            ServerResponse serverResponse = mendOrderService.mendChecking(houseId, worker.getWorkerTypeId(), 0);
            if (!serverResponse.isSuccess()) {
                return ServerResponse.createByErrorMessage(serverResponse.getResultMsg());
            }

            example = new Example(OrderSplit.class);
            example.createCriteria().andEqualTo(OrderSplit.HOUSE_ID, houseId)
                    .andEqualTo(OrderSplit.APPLY_STATUS, 0)
                    .andEqualTo(OrderSplit.WORKER_TYPE_ID, worker.getWorkerTypeId());
            List<OrderSplit> orderSplitList = orderSplitMapper.selectByExample(example);
            OrderSplit orderSplit;
            House house = houseMapper.selectByPrimaryKey(houseId);
            if (orderSplitList.size() > 0) {
                orderSplit = orderSplitList.get(0);
                /*删除之前子项*/
                example = new Example(OrderSplitItem.class);
                example.createCriteria().andEqualTo(OrderSplitItem.ORDER_SPLIT_ID, orderSplit.getId());
                orderSplitItemMapper.deleteByExample(example);

                /*删除补货信息*/
                if (!CommonUtil.isEmpty(orderSplit.getMendNumber())) {
                    mendOrderMapper.deleteByPrimaryKey(orderSplit.getMendNumber());
                    example = new Example(MendMateriel.class);
                    example.createCriteria().andEqualTo(MendMateriel.MEND_ORDER_ID, orderSplit.getMendNumber());
                    mendMaterialMapper.deleteByExample(example);
                    orderSplit.setMendNumber("");
                }
                orderSplit.setCreateDate(new Date());
                orderSplit.setModifyDate(new Date());
                orderSplit.setMemberId(worker.getId());
                orderSplit.setMemberName(worker.getName());
                orderSplit.setMobile(worker.getMobile());
                orderSplit.setWorkerTypeId(worker.getWorkerTypeId());
                orderSplit.setTotalAmount(new BigDecimal(0));
                orderSplitMapper.updateByPrimaryKeySelective(orderSplit);
            } else {
                example=new Example(MemberAddress.class);
                example.createCriteria().andEqualTo(MemberAddress.HOUSE_ID,houseId);
                MemberAddress memberAddress=iMasterMemberAddressMapper.selectOneByExample(example);

                example = new Example(OrderSplit.class);
                orderSplit = new OrderSplit();
                orderSplit.setNumber("DJ" + 200000 + orderSplitMapper.selectCountByExample(example));//要货单号
                orderSplit.setHouseId(houseId);
                orderSplit.setApplyStatus(0);//后台审核状态：0生成中, 1申请中, 2通过, 3不通过, 4业主待支付补货材料 后台(材料员)
                orderSplit.setMemberId(worker.getId());
                orderSplit.setMemberName(worker.getName());
                orderSplit.setMobile(worker.getMobile());
                orderSplit.setWorkerTypeId(worker.getWorkerTypeId());
                orderSplit.setTotalAmount(new BigDecimal(0));
                if(memberAddress!=null){
                    orderSplit.setAddressId(memberAddress.getId());
                }
                orderSplitMapper.insert(orderSplit);
            }

            //获取要货购物车数据
            List<Cart> cartList = cartMapper.cartList(houseId, worker.getWorkerTypeId(), worker.getId());
            List<Map<String, Object>> productList = new ArrayList<>();
            BigDecimal totalAmount = new BigDecimal(0);//总价
            for (Cart aCartList : cartList) {
                Double num = aCartList.getShopCount();
                String productId = aCartList.getProductId();
                Warehouse warehouse = warehouseMapper.getByProductId(productId, houseId);//定位到仓库id
                //增加店铺概念 现在cart productId为店铺商品表主键id
                StorefrontProduct storefrontProduct = iMasterStorefrontProductMapper.selectByPrimaryKey(productId);
                DjBasicsProductTemplate product = iMasterProductTemplateMapper.selectByPrimaryKey(storefrontProduct.getProdTemplateId());//forMasterAPI.getProduct(house.getCityId(), storefrontProduct.getProdTemplateId());
                example = new Example(OrderSplitItem.class);
                example.createCriteria()
                        .andEqualTo(OrderSplitItem.PRODUCT_ID, productId)
                        .andEqualTo(OrderSplitItem.ORDER_SPLIT_ID, orderSplit.getId());
                List<OrderSplitItem> orderSplitItems = orderSplitItemMapper.selectByExample(example);
                OrderSplitItem orderSplitItem = new OrderSplitItem();
                if (orderSplitItems.size() > 0) {
                    orderSplitItem = orderSplitItems.get(0);
                }
                orderSplitItem.setCityId(house.getCityId());
                if (warehouse != null) {
                    orderSplitItem.setOrderSplitId(orderSplit.getId());
                    orderSplitItem.setWarehouseId(warehouse.getId());//仓库子项id
                    orderSplitItem.setProductId(aCartList.getProductId());
                    orderSplitItem.setProductSn(aCartList.getProductSn());
                    orderSplitItem.setProductName(aCartList.getProductName());
                    orderSplitItem.setPrice(warehouse.getPrice());
                    orderSplitItem.setAskCount(warehouse.getAskCount());
                    orderSplitItem.setCost(warehouse.getCost());
                    orderSplitItem.setShopCount(warehouse.getShopCount());
                    orderSplitItem.setNum(num);
                    orderSplitItem.setUnitName(warehouse.getUnitName());
                    orderSplitItem.setTotalPrice(warehouse.getPrice() * num);//单项总价 销售价
                    orderSplitItem.setProductType(warehouse.getProductType());
                    orderSplitItem.setCategoryId(warehouse.getCategoryId());
                    orderSplitItem.setImage(storefrontProduct.getImage());//货品图片
                    orderSplitItem.setHouseId(houseId);
                    orderSplitItemMapper.insert(orderSplitItem);
                } else {
                    BasicsGoods goods = masterBasicsGoodsMapper.selectByPrimaryKey(product.getGoodsId());//forMasterAPI.getGoods(house.getCityId(), product.getGoodsId());
                    orderSplitItem.setOrderSplitId(orderSplit.getId());
                    orderSplitItem.setProductId(aCartList.getProductId());
                    orderSplitItem.setProductSn(aCartList.getProductSn());
                    orderSplitItem.setProductName(aCartList.getProductName());
                    orderSplitItem.setPrice(storefrontProduct.getSellPrice());
                    orderSplitItem.setAskCount(0d);
                    orderSplitItem.setCost(product.getCost());
                    orderSplitItem.setShopCount(0d);
                    orderSplitItem.setNum(num);
                    Unit unit=masterProductTemplateService.getUnitInfoByTemplateId(storefrontProduct.getProdTemplateId());
                    if(unit!=null){
                        orderSplitItem.setUnitName(unit.getName());//.getUnitName(house.getCityId(), product.getConvertUnit())
                    }
                    orderSplitItem.setTotalPrice(product.getPrice() * num);//单项总价 销售价
                    orderSplitItem.setProductType(goods.getType());
                    orderSplitItem.setCategoryId(product.getCategoryId());
                    orderSplitItem.setImage(storefrontProduct.getImage());//货品图片
                    orderSplitItem.setHouseId(houseId);
                    orderSplitItemMapper.insert(orderSplitItem);
                }
                //计算补货数量
                if (warehouse != null) {
                    //仓库剩余数
                    double surCount = warehouse.getShopCount() - warehouse.getAskCount() - (warehouse.getOwnerBack() == null ? 0D : warehouse.getOwnerBack());
                    //多出的数
                    double overflowCount = (num - surCount);
                    if (overflowCount > 0) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("num", overflowCount);
                        map.put("productId", productId);
                        productList.add(map);
                    } else {
                        //如果剩余数为负数
                        if (surCount < 0) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("num", num);
                            map.put("productId", productId);
                            productList.add(map);
                        }
                    }
                } else {
                    Map<String, Object> map = new HashMap<>();
                    map.put("num", num);
                    map.put("productId", productId);
                    productList.add(map);
                }
                totalAmount = totalAmount.add(BigDecimal.valueOf(aCartList.getPrice() * aCartList.getShopCount()));
            }
            //更新要货单总价
            orderSplit.setTotalAmount(totalAmount);
            orderSplitMapper.updateByPrimaryKeySelective(orderSplit);
            //补货材料列表
            String mendMaterialArr = JSON.toJSONString(productList);
            if (!CommonUtil.isEmpty(mendMaterialArr) && productList.size() > 0) {
                serverResponse = mendOrderService.saveMendMaterial(userToken, houseId, mendMaterialArr);
                if (serverResponse.isSuccess()) {
                    if (serverResponse.getResultObj() != null) {
                        //保存补货ID
                        orderSplit.setMendNumber(String.valueOf(serverResponse.getResultObj()));
                        orderSplitMapper.updateByPrimaryKeySelective(orderSplit);
                    }
                }
            }
            return ServerResponse.createBySuccessMessage("提交成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("提交失败");
        }
    }

    /**
     * 取消订单
     *
     * @param userToken
     * @param orderId
     * @return
     */
    public ServerResponse cancleBusinessOrderById(String userToken, String orderId) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = (Member) object;
            Order order = orderMapper.selectByPrimaryKey(orderId);
            if (order == null) {
                return ServerResponse.createByErrorMessage("该订单不存在");
            }
            House house = houseMapper.selectByPrimaryKey(order.getHouseId());
            if (house == null) {
                return ServerResponse.createByErrorMessage("该房产不存在");
            }
            //主订单取消
            Example orderexample = new Example(Order.class);
            orderexample.createCriteria().andEqualTo(Order.ID, orderId).andEqualTo(Order.MEMBER_ID, member.getId());
            Order order1 = new Order();
            order1.setOrderStatus("5");
            order1.setCancellationTime(new Date());
            orderMapper.updateByExampleSelective(order1, orderexample);
            //订单详情取消

            Example orderItemexample = new Example(OrderItem.class);
            orderItemexample.createCriteria().andEqualTo(Order.ID, orderId);
            OrderItem orderItem = new OrderItem();
//            orderItem.setDataStatus(1);
            orderItem.setOrderStatus("5");//取消订单
            orderItem.setCancellationTime(new Date());
            orderItemMapper.updateByExampleSelective(orderItem, orderItemexample);


//            List<OrderItem> OrderItemList = orderItemMapper.selectByExample(orderItemexample);
//            for (OrderItem OrderItem : OrderItemList) {
//                //要货单以及要货单详情表取消 orderSplitMapper  orderSplitItemMapper
//                String id = OrderItem.getId();
//                Example OrderSplitItemexample = new Example(OrderSplitItem.class);
//                OrderSplitItemexample.createCriteria().andEqualTo(OrderSplitItem.ORDER_ITEM_ID, id);
//                OrderSplitItem orderSplitItem = new OrderSplitItem();
//                orderSplitItem.setDataStatus(1);
//                orderSplitItemMapper.updateByExampleSelective(orderSplitItem, OrderSplitItemexample);
//                orderSplitItem = orderSplitItemMapper.selectByPrimaryKey(id);
//                //发货单取消
//                String splitItemId = orderSplitItem.getId();
//                Example splitDeliverExample = new Example(SplitDeliver.class);
//                splitDeliverExample.createCriteria().andEqualTo(SplitDeliver.NUMBER, splitItemId);
//                SplitDeliver splitDeliver = new SplitDeliver();
//                splitDeliver.setDataStatus(1);
//                splitDeliverMapper.updateByExampleSelective(splitDeliver, splitDeliverExample);
//            }
            return ServerResponse.createBySuccess("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("删除订单异常");
        }

    }

    /**
     *查询差价订单
     * @param userToken
     * @param houseId
     * @return
     */
    public ServerResponse getDiffOrderById(String userToken,String houseId){
        try{
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Map returnMap=new HashMap();
            House house=houseMapper.selectByPrimaryKey(houseId);
            Example example = new Example(MemberAddress.class);
            example.createCriteria().andEqualTo(MemberAddress.HOUSE_ID, house.getId());
            MemberAddress memberAddress=iMasterMemberAddressMapper.selectOneByExample(example);
            if(memberAddress==null||StringUtils.isEmpty(memberAddress.getId())){
                return ServerResponse.createByErrorMessage("未找到对应的录入信息！");
            }
            returnMap.put("square",house.getSquare());//总面积
            returnMap.put("inputArea",memberAddress.getInputArea());//支付面积
            returnMap.put("needPayArea",house.getSquare().subtract(memberAddress.getInputArea()));//需支付面积

            BudgetOrderDTO orderInfo=orderMapper.getOrderInfoByHouseId(houseId,"4","2");//查询待补差价的订单
            if(orderInfo!=null&&StringUtils.isNotEmpty(orderInfo.getOrderId())){
                String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
                //查询商品信息
                List<BudgetOrderItemDTO> orderItemDTOList=orderMapper.getOrderInfoItemList(orderInfo.getOrderId());
                getProductList(orderItemDTOList,address);
                orderInfo.setOrderDetailList(orderItemDTOList);
                orderInfo.setStorefrontIcon(address+orderInfo.getStorefrontIcon());
                returnMap.putAll(BeanUtils.beanToMap(orderInfo));
            }
            return ServerResponse.createBySuccess("查询成功",returnMap);
        }catch (Exception e){
            logger.error("查询差价订单异常：",e);
            return ServerResponse.createByErrorMessage("查询补差价订单异常");
        }

    }
    /**
     * 查询商品对应的规格详情，品牌，单位信息
     * @param productList
     * @param address
     */
    private  void getProductList(List<BudgetOrderItemDTO> productList, String address){
        if(productList!=null&&productList.size()>0){
            for(BudgetOrderItemDTO ap:productList){
                setProductInfo(ap,address);
            }
        }
    }
    /**
     * 替换对应的信息
     * @param ap
     * @param address
     */
    private  void setProductInfo(BudgetOrderItemDTO ap,String address){
        String productTemplateId=ap.getProductTemplateId();
        DjBasicsProductTemplate pt=iMasterProductTemplateMapper.selectByPrimaryKey(productTemplateId);
        if(pt!=null&& org.apache.commons.lang3.StringUtils.isNotBlank(pt.getId())){
            String image=ap.getImage();
            if (image == null) {
                image=pt.getImage();
            }
            ap.setConvertUnit(pt.getConvertUnit());
            ap.setCost(pt.getCost());
            ap.setCategoryId(pt.getCategoryId());
            if(ap.getStorefrontIcon()!=null&& org.apache.commons.lang3.StringUtils.isNotBlank(ap.getStorefrontIcon())){
                ap.setStorefrontIcon(address+ap.getStorefrontIcon());
            }
            //添加图片详情地址字段
            String[] imgArr = image.split(",");
            //StringBuilder imgStr = new StringBuilder();
            // StringBuilder imgUrlStr = new StringBuilder();
            // StringTool.get.getImages(address, imgArr, imgStr, imgUrlStr);
            if(imgArr!=null&&imgArr.length>0){
                ap.setImageUrl(address+imgArr[0]);//图片详情地址设置
            }

            String unitId=pt.getUnitId();
            //查询单位
            if(pt.getConvertQuality()!=null&&pt.getConvertQuality()>0){
                unitId=pt.getConvertUnit();
            }
            if(unitId!=null&& org.apache.commons.lang3.StringUtils.isNotBlank(unitId)){
                Unit unit= iMasterUnitMapper.selectByPrimaryKey(unitId);
                ap.setUnitId(unitId);
                ap.setUnitName(unit!=null?unit.getName():"");
                ap.setUnitType(unit!=null?unit.getType():2);
            }


            //查询规格名称
            if (org.apache.commons.lang3.StringUtils.isNotBlank(pt.getValueIdArr())) {
                ap.setValueIdArr(pt.getValueIdArr());
                ap.setValueNameArr(masterProductTemplateService.getNewValueNameArr(pt.getValueIdArr()).replaceAll(",", " "));
            }
        }

    }

    /***
     * 设计精算原订单
     * @param userToken
     * @param houseId
     * @return
     */
    public ServerResponse getBudgetOrderById(String userToken,String houseId){
        try{
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Map returnMap=new HashMap();
            //  House house=houseMapper.selectByPrimaryKey(houseId);
            Example example = new Example(MemberAddress.class);
            example.createCriteria().andEqualTo(MemberAddress.HOUSE_ID, houseId);
            MemberAddress memberAddress=iMasterMemberAddressMapper.selectOneByExample(example);
            if(memberAddress!=null&&StringUtils.isNotEmpty(memberAddress.getId())){
                returnMap.put("address",memberAddress.getAddress());
            }
            //判断是否有需要补差价的单
            boolean diffOrder=true;
            BudgetOrderDTO orderDiffInfo=orderMapper.getOrderInfoByHouseId(houseId,"4","2");//查询待补差价的订单
            if(orderDiffInfo==null||StringUtils.isEmpty(orderDiffInfo.getOrderId())){
                diffOrder=false;

            }else{
                returnMap.put("diffOrderId",orderDiffInfo.getOrderId());//补差价订单ID
            }
            BudgetOrderDTO orderInfo=orderMapper.getOrderInfoByHouseId(houseId,"1","3");//查询原订单信息
            if(orderInfo!=null&&StringUtils.isNotEmpty(orderInfo.getOrderId())){
                String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
                //查询商品信息
                List<BudgetOrderItemDTO> orderItemDTOList=orderMapper.getOrderInfoItemList(orderInfo.getOrderId());
                getProductList(orderItemDTOList,address);
                orderInfo.setOrderDetailList(orderItemDTOList);
                orderInfo.setStorefrontIcon(address+orderInfo.getStorefrontIcon());
                returnMap.putAll(BeanUtils.beanToMap(orderInfo));
            }
            returnMap.put("diffOrder",diffOrder);//是否有补差价订单，ture有，false无
            //查询设计师的总费用
            Double totalDesigenMoney=orderMapper.getDesgionTotalMoney(orderInfo.getOrderId());//查询所有的设计费用
            //查询计算量房费用，查询量房费率
            if(totalDesigenMoney!=null&&totalDesigenMoney>0L){
                Config config=iConfigMapper.selectConfigInfoByParamKey("ROOM_RATE_RATIO");//获取对应阶段需处理剩余时间
                BigDecimal roomRateRatio=new BigDecimal(20);//最低面积
                if(config!=null&& org.apache.commons.lang3.StringUtils.isNotBlank(config.getId())){
                    roomRateRatio=new BigDecimal(config.getParamValue());
                }
                returnMap.put("roomRateRatio",roomRateRatio);//量房费率
                returnMap.put("roomCharge",new BigDecimal(totalDesigenMoney).multiply(roomRateRatio).divideToIntegralValue(new BigDecimal(100)));//量房费用
            }
            return ServerResponse.createBySuccess("查询成功",returnMap);
        }catch (Exception e){
            logger.error("查询设计、精算原订单异常",e);
            return ServerResponse.createByErrorMessage("查询设计、精算原订单异常");
        }
    }

    /**
     * 设计精算，退原订单ID,取消补差价订单ID
     * @param userToken 用户TOKEN
     * @param houseId 房子ID
     * @param orderId 订单ID
     * @param diffOrderId 补差价订单ID
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse refundBudgetOrderInfo(String userToken,String houseId,String orderId,String diffOrderId){
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        House house=houseMapper.selectByPrimaryKey(houseId);
        //1.查询原订单的商品信息
        List<BudgetOrderItemDTO> orderItemDTOList=orderMapper.getOrderInfoItemList(orderId);
        //2.量房扣减费用
        //查询设计师的总费用
        Double totalDesigenMoney=orderMapper.getDesgionTotalMoney(orderId);//查询所有的设计费用
        //查询需支除的量房费用
        BigDecimal roomCharge=new BigDecimal(0);
        if(totalDesigenMoney!=null&&totalDesigenMoney>0L){
            Config config=iConfigMapper.selectConfigInfoByParamKey("ROOM_RATE_RATIO");//获取对应阶段需处理剩余时间
            BigDecimal roomRateRatio=new BigDecimal(20);//最低面积
            if(config!=null&& org.apache.commons.lang3.StringUtils.isNotBlank(config.getId())){
                roomRateRatio=new BigDecimal(config.getParamValue());
            }
            roomCharge=new BigDecimal(totalDesigenMoney).multiply(roomRateRatio).divideToIntegralValue(new BigDecimal(100));//量房费用
        }
        //转换成符合条件的退款单
        String productStr=getAccordWithProduct(orderItemDTOList);
        String repairMendOrderId="";
        if(productStr!=null&& org.apache.commons.lang3.StringUtils.isNotBlank(productStr)){
            //自动生成退款单，且退款同意
            repairMendOrderId=  repairMendOrderService.saveRefundInfoRecord(house.getCityId(),houseId,orderId,productStr,roomCharge);
            //3.将量房费用给到设计师
            if(roomCharge.doubleValue()>0){//如果有量房费用，则需给设计师增加对应的量房工钱
                repairMendOrderService.settleMemberMoney(houseId,roomCharge);
            }
            //4.修改补差价订单状态为已取消
            Order order=orderMapper.selectByPrimaryKey(diffOrderId);
            if(order.getOrderSource()==4){
                order.setOrderStatus("5");
                order.setModifyDate(new Date());
                orderMapper.updateByPrimaryKeySelective(order);
                //5.查询支付差价的订单，将其状态改为已取消
                Example example=new Example(BusinessOrder.class);
                example.createCriteria().andEqualTo(BusinessOrder.NUMBER,order.getBusinessOrderNumber());
                BusinessOrder businessOrder=businessOrderMapper.selectOneByExample(example);
                businessOrder.setState(4);//已取消
                businessOrder.setModifyDate(new Date());
                businessOrderMapper.updateByPrimaryKeySelective(businessOrder);
                //修改补差价任务为已处理（查贸易符合条件补差价订单信息
                TaskStack taskStack=taskStackService.selectTaskStackByData(houseId,7,order.getBusinessOrderNumber());
                if(taskStack!=null&&StringUtils.isNotEmpty(taskStack.getId())){
                    taskStack.setState(1);
                    taskStack.setModifyDate(new Date());
                    taskStackService.updateTaskStackInfo(taskStack);
                }
            }
        }else{
            return ServerResponse.createByErrorMessage("未找到符合条件的退款信息");
        }

        return ServerResponse.createBySuccess("退款成功",repairMendOrderId);
    }

    private String getAccordWithProduct(List<BudgetOrderItemDTO> orderItemDTOList){

        JSONArray listOfGoods=new JSONArray();
        if(orderItemDTOList!=null&&orderItemDTOList.size()>0){
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            for(BudgetOrderItemDTO product:orderItemDTOList){
                JSONObject jsonObject = new JSONObject();
                String productId = product.getProductId();
                if(productId==null|| org.apache.commons.lang3.StringUtils.isBlank(productId)){
                    continue;
                }
                String orderItemId=product.getOrderItemId();
                //查询增值商品信息
                String addedProductIds=iMasterDeliverOrderAddedProductMapper.getAddedPrdouctStr(orderItemId);
                //退款订单信息
                jsonObject.put("returnCount",product.getSurplusCount());//退订单的可退面积
                jsonObject.put("productId",productId);
                jsonObject.put("orderItemId",orderItemId);
                jsonObject.put("addedProductIds",addedProductIds); //增值订单ID，多个用逗号分隔
                listOfGoods.add(jsonObject);
            }
        }
        if(listOfGoods!=null&&listOfGoods.size()>0){
            return listOfGoods.toJSONString();
        }
        return "";
    }

    /**
     * 设计图纸不合格--审核设计图提交
     * @param userToken 用户token
     * @param houseId 房子ID
     * @param taskId 任务ID
     * @param type 类型：1当家平台设计，2平台外设计，3结束精算
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse saveDesignDrawingReview(String userToken,String houseId,String taskId,Integer type){
        logger.info("审核设计图提交，taskId={},type={},houseId={}",taskId,type,houseId);
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member member = (Member) object;
        //判断当前任务是否已完成
        TaskStack taskStack=taskStackService.selectTaskStackById(taskId);
        if(taskStack!=null&&taskStack.getState()==1){
            return ServerResponse.createByErrorMessage("此任务已审核完成，请勿重复提交。");
        }
        House house=houseMapper.selectByPrimaryKey(houseId);
        if(type==2){//平台外设计
            //1.修改精算师的审核状态为待审核
            house.setBudgetOk(1);
            house.setModifyDate(new Date());
            houseMapper.updateByPrimaryKeySelective(house);

        }else if(type==3){//结束精算
            //1.将当前精算订单结束掉，生成退货单
            Example example=new Example(Order.class);
            example.createCriteria().andEqualTo(Order.BUSINESS_ORDER_NUMBER,taskStack.getData());
            Order order=orderMapper.selectOneByExample(example);
            //查询原订单的商品信息
            List<BudgetOrderItemDTO> orderItemDTOList=orderMapper.getOrderInfoItemList(order.getId());
            //转换成符合条件的退款单
            String productStr=getAccordWithProduct(orderItemDTOList);
            if(productStr!=null&& org.apache.commons.lang3.StringUtils.isNotBlank(productStr)) {
                //自动生成退款单，且退款同意
                repairMendOrderService.saveRefundInfoRecord(house.getCityId(), houseId, order.getId(), productStr, new BigDecimal(0));
            }
        }
        //2.判断是否有已待支付的订单，若有，则改为取消状态
        if(StringUtils.isNotEmpty(taskStack.getData())&&!houseId.equals(taskStack.getData())){
            //取消订单
            Example example=new Example(Order.class);
            example.createCriteria().andEqualTo(Order.BUSINESS_ORDER_NUMBER,taskStack.getData());
            Order order=new Order();
            order.setCreateDate(null);
            order.setId(null);
            order.setOrderStatus("5");
            orderMapper.updateByExampleSelective(order,example);
            //取消待支付业务订单
            example=new Example(BusinessOrder.class);
            example.createCriteria().andEqualTo(BusinessOrder.NUMBER,taskStack.getData());
            BusinessOrder businessOrder=businessOrderMapper.selectOneByExample(example);
            businessOrder.setState(4);
            businessOrder.setModifyDate(new Date());
            businessOrderMapper.updateByPrimaryKeySelective(businessOrder);
        }
        //3.修改任务的状态为已完成
        taskStack.setState(1);
        taskStack.setModifyDate(new Date());
        taskStackService.updateTaskStackInfo(taskStack);
        return ServerResponse.createBySuccessMessage("提交成功");
    }

    /**
     * 提交设计师订单
     * @param userToken
     * @param houseId 房子ID
     * @param taskId 任务ID
     * @param productArr 商品列表
     * @return
     */
    public ServerResponse saveDesignOrderInfo(String userToken,String houseId,String taskId,String productArr){
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member member = (Member) object;
        //判断当前任务是否已完成
        TaskStack taskStack=taskStackService.selectTaskStackById(taskId);
        if(taskStack!=null&&taskStack.getState()==1){
            return ServerResponse.createByErrorMessage("此任务已审核完成，请勿重复提交。");
        }
        House house=houseMapper.selectByPrimaryKey(houseId);

        Example example=new Example(MemberAddress.class);
        example.createCriteria().andEqualTo(MemberAddress.HOUSE_ID,houseId);
        MemberAddress memberAddress=iMasterMemberAddressMapper.selectOneByExample(example);
        String addressId="";
        if(memberAddress!=null&&StringUtils.isNotEmpty(memberAddress.getId())){
            addressId=memberAddress.getId();
        }
        String productJsons = getNewProductJsons(house,productArr);
        if(StringUtils.isNotEmpty(productJsons)){
            //2.生成订单信息
            ServerResponse serverResponse = paymentService.generateOrderCommon(member, house.getId(), house.getCityId(), productJsons, null, addressId, 1,"1");
            if (serverResponse.getResultObj() != null) {
                String obj = serverResponse.getResultObj().toString();//获取对应的支付单号码
                //3.生成houseflow待抢单的流程(设计师的待创单流程)
                WorkerType workerType = workerTypeMapper.selectByPrimaryKey("1");
                example = new Example(HouseFlow.class);
                example.createCriteria()
                        .andEqualTo(HouseFlow.HOUSE_ID, house.getHouseId())
                        .andEqualTo(HouseFlow.WORKER_TYPE_ID, workerType.getId());
                List<HouseFlow> houseFlowList = houseFlowMapper.selectByExample(example);
                HouseFlow houseFlow;
                if (houseFlowList.size() == 1) {
                    houseFlow = houseFlowList.get(0);
                    houseFlow.setState(workerType.getState());
                    houseFlow.setSort(workerType.getSort());
                    houseFlow.setWorkType(1);//开始设计等待业主支付
                    houseFlow.setModifyDate(new Date());
                    houseFlow.setPayStatus(0);
                    houseFlow.setCityId(house.getCityId());
                    houseFlowMapper.updateByPrimaryKeySelective(houseFlow);
                } else {
                    houseFlow = new HouseFlow(true);
                    houseFlow.setCityId(house.getCityId());
                    houseFlow.setWorkerTypeId(workerType.getId());
                    houseFlow.setWorkerType(workerType.getType());
                    houseFlow.setHouseId(house.getId());
                    houseFlow.setState(workerType.getState());
                    houseFlow.setSort(workerType.getSort());
                    houseFlow.setWorkType(1);//开始设计等待业主支付
                    houseFlow.setModifyDate(new Date());
                    houseFlow.setCityId(house.getCityId());
                    houseFlow.setPayStatus(0);
                    houseFlowMapper.insert(houseFlow);
                }
                //取消旧的待提交订单信息
                if(StringUtils.isNotEmpty(taskStack.getData())&&!houseId.equals(taskStack.getData())){
                    //取消订单
                    example=new Example(Order.class);
                    example.createCriteria().andEqualTo(Order.BUSINESS_ORDER_NUMBER,taskStack.getData());
                    Order order=new Order();
                    order.setCreateDate(null);
                    order.setId(null);
                    order.setOrderStatus("5");
                    orderMapper.updateByExampleSelective(order,example);
                    //取消待支付业务订单
                    example=new Example(BusinessOrder.class);
                    example.createCriteria().andEqualTo(BusinessOrder.NUMBER,taskStack.getData());
                    BusinessOrder businessOrder=businessOrderMapper.selectOneByExample(example);
                    businessOrder.setState(4);
                    businessOrder.setModifyDate(new Date());
                    businessOrderMapper.updateByPrimaryKeySelective(businessOrder);
                }
                //4.修改任务的data字段为订单字段
                taskStack.setData(obj);
                taskStack.setModifyDate(new Date());
                taskStackService.updateTaskStackInfo(taskStack);
                return ServerResponse.createBySuccess("提交成功",obj);
            }
        }
        return ServerResponse.createByErrorMessage("提交失败,未找到符合条件的商品信息");
    }
    //生成设计师订单
    public String getNewProductJsons(House house,String productArr){
        //查询推荐商品列表
        JSONArray actuarialDesignList = JSONArray.parseArray(productArr);
        if(actuarialDesignList!=null&&actuarialDesignList.size()>0){
            //生成设计师订单,获取符合条件的商品
            JSONArray listOfGoods = new JSONArray();
            for (int i = 0; i < actuarialDesignList.size(); i++) {
                JSONObject productObj = (JSONObject) actuarialDesignList.get(i);
                JSONObject jsonObject = new JSONObject();
                String productId = productObj.getString("productId");
                if (productId == null || org.apache.commons.lang3.StringUtils.isBlank(productId)) {
                    continue;
                }
                String productTemplateId = productObj.getString("productTemplateId");
                jsonObject.put("shopCount", 1);
                //查询是否按面积计算价格
                Example example=new Example(DjActuarialTemplateConfig.class);
                example.createCriteria().andEqualTo(DjActuarialTemplateConfig.SERVICE_TYPE_ID,house.getHouseType())
                        .andEqualTo(DjActuarialTemplateConfig.CONFIG_TYPE,1);
                DjActuarialTemplateConfig djActuarialTemplateConfig=iMasterActuarialTemplateConfigMapper.selectOneByExample(example);

                example = new Example(DjActuarialProductConfig.class);
                example.createCriteria().andEqualTo(DjActuarialProductConfig.WORKER_TYPE_ID, 1)
                        .andEqualTo(DjActuarialProductConfig.ACTUARIAL_TEMPLATE_ID,djActuarialTemplateConfig.getId())
                        .andEqualTo(DjActuarialProductConfig.PRODUCT_ID, productId);
                DjActuarialProductConfig djActuarialProductConfig = iMasterActuarialProductConfigMapper.selectOneByExample(example);
                if (djActuarialProductConfig != null && "1".equals(djActuarialProductConfig.getIsCalculatedArea())) {
                    jsonObject.put("shopCount", house.getSquare());//按房子面积计算
                }
                //查询对应的商品信息，按价格取最低价的商品
                StorefrontProductDTO storefrontProductDTO=masterProductTemplateService.getStorefrontProductByTemplateId(productTemplateId);
                jsonObject.put("productId", storefrontProductDTO.getStorefrontId());
                jsonObject.put("workerTypeId", 1);
                listOfGoods.add(jsonObject);
            }
            return listOfGoods.toJSONString();
        }
        return "";
    }

    /**
     *  获取符合条件的精算师订单
     * @param house
     * @return
     */
    public String getBudgetProductJsons(House house){
        try{
            //查询推荐商品列表
            Example example=new Example(DjActuarialTemplateConfig.class);
            example.createCriteria().andEqualTo(DjActuarialTemplateConfig.SERVICE_TYPE_ID,house.getHouseType())
                    .andEqualTo(DjActuarialTemplateConfig.CONFIG_TYPE,2);
            DjActuarialTemplateConfig djActuarialTemplateConfig=iMasterActuarialTemplateConfigMapper.selectOneByExample(example);

            example = new Example(DjActuarialProductConfig.class);
            example.createCriteria().andEqualTo(DjActuarialProductConfig.WORKER_TYPE_ID, 2)
                    .andEqualTo(DjActuarialProductConfig.ACTUARIAL_TEMPLATE_ID,djActuarialTemplateConfig.getId())
                    .andEqualTo(DjActuarialProductConfig.DEFAULT_RECOMMEND, "1");//查询默认推荐的精算商品
            List<DjActuarialProductConfig> productConfigList=iMasterActuarialProductConfigMapper.selectByExample(example);
            if(productConfigList!=null&&productConfigList.size()>0){
                JSONArray listOfGoods = new JSONArray();
                for(DjActuarialProductConfig product:productConfigList){
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("shopCount", 1);
                    //查询是否按面积计算价格
                    if ("1".equals(product.getIsCalculatedArea())) {
                        jsonObject.put("shopCount", house.getSquare());//按房子面积计算
                    }
                    //查询对应的商品信息，按价格取最低价的商品
                    StorefrontProductDTO storefrontProductDTO=masterProductTemplateService.getStorefrontProductByTemplateId(product.getProductId());
                    jsonObject.put("productId", storefrontProductDTO.getStorefrontId());
                    jsonObject.put("workerTypeId", 2);
                    listOfGoods.add(jsonObject);
                }
                return listOfGoods.toJSONString();
            }
        }catch (Exception e){
            logger.error("查询精算商品失败：",e);
        }

        return "";
    }


    /**
     * 体验单验收
     * @param userToken 当前token
     * @param orderItemId 子单ID
     * @param remark 备注
     * @param images 图片，多个逗号分隔
     * @param orderStatus 状态 4=已上传验收  5=结束
     * @return
     */
    public ServerResponse checkExperience(String userToken, String orderItemId,String remark,String images,String orderStatus) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = (Member) object;

            OrderItem orderItem = orderItemMapper.selectByPrimaryKey(orderItemId);
            if(!CommonUtil.isEmpty(images)){
                orderItem.setImages(images);
                orderItem.setRemark(remark);
                orderItem.setModifyDate(new Date());
                orderItemMapper.updateByPrimaryKey(orderItem);
            }

            Order order=orderMapper.selectByPrimaryKey(orderItem.getOrderId());
            order.setOrderStatus(orderStatus);
            order.setUpdateBy(member.getId());
            order.setModifyDate(new Date());
            orderMapper.updateByPrimaryKey(order);

            Example example = new Example(HouseWorker.class);
            example.createCriteria().andEqualTo(HouseWorker.WORK_TYPE,6).andEqualTo(HouseWorker.TYPE,1).andEqualTo(HouseWorker.BUSINESS_ID,order.getId());
            List<HouseWorker> houseWorkers= houseWorkerMapper.selectByExample(example);
            HouseWorker houseWorker=null;
            if(houseWorkers.size()>0) {
                houseWorker = houseWorkers.get(0);
                houseWorker.setWorkType(8);
                houseWorker.setModifyDate(new Date());
                houseWorkerMapper.updateByPrimaryKeySelective(houseWorker);
            }
            return ServerResponse.createBySuccess("体验单处理完成");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("体验单处理异常");
        }

    }
}
