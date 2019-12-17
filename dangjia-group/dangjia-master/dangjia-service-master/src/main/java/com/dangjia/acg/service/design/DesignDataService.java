package com.dangjia.acg.service.design;

import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.design.DesignListDTO;
import com.dangjia.acg.dto.design.QuantityInfoDTO;
import com.dangjia.acg.dto.design.QuantityRoomDTO;
import com.dangjia.acg.dto.house.DesignDTO;
import com.dangjia.acg.dto.house.UserInfoDateDTO;
import com.dangjia.acg.mapper.core.IHouseFlowMapper;
import com.dangjia.acg.mapper.core.IHouseWorkerMapper;
import com.dangjia.acg.mapper.design.IDesignBusinessOrderMapper;
import com.dangjia.acg.mapper.design.IPayConfigurationMapper;
import com.dangjia.acg.mapper.design.IQuantityRoomImagesMapper;
import com.dangjia.acg.mapper.design.IQuantityRoomMapper;
import com.dangjia.acg.mapper.house.HouseRemarkMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.user.UserMapper;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.core.HouseWorker;
import com.dangjia.acg.modle.design.DesignBusinessOrder;
import com.dangjia.acg.modle.design.PayConfiguration;
import com.dangjia.acg.modle.design.QuantityRoomImages;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.house.HouseRemark;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.user.MainUser;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.dangjia.acg.util.Utils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

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
    @Autowired
    private IPayConfigurationMapper payConfigurationMapper;
    @Autowired
    private IDesignBusinessOrderMapper designBusinessOrderMapper;
    @Autowired
    private HouseRemarkMapper houseRemarkMapper;
    @Autowired
    private IHouseFlowMapper houseFlowMapper;

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
    public ServerResponse getDesign(String userToken, String houseId, Integer type) {
        Object object = constructionService.getMember(userToken);
        Member worker = null;
        if (object instanceof Member) {
            worker = (Member) object;
        }
        House house = houseMapper.selectByPrimaryKey(houseId);
        if (house == null) {
            return ServerResponse.createByErrorMessage("没有查询到相关房子");
        }
        if (house.getVisitState() == 4) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), "已经提前结束装修");
        }
        DesignListDTO designDTO = new DesignListDTO();
        if (worker != null && house.getDesignerState() != 3 && worker.getId().equals(house.getMemberId())) {//是业主而且没有设计完工将走审核逻辑
            if (!CommonUtil.isEmpty(type) && type == 0) {
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), "设计师还在设计中");
            }
            if (house.getDesignerState() != 5 && house.getDesignerState() != 2) {
//                if (house.getDesignerOk() != 0 && house.getDesignerOk() != 4 && house.getVisitState() == 1) {
//                    designDTO.setHistoryRecord(0);
//                    String webAddress = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class);
//                    designDTO.addButton(Utils.getButton("申请提前结束", webAddress + "ownerEnd?title=填写原因&houseId=" + houseId, 0));
//                    return ServerResponse.createBySuccess("设计师还在设计中", designDTO);
//                } else {
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), "设计师还在设计中");
//                }
            }
            Example example = new Example(PayConfiguration.class);
            Example.Criteria criteria = example.createCriteria()
                    .andEqualTo(PayConfiguration.DATA_STATUS, 0);
            String message;
            if(house.getVisitState() != 3) {
                designDTO.addButton(Utils.getButton("需要修改设计", 1));
                if (house.getDesignerState() == 5) {
                    ServerResponse serverResponse = getPlaneMap(houseId);
                    if (!serverResponse.isSuccess()) {
                        return serverResponse;
                    }
                    criteria.andEqualTo(PayConfiguration.TYPE, 1);
                    message = "温馨提示:您需要修改平面图次数超过%s次后将需要支付改图费,金额为%s元/次的费用。";
                    QuantityRoomDTO quantityRoomDTO = (QuantityRoomDTO) serverResponse.getResultObj();
                    designDTO.setData(quantityRoomDTO.getImages());
                    designDTO.addButton(Utils.getButton("确认平面图", 2));
                } else {
                    ServerResponse serverResponse = getConstructionPlans(houseId);
                    if (!serverResponse.isSuccess()) {
                        return serverResponse;
                    }
                    criteria.andEqualTo(PayConfiguration.TYPE, 2);
                    message = "温馨提示:您需要修改施工图次数超过%s次后将需要支付改图费,金额为%s元/次的费用。";
                    QuantityRoomDTO quantityRoomDTO = (QuantityRoomDTO) serverResponse.getResultObj();
                    designDTO.setData(quantityRoomDTO.getImages());
                    designDTO.addButton(Utils.getButton("确认施工图", 2));
                }
                List<PayConfiguration> payConfigurations = payConfigurationMapper.selectByExample(example);
                if (payConfigurations != null && payConfigurations.size() > 0) {
                    PayConfiguration payConfiguration = payConfigurations.get(0);
                    designDTO.setMessage(String.format(message,
                            payConfiguration.getFrequency() + "",
                            payConfiguration.getSumMoney().setScale(2, BigDecimal.ROUND_HALF_UP) + ""));
                }
            }
            designDTO.setHistoryRecord(0);
        } else {
            List<QuantityRoomImages> quantityRoomImages = new ArrayList<>();
            ServerResponse serverResponse = getConstructionPlans(houseId);
            if (serverResponse.isSuccess()) {
                QuantityRoomDTO quantityRoomDTO = (QuantityRoomDTO) serverResponse.getResultObj();
                quantityRoomImages.addAll(quantityRoomDTO.getImages());
            }
            if (quantityRoomImages.size() <= 0) {
                serverResponse = getPlaneMap(houseId);
                if (serverResponse.isSuccess()) {
                    QuantityRoomDTO quantityRoomDTO = (QuantityRoomDTO) serverResponse.getResultObj();
                    quantityRoomImages.addAll(quantityRoomDTO.getImages());
                }
            }
            if (quantityRoomImages.size() <= 0) {
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), "无相关记录");
            }
            designDTO.setData(quantityRoomImages);
            int historyRecord = (worker != null
                    && worker.getWorkerType() != null
                    && ((house.getDecorationType() == 2 && worker.getWorkerType() == 2)
                    || (house.getDecorationType() != 2 && worker.getWorkerType() == 1))) ? 1 : 0;
            designDTO.setHistoryRecord(historyRecord);
            if (house.getVisitState() != 3 && worker != null && worker.getId().equals(house.getMemberId())) {
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
                    if (order.getOperationState() == 1) {
                        designDTO.addButton(Utils.getButton("需要修改设计", 4));
                        designDTO.addButton(Utils.getButton("确认设计", 5));
                    }
                } else {
                    designDTO.addButton(Utils.getButton("申请额外修改设计", 3));
                }
            }
        }
        return ServerResponse.createBySuccess("查询成功", designDTO);
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
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), "无相关记录");
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
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), "无相关信息");
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
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), "无相关图片");
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
    public ServerResponse getDesignList(HttpServletRequest request, PageDTO pageDTO, int designerType,
                                        String searchKey,String workerKey,String userId) {
        String userID = request.getParameter(Constants.USERID);

        int  flag= 0;
        if(!CommonUtil.isEmpty(userId)){
            Member member = memberMapper.selectByPrimaryKey(userId);
            if(member != null){
                 //设计师
                 flag= member.getWorkerType();
            }
        }

        String cityKey = request.getParameter(Constants.CITY_ID);
        if (CommonUtil.isEmpty(cityKey)) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        String dataStatus = "0";//正常数据
        if (designerType < 0) {
            //当类型小于0时，则查询移除的数据
            dataStatus = "1";
        }
        List<DesignDTO> designDTOList = houseMapper.getDesignList(designerType, cityKey, searchKey,workerKey, dataStatus,flag,userId);
        PageInfo pageResult = new PageInfo(designDTOList);
        for (DesignDTO designDTO : designDTOList) {
            HouseWorker houseWorker = houseWorkerMapper.getByWorkerTypeId(designDTO.getHouseId(), "1", 6);
            designDTO.setHouseWorkerId(houseWorker==null?"":houseWorker.getId());
            //查询销售名称跟手机号码
            UserInfoDateDTO userInfoDTO =houseMapper.getUserList(designDTO.getMemberId());
            if(userInfoDTO != null){
                designDTO.setUserMobile(userInfoDTO.getUserMobile());
                designDTO.setUsername(userInfoDTO.getUsername());
            }

            //查询备注信息 取最新一条展示
            Example example1 = new Example(HouseRemark.class);
            example1.createCriteria().andEqualTo(HouseRemark.REMARK_TYPE, 0)
                    .andEqualTo(HouseRemark.HOUSE_ID, designDTO.getHouseId());
            example1.orderBy(HouseRemark.CREATE_DATE).desc();
            List<HouseRemark> storeList = houseRemarkMapper.selectByExample(example1);
            if(storeList.size() > 0){
                designDTO.setRemarkInfo(storeList.get(0).getRemarkInfo());
                designDTO.setRemarkDate(storeList.get(0).getCreateDate());
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
            designDTO.setShowUpdata(0);
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
                        Example example = new Example(DesignBusinessOrder.class);
                        Example.Criteria criteria = example.createCriteria()
                                .andEqualTo(DesignBusinessOrder.DATA_STATUS, 0)
                                .andEqualTo(DesignBusinessOrder.HOUSE_ID, designDTO.getHouseId())
                                .andEqualTo(DesignBusinessOrder.STATUS, 1)
                                .andNotEqualTo(DesignBusinessOrder.OPERATION_STATE, 2);
                        criteria.andEqualTo(DesignBusinessOrder.TYPE, 4);
                        List<DesignBusinessOrder> designBusinessOrders = designBusinessOrderMapper.selectByExample(example);
                        if (designBusinessOrders != null && designBusinessOrders.size() > 0) {
                            DesignBusinessOrder order = designBusinessOrders.get(0);
                            if (order.getOperationState() == 0) {
                                designDTO.setSchedule("待上传设计图");
                                designDTO.setShowUpdata(1);
                            } else {
                                designDTO.setSchedule("待审核设计图");
                            }
                        }
                        break;
                }
            }
        }
        pageResult.setList(designDTOList);
        return ServerResponse.createBySuccess("查询成功", pageResult);
    }

    public ServerResponse getHouseStatistics(String cityId,String workerTypeId,PageDTO pageDTO,String startDate, String endDate) {
        if (!CommonUtil.isEmpty(startDate) && !CommonUtil.isEmpty(endDate)) {
            startDate = startDate + " " + "00:00:00";
            endDate = endDate + " " + "23:59:59";
        }
        //"抢单数","业主支付数","已上传精算数","确认精算数","进入施工数","提前结束数"
        String[] fieldBudgetNames=new String[]{"grabOrders", "payment", "uploadActuarial", "confirmActuarial", "construction", "end"};
        //"抢单数","业主支付数","量房数","已上传平面图数","确认平面图数","已上传施工图数","确认施工图数","进入精算数","提前结束数"
        String[] fieldDesignNames=new String[]{
                "grabOrders", "payment", "measuringRoom", "uploadPlan", "confirmPlan", "uploadConstruction", "confirmConstruction", "sctuarialFigure", "end"};
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<Member> memberList = memberMapper.artisanList(cityId, null, workerTypeId, null, "2");
        List<Map> memberMapList =new ArrayList<>();
        PageInfo pageResult = new PageInfo(memberList);
        for (Member member : memberList) {
            Map map= BeanUtils.beanToMap(member);
            //设计统计字段
            if(!CommonUtil.isEmpty(workerTypeId)&&"1".equals(workerTypeId)){
                for (int i = 0; i < fieldDesignNames.length; i++) {
                    map.put(fieldDesignNames[i], memberMapper.getDesignStatisticsNum(member.getId(),startDate,endDate,(i+1)));
                }
            }
            //精算统计字段
            if(!CommonUtil.isEmpty(workerTypeId)&&"2".equals(workerTypeId)){
                for (int i = 0; i < fieldBudgetNames.length; i++) {
                    map.put(fieldBudgetNames[i], memberMapper.getBudgetStatisticsNum(member.getId(),startDate,endDate,(i+1)));
                }
            }
            memberMapList.add(map);
        }
        pageResult.setList(memberMapList);
        return ServerResponse.createBySuccess("获取成功", pageResult);
    }

    /**
     * 新增房子备注信息
     * @param houseRemark
     * @return
     */
    public ServerResponse addHouseRemark(HouseRemark houseRemark,String userId){
        try {

            if(!CommonUtil.isEmpty(userId)){
                if(!CommonUtil.isEmpty(houseRemark.getClient())){
                    //查询中台操作人
                    MainUser mainUser = userMapper.selectByPrimaryKey(userId);
                    if(mainUser != null){
                        houseRemark.setRemarkName(mainUser.getUsername());
                    }
                }else{
                    //查询app操作人
                    Member member = memberMapper.selectByPrimaryKey(userId);
                    if(member != null){
                        houseRemark.setRemarkName(member.getNickName());
                    }
                }
            }

            if (!CommonUtil.isEmpty(houseRemark)) {
                houseRemarkMapper.insert(houseRemark);
                return ServerResponse.createBySuccessMessage("新增成功");
            }
            return ServerResponse.createByErrorMessage("新增失败");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("新增失败");
        }
    }

    /**
     * 查询房子备注信息
     * @param remarkType
     * @param houseId
     * @return
     */
    public ServerResponse queryHouseRemark(PageDTO pageDTO,String remarkType, String houseId){
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        Example example = new Example(HouseRemark.class);
        example.createCriteria().andEqualTo(HouseRemark.REMARK_TYPE, remarkType)
                .andEqualTo(HouseRemark.HOUSE_ID, houseId);
        example.orderBy(HouseRemark.CREATE_DATE).desc();
        List<HouseRemark> storeList = houseRemarkMapper.selectByExample(example);
        if (storeList.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        return ServerResponse.createBySuccess("查询成功", storeList);
    }


    /**
     * 一次获取所有设计阶段历史记录
     * @param houseId houseId
     * @return ServerResponse
     */
    public ServerResponse getArrOdlQuantityRoomList(String houseId) {
        QuantityInfoDTO quantityInfoDTO = new QuantityInfoDTO();
        // type 事务类型：0:量房，1平面图，2施工图
        //查询业主名称，手机号码
        UserInfoDateDTO storeList = houseMapper.getDesignListInfo(houseId);
        //房子信息list
        quantityInfoDTO.setRowList(storeList);

        if(storeList != null){
            String str = storeList.getResidential() + storeList.getBuilding() + "栋" +
                    storeList.getUnit() + "单元" + storeList.getNumber() + "号";
            quantityInfoDTO.setArrHouseName(str);
            quantityInfoDTO.setName(storeList.getName());
            quantityInfoDTO.setMobile(storeList.getMobile());

            //查询销售名称跟手机号码
            UserInfoDateDTO userInfoDTO =houseMapper.getUserList(storeList.getMemberId());
            if(userInfoDTO != null){
                quantityInfoDTO.setUserMobile(userInfoDTO.getMobile());
                quantityInfoDTO.setUsername(userInfoDTO.getUsername());
            }
        }

        //查询抢单信息
        Example example = new Example(HouseWorker.class);
        example.createCriteria().andEqualTo(HouseWorker.HOUSE_ID, houseId)
                .andEqualTo(HouseWorker.WORKER_TYPE_ID, 1);
        example.orderBy(HouseWorker.CREATE_DATE).desc();
        //查询抢单时间
        List<HouseWorker> houseWorkers = houseWorkerMapper.selectByExample(example);

        List<Map<String,Object>> Lists = new ArrayList<>();

        Map<String,Object> map = new HashMap();
        if(houseWorkers.size() > 0){
            map.put("createDate",houseWorkers.get(0).getCreateDate());
            map.put("type","抢单");
            map.put("name",storeList.getName());
            Lists.add(map);
        }

        //查询支付时间
        example = new Example(HouseFlow.class);
        example.createCriteria().andEqualTo(HouseFlow.HOUSE_ID, houseId)
                .andEqualTo(HouseFlow.WORKER_TYPE_ID, 1);
        List<HouseFlow> houseFlows = houseFlowMapper.selectByExample(example);
        Date dd =null;
        if(houseFlows.size() > 0 ){
            map = new HashMap();
            dd = houseMapper.getModifyDate(houseFlows.get(0).getId());
            map.put("createDate",dd);
            map.put("type","支付");
            map.put("name",storeList.getOperatorName());
            Lists.add(map);
        }
        quantityInfoDTO.setListFour(Lists);


        //获取量房信息
        PageInfo oneList = getInfo(houseId,0);
        //获取平面图信息
        PageInfo twoList = getInfo(houseId,1);
        //获取施工图信息
        PageInfo threeList = getInfo(houseId,2);
        quantityInfoDTO.setTypeOneList(oneList);
        quantityInfoDTO.setTypeTwoList(twoList);
        quantityInfoDTO.setTypeThreeList(threeList);


        quantityInfoDTO.setNumberType(0);
        if(oneList.getSize()>0){
            quantityInfoDTO.setNumberType(2);
        }
        if(twoList.getSize()>0){
            quantityInfoDTO.setNumberType(3);
        }
        if(threeList.getSize() > 0){
            quantityInfoDTO.setNumberType(4);
        }

        List<QuantityRoomDTO> ddd = threeList.getList();
        for (QuantityRoomDTO aa:ddd) {
            if(aa.getFlag() == 0){
                quantityInfoDTO.setNumberType(5);
            }
        }

        return ServerResponse.createBySuccess("查询成功", quantityInfoDTO);
    }

    public PageInfo getInfo(String houseId, int type){
        List<QuantityRoomDTO> quantityRoomDTOS = quantityRoomMapper.getQuantityRoomList(houseId, type);
        if(type == 0){
            for (QuantityRoomDTO ddd:quantityRoomDTOS) {
                ddd.setUpOName("上传量房");
            }
        }else if(type == 1){
            for (QuantityRoomDTO ddd:quantityRoomDTOS) {
                if(ddd.getFlag() == 0){
                    ddd.setUpTName("审核通过");
                }else if(ddd.getFlag() == 1){
                    ddd.setUpTName("审核未通过");
                }else{
                    ddd.setUpTName("上传平面图");
                }
            }
        }else if(type == 2) {
            for (QuantityRoomDTO ddd : quantityRoomDTOS) {
                if (ddd.getFlag() == 0) {
                    ddd.setUpFName("审核通过");
                } else if (ddd.getFlag() == 1) {
                    ddd.setUpFName("审核未通过");
                } else {
                    ddd.setUpFName("上传施工图");
                }
            }
        }else if(type == 3){
            for (QuantityRoomDTO ddd : quantityRoomDTOS) {
                if (ddd.getFlag() == 0) {
                    ddd.setUpFaName("精算通过");
                } else if (ddd.getFlag() == 1) {
                    ddd.setUpFName("精算未通过");
                } else {
                    ddd.setUpFName("上传精算");
                }
            }
        }


        PageInfo pageResult = new PageInfo(quantityRoomDTOS);
        for (QuantityRoomDTO quantityRoomDTO : quantityRoomDTOS) {
            quantityRoomDTO.setUserType(-1);
            getUserName1(quantityRoomDTO);
        }

        pageResult.setList(quantityRoomDTOS);
        return pageResult;
    }

    private void getUserName1(QuantityRoomDTO quantityRoomDTO) {
        Member member = memberMapper.selectByPrimaryKey(quantityRoomDTO.getOwnerId());
        if (member != null) {
            quantityRoomDTO.setUserType(1);
            quantityRoomDTO.setMemberName(CommonUtil.isEmpty(member.getName()) ? member.getNickName() : member.getName());
        }

        MainUser mainUser = userMapper.selectByPrimaryKey(quantityRoomDTO.getUserId());
        if (mainUser != null) {
            quantityRoomDTO.setUserType(1);
            quantityRoomDTO.setUserName(mainUser.getUsername());
        }
    }

    /**
     * 一次获取所有 精算阶段 历史记录
     * @param houseId houseId
     * @return ServerResponse
     */
    public ServerResponse getArrCountList(String houseId) {
        QuantityInfoDTO quantityInfoDTO = new QuantityInfoDTO();
        //查询业主名称，手机号码
        UserInfoDateDTO storeList = houseMapper.getDesignListInfo(houseId);
        //房子信息list
        quantityInfoDTO.setRowList(storeList);

        if(storeList != null){
            String str = storeList.getResidential() + storeList.getBuilding() + "栋" +
                    storeList.getUnit() + "单元" + storeList.getNumber() + "号";
            quantityInfoDTO.setArrHouseName(str);
            quantityInfoDTO.setName(storeList.getName());
            quantityInfoDTO.setMobile(storeList.getMobile());

            //查询销售名称跟手机号码
            UserInfoDateDTO userInfoDTO =houseMapper.getUserList(storeList.getMemberId());
            if(userInfoDTO != null){
                quantityInfoDTO.setUserMobile(userInfoDTO.getMobile());
                quantityInfoDTO.setUsername(userInfoDTO.getUsername());
            }
        }

        Example example = new Example(HouseWorker.class);
        example.createCriteria().andEqualTo(HouseWorker.HOUSE_ID, houseId)
                .andEqualTo(HouseWorker.WORKER_TYPE_ID, 2);
        example.orderBy(HouseWorker.CREATE_DATE).desc();
        //查询精算抢单信息
        List<HouseWorker> houseWorkers = houseWorkerMapper.selectByExample(example);
        List<Map<String,Object>> Lists = new ArrayList<>();
        Map<String,Object> map = new HashMap();
        if(houseWorkers.size() > 0){
            map.put("createDate",houseWorkers.get(0).getCreateDate());
            map.put("type","抢单");
            map.put("name",storeList.getName());
            Lists.add(map);
        }

        //查询精算支付时间
        example = new Example(HouseFlow.class);
        example.createCriteria().andEqualTo(HouseFlow.HOUSE_ID, houseId)
                .andEqualTo(HouseFlow.WORKER_TYPE_ID, 2);
        List<HouseFlow> houseFlows = houseFlowMapper.selectByExample(example);
        Date dd =null;
        if(houseFlows.size() > 0 ){
            map = new HashMap();
            dd = houseMapper.getModifyDate(houseFlows.get(0).getId());
            map.put("createDate",dd);
            map.put("type","支付");
            map.put("name",storeList.getOperatorName());
            Lists.add(map);
        }

        quantityInfoDTO.setListFour(Lists);

        //获取精算信息
        PageInfo oneList = getJInfo(houseId,2);
        quantityInfoDTO.setJinList(oneList);

        quantityInfoDTO.setNumberType(0);
        if(Lists.size() > 0){
            quantityInfoDTO.setNumberType(2);
        }
        if(oneList.getSize()>0){
            quantityInfoDTO.setNumberType(3);
        }

        return ServerResponse.createBySuccess("查询成功", quantityInfoDTO);
    }

    public PageInfo getJInfo(String houseId, int type){
        List<QuantityRoomDTO> quantityRoomDTOS = quantityRoomMapper.getQuantityRoomList(houseId, type);
        if(type == 2){
            for (QuantityRoomDTO ddd : quantityRoomDTOS) {
                if (ddd.getFlag() == 0) {
                    ddd.setUpFaName("精算通过");
                } else if (ddd.getFlag() == 1) {
                    ddd.setUpFName("精算未通过");
                } else {
                    ddd.setUpFName("上传精算");
                }
            }
        }

        PageInfo pageResult = new PageInfo(quantityRoomDTOS);
        for (QuantityRoomDTO quantityRoomDTO : quantityRoomDTOS) {
            quantityRoomDTO.setUserType(-1);
            getUserName1(quantityRoomDTO);
        }
        pageResult.setList(quantityRoomDTOS);
        return pageResult;
    }


}
