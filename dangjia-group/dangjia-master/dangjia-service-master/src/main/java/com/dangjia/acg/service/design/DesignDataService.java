package com.dangjia.acg.service.design;

import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.enums.EventStatus;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.design.QuantityRoomDTO;
import com.dangjia.acg.dto.house.DesignDTO;
import com.dangjia.acg.mapper.core.IHouseWorkerMapper;
import com.dangjia.acg.mapper.design.IQuantityRoomImagesMapper;
import com.dangjia.acg.mapper.design.IQuantityRoomMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.user.UserMapper;
import com.dangjia.acg.modle.core.HouseWorker;
import com.dangjia.acg.modle.design.QuantityRoomImages;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.user.MainUser;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ruking.Cheng
 * @descrilbe 设计相关资料
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/5/6 5:57 PM
 */
@Service
public class DesignDataService {
    @Autowired
    private IHouseMapper houseMapper;
    //    @Autowired
//    private IDesignImageTypeMapper designImageTypeMapper;
//    @Autowired
//    private IHouseDesignImageMapper houseDesignImageMapper;//房子关联设计图
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private CraftsmanConstructionService constructionService;
    @Autowired
    private IQuantityRoomMapper quantityRoomMapper;
    @Autowired
    private IQuantityRoomImagesMapper quantityRoomImagesMapper;
    @Autowired
    private IMemberMapper memberMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private IHouseWorkerMapper houseWorkerMapper;


    /**
     * 获取平面图
     *
     * @param houseId houseId
     * @return ServerResponse
     */
    public ServerResponse getPlaneMap(String houseId) {
        return getQuantityRoom(houseId, 1);

    }

    /**
     * 获取施工图
     *
     * @param houseId houseId
     * @return ServerResponse
     */
    public ServerResponse getConstructionPlans(String houseId) {
        return getQuantityRoom(houseId, 2);
    }

    /**
     * 获取设计图
     *
     * @param userToken userToken
     * @param houseId   houseId
     * @return ServerResponse
     */
    public ServerResponse getDesign(String userToken, String houseId) {
        Object object = constructionService.getMember(userToken);
        Member worker = null;
        if (object instanceof Member) {
            worker = (Member) object;
        }
        House house = houseMapper.selectByPrimaryKey(houseId);
        if (house == null) {
            return ServerResponse.createByErrorMessage("没有查询到相关房子");
        }
        Map<String, Object> map = new HashMap<>();//返回体
        if (worker != null && house.getDesignerOk() != 3 && worker.getId().equals(house.getMemberId())) {//是业主而且没有设计完工将走审核逻辑
            if (house.getDesignerOk() != 5 && house.getDesignerOk() != 2) {
                return ServerResponse.createByErrorCodeMessage(EventStatus.NO_DATA.getCode(), "无相关记录");
            }
            if (house.getDesignerOk() == 5) {
                ServerResponse serverResponse = getPlaneMap(houseId);
                if (!serverResponse.isSuccess()) {
                    return serverResponse;
                }
                QuantityRoomDTO quantityRoomDTO = (QuantityRoomDTO) serverResponse.getResultObj();
                map.put("data", quantityRoomDTO.getImages());
                map.put("verification", 1);
                map.put("historyRecord", 0);//是否暂时历史记录
                map.put("button", "确认平面图");
                return ServerResponse.createBySuccess("查询成功", map);
            } else {
                ServerResponse serverResponse = getConstructionPlans(houseId);
                if (!serverResponse.isSuccess()) {
                    return serverResponse;
                }
                QuantityRoomDTO quantityRoomDTO = (QuantityRoomDTO) serverResponse.getResultObj();
                map.put("data", quantityRoomDTO.getImages());
                map.put("verification", 1);
                map.put("button", "确认施工图");
                map.put("historyRecord", 0);//是否暂时历史记录
                return ServerResponse.createBySuccess("查询成功", map);
            }
        } else {
            List<QuantityRoomImages> quantityRoomImages = new ArrayList<>();
//            ServerResponse serverResponse = getPlaneMap(houseId);
//            if (serverResponse.isSuccess()) {
//                QuantityRoomDTO quantityRoomDTO = (QuantityRoomDTO) serverResponse.getResultObj();
//                quantityRoomImages.addAll(quantityRoomDTO.getImages());
//            }
            ServerResponse serverResponse = getConstructionPlans(houseId);
            if (serverResponse.isSuccess()) {
                QuantityRoomDTO quantityRoomDTO = (QuantityRoomDTO) serverResponse.getResultObj();
                quantityRoomImages.addAll(quantityRoomDTO.getImages());
            }
            if (quantityRoomImages.size() <= 0) {
                return ServerResponse.createByErrorCodeMessage(EventStatus.NO_DATA.getCode(), "无相关记录");
            }
            map.put("data", quantityRoomImages);
            map.put("verification", 0);
            if (worker == null) {
                map.put("historyRecord", 0);//是否暂时历史记录
            } else {
                if (house.getDecorationType() == 2) {
                    map.put("historyRecord", (worker.getWorkerType() != null && worker.getWorkerType() == 2) ? 1 : 0);//是否暂时历史记录
                } else {
                    map.put("historyRecord", (worker.getWorkerType() != null && worker.getWorkerType() == 1) ? 1 : 0);//是否暂时历史记录
                }
            }
            return ServerResponse.createBySuccess("查询成功", map);
        }
    }

