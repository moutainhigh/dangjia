package com.dangjia.acg.controller.basics;

import com.dangjia.acg.api.basics.GoodsCategoryAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.basics.GoodsAttributeService;
import com.dangjia.acg.service.basics.GoodsCategoryService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
   * @类 名： GoodsCategoryController
   * @功能描述： 商品/服务类别管理
   * @作者信息： zmj
   * @创建时间： 2018-9-12下午1:55:15
 */
@RestController
public class GoodsCategoryController implements GoodsCategoryAPI {
    /**
     *service
     */
    @Autowired
    private GoodsCategoryService goodsCategoryService;
    @Autowired
    private GoodsAttributeService goodsAttributeService;

    /**新增商品类别
     * @Title: getProduct 
     */
    @Override
    @ApiMethod
    public ServerResponse insertGoodsCategory(String name, String parentID, String parentTop){
        return goodsCategoryService.insertGoodsCategory(name,parentID,parentTop);
    }
    /**修改商品类别
     * @Title: getProduct 
     */
    @Override
    @ApiMethod
    public ServerResponse doModifyGoodsCategory(String id,String name,String parentID,String parentTop){
        return goodsCategoryService.doModifyGoodsCategory(id,name,parentID,parentTop);
    }
    /**查询商品类别列表
     * @Title: getProduct 
     */
    @Override
    @ApiMethod
    public ServerResponse  queryGoodsCategory( String parentId ){
        return goodsCategoryService.queryGoodsCategory(parentId);
    }
    
    /**根据类别id查询关联属性
     * @Title: getProduct 
     */
    @Override
    @ApiMethod
    public ServerResponse<PageInfo>  queryGoodsAttribute(PageDTO pageDTO,String goodsCategoryId){
        return goodsAttributeService.queryGoodsAttribute(pageDTO.getPageNum(),pageDTO.getPageSize(),goodsCategoryId);
    }
    
    /**根据属性名称模糊查询属性
     * @Title: getProduct 
     */
    @Override
    @ApiMethod
    public ServerResponse<PageInfo>  queryGoodsAttributelikeName(PageDTO pageDTO,String name){
        return goodsAttributeService.queryGoodsAttributelikeName(pageDTO.getPageNum(),pageDTO.getPageSize(),name);
    }
    
    /**根据属性id查询属性及其下属属性选项
     * @Title: getProduct 
     */
    @Override
    @ApiMethod
    public ServerResponse queryAttributeValue(String goodsAttributeId){
        return goodsAttributeService.queryAttributeValue(goodsAttributeId);
    }
    
    /**新增属性及其属性选项
     * @Title: getProduct 
     */
    @Override
    @ApiMethod
    public ServerResponse insertGoodsAttribute(String goodsCategoryId,String attributeName,Integer type,String jsonStr){
        return goodsAttributeService.insertGoodsAttribute(goodsCategoryId,attributeName,type,jsonStr);
    }
    
    /**修改属性及其属性选项
     * @Title: getProduct 
     */
    @Override
    @ApiMethod
    public ServerResponse doModifyGoodsAttribute(String attributeId,String attributeName,Integer type,String jsonStr){
        return goodsAttributeService.doModifyGoodsAttribute(attributeId,attributeName,type,jsonStr);
    }
    
    /**
     * 删除商品属性
     */
    @Override
    @ApiMethod
    public ServerResponse deleteGoodsAttribute(String goodsAttributeId){
        return goodsAttributeService.deleteGoodsAttribute(goodsAttributeId);
    }
    
    /**
     * 删除商品属性选项
     */
    @Override
    @ApiMethod
    public ServerResponse deleteByAttributeId(String attributeValueId){
        return goodsAttributeService.deleteByAttributeId(attributeValueId);
    }

    /**删除商品类别
     * @Title: getProduct
     */
    @Override
    @ApiMethod
    public ServerResponse deleteGoodsCategory(String id){
        return goodsCategoryService.deleteGoodsCategory(id);
    }

    /**查询类别id查询所有父级以及父级属性
     * @Title: getProduct
     */
    @Override
    @ApiMethod
    public ServerResponse queryAttributeListById(String goodsCategoryId){
        return goodsCategoryService.queryAttributeListById(goodsCategoryId);
    }
}
