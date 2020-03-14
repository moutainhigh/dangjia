package com.dangjia.acg.api.repair;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * author: Ronalcheng
 * Date: 2018/12/7 0007
 * Time: 17:06
 */
@Api(description = "补人工管理")
@FeignClient("dangjia-service-goods")
public interface MendWorkerAPI {

    @PostMapping("/repair/mendWorker/repairBudgetWorker")
    @ApiOperation(value = "补人工查询", notes = "补人工查询")
    ServerResponse repairBudgetWorker(@RequestParam("request") HttpServletRequest request,
                                      @RequestParam("type") Integer type,
                                      @RequestParam("workerTypeId") String workerTypeId,
                                      @RequestParam("houseId") String houseId,
                                      @RequestParam("pageDTO") PageDTO pageDTO,
                                      @RequestParam("cityId") String cityId);

    /**
     *  查询符合条件的人工商品大类
     * @param request
     * @param workerId
     * @param cityId
     * @return
     */
   /* @PostMapping("/repair/mendWorker/getWorkerProductList")
    @ApiOperation(value = "查询当前工匠认证的所有人工商品大类", notes = "查询当前工匠认证的所有人工商品")
    ServerResponse getWorkerProductCategoryList(@RequestParam("request") HttpServletRequest request,
                                        @RequestParam("workerId") String workerId,
                                        @RequestParam("cityId") String cityId);
*/
    /**
     * 查询符合条件的人工商品
     * @param request
     * @param userToken
     * @param searchKey
     * @param pageDTO
     * @param cityId
     * @return
     */
    @PostMapping("/repair/mendWorker/getWorkerProductList")
    @ApiOperation(value = "查询当前工匠认证的所有人工商品", notes = "查询当前工匠认证的所有人工商品")
    ServerResponse getWorkerProductList(@RequestParam("request") HttpServletRequest request,
                                      @RequestParam("userToken") String userToken,
                                      @RequestParam("houseId") String houseId,
                                      @RequestParam("searchKey") String searchKey,
                                      @RequestParam("pageDTO") PageDTO pageDTO,
                                      @RequestParam("cityId") String cityId);

}
