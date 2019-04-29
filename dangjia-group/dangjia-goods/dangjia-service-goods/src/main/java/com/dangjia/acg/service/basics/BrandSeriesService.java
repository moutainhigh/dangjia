package com.dangjia.acg.service.basics;

import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.BaseException;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.mapper.basics.IBrandSeriesMapper;
import com.dangjia.acg.modle.brand.BrandSeries;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 
 * 
   * @类 名： BrandExplainServiceImpl.java
   * @功能描述：  
   * @作者信息： hb
   * @创建时间： 2018-9-13下午4:48:04
 */
@Service
public class BrandSeriesService{
	/**
	 * 注入BrandExplain接口
	 */
	@Autowired
	private IBrandSeriesMapper iBrandSeriesMapper;
	@Autowired
	private ConfigUtil configUtil;
	//查询所有
	public ServerResponse<PageInfo> getAllBrandExplain(PageDTO pageDTO) {
		PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
		try {
			String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
			List<Map<String, Object>> mapList=new ArrayList<>();
			List<BrandSeries> BrandExList = iBrandSeriesMapper.queryBrandSeries(null);
			for (BrandSeries brandExplain : BrandExList) {
				Map<String, Object> map=BeanUtils.beanToMap(brandExplain);
				map.put("image",address+brandExplain.getImage());
				map.put("imageUrl",brandExplain.getImage());
				mapList.add(map);
			}
			PageInfo pageResult = new PageInfo(BrandExList);
			pageResult.setList(mapList);
			return ServerResponse.createBySuccess("查询成功",pageResult);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BaseException(ServerCode.WRONG_PARAM, "查询失败");
		}
	}
	//修改
	public ServerResponse update(String id,String name,String content ){
		try {
			BrandSeries brandEx = new BrandSeries();
			brandEx.setId(id);
			brandEx.setName(name);
			brandEx.setContent(content);
			brandEx.setModifyDate(new Date());
			iBrandSeriesMapper.updateByPrimaryKeySelective(brandEx);
			return ServerResponse.createBySuccessMessage("修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			throw new BaseException(ServerCode.WRONG_PARAM, "修改失败");
		}
		
	}
	//新增
	public ServerResponse insert(String name,String content,String brandId){
		try {
			BrandSeries brandEx = new BrandSeries();
			brandEx.setName(name);
			brandEx.setContent(content);
			brandEx.setBrandId(brandId);
			iBrandSeriesMapper.insert(brandEx);
			return ServerResponse.createBySuccessMessage("新增成功");
		} catch (Exception e) {
			e.printStackTrace();
			throw new BaseException(ServerCode.WRONG_PARAM, "新增失败");
		}
	}

	//删除
	public ServerResponse deleteBrandExplain(String id){
		try {
			iBrandSeriesMapper.deleteByPrimaryKey(id);
			return ServerResponse.createBySuccessMessage("删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			throw new BaseException(ServerCode.WRONG_PARAM, "删除失败");
		}
	}
}
