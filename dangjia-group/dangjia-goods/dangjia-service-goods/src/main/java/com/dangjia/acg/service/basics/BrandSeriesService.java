package com.dangjia.acg.service.basics;

import com.dangjia.acg.api.product.MasterProductAPI;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.BaseException;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.mapper.basics.IBrandSeriesMapper;
import com.dangjia.acg.mapper.basics.IProductMapper;
import com.dangjia.acg.modle.basics.Product;
import com.dangjia.acg.modle.brand.BrandSeries;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.shiro.session.mgt.SessionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
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
	@Autowired
	private IProductMapper iProductMapper;
	@Autowired
	private ProductService productService;
	@Autowired
	private MasterProductAPI masterProductAPI;
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
	@Transactional(rollbackFor = Exception.class)
	public ServerResponse update(String id,String name,String content ){
		try {
			BrandSeries brandSeries = iBrandSeriesMapper.selectByPrimaryKey(id);
			BrandSeries brandEx = new BrandSeries();
			brandEx.setId(id);
			brandEx.setName(name);
			brandEx.setContent(content);
			brandEx.setModifyDate(new Date());
			iBrandSeriesMapper.updateByPrimaryKeySelective(brandEx);
			//修改品牌系列对应的product名称也更新
			Example example=new Example(Product.class);
			example.createCriteria().andEqualTo(Product.BRAND_SERIES_ID,id);
			List<Product> products = iProductMapper.selectByExample(example);
			if(products.size()>0||null!=products) {
				for (Product product : products) {
					product.setName(product.getName().replace(brandSeries.getName(), name));
					//调用product相关联的表更新
					productService.updateProductByProductId(product);
					//master相关联表更新
					masterProductAPI.updateProductByProductId(product.getId(),product.getCategoryId(),product.getBrandSeriesId()
							,product.getBrandId(),product.getName(),product.getUnitId(),product.getUnitName());
				}
			}
			return ServerResponse.createBySuccessMessage("修改成功");
		} catch (Exception e) {
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
