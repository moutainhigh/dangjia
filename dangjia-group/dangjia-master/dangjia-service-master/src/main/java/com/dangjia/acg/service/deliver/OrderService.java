package com.dangjia.acg.service.deliver;

import com.alibaba.fastjson.JSON;
import com.dangjia.acg.api.StorefrontConfigAPI;
import com.dangjia.acg.api.data.ForMasterAPI;
import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.enums.AppType;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.deliver.BusinessOrderDTO;
import com.dangjia.acg.dto.deliver.ItemDTO;
import com.dangjia.acg.dto.deliver.OrderDTO;
import com.dangjia.acg.dto.deliver.OrderItemDTO;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.delivery.*;
import com.dangjia.acg.mapper.house.IHouseDistributionMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.house.IWarehouseDetailMapper;
import com.dangjia.acg.mapper.house.IWarehouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.pay.IBusinessOrderMapper;
import com.dangjia.acg.mapper.product.IMasterStorefrontProductMapper;
import com.dangjia.acg.mapper.repair.IMendMaterialMapper;
import com.dangjia.acg.mapper.repair.IMendOrderMapper;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.deliver.*;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.house.HouseDistribution;
import com.dangjia.acg.modle.house.Warehouse;
import com.dangjia.acg.modle.house.WarehouseDetail;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.pay.BusinessOrder;
import com.dangjia.acg.modle.product.BasicsGoods;
import com.dangjia.acg.modle.product.DjBasicsProductTemplate;
import com.dangjia.acg.modle.repair.MendMateriel;
import com.dangjia.acg.modle.repair.MendOrder;
import com.dangjia.acg.modle.storefront.StorefrontProduct;
import com.dangjia.acg.service.acquisition.MasterCostAcquisitionService;
import com.dangjia.acg.service.config.ConfigMessageService;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.dangjia.acg.service.repair.MendOrderService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import java.math.BigDecimal;
import java.util.*;

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
    private IHouseMapper houseMapper;
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
    private ForMasterAPI forMasterAPI;
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

    /**
     * 删除订单
     */
    public ServerResponse delBusinessOrderById( String userToken, String orderId) {
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
            Example  orderexample=new Example(Order.class);
            orderexample.createCriteria().andEqualTo(Order.ID,orderId).andEqualTo(Order.MEMBER_ID,member.getId());
            Order order1=new Order();
            order1.setDataStatus(1);
            orderMapper.updateByExampleSelective(order1,orderexample);
            //订单详情删除

            Example  orderItemexample=new Example(OrderItem.class);
            orderItemexample.createCriteria().andEqualTo(Order.ID,orderId);
            OrderItem orderItem=new OrderItem();
            orderItem.setDataStatus(1);
            orderItemMapper.updateByExampleSelective(orderItem,orderItemexample);

            List<OrderItem> OrderItemList=orderItemMapper.selectByExample(orderItemexample);
            for(OrderItem OrderItem:OrderItemList)
            {
                //要货单以及要货单详情表删除 orderSplitMapper  orderSplitItemMapper
                String id=OrderItem.getId();
                Example  OrderSplitItemexample=new Example(OrderSplitItem.class);
                OrderSplitItemexample.createCriteria().andEqualTo(OrderSplitItem.ORDER_ITEM_ID,id);
                OrderSplitItem orderSplitItem=new OrderSplitItem();
                orderSplitItem.setDataStatus(1);
                orderSplitItemMapper.updateByExampleSelective(orderSplitItem,OrderSplitItemexample);
                orderSplitItem=orderSplitItemMapper.selectByPrimaryKey(id);
                //发货单删除
                String splitItemId=orderSplitItem.getId();
                Example  splitDeliverExample=new Example(SplitDeliver.class);
                splitDeliverExample.createCriteria().andEqualTo(SplitDeliver.NUMBER,splitItemId);
                SplitDeliver splitDeliver=new SplitDeliver();
                splitDeliver.setDataStatus(1);
                splitDeliverMapper.updateByExampleSelective(splitDeliver,splitDeliverExample);
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
                itemDTO.setName(house.getStyle());
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
                List<OrderDTO> orderDTOList = this.orderDTOList(businessOrder.getNumber(), house.getStyle());
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
     * 根据订单状态查询订单列表
     * @param pageDTO
     * @param userToken
     * @param houseId
     * @param queryId
     * @param orderStatus
     * @return
     */
    public ServerResponse queryBusinessOrderListByStatus(PageDTO pageDTO, String userToken, String houseId, String queryId, String orderStatus) {
        try {
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
                List<OrderDTO> orderDTOList = this.orderDTOList(businessOrder.getNumber(), house == null ? "" : house.getStyle(),orderStatus);
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
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("失败");
        }
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
            List<OrderDTO> orderDTOList = this.orderDTOList(businessOrder.getNumber(), house == null ? "" : house.getStyle());
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
    private List<OrderDTO> orderDTOList(String businessOrderNumber, String style,String orderStatus) {
        String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        List<OrderDTO> orderDTOList = new ArrayList<>();
        List<Order> orderList = orderMapper.byBusinessOrderNumberAndOrderStatus(businessOrderNumber,orderStatus);
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
     */
    public ServerResponse confirmOrderSplit(String userToken, String houseId) {
        try {
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
                example = new Example(OrderSplitItem.class);
                example.createCriteria().andEqualTo(OrderSplitItem.ORDER_SPLIT_ID, orderSplit.getId());
                List<OrderSplitItem> orderSplitItemList = orderSplitItemMapper.selectByExample(example);
                for (OrderSplitItem orderSplitItem : orderSplitItemList) {
                    Warehouse warehouse = warehouseMapper.getByProductId(orderSplitItem.getProductId(), orderSplit.getHouseId());
                    warehouse.setAskCount(warehouse.getAskCount() + orderSplitItem.getNum());//更新仓库已要总数
                    warehouse.setAskTime(warehouse.getAskTime() + 1);//更新该货品被要次数
                    warehouseMapper.updateByPrimaryKeySelective(warehouse);
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
                if (worker.getWorkerType() == 3) {
                    configMessageService.addConfigMessage(null, AppType.ZHUANGXIU, house.getMemberId(), "0", "大管家要服务",
                            String.format(DjConstants.PushMessage.STEWARD_Y_SERVER, house.getHouseName()), "");
                } else {
                    configMessageService.addConfigMessage(null, AppType.ZHUANGXIU, house.getMemberId(), "0", "工匠要材料", String.format
                            (DjConstants.PushMessage.CRAFTSMAN_Y_MATERIAL, house.getHouseName()), "");
                }
                return ServerResponse.createBySuccessMessage("操作成功");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("失败");
        }
    }


    /**
     * 补货提交订单接口
     * @param userToken
     * @param houseId
     * @return
     */
    public ServerResponse abrufbildungSubmitOrder(String userToken,String cityId, String houseId,
                                                  String mendOrderId, String addressId) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = (Member) object;
            MendOrder mendOrder = mendOrderMapper.selectByPrimaryKey(mendOrderId);
            Member worker = iMemberMapper.selectByPrimaryKey(mendOrder.getApplyMemberId());
            WorkerType workerType = workerTypeMapper.selectByPrimaryKey(worker.getWorkerTypeId());
            Order order = new Order();
            order.setHouseId(houseId);
            order.setTotalAmount(new BigDecimal(mendOrder.getTotalAmount()));
            order.setWorkerTypeName(workerType.getName());
            order.setWorkerTypeId(workerType.getId());
            order.setType(mendOrder.getType());
            order.setDataStatus(0);
            order.setOrderNumber(System.currentTimeMillis() + "-" + (int) (Math.random() * 9000 + 1000));
            order.setMemberId(member.getId());
            order.setWorkerId(worker.getId());
            order.setCityId(cityId);
            order.setTotalDiscountPrice(new BigDecimal(0));
            order.setTotalStevedorageCost(new BigDecimal(0));
            order.setTotalTransportationCost(new BigDecimal(0));
            order.setActualPaymentPrice(new BigDecimal(0));
            order.setOrderType("2");
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
            Example example=new Example(MendMateriel.class);
            example.createCriteria().andEqualTo(MendMateriel.MEND_ORDER_ID,mendOrderId);
            List<MendMateriel> mendMateriels = mendMaterialMapper.selectByExample(example);
            for (MendMateriel mendMateriel : mendMateriels) {
                BigDecimal totalMaterialPrice = new BigDecimal(0);//组总价
                BigDecimal totalPrice = new BigDecimal(mendMateriel.getPrice()*mendMateriel.getShopCount());
                if(mendMateriel.getProductType()==0) {
                    totalMaterialPrice = totalMaterialPrice.add(totalPrice);
                }
                paymentPrice = paymentPrice.add(totalPrice);
                Double freight=storefrontConfigAPI.getFreightPrice(mendMateriel.getStorefrontId(),totalMaterialPrice.doubleValue());
                freightPrice=freightPrice.add(new BigDecimal(freight));
                //搬运费运算
                Double moveDost=masterCostAcquisitionService.getStevedorageCost(houseId,mendMateriel.getProductId(),mendMateriel.getShopCount());
                totalMoveDost=totalMoveDost.add(new BigDecimal(moveDost));
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
            return ServerResponse.createBySuccess("提交成功", order.getId());
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ServerResponse.createByErrorMessage("提交失败：原因："+e.getMessage());
        }
    }



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
                List<Map<String,Object>> resMapList=new ArrayList<>();
                for (OrderSplitItem v : orderSplitItemList) {
                    v.initPath(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class));
                    //增加当前仓库商品的购买量和剩余量字段的显示
                    example = new Example(Warehouse.class);
                    example.createCriteria().andEqualTo(Warehouse.HOUSE_ID, houseId).andEqualTo(Warehouse.PRODUCT_ID, v.getProductId());
                    List<Warehouse> warehouseList = warehouseMapper.selectByExample(example);
                    Map orderSplitMap= BeanUtils.beanToMap(v);
                    if (warehouseList.size() > 0) {
                        Warehouse warehouse = warehouseList.get(0);
                        orderSplitMap.put("shopCount",warehouse.getShopCount());//购买量
                        orderSplitMap.put("surCount",warehouse.getShopCount() - (warehouse.getOwnerBack() == null ? 0D : warehouse.getOwnerBack()) - warehouse.getAskCount());
                    }else{
                        orderSplitMap.put("shopCount",0D);//购买量
                        orderSplitMap.put("surCount",0D);//剩余量
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
                orderSplit.setMemberId(worker.getId());
                orderSplit.setMemberName(worker.getName());
                orderSplit.setMobile(worker.getMobile());
                orderSplit.setWorkerTypeId(worker.getWorkerTypeId());
                orderSplitMapper.updateByPrimaryKeySelective(orderSplit);
            } else {
                example = new Example(OrderSplit.class);
                orderSplit = new OrderSplit();
                orderSplit.setNumber("DJ" + 200000 + orderSplitMapper.selectCountByExample(example));//要货单号
                orderSplit.setHouseId(houseId);
                orderSplit.setApplyStatus(0);//后台审核状态：0生成中, 1申请中, 2通过, 3不通过, 4业主待支付补货材料 后台(材料员)
                orderSplit.setMemberId(worker.getId());
                orderSplit.setMemberName(worker.getName());
                orderSplit.setMobile(worker.getMobile());
                orderSplit.setWorkerTypeId(worker.getWorkerTypeId());
                orderSplitMapper.insert(orderSplit);
            }

            //获取要货购物车数据
            List<Cart> cartList = cartMapper.cartList(houseId, worker.getWorkerTypeId(), worker.getId());
            List<Map<String, Object>> productList = new ArrayList<>();
            for (Cart aCartList : cartList) {
                Double num = aCartList.getShopCount();
                String productId = aCartList.getProductId();
                Warehouse warehouse = warehouseMapper.getByProductId(productId, houseId);//定位到仓库id
                //增加店铺概念 现在cart productId为店铺商品表主键id
                StorefrontProduct storefrontProduct = iMasterStorefrontProductMapper.selectByPrimaryKey(productId);
                DjBasicsProductTemplate product = forMasterAPI.getProduct(house.getCityId(), storefrontProduct.getProdTemplateId());
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
                    BasicsGoods goods = forMasterAPI.getGoods(house.getCityId(), product.getGoodsId());
                    orderSplitItem.setOrderSplitId(orderSplit.getId());
                    orderSplitItem.setProductId(aCartList.getProductId());
                    orderSplitItem.setProductSn(aCartList.getProductSn());
                    orderSplitItem.setProductName(aCartList.getProductName());
                    orderSplitItem.setPrice(storefrontProduct.getSellPrice());
                    orderSplitItem.setAskCount(0d);
                    orderSplitItem.setCost(product.getCost());
                    orderSplitItem.setShopCount(0d);
                    orderSplitItem.setNum(num);
                    orderSplitItem.setUnitName(forMasterAPI.getUnitName(house.getCityId(), product.getConvertUnit()));
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
            }

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

}
