package com.dangjia.acg.api.web.clue;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("dangjia-service-master")
@Api(value = "线索沟通功能", description = "线索沟通功能")
public interface WebClueTalkAPI {
    /**
     * 通过线索ID获取沟通记录
     */
    @PostMapping("web/clue/clueTalk/getAllTalk")
    @ApiOperation(value = "通过线索ID获取沟通记录", notes = "通过线索ID获取沟通记录")
    ServerResponse getTalkByClueId(@RequestParam("clueID") String clueID, @RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize);
    /**
     * 添加沟通内容
     */
    @PostMapping("web/clue/clueTalk/addTalk")
    @ApiOperation(value = "添加沟通内容", notes = "添加沟通内容")
    ServerResponse addTalk(@RequestParam("clueId") String clueId,@RequestParam("talkContent") String talkContent);
}
