package com.dangjia.acg.api.product.app;

import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @类 名： AppCategoryGoodsAPI
 * @功能描述： APP商品分类
 * @作者信息： QYX
 * @创建时间： 2019-9-16
 */
@Api(description = "商品3.0服务类别管理接口")
@FeignClient("dangjia-service-goods")
public interface AppCategoryGoodsAPI {


    @PostMapping("/app/category/top/label")
    @ApiOperation(value = "第一部分：顶部标签集合", notes = "第一部分：顶部标签集合")
    ServerResponse queryTopCategoryLabel(@RequestParam("cityId") String cityId);

    @PostMapping("/app/category/left/data")
    @ApiOperation(value = "第二部分：左侧分类集合", notes = "第二部分：左侧分类集合")
    ServerResponse queryLeftCategoryByDatas(@RequestParam("cityId") String cityId,@RequestParam("categoryLabelId") String categoryLabelId);

    @PostMapping("/app/category/right/data")
    @ApiOperation(value = " 第三部分：右侧侧分类集合", notes = " 第三部分：右侧侧分类集合")
    ServerResponse queryRightCategoryByDatas(@RequestParam("cityId") String cityId,@RequestParam("parentId") String parentId);


    @PostMapping("/app/category/goods/data")
    @ApiOperation(value = " 第四部分：二级商品列表搜索页面", notes = " 第四部分：二级商品列表搜索页面")
    ServerResponse serchCategoryProduct(@RequestParam("pageDTO") PageDTO pageDTO,
                                        @RequestParam("cityId") String cityId,
                                        @RequestParam("categoryId") String categoryId,
                                        @RequestParam("name") String name,
                                        @RequestParam("attributeVal") String attributeVal,
                                        @RequestParam("brandVal") String brandVal,
                                        @RequestParam("orderKey") String orderKey);

    @PostMapping("/app/category/brand/data")
    @ApiOperation(value = " 第四部分：二级商品品牌筛选数据", notes = " 第四部分：二级商品品牌筛选数据")
    ServerResponse queryBrandDatas(@RequestParam("cityId") String cityId,@RequestParam("categoryId") String categoryId,@RequestParam("wordKey") String wordKey);

    @PostMapping("/app/category/attribute/data")
    @ApiOperation(value = " 第四部分：二级商品规格筛选数据", notes = " 第四部分：二级商品规格筛选数据")
    ServerResponse queryAttributeDatas(@RequestParam("cityId") String cityId,@RequestParam("categoryId") String categoryId,@RequestParam("wordKey") String wordKey);

}
