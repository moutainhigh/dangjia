package com.dangjia.acg.service.repair;

import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.repair.MendOrderDetail;
import com.dangjia.acg.mapper.repair.IMendMaterialMapper;
import com.dangjia.acg.mapper.repair.IMendOrderMapper;
import com.dangjia.acg.mapper.repair.IMendWorkerMapper;
import com.dangjia.acg.modle.repair.MendMateriel;
import com.dangjia.acg.modle.repair.MendOrder;
import com.dangjia.acg.modle.repair.MendWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

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

    /**
     * 补退明细
     */
    public ServerResponse mendOrderDetail(String mendOrderId){
        try {
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            MendOrderDetail mendOrderDetail = new MendOrderDetail();
            MendOrder mendOrder = mendOrderMapper.selectByPrimaryKey(mendOrderId);
            mendOrderDetail.setNumber(mendOrder.getNumber());
            mendOrderDetail.setType(mendOrder.getType());
            mendOrderDetail.setLandlordState(mendOrder.getLandlordState());//业主退材料状态
            mendOrderDetail.setMaterialOrderState(mendOrder.getMaterialOrderState());
            mendOrderDetail.setWorkerOrderState(mendOrder.getWorkerOrderState());
            mendOrderDetail.setMaterialBackState(mendOrder.getMaterialBackState());
            mendOrderDetail.setWorkerBackState(mendOrder.getWorkerBackState());
            mendOrderDetail.setTotalAmount(mendOrder.getTotalAmount());
            mendOrderDetail.setCreateDate(mendOrder.getCreateDate());
            mendOrderDetail.setModifyDate(mendOrder.getModifyDate());

            List<Map<String,Object>> mapList = new ArrayList<>();
            if (mendOrder.getType() == 0 || mendOrder.getType() == 2){
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
                    map.put("price", "￥"+mendMateriel.getPrice()+"/"+mendMateriel.getUnitName());
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
                    map.put("price", "￥"+mendWorker.getPrice()+"/"+mendWorker.getUnitName());
                    map.put("shopCount", mendWorker.getShopCount());
                    map.put("totalPrice", mendWorker.getTotalPrice());
                    mapList.add(map);
                }
            }
            mendOrderDetail.setMapList(mapList);

            return ServerResponse.createBySuccess("查询成功",mendOrderDetail);
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     *  记录列表
     */
    public ServerResponse recordList(String houseId,Integer type){
        try{
            List<Map<String,Object>> returnMap = new ArrayList<>();

            Example example = new Example(MendOrder.class);
            example.createCriteria().andEqualTo(MendOrder.HOUSE_ID, houseId).andEqualTo(MendOrder.TYPE,type);
            List<MendOrder> mendOrderList = mendOrderMapper.selectByExample(example);
            for (MendOrder mendOrder : mendOrderList){
                Map<String,Object> map = new HashMap<>();
                map.put("mendOrderId", mendOrder.getId());
                map.put("totalAmount", "￥" + mendOrder.getTotalAmount());
                map.put("createDate", mendOrder.getCreateDate());
                map.put("type", type);
                map.put("materialOrderState", mendOrder.getMaterialOrderState());
                map.put("workerOrderState", mendOrder.getWorkerOrderState());
                map.put("materialBackState", mendOrder.getMaterialBackState());
                map.put("workerBackState", mendOrder.getWorkerBackState());
                map.put("landlordState", mendOrder.getLandlordState());
                returnMap.add(map);
            }

            return ServerResponse.createBySuccess("查询成功",returnMap);
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 补退记录
     *     //0:补材料;1:补人工;2:退材料;3:退人工
     */
    public ServerResponse mendList(String userToken, String houseId){
        try{
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            List<Map<String,Object>> returnMap = new ArrayList<>();

            Example example = new Example(MendOrder.class);
            example.createCriteria().andEqualTo(MendOrder.HOUSE_ID, houseId).andEqualTo(MendOrder.TYPE,0)
            .andNotEqualTo(MendOrder.MATERIAL_ORDER_STATE,0);
            List<MendOrder> mendOrderList = mendOrderMapper.selectByExample(example);
            if(mendOrderList.size() > 0){
                Map<String,Object> map = new HashMap<>();
                map.put("houseId", houseId);
                map.put("type", 0);
                map.put("image", address + "iconWork/zero.png");
                map.put("name", "补材料记录");
                map.put("size", "共"+mendOrderList.size()+"条");
                returnMap.add(map);
            }
            example = new Example(MendOrder.class);
            example.createCriteria().andEqualTo(MendOrder.HOUSE_ID, houseId).andEqualTo(MendOrder.TYPE,1)
                    .andNotEqualTo(MendOrder.WORKER_ORDER_STATE,0);
            mendOrderList = mendOrderMapper.selectByExample(example);
            if(mendOrderList.size() > 0){
                Map<String,Object> map = new HashMap<>();
                map.put("houseId", houseId);
                map.put("type", 1);
                map.put("image", address + "iconWork/one.png");
                map.put("name", "退材料记录");
                map.put("size", "共"+mendOrderList.size()+"条");
                returnMap.add(map);
            }
            example = new Example(MendOrder.class);
            example.createCriteria().andEqualTo(MendOrder.HOUSE_ID, houseId).andEqualTo(MendOrder.TYPE,2)
                    .andNotEqualTo(MendOrder.MATERIAL_BACK_STATE,0);
            mendOrderList = mendOrderMapper.selectByExample(example);
            if(mendOrderList.size() > 0){
                Map<String,Object> map = new HashMap<>();
                map.put("houseId", houseId);
                map.put("type", 2);
                map.put("image", address + "iconWork/two.png");
                map.put("name", "补人工记录");
                map.put("size", "共"+mendOrderList.size()+"条");
                returnMap.add(map);
            }
            example = new Example(MendOrder.class);
            example.createCriteria().andEqualTo(MendOrder.HOUSE_ID, houseId).andEqualTo(MendOrder.TYPE,3)
                    .andNotEqualTo(MendOrder.WORKER_BACK_STATE,0);
            mendOrderList = mendOrderMapper.selectByExample(example);
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
                    .andNotEqualTo(MendOrder.LANDLORD_STATE,0);
            mendOrderList = mendOrderMapper.selectByExample(example);
            if(mendOrderList.size() > 0){
                Map<String,Object> map = new HashMap<>();
                map.put("houseId", houseId);
                map.put("type", 4);
                map.put("image", address + "iconWork/four.png");
                map.put("name", "业主退货");
                map.put("size", "共"+mendOrderList.size()+"条");
                returnMap.add(map);
            }

            return ServerResponse.createBySuccess("查询成功",returnMap);
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }
}
