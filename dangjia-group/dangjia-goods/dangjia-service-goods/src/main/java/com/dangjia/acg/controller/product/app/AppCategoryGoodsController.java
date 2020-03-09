package com.dangjia.acg.controller.product.app;

import com.dangjia.acg.api.product.app.AppCategoryGoodsAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.product.app.AppCategoryGoodsService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @类 名： AppCategoryGoodsController
 * @功能描述： 商品分类app端
 * @作者信息： QYX
 * @创建时间： 2019-9-16下午2:33:37
 */
@RestController
public class AppCategoryGoodsController implements AppCategoryGoodsAPI {
    /**
     * service
     */
    @Autowired
    private AppCategoryGoodsService appCategoryGoodsService;

    @Override
    @ApiMethod
    public ServerResponse queryTopCategoryLabel(String cityId) {
        return appCategoryGoodsService.queryTopCategoryLabel();
    }

    @Override
    @ApiMethod

    public ServerResponse queryLeftCategoryByDatas(String cityId,String categoryLabelId) {
        return appCategoryGoodsService.queryLeftCategoryByDatas(categoryLabelId);
    }

    @Override
    @ApiMethod
    public ServerResponse queryRightCategoryByDatas(String cityId,String parentId,String categoryLabelId) {
        return appCategoryGoodsService.queryRightCategoryByDatas(parentId, categoryLabelId);
    }

    /**
     * 第四部分：二级商品列表搜索页面
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse serchCategoryProduct(PageDTO pageDTO, String cityId, String categoryId,String goodsId,String name,String attributeVal, String brandVal,String orderKey){
        return appCategoryGoodsService.serchCategoryProduct(pageDTO,  cityId,  categoryId, goodsId,name, attributeVal,  brandVal, orderKey);
    }

    /**
     * 第四部分：二级商品品牌筛选数据
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse queryBrandDatas(String cityId,String categoryId,String wordKey) {
        return appCategoryGoodsService.queryBrandDatas(categoryId, wordKey);
    }
    /**
     * 第四部分：二级商品规格筛选数据
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse queryAttributeDatas(String cityId,String categoryId,String wordKey) {
        return appCategoryGoodsService.queryAttributeDatas(categoryId, wordKey);
    }

    /**
     * 查询维保商品的顶级分类
     * @param cityId 城市ID
     * @param workerTypeId 工种ID
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse queryMaintenanceRecordTopCategory(String cityId,String workerTypeId){
        return appCategoryGoodsService.queryMaintenanceRecordTopCategory(cityId, workerTypeId);
    }

    /**
     * 查询所有的符合条件的维保商品
     * @param cityId 城市ID
     * @param workerTypeId 工种ID
     * @param topCategoryId 类别ID
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse queryMaintenanceRecordProduct(PageDTO pageDTO,String cityId,String workerTypeId,String topCategoryId,String searchKey){
        return appCategoryGoodsService.queryMaintenanceRecordProduct(pageDTO,cityId, workerTypeId,topCategoryId,searchKey);
    }


}
