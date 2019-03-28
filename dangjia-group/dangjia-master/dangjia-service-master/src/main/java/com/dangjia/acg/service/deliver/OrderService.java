package com.dangjia.acg.service.deliver;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.deliver.BusinessOrderDTO;
import com.dangjia.acg.dto.deliver.ItemDTO;
import com.dangjia.acg.dto.deliver.OrderDTO;
import com.dangjia.acg.dto.deliver.OrderItemDTO;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.deliver.IOrderItemMapper;
import com.dangjia.acg.mapper.deliver.IOrderMapper;
import com.dangjia.acg.mapper.deliver.IOrderSplitItemMapper;
import com.dangjia.acg.mapper.deliver.IOrderSplitMapper;
import com.dangjia.acg.mapper.design.IDesignImageTypeMapper;
import com.dangjia.acg.mapper.design.IHouseDesignImageMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.house.IWarehouseDetailMapper;
import com.dangjia.acg.mapper.house.IWarehouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.pay.IBusinessOrderMapper;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.deliver.Order;
import com.dangjia.acg.modle.deliver.OrderItem;
import com.dangjia.acg.modle.deliver.OrderSplit;
import com.dangjia.acg.modle.deliver.OrderSplitItem;
import com.dangjia.acg.modle.design.DesignImageType;
import com.dangjia.acg.modle.design.HouseDesignImage;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.house.Warehouse;
import com.dangjia.acg.modle.house.WarehouseDetail;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.pay.BusinessOrder;
import com.dangjia.acg.service.config.ConfigMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 要货
 * Date: 2018/11/8 0008
 * Time: 11:48
 */
@Service
public class OrderService {
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IMemberMapper memberMapper;
    @Autowired
    private IOrderSplitMapper orderSplitMapper;
    @Autowired
    private IOrderSplitItemMapper orderSplitItemMapper;
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
    private IHouseDesignImageMapper houseDesignImageMapper;
    @Autowired
    private IDesignImageTypeMapper designImageTypeMapper;
    @Autowired
    private ConfigMessageService configMessageService;
    @Autowired
    private IWorkerTypeMapper workerTypeMapper;

