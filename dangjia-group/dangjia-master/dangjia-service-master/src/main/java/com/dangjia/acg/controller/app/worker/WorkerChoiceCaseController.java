package com.dangjia.acg.controller.app.worker;

import com.dangjia.acg.api.app.worker.WorkerChoiceCaseAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.worker.WorkerChoiceCase;
import com.dangjia.acg.service.worker.WorkerChoiceCaseService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * author: qiyuxiang
 * Date: 2019/12/26
 */
@RestController
public class WorkerChoiceCaseController implements WorkerChoiceCaseAPI {

    @Autowired
    private WorkerChoiceCaseService workerChoiceCaseService;


    @Override
    @ApiMethod
    public ServerResponse getWorkerChoiceCases(HttpServletRequest request, String userToken) {
        return workerChoiceCaseService.getWorkerChoiceCases( userToken);
    }
    @Override
    @ApiMethod
    public ServerResponse getWorkerChoiceCasesCount(HttpServletRequest request, String userToken) {
        return workerChoiceCaseService.getWorkerChoiceCasesCount( userToken);
    }

    /**
     * 业主--查看案例
     * @param workerId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse queryChoiceByWorkerId(String workerId){
        return workerChoiceCaseService.queryChoiceByWorkerId( workerId);
    }
    /**
     * 删除工人精选案例
     *
     * @param id
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse delWorkerChoiceCase(HttpServletRequest request, String id) {
        return workerChoiceCaseService.delWorkerChoiceCase(id);
    }

    /**
     * 修改工人精选案例
     *
     * @param workerChoiceCase
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse editWorkerChoiceCase(HttpServletRequest request, WorkerChoiceCase workerChoiceCase) {
        return workerChoiceCaseService.editWorkerChoiceCase(workerChoiceCase);
    }

    /**
     * 新增工人精选案例
     *
     * @param workerChoiceCase
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse addWorkerChoiceCase(String userToken, WorkerChoiceCase workerChoiceCase) {
        return workerChoiceCaseService.addWorkerChoiceCase(userToken,workerChoiceCase);
    }
}
