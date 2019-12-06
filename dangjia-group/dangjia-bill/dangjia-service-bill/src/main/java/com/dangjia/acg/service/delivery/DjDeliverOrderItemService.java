package com.dangjia.acg.service.delivery;

import com.dangjia.acg.api.app.house.HouseAPI;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.delivery.AppointmentDTO;
import com.dangjia.acg.dto.delivery.AppointmentListDTO;
import com.dangjia.acg.dto.delivery.OrderStorefrontDTO;
import com.dangjia.acg.dto.order.PaymentToBeMadeDTO;
import com.dangjia.acg.mapper.delivery.BillDjDeliverOrderSplitItemMapper;
import com.dangjia.acg.mapper.delivery.IBillDjDeliverOrderMapper;
import com.dangjia.acg.mapper.pay.IBillBusinessOrderMapper;
import com.dangjia.acg.mapper.shoppingCart.IBillShoppingCartMapper;
import com.dangjia.acg.modle.deliver.Order;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.pay.BusinessOrder;
import com.dangjia.acg.modle.product.ShoppingCart;
import com.dangjia.acg.service.product.BillProductTemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.List;

@Service
public class DjDeliverOrderItemService {

    @Autowired
    private IBillDjDeliverOrderMapper iBillDjDeliverOrderMapper;
    @Autowired
    private ConfigUtil configUtil;
    private Logger logger = LoggerFactory.getLogger(DjDeliverOrderItemService.class);
    @Autowired
    private HouseAPI houseAPI;
    @Autowired
    private BillDjDeliverOrderSplitItemMapper billDjDeliverOrderSplitItemMapper;
    @Autowired
    private BillProductTemplateService billProductTemplateService;
    @Autowired
    private DjDeliverOrderService djDeliverOrderService;
    @Autowired
    private IBillBusinessOrderMapper iBillBusinessOrderMapper;
    @Autowired
    private IBillShoppingCartMapper iBillShoppingCartMapper;

    /**
     * 待付款/已取消订单详情
     * @param orderId
     * @return
     */
    public ServerResponse queryPaymentToBeMade(String orderId) {
        try {
            Order order = iBillDjDeliverOrderMapper.selectByPrimaryKey(orderId);
            PaymentToBeMadeDTO paymentToBeMadeDTO=new PaymentToBeMadeDTO();
            paymentToBeMadeDTO.setTotalTransportationCost(order.getTotalTransportationCost()!=null?order.getTotalTransportationCost():new BigDecimal(0));
            paymentToBeMadeDTO.setTotalStevedorageCost(order.getTotalStevedorageCost()!=null?order.getTotalTransportationCost():new BigDecimal(0));
            paymentToBeMadeDTO.setTotalDiscountPrice(order.getTotalDiscountPrice()!=null?order.getTotalDiscountPrice():new BigDecimal(0));
            paymentToBeMadeDTO.setActualPaymentPrice(order.getActualPaymentPrice()!=null?order.getActualPaymentPrice():new BigDecimal(0));
            paymentToBeMadeDTO.setTotalAmount(order.getTotalAmount()!=null?order.getTotalAmount():new BigDecimal(0));
            paymentToBeMadeDTO.setOrderNumber(order.getOrderNumber());
            paymentToBeMadeDTO.setCreateDate(order.getCreateDate());
            paymentToBeMadeDTO.setModifyDate(order.getModifyDate());
            House house= houseAPI.selectHouseById(order.getHouseId());
            Example example = new Example(ShoppingCart.class);
            example.createCriteria().andEqualTo(ShoppingCart.MEMBER_ID, house.getMemberId());
            paymentToBeMadeDTO.setShoppingCartsCount(iBillShoppingCartMapper.selectCountByExample(example));
            paymentToBeMadeDTO.setHouseId(house.getHouseId());
            paymentToBeMadeDTO.setHouseName( house.getResidential() + house.getBuilding() + "栋" + house.getUnit() + "单元" + house.getNumber() + "号");
            String imageAddress = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            List<OrderStorefrontDTO> orderStorefrontDTOS = iBillDjDeliverOrderMapper.queryPaymentToBeMade(orderId);
//            List<AppointmentListDTO> appointmentListDTOS = new ArrayList<>();
            orderStorefrontDTOS.forEach(orderStorefrontDTO -> {
                orderStorefrontDTO.setStorefrontIcon(imageAddress+orderStorefrontDTO.getStorefrontIcon());
                List<AppointmentDTO> appointmentDTOS = iBillDjDeliverOrderMapper.queryAppointmentHump(orderStorefrontDTO.getOrderId());
                AppointmentListDTO appointmentListDTO = new AppointmentListDTO();
                appointmentDTOS.forEach(appointmentDTO -> {
                    appointmentDTO.setImage(imageAddress+appointmentDTO.getImage());
                    if(!CommonUtil.isEmpty(appointmentDTO.getValueIdArr())) {
                        appointmentDTO.setValueNameArr(billProductTemplateService.getNewValueNameArr(appointmentDTO.getValueIdArr()).replaceAll(",", " "));
                    }
                });
                if("worker".equals(orderStorefrontDTO.getStorefrontType())){
                    Member member = djDeliverOrderService.queryWorker(orderStorefrontDTO.getHouseId(), orderStorefrontDTO.getWorkerTypeId());
                    if(member!=null) {
                        orderStorefrontDTO.setWorkerId(member.getId());
                        orderStorefrontDTO.setWorkerName(member.getName());
                    }
                }
                appointmentListDTO.setAppointmentDTOS(appointmentDTOS);
                appointmentListDTO.setOrderStorefrontDTO(orderStorefrontDTO);
                orderStorefrontDTO.setAppointmentDTOS(appointmentDTOS);
            });
            paymentToBeMadeDTO.setOrderStorefrontDTOS(orderStorefrontDTOS);
            if (orderStorefrontDTOS.size() <= 0)
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            return ServerResponse.createBySuccess("查询成功", paymentToBeMadeDTO);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("查询失败", e);
            return ServerResponse.createByErrorMessage("查询失败" + e);
        }
    }


