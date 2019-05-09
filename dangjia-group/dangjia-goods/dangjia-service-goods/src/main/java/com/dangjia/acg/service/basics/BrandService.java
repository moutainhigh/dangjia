package com.dangjia.acg.service.basics;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.product.MasterProductAPI;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.BaseException;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.mapper.basics.IBrandMapper;
import com.dangjia.acg.mapper.basics.IBrandSeriesMapper;
import com.dangjia.acg.mapper.basics.IProductMapper;
import com.dangjia.acg.modle.basics.Product;
import com.dangjia.acg.modle.brand.Brand;
import com.dangjia.acg.modle.brand.BrandSeries;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 * @类 名： BrandServiceImpl.java
 * @功能描述： 商品品牌Service实现类
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
    @Autowired
    private IProductMapper iProductMapper;
    @Autowired
    private ProductService productService;
    @Autowired
    private MasterProductAPI masterProductAPI;

    //查询所有品牌
    public ServerResponse<PageInfo> getAllBrand(PageDTO pageDTO) {
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        try {
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            List<Brand> Brandlist = iBrandMapper.getBrands();
            List<Map<String, Object>> list = new ArrayList<>();
            for (Brand brand : Brandlist) {
                Map<String, Object> obj = new HashMap<>();
                Map<String, Object> map = new HashMap<>();
                map.put("id", brand.getId());
                map.put("name", brand.getName());
                map.put("createDate", brand.getCreateDate().getTime());
                map.put("modifyDate", brand.getModifyDate().getTime());
                List<BrandSeries> mapList = iBrandSeriesMapper.queryBrandSeries(brand.getId());
                List<Map<String, Object>> mapList2 = new ArrayList<>();
                for (BrandSeries bs : mapList) {
//					String imageUrl=bs.getImage();
                    String[] imgArr = bs.getImage().split(",");
                    String imgStr = "";
                    String imgUrlStr = "";
                    for (int i = 0; i < imgArr.length; i++) {
                        if (i == imgArr.length - 1) {
                            imgStr += address + imgArr[i];
                            imgUrlStr += imgArr[i];
                        } else {
                            imgStr += address + imgArr[i] + ",";
                            imgUrlStr += imgArr[i] + ",";
                        }
                    }
//					bs.setImage(address+bs.getImage());
                    bs.setImage(imgStr);
                    Map<String, Object> mapSeries = BeanUtils.beanToMap(bs);
//					mapSeries.put("imageUrl",imageUrl);
                    mapSeries.put("imageUrl", imgUrlStr);
                    mapList2.add(mapSeries);
                }
                obj.put("mapList", mapList2);
                obj.put("brand", map);
                list.add(obj);
            }
            PageInfo pageResult = new PageInfo(Brandlist);
            pageResult.setList(list);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(ServerCode.WRONG_PARAM, "查询失败");
        }
    }

    //根据Id查询品牌
    public ServerResponse select(String brandId) {
        try {
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            Map<String, Object> obj = new HashMap<String, Object>();
            Brand brand = iBrandMapper.selectByPrimaryKey(brandId);
            List<BrandSeries> mapList = iBrandSeriesMapper.queryBrandSeries(brand.getId());
            List<Map<String, Object>> mapList2 = new ArrayList<>();
            for (BrandSeries bs : mapList) {
                String imageUrl = bs.getImage();
                bs.setImage(address + bs.getImage());
                Map<String, Object> mapSeries = BeanUtils.beanToMap(bs);
                mapSeries.put("imageUrl", imageUrl);
                mapList2.add(mapSeries);
            }
            obj.put("brand", brand);
            obj.put("mapList", mapList2);
            return ServerResponse.createBySuccess("查询成功", obj);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(ServerCode.WRONG_PARAM, "查询失败");
        }
    }

    //修改品牌
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse update(String id, String name, String brandSeriesList){
        try {
            List<Brand> brList = iBrandMapper.getBrandByName(name);
            Brand brand1 = iBrandMapper.selectByPrimaryKey(id);
            if (brList.size() == 1) {
                Brand br = brList.get(0);
                if (br != null && !br.getId().equals(id)) {
                    return ServerResponse.createByErrorMessage("品牌名称重复");
                }
            } else if (brList.size() > 1) {
                return ServerResponse.createByErrorMessage("品牌名称存在重复数据:" + name);
            }
            Brand brand = new Brand();
            brand.setId(id);
            brand.setName(name);
            brand.setModifyDate(new Date());
            iBrandMapper.updateByPrimaryKeySelective(brand);
            //修改品牌对应的product名称也更新
            Example example=new Example(Product.class);
            example.createCriteria().andEqualTo(Product.BRAND_ID,id);
            List<Product> products = iProductMapper.selectByExample(example);
            if(products.size()>0||null!=products) {
                for (Product product : products) {
                    product.setName(product.getName().replace(brand1.getName(), name));
                    //调用product相关联的表更新
                    productService.updateProductByProductId(product);
                    masterProductAPI.updateProductByProductId(product.getId(),product.getCategoryId(),
                            product.getBrandSeriesId(),product.getBrandId(),product.getName(),product.getUnitId(),product.getUnitName());
                }
            }
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
                if (image != null && !"".equals(image)) {
                    bSeries.setImage(image);
                }
                bSeries.setModifyDate(new Date());
                if (brandSeriesId == null || "".equals(brandSeriesId)) {
                    iBrandSeriesMapper.insert(bSeries);
                } else {
                    bSeries.setId(brandSeriesId);
                    iBrandSeriesMapper.updateByPrimaryKeySelective(bSeries);
                    for (Product product : products) {
                        productService.updateProductByProductId(product);
                        masterProductAPI.updateProductByProductId(product.getId(),product.getCategoryId(),product.getBrandSeriesId()
                                ,product.getBrandId(),product.getName(),product.getUnitId(),product.getUnitName());
                    }
                }
            }
            return ServerResponse.createBySuccessMessage("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(ServerCode.WRONG_PARAM, "修改失败");
        }
    }

    //新增品牌
    public ServerResponse insert(String brandSeriesList, String name) {
        try {
            Example example = new Example(Brand.class);
            example.createCriteria().andEqualTo("name", name);
            List<Brand> bList = iBrandMapper.selectByExample(example);
            if (bList != null && bList.size() > 0) {
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
    public ServerResponse<PageInfo> getBrandByName(PageDTO pageDTO, String name) {
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        try {
            List<Brand> Brandlist = iBrandMapper.getBrandByNames(name);
            List<Map<String, Object>> list = new ArrayList<>();
            for (Brand brand : Brandlist) {
                List<BrandSeries> mapList = iBrandSeriesMapper.queryBrandSeries(brand.getId());
                String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
                List<Map<String, Object>> mapList2 = new ArrayList<>();
                for (BrandSeries bs : mapList) {
                    String imgStr = "";
                    String imgUrlStr = "";
                    if (!CommonUtil.isEmpty(bs.getImage())) {
                        String[] imgArr = bs.getImage().split(",");
                        for (int i = 0; i < imgArr.length; i++) {
                            if (i == imgArr.length - 1) {
                                imgStr += address + imgArr[i];
                                imgUrlStr += imgArr[i];
                            } else {
                                imgStr += address + imgArr[i] + ",";
                                imgUrlStr += imgArr[i] + ",";
                            }
                        }
                    }
                    bs.setImage(imgStr);
                    Map<String, Object> mapSeries = BeanUtils.beanToMap(bs);
//					mapSeries.put("imageUrl",imageUrl);
                    mapSeries.put("imageUrl", imgUrlStr);
                    mapList2.add(mapSeries);
                }
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("brand", brand);
                map.put("mapList", mapList2);
                list.add(map);
            }
            PageInfo pageResult = new PageInfo(Brandlist);
            pageResult.setList(list);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(ServerCode.WRONG_PARAM, "查询失败");
        }
    }

    //根据Id删除品牌
    public ServerResponse deleteBrand(String brandId) {
        try {
            iBrandMapper.deleteById(brandId);
            return ServerResponse.createBySuccessMessage("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(ServerCode.WRONG_PARAM, "删除失败");
        }
    }
}
