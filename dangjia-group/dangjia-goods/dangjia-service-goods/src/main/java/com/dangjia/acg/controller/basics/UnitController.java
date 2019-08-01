package com.dangjia.acg.controller.basics;

import com.dangjia.acg.api.basics.UnitAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.basics.UnitService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @类 名： UnitController.java
 * @功能描述：
 * @作者信息： hb
 * @创建时间： 2018-9-13下午3:55:12
 */
@RestController
public class UnitController implements UnitAPI {
    /**
     * service
     */
    @Autowired
    private UnitService unitService;


    /**
     * 查询所有商品单位
     *
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse<PageInfo> getAllUnit(HttpServletRequest request, PageDTO pageDTO) {
        return unitService.getAllUnit(pageDTO);
    }

    /**
     * 修改商品单位
     *
     * @param unitId   单位ID
     * @param unitName 单位名称
     * @return 接口
     */
    @Override
    @ApiMethod
    public ServerResponse updateUnit(HttpServletRequest request, String unitId, String unitName, String linkUnitIdArr) {
        return unitService.update(unitId, unitName, linkUnitIdArr);
    }

    /**
     * 新增商品单位
     *
     * @param unitName 单位名称
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse insertUnit(HttpServletRequest request, String unitName, String linkUnitIdArr) {
        return unitService.insert(unitName, linkUnitIdArr);
    }

    @Override
    @ApiMethod
    public ServerResponse getUnitById(HttpServletRequest request, String cityId, String unitId) {
        return unitService.getUnitById(unitId);
    }

}
