package com.dangjia.acg.service.basics;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.BaseException;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.mapper.basics.IAttributeMapper;
import com.dangjia.acg.mapper.basics.IAttributeValueMapper;
import com.dangjia.acg.mapper.basics.IGoodsMapper;
import com.dangjia.acg.mapper.basics.IProductMapper;
import com.dangjia.acg.modle.attribute.*;
import com.dangjia.acg.modle.attribute.AttributeValue;
import com.dangjia.acg.modle.basics.Goods;
import com.dangjia.acg.modle.basics.Product;
import com.dangjia.acg.pojo.attribute.AttributePO;
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
    public ServerResponse<PageInfo> queryGoodsAttribute(Integer pageNum, Integer pageSize, String goodsCategoryId) {
        String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        try {
            if (pageNum == null) {
                pageNum = 1;
            }
            if (pageSize == null) {
                pageSize = 10;
            }
            PageHelper.startPage(pageNum, pageSize);
            List<Attribute> caList = new ArrayList<Attribute>();
            List<Map<String, Object>> rListMap = new ArrayList<Map<String, Object>>();
            if (goodsCategoryId == null || "".equals(goodsCategoryId)) {
                caList = iAttributeMapper.query();
            } else {
                LOG.info("queryGoodsAttribute **goodsCategoryId :" + goodsCategoryId);
                caList = iAttributeMapper.queryAttributeByCategoryId(goodsCategoryId);
            }
            for (Attribute ca : caList) {
                Map<String, Object> caMap = new HashMap<String, Object>();
                caMap.put("id", ca.getId());
                caMap.put("name", ca.getName());
                caMap.put("type", ca.getType());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                caMap.put("createDate", sdf.format(ca.getCreateDate()));
                caMap.put("modifyDate", sdf.format(ca.getModifyDate()));

                AttributePO attributepo = iAttributeMapper.queryPOById(ca.getId());
                List<AttributeValuePO> attributeValueList = attributepo.getAttributeValueLists();
//                LOG.info("attributeCC size:" + attributeValueList.size());
//                for(int i= 0;i<attributeValueList.size();i++)
//                    LOG.info("attributeCC size: name" + attributeValueList.get(i).getName());
//                for (AttributeValuePO attributeValue : attributeValueList) {
//                    LOG.info(" AttributeValue list:::" +  " name:" + attributeValue.getName());
//                    LOG.info(" AttributeValue list:::" + attributeValue.getAttribute().getName() + " name:" + attributeValue.getName());
//                }

//                List<AttributeValue> avList = iAttributeValueMapper.queryByAttributeId(ca.getId());
                List<AttributeValuePO> avList = iAttributeValueMapper.queryPOByAttributeId(ca.getId());
                List<Map<String, Object>> avListMap = new ArrayList<Map<String, Object>>();
                for (AttributeValuePO av : avList) {
                    AttributeValuePO sss = iAttributeValueMapper.getPOById(av.getId());
//                    LOG.info("myGetById :" + " name:" + sss.getName   () + " image:" + sss.getImage());
//                    LOG.info("myGetById getAttributeId :" + sss.getAttributeId());
//                    LOG.info("myGetById getName:" + sss.getAttribute().getName() + " id:" + sss.getAttribute().getId());
                    Map<String, Object> avMap = new HashMap<String, Object>();
                    avMap.put("avId", av.getId());
                    avMap.put("avName", av.getName());
                    avMap.put("image", address + av.getImage());
                    avMap.put("imageUrl", av.getImage());
                    avListMap.add(avMap);
                }
                caMap.put("avListMap", avListMap);
                rListMap.add(caMap);
            }
            PageInfo pageResult = new PageInfo(caList);
            pageResult.setList(rListMap);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(ServerCode.WRONG_PARAM, "查询失败");
        }
    }

    //根据属性名称模糊查询属性
    public ServerResponse<PageInfo> queryGoodsAttributelikeName(Integer pageNum, Integer pageSize, String name) {
        String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        try {
            if (pageNum == null) {
                pageNum = 1;
            }
            if (pageSize == null) {
                pageSize = 10;
            }
            PageHelper.startPage(pageNum, pageSize);
            List<Attribute> caList = new ArrayList<Attribute>();
            List<Map<String, Object>> rListMap = new ArrayList<Map<String, Object>>();
            if (name == null || "".equals(name)) {
                caList = iAttributeMapper.query();
            } else {
                caList = iAttributeMapper.queryGoodsAttributelikeName(name);
            }
            for (Attribute ca : caList) {
                Map<String, Object> caMap = new HashMap<String, Object>();
                caMap.put("id", ca.getId());
                caMap.put("name", ca.getName());
                caMap.put("type", ca.getType());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                caMap.put("createDate", sdf.format(ca.getCreateDate()));
                caMap.put("modifyDate", sdf.format(ca.getModifyDate()));
                List<AttributeValue> avList = iAttributeValueMapper.queryByAttributeId(ca.getId());
                List<Map<String, Object>> avListMap = new ArrayList<Map<String, Object>>();
                for (AttributeValue av : avList) {
                    Map<String, Object> avMap = new HashMap<String, Object>();
                    avMap.put("avId", av.getId());
                    avMap.put("avName", av.getName());
                    avMap.put("image", address + av.getImage());
                    avMap.put("imageUrl", av.getImage());
                    avListMap.add(avMap);
                }
                caMap.put("avListMap", avListMap);
                rListMap.add(caMap);
            }
            PageInfo pageResult = new PageInfo(caList);
            pageResult.setList(rListMap);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(ServerCode.WRONG_PARAM, "查询失败");
        }
    }

    //根据属性id查询属性及其所有关联属性选项
    public ServerResponse queryAttributeValue(String id) {
        String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        try {
            Map<String, Object> gaMap = new HashMap<String, Object>();
            Attribute goodsAttribute = iAttributeMapper.queryById(id);
            if (goodsAttribute != null) {
                gaMap.put("id", goodsAttribute.getId());
                gaMap.put("name", goodsAttribute.getName());
                gaMap.put("type", goodsAttribute.getType());
                List<AttributeValue> avList = iAttributeValueMapper.queryByAttributeId(goodsAttribute.getId());
                List<Map<String, Object>> avListMap = new ArrayList<Map<String, Object>>();
                for (AttributeValue av : avList) {
                    Map<String, Object> avMap = new HashMap<String, Object>();
                    avMap.put("avId", av.getId());
                    avMap.put("avName", av.getName());
                    avMap.put("image", address + av.getImage());
                    avMap.put("imageUrl", av.getImage());
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
    public ServerResponse insertGoodsAttribute(String goodsCategoryId, String attributeName, Integer type, String jsonStr) {
        try {
//			List<Attribute> attributeList = iAttributeMapper.queryCategoryAttribute(goodsCategoryId);//弃用
            List<Attribute> attributeList = iAttributeMapper.queryAttributeByCategoryId(goodsCategoryId);
            LOG.info("attributeList size:" + attributeList.size());
            for (Attribute attribute : attributeList) {
                LOG.info("attributeList:" + attribute.getName() + " == " + attribute.getName());
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
            attribute.setName(attributeName);
            attribute.setType(type);
            attribute.setCategoryId(goodsCategoryId);
            iAttributeMapper.insert(attribute);

//			//保存商品类别和属性的关联
//			CategoryAttribute ca = new CategoryAttribute();
//			ca.setCategoryId(goodsCategoryId);
//			ca.setAttributeId(attribute.getId());
//			iAttributeMapper.insertCategoryAttribute(ca);

            for (int i = 0; i < jsonArr.size(); i++) {
                JSONObject obj = jsonArr.getJSONObject(i);
                AttributeValue attributeValue = new AttributeValue();
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
    public ServerResponse doModifyGoodsAttribute(String attributeId, String attributeName, Integer type, String jsonStr) {
        try {
            Attribute srcAttribute = iAttributeMapper.queryById(attributeId);
            LOG.info("doModifyGoodsAttribute::::Id: " + attributeId + " name:" + attributeName);
            //只需查询 一个分类 对应的所有的属性名称 有没有被使用
            List<Attribute> attributeList = iAttributeMapper.queryAttributeByCategoryId(srcAttribute.getCategoryId());
            for (Attribute ae : attributeList) {
                LOG.info(" name:" + ae.getName());
                if (ae.getName().equals(srcAttribute.getName())) {
                    return ServerResponse.createByErrorMessage("该属性名称已被使用");
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
                List<AttributeValue> attributeValueList = iAttributeValueMapper.queryByAttributeId(attributeId);
                for (AttributeValue attributeValue : attributeValueList) {
                    //检查前端 提交的json 是否有重复的属性选项名
                    if (jsonName.equals(attributeValue.getName())) {
                        useCount++;
                        if (useCount > 1)  //不能有 2个同样属性选项名 入库
                            return ServerResponse.createByErrorMessage("属性选项名已存在");
                    }
                }
            }

            List<Goods> goodsList = iGoodsMapper.queryByCategoryId(srcAttribute.getCategoryId());//根据分类id查询是否有关联商品
//            if (goodsList.size() > 0)
//                return ServerResponse.createByErrorMessage("修改失败，该分类属性已被其他商品使用");


//				//检查该分类中的所有商品，是否有商品使用 该属性名和属性选项名
//				for(Goods gs: goodsList)
//				{
//					LOG.info("gs name:"+ gs.getName());
//					//检查属性名已经存在   属性名是否有商品使用
//					List<Product> productList = iProductMapper.queryByGoodsId(gs.getId());
//					for(Product product: productList)
//					{
//						String[] valueIdArr = product.getValueNameArr().split(",");
//						for(int i=0;i<valueIdArr.length;i++)
//						{
//							Attribute ae = iAttributeMapper.queryById(valueIdArr[i]);
//							if(Arrays.asList(valueIdArr).contains(ae.getName()))
//								return ServerResponse.createByErrorMessage("修改失败，该属性选项名已被其他商品使用");
//						}
//
//						String[] attributeIdArr = product.getAttributeIdArr().split(",");
//						for(int i=0;i<attributeIdArr.length;i++)
//						{
//							Attribute ae = iAttributeMapper.queryById(attributeIdArr[i]);
//							if(Arrays.asList(valueIdArr).contains(jsonName))
//								return ServerResponse.createByErrorMessage("修改失败，该属性选项名已被其他商品使用");
//						}
//
//					}
//				}

            Attribute goodsAttribute = new Attribute();
            goodsAttribute.setId(attributeId);
            goodsAttribute.setName(attributeName);
            goodsAttribute.setType(type);
            goodsAttribute.setModifyDate(new Date());
            iAttributeMapper.updateByPrimaryKeySelective(goodsAttribute);
            for (int i = 0; i < jsonArr.size(); i++) {
                JSONObject obj = jsonArr.getJSONObject(i);
                AttributeValue attributeValue = new AttributeValue();
                if (obj.getString("id") == null || "".equals(obj.getString("id"))) {//新增
                    attributeValue.setAttributeId(goodsAttribute.getId());
                    attributeValue.setName(obj.getString("name"));
                    if (type == 1) {//是规格属性不用存图
                        attributeValue.setImage(obj.getString("image"));
                    }
                    attributeValue.setCreateDate(new Date());
                    attributeValue.setModifyDate(new Date());
                    iAttributeValueMapper.insert(attributeValue);
                } else {//修改
                    attributeValue.setId(obj.getString("id"));
                    attributeValue.setAttributeId(goodsAttribute.getId());
                    attributeValue.setName(obj.getString("name"));
                    if (type == 1) {//是规格属性不用存图
                        attributeValue.setImage(obj.getString("image"));
                    }
                    attributeValue.setModifyDate(new Date());
                    iAttributeValueMapper.updateByPrimaryKeySelective(attributeValue);
                }
            }
            return ServerResponse.createBySuccessMessage("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(ServerCode.WRONG_PARAM, "修改失败");
        }
    }

    /**
     * 删除商品属性
     */
    public ServerResponse deleteGoodsAttribute(String goodsAttributeId) {
        try {
            Attribute srcAttribute = iAttributeMapper.queryById(goodsAttributeId);
            List<Goods> goodsList = iGoodsMapper.queryByCategoryId(srcAttribute.getCategoryId());//根据分类id查询是否有关联商品
            if (goodsList.size() > 0)
                return ServerResponse.createByErrorMessage("该商品属性有关联商品不能删除");

            //检查该分类中的所有商品，是否有商品使用 该属性名和属性选项名
            for (Goods gs : goodsList) {
//				LOG.info("gs name:"+ gs.getName());
                //检查属性名已经存在   属性名是否有商品使用
                List<Product> productList = iProductMapper.queryByGoodsId(gs.getId());
                for (Product product : productList) {
                    String[] valueIdArr = product.getValueNameArr().split(",");
                    for (int i = 0; i < valueIdArr.length; i++) {
                        if (Arrays.asList(valueIdArr).contains(valueIdArr[i]))
                            return ServerResponse.createByErrorMessage("删除失败，该属性选项名已被其他商品使用");
                    }

                    String[] attributeIdArr = product.getAttributeIdArr().split(",");
                    for (int i = 0; i < attributeIdArr.length; i++) {
                        Attribute ae = iAttributeMapper.queryById(attributeIdArr[i]);
                        if (srcAttribute.getName().equals(ae.getName()))
                            return ServerResponse.createByErrorMessage("删除失败，该属性选项名已被其他商品使用");
                    }
                }
            }

            iAttributeMapper.deleteById(goodsAttributeId);//删除商品属性
            iAttributeValueMapper.deleteByAttributeId(goodsAttributeId);//删除属性选项
//			iAttributeMapper.deleteCategoryAttribute(goodsAttributeId);//删除商品类别和属性的关联  弃用
            return ServerResponse.createBySuccessMessage("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(ServerCode.WRONG_PARAM, "删除失败");
        }
    }

    /**
     * 删除商品属性选项
     */
    public ServerResponse deleteByAttributeId(String attributeValueId) {
        try {
//            AttributeValue srcAttributeValue = iAttributeValueMapper.queryById(attributeValueId);
//            Attribute srcAe = iAttributeMapper.queryById(srcAttributeValue.getAttributeId());
            AttributeValue srcAttributeValue = iAttributeValueMapper.selectByPrimaryKey(attributeValueId);
            Attribute srcAe = iAttributeMapper.selectByPrimaryKey(srcAttributeValue.getAttributeId());
            LOG.info("deleteByAttributeId  :" + srcAe.getCategoryId());
            List<Goods> goodsList = iGoodsMapper.queryByCategoryId(srcAe.getCategoryId());//根据分类id查询是否有关联商品
            if (goodsList.size() > 0)
                return ServerResponse.createByErrorMessage("该商品属性有关联商品不能删除");

            iAttributeValueMapper.deleteById(attributeValueId);//删除属性选项
            return ServerResponse.createBySuccessMessage("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(ServerCode.WRONG_PARAM, "删除失败");
        }
    }

}
