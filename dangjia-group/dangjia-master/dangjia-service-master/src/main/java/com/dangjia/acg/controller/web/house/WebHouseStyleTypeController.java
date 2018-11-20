package com.dangjia.acg.controller.web.house;

import com.dangjia.acg.api.web.house.WebHouseStyleTypeAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.design.HouseStyleTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * author: Ronalcheng
 * Date: 2018/11/10 0010
 * Time: 10:10
 */
@RestController
public class WebHouseStyleTypeController implements WebHouseStyleTypeAPI {

    @Autowired
    private HouseStyleTypeService houseStyleTypeService;

    @Override
    @ApiMethod
    public ServerResponse getList(HttpServletRequest request){
        return houseStyleTypeService.getStyleList(request);
    }
}
