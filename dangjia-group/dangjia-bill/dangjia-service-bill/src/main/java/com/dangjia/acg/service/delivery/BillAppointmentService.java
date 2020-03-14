package com.dangjia.acg.service.delivery;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.app.member.MemberAPI;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.common.util.MathUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.delivery.AppointmentDTO;
import com.dangjia.acg.dto.delivery.AppointmentListDTO;
import com.dangjia.acg.dto.delivery.OrderStorefrontDTO;
import com.dangjia.acg.mapper.delivery.*;
import com.dangjia.acg.mapper.order.IBillWarehouseMapper;
import com.dangjia.acg.mapper.storeFront.BillStoreFrontProductMapper;
import com.dangjia.acg.modle.deliver.*;
import com.dangjia.acg.modle.house.Warehouse;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.storefront.StorefrontProduct;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 30/10/2019
 * Time: 下午 3:31
 */
@Service
public class BillAppointmentService {

    @Autowired
    private IBillDjDeliverOrderMapper djDeliverOrderMapper;
    @Autowired
    private IBillDjDeliverOrderItemMapper djDeliverOrderItemMapper;
    @Autowired
    private BillDjDeliverOrderSplitMapper billDjDeliverOrderSplitMapper;

    private Logger logger = LoggerFactory.getLogger(DjDeliverOrderService.class);

    @Autowired
    private BillStoreFrontProductMapper billStoreFrontProductMapper;

    @Autowired
    private MemberAPI memberAPI;

    @Autowired
    private BillDjDeliverOrderSplitItemMapper billDjDeliverOrderSplitItemMapper;

    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IBillWarehouseMapper iBillWarehouseMapper;

    /**
     * 我的预约查询
     *
     * @param pageDTO
     * @param houseId
     * @return
     */
    public ServerResponse queryAppointment(PageDTO pageDTO, String houseId, String userToken) {
        try {
            Object object = memberAPI.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            JSONObject job = (JSONObject) object;
            Member member = job.toJavaObject(Member.class);
            String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<OrderStorefrontDTO> orderStorefrontDTOS = djDeliverOrderMapper.queryDjDeliverOrderStorefront(houseId,member.getId());
            PageInfo pageResult = new PageInfo(orderStorefrontDTOS);
            List<AppointmentListDTO> appointmentListDTOS = new ArrayList<>();
            orderStorefrontDTOS.forEach(orderStorefrontDTO -> {
                orderStorefrontDTO.setStorefrontIcon(imageAddress+orderStorefrontDTO.getStorefrontIcon());
                AppointmentListDTO appointmentListDTO = new AppointmentListDTO();
                List<AppointmentDTO> appointmentDTOS = djDeliverOrderMapper.queryAppointment(orderStorefrontDTO.getOrderId());
                if (appointmentDTOS.size() > 0) {
                    appointmentDTOS.forEach(appointmentDTO -> {
                        appointmentDTO.setImage(imageAddress + appointmentDTO.getImage());
                    });
                    appointmentListDTO.setAppointmentDTOS(appointmentDTOS);
                    appointmentListDTO.setOrderStorefrontDTO(orderStorefrontDTO);
                    appointmentListDTOS.add(appointmentListDTO);
                }
            });
            if (appointmentListDTOS.size() <= 0)
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            pageResult.setList(appointmentListDTOS);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            logger.info("查询失败", e);
            return ServerResponse.createByErrorMessage("查询失败" + e);
        }
    }


