package com.dangjia.acg.service.repair;

import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.mapper.core.IHouseFlowApplyMapper;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.repair.IChangeOrderMapper;
import com.dangjia.acg.mapper.repair.IMendOrderMapper;
import com.dangjia.acg.modle.core.HouseFlowApply;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.repair.ChangeOrder;
import com.dangjia.acg.modle.repair.MendOrder;
import com.dangjia.acg.service.config.ConfigMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * author: Ronalcheng
 * Date: 2019/1/8 0008
 * Time: 14:01
 */
@Service
public class ChangeOrderService {
    @Autowired
    private IChangeOrderMapper changeOrderMapper;
    @Autowired
    private IWorkerTypeMapper workerTypeMapper;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private IHouseMapper houseMapper;
    @Autowired
    private ConfigMessageService configMessageService;
    @Autowired
    private IMendOrderMapper mendOrderMapper;
    @Autowired
    private IHouseFlowApplyMapper houseFlowApplyMapper;

    /**
     * 管家审核变更单
     *  check 1不通过 2通过
     */
    public ServerResponse supCheckChangeOrder(String userToken,String changeOrderId,Integer check){
        try {
            AccessToken accessToken = redisClient.getCache(userToken+ Constants.SESSIONUSERID,AccessToken.class);
            Member member = accessToken.getMember();
            ChangeOrder changeOrder = changeOrderMapper.selectByPrimaryKey(changeOrderId);
            changeOrder.setState(check);
            changeOrder.setSupId(member.getId());
            changeOrderMapper.updateByPrimaryKeySelective(changeOrder);

            House house = houseMapper.selectByPrimaryKey(changeOrder.getHouseId());
            WorkerType workerType = workerTypeMapper.selectByPrimaryKey(changeOrder.getWorkerTypeId());
            if (changeOrder.getType() == 1){//补
                /*configMessageService.addConfigMessage(null,"gj",house.getMemberId(),"0","工匠补人工申请",String.format
                        (DjConstants.PushMessage.STEWARD_B_CHECK_WORK,house.getHouseName(),workerType.getName()) ,"");*/
            }else {//退
                configMessageService.addConfigMessage(null,"gj",house.getMemberId(),"0","业主退人工变更",String.format
                        (DjConstants.PushMessage.STEWARD_T_CHECK_WORK,house.getHouseName()) ,"");
            }
            return ServerResponse.createBySuccessMessage("操作成功");
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }

    /**
     * 变更单详情
     */
    public ServerResponse changeOrderDetail(String changeOrderId){
        ChangeOrder changeOrder = changeOrderMapper.selectByPrimaryKey(changeOrderId);
        if(changeOrder != null){
            return ServerResponse.createBySuccess("查询成功",changeOrder);
        }else {
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 查询变更单列表
     * type 1工匠 2业主 3管家
     */
    public ServerResponse queryChangeOrder(String userToken,String houseId,Integer type){
        /*Example example = new Example(ChangeOrder.class);
        example.createCriteria().andEqualTo(MendOrder.HOUSE_ID, houseId);*/
        AccessToken accessToken = redisClient.getCache(userToken+ Constants.SESSIONUSERID,AccessToken.class);
        Member member = accessToken.getMember();
        List<ChangeOrder> changeOrderList;
        if (type == 1){
            changeOrderList = changeOrderMapper.getList(houseId,member.getWorkerTypeId());
        } else {
            changeOrderList = changeOrderMapper.getList(houseId,null);
        }
        List<Map<String,Object>> returnMap = new ArrayList<>();
        for (ChangeOrder changeOrder : changeOrderList){
            if (changeOrder.getState() == 2){
                List<MendOrder> mendOrderList = mendOrderMapper.getByChangeOrderId(changeOrder.getId());
                if (mendOrderList.size() == 0){
                    changeOrder.setState(3);
                    changeOrderMapper.updateByPrimaryKeySelective(changeOrder);
                }
            }
            Map<String,Object> map = BeanUtils.beanToMap(changeOrder);
            map.put("changeOrderId", changeOrder.getId());
            map.put("workerTypeName", workerTypeMapper.selectByPrimaryKey(changeOrder.getWorkerTypeId()).getName());
            returnMap.add(map);
        }
        return ServerResponse.createBySuccess("查询成功",returnMap);
    }

    /**
     * 提交变更单
     * type 1工匠补  2业主退
     */
    public ServerResponse workerSubmit(String userToken,String houseId,Integer type,String contentA,String contentB,String workerTypeId){
        AccessToken accessToken = redisClient.getCache(userToken+ Constants.SESSIONUSERID,AccessToken.class);
        Member member = accessToken.getMember();
        List<ChangeOrder> changeOrderList = changeOrderMapper.unCheckOrder(houseId, member.getWorkerTypeId());
        if (changeOrderList.size() > 0){
            return ServerResponse.createByErrorMessage("该工种有未处理变更单,通知管家处理");
        }

        List<HouseFlowApply> houseFlowApplyList = houseFlowApplyMapper.unCheckByWorkerTypeId(houseId, workerTypeId);
        if (houseFlowApplyList.size() > 0) {
            return ServerResponse.createByErrorMessage("该工种有未处理完工申请");
        }

        if (type == 1){
            ChangeOrder changeOrder = new ChangeOrder();
            changeOrder.setHouseId(houseId);
            changeOrder.setWorkerTypeId(member.getWorkerTypeId());
            changeOrder.setWorkerId(member.getId());
            changeOrder.setType(type);
            changeOrder.setContentA(contentA);
            changeOrder.setContentB(contentB);
            changeOrder.setState(0);
            changeOrderMapper.insert(changeOrder);
        }else if (type == 2){
            ChangeOrder changeOrder = new ChangeOrder();
            changeOrder.setHouseId(houseId);
            changeOrder.setWorkerTypeId(workerTypeId);
            changeOrder.setMemberId(member.getId());
            changeOrder.setType(type);
            changeOrder.setContentA(contentA);
            changeOrder.setContentB(contentB);
            changeOrder.setState(0);
            changeOrderMapper.insert(changeOrder);
        }
        return ServerResponse.createBySuccessMessage("操作成功");
    }
}
