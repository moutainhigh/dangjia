package com.dangjia.acg.service.repair;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.data.ForMasterAPI;
import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.mapper.core.IHouseFlowMapper;
import com.dangjia.acg.mapper.core.IHouseWorkerOrderMapper;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.deliver.IOrderSplitItemMapper;
import com.dangjia.acg.mapper.deliver.IOrderSplitMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.house.IWarehouseDetailMapper;
import com.dangjia.acg.mapper.house.IWarehouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.repair.*;
import com.dangjia.acg.mapper.worker.IWorkerDetailMapper;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.core.HouseWorkerOrder;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.deliver.OrderSplit;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.house.Warehouse;
import com.dangjia.acg.modle.house.WarehouseDetail;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.repair.*;
import com.dangjia.acg.modle.sup.Supplier;
import com.dangjia.acg.modle.sup.SupplierProduct;
import com.dangjia.acg.modle.worker.WorkerDetail;
import com.dangjia.acg.service.config.ConfigMessageService;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.dangjia.acg.service.deliver.OrderSplitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2019/1/18 0018
 * Time: 14:44
 */
@Service
public class MendOrderCheckService {
    @Autowired
    private IMendOrderCheckMapper mendOrderCheckMapper;
    @Autowired
    private IChangeOrderMapper changeOrderMapper;
    @Autowired
    private IMendOrderMapper mendOrderMapper;
    @Autowired
    private CraftsmanConstructionService constructionService;
    @Autowired
    private IHouseWorkerOrderMapper houseWorkerOrderMapper;
    @Autowired
    private ForMasterAPI forMasterAPI;
    @Autowired
    private IMendWorkerMapper mendWorkerMapper;
    @Autowired
    private IHouseMapper houseMapper;
    @Autowired
    private IMemberMapper memberMapper;

    @Autowired
    private IWorkerTypeMapper workerTypeMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IHouseFlowMapper houseFlowMapper;
    @Autowired
    private IMendMaterialMapper mendMaterialMapper;
    @Autowired
    private IMendDeliverMapper mendDeliverMapper;
    @Autowired
    private IWarehouseDetailMapper warehouseDetailMapper;
    @Autowired
    private IWarehouseMapper warehouseMapper;
    @Autowired
    private IWorkerDetailMapper workerDetailMapper;
    @Autowired
    private ConfigMessageService configMessageService;

    @Autowired
    private IOrderSplitMapper orderSplitMapper;
    @Autowired
    private OrderSplitService orderSplitService;
    @Autowired
    private IOrderSplitItemMapper orderSplitItemMapper;
    @Autowired
    private MendOrderService mendOrderService;

