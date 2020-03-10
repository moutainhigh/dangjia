package com.dangjia.acg.service.basics;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.BaseException;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.mapper.basics.IAttributeMapper;
import com.dangjia.acg.mapper.basics.IAttributeValueMapper;
import com.dangjia.acg.mapper.basics.IGoodsMapper;
import com.dangjia.acg.mapper.basics.IProductMapper;
import com.dangjia.acg.modle.attribute.Attribute;
import com.dangjia.acg.modle.attribute.AttributeValue;
import com.dangjia.acg.modle.basics.Product;
import com.dangjia.acg.modle.product.BasicsGoods;
import com.dangjia.acg.pojo.attribute.AttributeValuePO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @类 名： GoodsAttributeServiceImpl
 * @功能描述： 类型属性
 * @作者信息： zmj
 * @创建时间： 2018-9-13上午10:15:58
 */
@Service
public class AttributeService {
    @Autowired
    private IGoodsMapper iGoodsMapper;
    @Autowired
    private IProductMapper iProductMapper;
    @Autowired
    private IAttributeMapper iAttributeMapper;
    @Autowired
    private IAttributeValueMapper iAttributeValueMapper;
    @Autowired
    private ConfigUtil configUtil;
    private static Logger LOG = LoggerFactory.getLogger(AttributeService.class);

