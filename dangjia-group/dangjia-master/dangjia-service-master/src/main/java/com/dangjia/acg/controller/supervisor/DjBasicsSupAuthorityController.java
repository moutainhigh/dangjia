package com.dangjia.acg.controller.supervisor;

import com.dangjia.acg.api.supervisor.DjBasicsSupervisorAuthorityAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.supervisor.SupAuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class DjBasicsSupAuthorityController implements DjBasicsSupervisorAuthorityAPI {

    @Autowired
    private SupAuthorityService supAuthorityService ;

    @Override
    @ApiMethod
    public ServerResponse queryDvResponsibility(HttpServletRequest request,String houseId,PageDTO pageDTO) {
        return supAuthorityService.queryDvResponsibility(request,houseId,pageDTO);
    }

}