    /**
     * 根据mendOrderId查询审核情况
     */
    public ServerResponse auditSituation(String mendOrderId){
        try {
            Example example = new Example(MendOrderCheck.class);
            example.createCriteria().andEqualTo(MendOrderCheck.MEND_ORDER_ID,mendOrderId);
            example.orderBy(MendOrderCheck.SORT).asc();
            List<MendOrderCheck> mendOrderCheckList = mendOrderCheckMapper.selectByExample(example);
            MendOrder mendOrder = mendOrderMapper.selectByPrimaryKey(mendOrderId);
            if(mendOrder!=null && mendOrder.getState()==5){
                for(MendOrderCheck m:mendOrderCheckList){
                    m.setState(3);
                }
            }
            return ServerResponse.createBySuccess("查询成功",mendOrderCheckList);
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /** 审核补退单
     *  roleType 角色  1业主,2管家,3工匠,4材料员,5供应商
     *  state  1不通过,2通过
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse checkMendOrder(String userToken,String mendOrderId,String roleType,Integer state,String productArr){
        try {

            MendOrder mendOrder = mendOrderMapper.selectByPrimaryKey(mendOrderId);
            Member member = memberMapper.selectByPrimaryKey(mendOrder.getApplyMemberId());
            if(mendOrder.getState()!=0&&mendOrder.getState()!=1){
                return ServerResponse.createBySuccessMessage("审核成功");
            }
            //工匠、管家对材料服务流程变化
            if(mendOrder.getType()==2) {
                House house = houseMapper.selectByPrimaryKey(mendOrder.getHouseId());
                if (state == 1) {
                    mendOrder.setState(2);//不通过取消
                    mendOrderMapper.updateByPrimaryKeySelective(mendOrder);
                    Example example = new Example(OrderSplit.class);
                    example.createCriteria().andEqualTo(OrderSplit.MEND_NUMBER, mendOrderId);
                    List<OrderSplit> orderSplitList = orderSplitMapper.selectByExample(example);
                    //判断是否存在要货
                    if (orderSplitList.size() > 0) {
                        for (OrderSplit orderSplit : orderSplitList) {
                            //要货单打回
                            orderSplit.setApplyStatus(3);//不通过
                            orderSplitMapper.updateByPrimaryKeySelective(orderSplit);
                        }
                    }
                } else {
                    mendOrder.setState(3);//通过
                    mendOrder.setCarriage(0.0);//运费
                    mendOrderMapper.updateByPrimaryKeySelective(mendOrder);
                    /*全部通过执行补退单不同操作  计算运费*/
                }
                if (!CommonUtil.isEmpty(productArr)) {
                    JSONArray arr = JSONArray.parseArray(productArr);
                    for (int i = 0; i < arr.size(); i++) {
                        JSONObject obj = arr.getJSONObject(i);
                        String id = obj.getString("id");
                        String supplierId = obj.getString("supplierId");
                        Supplier supplier = forMasterAPI.getSupplier(house.getCityId(), supplierId);
                        MendMateriel mendMateriel = mendMaterialMapper.selectByPrimaryKey(id);
                        mendMateriel.setSupplierId(supplierId);//供应商id
                        mendMateriel.setSupplierTelephone(supplier.getTelephone());//供应商联系电话
                        mendMateriel.setSupplierName(supplier.getName());//供应商供应商名称

                        Example example = new Example(MendDeliver.class);
                        example.createCriteria().andEqualTo(MendDeliver.HOUSE_ID, mendOrder.getHouseId()).andEqualTo(MendDeliver.SUPPLIER_ID, supplierId)
                                .andEqualTo(MendDeliver.SHIPPING_STATE, 0).andEqualTo(MendDeliver.MEND_ORDER_ID, mendMateriel.getMendOrderId());
                        List<MendDeliver> mendDeliverList = mendDeliverMapper.selectByExample(example);
                        MendDeliver mendDeliver;
                        if (mendDeliverList.size() > 0) {
                            mendDeliver = mendDeliverList.get(0);
                        } else {
                            example = new Example(MendDeliver.class);
                            mendDeliver = new MendDeliver();
                            mendDeliver.setNumber(mendOrder.getNumber() + "00" + mendDeliverMapper.selectCountByExample(example));//发货单号
                            mendDeliver.setHouseId(house.getId());
                            mendDeliver.setMendOrderId(mendMateriel.getMendOrderId());
                            mendDeliver.setTotalAmount(0.0);
                            mendDeliver.setDeliveryFee(0.0);
                            mendDeliver.setApplyMoney(0.0);
                            mendDeliver.setShipName(member.getNickName() == null ? member.getName() : member.getNickName());
                            mendDeliver.setShipMobile(member.getMobile());
                            mendDeliver.setShipAddress(house.getHouseName());
                            mendDeliver.setSupplierId(supplierId);//供应商id
                            mendDeliver.setSupplierTelephone(supplier.getTelephone());//供应商联系电话
                            mendDeliver.setSupplierName(supplier.getName());//供应商供应商名称
                            mendDeliver.setSubmitTime(new Date());
                            mendDeliver.setShippingState(0);//待发货状态
                            mendDeliver.setApplyState(0);
                            mendDeliverMapper.insert(mendDeliver);
                        }
                        SupplierProduct supplierProduct = forMasterAPI.getSupplierProduct(house.getCityId(), supplierId, mendMateriel.getProductId());

                        mendDeliver.setApplyMoney(supplierProduct.getPrice() * mendMateriel.getShopCount() + mendDeliver.getApplyMoney());//累计供应商价总价
                        mendDeliver.setTotalAmount(mendMateriel.getPrice() * mendMateriel.getShopCount() + mendDeliver.getTotalAmount());//累计退货总价
                        mendDeliverMapper.updateByPrimaryKeySelective(mendDeliver);
                        mendMateriel.setRepairMendDeliverId(mendDeliver.getId());
                        mendMaterialMapper.updateByPrimaryKeySelective(mendMateriel);
                    }
                    WorkerType workType = workerTypeMapper.selectByPrimaryKey(mendOrder.getWorkerTypeId());//查询工种
                    //大管家
                    HouseFlow houseFlow = houseFlowMapper.getHouseFlowByHidAndWty(house.getId(), 3);
                    //推送消息给业主等待大管家商与供应商一起到现场清点可退材料
                    configMessageService.addConfigMessage(null, "gj", houseFlow.getWorkerId(),
                            "0", "工匠退材料", String.format(DjConstants.PushMessage.GONGJIANGTUICAILIAOQINGDIAN,
                                    house.getHouseName(),workType.getName()), "");
                }
                return ServerResponse.createBySuccessMessage("审核成功");
            }else{
                //除工匠和大管家退材料
                return checkMendWorkerOrder( userToken, mendOrder, roleType, state);
            }
        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }

    /** 审核补退人工单
     *  roleType 角色  1业主,2管家,3工匠,4材料员,5供应商
     *  state  1不通过,2通过
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse checkMendWorkerOrder(String userToken,MendOrder mendOrder,String roleType,Integer state){
        try {
            String auditorId;
            if(roleType.equals("4")){
                auditorId = "4";
            }else {
                Object object = constructionService.getMember(userToken);
                if (object instanceof ServerResponse) {
                    return (ServerResponse) object;
                }
                Member member = (Member) object;
                auditorId = member.getId();
            }
            MendOrderCheck mendOrderCheck = mendOrderCheckMapper.getByMendOrderId(mendOrder.getId(),roleType);
            if(mendOrderCheck != null){
                if(mendOrderCheck.getState()==0){
                    mendOrderCheck.setState(state);
                    mendOrderCheck.setAuditorId(auditorId);//审核人
                    mendOrderCheck.setModifyDate(new Date());
                    mendOrderCheckMapper.updateByPrimaryKeySelective(mendOrderCheck);
                }
            }

            if (state == 1){
                mendOrder.setState(2);//不通过取消
                mendOrderMapper.updateByPrimaryKeySelective(mendOrder);
                if (mendOrder.getType()==1 || mendOrder.getType()==3){//补退人工
                    ChangeOrder changeOrder = changeOrderMapper.selectByPrimaryKey(mendOrder.getChangeOrderId());
                    changeOrder.setState(1);//管家提交的数量单取消 需重新提交
                    changeOrderMapper.updateByPrimaryKeySelective(changeOrder);
                    pushMessage(mendOrder,roleType);

                }
                Example example = new Example(OrderSplit.class);
                example.createCriteria().andEqualTo(OrderSplit.MEND_NUMBER,mendOrder.getId());
                List<OrderSplit> orderSplitList = orderSplitMapper.selectByExample(example);
                //判断是否存在要货
                if (orderSplitList.size() >0) {
                    for (OrderSplit orderSplit : orderSplitList) {
                        //要货单打回
//                        orderSplitService.cancelOrderSplit(orderSplit.getId());
                        orderSplit.setApplyStatus(3);//不通过
                        orderSplitMapper.updateByPrimaryKeySelective(orderSplit);
                    }
                }
            }else {
                boolean flag = true;
                Example example = new Example(MendOrderCheck.class);
                example.createCriteria().andEqualTo(MendOrderCheck.MEND_ORDER_ID, mendOrder.getId());
                List<MendOrderCheck> mendOrderCheckList = mendOrderCheckMapper.selectByExample(example);//所有审核角色
                for (MendOrderCheck m : mendOrderCheckList){
                    if (m.getState() != 2 ){
                        flag = false;
                        break;
                    }
                }
                if (flag){//审核流程全部通过
                    mendOrder.setState(3);//流程全部通过
                    mendOrder.setCarriage(0.0);//运费
                    mendOrderMapper.updateByPrimaryKeySelective(mendOrder);
                    //消息推送
                    pushMessage(mendOrder,roleType);
                    /*全部通过执行补退单不同操作  计算运费*/
                    return this.settleMendOrder(mendOrder);
                }
            }
            return ServerResponse.createBySuccessMessage("审核成功");
        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }
    private void pushMessage(MendOrder mendOrder,String roleType){
        try {
            House house = houseMapper.selectByPrimaryKey(mendOrder.getHouseId());
            WorkerType workType = workerTypeMapper.selectByPrimaryKey(mendOrder.getWorkerTypeId());//查询工种
            //大管家
            HouseFlow houseFlow = houseFlowMapper.getHouseFlowByHidAndWty(house.getId(), 3);
            if(mendOrder.getState()==3){
                //补人工已通过
                if (mendOrder.getType() == 1) {
                    String urlyz = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class) + "refundList?title=要补退记录&houseId=" + house.getId() + "&roleType=3";
                    //工匠
                    configMessageService.addConfigMessage(null, "gj", mendOrder.getApplyMemberId(), "0", "补人工已通过", String.format
                            (DjConstants.PushMessage.GJ_B_004, house.getHouseName()), urlyz);


                    urlyz = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class) + "refundList?title=要补退记录&houseId=" + house.getId() + "&roleType=2";
                    //大管家
                    configMessageService.addConfigMessage(null, "gj", houseFlow.getWorkerId(), "0", "补人工已通过", String.format
                            (DjConstants.PushMessage.DGJ_B_012, house.getHouseName(), workType.getName()), urlyz);

                    urlyz = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class) + "refundList?title=要补退记录&houseId=" + house.getId() + "&roleType=1";
                    //业主
                    configMessageService.addConfigMessage(null, "zx", house.getMemberId(), "0", "补人工已通过", String.format
                            (DjConstants.PushMessage.YZ_B_100, house.getHouseName(), workType.getName()), urlyz);
                }

                //退人工已通过
                if (mendOrder.getType() == 3) {
                    String urlyz = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class) + "refundList?title=要补退记录&houseId=" + house.getId() + "&roleType=3";
                    //工匠
                    configMessageService.addConfigMessage(null, "gj", mendOrder.getApplyMemberId(), "0", "退人工已通过", String.format
                            (DjConstants.PushMessage.GJ_T_100, house.getHouseName()), urlyz);


                    urlyz = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class) + "refundList?title=要补退记录&houseId=" + house.getId() + "&roleType=2";
                    //大管家
                    configMessageService.addConfigMessage(null, "gj", houseFlow.getWorkerId(), "0", "退人工已通过", String.format
                            (DjConstants.PushMessage.DGJ_T_013, house.getHouseName(), workType.getName()), urlyz);

                    urlyz = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class) + "refundList?title=要补退记录&houseId=" + house.getId() + "&roleType=1";
                    //业主
                    configMessageService.addConfigMessage(null, "zx", house.getMemberId(), "0", "退人工已通过", String.format
                            (DjConstants.PushMessage.YZ_T_005, house.getHouseName(), workType.getName()), urlyz);
                }
            }else {
                //补人工未通过
                if (mendOrder.getType() == 1) {
                    //业主审核人工变更（补）未通过
                    if ("1".equals(roleType)) {
                        String urlyz = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class) + "refundList?title=要补退记录&houseId=" + house.getId() + "&roleType=3";
                        //工匠
                        configMessageService.addConfigMessage(null, "gj", mendOrder.getApplyMemberId(), "0", "补人工未通过", String.format
                                (DjConstants.PushMessage.GJ_B_003, house.getHouseName()), urlyz);

                        urlyz = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class) + "refundList?title=要补退记录&houseId=" + house.getId() + "&roleType=2";
                        //大管家
                        configMessageService.addConfigMessage(null, "gj", houseFlow.getWorkerId(), "0", "补人工未通过", String.format
                                (DjConstants.PushMessage.DGJ_B_010, house.getHouseName(), workType.getName()), urlyz);
                    }
                    //大管家审核人工变更（补）未通过
                    if ("2".equals(roleType)) {
                        configMessageService.addConfigMessage(null, "gj", mendOrder.getApplyMemberId(), "0", "补人工未通过", String.format
                                (DjConstants.PushMessage.GJ_B_001, house.getHouseName()), "");

                        String urlyz = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class) + "refundList?title=要补退记录&houseId=" + house.getId() + "&roleType=1";
                        //业主
                        configMessageService.addConfigMessage(null, "zx", house.getMemberId(), "0", "补人工未通过", String.format
                                (DjConstants.PushMessage.YZ_B_001, house.getHouseName(), workType.getName()), urlyz);

                    }
                    //工匠审核人工变更（补）未通过
                    if ("3".equals(roleType)) {
                        String urlyz = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class) + "refundList?title=要补退记录&houseId=" + house.getId() + "&roleType=2";
                        //大管家
                        configMessageService.addConfigMessage(null, "gj", houseFlow.getWorkerId(), "0", "补人工未通过", String.format
                                (DjConstants.PushMessage.DGJ_B_011, house.getHouseName(), workType.getName()), urlyz);

                        urlyz = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class) + "refundList?title=要补退记录&houseId=" + house.getId() + "&roleType=1";
                        //业主
                        configMessageService.addConfigMessage(null, "zx", house.getMemberId(), "0", "补人工未通过", String.format
                                (DjConstants.PushMessage.GJ_B_003, house.getHouseName()), urlyz);
                    }

                }
                //退人工未通过
                if (mendOrder.getType() == 3) {
                    //业主审核人工变更（退）未通过
                    if ("1".equals(roleType)) {
                        String urlyz = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class) + "refundList?title=要补退记录&houseId=" + house.getId() + "&roleType=3";
                        //工匠
                        configMessageService.addConfigMessage(null, "gj", mendOrder.getApplyMemberId(), "0", "退人工未通过", String.format
                                (DjConstants.PushMessage.GJ_T_005, house.getHouseName()), urlyz);

                        urlyz = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class) + "refundList?title=要补退记录&houseId=" + house.getId() + "&roleType=2";
                        //大管家
                        configMessageService.addConfigMessage(null, "gj", houseFlow.getWorkerId(), "0", "退人工未通过", String.format
                                (DjConstants.PushMessage.DGJ_T_012, house.getHouseName(), workType.getName()), urlyz);
                    }
                    //大管家审核人工变更（退）未通过
                    if ("2".equals(roleType)) {
                        configMessageService.addConfigMessage(null, "zx", house.getMemberId(), "0", "退人工未通过", String.format
                                (DjConstants.PushMessage.YZ_B_002, house.getHouseName()), "");

                        String urlyz = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class) + "refundList?title=要补退记录&houseId=" + house.getId() + "&roleType=3";
                        //工匠
                        configMessageService.addConfigMessage(null, "gj", mendOrder.getApplyMemberId(), "0", "退人工未通过", String.format
                                (DjConstants.PushMessage.GJ_T_005, house.getHouseName()), urlyz);

                    }
                    //工匠审核人工变更（退）未通过
                    if ("3".equals(roleType)) {
                        String urlyz = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class) + "refundList?title=要补退记录&houseId=" + house.getId() + "&roleType=2";
                        //大管家
                        configMessageService.addConfigMessage(null, "gj", houseFlow.getWorkerId(), "0", "退人工未通过", String.format
                                (DjConstants.PushMessage.DGJ_T_011, house.getHouseName(), workType.getName()), urlyz);

                        urlyz = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class) + "refundList?title=要补退记录&houseId=" + house.getId() + "&roleType=1";
                        //业主
                        configMessageService.addConfigMessage(null, "zx", house.getMemberId(), "0", "退人工未通过", String.format
                                (DjConstants.PushMessage.YZ_T_004, house.getHouseName()), urlyz);
                    }
                }
            }
        }catch (Exception e){

        }
    }

    /**
     * 大管家确认退货单
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse confirmMendOrder(String userToken,String mendOrderId,String mendDeliverId,String productArr){
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member operator = (Member) object;
            MendOrder mendOrder = mendOrderMapper.selectByPrimaryKey(mendOrderId);
            House house = houseMapper.selectByPrimaryKey(mendOrder.getHouseId());
            MendDeliver mendDeliver = mendDeliverMapper.selectByPrimaryKey(mendDeliverId);
            if(mendDeliver==null){
                return ServerResponse.createBySuccessMessage("提交失败，请联系平台部！");
            }
            Double actualTotalAmount=0D;
            Double applyMoney=0D;
            if(mendOrder.getActualTotalAmount()==null){
                mendOrder.setActualTotalAmount(0D);
            }
            if(mendOrder.getState()==4){
                return ServerResponse.createBySuccessMessage("提交成功");
            }
            //工匠、管家对材料服务流程变化
            if (!CommonUtil.isEmpty(productArr)) {
                JSONArray arr = JSONArray.parseArray(productArr);
                for (int i = 0; i < arr.size(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    String id = obj.getString("id");
                    String productId = obj.getString("productId");
                    String shopCount = obj.getString("shopCount");
                    MendMateriel mendMateriel;
                    if(!CommonUtil.isEmpty(id)){
                         mendMateriel = mendMaterialMapper.selectByPrimaryKey(id);
                    }else{
                         mendMateriel = mendOrderService.saveMendMaterial(mendOrder,house,productId,shopCount);
                    }

                    SupplierProduct supplierProduct = forMasterAPI.getSupplierProduct(house.getCityId(), mendDeliver.getSupplierId(), mendMateriel.getProductId());
                    mendMateriel.setActualCount(Double.parseDouble(shopCount));//实际退货数
                    mendMateriel.setActualPrice(mendMateriel.getActualCount() * mendMateriel.getPrice());
                    actualTotalAmount=actualTotalAmount+mendMateriel.getActualPrice();
                    applyMoney+=mendMateriel.getActualCount() * supplierProduct.getPrice();
                    mendMaterialMapper.updateByPrimaryKeySelective(mendMateriel);
                }
            }
            mendDeliver.setApplyMoney(applyMoney);//累计供应商结算总价
            mendDeliver.setTotalAmount(actualTotalAmount);//累计退货总价
            mendDeliver.setShippingState(1);
            mendDeliver.setOperatorId(operator.getId());
            mendDeliver.setBackTime(new Date());
            mendDeliverMapper.updateByPrimaryKeySelective(mendDeliver);
            mendOrder.setActualTotalAmount(mendOrder.getActualTotalAmount()+actualTotalAmount);
            mendOrder.setBusinessOrderNumber(mendDeliver.getId());//临时记录供应商退货单号，用于分供应商部分退材料钱


            //推送消息告知业主退材料退余款
            configMessageService.addConfigMessage(null, "zx", house.getMemberId(), "0", "工匠退货提醒",
                    String.format(DjConstants.PushMessage.TUIKWANGCHENG, house.getHouseName()), "");
            //退材料钱至业主钱包（立即）
            return settleMendOrder(mendOrder);
        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }
    /**
     * 审核完毕 结算补退单
     * type  0:补材料;1:补人工;2:退材料(剩余材料登记);3:退人工,4:业主退材料
     */
    public ServerResponse settleMendOrder(MendOrder mendOrder){
        try{
            if(mendOrder.getType() == 1){
                ChangeOrder changeOrder = changeOrderMapper.selectByPrimaryKey(mendOrder.getChangeOrderId());
                changeOrder.setState(5);//待业主支付
                changeOrderMapper.updateByPrimaryKeySelective(changeOrder);
            }

            if (mendOrder.getType() == 3){//退人工
                ChangeOrder changeOrder = changeOrderMapper.selectByPrimaryKey(mendOrder.getChangeOrderId());
                changeOrder.setState(6);//退人工完成
                changeOrderMapper.updateByPrimaryKeySelective(changeOrder);

                HouseWorkerOrder houseWorkerOrder = houseWorkerOrderMapper.getByHouseIdAndWorkerTypeId(mendOrder.getHouseId(), mendOrder.getWorkerTypeId());
                BigDecimal refund = new BigDecimal(mendOrder.getTotalAmount());
                if(houseWorkerOrder.getWorkPrice().doubleValue()<refund.doubleValue()){
                    refund=houseWorkerOrder.getWorkPrice();
                }
                BigDecimal workPrice=(houseWorkerOrder.getWorkPrice().subtract(refund));//减掉工钱
                houseWorkerOrder.setWorkPrice(workPrice);//剩余工钱

                houseWorkerOrderMapper.updateByPrimaryKeySelective(houseWorkerOrder);

                List<MendWorker> mendWorkerList = mendWorkerMapper.byMendOrderId(mendOrder.getId());
                /*记录退数量*/
                House house = houseMapper.selectByPrimaryKey(houseWorkerOrder.getHouseId());
                for (MendWorker mendWorker : mendWorkerList){
                    forMasterAPI.backCount(house.getCityId(), mendOrder.getHouseId(), mendWorker.getWorkerGoodsId(), mendWorker.getShopCount());
                }
                Member member = memberMapper.selectByPrimaryKey(house.getMemberId());
                BigDecimal haveMoney = member.getHaveMoney().add(refund);
                BigDecimal surplusMoney = member.getSurplusMoney().add(refund);

                member.setSurplusMoney(surplusMoney);
                member.setHaveMoney(haveMoney);
                memberMapper.updateByPrimaryKeySelective(member);

                //记录流水
                WorkerDetail workerDetail = new WorkerDetail();
                workerDetail.setName("退人工退款");
                workerDetail.setWorkerId(member.getId());
                workerDetail.setWorkerName(member.getName() == null?member.getNickName() : member.getName());
                workerDetail.setHouseId(mendOrder.getHouseId());
                workerDetail.setMoney(refund);
                workerDetail.setApplyMoney(refund);
                workerDetail.setWalletMoney(surplusMoney);
                workerDetail.setState(6);//退人工退款
                workerDetailMapper.insert(workerDetail);

                mendOrder.setState(4);
                mendOrderMapper.updateByPrimaryKeySelective(mendOrder);
            }
            if(mendOrder.getType() == 4){//业主退
                /*审核通过修改仓库数量,记录流水*/
                List<MendMateriel> mendMaterielList = mendMaterialMapper.byMendOrderId(mendOrder.getId());
                for (MendMateriel mendMateriel : mendMaterielList){
                    Warehouse warehouse = warehouseMapper.getByProductId(mendMateriel.getProductId(), mendOrder.getHouseId());
                    warehouse.setBackCount(warehouse.getBackCount() + mendMateriel.getShopCount());//更新退数量
                    warehouse.setBackTime(warehouse.getBackTime() + 1);//更新退次数
                    warehouse.setOwnerBack(warehouse.getOwnerBack()==null?mendMateriel.getShopCount():(warehouse.getOwnerBack() + mendMateriel.getShopCount())); //购买数量+业主退数量
                    warehouseMapper.updateByPrimaryKeySelective(warehouse);
                }

                WarehouseDetail warehouseDetail = new WarehouseDetail();
                warehouseDetail.setHouseId(mendOrder.getHouseId());
                warehouseDetail.setRelationId(mendOrder.getId());
                warehouseDetail.setRecordType(4);//业主退
                warehouseDetailMapper.insert(warehouseDetail);
                /*退钱给业主*/
                Member member = memberMapper.selectByPrimaryKey(houseMapper.selectByPrimaryKey(mendOrder.getHouseId()).getMemberId());
                BigDecimal haveMoney = member.getHaveMoney().add(new BigDecimal(mendOrder.getTotalAmount()));
                BigDecimal surplusMoney = member.getSurplusMoney().add(new BigDecimal(mendOrder.getTotalAmount()));
                //记录流水
                WorkerDetail workerDetail = new WorkerDetail();
                workerDetail.setName("业主退材料退款");
                workerDetail.setWorkerId(member.getId());
                workerDetail.setWorkerName(CommonUtil.isEmpty(member.getName()) ?member.getNickName() : member.getName());
                workerDetail.setHouseId(mendOrder.getHouseId());
                workerDetail.setMoney(new BigDecimal(mendOrder.getTotalAmount()));
                workerDetail.setApplyMoney(new BigDecimal(mendOrder.getTotalAmount()));
                workerDetail.setWalletMoney(surplusMoney);
                workerDetail.setState(4);//进钱//业主退
                workerDetailMapper.insert(workerDetail);

                member.setHaveMoney(haveMoney);
                member.setSurplusMoney(surplusMoney);
                memberMapper.updateByPrimaryKeySelective(member);

                mendOrder.setState(4);
                mendOrderMapper.updateByPrimaryKeySelective(mendOrder);

                //推送消息给业主退货退款通知
                configMessageService.addConfigMessage(null, "zx", member.getId(),
                        "0", "退货退款通知", String.format(DjConstants.PushMessage.YEZHUTUIHUO), "");

            }
            if(mendOrder.getType() == 2 ){//工匠退剩余材料管家退服务
                MendDeliver mendDeliver = mendDeliverMapper.selectByPrimaryKey(mendOrder.getBusinessOrderNumber());
                BigDecimal totalAmount=new BigDecimal(0);
                /*审核通过修改仓库数量,记录流水*/
                Example example =new Example(MendMateriel.class);
                example.createCriteria().andEqualTo(MendMateriel.MEND_ORDER_ID,mendOrder.getId())
                        .andEqualTo(MendMateriel.REPAIR_MEND_DELIVER_ID,mendOrder.getBusinessOrderNumber());
                List<MendMateriel> mendMaterielList = mendMaterialMapper.selectByExample(example);
                for (MendMateriel mendMateriel : mendMaterielList){
                    if(mendMateriel.getActualCount()!=null&&mendMateriel.getActualCount()>0) {
                        Warehouse warehouse = warehouseMapper.getByProductId(mendMateriel.getProductId(), mendOrder.getHouseId());
                        warehouse.setBackCount(warehouse.getBackCount() + mendMateriel.getActualCount());//更新退数量
                        warehouse.setBackTime(warehouse.getBackTime() + 1);//更新退次数
                        warehouse.setWorkBack(warehouse.getWorkBack() == null ? mendMateriel.getActualCount() : (warehouse.getWorkBack() + mendMateriel.getActualCount())); //收货数量+工匠退数量
                        warehouseMapper.updateByPrimaryKeySelective(warehouse);
                        totalAmount=totalAmount.add(new BigDecimal(mendMateriel.getActualPrice()));
                    }
                }

                if(totalAmount.doubleValue()>0) {
                    /*退钱给业主*/
                    Member member = memberMapper.selectByPrimaryKey(houseMapper.selectByPrimaryKey(mendOrder.getHouseId()).getMemberId());
                    BigDecimal haveMoney = member.getHaveMoney().add(totalAmount);
                    BigDecimal surplusMoney = member.getSurplusMoney().add(totalAmount);
                    //记录流水
                    WorkerDetail workerDetail = new WorkerDetail();
                    workerDetail.setName("工匠退材料退款");
                    workerDetail.setWorkerId(member.getId());
                    workerDetail.setWorkerName(CommonUtil.isEmpty(member.getName()) ? member.getNickName() : member.getName());
                    workerDetail.setHouseId(mendOrder.getHouseId());
                    workerDetail.setMoney(totalAmount);
                    workerDetail.setApplyMoney(totalAmount);
                    workerDetail.setWalletMoney(surplusMoney);
                    workerDetail.setState(5);//进钱//工匠退 登记剩余
                    workerDetailMapper.insert(workerDetail);

                    member.setHaveMoney(haveMoney);
                    member.setSurplusMoney(surplusMoney);
                    memberMapper.updateByPrimaryKeySelective(member);

                }
                mendDeliver.setShippingState(1);
                mendDeliver.setModifyDate(new Date());
                mendDeliverMapper.updateByPrimaryKeySelective(mendDeliver);

                example =new Example(MendDeliver.class);
                example.createCriteria().andEqualTo(MendDeliver.MEND_ORDER_ID,mendOrder.getId()).andEqualTo(MendDeliver.SHIPPING_STATE,0);
                int mendDeliverList = mendDeliverMapper.selectCountByExample(example);

                //如果没有待确认的退货单则更新结算
                if(mendDeliverList==0) {
                    WarehouseDetail warehouseDetail = new WarehouseDetail();
                    warehouseDetail.setHouseId(mendOrder.getHouseId());
                    warehouseDetail.setRelationId(mendOrder.getId());
                    warehouseDetail.setRecordType(3);//工匠退 登记剩余
                    warehouseDetailMapper.insert(warehouseDetail);
                    mendOrder.setState(4);
                }

                mendOrderMapper.updateByPrimaryKeySelective(mendOrder);

            }

            return ServerResponse.createBySuccessMessage("流程全部通过");
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("流程全部通过后异常");
        }
    }
}
