package com.dangjia.acg.controller.basics;

import com.dangjia.acg.api.basics.GoodsCategoryAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.product.BasicsGoodsCategory;
import com.dangjia.acg.service.basics.AttributeService;
import com.dangjia.acg.service.basics.GoodsCategoryService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @类 名： GoodsCategoryController
 * @功能描述： 商品/服务类别管理
 * @作者信息： zmj
 * @创建时间： 2018-9-12下午1:55:15
 */
@RestController
public class GoodsCategoryController implements GoodsCategoryAPI {
    /**
     * service
     */
    @Autowired
    private GoodsCategoryService goodsCategoryService;
    @Autowired
    private AttributeService goodsAttributeService;

    @Override
    public BasicsGoodsCategory  getGoodsCategory(String cityId, String categoryId) {
        return goodsCategoryService.getGoodsCategory(categoryId);
    }

    /**
     * 新增商品类别
     *
     * @Title: getProduct
     */
    @Override
    @ApiMethod
    public ServerResponse insertGoodsCategory(HttpServletRequest request, String name, String parentId, String parentTop, Integer sort,String cityId) {
        return goodsCategoryService.insertGoodsCategory(name, parentId, parentTop, sort,cityId);
    }

    /**
     * 修改商品类别
     *
     * @Title: getProduct
     */
    @Override
    @ApiMethod
    public ServerResponse doModifyGoodsCategory(HttpServletRequest request, String id, String name, String parentId, String parentTop, Integer sort,String cityId) {
        return goodsCategoryService.doModifyGoodsCategory(id, name, parentId, parentTop, sort,cityId);
    }

    /**
     * 查询商品类别列表
     *
     * @Title: getProduct
     */
    @Override
    @ApiMethod
    public ServerResponse queryGoodsCategory(HttpServletRequest request, String parentId,String cityId) {
        return goodsCategoryService.queryGoodsCategory(parentId,cityId);
    }

    /**
     * 根据类别id查询关联属性
     *
     * @Title: getProduct
     */
    @Override
    @ApiMethod
    public ServerResponse<PageInfo> queryGoodsAttribute(HttpServletRequest request, PageDTO pageDTO, String goodsCategoryId, String likeAttrName,String cityId,Integer type) {
        return goodsAttributeService.queryGoodsAttribute(pageDTO, goodsCategoryId, likeAttrName,cityId, type);
    }

    /**
     * 根据属性名称模糊查询属性
     *
     * @Title: getProduct
     */
    @Override
    @ApiMethod
    public ServerResponse<PageInfo> queryGoodsAttributelikeName(HttpServletRequest request, PageDTO pageDTO, String name,String cityId) {
        return goodsAttributeService.queryGoodsAttributelikeName(pageDTO, name,cityId);
    }

    /**
     * 根据属性id查询属性及其下属属性选项
     *
     * @Title: getProduct
     */
    @Override
    @ApiMethod
    public ServerResponse queryAttributeValue(HttpServletRequest request, String goodsAttributeId,String cityId) {
        return goodsAttributeService.queryAttributeValue(goodsAttributeId,cityId);
    }

    /**
     * 新增属性及其属性选项
     *
     * @Title: getProduct
     */
    @Override
    @ApiMethod
    public ServerResponse insertGoodsAttribute(HttpServletRequest request, String goodsCategoryId, String attributeName, Integer type, String jsonStr,String cityId) {
        return goodsAttributeService.insertGoodsAttribute(goodsCategoryId, attributeName, type, jsonStr,cityId);
    }

    /**
     * 修改属性及其属性选项
     *
     * @Title: getProduct
     */
    @Override
    @ApiMethod
    public ServerResponse doModifyGoodsAttribute(HttpServletRequest request, String attributeId, String attributeName, Integer type, String jsonStr,String cityId) {
        return goodsAttributeService.doModifyGoodsAttribute(attributeId, attributeName, type, jsonStr,cityId);
    }

    /**
     * 删除商品属性
     */
    @Override
    @ApiMethod
    public ServerResponse deleteGoodsAttribute(HttpServletRequest request, String goodsAttributeId,String cityId) {
        return goodsAttributeService.deleteGoodsAttribute(goodsAttributeId,cityId);
    }

    /**
     * 删除商品属性选项
     */
    @Override
    @ApiMethod
    public ServerResponse deleteByAttributeId(HttpServletRequest request, String attributeValueId,String cityId) {
        return goodsAttributeService.deleteByAttributeId(attributeValueId,cityId);
    }

    /**
     * 删除商品类别
     *
     * @Title: getProduct
     */
    @Override
    @ApiMethod
    public ServerResponse deleteGoodsCategory(HttpServletRequest request, String id,String cityId) {
        return goodsCategoryService.deleteGoodsCategory(id,cityId);
    }

    /**
     * 查询类别id查询所有父级以及父级属性
     *
     * @Title: getProduct
     */
    @Override
    @ApiMethod
    public ServerResponse queryAttributeListById(HttpServletRequest request, String goodsCategoryId,String cityId) {
        return goodsCategoryService.queryAttributeListById(goodsCategoryId,cityId);
    }

    /**
     * 查询两级商品分类
     *
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse queryGoodsCategoryTwo(HttpServletRequest request,String cityId) {
        return goodsCategoryService.queryGoodsCategoryTwo(cityId);
    }
}
