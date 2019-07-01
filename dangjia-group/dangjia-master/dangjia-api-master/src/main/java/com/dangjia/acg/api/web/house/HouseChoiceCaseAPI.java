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
     * showdoc
     *
     * @param pageNum  必选 int 页码
     * @param pageSize 必选 int 记录数
     * @param from     必选 string 0：App首页，1：中台，2：App更多精选案例
     * @param cityId   可选 string 城市ID
     * @return {"res": 1000,"msg": {"resultCode": 1000, "resultMsg": "ok", "resultObj": { "pageNum": 0,"pageSize": 10,"size": 1,"startRow": 1,"endRow": 1,"total": 1, "pages": 1,"list": [{返回参数说明}],"prePage": 0, "nextPage": 1,"isFirstPage": false,"isLastPage": false,"hasPreviousPage": false,"hasNextPage": true,"navigatePages": 8,"navigatepageNums": [1],"navigateFirstPage": 1,"navigateLastPage": 1}}}
     * @catalog 当家接口文档/精算案例模块
     * @title 获取所有房屋精选案例
     * @description 获取所有房屋精选案例
     * @method POST
     * @url master/config/choice/list
     * @return_param title string 标题（如房屋名称）
     * @return_param cityId string 城市id
     * @return_param label List<String> 标签（多个以逗号分隔）
     * @return_param image string 案例主图 (全路径)
     * @return_param imageUrl string 案例主图(半路径)
     * @return_param address string 跳转地址
     * @return_param source string 来源花费说明
     * @return_param money BigDecimal 金额
     * @return_param houseId string 房子ID
     * @return_param isShow int 展示方式 0: 展示 1：不展示 2: 定时展示
     * @return_param showTimeStart date-time 展示时间结束
     * @return_param showTimeEnd date-time 展示时间结束
     * @return_param textContent List 落地页数据
     * @return_param textContent_headline string 二级页面标题
     * @return_param textContent_image string[] 二级页面图片
     * @return_param textContent_imageUrl string[] 二级页面半路劲
     * @return_param textContent_describe string 二级页面图片描述
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 1
     * @Author: Ruking 18075121944
     * @Date: 2019/7/1 3:56 PM
     */
    @PostMapping("config/choice/list")
    @ApiOperation(value = "获取所有房屋精选案例", notes = "获取所有房屋精选案例")
    ServerResponse getHouseChoiceCases(@RequestParam("request") HttpServletRequest request,
                                       @RequestParam("pageDTO") PageDTO pageDTO,
                                       @RequestParam("from") Integer from,
                                       @RequestParam("cityId") String cityId);

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
