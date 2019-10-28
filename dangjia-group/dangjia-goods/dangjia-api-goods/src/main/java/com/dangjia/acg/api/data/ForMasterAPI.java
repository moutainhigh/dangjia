package com.dangjia.acg.api.data;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.actuary.ShopGoodsDTO;
import com.dangjia.acg.dto.product.ProductWorkerDTO;
import com.dangjia.acg.dto.product.StorefontInfoDTO;
import com.dangjia.acg.modle.actuary.BudgetMaterial;
import com.dangjia.acg.modle.basics.Technology;
import com.dangjia.acg.modle.product.BasicsGoods;
import com.dangjia.acg.modle.product.DjBasicsProductTemplate;
import com.dangjia.acg.modle.sup.Supplier;
import com.dangjia.acg.modle.sup.SupplierProduct;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * author: Ronalcheng
 * Date: 2018/11/24 0024
 * Time: 11:39
 * 给master提供精算数据
 */
@Api(description = "给master提供精算数据")
@FeignClient("dangjia-service-goods")
public interface ForMasterAPI {

    @PostMapping("/data/forMaster/getUnitName")
    @ApiOperation(value = "获取单位名", notes = "获取单位名")
    String getUnitName(@RequestParam("cityId") String cityId,@RequestParam("unitId") String unitId);

    @PostMapping("/data/forMaster/getSupplierProduct")
    @ApiOperation(value = "查询供应价", notes = "查询供应价")
    SupplierProduct getSupplierProduct(@RequestParam("cityId") String cityId,
                                       @RequestParam("supplierId") String supplierId,
                                       @RequestParam("productId") String productId);

    @PostMapping("/data/forMaster/getSupplier")
    @ApiOperation(value = "查询供应商", notes = "查询供应商")
    Supplier getSupplier(@RequestParam("cityId") String cityId,@RequestParam("supplierId") String supplierId);

    @PostMapping("/data/forMaster/backCount")
    @ApiOperation(value = "增加退数量", notes = "增加退数量")
    void backCount(@RequestParam("cityId") String cityId,@RequestParam("houseId") String houseId,
                   @RequestParam("workerGoodsId") String workerGoodsId,
                   @RequestParam("num") Double num);

    @PostMapping("/data/forMaster/repairCount")
    @ApiOperation(value = "增加补数量", notes = "增加补数量")
    void repairCount(@RequestParam("cityId") String cityId,@RequestParam("houseId") String houseId,
                     @RequestParam("workerGoodsId") String workerGoodsId,
                     @RequestParam("num") Double num);

    @PostMapping("/data/forMaster/byTechnologyId")
    @ApiOperation(value = "查工艺", notes = "查工艺")
    Technology byTechnologyId(@RequestParam("cityId") String cityId,@RequestParam("technologyId") String technologyId);

    //@PostMapping("/data/forMaster/brandSeriesName")
   //@ApiOperation(value = "查询品牌系列名", notes = "查询品牌系列名")
    //String brandSeriesName(@RequestParam("cityId") String cityId,@RequestParam("productId") String productId);

    @PostMapping("/data/forMaster/brandName")
    @ApiOperation(value = "查询品牌名", notes = "查询品牌名")
    String brandName(@RequestParam("cityId") String cityId,@RequestParam("productId") String productId);

    @PostMapping("/data/forMaster/getWorkerGoods")
    @ApiOperation(value = "工价商品信息", notes = "工价商品信息")
    ProductWorkerDTO getWorkerGoods(@RequestParam("cityId") String cityId, @RequestParam("workerGoodsId") String workerGoodsId);
   // WorkerGoods getWorkerGoods(@RequestParam("cityId") String cityId,@RequestParam("workerGoodsId") String workerGoodsId);

    @PostMapping("/data/goods/settop")
    @ApiOperation(value = "设置材料或者人工商品置顶或取消置顶", notes = "设置材料或者人工商品置顶或取消置顶")
    ServerResponse setProductOrWorkerGoodsIsTop(@RequestParam("gid") String gid, @RequestParam("type") Integer type,@RequestParam("istop") String istop);

    @PostMapping("/data/forMaster/getGoods")
    @ApiOperation(value = "货品信息", notes = "货品信息")
    BasicsGoods getGoods(@RequestParam("cityId") String cityId, @RequestParam("goodsId") String goodsId);

    @PostMapping("/data/forMaster/getProduct")
    @ApiOperation(value = "商品信息", notes = "商品信息")
    DjBasicsProductTemplate getProduct(@RequestParam("cityId") String cityId, @RequestParam("productId") String productId);

    @PostMapping("/data/forMaster/caiLiao")
    @ApiOperation(value = "支付回调获取材料精算", notes = "支付回调获取材料精算")
    List<BudgetMaterial> caiLiao(@RequestParam("cityId") String cityId,@RequestParam("houseFlowId") String houseFlowId);

    @PostMapping("/data/forMaster/renGong")
    @ApiOperation(value = "支付回调修改人工精算", notes = "支付回调修改人工精算")
    List<BudgetMaterial> renGong(@RequestParam("cityId") String cityId,@RequestParam("houseFlowId") String houseFlowId);

