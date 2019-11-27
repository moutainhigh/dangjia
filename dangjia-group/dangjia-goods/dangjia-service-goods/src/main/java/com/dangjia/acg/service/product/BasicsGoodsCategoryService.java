package com.dangjia.acg.service.product;

import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.BaseException;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.product.CategoryGoodsProductDTO;
import com.dangjia.acg.mapper.basics.IBrandMapper;
import com.dangjia.acg.mapper.product.DjBasicsAttributeMapper;
import com.dangjia.acg.mapper.product.IBasicsGoodsCategoryMapper;
import com.dangjia.acg.mapper.product.IBasicsGoodsMapper;
import com.dangjia.acg.mapper.product.ICategorySeriesMapper;
import com.dangjia.acg.modle.brand.Brand;
import com.dangjia.acg.modle.product.BasicsGoods;
import com.dangjia.acg.modle.product.BasicsGoodsCategory;
import com.dangjia.acg.modle.product.CategorySeries;
import com.dangjia.acg.modle.product.DjBasicsAttribute;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static Logger logger = LoggerFactory.getLogger(BasicsGoodsCategoryService.class);
    @Autowired
    private IBasicsGoodsCategoryMapper iBasicsGoodsCategoryMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IBasicsGoodsMapper iBasicsGoodsMapper;
    @Autowired
    private ICategorySeriesMapper iCategorySeriesMapper;
    @Autowired
    private DjBasicsAttributeMapper djBasicsAttributeMapper;
    @Autowired
    private IBrandMapper iBrandMapper;

    public BasicsGoodsCategory getGoodsCategory(String categoryId) {
        return iBasicsGoodsCategoryMapper.selectByPrimaryKey(categoryId);
    }

    public ServerResponse getBasicsGoodsCategory(String categoryId,String cityId) {
        try {
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            BasicsGoodsCategory basicsGoodsCategory = iBasicsGoodsCategoryMapper.selectByPrimaryKey(categoryId);
            Map categoryMap = BeanUtils.beanToMap(basicsGoodsCategory);
            String image = basicsGoodsCategory.getImage();
            if (image != null && !"".equalsIgnoreCase(image)) {
                categoryMap.put("imageUrl",getImageStr(image,address));
            }
            String coverImage = basicsGoodsCategory.getCoverImage();
            if (coverImage != null && !"".equalsIgnoreCase(coverImage)) {
                categoryMap.put("coverImageUrl",getImageStr(coverImage,address));
            }

            List<Brand> bList = iBasicsGoodsCategoryMapper.queryBrandByCategoryid(categoryId,cityId);
            categoryMap.put("brands", bList);
            categoryMap.put("brandsIds", getBrandids(bList));
            return ServerResponse.createBySuccess("查询成功", categoryMap);
        } catch (Exception e) {
            logger.error("getBasicsGoodsCategory查询失败：", e);
            return ServerResponse.createByErrorMessage("查询失败");

        }
    }

    /**
     * 获取图片的详情路径
     * @param image
     * @return
     */
    private String getImageStr(String image,String address){
        String[] imgArr = image.split(",");
//                String[] technologyIds = obj.getString("technologyIds").split(",");//工艺节点
        StringBuilder imgStr = new StringBuilder();
        for (int j = 0; j < imgArr.length; j++) {
            String img = imgArr[j];
            if (j == imgArr.length - 1) {
                imgStr.append(address + img);
            } else {
                imgStr.append(address + img).append(",");
            }
        }
        return imgStr.toString();
    }

    /**
     * 获取品牌ID 字段，用逗号分隔
     * @param list
     * @return
     */
    private String getBrandids(List<Brand> list){
        String brandsIds="";
        if(list!=null&&list.size()>0){
            for(int i=0;i<list.size();i++) {
                Brand brand=list.get(i);
                if("".equals(brandsIds)){
                    brandsIds=brand.getId();
                }else{
                    brandsIds=brandsIds+","+brand.getId();
                }
            }
        }
        return brandsIds;
    }

    //新增商品类别
    public ServerResponse insertBasicsGoodsCategory(String name, String parentId,
                                                    String parentTop, Integer sort,
                                                    String isLastCategory,
                                                    String purchaseRestrictions,
                                                    String brandIds, String coverImage,
                                                    String categoryLabelId,
                                                    String cityId) {
        try {
            List<BasicsGoodsCategory> goodsCategoryList = iBasicsGoodsCategoryMapper.queryCategoryByName(name,cityId);//根据name查询商品对象
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
            category.setCoverImage(coverImage);
            category.setCategoryLabelId(categoryLabelId);
            category.setCityId(cityId);
            iBasicsGoodsCategoryMapper.insert(category);
            //如果品牌不为空，则添加品牌信息
            if (StringUtils.isNoneBlank(brandIds)) {
                String[] arr = brandIds.split(",");
                for (int i = 0; i < arr.length; i++) {//新增goods关联品牌系列
                    String brandId = arr[i];
                    CategorySeries gs = new CategorySeries();
                    gs.setCityId(cityId);
                    gs.setGoodsId(category.getId());
                    if (StringUtils.isNoneBlank(brandId)) {
                        gs.setBrandId(brandId);
                    }
                    iCategorySeriesMapper.insert(gs);
                }
            }
            return ServerResponse.createBySuccess("新增成功", category.getId());
        } catch (Exception e) {
            logger.error("insertBasicsGoodsCategory新增失败：", e);
            throw new BaseException(ServerCode.WRONG_PARAM, "新增失败");
        }
    }

    //修改商品类别
    public ServerResponse doModifyBasicsGoodsCategory(String id, String name, String parentId,
                                                      String parentTop, Integer sort,
                                                      String isLastCategory,
                                                      String purchaseRestrictions,
                                                      String brandIds, String coverImage,
                                                      String categoryLabelId,
                                                      String cityId) {
        try {
            BasicsGoodsCategory oldCategory = iBasicsGoodsCategoryMapper.selectByPrimaryKey(id);
            if (!oldCategory.getName().equals(name)) { //如果 是修改name
                List<BasicsGoodsCategory> goodsCategoryList = iBasicsGoodsCategoryMapper.queryCategoryByName(name,cityId);//根据name查询商品对象
                if (goodsCategoryList.size() > 0)
                    return ServerResponse.createByErrorMessage("该类别已存在");
            }

            BasicsGoodsCategory category = oldCategory;
            category.setCityId(cityId);
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
            category.setCoverImage(coverImage);
            category.setCategoryLabelId(categoryLabelId);
            iBasicsGoodsCategoryMapper.updateByPrimaryKeySelective(category);
            if (StringUtils.isNoneBlank(brandIds)) {
                iBasicsGoodsCategoryMapper.deleteCategorysSeries(category.getId());
                String[] arr = brandIds.split(",");
                for (int i = 0; i < arr.length; i++) {//新增goods关联品牌系列
                    String brandId = arr[i];
                    CategorySeries gs = new CategorySeries();
                    gs.setCityId(cityId);
                    gs.setGoodsId(category.getId());
                    if (StringUtils.isNoneBlank(brandId)) {
                        gs.setBrandId(brandId);
                    }
                    iCategorySeriesMapper.insert(gs);
                }
            }

            return ServerResponse.createBySuccessMessage("修改成功");
        } catch (Exception e) {
            logger.error("doModifyBasicsGoodsCategory修改失败：", e);
            throw new BaseException(ServerCode.WRONG_PARAM, "修改失败");
        }
    }

    //查询商品属性列表 queryGoodsCategory
    public ServerResponse queryBasicsGoodsCategory(String parentId) {
        List<BasicsGoodsCategory> goodsCategoryList = iBasicsGoodsCategoryMapper.queryCategoryByParentId(parentId, null);
        if (goodsCategoryList.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        return ServerResponse.createBySuccess("查询成功", goodsCategoryList);
    }

    public ServerResponse queryLastCategoryList(String cityId,String searchKey){
        List<BasicsGoodsCategory> goodsCategoryList = iBasicsGoodsCategoryMapper.queryLastCategoryList(cityId, searchKey);
        if (goodsCategoryList.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        return ServerResponse.createBySuccess("查询成功", goodsCategoryList);
    }
    //删除商品类别
    public ServerResponse deleteGoodsCategory(String id) {
        try {
            List<BasicsGoodsCategory> goodsCategoryList = iBasicsGoodsCategoryMapper.queryCategoryByParentId(id, null);//根据id查询是否有下级类别
            List<BasicsGoods> goodsList = iBasicsGoodsMapper.queryByCategoryId(id,null);//根据id查询是否有关联商品
            List<DjBasicsAttribute> GoodsAList = djBasicsAttributeMapper.queryAttributeByCategoryId(id, null);//根据id查询是否有关联属性
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
            logger.error("deleteGoodsCategory删除失败：", e);
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
            List<DjBasicsAttribute> gaList = djBasicsAttributeMapper.queryAttributeByCategoryId(goodsCategory.getId(), null);
            while (goodsCategory != null) {
                goodsCategory = iBasicsGoodsCategoryMapper.selectByPrimaryKey(goodsCategory.getParentId());
                if (goodsCategory != null) {
                    gaList.addAll(djBasicsAttributeMapper.queryAttributeByCategoryId(goodsCategory.getId(), null));
                }
            }
            return ServerResponse.createBySuccess("查询成功", gaList);
        } catch (Exception e) {
            logger.error("queryAttributeListById查询失败：", e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    //查询两级商品分类
    public ServerResponse queryGoodsCategoryTwo() {
        try {
            List<Map<String, Object>> mapList = new ArrayList<>();
            List<BasicsGoodsCategory> goodsCategoryList = iBasicsGoodsCategoryMapper.queryCategoryByParentId("1", null);
            for (BasicsGoodsCategory goodsCategory : goodsCategoryList) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", goodsCategory.getId());
                map.put("name", goodsCategory.getName());
                List<Map<String, Object>> mapTwoList = new ArrayList<>();
                List<BasicsGoodsCategory> goodsCategoryList2 = iBasicsGoodsCategoryMapper.queryCategoryByParentId(goodsCategory.getId(), null);
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
            logger.error("queryGoodsCategoryTwo查询失败：", e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    //查询品牌
    public ServerResponse queryBrand(String cityId) {
        try {
            List<Brand> brandList = iBrandMapper.getBrands(cityId);
            return ServerResponse.createBySuccess("查询成功", brandList);
        } catch (Exception e) {
            logger.error("queryBrand查询失败：", e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 查询当前分类下的所有品处信息
     *
     * @param categoryId
     * @return
     */
    public ServerResponse queryBrandByCategoryId(String categoryId,String cityId) {

        try {
            List<Brand> bList = iBasicsGoodsCategoryMapper.queryBrandByCategoryid(categoryId,cityId);
            return ServerResponse.createBySuccess("查询成功", bList);
        } catch (Exception e) {
            logger.error("queryBrandByCategoryId查询失败：", e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    //查询商品属性列表 queryGoodsCategory
    public ServerResponse queryGoodsCategoryExistlastCategory(String parentId,String cityId) {
        List<BasicsGoodsCategory> goodsCategoryList = iBasicsGoodsCategoryMapper.queryGoodsCategoryExistlastCategory(parentId,cityId);
        if (goodsCategoryList.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        return ServerResponse.createBySuccess("查询成功", goodsCategoryList);
    }

    /**
     * 模糊查询goods及下属product
     *
     * @param pageDTO
     * @param categoryId
     * @param goodsName 货品名称
     * @return
     */
    public ServerResponse queryCategoryListByCategoryLikeName(PageDTO pageDTO, String categoryId, String goodsName,String cityId) {
        try {
            logger.info("queryCategoryListByCategoryLikeName type :" + categoryId);
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<CategoryGoodsProductDTO> categoryGoodsProductList  = iBasicsGoodsCategoryMapper.queryCategoryListByCategoryLikeName(categoryId,goodsName,cityId);
            PageInfo pageResult = new PageInfo(categoryGoodsProductList);
            pageResult.setList(categoryGoodsProductList);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

}
