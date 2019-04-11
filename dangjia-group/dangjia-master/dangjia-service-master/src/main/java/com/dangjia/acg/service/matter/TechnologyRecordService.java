package com.dangjia.acg.service.matter;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.api.actuary.BudgetWorkerAPI;
import com.dangjia.acg.api.data.ForMasterAPI;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.enums.EventStatus;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.dto.matter.TechnologyRecordDTO;
import com.dangjia.acg.dto.matter.WorkNodeDTO;
import com.dangjia.acg.mapper.core.IHouseFlowApplyMapper;
import com.dangjia.acg.mapper.core.IHouseFlowMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.house.IWarehouseMapper;
import com.dangjia.acg.mapper.matter.ITechnologyRecordMapper;
import com.dangjia.acg.modle.basics.Technology;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.core.HouseFlowApply;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.house.Warehouse;
import com.dangjia.acg.modle.matter.TechnologyRecord;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.member.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Date: 2018/11/20 0001
 * Time: 17:56
 */
@Service
public class TechnologyRecordService {
    @Autowired
    private ITechnologyRecordMapper technologyRecordMapper;
    @Autowired
    private IHouseFlowMapper houseFlowMapper;
    @Autowired
    private IHouseMapper houseMapper;
    @Autowired
    private BudgetWorkerAPI budgetWorkerAPI;
    @Autowired
    private IWarehouseMapper warehouseMapper;
    @Autowired
    private ForMasterAPI forMasterAPI;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private IHouseFlowApplyMapper houseFlowApplyMapper;


    /**
     * 1.31
     * 获取上传图片列表
     * applyType 0每日完工申请，1阶段完工申请，2整体完工申请,3停工申请，4：每日开工,5有效巡查,6无人巡查,7追加巡查
     */
    public ServerResponse nodeImageList(String nodeArr, Integer applyType, String houseFlowId) {
        try {
            if (StringUtil.isEmpty(houseFlowId))
                return ServerResponse.createByErrorMessage("houseFlowId不能为空");
            Map<String, Object> returnMap = new HashMap<>();
            if (applyType == 1 || applyType == 2 || applyType == 666) {
                returnMap.put("hint", "温馨提示:您在提交验收后被驳回次数<font color=red>超过2次后将产生罚款</font>,金额为100元/1次的费用.");
                List<HouseFlowApply> houseFlowApplyList = houseFlowApplyMapper.noPassList(houseFlowId);
                if (houseFlowApplyList.size() >= 2) {
                    returnMap.put("pop", "您的整改次数已经超过2次，从第3次起，每次整改将按照100元/次自动罚款，请知晓");
                } else {
                    returnMap.put("pop", "");
                }
            } else {
                returnMap.put("hint", "");
                returnMap.put("pop", "");
            }

            List<Map<String, Object>> listMap = new ArrayList<>();
            Map<String, Object> map;
            HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(houseFlowId);
            House house = houseMapper.selectByPrimaryKey(houseFlow.getHouseId());
            if (StringUtil.isNotEmpty(nodeArr)) {
                String[] idArr = nodeArr.split(",");
                for (String id : idArr) {
                    Technology technology = forMasterAPI.byTechnologyId(house.getCityId(), id);
                    map = new HashMap<>();
                    map.put("imageTypeId", id);
                    map.put("imageTypeName", technology.getName());
                    map.put("imageType", 3);  //节点照片
                    listMap.add(map);
                }
            } else {
                map = new HashMap<>();
                map.put("imageTypeId", "");
                map.put("imageTypeName", "现场照片");
                map.put("imageType", 2);
                listMap.add(0, map);
            }
            map = new HashMap<>();
            map.put("imageTypeId", "");
            map.put("imageTypeName", "材料照片");
            map.put("imageType", 0);
            listMap.add(listMap.size(), map);

            returnMap.put("listMap", listMap);
            return ServerResponse.createBySuccess("查询成功", returnMap);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 1.31
     * 管家工匠合并 节点列表
     * applyType 0每日完工申请，1阶段完工申请，2整体完工申请,3停工申请，4：每日开工,5有效巡查,6无人巡查,7追加巡查
     */
    public ServerResponse workNodeList(String userToken, String houseFlowId) {
        AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
        Member worker = accessToken.getMember();

        HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(houseFlowId);
        House house = houseMapper.selectByPrimaryKey(houseFlow.getHouseId());
        if (house.getPause() != null) {
            if (house.getPause() == 1) {
                return ServerResponse.createByErrorCodeMessage(EventStatus.ERROR.getCode(), "该房子已暂停施工,请勿提交申请！");
            }
        }

        List<WorkNodeDTO> workNodeDTOList = new ArrayList<>();
        if (worker.getWorkerType() == 3) { //管家查询管家应验收节点
            WorkNodeDTO workNodeDTOA = new WorkNodeDTO();
            //所有已进场未完工工序的节点
            List<TechnologyRecordDTO> trList = new ArrayList<>();
            JSONArray jsonArray = budgetWorkerAPI.getAllTechnologyByHouseId(house.getId());
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String technologyId = object.getString("technologyId");
                String technologyName = object.getString("technologyName");
                List<TechnologyRecord> technologyRecordList = technologyRecordMapper.checkByTechnologyId(house.getId(), technologyId, worker.getWorkerTypeId());
                TechnologyRecordDTO trd = new TechnologyRecordDTO();
                trd.setId(technologyId);
                trd.setName(technologyName);
                if (technologyRecordList.size() == 0) {
                    trd.setState(0);//未验收
                } else {
                    trd.setState(1);//已验收
                }
                trList.add(trd);
            }
            workNodeDTOA.setTecName("管家验收所有节点");
            workNodeDTOA.setTrList(trList);
            workNodeDTOList.add(workNodeDTOA);
        } else {//工匠提交的验收节点
            //含工艺人工商品
            JSONArray jsonArray = budgetWorkerAPI.getWorkerGoodsList(houseFlow.getHouseId(), houseFlowId);
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String workerGoodsId = object.getString("workerGoodsId");
                String workerGoodsName = object.getString("workerGoodsName");

                WorkNodeDTO workNodeDTOA = new WorkNodeDTO();
                workNodeDTOA.setTecName(workerGoodsName);//商品名
                JSONArray tecArray = budgetWorkerAPI.getTecList(workerGoodsId);
                List<TechnologyRecordDTO> trList = new ArrayList<>();
                for (int j = 0; j < tecArray.size(); j++) {
                    JSONObject tecObject = tecArray.getJSONObject(j);
                    String technologyId = tecObject.getString("technologyId");
                    String technologyName = tecObject.getString("technologyName");
                    TechnologyRecordDTO trd = new TechnologyRecordDTO();
                    trd.setId(technologyId);
                    trd.setName(technologyName);
                    List<TechnologyRecord> technologyRecordList = technologyRecordMapper.checkByTechnologyId(houseFlow.getHouseId(), technologyId, worker.getWorkerTypeId());
                    if (technologyRecordList.size() == 0) { //没有验收
                        trd.setState(0);
                    } else {
                        trd.setState(1);//已验收
                    }
                    trList.add(trd);
                }
                workNodeDTOA.setTrList(trList);
                workNodeDTOList.add(workNodeDTOA);
            }
        }
        return ServerResponse.createBySuccess("查询成功", workNodeDTOList);
    }

