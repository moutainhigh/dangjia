package com.dangjia.acg.api.product;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/9/12
 * Time: 9:40
 */
@Api(description = "货品接口")
@FeignClient("dangjia-service-goods")
public interface DjBasicsGoodsAPI {

    @PostMapping("/product/djBasicsGoods/addLabels")
    @ApiOperation(value = "货品打标签", notes = "货品打标签")
    ServerResponse addLabels(@RequestParam("request") HttpServletRequest request,
                             @RequestParam("goodsId") String goodsId,
                             @RequestParam("labels") String labels);


}
