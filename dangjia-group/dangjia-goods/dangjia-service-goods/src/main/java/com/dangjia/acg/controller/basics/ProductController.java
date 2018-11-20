package com.dangjia.acg.controller.basics;

import com.dangjia.acg.api.basics.ProductAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.basics.GoodsService;
import com.dangjia.acg.service.basics.ProductService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 
   * @类 名： ProductController
   * @功能描述： TODO
   * @作者信息： zmj
   * @创建时间： 2018-9-10上午9:25:10
 */
@RestController
public class ProductController implements ProductAPI {
    /**
     *service
     */
    @Autowired
    private ProductService productService;
    @Autowired
    private GoodsService goodsService;

    /**
     * 查询所有货品
     * @Title: queryProduct
     * @Description: TODO
     * @param: @param category_id
     * @param: @return   
     * @return: JsonResult   
     * @throws
     */
	@Override
	@ApiMethod
    public ServerResponse<PageInfo> queryProduct(PageDTO pageDTO, String categoryId){
        return productService.queryProduct(pageDTO.getPageNum(),pageDTO.getPageSize(),categoryId);
    }
    
    /**
     * 查询所有单位
     * @Title: queryUnit
     * @Description: TODO
     * @param: @return   
     * @return: JsonResult   
     * @throws
     */
	@Override
	@ApiMethod
  	public ServerResponse queryUnit(){
  		 return productService.queryUnit();
  	}
  	
    /**
     * 查询所有品牌
     * @Title: queryBrand
     * @Description: TODO
     * @param: @return   
     * @return: JsonResult   
     * @throws
     */
	@Override
	@ApiMethod
  	public ServerResponse queryBrand(){
  		 return productService.queryBrand();
  	}
  	
    /**
     * 根据品牌查询所有品牌系列
     * @Title: queryBrandSeries
     * @Description: TODO
     * @param: @return   
     * @return: JsonResult   
     * @throws
     */
	@Override
	@ApiMethod
  	public ServerResponse queryBrandSeries(String brandId){
  		 return productService.queryBrandSeries(brandId);
  	}
  	
  	/**
  	 * 新增商品
  	 */
	@Override
	@ApiMethod
  	public ServerResponse saveGoods(String name,String categoryId,Integer buy,
			Integer sales,String unitId,Integer type,String arrString){
		return goodsService.saveGoods(name, categoryId, buy, sales, unitId, type, arrString);
  		
  	}
    
    /**
  	 * 根据商品id查询关联品牌
  	 */
	@Override
	@ApiMethod
  	public ServerResponse queryBrandByGid(String goodsId){
		return goodsService.queryBrandByGid(goodsId);
  		
  	}
    
    /**
  	 * 根据商品id和品牌id查询关联品牌系列 
  	 */
	@Override
	@ApiMethod
  	public ServerResponse queryBrandByGidAndBid(String goodsId,String brandId){
		return goodsService.queryBrandByGidAndBid(goodsId,brandId);
  		
  	}
    /**
  	 * 新增货品
  	 */
	@Override
	@ApiMethod
  	public ServerResponse insertProduct(String productArr){
		return productService.insertProduct(productArr);
  		
  	}
    
    /**
     * 根据商品id查询对应商品
     */
	@Override
	@ApiMethod
  	public ServerResponse getGoodsByGid(String goodsId){
		return goodsService.getGoodsByGid(goodsId);
  		
  	}
    
    /**
     * 修改商品
     */
	@Override
	@ApiMethod
  	public ServerResponse updateGoods(String id,String name,String categoryId,Integer buy,
			Integer sales,String unitId,Integer type,String arrString){
		return goodsService.updateGoods(id,name, categoryId, buy, sales, unitId, type, arrString);
  		
  	}
    /**
  	 * 修改货品
  	 */
	@Override
	@ApiMethod
  	public ServerResponse updateProduct(String productArr){
		return productService.updateProduct(productArr);
  		
  	}
    
}
