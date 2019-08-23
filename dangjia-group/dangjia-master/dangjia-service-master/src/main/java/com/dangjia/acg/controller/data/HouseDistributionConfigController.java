package com.dangjia.acg.controller.data;

import com.dangjia.acg.api.data.HouseDistributionConfigAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.house.HouseDistributionConfig;
import com.dangjia.acg.service.house.HouseDistributionConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * author: qiyuxiang
 * Date: 2019/1/16 0001
 * Time: 17:56
 */
@RestController
public class HouseDistributionConfigController implements HouseDistributionConfigAPI {

    @Autowired
    private HouseDistributionConfigService houseDistributionConfigService;

    /**
     * 获取所有应用
     * @param houseDistributionConfig
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getHouseDistributionConfigs(HttpServletRequest request, HouseDistributionConfig houseDistributionConfig) {
        return houseDistributionConfigService.getHouseDistributionConfigs(request,houseDistributionConfig);
    }
    /**
     * 删除应用LOGO
     * @param id
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse delHouseDistributionConfig(HttpServletRequest request, String id) {
        return houseDistributionConfigService.delHouseDistributionConfig(request,id);
    }

    /**
     * 修改应用LOGO
     * @param houseDistributionConfig
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse editHouseDistributionConfig(HttpServletRequest request, HouseDistributionConfig houseDistributionConfig) {
        return houseDistributionConfigService.editHouseDistributionConfig(request,houseDistributionConfig);
    }
    /**
     * 新增应用LOGO
     * @param houseDistributionConfig
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse addHouseDistributionConfig(HttpServletRequest request,HouseDistributionConfig houseDistributionConfig) {
        return houseDistributionConfigService.addHouseDistributionConfig(request,houseDistributionConfig);
    }
}
