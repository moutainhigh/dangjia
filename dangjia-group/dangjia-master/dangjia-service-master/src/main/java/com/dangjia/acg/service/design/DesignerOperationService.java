package com.dangjia.acg.service.design;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.enums.AppType;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.mapper.core.IHouseFlowApplyMapper;
import com.dangjia.acg.mapper.core.IHouseFlowMapper;
import com.dangjia.acg.mapper.core.IHouseWorkerOrderMapper;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.design.IDesignBusinessOrderMapper;
import com.dangjia.acg.mapper.design.IMasterQuantityRoomProductMapper;
import com.dangjia.acg.mapper.design.IQuantityRoomImagesMapper;
import com.dangjia.acg.mapper.design.IQuantityRoomMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.member.IMasterMemberAddressMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.other.IWorkDepositMapper;
import com.dangjia.acg.mapper.worker.IWorkerDetailMapper;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.core.HouseFlowApply;
import com.dangjia.acg.modle.core.HouseWorkerOrder;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.design.DesignBusinessOrder;
import com.dangjia.acg.modle.design.DesignQuantityRoomProduct;
import com.dangjia.acg.modle.design.QuantityRoom;
import com.dangjia.acg.modle.design.QuantityRoomImages;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.house.TaskStack;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.member.MemberAddress;
import com.dangjia.acg.modle.other.WorkDeposit;
import com.dangjia.acg.modle.worker.WorkerDetail;
import com.dangjia.acg.service.config.ConfigMessageService;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.dangjia.acg.service.core.TaskStackService;
import com.dangjia.acg.service.deliver.OrderService;
import com.dangjia.acg.service.house.HouseService;
import com.dangjia.acg.service.pay.PaymentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Ruking.Cheng
 * @descrilbe 设计师资料操作类
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/5/6 5:45 PM
 */
@Service
public class DesignerOperationService {
    @Autowired
    private ConfigMessageService configMessageService;
    @Autowired
    private IHouseMapper houseMapper;
    @Autowired
    private OrderService orderService;
    @Autowired
    private HouseService houseService;
    @Autowired
    private IHouseFlowApplyMapper houseFlowApplyMapper;
    @Autowired
    private HouseDesignPayService houseDesignPayService;
    @Autowired
    private CraftsmanConstructionService constructionService;
    @Autowired
    private IWorkerTypeMapper workerTypeMapper;
    @Autowired
    private IHouseFlowMapper houseFlowMapper;
    @Autowired
    private IHouseWorkerOrderMapper houseWorkerOrderMapper;
    @Autowired
    private IWorkDepositMapper workDepositMapper;
    @Autowired
    private IQuantityRoomMapper quantityRoomMapper;
    @Autowired
    private IQuantityRoomImagesMapper quantityRoomImagesMapper;
    @Autowired
    private DesignDataService designDataService;
    @Autowired
    private IMemberMapper memberMapper;
    @Autowired
    private IWorkerDetailMapper workerDetailMapper;
    @Autowired
    private IDesignBusinessOrderMapper designBusinessOrderMapper;
    @Autowired
    private IMasterQuantityRoomProductMapper iMasterQuantityRoomProductMapper;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private IMasterMemberAddressMapper iMasterMemberAddressMapper;
    @Autowired
    private TaskStackService taskStackService;

