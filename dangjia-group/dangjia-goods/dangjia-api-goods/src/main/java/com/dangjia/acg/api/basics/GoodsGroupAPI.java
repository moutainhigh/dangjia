package com.dangjia.acg.api.basics;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.basics.GoodsGroup;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * 商品关联组
 *
 * @author Ronalcheng
 */
@Api(description = "商品关联组管理接口")
@FeignClient("dangjia-service-goods")
public interface GoodsGroupAPI {

    /*
     * 获取所有关联组
     */
    @PostMapping("/basics/goodsGroup/getAllList")
    @ApiOperation(value = "获取所有关联组", notes = "获取所有关联组")
    ServerResponse<PageInfo> getAllList(@RequestParam("request") HttpServletRequest request,
                                        @RequestParam("pageDTO") PageDTO pageDTO,
                                        @RequestParam("name") String name,
                                        @RequestParam("state") Integer state);

    /*
     * 添加关联组
     */
    @PostMapping("/basics/goodsGroup/addGoodsGroup")
    @ApiOperation(value = "获取所有关联组", notes = "获取所有关联组")
    ServerResponse addGoodsGroup(@RequestParam("request") HttpServletRequest request,
                                 @RequestParam("jsonStr") String jsonStr);

    /*
     * 修改关联组
     */
    @PostMapping("/basics/goodsGroup/updateGoodsGroup")
    @ApiOperation(value = "修改关联组", notes = "修改关联组")
    ServerResponse updateGoodsGroup(@RequestParam("request") HttpServletRequest request,
                                    @RequestParam("goodsGroup") GoodsGroup goodsGroup);

    /*
     * 查找所有顶级分类列表
     */
    @PostMapping("/basics/goodsGroup/getGoodsCategoryList")
    @ApiOperation(value = "查找所有顶级分类列表", notes = "查找所有顶级分类列表")
    ServerResponse getGoodsCategoryList(@RequestParam("request") HttpServletRequest request);

    /*
     * 查找所有子分类列表
     */
    @PostMapping("/basics/goodsGroup/getChildrenGoodsCategoryList")
    @ApiOperation(value = "查找所有子分类列表", notes = "查找所有子分类列表")
    ServerResponse getChildrenGoodsCategoryList(@RequestParam("request") HttpServletRequest request,
                                                @RequestParam("id") String id);

    /*
     * 查找所有商品列表
     */
    @PostMapping("/basics/goodsGroup/getGoodsListByCategoryId")
    @ApiOperation(value = "查找所有子分类列表", notes = "查找所有子分类列表")
    ServerResponse getGoodsListByCategoryId(@RequestParam("request") HttpServletRequest request,
                                            @RequestParam("id") String id);

    /*
     * 查找所有货品列表
     */
    @PostMapping("/basics/goodsGroup/getProductListByGoodsId")
    @ApiOperation(value = "查找所有货品列表", notes = "查找所有货品列表")
    ServerResponse getProductListByGoodsId(@RequestParam("request") HttpServletRequest request,
                                           @RequestParam("id") String id);

    /*
     * 添加关联组和货品关联关系
     */
    @PostMapping("/basics/goodsGroup/addGroupLink")
    @ApiOperation(value = "添加货品关联组关系", notes = "添加货品关联组关系")
    ServerResponse addGroupLink(@RequestParam("request") HttpServletRequest request,
                                @RequestParam("goodsGroupId") String goodsGroupId,
                                @RequestParam("listOfProductId") String listOfProductId);

    /*
     * 根据关联组id查询货品关联关系
     */
    @PostMapping("/basics/goodsGroup/getGoodsGroupById")
    @ApiOperation(value = "根据关联组id查询货品关联关系", notes = "根据关联组id查询货品关联关系")
    ServerResponse getGoodsGroupById(@RequestParam("request") HttpServletRequest request,
                                     @RequestParam("goodsGroupId") String goodsGroupId);

    /*
     * 根据关联组id删除关联组和货品关联关系
     */
    @PostMapping("/basics/goodsGroup/deleteGoodsGroupById")
    @ApiOperation(value = "根据关联组id删除关联组和货品关联关系", notes = "根据关联组id删除关联组和货品关联关系")
    ServerResponse deleteGoodsGroupById(@RequestParam("request") HttpServletRequest request,
                                        @RequestParam("goodsGroupId") String goodsGroupId);

    @PostMapping("/basics/goodsGroup/queryGoodsGroupListByCategoryLikeName")
    @ApiOperation(value = "按照name模糊查询商品及下属货品", notes = "按照name模糊查询商品及下属货品")
    ServerResponse queryGoodsGroupListByCategoryLikeName(@RequestParam("request") HttpServletRequest request,
                                                         @RequestParam("pageDTO") PageDTO pageDTO,
                                                         @RequestParam("categoryId") String categoryId,
                                                         @RequestParam("name") String name);

}
