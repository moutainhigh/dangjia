package com.dangjia.acg.service.product;

import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.BaseException;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.mapper.basics.IAttributeMapper;
import com.dangjia.acg.mapper.basics.IGoodsMapper;
import com.dangjia.acg.mapper.product.IBasicsGoodsCategoryMapper;
import com.dangjia.acg.modle.attribute.Attribute;
import com.dangjia.acg.modle.basics.Goods;
import com.dangjia.acg.modle.product.BasicsGoodsCategory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @类 名： ProductServiceImpl
 * @功能描述： 商品service实现类
 * @作者信息： fzh
 * @创建时间： 2019-9-11
 */
@Service
public class BasicsGoodsCategoryService {

    @Autowired
    private IBasicsGoodsCategoryMapper iBasicsGoodsCategoryMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IGoodsMapper iGoodsMapper;
    @Autowired
    private IAttributeMapper attributeMapper;

    public BasicsGoodsCategory getBasicsGoodsCategory(String categoryId) {
        String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
       BasicsGoodsCategory basicsGoodsCategory = iBasicsGoodsCategoryMapper.selectByPrimaryKey(categoryId);
       String coverImage=basicsGoodsCategory.getCoverImage();
       if(coverImage!=null&&!"".equalsIgnoreCase(coverImage)){
           basicsGoodsCategory.setCoverImage(address+coverImage);
       }
        return basicsGoodsCategory;
    }

    //新增商品类别
    public ServerResponse insertBasicsGoodsCategory(String name, String parentId, String parentTop, Integer sort, String isLastCategory, String purchaseRestrictions, String brandIds, String coverImage, String categoryLabelId) {
        try {
            List<BasicsGoodsCategory> goodsCategoryList = iBasicsGoodsCategoryMapper.queryCategoryByName(name);//根据name查询商品对象
            if (goodsCategoryList.size() > 0)
                return ServerResponse.createByErrorMessage("不能重复添加类别");
            BasicsGoodsCategory category = new BasicsGoodsCategory();
            category.setName(name);
            category.setParentId(parentId);
            category.setParentTop(parentTop);
            category.setImage("");
            if (sort == null) sort = 99;
            category.setSort(sort);
            category.setCreateDate(new Date());
            category.setModifyDate(new Date());
            category.setIsLastCategory(isLastCategory);
            category.setPurchaseRestrictions(purchaseRestrictions);
            category.setBrandIds(brandIds);
            category.setCoverImage(coverImage);
            category.setCategoryLabelId(categoryLabelId);
            iBasicsGoodsCategoryMapper.insert(category);
            return ServerResponse.createBySuccess("新增成功", category.getId());
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(ServerCode.WRONG_PARAM, "查询失败");
        }
    }

    //修改商品类别
    public ServerResponse doModifyBasicsGoodsCategory(String id, String name, String parentId, String parentTop, Integer sort, String isLastCategory, String purchaseRestrictions, String brandIds, String coverImage, String categoryLabelId) {
        try {
            BasicsGoodsCategory oldCategory = iBasicsGoodsCategoryMapper.selectByPrimaryKey(id);
            if (!oldCategory.getName().equals(name)) { //如果 是修改name
                List<BasicsGoodsCategory> goodsCategoryList = iBasicsGoodsCategoryMapper.queryCategoryByName(name);//根据name查询商品对象
                if (goodsCategoryList.size() > 0)
                    return ServerResponse.createByErrorMessage("该类别已存在");
            }

            BasicsGoodsCategory category = new BasicsGoodsCategory();
            category.setId(id);
            category.setName(name);
            category.setParentId(parentId);
            category.setParentTop(parentTop);
            if (sort != null) {
                category.setSort(sort);
            }
            category.setModifyDate(new Date());
            category.setIsLastCategory(isLastCategory);
            category.setPurchaseRestrictions(purchaseRestrictions);
            category.setBrandIds(brandIds);
            category.setCoverImage(coverImage);
            category.setCategoryLabelId(categoryLabelId);
            iBasicsGoodsCategoryMapper.updateByPrimaryKeySelective(category);
            return ServerResponse.createBySuccessMessage("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(ServerCode.WRONG_PARAM, "修改失败");
        }
    }

    //查询商品属性列表 queryGoodsCategory
    public ServerResponse queryBasicsGoodsCategory(String parentId) {
        List<BasicsGoodsCategory> goodsCategoryList = iBasicsGoodsCategoryMapper.queryCategoryByParentId(parentId);
        if (goodsCategoryList.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        return ServerResponse.createBySuccess("查询成功", goodsCategoryList);
    }

    //删除商品类别
    public ServerResponse deleteGoodsCategory(String id) {
        try {
            List<BasicsGoodsCategory> goodsCategoryList = iBasicsGoodsCategoryMapper.queryCategoryByParentId(id);//根据id查询是否有下级类别
            List<Goods> goodsList = iGoodsMapper.queryByCategoryId(id);//根据id查询是否有关联商品
            List<Attribute> GoodsAList = attributeMapper.queryAttributeByCategoryId(id, null);//根据id查询是否有关联属性
            if (goodsCategoryList.size() > 0) {
                return ServerResponse.createByErrorMessage("此类别有下级不能删除");
            }
            if (goodsList.size() > 0) {
                return ServerResponse.createByErrorMessage("此类别有关联商品不能删除");
            }
            if (GoodsAList.size() > 0) {
                return ServerResponse.createByErrorMessage("此类别有关联属性不能删除");
            }
            iBasicsGoodsCategoryMapper.deleteById(id);
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
            BasicsGoodsCategory goodsCategory = iBasicsGoodsCategoryMapper.selectByPrimaryKey(goodsCategoryId);
            if (goodsCategory == null) {
                return ServerResponse.createByErrorMessage("查询失败");
            }
            List<Attribute> gaList = attributeMapper.queryAttributeByCategoryId(goodsCategory.getId(), null);
            while (goodsCategory != null) {
                goodsCategory = iBasicsGoodsCategoryMapper.selectByPrimaryKey(goodsCategory.getParentId());
                if (goodsCategory != null) {
                    gaList.addAll(attributeMapper.queryAttributeByCategoryId(goodsCategory.getId(), null));
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
            List<Map<String, Object>> mapList = new ArrayList<>();
            List<BasicsGoodsCategory> goodsCategoryList = iBasicsGoodsCategoryMapper.queryCategoryByParentId("1");
            for (BasicsGoodsCategory goodsCategory : goodsCategoryList) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", goodsCategory.getId());
                map.put("name", goodsCategory.getName());
                List<Map<String, Object>> mapTwoList = new ArrayList<>();
                List<BasicsGoodsCategory> goodsCategoryList2 = iBasicsGoodsCategoryMapper.queryCategoryByParentId(goodsCategory.getId());
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
