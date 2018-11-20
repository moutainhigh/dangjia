package com.dangjia.acg.service.basics;

import com.dangjia.acg.common.exception.BaseException;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.mapper.basics.IGoodsAttributeMapper;
import com.dangjia.acg.mapper.basics.IGoodsCategoryMapper;
import com.dangjia.acg.mapper.basics.IGoodsMapper;
import com.dangjia.acg.modle.basics.Goods;
import com.dangjia.acg.modle.attribute.GoodsAttribute;
import com.dangjia.acg.modle.attribute.GoodsCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 
   * @类 名： ProductServiceImpl
   * @功能描述：  商品service实现类
   * @作者信息： zmj
   * @创建时间： 2018-9-10下午2:33:37
 */
@Service
public class GoodsCategoryService {

	@Autowired
	private IGoodsCategoryMapper iGoodsCategoryMapper;
	@Autowired
	private IGoodsMapper iGoodsMapper;
	@Autowired
	private IGoodsAttributeMapper iGoodsAttributeMapper;
	//新增商品类别
	public ServerResponse insertGoodsCategory(String name, String parentID, String parentTop) {
		try{
			GoodsCategory category=new GoodsCategory();
			category.setName(name);
			category.setParentId(parentID);
			category.setParentTop(parentTop);
			category.setImage("");
			category.setCreateDate(new Date());
			category.setModifyDate(new Date());
			iGoodsCategoryMapper.insert(category);
			return ServerResponse.createBySuccess("新增成功",category.getId());
		} catch (Exception e) {
			e.printStackTrace();
			throw new BaseException(ServerCode.WRONG_PARAM, "查询失败");
		}
	}
	//修改商品类别
	public ServerResponse doModifyGoodsCategory(String id,String name,String parentID,String parentTop) {
		try{
			GoodsCategory category=new GoodsCategory();
			category.setId(id);
			category.setName(name);
			category.setParentId(parentID);
			category.setParentTop(parentTop);
			category.setModifyDate(new Date());
			iGoodsCategoryMapper.updateByPrimaryKeySelective(category);
			return ServerResponse.createBySuccessMessage("修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			throw new BaseException(ServerCode.WRONG_PARAM, "修改失败");
		}
	}

	//查询商品属性列表
	public ServerResponse  queryGoodsCategory(String parentId) {
		try{
			List<Map<String, Object>> mapList=new ArrayList<Map<String,Object>>();
			List<GoodsCategory> goodsCategoryList = iGoodsCategoryMapper.queryCategoryByParentId(parentId);
			for(GoodsCategory goodsCategory:goodsCategoryList){
				Map<String, Object> map=new HashMap<String, Object>();
				map.put("id", goodsCategory.getId());
				map.put("name", goodsCategory.getName());
				mapList.add(map);
			}
			return ServerResponse.createBySuccess("查询成功", mapList);
		}catch (Exception e) {
			e.printStackTrace();
			return ServerResponse.createByErrorMessage("查询失败");
		}
	}
	//删除商品类别
	public ServerResponse deleteGoodsCategory(String id) {
		try{
			List<GoodsCategory> goodsCategoryList=iGoodsCategoryMapper.queryCategoryByParentId(id);//根据id查询是否有下级类别
			List<Goods> goodsList = iGoodsMapper.queryByCategoryId(id);//根据id查询是否有关联商品
			List<GoodsAttribute> GoodsAList=iGoodsAttributeMapper.queryCategoryAttribute(id);//根据id查询是否有关联属性
			if(goodsCategoryList.size()>0){
				return ServerResponse.createByErrorMessage("此类别有下级不能删除");
			}
			if(goodsList.size()>0){
				return ServerResponse.createByErrorMessage("此类别有关联商品不能删除");
			}
			if(GoodsAList.size()>0){
				return ServerResponse.createByErrorMessage("此类别有关联属性不能删除");
			}
			iGoodsCategoryMapper.deleteById(id);
			return ServerResponse.createBySuccessMessage("删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			throw new BaseException(ServerCode.WRONG_PARAM, "删除失败");
		}
	}

	//查询类别id查询所有父级以及父级属性
	public ServerResponse  queryAttributeListById(String goodsCategoryId) {
		try{
			GoodsCategory goodsCategory=iGoodsCategoryMapper.selectByPrimaryKey(goodsCategoryId);
			if(goodsCategory==null){
				return ServerResponse.createByErrorMessage("查询失败");
			}
			List<GoodsAttribute> gaList=iGoodsAttributeMapper.queryCategoryAttribute(goodsCategory.getId());
			while (goodsCategory!=null){
				goodsCategory=iGoodsCategoryMapper.selectByPrimaryKey(goodsCategory.getParentId());
				if(goodsCategory!=null){
					gaList.addAll(iGoodsAttributeMapper.queryCategoryAttribute(goodsCategory.getId()));
				}
			}
			return ServerResponse.createBySuccess("查询成功", gaList);
		}catch (Exception e) {
			e.printStackTrace();
			return ServerResponse.createByErrorMessage("查询失败");
		}
	}
}
