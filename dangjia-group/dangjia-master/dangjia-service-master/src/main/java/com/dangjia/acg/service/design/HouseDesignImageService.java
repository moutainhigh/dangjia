package com.dangjia.acg.service.design;

import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.enums.EventStatus;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.design.HouseDesignImageDTO;
import com.dangjia.acg.dto.design.QuantityRoomDTO;
import com.dangjia.acg.mapper.core.IHouseFlowMapper;
import com.dangjia.acg.mapper.core.IHouseWorkerMapper;
import com.dangjia.acg.mapper.core.IHouseWorkerOrderMapper;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.design.IDesignImageTypeMapper;
import com.dangjia.acg.mapper.design.IHouseDesignImageMapper;
import com.dangjia.acg.mapper.design.IQuantityRoomImagesMapper;
import com.dangjia.acg.mapper.design.IQuantityRoomMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.other.IWorkDepositMapper;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.core.HouseWorker;
import com.dangjia.acg.modle.core.HouseWorkerOrder;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.design.DesignImageType;
import com.dangjia.acg.modle.design.HouseDesignImage;
import com.dangjia.acg.modle.design.QuantityRoom;
import com.dangjia.acg.modle.design.QuantityRoomImages;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.other.WorkDeposit;
import com.dangjia.acg.service.config.ConfigMessageService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author: Ronalcheng
 * Date: 2018/11/8 0008
 * Time: 11:29
 */
@Service
public class HouseDesignImageService {
    @Autowired
    private IHouseMapper houseMapper;
    @Autowired
    private IHouseDesignImageMapper houseDesignImageMapper;
    @Autowired
    private IWorkerTypeMapper workerTypeMapper;
    @Autowired
    private IHouseFlowMapper houseFlowMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IHouseWorkerOrderMapper houseWorkerOrderMapper;
    @Autowired
    private ConfigMessageService configMessageService;
    @Autowired
    private IWorkDepositMapper workDepositMapper;
    @Autowired
    private DesignService designService;
    @Autowired
    private RedisClient redisClient;//缓存
    @Autowired
    private IQuantityRoomMapper quantityRoomMapper;
    @Autowired
    private IQuantityRoomImagesMapper quantityRoomImagesMapper;


