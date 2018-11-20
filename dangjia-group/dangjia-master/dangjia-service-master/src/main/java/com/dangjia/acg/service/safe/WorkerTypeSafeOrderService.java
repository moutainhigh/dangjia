package com.dangjia.acg.service.safe;

import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.enums.EventStatus;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.mapper.core.IHouseFlowMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.safe.IWorkerTypeSafeMapper;
import com.dangjia.acg.mapper.safe.IWorkerTypeSafeOrderMapper;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.safe.WorkerTypeSafe;
import com.dangjia.acg.modle.safe.WorkerTypeSafeOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

/**
 * author: Ronalcheng
 * Date: 2018/11/8 0008
 * Time: 11:48
 */
@Service
public class WorkerTypeSafeOrderService {
    @Autowired
    private IWorkerTypeSafeOrderMapper workerTypeSafeOrderMapper;
    @Autowired
    private IWorkerTypeSafeMapper workerTypeSafeMapper;
    @Autowired
    private IHouseFlowMapper houseFlowMapper;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private IHouseMapper houseMapper;

    /*
    切换保险
     */
    public ServerResponse changeSafeType(String userToken, String houseFlowId, String workerTypeSafeId, int selected){
        AccessToken accessToken = redisClient.getCache(userToken+ Constants.SESSIONUSERID,AccessToken.class);
        if(accessToken == null){//无效的token
            return ServerResponse.createByErrorCodeMessage(EventStatus.USER_TOKEN_ERROR.getCode(),"无效的token,请重新登录或注册!");
        }
        try{
            Example example = new Example(WorkerTypeSafeOrder.class);
            example.createCriteria().andEqualTo("houseFlowId", houseFlowId);
            workerTypeSafeOrderMapper.deleteByExample(example);
            if(selected == 0){//未勾选
                WorkerTypeSafe workerTypeSafe = workerTypeSafeMapper.selectByPrimaryKey(workerTypeSafeId);
                HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(houseFlowId);
                House house = houseMapper.selectByPrimaryKey(houseFlow.getHouseId());
                //生成工种保险服务订单
                WorkerTypeSafeOrder workerTypeSafeOrder = new WorkerTypeSafeOrder();
                workerTypeSafeOrder.setWorkerTypeSafeId(workerTypeSafeId); // 向保险订单中存入保险服务类型的id
                workerTypeSafeOrder.setHouseId(houseFlow.getHouseId()); // 存入房子id
                workerTypeSafeOrder.setMemberId(houseFlow.getMemberId());
                workerTypeSafeOrder.setWorkerTypeId(houseFlow.getWorkerTypeId()); // 工种id
                workerTypeSafeOrder.setWorkerType(houseFlow.getWorkerType());
                workerTypeSafeOrder.setHouseFlowId(houseFlowId);
                workerTypeSafeOrder.setPrice(workerTypeSafe.getPrice().multiply(house.getSquare()));
                workerTypeSafeOrder.setState(0);  //未支付
                workerTypeSafeOrderMapper.insert(workerTypeSafeOrder);
            }
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
        return ServerResponse.createBySuccessMessage("操作成功");
    }
}
