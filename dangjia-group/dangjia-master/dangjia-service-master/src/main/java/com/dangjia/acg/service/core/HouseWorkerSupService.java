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
     */
    public ServerResponse applyShutdown(String userToken, String houseFlowId, String applyDec, String startDate, String endDate){
        try{

            return ServerResponse.createBySuccessMessage("操作成功");
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("申请失败");
        }
    }
}