    /**
     * 待发货详情
     * @param orderId
     * @return
     */
    public ServerResponse queryHumpDetail(String orderId) {
        try {
            Order order = iBillDjDeliverOrderMapper.selectByPrimaryKey(orderId);
            PaymentToBeMadeDTO paymentToBeMadeDTO=new PaymentToBeMadeDTO();
            paymentToBeMadeDTO.setTotalTransportationCost(order.getTotalTransportationCost()!=null?order.getTotalTransportationCost():new BigDecimal(0));
            paymentToBeMadeDTO.setTotalStevedorageCost(order.getTotalStevedorageCost()!=null?order.getTotalTransportationCost():new BigDecimal(0));
            paymentToBeMadeDTO.setTotalDiscountPrice(order.getTotalDiscountPrice()!=null?order.getTotalDiscountPrice():new BigDecimal(0));
            paymentToBeMadeDTO.setActualPaymentPrice(order.getActualPaymentPrice()!=null?order.getActualPaymentPrice():new BigDecimal(0));
            paymentToBeMadeDTO.setTotalAmount(order.getTotalAmount()!=null?order.getTotalAmount():new BigDecimal(0));
            paymentToBeMadeDTO.setOrderNumber(order.getOrderNumber());
            paymentToBeMadeDTO.setCreateDate(order.getCreateDate());
            paymentToBeMadeDTO.setModifyDate(order.getModifyDate());
            House house= houseAPI.selectHouseById(order.getHouseId());
            paymentToBeMadeDTO.setHouseId(house.getHouseId());
            paymentToBeMadeDTO.setHouseName( house.getResidential() + house.getBuilding() + "栋" + house.getUnit() + "单元" + house.getNumber() + "号");
            String imageAddress = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            List<String> strings = billDjDeliverOrderSplitItemMapper.querySplitDeliverId(orderId);
            paymentToBeMadeDTO.setSplitDeliverCount(strings.size());
            paymentToBeMadeDTO.setSplitDeliverId(CommonUtil.isEmpty(String.join(",",strings))?null:String.join(",",strings));
            List<OrderStorefrontDTO> orderStorefrontDTOS = iBillDjDeliverOrderMapper.queryHumpDetail(orderId);
            orderStorefrontDTOS.forEach(orderStorefrontDTO -> {
                orderStorefrontDTO.setStorefrontIcon(imageAddress+orderStorefrontDTO.getStorefrontIcon());
                List<AppointmentDTO> appointmentDTOS = iBillDjDeliverOrderMapper.queryAppointmentHump(orderStorefrontDTO.getOrderId());
                AppointmentListDTO appointmentListDTO = new AppointmentListDTO();
                appointmentDTOS.forEach(appointmentDTO -> {
                    appointmentDTO.setImage(imageAddress+appointmentDTO.getImage());
                    if(!CommonUtil.isEmpty(appointmentDTO.getValueIdArr())) {
                        appointmentDTO.setValueNameArr(billProductTemplateService.getNewValueNameArr(appointmentDTO.getValueIdArr()).replaceAll(",", " "));
                    }
                });
                if("worker".equals(orderStorefrontDTO.getStorefrontType())){
                    Member member = djDeliverOrderService.queryWorker(orderStorefrontDTO.getHouseId(), orderStorefrontDTO.getWorkerTypeId());
                    if(member!=null) {
                        orderStorefrontDTO.setWorkerId(member.getId());
                        orderStorefrontDTO.setWorkerName(member.getName());
                    }
                }
                appointmentListDTO.setAppointmentDTOS(appointmentDTOS);
                appointmentListDTO.setOrderStorefrontDTO(orderStorefrontDTO);
                orderStorefrontDTO.setAppointmentDTOS(appointmentDTOS);
            });
            paymentToBeMadeDTO.setOrderStorefrontDTOS(orderStorefrontDTOS);
            if (orderStorefrontDTOS.size() <= 0)
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            return ServerResponse.createBySuccess("查询成功", paymentToBeMadeDTO);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("查询失败", e);
            return ServerResponse.createByErrorMessage("查询失败" + e);
        }
    }


