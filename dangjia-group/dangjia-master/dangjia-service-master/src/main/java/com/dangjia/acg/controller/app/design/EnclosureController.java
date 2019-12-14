package com.dangjia.acg.controller.app.design;

import com.dangjia.acg.api.app.design.EnclosureAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.design.EnclosureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Ruking.Cheng
 * @descrilbe 附件管理接口
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/12/14 2:59 PM
 */
@RestController
public class EnclosureController implements EnclosureAPI {
    @Autowired
    private EnclosureService enclosureService;

    @Override
    @ApiMethod
    public ServerResponse addEnclosure(HttpServletRequest request, String userToken, String userId, String houseId,
                                       String name, String enclosure, int enclosureType, String remarks) {
        return enclosureService.addEnclosure(userToken, userId, houseId, name, enclosure, enclosureType, remarks);
    }

    @Override
    @ApiMethod
    public ServerResponse deleteEnclosure(HttpServletRequest request, String enclosureId) {
        return enclosureService.deleteEnclosure(enclosureId);
    }

    @Override
    @ApiMethod
    public ServerResponse selectEnclosureList(HttpServletRequest request, String houseId, int enclosureType) {
        return enclosureService.selectEnclosureList(houseId, enclosureType);
    }
}
