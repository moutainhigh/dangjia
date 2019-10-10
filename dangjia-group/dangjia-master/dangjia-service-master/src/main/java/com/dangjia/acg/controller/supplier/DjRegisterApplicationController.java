package com.dangjia.acg.controller.supplier;

import com.dangjia.acg.api.supplier.DjRegisterApplicationAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.supplier.DjRegisterApplication;
import com.dangjia.acg.service.supplier.DjRegisterApplicationServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 8/10/2019
 * Time: 下午 2:19
 */
@RestController
public class DjRegisterApplicationController implements DjRegisterApplicationAPI {

    @Autowired
    private DjRegisterApplicationServices djSupplierServices;

    @Override
    @ApiMethod
    public ServerResponse registerSupAndStorefront(HttpServletRequest request, DjRegisterApplication djRegisterApplication) {
        return djSupplierServices.registerSupAndStorefront(djRegisterApplication);
    }

}
