package com.dangjia.acg.service.sale.client;

import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.enums.AppType;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.member.SaleMemberLabelDTO;
import com.dangjia.acg.dto.other.ClueDTO;
import com.dangjia.acg.dto.sale.client.CustomerIndexDTO;
import com.dangjia.acg.dto.sale.client.OrdersCustomerDTO;
import com.dangjia.acg.dto.sale.client.SaleClueDTO;
import com.dangjia.acg.dto.sale.residential.ResidentialRangeDTO;
import com.dangjia.acg.dto.sale.store.MonthlyTargetDTO;
import com.dangjia.acg.dto.sale.store.StoreUserDTO;
import com.dangjia.acg.mapper.clue.ClueMapper;
import com.dangjia.acg.mapper.clue.ClueTalkMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.house.IModelingVillageMapper;
import com.dangjia.acg.mapper.member.ICustomerMapper;
import com.dangjia.acg.mapper.member.IMemberLabelMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.sale.*;
import com.dangjia.acg.mapper.store.IStoreMapper;
import com.dangjia.acg.mapper.store.IStoreUserMapper;
import com.dangjia.acg.mapper.user.UserMapper;
import com.dangjia.acg.modle.clue.Clue;
import com.dangjia.acg.modle.home.IntentionHouse;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.house.ModelingVillage;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.member.Customer;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.sale.residential.ResidentialBuilding;
import com.dangjia.acg.modle.sale.residential.ResidentialRange;
import com.dangjia.acg.modle.sale.store.MonthlyTarget;
import com.dangjia.acg.modle.store.Store;
import com.dangjia.acg.modle.store.StoreUser;
import com.dangjia.acg.modle.user.MainUser;
import com.dangjia.acg.service.config.ConfigMessageService;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.dangjia.acg.service.sale.SaleService;
import com.dangjia.acg.util.Utils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/7/20
 * Time: 10:01
 */
@Service
public class ClientService {
    @Autowired
    private ClueMapper clueMapper;
    @Autowired
    private CraftsmanConstructionService constructionService;
    @Autowired
    private IMemberLabelMapper iMemberLabelMapper;
    @Autowired
    private ClueTalkMapper clueTalkMapper;
    @Autowired
    private MonthlyTargetMappper monthlyTargetMappper;
    @Autowired
    private ResidentialRangeMapper residentialRangeMapper;
    @Autowired
    private IModelingVillageMapper iModelingVillageMapper;
    @Autowired
    private ResidentialBuildingMapper residentialBuildingMapper;
    @Autowired
    private IStoreMapper iStoreMapper;
    @Autowired
    private IStoreUserMapper iStoreUserMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private SaleService saleService;
    @Autowired
    private ICustomerMapper iCustomerMapper;
    @Autowired
    private IMemberMapper iMemberMapper;
    @Autowired
    private IHouseMapper iHouseMapper;
    @Autowired
    private ConfigMessageService configMessageService;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IntentionHouseMapper intentionHouseMapper;
    @Autowired
    private DjAlreadyRobSingleMapper djAlreadyRobSingleMapper;

