package com.dangjia.acg.controller.web.matter;

import com.dangjia.acg.api.web.matter.WebRenovationManualAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.matter.RenovationManual;
import com.dangjia.acg.service.matter.RenovationManualService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * author: zmj
 * Date: 2018/11/5 0005
 * Time: 15:40
 */
@RestController
public class RenovationManualController implements WebRenovationManualAPI {

    @Autowired
    private RenovationManualService renovationManualService;

    /**
     * 根据工序id查询所有装修指南
     *
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse queryRenovationManual(PageDTO pageDTO, RenovationManual renovationManual) {
        return renovationManualService.queryRenovationManual(pageDTO, renovationManual);
    }

    /**
     * 新增装修指南
     *
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse addRenovationManual(RenovationManual renovationManual) {
        return renovationManualService.addRenovationManual(renovationManual);
    }

    /**
     * 修改装修指南
     *
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse updateRenovationManual(RenovationManual renovationManual) {
        return renovationManualService.updateRenovationManual(renovationManual);
    }

    /**
     * 删除装修指南
     *
     * @param id
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse deleteRenovationManual(String id) {
        return renovationManualService.deleteRenovationManual(id);
    }

    /**
     * 根据id查询装修指南对象
     *
     * @param id
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getRenovationManualById(String id) {
        return renovationManualService.getRenovationManualById(id);
    }

}