    /**
     * 获取上传图片列表
     * applyType 0每日完工申请，1阶段完工申请，2整体完工申请,3停工申请，4：每日开工,5有效巡查,6无人巡查,7追加巡查
     */
    @Deprecated
    public ServerResponse uploadingImageList(String nodeArr) {
        try {
            List<Map<String, Object>> listMap = new ArrayList<>();
            Map<String, Object> map;
            if (StringUtil.isNotEmpty(nodeArr)) {
                String[] idArr = nodeArr.split(",");
                for (String id : idArr) {
                    Technology technology = forMasterAPI.byTechnologyId("", id);
                    map = new HashMap<>();
                    map.put("imageTypeId", id);
                    map.put("imageTypeName", technology.getName());
                    map.put("imageType", 3);  //节点照片
                    listMap.add(map);
                }
            } else {
                map = new HashMap<>();
                map.put("imageTypeId", "");
                map.put("imageTypeName", "现场照片");
                map.put("imageType", 2);
                listMap.add(0, map);
            }
            map = new HashMap<>();
            map.put("imageTypeId", "");
            map.put("imageTypeName", "材料照片");
            map.put("imageType", 0);
            listMap.add(listMap.size(), map);
            return ServerResponse.createBySuccess("查询成功", listMap);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 查询验收节点
     * 供管家选择验收
     *
     * @see workNodeList(String userToken, String houseFlowId, Integer applyType)} constraint instead
     * @deprecated use the standard {@link com.dangjia.acg.service.matter.TechnologyRecordService
     */
    @Deprecated
    public ServerResponse technologyRecordList(String houseFlowId) {
        try {
            HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(houseFlowId);
            House house = houseMapper.selectByPrimaryKey(houseFlow.getHouseId());
            if (house.getPause() != null) {
                if (house.getPause() == 1) {
                    return ServerResponse.createByErrorCodeMessage(EventStatus.ERROR.getCode(), "该房子已暂停施工,请勿提交申请！");
                }
            }

            //所有已进场未完工工序的节点
            List<TechnologyRecordDTO> technologyRecordDTOS = new ArrayList<>();
            JSONArray jsonArray = budgetWorkerAPI.getAllTechnologyByHouseId(house.getId());
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String technologyId = object.getString("technologyId");
                String technologyName = object.getString("technologyName");
                List<TechnologyRecord> technologyRecordList = technologyRecordMapper.checkByTechnologyId(house.getId(), technologyId, "3");
                if (technologyRecordList.size() == 0) {
                    TechnologyRecordDTO dto = new TechnologyRecordDTO();
                    dto.setId(technologyId);
                    dto.setName(technologyName);
                    dto.setState(0);//未验收
                    technologyRecordDTOS.add(dto);
                }
            }

            return ServerResponse.createBySuccess("查询成功", technologyRecordDTOS);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 已进场未完工
     */
    public List<HouseFlow> unfinishedFlow(String houseId) {
        return houseFlowMapper.unfinishedFlow(houseId);
    }

    /**
     * 所有购买材料
     */
    public List<Warehouse> warehouseList(String houseId) {
        return warehouseMapper.warehouseList(houseId, null, null);
    }

    public ServerResponse getByProductId(String productId, String houseId) {
        Warehouse warehouse = warehouseMapper.getByProductId(productId, houseId);
        return ServerResponse.createBySuccess("查询成功", warehouse);
    }

}
