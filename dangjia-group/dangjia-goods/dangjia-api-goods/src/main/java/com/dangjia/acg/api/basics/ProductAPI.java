package com.dangjia.acg.api.basics;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * 
   * @类 名： ProductController
   * @功能描述： TODO
   * @作者信息： zmj
   * @创建时间： 2018-9-10上午9:25:10
 */
@Api(description = "货品管理接口")
@FeignClient("dangjia-service-goods")
public interface ProductAPI {

    /**
     * 查询所有货品
     * @Title: queryProduct
     * @Description: TODO
     * @param: @param category_id
     * @param: @return   
     * @return: JsonResult   
     * @throws
     */
	@PostMapping("/basics/product/queryProduct")
	@ApiOperation(value = "查询所有货品", notes = "查询所有货品")
    public ServerResponse<PageInfo> queryProduct(@RequestParam("request") HttpServletRequest request, @RequestParam("pageDTO") PageDTO pageDTO, @RequestParam("categoryId")String categoryId);
    
    /**
     * 查询所有单位
     * @Title: queryUnit
     * @Description: TODO
     * @param: @return   
     * @return: JsonResult   
     * @throws
     */
	@PostMapping("/basics/product/queryUnit")
	@ApiOperation(value = "查询所有单位", notes = "查询所有单位")
  	public ServerResponse queryUnit(@RequestParam("request") HttpServletRequest request);
  	
    /**
     * 查询所有品牌
     * @Title: queryBrand
     * @Description: TODO
     * @param: @return   
     * @return: JsonResult   
     * @throws
     */
	@PostMapping("/basics/product/queryBrand")
	@ApiOperation(value = "查询所有品牌", notes = "查询所有品牌")
  	public ServerResponse queryBrand(@RequestParam("request") HttpServletRequest request);
  	
    /**
     * 根据品牌查询所有品牌系列
     * @Title: queryBrandSeries
     * @Description: TODO
     * @param: @return   
     * @return: JsonResult   
     * @throws
     */
	@PostMapping("/basics/product/queryBrandSeries")
	@ApiOperation(value = "根据品牌查询所有品牌系列", notes = "根据品牌查询所有品牌系列")
  	public ServerResponse queryBrandSeries(@RequestParam("request") HttpServletRequest request,@RequestParam("brandId")String brandId);
  	
  	/**
  	 * 新增商品
  	 */
	@PostMapping("/basics/product/saveGoods")
	@ApiOperation(value = "新增商品", notes = "新增商品")
  	public ServerResponse saveGoods(@RequestParam("request") HttpServletRequest request,@RequestParam("name")String name,
									  @RequestParam("categoryId")String categoryId,@RequestParam("buy")Integer buy,
									  @RequestParam("sales")Integer sales,@RequestParam("unitId")String unitId,@RequestParam("type")Integer type,@RequestParam("arrString")String arrString);
    
    /**
  	 * 根据商品id查询关联品牌
  	 */
	@PostMapping("/basics/product/queryBrandByGid")
	@ApiOperation(value = "根据商品id查询关联品牌", notes = "根据商品id查询关联品牌")
  	public ServerResponse queryBrandByGid(@RequestParam("request") HttpServletRequest request,@RequestParam("goodsId")String goodsId);
    
    /**
  	 * 根据商品id和品牌id查询关联品牌系列 
  	 */
	@PostMapping("/basics/product/queryBrandByGidAndBid")
	@ApiOperation(value = "根据商品id和品牌id查询关联品牌系列", notes = "根据商品id和品牌id查询关联品牌系列")
  	public ServerResponse queryBrandByGidAndBid(@RequestParam("request") HttpServletRequest request,@RequestParam("goodsId") String goodsId,
												  @RequestParam("brandId")String brandId);
    /**
  	 * 新增货品
  	 */
	@PostMapping("/basics/product/insertProduct")
	@ApiOperation(value = "新增货品", notes = "新增货品")
  	ServerResponse insertProduct(@RequestParam("request") HttpServletRequest request,@RequestParam("productArr") String productArr);
    
