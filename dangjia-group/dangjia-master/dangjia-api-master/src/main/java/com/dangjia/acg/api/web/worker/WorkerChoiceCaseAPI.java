package com.dangjia.acg.api.web.worker;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.worker.WorkerChoiceCase;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * author: qiyuxiang
 * Date: 2019/12/26
 */
@FeignClient("dangjia-service-master")
@Api(value = "工人精选案例接口", description = "工人精选案例接口")
public interface WorkerChoiceCaseAPI {

    @PostMapping("worker/choice/list")
    @ApiOperation(value = "工人精选案例", notes = "工人精选案例")
    ServerResponse getWorkerChoiceCases(@RequestParam("request") HttpServletRequest request,
                                       @RequestParam("pageDTO") PageDTO pageDTO,
                                       @RequestParam("workerId") String workerId);

    /**
     * 删除工人精选案例
     *
     * @param id
     * @return
     */
    @PostMapping("/worker/choice/del")
    @ApiOperation(value = "删除工人精选案例", notes = "删除工人精选案例")
    ServerResponse delWorkerChoiceCase(@RequestParam("request") HttpServletRequest request,
                                      @RequestParam("id") String id);

    /**
     * 修改工人精选案例
     *
     * @param workerChoiceCase
     * @return
     */
    @PostMapping("/worker/choice/edit")
    @ApiOperation(value = "修改工人精选案例", notes = "修改工人精选案例")
    ServerResponse editWorkerChoiceCase(@RequestParam("request") HttpServletRequest request,
                                       @RequestParam("workerChoiceCase") WorkerChoiceCase workerChoiceCase);

    /**
     * 新增工人精选案例
     *
     * @param workerChoiceCase
     * @return
     */
    @PostMapping("/worker/choice/add")
    @ApiOperation(value = "新增工人精选案例", notes = "新增工人精选案例")
    ServerResponse addWorkerChoiceCase(@RequestParam("request") HttpServletRequest request,
                                      @RequestParam("workerChoiceCase") WorkerChoiceCase workerChoiceCase);

}
