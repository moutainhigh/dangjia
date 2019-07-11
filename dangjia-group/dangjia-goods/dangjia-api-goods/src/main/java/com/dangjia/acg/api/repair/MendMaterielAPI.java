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
 * Time: 10:32
 */
@Api(description = "补材料管理")
@FeignClient("dangjia-service-goods")
public interface MendMaterielAPI {

    @PostMapping("/repair/mendMateriel/surplusList")
    @ApiOperation(value = "材料列表", notes = "材料列表")
    ServerResponse surplusList(@RequestParam("cityId") String cityId,@RequestParam("workerTypeId")String workerTypeId,@RequestParam("houseId")String houseId);

    @PostMapping("/repair/mendMateriel/askAndQuit")
    @ApiOperation(value = "要退查询仓库", notes = "要退查询仓库")
    ServerResponse askAndQuit(@RequestParam("cityId") String cityId,@RequestParam("userToken") String userToken,
                                    @RequestParam("houseId") String houseId,
                                    @RequestParam("categoryId") String categoryId,
                                    @RequestParam("name") String name);

    @PostMapping("/repair/mendMateriel/selectProduct")
    @ApiOperation(value = "选择货", notes = "选择货")
    ServerResponse selectProduct(@RequestParam("cityId") String cityId,
                                 @RequestParam("goodsId") String goodsId,
                                 @RequestParam("selectVal") String selectVal,
                                 @RequestParam("attributeIdArr") String attributeIdArr);


    @PostMapping("/repair/mendMateriel/repairLibraryMaterial")
    @ApiOperation(value = "补货查询商品库商品", notes = "补货查询商品库商品")
    ServerResponse repairLibraryMaterial(@RequestParam("cityId") String cityId,@RequestParam("userToken") String userToken,
                                         @RequestParam("request") HttpServletRequest request,
                                         @RequestParam("categoryId") String categoryId,
                                         @RequestParam("name") String name,
                                         @RequestParam("pageDTO") PageDTO pageDTO);

    @PostMapping("/repair/mendMateriel/workerTypeBudget")
    @ApiOperation(value = "补退要查询工种精算商品", notes = "补退要查询工种精算商品")
    ServerResponse workerTypeBudget(@RequestParam("cityId") String cityId,@RequestParam("userToken") String userToken,
                                    @RequestParam("houseId") String houseId,
                                    @RequestParam("categoryId") String categoryId,
                                    @RequestParam("name") String name,
                                    @RequestParam("pageDTO") PageDTO pageDTO);
}
