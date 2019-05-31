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


    //添加申诉
    @Override
    @ApiMethod
    public ServerResponse addComplain(HttpServletRequest request, String userToken,  String memberId,Integer complainType, String businessId,
                                      String houseId,String files) {
        return complainService.addComplain(userToken,  memberId, complainType, businessId, houseId, files);
    }

    //查询申诉
    @Override
    @ApiMethod
    public ServerResponse getComplainList(HttpServletRequest request, PageDTO pageDTO, Integer complainType, Integer state, String searchKey) {
        return complainService.getComplainList(pageDTO, complainType, state, searchKey);
    }

    //修改申诉
    @Override
    @ApiMethod
    public ServerResponse updataComplain(HttpServletRequest request, String userId, String complainId, Integer state, String description,
                                         String files,String operateId,String operateName) {
        return complainService.updataComplain(userId, complainId, state, description, files,operateId,operateName);
    }

    @Override
    @ApiMethod
    public ServerResponse getComplain(HttpServletRequest request, String complainId) {
        return complainService.getComplain(complainId);
    }

    @Override
    @ApiMethod
    public ServerResponse userStop(String houseId, String userToken,String content) {
        return complainService.userStop(houseId,userToken,content);
    }

    @Override
    @ApiMethod
    public ServerResponse adminStop(String houseId) {
        return complainService.adminStop(houseId);
    }

    @Override
    @ApiMethod
    public ServerResponse updateAdminStop(String jsonStr, String content, String houseId) {
        return complainService.updateAdminStop(jsonStr,content,houseId);
    }

    @Override
    @ApiMethod
    public ServerResponse commitStop(String backMoney, String content, String userToken, String houseId) {
        return complainService.commitStop(backMoney,content,userToken,houseId);
    }
}
