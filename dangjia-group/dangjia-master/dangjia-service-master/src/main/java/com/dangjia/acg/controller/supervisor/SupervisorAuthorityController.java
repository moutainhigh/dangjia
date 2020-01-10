package com.dangjia.acg.controller.supervisor;

import com.dangjia.acg.api.supervisor.SupervisorAuthorityAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.supervisor.SupervisorAppService;
import com.dangjia.acg.service.supervisor.SupervisorWebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Ruking.Cheng
 * @descrilbe 督导权限配置接口
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2020/1/7 6:47 PM
 */
@RestController
public class SupervisorAuthorityController implements SupervisorAuthorityAPI {

    @Autowired
    private SupervisorWebService supervisorWebService;
    @Autowired
    private SupervisorAppService supervisorAppService;

    @Override
    @ApiMethod
    public ServerResponse getStayAuthorityList(HttpServletRequest request, PageDTO pageDTO, String cityId, String memberId, Integer visitState, String searchKey) {
        return supervisorWebService.getStayAuthorityList(pageDTO, cityId, memberId, visitState, searchKey);
    }

    @Override
    @ApiMethod
    public ServerResponse getAuthorityList(HttpServletRequest request, PageDTO pageDTO, String memberId, Integer visitState, String searchKey) {
        return supervisorWebService.getAuthorityList(pageDTO, memberId, visitState, searchKey);
    }

    @Override
    @ApiMethod
    public ServerResponse addAuthority(HttpServletRequest request, String memberId, String houseIds, String userId) {
        return supervisorWebService.addAuthority(memberId, houseIds, userId);
    }

    @Override
    @ApiMethod
    public ServerResponse deleteAuthority(HttpServletRequest request, String memberId, String houseIds) {
        return supervisorWebService.deleteAuthority(memberId, houseIds);
    }

    @Override
    @ApiMethod
    public ServerResponse getSupHomePage(HttpServletRequest request, String userToken, String cityId) {
        return supervisorAppService.getSupHomePage(userToken, cityId);
    }

    @Override
    @ApiMethod
    public ServerResponse getSupHouseList(HttpServletRequest request, PageDTO pageDTO, String userToken, String cityId, Integer sortNum, Integer type, String latitude, String longitude, String searchKey) {
        return supervisorAppService.getSupHouseList(pageDTO, userToken, cityId, sortNum, type, latitude, longitude, searchKey);
    }

    @Override
    @ApiMethod
    public ServerResponse getSupHouseDetails(HttpServletRequest request, String userToken, String houseId) {
        return supervisorAppService.getSupHouseDetails(userToken, houseId);
    }
}
