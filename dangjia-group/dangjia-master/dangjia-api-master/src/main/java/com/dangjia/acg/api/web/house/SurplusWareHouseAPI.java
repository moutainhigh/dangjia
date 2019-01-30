package com.dangjia.acg.api.web.house;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.deliver.SplitDeliver;
import com.dangjia.acg.modle.house.SurplusWareDivert;
import com.dangjia.acg.modle.house.SurplusWareHouse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * 剩余材料的临时仓库  (临时仓库 包括：供应商， 所有业主的房子， 公司仓库 )
 * author: ysl
 * Date: 2019/1/24 0018
 * Time: 10:52
 */
@FeignClient("dangjia-service-master")
@Api(value = "剩余材料的临时仓库", description = "剩余材料的临时仓库")
public interface SurplusWareHouseAPI {

    /**
     * 所有剩余材料的临时仓库
     */
    @PostMapping("web/surplus/wareHouse/getAllSurplusWareHouse")
    @ApiOperation(value = "所有剩余材料的临时仓库", notes = "所有所有剩余材料的临时仓库")
    ServerResponse getAllSurplusWareHouse(@RequestParam("request") HttpServletRequest request, @RequestParam("pageDTO") PageDTO pageDTO,
                                          @RequestParam("state") Integer state, @RequestParam("beginDate") String beginDate,
                                          @RequestParam("endDate") String endDate);

    /**
     * 修改、添加临时仓库的信息
     */
    @PostMapping("web/surplus/wareHouse/setSurplusWareHouse")
    @ApiOperation(value = "修改临时仓库的信息", notes = "修改临时仓库的信息")
    ServerResponse setSurplusWareHouse(@RequestParam("request") HttpServletRequest request, @RequestParam("withdrawDeposit") SurplusWareHouse surplusWareHouse);

    /**
     * 添加临时仓库清点数据
     * @param request
     * @param jsonStr
     * @return
     */
    @PostMapping("web/surplus/wareHouse/addSurplusWareHouseItem")
    @ApiOperation(value = "修改临时仓库的信息", notes = "修改临时仓库的信息")
    ServerResponse addSurplusWareHouseItem(@RequestParam("request") HttpServletRequest request,@RequestParam("jsonStr") String jsonStr);

    /**
     * 查看临时仓库清点的所有商品数据
     * @param request
     * @param surplusWareHouseId  库存id
     * @return
     */
    @PostMapping("web/surplus/wareHouse/getAllSurplusWareHouseItemBySId")
    @ApiOperation(value = "查看临时仓库清点数据", notes = "查看临时仓库清点数据")
    ServerResponse getAllSurplusWareHouseItemBySId(@RequestParam("request") HttpServletRequest request,  @RequestParam("pageDTO") PageDTO pageDTO,
                                                   @RequestParam("surplusWareHouseId") String surplusWareHouseId);

    /**
     * 添加挪货记录
     * @param request
     * @param jsonStr
     * @return
     */
    @PostMapping("web/surplus/wareHouse/addSurplusWareDivertList")
    @ApiOperation(value = "修改临时仓库的信息", notes = "修改临时仓库的信息")
    ServerResponse addSurplusWareDivertList(@RequestParam("request") HttpServletRequest request,@RequestParam("jsonStr") String jsonStr);

    /**
     * 查询指定仓库id的挪货记录
     * @param request
     * @param pageDTO
     * @param surplusWareHouseId
     * @return
     */
    @PostMapping("web/surplus/wareHouse/getAllSurplusWareDivertListBySId")
    @ApiOperation(value = "查询指定仓库id的挪货记录", notes = "查询指定仓库id的挪货记录")
    ServerResponse getAllSurplusWareDivertListBySId(@RequestParam("request") HttpServletRequest request, @RequestParam("pageDTO") PageDTO pageDTO,
                                           @RequestParam("surplusWareHouseId") String surplusWareHouseId);

}