    /**
     * 查看施工图
     */
    public ServerResponse designImageList(String houseId) {
        try {
            if (StringUtil.isEmpty(houseId)) {
                return ServerResponse.createByErrorMessage("houseId不能为空");
            }
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            List<HouseDesignImageDTO> houseDesignImageList = houseDesignImageMapper.queryHouseDesignImage(houseId);
            if (houseDesignImageList == null || houseDesignImageList.size() == 0) {
                return ServerResponse.createByErrorMessage("找不到房子对应图类型");
            }
            for (HouseDesignImageDTO houseDesignImage : houseDesignImageList) {
                if (!CommonUtil.isEmpty(houseDesignImage.getImageurl())) {
                    if (StringUtil.isNotEmpty(houseDesignImage.getImageurl())) {
                        houseDesignImage.setImageurl(address + houseDesignImage.getImageurl());
                    }
                }
            }
            return ServerResponse.createBySuccess("查询成功", houseDesignImageList);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 设计通过
     * 发送设计图业主
     * 设计状态:
     * 0=未确定设计师
     * 4=设计待抢单
     * 1=已支付-设计师待量房
     * 9=量房图确认，设计师待发平面图
     * 5=平面图发给业主
     * 6=平面图审核不通过
     * 7=通过平面图待发施工图
     * 2=已发给业主施工图
     * 8=施工图片审核不通过
     * 3=施工图(全部图)审核通过
     * 。。。。。。。。。。。。。。。。。⦧--6。。⦧--8
     * 远程设计流程：0---4---1---9---5---7---2---3
     *
     * 。。。。。。。。。。。。⦧--6。。⦧--8
     * 自带设计流程：0---1---5---7---2---3
     */
    public ServerResponse checkPass(String houseId, int type) {
        House house = houseMapper.selectByPrimaryKey(houseId);
        if (house == null) {
            return ServerResponse.createByErrorMessage("没有查询到相关房子");
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
     * 审核设计图
     */
    public ServerResponse checkDesign(String houseId) {
        Map<String, Object> map = new HashMap<>();
        House house = houseMapper.selectByPrimaryKey(houseId);
        List<HouseDesignImageDTO> houseDesignImageDTOList = new ArrayList<>();
        List<HouseDesignImage> houseDesignImageList;
        HouseDesignImageDTO houseDesignImageDTO;
        Example example = new Example(HouseDesignImage.class);
        if (house.getDesignerOk() == 5) {
            example.createCriteria()
                    .andEqualTo(HouseDesignImage.HOUSE_ID, houseId)
                    .andEqualTo(HouseDesignImage.DESIGN_IMAGE_TYPE_ID, "1");
            example.orderBy(HouseDesignImage.CREATE_DATE).desc();
            houseDesignImageList = houseDesignImageMapper.selectByExample(example);
            HouseDesignImage houseDesignImage = houseDesignImageList.get(0);
            houseDesignImageDTO = new HouseDesignImageDTO();
            houseDesignImageDTO.setImageurl(configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class) + houseDesignImage.getImageurl());
            houseDesignImageDTO.setName("平面图");
            houseDesignImageDTOList.add(houseDesignImageDTO);
            map.put("button", "确认平面图");
            map.put("list", houseDesignImageDTOList);
            return ServerResponse.createBySuccess("查询成功", map);
        } else if (house.getDesignerOk() == 2) {
            ServerResponse serverResponse = designService.getImagesList(houseId);
            map.put("list", serverResponse.getResultObj());
            map.put("button", "确认施工图");
            return ServerResponse.createBySuccess("查询成功", map);
        }
        return ServerResponse.createByErrorMessage("查询失败");
    }

    /**
     * 升级设计
     */
    public ServerResponse upgradeDesign(String userToken, String houseId, String designImageTypeId, int selected) {
        try {
            if (selected == 0) {//新增
                HouseDesignImage houseDesignImage = new HouseDesignImage();
                houseDesignImage.setHouseId(houseId);
                houseDesignImage.setDesignImageTypeId(designImageTypeId);
                houseDesignImage.setSell(1);
                houseDesignImageMapper.insert(houseDesignImage);
            } else {//删除
                Example example = new Example(HouseDesignImage.class);
                example.createCriteria().andEqualTo("houseId", houseId).andEqualTo("designImageTypeId", designImageTypeId);
                houseDesignImageMapper.deleteByExample(example);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
        return ServerResponse.createBySuccessMessage("操作成功");
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
        AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
        if (accessToken == null && CommonUtil.isEmpty(userId)) {
            return ServerResponse.createByErrorCodeMessage(EventStatus.USER_TOKEN_ERROR.getCode(), EventStatus.USER_TOKEN_ERROR.getDesc());
        }
        if (CommonUtil.isEmpty(images)) {
            return ServerResponse.createByErrorMessage("请上传图片");
        }
        House house = houseMapper.selectByPrimaryKey(houseId);
        if (house == null) {
            return ServerResponse.createByErrorMessage("没有查询到相关房子");
        }
        if (house.getDecorationType() == 2) {
            return ServerResponse.createByErrorMessage("自带设计无需量房");
        }
        if (house.getDesignerOk() != 1) {
            return ServerResponse.createByErrorMessage("该阶段无法上传量房信息");
        }
        QuantityRoom quantityRoom = new QuantityRoom();
        if (accessToken != null) {
            quantityRoom.setMemberId(accessToken.getMemberId());
        } else {
            quantityRoom.setUserId(userId);
        }
        quantityRoom.setHouseId(houseId);
        quantityRoom.setType(0);
        quantityRoom.setOperationType(0);
        quantityRoomMapper.insert(quantityRoom);
        String[] image = images.split(",");
        for (String s : image) {
            if (!CommonUtil.isEmpty(s.trim())) {
                QuantityRoomImages quantityRoomImages = new QuantityRoomImages();
                quantityRoomImages.setHouseId(houseId);
                quantityRoomImages.setQuantityRoomId(quantityRoom.getId());
                quantityRoomImages.setName("量房");
                quantityRoomImages.setImage(s);
                quantityRoomImagesMapper.insert(quantityRoomImages);
            }
        }
        house.setDesignerOk(9);
        houseMapper.updateByPrimaryKeySelective(house);
        return ServerResponse.createBySuccessMessage("操作成功");
    }

    /**
     * 获取量房记录
     *
     * @param houseId 房子ID
     * @return ServerResponse
     */
    public ServerResponse getQuantityRoom(String houseId) {
        House house = houseMapper.selectByPrimaryKey(houseId);
        if (house == null) {
            return ServerResponse.createByErrorMessage("没有查询到相关房子");
        }
        QuantityRoomDTO quantityRoomDTO = quantityRoomMapper.getQuantityRoom(houseId, 0);
        if (quantityRoomDTO == null) {
            return ServerResponse.createByErrorCodeMessage(EventStatus.NO_DATA.getCode(), "无量房信息");
        }
        Example example = new Example(QuantityRoomImages.class);
        example.createCriteria()
                .andEqualTo(QuantityRoomImages.QUANTITY_ROOM_ID, quantityRoomDTO.getId())
                .andEqualTo(QuantityRoomImages.DATA_STATUS, 0);
        example.orderBy(QuantityRoomImages.CREATE_DATE).desc();
        List<QuantityRoomImages> quantityRoomImages = quantityRoomImagesMapper.selectByExample(example);
        if (quantityRoomImages == null || quantityRoomImages.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(EventStatus.NO_DATA.getCode(), "无量房图片");
        }
        String imageAddress = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        for (QuantityRoomImages quantityRoomImage : quantityRoomImages) {
            quantityRoomImage.initPath(imageAddress);
        }
        quantityRoomDTO.setIamges(quantityRoomImages);
        return ServerResponse.createBySuccess("获取成功", quantityRoomDTO);
    }
}
