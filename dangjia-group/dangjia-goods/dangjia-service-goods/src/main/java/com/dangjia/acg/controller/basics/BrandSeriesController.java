package com.dangjia.acg.controller.basics;

import com.dangjia.acg.api.basics.BrandSeriesAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.basics.BrandSeriesService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * 
   * @类 名： BrandExpalinController.java
   * @功能描述：  
   * @作者信息： hb
   * @创建时间： 2018-9-13下午5:28:18
 */
@RestController
public class BrandSeriesController implements BrandSeriesAPI {
	 /**
     *service
     */
	 @Autowired
    private BrandSeriesService brandExplainService;
    /**
     * 查询所有
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse<PageInfo>  getAllBrandExplain(HttpServletRequest request, PageDTO pageDTO){
    	return brandExplainService.getAllBrandExplain(pageDTO.getPageNum(),pageDTO.getPageSize());
    }
    /**
     * 修改
     * @param id
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse updateBrandExplain(HttpServletRequest request,String id,String name,String content){
    	return brandExplainService.update(id, name,content);
    }
    /**
     * 新增
     * @param brandId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse insetBrandExplain(HttpServletRequest request,String name,String content,String brandId){
    	return brandExplainService.insert(name,content, brandId);
    }
    /**
     * 删除
     * @param id
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse deleteBrandExplain(HttpServletRequest request,String id){
        return brandExplainService.deleteBrandExplain(id);
    }
}
