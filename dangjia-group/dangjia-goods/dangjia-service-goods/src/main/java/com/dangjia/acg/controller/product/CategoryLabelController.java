package com.dangjia.acg.controller.product;

import com.dangjia.acg.api.product.CatetgoryLabelAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.product.CategoryLabelService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 *
 *
 * @类 名： CategoryLabelController.java
 * @功能描述：
 * @作者信息： fzh
 * @创建时间： 2018-12-11下午2:55:12
 */
@RestController
public class CategoryLabelController implements CatetgoryLabelAPI {
    /**
     *service
     */
    @Autowired
    private CategoryLabelService categoryLabelService;


    /**
     * 查询所有类别标签
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse<PageInfo> getAllCategoryLabel(HttpServletRequest request, PageDTO pageDTO){
        return categoryLabelService.getAllCategoryLabel(pageDTO);
    }

    /**
     * 修改类别标签
     * @param labelId 标签ID
     * @param labelName	标签名称
     * @return	接口
     */
    @Override
    @ApiMethod
    public ServerResponse updateCategoryLabel(HttpServletRequest request,String labelId,String labelName){
        return categoryLabelService.update(labelId, labelName);
    }
    /**
     * 新增类别标签
     * @param labelName 标签名称
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse insertCategoryLabel(HttpServletRequest request,String labelName){
        return categoryLabelService.insert( labelName);
    }

    /**
     * 根据ID查询类别标签
     * @param labelId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse selectCategoryLabelById(HttpServletRequest request,String labelId){
        return categoryLabelService.selectCategoryLabelById(labelId);
    }
    /**
     * 根据ID删除类别标签
     * @param labelId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse deleteCategoryLabelById(HttpServletRequest request,String labelId){
        return categoryLabelService.deleteCategoryLabelById(labelId);
    }


}
