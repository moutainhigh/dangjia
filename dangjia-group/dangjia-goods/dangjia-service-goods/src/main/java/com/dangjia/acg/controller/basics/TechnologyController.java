package com.dangjia.acg.controller.basics;

import com.dangjia.acg.api.basics.TechnologyAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.basics.Technology;
import com.dangjia.acg.service.basics.TechnologyService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @类 名： TechnologyController
 * @功能描述： TODO
 * @作者信息： zmj
 * @创建时间： 2018-9-10上午9:25:10
 */
@RestController
public class TechnologyController implements TechnologyAPI {
    /**
     * service
     */
    @Autowired
    private TechnologyService technologyService;

    /**
     * 新增工艺说明
     *
     * @throws
     * @Title: insertTechnology
     * @Description: TODO
     * @param: @param name
     * @param: @param content
     * @param: @return
     * @return: JsonResult
     */
    @Override
    @ApiMethod
    public ServerResponse insertTechnology(HttpServletRequest request,  Technology technology) {
        return technologyService.insertTechnology(technology);
    }

    /**
     * 修改工艺说明
     *
     * @throws
     * @Title: updateTechnology
     * @Description: TODO
     * @param: @param id
     * @param: @param name
     * @param: @param content
     * @param: @return
     * @return: JsonResult
     */
    @Override
    @ApiMethod
    public ServerResponse updateTechnology(HttpServletRequest request, Technology technology) {
        return technologyService.updateTechnology(technology);
    }

    /**
     * 删除工艺说明
     *
     * @throws
     * @Title: deleteTechnology
     * @Description: TODO
     * @param: @param id
     * @param: @return
     * @return: JsonResult
     */
    @Override
    @ApiMethod
    public ServerResponse deleteTechnology(HttpServletRequest request, String id) {
        return technologyService.deleteTechnology(id);
    }

    /**
     * 查询所有工艺说明
     *
     * @throws
     * @Title: queryTechnology
     * @Description: TODO
     * @param: @param name
     * @param: @param content
     * @param: @return
     * @return: JsonResult
     */
    @Override
    @ApiMethod
    public ServerResponse<PageInfo> queryTechnology(HttpServletRequest request, PageDTO pageDTO, String workerTypeId, String name,
                                                    Integer materialOrWorker,String cityId) {
        return technologyService.queryTechnology(pageDTO, workerTypeId, name, materialOrWorker,cityId);
    }

    @Override
    @ApiMethod
    public ServerResponse getTechnology(HttpServletRequest request, String technologyId) {
        return technologyService.getTechnology(technologyId);
    }
    /**
     * 根据商品id查询人工商品关联工艺实体
     *
     * @throws
     * @Title: queryTechnologyByWgId
     * @Description: TODO
     * @param: @param worker_goods_id
     * @param: @return
     * @return: JsonResult
     */
    @Override
    @ApiMethod
    public ServerResponse queryTechnologyByWgId(HttpServletRequest request, String workerGoodsId,String cityId) {
        return technologyService.queryTechnologyByWgId(workerGoodsId,cityId);
    }

    //根据名称查询所有工艺（名称去重）
    @Override
    @ApiMethod
    public ServerResponse queryByName(HttpServletRequest request,String name,String workerTypeId,String cityId,Integer materialOrWorker,PageDTO pageDTO){
        return technologyService.queryByName(name, workerTypeId,cityId,materialOrWorker,pageDTO);
    }

    @Override
    @ApiMethod
    public ServerResponse queryTechnologyWorkerType(HttpServletRequest request) {
        return technologyService.queryTechnologyWorkerType();
    }

    @Override
    @ApiMethod
    public ServerResponse queryTechnologDetail(String id) {
        return technologyService.queryTechnologDetail(id);
    }
}
