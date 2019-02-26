package com.dangjia.acg.service.basics;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.BaseException;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.mapper.basics.IBrandMapper;
import com.dangjia.acg.mapper.basics.IBrandSeriesMapper;
import com.dangjia.acg.modle.brand.Brand;
import com.dangjia.acg.modle.brand.BrandSeries;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 * 
 * 
 * @类 名： BrandServiceImpl.java
 * @功能描述：  商品品牌Service实现类
 * @作者信息： hb
 * @创建时间： 2018-9-13下午3:30:02
 */
@Service
public class BrandService {

	@Autowired
	private IBrandMapper iBrandMapper;
	@Autowired
	private IBrandSeriesMapper iBrandSeriesMapper;
	@Autowired
	private ConfigUtil configUtil;

	//查询所有品牌
	public ServerResponse<PageInfo> getAllBrand(Integer pageNum, Integer pageSize) {
		try {
			if (pageNum == null) {
				pageNum = 1;
			}
			if (pageSize == null) {
				pageSize = 10;
			}
			String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
			PageHelper.startPage(pageNum, pageSize);
			List<Brand> Brandlist = iBrandMapper.getBrands();
			List<Map<String, Object>> list=new ArrayList<Map<String, Object>>();
			for (Brand brand : Brandlist) {
				Map<String, Object> obj = new HashMap<String, Object>();
				Map<String, Object> map=new HashMap<String, Object>();
				map.put("id", brand.getId());
				map.put("name", brand.getName());
				map.put("createDate", brand.getCreateDate().getTime());
				map.put("modifyDate", brand.getModifyDate().getTime());
				List<BrandSeries> mapList=iBrandSeriesMapper.queryBrandSeries(brand.getId());
				List<Map<String,Object>> mapList2=new ArrayList<>();
				for(BrandSeries bs:mapList){
					String imageUrl=bs.getImage();
					bs.setImage(address+bs.getImage());
					Map<String,Object> mapSeries = BeanUtils.beanToMap(bs);
					mapSeries.put("imageUrl",imageUrl);
					mapList2.add(mapSeries);
				}
				obj.put("mapList", mapList2);
				obj.put("brand", map);
				list.add(obj);
			}
			PageInfo pageResult = new PageInfo(Brandlist);
			pageResult.setList(list);
			return ServerResponse.createBySuccess("查询成功",pageResult);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BaseException(ServerCode.WRONG_PARAM, "查询失败");
		}
	}

	//根据Id查询品牌
	public ServerResponse select(String brandId){
		try {
			String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
			Map<String, Object> obj=new HashMap<String, Object>();
			Brand brand = iBrandMapper.selectByPrimaryKey(brandId);
			List<BrandSeries> mapList=iBrandSeriesMapper.queryBrandSeries(brand.getId());
			List<Map<String,Object>> mapList2=new ArrayList<>();
			for(BrandSeries bs:mapList){
				String imageUrl=bs.getImage();
				bs.setImage(address+bs.getImage());
				Map<String,Object> mapSeries = BeanUtils.beanToMap(bs);
				mapSeries.put("imageUrl",imageUrl);
				mapList2.add(mapSeries);
			}
			obj.put("brand", brand);
			obj.put("mapList", mapList2);
			return ServerResponse.createBySuccess("查询成功",obj);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BaseException(ServerCode.WRONG_PARAM, "查询失败");
		}
	}

