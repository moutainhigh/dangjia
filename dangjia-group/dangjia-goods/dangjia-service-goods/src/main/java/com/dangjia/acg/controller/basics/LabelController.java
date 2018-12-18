package com.dangjia.acg.controller.basics;

import com.dangjia.acg.api.basics.LabelAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.basics.LabelService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * 
   * @类 名： LabelController.java
   * @功能描述：  
   * @作者信息： ysl
   * @创建时间： 2018-12-11下午2:55:12
 */
@RestController
public class LabelController implements LabelAPI {
	/**
     *service
     */
    @Autowired
    private LabelService labelService;
    
    
    /**
     * 查询所有商品标签
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse<PageInfo> getAllLabel(HttpServletRequest request, PageDTO pageDTO){
    	return labelService.getAllLabel(pageDTO);
    }
    
    /**
     * 修改商品标签
     * @param labelId 标签ID
     * @param labelName	标签名称
     * @return	接口
     */
    @Override
    @ApiMethod
    public ServerResponse updateLabel(HttpServletRequest request,String labelId,String labelName){
    	return labelService.update(labelId, labelName);
    }
    /**
     * 新增商品标签
     * @param labelName 标签名称
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse insertLabel(HttpServletRequest request,String labelName){
    	return labelService.insert( labelName);
    }

    /**
     * 根据ID查询商品标签
     * @param labelId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse selectLabelById(HttpServletRequest request,String labelId){
    	return labelService.selectById(labelId);
    }
    /**
     * 根据ID删除商品标签
     * @param labelId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse deleteById(HttpServletRequest request,String labelId){
    	return labelService.deleteById(labelId);
    }
}
