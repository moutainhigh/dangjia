package com.dangjia.acg.service.basics;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.data.WorkerTypeAPI;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.mapper.actuary.ISearchBoxMapper;
import com.dangjia.acg.mapper.basics.IProductMapper;
import com.dangjia.acg.mapper.basics.ITechnologyMapper;
import com.dangjia.acg.mapper.basics.IWorkerGoodsMapper;
import com.dangjia.acg.modle.actuary.SearchBox;
import com.dangjia.acg.modle.basics.Product;
import com.dangjia.acg.modle.basics.Technology;
import com.dangjia.acg.modle.basics.WorkerTechnology;
import com.dangjia.acg.modle.basics.WorkerGoods;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 * 
   * @类 名： TechnologyServiceImpl
   * @功能描述：  工艺service实现类
   * @作者信息： zmj
   * @创建时间： 2018-9-10下午2:33:37
 */
@Service
public class TechnologyService {

	@Autowired
	private ITechnologyMapper iTechnologyMapper;
	@Autowired
	private IWorkerGoodsMapper iWorkerGoodsMapper;
	@Autowired
	private IProductMapper iProductMapper;
    @Autowired
	private WorkerTypeAPI workerTypeAPI;
	@Autowired
	private ConfigUtil configUtil;
	@Autowired
	private ISearchBoxMapper searchBoxMapper;
	//新增工艺说明
	public ServerResponse insertTechnology(String name,String content,String workerTypeId,Integer type,String image,Integer materialOrWorker) {
		try{
			Technology t=new Technology();
			t.setName(name);
			t.setCreateDate(new Date());
			t.setModifyDate(new Date());
			t.setContent(content);
			String[] imgArr=image.split(",");
			String imgStr="";
			for(int i=0;i<imgArr.length;i++){
				String img=imgArr[i];
				int first4 = img.indexOf("/20");
				if(i==imgArr.length-1) {
					if (first4 >= 0) {
						imgStr += img.substring(first4);
					}
				}else{
					if (first4 >= 0) {
						imgStr +=img.substring(first4)+",";
					}
				}
			}
			t.setImage(imgStr);
			if(materialOrWorker==1){
				t.setWorkerTypeId(workerTypeId);
				t.setType(type);
				t.setMaterialOrWorker(1);
			}else{
				t.setType(1);
				t.setMaterialOrWorker(0);
			}
			iTechnologyMapper.insertSelective(t);
			return ServerResponse.createBySuccessMessage("新增成功");
		}catch (Exception e) {
			e.printStackTrace();
			return ServerResponse.createByErrorMessage("新增失败");
		}
	}
	
	//修改工艺说明
	public ServerResponse updateTechnology(String id,String name,String content,Integer type,String image) {
		try{
			Technology t=iTechnologyMapper.selectByPrimaryKey(id);
			if(t==null){
				return ServerResponse.createByErrorMessage("不存在此工艺,修改失败");
			}
			t.setId(id);
			t.setName(name);
			t.setModifyDate(new Date());
			t.setContent(content);
			t.setType(type);
			String[] imgArr=image.split(",");
			String imgStr="";
			for(int i=0;i<imgArr.length;i++){
				String img=imgArr[i];
				int first4 = img.indexOf("/20");
				if(i==imgArr.length-1) {
					if (first4 >= 0) {
						imgStr += img.substring(first4);
					}
				}else{
					if (first4 >= 0) {
						imgStr +=img.substring(first4)+",";
					}
				}
			}
			t.setImage(imgStr);
			if(t.getMaterialOrWorker()==1){
				t.setType(type);
			}else{
				t.setType(1);
			}
			iTechnologyMapper.updateByPrimaryKeySelective(t);
			return ServerResponse.createBySuccessMessage("修改成功");
		}catch (Exception e) {
			e.printStackTrace();
			return ServerResponse.createByErrorMessage("修改失败");
		}
	}
	
	//删除工艺说明
	public ServerResponse deleteTechnology(String id) {
		try{
			iTechnologyMapper.deleteById(id);
			return ServerResponse.createBySuccessMessage("删除成功");
		}catch (Exception e) {
			e.printStackTrace();
			return ServerResponse.createByErrorMessage("删除失败");
		}
	}
	