	//修改品牌
	public ServerResponse update(String id ,String name,String brandSeriesList){
		try {
		    	Brand br=iBrandMapper.getBrandByName(name);
		    	if(br!=null&&!br.getId().equals(id)){
					return ServerResponse.createByErrorMessage("品牌名称重复");
				}
				Brand brand = new Brand();
			    brand.setId(id);
				brand.setName(name);
			    brand.setModifyDate(new Date());
			    iBrandMapper.updateByPrimaryKeySelective(brand);
				JSONArray brandSeriesLists = JSONArray.parseArray(brandSeriesList);
				for (int i = 0; i < brandSeriesLists.size(); i++) {
					JSONObject brandSeries = brandSeriesLists.getJSONObject(i);
					String brandSeriesId = brandSeries.getString("id");
					String brandSeriesName = brandSeries.getString("name");
					String content = brandSeries.getString("content");
					String image = brandSeries.getString("image");
					BrandSeries bSeries = new BrandSeries();
					bSeries.setBrandId(brand.getId());
					bSeries.setContent(content);
					bSeries.setName(brandSeriesName);
					if(image!=null && !"".equals(image)){
						bSeries.setImage(image);
					}
					bSeries.setModifyDate(new Date());
					if(brandSeriesId==null||"".equals(brandSeriesId)){
						iBrandSeriesMapper.insert(bSeries);
					}else{
						bSeries.setId(brandSeriesId);
						iBrandSeriesMapper.updateByPrimaryKeySelective(bSeries);
					}
				}
			return ServerResponse.createBySuccessMessage("修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			throw new BaseException(ServerCode.WRONG_PARAM, "修改失败");
		}
	}
	//新增品牌
	public ServerResponse insert(String brandSeriesList,String name){
		try {
			Example example = new Example(Brand.class);
			example.createCriteria().andEqualTo("name",name);
			List<Brand> bList=iBrandMapper.selectByExample(example);
			if(bList!=null&&bList.size()>0){
				return ServerResponse.createByErrorMessage("品牌名称重复");
			}
			Brand brand = new Brand();
			brand.setName(name);
			brand.setCreateDate(new Date());
			brand.setModifyDate(new Date());
			iBrandMapper.insert(brand);
			JSONArray brandSeriesLists = JSONArray.parseArray(brandSeriesList);
			for (int i = 0; i < brandSeriesLists.size(); i++) {
				JSONObject brandSeries = brandSeriesLists.getJSONObject(i);
				String brandSeriesName = brandSeries.getString("name");
				String content = brandSeries.getString("content");
				String image = brandSeries.getString("image");

				BrandSeries bSeries = new BrandSeries();
				bSeries.setBrandId(brand.getId());
				bSeries.setContent(content);
				bSeries.setName(brandSeriesName);
				bSeries.setImage(image);
				bSeries.setCreateDate(new Date());
				bSeries.setModifyDate(new Date());
				iBrandSeriesMapper.insert(bSeries);
			}
			return ServerResponse.createBySuccessMessage("新增成功");
		} catch (Exception e) {
			e.printStackTrace();
			throw new BaseException(ServerCode.WRONG_PARAM, "新增失败");
		}
	}
	//根据品牌名称查询品牌
	public ServerResponse<PageInfo> getBrandByName(Integer pageNum, Integer pageSize,String name){
		try {
			if (pageNum == null) {
				pageNum = 1;
			}
			if (pageSize == null) {
				pageSize = 10;
			}
			PageHelper.startPage(pageNum, pageSize);
			List<Brand> Brandlist = iBrandMapper.getBrandByNames(name);
			List<Map<String, Object>> list=new ArrayList<Map<String, Object>>();
			for (Brand brand : Brandlist) {
				List<BrandSeries> mapList=iBrandSeriesMapper.queryBrandSeries(brand.getId());
				String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
				List<Map<String,Object>> mapList2=new ArrayList<>();
				for(BrandSeries bs:mapList) {
					String imageUrl=bs.getImage();
					bs.setImage(address+bs.getImage());
					Map<String,Object> mapSeries = BeanUtils.beanToMap(bs);
					mapSeries.put("imageUrl",imageUrl);
					mapList2.add(mapSeries);
				}
				Map<String, Object> map=new HashMap<String, Object>();
				map.put("brand", brand);
				map.put("mapList", mapList2);
				list.add(map);
			}
			PageInfo pageResult = new PageInfo(Brandlist);
			pageResult.setList(list);
			return ServerResponse.createBySuccess("查询成功",pageResult);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BaseException(ServerCode.WRONG_PARAM, "查询失败");
		}
	}

	//根据Id删除品牌
	public ServerResponse deleteBrand(String brandId){
		try {
			iBrandMapper.deleteById(brandId);
			return ServerResponse.createBySuccessMessage("删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			throw new BaseException(ServerCode.WRONG_PARAM, "删除失败");
		}
	}
}
