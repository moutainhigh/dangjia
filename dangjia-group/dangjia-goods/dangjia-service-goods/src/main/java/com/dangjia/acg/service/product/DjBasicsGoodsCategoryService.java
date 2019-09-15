package com.dangjia.acg.service.product;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.common.exception.BaseException;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.mapper.basics.IGoodsMapper;
import com.dangjia.acg.mapper.product.*;
import com.dangjia.acg.modle.product.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/9/15
 * Time: 10:04
 */
@Service
public class DjBasicsGoodsCategoryService {
    @Autowired
    private DjBasicsGoodsCategoryMapper djBasicsGoodsCategoryMapper;
    @Autowired
    private DjBasicsAttributeMapper djBasicsAttributeMapper;
    @Autowired
    private DjBasicsAttributeValueMapper djBasicsAttributeValueMapper;
    private static Logger LOG = LoggerFactory.getLogger(DjBasicsGoodsCategoryService.class);
    @Autowired
    private DjBasicsProductMapper djBasicsProductMapper;
    @Autowired
    private DjBasicsGoodsMapper djBasicsGoodsMapper;


    /**
     * 新增商品类别
     * @param name
     * @param parentId
     * @param parentTop
     * @param sort
     * @param isLastCategory
     * @param categoryLabelId
     * @param coverImage
     * @param purchaseRestrictions
     * @param brandId
     * @return
     */
    public ServerResponse addGoodsCategory(String name, String parentId, String parentTop, Integer sort, Integer isLastCategory,
                                           String categoryLabelId, String coverImage, Integer purchaseRestrictions, String brandId) {
        try {
            Example example=new Example(DjBasicsGoodsCategory.class);
            example.createCriteria().andEqualTo(DjBasicsGoodsCategory.NAME,name);
            List<DjBasicsGoodsCategory> djBasicsGoodsCategories = djBasicsGoodsCategoryMapper.selectByExample(example);//根据name查询商品对象
            if (djBasicsGoodsCategories.size() > 0)
                return ServerResponse.createByErrorMessage("不能重复添加类别");
            DjBasicsGoodsCategory djBasicsGoodsCategory = new DjBasicsGoodsCategory();
            djBasicsGoodsCategory.setName(name);
            djBasicsGoodsCategory.setParentId(parentId);
            djBasicsGoodsCategory.setParentTop(parentTop);
            djBasicsGoodsCategory.setIsLastCategory(isLastCategory);
            djBasicsGoodsCategory.setCategoryLabelId(categoryLabelId);
            djBasicsGoodsCategory.setImage(coverImage);
            djBasicsGoodsCategory.setPurchaseRestrictions(purchaseRestrictions);
            djBasicsGoodsCategory.setBrandId(brandId);
            if (sort == null) sort = 99;
            djBasicsGoodsCategory.setSort(sort);
            djBasicsGoodsCategoryMapper.insert(djBasicsGoodsCategory);
            return ServerResponse.createBySuccess("新增成功", djBasicsGoodsCategory.getId());
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(ServerCode.WRONG_PARAM, "查询失败");
        }
    }


