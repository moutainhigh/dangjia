package com.dangjia.acg.controller.product.app;

import com.dangjia.acg.api.product.BasicsGoodsCategoryAPI;
import com.dangjia.acg.api.product.app.AppCategoryGoodsAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.basics.AttributeService;
import com.dangjia.acg.service.product.BasicsGoodsCategoryService;
import com.dangjia.acg.service.product.app.AppCategoryGoodsService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @类 名： AppCategoryGoodsController
 * @功能描述： 商品分类app端
 * @作者信息： QYX
 * @创建时间： 2019-9-16下午2:33:37
 */
@RestController
public class AppCategoryGoodsController implements AppCategoryGoodsAPI {
    /**
     * service
     */
    @Autowired
    private AppCategoryGoodsService appCategoryGoodsService;

    @Override
    @ApiMethod
    public ServerResponse queryTopCategoryLabel(String cityId) {
        return appCategoryGoodsService.queryTopCategoryLabel();
    }

    @Override
    @ApiMethod

    public ServerResponse queryLeftCategoryByDatas(String cityId,String categoryLabelId) {
        return appCategoryGoodsService.queryLeftCategoryByDatas(categoryLabelId);
    }

    @Override
    @ApiMethod
    public ServerResponse queryRightCategoryByDatas(String cityId,String parentId) {
        return appCategoryGoodsService.queryRightCategoryByDatas(parentId);
    }
}
