package com.dangjia.acg.controller.product;

import com.dangjia.acg.api.product.DjBasicsLabelAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.product.DjBasicsLabelService;
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
public class DjBasicsLabelController implements DjBasicsLabelAPI {
    @Autowired
    private DjBasicsLabelService djBasicsLabelService;

    @Override
    @ApiMethod
    public ServerResponse addCommodityLabels(HttpServletRequest request, String labelName, String labelValue) {
        return djBasicsLabelService.addCommodityLabels(labelName,labelValue);
    }

    @Override
    @ApiMethod
    public ServerResponse updateCommodityLabels(HttpServletRequest request, String id, String labelName, String labelValue) {
        return djBasicsLabelService.updateCommodityLabels(id, labelName, labelValue);
    }

    @Override
    @ApiMethod
    public ServerResponse delCommodityLabels(HttpServletRequest request, String id) {
        return djBasicsLabelService.delCommodityLabels(id);
    }

    @Override
    @ApiMethod
    public ServerResponse queryCommodityLabelsById(HttpServletRequest request, String id) {
        return djBasicsLabelService.queryCommodityLabelsById(id);
    }

    @Override
    @ApiMethod
    public ServerResponse queryCommodityLabels(HttpServletRequest request) {
        return djBasicsLabelService.queryCommodityLabels();
    }
}
