package com.dangjia.acg.service.finance;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.deliver.*;
import com.dangjia.acg.mapper.activity.IActivityRedPackMapper;
import com.dangjia.acg.mapper.activity.IActivityRedPackRecordMapper;
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
import com.dangjia.acg.modle.activity.ActivityRedPack;
import com.dangjia.acg.modle.activity.ActivityRedPackRecord;
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
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

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
    private ConfigUtil configUtil;
    @Autowired
    private IMemberMapper memberMapper;
    @Autowired
    private IOrderSplitMapper iOrderSplitMapper;
    @Autowired
    private IOrderSplitItemMapper iOrderSplitItemMapper;
    @Autowired
    private IOrderMapper iOrderMapper;
    @Autowired
    private IOrderItemMapper iOrderItemMapper;
    @Autowired
    private IHouseMapper iHouseMapper;
    @Autowired
    private IBusinessOrderMapper iBusinessOrderMapper;
    @Autowired
    private IActivityRedPackRecordMapper iActivityRedPackRecordMapper;
    @Autowired
    private IActivityRedPackMapper iActivityRedPackMapper;

    /*所有订单流水*/
    public ServerResponse getAllOrders(PageDTO pageDTO) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());

            List<WebOrderDTO> webOrderDTOList = new ArrayList<>();
            List<BusinessOrder> orderList = iBusinessOrderMapper.getAllBusinessOrder();

            for (BusinessOrder businessOrder : orderList) {
                WebOrderDTO webOrderDTO = new WebOrderDTO();
                webOrderDTO.setOrderId(businessOrder.getNumber());
                webOrderDTO.setTotalAmount(businessOrder.getTotalPrice());
                webOrderDTO.setState("待支付");
                if (businessOrder.getState() == 3)
                    webOrderDTO.setState("已支付");
                webOrderDTO.setCreateDate(businessOrder.getCreateDate());
                webOrderDTO.setModifyDate(businessOrder.getModifyDate());
                House house = iHouseMapper.selectByPrimaryKey(businessOrder.getHouseId());
                webOrderDTO.setHouseName(house.getHouseName());
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

