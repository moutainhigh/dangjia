package com.dangjia.acg.controller.product;

import com.dangjia.acg.api.product.DjBasicsActuarialConfigurationAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.product.DjBasicsActuarialConfigurationServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/9/20
 * Time: 16:44
 */
@RestController
public class DjBasicsActuarialConfigurationController implements DjBasicsActuarialConfigurationAPI {
    @Autowired
    private DjBasicsActuarialConfigurationServices djBasicsActuarialConfigurationServices;

    @Override
    @ApiMethod
    public ServerResponse addConfiguration(HttpServletRequest request,String jsonStr) {
        return djBasicsActuarialConfigurationServices.addConfiguration(jsonStr);
    }

    @Override
    @ApiMethod
    public ServerResponse queryConfiguration(HttpServletRequest request) {
        return djBasicsActuarialConfigurationServices.queryConfiguration();
    }

    @Override
    @ApiMethod
    public ServerResponse querySingleConfiguration(HttpServletRequest request, String phaseId) {
        return djBasicsActuarialConfigurationServices.querySingleConfiguration(phaseId);
    }
}
