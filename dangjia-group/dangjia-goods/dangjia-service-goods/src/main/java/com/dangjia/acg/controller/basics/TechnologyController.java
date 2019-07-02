package com.dangjia.acg.controller.basics;

import com.dangjia.acg.api.basics.TechnologyAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
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
    public ServerResponse insertTechnology(HttpServletRequest request, String name, String content, String workerTypeId, Integer type, String image, Integer materialOrWorker) {
        return technologyService.insertTechnology(name, content, workerTypeId, type, image, materialOrWorker);
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
    public ServerResponse updateTechnology(HttpServletRequest request, String id, String name, String content, Integer type, String image) {
        return technologyService.updateTechnology(id, name, content, type, image);
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
    public ServerResponse<PageInfo> queryTechnology(HttpServletRequest request, PageDTO pageDTO, String workerTypeId, String name, Integer materialOrWorker) {
        return technologyService.queryTechnology(pageDTO, workerTypeId, name, materialOrWorker);
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
    public ServerResponse queryTechnologyByWgId(HttpServletRequest request, String workerGoodsId) {
        return technologyService.queryTechnologyByWgId(workerGoodsId);
    }

}
