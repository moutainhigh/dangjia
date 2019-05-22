package com.dangjia.acg.service.repair;

import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.mapper.core.IHouseFlowApplyMapper;
import com.dangjia.acg.mapper.core.IHouseFlowMapper;
import com.dangjia.acg.mapper.core.IHouseWorkerOrderMapper;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.repair.IChangeOrderMapper;
import com.dangjia.acg.mapper.repair.IMendOrderCheckMapper;
import com.dangjia.acg.mapper.repair.IMendOrderMapper;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.core.HouseFlowApply;
import com.dangjia.acg.modle.core.HouseWorkerOrder;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.repair.ChangeOrder;
import com.dangjia.acg.modle.repair.MendOrder;
import com.dangjia.acg.modle.repair.MendOrderCheck;
import com.dangjia.acg.service.config.ConfigMessageService;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
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
    private IHouseMapper houseMapper;
    @Autowired
    private ConfigMessageService configMessageService;
    @Autowired
    private IMendOrderMapper mendOrderMapper;
    @Autowired
    private IHouseFlowApplyMapper houseFlowApplyMapper;
    @Autowired
    private CraftsmanConstructionService constructionService;

    @Autowired
    private IMendOrderCheckMapper mendOrderCheckMapper;
    @Autowired
    private IHouseFlowMapper houseFlowMapper;
    @Autowired
    private IHouseWorkerOrderMapper houseWorkerOrderMapper;
    /**
     * 管家审核变更单
     *  check 1不通过 2通过
     */
    public ServerResponse supCheckChangeOrder(String userToken,String changeOrderId,Integer check){
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = (Member) object;
            ChangeOrder changeOrder = changeOrderMapper.selectByPrimaryKey(changeOrderId);
            //大管家未确认，不做任何变更
            if (check != 2) {
                changeOrder.setState(check);
                changeOrder.setSupId(member.getId());
                changeOrderMapper.updateByPrimaryKeySelective(changeOrder);
            }

            House house = houseMapper.selectByPrimaryKey(changeOrder.getHouseId());
//            WorkerType workerType = workerTypeMapper.selectByPrimaryKey(changeOrder.getWorkerTypeId());
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
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member member = (Member) object;
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
            if (changeOrder.getState() == 1){
                map.put("roleType","2");
                List<MendOrder> mendOrderList = mendOrderMapper.getByChangeOrderId(changeOrder.getId());
                if (mendOrderList.size() >0){
                    Example example=new Example(MendOrderCheck.class);
                    example.createCriteria().andEqualTo(MendOrderCheck.STATE,1).andEqualTo(MendOrderCheck.MEND_ORDER_ID,mendOrderList.get(0).getId());
                    example.orderBy(MendOrderCheck.CREATE_DATE).desc();
                    List<MendOrderCheck> list=mendOrderCheckMapper.selectByExample(example);
                    if(list.size()>0){
                        map.put("roleType",list.get(0).getRoleType());//1业主,2管家,3工匠,4材料员,5供应商
                    }
                }
            }

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
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member member = (Member) object;
        ChangeOrder changeOrder = new ChangeOrder();
        changeOrder.setHouseId(houseId);
        if (type == 1){
            changeOrder.setWorkerId(member.getId());
            changeOrder.setWorkerTypeId(member.getWorkerTypeId());
        }else if (type == 2){
            changeOrder.setWorkerTypeId(workerTypeId);
        }

        List<ChangeOrder> changeOrderList = changeOrderMapper.unCheckOrder(houseId, changeOrder.getWorkerTypeId());
        if (changeOrderList.size() > 0){
            return ServerResponse.createByErrorMessage("该工种有未处理变更单,通知管家处理");
        }

        boolean isCheck=false;
        List<HouseFlowApply> houseFlowApplyList = houseFlowApplyMapper.unCheckByWorkerTypeId(houseId, workerTypeId);
        if (houseFlowApplyList.size() > 0) {
            for (HouseFlowApply houseFlowApply : houseFlowApplyList) {
                if(houseFlowApply.getApplyType()==2){
                    isCheck=true;
                    break;
                }
            }
            if(isCheck) {
                return ServerResponse.createByErrorMessage("该工种已发起整体完工申请，不能发起补/退人工申请");
            }
        }
        HouseFlow houseFlow=houseFlowMapper.getByWorkerTypeId(houseId,workerTypeId);
        if(houseFlow.getWorkSteta()==2){
            return ServerResponse.createByErrorMessage("该工种已整体完工，不能发起补/退人工申请");
        }

        changeOrder.setMemberId(member.getId());
        changeOrder.setType(type);
        changeOrder.setContentA(contentA);
        changeOrder.setContentB(contentB);
        changeOrder.setState(0);
        changeOrderMapper.insert(changeOrder);
        return ServerResponse.createBySuccessMessage("操作成功");
    }
    /**
     * 请求地址：app/repair/changeOrder/checkHouseFlowApply
     * 说明：申请退人工或业主验收检测
     * userToken：用户TOKEN
     * houseId：房子ID
     * workerTypeId：工序ID
     * type 1阶段验收检测  2退人工检测
     *
     * return 状态正常则不弹出提示，异常则弹出并且待确认继续确认
     */
    public ServerResponse checkHouseFlowApply(String userToken,String houseId,Integer type,String workerTypeId){
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        WorkerType workerType = workerTypeMapper.selectByPrimaryKey(workerTypeId);
        HouseWorkerOrder houseWorkerOrder = houseWorkerOrderMapper.getByHouseIdAndWorkerTypeId(houseId, workerTypeId);
        if (houseWorkerOrder != null) {
            if (type == 1) {
                List<ChangeOrder> changeOrderList = changeOrderMapper.unCheckOrder(houseId,workerTypeId);
                List<HouseFlowApply> houseFlowApplyList = houseFlowApplyMapper.unCheckByWorkerTypeId(houseId, workerTypeId);
                if (changeOrderList.size() > 0&&houseFlowApplyList.size() > 0) {
                    HouseFlowApply houseFlowApply=houseFlowApplyList.get(0);
                    BigDecimal remain=houseFlowApply.getOtherMoney();//剩下的钱
                    remain =remain.setScale(2,BigDecimal.ROUND_HALF_UP);
                    if(remain.doubleValue()<0){//负数冲正
                        remain=new BigDecimal(0);
                    }
                    return ServerResponse.createByErrorMessage("审核通过后 ，可退人工金额上限为"+remain+"元(不包含滞留金)，确定验收吗？");
                }
            }
            if (type == 2) {
                if(houseWorkerOrder.getDeductPrice()==null){
                    houseWorkerOrder.setDeductPrice(new BigDecimal(0));
                }
                BigDecimal alsoMoney = new BigDecimal(houseWorkerOrder.getWorkPrice().doubleValue()-houseWorkerOrder.getHaveMoney().doubleValue()+houseWorkerOrder.getRepairPrice().doubleValue()-houseWorkerOrder.getRetentionMoney().doubleValue()-houseWorkerOrder.getDeductPrice().doubleValue());
                alsoMoney =alsoMoney.setScale(2,BigDecimal.ROUND_HALF_UP);
                List<HouseFlowApply> houseFlowApplyList = houseFlowApplyMapper.unCheckByWorkerTypeId(houseId, workerTypeId);
                if (houseFlowApplyList.size() > 0) {
                    return ServerResponse.createByErrorMessage("当前" + workerType.getName() + "阶段完工正在申请中，可退人工金额上限为"+alsoMoney+"元，确定申请退人工吗？");
                }
                HouseFlow houseFlow=houseFlowMapper.getByWorkerTypeId(houseId, workerTypeId);
                if (houseFlow.getWorkSteta()==1) {
                    return ServerResponse.createByErrorMessage("当前" + workerType.getName() + "已阶段完工，可退人工金额上限为"+alsoMoney+"元，确定申请退人工吗？");

                }
            }
        }
        return ServerResponse.createBySuccessMessage("检测通过");
    }
}
