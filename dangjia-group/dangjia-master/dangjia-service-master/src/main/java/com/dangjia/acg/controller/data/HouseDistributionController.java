package com.dangjia.acg.controller.data;

import com.dangjia.acg.api.data.HouseDistributionAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.house.HouseDistribution;
import com.dangjia.acg.modle.house.WebsiteVisit;
import com.dangjia.acg.service.house.HouseDistributionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * author: qiyuxiang
 * Date: 2019/1/16 0001
 * Time: 17:56
 */
@RestController
public class HouseDistributionController implements HouseDistributionAPI {

    @Autowired
    private HouseDistributionService houseDistributionService;

    /**
     * 获取所有验房分销
     *
     * @param houseDistribution
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getHouseDistribution(HttpServletRequest request, PageDTO pageDTO, HouseDistribution houseDistribution,
                                               String startDate, String endDate,String searchKey) {
        return houseDistributionService.getHouseDistribution(request, pageDTO, houseDistribution, startDate, endDate,searchKey);
    }

    /**
     * 新增
     *
     * @param houseDistribution
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse addHouseDistribution(HttpServletRequest request, HouseDistribution houseDistribution) {
        return houseDistributionService.addHouseDistribution(request, houseDistribution);
    }

    @Override
    @ApiMethod
    public ServerResponse addWebsiteVisit(HttpServletRequest request, WebsiteVisit websiteVisit) {
        return houseDistributionService.addWebsiteVisit(request, websiteVisit);
    }
}