    /**
     * 设计师将设计图或施工图发送给业主
     * 。。。。。。。。。。。。。。。。。⦧--6。。⦧--8
     * 远程设计流程：0---4---1---9---5---7---2---3
     *
     * 。。。。。。。。。。。。⦧--6。。⦧--8
     * 自带设计流程：0---1---5---7---2---3
     */
    public ServerResponse sendPictures(String houseId) {
        House house = houseMapper.selectByPrimaryKey(houseId);
        if (house == null) {
            return ServerResponse.createByErrorMessage("未找到该房子");
        }
        if (house.getVisitState() != 1) {
            return ServerResponse.createByErrorMessage("该房子不在装修中");
        }
        if (house.getDesignerState() == 3) {
            Example example = new Example(DesignBusinessOrder.class);
            Example.Criteria criteria = example.createCriteria()
                    .andEqualTo(DesignBusinessOrder.DATA_STATUS, 0)
                    .andEqualTo(DesignBusinessOrder.HOUSE_ID, house.getId())
                    .andEqualTo(DesignBusinessOrder.STATUS, 1)
                    .andNotEqualTo(DesignBusinessOrder.OPERATION_STATE, 2);
            if (house.getDecorationType() != 2) {
                criteria.andEqualTo(DesignBusinessOrder.TYPE, 4);
                List<DesignBusinessOrder> designBusinessOrders = designBusinessOrderMapper.selectByExample(example);
                if (designBusinessOrders != null && designBusinessOrders.size() > 0) {
                    DesignBusinessOrder order = designBusinessOrders.get(0);
                    if (order.getOperationState() == 0) {
                        order.setOperationState(1);
                        designBusinessOrderMapper.updateByPrimaryKeySelective(order);
                        configMessageService.addConfigMessage(null, AppType.ZHUANGXIU, house.getMemberId(), "0", "设计图上传提醒",
                                String.format("业主您好！您的美宅【%s】设计图已经上传，请确认。", house.getHouseName()), "");
                        return ServerResponse.createBySuccessMessage("发送成功");
                    }
                }
            } else {
                criteria.andEqualTo(DesignBusinessOrder.TYPE, 3);
                List<DesignBusinessOrder> designBusinessOrders = designBusinessOrderMapper.selectByExample(example);
                if (designBusinessOrders != null && designBusinessOrders.size() > 0) {
                    DesignBusinessOrder order = designBusinessOrders.get(0);
                    if (order.getOperationState() == 0) {
                        order.setOperationState(2);
                        designBusinessOrderMapper.updateByPrimaryKeySelective(order);
                        configMessageService.addConfigMessage(null, AppType.ZHUANGXIU, house.getMemberId(), "0", "设计图上传提醒",
                                String.format("业主您好！您的美宅【%s】设计图已经上传。", house.getHouseName()), "");
                        return ServerResponse.createBySuccessMessage("发送成功");
                    }
                }
            }
        } else if (house.getDecorationType() == 2) {//自带设计流程
            if (house.getDesignerState() == 1 || house.getDesignerState() == 6 ||
                    house.getDesignerState() == 7 || house.getDesignerState() == 8) {
                return constructionPlans(house);
            }
        } else {
            if (house.getDesignerState() == 9 || house.getDesignerState() == 6) {
                return sendPlan(house);
            } else if (house.getDesignerState() == 7 || house.getDesignerState() == 8) {
                return constructionPlans(house);
            }
        }
        return ServerResponse.createByErrorMessage("设计进度还未达到发送要求");
    }

    /**
     * 发送平面图给业主
     *
     * @param house 房子
     */
    private ServerResponse sendPlan(House house) {
        if (!designDataService.getPlaneMap(house.getId()).isSuccess()) {
            return ServerResponse.createByErrorMessage("请上传平面图");
        }
        house.setDesignerOk(5);//平面图发给业主
        house.setDataStatus(0);
        houseMapper.updateByPrimaryKeySelective(house);
        //app推送给业主

        HouseFlow houseFlow = houseFlowMapper.getByWorkerTypeId(house.getId(), "1");
        //添加一条记录
        HouseFlowApply hfa = new HouseFlowApply();//发起申请任务
        hfa.setHouseFlowId(houseFlow.getId());//工序id
        hfa.setWorkerId(houseFlow.getWorkerId());//工人id
        hfa.setOperator(houseFlow.getWorkerId());
        hfa.setWorkerTypeId(houseFlow.getWorkerTypeId());//工种id
        hfa.setWorkerType(houseFlow.getWorkerType());//工种类型
        hfa.setHouseId(houseFlow.getHouseId());//房子id
        //设计状态,默认0未确定设计师,4有设计抢单待支付,1已支付设计师待发平面图,5平面图发给业主
        // 6平面图审核不通过,7通过平面图待发施工图,2已发给业主施工图,8施工图片审核不通过,3施工图(全部图)审核通过
        hfa.setApplyType(14);
        hfa.setApplyMoney(new BigDecimal(0));//申请得钱
        hfa.setSupervisorMoney(new BigDecimal(0));
        hfa.setOtherMoney(new BigDecimal(0));
        hfa.setMemberCheck(1);//业主审核状态0未审核，1审核通过，2审核不通过，3自动审核
        hfa.setSupervisorCheck(1);//大管家审核状态0未审核，1审核通过，2审核不通过
        hfa.setPayState(0);//是否付款
        hfa.setApplyDec("我是设计师，我已经上传了设计图 ");//描述
        hfa.setIsReadType(0);
        houseFlowApplyMapper.insert(hfa);
        houseService.insertConstructionRecord(hfa);
        configMessageService.addConfigMessage(null, AppType.ZHUANGXIU, house.getMemberId(), "0", "设计图上传提醒",
                String.format(DjConstants.PushMessage.PLANE_UPLOADING, house.getHouseName()), "");
        return ServerResponse.createBySuccessMessage("发送成功");
    }


