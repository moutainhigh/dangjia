package com.dangjia.acg.controller.app.other;

import com.dangjia.acg.api.app.other.IndexPageAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
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
     * 施工现场
     */
    @Override
    @ApiMethod
    public ServerResponse houseDetails(HttpServletRequest request, String houseId){
        return indexPageService.houseDetails(request,houseId);
    }
}
