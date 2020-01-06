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
    public ServerResponse queryRenovationSayData(String id) {
        return renovationSayService.queryRenovationSayData(id);
    }

    @Override
    @ApiMethod
    public ServerResponse queryAppRenovationSayList() {
        return renovationSayService.queryAppRenovationSayList();
    }

}

