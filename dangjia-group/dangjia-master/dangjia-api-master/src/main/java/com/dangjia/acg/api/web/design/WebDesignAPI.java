package com.dangjia.acg.api.web.design;

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
 * Date: 2018/11/10 0010
 * Time: 16:17
 */
@FeignClient("dangjia-service-master")
@Api(value = "后台设计端接口", description = "后台设计端接口")
public interface WebDesignAPI {

    @PostMapping("web/design/sendPictures")
    @ApiOperation(value = "发送设计图给业主", notes = "发送设计图给业主")
    ServerResponse sendPictures(@RequestParam("request") HttpServletRequest request,
                                @RequestParam("houseId") String houseId);

    @PostMapping("web/design/uploadPictures")
    @ApiOperation(value = "上传图片", notes = "上传图片")
    ServerResponse uploadPictures(@RequestParam("request") HttpServletRequest request,
                                  @RequestParam("houseId") String houseId,
                                  @RequestParam("designImageTypeId") String designImageTypeId,
                                  @RequestParam("imageurl") String imageurl);

    @PostMapping("web/design/getList")
    @ApiOperation(value = "设计师任务列表", notes = "设计师任务列表")
    ServerResponse getDesignList(@RequestParam("request") HttpServletRequest request,
                                 @RequestParam("pageDTO") PageDTO pageDTO,
                                 @RequestParam("designerType") int designerType,
                                 @RequestParam("searchKey") String searchKey);

    @PostMapping("web/design/getImagesList")
    @ApiOperation(value = "设计图列表", notes = "设计图列表")
    ServerResponse getImagesList(@RequestParam("request") HttpServletRequest request,
                                 @RequestParam("houseId") String houseId);
}
