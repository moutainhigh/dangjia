package com.dangjia.acg.controller.supervisor;

import com.dangjia.acg.api.supervisor.DjBasicsSiteMemoAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.supervisor.DjBaicsSiteMemo;
import com.dangjia.acg.service.supervisor.SiteMemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class DjBasicsSiteMemoController implements DjBasicsSiteMemoAPI {
    @Autowired
    private SiteMemoService siteMemoService ;
    @Override
    @ApiMethod
    public ServerResponse delSiteMemo(HttpServletRequest request,String id) {
        return siteMemoService.delSiteMemo(request,id);
    }

    @Override
    @ApiMethod
    public ServerResponse addSiteMemo(HttpServletRequest request, DjBaicsSiteMemo djBaicsSiteMemo) {
        return siteMemoService.addSiteMemo(request,djBaicsSiteMemo);
    }

    @Override
    @ApiMethod
    public ServerResponse querySiteMemo(HttpServletRequest request, String memberId,PageDTO pageDTO) {
        return siteMemoService.querySiteMemo(request,memberId,pageDTO);
    }

    @Override
    @ApiMethod
    public ServerResponse querySiteMemoDetail(HttpServletRequest request, String id) {
        return siteMemoService.querySiteMemoDetail(request,id);
    }
}
