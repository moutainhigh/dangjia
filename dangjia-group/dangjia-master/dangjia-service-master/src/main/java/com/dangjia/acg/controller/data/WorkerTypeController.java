package com.dangjia.acg.controller.data;

import com.dangjia.acg.api.data.WorkerTypeAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.service.core.WorkerTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Ruking.Cheng
 * @descrilbe 工种查询
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/2/26 20:13
 */
@RestController
public class WorkerTypeController implements WorkerTypeAPI {

    @Autowired
    private WorkerTypeService workerTypeService;

    @Override
    @ApiMethod
    public ServerResponse getWorkerTypeList(Integer type) {
        return workerTypeService.getWorkerTypeList(type);
    }

    @Override
    @ApiMethod
    public WorkerType queryWorkerType(String workerTypeId) {
        return workerTypeService.queryWorkerType(workerTypeId);
    }

    @Override
    @ApiMethod
    public ServerResponse getWorkerType(String workerTypeId) {
        return workerTypeService.getWorkerType(workerTypeId);
    }

    @Override
    @ApiMethod
    public ServerResponse updataWorkerType(String workerTypeId, Integer methods, Integer inspectNumber, Integer safeState) {
        return workerTypeService.updataWorkerType(workerTypeId, methods, inspectNumber, safeState);
    }
}