    /**
     * 删除订单
     * @param orderId
     * @return
     */
    public ServerResponse deleteOrder(String orderId) {
        try {
            Order order=new Order();
            order.setId(orderId);
            order.setDataStatus(1);
            iBillDjDeliverOrderMapper.updateByPrimaryKeySelective(order);
            return ServerResponse.createBySuccessMessage("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("查询失败", e);
            return ServerResponse.createByErrorMessage("删除失败" + e);
        }
    }


    /**
     * 订单快照
     * @param orderId
     * @return
     */
    public ServerResponse queryOrderSnapshot(String orderId) {
        try {
            Order order = iBillDjDeliverOrderMapper.selectByPrimaryKey(orderId);
            PaymentToBeMadeDTO paymentToBeMadeDTO=new PaymentToBeMadeDTO();
            paymentToBeMadeDTO.setTotalTransportationCost(order.getTotalTransportationCost()!=null?order.getTotalTransportationCost():new BigDecimal(0));
            paymentToBeMadeDTO.setTotalStevedorageCost(order.getTotalStevedorageCost()!=null?order.getTotalTransportationCost():new BigDecimal(0));
            paymentToBeMadeDTO.setTotalDiscountPrice(order.getTotalDiscountPrice()!=null?order.getTotalDiscountPrice():new BigDecimal(0));
            paymentToBeMadeDTO.setActualPaymentPrice(order.getActualPaymentPrice()!=null?order.getActualPaymentPrice():new BigDecimal(0));
            paymentToBeMadeDTO.setTotalAmount(order.getTotalAmount()!=null?order.getTotalAmount():new BigDecimal(0));
            paymentToBeMadeDTO.setTotalAmount(order.getTotalAmount());
            paymentToBeMadeDTO.setOrderNumber(order.getOrderNumber());
            paymentToBeMadeDTO.setCreateDate(order.getCreateDate());
            paymentToBeMadeDTO.setModifyDate(order.getModifyDate());
            House house= houseAPI.selectHouseById(order.getHouseId());
            paymentToBeMadeDTO.setHouseId(house.getHouseId());
            paymentToBeMadeDTO.setHouseName( house.getResidential() + house.getBuilding() + "栋" + house.getUnit() + "单元" + house.getNumber() + "号");
            String imageAddress = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            List<String> strings = billDjDeliverOrderSplitItemMapper.querySplitDeliverId(orderId);
            paymentToBeMadeDTO.setSplitDeliverCount(strings.size());
            paymentToBeMadeDTO.setSplitDeliverId(String.join(",",strings));
            List<OrderStorefrontDTO> orderStorefrontDTOS = iBillDjDeliverOrderMapper.queryOrderSnapshot(orderId);
            orderStorefrontDTOS.forEach(orderStorefrontDTO -> {
                orderStorefrontDTO.setStorefrontIcon(imageAddress+orderStorefrontDTO.getStorefrontIcon());
                List<AppointmentDTO> appointmentDTOS = iBillDjDeliverOrderMapper.queryOrderSnapshotHump(orderStorefrontDTO.getOrderId());
                AppointmentListDTO appointmentListDTO = new AppointmentListDTO();
                appointmentDTOS.forEach(appointmentDTO -> {
                    appointmentDTO.setImage(imageAddress+appointmentDTO.getImage());
                    if(!CommonUtil.isEmpty(appointmentDTO.getValueIdArr())) {
                        appointmentDTO.setValueNameArr(billProductTemplateService.getNewValueNameArr(appointmentDTO.getValueIdArr()).replaceAll(",", " "));
                    }
                });
                if("worker".equals(orderStorefrontDTO.getStorefrontType())){
                    Member member = djDeliverOrderService.queryWorker(orderStorefrontDTO.getHouseId(), orderStorefrontDTO.getWorkerTypeId());
                    if(member!=null) {
                        orderStorefrontDTO.setWorkerId(member.getId());
                        orderStorefrontDTO.setWorkerName(member.getName());
                    }
                }
                appointmentListDTO.setAppointmentDTOS(appointmentDTOS);
                appointmentListDTO.setOrderStorefrontDTO(orderStorefrontDTO);
                orderStorefrontDTO.setAppointmentDTOS(appointmentDTOS);
            });
            paymentToBeMadeDTO.setOrderStorefrontDTOS(orderStorefrontDTOS);
            if (orderStorefrontDTOS.size() <= 0)
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            return ServerResponse.createBySuccess("查询成功", paymentToBeMadeDTO);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("查询失败", e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 取消订单
     * @param orderId
     * @return
     */
    public ServerResponse setCancellationOrder(String orderId) {
        try {
            Order order = iBillDjDeliverOrderMapper.selectByPrimaryKey(orderId);
            Example example=new Example(BusinessOrder.class);
            example.createCriteria().andEqualTo(BusinessOrder.NUMBER,order.getBusinessOrderNumber());
            BusinessOrder businessOrder=new BusinessOrder();
            businessOrder.setState(4);
            iBillBusinessOrderMapper.updateByExampleSelective(businessOrder,example);
            return ServerResponse.createBySuccessMessage("取消订单成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("取消订单失败", e);
            return ServerResponse.createByErrorMessage("取消订单失败");
        }
    }




}
