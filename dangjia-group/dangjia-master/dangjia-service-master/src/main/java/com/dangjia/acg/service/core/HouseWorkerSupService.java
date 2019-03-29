package com.dangjia.acg.service.core;

import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.mapper.core.IHouseFlowMapper;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.member.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * author: Ronalcheng
 * Date: 2019/3/27 0027
 * Time: 9:55
 *  1.31业务补充
 */
@Service
public class HouseWorkerSupService {
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private IHouseFlowMapper houseFlowMapper;


    /**
     * 工匠申请停工
     */
    public ServerResponse applyShutdown(String userToken, String houseFlowId, String applyDec, String startDate, String endDate){
        try{
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            Member worker = accessToken.getMember();
            HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(houseFlowId);

            return ServerResponse.createBySuccessMessage("操作成功");
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("申请失败");
        }
    }
}
