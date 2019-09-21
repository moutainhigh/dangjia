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
import com.dangjia.acg.mapper.design.IQuantityRoomImagesMapper;
import com.dangjia.acg.mapper.design.IQuantityRoomMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.other.IWorkDepositMapper;
import com.dangjia.acg.mapper.worker.IWorkerDetailMapper;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.core.HouseFlowApply;
import com.dangjia.acg.modle.core.HouseWorkerOrder;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.design.DesignBusinessOrder;
import com.dangjia.acg.modle.design.QuantityRoom;
import com.dangjia.acg.modle.design.QuantityRoomImages;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.other.WorkDeposit;
import com.dangjia.acg.modle.worker.WorkerDetail;
import com.dangjia.acg.service.config.ConfigMessageService;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.dangjia.acg.service.house.HouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.ArrayList;
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
        } else {
            house.setDesignerOk(2);//施工图(其它图)发给业主
        }
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
                    if (hwo != null) {
                        configMessageService.addConfigMessage(null, AppType.GONGJIANG, hwo.getWorkerId(), "0", "平面图已通过", String.format(DjConstants.PushMessage.PLANE_OK, house.getHouseName()), "");
                    }
                    houseMapper.updateByPrimaryKeySelective(house);
                    return ServerResponse.createBySuccessMessage("操作成功");
                } else if (type == 0) {//不通过
                    return houseDesignPayService.checkPass(house, hwo, worker, 1);
                }
                return ServerResponse.createByErrorMessage("请选择是否通过");
            case 2://审核施工图
                if (type == 1) {//通过
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
                    WorkerType workerType = workerTypeMapper.selectByPrimaryKey("2");
                    Example example = new Example(HouseFlow.class);
                    example.createCriteria().andEqualTo(HouseFlow.HOUSE_ID, house.getId()).andEqualTo(HouseFlow.WORKER_TYPE_ID, workerType.getId());
                    List<HouseFlow> houseFlowList = houseFlowMapper.selectByExample(example);
                    if (houseFlowList.size() > 0) {
                        return ServerResponse.createByErrorMessage("设计通过生成精算houseFlow异常");
                    } else {
                        HouseFlow houseFlow = new HouseFlow(true);
                        houseFlow.setCityId(house.getCityId());
                        houseFlow.setWorkerTypeId(workerType.getId());
                        houseFlow.setWorkerType(workerType.getType());
                        houseFlow.setHouseId(house.getId());
                        houseFlow.setState(workerType.getState());
                        houseFlow.setSort(workerType.getSort());
                        houseFlow.setWorkType(2);//设置可抢单
                        //这里算出精算费
                        WorkDeposit workDeposit = workDepositMapper.selectByPrimaryKey(house.getWorkDepositId());//结算比例表
                        houseFlow.setWorkPrice(house.getSquare().multiply(workDeposit.getBudgetCost()));
                        houseFlowMapper.insert(houseFlow);
                    }
                    if (hwo != null) {
                        configMessageService.addConfigMessage(null, AppType.GONGJIANG, hwo.getWorkerId(), "0", "施工图已通过", String.format(DjConstants.PushMessage.CONSTRUCTION_OK, house.getHouseName()), "");
                    }
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
     * 添加平面图
     *
     * @param userToken 可以为空
     * @param houseId   房子ID
     * @param userId    可以为空
     * @param image     图片只上传一张
     * @return ServerResponse
     */
    public ServerResponse setPlaneMap(String userToken, String houseId, String userId, String image) {
        return setQuantityRoom(userToken, houseId, userId, image, 1, null, null);
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
    public ServerResponse setConstructionPlans(String userToken, String houseId, String userId, String imageJson) {
        return setQuantityRoom(userToken, houseId, userId, imageJson, 2, null, null);
    }

    /**
     * 添加量房
     *
     * @param userToken 可以为空
     * @param houseId   房子ID
     * @param userId    可以为空
     * @param images    图片","号分割
     * @return ServerResponse
     */
    public ServerResponse setQuantityRoom(String userToken, String houseId, String userId, String images, Integer elevator, String floor) {
        return setQuantityRoom(userToken, houseId, userId, images, 0, elevator, floor);
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
    private ServerResponse setQuantityRoom(String userToken, String houseId, String userId, String imageString, int type, Integer elevator, String floor) {
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
            case 0:
                if (house.getDecorationType() == 2) {
                    return ServerResponse.createByErrorMessage("自带设计无需量房");
                }
                if (house.getDesignerState() != 1) {
                    return ServerResponse.createByErrorMessage("该阶段无法上传量房信息");
                }
                if (CommonUtil.isEmpty(elevator)) {
                    return ServerResponse.createByErrorMessage("请选择是否为电梯房");
                }
                if (elevator == 0 && CommonUtil.isEmpty(floor)) {
                    return ServerResponse.createByErrorMessage("请输入楼层");
                }
                break;
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
        quantityRoom.setElevator(elevator);
        quantityRoom.setFloor(floor);
        quantityRoom.setType(type);
        quantityRoom.setOperationType(0);
        switch (type) {
            case 0:
                String[] image = imageString.split(",");
                for (int i = 0; i < image.length; i++) {
                    String s = image[i];
                    if (!CommonUtil.isEmpty(s.trim())) {
                        QuantityRoomImages quantityRoomImages = new QuantityRoomImages();
                        quantityRoomImages.setHouseId(houseId);
                        quantityRoomImages.setQuantityRoomId(quantityRoom.getId());
                        quantityRoomImages.setName("量房");
                        quantityRoomImages.setImage(s);
                        quantityRoomImages.setSort(i);
                        quantityRoomImagesMapper.insert(quantityRoomImages);
                    }
                }
                house.setDesignerOk(9);
                houseMapper.updateByPrimaryKeySelective(house);
                //推送消息给业主已完成量房
                configMessageService.addConfigMessage(null, AppType.ZHUANGXIU, house.getMemberId(),
                        "0", "设计师完成量房", String.format(DjConstants.PushMessage.LIANGFANGWANCHENG,
                                house.getHouseName()), "");
                break;
            case 1:
                QuantityRoomImages quantityRoomImages = new QuantityRoomImages();
                quantityRoomImages.setHouseId(houseId);
                quantityRoomImages.setQuantityRoomId(quantityRoom.getId());
                quantityRoomImages.setName("平面图");
                quantityRoomImages.setImage(imageString);
                quantityRoomImages.setSort(0);
                house.setImage(imageString);
                houseMapper.updateByPrimaryKeySelective(house);
                quantityRoomImagesMapper.insert(quantityRoomImages);
                break;
            case 2:
                try {
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
                                    houseMapper.updateByPrimaryKeySelective(house);
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
                } catch (Exception e) {
                    return ServerResponse.createByErrorMessage("图片传入格式有误");
                }
                break;
        }
        quantityRoomMapper.insert(quantityRoom);
        return ServerResponse.createBySuccessMessage("操作成功");
    }

    /**
     * 升级设计
     */
    public ServerResponse upgradeDesign(String userToken, String houseId, String designImageTypeId, int selected) {
        //TODO 设计师升级服务暂时取消
        return ServerResponse.createByErrorMessage("操作失败");
    }
}
