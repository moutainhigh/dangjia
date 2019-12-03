package com.dangjia.acg.controller.product;

import com.dangjia.acg.api.product.BasicsGoodsCategoryAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.attribute.GoodsCategory;
import com.dangjia.acg.modle.product.BasicsGoodsCategory;
import com.dangjia.acg.service.basics.AttributeService;
import com.dangjia.acg.service.product.BasicsGoodsCategoryService;
import com.dangjia.acg.service.product.DjBasicsAttributeServices;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @类 名： BasicsGoodsCategoryController
 * @功能描述： 商品/服务类别管理
 * @作者信息： zmj
 * @创建时间： 2018-9-12下午1:55:15
 */
@RestController
public class BasicsGoodsCategoryController implements BasicsGoodsCategoryAPI {
    /**
     * service
     */
    @Autowired
    private BasicsGoodsCategoryService basicsGoodsCategoryService;
    @Autowired
    private DjBasicsAttributeServices djBasicsAttributeServices;

    @Override
    @ApiMethod
    public BasicsGoodsCategory getGoodsCategory(String cityId, String categoryId) {
        return basicsGoodsCategoryService.getGoodsCategory(categoryId);
    }

    /**
     * 商品分类类别查询
     *
     * @param cityId     城市ID
     * @param categoryId 类别Id
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getBasicsGoodsCategory(String categoryId,String cityId) {
        return basicsGoodsCategoryService.getBasicsGoodsCategory(categoryId,cityId);
    }

    /**
     * 添加分类类别
     *
     * @param request
     * @param name                 名称
     * @param parentId             上级ID
     * @param parentTop            顶级ID
     * @param sort                 排序
     * @param isLastCategory       是否末级分类（1是，0否）
     * @param purchaseRestrictions 购买限制（0自由购房；1有房无精算；2有房有精算）
     * @param brandIds             关联的品牌ID，多个逗号分割
     * @param coverImage           上传封面图
     * @param categoryLabelId      分类标签ID
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse insertBasicsGoodsCategory(HttpServletRequest request, String name, String parentId, String parentTop, Integer sort, String isLastCategory, String purchaseRestrictions, String brandIds, String coverImage, String categoryLabelId,String cityId,String ownerDisplay) {
        return basicsGoodsCategoryService.insertBasicsGoodsCategory(name, parentId, parentTop, sort, isLastCategory, purchaseRestrictions, brandIds, coverImage, categoryLabelId,cityId,ownerDisplay);
    }

    /**
     * 修改分类类别
     *
     * @param request
     * @param id                   类别ID
     * @param name                 名称
     * @param parentId             上级ID
     * @param parentTop            顶级ID
     * @param sort                 排序
     * @param isLastCategory       是否末级分类（1是，0否）
     * @param purchaseRestrictions 购买限制（0自由购房；1有房无精算；2有房有精算）
     * @param brandIds             关联的品牌ID，多个逗号分割
     * @param coverImage           上传封面图
     * @param categoryLabelId      分类标签ID
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse doModifyBasicsGoodsCategory(HttpServletRequest request, String id, String name,
                                                      String parentId, String parentTop,
                                                      Integer sort,
                                                      String isLastCategory,
                                                      String purchaseRestrictions,
                                                      String brandIds, String coverImage,
                                                      String categoryLabelId,
                                                      String cityId,
                                                      String ownerDisplay) {
        return basicsGoodsCategoryService.doModifyBasicsGoodsCategory(id, name, parentId, parentTop,
                sort, isLastCategory, purchaseRestrictions, brandIds, coverImage, categoryLabelId,cityId,ownerDisplay);
    }

    /**
     * 查询分类类别信息
     *
     * @param request
     * @param parentId 上级ID
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse queryBasicsGoodsCategory(HttpServletRequest request, String parentId) {
        return basicsGoodsCategoryService.queryBasicsGoodsCategory(parentId);
    }

    /**
     * 查询所有的末级分类类别
     * @param request
     * @param cityId
     * @param searchKey
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse queryLastCategoryList(HttpServletRequest request,String cityId,String searchKey){
        return basicsGoodsCategoryService.queryLastCategoryList(cityId,searchKey);
    }

    /**
     * 根据类别id查询关联属性
     *
     * @Title: getProduct
     */
    @Override
    @ApiMethod
    public ServerResponse<PageInfo> queryBasicsGoodsAttribute(HttpServletRequest request, PageDTO pageDTO,
                                                              String goodsCategoryId,
                                                              String likeAttrName,
                                                              String cityId) {
        return djBasicsAttributeServices.queryGoodsAttribute(pageDTO, goodsCategoryId, likeAttrName,cityId);
    }

