package com.dangjia.acg.controller.product;

import com.dangjia.acg.api.product.CatetgoryLabelAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.product.CategoryLabelService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Example;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @类 名： CategoryLabelController.java
 * @功能描述：
 * @作者信息： fzh
 * @创建时间： 2018-12-11下午2:55:12
 */
@RestController
public class CategoryLabelController implements CatetgoryLabelAPI {
    private static Logger logger = LoggerFactory.getLogger(CategoryLabelController.class);
    /**
     * service
     */
    @Autowired
    private CategoryLabelService categoryLabelService;


    /**
     * 查询所有类别标签
     *
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse<PageInfo> getAllCategoryLabel(HttpServletRequest request,String cityId) {
        return categoryLabelService.getAllCategoryLabel(cityId);
    }

    /**
     * 查询所有类别标签,不分页
     *
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getAllCategoryLabelList(HttpServletRequest request,String cityId) {
        return categoryLabelService.getAllCategoryLabelList(cityId);
    }

    /**
     * 修改类别标签
     *
     * @param labelId   标签ID
     * @param labelName 标签名称
     * @return 接口
     */
    @Override
    @ApiMethod
    public ServerResponse updateCategoryLabel(HttpServletRequest request, String labelId,
                                              String labelName,String cityId) {
        return categoryLabelService.update(labelId, labelName,cityId);
    }

    /**
     * 新增类别标签
     *
     * @param labelName 标签名称
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse insertCategoryLabel(HttpServletRequest request, String labelName,String cityId) {
        return categoryLabelService.insert(labelName,cityId);
    }

    /**
     * 根据ID查询类别标签
     *
     * @param labelId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse selectCategoryLabelById(HttpServletRequest request, String labelId) {
        return categoryLabelService.selectCategoryLabelById(labelId);
    }

    /**
     * 根据ID删除类别标签
     *
     * @param labelId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse deleteCategoryLabelById(HttpServletRequest request, String labelId) {
        return categoryLabelService.deleteCategoryLabelById(labelId);
    }

    /**
     * 修改标签排序
     * @param request
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getAllCategoryLabel(HttpServletRequest request,String beforeLabelId,
                                       Integer beforeSort,String afterLabelId,Integer afterSort){
        try{
            return categoryLabelService.getAllCategoryLabel(beforeLabelId,beforeSort,afterLabelId,afterSort);
        }catch(Exception e){
            logger.error("修改失败：",e);
            return ServerResponse.createByErrorMessage("修改失败");
        }


    }


}
