package com.dangjia.acg.service.core;

import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.modle.core.WorkerType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

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
        Example example = new Example(WorkerType.class);
        Example.Criteria criteria = example.createCriteria();
        if (type != null && type == 0) {
            criteria.andCondition("type not in (2,7) ");
        } else if (type != null && type == 1) {
            criteria.andCondition("type not in (1,2,7)");
        } else if (type != null && type == 2) {
            criteria.andCondition("type not in (7)");
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
        }
        return ServerResponse.createBySuccess("查询成功", maps);
    }

    /**
     * 根据workerTypeId返回工种对象
     *
     * @param workerTypeId workerTypeId
     * @return
     */
    public ServerResponse getWorkerType(String workerTypeId) {
        WorkerType workerType = workerTypeMapper.selectByPrimaryKey(workerTypeId);
        if (workerType == null) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode()
                    , "查无工种");
        }
        Map map = BeanUtils.beanToMap(workerType);
        map.put("workerTypeId", map.get(WorkerType.ID));
        return ServerResponse.createBySuccess("查询成功", map);
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
        WorkerType workerType = new WorkerType();
        workerType.setId(workerTypeId);
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
        return ServerResponse.createBySuccessMessage("操作成功");
    }
}
