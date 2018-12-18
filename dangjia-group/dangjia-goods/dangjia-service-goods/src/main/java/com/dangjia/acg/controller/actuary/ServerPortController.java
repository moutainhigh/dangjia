package com.dangjia.acg.controller.actuary;

import com.dangjia.acg.api.actuary.ServerPortAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.basics.TechnologyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 *
 *
 * @类 名： 服务化接口
 * @功能描述：
 * @作者信息： zmj
 * @创建时间： 2018-9-18下午3:52:43
 */
@RestController
public class ServerPortController implements ServerPortAPI {
    @Autowired
    private TechnologyService technologyService;

    @Override
    @ApiMethod
    public ServerResponse getSearchBox(HttpServletRequest request, PageDTO pageDTO,String content, String cityId, int type){
        return technologyService.queryByName(content, pageDTO, cityId,  type);
    }

    @Override
    @ApiMethod
    public ServerResponse getHeatSearchBox(HttpServletRequest request) {
        return technologyService.getHeatSearchBox();
    }
}