	//根据工种查询所有工艺说明
	public ServerResponse queryTechnology(Integer pageNum, Integer pageSize,String workerTypeId,String name,Integer materialOrWorker) {
		try{
			if (pageNum == null) {
				pageNum = 1;
			}
			if (pageSize == null) {
				pageSize = 10;
			}
			String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class) + configUtil.getValue(SysConfig.PUBLIC_TEMPORARY_FILE_ADDRESS, String.class);
			PageHelper.startPage(pageNum, pageSize);
			List<Technology> tList=iTechnologyMapper.query(workerTypeId,name,materialOrWorker);
			List<Map<String,Object>> mapList=new ArrayList<>();
			for(Technology t:tList){
			    Map<String,Object> map = new HashMap<>();
			    map.put("id",t.getId());
                map.put("name",t.getName());
                map.put("workerTypeId",t.getWorkerTypeId());
				ServerResponse serverResponse =workerTypeAPI.getNameByWorkerTypeId(t.getWorkerTypeId());
				String  workerTypeName="";
				if(serverResponse.isSuccess()) {
					workerTypeName = serverResponse.getResultObj().toString();
				}
                map.put("workerTypeName",workerTypeName);
                map.put("content",t.getContent());
				map.put("type",t.getType());
				String imgStr="";
				if(t.getImage()!=null){
					String[] imgArr=t.getImage().split(",");
					for(int i=0;i<imgArr.length;i++){
						if(i==imgArr.length-1) {
							imgStr += address+imgArr[i];
						}else{
							imgStr += address+imgArr[i]+",";
						}
					}
				}
				map.put("image",imgStr);
				map.put("materialOrWorker",t.getMaterialOrWorker());
                mapList.add(map);
            }
			PageInfo pageResult = new PageInfo(tList);
			pageResult.setList(mapList);
			return ServerResponse.createBySuccess("查询成功",pageResult);
		}catch (Exception e) {
				e.printStackTrace();
				return ServerResponse.createByErrorMessage("查询失败");
		}
	}
	
	//新增人工商品关联工艺
	public ServerResponse insertWokerTechnology(String workerGoodsId,String tIdArr) {
		try{
			JSONArray arr=JSONArray.parseArray(JSON.toJSONString(tIdArr));
			for(int i=0;i<arr.size();i++){
				JSONObject obj=arr.getJSONObject(i);
				WorkerTechnology wt=new WorkerTechnology();
				wt.setWorkerGoodsId(workerGoodsId);
				wt.setTechnologyId(obj.getString("technologyId"));
				wt.setCreateDate(new Date());
				wt.setModifyDate(new Date());
				iTechnologyMapper.insertWokerTechnology(wt);
			}
			return ServerResponse.createBySuccessMessage("新增成功");
		}catch (Exception e) {
			e.printStackTrace();
			return ServerResponse.createByErrorMessage("新增失败");
		}
	}
	
	//根据商品id查询人工商品关联工艺实体
	public ServerResponse queryTechnologyByWgId(String workerGoodsId) {
		try{
			String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class) + configUtil.getValue(SysConfig.PUBLIC_TEMPORARY_FILE_ADDRESS, String.class);

			List<Technology> tList=iTechnologyMapper.queryTechnologyByWgId(workerGoodsId);
			List<Map<String,Object>> mapList=new ArrayList<>();
			for(Technology t:tList){
				Map<String,Object> map = new HashMap<>();
				map.put("id",t.getId());
				map.put("name",t.getName());
				map.put("workerTypeId",t.getWorkerTypeId());
				map.put("content",t.getContent());
				map.put("type",t.getType());
				String imgStr="";
				if(t.getImage()!=null){
					String[] imgArr=t.getImage().split(",");
					for(int i=0;i<imgArr.length;i++){
						if(i==imgArr.length-1) {
							imgStr += address+imgArr[i];
						}else{
							imgStr += address+imgArr[i]+",";
						}
					}
				}
				map.put("image",imgStr);
				map.put("materialOrWorker",t.getMaterialOrWorker());
				mapList.add(map);
			}
			return ServerResponse.createBySuccess("新增成功",mapList);
		}catch (Exception e) {
			e.printStackTrace();
			return ServerResponse.createByErrorMessage("新增失败");
		}
	}
	
	//根据内容模糊搜索
	public ServerResponse queryByName(String name) {
		JSONArray arr=new JSONArray();
		try{
			Example example = new Example(SearchBox.class);
			example.createCriteria().andEqualTo("content", name);
			List<SearchBox> slist=searchBoxMapper.selectByExample(example);
			if(slist.size()>0){
				SearchBox s=slist.get(0);
				s.setNumber(s.getNumber()+1);
				s.setModifyDate(new Date());
				searchBoxMapper.updateByPrimaryKeySelective(s);
			}else{
				SearchBox serchBox = new SearchBox();
				serchBox.setContent(name);
				serchBox.setNumber(1);
				searchBoxMapper.insertSelective(serchBox);
			}
			//根据内容模糊搜索工艺
			List<Technology> tList=iTechnologyMapper.queryByName(name);
			for(Technology t:tList){
				JSONObject object = new JSONObject();
				object.put("name",t.getName());
				object.put("url","www.baidu.com");//0:工艺；1：商品；2：人工
				arr.add(object);
			}
			//根据内容模糊搜索商品
			List<Product> pList=iProductMapper.queryByName(name);
			for(Product t:pList){
				JSONObject object = new JSONObject();
				object.put("name",t.getName());
				object.put("url","www.baidu.com");//0:工艺；1：商品；2：人工
				arr.add(object);
			}
			//根据内容模糊搜索人工
			List<WorkerGoods> wList=iWorkerGoodsMapper.queryByName(name);
			for(WorkerGoods t:wList){
				JSONObject object = new JSONObject();
				object.put("name",t.getName());
				object.put("url","www.baidu.com");//0:工艺；1：商品；2：人工
				arr.add(object);
			}
		}catch (Exception e) {
			e.printStackTrace();
			return ServerResponse.createByErrorMessage("查询失败");
		}
		return ServerResponse.createBySuccess("查询成功",arr);
	}

	/**
	 * 查询热门搜索
	 */
	public ServerResponse getHeatSearchBox() {
		try {
			return ServerResponse.createBySuccess("查询成功", searchBoxMapper.getHeatSearchBox());
		} catch (Exception e) {
			e.printStackTrace();
			return ServerResponse.createByErrorMessage("查询失败");
		}
	}

}
