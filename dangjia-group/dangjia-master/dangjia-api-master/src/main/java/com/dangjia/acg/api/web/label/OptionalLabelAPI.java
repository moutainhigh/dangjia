package com.dangjia.acg.api.web.label;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.label.OptionalLabel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/6/17
 * Time: 10:56
 */
@FeignClient("dangjia-service-master")
@Api(value = "选配标签接口", description = "选配标签接口")
public interface OptionalLabelAPI {

    @PostMapping("/web/label/addOptionalLabel")
    @ApiOperation(value = "添加标签", notes = "添加标签")
    ServerResponse addOptionalLabel(@RequestParam("optionalLabel") OptionalLabel optionalLabel);

    @PostMapping("/web/label/queryOptionalLabel")
    @ApiOperation(value = "查询标签", notes = "查询标签")
    ServerResponse queryOptionalLabel(@RequestParam("id") String id);

    @PostMapping("/web/label/delOptionalLabel")
    @ApiOperation(value = "删除标签", notes = "删除标签")
    ServerResponse delOptionalLabel(@RequestParam("id") String id);

    @PostMapping("/web/label/editOptionalLabel")
    @ApiOperation(value = "编辑标签", notes = "编辑标签")
    ServerResponse editOptionalLabel(@RequestParam("optionalLabel") OptionalLabel optionalLabel);
}