    /**
     * 发送施工图给业主
     *
     * @param house 房子
     */
    private ServerResponse constructionPlans(House house) {
        if (!designDataService.getConstructionPlans(house.getId()).isSuccess()) {
            return ServerResponse.createByErrorMessage(house.getDecorationType() == 2 ? "请上传设计图" : "请上传施工图");
        }
        if (house.getDecorationType() == 2) {//自带设计直接上传施工图
            house.setDesignerOk(3);
            //增加施工审核流水
            QuantityRoom quantityRoom = new QuantityRoom();
            quantityRoom.setHouseId(house.getHouseId());
            quantityRoom.setOwnerId(house.getMemberId());
            quantityRoom.setRoomType(0);
            quantityRoom.setType(2);
            quantityRoomMapper.insert(quantityRoom);
        } else {
            house.setDesignerOk(2);//施工图(其它图)发给业主
        }
        house.setDataStatus(0);
        houseMapper.updateByPrimaryKeySelective(house);
        //app推送给业主
        HouseFlow houseFlow = houseFlowMapper.getByWorkerTypeId(house.getId(), "1");
        if (houseFlow == null) {
            houseFlow = houseFlowMapper.getByWorkerTypeId(house.getId(), "2");
        }
        //添加一条记录
        HouseFlowApply hfa = new HouseFlowApply();//发起申请任务
        hfa.setHouseFlowId(houseFlow.getId());//工序id
        hfa.setWorkerId(houseFlow.getWorkerId());//工人id
        hfa.setOperator(houseFlow.getWorkerId());
        hfa.setWorkerTypeId(houseFlow.getWorkerTypeId());//工种id
        hfa.setWorkerType(houseFlow.getWorkerType());//工种类型
        hfa.setHouseId(houseFlow.getHouseId());//房子id
        //设计状态,默认0未确定设计师,4有设计抢单待支付,1已支付设计师待发平面图,5平面图发给业主
        // 6平面图审核不通过,7通过平面图待发施工图,2已发给业主施工图,8施工图片审核不通过,3施工图(全部图)审核通过
        hfa.setApplyType(15);
        hfa.setApplyMoney(new BigDecimal(0));//申请得钱
        hfa.setSupervisorMoney(new BigDecimal(0));
        hfa.setOtherMoney(new BigDecimal(0));
        hfa.setMemberCheck(1);//业主审核状态0未审核，1审核通过，2审核不通过，3自动审核
        hfa.setSupervisorCheck(1);//大管家审核状态0未审核，1审核通过，2审核不通过
        hfa.setPayState(0);//是否付款
        if (houseFlow.getWorkerType() == 1) {
            hfa.setApplyDec("我是设计师，我已经上传了施工图");//描述
        } else {
            hfa.setApplyDec("我是精算师，我已经上传了施工图");//描述
        }
        hfa.setIsReadType(0);
        houseFlowApplyMapper.insert(hfa);
        houseService.insertConstructionRecord(hfa);
        configMessageService.addConfigMessage(null, AppType.ZHUANGXIU, house.getMemberId(), "0", "设计图上传提醒",
                String.format(DjConstants.PushMessage.CONSTRUCTION_UPLOADING, house.getHouseName()), "");
        return ServerResponse.createBySuccessMessage("发送成功");
    }

