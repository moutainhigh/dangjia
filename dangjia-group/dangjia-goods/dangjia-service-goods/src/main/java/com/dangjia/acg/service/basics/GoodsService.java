package com.dangjia.acg.service.basics;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.mapper.basics.*;
import com.dangjia.acg.modle.attribute.AttributeValue;
import com.dangjia.acg.modle.basics.Goods;
import com.dangjia.acg.modle.basics.Label;
import com.dangjia.acg.modle.basics.Product;
import com.dangjia.acg.modle.brand.Brand;
import com.dangjia.acg.modle.brand.BrandSeries;
import com.dangjia.acg.modle.brand.GoodsSeries;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 * goods业务层
 *
 * @ClassName: GoodsServiceImpl
 * @Description: TODO
 * @author: zmj
 * @date: 2018-9-20下午2:44:47
 */
@Service
public class GoodsService {
    private static Logger LOG = LoggerFactory.getLogger(GoodsService.class);
    @Autowired
    private IGoodsMapper iGoodsMapper;
    @Autowired
    private IGoodsSeriesMapper iGoodsSeriesMapper;
    @Autowired
    private IUnitMapper iUnitMapper;
    @Autowired
    private IAttributeValueMapper iAttributeValueMapper;
    @Autowired
    private IProductMapper iProductMapper;
    @Autowired
    private ILabelMapper iLabelMapper;
    @Autowired
    private ConfigUtil configUtil;

