package com.dangjia.acg.service.deliver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.api.data.ForMasterAPI;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.deliver.BusinessOrderDTO;
import com.dangjia.acg.dto.deliver.ItemDTO;
import com.dangjia.acg.dto.deliver.OrderDTO;
import com.dangjia.acg.dto.deliver.OrderItemDTO;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.deliver.*;
import com.dangjia.acg.mapper.design.IDesignImageTypeMapper;
import com.dangjia.acg.mapper.design.IHouseDesignImageMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.house.IWarehouseDetailMapper;
import com.dangjia.acg.mapper.house.IWarehouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.pay.IBusinessOrderMapper;
import com.dangjia.acg.modle.basics.Goods;
import com.dangjia.acg.modle.basics.Product;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.deliver.*;
import com.dangjia.acg.modle.design.DesignImageType;
import com.dangjia.acg.modle.design.HouseDesignImage;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.house.Warehouse;
import com.dangjia.acg.modle.house.WarehouseDetail;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.pay.BusinessOrder;
import com.dangjia.acg.service.config.ConfigMessageService;
import com.dangjia.acg.service.repair.MendOrderService;
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

    @Autowired
    private ISplitDeliverMapper splitDeliverMapper;
    @Autowired
    private ForMasterAPI forMasterAPI;
    @Autowired
    private MendOrderService mendOrderService;

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
                //如果存在补货单，则告知业主补货支付
                if(!CommonUtil.isEmpty(orderSplit.getMendNumber())){
                    orderSplit.setApplyStatus(4);
                    orderSplitMapper.updateByPrimaryKeySelective(orderSplit);
                    return mendOrderService.confirmMendMaterial(userToken,houseId);
                }
                example = new Example(OrderSplitItem.class);
                example.createCriteria().andEqualTo(OrderSplitItem.ORDER_SPLIT_ID, orderSplit.getId());
                List<OrderSplitItem> orderSplitItemList = orderSplitItemMapper.selectByExample(example);
                for (OrderSplitItem orderSplitItem : orderSplitItemList){
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
            int surplus = orderSplitMapper.selectCountByExample(example);
            map.put("surplus", workerType.getSafeState() - surplus);

            example = new Example(OrderSplit.class);
            example.createCriteria().andEqualTo(OrderSplit.HOUSE_ID, houseId).andEqualTo(OrderSplit.APPLY_STATUS, 0)
            .andEqualTo(OrderSplit.WORKER_TYPE_ID,worker.getWorkerTypeId());
            List<OrderSplit> orderSplitList = orderSplitMapper.selectByExample(example);
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
            example.createCriteria().andEqualTo(OrderSplit.HOUSE_ID, houseId).andEqualTo(OrderSplit.APPLY_STATUS, 4)
                    .andEqualTo(OrderSplit.WORKER_TYPE_ID,worker.getWorkerTypeId());
            int orderSplitnum= orderSplitMapper.selectCountByExample(example);
            if(orderSplitnum>0){
                return ServerResponse.createByErrorMessage("存在业主未处理的补货单，无法提交要货！");
            }

            example = new Example(SplitDeliver.class);
            example.createCriteria().andEqualTo(SplitDeliver.HOUSE_ID, houseId)
                    .andEqualTo(SplitDeliver.SHIPPING_STATE, 1).andCondition(" DATE_SUB(CURDATE(), INTERVAL 7 DAY) > date(send_time) ");
            int list=splitDeliverMapper.selectCountByExample(example);
            if(list>0){
                return ServerResponse.createByErrorMessage("存在供应商发货后7天还未签收,无法提交要货！");
            }
            ServerResponse serverResponse=mendOrderService.mendChecking(houseId,worker.getWorkerTypeId(),0);
            if(!serverResponse.isSuccess()){
                return ServerResponse.createByErrorMessage(serverResponse.getResultMsg());
            }
            example = new Example(OrderSplit.class);
            example.createCriteria().andEqualTo(OrderSplit.HOUSE_ID, houseId).andEqualTo(OrderSplit.APPLY_STATUS, 0)
                    .andEqualTo(OrderSplit.WORKER_TYPE_ID,worker.getWorkerTypeId());
            List<OrderSplit> orderSplitList = orderSplitMapper.selectByExample(example);
            OrderSplit orderSplit;
            House house = houseMapper.selectByPrimaryKey(houseId);
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
                orderSplit.setNumber("DJ" + 200000 + orderSplitMapper.selectCountByExample(example));//要货单号
                orderSplit.setHouseId(houseId);
                orderSplit.setApplyStatus(0);//后台审核状态：0生成中, 1申请中, 2通过, 3不通过, 4业主待支付补货材料 后台(材料员)
                orderSplit.setSupervisorId(worker.getId());
                orderSplit.setSupervisorName(worker.getName());
                orderSplit.setSupervisorTel(worker.getMobile());
                orderSplit.setWorkerTypeId(worker.getWorkerTypeId());
                orderSplitMapper.insert(orderSplit);
            }

            JSONArray arr = JSONArray.parseArray(productArr);
            List productList=new ArrayList();
            Map mapczai=new HashMap();
            for(int i=0; i<arr.size(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                Double num = Double.parseDouble(obj.getString("num"));
                String productId = obj.getString("productId");
                Warehouse warehouse = warehouseMapper.getByProductId(productId, houseId);//定位到仓库id

                //判断如果该商品已经插入，则不再进行第二次插入，防止数据重复展示
                if(mapczai.get(orderSplit.getId()+"-"+productId+"-"+houseId)!=null){
                    continue;
                }
                Product product=forMasterAPI.getProduct(house.getCityId(), productId);
                if(warehouse!=null) {
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
                }else{

                    Goods goods=forMasterAPI.getGoods(house.getCityId(), product.getGoodsId());
                    OrderSplitItem orderSplitItem = new OrderSplitItem();
                    orderSplitItem.setOrderSplitId(orderSplit.getId());
                    orderSplitItem.setProductId(product.getId());
                    orderSplitItem.setProductSn(product.getProductSn());
                    orderSplitItem.setProductName(product.getName());
                    orderSplitItem.setPrice(product.getPrice());
                    orderSplitItem.setAskCount(0d);
                    orderSplitItem.setCost(product.getCost());
                    orderSplitItem.setShopCount(0d);
                    orderSplitItem.setNum(num);
                    orderSplitItem.setUnitName(product.getUnitName());
                    orderSplitItem.setTotalPrice(product.getPrice() * num);//单项总价 销售价
                    orderSplitItem.setProductType(goods.getType());
                    orderSplitItem.setCategoryId(product.getCategoryId());
                    orderSplitItem.setImage(product.getImage());//货品图片
                    orderSplitItem.setHouseId(houseId);
                    orderSplitItemMapper.insert(orderSplitItem);
                }
                mapczai.put(orderSplit.getId()+"-"+productId+"-"+houseId,"1");
                Double numObj=0D;
                //计算补货数量
                if(warehouse!=null) {
                    //仓库剩余数
                    Double surCount = warehouse.getShopCount() - warehouse.getAskCount() - (warehouse.getOwnerBack()==null?0D:warehouse.getOwnerBack());
                    //多出的数
                    Double overflowCount = (num - surCount);
                    if (overflowCount > 0) {
                        numObj=overflowCount;
                        Map map = new HashMap();
                        map.put("num", overflowCount);
                        map.put("productId", productId);
                        productList.add(map);
                    }else {
                        //如果剩余数为负数
                        if (surCount < 0) {
                            numObj=num;
                            Map map = new HashMap();
                            map.put("num", num);
                            map.put("productId", productId);
                            productList.add(map);
                        }
                    }
                }else{
                    numObj=num;
                    Map map = new HashMap();
                    map.put("num", num);
                    map.put("productId", productId);
                    productList.add(map);
                }
                if(numObj>0&&product.getType()==1&&product.getMaket()==1){
                    return ServerResponse.createBySuccessMessage("商品（"+product.getName()+"）已下架，无法要货！");
                }
            }

            //补货材料列表
            String mendMaterialArr=JSON.toJSONString(productList);
            if(!CommonUtil.isEmpty(mendMaterialArr)&&productList.size()>0){
                serverResponse= mendOrderService.saveMendMaterial( userToken, houseId, mendMaterialArr);
                if (serverResponse.isSuccess()) {
                    if (serverResponse.getResultObj() != null) {
                        //保存补货ID
                        orderSplit.setMendNumber(String.valueOf(serverResponse.getResultObj()));
                        orderSplitMapper.updateByPrimaryKeySelective(orderSplit);
                    }
                }
            }
            return ServerResponse.createBySuccessMessage("提交成功");
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("提交失败");
        }
    }

}
