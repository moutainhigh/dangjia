package com.dangjia.acg.service.deliver;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.data.ForMasterAPI;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.deliver.SplitDeliverDTO;
import com.dangjia.acg.dto.deliver.SplitDeliverItemDTO;
import com.dangjia.acg.mapper.deliver.IOrderSplitItemMapper;
import com.dangjia.acg.mapper.deliver.ISplitDeliverMapper;
import com.dangjia.acg.mapper.house.IWarehouseMapper;
import com.dangjia.acg.modle.deliver.OrderSplitItem;
import com.dangjia.acg.modle.deliver.SplitDeliver;
import com.dangjia.acg.modle.house.Warehouse;
import com.dangjia.acg.modle.sup.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
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
    private ForMasterAPI forMasterAPI;
    @Autowired
    private IWarehouseMapper warehouseMapper;


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
                /*统计收货数量*/
                Warehouse warehouse = warehouseMapper.getByProductId(orderSplitItem.getProductId(), splitDeliver.getHouseId());
                warehouse.setReceive(warehouse.getReceive() + receive);
                warehouseMapper.updateByPrimaryKeySelective(warehouse);
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
            splitDeliver.setModifyDate(new Date());//收货时间
            splitDeliverMapper.updateByPrimaryKeySelective(splitDeliver);
            orderSplitItemMapper.affirmSplitDeliver(splitDeliverId);
            /*统计收货数量*/
            Example example = new Example(OrderSplitItem.class);
            example.createCriteria().andEqualTo(OrderSplitItem.SPLIT_DELIVER_ID, splitDeliverId);
            List<OrderSplitItem> orderSplitItemList = orderSplitItemMapper.selectByExample(example);
            for (OrderSplitItem orderSplitItem : orderSplitItemList){
                Warehouse warehouse = warehouseMapper.getByProductId(orderSplitItem.getProductId(), splitDeliver.getHouseId());
                warehouse.setReceive(warehouse.getReceive() + orderSplitItem.getNum());
                warehouseMapper.updateByPrimaryKeySelective(warehouse);
            }
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
     * 发货单明细
     */
    public ServerResponse splitDeliverDetail(String splitDeliverId){
        try{
            SplitDeliver splitDeliver = splitDeliverMapper.selectByPrimaryKey(splitDeliverId);
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            SplitDeliverDTO splitDeliverDTO = new SplitDeliverDTO();
            splitDeliverDTO.setShipState(splitDeliver.getShipState());//发货状态
            splitDeliverDTO.setNumber(splitDeliver.getNumber());
            splitDeliverDTO.setCreateDate(splitDeliver.getCreateDate());
            splitDeliverDTO.setSendTime(splitDeliver.getSendTime());
            splitDeliverDTO.setSubmitTime(splitDeliver.getSubmitTime());
            splitDeliverDTO.setModifyDate(splitDeliver.getModifyDate());//收货时间
            splitDeliverDTO.setTotalAmount(splitDeliver.getTotalAmount());
            splitDeliverDTO.setSupState(splitDeliver.getSupState());//大管家收货状态

            if(!CommonUtil.isEmpty(splitDeliver.getImage())) {
                List<String> imageList = new ArrayList<>();
                String[] imageArr = splitDeliver.getImage().split(",");
                for (int i = 0; i < imageArr.length; i++) {
                    String image = address + imageArr[i];
                    imageList.add(image);
                }
                splitDeliverDTO.setImageList(imageList);
            }
            Example example = new Example(OrderSplitItem.class);
            example.createCriteria().andEqualTo(OrderSplitItem.SPLIT_DELIVER_ID, splitDeliver.getId());
            List<OrderSplitItem> orderSplitItemList = orderSplitItemMapper.selectByExample(example);
            List<SplitDeliverItemDTO> splitDeliverItemDTOList = new ArrayList<>();
            for (OrderSplitItem orderSplitItem : orderSplitItemList){
                SplitDeliverItemDTO splitDeliverItemDTO = new SplitDeliverItemDTO();
                splitDeliverItemDTO.setImage(address + orderSplitItem.getImage());
                splitDeliverItemDTO.setProductName(orderSplitItem.getProductName());
                splitDeliverItemDTO.setTotalPrice(orderSplitItem.getTotalPrice());
                splitDeliverItemDTO.setShopCount(orderSplitItem.getShopCount());
                splitDeliverItemDTO.setNum(orderSplitItem.getNum());
                splitDeliverItemDTO.setUnitName(orderSplitItem.getUnitName());
                splitDeliverItemDTO.setBrandSeriesName(forMasterAPI.brandSeriesName(orderSplitItem.getProductId()));
                splitDeliverItemDTO.setPrice(orderSplitItem.getPrice());
                splitDeliverItemDTO.setId(orderSplitItem.getId());
                splitDeliverItemDTO.setReceive(orderSplitItem.getReceive());//收货数量
                splitDeliverItemDTOList.add(splitDeliverItemDTO);
            }
            splitDeliverDTO.setSplitDeliverItemDTOList(splitDeliverItemDTOList);//明细

            return ServerResponse.createBySuccess("查询成功", splitDeliverDTO);
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 收货列表
     * shipState  0待发货,1已发待收货,2已收货,3取消  5所有
     */
    public ServerResponse splitDeliverList(String houseId, int shipState){
        try{
            Example example = new Example(SplitDeliver.class);
            if (shipState == 5){
                example.createCriteria().andEqualTo(SplitDeliver.ORDER_SPLIT_ID, houseId);//中台用
            }else {
                example.createCriteria().andEqualTo(SplitDeliver.HOUSE_ID, houseId).andEqualTo(SplitDeliver.SHIP_STATE,shipState);
            }
            List<SplitDeliver> splitDeliverList = splitDeliverMapper.selectByExample(example);
            List<SplitDeliverDTO> splitDeliverDTOList = new ArrayList<>();
            for (SplitDeliver splitDeliver : splitDeliverList){
                SplitDeliverDTO splitDeliverDTO = new SplitDeliverDTO();
                splitDeliverDTO.setSplitDeliverId(splitDeliver.getId());
                splitDeliverDTO.setCreateDate(splitDeliver.getCreateDate());
                splitDeliverDTO.setShipState(splitDeliver.getShipState());
                splitDeliverDTO.setNumber(splitDeliver.getNumber());
                splitDeliverDTO.setSendTime(splitDeliver.getSendTime());//发货时间
                splitDeliverDTO.setModifyDate(splitDeliver.getModifyDate());//收货时间
                Supplier supplier = forMasterAPI.getSupplier(splitDeliver.getSupplierId());
                splitDeliverDTO.setSupId(supplier.getId());//供应商id
                splitDeliverDTO.setSupMobile(supplier.getTelephone());
                splitDeliverDTO.setSupName(supplier.getName());
                splitDeliverDTO.setTotalAmount(splitDeliver.getTotalAmount());

                example = new Example(OrderSplitItem.class);
                example.createCriteria().andEqualTo(OrderSplitItem.SPLIT_DELIVER_ID, splitDeliver.getId());
                List<OrderSplitItem> orderSplitItemList = orderSplitItemMapper.selectByExample(example);

                splitDeliverDTO.setTol(orderSplitItemList.size());//几种
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