    /**
     * 保存goods
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
            if (!StringUtils.isNotBlank(name))
                return ServerResponse.createByErrorMessage("名字不能为空");

            List<Goods> goodsList = iGoodsMapper.queryByName(name);
            if (goodsList.size() > 0)
                return ServerResponse.createByErrorMessage("名字不能重复");

            if (!StringUtils.isNotBlank(unitId))
                return ServerResponse.createByErrorMessage("单位id不能为空");

            if (!StringUtils.isNotBlank(categoryId))
                return ServerResponse.createByErrorMessage("分类不能为空");

            if (type < -1)
                return ServerResponse.createByErrorMessage("性质不能为空");

            Goods goods = new Goods();
            goods.setName(name);
            goods.setCategoryId(categoryId);//分类
            goods.setBuy(buy);//购买性质
            goods.setSales(sales);//退货性质
            goods.setUnitId(unitId);//单位
            goods.setType(type);//goods性质
            goods.setCreateDate(new Date());
            goods.setModifyDate(new Date());
            iGoodsMapper.insert(goods);
            if (buy != 2) //非自购
            {
                if (!StringUtils.isNoneBlank(arrString)) {
                    GoodsSeries gs = new GoodsSeries();
                    gs.setGoodsId(goods.getId());
                    gs.setBrandId(null);
                    gs.setSeriesId(null);
                    iGoodsSeriesMapper.insert(gs);
                } else {
                    JSONArray arr = JSONArray.parseArray(arrString);
                    for (int i = 0; i < arr.size(); i++) {//新增goods关联品牌系列
                        JSONObject obj = arr.getJSONObject(i);
                        GoodsSeries gs = new GoodsSeries();
                        gs.setGoodsId(goods.getId());
                        if (!StringUtils.isNoneBlank(obj.getString("brandId"))) {
                            gs.setBrandId(null);
                        } else {
                            gs.setBrandId(obj.getString("brandId"));
                        }

                        if (!StringUtils.isNoneBlank(obj.getString("seriesId"))) {
                            gs.setSeriesId(null);
                        } else {
                            gs.setSeriesId(obj.getString("seriesId"));
                        }
                        iGoodsSeriesMapper.insert(gs);
                    }
                }

            }

            return ServerResponse.createBySuccess("新增成功", goods.getId());
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("新增失败");
        }
    }

    /**
     * 根据goodsid查询关联品牌
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
     * 根据goodsid和品牌id查询关联品牌系列
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
     * 根据goodsid查询对应goods
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
     * 修改goods
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
            Goods oldGoods = iGoodsMapper.selectByPrimaryKey(id);
            if (!oldGoods.getName().equals(name)) {
                List<Goods> goodsList = iGoodsMapper.queryByName(name);
                if (goodsList.size() > 0)
                    return ServerResponse.createByErrorMessage("该货品已存在");
            }

            Goods goods = new Goods();
            goods.setId(id);
            goods.setName(name);
            goods.setCategoryId(categoryId);//分类
            goods.setBuy(buy);//购买性质
            goods.setSales(sales);//退货性质
            goods.setUnitId(unitId);//单位
            goods.setType(type);//goods性质
            goods.setModifyDate(new Date());
            iGoodsMapper.updateByPrimaryKeySelective(goods);
            if (buy != 2) //非自购goods ，有品牌
            {
                if (!StringUtils.isNoneBlank(arrString)) {
                    GoodsSeries gs = new GoodsSeries();
                    gs.setGoodsId(id);
                    gs.setBrandId(null);
                    gs.setSeriesId(null);
                    gs.setCreateDate(new Date());
                    gs.setModifyDate(new Date());
                    iGoodsSeriesMapper.insert(gs);
                } else {
                    JSONArray arr = JSONArray.parseArray(arrString);
                    iGoodsMapper.deleteGoodsSeries(id);//先删除goods所有跟品牌关联
                    for (int i = 0; i < arr.size(); i++) {//新增goods关联品牌系列
                        JSONObject obj = arr.getJSONObject(i);
                        GoodsSeries gs = new GoodsSeries();
                        gs.setGoodsId(id);

                        if (!StringUtils.isNoneBlank(obj.getString("brandId"))) {
                            gs.setBrandId(null);
                        } else {
                            gs.setBrandId(obj.getString("brandId"));
                        }

                        if (!StringUtils.isNoneBlank(obj.getString("seriesId"))) {
                            gs.setSeriesId(null);
                        } else {
                            gs.setSeriesId(obj.getString("seriesId"));
                        }
                        iGoodsSeriesMapper.insert(gs);
                    }
                }


            }

            return ServerResponse.createBySuccessMessage("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("修改失败");

        }
    }

    /**
     * 根据id删除goods和下属product
     *
     * @param id
     * @return
     */
    public ServerResponse deleteGoods(String id) {
        try {
//            if (true)
//                return ServerResponse.createByErrorMessage("不能执行删除操作");
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
     * 查询goods及下属product
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
                Map<String, Object> gMap = BeanUtils.beanToMap(goods);
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
                    Map<String, Object> map = BeanUtils.beanToMap(p);
                    map.put("imageUrl", imgUrlStr);
                    if (!StringUtils.isNotBlank(p.getLabelId())) {
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
     * 模糊查询goods及下属product
     *
     * @param pageDTO
     * @param categoryId
     * @param name
     * @param type       是否禁用  0：禁用；1不禁用 ;  -1全部默认
     * @return
     */
    public ServerResponse queryGoodsListByCategoryLikeName(PageDTO pageDTO, String categoryId, String name, Integer type) {
        try {
            LOG.info("tqueryGoodsListByCategoryLikeName type :" + type);
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
                Map<String, Object> gMap = BeanUtils.beanToMap(goods);
                List<Map<String, Object>> mapList = new ArrayList<>();
                gMap.put("goodsUnitName", iUnitMapper.selectByPrimaryKey(goods.getUnitId()).getName());
                if (2 != goods.getBuy())//非自购
                {
                    List<Product> productList = iProductMapper.queryByGoodsId(goods.getId());
                    for (Product p : productList) {
                        //type表示： 是否禁用  0：禁用；1不禁用 ;  -1全部默认
                        if (type!=null&&type != p.getType() && -1 != type) //不等于 type 的不返回给前端
                            continue;

                        String imgUrlStr = "";
                        String imgStr = "";
                        if (!CommonUtil.isEmpty(p.getImage())) {
                            String[] imgArr = p.getImage().split(",");
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
                        p.setImage(imgStr);

                        Map<String, Object> map = BeanUtils.beanToMap(p);
                        map.put("imageUrl", imgUrlStr);

                        map.put("convertUnitName", iUnitMapper.selectByPrimaryKey(p.getConvertUnit()).getName());

                        String strNewValueNameArr = "";
                        if (StringUtils.isNotBlank(p.getValueIdArr())) {
                            String[] newValueNameArr = p.getValueIdArr().split(",");

                            for (int i = 0; i < newValueNameArr.length; i++) {
                                String valueId = newValueNameArr[i];
                                if (StringUtils.isNotBlank(valueId)) {
                                    AttributeValue attributeValue = iAttributeValueMapper.selectByPrimaryKey(valueId);
                                    if (i == 0) {
                                        strNewValueNameArr = attributeValue.getName();
                                    } else {
                                        strNewValueNameArr = strNewValueNameArr + "," + attributeValue.getName();
                                    }
                                }
                            }
                        }
                        map.put("newValueNameArr", strNewValueNameArr);

                        if (!StringUtils.isNotBlank(p.getLabelId())) {
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
     * 根据goodsid和标签id ，找出对应的product对象集合
     *
     * @param goodsArr   :  goodsArr  数组
     * @param srcLabelId :   srcLabelId
     * @return
     */
    public ServerResponse queryProductListByGoodsIdAndLabelId(String goodsArr, String srcLabelId) {
        try {
//            LOG.info("queryProductListByGoodsIdAndLabelId goodsArr::" + goodsArr + " id:" + srcLabelId);
//            List<String> goodsList = Arrays.asList(goodsArr);
            String[] goodsList = goodsArr.split(",");
            if (!StringUtils.isNotBlank(srcLabelId))
                return ServerResponse.createByErrorMessage("标签id不能为空");

            if (!StringUtils.isNotBlank(goodsArr))
                return ServerResponse.createByErrorMessage("查询货品id不能为空");

            boolean isFindLabel = false; //是否找到 标签  false: 没有找到
            List<Map<String, Object>> gMapList = new ArrayList<>();
            for (String srcGoodsId : goodsList) {
                if (!StringUtils.isNotBlank(srcGoodsId))
                    return ServerResponse.createByErrorMessage("货品id不能为空");
//                LOG.info("for srcGoodsId:" + srcGoodsId);
                Goods goods = iGoodsMapper.selectByPrimaryKey(srcGoodsId);
                if (goods == null)
                    return ServerResponse.createByErrorMessage("货品不存在");

                List<Product> products = iProductMapper.queryByGoodsId(goods.getId());
                for (Product product : products) {
                    if (product.getLabelId() != null) {
                        if (!product.getLabelId().equals(srcLabelId))
                            continue;
//                        Map<String, Object> map = BeanUtils.beanToMap(p);
                        Map<String, Object> map = new HashMap<>();
                        Label label = iLabelMapper.selectByPrimaryKey(product.getLabelId());
                        if (label != null) {
                            map.put("goodsId", goods.getId());
                            map.put("productId", product.getId());
                            map.put("productName", product.getName());
                            map.put("labelId", product.getLabelId());
                            map.put("labelName", label.getName());
                            map.put("unitName", product.getUnitName());
                            gMapList.add(map);
                            isFindLabel = true;
                        }
                    }
                }
            }

            if (!isFindLabel)
                return ServerResponse.createByErrorMessage("列表中的货品里面没有该标签的商品");

            return ServerResponse.createBySuccess("查询成功", gMapList);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


}
