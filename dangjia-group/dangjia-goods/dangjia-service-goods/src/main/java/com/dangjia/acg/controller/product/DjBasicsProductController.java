package com.dangjia.acg.controller.product;

import com.dangjia.acg.api.product.DjBasicsProductAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.product.BasicsGoodsDTO;
import com.dangjia.acg.dto.product.BasicsProductDTO;
import com.dangjia.acg.modle.product.DjBasicsProduct;
import com.dangjia.acg.service.product.DjBasicsGoodsService;
import com.dangjia.acg.service.product.DjBasicsProductService;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


/**
 * 产品控制层
 * author: fzh
 * Date: 2019/9/15
 */
@RestController
public class DjBasicsProductController implements DjBasicsProductAPI {
    private static Logger logger = LoggerFactory.getLogger(DjBasicsProductService.class);

    @Autowired
    private DjBasicsProductService djBasicsProductService;

    @Autowired
    private DjBasicsGoodsService djBasicsGoodsService;

    @Override
    @ApiMethod
    public ServerResponse queryProductData(HttpServletRequest request, String name) {
        return djBasicsProductService.queryProductData(name);

    }

    @Override
    @ApiMethod
    public DjBasicsProduct queryProductDataByID(HttpServletRequest request, String id) {
        return djBasicsProductService.queryProductDataByID(request,id);
    }
    @Override
    @ApiMethod
    public ServerResponse queryProductLabels(HttpServletRequest request, String productId) {
        return djBasicsProductService.queryProductLabels(productId);
    }

    @Override
    @ApiMethod
    public ServerResponse addLabelsValue(HttpServletRequest request, String jsonStr) {
        return djBasicsProductService.addLabelsValue(jsonStr);
    }

    @Override
    @ApiMethod
    public ServerResponse queryDataByProductId(HttpServletRequest request, String productSn) {
        return djBasicsProductService.queryDataByProductId(request,productSn);
    }

    /**
     * 新增货品
     */
    @Override
    @ApiMethod
    public ServerResponse saveBasicsGoods(HttpServletRequest request, BasicsGoodsDTO basicsGoodsDTO) {
        return djBasicsGoodsService.saveBasicsGoods(basicsGoodsDTO);

    }

    /**
     * 修改货品
     * @param request
     * @param basicsGoodsDTO
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse updateBasicsGoods(HttpServletRequest request,BasicsGoodsDTO basicsGoodsDTO){
        return djBasicsGoodsService.updateBasicsGoods(basicsGoodsDTO);
    }
    /**
     * 新增商品
     */
    @Override
    @ApiMethod
    public ServerResponse insertBatchProduct(HttpServletRequest request, String productArr) {
        try{
            return djBasicsProductService.insertBatchProduct(productArr);
        }catch (Exception e){
            logger.error("新增商品信息失败：",e);
            return ServerResponse.createBySuccessMessage("新增失败");
        }

    }

