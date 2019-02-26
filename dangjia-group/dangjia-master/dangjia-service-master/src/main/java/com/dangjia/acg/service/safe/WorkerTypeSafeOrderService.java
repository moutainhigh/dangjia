package com.dangjia.acg.service.safe;

import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.enums.EventStatus;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.mapper.core.IHouseFlowApplyImageMapper;
import com.dangjia.acg.mapper.core.IHouseFlowMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.safe.IWorkerTypeSafeMapper;
import com.dangjia.acg.mapper.safe.IWorkerTypeSafeOrderMapper;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.safe.WorkerTypeSafe;
import com.dangjia.acg.modle.safe.WorkerTypeSafeOrder;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IHouseFlowApplyImageMapper houseFlowApplyImageMapper;

    /**
     * 切换保险
     */
    public ServerResponse changeSafeType(String userToken, String houseFlowId, String workerTypeSafeId, int selected){
        AccessToken accessToken = redisClient.getCache(userToken+ Constants.SESSIONUSERID,AccessToken.class);
        if(accessToken == null){//无效的token
            return ServerResponse.createByErrorCodeMessage(EventStatus.USER_TOKEN_ERROR.getCode(),"无效的token,请重新登录或注册!");
        }
        try{
            HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(houseFlowId);
            Example example = new Example(WorkerTypeSafeOrder.class);
            example.createCriteria().andEqualTo(WorkerTypeSafeOrder.HOUSE_ID, houseFlow.getHouseId()).andEqualTo(WorkerTypeSafeOrder.WORKER_TYPE_ID,houseFlow.getWorkerTypeId());
            workerTypeSafeOrderMapper.deleteByExample(example);
            if(selected == 0){//未勾选
                WorkerTypeSafe workerTypeSafe = workerTypeSafeMapper.selectByPrimaryKey(workerTypeSafeId);
                House house = houseMapper.selectByPrimaryKey(houseFlow.getHouseId());
                //生成工种保险服务订单
                WorkerTypeSafeOrder workerTypeSafeOrder = new WorkerTypeSafeOrder();
                workerTypeSafeOrder.setWorkerTypeSafeId(workerTypeSafeId); // 向保险订单中存入保险服务类型的id
                workerTypeSafeOrder.setHouseId(houseFlow.getHouseId()); // 存入房子id
                workerTypeSafeOrder.setWorkerTypeId(houseFlow.getWorkerTypeId()); // 工种id
                workerTypeSafeOrder.setWorkerType(houseFlow.getWorkerType());
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

    /**
     *我的质保卡
     */
    public ServerResponse queryMySafeTypeOrder(String userToken, String houseId, PageDTO pageDTO){
        AccessToken accessToken = redisClient.getCache(userToken+ Constants.SESSIONUSERID,AccessToken.class);
        Example example = new Example(WorkerTypeSafeOrder.class);
        example.createCriteria().andEqualTo(WorkerTypeSafeOrder.HOUSE_ID, houseId);
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<WorkerTypeSafeOrder> list=workerTypeSafeOrderMapper.selectByExample(example);
        List<Map> listMap=new ArrayList<>();
        PageInfo pageResult = new PageInfo(list);
        for (WorkerTypeSafeOrder wtso:list) {
            Map map = BeanUtils.beanToMap(wtso);
            WorkerTypeSafe wts = workerTypeSafeMapper.selectByPrimaryKey(wtso.getWorkerTypeSafeId());//获得类型算出时间
            map.put("workerTypeSafe",wts);
            listMap.add(map);
        }
        pageResult.setList(listMap);
        return ServerResponse.createBySuccess("ok",pageResult);
    }

    /*
     *我的质保卡明细
     */
    public ServerResponse getMySafeTypeOrderDetail(String id){
        WorkerTypeSafeOrder wtso=workerTypeSafeOrderMapper.selectByPrimaryKey(id);
        Map map = BeanUtils.beanToMap(wtso);
       /* WorkerTypeSafe wts = workerTypeSafeMapper.selectByPrimaryKey(wtso.getWorkerTypeSafeId());//获得类型算出时间
        map.put("workerTypeSafe",wts);
        List<HouseFlowApplyImage> imglist=houseFlowApplyImageMapper.getHouseFlowApplyImageList(wtso.getWorkerTypeId(), String.valueOf(wtso.getWorkerType()), wtso.getHouseId(), wtso.getHouseFlowId(), "0");
        for (HouseFlowApplyImage msg:imglist) {
            msg.initPath(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class));
        }
        map.put("imglist",imglist);*/
        return ServerResponse.createBySuccess("ok",map);
    }

}