    public ServerResponse invalidHouse(String houseId) {
        House house = houseMapper.selectByPrimaryKey(houseId);
        if (house.getDataStatus() == 0) {
            house.setDataStatus(1);
            house.setIsSelect(0);
            houseMapper.updateByPrimaryKeySelective(house);
            return ServerResponse.createBySuccessMessage("作废成功");
        } else {
            house.setDataStatus(0);
            houseMapper.updateByPrimaryKeySelective(house);
            return ServerResponse.createBySuccessMessage("还原成功");
        }

    }

    /**
     * 业主审核平面图或施工图
     * 。。。。。。。。。。。。。。。。。⦧--6。。⦧--8
     * 远程设计流程：0---4---1---9---5---7---2---3
     *
     * 。。。。。。。。。。。。⦧--6。。⦧--8
     * 自带设计流程：0---1---5---7---2---3
     */
    public ServerResponse checkPass(String userToken, String houseId, int type) {
        House house = houseMapper.selectByPrimaryKey(houseId);
        if (house == null) {
            return ServerResponse.createByErrorMessage("没有查询到相关房子");
        }
        if (house.getVisitState() != 1) {
            return ServerResponse.createByErrorMessage("该房子不在装修中");
        }
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member worker = (Member) object;
        if (!worker.getId().equals(house.getMemberId())) {
            return ServerResponse.createByErrorMessage("您无权操作此房产");
        }
        Example examples = new Example(HouseFlow.class);
        examples.createCriteria().andEqualTo(HouseFlow.HOUSE_ID, house.getId()).andEqualTo(HouseFlow.WORKER_TYPE, "1");
        List<HouseFlow> houseFlows = houseFlowMapper.selectByExample(examples);
        HouseWorkerOrder hwo = null;
        if (houseFlows.size() > 0) {
            hwo = houseWorkerOrderMapper.getByHouseIdAndWorkerTypeId(houseFlows.get(0).getHouseId(), houseFlows.get(0).getWorkerTypeId());
        }
        switch (house.getDesignerState()) {
            case 5://审核平面图
                if (type == 1) {//通过
                    house.setDesignerOk(7);

                    //增加平面审核流水
                    QuantityRoom quantityRoom = new QuantityRoom();
                    quantityRoom.setHouseId(house.getHouseId());
                    quantityRoom.setOwnerId(house.getMemberId());
                    quantityRoom.setFlag(0);
                    quantityRoom.setType(1);
                    quantityRoomMapper.insert(quantityRoom);

                    if (hwo != null) {
                        configMessageService.addConfigMessage(null, AppType.GONGJIANG, hwo.getWorkerId(), "0", "平面图已通过", String.format(DjConstants.PushMessage.PLANE_OK, house.getHouseName()), "");
                    }
                    house.setDataStatus(0);
                    houseMapper.updateByPrimaryKeySelective(house);
                    return ServerResponse.createBySuccessMessage("操作成功");
                } else if (type == 0) {//不通过
                    return houseDesignPayService.checkPass(house, hwo, worker, 1);
                }
                return ServerResponse.createByErrorMessage("请选择是否通过");
            case 2://审核施工图
                if (type == 1) {//通过
                    //增加施工审核流水
                    QuantityRoom quantityRoom = new QuantityRoom();
                    quantityRoom.setHouseId(house.getHouseId());
                    quantityRoom.setOwnerId(house.getMemberId());
                    quantityRoom.setRoomType(0);
                    quantityRoom.setType(2);
                    quantityRoomMapper.insert(quantityRoom);

                    house.setDesignerOk(3);

                    if (hwo != null) {
                        //订单拿钱更新
                        hwo.setHaveMoney(hwo.getWorkPrice());
                        houseWorkerOrderMapper.updateByPrimaryKeySelective(hwo);
                        //用户入账
                        Member member = memberMapper.selectByPrimaryKey(hwo.getWorkerId());
                        if (member != null) {
                            BigDecimal haveMoney = member.getHaveMoney().add(hwo.getWorkPrice());
                            BigDecimal surplusMoney = member.getSurplusMoney().add(hwo.getWorkPrice());
                            member.setHaveMoney(haveMoney);//添加已获总钱
                            member.setSurplusMoney(surplusMoney);//添加余额
                            memberMapper.updateByPrimaryKeySelective(member);
                            //添加流水
                            //处理设计师工钱
                            WorkerDetail workerDetail = new WorkerDetail();
                            workerDetail.setName("设计费");
                            workerDetail.setWorkerId(hwo.getWorkerId());
                            workerDetail.setWorkerName(memberMapper.selectByPrimaryKey(hwo.getWorkerId()).getName());
                            workerDetail.setHouseId(hwo.getHouseId());
                            workerDetail.setMoney(hwo.getWorkPrice());
                            workerDetail.setState(0);//进工钱
                            workerDetail.setWalletMoney(surplusMoney);//更新后的余额
                            workerDetail.setHaveMoney(hwo.getHaveMoney());
                            workerDetail.setHouseWorkerOrderId(hwo.getId());
                            workerDetail.setApplyMoney(hwo.getWorkPrice());
                            workerDetailMapper.insert(workerDetail);
                        }
                    }
                    //生成精算订单，及精算师抢单流程
                    manageBudgetInfo(house);
                    if (hwo != null) {
                        configMessageService.addConfigMessage(null, AppType.GONGJIANG, hwo.getWorkerId(), "0", "施工图已通过", String.format(DjConstants.PushMessage.CONSTRUCTION_OK, house.getHouseName()), "");
                    }
                    house.setDataStatus(0);
                    houseMapper.updateByPrimaryKeySelective(house);
                    return ServerResponse.createBySuccessMessage("操作成功");
                } else if (type == 0) {//不通过
                    return houseDesignPayService.checkPass(house, hwo, worker, 2);
                }
                return ServerResponse.createByErrorMessage("请选择是否通过");
            default:
                return ServerResponse.createByErrorMessage("该房子暂未到需要审核这个环节");
        }
    }

