package com.dangjia.acg.controller.sale.royalty;


import com.dangjia.acg.api.sale.royalty.RoyaltyAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.sale.royalty.RoyaltyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 提成配置模块
 * Created with IntelliJ IDEA.
 * author: ljl
 * Date: 2019/7/26
 * Time: 16:16
 */
@RestController
public class RoyaltyController implements RoyaltyAPI {

    @Autowired
    private RoyaltyService service;

    /**
     * 查询提成列表
     * @param request
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse queryRoyaltySurface(HttpServletRequest request, PageDTO pageDTO) {
        return service.queryRoyaltySurface(pageDTO);
    }

    /**
     * 新增提成信息
     * @param request
     * @param lists
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse addRoyaltyData(HttpServletRequest request,
                                         String lists){
        return service.addRoyaltyData(lists);
    }

    /**
     * 查询提成详细信息
     * @param request
     * @param id
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse queryRoyaltyData(HttpServletRequest request,String id){
        return service.queryRoyaltyData(id);
    }



    /**
     * 新增房屋提成信息
     * @param request
     * @param lists
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse addAreaMatch(HttpServletRequest request,
                                         String lists,
                                       String villageId,
                                       String villageName,
                                       String buildingName,
                                       String buildingId,
                                       String resourceId){
        return service.addAreaMatch(lists,villageId,villageName,buildingName,buildingId,resourceId);
    }



    /**
     * 查询楼栋配置详情
     * @param request
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse queryAreaMatchInFo(HttpServletRequest request,String resourceId){
        return service.queryAreaMatchInFo(resourceId);
    }


    /**
     *查询提成配置表
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse queryAreaMatch(String villageId) {
        return service.queryAreaMatch(villageId);
    }


    /**
     * 删除提成配置表
     * @param id
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse delAreaMatch(String id) {
        return service.delAreaMatch(id);
    }

}
