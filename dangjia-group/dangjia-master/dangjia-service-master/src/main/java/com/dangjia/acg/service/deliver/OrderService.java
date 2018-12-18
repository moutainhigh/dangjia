package com.dangjia.acg.service.deliver;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.mapper.deliver.IOrderSplitItemMapper;
import com.dangjia.acg.mapper.deliver.IOrderSplitMapper;
import com.dangjia.acg.mapper.house.IWarehouseDetailMapper;
import com.dangjia.acg.mapper.house.IWarehouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.modle.deliver.OrderSplit;
import com.dangjia.acg.modle.deliver.OrderSplitItem;
import com.dangjia.acg.modle.house.Warehouse;
import com.dangjia.acg.modle.house.WarehouseDetail;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.member.Member;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import java.util.List;

/**
 * 要货
 * Date: 2018/11/8 0008
 * Time: 11:48
 */
@Service
public class OrderService {
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IMemberMapper memberMapper;
    @Autowired
    private IOrderSplitMapper orderSplitMapper;
    @Autowired
    private IOrderSplitItemMapper orderSplitItemMapper;
    @Autowired
    private IWarehouseMapper warehouseMapper;
    @Autowired
    private IWarehouseDetailMapper warehouseDetailMapper;


    /**
     * 管家确认要货
     * 提交到后台材料员审核
     */
    public ServerResponse confirmOrderSplit(String houseId){
        try{
            Example example = new Example(OrderSplit.class);
            example.createCriteria().andEqualTo(OrderSplit.HOUSE_ID, houseId).andEqualTo(OrderSplit.APPLY_STATUS, 0);
            List<OrderSplit> orderSplitList = orderSplitMapper.selectByExample(example);
            if (orderSplitList.size() == 0){
                return ServerResponse.createByErrorMessage("大管家还没有生成要货单");
            }else if (orderSplitList.size() > 1){
                return ServerResponse.createByErrorMessage("生成多个未提交要货单,异常联系平台部");
            }else {
                OrderSplit orderSplit = orderSplitList.get(0);
                example = new Example(OrderSplitItem.class);
                example.createCriteria().andEqualTo(OrderSplitItem.ORDER_SPLIT_ID, orderSplit.getId());
                List<OrderSplitItem> orderSplitItemList = orderSplitItemMapper.selectByExample(example);
                for (OrderSplitItem orderSplitItem : orderSplitItemList){
                    Warehouse warehouse = warehouseMapper.selectByPrimaryKey(orderSplitItem.getWarehouseId());
                    warehouse.setAskCount(warehouse.getAskCount() + orderSplitItem.getNum());//更新仓库已要总数
                    warehouse.setAskTime(warehouse.getAskTime() + 1);//更新该货品被要次数
                    warehouseMapper.updateByPrimaryKeySelective(warehouse);
                }
                orderSplit.setApplyStatus(1);//提交到后台
                orderSplitMapper.updateByPrimaryKeySelective(orderSplit);

                //记录仓库流水
                WarehouseDetail warehouseDetail = new WarehouseDetail();
                warehouseDetail.setHouseId(houseId);
                warehouseDetail.setRelationId(orderSplit.getId());//要货单
                warehouseDetail.setRecordType(1);//要
                warehouseDetailMapper.insert(warehouseDetail);
                return ServerResponse.createBySuccessMessage("操作成功");
            }

        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("失败");
        }
    }


