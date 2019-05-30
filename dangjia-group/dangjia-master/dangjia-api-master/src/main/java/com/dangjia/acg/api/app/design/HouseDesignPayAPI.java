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
@Api(value = "设计图修改支付接口", description = "设计图修改支付接口")
public interface HouseDesignPayAPI {


    @PostMapping("web/design/sendPictures")
    @ApiOperation(value = "发送设计图给业主", notes = "发送设计图给业主")
    ServerResponse sendPictures(@RequestParam("request") HttpServletRequest request,
                                @RequestParam("houseId") String houseId);

}
