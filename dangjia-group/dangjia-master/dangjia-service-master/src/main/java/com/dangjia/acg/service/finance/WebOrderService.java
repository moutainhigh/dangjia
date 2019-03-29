package com.dangjia.acg.service.finance;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.deliver.WebOrderDTO;
import com.dangjia.acg.mapper.activity.IActivityRedPackMapper;
import com.dangjia.acg.mapper.activity.IActivityRedPackRecordMapper;
import com.dangjia.acg.mapper.core.IHouseFlowMapper;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.pay.IBusinessOrderMapper;
import com.dangjia.acg.mapper.pay.IPayOrderMapper;
import com.dangjia.acg.mapper.repair.IMendOrderMapper;
import com.dangjia.acg.modle.activity.ActivityRedPack;
import com.dangjia.acg.modle.activity.ActivityRedPackRecord;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.pay.BusinessOrder;
import com.dangjia.acg.modle.pay.PayOrder;
import com.dangjia.acg.modle.repair.MendOrder;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * ysl
 * Date: 2019/1/24 0008
 * Time: 16:48
 */
@Service
public class WebOrderService {
    @Autowired
    private IMemberMapper memberMapper;
    @Autowired
    private IHouseMapper iHouseMapper;
    @Autowired
    private IBusinessOrderMapper iBusinessOrderMapper;
    @Autowired
    private IActivityRedPackRecordMapper iActivityRedPackRecordMapper;
    @Autowired
    private IActivityRedPackMapper iActivityRedPackMapper;
    @Autowired
    private IHouseFlowMapper houseFlowMapper;
    @Autowired
    private IWorkerTypeMapper workerTypeMapper;
    @Autowired
    private IMendOrderMapper mendOrderMapper;
    @Autowired
    private IPayOrderMapper payOrderMapper;

    /*所有订单流水*/
    public ServerResponse getAllOrders(PageDTO pageDTO, String likeMobile, String likeAddress) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());

            List<WebOrderDTO> webOrderDTOList = new ArrayList<>();
            List<BusinessOrder> orderList = iBusinessOrderMapper.getAllBusinessOrder(likeMobile, likeAddress);

            for (BusinessOrder businessOrder : orderList) {
                WebOrderDTO webOrderDTO = new WebOrderDTO();
                webOrderDTO.setOrderId(businessOrder.getNumber());
                webOrderDTO.setTotalAmount(businessOrder.getTotalPrice());
                webOrderDTO.setState("待支付");
                if (businessOrder.getState() == 2) {
                    webOrderDTO.setState("支付中");
                }
                if (businessOrder.getState() == 3) {
                    PayOrder payOrder = payOrderMapper.getByNumber(businessOrder.getPayOrderNumber());
                    if(payOrder.getPayState().equals("1")){
                        webOrderDTO.setState("微信已支付");
                    }else {
                        webOrderDTO.setState("支付宝已支付");
                    }

                    if(businessOrder.getType() == 1){//工序支付
                        HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(businessOrder.getTaskId());
                        if(houseFlow != null){
                            webOrderDTO.setType(workerTypeMapper.selectByPrimaryKey(houseFlow.getWorkerTypeId()).getName()+"抢单");
                        }
                    }
                    if(businessOrder.getType() == 2){//补货补人工
                        MendOrder mendOrder = mendOrderMapper.selectByPrimaryKey(businessOrder.getTaskId());
                        if(mendOrder != null){
                            if (mendOrder.getType() == 0){
                                webOrderDTO.setType(workerTypeMapper.selectByPrimaryKey(mendOrder.getWorkerTypeId()).getName()+"补材料");
                            }else {
                                webOrderDTO.setType(workerTypeMapper.selectByPrimaryKey(mendOrder.getWorkerTypeId()).getName()+"补人工");
                            }
                        }
                    }

                    if(businessOrder.getType() == 4) {//只付材料
                        HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(businessOrder.getTaskId());
                        if(houseFlow != null){
                            webOrderDTO.setType(workerTypeMapper.selectByPrimaryKey(houseFlow.getWorkerTypeId()).getName()+"付材料");
                        }
                    }
                    if(businessOrder.getType() == 5) {
                        webOrderDTO.setType("验房分销");
                    }
                }
                webOrderDTO.setCreateDate(businessOrder.getCreateDate());
                webOrderDTO.setModifyDate(businessOrder.getModifyDate());
                House house = iHouseMapper.selectByPrimaryKey(businessOrder.getHouseId());
                if (house != null) {
//                    return ServerResponse.createByErrorMessage("找不到房子信息 houseId:" + businessOrder.getHouseId());
                    webOrderDTO.setHouseName(house.getHouseName());
                    Member member = memberMapper.selectByPrimaryKey(house.getMemberId());
                    if (member != null) {
                        webOrderDTO.setMemberId(member.getId());
                        webOrderDTO.setMobile(member.getMobile());
                    }
                }
                webOrderDTO.setPayOrderNumber(businessOrder.getPayOrderNumber());
                webOrderDTO.setActualPayment(businessOrder.getPayPrice());
                ActivityRedPackRecord activityRedPackRecord = iActivityRedPackRecordMapper.getRedPackRecordsByBusinessOrderNumber(businessOrder.getNumber());
                if (activityRedPackRecord != null) {
                    ActivityRedPack activityRedPack = iActivityRedPackMapper.selectByPrimaryKey(activityRedPackRecord.getRedPackId());
                    webOrderDTO.setRedPackName(activityRedPack.getName());
                    webOrderDTO.setRedPackAmount(businessOrder.getDiscountsPrice());
                }
                webOrderDTOList.add(webOrderDTO);
            }

            PageInfo pageResult = new PageInfo(orderList);
            pageResult.setList(webOrderDTOList);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


}