    @Override
    @ApiMethod
    public ServerResponse queryGoodsListByCategoryLikeName(HttpServletRequest request, PageDTO pageDTO, String categoryId, String name, String cityId, Integer type,String categoryName) {
        return djBasicsProductService.queryGoodsListByCategoryLikeName(pageDTO, categoryId, name, type,categoryName);
    }
    /**
     * 单个新增修改商品信息
     * @param request
     * @param basicsProductDTO
     * @param technologyList  添加工艺列表
     * @param deleteTechnologyIds 删除工艺列表的D
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse editSingleProduct( HttpServletRequest request,BasicsProductDTO basicsProductDTO,String technologyList, String  deleteTechnologyIds){
        try{
            return djBasicsProductService.saveProductTemporaryStorage(basicsProductDTO, technologyList, deleteTechnologyIds,0);
        }catch (Exception e){
            logger.error("保存单个商品信息失败：",e);
            return ServerResponse.createBySuccessMessage("保存单个商品失败");
        }
    }

    /**
     * 暂存商品
     * @param request
     * @param basicsProductDTO
     * @param technologyList  添加工艺列表
     * @param deleteTechnologyIds 删除工艺列表的D
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse saveProductTemporaryStorage(HttpServletRequest request,
                                                      BasicsProductDTO basicsProductDTO, String technologyList, String  deleteTechnologyIds){
        try{
            return djBasicsProductService.saveProductTemporaryStorage(basicsProductDTO, technologyList, deleteTechnologyIds,2);
        }catch (Exception e){
            logger.error("保存商品信息失败：",e);
            return ServerResponse.createBySuccessMessage("保存商品失败");
        }
    }
    /**
     * 根据货品id删除商品对象
     *
     * @param id
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse deleteBasicsProductById(HttpServletRequest request, String id) {
        try{
            return djBasicsProductService.deleteBasicsProductById(id);
        }catch (Exception e){
            logger.error("删除商品失败："+id,e);
            return ServerResponse.createByErrorMessage("删除商品失败");
        }

    }

    /**
     * 根据id删除货品及其下面的商品
     *
     * @param id
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse deleteBasicsGoods(HttpServletRequest request, String id) {
        try{
            return djBasicsGoodsService.deleteBasicsGoods(id);
        }catch (Exception e){
            logger.error("删除货品失败："+id,e);
            return ServerResponse.createByErrorMessage("删除货品失败");
        }

    }
    /**
     * 根据货品ID查询对应的货品信息
     */
    @Override
    @ApiMethod
    public ServerResponse getBasicsGoodsByGid( String cityId, String goodsId) {
        return djBasicsGoodsService.getBasicsGoodsByGid(goodsId);

    }
    /**
     * 查询所有商品
     *
     * @throws
     * @Title: queryProduct
     * @param: @param category_id
     * @param: @return
     * @return: JsonResult
     */
    @Override
    @ApiMethod
    public ServerResponse<PageInfo> queryProduct(HttpServletRequest request, PageDTO pageDTO, String categoryId) {
        return djBasicsProductService.queryProduct(pageDTO, categoryId);
    }

    /**
     * 查询所有单位
     *
     * @throws
     * @Title: queryUnit
     * @param: @return
     * @return: JsonResult
     */
    @Override
    @ApiMethod
    public ServerResponse queryUnit(HttpServletRequest request) {
        return djBasicsProductService.queryUnit();
    }
    /**
     * 根据商品ID查询商品对象
     *
     * @param id
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getProductById(String cityId, String id) {
        return djBasicsProductService.getProductById(id);
    }

    /**
     * 查询货品下暂存的商品信息
     * @param cityId
     * @param goodsId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getTemporaryStorageProductByGoodsId(String cityId, String goodsId) {
        return djBasicsProductService.getTemporaryStorageProductByGoodsId(goodsId);
    }

    /**
     * 根据类别Id查询所属货品
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getAllGoodsByCategoryId(HttpServletRequest request,String categoryId){
        return djBasicsProductService.getAllGoodsByCategoryId(categoryId);
    }
    /**
     * 根据货品ID查询商品
     * @param goodsId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse  getAllProductByGoodsId(HttpServletRequest request,String goodsId){
        return djBasicsProductService.getAllProductByGoodsId(goodsId);
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
    public ServerResponse queryGoodsList(HttpServletRequest request, PageDTO pageDTO, String categoryId, String name, String cityId, Integer type) {
        return djBasicsGoodsService.queryGoodsList(pageDTO, categoryId, name, type);
    }


    @Override
    @ApiMethod
    public ServerResponse randQueryProduct(HttpServletRequest request, String goodsId) {
        return djBasicsProductService.getAllProductByGoodsIdLimit12(goodsId);
    }
    @Override
    public PageInfo queryBasicsProductData(String  cityId, Integer pageNum,Integer pageSize, String name, String categoryId, String productType, String[] productId) {
        PageInfo productList = djBasicsProductService.queryBasicsProductData(pageNum,pageSize, name, categoryId, productType, productId);
        return productList;
    }
}
