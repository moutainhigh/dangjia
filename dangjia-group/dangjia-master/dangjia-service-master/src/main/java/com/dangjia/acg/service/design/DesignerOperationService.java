package com.dangjia.acg.service.design;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.enums.EventStatus;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.mapper.core.IHouseFlowMapper;
import com.dangjia.acg.mapper.core.IHouseWorkerOrderMapper;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.design.IQuantityRoomImagesMapper;
import com.dangjia.acg.mapper.design.IQuantityRoomMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.other.IWorkDepositMapper;
import com.dangjia.acg.mapper.worker.IWorkerDetailMapper;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.core.HouseWorkerOrder;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.design.QuantityRoom;
import com.dangjia.acg.modle.design.QuantityRoomImages;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.other.WorkDeposit;
import com.dangjia.acg.modle.worker.WorkerDetail;
import com.dangjia.acg.service.config.ConfigMessageService;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

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
    //    @Autowired
//    private IHouseDesignImageMapper houseDesignImageMapper;//房子关联设计图
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
    private RedisClient redisClient;//缓存
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
        if (house.getDecorationType() == 2) {//自带设计流程
            if (house.getDesignerOk() == 1 || house.getDesignerOk() == 6) {
                return sendPlan(house);
            } else if (house.getDesignerOk() == 7 || house.getDesignerOk() == 8) {
                return constructionPlans(house);
            } else {
                return ServerResponse.createByErrorMessage("设计进度还未达到发送要求");
            }
        } else {
            if (house.getDesignerOk() == 9 || house.getDesignerOk() == 6) {
                return sendPlan(house);
            } else if (house.getDesignerOk() == 7 || house.getDesignerOk() == 8) {
                return constructionPlans(house);
            } else {
                return ServerResponse.createByErrorMessage("设计进度还未达到发送要求");
            }
        }
    }

    /**
     * 发送平面图给业主
     *
     * @param house 房子
     * @return
     */
    private ServerResponse sendPlan(House house) {
        if (!designDataService.getPlaneMap(house.getId()).isSuccess()) {
            return ServerResponse.createByErrorMessage("请上传平面图");
        }
        house.setDesignerOk(5);//平面图发给业主
        houseMapper.updateByPrimaryKeySelective(house);
        //app推送给业主
        configMessageService.addConfigMessage(null, "zx", house.getMemberId(), "0", "设计图上传提醒",
                String.format(DjConstants.PushMessage.PLANE_UPLOADING, house.getHouseName()), "");
        return ServerResponse.createBySuccessMessage("发送成功");
    }


    /**
     * 发送施工图给业主
     *
     * @param house 房子
     * @return
     */
    private ServerResponse constructionPlans(House house) {
        if (!designDataService.getConstructionPlans(house.getId()).isSuccess()) {
            return ServerResponse.createByErrorMessage("请上传平面图");
        }
        house.setDesignerOk(2);//施工图(其它图)发给业主
        houseMapper.updateByPrimaryKeySelective(house);
        //app推送给业主
        configMessageService.addConfigMessage(null, "zx", house.getMemberId(), "0", "设计图上传提醒",
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
        switch (house.getDesignerOk()) {
            case 5://审核平面图
                if (type == 1) {//通过
                    house.setDesignerOk(7);
                    if (hwo != null) {
                        configMessageService.addConfigMessage(null, "gj", hwo.getWorkerId(), "0", "平面图已通过", String.format(DjConstants.PushMessage.PLANE_OK, house.getHouseName()), "");
                    }
                } else if (type == 0) {//不通过
                    house.setDesignerOk(6);
                    if (hwo != null) {
                        configMessageService.addConfigMessage(null, "gj", hwo.getWorkerId(), "0", "平面图未通过", String.format(DjConstants.PushMessage.PLANE_ERROR, house.getHouseName()), "");
                    }
                }
                houseMapper.updateByPrimaryKeySelective(house);
                return ServerResponse.createBySuccessMessage("操作成功");
            case 2://审核施工图
                if (type == 1) {//通过
                    house.setDesignerOk(3);
                    if (hwo != null) {
                        hwo.setHaveMoney(hwo.getWorkPrice());
                        houseWorkerOrderMapper.updateByPrimaryKeySelective(hwo);
                        //处理设计师工钱
                        WorkerDetail workerDetail = new WorkerDetail();
                        workerDetail.setName("设计费");
                        workerDetail.setWorkerId(hwo.getWorkerId());
                        workerDetail.setWorkerName(memberMapper.selectByPrimaryKey(hwo.getWorkerId()).getName());
                        workerDetail.setHouseId(hwo.getHouseId());
                        workerDetail.setMoney(hwo.getWorkPrice());
                        workerDetail.setState(0);//进工钱
                        workerDetail.setHaveMoney(hwo.getHaveMoney());
                        workerDetail.setHouseWorkerOrderId(hwo.getId());
                        workerDetail.setApplyMoney(hwo.getWorkPrice());
                        workerDetailMapper.insert(workerDetail);
                    }
                    //TODO 设计师钱没有进入账户余额
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
                        configMessageService.addConfigMessage(null, "gj", hwo.getWorkerId(), "0", "施工图已通过", String.format(DjConstants.PushMessage.CONSTRUCTION_OK, house.getHouseName()), "");
                    }
                } else if (type == 0) {//不通过
                    house.setDesignerOk(8);
                    if (hwo != null) {
                        configMessageService.addConfigMessage(null, "gj", hwo.getWorkerId(), "0", "施工图未通过", String.format(DjConstants.PushMessage.CONSTRUCTION_ERROR, house.getHouseName()), "");
                    }
                }
                houseMapper.updateByPrimaryKeySelective(house);
                return ServerResponse.createBySuccessMessage("操作成功");
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
        return setQuantityRoom(userToken, houseId, userId, image, 1);
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
        return setQuantityRoom(userToken, houseId, userId, imageJson, 2);
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
    public ServerResponse setQuantityRoom(String userToken, String houseId, String userId, String images) {
        return setQuantityRoom(userToken, houseId, userId, images, 0);
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
    private ServerResponse setQuantityRoom(String userToken, String houseId, String userId, String imageString, int type) {
        AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
        if (accessToken == null && CommonUtil.isEmpty(userId)) {
            return ServerResponse.createByErrorCodeMessage(EventStatus.USER_TOKEN_ERROR.getCode(), EventStatus.USER_TOKEN_ERROR.getDesc());
        }
        if (CommonUtil.isEmpty(imageString)) {
            return ServerResponse.createByErrorMessage("请上传图片");
        }
        House house = houseMapper.selectByPrimaryKey(houseId);
        if (house == null) {
            return ServerResponse.createByErrorMessage("没有查询到相关房子");
        }
        switch (type) {
            case 0:
                if (house.getDecorationType() == 2) {
                    return ServerResponse.createByErrorMessage("自带设计无需量房");
                }
                if (house.getDesignerOk() != 1) {
                    return ServerResponse.createByErrorMessage("该阶段无法上传量房信息");
                }
                break;
            case 1:
                if (house.getDecorationType() == 2) {
                    if (house.getDesignerOk() != 1 || house.getDesignerOk() != 6) {
                        return ServerResponse.createByErrorMessage("该阶段无法上传平面图");
                    }
                } else {
                    if (house.getDesignerOk() != 9 || house.getDesignerOk() != 6) {
                        return ServerResponse.createByErrorMessage("该阶段无法上传平面图");
                    }
                }
                break;
            case 2:
                if (house.getDesignerOk() != 7 || house.getDesignerOk() != 8) {
                    return ServerResponse.createByErrorMessage("该阶段无法上传施工图");
                }
                break;
        }
        QuantityRoom quantityRoom = new QuantityRoom();
        if (accessToken != null) {
            quantityRoom.setMemberId(accessToken.getMemberId());
        } else {
            quantityRoom.setUserId(userId);
        }
        quantityRoom.setHouseId(houseId);
        quantityRoom.setType(type);
        quantityRoom.setOperationType(0);
        QuantityRoomImages quantityRoomImages;
        switch (type) {
            case 0:
                String[] image = imageString.split(",");
                for (int i = 0; i < image.length; i++) {
                    String s = image[i];
                    if (!CommonUtil.isEmpty(s.trim())) {
                        quantityRoomImages = new QuantityRoomImages();
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
                break;
            case 1:
                quantityRoomImages = new QuantityRoomImages();
                quantityRoomImages.setHouseId(houseId);
                quantityRoomImages.setQuantityRoomId(quantityRoom.getId());
                quantityRoomImages.setName("平面图");
                quantityRoomImages.setImage(imageString);
                quantityRoomImages.setSort(0);
                quantityRoomImagesMapper.insert(quantityRoomImages);
                break;
            case 2:
                try {
                    JSONArray jsonArray = JSON.parseArray(imageString);
                    if (jsonArray == null || jsonArray.size() <= 0) {
                        return ServerResponse.createByErrorMessage("图片传入格式有误");
                    }
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        if (!CommonUtil.isEmpty(object.getString("image"))
                                && !CommonUtil.isEmpty(object.getString("name"))) {
                            quantityRoomImages = new QuantityRoomImages();
                            quantityRoomImages.setHouseId(houseId);
                            quantityRoomImages.setQuantityRoomId(quantityRoom.getId());
                            quantityRoomImages.setName(object.getString("name"));
                            quantityRoomImages.setImage(object.getString("image"));
                            quantityRoomImages.setSort(object.getInteger("sort"));
                            quantityRoomImagesMapper.insert(quantityRoomImages);
                        }
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
//        try {
//            if (selected == 0) {//新增
//                HouseDesignImage houseDesignImage = new HouseDesignImage();
//                houseDesignImage.setHouseId(houseId);
//                houseDesignImage.setDesignImageTypeId(designImageTypeId);
//                houseDesignImage.setSell(1);
//                houseDesignImageMapper.insert(houseDesignImage);
//            } else {//删除
//                Example example = new Example(HouseDesignImage.class);
//                example.createCriteria().andEqualTo("houseId", houseId).andEqualTo("designImageTypeId", designImageTypeId);
//                houseDesignImageMapper.deleteByExample(example);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
        return ServerResponse.createByErrorMessage("操作失败");
//        }
//        return ServerResponse.createBySuccessMessage("操作成功");
    }
}
