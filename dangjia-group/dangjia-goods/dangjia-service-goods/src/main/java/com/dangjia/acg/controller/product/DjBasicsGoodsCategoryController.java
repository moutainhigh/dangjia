package com.dangjia.acg.controller.product;

import com.dangjia.acg.api.product.DjBasicsGoodsCategoryAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.product.DjBasicsGoodsCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/9/15
 * Time: 10:03
 */
@RestController
public class DjBasicsGoodsCategoryController implements DjBasicsGoodsCategoryAPI {
    @Autowired
    private DjBasicsGoodsCategoryService djBasicsGoodsCategoryService;

    @Override
    @ApiMethod
    public ServerResponse addGoodsCategory(HttpServletRequest request, String name, String parentId, String parentTop, Integer sort, Integer isLastCategory, String categoryLabelId, String coverImage, Integer purchaseRestrictions, String brandId) {
        return djBasicsGoodsCategoryService.addGoodsCategory(name,parentId,parentTop,sort,isLastCategory,categoryLabelId,coverImage,purchaseRestrictions,brandId);
    }

    @Override
    @ApiMethod
    public ServerResponse updateGoodsCategory(HttpServletRequest request, String id, String name, String parentId, String parentTop, Integer sort, Integer isLastCategory, String categoryLabelId, String coverImage, Integer purchaseRestrictions, String brandId) {
        return djBasicsGoodsCategoryService.updateGoodsCategory(id,name,parentId,parentTop,sort,isLastCategory,categoryLabelId,coverImage,purchaseRestrictions,brandId);
    }

    @Override
    @ApiMethod
    public ServerResponse queryGoodsCategory(HttpServletRequest request, String parentId) {
        return djBasicsGoodsCategoryService.queryGoodsCategory(parentId);
    }

    @Override
    @ApiMethod
    public ServerResponse addGoodsAttribute(HttpServletRequest request, String goodsCategoryId, String attributeName, Integer type, String jsonStr, Integer isScreenConditions) {
        return djBasicsGoodsCategoryService.addGoodsAttribute(goodsCategoryId,attributeName,type,jsonStr,isScreenConditions);
    }

    @Override
    @ApiMethod
    public ServerResponse updateGoodsAttribute(HttpServletRequest request, String attributeId, String attributeName, Integer type, String jsonStr, Integer isScreenConditions) {
        return djBasicsGoodsCategoryService.updateGoodsAttribute(attributeId,attributeName,type,jsonStr,isScreenConditions);
    }

    @Override
    @ApiMethod
    public ServerResponse deleteByAttributeId(HttpServletRequest request, String attributeValueId) {
        return djBasicsGoodsCategoryService.deleteByAttributeId(attributeValueId);
    }

    @Override
    @ApiMethod
    public ServerResponse queryAttributeListById(HttpServletRequest request, String goodsCategoryId) {
        return djBasicsGoodsCategoryService.queryAttributeListById(goodsCategoryId);
    }

    @Override
    @ApiMethod
    public ServerResponse queryGoodsCategoryTwo(HttpServletRequest request) {
        return djBasicsGoodsCategoryService.queryGoodsCategoryTwo();
    }
}
