package com.dangjia.acg.service.delivery;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.app.member.MemberAPI;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.delivery.AppointmentDTO;
import com.dangjia.acg.dto.delivery.AppointmentListDTO;
import com.dangjia.acg.dto.delivery.OrderStorefrontDTO;
import com.dangjia.acg.mapper.delivery.BillDjDeliverOrderSplitItemMapper;
import com.dangjia.acg.mapper.delivery.BillDjDeliverOrderSplitMapper;
import com.dangjia.acg.mapper.delivery.DjDeliverOrderItemMapper;
import com.dangjia.acg.mapper.delivery.DjDeliverOrderMapper;
import com.dangjia.acg.mapper.storeFront.BillStoreFrontProductMapper;
import com.dangjia.acg.modle.deliver.OrderItem;
import com.dangjia.acg.modle.deliver.OrderSplit;
import com.dangjia.acg.modle.deliver.OrderSplitItem;
import com.dangjia.acg.modle.delivery.DjDeliverOrder;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.storefront.StorefrontProduct;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

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
    private DjDeliverOrderMapper djDeliverOrderMapper;
    @Autowired
    private DjDeliverOrderItemMapper djDeliverOrderItemMapper;
    @Autowired
    private BillDjDeliverOrderSplitMapper billDjDeliverOrderSplitMapper;

    private Logger logger = LoggerFactory.getLogger(DjDeliverOrderService.class);

    @Autowired
    private BillStoreFrontProductMapper billStoreFrontProductMapper;

    @Autowired
    private MemberAPI memberAPI;

    @Autowired
    private BillDjDeliverOrderSplitItemMapper billDjDeliverOrderSplitItemMapper;


    /**
     * 我的预约查询
     *
     * @param pageDTO
     * @param houseId
     * @return
     */
    public ServerResponse queryAppointment(PageDTO pageDTO, String houseId) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<OrderStorefrontDTO> orderStorefrontDTOS = djDeliverOrderMapper.queryDjDeliverOrderStorefront(houseId);
            List<AppointmentListDTO> appointmentListDTOS = null;
            orderStorefrontDTOS.forEach(orderStorefrontDTO -> {
                AppointmentListDTO appointmentListDTO = new AppointmentListDTO();
                List<AppointmentDTO> appointmentDTOS = djDeliverOrderMapper.queryAppointment(orderStorefrontDTO.getOrderId());
                if(appointmentDTOS.size()>0) {
                    appointmentListDTO.setAppointmentDTOS(appointmentDTOS);
                    appointmentListDTO.setOrderStorefrontDTO(orderStorefrontDTO);
                    appointmentListDTOS.add(appointmentListDTO);
                }
            });
            if(appointmentListDTOS.size()<=0)
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(),ServerCode.NO_DATA.getDesc());
            PageInfo pageResult = new PageInfo(appointmentListDTOS);
            return ServerResponse.createBySuccess("查询成功",pageResult);
        } catch (Exception e) {
            logger.info("查询失败",e);
            return ServerResponse.createByErrorMessage("查询失败"+e);
        }
    }


    /**
     * 预约发货
     * @param jsonStr
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse insertAppointment(String userToken,String jsonStr) {
        try {
            Object object = memberAPI.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            JSONObject job = (JSONObject)object;
            Member member = job.toJavaObject(Member.class);
            JSONArray jsonArr = JSONArray.parseArray(jsonStr);
            jsonArr.forEach(str ->{
                JSONObject obj = (JSONObject) str;
                String orderId=obj.getString("orderId");
                Date reservationDeliverTime=obj.getDate("reservationDeliverTime");
                DjDeliverOrder djDeliverOrder = djDeliverOrderMapper.selectByPrimaryKey(orderId);
                OrderSplit orderSplit=new OrderSplit();
                Example example = new Example(OrderSplit.class);
                orderSplit.setNumber("DJ" + 200000 + djDeliverOrderItemMapper.selectCountByExample(example));//要货单号
                orderSplit.setHouseId(djDeliverOrder.getHouseId());
                orderSplit.setApplyStatus(0);//后台审核状态：0生成中, 1申请中, 2通过, 3不通过, 4业主待支付补货材料 后台(材料员)
                orderSplit.setMemberId(member.getId());
                orderSplit.setMemberName(member.getName());
                orderSplit.setMobile(member.getMobile());
                orderSplit.setWorkerTypeId(member.getWorkerTypeId());
                orderSplit.setStorefrontId(orderSplit.getStorefrontId());
                orderSplit.setCityId(djDeliverOrder.getCityId());
                orderSplit.setOrderId(orderId);
                billDjDeliverOrderSplitMapper.insert(orderSplit);
                String[] orderItemsIds = obj.getString("orderItemsId").split(",");
                for (String orderItemsId : orderItemsIds) {
                    OrderItem orderItem = djDeliverOrderItemMapper.selectByPrimaryKey(orderItemsId);
                    OrderSplitItem orderSplitItem=new OrderSplitItem();
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
                    orderSplitItem.setStorefrontId(orderSplitItem.getStorefrontId());
                    StorefrontProduct storefrontProduct = billStoreFrontProductMapper.selectByPrimaryKey(orderItem.getProductId());
                    orderSplitItem.setIsDeliveryInstall(storefrontProduct.getIsDeliveryInstall());
                    orderSplitItem.setOrderItemId(orderItem.getId());
                    orderSplitItem.setIsReservationDeliver(1);//是否需要预约(1是，0否）
                    orderSplitItem.setReservationDeliverTime(reservationDeliverTime);
                    billDjDeliverOrderSplitItemMapper.insert(orderSplitItem);
                    orderItem.setIsReservationDeliver("1");
                    orderItem.setReservationDeliverTime(reservationDeliverTime);
                    djDeliverOrderItemMapper.updateByPrimaryKey(orderItem);
                }
            });
            return ServerResponse.createBySuccessMessage("操作成功");
        } catch (Exception e) {
            logger.info("操作失败",e);
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }


    /**
     * 已预约
     * @param pageDTO
     * @param houseId
     * @return
     */
    public ServerResponse queryReserved(PageDTO pageDTO, String houseId) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<OrderStorefrontDTO> orderStorefrontDTOS = djDeliverOrderMapper.queryReservedStorefront(houseId);
            List<AppointmentListDTO> appointmentListDTOS = null;
            orderStorefrontDTOS.forEach(orderStorefrontDTO -> {
                AppointmentListDTO appointmentListDTO = new AppointmentListDTO();
                List<AppointmentDTO> appointmentDTOS = djDeliverOrderMapper.queryReserved(orderStorefrontDTO.getOrderId());
                if(appointmentDTOS.size()>0) {
                    appointmentListDTO.setAppointmentDTOS(appointmentDTOS);
                    appointmentListDTO.setOrderStorefrontDTO(orderStorefrontDTO);
                    appointmentListDTOS.add(appointmentListDTO);
                }
            });
            if(appointmentListDTOS.size()<=0)
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(),ServerCode.NO_DATA.getDesc());
            PageInfo pageResult = new PageInfo(appointmentListDTOS);
            return ServerResponse.createBySuccess("查询成功",pageResult);
        } catch (Exception e) {
            logger.info("查询失败",e);
            return ServerResponse.createByErrorMessage("查询失败"+e);
        }
    }


    /**
     * 取消预约
     * @param orderSplitId
     * @return
     */
    public ServerResponse updateReserved(String orderSplitId) {
        try {
            OrderSplit orderSplit = billDjDeliverOrderSplitMapper.selectByPrimaryKey(orderSplitId);
            djDeliverOrderItemMapper.updateDjDeliverOrderItemByOrderId(orderSplit.getOrderId());
            djDeliverOrderMapper.cancelBooking(orderSplitId);
            return ServerResponse.createBySuccessMessage("操作成功");
        } catch (Exception e) {
            logger.info("操作失败",e);
            return ServerResponse.createByErrorMessage("操作失败"+e);
        }
    }





}
