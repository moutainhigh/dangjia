package com.dangjia.acg.api.product;



import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.dangjia.acg.modle.basics.Product;
import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/5/7
 * Time: 15:28
 */
@FeignClient("dangjia-service-master")
@Api(value = "修改商品全局更新接口", description = "修改商品全局更新接口")
public interface MasterProductAPI {

    @PostMapping("/product/updateProductByProductId")
    @ApiOperation(value = "修改商品库内商品,全局更新", notes ="修改商品库内商品,全局更新")
    ServerResponse updateProductByProductId(@RequestParam("id") String id,
                                            @RequestParam("categoryId") String  categoryId,
                                            @RequestParam("brandSeriesId") String brandSeriesId,
                                            @RequestParam("brandId") String brandId,
                                            @RequestParam("name") String name,
                                            @RequestParam("unitId") String unitId,
                                            @RequestParam("unitName") String unitName);

}
