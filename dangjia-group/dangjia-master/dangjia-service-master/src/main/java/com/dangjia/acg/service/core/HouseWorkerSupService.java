package com.dangjia.acg.service.core;

import com.dangjia.acg.common.response.ServerResponse;
import org.springframework.stereotype.Service;

/**
 * author: Ronalcheng
 * Date: 2019/3/27 0027
 * Time: 9:55
 *  1.31业务补充
 */
@Service
public class HouseWorkerSupService {


    /**
     * 工匠申请停工
     * @param userToken
     * @param applyDec   内容
     * @param startDate 开始时间
     * @param endDate 结束时间
     */
    public ServerResponse applyShutdown(String userToken,String houseId,String applyDec,String startDate,String endDate){
        return null;
    }
}
