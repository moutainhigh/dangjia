package com.dangjia.acg.service.basics;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.mapper.basics.IBrandMapper;
import com.dangjia.acg.mapper.basics.IBrandSeriesMapper;
import com.dangjia.acg.mapper.basics.IProductMapper;
import com.dangjia.acg.mapper.basics.IUnitMapper;
import com.dangjia.acg.modle.brand.Brand;
import com.dangjia.acg.modle.brand.BrandSeries;
import com.dangjia.acg.modle.basics.Product;
import com.dangjia.acg.modle.brand.Unit;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
/**
 * 
   * @类 名： ProductServiceImpl
   * @功能描述：  商品service实现类
   * @作者信息： zmj
   * @创建时间： 2018-9-10下午2:33:37
 */
@Service
public class ProductService {

	@Autowired
	private IProductMapper iProductMapper;
	@Autowired
	private IUnitMapper iUnitMapper;
	@Autowired
	private IBrandMapper iBrandMapper;
	@Autowired
	private IBrandSeriesMapper iBrandSeriesMapper;
	@Autowired
	private ConfigUtil configUtil;
	//查询货品
	public ServerResponse<PageInfo> queryProduct(Integer pageNum, Integer pageSize, String categoryId) {
		try{
			String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class) + configUtil.getValue(SysConfig.PUBLIC_TEMPORARY_FILE_ADDRESS, String.class);
			if(pageNum==null){
				pageNum=1;
			}
			if(pageSize==null){
				pageSize=10;
			}
			PageHelper.startPage(pageNum, pageSize);
			List<Product> productList = iProductMapper.query(categoryId);
			PageInfo pageResult = new PageInfo(productList);
			List<Product> productList2=new ArrayList<>();
			for(Product p:productList){
				String[] imgArr=p.getImage().split(",");
				String imgStr="";
				for(int i=0;i<imgArr.length;i++){
					if(i==imgArr.length-1) {
						imgStr += address+imgArr[i];
					}else{
						imgStr += address+imgArr[i]+",";
					}
				}
				p.setImage(imgStr);
				productList2.add(p);
			}
			pageResult.setList(productList2);
			return ServerResponse.createBySuccess("查询成功", pageResult);
		}catch (Exception e) {
			e.printStackTrace();
			return ServerResponse.createByErrorMessage("查询失败");
		}
	}
	
	//查询单位
	public ServerResponse queryUnit(){
		try{
			List<Unit> unitList=iUnitMapper.getUnit();
			return ServerResponse.createBySuccess("查询成功",unitList);
		}catch (Exception e) {
			e.printStackTrace();
			return ServerResponse.createByErrorMessage("查询失败");
		}
	}
	
	//查询品牌
	public ServerResponse queryBrand(){
		try{
			List<Brand> brandList=iBrandMapper.getBrands();
			return ServerResponse.createBySuccess("查询成功",brandList);
		}catch (Exception e) {
			e.printStackTrace();
			return ServerResponse.createByErrorMessage("查询失败");
		}
	}
	/**
	 * 根据品牌id查询品牌系列 
	 * @Title: queryBrandSeries
	 * @Description: TODO
	 * @param: @return   
	 * @return: JsonResult   
	 * @throws
	 */
	public ServerResponse queryBrandSeries(String brandId){
		try{
			List<BrandSeries> brandList=iBrandSeriesMapper.queryBrandSeries(brandId);
			return ServerResponse.createBySuccess("查询成功",brandList);
		}catch (Exception e) {
			e.printStackTrace();
			return ServerResponse.createByErrorMessage("查询失败");
		}
	}
    /**
     * 新增货品
     * <p>Title: insertProduct</p>
     * <p>Description: </p>
     * @param productArr
     * @return
     */
	public ServerResponse insertProduct(String productArr){
		try{
			JSONArray jsonArr=JSONArray.parseArray(productArr);
			for(int i=0;i<jsonArr.size();i++){
				JSONObject obj=jsonArr.getJSONObject(i);
				Product product=new Product();
				product.setName(obj.getString("name"));//货品品名称
				product.setCategoryId(obj.getString("categoryId"));//分类id
				product.setGoodsId(obj.getString("goodsId"));//商品id
				product.setProductSn(obj.getString("productSn"));//货品编号
				String[] imgArr=obj.getString("image").split(",");
				String imgStr="";
				for(int j=0;j<imgArr.length;j++){
					String img=imgArr[j];
					int first4 = img.indexOf("/20");
					if(j==imgArr.length-1) {
						if (first4 >= 0) {
							imgStr += img.substring(first4);
						}
					}else{
						if (first4 >= 0) {
							imgStr +=img.substring(first4)+",";
						}
					}
				}
				product.setImage(imgStr);//图片地址
				product.setUnit(obj.getString("unit"));//单位
				product.setWeight(obj.getString("weight"));//重量
				product.setConvertQuality(obj.getDouble("convertQuality"));//换算量
				product.setConvertUnit(obj.getString("convertUnit"));//换算单位
				product.setType(obj.getInteger("type"));//是否禁用0：禁用；1不禁用
				product.setMaket(obj.getInteger("maket"));//是否上架0：不上架；1：上架
				product.setCost(obj.getDouble("cost"));//平均成本价
				product.setPrice(obj.getDouble("price"));//销售价
				product.setProfit(obj.getDouble("profit"));//利润率
				product.setBrandId(obj.getString("brandId"));//品牌id
				product.setBrandSeriesId(obj.getString("brandSeriesId"));//品牌系列id
				String s= obj.getString("attributeNameArr");
				product.setAttributeNameArr(obj.getString("attributeNameArr"));//选中的属性选项名称字符串
				product.setAttributeIdArr(obj.getString("attributeIdArr"));//选中的属性选项名称字符串
				product.setCreateDate(new Date());
				product.setModifyDate(new Date());
				iProductMapper.insert(product);
			}
			return ServerResponse.createBySuccessMessage("新增成功");
		}catch (Exception e) {
			e.printStackTrace();
			return ServerResponse.createByErrorMessage("新增失败");
		}
	}
	
	/**
	 * 修改货品
	 * <p>Title: updateProduct</p>
	 * <p>Description: </p>
	 * @param productArr
	 * @return
	 */
	public ServerResponse updateProduct(String productArr){
		try{
			JSONArray jsonArr=JSONArray.parseArray(productArr);
			for(int i=0;i<jsonArr.size();i++){
				JSONObject obj=jsonArr.getJSONObject(i);
				Product product=new Product();
				product.setId(obj.getString("id"));
				product.setName(obj.getString("name"));//货品品名称
				product.setCategoryId(obj.getString("categoryId"));//分类id
				product.setGoodsId(obj.getString("goodsId"));//商品id
				product.setProductSn(obj.getString("productSn"));//货品编号
				String[] imgArr=obj.getString("image").split(",");
				String imgStr="";
				for(int j=0;j<imgArr.length;j++){
					String img=imgArr[j];
					int first4 = img.indexOf("/20");
					if(j==imgArr.length-1) {
						if (first4 >= 0) {
							imgStr += img.substring(first4);
						}
					}else{
						if (first4 >= 0) {
							imgStr +=img.substring(first4)+",";
						}
					}
				}
				product.setImage(imgStr);//图片地址
				product.setUnit(obj.getString("unit"));//单位
				product.setWeight(obj.getString("weight"));//重量
				product.setConvertQuality(obj.getDouble("convertQuality"));//换算量
				product.setConvertUnit(obj.getString("convertUnit"));//换算单位
				product.setType(obj.getInteger("type"));//是否禁用0：禁用；1不禁用
				product.setMaket(obj.getInteger("maket"));//是否上架0：不上架；1：上架
				product.setCost(obj.getDouble("cost"));//平均成本价
				product.setPrice(obj.getDouble("price"));//销售价
				product.setProfit(obj.getDouble("profit"));//利润率
				product.setBrandId(obj.getString("brand_id"));//品牌id
				product.setBrandSeriesId(obj.getString("brandSeriesId"));//品牌系列id
				product.setAttributeNameArr(obj.getString("attributeNameArr"));//选中的属性选项名称字符串
				product.setAttributeIdArr(obj.getString("attributeIdArr"));//选中的属性选项名称字符串
				product.setModifyDate(new Date());
				int p = iProductMapper.updateByPrimaryKeySelective(product);
			}
			return ServerResponse.createBySuccessMessage("修改成功");
		}catch (Exception e) {
			e.printStackTrace();
			return ServerResponse.createByErrorMessage("修改失败");
		}
	}
}
