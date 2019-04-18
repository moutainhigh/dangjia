package com.dangjia.acg.api.web.house;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.house.HouseChoiceCase;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * author: qiyuxiang
 * Date: 2018/11/07
 * Time: 16:16
 */
@FeignClient("dangjia-service-master")
@Api(value = "房屋精选案例接口", description = "房屋精选案例接口")
public interface HouseChoiceCaseAPI {
    /**
     * 获取所有房屋精选案例
     *
     * @param houseChoiceCase
     * @return
     */
    @PostMapping("/config/choice/list")
    @ApiOperation(value = "获取所有房屋精选案例", notes = "获取所有房屋精选案例")
    ServerResponse getHouseChoiceCases(@RequestParam("request") HttpServletRequest request,
                                       @RequestParam("from") Integer from,
                                       @RequestParam("houseChoiceCase") PageDTO pageDTO,
                                       @RequestParam("houseChoiceCase") HouseChoiceCase houseChoiceCase);

    /**
     * 删除房屋精选案例
     *
     * @param id
     * @return
     */
    @PostMapping("/config/choice/del")
    @ApiOperation(value = "删除房屋精选案例", notes = "删除房屋精选案例")
    ServerResponse delHouseChoiceCase(@RequestParam("request") HttpServletRequest request,
                                      @RequestParam("id") String id);

    /**
     * 修改房屋精选案例
     *
     * @param houseChoiceCase
     * @return
     */
    @PostMapping("/config/choice/edit")
    @ApiOperation(value = "修改房屋精选案例", notes = "修改房屋精选案例")
    ServerResponse editHouseChoiceCase(@RequestParam("request") HttpServletRequest request,
                                       @RequestParam("houseChoiceCase") HouseChoiceCase houseChoiceCase);

    /**
     * 新增房屋精选案例
     *
     * @param houseChoiceCase
     * @return
     */
    @PostMapping("/config/choice/add")
    @ApiOperation(value = "新增房屋精选案例", notes = "新增房屋精选案例")
    ServerResponse addHouseChoiceCase(@RequestParam("request") HttpServletRequest request,
                                      @RequestParam("houseChoiceCase") HouseChoiceCase houseChoiceCase);

}
