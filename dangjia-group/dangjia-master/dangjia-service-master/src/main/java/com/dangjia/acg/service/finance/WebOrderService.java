package com.dangjia.acg.service.finance;

import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.MathUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.deliver.*;
import com.dangjia.acg.mapper.activity.IActivityRedPackMapper;
import com.dangjia.acg.mapper.activity.IActivityRedPackRecordMapper;
import com.dangjia.acg.mapper.activity.IActivityRedPackRuleMapper;
import com.dangjia.acg.mapper.core.IHouseFlowMapper;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.design.IQuantityRoomMapper;
import com.dangjia.acg.mapper.pay.IBusinessOrderMapper;
import com.dangjia.acg.mapper.pay.IPayOrderMapper;
import com.dangjia.acg.mapper.repair.IMendOrderMapper;
import com.dangjia.acg.mapper.store.IStoreMapper;
import com.dangjia.acg.modle.activity.ActivityRedPack;
import com.dangjia.acg.modle.activity.ActivityRedPackRecord;
import com.dangjia.acg.modle.activity.ActivityRedPackRule;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.design.QuantityRoom;
import com.dangjia.acg.modle.pay.BusinessOrder;
import com.dangjia.acg.modle.pay.PayOrder;
import com.dangjia.acg.modle.repair.MendOrder;
import com.dangjia.acg.modle.store.Store;
import com.dangjia.acg.util.StringTool;
import com.dangjia.acg.util.Utils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ysl
 * Date: 2019/1/24 0008
 * Time: 16:48
 */
@Service
public class WebOrderService {
    private static Logger logger = LoggerFactory.getLogger(WebOrderService.class);
    @Autowired
    private IBusinessOrderMapper iBusinessOrderMapper;
    @Autowired
    private IActivityRedPackRecordMapper iActivityRedPackRecordMapper;
    @Autowired
    private IActivityRedPackMapper iActivityRedPackMapper;

    @Autowired
    private IPayOrderMapper iPayOrderMapper;
    @Autowired
    private IStoreMapper iStoreMapper;
    @Autowired
    private IQuantityRoomMapper iQuantityRoomMapper;


    @Autowired
    private IHouseFlowMapper houseFlowMapper;
    @Autowired
    private IWorkerTypeMapper workerTypeMapper;
    @Autowired
    private IMendOrderMapper mendOrderMapper;

    @Autowired
    private ConfigUtil configUtil;

