package com.dangjia.acg.service.basics;

import com.dangjia.acg.common.exception.BaseException;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.mapper.basics.IAttributeMapper;
import com.dangjia.acg.mapper.basics.IGoodsCategoryMapper;
import com.dangjia.acg.mapper.basics.IGoodsMapper;
import com.dangjia.acg.modle.attribute.Attribute;
import com.dangjia.acg.modle.attribute.GoodsCategory;
import com.dangjia.acg.modle.product.BasicsGoods;
import com.dangjia.acg.modle.product.BasicsGoodsCategory;
import org.apache.commons.lang3.StringUtils;
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

    public BasicsGoodsCategory getGoodsCategory(String categoryId) {
        return iGoodsCategoryMapper.selectByPrimaryKey(categoryId);
    }

    //新增商品类别
    public ServerResponse insertGoodsCategory(String name, String parentId, String parentTop, Integer sort,String cityId) {
        try {
            List<BasicsGoodsCategory> goodsCategoryList = iGoodsCategoryMapper.queryCategoryByName(name,cityId);//根据name查询商品对象
            if (goodsCategoryList.size() > 0)
                return ServerResponse.createByErrorMessage("不能重复添加类别");
            BasicsGoodsCategory category = new BasicsGoodsCategory();
            category.setCityId(cityId);
            category.setName(name);
            category.setParentId(parentId);
            category.setParentTop(parentTop);
            category.setImage("");
            if (sort == null) sort = 99;
            category.setSort(sort);
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
    public ServerResponse doModifyGoodsCategory(String id, String name, String parentId, String parentTop, Integer sort,String cityId) {
        try {
            BasicsGoodsCategory oldCategory = iGoodsCategoryMapper.selectByPrimaryKey(id);
            if (!oldCategory.getName().equals(name)) { //如果 是修改name
                List<BasicsGoodsCategory> goodsCategoryList = iGoodsCategoryMapper.queryCategoryByName(name,cityId);//根据name查询商品对象
                if (goodsCategoryList.size() > 0)
                    return ServerResponse.createByErrorMessage("该类别已存在");
            }

            BasicsGoodsCategory category = new BasicsGoodsCategory();
            category.setCityId(cityId);
            category.setId(id);
            category.setName(name);
            category.setParentId(parentId);
            category.setParentTop(parentTop);
            if (sort != null) {
                category.setSort(sort);
            }
            category.setModifyDate(new Date());
            iGoodsCategoryMapper.updateByPrimaryKeySelective(category);
            return ServerResponse.createBySuccessMessage("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(ServerCode.WRONG_PARAM, "修改失败");
        }
    }

    //查询商品属性列表 queryGoodsCategory
    public ServerResponse queryGoodsCategory(String parentId,String cityId) {
        List<BasicsGoodsCategory> goodsCategoryList = iGoodsCategoryMapper.queryCategoryByParentId(parentId,cityId);
        if (goodsCategoryList.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        return ServerResponse.createBySuccess("查询成功", goodsCategoryList);
    }

    //删除商品类别
    public ServerResponse deleteGoodsCategory(String id,String cityId) {
        try {
            List<BasicsGoodsCategory> goodsCategoryList = iGoodsCategoryMapper.queryCategoryByParentId(id,cityId);//根据id查询是否有下级类别
            List<BasicsGoods> goodsList = iGoodsMapper.queryByCategoryId(id);//根据id查询是否有关联商品
            List<Attribute> GoodsAList = attributeMapper.queryAttributeByCategoryId(id, null,cityId);//根据id查询是否有关联属性
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
    public ServerResponse queryAttributeListById(String goodsCategoryId,String cityId) {
        try {
            if (!StringUtils.isNoneBlank(goodsCategoryId)) {
                return ServerResponse.createByErrorMessage("goodsCategoryId不能为null");
            }
            BasicsGoodsCategory goodsCategory = iGoodsCategoryMapper.selectByPrimaryKey(goodsCategoryId);
            if (goodsCategory == null) {
                return ServerResponse.createByErrorMessage("查询失败");
            }
            List<Attribute> gaList = attributeMapper.queryAttributeByCategoryId(goodsCategory.getId(), null,cityId);
            while (goodsCategory != null) {
                goodsCategory = iGoodsCategoryMapper.selectByPrimaryKey(goodsCategory.getParentId());
                if (goodsCategory != null) {
                    gaList.addAll(attributeMapper.queryAttributeByCategoryId(goodsCategory.getId(), null,cityId));
                }
            }
            return ServerResponse.createBySuccess("查询成功", gaList);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    //查询两级商品分类
    public ServerResponse queryGoodsCategoryTwo(String cityId) {
        try {
            List<Map<String, Object>> mapList = new ArrayList<>();
            List<BasicsGoodsCategory> goodsCategoryList = iGoodsCategoryMapper.queryCategoryByParentId("1",cityId);
            for (BasicsGoodsCategory goodsCategory : goodsCategoryList) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", goodsCategory.getId());
                map.put("name", goodsCategory.getName());
                List<Map<String, Object>> mapTwoList = new ArrayList<>();
                List<BasicsGoodsCategory> goodsCategoryList2 = iGoodsCategoryMapper.queryCategoryByParentId(goodsCategory.getId(),cityId);
                for (BasicsGoodsCategory goodsCategory2 : goodsCategoryList2) {
                    Map<String, Object> mapTwo = new HashMap<>();
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
