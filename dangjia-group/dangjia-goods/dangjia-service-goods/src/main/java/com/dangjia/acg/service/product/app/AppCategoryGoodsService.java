package com.dangjia.acg.service.product.app;

import com.alibaba.fastjson.JSONArray;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.actuary.AttributeDTO;
import com.dangjia.acg.dto.actuary.app.ActuarialProductAppDTO;
import com.dangjia.acg.mapper.basics.IUnitMapper;
import com.dangjia.acg.mapper.product.*;
import com.dangjia.acg.modle.brand.Brand;
import com.dangjia.acg.modle.product.BasicsGoodsCategory;
import com.dangjia.acg.modle.product.CategoryLabel;
import com.dangjia.acg.modle.product.DjBasicsMaintain;
import com.dangjia.acg.service.actuary.app.SearchActuarialConfigServices;
import com.dangjia.acg.util.StringTool;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @类 名： AppCategoryGoodsService
 * @功能描述： 商品分类app端service实现类
 * @作者信息： QYX
 * @创建时间： 2019-9-16下午2:33:37
 */
@Service
public class AppCategoryGoodsService {

    @Autowired
    private IUnitMapper iUnitMapper;
    @Autowired
    private ICategoryLabelMapper iCategoryLabelMapper;
    @Autowired
    private IBasicsGoodsCategoryMapper iBasicsGoodsCategoryMapper;

    @Autowired
    private DjBasicsAttributeMapper djBasicsAttributeMapper;
    @Autowired
    private DjBasicsMaintainMapper djBasicsMaintainMapper;

    @Autowired
    private IBasicsProductTemplateMapper iBasicsProductTemplateMapper;
    @Autowired
    private ConfigUtil configUtil;


    @Autowired
    private SearchActuarialConfigServices searchActuarialConfigServices;

    /************************APP 商品3.0 分类模块********************************/
    /**
     * 第一部分：顶部标签集合
     * @return
     */
    public ServerResponse queryTopCategoryLabel() {
        List<CategoryLabel> list =new ArrayList<>();
        CategoryLabel categoryLabel =new CategoryLabel();
        categoryLabel.setName("全部");
        categoryLabel.setId("");
        list.add(categoryLabel);
        List<CategoryLabel> goodsCategoryList = iCategoryLabelMapper.queryAPPCategoryLabel();
        list.addAll(goodsCategoryList);
        return ServerResponse.createBySuccess("查询成功", list);
    }

    /**
     * 第二部分：左侧分类集合
     * @return
     */

    public ServerResponse queryLeftCategoryByDatas(String categoryLabelId) {
        List<BasicsGoodsCategory> goodsCategoryList = iBasicsGoodsCategoryMapper.queryCategoryByParentId("1",categoryLabelId);
        return ServerResponse.createBySuccess("查询成功", goodsCategoryList);
    }

    /**
     * 第三部分：右侧侧分类集合
     * @return
     */

    public ServerResponse queryRightCategoryByDatas(String parentId) {
        //查询两级商品分类
        try {
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            List<Map<String, Object>> mapList = new ArrayList<>();
            Map<String, Object> mapBrand = new HashMap<>();
            mapBrand.put("name","推荐品牌");
            mapBrand.put("type",1);//type: 0=分类ID  1=品牌ID
            List<Map<String, Object>> mapTwoBrandList = new ArrayList<>();
            List<Brand> brands = iBasicsGoodsCategoryMapper.queryBrandByTopCategoryid(parentId,null);
            for (Brand brand : brands) {
                Map<String, Object> mapTwo = new HashMap<>();
                mapTwo.put("id", brand.getId());
                mapTwo.put("name", brand.getName());
                mapTwo.put("image", address+brand.getImage());
                mapTwo.put("type",1);//type: 0=分类ID  1=品牌ID
                mapTwoBrandList.add(mapTwo);
            }
            mapBrand.put("nextList", mapTwoBrandList);
            mapList.add(mapBrand);

            List<BasicsGoodsCategory> goodsCategoryList = iBasicsGoodsCategoryMapper.queryCategoryByParentId(parentId,null);
            for (BasicsGoodsCategory goodsCategory : goodsCategoryList) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", goodsCategory.getId());
                map.put("name", goodsCategory.getName());
                map.put("image", address+goodsCategory.getImage());
                map.put("type",0);//type: 0=分类ID  1=品牌ID
                List<Map<String, Object>> mapTwoList = new ArrayList<>();
                List<BasicsGoodsCategory> goodsCategoryList2 = iBasicsGoodsCategoryMapper.queryCategoryByParentId(goodsCategory.getId(),null);
                for (BasicsGoodsCategory goodsCategory2 : goodsCategoryList2) {
                    Map<String, Object> mapTwo = new HashMap<>();
                    mapTwo.put("id", goodsCategory2.getId());
                    mapTwo.put("name", goodsCategory2.getName());
                    mapTwo.put("image", address+goodsCategory.getImage());
                    mapTwo.put("type",0);//type: 0=分类ID  1=品牌ID
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

    /**
     * 第四部分：二级商品列表搜索页面
     * @return
     */
    public ServerResponse serchCategoryProduct(PageDTO pageDTO, String cityId, String categoryId,String goodsId,String name,String attributeVal, String brandVal,String orderKey) {
        JSONArray arr = new JSONArray();
        PageInfo pageResult = null;
        try {
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            if(!CommonUtil.isEmpty(name)){
                Example example = new Example(DjBasicsMaintain.class);
                example.createCriteria().andCondition(" FIND_IN_SET(search_item,'"+name+"')");
                //根据搜索词,查询关键词名称
                List<DjBasicsMaintain> list = djBasicsMaintainMapper.selectByExample(example);
                if(list.size() > 0){
                    name = list.get(0).getSearchItem();
                }
            }

            //根据内容模糊搜索商品
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            String[] attributeVals=null;
            if(!CommonUtil.isEmpty(attributeVal)){
                attributeVals=attributeVal.split(",");
            }
            String[] brandVals = null;
            if (!CommonUtil.isEmpty(brandVal)) {
                brandVals = brandVal.split(",");
            }
            String[] names=null;
            if(!CommonUtil.isEmpty(name)){
                names=name.split(",");
            }
            List<ActuarialProductAppDTO> pList = iBasicsProductTemplateMapper.serchCategoryProduct(categoryId,goodsId,names,brandVals,attributeVals,orderKey);
            pageResult = new PageInfo<>(pList);
            searchActuarialConfigServices.getProductList(pList,address);
            pageResult.setList(pList);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
        return ServerResponse.createBySuccess("查询成功", pageResult);
    }

    /**
     * 第四部分：二级商品品牌筛选数据
     * @return
     */
    public ServerResponse queryBrandDatas(String categoryId,String wordKey) {
        List<Brand> brands = iBasicsGoodsCategoryMapper.queryBrandByTopCategoryid(categoryId,StringTool.getLikeV(wordKey));
        return ServerResponse.createBySuccess("查询成功", brands);
    }
    /**
     * 第四部分：二级商品规格筛选数据
     * @return
     */
    public ServerResponse queryAttributeDatas(String categoryId,String wordKey) {
        List<AttributeDTO> attributeDTOS = djBasicsAttributeMapper.queryAttributeDatas(categoryId,StringTool.getLikeV(wordKey));
        return ServerResponse.createBySuccess("查询成功", attributeDTOS);
    }


    /************************APP 商品3.0 分类模块********************************/
}
