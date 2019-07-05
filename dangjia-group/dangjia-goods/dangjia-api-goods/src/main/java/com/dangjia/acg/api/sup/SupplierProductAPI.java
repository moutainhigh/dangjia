package com.dangjia.acg.api.sup;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.sup.Supplier;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName: SupplierProductController
 * @Description:
 * @author: zmj
 * @date: 2018-9-18下午3:19:58
 */
@Api(description = "供应商管理接口")
@FeignClient("dangjia-service-goods")
public interface SupplierProductAPI {


    @PostMapping("/sup/supplierProduct/info")
    @ApiOperation(value = "供应商明细", notes = "供应商明细")
    Supplier getSupplier(@RequestParam("productId") String productId);

    @PostMapping("/sup/supplierProduct/byTelephone")
    @ApiOperation(value = "供应商登录", notes = "供应商登录")
    ServerResponse byTelephone(@RequestParam("telephone") String telephone);

    @PostMapping("/sup/supplierProduct/supplierList")
    @ApiOperation(value = "查询供应商", notes = "查询供应商")
    ServerResponse supplierList(@RequestParam("request") HttpServletRequest request,
                                @RequestParam("productId") String productId);

    @PostMapping("/sup/supplierProduct/insertSupplier")
    @ApiOperation(value = "新增供应商", notes = "新增供应商")
    ServerResponse insertSupplier(@RequestParam("request") HttpServletRequest request,
                                  @RequestParam("name") String name,
                                  @RequestParam("address") String address,
                                  @RequestParam("telephone") String telephone,
                                  @RequestParam("checkPeople") String checkPeople,
                                  @RequestParam("gender") Integer gender,
                                  @RequestParam("email") String email,
                                  @RequestParam("notice") String notice,
                                  @RequestParam("supplierLevel") Integer supplierLevel,
                                  @RequestParam("state") Integer state);

    @PostMapping("/sup/supplierProduct/updateSupplier")
    @ApiOperation(value = "新增供应商", notes = "新增供应商")
    ServerResponse updateSupplier(@RequestParam("request") HttpServletRequest request,
                                  @RequestParam("id") String id,
                                  @RequestParam("name") String name,
                                  @RequestParam("address") String address,
                                  @RequestParam("telephone") String telephone,
                                  @RequestParam("checkPeople") String checkPeople,
                                  @RequestParam("gender") Integer gender,
                                  @RequestParam("email") String email,
                                  @RequestParam("notice") String notice,
                                  @RequestParam("supplierLevel") Integer supplierLevel,
                                  @RequestParam("state") Integer state);

    @PostMapping("/sup/supplierProduct/querySupplierListLikeByName")
    @ApiOperation(value = "按照名字模糊查询所有供应商", notes = "按照名字模糊查询所有供应商")
    ServerResponse<PageInfo> querySupplierListLikeByName(@RequestParam("request") HttpServletRequest request,
                                                         @RequestParam("pageDTO") PageDTO pageDTO,
                                                         @RequestParam("name") String name);

    @PostMapping("/sup/supplierProduct/querySupplierProduct")
    @ApiOperation(value = "查询所有货品供应关系", notes = "查询所有货品供应关系")
    ServerResponse querySupplierProduct(@RequestParam("request") HttpServletRequest request,
                                        @RequestParam("type") int type,
                                        @RequestParam("supplierId") String supplierId,
                                        @RequestParam("likeProductName") String likeProductName,
                                        @RequestParam("pageDTO") PageDTO pageDTO);

    @PostMapping("/sup/supplierProduct/saveSupplierProduct")
    @ApiOperation(value = "保存供应商与货品供应关系", notes = "保存供应商与货品供应关系")
    ServerResponse saveSupplierProduct(@RequestParam("arrString") String arrString);

    @PostMapping("/sup/supplierProduct/querySupplierProductByPid")
    @ApiOperation(value = "根据货品查询相应供应商", notes = "根据货品查询相应供应商")
    ServerResponse querySupplierProductByPid(@RequestParam("pageDTO") PageDTO pageDTO,
                                             @RequestParam("productId") String productId);
}
