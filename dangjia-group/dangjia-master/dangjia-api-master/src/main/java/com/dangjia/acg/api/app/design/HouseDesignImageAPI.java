package com.dangjia.acg.api.app.design;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * author: Ronalcheng
 * Date: 2018/11/8 0008
 * Time: 11:17
 */
@FeignClient("dangjia-service-master")
@Api(value = "设计图接口", description = "设计图接口")
public interface HouseDesignImageAPI {

    @PostMapping("app/design/houseDesignImage/designImageList")
    @ApiOperation(value = "查看施工图", notes = "查看施工图")
    ServerResponse designImageList(@RequestParam("houseId") String houseId);

    @PostMapping("app/design/houseDesignImage/checkPass")
    @ApiOperation(value = "设计通过", notes = "设计通过")
    ServerResponse checkPass(@RequestParam("userToken") String userToken,
                             @RequestParam("houseId") String houseId,
                             @RequestParam("type") int type);

    @PostMapping("app/design/houseDesignImage/checkDesign")
    @ApiOperation(value = "设计详情", notes = "设计详情")
    ServerResponse checkDesign(@RequestParam("userToken") String userToken,
                               @RequestParam("houseId") String houseId);

    @PostMapping("app/design/houseDesignImage/upgradeDesign")
    @ApiOperation(value = "升级设计", notes = "升级设计")
    ServerResponse upgradeDesign(@RequestParam("userToken") String userToken,
                                 @RequestParam("houseId") String houseId,
                                 @RequestParam("designImageTypeId") String designImageTypeId,
                                 @RequestParam("selected") int selected);


    @PostMapping("app/design/setQuantityRoom")
    @ApiOperation(value = "添加量房", notes = "添加量房")
    ServerResponse setQuantityRoom(@RequestParam("request") HttpServletRequest request,
                                   @RequestParam("userToken") String userToken,
                                   @RequestParam("houseId") String houseId,
                                   @RequestParam("userId") String userId,
                                   @RequestParam("images") String images);

    @PostMapping("app/design/getQuantityRoom")
    @ApiOperation(value = "查询量房", notes = "查询量房")
    ServerResponse getQuantityRoom(@RequestParam("request") HttpServletRequest request,
                                   @RequestParam("houseId") String houseId);
}