    /**
     * 根据属性名称模糊查询属性
     *
     * @Title: getProduct
     */
    @Override
    @ApiMethod
    public ServerResponse<PageInfo> queryGoodsAttributelikeName(HttpServletRequest request,
                                                                PageDTO pageDTO,
                                                                String name,
                                                                String cityId) {
        return djBasicsAttributeServices.queryGoodsAttributelikeName(pageDTO, name,cityId);
    }

    /**
     * 根据属性id查询属性及其下属属性选项
     *
     * @Title: getProduct
     */
    @Override
    @ApiMethod
    public ServerResponse queryAttributeValue(HttpServletRequest request, String goodsAttributeId) {
        return djBasicsAttributeServices.queryAttributeValue(goodsAttributeId);
    }

    /**
     * 新增属性及其属性选项
     *
     * @Title: getProduct
     */
    @Override
    @ApiMethod
    public ServerResponse addGoodsAttribute(HttpServletRequest request, String goodsCategoryId,
                                            String attributeName, Integer type, String jsonStr,
                                            Integer isScreenConditions,
                                            String cityId) {
        return djBasicsAttributeServices.addGoodsAttribute(goodsCategoryId, attributeName, type,
                jsonStr, isScreenConditions,cityId);
    }

    /**
     * 修改属性及其属性选项
     *
     * @Title: getProduct
     */
    @Override
    @ApiMethod
    public ServerResponse updateGoodsAttribute(HttpServletRequest request, String attributeId,
                                               String attributeName, Integer type,
                                               String jsonStr,
                                               Integer isScreenConditions,
                                               String cityId) {
        return djBasicsAttributeServices.updateGoodsAttribute(attributeId, attributeName, type,
                jsonStr, isScreenConditions,cityId);
    }

    /**
     * 删除商品属性
     */
    @Override
    @ApiMethod
    public ServerResponse deleteGoodsAttribute(HttpServletRequest request, String goodsAttributeId) {
        return djBasicsAttributeServices.deleteGoodsAttribute(goodsAttributeId);
    }

    /**
     * 删除商品属性选项
     */
    @Override
    @ApiMethod
    public ServerResponse deleteByAttributeId(HttpServletRequest request, String attributeValueId) {
        return djBasicsAttributeServices.deleteByAttributeId(attributeValueId);
    }

    /**
     * 删除商品类别
     *
     * @Title: getProduct
     */
    @Override
    public ServerResponse deleteBasicsGoodsCategory(HttpServletRequest request, String id) {
        return basicsGoodsCategoryService.deleteGoodsCategory(id);
    }

    /**
     * 查询类别id查询所有父级以及父级属性
     *
     * @Title: getProduct
     */
    @Override
    @ApiMethod
    public ServerResponse queryAttributeListById(HttpServletRequest request, String goodsCategoryId) {
        return basicsGoodsCategoryService.queryAttributeListById(goodsCategoryId);
    }

    /**
     * 查询两级商品分类
     *
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse queryGoodsCategoryTwo(HttpServletRequest request) {
        return basicsGoodsCategoryService.queryGoodsCategoryTwo();
    }

    /**
     * 查询所有品牌
     *
     * @throws
     * @Title: queryBrand
     * @param: @return
     * @return: JsonResult
     */
    @Override
    @ApiMethod
    public ServerResponse queryBrand(HttpServletRequest request,String cityId) {

        return basicsGoodsCategoryService.queryBrand(cityId);
    }

    /**
     * 查询分类下的所有品牌
     *
     * @param request
     * @param categoryId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse queryBrandByCategoryId(HttpServletRequest request, String categoryId,String cityId) {
        return basicsGoodsCategoryService.queryBrandByCategoryId(categoryId,cityId);
    }

    /**
     * 查询商品类别列表,不包含末级分类
     *
     * @Title: getProduct
     */
    @Override
    @ApiMethod
    public ServerResponse queryGoodsCategoryExistlastCategory(HttpServletRequest request,
                                                              String parentId,
                                                              String cityId) {
        return basicsGoodsCategoryService.queryGoodsCategoryExistlastCategory(parentId,cityId);
    }

    /**
     * 查询对应的末级分类，及对应的货品，商品
     * @param request
     * @param pageDTO 分页
     * @param categoryId 类别ID
     * @param name  类别名称
     * @param cityId 城市ID
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse queryCategoryListByCategoryLikeName(HttpServletRequest request,
                                                        PageDTO pageDTO,
                                                        String categoryId,
                                                        String name,
                                                        String cityId){
        return basicsGoodsCategoryService.queryCategoryListByCategoryLikeName(pageDTO, categoryId, name,cityId);
    }
}
