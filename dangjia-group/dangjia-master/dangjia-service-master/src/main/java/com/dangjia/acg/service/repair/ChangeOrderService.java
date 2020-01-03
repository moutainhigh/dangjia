package com.dangjia.acg.service.repair;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.enums.AppType;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.MathUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.mapper.core.IHouseFlowApplyMapper;
import com.dangjia.acg.mapper.core.IHouseFlowMapper;
import com.dangjia.acg.mapper.core.IHouseWorkerOrderMapper;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.delivery.IMasterOrderProgressMapper;
import com.dangjia.acg.mapper.delivery.IOrderItemMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.repair.IChangeOrderMapper;
import com.dangjia.acg.mapper.repair.IMendOrderCheckMapper;
import com.dangjia.acg.mapper.repair.IMendOrderMapper;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.core.HouseFlowApply;
import com.dangjia.acg.modle.core.HouseWorkerOrder;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.deliver.OrderItem;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.house.TaskStack;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.order.OrderProgress;
import com.dangjia.acg.modle.repair.ChangeOrder;
import com.dangjia.acg.modle.repair.MendOrder;
import com.dangjia.acg.modle.repair.MendOrderCheck;
import com.dangjia.acg.service.config.ConfigMessageService;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.dangjia.acg.service.core.TaskStackService;
import com.dangjia.acg.service.deliver.RepairMendOrderService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * author: Ronalcheng
 * Date: 2019/1/8 0008
 * Time: 14:01
 */
@Service
public class ChangeOrderService {

    protected static final Logger logger = LoggerFactory.getLogger(ChangeOrderService.class);
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
    @Autowired
    private IMasterOrderProgressMapper iMasterOrderProgressMapper;

    @Autowired
    private RepairMendOrderService repairMendOrderService;

    @Autowired
    private MendOrderService mendOrderService;
    @Autowired
    private IOrderItemMapper iOrderItemMapper;
    @Autowired
    private ConfigUtil configUtil;

