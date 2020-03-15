package com.dangjia.acg.api;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.recommend.LatticeCoding;
import com.dangjia.acg.modle.recommend.LatticeContentType;
import com.dangjia.acg.modle.recommend.LatticeStyle;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Api(description = "方格操作接口")
@FeignClient("dangjia-service-recommend")
public interface LatticeOperationAPI {

    @PostMapping("/lattice/style/list")
    @ApiOperation(value = "查询方格样式列表", notes = "")
    ServerResponse queryLatticeStyleList();

    @PostMapping("/lattice/content/type/list")
    @ApiOperation(value = "查询方格内容类型列表", notes = "")
    ServerResponse queryLatticeContentTypeList();

    @PostMapping("/lattice/content/list")
    @ApiOperation(value = "查询方格内容列表", notes = "")
    ServerResponse queryLatticeContentList();

    @PostMapping("/lattice/content/single")
    @ApiOperation(value = "查询方格内容单个", notes = "")
    ServerResponse queryLatticeContentSingle(@RequestParam("id")String id);

    @PostMapping("/lattice/content/save")
    @ApiOperation(value = "保存方格内容", notes = "")
    ServerResponse saveLatticeContent(@RequestParam("contentListJsonStr")String contentListJsonStr);

    @PostMapping("/lattice/coding/list")
    @ApiOperation(value = "查询方格编号列表", notes = "")
    ServerResponse queryLatticeCodingList();

    @PostMapping("/lattice/content/optional/list")
    @ApiOperation(value = "查询可选内容列表", notes = "")
    ServerResponse queryOptionalContentList(@RequestParam("Integer") String typeId,
                                            @RequestParam("targetName") String keyword,
                                            @RequestParam("pageDTO") PageDTO pageDTO);
}
