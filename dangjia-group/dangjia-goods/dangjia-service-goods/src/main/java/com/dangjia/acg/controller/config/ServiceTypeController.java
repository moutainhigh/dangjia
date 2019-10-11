package com.dangjia.acg.controller.config;

import com.dangjia.acg.api.config.ServiceTypeAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.config.ServiceType;
import com.dangjia.acg.service.config.ServiceTypeService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @类 名： ServiceTypeController
 */
@RestController
public class ServiceTypeController implements ServiceTypeAPI {

    @Autowired
    private ServiceTypeService serviceTypeService;


    /**
     * 根据ID查询服务详情
     * @param id
     * @return
     */
    @Override
    @ApiMethod
    public ServiceType getServiceTypeById(String cityId, String id) {
        return serviceTypeService.getServiceTypeById(id);
    }
    /**
     * 根据ID查询服务详情
     * @param request
     * @param id
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse selectServiceTypeById(HttpServletRequest request, String id) {
        return serviceTypeService.selectServiceTypeById(id);
    }

    /**
     * 查询服务类型
     * @param request
     * @param pageDTO
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse<PageInfo> selectServiceTypeList(HttpServletRequest request, PageDTO pageDTO) {

        return serviceTypeService.selectServiceTypeList(pageDTO);
    }

    /**
     * 修改服务类型
     * @param request
     * @param id
     * @param name
     * @param image
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse updateServiceType(HttpServletRequest request, String id, String name, String image) {
        return serviceTypeService.updateServiceType(id,name,image);
    }

    /**
     * 添加服务类型
     * @param request
     * @param name
     * @param image
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse insertServiceType(HttpServletRequest request, String name, String image) {
        return serviceTypeService.insertServiceType(name,image);
    }

    /**
     * 删除服务类型
     * @param request
     * @param id
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse deleteServiceType(HttpServletRequest request, String id) {

        return serviceTypeService.deleteServiceType(id);
    }
}
