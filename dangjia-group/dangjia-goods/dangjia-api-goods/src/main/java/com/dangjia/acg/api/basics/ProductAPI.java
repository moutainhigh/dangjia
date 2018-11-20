package com.dangjia.acg.api.basics;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    public ServerResponse<PageInfo> queryProduct(@RequestParam("pageDTO") PageDTO pageDTO, String categoryId);
    
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
  	public ServerResponse queryUnit();
  	
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
  	public ServerResponse queryBrand();
  	
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
  	public ServerResponse queryBrandSeries(String brandId);
  	
  	/**
  	 * 新增商品
  	 */
	@PostMapping("/basics/product/saveGoods")
	@ApiOperation(value = "新增商品", notes = "新增商品")
  	public ServerResponse saveGoods(String name,String categoryId,Integer buy,
			Integer sales,String unitId,Integer type,String arrString);
    
    /**
  	 * 根据商品id查询关联品牌
  	 */
	@PostMapping("/basics/product/queryBrandByGid")
	@ApiOperation(value = "根据商品id查询关联品牌", notes = "根据商品id查询关联品牌")
  	public ServerResponse queryBrandByGid(String goodsId);
    
    /**
  	 * 根据商品id和品牌id查询关联品牌系列 
  	 */
	@PostMapping("/basics/product/queryBrandByGidAndBid")
	@ApiOperation(value = "根据商品id和品牌id查询关联品牌系列", notes = "根据商品id和品牌id查询关联品牌系列")
  	public ServerResponse queryBrandByGidAndBid(String goodsId,String brandId);
    /**
  	 * 新增货品
  	 */
	@PostMapping("/basics/product/insertProduct")
	@ApiOperation(value = "新增货品", notes = "新增货品")
  	public ServerResponse insertProduct(String productArr);
    
    /**
     * 根据商品id查询对应商品
     */
	@PostMapping("/basics/product/getGoodsByGid")
	@ApiOperation(value = "根据商品id查询对应商品", notes = "根据商品id查询对应商品")
  	public ServerResponse getGoodsByGid(String goodsId);
    
    /**
     * 修改商品
     */
	@PostMapping("/basics/product/updateGoods")
	@ApiOperation(value = "修改商品", notes = "修改商品")
  	public ServerResponse updateGoods(String id,String name,String categoryId,Integer buy,
			Integer sales,String unitId,Integer type,String arrString);
    /**
  	 * 修改货品
  	 */
	@PostMapping("/basics/product/updateProduct")
	@ApiOperation(value = "修改货品", notes = "修改货品")
  	public ServerResponse updateProduct(String productArr);
    
}