    /**
     * 订单详情
     */
    public ServerResponse orderDetail(String orderId){
        try{
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            Order order = orderMapper.selectByPrimaryKey(orderId);
            House house = houseMapper.selectByPrimaryKey(order.getHouseId());
            OrderItemDTO orderItemDTO = new OrderItemDTO();
            orderItemDTO.setOrderId(order.getId());
            orderItemDTO.setTotalAmount(order.getTotalAmount());

            List<ItemDTO> itemDTOList = new ArrayList<>();
            if(order.getWorkerTypeId().equals("1")){//设计
                ItemDTO itemDTO = new ItemDTO();
                itemDTO.setName(house.getStyle());
                itemDTO.setImage(address + "icon/shejiF.png");
                itemDTO.setPrice("¥" + String.format("%.2f",order.getStylePrice().doubleValue())+"/㎡");
                itemDTO.setShopCount(house.getSquare().doubleValue());
                itemDTO.setProductType(3);
                itemDTOList.add(itemDTO);

                List<HouseDesignImage> houseDesignImageList = houseDesignImageMapper.byNumber(order.getHouseId(),order.getBusinessOrderNumber());
                for (HouseDesignImage houseDesignImage : houseDesignImageList){
                    DesignImageType designImageType = designImageTypeMapper.selectByPrimaryKey(houseDesignImage.getDesignImageTypeId());
                    itemDTO = new ItemDTO();
                    itemDTO.setName(designImageType.getName());  //设计图名字
                    itemDTO.setImage(address + houseDesignImage.getImageurl());
                    itemDTO.setPrice("¥" + String.format("%.2f",designImageType.getPrice().doubleValue()));
                    itemDTO.setShopCount(1.0);
                    itemDTO.setProductType(3);
                    itemDTOList.add(itemDTO);
                }
            }else if (order.getWorkerTypeId().equals("2")){
                ItemDTO itemDTO = new ItemDTO();
                itemDTO.setName("当家精算");
                itemDTO.setImage(address + "icon/jingsuanF.png");
                itemDTO.setPrice("¥" + String.format("%.2f",order.getBudgetCost().doubleValue())+"/㎡");
                itemDTO.setShopCount(house.getSquare().doubleValue());
                itemDTO.setProductType(3);
                itemDTOList.add(itemDTO);
            }else {
                List<OrderItem> orderItemList = orderItemMapper.byOrderIdList(orderId);
                for(OrderItem orderItem : orderItemList){
                    ItemDTO itemDTO = new ItemDTO();
                    itemDTO.setImage(address + orderItem.getImage());
                    itemDTO.setPrice("¥" + String.format("%.2f",orderItem.getPrice()));
                    itemDTO.setShopCount(orderItem.getShopCount());
                    if (order.getType() == 1){//人工
                        itemDTO.setName(orderItem.getWorkerGoodsName());
                        itemDTO.setProductType(2);//人工
                    }else if(order.getType() == 2){//材料
                        itemDTO.setName(orderItem.getProductName());
                        itemDTO.setProductType(orderItem.getProductType());
                    }
                    itemDTOList.add(itemDTO);
                }
            }

            orderItemDTO.setItemDTOList(itemDTOList);
            return ServerResponse.createBySuccess("查询成功", orderItemDTO);
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 订单详情
     */
    public ServerResponse orderList(String businessOrderId){
        try{
            BusinessOrder businessOrder = businessOrderMapper.selectByPrimaryKey(businessOrderId);
            House house = houseMapper.selectByPrimaryKey(businessOrder.getHouseId());
            BusinessOrderDTO businessOrderDTO = new BusinessOrderDTO();
            businessOrderDTO.setHouseName(house.getHouseName());
            businessOrderDTO.setCreateDate(businessOrder.getCreateDate());
            businessOrderDTO.setNumber(businessOrder.getNumber());
            List<OrderDTO> orderDTOList = this.orderDTOList(businessOrder.getNumber(),house.getStyle());
            businessOrderDTO.setOrderDTOList(orderDTOList);
            businessOrderDTO.setTotalPrice(businessOrder.getTotalPrice());
            businessOrderDTO.setDiscountsPrice(businessOrder.getDiscountsPrice());
            businessOrderDTO.setPayPrice(businessOrder.getPayPrice());
            businessOrderDTO.setCarriage(0.0);//运费

            return ServerResponse.createBySuccess("查询成功", businessOrderDTO);
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 业务订单列表
     */
    public ServerResponse businessOrderList(String userToken){
        try{
            AccessToken accessToken = redisClient.getCache(userToken+ Constants.SESSIONUSERID,AccessToken.class);
            Member member = accessToken.getMember();

            List<BusinessOrder> businessOrderList = businessOrderMapper.byMemberId(member.getId());
            List<BusinessOrderDTO> businessOrderDTOS = new ArrayList<>();
            for (BusinessOrder businessOrder : businessOrderList){
                House house = houseMapper.selectByPrimaryKey(businessOrder.getHouseId());
                BusinessOrderDTO businessOrderDTO = new BusinessOrderDTO();
                businessOrderDTO.setBusinessOrderId(businessOrder.getId());
                businessOrderDTO.setHouseName(house.getHouseName());
                businessOrderDTO.setCreateDate(businessOrder.getCreateDate());
                businessOrderDTO.setNumber(businessOrder.getNumber());
                List<OrderDTO> orderDTOList = this.orderDTOList(businessOrder.getNumber(),house.getStyle());
                businessOrderDTO.setOrderDTOList(orderDTOList);
                businessOrderDTO.setPayPrice(businessOrder.getPayPrice());
                businessOrderDTOS.add(businessOrderDTO);
            }

            return ServerResponse.createBySuccess("查询成功",businessOrderDTOS);
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }
    /*订单流水*/
    private List<OrderDTO> orderDTOList(String businessOrderNumber,String style){
        String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        List<OrderDTO> orderDTOList = new ArrayList<>();
        List<Order> orderList = orderMapper.byBusinessOrderNumber(businessOrderNumber);
        for (Order order : orderList){
            OrderDTO orderDTO = new OrderDTO();
            orderDTO.setOrderId(order.getId());
            orderDTO.setTotalAmount(order.getTotalAmount());
            orderDTO.setWorkerTypeName(order.getWorkerTypeName());
            if (StringUtil.isEmpty(order.getWorkerTypeId())){
                if(order.getType() == 2){//材料
                    orderDTO.setImage(address + "icon/bucailiao.png");
                    orderDTO.setName("补材料商品");
                }else {
                    orderDTO.setImage(address + "icon/burengong.png");
                    orderDTO.setName("人工商品");
                }
            }else if(order.getWorkerTypeId().equals("1")){//设计
                orderDTO.setName(style);
                orderDTO.setImage(address + "icon/shejiF.png");
            }else if (order.getWorkerTypeId().equals("2")){
                orderDTO.setName("当家精算");
                orderDTO.setImage(address + "icon/jingsuanF.png");
            }else {
                List<OrderItem> orderItemList = orderItemMapper.byOrderIdList(order.getId());
                if(orderItemList.size() > 0){
                    if (order.getType() == 1){//人工
                        orderDTO.setImage(address + "icon/Arengong.png");
                        orderDTO.setName("人工类商品");
                    }else if(order.getType() == 2){//材料
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
    public ServerResponse confirmOrderSplit(String userToken,String houseId){
        try{
            AccessToken accessToken=redisClient.getCache(userToken+ Constants.SESSIONUSERID,AccessToken.class);
            Member worker = memberMapper.selectByPrimaryKey(accessToken.getMember().getId());

            Example example = new Example(OrderSplit.class);
            example.createCriteria().andEqualTo(OrderSplit.HOUSE_ID, houseId).andEqualTo(OrderSplit.APPLY_STATUS, 0)
                    .andEqualTo(OrderSplit.WORKER_TYPE_ID,worker.getWorkerTypeId());
            List<OrderSplit> orderSplitList = orderSplitMapper.selectByExample(example);
            if (orderSplitList.size() == 0){
                return ServerResponse.createByErrorMessage("没有生成要货单");
            }else if (orderSplitList.size() > 1){
                return ServerResponse.createByErrorMessage("生成多个未提交要货单,异常联系平台部");
            }else {
                OrderSplit orderSplit = orderSplitList.get(0);
                example = new Example(OrderSplitItem.class);
                example.createCriteria().andEqualTo(OrderSplitItem.ORDER_SPLIT_ID, orderSplit.getId());
                List<OrderSplitItem> orderSplitItemList = orderSplitItemMapper.selectByExample(example);
                for (OrderSplitItem orderSplitItem : orderSplitItemList){
                    Warehouse warehouse = warehouseMapper.selectByPrimaryKey(orderSplitItem.getWarehouseId());
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
                if(worker.getWorkerType() == 3){
                    configMessageService.addConfigMessage(null,"zx",house.getMemberId(),"0","大管家要服务",
                            String.format(DjConstants.PushMessage.STEWARD_Y_SERVER,house.getHouseName()) ,"");
                }else {
                    configMessageService.addConfigMessage(null,"zx",house.getMemberId(),"0","工匠要材料",String.format
                            (DjConstants.PushMessage.CRAFTSMAN_Y_MATERIAL,house.getHouseName()) ,"");
                }
                return ServerResponse.createBySuccessMessage("操作成功");
            }

        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("失败");
        }
    }


    /**
     * 返回已添加要货单明细
     */
    public ServerResponse getOrderItemList(String userToken,String houseId){
        try{
            AccessToken accessToken=redisClient.getCache(userToken+ Constants.SESSIONUSERID,AccessToken.class);
            Member worker = memberMapper.selectByPrimaryKey(accessToken.getMember().getId());
            Map<String,Object> map = new HashMap<>();
            WorkerType workerType = workerTypeMapper.selectByPrimaryKey(worker.getWorkerTypeId());
            map.put("times", workerType.getSafeState());//要货次数
            Example example = new Example(OrderSplit.class);
            example.createCriteria().andEqualTo(OrderSplit.HOUSE_ID, houseId).andEqualTo(OrderSplit.WORKER_TYPE_ID, worker.getWorkerTypeId());
            List<OrderSplit> orderSplitList = orderSplitMapper.selectByExample(example);
            map.put("surplus", workerType.getSafeState() - orderSplitList.size());

            example = new Example(OrderSplit.class);
            example.createCriteria().andEqualTo(OrderSplit.HOUSE_ID, houseId).andEqualTo(OrderSplit.APPLY_STATUS, 0)
            .andEqualTo(OrderSplit.WORKER_TYPE_ID,worker.getWorkerTypeId());
            orderSplitList = orderSplitMapper.selectByExample(example);
            if (orderSplitList.size() == 0){
                return ServerResponse.createBySuccessMessage("没有生成中要货单");
            }else if (orderSplitList.size() > 1){
                return ServerResponse.createByErrorMessage("生成多个未提交要货单,异常联系平台部");
            }else {
                OrderSplit orderSplit = orderSplitList.get(0);
                example = new Example(OrderSplitItem.class);
                example.createCriteria().andEqualTo(OrderSplitItem.ORDER_SPLIT_ID, orderSplit.getId());
                List<OrderSplitItem> orderSplitItemList = orderSplitItemMapper.selectByExample(example);
                for (OrderSplitItem v : orderSplitItemList){
                    v.initPath(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class));
                }
                map.put("orderSplitItemList",orderSplitItemList);
                return ServerResponse.createBySuccess("查询成功", map);
            }
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 管家要服务
     * 工匠要工序材料
     * 提交到要货
     */
    public ServerResponse saveOrderSplit(String productArr, String houseId, String userToken){
        try{
            AccessToken accessToken=redisClient.getCache(userToken+ Constants.SESSIONUSERID,AccessToken.class);
            Member worker = memberMapper.selectByPrimaryKey(accessToken.getMember().getId());

            Example example = new Example(OrderSplit.class);
            example.createCriteria().andEqualTo(OrderSplit.HOUSE_ID, houseId).andEqualTo(OrderSplit.APPLY_STATUS, 0);
            List<OrderSplit> orderSplitList = orderSplitMapper.selectByExample(example);
            OrderSplit orderSplit;
            if (orderSplitList.size() > 0){
                orderSplit = orderSplitList.get(0);
                /*删除之前子项*/
                example = new Example(OrderSplitItem.class);
                example.createCriteria().andEqualTo(OrderSplitItem.ORDER_SPLIT_ID, orderSplit.getId());
                orderSplitItemMapper.deleteByExample(example);

                orderSplit.setSupervisorId(worker.getId());
                orderSplit.setSupervisorName(worker.getName());
                orderSplit.setSupervisorTel(worker.getMobile());
                orderSplit.setWorkerTypeId(worker.getWorkerTypeId());
                orderSplitMapper.updateByPrimaryKeySelective(orderSplit);
            }else {
                example = new Example(OrderSplit.class);
                orderSplit = new OrderSplit();
                orderSplit.setNumber("dj" + 200000 + orderSplitMapper.selectCountByExample(example));//要货单号
                orderSplit.setHouseId(houseId);
                orderSplit.setApplyStatus(0);//后台审核状态：0生成中, 1申请中, 2通过, 3不通过 后台(材料员)
                orderSplit.setSupervisorId(worker.getId());
                orderSplit.setSupervisorName(worker.getName());
                orderSplit.setSupervisorTel(worker.getMobile());
                orderSplit.setWorkerTypeId(worker.getWorkerTypeId());
                orderSplitMapper.insert(orderSplit);
            }

            JSONArray arr = JSONArray.parseArray(productArr);
            for(int i=0; i<arr.size(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                Double num = Double.parseDouble(obj.getString("num"));
                String productId = obj.getString("productId");

                Warehouse warehouse = warehouseMapper.getByProductId(productId, houseId);//定位到仓库id
                OrderSplitItem orderSplitItem = new OrderSplitItem();
                orderSplitItem.setOrderSplitId(orderSplit.getId());
                orderSplitItem.setWarehouseId(warehouse.getId());//仓库子项id
                orderSplitItem.setProductId(warehouse.getProductId());
                orderSplitItem.setProductSn(warehouse.getProductSn());
                orderSplitItem.setProductName(warehouse.getProductName());
                orderSplitItem.setPrice(warehouse.getPrice());
                orderSplitItem.setAskCount(warehouse.getAskCount());
                orderSplitItem.setCost(warehouse.getCost());
                orderSplitItem.setShopCount(warehouse.getShopCount());
                orderSplitItem.setNum(num);
                orderSplitItem.setUnitName(warehouse.getUnitName());
                orderSplitItem.setTotalPrice(warehouse.getPrice() * num);//单项总价 销售价
                orderSplitItem.setProductType(warehouse.getProductType());
                orderSplitItem.setCategoryId(warehouse.getCategoryId());
                orderSplitItem.setImage(warehouse.getImage());//货品图片
                orderSplitItem.setHouseId(houseId);
                orderSplitItemMapper.insert(orderSplitItem);
            }

            return ServerResponse.createBySuccessMessage("提交成功");
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("提交失败");
        }
    }

}
