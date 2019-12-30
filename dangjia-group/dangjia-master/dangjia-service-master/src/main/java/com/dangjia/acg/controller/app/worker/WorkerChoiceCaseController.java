package com.dangjia.acg.controller.app.worker;

import com.dangjia.acg.api.app.worker.WorkerChoiceCaseAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.worker.WorkerChoiceCase;
import com.dangjia.acg.service.worker.WorkerChoiceCaseService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ServerResponse getWorkerChoiceCases(HttpServletRequest request, PageDTO pageDTO, String workerId) {
        return workerChoiceCaseService.getWorkerChoiceCases(pageDTO, workerId);
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
    public ServerResponse addWorkerChoiceCase(HttpServletRequest request, WorkerChoiceCase workerChoiceCase) {
        return workerChoiceCaseService.addWorkerChoiceCase(workerChoiceCase);
    }
}