    /**
     * 返回已添加要货单明细
     */
    public ServerResponse getOrderItemList(String houseId){
        try{
            Example example = new Example(OrderSplit.class);
            example.createCriteria().andEqualTo(OrderSplit.HOUSE_ID, houseId).andEqualTo(OrderSplit.APPLY_STATUS, 0);
            List<OrderSplit> orderSplitList = orderSplitMapper.selectByExample(example);
            if (orderSplitList.size() == 0){
                return ServerResponse.createBySuccessMessage("没有生成中要货单");
            }else if (orderSplitList.size() > 1){
                return ServerResponse.createByErrorMessage("生成多个未提交要货单,异常联系平台部");
            }else {
                OrderSplit orderSplit = orderSplitList.get(0);
                example = new Example(OrderSplitItem.class);
                example.createCriteria().andEqualTo(OrderSplitItem.ORDER_SPLIT_ID, orderSplit.getId());
                List<OrderSplitItem> orderSplitItemList = orderSplitItemMapper.selectByExample(example);
                for (OrderSplitItem v : orderSplitItemList){
                    v.initPath(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class));
                }
                return ServerResponse.createBySuccess("查询成功", orderSplitItemList);
            }
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 提交到要货
     */
    public ServerResponse saveOrderSplit(String productArr, String houseId, String userToken){
        try{
            AccessToken accessToken=redisClient.getCache(userToken+ Constants.SESSIONUSERID,AccessToken.class);
            //管家信息
            Member supervisor = memberMapper.selectByPrimaryKey(accessToken.getMember().getId());

            Example example = new Example(OrderSplit.class);
            example.createCriteria().andEqualTo(OrderSplit.HOUSE_ID, houseId).andEqualTo(OrderSplit.APPLY_STATUS, 0);
            List<OrderSplit> orderSplitList = orderSplitMapper.selectByExample(example);
            OrderSplit orderSplit;
            if (orderSplitList.size() > 0){
                orderSplit = orderSplitList.get(0);
                /*删除之前子项*/
                example = new Example(OrderSplitItem.class);
                example.createCriteria().andEqualTo(OrderSplitItem.ORDER_SPLIT_ID, orderSplit.getId());
                orderSplitItemMapper.deleteByExample(example);
            }else {
                orderSplit = new OrderSplit();
                orderSplit.setNumber("dj" + 200000 + orderSplitMapper.selectCount(orderSplit));//要货单号
                orderSplit.setHouseId(houseId);
                orderSplit.setApplyStatus(0);//后台审核状态：0生成中, 1申请中, 2通过, 3不通过 后台(材料员)
                orderSplit.setSupervisorId(supervisor.getId());
                orderSplit.setSupervisorName(supervisor.getName());
                orderSplit.setSupervisorTel(supervisor.getMobile());
                orderSplitMapper.insert(orderSplit);
            }

            JSONArray arr = JSONArray.parseArray(productArr);
            for(int i=0; i<arr.size(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                Double num = Double.parseDouble(obj.getString("num"));
                String productId = obj.getString("productId");

                Warehouse warehouse = warehouseMapper.getByProductId(productId, houseId);//定位到仓库id
                OrderSplitItem orderSplitItem = new OrderSplitItem();
                orderSplitItem.setOrderSplitId(orderSplit.getId());
                orderSplitItem.setWarehouseId(warehouse.getId());//仓库子项id
                orderSplitItem.setProductId(warehouse.getProductId());
                orderSplitItem.setProductSn(warehouse.getProductSn());
                orderSplitItem.setProductName(warehouse.getProductName());
                orderSplitItem.setPrice(warehouse.getPrice());
                orderSplitItem.setCost(warehouse.getCost());
                orderSplitItem.setShopCount(warehouse.getShopCount());
                orderSplitItem.setNum(num);
                orderSplitItem.setUnitName(warehouse.getUnitName());
                orderSplitItem.setTotalPrice(warehouse.getPrice() * num);//单项总价
                orderSplitItem.setProductType(warehouse.getProductType());
                orderSplitItem.setCategoryId(warehouse.getCategoryId());
                orderSplitItem.setImage(warehouse.getImage());//货品图片
                orderSplitItem.setHouseId(houseId);
                orderSplitItemMapper.insert(orderSplitItem);
            }

            return ServerResponse.createBySuccessMessage("提交成功");
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("提交失败");
        }
    }

    /**
     * 模糊搜仓库
     */
    public ServerResponse warehouseList(Integer pageNum, Integer pageSize,String houseId,String categoryId, String name){
        try{
            if(StringUtil.isEmpty(houseId)){
                return ServerResponse.createByErrorMessage("houseId不能为空");
            }
            if(pageNum == null){
                pageNum = 1;
            }
            if(pageSize == null){
                pageSize = 5;
            }
            PageHelper.startPage(pageNum, pageSize);
            List<Warehouse> warehouseList = warehouseMapper.warehouseList(houseId,categoryId,name);
            PageInfo pageResult = new PageInfo(warehouseList);
            for (Warehouse v : warehouseList){
                v.initPath(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class));
            }
            pageResult.setList(warehouseList);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }



}