    /**
     * 获取历史记录
     *
     * @param pageDTO pageDTO
     * @param houseId houseId
     * @param type    事务类型：0:量房，1平面图，2施工图
     * @return ServerResponse
     */
    public ServerResponse getOdlQuantityRoomList(PageDTO pageDTO, String houseId, int type) {
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<QuantityRoomDTO> quantityRoomDTOS = quantityRoomMapper.getQuantityRoomList(houseId, type);
        if (quantityRoomDTOS == null || quantityRoomDTOS.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(EventStatus.NO_DATA.getCode(), "无相关记录");
        }
        PageInfo pageResult = new PageInfo(quantityRoomDTOS);
        for (QuantityRoomDTO quantityRoomDTO : quantityRoomDTOS) {
            quantityRoomDTO.setUserType(-1);
            getUserName(quantityRoomDTO);
        }
        pageResult.setList(quantityRoomDTOS);
        return ServerResponse.createBySuccess("查询历史记录成功", pageResult);

    }

    private void getUserName(QuantityRoomDTO quantityRoomDTO) {
        if (!CommonUtil.isEmpty(quantityRoomDTO.getMemberId())) {
            Member member = memberMapper.selectByPrimaryKey(quantityRoomDTO.getMemberId());
            if (member != null) {
                quantityRoomDTO.setUserType(0);
                quantityRoomDTO.setUserName(CommonUtil.isEmpty(member.getName()) ? member.getNickName() : member.getName());
            }
        } else if (!CommonUtil.isEmpty(quantityRoomDTO.getUserId())) {
            MainUser mainUser = userMapper.selectByPrimaryKey(quantityRoomDTO.getUserId());
            if (mainUser != null) {
                quantityRoomDTO.setUserType(1);
                quantityRoomDTO.setUserName(mainUser.getUsername());
            }
        }
    }

    /**
     * 通过ID获取对应的信息
     *
     * @param quantityRoomId 量房/平面图/设计图ID
     * @return ServerResponse
     */
    public ServerResponse getIdQuantityRoom(String quantityRoomId) {
        return getQuantityRoom(quantityRoomMapper.getIdQuantityRoom(quantityRoomId));
    }

    /**
     * 获取量房记录
     *
     * @param houseId 房子ID
     * @return ServerResponse
     */
    public ServerResponse getQuantityRoom(String houseId) {
        return getQuantityRoom(houseId, 0);
    }

    private ServerResponse getQuantityRoom(String houseId, int type) {
        House house = houseMapper.selectByPrimaryKey(houseId);
        if (house == null) {
            return ServerResponse.createByErrorMessage("没有查询到相关房子");
        }
        return getQuantityRoom(quantityRoomMapper.getQuantityRoom(houseId, type));
    }

    private ServerResponse getQuantityRoom(QuantityRoomDTO quantityRoomDTO) {
        if (quantityRoomDTO == null) {
            return ServerResponse.createByErrorCodeMessage(EventStatus.NO_DATA.getCode(), "无相关信息");
        }
        quantityRoomDTO.setUserType(-1);
        getUserName(quantityRoomDTO);
        Example example = new Example(QuantityRoomImages.class);
        example.createCriteria()
                .andEqualTo(QuantityRoomImages.QUANTITY_ROOM_ID, quantityRoomDTO.getId())
                .andEqualTo(QuantityRoomImages.DATA_STATUS, 0);
        example.orderBy(QuantityRoomImages.SORT).asc();
        List<QuantityRoomImages> quantityRoomImages = quantityRoomImagesMapper.selectByExample(example);
        if (quantityRoomImages == null || quantityRoomImages.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(EventStatus.NO_DATA.getCode(), "无相关图片");
        }
        String imageAddress = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        for (QuantityRoomImages quantityRoomImage : quantityRoomImages) {
            quantityRoomImage.initPath(imageAddress);
        }
        quantityRoomDTO.setImages(quantityRoomImages);
        return ServerResponse.createBySuccess("获取成功", quantityRoomDTO);
    }

