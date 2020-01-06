package com.dangjia.acg.controller.data;

import com.dangjia.acg.api.data.ActuaryAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.data.ActuaryService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * author: Ronalcheng
 * Date: 2018/10/31 0031
 * Time: 20:07
 */
@RestController
public class ActuaryController implements ActuaryAPI {

    protected static final Logger logger = LoggerFactory.getLogger(ActuaryController.class);
    @Autowired
    private ActuaryService actuaryService;

    /**
     * 返回作废的精算列表
     *   budgetOk:
     *          1=待提交精算
     *          2=待业主确认
     *          3=已完成
     *          5=待业主支付
     *          -1=废弃的精算
     *
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getActuaryBudgetOk(HttpServletRequest request,PageDTO pageDTO,String name,String budgetOk, String workerKey,String userId,String budgetStatus,
                                            String decorationType){
        return actuaryService.getActuaryAll(request,pageDTO,name,budgetOk,  workerKey,userId,budgetStatus,decorationType);

    }

    /**
     * 精算设计--查询默认配置的设计商品
     */
    @Override
    @ApiMethod
    public ServerResponse searchActuarialProductList(HttpServletRequest request,String cityId){
        return actuaryService.searchActuarialProductList(cityId,null);
    }
    /**
     * 精算接口--保存推荐的设计商品
     */
    @Override
    @ApiMethod
    public ServerResponse saveRecommendedGoods(HttpServletRequest request,String cityId, String houseId,String productStr){
        return actuaryService.saveRecommendedGoods(cityId,houseId,productStr);
    }

    /**
     * 精算接口，审核结果类型
     * @param request
     * @param cityId 城市ID
     * @param houseId 房子ID
     * @param auditResultType 审核结果审核结果（1审核通过，2审核不通过）
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse checkDesignPicture(HttpServletRequest request,String cityId,String houseId,String auditResultType){
        try{
            return actuaryService.checkDesignPicture(cityId,houseId,auditResultType);
        }catch (Exception e){
            logger.error("审核保存失败",e);
            return ServerResponse.createByErrorMessage("审核保存失败");
        }

    }

    /**
     * 查询精算的订单详情
     * @param cityId 城市ID
     * @param houseId 房子ID
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getBudgetOrderDetail(String cityId,String houseId,String workerTypeId){
        return actuaryService.getBudgetOrderDetail(cityId,houseId,workerTypeId);
    }
    /**
     * 返回待业主支付精算列表
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getActuaryWaitPay(HttpServletRequest request,PageDTO pageDTO,String name,
                                            String workerKey){
        return actuaryService.getActuaryAll(request,pageDTO,name,"5",workerKey,null,null,null);

    }

    /**
     * 返回待提交精算列表
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getActuaryCommit(HttpServletRequest request,PageDTO pageDTO,String name, String workerKey){

        return actuaryService.getActuaryAll(request,pageDTO,name,"1",workerKey,null,null,null);
    }

    /**
     * 返回待业主确认精算列表
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getActuaryConfirm(HttpServletRequest request,PageDTO pageDTO,String name, String workerKey){
        return actuaryService.getActuaryAll(request,pageDTO,name,"2",workerKey,null,null,null);
    }

    /**
     * 返回已完成精算列表
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getActuaryComplete(HttpServletRequest request,PageDTO pageDTO,String name, String workerKey){
        return actuaryService.getActuaryAll(request,pageDTO,name,"3",workerKey,null,null,null);
    }

    /**
     * 返回统计列表
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getStatistics(HttpServletRequest request,PageDTO pageDTO,String name){
        return actuaryService.getStatistics();
    }

    /**
     * 返回按日期统计列表
     * @param startDate
     * @param endDate
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getStatisticsByDate(String startDate, String endDate){
        return actuaryService.getStatisticsByDate(startDate,endDate);
    }
}
