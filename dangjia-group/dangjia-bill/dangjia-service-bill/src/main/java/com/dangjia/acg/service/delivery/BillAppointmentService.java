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
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.delivery.AppointmentDTO;
import com.dangjia.acg.dto.delivery.AppointmentListDTO;
import com.dangjia.acg.dto.delivery.OrderStorefrontDTO;
import com.dangjia.acg.mapper.delivery.*;
import com.dangjia.acg.mapper.member.IBillMemberAddressMapper;
import com.dangjia.acg.mapper.storeFront.BillStoreFrontProductMapper;
import com.dangjia.acg.modle.deliver.*;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.member.MemberAddress;
import com.dangjia.acg.modle.storefront.StorefrontProduct;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

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
            if(!CommonUtil.isEmpty(orderIds)) {//订单列表页面预约发货
                Order order = djDeliverOrderMapper.selectByPrimaryKey(orderIds);
                OrderSplit orderSplit = new OrderSplit();
                Example example = new Example(OrderSplit.class);
                orderSplit.setNumber("DJ" + 200000 + djDeliverOrderItemMapper.selectCountByExample(example));//要货单号
                orderSplit.setHouseId(order.getHouseId());
                orderSplit.setApplyStatus(0);//后台审核状态：0生成中, 1申请中, 2通过, 3不通过, 4业主待支付补货材料 后台(材料员)
                orderSplit.setMemberId(member.getId());
                orderSplit.setMemberName(member.getName());
                orderSplit.setMobile(member.getMobile());
                orderSplit.setWorkerTypeId(member.getWorkerTypeId());
                orderSplit.setStorefrontId(order.getStorefontId());
                orderSplit.setCityId(order.getCityId());
                orderSplit.setOrderId(orderIds);
                orderSplit.setIsReservationDeliver("1");
                orderSplit.setReservationDeliverTime(DateUtil.toDate(reservationDeliverTime));
                billDjDeliverOrderSplitMapper.insert(orderSplit);
                example=new Example(OrderItem.class);
                example.createCriteria().andEqualTo(OrderItem.ORDER_ID,orderIds)
                        .andEqualTo(OrderItem.IS_RESERVATION_DELIVER,1);
                List<OrderItem> orderItems = djDeliverOrderItemMapper.selectByExample(example);
                orderItems.forEach(orderItem -> {
                    OrderSplitItem orderSplitItem = new OrderSplitItem();
                    orderSplitItem.setOrderSplitId(orderSplit.getId());
                    orderSplitItem.setProductId(orderItem.getProductId());
                    orderSplitItem.setProductSn(orderItem.getProductSn());
                    orderSplitItem.setProductName(orderItem.getProductName());
                    orderSplitItem.setPrice(orderItem.getPrice());
                    orderSplitItem.setAskCount(orderItem.getAskCount());
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
                    billDjDeliverOrderSplitItemMapper.insert(orderSplitItem);
                    orderItem.setReservationDeliverTime(DateUtil.toDate(reservationDeliverTime));
                    djDeliverOrderItemMapper.updateByPrimaryKey(orderItem);
                });
            }else {//预约发货页面预约发货
                JSONObject villageObj = JSONObject.parseObject(jsonStr);
                String objList = villageObj.getString("objList");
                JSONArray jsonArr = JSONArray.parseArray(objList);
                jsonArr.forEach(str -> {
                    JSONObject obj = (JSONObject) str;
                    String orderId = obj.getString("orderId");
                    String[] orderItemsIds = obj.getString("orderItemId").split(",");
                    Order djDeliverOrder = djDeliverOrderMapper.selectByPrimaryKey(orderId);
                    OrderSplit orderSplit = new OrderSplit();
                    Example example = new Example(OrderSplit.class);
                    orderSplit.setNumber("DJ" + 200000 + djDeliverOrderItemMapper.selectCountByExample(example));//要货单号
                    orderSplit.setHouseId(djDeliverOrder.getHouseId());
                    orderSplit.setApplyStatus(0);//后台审核状态：0生成中, 1申请中, 2通过, 3不通过, 4业主待支付补货材料 后台(材料员)
                    orderSplit.setMemberId(member.getId());
                    orderSplit.setMemberName(member.getName());
                    orderSplit.setMobile(member.getMobile());
                    orderSplit.setWorkerTypeId(member.getWorkerTypeId());
                    orderSplit.setStorefrontId(djDeliverOrder.getStorefontId());
                    orderSplit.setCityId(djDeliverOrder.getCityId());
                    orderSplit.setOrderId(orderId);
                    orderSplit.setIsReservationDeliver("1");
                    orderSplit.setReservationDeliverTime(DateUtil.toDate(reservationDeliverTime));
                    billDjDeliverOrderSplitMapper.insert(orderSplit);
                    for (String orderItemsId : orderItemsIds) {
                        OrderItem orderItem = djDeliverOrderItemMapper.selectByPrimaryKey(orderItemsId);
                        OrderSplitItem orderSplitItem = new OrderSplitItem();
                        orderSplitItem.setOrderSplitId(orderSplit.getId());
                        orderSplitItem.setProductId(orderItem.getProductId());
                        orderSplitItem.setProductSn(orderItem.getProductSn());
                        orderSplitItem.setProductName(orderItem.getProductName());
                        orderSplitItem.setPrice(orderItem.getPrice());
                        orderSplitItem.setAskCount(orderItem.getAskCount());
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
                        billDjDeliverOrderSplitItemMapper.insert(orderSplitItem);
                        orderItem.setReservationDeliverTime(DateUtil.toDate(reservationDeliverTime));
                        djDeliverOrderItemMapper.updateByPrimaryKey(orderItem);
                    }
                });
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
                if (appointmentDTOS.size() > 0) {
                    appointmentDTOS.forEach(appointmentDTO -> {
                        OrderSplit orderSplit = billDjDeliverOrderSplitMapper.selectByPrimaryKey(appointmentDTO.getOrderSplitId());
                        Order order = djDeliverOrderMapper.selectByPrimaryKey(orderSplit.getOrderId());
                        appointmentDTO.setOrderStatus(order.getOrderStatus());
                        if(CommonUtil.isEmpty(order.getOrderStatus())){
                            appointmentDTO.setOrderStatus("0");
                        }
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
