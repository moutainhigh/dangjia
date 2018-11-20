package com.dangjia.acg.service.basics;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.common.exception.BaseException;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.mapper.basics.IGoodsMapper;
import com.dangjia.acg.modle.brand.Brand;
import com.dangjia.acg.modle.brand.BrandSeries;
import com.dangjia.acg.modle.basics.Goods;
import com.dangjia.acg.modle.brand.GoodsSeries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
/**
 * 商品业务层
 * @ClassName: GoodsServiceImpl
 * @Description: TODO
 * @author: zmj
 * @date: 2018-9-20下午2:44:47
 */
@Service
public class GoodsService {

	@Autowired
	private IGoodsMapper iGoodsMapper;
	/**
	 * 保存商品
	 * <p>Title: saveGoods</p>
	 * <p>Description: </p>
	 * @param name
	 * @param categoryId
	 * @param buy
	 * @param sales
	 * @param unitId
	 * @param type
	 * @param arrString
	 * @return
	 */
	public ServerResponse saveGoods(String name, String categoryId, Integer buy,
									Integer sales, String unitId, Integer type, String arrString){
		try{
			Goods goods=new Goods();
			goods.setName(name);
			goods.setCategoryId(categoryId);//分类
			goods.setBuy(buy);//购买性质
			goods.setSales(sales);//退货性质
			goods.setUnitId(unitId);//单位
			goods.setType(type);//商品性质
			goods.setCreateDate(new Date());
			goods.setModifyDate(new Date());
			iGoodsMapper.insert(goods);
			JSONArray arr=JSONArray.parseArray(arrString);
			for(int i=0;i<arr.size();i++){//新增商品关联品牌系列
				JSONObject obj=arr.getJSONObject(i);
				GoodsSeries gs=new GoodsSeries();
				gs.setGoodsId(goods.getId());
				gs.setBrandId(obj.getString("brandId"));
				gs.setSeriesId(obj.getString("seriesId"));
				gs.setCreateDate(new Date());
				gs.setModifyDate(new Date());
				iGoodsMapper.insertGoodsSeries(gs);
			}
			return ServerResponse.createBySuccess("新增成功",goods.getId());
		} catch (Exception e) {
			e.printStackTrace();
			throw new BaseException(ServerCode.WRONG_PARAM, "新增失败");
		}
	}
    /**
     * 根据商品id查询关联品牌
     * <p>Title: queryBrandByGid</p>
     * <p>Description: </p>
     * @param goodsId
     * @return
     */
	public ServerResponse queryBrandByGid(String goodsId) {
		try{
			List<Brand> bList= iGoodsMapper.queryBrandByGid(goodsId);
			return ServerResponse.createBySuccess("查询成功",bList);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BaseException(ServerCode.WRONG_PARAM, "查询失败");
		}
	}
	
	/**
     * 根据商品id和品牌id查询关联品牌系列 
     * <p>Title: queryBrandByGid</p>
     * <p>Description: </p>
     * @param goodsId
     * @return
     */
	public ServerResponse queryBrandByGidAndBid(String goodsId,String brandId) {
		try{
			List<BrandSeries> bList= iGoodsMapper.queryBrandByGidAndBid(goodsId,brandId);
			return ServerResponse.createBySuccess("查询成功",bList);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BaseException(ServerCode.WRONG_PARAM, "查询失败");
		}
	}
	
	/**
	 * 根据商品id查询对应商品
	 * <p>Title: getGoodsByGid</p>
	 * <p>Description: </p>
	 * @param goodsId
	 * @return
	 */
	public ServerResponse getGoodsByGid(String goodsId) {
		try{
			Goods goods= iGoodsMapper.queryById(goodsId);
			return ServerResponse.createBySuccess("查询成功",goods);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BaseException(ServerCode.WRONG_PARAM, "查询失败");
		}
	}
	
	/**
	 * 修改商品
	 * <p>Title: updateGoods</p>
	 * <p>Description: </p>
	 * @param id
	 * @param name
	 * @param categoryId
	 * @param buy
	 * @param sales
	 * @param unitId
	 * @param type
	 * @param arrString
	 * @return
	 */
	public ServerResponse updateGoods(String id,String name,String categoryId,Integer buy,
			Integer sales,String unitId,Integer type,String arrString){
		try{
			Goods goods=new Goods();
			goods.setId(id);
			goods.setName(name);
			goods.setCategoryId(categoryId);//分类
			goods.setBuy(buy);//购买性质
			goods.setSales(sales);//退货性质
			goods.setUnitId(unitId);//单位
			goods.setType(type);//商品性质
			goods.setModifyDate(new Date());
			iGoodsMapper.updateByPrimaryKeySelective(goods);
			JSONArray arr=JSONArray.parseArray(arrString);
			iGoodsMapper.deleteGoodsSeries(id);//先删除商品所有跟品牌关联
			for(int i=0;i<arr.size();i++){//新增商品关联品牌系列
				JSONObject obj=arr.getJSONObject(i);
				GoodsSeries gs=new GoodsSeries();
				gs.setGoodsId(id);
				gs.setBrandId(obj.getString("brandId"));
				gs.setSeriesId(obj.getString("seriesId"));
				gs.setCreateDate(new Date());
				gs.setModifyDate(new Date());
				iGoodsMapper.insertGoodsSeries(gs);
			}
			return ServerResponse.createBySuccessMessage("修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			throw new BaseException(ServerCode.WRONG_PARAM, "修改失败");
		}
	}
}
