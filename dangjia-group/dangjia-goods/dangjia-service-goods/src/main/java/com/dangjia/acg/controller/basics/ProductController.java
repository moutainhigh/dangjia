package com.dangjia.acg.controller.basics;


import com.dangjia.acg.api.basics.ProductAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.basics.Product;
import com.dangjia.acg.service.basics.GoodsService;
import com.dangjia.acg.service.basics.ProductService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @类 名： ProductController
 * @功能描述： TODO
 * @作者信息： zmj
 * @创建时间： 2018-9-10上午9:25:10queryGoodsListByCategoryLikeName
 */
@RestController
public class ProductController implements ProductAPI {
    @Autowired
    private ProductService productService;
    @Autowired
    private GoodsService goodsService;

    /**
     * 查询所有货品
     *
     * @throws
     * @Title: queryProduct
     * @Description: TODO
     * @param: @param category_id
     * @param: @return
     * @return: JsonResult
     */
    @Override
    @ApiMethod
    public ServerResponse<PageInfo> queryProduct(HttpServletRequest request, PageDTO pageDTO, String categoryId,
                                                 String cityId) {
        return productService.queryProduct(pageDTO, categoryId,cityId);
    }

    /**
     * 查询所有单位
     *
     * @throws
     * @Title: queryUnit
     * @Description: TODO
     * @param: @return
     * @return: JsonResult
     */
    @Override
    @ApiMethod
    public ServerResponse queryUnit(HttpServletRequest request,String cityId) {
        return productService.queryUnit(cityId);
    }

    /**
     * 查询所有品牌
     *
     * @throws
     * @Title: queryBrand
     * @Description: TODO
     * @param: @return
     * @return: JsonResult
     */
    @Override
    @ApiMethod
    public ServerResponse queryBrand(HttpServletRequest request,String cityId) {
        return productService.queryBrand(cityId);
    }

    /**
     * 根据品牌查询所有品牌系列
     *
     * @throws
     * @Title: queryBrandSeries
     * @Description: TODO
     * @param: @return
     * @return: JsonResult
     */
    @Override
    @ApiMethod
    public ServerResponse queryBrandSeries(HttpServletRequest request, String brandId,String cityId) {
        return productService.queryBrandSeries(brandId,cityId);
    }

    /**
     * 新增商品
     */
    @Override
    @ApiMethod
    public ServerResponse saveGoods(HttpServletRequest request, String name, String categoryId, Integer buy,
                                    Integer sales, String unitId, Integer type, String arrString, String otherName,String cityId) {
        return goodsService.saveGoods(name, categoryId, buy, sales, unitId, type, arrString, otherName,cityId);

    }

    /**
     * 根据商品id查询关联品牌
     */
    /*@Override
    @ApiMethod
    public ServerResponse queryBrandByGid(HttpServletRequest request, String goodsId) {
        return goodsService.queryBrandByGid(goodsId);

    }*/

    /**
     * 根据商品id和品牌id查询关联品牌系列
     */
   /* @Override
    @ApiMethod
    public ServerResponse queryBrandByGidAndBid(HttpServletRequest request, String goodsId, String brandId) {
        return goodsService.queryBrandByGidAndBid(goodsId, brandId);

    }*/

    /**
     * 新增货品
     */
    @Override
    @ApiMethod
    public ServerResponse insertProduct(HttpServletRequest request, String productArr,String cityId) {
        return productService.insertProduct(productArr,cityId);

    }

    /**
     * 根据商品id查询对应商品
     */
    /*@Override
    @ApiMethod
    public ServerResponse getGoodsByGid( String cityId, String goodsId) {
        return goodsService.getGoodsByGid(goodsId);

    }*/

    /**
     * 修改商品
     */
    @Override
    @ApiMethod
    public ServerResponse updateGoods(HttpServletRequest request, String id, String name, String categoryId, Integer buy,
                                      Integer sales, String unitId, Integer type, String arrString, String otherName,String cityId) {
        return goodsService.updateGoods(id, name, categoryId, buy, sales, unitId, type, arrString, otherName,cityId);

    }

    /**
     * 根据货品id查询货品对象
     *
     * @param id
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getProductById(String cityId, String id) {
        return productService.getProductById(id);
    }

    /**
     * 根据货品id删除货品对象
     *
     * @param id
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse deleteProductById(HttpServletRequest request, String id) {
        return productService.deleteProductById(id);
    }

    /**
     * 根据id删除商品和下属货品
     *
     * @param id
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse deleteGoods(HttpServletRequest request, String id) {
        return goodsService.deleteGoods(id);
    }

    @Override
    @ApiMethod
    public ServerResponse updateProductById(HttpServletRequest request, String id, String name) {
        return productService.updateProductById(id, name);
    }

    @Override
    @ApiMethod
    public ServerResponse updateProduct(HttpServletRequest request,Product product){
        String cityId = request.getParameter(Constants.CITY_ID);
        return productService.updateProduct(cityId,product);
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
    public ServerResponse queryGoodsListByCategoryLikeName(HttpServletRequest request, PageDTO pageDTO, String categoryId, String name, String cityId, Integer type) {
        return goodsService.queryGoodsListByCategoryLikeName(pageDTO, categoryId, name, cityId,type);
    }

    /**
     * 批量添加/修改货品标签
     *
     * @param request
     * @param productLabelList
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse updateProductLabelList(HttpServletRequest request, String productLabelList,String cityId) {
        return productService.updateProductLabelList(productLabelList,cityId);
    }

    /*
     * 查找所有货品列表
     */
    @Override
    @ApiMethod
    public ServerResponse queryProductListByGoodsIdAndLabelId(HttpServletRequest request, String goodsArr, String labelId,String cityId) {
        return goodsService.queryProductListByGoodsIdAndLabelId(goodsArr, labelId,cityId);
    }

    @Override
    public PageInfo queryProductData(String  cityId, Integer pageNum,Integer pageSize, String name, String categoryId, String productType, String[] productId) {
        PageInfo productList = productService.queryProductData(pageNum,pageSize, name, categoryId, productType, productId);
        return productList;
    }

}