    /**
     * 设计师完工后，精算师扭转环节
     * @param house
     */
    private void manageBudgetInfo(House house){
        WorkerType workerType = workerTypeMapper.selectByPrimaryKey("2");
        Example example = new Example(HouseFlow.class);
        example.createCriteria().andEqualTo(HouseFlow.HOUSE_ID, house.getId()).andEqualTo(HouseFlow.WORKER_TYPE_ID, workerType.getId());
        List<HouseFlow> houseFlowList = houseFlowMapper.selectByExample(example);
        if (houseFlowList.size() > 0) {
            // return ServerResponse.createByErrorMessage("设计通过生成精算houseFlow异常");
            HouseFlow houseFlow=houseFlowList.get(0);
            if(StringUtils.isNotBlank(houseFlow.getWorkerId())){//若已有工匠，则通知精算工匠继续进行精算
                configMessageService.addConfigMessage(null,AppType.GONGJIANG,houseFlow.getWorkerId(),"0","设计图纸已完成",String.format(DjConstants.PushMessage.GZ_T_WORK, house.getHouseName()),"");
            }else{//若没有工匠，但有流程，则将其流程改为抢单状态，让对应的工匠抢单
                houseFlow.setState(2);//已支付待工匠抢单
                houseFlow.setModifyDate(new Date());
                houseFlowMapper.updateByPrimaryKeySelective(houseFlow);
            }

        } else {//若未有精算流程，则生成精算流程，及对应的待支付的精算订单
            HouseFlow houseFlow = new HouseFlow(true);
            houseFlow.setCityId(house.getCityId());
            houseFlow.setWorkerTypeId(workerType.getId());
            houseFlow.setWorkerType(workerType.getType());
            houseFlow.setHouseId(house.getId());
            houseFlow.setState(workerType.getState());
            houseFlow.setSort(workerType.getSort());
            houseFlow.setWorkType(5);//设置待业主支付
            houseFlow.setModifyDate(new Date());
            //这里算出精算费
            // WorkDeposit workDeposit = workDepositMapper.selectByPrimaryKey(house.getWorkDepositId());//结算比例表
            //houseFlow.setWorkPrice(house.getSquare().multiply(workDeposit.getBudgetCost()));
            houseFlowMapper.insert(houseFlow);
            //生成精算订单
            Member member=memberMapper.selectByPrimaryKey(house.getMemberId());
            example=new Example(MemberAddress.class);
            example.createCriteria().andEqualTo(MemberAddress.HOUSE_ID,house.getId());
            MemberAddress memberAddress=iMasterMemberAddressMapper.selectOneByExample(example);
            String addressId="";
            if(memberAddress!=null&& cn.jiguang.common.utils.StringUtils.isNotEmpty(memberAddress.getId())){
                addressId=memberAddress.getId();
            }
            String productJsons = orderService.getBudgetProductJsons(house);
            if(productJsons!=null&&StringUtils.isNotBlank(productJsons)){
                //2.生成订单信息
                ServerResponse serverResponse = paymentService.generateOrderCommon(member, house.getId(), house.getCityId(), productJsons, null, addressId, 1,"2");
                if (serverResponse.getResultObj() != null) {
                    String obj = serverResponse.getResultObj().toString();//获取对应的支付单号码
                    taskStackService.inserTaskStackInfo(house.getId(),member.getId(),"待支付精算费",workerType.getImage(),1,obj);//支付精算的任务
                }
            }
        }
    }