    @Autowired
    private IActivityRedPackRuleMapper activityRedPackRuleMapper;
    /**
     * 所有订单流水
     *
     * @param pageDTO   分页
     * @param state     处理状态 -1全部， 1刚生成(可编辑),2去支付(不修改),3已支付
     * @param searchKey 模糊搜索：订单号,房屋信息,电话,支付单号(业务订单号)
     * @return
     */
    public ServerResponse getAllOrders(PageDTO pageDTO,String cityId, Integer state, String searchKey, String beginDate,String endDate) {
        try {
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            if (state == null) {
                state = -1;
            }
            if (!CommonUtil.isEmpty(beginDate) && !CommonUtil.isEmpty(endDate)) {
                if (beginDate.equals(endDate)) {
                    beginDate = beginDate + " " + "00:00:00";
                    endDate = endDate + " " + "23:59:59";
                }
            }
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<WebOrderDTO> orderList = iBusinessOrderMapper.getWebOrderList(cityId,state, searchKey,beginDate,endDate);
            PageInfo pageResult = new PageInfo(orderList);
            for (WebOrderDTO webOrderDTO : orderList) {
                if(!CommonUtil.isEmpty(webOrderDTO.getPayOrderNumber())) {
                    PayOrder payOrder= iPayOrderMapper.getByNumber(webOrderDTO.getPayOrderNumber());
                    if(payOrder!=null){
                        webOrderDTO.setCreateDate(payOrder.getCreateDate());
                        webOrderDTO.setPayType(Integer.parseInt(payOrder.getPayState()));
                    }
                }
                if(!CommonUtil.isEmpty(webOrderDTO.getStoreName())) {
                    Example example=new Example(Store.class);
                    example.createCriteria().andLike(Store.VILLAGES,"%" + webOrderDTO.getStoreName() + "%");
                    List<Store> stores = iStoreMapper.selectByExample(example);
                    if(stores.size()>0){
                        webOrderDTO.setStoreName(stores.size()>0?stores.get(0).getStoreName():null);
                    }
                }

                webOrderDTO.setImage(Utils.getImageAddress(address,webOrderDTO.getImage()));
                if (webOrderDTO.getState() == 3) {
                    if (webOrderDTO.getType() == 1) {//工序支付
                        HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(webOrderDTO.getTaskId());
                        if (houseFlow != null) {
                            webOrderDTO.setTypeText(workerTypeMapper.selectByPrimaryKey(houseFlow.getWorkerTypeId()).getName() + "抢单");
                        }else{
                            webOrderDTO.setTypeText("工序抢单");
                        }
                    }
                    if (webOrderDTO.getType() == 2) {//补货补人工
                        MendOrder mendOrder = mendOrderMapper.selectByPrimaryKey(webOrderDTO.getTaskId());
                        if (mendOrder != null) {
                            WorkerType workerType=workerTypeMapper.selectByPrimaryKey(mendOrder.getWorkerTypeId());
                            if (mendOrder.getType() == 0) {
                                if(workerType.getType()==3){
                                    webOrderDTO.setTypeText(workerType.getName() + "补包工包料");
                                }else{
                                    webOrderDTO.setTypeText(workerType.getName() + "补材料");
                                }

                            } else {
                                webOrderDTO.setTypeText(workerType.getName() + "补人工");
                            }
                        }
                    }
                    if (webOrderDTO.getType() == 4) {//只付材料
                        HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(webOrderDTO.getTaskId());
                        if (houseFlow != null) {
                            webOrderDTO.setTypeText(workerTypeMapper.selectByPrimaryKey(houseFlow.getWorkerTypeId()).getName() + "付材料");
                        }else{
                            webOrderDTO.setTypeText("工序付材料");
                        }
                    }
                    if (webOrderDTO.getType() == 5) {
                        webOrderDTO.setTypeText("验房分销");
                    }
                    // 换货单
                    if (webOrderDTO.getType() == 6) {
                        webOrderDTO.setTypeText("换货补差价");
                    } // 换货单
                    if (webOrderDTO.getType() == 7) {
                        webOrderDTO.setTypeText("设计/精算改图");
                    }
                    if (webOrderDTO.getType() == 8) {
                        webOrderDTO.setTypeText("业主购买");
                    }
                    if (webOrderDTO.getType() == 9) {
                        webOrderDTO.setTypeText("工人保险");
                    }
                    if (webOrderDTO.getType() == 10) {
                        webOrderDTO.setTypeText("维保订单");
                    }
                }
                //优惠卷
                ActivityRedPackRecord activityRedPackRecord = iActivityRedPackRecordMapper.selectByPrimaryKey(webOrderDTO.getRedPackId());
                if (activityRedPackRecord != null) {
                    ActivityRedPack activityRedPack = iActivityRedPackMapper.selectByPrimaryKey(activityRedPackRecord.getRedPackId());
                    webOrderDTO.setRedPackName(activityRedPack.getName());
                    if(activityRedPack.getSourceType()==2){
                        webOrderDTO.setRedPackType("店铺券");
                    }else{
                        webOrderDTO.setRedPackType("平台券");
                    }
                    webOrderDTO.setRedPackNumber(activityRedPackRecord.getPackNum());
                }
            }
            pageResult.setList(orderList);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 财务--订单管理--订单详情
     * @param businessNumber 业务支付单号
     * @return
     */
    public ServerResponse getOrderItemInfoList( String businessNumber){
        try{
            //1.查询订单基础信息
            BusinessOrderInfoDTO businessOrderInfoDTO=iBusinessOrderMapper.selectBusinessOrderInfo(businessNumber);
            if(businessOrderInfoDTO==null){
                return ServerResponse.createByErrorMessage("未找到符合条件的订单");
            }
            //查询楼层数
            //查询房子信息，获取房子对应的楼层
            QuantityRoom quantityRoom=iQuantityRoomMapper.getQuantityRoom(businessOrderInfoDTO.getHouseId(),0);
            if(quantityRoom!=null&& StringUtils.isNotBlank(quantityRoom.getId())){
                Integer elevator=quantityRoom.getElevator();//是否电梯房
                String floor=quantityRoom.getFloor();//楼层
                businessOrderInfoDTO.setFloor(floor);//楼层
                businessOrderInfoDTO.setElevator(elevator);//是否电梯房
            }
            //优惠卷
            ActivityRedPackRecord activityRedPackRecord = iActivityRedPackRecordMapper.selectByPrimaryKey(businessOrderInfoDTO.getRedPackId());
            if (activityRedPackRecord != null) {
                ActivityRedPack activityRedPack = iActivityRedPackMapper.selectByPrimaryKey(activityRedPackRecord.getRedPackId());
                businessOrderInfoDTO.setTotalDiscountPrice(businessOrderInfoDTO.getRedPackAmount());
                if(activityRedPack.getSourceType()==2){
                    businessOrderInfoDTO.setDiscountType("店铺券");
                }else{
                    businessOrderInfoDTO.setDiscountType("平台券");
                }
                businessOrderInfoDTO.setDiscountNumber(activityRedPackRecord.getPackNum());
                if(activityRedPack.getType()==0){
                    businessOrderInfoDTO.setDiscountName( "满"+activityRedPack.getSatisfyMoney()+"减"+activityRedPack.getMoney());//优惠卷方式
                    businessOrderInfoDTO.setDiscountPrice(activityRedPack.getMoney()+"元");//优惠卷金额
                }else if(activityRedPack.getType()==1){
                    if(activityRedPack.getSatisfyMoney()!=null&&activityRedPack.getSatisfyMoney().doubleValue()>0){
                        businessOrderInfoDTO.setDiscountName( "满"+activityRedPack.getSatisfyMoney()+"打"+activityRedPack.getMoney()+"折");//优惠卷方式
                        businessOrderInfoDTO.setDiscountPrice( activityRedPack.getMoney()+"元");//优惠卷金额
                    }else{
                        businessOrderInfoDTO.setDiscountName(  "无门槛");//优惠卷方式
                        businessOrderInfoDTO.setDiscountPrice(activityRedPack.getMoney()+"折");//优惠卷金额
                    }
                }else{
                    businessOrderInfoDTO.setDiscountName(  "无门槛");//优惠卷方式
                    businessOrderInfoDTO.setDiscountPrice(activityRedPack.getMoney()+"元");//优惠卷金额
                }
            }
            //查询订单子单汇总金额，按店铺划分
            List<OrderDTO> orderInfoList=iBusinessOrderMapper.selectOrderInfoList(businessNumber);
            if(orderInfoList!=null){
                for(OrderDTO order:orderInfoList){
                    //查询每个店铺下的商品详情
                    List<OrderItemDTO> orderItemList = iBusinessOrderMapper.queryOrderItemList(order.getOrderId());
                    Double totalPrice=0d;//商品总额（单价*数量）
                    String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
                    for (OrderItemDTO orderItemDTO : orderItemList) {
                        orderItemDTO.setImage(StringTool.getImage(orderItemDTO.getImage(),imageAddress));
                        totalPrice= MathUtil.add(totalPrice,orderItemDTO.getTotalPrice());
                    }
                    order.setOrderItemList(orderItemList);
                    order.setTotalPrice(totalPrice);
                    if(order.getTotalDiscountPrice()!=null&&order.getTotalDiscountPrice()>0){
                        order.setDiscountName(businessOrderInfoDTO.getDiscountName());
                        order.setDiscountPrice(businessOrderInfoDTO.getDiscountPrice());
                        order.setDiscountNumber(businessOrderInfoDTO.getBusinessNumber());
                        order.setDiscountType(businessOrderInfoDTO.getDiscountType());
                    }
                }
            }
            businessOrderInfoDTO.setOrderInfoList(orderInfoList);
            return ServerResponse.createBySuccess("查询成功",businessOrderInfoDTO);
        }catch (Exception e){
            logger.error("查询失败",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 获取订单详情
     * @param businessNumber
     * @return
     */
    public ServerResponse getOrderItem(PageDTO pageDTO,String businessNumber){
        String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<OrderItemByDTO> orderItemList= iBusinessOrderMapper.getOrderItem(businessNumber);
        PageInfo pageResult = new PageInfo(orderItemList);
        for (OrderItemByDTO orderItemByDTO : orderItemList) {
            orderItemByDTO.setImage(imageAddress+orderItemByDTO.getImage());
        }
        pageResult.setList(orderItemList);
        return ServerResponse.createBySuccess("查询成功", pageResult);

    }
    /**
     * 获取订单优惠券详情
     * @param businessId
     * @return
     */
    public ServerResponse getOrderRedItem(String businessId){
        BusinessOrder businessOrder = iBusinessOrderMapper.selectByPrimaryKey(businessId);
        ActivityRedPackRecord activityRedPackRecord = iActivityRedPackRecordMapper.getRedPackRecordsByBusinessOrderNumber(businessOrder.getNumber());

        String red;
        if(activityRedPackRecord!=null) {
            ActivityRedPack activityRedPack = iActivityRedPackMapper.selectByPrimaryKey(activityRedPackRecord.getRedPackId());
            ActivityRedPackRule activityRedPackRule = activityRedPackRuleMapper.selectByPrimaryKey(activityRedPackRecord.getRedPackRuleId());
            //0为减免金额券 1 为折扣券 2代金券'
            if (activityRedPack.getType() == 0) {
                red = "满" + activityRedPackRule.getSatisfyMoney().stripTrailingZeros().toPlainString() + "减" + activityRedPackRule.getMoney().stripTrailingZeros().toPlainString();
            } else if (activityRedPack.getType() == 1) {
                red = "折扣券" + activityRedPackRule.getMoney().stripTrailingZeros().toPlainString() + "折";
            } else {
                red = "代金券" + activityRedPackRule.getMoney().stripTrailingZeros().toPlainString() + "元";
            }
        }else {
            red="无";
        }
        Map map=new HashMap<>();
        map.put("red",red);
        map.put("redPackAmount",businessOrder.getDiscountsPrice());
        return ServerResponse.createBySuccess("查询成功", map);

    }
    /**
     * 查询到时业主未审核申请
     */
    public void autoOrderCancel(){
        BusinessOrder businessOrder=new BusinessOrder();
        businessOrder.setId(null);
        businessOrder.setCreateDate(null);
        businessOrder.setState(4);
        Example example = new Example(BusinessOrder.class);
        example.createCriteria().andEqualTo(BusinessOrder.STATE, 1);
        iBusinessOrderMapper.updateByExampleSelective(businessOrder,example);
    }
}

