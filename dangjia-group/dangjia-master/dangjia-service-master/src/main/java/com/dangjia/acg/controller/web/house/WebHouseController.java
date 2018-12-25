package com.dangjia.acg.controller.web.house;

import com.dangjia.acg.api.web.house.WebHouseAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.house.HouseDTO;
import com.dangjia.acg.service.house.HouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * author: Ronalcheng
 * Date: 2018/11/5 0005
 * Time: 15:40
 */
@RestController
public class WebHouseController implements WebHouseAPI {

    @Autowired
    private HouseService houseService;

    @Override
    @ApiMethod
    public ServerResponse getList(HttpServletRequest request, String memberId){
        return houseService.getList(memberId);
    }

    @Override
    @ApiMethod
    public ServerResponse startWorkPage(HttpServletRequest request,String houseId){
        return houseService.startWorkPage(request,houseId);
    }

    @Override
    @ApiMethod
    public ServerResponse startWork(HttpServletRequest request, HouseDTO houseDTO,String members,String prefixs){
        return houseService.startWork(request,houseDTO, members, prefixs);
    }
}