    /**
     * 预约发货
     *
     * @param jsonStr
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse insertAppointment(String userToken, String jsonStr,String reservationDeliverTime, String orderIds) {
        try {
            Object object = memberAPI.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            JSONObject job = (JSONObject) object;
            Member member = job.toJavaObject(Member.class);
            if(!CommonUtil.isEmpty(orderIds)) {//我的订单库存页面预约发货
                Order order = djDeliverOrderMapper.selectByPrimaryKey(orderIds);
                OrderSplit orderSplit = new OrderSplit();
                Example example = new Example(OrderSplit.class);
                orderSplit.setNumber("DJ" + 200000 + djDeliverOrderItemMapper.selectCountByExample(example));//要货单号
                orderSplit.setHouseId(order.getHouseId());
                orderSplit.setApplyStatus(1);//后台审核状态：0生成中, 1申请中, 2通过, 3不通过, 4业主待支付补货材料 后台(材料员)
                orderSplit.setMemberId(member.getId());
                orderSplit.setMemberName(member.getName());
                orderSplit.setMobile(member.getMobile());
                orderSplit.setWorkerTypeId(member.getWorkerTypeId());
                orderSplit.setStorefrontId(order.getStorefontId());
                orderSplit.setCityId(order.getCityId());
                orderSplit.setOrderId(orderIds);
                orderSplit.setIsReservationDeliver("1");
                orderSplit.setAddressId(order.getAddressId());
                orderSplit.setReservationDeliverTime(DateUtil.toDate(reservationDeliverTime));
                Double totalPrice=0d;
                example=new Example(OrderItem.class);
                example.createCriteria().andEqualTo(OrderItem.ORDER_ID,orderIds)
                        .andEqualTo(OrderItem.IS_RESERVATION_DELIVER,1);
                List<OrderItem> orderItems = djDeliverOrderItemMapper.selectByExample(example);
                for(OrderItem orderItem:orderItems) {
                    OrderSplitItem orderSplitItem = new OrderSplitItem();
                    orderSplitItem.setOrderSplitId(orderSplit.getId());
                    orderSplitItem.setProductId(orderItem.getProductId());
                    orderSplitItem.setProductSn(orderItem.getProductSn());
                    orderSplitItem.setProductName(orderItem.getProductName());
                    orderSplitItem.setPrice(orderItem.getPrice());
                    orderSplitItem.setCost(orderItem.getCost());
                    orderSplitItem.setShopCount(orderItem.getShopCount());
                    orderSplitItem.setNum(orderItem.getShopCount());
                    orderSplitItem.setUnitName(orderItem.getUnitName());
                    orderSplitItem.setTotalPrice(orderItem.getPrice() * orderItem.getShopCount());//单项总价 销售价
                    orderSplitItem.setProductType(orderItem.getProductType());
                    orderSplitItem.setCategoryId(orderItem.getCategoryId());
                    orderSplitItem.setImage(orderItem.getImage());//货品图片
                    orderSplitItem.setHouseId(orderItem.getHouseId());
                    orderSplitItem.setCityId(orderItem.getCityId());
                    orderSplitItem.setAddressId(order.getAddressId());
                    orderSplitItem.setStorefrontId(orderItem.getStorefontId());
                    StorefrontProduct storefrontProduct = billStoreFrontProductMapper.selectByPrimaryKey(orderItem.getProductId());
                    orderSplitItem.setIsDeliveryInstall(storefrontProduct.getIsDeliveryInstall());
                    orderSplitItem.setOrderItemId(orderItem.getId());
                    orderSplitItem.setIsReservationDeliver(1);//是否需要预约(1是，0否）
                    orderSplitItem.setReservationDeliverTime(DateUtil.toDate(reservationDeliverTime));

                    //扣除业主仓库数据
                    example=new Example(Warehouse.class);
                    example.createCriteria().andEqualTo(Warehouse.HOUSE_ID,orderSplit.getHouseId())
                            .andEqualTo(Warehouse.PRODUCT_ID,orderSplitItem.getProductId());
                    Warehouse warehouse = iBillWarehouseMapper.selectOneByExample(example);
                    warehouse.setAskCount(warehouse.getAskCount() + orderSplitItem.getNum());//更新仓库已要总数
                    warehouse.setAskTime(warehouse.getAskTime() + 1);//更新该货品被要次数
                    iBillWarehouseMapper.updateByPrimaryKeySelective(warehouse);

                    //计算运费，搬运费
                    Double transportationCost=orderItem.getTransportationCost()!=null?orderItem.getTransportationCost():0;//运费
                    Double stevedorageCost=orderItem.getStevedorageCost()!=null?orderItem.getStevedorageCost():0;//搬运费
                    Double askCount=orderItem.getShopCount();
                    //计算运费
                    if(transportationCost>0.0) {//（运费/总数量）*收货量
                        orderSplitItem.setTransportationCost(MathUtil.mul(MathUtil.div(transportationCost,orderItem.getShopCount()!=null?orderItem.getShopCount():1),askCount));
                    }else{
                        orderSplitItem.setTransportationCost(0d);
                    }
                    //计算搬运费
                    if(stevedorageCost>0.0){//（搬运费/总数量）*收货量
                        orderSplitItem.setStevedorageCost(MathUtil.mul(MathUtil.div(stevedorageCost,orderItem.getShopCount()!=null?orderItem.getShopCount():1),askCount));
                    }else{
                        orderSplitItem.setStevedorageCost(0d);
                    }
                    //优惠券
                    if(orderItem.getDiscountPrice()!=null&&orderItem.getDiscountPrice()>0){
                        orderSplitItem.setDiscountPrice(MathUtil.mul(MathUtil.div(orderItem.getDiscountPrice(),orderItem.getShopCount()!=null?orderItem.getShopCount():1),askCount));
                    }else{
                        orderSplitItem.setDiscountPrice(0d);
                    }
                    totalPrice=MathUtil.add(totalPrice,MathUtil.add(orderSplitItem.getTotalPrice(),MathUtil.add(orderSplitItem.getTransportationCost(),orderSplitItem.getStevedorageCost())));
                    totalPrice=MathUtil.sub(totalPrice,orderSplitItem.getDiscountPrice());
                    billDjDeliverOrderSplitItemMapper.insert(orderSplitItem);

                    //修改订单明细中的要货量
                    if(orderItem.getAskCount()==null)
                        orderItem.setAskCount(0d);
                    orderItem.setAskCount(MathUtil.add(orderItem.getAskCount(),orderItem.getShopCount()));
                    orderItem.setReservationDeliverTime(DateUtil.toDate(reservationDeliverTime));
                    djDeliverOrderItemMapper.updateByPrimaryKey(orderItem);

                }
                orderSplit.setTotalAmount(BigDecimal.valueOf(totalPrice));
                billDjDeliverOrderSplitMapper.insert(orderSplit);
            }else {//预约发货页面预约发货
                JSONObject villageObj = JSONObject.parseObject(jsonStr);
                String objList = villageObj.getString("objList");
                JSONArray jsonArr = JSONArray.parseArray(objList);
                for(int i=0;i<jsonArr.size();i++){
                    JSONObject obj = jsonArr.getJSONObject(i);
                    String orderId = obj.getString("orderId");
                    String[] orderItemsIds = obj.getString("orderItemId").split(",");
                    Order djDeliverOrder = djDeliverOrderMapper.selectByPrimaryKey(orderId);
                    OrderSplit orderSplit = new OrderSplit();
                    Example example = new Example(OrderSplit.class);
                    orderSplit.setNumber("DJ" + 200000 + djDeliverOrderItemMapper.selectCountByExample(example));//要货单号
                    orderSplit.setHouseId(djDeliverOrder.getHouseId());
                    orderSplit.setApplyStatus(1);//后台审核状态：0生成中, 1申请中, 2通过, 3不通过, 4业主待支付补货材料 后台(材料员)
                    orderSplit.setMemberId(member.getId());
                    orderSplit.setMemberName(member.getName());
                    orderSplit.setMobile(member.getMobile());
                    orderSplit.setWorkerTypeId(member.getWorkerTypeId());
                    orderSplit.setStorefrontId(djDeliverOrder.getStorefontId());
                    orderSplit.setCityId(djDeliverOrder.getCityId());
                    orderSplit.setOrderId(orderId);
                    orderSplit.setIsReservationDeliver("1");
                    orderSplit.setAddressId(djDeliverOrder.getAddressId());
                    orderSplit.setReservationDeliverTime(DateUtil.toDate(reservationDeliverTime));
                    Double totalPrice=0d;
                    for (String orderItemsId : orderItemsIds) {
                        OrderItem orderItem = djDeliverOrderItemMapper.selectByPrimaryKey(orderItemsId);
                        OrderSplitItem orderSplitItem = new OrderSplitItem();
                        orderSplitItem.setOrderSplitId(orderSplit.getId());
                        orderSplitItem.setProductId(orderItem.getProductId());
                        orderSplitItem.setProductSn(orderItem.getProductSn());
                        orderSplitItem.setProductName(orderItem.getProductName());
                        orderSplitItem.setPrice(orderItem.getPrice());
                        orderSplitItem.setCost(orderItem.getCost());
                        orderSplitItem.setShopCount(orderItem.getShopCount());
                        orderSplitItem.setNum(orderItem.getShopCount());
                        orderSplitItem.setUnitName(orderItem.getUnitName());
                        orderSplitItem.setTotalPrice(orderItem.getPrice() * orderItem.getShopCount());//单项总价 销售价
                        orderSplitItem.setProductType(orderItem.getProductType());
                        orderSplitItem.setCategoryId(orderItem.getCategoryId());
                        orderSplitItem.setImage(orderItem.getImage());//货品图片
                        orderSplitItem.setHouseId(orderItem.getHouseId());
                        orderSplitItem.setCityId(orderItem.getCityId());
                        orderSplitItem.setAddressId(djDeliverOrder.getAddressId());
                        orderSplitItem.setStorefrontId(orderItem.getStorefontId());
                        StorefrontProduct storefrontProduct = billStoreFrontProductMapper.selectByPrimaryKey(orderItem.getProductId());
                        orderSplitItem.setIsDeliveryInstall(storefrontProduct.getIsDeliveryInstall());
                        orderSplitItem.setOrderItemId(orderItem.getId());
                        orderSplitItem.setIsReservationDeliver(1);//是否需要预约(1是，0否）
                        orderSplitItem.setReservationDeliverTime(DateUtil.toDate(reservationDeliverTime));
                        Double askCount=orderItem.getShopCount();

                        //扣除业主仓库数据
                        example=new Example(Warehouse.class);
                        example.createCriteria().andEqualTo(Warehouse.HOUSE_ID,orderSplit.getHouseId())
                                .andEqualTo(Warehouse.PRODUCT_ID,orderSplitItem.getProductId());
                        Warehouse warehouse = iBillWarehouseMapper.selectOneByExample(example);
                        warehouse.setAskCount(warehouse.getAskCount() + orderSplitItem.getNum());//更新仓库已要总数
                        warehouse.setAskTime(warehouse.getAskTime() + 1);//更新该货品被要次数
                        iBillWarehouseMapper.updateByPrimaryKeySelective(warehouse);

                        //计算运费，搬运费
                        Double transportationCost=orderItem.getTransportationCost()!=null?orderItem.getTransportationCost():0;//运费
                        Double stevedorageCost=orderItem.getStevedorageCost()!=null?orderItem.getStevedorageCost():0;//搬运费
                        //计算运费
                        if(transportationCost>0.0) {//（运费/总数量）*收货量
                            orderSplitItem.setTransportationCost(MathUtil.mul(MathUtil.div(transportationCost,orderItem.getShopCount()!=null?orderItem.getShopCount():1),askCount));
                        }else{
                            orderSplitItem.setTransportationCost(0d);
                        }
                        //计算搬运费
                        if(stevedorageCost>0.0){//（搬运费/总数量）*收货量
                            orderSplitItem.setStevedorageCost(MathUtil.mul(MathUtil.div(stevedorageCost,orderItem.getShopCount()!=null?orderItem.getShopCount():1),askCount));
                        }else{
                            orderSplitItem.setStevedorageCost(0d);
                        }
                        //优惠券
                        if(orderItem.getDiscountPrice()!=null&&orderItem.getDiscountPrice()>0){
                            orderSplitItem.setDiscountPrice(MathUtil.mul(MathUtil.div(orderItem.getDiscountPrice(),orderItem.getShopCount()!=null?orderItem.getShopCount():1),askCount));
                        }else{
                            orderSplitItem.setDiscountPrice(0d);
                        }
                        totalPrice=MathUtil.add(totalPrice,MathUtil.add(orderSplitItem.getTotalPrice(),MathUtil.add(orderSplitItem.getTransportationCost(),orderSplitItem.getStevedorageCost())));
                        totalPrice=MathUtil.sub(totalPrice,orderSplitItem.getDiscountPrice());
                        billDjDeliverOrderSplitItemMapper.insert(orderSplitItem);

                        //修改订单明细中的要货量
                        if(orderItem.getAskCount()==null)
                            orderItem.setAskCount(0d);
                        orderItem.setAskCount(MathUtil.add(orderItem.getAskCount(),orderSplitItem.getShopCount()));
                        orderItem.setReservationDeliverTime(DateUtil.toDate(reservationDeliverTime));
                        djDeliverOrderItemMapper.updateByPrimaryKey(orderItem);
                    }
                    orderSplit.setTotalAmount(BigDecimal.valueOf(totalPrice));
                    billDjDeliverOrderSplitMapper.insert(orderSplit);
                }

            }
            return ServerResponse.createBySuccessMessage("操作成功");
        } catch (Exception e) {
            logger.info("操作失败", e);
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }


    /**
     * 已预约
     *
     * @param pageDTO
     * @param houseId
     * @return
     */
    public ServerResponse queryReserved(PageDTO pageDTO, String houseId, String userToken) {
        try {
            Object object = memberAPI.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            JSONObject job = (JSONObject) object;
            Member member = job.toJavaObject(Member.class);
            String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<OrderStorefrontDTO> orderStorefrontDTOS = djDeliverOrderMapper.queryReservedStorefront(houseId,member.getId());
            List<AppointmentListDTO> appointmentListDTOS = new ArrayList<>();
            orderStorefrontDTOS.forEach(orderStorefrontDTO -> {
                orderStorefrontDTO.setStorefrontIcon(imageAddress+orderStorefrontDTO.getStorefrontIcon());
                AppointmentListDTO appointmentListDTO = new AppointmentListDTO();
                List<AppointmentDTO> appointmentDTOS = djDeliverOrderMapper.queryReserved(orderStorefrontDTO.getOrderSplitId());
                OrderSplit orderSplit = billDjDeliverOrderSplitMapper.selectByPrimaryKey(orderStorefrontDTO.getOrderSplitId());
                if (appointmentDTOS.size() > 0) {
                    appointmentDTOS.forEach(appointmentDTO -> {
                        if(orderSplit.getApplyStatus()==0){
                            appointmentDTO.setApplyStatusName("取消预约");
                        }
                        appointmentDTO.setApplyStatus(orderSplit.getApplyStatus());
                        appointmentDTO.setImage(imageAddress + appointmentDTO.getImage());
                    });
                    appointmentListDTO.setAppointmentDTOS(appointmentDTOS);
                    appointmentListDTO.setOrderStorefrontDTO(orderStorefrontDTO);
                    appointmentListDTOS.add(appointmentListDTO);
                }
            });
            if (appointmentListDTOS.size() <= 0)
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            PageInfo pageResult = new PageInfo(appointmentListDTOS);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            logger.info("查询失败", e);
            return ServerResponse.createByErrorMessage("查询失败" + e);
        }
    }


