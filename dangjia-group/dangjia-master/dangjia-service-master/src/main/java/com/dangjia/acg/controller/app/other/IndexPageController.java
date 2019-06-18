package com.dangjia.acg.controller.app.other;

import com.dangjia.acg.api.app.other.IndexPageAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.other.IndexPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * author: Ronalcheng
 * Date: 2019/1/4 0004
 * Time: 9:47
 */
@RestController
public class IndexPageController implements IndexPageAPI {
    @Autowired
    private IndexPageService indexPageService;

    /**
     * 根据城市，小区，最小最大面积查询房子
     */
    @Override
    @ApiMethod
    public ServerResponse queryHouseDistance(HttpServletRequest request,String userToken, String cityId, String villageId, Double square, PageDTO pageDTO){
        return indexPageService.queryHouseDistance( request, userToken,  cityId,  villageId,  square,  pageDTO);
    }
    /**
     * 施工现场详情
     */
    @Override
    @ApiMethod
    public ServerResponse houseDetails(HttpServletRequest request, String houseId){
        return indexPageService.houseDetails(request,houseId);
    }

    /**
     * 施工现场
     * @param request
     * @param latitude
     * @param longitude
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse jobLocation(HttpServletRequest request, String latitude, String longitude) {
        return indexPageService.jobLocation(request,latitude,longitude);
    }
}
