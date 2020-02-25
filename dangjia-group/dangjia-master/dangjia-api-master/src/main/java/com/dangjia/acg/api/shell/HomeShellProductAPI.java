package com.dangjia.acg.api.shell;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.shell.HomeShellProductDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * author: fzh
 * Date: 25/02/2020
 * Time: 下午 3:29
 */
@Api(description = "当家贝商品接口")
@FeignClient("dangjia-service-master")
public interface HomeShellProductAPI {


    /**
     * 当家贝商品列表
     * @param request
     * @param pageDTO 分页
     * @param productType 商品类型：1实物商品 2虚拟商品
     * @param searchKey 商品名称/编码
     * @return
     */
    @PostMapping("/web/homeShell/queryHomeShellProductList")
    @ApiOperation(value = "当家贝商品列表", notes = "当家贝商品列表")
    ServerResponse queryHomeShellProductList(@RequestParam("request") HttpServletRequest request,
                                    @RequestParam("pageDTO") PageDTO pageDTO,
                                    @RequestParam("productType") String productType,
                                    @RequestParam("searchKey") String searchKey);

    /**
     * 商品详情
     * @param request
     * @param shellProductId 当家贝商品ID
     * @return
     */
    @PostMapping("/web/homeShell/queryHomeShellProductInfo")
    @ApiOperation(value = "当家贝商品详情", notes = "当家贝商品详情")
    ServerResponse queryHomeShellProductInfo(@RequestParam("request") HttpServletRequest request,
                                             @RequestParam("shellProductId") String shellProductId);

    /**
     * 添加修改商品
     * @param request
     * @param homeShellProductDTO 商品内容
     * @return
     */
    @PostMapping("/web/homeShell/editHomeShellProductInfo")
    @ApiOperation(value = "当家贝商品详情", notes = "当家贝商品详情")
    ServerResponse editHomeShellProductInfo(@RequestParam("request") HttpServletRequest request,
                                             @RequestParam("homeShellProductDTO") HomeShellProductDTO homeShellProductDTO,
                                            @RequestParam("cityId") String cityId);

    /**
     * 删除商品(修改商品的状态为删除状态）
     * @param request
     * @param shellProductId 当家贝商品ID
     * @return
     */
    @PostMapping("/web/homeShell/deleteHomeShellProduct")
    @ApiOperation(value = "删除商品信息", notes = "删除商品信息")
    ServerResponse deleteHomeShellProduct(@RequestParam("request") HttpServletRequest request,
                                             @RequestParam("shellProductId") String shellProductId);

}
