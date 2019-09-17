package com.dangjia.acg.api.product.app;

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
    ServerResponse queryRightCategoryByDatas(@RequestParam("cityId") String cityId,@RequestParam("parentId") String parentId);


}
