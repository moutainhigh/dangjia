package com.dangjia.acg.service.deliver;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.deliver.SplitDeliverDTO;
import com.dangjia.acg.mapper.deliver.IOrderItemMapper;
import com.dangjia.acg.mapper.deliver.IOrderSplitItemMapper;
import com.dangjia.acg.mapper.deliver.ISplitDeliverMapper;
import com.dangjia.acg.modle.deliver.OrderSplitItem;
import com.dangjia.acg.modle.deliver.SplitDeliver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2018/12/5 0005
 * Time: 20:31
 */
@Service
public class SplitDeliverService {

    @Autowired
    private ISplitDeliverMapper splitDeliverMapper;
    @Autowired
    private IOrderSplitItemMapper orderSplitItemMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IOrderItemMapper orderItemMapper;


    /**
     * 部分收货
     */
    public ServerResponse partSplitDeliver(String splitDeliverId, String image ,String splitItemList){
        try{
            SplitDeliver splitDeliver = splitDeliverMapper.selectByPrimaryKey(splitDeliverId);
            splitDeliver.setShipState(4);//部分收货
            splitDeliver.setImage(image);//收货图片
            splitDeliverMapper.updateByPrimaryKeySelective(splitDeliver);
            JSONArray arr = JSONArray.parseArray(splitItemList);
            for(int i=0; i<arr.size(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                String id = obj.getString("id");
                Double receive = Double.parseDouble(obj.getString("receive"));//本次收货数量
                OrderSplitItem orderSplitItem = orderSplitItemMapper.selectByPrimaryKey(id);
                orderSplitItem.setReceive(receive);//本次收货数量
                orderSplitItemMapper.updateByPrimaryKeySelective(orderSplitItem);
            }

            return ServerResponse.createBySuccessMessage("操作成功");
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }

    /**
     * 确认收货
     */
    public ServerResponse affirmSplitDeliver(String splitDeliverId, String image){
        try{
            SplitDeliver splitDeliver = splitDeliverMapper.selectByPrimaryKey(splitDeliverId);
            splitDeliver.setShipState(2);//收货
            splitDeliver.setImage(image);//收货图片
            splitDeliverMapper.updateByPrimaryKeySelective(splitDeliver);

            orderSplitItemMapper.affirmSplitDeliver(splitDeliverId);
            return ServerResponse.createBySuccessMessage("操作成功");
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }


    /**
     * 委托大管家收货
     */
    public ServerResponse supState(String splitDeliverId){
        try{
            splitDeliverMapper.supState(splitDeliverId);
            return ServerResponse.createBySuccessMessage("操作成功");
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }

    /**
     * 收货列表
     * shipState  0待发货,1已发待收货,2已收货,3取消,4部分收
     */
    public ServerResponse splitDeliverList(String houseId, int shipState){
        try{
            Example example = new Example(SplitDeliver.class);
            example.createCriteria().andEqualTo(SplitDeliver.HOUSE_ID, houseId).andEqualTo(SplitDeliver.SHIP_STATE,shipState);
            List<SplitDeliver> splitDeliverList = splitDeliverMapper.selectByExample(example);
            List<SplitDeliverDTO> splitDeliverDTOList = new ArrayList<SplitDeliverDTO>();
            for (SplitDeliver splitDeliver : splitDeliverList){
                SplitDeliverDTO splitDeliverDTO = new SplitDeliverDTO();
                splitDeliverDTO.setSplitDeliverId(splitDeliver.getId());
                splitDeliverDTO.setShipState(splitDeliver.getShipState());
                splitDeliverDTO.setNumber(splitDeliver.getNumber());
                splitDeliverDTO.setSendTime(splitDeliver.getSendTime());
                splitDeliverDTO.setSubmitTime(splitDeliver.getSubmitTime());
                splitDeliverDTO.setTotalAmount(splitDeliver.getTotalAmount());

                example = new Example(OrderSplitItem.class);
                example.createCriteria().andEqualTo(OrderSplitItem.SPLIT_DELIVER_ID, splitDeliver.getId());
                List<OrderSplitItem> orderSplitItemList = orderSplitItemMapper.selectByExample(example);
                splitDeliverDTO.setOrderSplitItemList(orderSplitItemList);//所有子项明细
                splitDeliverDTO.setTol(orderSplitItemList.size());
                splitDeliverDTO.setName(orderSplitItemList.get(0).getProductName());
                splitDeliverDTO.setImage(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class) + orderSplitItemList.get(0).getImage());
                splitDeliverDTOList.add(splitDeliverDTO);
            }

            return ServerResponse.createBySuccess("查询成功", splitDeliverDTOList);
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

}
