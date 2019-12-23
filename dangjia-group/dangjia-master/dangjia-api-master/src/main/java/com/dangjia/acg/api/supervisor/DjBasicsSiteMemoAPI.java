package com.dangjia.acg.api.supervisor;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.supervisor.DjBaicsSiteMemo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;


@Api(description = "备忘录管理接口")
@FeignClient("dangjia-service-master")
public interface DjBasicsSiteMemoAPI {

    @PostMapping("app/supervisor/delSiteMemo")
    @ApiOperation(value = "删除备忘录", notes = "删除备忘录")
    ServerResponse delSiteMemo(@RequestParam("request") HttpServletRequest request, @RequestParam("id") String id);

    @PostMapping("app/supervisor/addSiteMemo")
    @ApiOperation(value = "新增备忘录", notes = "新增备忘录")
    ServerResponse addSiteMemo(@RequestParam("request") HttpServletRequest request, @RequestParam("djBaicsSiteMemo") DjBaicsSiteMemo djBaicsSiteMemo);

    @PostMapping("app/supervisor/querySiteMemo")
    @ApiOperation(value = "查询备忘录列表", notes = "查询备忘录列表")
    ServerResponse querySiteMemo(@RequestParam("request") HttpServletRequest request, @RequestParam("memberId") String memberId, @RequestParam("pageDTO") PageDTO pageDTO);

    @PostMapping("app/supervisor/querySiteMemoDetail")
    @ApiOperation(value = "查询备忘录详情", notes = "查询备忘录详情")
    ServerResponse querySiteMemoDetail(@RequestParam("request") HttpServletRequest request, @RequestParam("id") String id);

}
