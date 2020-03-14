package com.dangjia.acg.controller.actuary.app;

import com.ctc.wstx.sw.EncodingXmlWriter;
import com.dangjia.acg.api.actuary.DjBasicsActuarialConfigurationAPI;
import com.dangjia.acg.api.actuary.app.SearchActuarialConfigAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.actuary.DjBasicsActuarialConfigurationServices;
import com.dangjia.acg.service.actuary.app.SearchActuarialConfigServices;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * Date: 2019/10/22 fzh
 * Time: 16:44
 */
@RestController
public class SearchActuarialConfigController implements SearchActuarialConfigAPI {
    private static Logger logger = LoggerFactory.getLogger(SearchActuarialConfigController.class);
    @Autowired
    private SearchActuarialConfigServices searchActuarialConfigServices;

    /**
     * 我要装修--首页，查询设计精算所有的商品
     * @param request
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse searchActuarialList(HttpServletRequest request,String cityId,String serviceTypeId) {
        return searchActuarialConfigServices.searchActuarialList(cityId,serviceTypeId);
    }

    /**
     * 查询默认配置的设计商品
     * @param request
     * @param cityId
     * @param houseId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse searchDesginActuarialList(HttpServletRequest request,String cityId,String houseId){
        return searchActuarialConfigServices.searchDesginActuarialList(cityId,houseId);
    }


    /**
     * 可切换商品列表
     * @param request
     * @param goodsId 货品ID
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse searchChangeProductList(HttpServletRequest request,String goodsId,String houseId){
        return searchActuarialConfigServices.searchChangeProductList(goodsId,houseId);
    }

    /**
     * 我要装修--模拟花费标题查询
     * @param request
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse searchSimulationTitleList(HttpServletRequest request,String cityId,String serviceTypeId){
        return searchActuarialConfigServices.searchSimulationTitleList(cityId,serviceTypeId);
    }

    /**
     * 我要装修--模拟花费标题列表查询
     * @param request
     * @param titleId 标题 ID
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse searchSimulationTitleDetailList(HttpServletRequest request,String titleId,String cityId){
        return searchActuarialConfigServices.searchSimulationTitleDetailList(titleId,cityId);
    }

    /**
     * 根据组合查询对应花费详情
     * @param request
     * @param groupCode
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse searchSimulateCostInfoList(HttpServletRequest request,String groupCode,String cityId,String serviceTypeId){
        return searchActuarialConfigServices.searchSimulateCostInfoList(groupCode,cityId,serviceTypeId);
    }
}