    /**
     * 根据商品id查询对应商品
     */
	@PostMapping("/basics/product/getGoodsByGid")
	@ApiOperation(value = "根据商品id查询对应商品", notes = "根据商品id查询对应商品")
  	public ServerResponse getGoodsByGid(@RequestParam("request") HttpServletRequest request,@RequestParam("goodsId")String goodsId);
    
    /**
     * 修改商品
     */
	@PostMapping("/basics/product/updateGoods")
	@ApiOperation(value = "修改商品", notes = "修改商品")
  	public ServerResponse updateGoods(@RequestParam("request") HttpServletRequest request,@RequestParam("id")String id,@RequestParam("name")String name,
										@RequestParam("categoryId")String categoryId,@RequestParam("buy")Integer buy,
										@RequestParam("sales")Integer sales,@RequestParam("unitId")String unitId,@RequestParam("type")Integer type,
										@RequestParam("arrString")String arrString);
    /**
  	 * 修改货品
  	 */
	@PostMapping("/basics/product/updateProduct")
	@ApiOperation(value = "修改货品", notes = "修改货品")
  	ServerResponse updateProduct(@RequestParam("request") HttpServletRequest request,@RequestParam("productArr")String productArr);

	/**
	 * 根据货品id查询货品对象
	 */
	@PostMapping("/basics/product/getProductById")
	@ApiOperation(value = "根据货品id查询货品对象", notes = "根据货品id查询货品对象")
	public ServerResponse getProductById(@RequestParam("request") HttpServletRequest request,@RequestParam("id")String id);

	@PostMapping("/basics/product/deleteProductById")
	@ApiOperation(value = "根据货品id删除货品对象", notes = "根据货品id删除货品对象")
	public ServerResponse deleteProductById(@RequestParam("request") HttpServletRequest request,@RequestParam("id")String id);

	@PostMapping("/basics/product/deleteGoods")
	@ApiOperation(value = "根据id删除商品和下属货品", notes = "根据id删除商品和下属货品")
	public ServerResponse deleteGoods(@RequestParam("request") HttpServletRequest request,@RequestParam("id")String id);

	@PostMapping("/basics/product/queryGoodsList")
	@ApiOperation(value = "查询商品及下属货品", notes = "查询商品及下属货品")
	public ServerResponse queryGoodsList(@RequestParam("request")HttpServletRequest request,@RequestParam("pageDTO")PageDTO pageDTO,
										 @RequestParam("categoryId")String categoryId,@RequestParam("name")String name);

	@PostMapping("/basics/product/queryGoodsListByCategoryLikeName")
	@ApiOperation(value = "按照name模糊查询商品及下属货品", notes = "按照name模糊查询商品及下属货品")
	public ServerResponse queryGoodsListByCategoryLikeName(@RequestParam("request")HttpServletRequest request,@RequestParam("pageDTO")PageDTO pageDTO,
														   @RequestParam("categoryId")String categoryId,@RequestParam("name")String name);

	/**
	 * 批量添加/修改货品标签
	 * @param request
	 * @param productLabeList
	 * @return
	 */
	@PostMapping("/basics/product/updateProductLabelList")
	@ApiOperation(value = "批量添加/修改货品标签", notes = "批量添加/修改货品标签")
	public ServerResponse updateProductLabelList(@RequestParam("request") HttpServletRequest request,@RequestParam("productLabeList") String productLabeList);

	/*@PostMapping("/basics/product/getSwitchProduct")
	@ApiOperation(value = "根据系列和属性查询切换货品", notes = "根据系列和属性查询切换货品")
	public  ServerResponse getSwitchProduct(@RequestParam("request")HttpServletRequest request,@RequestParam("brandSeriesId")String brandSeriesId,
											@RequestParam("attributeIdArr")String attributeIdArr);*/
}