    /**
     * 管家审核变更单
     * check 1不通过 2通过
     */
    public ServerResponse supCheckChangeOrder(String userToken, String changeOrderId, Integer check) {
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
            if (changeOrder.getType() == 1) {//补
                /*configMessageService.addConfigMessage(null,AppType.GONGJIANG,house.getMemberId(),"0","工匠补人工申请",String.format
                        (DjConstants.PushMessage.STEWARD_B_CHECK_WORK,house.getHouseName(),workerType.getName()) ,"");*/
            } else {//退
                configMessageService.addConfigMessage(null, AppType.GONGJIANG, house.getMemberId(), "0", "业主退人工变更", String.format
                        (DjConstants.PushMessage.STEWARD_T_CHECK_WORK, house.getHouseName()), "");
                //退人工流水记录判断
                if(check!=2){
                    //fzh.3.0大管家审核不通过，流水记录
                    updateOrderProgressInfo(changeOrder.getId(),"2","REFUND_AFTER_SALES","RA_014",member.getId());//您的退人工申请已提交
                }
            }
            return ServerResponse.createBySuccessMessage("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }

    /**
     * 变更单详情
     */
    public ServerResponse changeOrderDetail(String changeOrderId) {
        ChangeOrder changeOrder = changeOrderMapper.selectByPrimaryKey(changeOrderId);
        if (changeOrder != null) {
            return ServerResponse.createBySuccess("查询成功", changeOrder);
        } else {
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 查询变更单列表
     * type 1工匠 2业主 3管家
     */
    public ServerResponse queryChangeOrder(String userToken, String houseId, Integer type) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member member = (Member) object;
        List<ChangeOrder> changeOrderList;
        if (type == 1) {
            changeOrderList = changeOrderMapper.getList(houseId, member.getWorkerTypeId());
        } else {
            changeOrderList = changeOrderMapper.getList(houseId, null);
        }
        List<Map<String, Object>> returnMap = new ArrayList<>();
        for (ChangeOrder changeOrder : changeOrderList) {
            if (changeOrder.getState() == 2) {
                List<MendOrder> mendOrderList = mendOrderMapper.getByChangeOrderId(changeOrder.getId());
                if (mendOrderList.size() == 0) {
                    changeOrder.setState(0);
                    changeOrderMapper.updateByPrimaryKeySelective(changeOrder);
                }
            }
            Map<String, Object> map = BeanUtils.beanToMap(changeOrder);
            if (changeOrder.getState() == 1) {
                map.put("roleType", "2");
                List<MendOrder> mendOrderList = mendOrderMapper.getByChangeOrderId(changeOrder.getId());
                if (mendOrderList.size() > 0) {
                    Example example = new Example(MendOrderCheck.class);
                    example.createCriteria().andEqualTo(MendOrderCheck.STATE, 1).andEqualTo(MendOrderCheck.MEND_ORDER_ID, mendOrderList.get(0).getId());
                    example.orderBy(MendOrderCheck.CREATE_DATE).desc();
                    List<MendOrderCheck> list = mendOrderCheckMapper.selectByExample(example);
                    if (list.size() > 0) {
                        map.put("roleType", list.get(0).getRoleType());//1业主,2管家,3工匠,4材料员,5供应商
                    }
                }
            }

            map.put("changeOrderId", changeOrder.getId());
            map.put("workerTypeName", workerTypeMapper.selectByPrimaryKey(changeOrder.getWorkerTypeId()).getName());
            returnMap.add(map);
        }
        return ServerResponse.createBySuccess("查询成功", returnMap);
    }

    /**
     * 提交变更单
     * type 1工匠补人工,2业主退人工,3业主补人工
     * @param  productArr 退人工商品列表
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse workerSubmit(String userToken, String houseId, Integer type, String contentA, String contentB, String workerTypeId,String productArr) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        //判断是否可退款
       /* HouseWorkerOrder houseWorkerOrder = houseWorkerOrderMapper.getByHouseIdAndWorkerTypeId(houseId, workerTypeId);
        String str=checkRefundMoney(houseWorkerOrder,productArr);
        if(StringUtils.isNotBlank(str)){
            return ServerResponse.createByErrorMessage(str);
        }*/

        Member member = (Member) object;
        ChangeOrder changeOrder = new ChangeOrder();
        changeOrder.setHouseId(houseId);
        if (type == 1) {
            changeOrder.setWorkerId(member.getId());
            changeOrder.setWorkerTypeId(member.getWorkerTypeId());
            workerTypeId=member.getWorkerTypeId();
        } else if (type == 2) {
            changeOrder.setWorkerTypeId(workerTypeId);
        }

        List<ChangeOrder> changeOrderList = changeOrderMapper.unCheckOrder(houseId, changeOrder.getWorkerTypeId());
        if (changeOrderList.size() > 0) {
            return ServerResponse.createByErrorMessage("该工种有未处理变更单,通知业主处理");
        }

        boolean isCheck = false;
        List<HouseFlowApply> houseFlowApplyList = houseFlowApplyMapper.unCheckByWorkerTypeId(houseId, changeOrder.getWorkerTypeId());
        if (houseFlowApplyList.size() > 0) {
            for (HouseFlowApply houseFlowApply : houseFlowApplyList) {
                if (houseFlowApply.getApplyType() == 2) {
                    isCheck = true;
                    break;
                }
            }
            if (isCheck) {
                return ServerResponse.createByErrorMessage("该工种已发起整体完工申请，不能发起补/退人工申请");
            }
        }
        HouseFlow houseFlow = houseFlowMapper.getByWorkerTypeId(houseId, workerTypeId);
        if (houseFlow != null && houseFlow.getWorkSteta() == 2) {
            return ServerResponse.createByErrorMessage("该工种已整体完工，不能发起补/退人工申请");
        }
        WorkerType workerType=workerTypeMapper.selectByPrimaryKey(workerTypeId);
        House house=houseMapper.selectByPrimaryKey(houseId);
        changeOrder.setMemberId(house.getMemberId());//业主ID
        changeOrder.setType(type);
        if(type==1){
            changeOrder.setTitleName(workerType.getName()+"申请补人工");
        }else if(type==2){
            changeOrder.setTitleName("申请退"+workerType.getName());
        }
        changeOrder.setContentA(contentA);
        changeOrder.setContentB(contentB);
        changeOrder.setState(2);//业主审核中、工匠审核中
        changeOrderMapper.insert(changeOrder);
        //增加节点（退人工流水记录状态)
        if (type == 2) {//业主退人工
            changeOrder.setWorkerTypeId(houseFlow.getWorkerId());
            //生成退人工订单
            MendOrder mendOrder=repairMendOrderService.insertRefundOrder(houseId,member.getId(),workerTypeId,changeOrder.getId(),productArr);
            //生成审核任务
            if (!mendOrderService.createMendCheck(mendOrder)) {
                return ServerResponse.createByErrorMessage("添加审核流程失败");
            }
            //通知消息给工匠
            String url = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class) + "changeArtificial?userToken=" + userToken + "&cityId=" + house.getCityId() + "&title=人工变更&houseId=" + houseId + "&houseFlowId=" + houseFlow.getId() + "&roleType=3";
            configMessageService.addConfigMessage(null, AppType.GONGJIANG, houseFlow.getWorkerId(), "0", "退人工", String.format
                    (DjConstants.PushMessage.GJ_T_003, house.getHouseName()), url);

            //生成退人工审核任务给到业主
            //taskStackService.inserTaskStackInfo(houseId,houseFlow.getWorkerId(),"退人工审核",workerType.getImage(),12,changeOrder.getId());//存变更申请单ID
            updateOrderProgressInfo(changeOrder.getId(),"2","REFUND_AFTER_SALES","RA_012",member.getId());//您的退人工申请已提交
            updateOrderProgressInfo(changeOrder.getId(),"2","REFUND_AFTER_SALES","RA_016",member.getId());// 工匠审核中
        }else if(type==1){//工匠补人工
            //生成补人工订单
            MendOrder mendOrder=repairMendOrderService.insertSupplementLaborOrder(houseId,member.getId(),member.getWorkerTypeId(),changeOrder.getId());
            //生成审核任务
            if (!mendOrderService.createMendCheck(mendOrder)) {
                return ServerResponse.createByErrorMessage("添加审核流程失败");
            }
            //通知消息给业主
            configMessageService.addConfigMessage(null, AppType.ZHUANGXIU, house.getMemberId(), "0", "补人工", String.format
                    (DjConstants.PushMessage.YZ_Y_001, house.getHouseName(), workerType.getName()), "");

            //生成补人工审核任务给到业主
           // taskStackService.inserTaskStackInfo(houseId,house.getMemberId(),workerType.getName()+"申请补人工",workerType.getImage(),2,changeOrder.getId());//存变更申请单ID
           updateOrderProgressInfo(changeOrder.getId(),"3","REFUND_AFTER_SALES","RA_020",member.getId());//您的补人工申请已提交
           updateOrderProgressInfo(changeOrder.getId(),"3","REFUND_AFTER_SALES","RA_021",member.getId());//业主审核中
        }
        return ServerResponse.createBySuccessMessage("操作成功");
    }
    /**
     * //添加进度信息
     * @param orderId 订单ID
     * @param progressType 订单类型
     * @param nodeType 节点类型
     * @param nodeCode 节点编码
     * @param userId 用户id
     */
    private void updateOrderProgressInfo(String orderId,String progressType,String nodeType,String nodeCode,String userId){
        OrderProgress orderProgress=new OrderProgress();
        orderProgress.setProgressOrderId(orderId);
        orderProgress.setProgressType(progressType);
        orderProgress.setNodeType(nodeType);
        orderProgress.setNodeCode(nodeCode);
        orderProgress.setCreateBy(userId);
        orderProgress.setUpdateBy(userId);
        orderProgress.setCreateDate(new Date());
        orderProgress.setModifyDate(new Date());
        iMasterOrderProgressMapper.insert(orderProgress);
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
    public ServerResponse checkHouseFlowApply(String userToken, String houseId, Integer type, String workerTypeId,String productArr) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        WorkerType workerType = workerTypeMapper.selectByPrimaryKey(workerTypeId);
        HouseWorkerOrder houseWorkerOrder = houseWorkerOrderMapper.getByHouseIdAndWorkerTypeId(houseId, workerTypeId);
        if (houseWorkerOrder != null) {
            if (type == 1) {
                List<ChangeOrder> changeOrderList = changeOrderMapper.unCheckOrder(houseId, workerTypeId);
                List<HouseFlowApply> houseFlowApplyList = houseFlowApplyMapper.unCheckByWorkerTypeId(houseId, workerTypeId);
                if (changeOrderList.size() > 0 && houseFlowApplyList.size() > 0) {
                    HouseFlowApply houseFlowApply = houseFlowApplyList.get(0);
                    BigDecimal remain = houseFlowApply.getOtherMoney();//剩下的钱
                    remain = remain.setScale(2, BigDecimal.ROUND_HALF_UP);
                    if (remain.doubleValue() < 0) {//负数冲正
                        remain = new BigDecimal(0);
                    }
                    return ServerResponse.createByErrorMessage("审核通过后 ，可退人工金额上限为" + remain + "元(不包含滞留金)，确定验收吗？");
                }
            }
            if (type == 2) {

                String str=checkRefundMoney(houseWorkerOrder,productArr);
                if(StringUtils.isNotBlank(str)){
                    return ServerResponse.createByErrorMessage(str);
                }
                /*List<HouseFlowApply> houseFlowApplyList = houseFlowApplyMapper.unCheckByWorkerTypeId(houseId, workerTypeId);
                if (houseFlowApplyList.size() > 0) {
                    return ServerResponse.createByErrorMessage("当前" + workerType.getName() + "阶段完工正在申请中，可退人工金额上限为" + alsoMoney + "元，确定申请退人工吗？");
                }
                HouseFlow houseFlow = houseFlowMapper.getByWorkerTypeId(houseId, workerTypeId);
                if (houseFlow.getWorkSteta() == 1) {
                    return ServerResponse.createByErrorMessage("当前" + workerType.getName() + "已阶段完工，可退人工金额上限为" + alsoMoney + "元，确定申请退人工吗？");

                }*/
            }
        }else{
            if (type == 2) {
                Double totalAllPrice=getTotalAllPrice(productArr);
                return ServerResponse.createByErrorMessage("申请退款金额为"+totalAllPrice+"，可退金额上限为0，不能申请退人工。");
            }
        }
        return ServerResponse.createBySuccessMessage("检测通过");
    }

    /**
     * 判断是否可退款
     * @param houseWorkerOrder
     * @param productArr
     * @return
     */
    public String checkRefundMoney(HouseWorkerOrder houseWorkerOrder,String productArr){
        if (houseWorkerOrder.getDeductPrice() == null) {
            houseWorkerOrder.setDeductPrice(new BigDecimal(0));
        }
        BigDecimal alsoMoney = new BigDecimal(houseWorkerOrder.getWorkPrice().doubleValue() - houseWorkerOrder.getHaveMoney().doubleValue() + houseWorkerOrder.getRepairPrice().doubleValue() - houseWorkerOrder.getRetentionMoney().doubleValue() - houseWorkerOrder.getDeductPrice().doubleValue());
        if (alsoMoney.doubleValue() < 0) {
            alsoMoney = new BigDecimal(0);
        }
        alsoMoney = alsoMoney.setScale(2, BigDecimal.ROUND_HALF_UP);
        //查询业主选择的可退商品
        Double totalAllPrice=getTotalAllPrice(productArr);

        if(new BigDecimal(totalAllPrice).compareTo(alsoMoney)<0){
            return "申请退款金额为"+totalAllPrice+"，可退金额上限为"+alsoMoney+"，不能申请退人工。";
        }
        return "";
    }


    /**
     * 获取申请退款总额
     * @param productArr
     * @return
     */
    Double getTotalAllPrice(String productArr){
        Double totalAllPrice=0d;
        JSONArray orderItemProductList= JSONArray.parseArray(productArr);
        if(orderItemProductList!=null&&orderItemProductList.size()>0) {
            for (int i = 0; i < orderItemProductList.size(); i++) {
                JSONObject productObj = (JSONObject) orderItemProductList.get(i);
                String orderItemId = (String) productObj.get("orderItemId");//订单详情号
               // String productId = (String) productObj.get("productId");//产品ID
                Double returnCount = productObj.getDouble("returnCount");//退货量
                OrderItem orderItem=iOrderItemMapper.selectByPrimaryKey(orderItemId);
                Double totalPrice= MathUtil.mul(orderItem.getPrice(),returnCount);
                totalAllPrice=MathUtil.add(totalAllPrice,totalPrice);//汇总总退款金额
            }
        }
        return totalAllPrice;
    }

}
