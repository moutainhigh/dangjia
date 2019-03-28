package com.dangjia.acg.controller.web.clue;

import com.dangjia.acg.api.web.clue.WebClueTalkAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.clue.ClueTalkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebClueTalkController implements WebClueTalkAPI {
   @Autowired
   private ClueTalkService clueTalkService;
    @Override
    @ApiMethod
    public ServerResponse getTalkByClueId( String clueID,Integer pageNum, Integer pageSize) {
        return clueTalkService.getTalkByClueId(clueID,pageNum,pageSize);
    }

    /**
     * 添加沟通内容
     * @param clueId
     * @param talkContent
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse addTalk(String clueId,String talkContent){
        return clueTalkService.addTalk(clueId,talkContent);
    }
}