    /**
     * 录入客户
     *
     * @param clue
     * @param userToken
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse enterCustomer(Clue clue, String userToken) {
        Object object = constructionService.getAccessToken(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        AccessToken accessToken = (AccessToken) object;
        if (CommonUtil.isEmpty(accessToken.getUserId())) {
            return ServerResponse.createbyUserTokenError();
        }
        MainUser user = userMapper.getNameById(accessToken.getUserId());
        //线索阶段是否报备
        List<Clue> groupBys = clueMapper.getGroupBy(clue.getPhone(), null, null);
        for (Clue groupBy : groupBys) {
            if (null!=groupBy.getReportDate() && new Date().getTime()>groupBy.getReportDate().getTime()) {
                long time = groupBy.getReportDate().getTime()-new Date().getTime();
                return ServerResponse.createByErrorMessage(String.valueOf(time));
            }
        }
        //客户阶段是否报备
        List<Customer> customerGroupBy = iCustomerMapper.getCustomerGroupBy(clue.getPhone());
        for (Customer customer : customerGroupBy) {
            if (null!=customer.getReportDate() && new Date().getTime()>customer.getReportDate().getTime()) {
                long time = customer.getReportDate().getTime()-new Date().getTime();
                return ServerResponse.createByErrorMessage(String.valueOf(time));
            }
        }
        //如果客户已录入过则把录入的房子变为意向房子
        Clue clue1 = clueMapper.getClue(clue.getPhone(), user.getId());
        if (null != clue1) {
            Example example = new Example(IntentionHouse.class);
            example.createCriteria().andEqualTo(IntentionHouse.RESIDENTIAL_NAME, clue.getAddress())
                    .andEqualTo(IntentionHouse.BUILDING_NAME, clue.getBuilding())
                    .andEqualTo(IntentionHouse.NUMBER_NAME, clue.getNumber())
                    .andEqualTo(IntentionHouse.CLUE_ID,clue1.getId());
            List<IntentionHouse> intentionHouses = intentionHouseMapper.selectByExample(example);
            if (intentionHouses.size() > 0) {
                return ServerResponse.createByErrorMessage("该线索已存在");
            } else if(!CommonUtil.isEmpty(clue.getBuilding())){
                IntentionHouse intentionHouse = new IntentionHouse();
                intentionHouse.setClueId(clue1.getId());
                intentionHouse.setBuildingName(clue.getBuilding());
                intentionHouse.setNumberName(clue.getNumber());
                intentionHouse.setResidentialName(clue.getAddress());
                intentionHouseMapper.insert(intentionHouse);
                return ServerResponse.createByErrorMessage("该线索已存在录入为该线索的意向房子");
            }
        }
        Example example = new Example(Member.class);
        example.createCriteria().andEqualTo(Member.MOBILE, clue.getPhone());
        List<Member> members = iMemberMapper.selectByExample(example);
        example = new Example(StoreUser.class);
        example.createCriteria().andEqualTo(StoreUser.USER_ID, accessToken.getUserId())
                .andEqualTo(Store.DATA_STATUS, 0);
        List<StoreUser> storeUsers = iStoreUserMapper.selectByExample(example);
        if (storeUsers.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        StoreUser storeUser = storeUsers.get(0);
        Store store = iStoreMapper.selectByPrimaryKey(storeUser.getStoreId());
        if (members.size() > 0) {//如果线索已注册
            Customer customer = new Customer();
            clue.setStage(1);
            clue.setDataStatus(0);
            clue.setStoreId(storeUser.getStoreId());
            clue.setCusService(user.getId());
            clue.setClueType(0);
            clue.setTurnStatus(0);
            clue.setTips("1");
            clue.setPhaseStatus(1);
            clue.setCityId(store.getCityId());
            if(!CommonUtil.isEmpty(clue.getBuilding())) {
                IntentionHouse intentionHouse = new IntentionHouse();
                intentionHouse.setClueId(clue.getId());
                intentionHouse.setBuildingName(clue.getBuilding());
                intentionHouse.setNumberName(clue.getNumber());
                intentionHouse.setResidentialName(clue.getAddress());
                intentionHouseMapper.insert(intentionHouse);
            }
            customer.setUserId(user.getId());
            customer.setMemberId(members.get(0).getId());
            customer.setStage(1);
            customer.setStoreId(storeUser.getStoreId());
            customer.setCityId(store.getCityId());
            customer.setTurnStatus(0);
            customer.setPhaseStatus(1);
            customer.setClueType(0);
            customer.setDataStatus(0);
            customer.setTips("1");
            customer.setPhaseStatus(1);
            clue.setMemberId(members.get(0).getId());
            clueMapper.insert(clue);//记录进入线索线索状态为转客户客户阶段
            iCustomerMapper.insert(customer);
            return ServerResponse.createBySuccessMessage("提交成功");
        } else {
            clue.setCusService(user.getId());
            clue.setStoreId(store.getId());
            clue.setStage(0);
            clue.setTips("1");
            clue.setPhaseStatus(0);
            if (clueMapper.insert(clue) > 0) {
                IntentionHouse intentionHouse = new IntentionHouse();
                intentionHouse.setClueId(clue.getId());
                intentionHouse.setBuildingName(clue.getBuilding());
                intentionHouse.setNumberName(clue.getNumber());
                intentionHouse.setResidentialName(clue.getAddress());
                intentionHouseMapper.insert(intentionHouse);
                return ServerResponse.createBySuccessMessage("提交成功");
            } else {
                return ServerResponse.createByErrorMessage("提交失败");
            }
        }
    }


    /**
     * 跨域下单
     *
     * @param clue
     * @param userToken
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse crossDomainOrder(Clue clue, String userToken, String villageId ,String buildingId,String cityId) {
        Object object = constructionService.getAccessToken(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        AccessToken accessToken = (AccessToken) object;
        if (CommonUtil.isEmpty(accessToken.getUserId())) {
            return ServerResponse.createbyUserTokenError();
        }
        ModelingVillage modelingVillage = iModelingVillageMapper.selectByPrimaryKey(villageId);
        if (modelingVillage == null) {
            clue.setClueType(1);
            clue.setTurnStatus(0);
            clue.setPhaseStatus(0);
            clue.setCityId(cityId);
            clueMapper.insert(clue);//记录为中台的线索
            return ServerResponse.createBySuccessMessage("记录为中台的线索");
        }
        ResidentialBuilding residentialBuilding = residentialBuildingMapper.selectByPrimaryKey(buildingId);
        ResidentialRange residentialRange = residentialRangeMapper.selectSingleResidentialRange(residentialBuilding.getId());
        clue.setStage(0);
        clue.setDataStatus(0);
        clue.setStoreId(residentialBuilding.getStoreId());
        clue.setClueType(0);
        clue.setTurnStatus(0);
        clue.setCityId(modelingVillage.getCityId());
        clue.setClueType(1);
        clue.setPhaseStatus(0);
        clue.setCusService(residentialRange.getUserId());
        clueMapper.insert(clue);
        return ServerResponse.createBySuccessMessage("提交成功");
//        Example example = new Example(Store.class);
//        example.createCriteria().andEqualTo(Store.CITY_ID, modelingVillage.getCityId());
//        List<Store> stores = iStoreMapper.selectByExample(example);
//        String url = configUtil.getValue(SysConfig.PUBLIC_SALE_APP_ADDRESS, String.class);
//        for (Store store : stores) {
//            if (GaoDeUtils.isInPolygon(modelingVillage.getLocationx() + "," + modelingVillage.getLocationy(), store.getScopeItude())) {
//                List<Clue> groupBys = clueMapper.getGroupBy(clue.getPhone(), null, store.getId());
//                //如果转出的该客户为自己门店范围内未录入的（野生），则记录进店长的线索阶段（给店长系统推送），然后仅分配给内场
//                if (groupBys.size() <= 0) {
//                    clue.setStage(0);
//                    clue.setDataStatus(0);
//                    clue.setStoreId(store.getId());
//                    clue.setClueType(0);
//                    clue.setTurnStatus(0);
//                    clue.setCityId(store.getCityId());
//                    clue.setClueType(1);
//                    clue.setPhaseStatus(0);
//                    clueMapper.insert(clue);
//                    MainUser u = userMapper.selectByPrimaryKey(store.getUserId());
//                    if (u != null && !CommonUtil.isEmpty(u.getMemberId()))
//                        configMessageService.addConfigMessage(AppType.SALE, u.getMemberId(), "跨域客户提醒",
//                                "您收到了一个跨域客户，快去分配给内场销售吧。", 0, url
//                                        + Utils.getCustomerDetails(u.getMemberId(), clue.getId(), 0, "0"));
//                    return ServerResponse.createBySuccessMessage("提交成功");
//                }
//                ResidentialBuilding residentialBuilding = residentialBuildingMapper.selectSingleResidentialBuilding(store.getId(), clue.getBuilding(), villageId);
//                if (null != residentialBuilding) {
//                    ResidentialRange residentialRange = residentialRangeMapper.selectSingleResidentialRange(residentialBuilding.getId());
//                    if (null != residentialRange) {
//                        clue.setCusService(residentialRange.getUserId());
//                    }
//                }
//                clue.setStage(0);
//                clue.setDataStatus(0);
//                clue.setStoreId(store.getId());
//                clue.setClueType(0);
//                clue.setTurnStatus(0);
//                clue.setCityId(store.getCityId());
//                clue.setClueType(1);
//                clue.setPhaseStatus(0);
//                clueMapper.insert(clue);
//                MainUser u = userMapper.selectByPrimaryKey(clue.getCusService());
//                if (u != null && !CommonUtil.isEmpty(u.getMemberId()))
//                    configMessageService.addConfigMessage(AppType.SALE, u.getMemberId(), "跨域客户提醒",
//                            "您收到了一个跨域客户。", 0, url
//                                    + Utils.getCustomerDetails(u.getMemberId(), clue.getId(), 0, "0"));
//                return ServerResponse.createBySuccessMessage("提交成功");
//            }
//        }
//        example = new Example(Member.class);
//        example.createCriteria().andEqualTo(Member.MOBILE, clue.getPhone());
//        List<Member> members = iMemberMapper.selectByExample(example);
//        List<Customer> customerGroupBy = iCustomerMapper.getCustomerGroupBy(clue.getPhone());
//        if (members.size() > 0) {//判断该线索是否注册已经经是客户阶段
//            Customer customer = iCustomerMapper.selectByPrimaryKey(customerGroupBy.get(0).getId());
//            if (customer != null) {
//                customer.setMemberId(members.get(0).getId());
//                customer.setStage(1);
//                customer.setCityId(modelingVillage.getCityId());
//                customer.setTurnStatus(0);
//                customer.setPhaseStatus(1);
//                customer.setClueType(1);
//                customer.setDataStatus(0);
//                customer.setModifyDate(new Date());
//                iCustomerMapper.updateByPrimaryKeySelective(customer);
//                MainUser u = userMapper.selectByPrimaryKey(customer.getUserId());
//                if (u != null && !CommonUtil.isEmpty(u.getMemberId()))
//                    configMessageService.addConfigMessage(AppType.SALE, u.getMemberId(), "跨域客户提醒",
//                            "您收到了一个跨域客户，快去分配给内场销售吧。", 0, url
//                                    + Utils.getCustomerDetails(u.getMemberId(), "", 0, "1"));
//            }
//            return ServerResponse.createBySuccessMessage("提交成功");
//        }
//        clue.setTurnStatus(0);
//        clue.setPhaseStatus(0);
//        clue.setCityId(modelingVillage.getCityId());
//        clueMapper.insert(clue);//记录为中台的线索
//        return ServerResponse.createBySuccessMessage("提交成功");
    }

    /**
     * 编辑客户
     *
     * @param clue
     * @return
     */
    public ServerResponse updateCustomer(Clue clue) {
        if (clueMapper.updateByPrimaryKeySelective(clue) > 0) {
            return ServerResponse.createBySuccessMessage("提交成功");
        }
        return ServerResponse.createByErrorMessage("提交失败");
    }


