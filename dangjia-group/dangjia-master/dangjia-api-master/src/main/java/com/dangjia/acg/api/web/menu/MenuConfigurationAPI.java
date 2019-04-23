package com.dangjia.acg.api.web.menu;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.menu.MenuConfiguration;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@FeignClient("dangjia-service-master")
@Api(value = "web端菜单编辑接口", description = "web端菜单编辑接口")
public interface MenuConfigurationAPI {

    @PostMapping("web/menu/setMenuConfiguration")
    @ApiOperation(value = "新增/编辑菜单", notes = "新增/编辑菜单")
    ServerResponse setMenuConfiguration(@RequestParam("request") HttpServletRequest request,
                                        @RequestParam("menuConfiguration") MenuConfiguration menuConfiguration);

    @PostMapping("web/menu/delMenuConfiguration")
    @ApiOperation(value = "删除菜单", notes = "删除菜单")
    ServerResponse delMenuConfiguration(@RequestParam("request") HttpServletRequest request,
                                        @RequestParam("menuConfigurationId") String menuConfigurationId);

    @PostMapping("web/menu/getMenuConfigurations")
    @ApiOperation(value = "获取菜单", notes = "获取菜单")
    ServerResponse getMenuConfigurations(@RequestParam("request") HttpServletRequest request,
                                         @RequestParam("pageDTO") PageDTO pageDTO,
                                         @RequestParam("menuConfiguration") MenuConfiguration menuConfiguration);
}
