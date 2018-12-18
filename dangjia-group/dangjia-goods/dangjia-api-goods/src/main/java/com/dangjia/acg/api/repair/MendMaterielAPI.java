package com.dangjia.acg.api.repair;

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

    @PostMapping("/repair/mendMateriel/selectProduct")
    @ApiOperation(value = "选择货", notes = "选择货")
    ServerResponse selectProduct(@RequestParam("request") HttpServletRequest request, @RequestParam("goodsId")String goodsId, @RequestParam("brandSeriesId")String brandSeriesId
            , @RequestParam("attributeIdArr")String attributeIdArr);


    @PostMapping("/repair/mendMateriel/repairLibraryMaterial")
    @ApiOperation(value = "补货查询商品库商品", notes = "补货查询商品库商品")
    ServerResponse repairLibraryMaterial(@RequestParam("request") HttpServletRequest request, @RequestParam("categoryId")String categoryId,
                                        @RequestParam("name")String name, @RequestParam("pageNum")Integer pageNum,@RequestParam("pageSize") Integer pageSize);
}