    /**
     * 修改商品类别
     * @param name
     * @param parentId
     * @param parentTop
     * @param sort
     * @param isLastCategory
     * @param categoryLabelId
     * @param coverImage
     * @param purchaseRestrictions
     * @param brandId
     * @return
     */
    public ServerResponse updateGoodsCategory(String id,String name, String parentId, String parentTop, Integer sort, Integer isLastCategory,
                                              String categoryLabelId, String coverImage, Integer purchaseRestrictions, String brandId) {
        try {
            DjBasicsGoodsCategory djBasicsGoodsCategory = djBasicsGoodsCategoryMapper.selectByPrimaryKey(id);
            if (!djBasicsGoodsCategory.getName().equals(name)) { //如果 是修改name
                Example example=new Example(DjBasicsGoodsCategory.class);
                example.createCriteria().andEqualTo(DjBasicsGoodsCategory.NAME,name);
                List<DjBasicsGoodsCategory> djBasicsGoodsCategories = djBasicsGoodsCategoryMapper.selectByExample(example);//根据name查询商品对象
                if (djBasicsGoodsCategories.size() > 0)
                    return ServerResponse.createByErrorMessage("该类别已存在");
            }
            djBasicsGoodsCategory.setName(name);
            djBasicsGoodsCategory.setParentId(parentId);
            djBasicsGoodsCategory.setParentTop(parentTop);
            djBasicsGoodsCategory.setIsLastCategory(isLastCategory);
            djBasicsGoodsCategory.setCategoryLabelId(categoryLabelId);
            djBasicsGoodsCategory.setImage(coverImage);
            djBasicsGoodsCategory.setPurchaseRestrictions(purchaseRestrictions);
            djBasicsGoodsCategory.setBrandId(brandId);
            if (sort != null) {
                djBasicsGoodsCategory.setSort(sort);
            }
            djBasicsGoodsCategoryMapper.updateByPrimaryKeySelective(djBasicsGoodsCategory);
            return ServerResponse.createBySuccessMessage("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(ServerCode.WRONG_PARAM, "修改失败");
        }
    }

    //查询商品属性列表 queryGoodsCategory
    public ServerResponse queryGoodsCategory(String parentId) {
        List<DjBasicsGoodsCategory> djBasicsGoodsCategories = djBasicsGoodsCategoryMapper.queryCategoryByParentId(parentId);
        if (djBasicsGoodsCategories.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        return ServerResponse.createBySuccess("查询成功", djBasicsGoodsCategories);
    }


    //新增属性及其属性选项
    public ServerResponse addGoodsAttribute(String goodsCategoryId, String attributeName, Integer type, String jsonStr, Integer isScreenConditions) {
        try {
            List<DjBasicsAttribute> djBasicsAttributes = djBasicsAttributeMapper.queryAttributeByCategoryId(goodsCategoryId, null);
            for (DjBasicsAttribute djBasicsAttribute : djBasicsAttributes) {
                if (djBasicsAttribute.getName().equals(attributeName))
                    return ServerResponse.createByErrorMessage("不能重复添加属性名称");
            }
            JSONArray jsonArr = JSONArray.parseArray(jsonStr);
            for (int i = 0; i < jsonArr.size(); i++) {
                String name = jsonArr.getJSONObject(i).getString("name");
                int count = 0;//标记每个属性出现的次数
                for (int j = 0; j < jsonArr.size(); j++) {
                    if (name.equals(jsonArr.getJSONObject(j).getString("name"))) {
                        count++;
                        if (count > 1)//如果该属性名 出现2次，说明是有2个是重复的
                            return ServerResponse.createByErrorMessage("不能重复添加属性选项名");
                    }

                }
            }
            DjBasicsAttribute djBasicsAttribute = new DjBasicsAttribute();
            djBasicsAttribute.setName(attributeName);
            djBasicsAttribute.setType(type);
            djBasicsAttribute.setCategoryId(goodsCategoryId);
            djBasicsAttribute.setIsScreenConditions(isScreenConditions);
            djBasicsAttributeMapper.insert(djBasicsAttribute);
            for (int i = 0; i < jsonArr.size(); i++) {
                JSONObject obj = jsonArr.getJSONObject(i);
                DjBasicsAttributeValue djBasicsAttributeValue = new DjBasicsAttributeValue();
                djBasicsAttributeValue.setAttributeId(djBasicsAttribute.getId());
                djBasicsAttributeValue.setName(obj.getString("name"));
                djBasicsAttributeValueMapper.insert(djBasicsAttributeValue);
            }
            return ServerResponse.createBySuccessMessage("新增成功");
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(ServerCode.WRONG_PARAM, "新增失败");
        }
    }


    //修改属性及其属性选项
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse updateGoodsAttribute(String attributeId, String attributeName, Integer type, String jsonStr ,Integer isScreenConditions) {
        try {
            DjBasicsAttribute djBasicsAttribute = djBasicsAttributeMapper.selectByPrimaryKey(attributeId);
            LOG.info("doModifyGoodsAttribute::::Id: " + attributeId + " name:" + attributeName);

            if (!attributeName.equals(djBasicsAttribute.getName())) {//修改了属性名字
                //只需查询 一个分类 对应的所有的属性名称 有没有被使用
                List<DjBasicsAttribute> djBasicsAttributes = djBasicsAttributeMapper.queryAttributeByCategoryIdAndAttrName(djBasicsAttribute.getCategoryId(), attributeName);
                for (DjBasicsAttribute djBasicsAttribute1 : djBasicsAttributes) {
                    LOG.info(" name:" + djBasicsAttribute1.getName());
//                List<AttributeValue> attributeValueList = iAttributeValueMapper.queryByAttributeId(ae.getId());
                    if (djBasicsAttribute1.getName().equals(djBasicsAttribute.getName())) {
                        return ServerResponse.createByErrorMessage("该属性名称已被使用");
                    }
                }
            }

            JSONArray jsonArr = JSONArray.parseArray(jsonStr);
            for (int i = 0; i < jsonArr.size(); i++) {
                String jsonName = jsonArr.getJSONObject(i).getString("name");
                int nameCount = 0;
                for (int j = 0; j < jsonArr.size(); j++) {
                    if (jsonName.equals(jsonArr.getJSONObject(j).getString("name"))) {
                        nameCount++;
                        //检查前端 提交的json 是否有重复的属性选项名
                        if (nameCount > 1)
                            return ServerResponse.createByErrorMessage("属性选项名不能重复");
                    }
                }
                int useCount = 0;
                Example example=new Example(DjBasicsAttributeValue.class);
                example.createCriteria().andEqualTo(DjBasicsAttributeValue.ATTRIBUTE_ID,attributeId);
                example.orderBy(DjBasicsAttributeValue.CREATE_DATE).desc();
                List<DjBasicsAttributeValue> djBasicsAttributeValues = djBasicsAttributeValueMapper.selectByExample(example);
                for (DjBasicsAttributeValue djBasicsAttributeValue : djBasicsAttributeValues) {
                    //检查前端 提交的json 是否有重复的属性选项名
                    if (jsonName.equals(djBasicsAttributeValue.getName())) {
                        useCount++;
                        if (useCount > 1)  //不能有 2个同样属性选项名 入库
                            return ServerResponse.createByErrorMessage("属性选项名已存在");
                    }
                }
            }
            DjBasicsAttribute djBasicsAttribute1 = new DjBasicsAttribute();
            djBasicsAttribute1.setId(attributeId);
            djBasicsAttribute1.setName(attributeName);
            djBasicsAttribute1.setType(type);
            djBasicsAttribute1.setIsScreenConditions(isScreenConditions);
            djBasicsAttributeMapper.updateByPrimaryKeySelective(djBasicsAttribute1);
            for (int i = 0; i < jsonArr.size(); i++) {
                JSONObject obj = jsonArr.getJSONObject(i);
                DjBasicsAttributeValue djBasicsAttributeValue = new DjBasicsAttributeValue();
                if (obj.getString("id") == null || "".equals(obj.getString("id"))) {//新增
                    djBasicsAttributeValue.setAttributeId(djBasicsAttribute1.getId());
                    djBasicsAttributeValue.setName(obj.getString("name"));
                    djBasicsAttributeValueMapper.insert(djBasicsAttributeValue);
                } else {//修改
                    djBasicsAttributeValue.setId(obj.getString("id"));
                    djBasicsAttributeValue.setAttributeId(djBasicsAttribute1.getId());
                    djBasicsAttributeValue.setName(obj.getString("name"));
                    djBasicsAttributeValueMapper.updateByPrimaryKeySelective(djBasicsAttributeValue);
//                    //更新指定属性值关联的商品属性名称
//                    iProductMapper.updateProductValueId(djBasicsAttributeValue.getId());
                }
            }
            return ServerResponse.createBySuccessMessage("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(ServerCode.WRONG_PARAM, "保存失败");
        }
    }


    public ServerResponse deleteByAttributeId(String attributeValueId) {
        try {
            DjBasicsAttributeValue djBasicsAttributeValue = djBasicsAttributeValueMapper.selectByPrimaryKey(attributeValueId);
            DjBasicsAttribute djBasicsAttribute = djBasicsAttributeMapper.selectByPrimaryKey(djBasicsAttributeValue.getAttributeId());
            Example example=new Example(DjBasicsGoods.class);
            example.createCriteria().andEqualTo(DjBasicsGoods.CATEGORY_ID,djBasicsAttribute.getCategoryId());
            example.orderBy(DjBasicsGoods.CREATE_DATE).desc();
            List<DjBasicsGoods> djBasicsGoods = djBasicsGoodsMapper.selectByExample(example);
            if (djBasicsGoods.size() > 0)
                return ServerResponse.createByErrorMessage("该商品属性有关联商品不能删除");
            List<DjBasicsProduct> pListByValueIdArrOrAttrId = djBasicsProductMapper.getPListByValueIdArrOrAttrId(null, attributeValueId);
            if (pListByValueIdArrOrAttrId.size() > 0)
                return ServerResponse.createByErrorMessage("该商品属性有关联商品不能删除");
            djBasicsAttributeValueMapper.deleteByPrimaryKey(attributeValueId);//删除属性选项
            return ServerResponse.createBySuccessMessage("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(ServerCode.WRONG_PARAM, "删除失败");
        }
    }

    //查询类别id查询所有父级以及父级属性
    public ServerResponse queryAttributeListById(String goodsCategoryId) {
        try {
            if (!StringUtils.isNoneBlank(goodsCategoryId)) {
                return ServerResponse.createByErrorMessage("goodsCategoryId不能为null");
            }
            DjBasicsGoodsCategory djBasicsGoodsCategory = djBasicsGoodsCategoryMapper.selectByPrimaryKey(goodsCategoryId);
            if (djBasicsGoodsCategory == null) {
                return ServerResponse.createByErrorMessage("查询失败");
            }
            List<DjBasicsAttribute> djBasicsAttributes = djBasicsAttributeMapper.queryAttributeByCategoryId(djBasicsGoodsCategory.getId(), null);
            while (djBasicsGoodsCategory != null) {
                djBasicsGoodsCategory = djBasicsGoodsCategoryMapper.selectByPrimaryKey(djBasicsGoodsCategory.getParentId());
                if (djBasicsGoodsCategory != null) {
                    djBasicsAttributes.addAll(djBasicsAttributeMapper.queryAttributeByCategoryId(djBasicsGoodsCategory.getId(), null));
                }
            }
            return ServerResponse.createBySuccess("查询成功", djBasicsAttributes);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    //查询两级商品分类
    public ServerResponse queryGoodsCategoryTwo() {
        try {
            List<Map<String, Object>> mapList = new ArrayList<>();
            List<DjBasicsGoodsCategory> djBasicsGoodsCategories = djBasicsGoodsCategoryMapper.queryCategoryByParentId("1");
            for (DjBasicsGoodsCategory djBasicsGoodsCategory : djBasicsGoodsCategories) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", djBasicsGoodsCategory.getId());
                map.put("name", djBasicsGoodsCategory.getName());
                List<Map<String, Object>> mapTwoList = new ArrayList<>();
                List<DjBasicsGoodsCategory> djBasicsGoodsCategories1 = djBasicsGoodsCategoryMapper.queryCategoryByParentId(djBasicsGoodsCategory.getId());
                for (DjBasicsGoodsCategory djBasicsGoodsCategory1 : djBasicsGoodsCategories1) {
                    Map<String, Object> mapTwo = new HashMap<>();
                    mapTwo.put("id", djBasicsGoodsCategory1.getId());
                    mapTwo.put("name", djBasicsGoodsCategory1.getName());
                    mapTwoList.add(mapTwo);
                }
                map.put("nextList", mapTwoList);
                mapList.add(map);
            }
            return ServerResponse.createBySuccess("查询成功", mapList);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


}
