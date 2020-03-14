package com.dangjia.acg.service.basics;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
import com.dangjia.acg.modle.brand.Brand;
import com.dangjia.acg.modle.brand.BrandSeries;
import com.dangjia.acg.util.StringTool;
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

    //查询所有品牌
    public ServerResponse<PageInfo> getAllBrand(PageDTO pageDTO,String cityId) {
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        try {
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            List<Brand> brandlist = iBrandMapper.getBrands(cityId);
            List<Map<String, Object>> list = new ArrayList<>();
            for (Brand brand : brandlist) {
                Map<String, Object> obj = new HashMap<>();
                Map<String, Object> map = new HashMap<>();
                map.put("id", brand.getId());
                map.put("name", brand.getName());
                map.put("image", brand.getImage());
                map.put("createDate", brand.getCreateDate().getTime());
                map.put("modifyDate", brand.getModifyDate().getTime());
                List<BrandSeries> mapList = iBrandSeriesMapper.queryBrandSeries(brand.getId(),cityId);
                List<Map<String, Object>> mapList2 = new ArrayList<>();
                for (BrandSeries bs : mapList) {
                    String[] imgArr = bs.getImage().split(",");
                    StringBuilder imgStr = new StringBuilder();
                    StringBuilder imgUrlStr = new StringBuilder();
                    StringTool.getImages(address, imgArr, imgStr, imgUrlStr);
                    bs.setImage(imgStr.toString());
                    Map<String, Object> mapSeries = BeanUtils.beanToMap(bs);
                    mapSeries.put("imageUrl", imgUrlStr.toString());
                    mapList2.add(mapSeries);
                }
                obj.put("mapList", mapList2);
                map.put("imageUrl",address+brand.getImage());
                obj.put("brand", map);
                list.add(obj);
            }
            PageInfo pageResult = new PageInfo(brandlist);
            pageResult.setList(list);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(ServerCode.WRONG_PARAM, "查询失败");
        }
    }

    //根据Id查询品牌
    public ServerResponse select(String brandId,String cityId) {
        try {
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            Map<String, Object> obj = new HashMap<>();
            Brand brand = iBrandMapper.selectByPrimaryKey(brandId);
            List<BrandSeries> mapList = iBrandSeriesMapper.queryBrandSeries(brand.getId(),cityId);
            List<Map<String, Object>> mapList2 = new ArrayList<>();
            for (BrandSeries bs : mapList) {
                String imageUrl = bs.getImage();
                bs.setImage(address + bs.getImage());
                Map<String, Object> mapSeries = BeanUtils.beanToMap(bs);
                mapSeries.put("imageUrl", imageUrl);
                mapList2.add(mapSeries);
            }
            Map brandMap=BeanUtils.beanToMap(brand);
            brandMap.put("imageUrl",address+brand.getImage());
            obj.put("brand", brandMap);
            obj.put("mapList", mapList2);
            return ServerResponse.createBySuccess("查询成功", obj);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(ServerCode.WRONG_PARAM, "查询失败");
        }
    }

    //修改品牌
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse update(String id, String name,String brandImage,
                                 String brandSeriesList,String cityId)throws RuntimeException{
        try {
            List<Brand> brList = iBrandMapper.getBrandByName(name,cityId);
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
            brand.setImage(brandImage);
            brand.setModifyDate(new Date());
            brand.setCityId(cityId);
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
                bSeries.setCityId(cityId);
                if (image != null && !"".equals(image)) {
                    bSeries.setImage(image);
                }
                bSeries.setModifyDate(new Date());
                if (brandSeriesId == null || "".equals(brandSeriesId)) {
                    iBrandSeriesMapper.insert(bSeries);
                } else {
                    bSeries.setId(brandSeriesId);
                    BrandSeries brandSeries1 = iBrandSeriesMapper.selectByPrimaryKey(brandSeriesId);
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
    public ServerResponse insert(String brandSeriesList, String name,String brandImage,String cityId) {
        try {
            Example example = new Example(Brand.class);
            example.createCriteria().andEqualTo("name", name);
            List<Brand> bList = iBrandMapper.selectByExample(example);
            if (bList != null && bList.size() > 0) {
                return ServerResponse.createByErrorMessage("品牌名称重复");
            }
            Brand brand = new Brand();
            brand.setCityId(cityId);
            brand.setName(name);
            brand.setImage(brandImage);
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
                bSeries.setCityId(cityId);
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
    public ServerResponse<PageInfo> getBrandByName(PageDTO pageDTO, String name,String cityId) {
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        try {
            List<Brand> Brandlist = iBrandMapper.getBrandByNames(name,cityId);
            List<Map<String, Object>> list = new ArrayList<>();
            for (Brand brand : Brandlist) {
                List<BrandSeries> mapList = iBrandSeriesMapper.queryBrandSeries(brand.getId(),cityId);
                String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
                List<Map<String, Object>> mapList2 = new ArrayList<>();
                for (BrandSeries bs : mapList) {
                    StringBuilder imgStr = new StringBuilder();
                    StringBuilder imgUrlStr = new StringBuilder();
                    if (!CommonUtil.isEmpty(bs.getImage())) {
                        String[] imgArr = bs.getImage().split(",");
                        StringTool.getImages(address, imgArr, imgStr, imgUrlStr);
                    }
                    bs.setImage(imgStr.toString());
                    Map<String, Object> mapSeries = BeanUtils.beanToMap(bs);
//					mapSeries.put("imageUrl",imageUrl);
                    mapSeries.put("imageUrl", imgUrlStr.toString());
                    mapList2.add(mapSeries);
                }
                Map<String, Object> map = new HashMap<String, Object>();
                Map brandMap=BeanUtils.beanToMap(brand);
                brandMap.put("imageUrl",address+brand.getImage());
                map.put("brand", brandMap);
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
