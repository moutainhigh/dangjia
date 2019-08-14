package com.dangjia.acg.controller.sale.rob;

import com.dangjia.acg.api.sale.rob.RobAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.clue.ClueTalkDTO;
import com.dangjia.acg.dto.sale.rob.CustomerRecDTO;
import com.dangjia.acg.modle.clue.Clue;
import com.dangjia.acg.modle.home.IntentionHouse;
import com.dangjia.acg.modle.sale.royalty.DjRobSingle;
import com.dangjia.acg.service.sale.rob.RobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


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
    public ServerResponse queryRobSingledata(HttpServletRequest request, String userToken, String storeId,Integer isRobStats) {
        return robService.queryRobSingledata(userToken, storeId,isRobStats);
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
    public List<ClueTalkDTO> getTodayDescribes() {
        return robService.getTodayDescribes();
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
    public ServerResponse upDateCustomerInfo(HttpServletRequest request, Clue clue,Integer phaseStatus,String memberId) {
        return robService.upDateCustomerInfo(clue,phaseStatus,memberId);
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

    /**
     * 新增配置时间
     * @param request
     * @param djRobSingle
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse addDjRobSingle(HttpServletRequest request, DjRobSingle djRobSingle) {
        return robService.addDjRobSingle(djRobSingle);
    }

    /**
     * 修改配置时间
     * @param request
     * @param djRobSingle
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse upDateDjRobSingle(HttpServletRequest request, DjRobSingle djRobSingle) {
        return robService.upDateDjRobSingle(djRobSingle);
    }


    /**
     * 删除配置时间
     * @param request
     * @param djRobSingle
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse deleteDjRobSingle(HttpServletRequest request, DjRobSingle djRobSingle) {
        return robService.deleteDjRobSingle(djRobSingle);
    }

    /**
     * 删除配置时间
     * @param request
     * @param pageDTO
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse queryDjRobSingle(HttpServletRequest request, PageDTO pageDTO) {
        return robService.queryDjRobSingle(pageDTO);
    }
}
