package com.dangjia.acg.controller.basics;


import com.dangjia.acg.api.basics.ProductAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
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
    public ServerResponse<PageInfo> queryProduct(HttpServletRequest request, PageDTO pageDTO, String categoryId) {
        return productService.queryProduct(pageDTO, categoryId);
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
    public ServerResponse queryUnit(HttpServletRequest request) {
        return productService.queryUnit();
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
    public ServerResponse queryBrand(HttpServletRequest request) {
        return productService.queryBrand();
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
    public ServerResponse queryBrandSeries(HttpServletRequest request, String brandId) {
        return productService.queryBrandSeries(brandId);
    }

    /**
     * 新增商品
     */
    @Override
    @ApiMethod
    public ServerResponse saveGoods(HttpServletRequest request, String name, String categoryId, Integer buy,
                                    Integer sales, String unitId, Integer type, String arrString, String otherName) {
        return goodsService.saveGoods(name, categoryId, buy, sales, unitId, type, arrString, otherName);

    }

    /**
     * 根据商品id查询关联品牌
     */
    @Override
    @ApiMethod
    public ServerResponse queryBrandByGid(HttpServletRequest request, String goodsId) {
        return goodsService.queryBrandByGid(goodsId);

    }

    /**
     * 根据商品id和品牌id查询关联品牌系列
     */
    @Override
    @ApiMethod
    public ServerResponse queryBrandByGidAndBid(HttpServletRequest request, String goodsId, String brandId) {
        return goodsService.queryBrandByGidAndBid(goodsId, brandId);

    }

    /**
     * 新增货品
     */
    @Override
    @ApiMethod
    public ServerResponse insertProduct(HttpServletRequest request, String productArr) {
        return productService.insertProduct(productArr);

    }

    /**
     * 根据商品id查询对应商品
     */
    @Override
    @ApiMethod
    public ServerResponse getGoodsByGid(HttpServletRequest request, String goodsId) {
        return goodsService.getGoodsByGid(goodsId);

    }

    /**
     * 修改商品
     */
    @Override
    @ApiMethod
    public ServerResponse updateGoods(HttpServletRequest request, String id, String name, String categoryId, Integer buy,
                                      Integer sales, String unitId, Integer type, String arrString, String otherName) {
        return goodsService.updateGoods(id, name, categoryId, buy, sales, unitId, type, arrString, otherName);

    }

    /**
     * 根据货品id查询货品对象
     *
     * @param id
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getProductById(HttpServletRequest request, String id) {
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
        return goodsService.queryGoodsListByCategoryLikeName(pageDTO, categoryId, name, type);
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
    public ServerResponse updateProductLabelList(HttpServletRequest request, String productLabelList) {
        return productService.updateProductLabelList(productLabelList);
    }

    /*
     * 查找所有货品列表
     */
    @Override
    @ApiMethod
    public ServerResponse queryProductListByGoodsIdAndLabelId(HttpServletRequest request, String goodsArr, String labelId) {
        return goodsService.queryProductListByGoodsIdAndLabelId(goodsArr, labelId);
    }

    @Override
    public PageInfo queryProductData(HttpServletRequest request, PageDTO pageDTO, String name, String categoryId, String productType, String[] productId) {
        PageInfo productList = productService.queryProductData(pageDTO, name, categoryId, productType, productId);
        return productList;
    }
//
//	/**
//	 * 修改货品（全局更新）
//	 * @param request
//	 * @param id
//	 * @param categoryId
//	 * @param brandSeriesId
//	 * @param brandId
//	 * @param name
//	 * @param unitId
//	 * @param unitName
//	 * @return
//	 */
//	@Override
//	@ApiMethod
//	public ServerResponse updateProductByProductId(HttpServletRequest request, String id, String categoryId, String brandSeriesId,
//												   String brandId, String name, String unitId, String unitName) {
//		return productService.updateProductByProductId(id,categoryId,brandSeriesId,brandId,name,unitId,unitName);
//	}

    /**
     * 根据系列和属性查询切换货品
     * @param request
     * @param brandSeriesId
     * @param attributeIdArr
     * @return
     *//*
	@Override
	@ApiMethod
	public  ServerResponse getSwitchProduct(HttpServletRequest request,String brandSeriesId, String attributeIdArr){
		return productService.getSwitchProduct(brandSeriesId,attributeIdArr);
	}*/

}
