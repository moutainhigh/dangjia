package com.dangjia.acg.service.matter;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.actuary.BudgetWorkerAPI;
import com.dangjia.acg.api.data.ForMasterAPI;
import com.dangjia.acg.common.enums.EventStatus;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.matter.TechnologyRecordDTO;
import com.dangjia.acg.mapper.core.IHouseFlowMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.house.IWarehouseMapper;
import com.dangjia.acg.mapper.matter.ITechnologyRecordMapper;
import com.dangjia.acg.modle.basics.Technology;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.house.Warehouse;
import com.dangjia.acg.modle.matter.TechnologyRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author: zmj
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

    /**
     * 获取上传图片列表
     * applyType 0每日完工申请，1阶段完工申请，2整体完工申请,3停工申请，4：每日开工,5有效巡查,6无人巡查,7追加巡查
     */
    public ServerResponse uploadingImageList(String nodeArr){
        try{
            List<Map<String,Object>> listMap = new ArrayList<>();
            Map<String,Object> map;

            if (StringUtil.isNotEmpty(nodeArr)){
                String[] idArr = nodeArr.split(",");
                for(int i=0; i<idArr.length; i++){
                    String id = idArr[i];
                    Technology technology = forMasterAPI.byTechnologyId(id);
                    map = new HashMap<>();
                    map.put("imageTypeId", id);
                    map.put("imageTypeName", technology.getName());
                    map.put("imageType", 3);  //节点照片
                    listMap.add(map);
                }
            }else {
                map = new HashMap<>();
                map.put("imageTypeId","");
                map.put("imageTypeName","现场照片");
                map.put("imageType",2);
                listMap.add(0,map);
            }

            map = new HashMap<>();
            map.put("imageTypeId","");
            map.put("imageTypeName","材料照片");
            map.put("imageType",0);
            listMap.add(listMap.size(), map);

         /*   map = new HashMap<>();
            map.put("imageTypeId","");
            map.put("imageTypeName","进度照片");
            map.put("imageType",1);
            listMap.add(listMap.size(), map);*/

            return ServerResponse.createBySuccess("查询成功",listMap);
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 查询验收节点
     * 供管家选择验收
     */
    public ServerResponse technologyRecordList(String houseFlowId){
        try{
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
            for(int i=0;i<jsonArray.size();i++){
                JSONObject object = jsonArray.getJSONObject(i);
                String technologyId = object.getString("technologyId");
                String technologyName = object.getString("technologyName");
                List<TechnologyRecord> technologyRecordList = technologyRecordMapper.checkByTechnologyId(house.getId(),technologyId);
                if (technologyRecordList.size() == 0){
                    TechnologyRecordDTO dto = new TechnologyRecordDTO();
                    dto.setId(technologyId);
                    dto.setName(technologyName);
                    dto.setState(0);//未验收
                    technologyRecordDTOS.add(dto);
                }
            }

            return ServerResponse.createBySuccess("查询成功",technologyRecordDTOS);
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 已进场未完工
     */
    public List<HouseFlow> unfinishedFlow(String houseId){
        return houseFlowMapper.unfinishedFlow(houseId);
    }

    /**
     * 所有购买材料
     */
    public List<Warehouse> warehouseList(String houseId){
        return warehouseMapper.warehouseList(houseId,null,null);
    }

}
