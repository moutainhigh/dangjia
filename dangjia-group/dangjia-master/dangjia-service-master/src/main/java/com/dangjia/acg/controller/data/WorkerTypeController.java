package com.dangjia.acg.controller.data;

import com.dangjia.acg.api.data.WorkerTypeAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.service.core.WorkerTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with IntelliJ IDEA.
 * author: Ronalcheng
 * Date: 2018/10/31 0031
 * Time: 13:50
 */
@RestController
public class WorkerTypeController implements WorkerTypeAPI {

    @Autowired
    private WorkerTypeService workerTypeService;

    /**
     * 注册用
     */
    @Override
    @ApiMethod
    public ServerResponse getWorkerTypeRegister(){
        return workerTypeService.getWorkerTypeRegister();
    }

    @Override
    @ApiMethod
    public ServerResponse getWorkerTypeList(){
        return workerTypeService.getWorkerTypeList();
    }
    @Override
    @ApiMethod
    public ServerResponse getWorkerTypeListAll(){
        return workerTypeService.getWorkerTypeListAll();
    }

    /**
     *  根据workerTypeId返回工种名字
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getNameByWorkerTypeId(String workerTypeId){
        WorkerType workerType = workerTypeService.getWorkerTypeId(workerTypeId);
        if(workerType==null){
            return ServerResponse.createBySuccess("查询成功","");
        }
        return ServerResponse.createBySuccess("查询成功", workerType.getName());
    }

    /**
     *  根据workerTypeId返回工种对象
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getWorkerType(String workerTypeId){
        WorkerType workerType = workerTypeService.getWorkerTypeId(workerTypeId);
        return ServerResponse.createBySuccess("查询成功", workerType);
    }
}
