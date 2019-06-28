package com.dangjia.acg.controller.basics;

import com.dangjia.acg.api.basics.GoodsGroupAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.basics.GoodsGroup;
import com.dangjia.acg.service.basics.GoodsGroupService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 商品关联组
 *
 * @author Ronalcheng
 */
@RestController
public class GoodsGroupController implements GoodsGroupAPI {

    @Autowired
    private GoodsGroupService goodsGroupService;

    /*
     * 获取所有关联组
     */
    @Override
    @ApiMethod
    public ServerResponse<PageInfo> getAllList(HttpServletRequest request, PageDTO pageDTO, String name, Integer state) {
        return goodsGroupService.getAllList(pageDTO, name, state);
    }

    /*
     * 添加关联组和货品关联关系
     */
    @Override
    @ApiMethod
    public ServerResponse addGroupLink(HttpServletRequest request, String goodsGroupId, String listOfProductId) {
        return goodsGroupService.addGroupLink(goodsGroupId, listOfProductId);
    }

    /*
     * 根据关联组id查询货品关联关系
     */
    @Override
    @ApiMethod
    public ServerResponse getGoodsGroupById(HttpServletRequest request, String goodsGroupId) {
        return goodsGroupService.getGoodsGroupById(goodsGroupId);
    }

    /*
     * 查找所有顶级分类列表
     */
    @Override
    @ApiMethod
    public ServerResponse getGoodsCategoryList(HttpServletRequest request) {
        return goodsGroupService.getGoodsCategoryList();
    }

    /*
     * 查找所有子分类列表
     */
    @Override
    @ApiMethod
    public ServerResponse getChildrenGoodsCategoryList(HttpServletRequest request, String id) {
        return goodsGroupService.getChildrenGoodsCategoryList(id);
    }

    /*
     * 查找所有商品列表
     */
    @Override
    @ApiMethod
    public ServerResponse getGoodsListByCategoryId(HttpServletRequest request, String id) {
        return goodsGroupService.getGoodsListByCategoryId(id);
    }

    /*
     * 查找所有货品列表
     */
    @Override
    @ApiMethod
    public ServerResponse getProductListByGoodsId(HttpServletRequest request, String id) {
        return goodsGroupService.getProductListByGoodsId(id);
    }

    /*
     * 添加关联组
     */
    @Override
    @ApiMethod
    public ServerResponse addGoodsGroup(HttpServletRequest request, String jsonStr) {
        return goodsGroupService.addGoodsGroup(jsonStr);
    }

    /*
     * 修改关联组
     */
    @Override
    @ApiMethod
    public ServerResponse updateGoodsGroup(HttpServletRequest request, GoodsGroup goodsGroup) {
        return goodsGroupService.updateGoodsGroup(goodsGroup);
    }

    /*
     * 根据关联组id删除关联组和货品关联关系
     */
    @Override
    @ApiMethod
    public ServerResponse deleteGoodsGroupById(HttpServletRequest request, String goodsGroupId) {
        return goodsGroupService.deleteGoodsGroupById(goodsGroupId);
    }

    /**
     * 查询商品及下属货品
     *
     * @param request
     * @param pageDTO
     * @param categoryId
     * @param name
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse queryGoodsGroupListByCategoryLikeName(HttpServletRequest request, PageDTO pageDTO, String categoryId, String name) {
        return goodsGroupService.queryGoodsGroupListByCategoryLikeName(pageDTO, categoryId, name);
    }
}
