package com.dangjia.acg.controller.app.product;

import com.dangjia.acg.api.app.product.BrowseRecordAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.product.BrowseRecord;
import com.dangjia.acg.service.product.BrowseRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class BrowseRecordController  implements BrowseRecordAPI {
    @Autowired
    private BrowseRecordService browseRecordService;

    @Override
    @ApiMethod
    public ServerResponse queryBrowseRecord(HttpServletRequest request, String userToken, PageDTO pageDTO) {
        return browseRecordService.queryBrowseRecord(request,userToken,pageDTO);
    }

    @Override
    @ApiMethod
    public ServerResponse addBrowseRecord(HttpServletRequest request, String userToken, BrowseRecord browseRecord) {
        return browseRecordService.addBrowseRecord(request,userToken,browseRecord);
    }
}
