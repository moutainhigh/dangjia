package com.dangjia.acg.api.actuary;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.actuary.BudgetMaterial;
import com.dangjia.acg.modle.basics.Product;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @类 名： serverPortAPI
 * @功能描述：
 * @作者信息： zmj
 * @创建时间： 2018-11-15上午13:35:10
 */
@Api(description = "服务化接口")
@FeignClient("dangjia-service-goods")
public interface ServerPortAPI {

    //根据内容模糊搜索
    @PostMapping("/actuary/serverPort/getSearchBox")
    @ApiOperation(value = "根据内容模糊搜索", notes = "根据内容模糊搜索")
    ServerResponse getSearchBox(@RequestParam("request") HttpServletRequest request,
                                @RequestParam("pageDTO") PageDTO pageDTO,
                                @RequestParam("content") String content,
                                @RequestParam("cityId") String cityId,
                                @RequestParam("type") int type);

    //查询热门搜索
    @PostMapping("/actuary/serverPort/getHeatSearchBox")
    @ApiOperation(value = "查询热门搜索", notes = "查询热门搜索")
    ServerResponse getHeatSearchBox(@RequestParam("request") HttpServletRequest request);


    @PostMapping("serverPort/getBudgetMaterialList")
    List<BudgetMaterial> getBudgetMaterialList(@RequestParam("cityId") String cityId,
                                               @RequestParam("houseId") String houseId);

    @PostMapping("serverPort/getProduct")
    Product getProduct(@RequestParam("cityId") String cityId,
                       @RequestParam("productId") String productId);

    @PostMapping("serverPort/getAttributes")
    String getAttributes(@RequestParam("cityId") String cityId,
                         @RequestParam("productId") String productId);

    @PostMapping("serverPort/getInIdsBudgetMaterialList")
    List<BudgetMaterial> getInIdsBudgetMaterialList(@RequestParam("cityId") String cityId,
                                                    @RequestParam("ids") String[] ids);

    @PostMapping("serverPort/updateBudgetMaterial")
    void updateBudgetMaterial(@RequestParam("cityId") String cityId,
                              @RequestParam("budgetMaterial") BudgetMaterial budgetMaterial);

}