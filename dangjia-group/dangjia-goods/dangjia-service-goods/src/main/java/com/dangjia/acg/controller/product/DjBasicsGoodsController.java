package com.dangjia.acg.controller.product;

import com.dangjia.acg.api.product.DjBasicsGoodsAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.product.DjBasicsGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/9/12
 * Time: 9:54
 */
@RestController
public class DjBasicsGoodsController implements DjBasicsGoodsAPI {
    @Autowired
    private DjBasicsGoodsService djBasicsGoodsService;

    @Override
    @ApiMethod
    public ServerResponse addLabels(HttpServletRequest request, String goodsId, String labels,String cityId) {
        try {
            return djBasicsGoodsService.addLabels(goodsId, labels,cityId);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }

    @Override
    @ApiMethod
    public ServerResponse queryGoodsLabels(HttpServletRequest request, String goodsId) {
        return djBasicsGoodsService.queryGoodsLabels(goodsId);
    }
}
