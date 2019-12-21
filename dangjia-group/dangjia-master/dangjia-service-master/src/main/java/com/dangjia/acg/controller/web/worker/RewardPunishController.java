package com.dangjia.acg.controller.web.worker;

import com.dangjia.acg.api.web.worker.RewardPunishAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.worker.RewardPunishRecord;
import com.dangjia.acg.service.worker.RewardPunishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * 奖罚管理
 * author: zmj
 * Date: 2018/11/5 0005
 * Time: 15:40
 */
@RestController
public class RewardPunishController implements RewardPunishAPI {
    @Autowired
    private RewardPunishService rewardPunishService;

    /**
     * 保存奖罚条件及条件明细
     *
     * @param id
     * @param name
     * @param content
     * @param type
     * @param state
     * @param conditionArr
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse addRewardPunishCorrelation(String id, String name, String content, Integer type, Integer state, String conditionArr, BigDecimal quantity) {
        return rewardPunishService.addRewardPunishCorrelation(id, name, content, type, state, conditionArr,quantity);
    }

    /**
     * 删除奖罚条件及条件明细
     *
     * @param id
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse deleteRewardPunishCorrelation(String id) {
        return rewardPunishService.deleteRewardPunishCorrelation(id);
    }

    /**
     * 查询所有奖罚条件及条件明细
     *
     * @param pageDTO
     * @param name
     * @param type
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse queryCorrelation(PageDTO pageDTO, String name, Integer type) {
        return rewardPunishService.queryCorrelation(pageDTO, name, type);
    }

    /**
     * 根据id查询奖罚条件及明细
     *
     * @param id
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse queryCorrelationById(String id) {
        return rewardPunishService.queryCorrelationById(id);
    }

    /**
     * 添加奖罚记录
     *
     * @param userToken
     * @param rewardPunishRecord
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse addRewardPunishRecord(String userToken, String userId, RewardPunishRecord rewardPunishRecord) {
        return rewardPunishService.addRewardPunishRecord(userToken, userId, rewardPunishRecord);
    }

    @Override
    @ApiMethod
    public ServerResponse queryCraftsmenList(PageDTO pageDTO, String houseId) {
        return rewardPunishService.queryCraftsmenList(pageDTO,houseId);
    }

    @Override
    @ApiMethod
    public ServerResponse queryCorrelationList(PageDTO pageDTO, String type) {
        return rewardPunishService.queryCorrelationList(pageDTO,type);
    }

    @Override
    @ApiMethod
    public ServerResponse queryPunishRecordList(PageDTO pageDTO, String houseId) {
        return rewardPunishService.queryPunishRecordList(pageDTO,houseId);
    }

}
