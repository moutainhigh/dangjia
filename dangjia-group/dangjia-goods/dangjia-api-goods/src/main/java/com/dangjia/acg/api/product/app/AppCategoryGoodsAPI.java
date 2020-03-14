package com.dangjia.acg.api.product.app;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
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
    ServerResponse queryRightCategoryByDatas(@RequestParam("cityId") String cityId,@RequestParam("parentId") String parentId,@RequestParam("categoryLabelId") String categoryLabelId);


    @PostMapping("/app/category/goods/data")
    @ApiOperation(value = " 第四部分：二级商品列表搜索页面", notes = " 第四部分：二级商品列表搜索页面")
    ServerResponse serchCategoryProduct(@RequestParam("pageDTO") PageDTO pageDTO,
                                        @RequestParam("cityId") String cityId,
                                        @RequestParam("categoryId") String categoryId,
                                        @RequestParam("goodsId") String goodsId,
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


    /**
     * 查询有维保商品的顶级分类
     * @param cityId
     * @return
     */
    @PostMapping("/app/category/queryMaintenanceRecordTopCategory")
    @ApiOperation(value = "第一部分：查询维保商品的顶级分类", notes = "第一部分：查询维保商品的顶级")
    ServerResponse queryMaintenanceRecordTopCategory(@RequestParam("cityId") String cityId,@RequestParam("workerTypeId") String workerTypeId);

    /**
     * 查询有当前顶级分类下的所有维保商品
     * @param cityId
     * @param searchKey 商品名称
     * @param workerTypeId 工种ID
     * @param topCategoryId 顶级分类ID
     * @return
     */
    @PostMapping("/app/category/queryMaintenanceRecordProduct")
    @ApiOperation(value = "第二部分：查询有当前顶级分类下的所有维保商品", notes = "第二部分：查询有当前顶级分类下的所有维保商品")
    ServerResponse queryMaintenanceRecordProduct(@RequestParam("pageDTO") PageDTO pageDTO,@RequestParam("cityId") String cityId
            ,@RequestParam("workerTypeId") String workerTypeId,@RequestParam("topCategoryId") String topCategoryId,
                                                 @RequestParam("searchKey") String searchKey);

}
