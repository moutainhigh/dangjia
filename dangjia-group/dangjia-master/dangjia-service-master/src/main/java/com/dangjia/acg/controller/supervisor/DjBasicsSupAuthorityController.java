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
    public ServerResponse queryApplicationInfo(HttpServletRequest request,String houseId,PageDTO pageDTO) {
        return supAuthorityService.queryApplicationInfo(request,houseId,pageDTO);
    }

    @Override
    @ApiMethod
    public ServerResponse queryDvResponsibility(HttpServletRequest request,String houseId,PageDTO pageDTO) {
        return supAuthorityService.queryDvResponsibility(request,houseId,pageDTO);
    }

    @Override
    @ApiMethod
    public ServerResponse queryMaintenanceHostList(HttpServletRequest request,PageDTO pageDTO, String userToken,String keyWord) {
        return supAuthorityService.queryMaintenanceHostList(request,pageDTO,userToken,keyWord);
    }

    @Override
    @ApiMethod
    public ServerResponse queryMtHostListDetail(HttpServletRequest request, String houseId,String userToken) {
        return supAuthorityService.queryMtHostListDetail(request,houseId,userToken);
    }

}
