package com.dangjia.acg.service.basics;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.mapper.basics.IGoodsMapper;
import com.dangjia.acg.mapper.basics.ILabelMapper;
import com.dangjia.acg.mapper.basics.IProductMapper;
import com.dangjia.acg.modle.basics.Goods;
import com.dangjia.acg.modle.basics.Label;
import com.dangjia.acg.modle.basics.Product;
import com.dangjia.acg.modle.brand.Brand;
import com.dangjia.acg.modle.brand.BrandSeries;
import com.dangjia.acg.modle.brand.GoodsSeries;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 商品业务层
 *
 * @ClassName: GoodsServiceImpl
 * @Description: TODO
 * @author: zmj
 * @date: 2018-9-20下午2:44:47
 */
@Service
public class GoodsService {

    @Autowired
    private IGoodsMapper iGoodsMapper;
    @Autowired
    private IProductMapper iProductMapper;
    @Autowired
    private ILabelMapper iLabelMapper;
    @Autowired
    private ConfigUtil configUtil;

    /**
     * 保存商品
     * <p>Title: saveGoods</p>
     * <p>Description: </p>
     *
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
                                    Integer sales, String unitId, Integer type, String arrString) {
        try {
            Goods goods = new Goods();
            goods.setName(name);
            goods.setCategoryId(categoryId);//分类
            goods.setBuy(buy);//购买性质
            goods.setSales(sales);//退货性质
            goods.setUnitId(unitId);//单位
            goods.setType(type);//商品性质
            goods.setCreateDate(new Date());
            goods.setModifyDate(new Date());
            iGoodsMapper.insert(goods);
            JSONArray arr = JSONArray.parseArray(arrString);
            for (int i = 0; i < arr.size(); i++) {//新增商品关联品牌系列
                JSONObject obj = arr.getJSONObject(i);
                GoodsSeries gs = new GoodsSeries();
                gs.setGoodsId(goods.getId());
                gs.setBrandId(obj.getString("brandId"));
                gs.setSeriesId(obj.getString("seriesId"));
                gs.setCreateDate(new Date());
                gs.setModifyDate(new Date());
                iGoodsMapper.insertGoodsSeries(gs);
            }
            return ServerResponse.createBySuccess("新增成功", goods.getId());
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("新增失败");
        }
    }

    /**
     * 根据商品id查询关联品牌
     * <p>Title: queryBrandByGid</p>
     * <p>Description: </p>
     *
     * @param goodsId
     * @return
     */
    public ServerResponse queryBrandByGid(String goodsId) {
        try {
            List<Brand> bList = iGoodsMapper.queryBrandByGid(goodsId);
            return ServerResponse.createBySuccess("查询成功", bList);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 根据商品id和品牌id查询关联品牌系列
     * <p>Title: queryBrandByGid</p>
     * <p>Description: </p>
     *
     * @param goodsId
     * @return
     */
    public ServerResponse queryBrandByGidAndBid(String goodsId, String brandId) {
        try {
            List<BrandSeries> bList = iGoodsMapper.queryBrandByGidAndBid(goodsId, brandId);
            return ServerResponse.createBySuccess("查询成功", bList);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 根据商品id查询对应商品
     * <p>Title: getGoodsByGid</p>
     * <p>Description: </p>
     *
     * @param goodsId
     * @return
     */
    public ServerResponse getGoodsByGid(String goodsId) {
        try {
            Goods goods = iGoodsMapper.queryById(goodsId);
            return ServerResponse.createBySuccess("查询成功", goods);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");

        }
    }

    /**
     * 修改商品
     * <p>Title: updateGoods</p>
     * <p>Description: </p>
     *
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
    public ServerResponse updateGoods(String id, String name, String categoryId, Integer buy,
                                      Integer sales, String unitId, Integer type, String arrString) {
        try {
            Goods goods = new Goods();
            goods.setId(id);
            goods.setName(name);
            goods.setCategoryId(categoryId);//分类
            goods.setBuy(buy);//购买性质
            goods.setSales(sales);//退货性质
            goods.setUnitId(unitId);//单位
            goods.setType(type);//商品性质
            goods.setModifyDate(new Date());
            iGoodsMapper.updateByPrimaryKeySelective(goods);
            JSONArray arr = JSONArray.parseArray(arrString);
            iGoodsMapper.deleteGoodsSeries(id);//先删除商品所有跟品牌关联
            for (int i = 0; i < arr.size(); i++) {//新增商品关联品牌系列
                JSONObject obj = arr.getJSONObject(i);
                GoodsSeries gs = new GoodsSeries();
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
            return ServerResponse.createByErrorMessage("修改失败");

        }
    }

    /**
     * 根据id删除商品和下属货品
     *
     * @param id
     * @return
     */
    public ServerResponse deleteGoods(String id) {
        try {
            if (true)
                return ServerResponse.createByErrorMessage("不能执行删除操作");
            iGoodsMapper.deleteByPrimaryKey(id);
            Example example = new Example(Product.class);
            example.createCriteria().andEqualTo("goodsId", id);
            iProductMapper.deleteByExample(example);
            return ServerResponse.createBySuccessMessage("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("删除失败");
        }
    }


    /**
     * 查询商品及下属货品
     *
     * @param pageNum
     * @param pageSize
     * @param categoryId
     * @param name
     * @return
     */
    public ServerResponse queryGoodsListByCategoryLikeName(Integer pageNum, Integer pageSize, String categoryId, String name) {
        try {
            if (pageNum == null) {
                pageNum = 1;
            }
            if (pageSize == null) {
                pageSize = 10;
            }
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            PageHelper.startPage(pageNum, pageSize);
            List<Goods> goodsList = iGoodsMapper.queryGoodsList(categoryId, name);
            List<Map<String, Object>> gMapList = new ArrayList<>();
            for (Goods goods : goodsList) {
                Map<String, Object> gMap = CommonUtil.beanToMap(goods);
                List<Product> productList = iProductMapper.queryByGoodsId(goods.getId());
                List<Map<String, Object>> mapList = new ArrayList<>();
                for (Product p : productList) {
                    if (p.getImage() == null) {
                        continue;
                    }
                    String[] imgArr = p.getImage().split(",");
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
                    p.setImage(imgStr);
                    Map<String, Object> map = CommonUtil.beanToMap(p);
                    map.put("imageUrl", imgUrlStr);
                    if (p.getLabelId() == null) {
                        map.put("labelId", "");
                        map.put("labelName", "");
                    } else {
                        map.put("labelId", p.getLabelId());
                        Label label = iLabelMapper.selectByPrimaryKey(p.getLabelId());
                        if (label.getName() != null)
                            map.put("labelName", label.getName());
                    }
                    mapList.add(map);
                }
                gMap.put("productList", mapList);
                gMapList.add(gMap);
            }
            PageInfo pageResult = new PageInfo(goodsList);
            pageResult.setList(gMapList);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 查询商品及下属货品
     *
     * @param pageDTO
     * @param categoryId
     * @param name
     * @return
     */
    public ServerResponse queryGoodsList(PageDTO pageDTO, String categoryId, String name) {
        try {
            if (pageDTO.getPageNum() == null) {
                pageDTO.setPageNum(1);
            }
            if (pageDTO.getPageSize() == null) {
                pageDTO.setPageSize(10);
            }
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<Goods> goodsList = iGoodsMapper.queryGoodsList(categoryId, name);
            List<Map<String, Object>> gMapList = new ArrayList<>();
            for (Goods goods : goodsList) {
                Map<String, Object> gMap = CommonUtil.beanToMap(goods);
                List<Product> productList = iProductMapper.queryByGoodsId(goods.getId());
                List<Map<String, Object>> mapList = new ArrayList<>();
                for (Product p : productList) {
                    if (p.getImage() == null) {
                        continue;
                    }
                    String[] imgArr = p.getImage().split(",");
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
                    p.setImage(imgStr);
                    Map<String, Object> map = CommonUtil.beanToMap(p);
                    map.put("imageUrl", imgUrlStr);
                    if (p.getLabelId() == null) {
                        map.put("labelId", "");
                        map.put("labelName", "");
                    } else {
                        map.put("labelId", p.getLabelId());
                        Label label = iLabelMapper.selectByPrimaryKey(p.getLabelId());
                        if (label.getName() != null)
                            map.put("labelName", label.getName());
                    }
                    mapList.add(map);
                }
                gMap.put("productList", mapList);
                gMapList.add(gMap);
            }
            PageInfo pageResult = new PageInfo(goodsList);
            pageResult.setList(gMapList);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 模糊查询商品及下属货品
     *
     * @param pageDTO
     * @param categoryId
     * @param name
     * @return
     */
    public ServerResponse queryGoodsListByCategoryLikeName(PageDTO pageDTO, String categoryId, String name) {
        try {
            if (pageDTO.getPageNum() == null) {
                pageDTO.setPageNum(1);
            }
            if (pageDTO.getPageSize() == null) {
                pageDTO.setPageSize(10);
            }
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<Goods> goodsList = iGoodsMapper.queryGoodsListByCategoryLikeName(categoryId, name);
            List<Map<String, Object>> gMapList = new ArrayList<>();
            for (Goods goods : goodsList) {
                Map<String, Object> gMap = CommonUtil.beanToMap(goods);
                List<Product> productList = iProductMapper.queryByGoodsId(goods.getId());
                List<Map<String, Object>> mapList = new ArrayList<>();
                for (Product p : productList) {
                    if (p.getImage() == null) {
                        continue;
                    }
                    String[] imgArr = p.getImage().split(",");
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
                    p.setImage(imgStr);
                    Map<String, Object> map = CommonUtil.beanToMap(p);
                    map.put("imageUrl", imgUrlStr);
                    if (p.getLabelId() == null) {
                        map.put("labelId", "");
                        map.put("labelName", "");
                    } else {
                        map.put("labelId", p.getLabelId());
                        Label label = iLabelMapper.selectByPrimaryKey(p.getLabelId());
                        if (label.getName() != null)
                            map.put("labelName", label.getName());
                    }
                    mapList.add(map);
                }
                gMap.put("productList", mapList);
                gMapList.add(gMap);
            }
            PageInfo pageResult = new PageInfo(goodsList);
            pageResult.setList(gMapList);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

}
