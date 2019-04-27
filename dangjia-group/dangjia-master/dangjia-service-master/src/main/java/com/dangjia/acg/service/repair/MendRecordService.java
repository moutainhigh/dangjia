package com.dangjia.acg.service.repair;

import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.repair.MendOrderDetail;
import com.dangjia.acg.mapper.deliver.IOrderSplitItemMapper;
import com.dangjia.acg.mapper.deliver.IOrderSplitMapper;
import com.dangjia.acg.mapper.repair.IChangeOrderMapper;
import com.dangjia.acg.mapper.repair.IMendMaterialMapper;
import com.dangjia.acg.mapper.repair.IMendOrderMapper;
import com.dangjia.acg.mapper.repair.IMendWorkerMapper;
import com.dangjia.acg.modle.deliver.OrderSplit;
import com.dangjia.acg.modle.deliver.OrderSplitItem;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.repair.ChangeOrder;
import com.dangjia.acg.modle.repair.MendMateriel;
import com.dangjia.acg.modle.repair.MendOrder;
import com.dangjia.acg.modle.repair.MendWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author: Ronalcheng
 * Date: 2018/12/24 0024
 * Time: 14:02
 * 补退记录
 */
@Service
public class MendRecordService {
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IMendOrderMapper mendOrderMapper;
    @Autowired
    private IMendMaterialMapper mendMaterialMapper;
    @Autowired
    private IMendWorkerMapper mendWorkerMapper;
    @Autowired
    private IOrderSplitMapper orderSplitMapper;
    @Autowired
    private IOrderSplitItemMapper orderSplitItemMapper;
    @Autowired
    private IChangeOrderMapper changeOrderMapper;
    @Autowired
    private RedisClient redisClient;

