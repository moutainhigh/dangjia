package com.dangjia.acg.controller.supplier;

import com.dangjia.acg.api.supplier.DjSupApplicationAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.modle.supplier.DjSupApplication;
import com.dangjia.acg.service.supplier.DjSupApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 10/10/2019
 * Time: 下午 3:43
 */
@RestController
public class DjSupApplicationController implements DjSupApplicationAPI {

    @Autowired
    private DjSupApplicationService djSupApplicationService;

    @Override
    @ApiMethod
    public List<DjSupApplication> queryDjSupApplicationBySupId(String supId) {
        return djSupApplicationService.queryDjSupApplicationBySupId(supId);
    }
}
