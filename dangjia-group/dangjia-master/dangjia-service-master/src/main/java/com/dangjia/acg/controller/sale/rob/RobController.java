package com.dangjia.acg.controller.sale.rob;

import com.dangjia.acg.api.sale.rob.RobAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.sale.rob.CustomerRecDTO;
import com.dangjia.acg.modle.clue.Clue;
import com.dangjia.acg.modle.home.IntentionHouse;
import com.dangjia.acg.service.sale.rob.RobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


/**
 * Created with IntelliJ IDEA.
 * 抢单模块
 * author: ljl
 * Date: 2019/7/30
 * Time: 9:59
 */
@RestController
public class RobController implements RobAPI {

    @Autowired
    private RobService robService;

    /**
     * 查询抢单列表
     *
     * @param request
     * @param userToken
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse queryRobSingledata(HttpServletRequest request, String userToken, String storeId) {
        return robService.queryRobSingledata(userToken, storeId);
    }

    /**
     * 抢单
     *
     * @param id
     * @return
     */
    public ServerResponse upDateIsRobStats(HttpServletRequest request, String id) {
        return robService.upDateIsRobStats(id);
    }

    /**
     * 查询客户详情
     *
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse queryCustomerInfo(HttpServletRequest request,
                                            String userToken,
                                            String memberId,
                                            String clueId,
                                            Integer phaseStatus,
                                            String stage) {
        return robService.queryCustomerInfo(userToken, memberId, clueId, phaseStatus, stage);
    }


    /**
     * 新增标签
     *
     * @param labelId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse addLabel(HttpServletRequest request,
                                   String mcId,
                                   String labelId,
                                   String clueId,
                                   Integer phaseStatus) {
        return robService.addLabel(mcId, labelId, clueId, phaseStatus);
    }

    /**
     * 删除标签
     *
     * @param labelIdArr
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse deleteLabel(HttpServletRequest request,
                                      String mcId,
                                      String labelIdArr,
                                      String clueId,
                                      Integer phaseStatus) {
        return robService.deleteLabel(mcId, labelIdArr, clueId, phaseStatus);
    }

    /**
     * 新增沟通记录
     *
     * @param customerRecDTO
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse addDescribes(HttpServletRequest request, CustomerRecDTO customerRecDTO,String userToken) {
        return robService.addDescribes(customerRecDTO,userToken);
    }

    @Override
    @ApiMethod
    public void remindTime() {
        robService.remindTime();
    }

    /**
     * 修改客户信息
     *
     * @param clue
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse upDateCustomerInfo(HttpServletRequest request, Clue clue) {
        return robService.upDateCustomerInfo(clue);
    }


    /**
     * 新增意向房子
     *
     * @param intentionHouse
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse addIntentionHouse(HttpServletRequest request, IntentionHouse intentionHouse) {
        return robService.addIntentionHouse(intentionHouse);
    }

    /**
     * 删除意向房子
     *
     * @param id
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse deleteIntentionHouse(HttpServletRequest request, String id) {
        return robService.deleteIntentionHouse(id);
    }

}
