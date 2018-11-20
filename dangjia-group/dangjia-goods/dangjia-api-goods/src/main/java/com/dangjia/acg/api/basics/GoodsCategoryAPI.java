package com.dangjia.acg.api.basics;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
   * @类 名： GoodsCategoryController
   * @功能描述： 商品/服务类别管理
   * @作者信息： zmj
   * @创建时间： 2018-9-12下午1:55:15
 */
@Api(description = "服务类别管理接口")
@FeignClient("dangjia-service-goods")
public interface GoodsCategoryAPI {

    /**新增商品类别
     * @Title: getProduct 
     */
    @PostMapping("/basics/goodsCategory/insertGoodsCategory")
    @ApiOperation(value = "新增商品类别", notes = "新增商品类别")
    public ServerResponse insertGoodsCategory(String name, String parentID, String parentTop);
    /**修改商品类别
     * @Title: getProduct 
     */
    @PostMapping("/basics/goodsCategory/doModifyGoodsCategory")
    @ApiOperation(value = "修改商品类别", notes = "修改商品类别")
    public ServerResponse doModifyGoodsCategory(String id,String name,String parentID,String parentTop);
    /**查询商品类别列表
     * @Title: getProduct 
     */
    @PostMapping("/basics/goodsCategory/queryGoodsCategory")
    @ApiOperation(value = "查询商品类别列表", notes = "查询商品类别列表")
    public ServerResponse  queryGoodsCategory( String parentId );
    
    /**根据类别id查询关联属性
     * @Title: getProduct 
     */
    @PostMapping("/basics/goodsCategory/queryGoodsAttribute")
    @ApiOperation(value = "根据类别id查询关联属性", notes = "根据类别id查询关联属性")
    public ServerResponse<PageInfo> queryGoodsAttribute(@RequestParam("pageDTO") PageDTO pageDTO,@RequestParam("goodsCategoryId")String goodsCategoryId);
    
    /**根据属性名称模糊查询属性
     * @Title: getProduct 
     */
    @PostMapping("/basics/goodsCategory/queryGoodsAttributelikeName")
    @ApiOperation(value = "根据属性名称模糊查询属性", notes = "根据属性名称模糊查询属性")
    public ServerResponse<PageInfo> queryGoodsAttributelikeName(@RequestParam("pageDTO") PageDTO pageDTO,@RequestParam("name")String name);
    
    /**根据属性id查询属性及其下属属性选项
     * @Title: getProduct 
     */
    @PostMapping("/basics/goodsCategory/queryAttributeValue")
    @ApiOperation(value = "根据属性id查询属性及其下属属性选项", notes = "根据属性id查询属性及其下属属性选项")
    public ServerResponse queryAttributeValue(String goodsAttributeId);
    
    /**新增属性及其属性选项
     * @Title: getProduct 
     */
    @PostMapping("/basics/goodsCategory/insertGoodsAttribute")
    @ApiOperation(value = "新增属性及其属性选项", notes = "新增属性及其属性选项")
    public ServerResponse insertGoodsAttribute(String goodsCategoryId,String attributeName,Integer type,String jsonStr);
    
    /**修改属性及其属性选项
     * @Title: getProduct 
     */
    @PostMapping("/basics/goodsCategory/doModifyGoodsAttribute")
    @ApiOperation(value = "修改属性及其属性选项", notes = "修改属性及其属性选项")
    public ServerResponse doModifyGoodsAttribute(String attributeId,String attributeName,Integer type,String jsonStr);
    
    /**
     * 删除商品属性
     */
    @PostMapping("/basics/goodsCategory/deleteGoodsAttribute")
    @ApiOperation(value = "删除商品属性", notes = "删除商品属性")
    public ServerResponse deleteGoodsAttribute(String goodsAttributeId);
    
    /**
     * 删除商品属性选项
     */
    @PostMapping("/basics/goodsCategory/deleteByAttributeId")
    @ApiOperation(value = "删除商品属性选项", notes = "删除商品属性选项")
    public ServerResponse deleteByAttributeId(String attributeValueId);

    /**
     * 删除商品类别
     */
    @PostMapping("/basics/goodsCategory/deleteGoodsCategory")
    @ApiOperation(value = "删除商品类别", notes = "删除商品类别")
    public ServerResponse deleteGoodsCategory(String id);

    /**
     * 查询类别id查询所有父级以及父级属性
     */
    @PostMapping("/basics/goodsCategory/queryAttributeListById")
    @ApiOperation(value = "查询类别id查询所有父级以及父级属性", notes = "查询类别id查询所有父级以及父级属性")
    public ServerResponse queryAttributeListById(@RequestParam("goodsCategoryId")String goodsCategoryId);
}
