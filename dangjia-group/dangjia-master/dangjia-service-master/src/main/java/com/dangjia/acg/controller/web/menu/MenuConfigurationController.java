package com.dangjia.acg.controller.web.menu;

import com.dangjia.acg.api.web.menu.MenuConfigurationAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.menu.MenuConfiguration;
import com.dangjia.acg.service.menu.MenuConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Ruking.Cheng
 * @descrilbe web端菜单编辑接口实现
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/4/23 5:54 PM
 */
@RestController
public class MenuConfigurationController implements MenuConfigurationAPI {
    @Autowired
    private MenuConfigurationService menuConfigurationService;


    @Override
    @ApiMethod
    public ServerResponse setMenuConfiguration(HttpServletRequest request, MenuConfiguration menuConfiguration) {
        return menuConfigurationService.setMenuConfiguration(menuConfiguration);
    }

    @Override
    @ApiMethod
    public ServerResponse delMenuConfiguration(HttpServletRequest request, String menuConfigurationId) {
        return menuConfigurationService.delMenuConfiguration(menuConfigurationId);
    }

    @Override
    @ApiMethod
    public ServerResponse getMenuConfigurations(HttpServletRequest request, PageDTO pageDTO, MenuConfiguration menuConfiguration) {
        return menuConfigurationService.getMenuConfigurations(pageDTO, menuConfiguration);
    }
}