    /**
     * 要补退明细
     * 0:补材料;1:补人工;2:退材料(剩余材料登记);3:退人工,4:业主退材料, 5 要货
     */
    public ServerResponse mendOrderDetail(String mendOrderId,Integer type){
        try {
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            MendOrderDetail mendOrderDetail = new MendOrderDetail();

            if(type == 5){
                OrderSplit orderSplit = orderSplitMapper.selectByPrimaryKey(mendOrderId);
                mendOrderDetail.setNumber(orderSplit.getNumber());
                mendOrderDetail.setMendOrderId(orderSplit.getMendNumber());
                mendOrderDetail.setType(5);
                mendOrderDetail.setState(orderSplit.getApplyStatus());
                mendOrderDetail.setCreateDate(orderSplit.getCreateDate());
                /*
                计算要货单钱
                 */
                mendOrderDetail.setTotalAmount(orderSplitItemMapper.getOrderSplitPrice(mendOrderId));

                List<Map<String,Object>> mapList = new ArrayList<>();
                Example example = new Example(OrderSplitItem.class);
                example.createCriteria().andEqualTo(OrderSplitItem.ORDER_SPLIT_ID, orderSplit.getId());
                List<OrderSplitItem> orderSplitItemList = orderSplitItemMapper.selectByExample(example);

                for (OrderSplitItem orderSplitItem : orderSplitItemList){
                    Map<String,Object> map = new HashMap<>();
                    map.put("image", address + orderSplitItem.getImage());
                    if (orderSplitItem.getProductType() == 0){
                        map.put("goodsType", "材料");
                    }else {
                        map.put("goodsType", "服务");
                    }
                    map.put("name", orderSplitItem.getProductName());
                    map.put("price", "¥" + String.format("%.2f",orderSplitItem.getPrice())+"/"+orderSplitItem.getUnitName());
                    map.put("shopCount", orderSplitItem.getNum());//本次数量
                    map.put("repairCount","0");
                    map.put("totalPrice", orderSplitItem.getTotalPrice());
                    if(!CommonUtil.isEmpty(orderSplit.getMendNumber())) {
                        MendMateriel mendMateriel = mendMaterialMapper.getMendOrderGoods(orderSplit.getMendNumber(),orderSplitItem.getProductId());
                        if(mendMateriel!=null){
                            map.put("repairCount",mendMateriel.getShopCount());
                        }

                    }
                    mapList.add(map);
                }
                mendOrderDetail.setMapList(mapList);

            }else {
                MendOrder mendOrder = mendOrderMapper.selectByPrimaryKey(mendOrderId);
                mendOrderDetail.setMendOrderId(mendOrderId);
                mendOrderDetail.setNumber(mendOrder.getNumber());
                mendOrderDetail.setType(mendOrder.getType());
                mendOrderDetail.setState(mendOrder.getState());//0生成中,1处理中,2不通过取消,3已通过,4已结算
                mendOrderDetail.setTotalAmount(mendOrder.getTotalAmount());
                mendOrderDetail.setCreateDate(mendOrder.getCreateDate());
                mendOrderDetail.setModifyDate(mendOrder.getModifyDate());

                List<Map<String,Object>> mapList = new ArrayList<>();
                if (mendOrder.getType() == 0 || mendOrder.getType() == 2 || mendOrder.getType() == 4){
                    List<MendMateriel> mendMaterielList = mendMaterialMapper.byMendOrderId(mendOrderId);
                    for (MendMateriel mendMateriel : mendMaterielList){
                        Map<String,Object> map = new HashMap<>();
                        map.put("image", address + mendMateriel.getImage());
                        if (mendMateriel.getProductType() == 0){
                            map.put("goodsType", "材料");
                        }else {
                            map.put("goodsType", "服务");
                        }
                        map.put("name", mendMateriel.getProductName());
                        map.put("price", "¥" + String.format("%.2f",mendMateriel.getPrice())+"/"+mendMateriel.getUnitName());
                        map.put("shopCount", mendMateriel.getShopCount());
                        map.put("totalPrice", mendMateriel.getTotalPrice());
                        mapList.add(map);
                    }
                }else if (mendOrder.getType() == 1 || mendOrder.getType() == 3){
                    List<MendWorker> mendWorkerList = mendWorkerMapper.byMendOrderId(mendOrderId);
                    for (MendWorker mendWorker : mendWorkerList){
                        Map<String,Object> map = new HashMap<>();
                        map.put("image", address + mendWorker.getImage());
                        map.put("goodsType", "人工");
                        map.put("name", mendWorker.getWorkerGoodsName());
                        map.put("price", "¥" + String.format("%.2f",mendWorker.getPrice())+"/"+mendWorker.getUnitName());
                        map.put("shopCount", mendWorker.getShopCount());
                        map.put("totalPrice", String.format("%.2f",mendWorker.getTotalPrice()));
                        mapList.add(map);
                    }
                }
                mendOrderDetail.setMapList(mapList);

                if (mendOrder.getType() == 2 && StringUtil.isNotEmpty(mendOrder.getImageArr())){
                    String[] imageArr = mendOrder.getImageArr().split(",");
                    if (imageArr.length > 0){
                        List<String> imageList = new ArrayList<>();
                        for (int i=0; i<imageArr.length; i++){
                            imageList.add(address + imageArr[i]);
                        }
                        mendOrderDetail.setImageList(imageList);
                    }
                }

                if(mendOrder.getType() == 1 || mendOrder.getType() == 3){//补退人工
                    ChangeOrder changeOrder = changeOrderMapper.selectByPrimaryKey(mendOrder.getChangeOrderId());
                    mendOrderDetail.setChangeOrder(changeOrder);
                }
            }

            return ServerResponse.createBySuccess("查询成功",mendOrderDetail);
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     *  记录列表
     *  0:补材料;1:补人工;2:退材料(剩余材料登记);3:退人工,4:业主退材料, 5 要货
     */
    public ServerResponse recordList(String houseId,Integer type){
        try{
            List<Map<String,Object>> returnMap = new ArrayList<>();

            if(type == 5){
                Example example = new Example(OrderSplit.class);
                example.createCriteria().andEqualTo(OrderSplit.HOUSE_ID, houseId).andNotEqualTo(OrderSplit.APPLY_STATUS,0);
                example.orderBy(OrderSplit.CREATE_DATE).desc();
                List<OrderSplit> orderSplitList = orderSplitMapper.selectByExample(example);
                for (OrderSplit orderSplit : orderSplitList){
                    Map<String,Object> map = new HashMap<>();
                    map.put("mendOrderId", orderSplit.getId());
                    map.put("number", orderSplit.getNumber());
                    map.put("state", orderSplit.getApplyStatus());
                    map.put("createDate", orderSplit.getCreateDate());
                    map.put("type", type);
                    returnMap.add(map);
                }
            }else {
                Example example = new Example(MendOrder.class);
                example.createCriteria().andEqualTo(MendOrder.HOUSE_ID, houseId).andEqualTo(MendOrder.TYPE,type)
                        .andNotEqualTo(MendOrder.STATE,0);
                example.orderBy(MendOrder.CREATE_DATE).desc();
                List<MendOrder> mendOrderList = mendOrderMapper.selectByExample(example);
                for (MendOrder mendOrder : mendOrderList){
                    Map<String,Object> map = new HashMap<>();
                    map.put("mendOrderId", mendOrder.getId());
                    map.put("number", mendOrder.getNumber());
                    map.put("state", mendOrder.getState());
                    map.put("totalAmount", "¥" + String.format("%.2f",+ mendOrder.getTotalAmount()));
                    map.put("createDate", mendOrder.getCreateDate());
                    map.put("type", type);
                    returnMap.add(map);
                }
            }


            return ServerResponse.createBySuccess("查询成功",returnMap);
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 要补退记录
     *  0:补材料;1:补人工;2:退材料(剩余材料登记);3:退人工,4:业主退材料
     *  roleType 1业主 2管家 3工匠
     */
    public ServerResponse mendList(String userToken,String houseId, int roleType){
        try{
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            List<Map<String,Object>> returnMap = new ArrayList<>();
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            Member worker = accessToken.getMember();

            Example example = new Example(MendOrder.class);
            example.createCriteria().andEqualTo(MendOrder.HOUSE_ID, houseId).andEqualTo(MendOrder.TYPE,0)
            .andNotEqualTo(MendOrder.STATE,0);
            List<MendOrder> mendOrderList = mendOrderMapper.selectByExample(example);
            if(mendOrderList.size() > 0){
                Map<String,Object> map = new HashMap<>();
                map.put("houseId", houseId);
                map.put("type", 0);
                map.put("image", address + "iconWork/zero.png");
                map.put("name", "补材料/服务记录");
                map.put("size", "共"+mendOrderList.size()+"条");
                returnMap.add(map);
            }

//            List<MendOrder> mendOrderList;
            if(roleType == 3){//工匠
                mendOrderList = mendOrderMapper.workerMendOrder(houseId,1,worker.getWorkerTypeId());
            }else {
                mendOrderList = mendOrderMapper.workerMendOrder(houseId,1,"");
            }
            if(mendOrderList.size() > 0){
                Map<String,Object> map = new HashMap<>();
                map.put("houseId", houseId);
                map.put("type", 1);
                map.put("image", address + "iconWork/one.png");
                map.put("name", "补人工记录");
                map.put("size", "共"+mendOrderList.size()+"条");
                returnMap.add(map);
            }
            example = new Example(MendOrder.class);
            example.createCriteria().andEqualTo(MendOrder.HOUSE_ID, houseId).andEqualTo(MendOrder.TYPE,2)
                    .andNotEqualTo(MendOrder.STATE,0);
            mendOrderList = mendOrderMapper.selectByExample(example);
            if(mendOrderList.size() > 0){
                Map<String,Object> map = new HashMap<>();
                map.put("houseId", houseId);
                map.put("type", 2);
                map.put("image", address + "iconWork/two.png");
                map.put("name", "退材料/服务记录");
                map.put("size", "共"+mendOrderList.size()+"条");
                returnMap.add(map);
            }

            if(roleType == 3){//工匠
                mendOrderList = mendOrderMapper.workerMendOrder(houseId,3,worker.getWorkerTypeId());
            }else {
                mendOrderList = mendOrderMapper.workerMendOrder(houseId,3,"");
            }
            if(mendOrderList.size() > 0){
                Map<String,Object> map = new HashMap<>();
                map.put("houseId", houseId);
                map.put("type", 3);
                map.put("image", address + "iconWork/three.png");
                map.put("name", "退人工记录");
                map.put("size", "共"+mendOrderList.size()+"条");
                returnMap.add(map);
            }

            example = new Example(MendOrder.class);
            example.createCriteria().andEqualTo(MendOrder.HOUSE_ID, houseId).andEqualTo(MendOrder.TYPE,4)
                    .andNotEqualTo(MendOrder.STATE,0);
            mendOrderList = mendOrderMapper.selectByExample(example);
            if(mendOrderList.size() > 0){
                Map<String,Object> map = new HashMap<>();
                map.put("houseId", houseId);
                map.put("type", 4);
                map.put("image", address + "iconWork/four.png");
                map.put("name", "业主退材料/服务记录");
                map.put("size", "共"+mendOrderList.size()+"条");
                returnMap.add(map);
            }

            /*要货单记录*/
            example = new Example(OrderSplit.class);
            example.createCriteria().andEqualTo(OrderSplit.HOUSE_ID, houseId).andGreaterThan(OrderSplit.APPLY_STATUS,0);
            List<OrderSplit> orderSplitList = orderSplitMapper.selectByExample(example);
            if(orderSplitList.size() > 0){
                Map<String,Object> map = new HashMap<>();
                map.put("houseId", houseId);
                map.put("type", 5);
                map.put("image", address + "iconWork/five.png");
                map.put("name", "要货记录");
                map.put("size", "共"+orderSplitList.size()+"条");
                returnMap.add(map);
            }

            return ServerResponse.createBySuccess("查询成功",returnMap);
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }
}
