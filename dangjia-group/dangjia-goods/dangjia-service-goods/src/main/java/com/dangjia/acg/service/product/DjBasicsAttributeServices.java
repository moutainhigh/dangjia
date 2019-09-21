package com.dangjia.acg.service.product;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.common.exception.BaseException;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.mapper.product.*;
import com.dangjia.acg.modle.product.*;
import com.dangjia.acg.pojo.product.DjBasicsAttributeValuePO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/9/18
 * Time: 10:08
 */
@Service
public class DjBasicsAttributeServices {
    @Autowired
    private DjBasicsAttributeMapper djBasicsAttributeMapper;
    @Autowired
    private DjBasicsAttributeValueMapper djBasicsAttributeValueMapper;
    private static Logger LOG = LoggerFactory.getLogger(DjBasicsAttributeServices.class);
    @Autowired
    private DjBasicsProductMapper djBasicsProductMapper;
    @Autowired
    private DjBasicsGoodsMapper djBasicsGoodsMapper;
    @Autowired
    private DjBasicsProductMaterialMapper djBasicsProductMaterialMapper;



    //根据类别id查询关联属性
    public ServerResponse<PageInfo> queryGoodsAttribute(PageDTO pageDTO, String goodsCategoryId, String likeAttrName) {
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        try {
            List<DjBasicsAttribute> caList  = djBasicsAttributeMapper.queryAttributeByCategoryId(goodsCategoryId, likeAttrName);
            List<Map<String, Object>> rListMap = new ArrayList<>();
            PageInfo pageResult = new PageInfo(caList);
            caList.forEach(ca ->{
                Map<String, Object> caMap = new HashMap<>();
                caMap.put("id", ca.getId());
                caMap.put("name", ca.getName());
                caMap.put("type", ca.getType());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                caMap.put("createDate", sdf.format(ca.getCreateDate()));
                caMap.put("modifyDate", sdf.format(ca.getModifyDate()));
                List<DjBasicsAttributeValuePO> avList = djBasicsAttributeValueMapper.queryPOByAttributeId(ca.getId());
                List<Map<String, Object>> avListMap = new ArrayList<>();
                avList.forEach(av ->{
                    Map<String, Object> avMap = new HashMap<>();
                    avMap.put("avId", av.getId());
                    avMap.put("avName", av.getName());
                    avListMap.add(avMap);
                });
                caMap.put("avListMap", avListMap);
                rListMap.add(caMap);
            });
            pageResult.setList(rListMap);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(ServerCode.WRONG_PARAM, "查询失败");
        }
    }


    //根据属性名称模糊查询属性
    public ServerResponse<PageInfo> queryGoodsAttributelikeName(PageDTO pageDTO, String name) {
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        try {
            List<DjBasicsAttribute> caList;
            List<Map<String, Object>> rListMap = new ArrayList<>();
            if (name == null || "".equals(name)) {
                caList = djBasicsAttributeMapper.selectAll();
            } else {
                Example example=new Example(DjBasicsAttribute.class);
                example.createCriteria().andLike(DjBasicsAttribute.NAME,"%" + name + "%");
                example.orderBy(DjBasicsAttribute.CREATE_DATE).desc();
                caList = djBasicsAttributeMapper.selectByExample(example);
            }
            PageInfo pageResult = new PageInfo(caList);
            for (DjBasicsAttribute ca : caList) {
                Map<String, Object> caMap = new HashMap<>();
                caMap.put("id", ca.getId());
                caMap.put("name", ca.getName());
                caMap.put("type", ca.getType());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                caMap.put("createDate", sdf.format(ca.getCreateDate()));
                caMap.put("modifyDate", sdf.format(ca.getModifyDate()));
                Example example=new Example(DjBasicsAttributeValue.class);
                example.createCriteria().andEqualTo(DjBasicsAttributeValue.ATTRIBUTE_ID,ca.getId());
                example.orderBy(DjBasicsAttributeValue.CREATE_DATE).desc();
                List<DjBasicsAttributeValue> avList = djBasicsAttributeValueMapper.selectByExample(example);
                List<Map<String, Object>> avListMap = new ArrayList<>();
                for (DjBasicsAttributeValue av : avList) {
                    Map<String, Object> avMap = new HashMap<>();
                    avMap.put("avId", av.getId());
                    avMap.put("avName", av.getName());
                    avListMap.add(avMap);
                }
                caMap.put("avListMap", avListMap);
                rListMap.add(caMap);
            }
            pageResult.setList(rListMap);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(ServerCode.WRONG_PARAM, "查询失败");
        }
    }


