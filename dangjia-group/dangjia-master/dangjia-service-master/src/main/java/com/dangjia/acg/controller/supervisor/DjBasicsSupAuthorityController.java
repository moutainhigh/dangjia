package com.dangjia.acg.controller.supervisor;

import com.dangjia.acg.api.supervisor.DjBasicsSupervisorAuthorityAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.supervisor.DjBasicsSupervisorAuthorityDTO;
import com.dangjia.acg.modle.supervisor.DjBasicsSupervisorAuthority;
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
    public ServerResponse delAuthority(HttpServletRequest request, String id) {
        return supAuthorityService.delAuthority(request,id);
    }

    @Override
    @ApiMethod
    public ServerResponse searchAuthority(HttpServletRequest request, String visitState, String keyWord, PageDTO pageDTO) {
        return supAuthorityService.searchAuthority(request,visitState,keyWord,pageDTO);
    }

    @Override
    @ApiMethod
    public ServerResponse addAuthority(HttpServletRequest request, DjBasicsSupervisorAuthority djBasicsSupervisorAuthority) {
        return supAuthorityService.addAuthority(request,djBasicsSupervisorAuthority);
    }

    @Override
    @ApiMethod
    public ServerResponse addAllAuthority(HttpServletRequest request, String strAuthority,String operateId) {
        return supAuthorityService.addAllAuthority(request,strAuthority, operateId);
    }
}