    /**
     * 添加平面图
     *
     * @param userToken 可以为空
     * @param houseId   房子ID
     * @param userId    可以为空
     * @param image     图片只上传一张
     * @return ServerResponse
     */
    public ServerResponse setPlaneMap(String userToken, String houseId, String userId, String image) {
        return setQuantityRoom(userToken, houseId, userId, image, 1, "");
    }

    /**
     * 添加施工图
     *
     * @param userToken 可以为空
     * @param houseId   房子ID
     * @param userId    可以为空
     * @param imageJson 图片Json串，
     *                  [{"name":"图片名称","image":"图片地址","sort":1},{"name":"图片名称","image":"图片地址","sort":1}]
     *                  ,sort为优先级，数字越小越靠前
     * @return ServerResponse
     */
    public ServerResponse setConstructionPlans(String userToken, String houseId, String userId, String imageJson, String productIds) {
        return setQuantityRoom(userToken, houseId, userId, imageJson, 2, productIds);
    }

    /**
     * 添加记录图片
     *
     * @param userToken   可以为空
     * @param houseId     房子ID
     * @param userId      可以为空
     * @param imageString 图片
     * @param type        事务类型：0:量房，1平面图，2施工图
     * @return ServerResponse
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse setQuantityRoom(String userToken, String houseId, String userId, String imageString, int type, String productIds) {
        House house = houseMapper.selectByPrimaryKey(houseId);
        if (house == null) {
            return ServerResponse.createByErrorMessage("没有查询到相关房子");
        }
        if (house.getVisitState() != 1) {
            return ServerResponse.createByErrorMessage("该房子不在装修中");
        }
        Object objectm = constructionService.getMember(userToken);
        Member member = null;
        if (objectm instanceof Member) {
            member = (Member) objectm;
        }
        if (member == null && CommonUtil.isEmpty(userId)) {
            return ServerResponse.createbyUserTokenError();
        }
        if (CommonUtil.isEmpty(imageString)) {
            return ServerResponse.createByErrorMessage("请上传图片");
        }
        switch (type) {
            case 1:
                if (house.getDecorationType() == 2) {
                    if (house.getDesignerState() != 1 && house.getDesignerState() != 6) {
                        return ServerResponse.createByErrorMessage("该阶段无法上传平面图");
                    }
                } else {
                    if (house.getDesignerState() != 9 && house.getDesignerState() != 6) {
                        return ServerResponse.createByErrorMessage("该阶段无法上传平面图");
                    }
                }
                break;
            case 2:
                Example example = new Example(DesignBusinessOrder.class);
                Example.Criteria criteria = example.createCriteria()
                        .andEqualTo(DesignBusinessOrder.DATA_STATUS, 0)
                        .andEqualTo(DesignBusinessOrder.HOUSE_ID, house.getId())
                        .andEqualTo(DesignBusinessOrder.STATUS, 1)
                        .andNotEqualTo(DesignBusinessOrder.OPERATION_STATE, 2);
                if (house.getDecorationType() != 2) {
                    criteria.andEqualTo(DesignBusinessOrder.TYPE, 4);
                } else {
                    criteria.andEqualTo(DesignBusinessOrder.TYPE, 3);
                }
                List<DesignBusinessOrder> designBusinessOrders = designBusinessOrderMapper.selectByExample(example);
                if (designBusinessOrders != null && designBusinessOrders.size() > 0) {
                    DesignBusinessOrder order = designBusinessOrders.get(0);
                    if (order.getOperationState() == 0) {
                        break;
                    }
                }
                if (house.getDecorationType() != 2) {//自带设计不需要判断
                    if (house.getDesignerState() != 7 && house.getDesignerState() != 8) {
                        return ServerResponse.createByErrorMessage("该阶段无法上传施工图");
                    }
                }
                break;
        }
        QuantityRoom quantityRoom = new QuantityRoom();
        if (member != null) {
            quantityRoom.setMemberId(member.getId());
        } else {
            quantityRoom.setUserId(userId);
        }
        quantityRoom.setHouseId(houseId);
        quantityRoom.setType(type);
        quantityRoom.setOperationType(0);
        quantityRoom.setRoomType(0);
        quantityRoom.setFlag(3);
        switch (type) {
            case 1:
                QuantityRoomImages quantityRoomImages = new QuantityRoomImages();
                quantityRoomImages.setHouseId(houseId);
                quantityRoomImages.setQuantityRoomId(quantityRoom.getId());
                quantityRoomImages.setName("平面图");
                quantityRoomImages.setImage(imageString);
                quantityRoomImages.setSort(0);
                house.setImage(imageString);
                quantityRoomImagesMapper.insert(quantityRoomImages);
                break;
            case 2:
                JSONArray jsonArray = JSON.parseArray(imageString);
                if (jsonArray == null || jsonArray.size() <= 0) {
                    return ServerResponse.createByErrorMessage("图片传入格式有误");
                }
                List<QuantityRoomImages> quantityRoomImagesList = new ArrayList<>();
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    if (!CommonUtil.isEmpty(object.getString("image"))
                            && !CommonUtil.isEmpty(object.getString("name"))) {
                        QuantityRoomImages images = new QuantityRoomImages();
                        images.setHouseId(houseId);
                        images.setQuantityRoomId(quantityRoom.getId());
                        images.setName(object.getString("name"));
                        images.setImage(object.getString("image"));
                        images.setSort(object.getInteger("sort"));
                        quantityRoomImagesList.add(images);
                        if (i == 1) {
                            if (CommonUtil.isEmpty(house.getImage())) {
                                house.setImage(object.getString("image"));
                            }
                        }
                    } else {
                        return ServerResponse.createByErrorMessage("图片传入参数有误，请确认图片名和地址无误");
                    }
                }
                if (quantityRoomImagesList.size() > 0) {
                    for (QuantityRoomImages images : quantityRoomImagesList) {
                        quantityRoomImagesMapper.insert(images);
                    }
                } else {
                    return ServerResponse.createByErrorMessage("请传入图片");
                }
                if (!CommonUtil.isEmpty(productIds)) {
                    //删除之前提交的
                    Example example = new Example(DesignQuantityRoomProduct.class);
                    example.createCriteria()
                            .andEqualTo(DesignQuantityRoomProduct.HOUSE_ID, houseId)
                            .andEqualTo(DesignQuantityRoomProduct.TYPE, 0)
                            .andEqualTo(DesignQuantityRoomProduct.DATA_STATUS, 0);
                    DesignQuantityRoomProduct product = new DesignQuantityRoomProduct();
                    product.setId(null);
                    product.setCreateDate(null);
                    product.setDataStatus(1);
                    iMasterQuantityRoomProductMapper.updateByExampleSelective(product, example);
                    String[] productIdList = productIds.split(",");
                    for (String productId : productIdList) {
                        if (!CommonUtil.isEmpty(productId)) {
                            DesignQuantityRoomProduct designQuantityRoomProduct = new DesignQuantityRoomProduct();
                            designQuantityRoomProduct.setHouseId(houseId);
                            designQuantityRoomProduct.setProductId(productId);//商品ID
                            designQuantityRoomProduct.setType(0);//推荐商品
                            iMasterQuantityRoomProductMapper.insertSelective(designQuantityRoomProduct);
                        }
                    }
                }
                break;
        }
        houseMapper.updateByPrimaryKeySelective(house);
        quantityRoomMapper.insert(quantityRoom);
        return ServerResponse.createBySuccessMessage("操作成功");
    }
}
