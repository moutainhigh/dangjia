package com.dangjia.acg.controller.web.house;

import com.dangjia.acg.api.web.house.HouseChoiceCaseAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.house.HouseChoiceCase;
import com.dangjia.acg.service.house.HouseChoiceCaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * author: qiyuxiang
 * Date: 2018/11/07
 * Time: 16:16
 */
@RestController
public class WebHouseChoiceCaseController implements HouseChoiceCaseAPI {

    @Autowired
    private HouseChoiceCaseService houseChoiceCaseService;


    @Override
    @ApiMethod
    public ServerResponse getHouseChoiceCases(HttpServletRequest request, PageDTO pageDTO, Integer from, String cityId) {
        return houseChoiceCaseService.getHouseChoiceCases(pageDTO, from, cityId);
    }

    /**
     * 删除房屋精选案例
     *
     * @param id
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse delHouseChoiceCase(HttpServletRequest request, String id) {
        return houseChoiceCaseService.delHouseChoiceCase(id);
    }

    /**
     * 修改房屋精选案例
     *
     * @param houseChoiceCase
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse editHouseChoiceCase(HttpServletRequest request, HouseChoiceCase houseChoiceCase) {
        return houseChoiceCaseService.editHouseChoiceCase(houseChoiceCase);
    }

    /**
     * 新增房屋精选案例
     *
     * @param houseChoiceCase
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse addHouseChoiceCase(HttpServletRequest request, HouseChoiceCase houseChoiceCase) {
        return houseChoiceCaseService.addHouseChoiceCase(houseChoiceCase);
    }
}