    //根据类别id查询关联属性
    public ServerResponse<PageInfo> queryGoodsAttribute(PageDTO pageDTO, String goodsCategoryId, String likeAttrName,String cityId,Integer type) {
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        try {
            List<Attribute> caList  = iAttributeMapper.queryAttributeByCategoryId(goodsCategoryId, likeAttrName,cityId,type);
            List<Map<String, Object>> rListMap = new ArrayList<>();
            PageInfo pageResult = new PageInfo(caList);
            for (Attribute ca : caList) {
                Map<String, Object> caMap = new HashMap<>();
                caMap.put("id", ca.getId());
                caMap.put("name", ca.getName());
                caMap.put("type", ca.getType());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                caMap.put("createDate", sdf.format(ca.getCreateDate()));
                caMap.put("modifyDate", sdf.format(ca.getModifyDate()));
                List<AttributeValuePO> avList = iAttributeValueMapper.queryPOByAttributeId(ca.getId(),cityId);
                List<Map<String, Object>> avListMap = new ArrayList<>();
                for (AttributeValuePO av : avList) {
                    Map<String, Object> avMap = new HashMap<>();
                    avMap.put("avId", av.getId());
                    avMap.put("avName", av.getName());
                    if(!CommonUtil.isEmpty(av.getImage())) {
                        avMap.put("image", address + av.getImage());
                        avMap.put("imageUrl", av.getImage());
                    }else{
                        avMap.put("image", null);
                        avMap.put("imageUrl", null);
                    }
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

    //根据属性名称模糊查询属性
    public ServerResponse<PageInfo> queryGoodsAttributelikeName(PageDTO pageDTO, String name,String cityId) {
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        try {
            List<Attribute> caList;
            List<Map<String, Object>> rListMap = new ArrayList<>();
            if (name == null || "".equals(name)) {
                caList = iAttributeMapper.query(cityId);
            } else {
                caList = iAttributeMapper.queryGoodsAttributelikeName(name,cityId);
            }
            PageInfo pageResult = new PageInfo(caList);
            for (Attribute ca : caList) {
                Map<String, Object> caMap = new HashMap<>();
                caMap.put("id", ca.getId());
                caMap.put("name", ca.getName());
                caMap.put("type", ca.getType());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                caMap.put("createDate", sdf.format(ca.getCreateDate()));
                caMap.put("modifyDate", sdf.format(ca.getModifyDate()));
                List<AttributeValue> avList = iAttributeValueMapper.queryByAttributeId(ca.getId(),cityId);
                List<Map<String, Object>> avListMap = new ArrayList<>();
                for (AttributeValue av : avList) {
                    Map<String, Object> avMap = new HashMap<>();
                    avMap.put("avId", av.getId());
                    avMap.put("avName", av.getName());
                    if(!CommonUtil.isEmpty(av.getImage())) {
                        avMap.put("image", address + av.getImage());
                        avMap.put("imageUrl", av.getImage());
                    }else{
                        avMap.put("image", null);
                        avMap.put("imageUrl", null);
                    }
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
    public ServerResponse queryAttributeValue(String id,String cityId) {
        String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        try {
            Map<String, Object> gaMap = new HashMap<>();
            Attribute goodsAttribute = iAttributeMapper.queryById(id);
            if (goodsAttribute != null) {
                gaMap.put("id", goodsAttribute.getId());
                gaMap.put("name", goodsAttribute.getName());
                gaMap.put("type", goodsAttribute.getType());
                List<AttributeValue> avList = iAttributeValueMapper.queryByAttributeId(goodsAttribute.getId(),cityId);
                List<Map<String, Object>> avListMap = new ArrayList<>();
                for (AttributeValue av : avList) {
                    Map<String, Object> avMap = new HashMap<>();
                    avMap.put("avId", av.getId());
                    avMap.put("avName", av.getName());
                    if(!CommonUtil.isEmpty(av.getImage())) {
                        avMap.put("image", address + av.getImage());
                        avMap.put("imageUrl", av.getImage());
                    }else{
                        avMap.put("image", null);
                        avMap.put("imageUrl", null);
                    }
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
    public ServerResponse insertGoodsAttribute(String goodsCategoryId, String attributeName, Integer type, String jsonStr,String cityId) {
        try {
            List<Attribute> attributeList = iAttributeMapper.queryAttributeByCategoryId(goodsCategoryId, null,cityId,null);
            for (Attribute attribute : attributeList) {
                if (attribute.getName().equals(attributeName))
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
            Attribute attribute = new Attribute();
            attribute.setCityId(cityId);
            attribute.setName(attributeName);
            attribute.setType(type);
            attribute.setCategoryId(goodsCategoryId);
            iAttributeMapper.insert(attribute);
            for (int i = 0; i < jsonArr.size(); i++) {
                JSONObject obj = jsonArr.getJSONObject(i);
                AttributeValue attributeValue = new AttributeValue();
                attributeValue.setCityId(cityId);
                attributeValue.setAttributeId(attribute.getId());
                attributeValue.setName(obj.getString("name"));
                if (type == 1) {//是规格属性不用存图
                    attributeValue.setImage(obj.getString("image"));
                }
                attributeValue.setCreateDate(new Date());
                attributeValue.setModifyDate(new Date());
                iAttributeValueMapper.insert(attributeValue);
            }
            return ServerResponse.createBySuccessMessage("新增成功");
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(ServerCode.WRONG_PARAM, "新增失败");
        }
    }

    //修改属性及其属性选项
    public ServerResponse doModifyGoodsAttribute(String attributeId, String attributeName, Integer type, String jsonStr,String cityId) {
        try {
            Attribute srcAttribute = iAttributeMapper.queryById(attributeId);
            LOG.info("doModifyGoodsAttribute::::Id: " + attributeId + " name:" + attributeName);

            if (!attributeName.equals(srcAttribute.getName())) {//修改了属性名字
                //只需查询 一个分类 对应的所有的属性名称 有没有被使用
                List<Attribute> attributeList = iAttributeMapper.queryAttributeByCategoryIdAndAttrName(srcAttribute.getCategoryId(), attributeName,cityId);
                for (Attribute ae : attributeList) {
                    LOG.info(" name:" + ae.getName());
//                List<AttributeValue> attributeValueList = iAttributeValueMapper.queryByAttributeId(ae.getId());
                    if (ae.getName().equals(srcAttribute.getName())) {
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
                List<AttributeValue> attributeValueList = iAttributeValueMapper.queryByAttributeId(attributeId,cityId);
                for (AttributeValue attributeValue : attributeValueList) {
                    //检查前端 提交的json 是否有重复的属性选项名
                    if (jsonName.equals(attributeValue.getName())) {
                        useCount++;
                        if (useCount > 1)  //不能有 2个同样属性选项名 入库
                            return ServerResponse.createByErrorMessage("属性选项名已存在");
                    }
                }
            }
            Attribute goodsAttribute = new Attribute();
            goodsAttribute.setCityId(cityId);
            goodsAttribute.setId(attributeId);
            goodsAttribute.setName(attributeName);
            goodsAttribute.setType(type);
            goodsAttribute.setModifyDate(new Date());
            iAttributeMapper.updateByPrimaryKeySelective(goodsAttribute);
            for (int i = 0; i < jsonArr.size(); i++) {
                JSONObject obj = jsonArr.getJSONObject(i);
                AttributeValue attributeValue = new AttributeValue();
                if (obj.getString("id") == null || "".equals(obj.getString("id"))) {//新增
                    attributeValue.setCityId(cityId);
                    attributeValue.setAttributeId(goodsAttribute.getId());
                    attributeValue.setName(obj.getString("name"));
                    if (type == 1) {//是规格属性不用存图
                        if(!CommonUtil.isEmpty(obj.getString("image"))) {//是规格属性不用存图
                            attributeValue.setImage(obj.getString("image"));
                        }
                    }
                    attributeValue.setCreateDate(new Date());
                    attributeValue.setModifyDate(new Date());
                    iAttributeValueMapper.insert(attributeValue);
                } else {//修改
                    attributeValue.setId(obj.getString("id"));
                    attributeValue.setAttributeId(goodsAttribute.getId());
                    attributeValue.setName(obj.getString("name"));
                    if (type == 1) {
                        if(!CommonUtil.isEmpty(obj.getString("image"))) {//是规格属性不用存图
                            attributeValue.setImage(obj.getString("image"));
                        }
                    }
                    attributeValue.setModifyDate(new Date());
                    attributeValue.setCityId(cityId);
                    iAttributeValueMapper.updateByPrimaryKeySelective(attributeValue);
                    //更新指定属性值关联的商品属性名称
                    iProductMapper.updateProductValueId(attributeValue.getId());
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
    public ServerResponse deleteGoodsAttribute(String goodsAttributeId,String cityId) {
        try {

            Attribute srcAttribute = iAttributeMapper.queryById(goodsAttributeId);
            List<BasicsGoods> goodsList = iGoodsMapper.queryByCategoryId(srcAttribute.getCategoryId());//根据分类id查询是否有关联商品
            if (goodsList.size() > 0)
                return ServerResponse.createByErrorMessage("该商品属性有关联商品不能删除");

            List<Product> productLists = iProductMapper.getPListByValueIdArrOrAttrId(srcAttribute.getId(), null,cityId);
            if (productLists.size() > 0)
                return ServerResponse.createByErrorMessage("该商品属性有关联商品不能删除");


            //检查该分类中的所有商品，是否有商品使用 该属性名和属性选项名
            for (BasicsGoods gs : goodsList) {
//				LOG.info("gs name:"+ gs.getName());
                //检查属性名已经存在   属性名是否有商品使用
                List<Product> productList = iProductMapper.queryByGoodsId(gs.getId(),cityId);
                for (Product product : productList) {
                    String[] valueIdArr = product.getValueNameArr().split(",");
                    for (String aValueIdArr : valueIdArr) {
                        if (Arrays.asList(valueIdArr).contains(aValueIdArr))
                            return ServerResponse.createByErrorMessage("删除失败，该属性选项名已被其他商品使用");
                    }

                    String[] attributeIdArr = product.getAttributeIdArr().split(",");
                    for (String anAttributeIdArr : attributeIdArr) {
                        Attribute ae = iAttributeMapper.queryById(anAttributeIdArr);
                        if (srcAttribute.getName().equals(ae.getName()))
                            return ServerResponse.createByErrorMessage("删除失败，该属性选项名已被其他商品使用");
                    }
                }
            }

            iAttributeMapper.deleteById(goodsAttributeId);//删除商品属性
            iAttributeValueMapper.deleteByAttributeId(goodsAttributeId);//删除属性选项
            return ServerResponse.createBySuccessMessage("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(ServerCode.WRONG_PARAM, "删除失败");
        }
    }

    /**
     * 删除商品属性选项
     */
    public ServerResponse deleteByAttributeId(String attributeValueId,String cityId) {
        try {
            AttributeValue srcAttributeValue = iAttributeValueMapper.selectByPrimaryKey(attributeValueId);
            Attribute srcAe = iAttributeMapper.selectByPrimaryKey(srcAttributeValue.getAttributeId());
            List<BasicsGoods> goodsList = iGoodsMapper.queryByCategoryId(srcAe.getCategoryId());//根据分类id查询是否有关联商品
            if (goodsList.size() > 0)
                return ServerResponse.createByErrorMessage("该商品属性有关联商品不能删除");
            List<Product> productLists = iProductMapper.getPListByValueIdArrOrAttrId(null, attributeValueId,cityId);
            if (productLists.size() > 0)
                return ServerResponse.createByErrorMessage("该商品属性有关联商品不能删除");
            iAttributeValueMapper.deleteById(attributeValueId);//删除属性选项
            return ServerResponse.createBySuccessMessage("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(ServerCode.WRONG_PARAM, "删除失败");
        }
    }

}
