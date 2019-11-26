package com.dangjia.acg.service.delivery;

import com.dangjia.acg.api.app.house.HouseAPI;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.delivery.AppointmentDTO;
import com.dangjia.acg.dto.delivery.AppointmentListDTO;
import com.dangjia.acg.dto.delivery.OrderStorefrontDTO;
import com.dangjia.acg.dto.order.PaymentToBeMadeDTO;
import com.dangjia.acg.mapper.delivery.BillDjDeliverOrderSplitItemMapper;
import com.dangjia.acg.mapper.delivery.IBillDjDeliverOrderMapper;
import com.dangjia.acg.modle.deliver.Order;
import com.dangjia.acg.modle.house.House;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    /**
     * 待付款/已取消订单详情
     * @param orderId
     * @return
     */
    public ServerResponse queryPaymentToBeMade(String orderId) {
        try {
            Order order = iBillDjDeliverOrderMapper.selectByPrimaryKey(orderId);
            PaymentToBeMadeDTO paymentToBeMadeDTO=new PaymentToBeMadeDTO();
            paymentToBeMadeDTO.setTotalTransportationCost(order.getTotalTransportationCost());
            paymentToBeMadeDTO.setTotalStevedorageCost(order.getTotalStevedorageCost());
            paymentToBeMadeDTO.setTotalDiscountPrice(order.getTotalDiscountPrice());
            paymentToBeMadeDTO.setTotalAmount(order.getTotalAmount());
            paymentToBeMadeDTO.setOrderNumber(order.getOrderNumber());
            paymentToBeMadeDTO.setCreateDate(order.getCreateDate());
            paymentToBeMadeDTO.setModifyDate(order.getModifyDate());
            House house= houseAPI.selectHouseById(order.getHouseId());
            paymentToBeMadeDTO.setHouseId(house.getHouseId());
            paymentToBeMadeDTO.setHouseName( house.getResidential() + house.getBuilding() + "栋" + house.getUnit() + "单元" + house.getNumber() + "号");
            String imageAddress = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            List<OrderStorefrontDTO> orderStorefrontDTOS = iBillDjDeliverOrderMapper.queryPaymentToBeMade(orderId);
            List<AppointmentListDTO> appointmentListDTOS = new ArrayList<>();
            orderStorefrontDTOS.forEach(orderStorefrontDTO -> {
                orderStorefrontDTO.setStorefrontLogo(imageAddress+orderStorefrontDTO.getStorefrontLogo());
                List<AppointmentDTO> appointmentDTOS = iBillDjDeliverOrderMapper.queryAppointmentHump(orderStorefrontDTO.getOrderId());
                AppointmentListDTO appointmentListDTO = new AppointmentListDTO();
                appointmentDTOS.forEach(appointmentDTO -> {
                    appointmentDTO.setImage(imageAddress+appointmentDTO.getImage());
                });
                appointmentListDTO.setAppointmentDTOS(appointmentDTOS);
                appointmentListDTO.setOrderStorefrontDTO(orderStorefrontDTO);
                appointmentListDTOS.add(appointmentListDTO);
            });
            paymentToBeMadeDTO.setAppointmentListDTOS(appointmentListDTOS);
            if (appointmentListDTOS.size() <= 0)
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
            paymentToBeMadeDTO.setTotalTransportationCost(order.getTotalTransportationCost());
            paymentToBeMadeDTO.setTotalStevedorageCost(order.getTotalStevedorageCost());
            paymentToBeMadeDTO.setTotalDiscountPrice(order.getTotalDiscountPrice());
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
            List<OrderStorefrontDTO> orderStorefrontDTOS = iBillDjDeliverOrderMapper.queryHumpDetail(orderId);
            List<AppointmentListDTO> appointmentListDTOS = new ArrayList<>();
            orderStorefrontDTOS.forEach(orderStorefrontDTO -> {
                orderStorefrontDTO.setStorefrontLogo(imageAddress+orderStorefrontDTO.getStorefrontLogo());
                List<AppointmentDTO> appointmentDTOS = iBillDjDeliverOrderMapper.queryAppointmentHump(orderStorefrontDTO.getOrderId());
                AppointmentListDTO appointmentListDTO = new AppointmentListDTO();
                appointmentDTOS.forEach(appointmentDTO -> {
                    appointmentDTO.setImage(imageAddress+appointmentDTO.getImage());
                });
                appointmentListDTO.setAppointmentDTOS(appointmentDTOS);
                appointmentListDTO.setOrderStorefrontDTO(orderStorefrontDTO);
                appointmentListDTOS.add(appointmentListDTO);
            });
            paymentToBeMadeDTO.setAppointmentListDTOS(appointmentListDTOS);
            if (appointmentListDTOS.size() <= 0)
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




}
