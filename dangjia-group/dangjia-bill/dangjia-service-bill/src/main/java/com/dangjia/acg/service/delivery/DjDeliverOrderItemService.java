package com.dangjia.acg.service.delivery;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.app.house.HouseAPI;
import com.dangjia.acg.api.app.member.MemberAPI;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.delivery.AppointmentDTO;
import com.dangjia.acg.dto.delivery.AppointmentListDTO;
import com.dangjia.acg.dto.delivery.OrderStorefrontDTO;
import com.dangjia.acg.dto.order.AcceptanceEvaluationListDTO;
import com.dangjia.acg.dto.order.PaymentToBeMadeDTO;
import com.dangjia.acg.mapper.delivery.BillDjDeliverOrderSplitItemMapper;
import com.dangjia.acg.mapper.delivery.BillDjDeliverSplitDeliverMapper;
import com.dangjia.acg.mapper.delivery.IBillDjDeliverOrderMapper;
import com.dangjia.acg.mapper.delivery.IBillDjStoreActivityMapper;
import com.dangjia.acg.mapper.member.IBillMemberAddressMapper;
import com.dangjia.acg.mapper.order.IBillDjAcceptanceEvaluationMapper;
import com.dangjia.acg.mapper.order.IBillHouseMapper;
import com.dangjia.acg.mapper.pay.IBillBusinessOrderMapper;
import com.dangjia.acg.mapper.sale.IBillMemberMapper;
import com.dangjia.acg.mapper.shoppingCart.IBillShoppingCartMapper;
import com.dangjia.acg.modle.activity.DjStoreActivity;
import com.dangjia.acg.modle.deliver.Order;
import com.dangjia.acg.modle.deliver.SplitDeliver;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.member.MemberAddress;
import com.dangjia.acg.modle.order.DjAcceptanceEvaluation;
import com.dangjia.acg.modle.pay.BusinessOrder;
import com.dangjia.acg.modle.product.ShoppingCart;
import com.dangjia.acg.service.product.BillProductTemplateService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
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
    private IBillMemberAddressMapper billMemberAddressMapper;
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
    @Autowired
    private MemberAPI memberAPI;
    @Autowired
    private IBillDjAcceptanceEvaluationMapper iBillDjAcceptanceEvaluationMapper;
    @Autowired
    private BillDjDeliverSplitDeliverMapper billDjDeliverSplitDeliverMapper;
    @Autowired
    private IBillHouseMapper houseMapper;
    @Autowired
    private IBillDjStoreActivityMapper iBillDjStoreActivityMapper;
    @Autowired
    private IBillMemberMapper iBillMemberMapper;

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
            paymentToBeMadeDTO.setTotalStevedorageCost(order.getTotalStevedorageCost()!=null?order.getTotalStevedorageCost():new BigDecimal(0));
            paymentToBeMadeDTO.setTotalDiscountPrice(order.getTotalDiscountPrice()!=null?order.getTotalDiscountPrice():new BigDecimal(0));
            paymentToBeMadeDTO.setActualPaymentPrice(order.getActualPaymentPrice()!=null?order.getActualPaymentPrice():new BigDecimal(0));
            paymentToBeMadeDTO.setTotalAmount(order.getTotalAmount()!=null?order.getTotalAmount():new BigDecimal(0));
            paymentToBeMadeDTO.setOrderNumber(order.getOrderNumber());
            paymentToBeMadeDTO.setCreateDate(order.getCreateDate());
            paymentToBeMadeDTO.setModifyDate(order.getModifyDate());
            paymentToBeMadeDTO.setOrderSource(order.getOrderSource());
            paymentToBeMadeDTO.setBusinessOrderNumber(order.getBusinessOrderNumber());
            paymentToBeMadeDTO.setOrderId(orderId);
            String memberId="";
            MemberAddress memberAddress=billMemberAddressMapper.selectByPrimaryKey(order.getAddressId());
            if(memberAddress!=null){
                paymentToBeMadeDTO.setHouseId(memberAddress.getHouseId());
                paymentToBeMadeDTO.setHouseName( memberAddress.getAddress());
                memberId=memberAddress.getMemberId();
            }else{
                House house=houseMapper.selectByPrimaryKey(order.getHouseId());
                if(house!=null){
                    paymentToBeMadeDTO.setHouseId(house.getHouseId());
                    paymentToBeMadeDTO.setHouseName( house.getResidential() + house.getBuilding() + "栋" + house.getUnit() + "单元" + house.getNumber() + "号");

                }
            }
            Example example = new Example(ShoppingCart.class);
            example.createCriteria().andEqualTo(ShoppingCart.MEMBER_ID, memberId);
            paymentToBeMadeDTO.setShoppingCartsCount(iBillShoppingCartMapper.selectCountByExample(example));
            List<OrderStorefrontDTO> orderStorefrontDTOS = iBillDjDeliverOrderMapper.queryPaymentToBeMade(orderId);
            this.duplicatedCode(orderStorefrontDTOS);
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
     * 库存详情
     * @param orderId
     * @return
     */
    public ServerResponse queryHumpDetail(String orderId) {
        try {
            String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            Order order = iBillDjDeliverOrderMapper.selectByPrimaryKey(orderId);
            PaymentToBeMadeDTO paymentToBeMadeDTO=new PaymentToBeMadeDTO();
            if("6".equals(order.getOrderSource())){
                Example example=new Example(Order.class);
                Example.Criteria criteria = example.createCriteria().andEqualTo(Order.DATA_STATUS, 0)
                        .andEqualTo(Order.ORDER_STATUS, 9);
                if(org.apache.commons.lang.StringUtils.isNotBlank(order.getParentOrderId())){
                    criteria.andEqualTo(Order.PARENT_ORDER_ID,order.getParentOrderId())
                            .orEqualTo(Order.ID,order.getParentOrderId());
                    Order parentOrder = iBillDjDeliverOrderMapper.selectByPrimaryKey(order.getParentOrderId());
                    paymentToBeMadeDTO.setOrderGenerationTime(parentOrder.getOrderGenerationTime());
                }else{
                    criteria.andEqualTo(Order.PARENT_ORDER_ID,order.getId())
                            .orEqualTo(Order.ID,order.getId());
                    paymentToBeMadeDTO.setOrderGenerationTime(order.getOrderGenerationTime());
                }
                DjStoreActivity djStoreActivity =
                        iBillDjStoreActivityMapper.selectByPrimaryKey(order.getStoreActivityId());
                List<Order> orders = iBillDjDeliverOrderMapper.selectByExample(example);
                List<String> list=new ArrayList<>();
                orders.forEach(order1 -> {
                    Member member = iBillMemberMapper.selectByPrimaryKey(order1.getMemberId());
                    list.add(imageAddress+member.getHead());
                });
                paymentToBeMadeDTO.setHeadList(list);
                paymentToBeMadeDTO.setShortPeople(djStoreActivity.getSpellGroup()-orders.size());
            }
            paymentToBeMadeDTO.setTotalTransportationCost(order.getTotalTransportationCost()!=null?order.getTotalTransportationCost():new BigDecimal(0));
            paymentToBeMadeDTO.setTotalStevedorageCost(order.getTotalStevedorageCost()!=null?order.getTotalTransportationCost():new BigDecimal(0));
            paymentToBeMadeDTO.setTotalDiscountPrice(order.getTotalDiscountPrice()!=null?order.getTotalDiscountPrice():new BigDecimal(0));
            paymentToBeMadeDTO.setActualPaymentPrice(order.getActualPaymentPrice()!=null?order.getActualPaymentPrice():new BigDecimal(0));
            paymentToBeMadeDTO.setTotalAmount(order.getTotalAmount()!=null?order.getTotalAmount():new BigDecimal(0));
            paymentToBeMadeDTO.setOrderNumber(order.getOrderNumber());
            paymentToBeMadeDTO.setCreateDate(order.getCreateDate());
            paymentToBeMadeDTO.setModifyDate(order.getModifyDate());
            MemberAddress memberAddress=billMemberAddressMapper.selectByPrimaryKey(order.getAddressId());
            if(memberAddress!=null){
                paymentToBeMadeDTO.setHouseId(memberAddress.getHouseId());
                paymentToBeMadeDTO.setHouseName( memberAddress.getAddress());

            }else{
                House house=houseMapper.selectByPrimaryKey(order.getHouseId());
                if(house!=null){
                    paymentToBeMadeDTO.setHouseId(house.getHouseId());
                    paymentToBeMadeDTO.setHouseName( house.getResidential() + house.getBuilding() + "栋" + house.getUnit() + "单元" + house.getNumber() + "号");

                }
            }
            List<String> strings = billDjDeliverOrderSplitItemMapper.querySplitDeliverId(orderId);
            paymentToBeMadeDTO.setSplitDeliverCount(strings.size());
            paymentToBeMadeDTO.setSplitDeliverId(CommonUtil.isEmpty(String.join(",",strings))?null:String.join(",",strings));
            List<OrderStorefrontDTO> orderStorefrontDTOS = iBillDjDeliverOrderMapper.queryHumpDetail(orderId);
            this.duplicatedCode(orderStorefrontDTOS);
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

    private void duplicatedCode(List<OrderStorefrontDTO> orderStorefrontDTOS){
        String imageAddress = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
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
            BusinessOrder businessOrder=iBillBusinessOrderMapper.selectOneByExample(example);
            businessOrder.setId(null);
            businessOrder.setCreateDate(null);
            businessOrder.setDataStatus(null);
            businessOrder.setState(4);
            iBillBusinessOrderMapper.updateByPrimaryKey(businessOrder);
            if(order.getType()==1&&order.getWorkerId()!=null&&"firstOrder".equals(order.getWorkerId())){//若为装修房子订单，首次提交的订单取消时，需将地址中绑定的房子解除，房子状态改为用户已撤回
                //判断是否为设计、精算提交的订单
                MemberAddress memberAddress=billMemberAddressMapper.selectByPrimaryKey(order.getAddressId());
                if(memberAddress!=null){//解除房子地址的绑定
                    memberAddress.setHouseId(null);
                    memberAddress.setRenovationType(0);//改为非装修地址
                    memberAddress.setModifyDate(new Date());
                    billMemberAddressMapper.updateByPrimaryKey(memberAddress);
                }
                House house=houseMapper.selectByPrimaryKey(order.getHouseId());
                if(house!=null){//撤销房子信息
                    house.setMemberId(null);
                    house.setDataStatus(1);
                    house.setType(3);
                    house.setShowHouse(0);
                    house.setModifyDate(new Date());
                    houseMapper.updateByPrimaryKey(house);
                }
                example=new Example(House.class);
                example.createCriteria().andEqualTo(House.MEMBER_ID,businessOrder.getMemberId());
                List<House> houseList=houseMapper.selectByExample(example);
                if(houseList!=null&&houseList.size()>0){//设置默认的房产地址
                    house=houseList.get(0);
                    house.setIsSelect(1);
                    houseMapper.updateByPrimaryKeySelective(house);
                }

            }

            return ServerResponse.createBySuccessMessage("取消订单成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("取消订单失败", e);
            return ServerResponse.createByErrorMessage("取消订单失败");
        }
    }


    /**
     * 验收评价列表
     * @param splitDeliverId
     * @return
     */
    public ServerResponse queryAcceptanceEvaluationList(String splitDeliverId, PageDTO pageDTO) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<AcceptanceEvaluationListDTO> acceptanceEvaluationListDTOS = billDjDeliverSplitDeliverMapper.queryAcceptanceEvaluationList(splitDeliverId);
            acceptanceEvaluationListDTOS.forEach(acceptanceEvaluationListDTO -> {
                if(StringUtils.isNotBlank(acceptanceEvaluationListDTO.getValueIdArr())){
                    acceptanceEvaluationListDTO.setValueNameArr(billProductTemplateService.getNewValueNameArr(acceptanceEvaluationListDTO.getValueIdArr()).replaceAll(",", " "));
                }
            });
            if(acceptanceEvaluationListDTOS.size()<=0)
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(),ServerCode.NO_DATA.getDesc());
            PageInfo pageResult = new PageInfo(acceptanceEvaluationListDTOS);
            return ServerResponse.createBySuccess("查询成功",pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 验收评价
     * @param userToken
     * @param jsonStr
     * @return
     */
    public ServerResponse setAcceptanceEvaluation(String userToken, String jsonStr, String splitDeliverId) {
        try {
            Object object = memberAPI.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            JSONObject job = (JSONObject)object;
            Member member = job.toJavaObject(Member.class);
            JSONArray jsonArr = JSONArray.parseArray(jsonStr);
            jsonArr.forEach(str -> {
                JSONObject obj = (JSONObject) str;
                DjAcceptanceEvaluation djAcceptanceEvaluation=new DjAcceptanceEvaluation();
                djAcceptanceEvaluation.setSplitItemId(obj.getString("splitItemId"));
                djAcceptanceEvaluation.setDataStatus(0);
                djAcceptanceEvaluation.setContent(obj.getString("content"));
                djAcceptanceEvaluation.setImage(obj.getString("image"));
                djAcceptanceEvaluation.setMemberId(member.getId());
                djAcceptanceEvaluation.setProductId(obj.getString("productId"));
                djAcceptanceEvaluation.setStar(obj.getInteger("star"));
                djAcceptanceEvaluation.setState(1);
                iBillDjAcceptanceEvaluationMapper.insert(djAcceptanceEvaluation);
            });
            SplitDeliver splitDeliver=new SplitDeliver();
            splitDeliver.setId(splitDeliverId);
            splitDeliver.setShippingState(8);
            billDjDeliverSplitDeliverMapper.updateByPrimaryKeySelective(splitDeliver);
            return ServerResponse.createBySuccessMessage("评价成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("评价失败");
        }
    }




}
