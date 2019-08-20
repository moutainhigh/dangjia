package com.dangjia.acg.api.app.house;

import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.basics.Product;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;


@FeignClient("dangjia-service-master")
@Api(value = "材料仓库统计", description = "材料仓库统计")
public interface WarehouseAPI {



    @PostMapping("app/house/warehouse/checkWarehouseSurplus")
    @ApiOperation(value = "查询仓库剩余总金额", notes = "查询仓库剩余总金额")
    ServerResponse checkWarehouseSurplus(@RequestParam("userToken") String userToken,
                                         @RequestParam("houseId") String houseId);


    @PostMapping("app/house/warehouse/warehouseList")
    @ApiOperation(value = "我购买的材料", notes = "我购买的材料")
    ServerResponse warehouseList(@RequestParam("userToken") String userToken,
                                 @RequestParam("pageDTO") PageDTO pageDTO,
                                 @RequestParam("houseId") String houseId,
                                 @RequestParam("categoryId") String categoryId,
                                 @RequestParam("name") String name,
                                 @RequestParam("type") String type);

    @PostMapping("app/house/warehouse/warehouseGmList")
    @ApiOperation(value = "我购买的材料", notes = "我购买的材料")
    ServerResponse warehouseGmList(@RequestParam("request") HttpServletRequest request,
                                   @RequestParam("userToken") String userToken,
                                   @RequestParam("houseId") String houseId,
                                   @RequestParam("name") String name,
                                   @RequestParam("type") String type);

    @PostMapping("edit/product/edit")
    @ApiOperation(value = "批量更新指定商品信息", notes = "批量更新指定商品信息")
    ServerResponse editProductData(@RequestParam("cityId") String cityId,@RequestParam("productJson")  String productJson);


    /**
     * 查询指定已购买的商品仓库明细
     * url = master/app/house/warehouse/data
     * @param houseId 工地ID
     * @param gid 商品ID
     * @param type 类型：  0 = 材料/包工包料   1=人工
     * @return
     *    repairCount=补货数
     *    shopCount=购买数
     *    receive=收货数
     *    workBack=工匠退货数
     *    ownerBack=业主退货数
     *    surCount=剩余数
     *    tolPrice=实际花费
     *    list=材料记录:
     *         ["orderId":"订单ID"]
     *         ["type":"0:补货单;1:补人工单;2:退货单;3:退人工单,4:业主退,5:发货单"]
     *         ["number":"订单编号"]
     *         ["createDate":"时间"]
     *         ["state":"订单类型为0-4时：1处理中,2不通过取消,3已通过,4已全部结算,5已撤回
     *                  订单类型为5时：配送状态（0待发货,1已发待收货,2已收货,3取消,4部分收,5已结算,6材料员撤回
     *                  "]
     *
     */
    @PostMapping("app/house/warehouse/data")
    @ApiOperation(value = "查询指定已购买的商品仓库明细", notes = "查询指定已购买的商品仓库明细")
    ServerResponse getWarehouseData(@RequestParam("houseId")String houseId,
                                    @RequestParam("gid")String gid,
                                    @RequestParam("type")Integer type);

}
