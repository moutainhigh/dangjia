package com.dangjia.acg.service.matter;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.mapper.core.IHouseFlowMapper;
import com.dangjia.acg.mapper.matter.ITechnologyRecordMapper;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.matter.TechnologyRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

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


    /**
     * 添加工艺验收节点
     */
    public void addTechnologyRecord(TechnologyRecord technologyRecord){
        technologyRecordMapper.insert(technologyRecord);
    }

    /**
     * 根据houseFlowId查询验收节点
     * @param houseFlowId
     * @return
     */
    public ServerResponse getCheckTechnologyList(String houseFlowId,Integer applyType){
        try{
            List<Map<String,Object>> listMap=new ArrayList<>();
            HouseFlow houseFlow=houseFlowMapper.selectByPrimaryKey(houseFlowId);
            if(applyType==5||applyType==6||applyType==7){//0每日完工申请，1阶段完工申请，2整体完工申请,3停工申请，4：每日开工,5有效巡查,6无人巡查,7追加巡查
                Example example=new Example(TechnologyRecord.class);
                example.createCriteria().andEqualTo("houseFlowId",houseFlowId)
                        .andEqualTo("type",1).andEqualTo("state",0);
                List<TechnologyRecord> technologyRecordList=technologyRecordMapper.selectByExample(example);
                for(TechnologyRecord technologyRecord:technologyRecordList){
                    Map<String,Object> map = new HashMap<>();
                    map.put("imageTypeId",technologyRecord.getTechnologyId());
                    map.put("imageTypeName",technologyRecord.getName());
                    map.put("imageType",3);
                    listMap.add(map);
                }
            }else{
                Map<String,Object> map1=new HashMap<>();
                map1.put("imageTypeId","");
                map1.put("imageTypeName","现场照片");
                map1.put("imageType",2);
                listMap.add(0,map1);
            }
            Map<String,Object> map2=new HashMap<>();
            map2.put("imageTypeId","");
            map2.put("imageTypeName","材料照片");
            map2.put("imageType",0);
            Map<String,Object> map3=new HashMap<>();
            map3.put("imageTypeId","");
            map3.put("imageTypeName","进度照片");
            map3.put("imageType",1);
            if(listMap.size()==0){
                listMap.add(0,map2);
                listMap.add(1,map3);
            }else{
                listMap.add(1,map2);
                listMap.add(2,map3);
            }
            return ServerResponse.createBySuccess("查询成功",listMap);
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }
}
