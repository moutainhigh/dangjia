package com.dangjia.acg.controller.say;

import com.dangjia.acg.api.say.RenovationSayAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.say.RenovationSayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;



@RestController
public class RenovationSayController implements RenovationSayAPI {

    @Autowired
    private RenovationSayService renovationSayService;


    @Override
    @ApiMethod
    public ServerResponse insertRenovationSay(String content,String coverImage,String contentImage) {
        return renovationSayService.insertRenovationSay(content,coverImage,contentImage);
    }

    @Override
    @ApiMethod
    public ServerResponse upDateRenovationSay(String id,String content,String coverImage,String contentImage) {
        return renovationSayService.upDateRenovationSay(id,content,coverImage,contentImage);
    }

    @Override
    @ApiMethod
    public ServerResponse deleteRenovationSay(String id) {
        return renovationSayService.deleteRenovationSay(id);
    }

    @Override
    @ApiMethod
    public ServerResponse queryRenovationSayList(PageDTO pageDTO) {
        return renovationSayService.queryRenovationSayList(pageDTO);
    }

    @Override
    @ApiMethod
    public ServerResponse queryRenovationSayData(String userToken,String id) {
        return renovationSayService.queryRenovationSayData(userToken,id);
    }

    @Override
    @ApiMethod
    public ServerResponse queryAppRenovationSayList(String userToken,PageDTO pageDTO) {
        return renovationSayService.queryAppRenovationSayList(userToken,pageDTO);
    }

    @Override
    @ApiMethod
    public ServerResponse setThumbUp(String userToken, String id) {
        try {
            return renovationSayService.setThumbUp(userToken,id);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("点赞失败");
        }
    }

    @Override
    @ApiMethod
    public ServerResponse setPageView(String id) {
        return renovationSayService.setPageView(id);
    }

}

