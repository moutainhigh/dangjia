package com.dangjia.acg.controller.product;

import com.dangjia.acg.api.product.DjBasicsMaintainAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.product.DjBasicsMaintainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/7/25
 * Time: 13:56
 */
@RestController
public class DjBasicsMaintainController implements DjBasicsMaintainAPI {

    @Autowired
    private DjBasicsMaintainService djBasicsMaintainService;

//    @Override
    @ApiMethod
    public ServerResponse queryMatchWord(HttpServletRequest request,String name) {
        return djBasicsMaintainService.queryMatchWord(name);

    }

    @Override
    @ApiMethod
    public ServerResponse addKeywords(String keywordName, String searchItem) {
        return djBasicsMaintainService.addKeywords(keywordName,searchItem);
    }

    @Override
    @ApiMethod
    public ServerResponse updateKeywords(String id, String keywordName, String searchItem) {
        return djBasicsMaintainService.updateKeywords(id,keywordName,searchItem);
    }

    @Override
    @ApiMethod
    public ServerResponse addRelatedTags(String id, String labelIds) {
        return djBasicsMaintainService.addRelatedTags(id,labelIds);
    }

    @Override
    @ApiMethod
    public ServerResponse delKeywords(String id) {
        return djBasicsMaintainService.delKeywords(id);
    }

    @Override
    @ApiMethod
    public ServerResponse queryKeywords(String id) {
        return djBasicsMaintainService.queryKeywords(id);
    }
}
