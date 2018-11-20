package com.dangjia.acg.service.basics;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.BaseException;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.mapper.basics.IAttributeValueMapper;
import com.dangjia.acg.mapper.basics.IGoodsAttributeMapper;
import com.dangjia.acg.modle.attribute.AttributeValue;
import com.dangjia.acg.modle.attribute.CategoryAttribute;
import com.dangjia.acg.modle.attribute.GoodsAttribute;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
   * @类 名： GoodsAttributeServiceImpl
   * @功能描述： 类型属性
   * @作者信息： zmj
   * @创建时间： 2018-9-13上午10:15:58
 */
@Service
public class GoodsAttributeService {
	@Autowired
	private IGoodsAttributeMapper iGoodsAttributeMapper;
	@Autowired
	private IAttributeValueMapper iAttributeValueMapper;
	@Autowired
	private ConfigUtil configUtil;

	//根据类别id查询关联属性
	public ServerResponse<PageInfo>  queryGoodsAttribute(Integer pageNum, Integer pageSize,String goodsCategoryId) {
		String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class) + configUtil.getValue(SysConfig.PUBLIC_TEMPORARY_FILE_ADDRESS, String.class);
		try{
			if (pageNum == null) {
				pageNum = 1;
			}
			if (pageSize == null) {
				pageSize = 10;
			}
			PageHelper.startPage(pageNum, pageSize);
			List<GoodsAttribute> caList= new ArrayList<GoodsAttribute>();
			List<Map<String, Object>> rListMap=new ArrayList<Map<String,Object>>();
			if(goodsCategoryId==null||"".equals(goodsCategoryId)){
				caList=iGoodsAttributeMapper.query();
			}else{
				caList=iGoodsAttributeMapper.queryCategoryAttribute(goodsCategoryId);
			}
			for(GoodsAttribute ca:caList){
				Map<String, Object> caMap=new HashMap<String, Object>();
				caMap.put("id", ca.getId());
				caMap.put("name", ca.getName());
				caMap.put("type", ca.getType());
				SimpleDateFormat  sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				caMap.put("createDate", sdf.format(ca.getCreateDate()));
				caMap.put("modifyDate", sdf.format(ca.getModifyDate()));
				List<AttributeValue> avList=iAttributeValueMapper.queryByAttributeId(ca.getId());
				List<Map<String, Object>> avListMap=new ArrayList<Map<String,Object>>();
				for(AttributeValue av:avList){
					Map<String, Object> avMap=new HashMap<String, Object>();
					avMap.put("avId", av.getId());
					avMap.put("avName", av.getName());
					avMap.put("image", address+av.getImage());
					avListMap.add(avMap);
				}
				caMap.put("avListMap", avListMap);
				rListMap.add(caMap);
			}
			PageInfo pageResult = new PageInfo(caList);
			pageResult.setList(rListMap);
			return ServerResponse.createBySuccess("查询成功",pageResult);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BaseException(ServerCode.WRONG_PARAM, "查询失败");
		}
	}
	
	//根据属性名称模糊查询属性
	public ServerResponse<PageInfo>  queryGoodsAttributelikeName(Integer pageNum, Integer pageSize,String name) {
		String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class) + configUtil.getValue(SysConfig.PUBLIC_TEMPORARY_FILE_ADDRESS, String.class);
		try{
			if (pageNum == null) {
				pageNum = 1;
			}
			if (pageSize == null) {
				pageSize = 10;
			}
			PageHelper.startPage(pageNum, pageSize);
			List<GoodsAttribute> caList= new ArrayList<GoodsAttribute>();
			List<Map<String, Object>> rListMap=new ArrayList<Map<String,Object>>();
			if(name==null||"".equals(name)){
				caList=iGoodsAttributeMapper.query();
			}else{
				caList=iGoodsAttributeMapper.queryGoodsAttributelikeName(name);
			}
			for(GoodsAttribute ca:caList){
				Map<String, Object> caMap=new HashMap<String, Object>();
				caMap.put("id", ca.getId());
				caMap.put("name", ca.getName());
				caMap.put("type", ca.getType());
				SimpleDateFormat  sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				caMap.put("createDate", sdf.format(ca.getCreateDate()));
				caMap.put("modifyDate", sdf.format(ca.getModifyDate()));
				List<AttributeValue> avList=iAttributeValueMapper.queryByAttributeId(ca.getId());
				List<Map<String, Object>> avListMap=new ArrayList<Map<String,Object>>();
				for(AttributeValue av:avList){
					Map<String, Object> avMap=new HashMap<String, Object>();
					avMap.put("avId", av.getId());
					avMap.put("avName", av.getName());
					avMap.put("image", address+av.getImage());
					avListMap.add(avMap);
				}
				caMap.put("avListMap", avListMap);
				rListMap.add(caMap);
			}
			PageInfo pageResult = new PageInfo(caList);
			pageResult.setList(rListMap);
			return ServerResponse.createBySuccess("查询成功",pageResult);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BaseException(ServerCode.WRONG_PARAM, "查询失败");
		}
	}
	
	//根据属性id查询属性及其所有关联属性选项
	public ServerResponse queryAttributeValue(String id) {
		String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class) + configUtil.getValue(SysConfig.PUBLIC_TEMPORARY_FILE_ADDRESS, String.class);
		try{
			Map<String, Object> gaMap=new HashMap<String, Object>();
			GoodsAttribute goodsAttribute =iGoodsAttributeMapper.queryById(id);
			if(goodsAttribute!=null){
				gaMap.put("id", goodsAttribute.getId());
				gaMap.put("name", goodsAttribute.getName());
				gaMap.put("type", goodsAttribute.getType());
				List<AttributeValue> avList=iAttributeValueMapper.queryByAttributeId(goodsAttribute.getId());
				List<Map<String, Object>> avListMap=new ArrayList<Map<String,Object>>();
				for(AttributeValue av:avList){
					Map<String, Object> avMap=new HashMap<String, Object>();
					avMap.put("avId", av.getId());
					avMap.put("avName", av.getName());
					avMap.put("image", address+av.getImage());
					avListMap.add(avMap);
				}
				gaMap.put("avListMap", avListMap);
			}
			return ServerResponse.createBySuccess("查询成功",gaMap);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BaseException(ServerCode.WRONG_PARAM, "查询失败");
		}
	}

	//新增属性及其属性选项
	public ServerResponse insertGoodsAttribute(String goodsCategoryId,String attributeName,Integer type,String jsonStr) {
		try{
			JSONArray jsonArr=JSONArray.parseArray(jsonStr);
			GoodsAttribute goodsAttribute=new GoodsAttribute();
			goodsAttribute.setName(attributeName);
			goodsAttribute.setType(type);
			goodsAttribute.setCreateDate(new Date());
			goodsAttribute.setModifyDate(new Date());
			iGoodsAttributeMapper.insert(goodsAttribute);
			//保存商品类别和属性的关联
			CategoryAttribute ca=new CategoryAttribute();
			ca.setCategoryId(goodsCategoryId);
			ca.setAttributeId(goodsAttribute.getId());
			ca.setCreateDate(new Date());
			ca.setModifyDate(new Date());
			iGoodsAttributeMapper.insertCategoryAttribute(ca);
			for(int i=0;i<jsonArr.size();i++){
				JSONObject obj=jsonArr.getJSONObject(i);
				AttributeValue attributeValue=new AttributeValue();
				attributeValue.setAttributeId(goodsAttribute.getId());
				attributeValue.setName(obj.getString("name"));
				if(type==1) {//是规格属性不用存图
					int first4 = obj.getString("image").indexOf("/20");
					String image = "";
					if (first4 >= 0) {
						image = obj.getString("image").substring(first4);
					}
					attributeValue.setImage(image);
				}
				attributeValue.setCreateDate(new Date());
				attributeValue.setModifyDate(new Date());
				iAttributeValueMapper.insert(attributeValue);
			}
			return ServerResponse.createBySuccessMessage("新增成功");
		} catch (Exception e) {
			e.printStackTrace();
			throw new BaseException(ServerCode.WRONG_PARAM, "新增失败");
		}
	}
	
	//修改属性及其属性选项
	public ServerResponse doModifyGoodsAttribute(String attributeId,String attributeName,Integer type,String jsonStr) {
		try{
			JSONArray jsonArr=JSONArray.parseArray(jsonStr);
			GoodsAttribute goodsAttribute=new GoodsAttribute();
			goodsAttribute.setId(attributeId);
			goodsAttribute.setName(attributeName);
			goodsAttribute.setType(type);
			goodsAttribute.setModifyDate(new Date());
			iGoodsAttributeMapper.updateByPrimaryKeySelective(goodsAttribute);
			for(int i=0;i<jsonArr.size();i++) {
				JSONObject obj = jsonArr.getJSONObject(i);
				AttributeValue attributeValue = new AttributeValue();
				if (obj.getString("id") == null || "".equals(obj.getString("id"))) {//新增
					attributeValue.setAttributeId(goodsAttribute.getId());
					attributeValue.setName(obj.getString("name"));
					if (type == 1) {//是规格属性不用存图
						int first4 = obj.getString("image").indexOf("/20");
						String image = "";
						if (first4 >= 0) {
							image = obj.getString("image").substring(first4);
						}
						attributeValue.setImage(image);
					}
					attributeValue.setCreateDate(new Date());
					attributeValue.setModifyDate(new Date());
					iAttributeValueMapper.insert(attributeValue);
				} else {//修改
					attributeValue.setId(obj.getString("id"));
					attributeValue.setAttributeId(goodsAttribute.getId());
					attributeValue.setName(obj.getString("name"));
					if (type == 1) {//是规格属性不用存图
						int first4 = obj.getString("image").indexOf("/20");
						String image = "";
						if (first4 >= 0) {
							image = obj.getString("image").substring(first4);
						}
						attributeValue.setImage(image);
					}
					attributeValue.setModifyDate(new Date());
					iAttributeValueMapper.updateByPrimaryKeySelective(attributeValue);
				}
			}
			return ServerResponse.createBySuccessMessage("修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			throw new BaseException(ServerCode.WRONG_PARAM, "修改失败");
		}
	}
    /**
     * 删除商品属性
     */
	public ServerResponse deleteGoodsAttribute(String goodsAttributeId){
		try{
			iGoodsAttributeMapper.deleteById(goodsAttributeId);//删除商品属性
			iAttributeValueMapper.deleteByAttributeId(goodsAttributeId);//删除属性选项
			iGoodsAttributeMapper.deleteCategoryAttribute(goodsAttributeId);//删除商品类别和属性的关联
			return ServerResponse.createBySuccessMessage("删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			throw new BaseException(ServerCode.WRONG_PARAM, "删除失败");
		}
	}
	
	/**
     * 删除商品属性选项
     */
	public ServerResponse deleteByAttributeId(String attributeValueId){
		try{
			iAttributeValueMapper.deleteById(attributeValueId);//删除属性选项
			return ServerResponse.createBySuccessMessage("删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			throw new BaseException(ServerCode.WRONG_PARAM, "删除失败");
		}
	}

}
