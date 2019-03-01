package com.dangjia.acg.api.app.worker;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * author: Ronalcheng
 * Date: 2018/11/28 0028
 * Time: 14:18
 */
@FeignClient("dangjia-service-master")
@Api(value = "管家功能", description = "管家功能")
public interface StewardAPI {


    @PostMapping("app/worker/steward/scanCode")
    @ApiOperation(value = "管家巡查扫验证二维码", notes = "管家巡查扫验证二维码")
    ServerResponse scanCode(@RequestParam("userToken") String userToken,
                            @RequestParam("code") String code,
                            @RequestParam("latitude") String latitude,
                            @RequestParam("longitude") String longitude);

    @PostMapping("app/worker/steward/workerQrcode")
    @ApiOperation(value = "工匠生成二维码内容", notes = "工匠生成二维码内容")
    ServerResponse workerQrcode(@RequestParam("userToken") String userToken,
                                @RequestParam("houseFlowId") String houseFlowId,
                                @RequestParam("latitude") String latitude,
                                @RequestParam("longitude") String longitude);

    @PostMapping("app/worker/steward/passShutWork")
    @ApiOperation(value = "管家审核停工申请", notes = "管家审核停工申请")
    ServerResponse passShutWork(@RequestParam("userToken") String userToken,
                                @RequestParam("houseFlowApplyId") String houseFlowApplyId,
                                @RequestParam("content") String content,
                                @RequestParam("state") int state);

    @PostMapping("app/worker/steward/readProjectInfo")
    @ApiOperation(value = "成功返回交底内容", notes = "成功返回交底内容")
    ServerResponse readProjectInfo(@RequestParam("houseFlowId") String houseFlowId);


    @PostMapping("app/worker/steward/confirmProjectInfo")
    @ApiOperation(value = "提交完成交底", notes = "提交完成交底")
    ServerResponse confirmProjectInfo(@RequestParam("houseFlowId") String houseFlowId);


    @PostMapping("app/worker/steward/tellCode")
    @ApiOperation(value = "交底工匠扫二维码调用", notes = "交底工匠扫二维码调用")
    ServerResponse tellCode(@RequestParam("userToken") String userToken,
                            @RequestParam("code") String code);

    @PostMapping("app/worker/steward/stewardQrcode")
    @ApiOperation(value = "管家交底生成二维码", notes = "管家交底生成二维码")
    ServerResponse stewardQrcode(@RequestParam("houseFlowId") String houseFlowId,
                                 @RequestParam("disclosureIds") String disclosureIds);

    @PostMapping("app/worker/steward/getCourse")
    @ApiOperation(value = "管家查看工序进程", notes = "管家查看工序进程")
    ServerResponse getCourse(@RequestParam("houseFlowId") String houseFlowId);
}
