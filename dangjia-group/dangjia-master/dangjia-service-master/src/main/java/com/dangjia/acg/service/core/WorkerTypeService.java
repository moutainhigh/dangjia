package com.dangjia.acg.service.core;

import com.dangjia.acg.common.exception.BaseException;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.core.WorkerTypeDTO;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.modle.core.WorkerType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: Ronalcheng
 * Date: 2018/10/31 0031
 * Time: 11:07
 * 工种
 */
@Service
public class WorkerTypeService {

    @Autowired
    private IWorkerTypeMapper workerTypeMapper;

    public WorkerType getWorkerTypeId(String workerTypeId){
        WorkerType workerType = workerTypeMapper.selectByPrimaryKey(workerTypeId);
        return workerType;
    }

    /**
     * 除精算防水工种列表
     */
    public ServerResponse getWorkerTypeRegister(){
        try{
            List<WorkerTypeDTO> wtList = workerTypeMapper.getWorkerTypeRegister();
            return ServerResponse.createBySuccess("查询成功", wtList);
        }catch (Exception e){
            e.printStackTrace();
            throw new BaseException(ServerCode.WRONG_PARAM, "查询失败");
        }
    }

    /**
     * 除设计精算防水工种列表
     */
    public ServerResponse getWorkerTypeList(){
        try{
            List<WorkerTypeDTO> wtList = workerTypeMapper.getWorkerTypeList();
            return ServerResponse.createBySuccess("查询成功", wtList);
        }catch (Exception e){
            e.printStackTrace();
            throw new BaseException(ServerCode.WRONG_PARAM, "查询失败");
        }
    }
    /**
     * 所有工种列表
     */
    public ServerResponse getWorkerTypeListAll(){
        try{
            List<WorkerTypeDTO> wtList = workerTypeMapper.getWorkerTypeListAll();
            return ServerResponse.createBySuccess("查询成功", wtList);
        }catch (Exception e){
            e.printStackTrace();
            throw new BaseException(ServerCode.WRONG_PARAM, "查询失败");
        }
    }
}
