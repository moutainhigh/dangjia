package com.dangjia.acg.api.shell;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * author: fzh
 * Date: 25/02/2020
 * Time: 下午 3:29
 */
@Api(description = "当家贝订单")
@FeignClient("dangjia-service-bill")
public interface BillHomeShellOrderAPI {


    /**
     * 查询兑换记录列表
     * @param request
     * @param pageDTO 分页
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param searchKey 兑换人姓名/电话/单号
     * @return
     */
    @PostMapping("/app/shellOrder/queryOrderInfoList")
    @ApiOperation(value = "查询当家贝兑换记录", notes = "查询当家贝兑换记录")
    ServerResponse queryOrderInfoList(@RequestParam("request") HttpServletRequest request,
                                    @RequestParam("pageDTO") PageDTO pageDTO,
                                    @RequestParam("startTime") Date startTime,
                                    @RequestParam("endTime") Date endTime,
                                    @RequestParam("searchKey") String searchKey);

}
