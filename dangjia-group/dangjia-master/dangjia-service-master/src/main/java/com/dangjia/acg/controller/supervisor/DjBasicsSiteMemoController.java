package com.dangjia.acg.controller.supervisor;

import com.dangjia.acg.api.supervisor.DjBasicsSiteMemoAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.supervisor.SiteMemo;
import com.dangjia.acg.service.supervisor.SiteMemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class DjBasicsSiteMemoController implements DjBasicsSiteMemoAPI {
    @Autowired
    private SiteMemoService siteMemoService;

    @Override
    @ApiMethod
    public ServerResponse addSiteMemo(HttpServletRequest request, String userToken, String houseId, Integer type, String remark, String remindMemberIds, String reminderTime) {
        return siteMemoService.addSiteMemo(userToken, houseId, type, remark, remindMemberIds, reminderTime);
    }

    @Override
    @ApiMethod
    public ServerResponse deleteSiteMemo(HttpServletRequest request, String userToken, String memoId) {
        return siteMemoService.deleteSiteMemo(userToken, memoId);
    }

    @Override
    @ApiMethod
    public ServerResponse getMemoMessage(HttpServletRequest request, String userToken) {
        return siteMemoService.getMemoMessage(userToken);
    }

    @Override
    @ApiMethod
    public ServerResponse getMemoMessageList(HttpServletRequest request, PageDTO pageDTO, String userToken) {
        return siteMemoService.getMemoList(pageDTO, userToken, 1);
    }

    @Override
    @ApiMethod
    public ServerResponse getMemoList(HttpServletRequest request, PageDTO pageDTO, String userToken) {
        return siteMemoService.getMemoList(pageDTO, userToken, 0);
    }

    @Override
    @ApiMethod
    public ServerResponse getSiteMemo(HttpServletRequest request, String userToken, String memoId) {
        return siteMemoService.getSiteMemo(userToken, memoId);
    }

    @Override
    @ApiMethod
    public ServerResponse remindSiteMemo() {
        return siteMemoService.remindSiteMemo();
    }

    @Override
    @ApiMethod
    public ServerResponse getHouseMemberList(HttpServletRequest request, String userToken, String houseId) {
        return siteMemoService.getHouseMemberList(userToken, houseId);
    }

}
