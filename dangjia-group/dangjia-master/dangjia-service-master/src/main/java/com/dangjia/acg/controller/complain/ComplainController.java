package com.dangjia.acg.controller.complain;

import com.dangjia.acg.api.complain.ComplainAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.complain.ComplainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


@RestController
public class ComplainController implements ComplainAPI {

    @Autowired
    ComplainService complainService;


    //添加申述
    @Override
    @ApiMethod
    public ServerResponse addComplain(HttpServletRequest request, String userToken, Integer complainType, String businessId, String houseId) {
        return complainService.addComplain(userToken, complainType, businessId, houseId);
    }

    //查询申述
    @Override
    @ApiMethod
    public ServerResponse getComplainList(HttpServletRequest request, PageDTO pageDTO, Integer complainType, Integer state, String searchKey) {
        return complainService.getComplainList(pageDTO, complainType, state, searchKey);
    }

    //修改申述
    @Override
    @ApiMethod
    public ServerResponse updataComplain(HttpServletRequest request, String userId, String complainId, Integer state, String description, String files) {
        return complainService.updataComplain(userId, complainId, state, description, files);
    }

    @Override
    @ApiMethod
    public ServerResponse getComplain(HttpServletRequest request, String complainId) {
        return complainService.getComplain(complainId);
    }
}