    /**
     * 取消预约
     *
     * @param orderSplitItemId
     * @return
     */
    public ServerResponse updateReserved(String orderSplitItemId,String productId) {
        try {
            Example example = new Example(OrderSplitItem.class);
            example.createCriteria().andEqualTo(OrderSplitItem.ID, orderSplitItemId)
                    .andIsNotNull(OrderSplitItem.SPLIT_DELIVER_ID);
            List<OrderSplitItem> orderSplitItems = billDjDeliverOrderSplitItemMapper.selectByExample(example);
            if (orderSplitItems.size() > 0)
                return ServerResponse.createByErrorMessage("供应商已发货不能取消");
            OrderSplitItem orderSplitItem = billDjDeliverOrderSplitItemMapper.selectByPrimaryKey(orderSplitItemId);
            OrderSplit orderSplit = billDjDeliverOrderSplitMapper.selectByPrimaryKey(orderSplitItem.getOrderSplitId());
            djDeliverOrderItemMapper.updateReserved(orderSplit.getOrderId(),productId);
            billDjDeliverOrderSplitItemMapper.deleteByPrimaryKey(orderSplitItemId);
            return ServerResponse.createBySuccessMessage("操作成功");
        } catch (Exception e) {
            logger.info("操作失败", e);
            return ServerResponse.createByErrorMessage("操作失败" + e);
        }
    }


    /**
     * 修改预约时间
     * @param orderSplitItemId
     * @param reservationDeliverTime
     * @return
     */
    public ServerResponse updateReservationDeliverTime(String orderSplitItemId,Date reservationDeliverTime) {
        try {
            Example example=new Example(OrderSplitItem.class);
            example.createCriteria().andEqualTo(OrderSplitItem.ID,orderSplitItemId)
                    .andIsNotNull(OrderSplitItem.SPLIT_DELIVER_ID);
            List<OrderSplitItem> orderSplitItems = billDjDeliverOrderSplitItemMapper.selectByExample(example);
            if(orderSplitItems.size()>0)
                return ServerResponse.createByErrorMessage("供应商已发货不能修改");
            OrderSplitItem orderSplitItem=new OrderSplitItem();
            orderSplitItem.setId(orderSplitItemId);
            orderSplitItem.setReservationDeliverTime(reservationDeliverTime);
            billDjDeliverOrderSplitItemMapper.updateByPrimaryKeySelective(orderSplitItem);
            return ServerResponse.createBySuccessMessage("操作成功");
        } catch (Exception e) {
            logger.info("操作失败",e);
            return ServerResponse.createByErrorMessage("操作失败"+e);
        }
    }
}