    /**
     * 设计任务列表
     * 。。。。。。。。。。。。。。。。。⦧--6。。⦧--8
     * 远程设计流程：0---4---1---9---5---7---2---3
     *
     * 。。。。。。。。。。。。⦧--6。。⦧--8
     * 自带设计流程：0---1---5---7---2---3
     *
     * @param pageDTO      分页码
     * @param designerType 0：未支付和设计师未抢单，1：带量房，2：平面图，3：施工图，4：完工
     * @param searchKey    业主手机号/房子名称
     */
    public ServerResponse getDesignList(PageDTO pageDTO, int designerType, String searchKey) {
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        String dataStatus = "0";//正常数据
        if (designerType < 0) {
            //当类型小于0时，则查询移除的数据
            dataStatus = "1";
        }
        List<DesignDTO> designDTOList = houseMapper.getDesignList(designerType, searchKey, dataStatus);
        PageInfo pageResult = new PageInfo(designDTOList);
        for (DesignDTO designDTO : designDTOList) {
            HouseWorker houseWorker = houseWorkerMapper.getHwByHidAndWtype(designDTO.getHouseId(), 1);
            if (houseWorker != null) {
                Member workerSup = memberMapper.selectByPrimaryKey(houseWorker.getWorkerId());
                if (workerSup != null) {
                    designDTO.setOperatorName(workerSup.getName());//大管家名字
                    designDTO.setOperatorMobile(workerSup.getMobile());
                    designDTO.setOperatorId(workerSup.getId());
                }
            }
            ServerResponse serverResponse = getPlaneMap(designDTO.getHouseId());
            if (!serverResponse.isSuccess()) {
                serverResponse = getConstructionPlans(designDTO.getHouseId());
            }
            if (serverResponse.isSuccess()) {
                QuantityRoomDTO quantityRoomDTO = (QuantityRoomDTO) serverResponse.getResultObj();
                List<QuantityRoomImages> images = quantityRoomDTO.getImages();
                if (images != null && images.size() > 0) {
                    String imageAddress = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
                    designDTO.setImage(images.get(0).getBaseImage(imageAddress));
                    designDTO.setImageUrl(images.get(0).getImage());
                }
            }
            if (designDTO.getDecorationType() == 2) {//自带设计流程
                switch (designDTO.getDesignerOk()) {
                    case 0://0未确定设计师
                        designDTO.setSchedule("待抢单");
                        break;
                    case 4://4设计待抢单
                    case 1://1已支付-设计师待量房
                    case 9://9量房图发给业主
                        designDTO.setSchedule("待上传平面图");
                        break;
                    case 5://5平面图发给业主 （发给了业主）
                        designDTO.setSchedule("待审核平面图");
                        break;
                    case 6://6平面图审核不通过（NG，可编辑平面图）
                        designDTO.setSchedule("待修改平面图");
                        break;
                    case 7://7通过平面图待发施工图（OK，可编辑施工图）
                        designDTO.setSchedule("待上传施工图");
                        break;
                    case 2://2已发给业主施工图 （发给了业主）
                        designDTO.setSchedule("待审核施工图");
                        break;
                    case 8://8施工图片审核不通过（NG，可编辑施工图）
                        designDTO.setSchedule("待修改施工图");
                        break;
                    case 3://施工图(全部图)审核通过（OK，完成）
                        designDTO.setSchedule("完成");
                        break;
                }
            } else {
                switch (designDTO.getDesignerOk()) {
                    case 0://0未确定设计师
                        designDTO.setSchedule("待抢单");
                        break;
                    case 4://4设计待抢单
                        designDTO.setSchedule("待业主支付");
                        break;
                    case 1://1已支付-设计师待量房
                        designDTO.setSchedule("待量房");
                        break;
                    case 9://9量房图发给业主
                        designDTO.setSchedule("待上传平面图");
                        break;
                    case 5://5平面图发给业主 （发给了业主）
                        designDTO.setSchedule("待审核平面图");
                        break;
                    case 6://6平面图审核不通过（NG，可编辑平面图）
                        designDTO.setSchedule("待修改平面图");
                        break;
                    case 7://7通过平面图待发施工图（OK，可编辑施工图）
                        designDTO.setSchedule("待上传施工图");
                        break;
                    case 2://2已发给业主施工图 （发给了业主）
                        designDTO.setSchedule("待审核施工图");
                        break;
                    case 8://8施工图片审核不通过（NG，可编辑施工图）
                        designDTO.setSchedule("待修改施工图");
                        break;
                    case 3://施工图(全部图)审核通过（OK，完成）
                        designDTO.setSchedule("完成");
                        break;
                }
            }
        }
        pageResult.setList(designDTOList);
        return ServerResponse.createBySuccess("查询用户列表成功", pageResult);
    }

}
