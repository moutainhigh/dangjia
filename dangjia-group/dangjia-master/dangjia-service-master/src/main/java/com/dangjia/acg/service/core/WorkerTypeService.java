package com.dangjia.acg.service.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.ElasticSearchAPI;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dto.ElasticSearchDTO;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.modle.core.WorkerType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ruking.Cheng
 * @descrilbe 工种查询
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/2/26 20:13
 */
@Service
public class WorkerTypeService {

    @Autowired
    private IWorkerTypeMapper workerTypeMapper;
    @Autowired
    private ElasticSearchAPI elasticSearchAPI;

    public ServerResponse unfinishedFlow(String houseId) {
        try {
            List<WorkerType> workerTypeList = workerTypeMapper.unfinishedFlow(houseId);
            if(workerTypeList.size()==0){
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), "无相关记录");
            }
            return ServerResponse.createBySuccess("查询成功", workerTypeList);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    public WorkerType queryWorkerType(String workerTypeId) {
        return workerTypeMapper.selectByPrimaryKey(workerTypeId);
    }

    /**
     * 获取工种列表
     *
     * @param type -1：全部，0：除精算防水工种列表，1：除设计精算防水工种列表
     * @return
     */
    public ServerResponse getWorkerTypeList(Integer type) {
        ElasticSearchDTO elasticSearchDTO=new ElasticSearchDTO();
        //表名字
        elasticSearchDTO.setTableTypeName(WorkerType.class.getSimpleName());

        //排序字段
        Map<String, Integer> sortMap = new HashMap<>();
        sortMap.put(WorkerType.SORT, 0);
        elasticSearchDTO.setSortMap(sortMap);

        //不包含数据
        Map<String, String> notParamMap = new HashMap<>();
        if (type != null && type == 0) {
            notParamMap.put(WorkerType.TYPE, "2,7");
        } else if (type != null && type == 1) {
            notParamMap.put(WorkerType.TYPE, "1,2,7");
        } else if (type != null && type == 2) {
            notParamMap.put(WorkerType.TYPE, "7");
        }
        notParamMap.put(WorkerType.STATE, "2");
        if(!notParamMap.isEmpty()){
            elasticSearchDTO.setNotParamMap(notParamMap);
        }
        List<JSONObject> redata =elasticSearchAPI.searchESJson(elasticSearchDTO);
        if(redata==null || redata.size()>0) {
            Example example = new Example(WorkerType.class);
            Example.Criteria criteria = example.createCriteria();
            if (notParamMap!=null&& !CommonUtil.isEmpty(notParamMap.get(WorkerType.TYPE))) {
                criteria.andCondition("type not in ("+notParamMap.get(WorkerType.TYPE)+") ");
            }
            criteria.andNotEqualTo(WorkerType.STATE, 2);
            example.orderBy(WorkerType.SORT).asc();
            List<WorkerType> workerTypeList = workerTypeMapper.selectByExample(example);
            if (workerTypeList == null || workerTypeList.size() <= 0) {
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode()
                        , "查无数据");
            }
            List<Map> maps = (List<Map>) BeanUtils.listToMap(workerTypeList);
            for (Map map : maps) {
                map.put("workerTypeId", map.get(WorkerType.ID));
//                System.out.println(JSON.toJSONString(map));
//                elasticSearchAPI.saveESJson(JSON.toJSONString(map),  WorkerType.class.getSimpleName());
            }
            return ServerResponse.createBySuccess("查询成功", maps);
        }
        return ServerResponse.createBySuccess("查询成功", redata);
    }

    /**
     * 根据workerTypeId返回工种对象
     *
     * @param workerTypeId workerTypeId
     * @return
     */
    public ServerResponse getWorkerType(String workerTypeId) {
        JSONObject workerType = elasticSearchAPI.getSearchJsonId(WorkerType.class.getSimpleName(),workerTypeId);
        if (workerType == null) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode()
                    , "查无工种");
        }
        return ServerResponse.createBySuccess("查询成功", workerType);
    }

    /**
     * 修改可抢单数，标准巡查次数，免费次数
     *
     * @param workerTypeId  workerTypeId
     * @param methods       可抢单数
     * @param inspectNumber 标准巡查次数
     * @param safeState     免费要货次数
     * @return
     */
    public ServerResponse updataWorkerType(String workerTypeId, Integer methods, Integer inspectNumber, Integer safeState) {
        if (workerTypeId == null) {
            return ServerResponse.createByErrorMessage("操作失败，请传入workerTypeId");
        }
        WorkerType workerType = workerTypeMapper.selectByPrimaryKey(workerTypeId);
        if (methods != null) {
            workerType.setMethods(methods);
        }
        if (inspectNumber != null) {
            workerType.setInspectNumber(inspectNumber);
        }
        if (safeState != null) {
            workerType.setSafeState(safeState);
        }
        workerTypeMapper.updateByPrimaryKeySelective(workerType);
        elasticSearchAPI.updateResponse(JSON.toJSONString(workerType),WorkerType.class.getSimpleName(),workerTypeId);
        return ServerResponse.createBySuccessMessage("操作成功");
    }
}