    //根据属性id查询属性及其所有关联属性选项
    public ServerResponse queryAttributeValue(String id) {
        try {
            Map<String, Object> gaMap = new HashMap<>();
            DjBasicsAttribute goodsAttribute = djBasicsAttributeMapper.selectByPrimaryKey(id);
            if (goodsAttribute != null) {
                gaMap.put("id", goodsAttribute.getId());
                gaMap.put("name", goodsAttribute.getName());
                gaMap.put("type", goodsAttribute.getType());
                Example example=new Example(DjBasicsAttributeValue.class);
                example.createCriteria().andEqualTo(DjBasicsAttributeValue.ATTRIBUTE_ID,goodsAttribute.getId());
                example.orderBy(DjBasicsAttributeValue.CREATE_DATE).desc();
                List<DjBasicsAttributeValue> avList = djBasicsAttributeValueMapper.selectByExample(example);
                List<Map<String, Object>> avListMap = new ArrayList<>();
                for (DjBasicsAttributeValue av : avList) {
                    Map<String, Object> avMap = new HashMap<>();
                    avMap.put("avId", av.getId());
                    avMap.put("avName", av.getName());
                    avListMap.add(avMap);
                }
                gaMap.put("avListMap", avListMap);
            }
            return ServerResponse.createBySuccess("查询成功", gaMap);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(ServerCode.WRONG_PARAM, "查询失败");
        }
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
                    djBasicsProductMapper.updateProductValueId(djBasicsAttributeValue.getId());
                }
            }
            return ServerResponse.createBySuccessMessage("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(ServerCode.WRONG_PARAM, "保存失败");
        }
    }

    /**
     * 删除商品属性
     */
    public ServerResponse deleteGoodsAttribute(String goodsAttributeId) {
        try {
            DjBasicsAttribute srcAttribute = djBasicsAttributeMapper.selectByPrimaryKey(goodsAttributeId);
            List<DjBasicsGoods> goodsList = djBasicsGoodsMapper.queryByCategoryId(srcAttribute.getCategoryId());//根据分类id查询是否有关联商品
            if (goodsList.size() > 0)
                return ServerResponse.createByErrorMessage("该商品属性有关联商品不能删除");

            List<DjBasicsProduct> productLists = djBasicsProductMapper.getPListByValueIdArrOrAttrId(srcAttribute.getId(), null);
            if (productLists.size() > 0)
                return ServerResponse.createByErrorMessage("该商品属性有关联商品不能删除");


            //检查该分类中的所有商品，是否有商品使用 该属性名和属性选项名
            for (DjBasicsGoods gs : goodsList) {
//				LOG.info("gs name:"+ gs.getName());
                //检查属性名已经存在   属性名是否有商品使用
                List<DjBasicsProduct> productList = djBasicsProductMapper.queryByGoodsId(gs.getId());
                Example example=new Example(DjBasicsProductMaterial.class);
                String[] attributeIdArr = gs.getAttributeIdArr().split(",");
                for (String anAttributeIdArr : attributeIdArr) {
                    DjBasicsAttribute ae = djBasicsAttributeMapper.selectByPrimaryKey(anAttributeIdArr);
                    if (srcAttribute.getName().equals(ae.getName()))
                        return ServerResponse.createByErrorMessage("删除失败，该属性选项名已被其他商品使用");
                }
            }

            djBasicsAttributeMapper.deleteByPrimaryKey(goodsAttributeId);//删除商品属性
            Example example=new Example(DjBasicsAttributeValue.class);
            example.createCriteria().andEqualTo(DjBasicsAttributeValue.ATTRIBUTE_ID,goodsAttributeId);
            djBasicsAttributeValueMapper.deleteByExample(example);//删除属性选项
            return ServerResponse.createBySuccessMessage("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(ServerCode.WRONG_PARAM, "删除失败");
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

}