    @PostMapping("/data/forMaster/getBudgetWorkerPrice")
    @ApiOperation(value = "支付时工种人工总价", notes = "支付时工种人工总价")
    Double getBudgetWorkerPrice(@RequestParam("houseId") String houseId,
                                @RequestParam("workerTypeId") String workerTypeId,
                                @RequestParam("cityId") String cityId);

    @PostMapping("/data/forMaster/getBudgetCaiPrice")
    @ApiOperation(value = "支付时工种材料总价", notes = "支付时工种材料总价")
    Double getBudgetCaiPrice(@RequestParam("houseId") String houseId,
                             @RequestParam("workerTypeId") String workerTypeId,
                             @RequestParam("cityId") String cityId);

    @PostMapping("/data/forMaster/nonPaymentCai")
    @ApiOperation(value = "未付款材料", notes = "未付款材料")
    Double nonPaymentCai(@RequestParam("houseId") String houseId,
                         @RequestParam("workerTypeId") String workerTypeId,
                         @RequestParam("cityId") String cityId);


    @PostMapping("/data/forMaster/getNotCaiPrice")
    @ApiOperation(value = "支付时工种未选择材料总价", notes = "支付时工种未选择材料总价")
    Double getNotCaiPrice(@RequestParam("houseId") String houseId,
                          @RequestParam("workerTypeId") String workerTypeId,
                          @RequestParam("cityId") String cityId);

    @PostMapping("/data/forMaster/getBudgetSerPrice")
    @ApiOperation(value = "支付时工种服务总价", notes = "支付时工种服务总价")
    Double getBudgetSerPrice(@RequestParam("houseId") String houseId,
                             @RequestParam("workerTypeId") String workerTypeId,
                             @RequestParam("cityId") String cityId);

    @PostMapping("/data/forMaster/nonPaymentSer")
    @ApiOperation(value = "未付款工种服务总价", notes = "未付款工种服务总价")
    Double nonPaymentSer(@RequestParam("houseId") String houseId,
                         @RequestParam("workerTypeId") String workerTypeId,
                         @RequestParam("cityId") String cityId);

    @PostMapping("/data/forMaster/getNotSerPrice")
    @ApiOperation(value = "支付时工种未选择服务总价", notes = "支付时工种未选择服务总价")
    Double getNotSerPrice(@RequestParam("houseId") String houseId,
                          @RequestParam("workerTypeId") String workerTypeId,
                          @RequestParam("cityId") String cityId);


    @PostMapping("/data/budget/label")
    @ApiOperation(value = "查询工种材料未支付所有商品的标签", notes = "查询工种材料未支付所有商品的标签")
    List<ShopGoodsDTO> queryShopGoods(@RequestParam("houseId") String houseId, @RequestParam("workerTypeId") String workerTypeId, @RequestParam("cityId") String cityId);


    @PostMapping("/data/house/getStroreProductInfo")
    @ApiOperation(value = "获取商品对应的基本信息及对应的货品，商品列表", notes = "获取商品对应的基本信息及对应的货品，商品列表")
    StorefontInfoDTO getStroreProductInfo(@RequestParam("cityId") String cityId,
                                          @RequestParam("storefontId") String storefontId,
                                          @RequestParam("productId") String productId);

    //
    @PostMapping("/data/house/getproductTempListByStorefontId")
    @ApiOperation(value = "查询当前店铺下对应货品下的所有商品", notes = "查询当前店铺下对应货品下的所有商品列表")
    ServerResponse getProductTempListByStorefontId(@RequestParam("cityId") String cityId,
                                                   @RequestParam("storefontId") String storefontId,
                                                   @RequestParam("goodsId") String goodsId);

    @PostMapping("/data/house/getStroreProductInfoById")
    @ApiOperation(value = "获取商品对应的基本信息(店铺上加商品信息)", notes = "获取商品对应的基本信息(店铺商品信息)")
    StorefontInfoDTO getStroreProductInfoById(@RequestParam("cityId") String cityId,
                                          @RequestParam("productId") String productId);

    @PostMapping("/data/house/insertActuarialDesignInfo")
    @ApiOperation(value = "添加对应的精算信息", notes = "添加对应的精算信息")
    void  insertActuarialDesignInfo(@RequestParam("cityId") String cityId,
                              @RequestParam("actuarialDesignAttr") String actuarialDesignAttr,
                              @RequestParam("houseId") String houseId,
                              @RequestParam("square") BigDecimal square);

    @PostMapping("/data/house/getAllBudgetMaterialWorkerList")
    @ApiOperation(value = "获取对应房屋对应工种的精算信息(中台，我要装修列表用)", notes = "获取对应房屋对应工种的精算信息(中台，我要装修列表用)")
    Map<String, Object> getAllBudgetMaterialWorkerList(@RequestParam("cityId") String cityId,
                                                     @RequestParam("houseId") String houseId,
                                                     @RequestParam("workerTypeId") String workerTypeId);
    @PostMapping("/data/house/getHouseDetailInfoList")
    @ApiOperation(value = "APP我要装修页面，下单详情显示", notes = "APP我要装修页面，下单详情显示)")
    List<Map<String,Object>> getHouseDetailInfoList(@RequestParam("cityId") String cityId,
                                                    @RequestParam("houseId") String houseId);
}
