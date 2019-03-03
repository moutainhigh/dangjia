package com.dangjia.acg.service.basics;

import com.dangjia.acg.common.exception.BaseException;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.mapper.basics.IAttributeMapper;
import com.dangjia.acg.mapper.basics.IGoodsCategoryMapper;
import com.dangjia.acg.mapper.basics.IGoodsMapper;
import com.dangjia.acg.modle.attribute.Attribute;
import com.dangjia.acg.modle.attribute.GoodsCategory;
import com.dangjia.acg.modle.basics.Goods;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @类 名： ProductServiceImpl
 * @功能描述： 商品service实现类
 * @作者信息： zmj
 * @创建时间： 2018-9-10下午2:33:37
 */
@Service
public class GoodsCategoryService {

    @Autowired
    private IGoodsCategoryMapper iGoodsCategoryMapper;
    @Autowired
    private IGoodsMapper iGoodsMapper;
    @Autowired
    private IAttributeMapper attributeMapper;
    private static Logger LOG = LoggerFactory.getLogger(GoodsCategoryService.class);

    //新增商品类别
    public ServerResponse insertGoodsCategory(String name, String parentId, String parentTop) {
        try {
//            List<GoodsCategory> goodsCategoryList = iGoodsCategoryMapper.queryCategoryByParentId(parentId);//根据id查询是否有下级类别
//            for (GoodsCategory goodsCategory : goodsCategoryList) {
//                if (goodsCategory.getName().equals(name))
//                    return ServerResponse.createByErrorMessage("不能重复添加类别");
//            }
            List<GoodsCategory> goodsCategoryList = iGoodsCategoryMapper.queryCategoryByName(name);//根据name查询商品对象
            if (goodsCategoryList.size() > 0)
                return ServerResponse.createByErrorMessage("不能重复添加类别");

            GoodsCategory category = new GoodsCategory();
            category.setName(name);
            category.setParentId(parentId);
            category.setParentTop(parentTop);
            category.setImage("");
            category.setCreateDate(new Date());
            category.setModifyDate(new Date());
            iGoodsCategoryMapper.insert(category);
            return ServerResponse.createBySuccess("新增成功", category.getId());
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(ServerCode.WRONG_PARAM, "查询失败");
        }
    }

    //修改商品类别
    public ServerResponse doModifyGoodsCategory(String id, String name, String parentId, String parentTop) {
        try {
//            List<GoodsCategory> goodsCategoryList = iGoodsCategoryMapper.queryCategoryByParentId(parentId);//根据id查询是否有下级类别
//            for (GoodsCategory goodsCategory : goodsCategoryList) {
//                if (goodsCategory.getName().equals(name))
//                    return ServerResponse.createByErrorMessage("该类别已存在");
//            }
            GoodsCategory oldCategory = iGoodsCategoryMapper.selectByPrimaryKey(id);
            if (!oldCategory.getName().equals(name)) { //如果 是修改name
                List<GoodsCategory> goodsCategoryList = iGoodsCategoryMapper.queryCategoryByName(name);//根据name查询商品对象
                if (goodsCategoryList.size() > 0)
                    return ServerResponse.createByErrorMessage("该类别已存在");
            }

            GoodsCategory category = new GoodsCategory();
            category.setId(id);
            category.setName(name);
            category.setParentId(parentId);
            category.setParentTop(parentTop);
            category.setModifyDate(new Date());
            iGoodsCategoryMapper.updateByPrimaryKeySelective(category);
            return ServerResponse.createBySuccessMessage("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(ServerCode.WRONG_PARAM, "修改失败");
        }
    }

    //查询商品属性列表 queryGoodsCategory
    public ServerResponse queryGoodsCategory(String parentId) {
        try {
            List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
            List<GoodsCategory> goodsCategoryList = iGoodsCategoryMapper.queryCategoryByParentId(parentId);
            for (GoodsCategory goodsCategory : goodsCategoryList) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("id", goodsCategory.getId());
                map.put("name", goodsCategory.getName());
                mapList.add(map);
            }
            return ServerResponse.createBySuccess("查询成功", mapList);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    //删除商品类别
    public ServerResponse deleteGoodsCategory(String id) {
        try {
            List<GoodsCategory> goodsCategoryList = iGoodsCategoryMapper.queryCategoryByParentId(id);//根据id查询是否有下级类别
            List<Goods> goodsList = iGoodsMapper.queryByCategoryId(id);//根据id查询是否有关联商品
//			List<Attribute> GoodsAList=attributeMapper.queryCategoryAttribute(id);//根据id查询是否有关联属性 （弃用）
            List<Attribute> GoodsAList = attributeMapper.queryAttributeByCategoryId(id);//根据id查询是否有关联属性
            if (goodsCategoryList.size() > 0) {
                return ServerResponse.createByErrorMessage("此类别有下级不能删除");
            }
            if (goodsList.size() > 0) {
                return ServerResponse.createByErrorMessage("此类别有关联商品不能删除");
            }
            if (GoodsAList.size() > 0) {
                return ServerResponse.createByErrorMessage("此类别有关联属性不能删除");
            }
            iGoodsCategoryMapper.deleteById(id);
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
            GoodsCategory goodsCategory = iGoodsCategoryMapper.selectByPrimaryKey(goodsCategoryId);
            if (goodsCategory == null) {
                return ServerResponse.createByErrorMessage("查询失败");
            }
//			List<Attribute> gaList=attributeMapper.queryCategoryAttribute(goodsCategory.getId());//弃用
            List<Attribute> gaList = attributeMapper.queryAttributeByCategoryId(goodsCategory.getId());
            while (goodsCategory != null) {
                goodsCategory = iGoodsCategoryMapper.selectByPrimaryKey(goodsCategory.getParentId());
                if (goodsCategory != null) {
//					gaList.addAll(attributeMapper.queryCategoryAttribute(goodsCategory.getId()));//弃用
                    gaList.addAll(attributeMapper.queryAttributeByCategoryId(goodsCategory.getId()));
                }
            }
            return ServerResponse.createBySuccess("查询成功", gaList);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    //查询两级商品分类
    public ServerResponse queryGoodsCategoryTwo() {
        try {
            List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
            List<GoodsCategory> goodsCategoryList = iGoodsCategoryMapper.queryCategoryByParentId("1");
            for (GoodsCategory goodsCategory : goodsCategoryList) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("id", goodsCategory.getId());
                map.put("name", goodsCategory.getName());
                List<Map<String, Object>> mapTwoList = new ArrayList<Map<String, Object>>();
                List<GoodsCategory> goodsCategoryList2 = iGoodsCategoryMapper.queryCategoryByParentId(goodsCategory.getId());
                for (GoodsCategory goodsCategory2 : goodsCategoryList2) {
                    Map<String, Object> mapTwo = new HashMap<String, Object>();
                    mapTwo.put("id", goodsCategory2.getId());
                    mapTwo.put("name", goodsCategory2.getName());
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