    /**
     * 报备
     *
     * @param clueId
     * @param phaseStatus 0:线索阶段 1:客户阶段
     * @param mcId
     * @return
     */
    public ServerResponse setReported(String clueId, Integer phaseStatus, String mcId) {
        if (phaseStatus == 0) {
            Clue clue1 = clueMapper.selectByPrimaryKey(clueId);
            if (null != clue1.getReportDate()) {
                return ServerResponse.createByErrorMessage("已报备不能再次报备");
            }
            Clue clue = new Clue();
            clue.setId(clueId);
            clue.setReportDate(new Date());
            clueMapper.updateByPrimaryKeySelective(clue);
        } else if (phaseStatus == 1) {
            Customer customer1 = iCustomerMapper.selectByPrimaryKey(mcId);
            if (null != customer1.getReportDate()) {
                return ServerResponse.createByErrorMessage("已报备不能再次报备");
            }
            Customer customer = new Customer();
            customer.setId(mcId);
            customer.setReportDate(new Date());
            iCustomerMapper.updateByPrimaryKeySelective(customer);
        }
        return ServerResponse.createBySuccessMessage("报备成功");
    }


    /**
     * 放弃跟进
     *
     * @param clueId
     * @param phaseStatus 0:线索阶段 1:客户阶段
     * @param mcId
     * @return
     */
    public ServerResponse setFollow(String clueId, Integer phaseStatus, String mcId) {
        String url = configUtil.getValue(SysConfig.PUBLIC_SALE_APP_ADDRESS, String.class);
        MainUser user;
        String memberId = "";
        if (phaseStatus == 0) {
            Clue clue = clueMapper.selectByPrimaryKey(clueId);
            if (clue == null) {
                return ServerResponse.createByErrorMessage("找不到此线索");
            }
            clue.setModifyDate(new Date());
            user = userMapper.selectByPrimaryKey(clue.getCusService());
        } else {
            Customer customer = iCustomerMapper.selectByPrimaryKey(mcId);
            if (customer == null) {
                return ServerResponse.createByErrorMessage("找不到此客户");
            }
            user = userMapper.selectByPrimaryKey(customer.getUserId());
        }
        clueMapper.setFollow(clueId,phaseStatus,mcId);
        if (user != null) {
            Example example = new Example(StoreUser.class);
            example.createCriteria().andEqualTo(StoreUser.USER_ID, user.getId())
                    .andEqualTo(StoreUser.DATA_STATUS, 0);
            List<StoreUser> storeUserList = iStoreUserMapper.selectByExample(example);
            if (storeUserList.size() > 0) {
                Store store = iStoreMapper.selectByPrimaryKey(storeUserList.get(0).getStoreId());
                if (store != null) {
                    MainUser userStore = userMapper.selectByPrimaryKey(store.getUserId());
                    if (userStore != null && !CommonUtil.isEmpty(userStore.getMemberId()))
                        configMessageService.addConfigMessage(AppType.SALE, userStore.getMemberId(), "沉睡客户",
                                "收到了【" + user.getUsername() + "】放弃跟进的客户，快去分配给员工吧。", 0, url
                                        + Utils.getCustomerDetails(memberId, clueId, phaseStatus, "2"));
                }
            }
        }
        return ServerResponse.createBySuccessMessage("操作成功");
    }


    /**
     * 客户页
     *
     * @param userToken
     * @return
     */
    public ServerResponse clientPage(String userToken) {
        Object object = constructionService.getAccessToken(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        AccessToken accessToken = (AccessToken) object;
        if (CommonUtil.isEmpty(accessToken.getUserId())) {
            return ServerResponse.createbyUserTokenError();
        }
        MainUser user = userMapper.getNameById(accessToken.getUserId());
        Map<String, Object> map = new HashedMap();
        Example example = new Example(Store.class);
        example.createCriteria().andEqualTo(Store.USER_ID, user.getId());
        if (iStoreMapper.selectByExample(example).size() <= 0) {//判断用户是否为店长
            CustomerIndexDTO customerIndexDTO = clueMapper.clientPage(0, user.getId(), null);
            if (null != customerIndexDTO) {
                customerIndexDTO.setTips(clueMapper.getTips(0, user.getId(), null) > 0 ? 1 : 0);
            }
            map.put("followList", customerIndexDTO);

            customerIndexDTO = clueMapper.clientPage(1, user.getId(), null);
            if (null != customerIndexDTO) {
                customerIndexDTO.setTips(clueMapper.getTips(1, user.getId(), null) > 0 ? 1 : 0);
            }
            map.put("placeOrder", customerIndexDTO);

            customerIndexDTO = clueMapper.clientPage(2, user.getId(), null);
            if (null != customerIndexDTO) {
                customerIndexDTO.setTips(clueMapper.getTips(2, user.getId(), null) > 0 ? 1 : 0);
            }
            map.put("completion", customerIndexDTO);
            MonthlyTargetDTO monthlyTargetDTO = new MonthlyTargetDTO();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
            String date = dateFormat.format(new Date());
            monthlyTargetDTO.setModifyDate(date);
            monthlyTargetDTO.setComplete(clueMapper.Complete(user.getId(), date));
            List<MonthlyTarget> monthlyTargets = getMonthlyTargetList(user.getId());
            monthlyTargetDTO.setTargetNumber(monthlyTargets.size() > 0 ? monthlyTargets.get(0).getTargetNumber() : 0);
            map.put("monthlyTarget", monthlyTargetDTO);
            map.put("outField", getResidentialRangeDTOList(user.getId()));
            example.createCriteria().andEqualTo(StoreUser.USER_ID, accessToken.getUserId())
                    .andEqualTo(Store.DATA_STATUS, 0);
            List<StoreUser> storeUsers = iStoreUserMapper.selectByExample(example);
            if (storeUsers.size() <= 0) {
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            }
            StoreUser storeUser = storeUsers.get(0);
            map.put("storeId", storeUser.getStoreId());
        } else {
            object = saleService.getStore(accessToken.getUserId());
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Store store = (Store) object;
            List<StoreUserDTO> storeUsers = iStoreUserMapper.getStoreUsers(store.getId(), null, null);
            CustomerIndexDTO customerIndexDTO = clueMapper.clientPage(0, null, storeUsers);
            if (null != customerIndexDTO) {
                customerIndexDTO.setTips(clueMapper.getTips(0, user.getId(), null) > 0 ? 1 : 0);
            }
            map.put("followList", customerIndexDTO);

            customerIndexDTO = clueMapper.clientPage(1, null, storeUsers);
            if (null != customerIndexDTO) {
                customerIndexDTO.setTips(clueMapper.getTips(1, user.getId(), null) > 0 ? 1 : 0);
            }
            map.put("placeOrder", customerIndexDTO);

            customerIndexDTO = clueMapper.clientPage(2, null, storeUsers);
            if (null != customerIndexDTO) {
                customerIndexDTO.setTips(clueMapper.getTips(2, user.getId(), null) > 0 ? 1 : 0);
            }
            map.put("completion", customerIndexDTO);

            List<CustomerIndexDTO> customerIndexDTOS = clueMapper.sleepingCustomer(store.getId(), null, "desc", null);
            if (customerIndexDTOS.size() > 0) {
                customerIndexDTOS.get(0).setTips(clueMapper.getSleepingCustomerTips() > 0 ? 1 : 0);
                map.put("sleepingCustomer", customerIndexDTOS.get(0));
            }
            List<CustomerIndexDTO> customerIndexDTOS1 = iCustomerMapper.waitDistribution(user.getId(), null, "desc");
            if (customerIndexDTOS1.size() > 0) {
                customerIndexDTOS1.get(0).setTips(iCustomerMapper.getwaitDistributionTips() > 0 ? 1 : 0);
                map.put("waitDistribution", customerIndexDTOS1.get(0));
            }
            map.put("storeId", store.getId());
            map.put("grabSheet", iCustomerMapper.grabSheet(store.getId()));
        }
        return ServerResponse.createBySuccess("查询成功", map);
    }


    /**
     * 获取当前月份的目标
     *
     * @param userId
     * @return
     */
    public List<MonthlyTarget> getMonthlyTargetList(String userId) {
        Example example = new Example(MonthlyTarget.class);
        example.createCriteria()
                .andEqualTo(MonthlyTarget.USER_ID, userId)
                .andBetween(MonthlyTarget.TARGET_DATE, DateUtil.getTimesMonthmorning(), DateUtil.getTimesMonthnight());
        return monthlyTargetMappper.selectByExample(example);
    }


    /**
     * 获取销售范围
     *
     * @param userId
     * @return
     */
    public List<ResidentialRangeDTO> getResidentialRangeDTOList(String userId) {
        Example example = new Example(ResidentialRange.class);
        example.createCriteria().andEqualTo(ResidentialRange.USER_ID, userId);
        List<ResidentialRange> residentialRanges = residentialRangeMapper.selectByExample(example);
        List<ResidentialRangeDTO> residentialRangeDTOList = new ArrayList<>();
        String[] buildingId = {};
        for (ResidentialRange residentialRange : residentialRanges) {
            if (!CommonUtil.isEmpty(residentialRange.getBuildingId())) {
                buildingId = residentialRange.getBuildingId().split(",");
            }
        }
        if (buildingId.length > 0) {
            example = new Example(ResidentialBuilding.class);
            example.createCriteria().andIn(ResidentialBuilding.ID, Arrays.asList(buildingId));
            List<ResidentialBuilding> residentialBuildings = residentialBuildingMapper.selectByExample(example);
            List<ResidentialBuilding> getvillageIdGroupBy = residentialBuildingMapper.getvillageIdGroupBy(buildingId);
            for (ResidentialBuilding residentialBuilding : getvillageIdGroupBy) {
                ResidentialRangeDTO residentialRangeDTO = new ResidentialRangeDTO();
                ModelingVillage modelingVillage = iModelingVillageMapper.selectByPrimaryKey(residentialBuilding.getVillageId());
                residentialRangeDTO.setVillageId(modelingVillage.getId());
                residentialRangeDTO.setVillagename(modelingVillage.getName());
                for (ResidentialBuilding residentialBuilding1 : residentialBuildings) {
                    if(residentialBuilding1.getVillageId().equals(residentialRangeDTO.getVillageId())) {
                        residentialRangeDTO.getList().add(residentialBuilding1);
                    }
                }
                residentialRangeDTOList.add(residentialRangeDTO);
            }
        }
        return residentialRangeDTOList;
    }

    /**
     * 跟进列表
     *
     * @param userToken
     * @param label
     * @param pageDTO
     * @param time
     * @param stage
     * @return
     */
    public ServerResponse followList(String userToken, PageDTO pageDTO, String label, String time, Integer stage, String searchKey, String userId) {
        try {
            Object object = constructionService.getAccessToken(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            AccessToken accessToken = (AccessToken) object;
            if (CommonUtil.isEmpty(accessToken.getUserId())) {
                return ServerResponse.createbyUserTokenError();
            }
            MainUser user = userMapper.getNameById(accessToken.getUserId());
            Example example = new Example(Store.class);
            example.createCriteria().andEqualTo(Store.USER_ID, user.getId());
            List<ClueDTO> clueDTOS;
            if (iStoreMapper.selectByExample(example).size() <= 0) {//判断用户是否为店长
                PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
                clueDTOS = clueMapper.followList(label, time, stage, searchKey, user.getId(), null);
            } else {
                object = saleService.getStore(accessToken.getUserId());
                if (object instanceof ServerResponse) {
                    return (ServerResponse) object;
                }
                Store store = (Store) object;
                PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
                clueDTOS = clueMapper.followList(label, time, stage, searchKey, userId, store.getId());
            }
            List<SaleClueDTO> list = new ArrayList<>();
            for (ClueDTO clueDTO : clueDTOS) {
                SaleClueDTO saleClueDTO = new SaleClueDTO();
                if (!CommonUtil.isEmpty(clueDTO.getLabelId())) {
                    String[] labelIds = clueDTO.getLabelId().split(",");
                    List<SaleMemberLabelDTO> labelByIds = iMemberLabelMapper.getLabelByIds(labelIds);
                    saleClueDTO.setList(labelByIds);
                }
                saleClueDTO.setStage(clueDTO.getStage());
                saleClueDTO.setMemberId(clueDTO.getMemberId());
                saleClueDTO.setUserId(clueDTO.getUserId());
                saleClueDTO.setClueId(clueDTO.getClueId());
                saleClueDTO.setMcId(clueDTO.getMcId());
                saleClueDTO.setPhaseStatus(clueDTO.getPhaseStatus());
                saleClueDTO.setUsername(clueDTO.getUserName());
                saleClueDTO.setClueType(clueDTO.getClueType());
                saleClueDTO.setOwername(clueDTO.getOwername());
                saleClueDTO.setPhone(clueDTO.getPhone());
                saleClueDTO.setReportDate(clueDTO.getReportDate());
                saleClueDTO.setCreateDate(clueDTO.getCreateDate());
                saleClueDTO.setModifyDate(clueDTO.getModifyDate());//最新跟进时间
                saleClueDTO.setClueType(clueDTO.getClueType());
                saleClueDTO.setCommunicationDate(clueTalkMapper.getMaxDate(clueDTO.getClueId()));//沟通时间
                list.add(saleClueDTO);
            }
            PageInfo pageResult = new PageInfo(list);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 已下单竣工列表
     *
     * @param userToken
     * @param pageDTO
     * @param visitState
     * @param time
     * @param searchKey
     * @return
     */
    public ServerResponse ordersCustomer(String userToken, String visitState, PageDTO pageDTO, String searchKey, String time, Integer type, String userId) {
        Object object = constructionService.getAccessToken(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        AccessToken accessToken = (AccessToken) object;
        if (CommonUtil.isEmpty(accessToken.getUserId())) {
            return ServerResponse.createbyUserTokenError();
        }
        MainUser user = userMapper.getNameById(accessToken.getUserId());
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<OrdersCustomerDTO> ordersCustomerDTOS = new ArrayList<>();
        if (!CommonUtil.isEmpty(visitState)) {
            Example example = new Example(Store.class);
            example.createCriteria().andEqualTo(Store.USER_ID, user.getId());
            if (iStoreMapper.selectByExample(example).size() <= 0) {
                ordersCustomerDTOS = clueMapper.ordersCustomer(user.getId(), visitState, searchKey, time, null, null);
            } else {
                object = saleService.getStore(accessToken.getUserId());
                if (object instanceof ServerResponse) {
                    return (ServerResponse) object;
                }
                Store store = (Store) object;
                ordersCustomerDTOS = clueMapper.ordersCustomer(null, visitState, searchKey, time, store.getId(), userId);
            }
        } else {
            List<CustomerIndexDTO> customerIndexDTOS = new ArrayList<>();
            if (null != type && type == 1) {//待分配客户
                customerIndexDTOS = iCustomerMapper.waitDistribution(user.getId(), searchKey, time);
            }
            if (null != type && type == 2) {//沉睡客户
                object = saleService.getStore(accessToken.getUserId());
                if (object instanceof ServerResponse) {
                    return (ServerResponse) object;
                }
                Store store = (Store) object;
                customerIndexDTOS = clueMapper.sleepingCustomer(store.getId(), searchKey, time, userId);
            }
            for (CustomerIndexDTO customerIndexDTO : customerIndexDTOS) {
                OrdersCustomerDTO ordersCustomerDTO = new OrdersCustomerDTO();
                if (!CommonUtil.isEmpty(customerIndexDTO.getLabelIdArr())) {
                    String[] labelIds = customerIndexDTO.getLabelIdArr().split(",");
                    List<SaleMemberLabelDTO> labelByIds = iMemberLabelMapper.getLabelByIds(labelIds);
                    ordersCustomerDTO.setList(labelByIds);
                }
                ordersCustomerDTO.setUserId(customerIndexDTO.getUserId());
                ordersCustomerDTO.setPhaseStatus(customerIndexDTO.getPhaseStatus());
                ordersCustomerDTO.setClueId(customerIndexDTO.getClueId());
                ordersCustomerDTO.setMcId(customerIndexDTO.getMcId());
                ordersCustomerDTO.setMemberId(customerIndexDTO.getMemberId());
                ordersCustomerDTO.setMobile(customerIndexDTO.getPhone());
                ordersCustomerDTO.setName(customerIndexDTO.getName());
                ordersCustomerDTO.setCreateDate(customerIndexDTO.getCreateDate());
                ordersCustomerDTO.setModifyDate(customerIndexDTO.getModifyDate());
                ordersCustomerDTO.setUserName(customerIndexDTO.getUserName());
                ordersCustomerDTO.setWechat(customerIndexDTO.getWechat());
                ordersCustomerDTOS.add(ordersCustomerDTO);
            }
        }
        PageInfo pageResult = new PageInfo(ordersCustomerDTOS);
        return ServerResponse.createBySuccess("查询成功", pageResult);
    }


    /**
     * 中台转出
     *
     * @param cityId
     * @param storeId
     * @param id
     * @param phaseStatus
     * @return
     */
    public ServerResponse setTurnOut(String cityId, String storeId, String id, Integer phaseStatus) {
        Store store = iStoreMapper.selectByPrimaryKey(storeId);
        if (phaseStatus == 0) {
            Clue clue = new Clue();
            clue.setId(id);
            clue.setStoreId(storeId);
            clue.setCusService(store.getUserId());
            clue.setCityId(cityId);
            clue.setTurnStatus(1);
            clueMapper.updateByPrimaryKeySelective(clue);
        } else if (phaseStatus == 1) {
            Customer customer = new Customer();
            customer.setId(id);
            customer.setStoreId(storeId);
            customer.setUserId(store.getUserId());
            customer.setCityId(cityId);
            customer.setTurnStatus(1);
            iCustomerMapper.updateByPrimaryKeySelective(customer);
        }
        return ServerResponse.createBySuccessMessage("操作成功");
    }


    /**
     * 撤回
     *
     * @param mcId
     * @param houseId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse setWithdraw(String mcId, String houseId,String alreadyId) {
        Customer customer = iCustomerMapper.selectByPrimaryKey(mcId);
        House house = new House();
        house.setId(houseId);
        house.setDataStatus(1);
        Example example=new Example(Customer.class);
        example.createCriteria().andEqualTo(Customer.MEMBER_ID,customer.getMemberId());
        Customer customer1=new Customer();
        customer1.setId(null);
        customer1.setStage(1);
        Clue clue=new Clue();
        clue.setId(null);
        clue.setStage(1);
        example=new Example(Clue.class);
        example.createCriteria().andEqualTo(Clue.MEMBER_ID,customer.getMemberId());

        djAlreadyRobSingleMapper.deleteByPrimaryKey(alreadyId);
        if (iHouseMapper.updateByPrimaryKeySelective(house) > 0 && iCustomerMapper.updateByExampleSelective(customer1,example)>0 &&clueMapper.updateByExampleSelective(clue,example)>0) {
            return ServerResponse.createBySuccessMessage("撤回成功");
        } else {
            return ServerResponse.createByErrorMessage("撤回失败");
        }
    }


    /**
     * 红点提示
     *
     * @param mcId
     * @param clueId
     * @return
     */
    public ServerResponse setTips(String mcId, String clueId) {
        clueMapper.setTips(clueId,mcId);
        return ServerResponse.createBySuccessMessage("新消息已查看");
    }
}

