package com.dangjia.acg.service.sale.rob;

import com.dangjia.acg.auth.config.RedisSessionDAO;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.clue.ClueTalkDTO;
import com.dangjia.acg.dto.member.CustomerRecordInFoDTO;
import com.dangjia.acg.dto.member.IntentionHouseDTO;
import com.dangjia.acg.dto.member.SaleMemberLabelDTO;
import com.dangjia.acg.dto.member.WorkerTypeDTO;
import com.dangjia.acg.dto.sale.achievement.UserAchievementDTO;
import com.dangjia.acg.dto.sale.rob.*;
import com.dangjia.acg.dto.sale.store.GrabSheetDTO;
import com.dangjia.acg.dto.sale.store.OrderStoreDTO;
import com.dangjia.acg.mapper.clue.ClueMapper;
import com.dangjia.acg.mapper.clue.ClueTalkMapper;
import com.dangjia.acg.mapper.house.IHouseAddressMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.member.ICustomerMapper;
import com.dangjia.acg.mapper.member.IMemberLabelMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.sale.DjAlreadyRobSingleMapper;
import com.dangjia.acg.mapper.sale.DjOrderSurfaceMapper;
import com.dangjia.acg.mapper.sale.DjRobSingleMapper;
import com.dangjia.acg.mapper.sale.IntentionHouseMapper;
import com.dangjia.acg.mapper.store.IStoreMapper;
import com.dangjia.acg.mapper.store.IStoreUserMapper;
import com.dangjia.acg.modle.clue.Clue;
import com.dangjia.acg.modle.clue.ClueTalk;
import com.dangjia.acg.modle.home.IntentionHouse;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.house.HouseAddress;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.member.Customer;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.sale.royalty.DjAlreadyRobSingle;
import com.dangjia.acg.modle.sale.royalty.DjOrderSurface;
import com.dangjia.acg.modle.sale.royalty.DjRobSingle;
import com.dangjia.acg.modle.store.StoreUser;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.dangjia.acg.service.house.HouseService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 * 抢单模块
 * Created with IntelliJ IDEA.
 * author: ljl
 * Date: 2019/7/30
 * Time: 16:16
 */
@Service
public class RobService {
    @Autowired
    private ClueMapper clueMapper;
    @Autowired
    private IMemberLabelMapper iMemberLabelMapper;
    @Autowired
    private ICustomerMapper iCustomerMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private CraftsmanConstructionService constructionService;
    @Autowired
    private ClueTalkMapper clueTalkMapper;
    @Autowired
    private IntentionHouseMapper intentionHouseMapper;
    @Autowired
    private IMemberMapper iMemberMapper;
    @Autowired
    private DjRobSingleMapper djRobSingleMapper;
    @Autowired
    private HouseService houseService;
    @Autowired
    private IStoreMapper iStoreMapper;
    @Autowired
    private DjAlreadyRobSingleMapper djAlreadyRobSingleMapper;
    @Autowired
    private IHouseMapper iHouseMapper;
    @Autowired
    private IHouseAddressMapper iHouseAddressMapper;


    @Autowired
    private IStoreUserMapper iStoreUserMapper;
    /**
     * 查询待抢单列表
     * @param userToken
     * @param storeId
     * @return
     */
    public ServerResponse queryRobSingledata(String userToken, String storeId,Integer isRobStats) {

        Object object = constructionService.getAccessToken(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        AccessToken accessToken = (AccessToken) object;
        if (CommonUtil.isEmpty(accessToken.getUserId())) {
            return ServerResponse.createbyUserTokenError();
        }




        Example example = new Example(StoreUser.class);
        example.createCriteria().andEqualTo(StoreUser.USER_ID, accessToken.getUserId())
                .andEqualTo(StoreUser.DATA_STATUS, 0);
        List<StoreUser> storeList = iStoreUserMapper.selectByExample(example);
        if (storeList.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }

        Map<String, Object> map = new HashMap<>();
        map.put("userId", accessToken.getUserId());

        if (!CommonUtil.isEmpty(storeId)) {
            map.put("storeId", storeId);
        }else{
            map.put("storeId", storeList.get(0).getStoreId());
        }

        map.put("isRobStats", 0);

        List<RobDTO> list = clueMapper.queryRobSingledata(map);




        //查询标签
        List<RobDTO> robDTOs = new ArrayList<>();
        if(!list.isEmpty()){
            for (RobDTO li : list) {
                RobDTO robDTO = new RobDTO();
                if (!CommonUtil.isEmpty(li.getLabelIdArr())) {
                    String[] labelIds = li.getLabelIdArr().split(",");
                    List<SaleMemberLabelDTO> labelByIds = iMemberLabelMapper.getLabelByIds(labelIds);
                    robDTO.setList(labelByIds);
                }
                robDTO.setCusService(li.getCusService());
                robDTO.setCreateDate(li.getCreateDate());
                robDTO.setPhone(li.getPhone());
                robDTO.setOwerName(li.getOwerName());
                robDTO.setVisitState(li.getVisitState());
                robDTO.setClueId(li.getClueId());
                robDTO.setMcId(li.getMcId());
                robDTO.setStage(li.getStage());
                robDTO.setPhaseStatus(li.getPhaseStatus());
                robDTO.setHouseId(li.getHouseId());
                robDTO.setMemberId(li.getMemberId());
                robDTOs.add(robDTO);
            }
        }



        if (robDTOs.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        return ServerResponse.createBySuccess("查询待抢单列表", robDTOs);
    }



    /**
     * 查询已抢单列表
     * @param userToken
     * @param userId
     * @return
     */
    public ServerResponse queryAlreadyRobSingledata(String userToken,String userId) {

        Object object = constructionService.getAccessToken(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        AccessToken accessToken = (AccessToken) object;
        if (CommonUtil.isEmpty(accessToken.getUserId())) {
            return ServerResponse.createbyUserTokenError();
        }

        if(CommonUtil.isEmpty(userId)){
            userId = accessToken.getUserId();
        }

        Example example = new Example(DjAlreadyRobSingle.class);
        example.createCriteria()
                .andEqualTo(DjAlreadyRobSingle.USER_ID, userId)
                .andEqualTo(DjAlreadyRobSingle.DATA_STATUS, 0);
        List<DjAlreadyRobSingle> lists = djAlreadyRobSingleMapper.selectByExample(example);

        List<RobDTO> robDTOs = new ArrayList<>();

        for (DjAlreadyRobSingle da: lists) {
            RobDTO robDTO = new RobDTO();
            robDTO.setAlreadyId(da.getId());
            robDTO.setCreateDate(da.getCreateDate());
            Member member = iMemberMapper.selectByPrimaryKey(da.getMemberId());
            if(!CommonUtil.isEmpty(member)){
                robDTO.setOwerName(member.getNickName());
                robDTO.setMemberId(member.getId());
            }

            Clue clue = clueMapper.selectByPrimaryKey(da.getClueId());
            if(!CommonUtil.isEmpty(clue)){
                robDTO.setPhone(clue.getPhone());
                robDTO.setClueId(clue.getId());
                robDTO.setStage(clue.getStage());
                robDTO.setPhaseStatus(clue.getPhaseStatus());
                robDTO.setCusService(clue.getCusService());
            }

            House house = iHouseMapper.selectByPrimaryKey(da.getHouseId());
            if(!CommonUtil.isEmpty(house)){
                robDTO.setCreateDate(house.getCreateDate());
                robDTO.setIsRobStats(house.getIsRobStats());
                robDTO.setVisitState(house.getVisitState());
                robDTO.setHouseId(house.getId());
            }

            Customer customer = iCustomerMapper.selectByPrimaryKey(da.getMcId());
            if(!CommonUtil.isEmpty(customer)){
                //查询标签
                if (!CommonUtil.isEmpty(customer.getLabelIdArr())) {
                    String[] labelIds = customer.getLabelIdArr().split(",");
                    List<SaleMemberLabelDTO> labelByIds = iMemberLabelMapper.getLabelByIds(labelIds);
                    robDTO.setList(labelByIds);
                }
            }
            robDTOs.add(robDTO);
        }

        if (robDTOs.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        return ServerResponse.createBySuccess("查询已抢单列表", robDTOs);
    }

    /**
     * 抢单
     * @param djAlreadyRobSingle
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse upDateIsRobStats(DjAlreadyRobSingle djAlreadyRobSingle,String userToken) {
        try {
            if (!CommonUtil.isEmpty(djAlreadyRobSingle)) {

                logger.info("userToken==================="+ userToken);

                Object object = constructionService.getAccessToken(userToken);
                if (object instanceof ServerResponse) {
                    return (ServerResponse) object;
                }
                AccessToken accessToken = (AccessToken) object;
                if (CommonUtil.isEmpty(accessToken.getUserId())) {
                    return ServerResponse.createbyUserTokenError();
                }
                Example example=new Example(House.class);
                example.createCriteria().andEqualTo(House.ID,djAlreadyRobSingle.getHouseId())
                        .andEqualTo(House.DATA_STATUS,0);
                if(iHouseMapper.selectByExample(example).size()<=0){
                    return ServerResponse.createByErrorMessage("业主已撤回");
                }
                example=new Example(DjAlreadyRobSingle.class);
                example.createCriteria().andEqualTo(DjAlreadyRobSingle.HOUSE_ID,djAlreadyRobSingle.getHouseId());
                if(djAlreadyRobSingleMapper.selectByExample(example).size()>0){
                    return ServerResponse.createByErrorMessage("该订单已被抢");
                }

                djAlreadyRobSingle.setUserId(accessToken.getUserId());

                //新增抢单表数据
                djAlreadyRobSingleMapper.insert(djAlreadyRobSingle);

                Map<String, Object> map = new HashMap<>();
                map.put("id", djAlreadyRobSingle.getHouseId());
                map.put("isRobStats", 1);
                clueMapper.upDateIsRobStats(map);

                map = new HashMap<>();
                map.put("clueId",djAlreadyRobSingle.getClueId());
                map.put("cusService",accessToken.getUserId());
                clueMapper.upDateClueCusService(map);

                map = new HashMap<>();
                map.put("mcId",djAlreadyRobSingle.getMcId());
                map.put("userId",accessToken.getUserId());
                clueMapper.upDateMcUserId(map);
                return ServerResponse.createBySuccessMessage("抢单成功");
            }
            return ServerResponse.createByErrorMessage("抢单失败");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("修改失败");
        }

    }

    /**
     * 放弃
     * @param
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse upDateAlready(String houseId,String alreadyId) {
        try {
            Map<String, Object> map = new HashMap<>();
            if (!CommonUtil.isEmpty(houseId)) {
                map.put("id", houseId);
            }
            map.put("isRobStats", 0);
            clueMapper.upDateIsRobStats(map);
            djAlreadyRobSingleMapper.deleteByPrimaryKey(alreadyId);
            return ServerResponse.createBySuccessMessage("放弃成功");

        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("放弃失败");
        }

    }


    /**
     * 查询客户详情
     *
     * @param memberId
     * @return
     */
    public ServerResponse queryCustomerInfo(String userToken,
                                            String memberId,
                                            String clueId,
                                            Integer phaseStatus,
                                            String stage) {

        if(CommonUtil.isEmpty(clueId)){
            return ServerResponse.createByErrorMessage("线索不能为空");
        }
        //获取图片url
        String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);

        Clue clue = clueMapper.selectByPrimaryKey(clueId);



        Object object = constructionService.getAccessToken(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        AccessToken accessToken = (AccessToken) object;
        if (CommonUtil.isEmpty(accessToken.getUserId())) {
            return ServerResponse.createbyUserTokenError();
        }

        Example example = new Example(DjAlreadyRobSingle.class);
        example.createCriteria()
                .andEqualTo(DjAlreadyRobSingle.USER_ID, accessToken.getUserId())
                .andEqualTo(DjAlreadyRobSingle.DATA_STATUS, 0)
                .andEqualTo(DjAlreadyRobSingle.CLUE_ID, clueId);
        List<DjAlreadyRobSingle> djAlreadyRobSingle = djAlreadyRobSingleMapper.selectByExample(example);

        //客户阶段查询客户详情
        if (phaseStatus == 1) {
            Map<String, Object> map = new HashMap<>();
            if (!CommonUtil.isEmpty(memberId)) {
                map.put("memberId", memberId);
            }
            map.put("userId", clue.getCusService());
            if(clue != null){
                map.put("stage", clue.getStage());
            }
            map.put("phaseStatus", phaseStatus);
            RobArrInFoDTO robArrInFoDTO = new RobArrInFoDTO();
            robArrInFoDTO.setBranchUser(clue.getBranchUser());
            List<RobInfoDTO> robInfoDTO = clueMapper.queryCustomerInfo(map);

            if (!CommonUtil.isEmpty(robInfoDTO)) {
                robArrInFoDTO.setAlreadyId(
                        djAlreadyRobSingle.size()> 0 ?djAlreadyRobSingle.get(0).getId():null);
                robArrInFoDTO.setOwerName(robInfoDTO.get(0).getOwerName());
                robArrInFoDTO.setPhone(robInfoDTO.get(0).getPhone());
                robArrInFoDTO.setCompletedDate(robInfoDTO.get(0).getCompletedDate());
                robArrInFoDTO.setMcCreateDate(robInfoDTO.get(0).getMcCreateDate());
                robArrInFoDTO.setWechat(robInfoDTO.get(0).getWechat());
                robArrInFoDTO.setRemark(robInfoDTO.get(0).getRemark());
                robArrInFoDTO.setMemberId(robInfoDTO.get(0).getMemberId());
                robArrInFoDTO.setClueId(robInfoDTO.get(0).getClueId());
                robArrInFoDTO.setMcId(robInfoDTO.get(0).getMcId());
                robArrInFoDTO.setPhaseStatus(robInfoDTO.get(0).getPhaseStatus());
                robArrInFoDTO.setIsRobStats(robInfoDTO.get(0).getIsRobStats());
                if(robInfoDTO.get(0).getHouseCreateDate() != null){
                    robArrInFoDTO.setHouseCreateDate(robInfoDTO.get(0).getHouseCreateDate());
                }
                robArrInFoDTO.setCreateDate(robInfoDTO.get(0).getCreateDate());
                robArrInFoDTO.setUserId(robInfoDTO.get(0).getUserId());
                robArrInFoDTO.setStage(robInfoDTO.get(0).getStage());
                robArrInFoDTO.setDrawings(robInfoDTO.get(0).getDrawings());
                Member member = iMemberMapper.selectByPrimaryKey(robInfoDTO.get(0).getMemberId());

                if(null != member.getNickName()){
                    robArrInFoDTO.setNickName(member.getNickName());
                    robArrInFoDTO.setPhone(member.getMobile());
                    robArrInFoDTO.setMCreateDate(member.getCreateDate());
                }

                //查询意向房子
                List<IntentionHouseDTO> intentionHouseList = intentionHouseMapper.queryIntentionHouse(robInfoDTO.get(0).getClueId());
                robArrInFoDTO.setIntentionHouseList(intentionHouseList);
            }

            List<UserAchievementDTO> uadto= new ArrayList<>();
            Map<String,Object> parmMap= new HashMap<>();
            List<String> houseIds=new ArrayList<>();
            if (!CommonUtil.isEmpty(robInfoDTO)) {
                for (RobInfoDTO to : robInfoDTO) {
                    //查询大管家信息
                    if (!CommonUtil.isEmpty(to.getHouseId())) {
                        WorkerTypeDTO workerTypeDTO = iMemberLabelMapper.queryWorkerType(to.getHouseId());
                        if (null != workerTypeDTO) {
                            workerTypeDTO.setHead(imageAddress + workerTypeDTO.getHead());
                            List<WorkerTypeDTO> wtd = iMemberLabelMapper.queryType(to.getHouseId());
                            logger.info("wtd=============================="+ wtd);
                            logger.info("wtd.get(0).getType()=============================="+ wtd.get(0).getType());
                            logger.info("wtd.get(0).getWorkerTypeId()=============================="+ wtd.get(0).getWorkerTypeId());
                            logger.info("wtd.get(0).getWorkSteta()=============================="+ wtd.get(0).getWorkSteta());
                            logger.info("wtd.get(1).getType()=============================="+ wtd.get(1).getType());
                            logger.info("wtd.get(1).getWorkSteta()=============================="+ wtd.get(1).getWorkSteta());
                            if(wtd.size() > 0){
                                workerTypeDTO.setWorkerTypeId(wtd.get(0).getWorkerTypeId());
                                workerTypeDTO.setType(wtd.get(0).getType());
                                workerTypeDTO.setWorkSteta(wtd.get(0).getWorkSteta());
                                if(wtd.get(0).getWorkSteta().equals("0")){
                                    workerTypeDTO.setType(3);
                                }
                            }
                        }
                        to.setWorkerTypeDTO(workerTypeDTO);
                    }

                    //查询销售人员输入的房子类型
                    Example example1 = new Example(HouseAddress.class);
                    example1.createCriteria()
                            .andEqualTo(HouseAddress.HOUSE_ID, to.getHouseId())
                            .andEqualTo(HouseAddress.DATA_STATUS, 0);

                    //查询房子类型
                    List<HouseAddress> houseAddress = iHouseAddressMapper.selectByExample(example1);
                    logger.info("查询房子类型===================************"+houseAddress.size());
                    if(houseAddress.size() > 0){
                        logger.info("查询房子类型==================="+houseAddress.get(0).getHouseType());
                        to.setMcHouseType(houseAddress.get(0).getHouseType());
                    }

                    //查询小区名称
                    if (!CommonUtil.isEmpty(to.getHouseId())) {
                        String addressName = iMemberLabelMapper.queryAddressName(to.getHouseId());
                        to.setAddressName(addressName);
                    }

                    //改小区名称
                    if(CommonUtil.isEmpty(to.getResidential())){
                        ServerResponse serverResponse = houseService.getHouseAddress("houseId");
                        if (serverResponse.isSuccess()) {
                            HouseAddress address = (HouseAddress) serverResponse.getResultObj();
                            if(!CommonUtil.isEmpty(address.getName())){
                                to.setResidential(address.getName());
                            }
                        }
                    }

                    logger.info("userId================="+to.getCusService());
                    parmMap.put("userId",to.getCusService());

                    logger.info("userId================="+to.getHouseId());
                    if(!CommonUtil.isEmpty(to.getHouseId())){
                        logger.info("111111111111111111111"+to.getHouseId());
                        houseIds.add(to.getHouseId());
                        logger.info("houseIds================="+houseIds);
                        parmMap.put("houseIds",houseIds);
                        logger.info("parmMap================="+parmMap);
                        //查询业绩
                        List<UserAchievementDTO> userAchievementDTOS = clueMapper.queryUserAchievementInFo(parmMap);
                        logger.info("userAchievementDTOS================="+userAchievementDTOS);
                        logger.info("userAchievementDTOS================="+userAchievementDTOS.size());
                        if(null != userAchievementDTOS && !userAchievementDTOS.isEmpty()){
                            logger.info("userAchievementDTOS================="+userAchievementDTOS.size());
                            for (UserAchievementDTO userAchievementDTO : userAchievementDTOS) {
                                if(null !=userAchievementDTO){
                                    userAchievementDTO.setHead(imageAddress+userAchievementDTO.getHead());
                                    if(userAchievementDTO.getDataStatus() == 1){
                                        userAchievementDTO.setMonthRoyaltys(0);
                                        userAchievementDTO.setMeterRoyaltys(0);
                                        userAchievementDTO.setArrRoyalty(0);
                                    }

                                }
                            }
                            uadto.addAll(userAchievementDTOS);
                        }
                    }
                }
            }

            robArrInFoDTO.setUserInFo(uadto);

            //查询客户标签
            if (!CommonUtil.isEmpty(robInfoDTO)) {
                String str = robInfoDTO.get(0).getLabelIdArr();
                if (null != str) {
                    String[] labelIds = str.split(",");
                    List<SaleMemberLabelDTO> labelByIds = iMemberLabelMapper.getLabelByIds(labelIds);
                    robArrInFoDTO.setList(labelByIds);
                }
            }

            //查询沟通记录
            List<CustomerRecordInFoDTO> data = iMemberLabelMapper.queryTalkContent(clueId);
            if (!data.isEmpty()) {
                for (CustomerRecordInFoDTO datum : data) {
                    datum.setHead(imageAddress + datum.getHead());
                }
                robArrInFoDTO.setData(data);
            }

            robArrInFoDTO.setCustomerList(robInfoDTO);
            if (CommonUtil.isEmpty(robArrInFoDTO)) {
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            }
            return ServerResponse.createBySuccess("查询客户详情", robArrInFoDTO);
        } else {
            //线索阶段查询详情
            Map<String, Object> map = new HashMap<>();
            map.put("id", clueId);
            UserInfoDTO userInfoDTO = clueMapper.queryTips(map);

            if (!CommonUtil.isEmpty(userInfoDTO)) {
                //查询意向房子
                List<IntentionHouseDTO> intentionHouseList = intentionHouseMapper.queryIntentionHouse(userInfoDTO.getClueId());
                userInfoDTO.setIntentionHouseList(intentionHouseList);
            }

            //查询线索阶段标签
            if (!CommonUtil.isEmpty(userInfoDTO)) {
                if (!CommonUtil.isEmpty(userInfoDTO)) {
                    String str = userInfoDTO.getLabelId();
                    if (null != str) {
                        String[] labelIds = str.split(",");
                        List<SaleMemberLabelDTO> labelByIds = iMemberLabelMapper.getLabelByIds(labelIds);
                        userInfoDTO.setList(labelByIds);
                    }
                }
            }

            //查询线索沟通记录
            List<CustomerRecordInFoDTO> data = iMemberLabelMapper.queryTalkContent(clueId);
            if (!data.isEmpty()) {
                for (CustomerRecordInFoDTO datum : data) {
                    datum.setHead(imageAddress + datum.getHead());
                }
                userInfoDTO.setData(data);
            }
            return ServerResponse.createBySuccess("查询客户详情", userInfoDTO);
        }

    }
    private static Logger logger = LoggerFactory.getLogger(RedisSessionDAO.class);


    /**
     * 新增标签
     *
     * @param labelId
     * @return
     */
    public ServerResponse addLabel(String mcId,
                                   String labelId,
                                   String clueId,
                                   Integer phaseStatus) {
        try {
            if (phaseStatus == 1) {
                //客户阶段新增标签
                Map<String, Object> Map = new HashMap<>();
                if (!CommonUtil.isEmpty(mcId)) {
                    String str = iCustomerMapper.queryLabelIdArr(mcId);
                    if (!CommonUtil.isEmpty(str)) {
                        String[] strs = str.split(",");
                        for (String s : strs) {
                            if (s.equals(labelId)) {
                                return ServerResponse.createByErrorMessage("标签已存在");
                            }
                        }
                    }

                    String labelIdrr = str + "," + labelId;
                    Map.put("labelIdArr", labelIdrr);
                    Map.put("mcId", mcId);
                    iCustomerMapper.upDateLabelIdArr(Map);
                    return ServerResponse.createBySuccessMessage("新增成功");
                }
            } else {
                //线索阶段新增标签
                if (!CommonUtil.isEmpty(clueId)) {
                    Map<String, Object> Map = new HashMap<>();
                    String str = iCustomerMapper.queryLabelId(clueId);
                    if (!CommonUtil.isEmpty(str)) {
                        String[] strs = str.split(",");
                        for (String s : strs) {
                            if (s.equals(labelId)) {
                                return ServerResponse.createByErrorMessage("标签已存在 ");
                            }
                        }
                    }
                    String labelIdrr = str + "," + labelId;
                    Map.put("labelId", labelIdrr);
                    Map.put("clueId", clueId);
                    iCustomerMapper.upDateLabelId(Map);
                    return ServerResponse.createBySuccessMessage("新增成功");
                }
            }

            return ServerResponse.createByErrorMessage("新增失败");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("新增失败");
        }
    }

    /**
     * 删除标签
     *
     * @param labelIdArr
     * @return
     */
    public ServerResponse deleteLabel(String mcId,
                                      String labelIdArr,
                                      String clueId,
                                      Integer phaseStatus) {
        try {
            if (phaseStatus == 1) {
                //删除客户阶段标签
                Map<String, Object> Map = new HashMap<>();
                if (!CommonUtil.isEmpty(mcId)) {
                    Map.put("labelIdArr", labelIdArr);
                    Map.put("mcId", mcId);
                    iCustomerMapper.upDateLabelIdArr(Map);
                    return ServerResponse.createBySuccessMessage("删除成功");
                }
            } else {
                //删除线索阶段标签
                Map<String, Object> Map = new HashMap<>();
                if (!CommonUtil.isEmpty(clueId)) {
                    Map.put("labelId", labelIdArr);
                    Map.put("clueId", clueId);
                    iCustomerMapper.upDateLabelId(Map);
                    return ServerResponse.createBySuccessMessage("删除成功 ");
                }
            }

            return ServerResponse.createByErrorMessage("删除失败");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("删除失败");
        }
    }

    /**
     * 新增沟通记录
     *
     * @param customerRecDTO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse addDescribes(CustomerRecDTO customerRecDTO,String userToken) {
        try {

            AccessToken accessToken =null;
            if (!CommonUtil.isEmpty(userToken)) {
                Object object = constructionService.getAccessToken(userToken);
                if (object instanceof ServerResponse) {
                    return (ServerResponse) object;
                }
                accessToken = (AccessToken) object;
                if (CommonUtil.isEmpty(accessToken.getUserId())) {
                    return ServerResponse.createbyUserTokenError();
                }
            }

            if (!CommonUtil.isEmpty(customerRecDTO)) {
                // 线索阶段新增沟通记录
                ClueTalk clueTalk = new ClueTalk();
                if(accessToken != null){
                    clueTalk.setUserId(accessToken.getUserId());
                }
                if(CommonUtil.isEmpty(customerRecDTO.getRemindTime())){
                    clueTalk.setRemindTime(null);
                }else{
                    clueTalk.setRemindTime(customerRecDTO.getRemindTime());
                }

                if(null != customerRecDTO.getMemberId()){
                    clueTalk.setMemberId(customerRecDTO.getMemberId());
                }
                clueTalk.setClueId(customerRecDTO.getClueId());
                clueTalk.setTalkContent(customerRecDTO.getDescribes());
                clueTalk.setDataStatus(0);
                clueTalkMapper.insert(clueTalk);
                Clue clue=new Clue();
                clue.setId(customerRecDTO.getClueId());
                clue.setCreateDate(null);
                clue.setTimeSequencing(clueTalk.getCreateDate());
                clueMapper.updateByPrimaryKeySelective(clue);//沟通记录更新线索排序时间
                return ServerResponse.createBySuccessMessage("新增成功");
            }
            return ServerResponse.createByErrorMessage("新增失败");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("新增失败");
        }
    }


    /**
     * 查询今天提醒的沟通记录
     * @return
     */
    public List<ClueTalkDTO> getTodayDescribes(){
        return clueTalkMapper.getTodayDescribes(DateUtil.dateToString(new Date(), DateUtil.FORMAT11));
    }

    public void remindTime() {
//        String url = configUtil.getValue(SysConfig.PUBLIC_SALE_APP_ADDRESS, String.class);
//        Example example = new Example(ClueTalk.class);
//        example.createCriteria().andCondition(" DATE_FORMAT(remind_time, '%Y-%m-%d %I:%i')= '"
//                + DateUtil.dateToString(new Date(), DateUtil.FORMAT11) + "' ");
//        List<ClueTalk> customerRecords = clueTalkMapper.selectByExample(example);
//
//        for (ClueTalk customerRecord : customerRecords) {
//            MainUser u = userMapper.selectByPrimaryKey(customerRecord.getUserId());
//            if (u != null && !CommonUtil.isEmpty(u.getMemberId()))
//                configMessageService.addConfigMessage(AppType.SALE, u.getMemberId(), "沟通记录提示",
//                        "沟通记录的提醒时间到了，请及时跟进。", 0, url
//                                + Utils.getCustomerDetails(customerRecord.getMemberId(), customerRecord.getClueId(), 1, "1"));
//        }
    }


    /**
     * 修改客户信息
     *
     * @param clue
     * @return
     */
    public ServerResponse upDateCustomerInfo(Clue clue,Integer phaseStatus,String memberId) {
        try {
            Member member = new Member();
            if (!CommonUtil.isEmpty(clue)) {
                if(phaseStatus == 1){
                    member.setNickName(clue.getOwername());
                    member.setRemarks(clue.getRemark());
                    member.setCreateDate(null);
                    member.setMobile(clue.getPhone());
                    member.setId(memberId);
                    iMemberMapper.updateByPrimaryKeySelective(member);
                }
                clue.setCreateDate(null);
                clue.setMemberId(null);
                clueMapper.updateByPrimaryKeySelective(clue);
                return ServerResponse.createBySuccessMessage("修改成功");
            }

            return ServerResponse.createByErrorMessage("修改失败");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("修改失败");
        }
    }

    /**
     * 添加意向房子
     *
     * @param intentionHouse
     * @return
     */
    public ServerResponse addIntentionHouse(IntentionHouse intentionHouse) {
        try {
            if (!CommonUtil.isEmpty(intentionHouse)) {
                Example example = new Example(IntentionHouse.class);
                example.createCriteria().andEqualTo(IntentionHouse.RESIDENTIAL_NAME, intentionHouse.getResidentialName())
                        .andEqualTo(IntentionHouse.BUILDING_NAME, intentionHouse.getBuildingName())
                        .andEqualTo(IntentionHouse.NUMBER_NAME, intentionHouse.getNumberName())
                        .andEqualTo(IntentionHouse.CLUE_ID, intentionHouse.getClueId());
                List<IntentionHouse> intentionHouses = intentionHouseMapper.selectByExample(example);
                if (intentionHouses.size() > 0) {
                    return ServerResponse.createByErrorMessage("该意向房子已存在");
                }
                intentionHouseMapper.insert(intentionHouse);
                return ServerResponse.createBySuccessMessage("新增成功");
            }
            return ServerResponse.createByErrorMessage("新增失败");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("新增失败");
        }

    }


    /**
     * 删除意向房子
     *
     * @param id
     * @return
     */
    public ServerResponse deleteIntentionHouse(String id) {
        try {
            if (!CommonUtil.isEmpty(id)) {
                intentionHouseMapper.deleteIntentionHouse(id);
                return ServerResponse.createBySuccessMessage("删除成功");
            }
            return ServerResponse.createByErrorMessage("删除失败");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("删除失败");
        }
    }


    @Autowired
    private DjOrderSurfaceMapper djOrderSurfaceMapper;
    /**
     * 未录入抢单
     * @return
     */
    public ServerResponse notEnteredGrabSheet() {
        List<GrabSheetDTO> grabSheetDTOS = clueMapper.notEnteredGrabSheet();
        logger.info("grabSheetDTOS==================================="+grabSheetDTOS);
        logger.info("grabSheetDTOS.size()==================================="+grabSheetDTOS.size());
        if(grabSheetDTOS.size()>0) {
            for (GrabSheetDTO grabSheetDTO : grabSheetDTOS) {
                Example example=new Example(DjOrderSurface.class);
                example.createCriteria().andEqualTo(DjOrderSurface.CLUE_ID,grabSheetDTO.getClueId())
                        .andEqualTo(DjOrderSurface.DATA_STATUS,0);
                List<DjOrderSurface> djOrderSurfaces = djOrderSurfaceMapper.selectByExample(example);
                List<OrderStoreDTO> orderStore = iStoreMapper.getOrderStore(grabSheetDTO.getLatitude(), grabSheetDTO.getLongitude(),djOrderSurfaces);

                if(djOrderSurfaces.size()<=1){
                    djOrderSurfaces=null;
                }
                List<DjRobSingle> djRobSingles = djRobSingleMapper.getRobDate(djOrderSurfaces);
                if(djRobSingles.size() > orderStore.size()){
                    logger.info("11111==================================="+orderStore);
                    for (int i = 0; i < orderStore.size(); i++) {
                        orderStore.get(i).setRobDate(djRobSingles.get(i).getRobDate());
                        orderStore.get(i).setRobDateId(djRobSingles.get(i).getId());
                    }
                }else{
                    logger.info("22222==================================="+orderStore);
                    for (int i = 0; i < djRobSingles.size(); i++) {
                        orderStore.get(i).setRobDate(djRobSingles.get(i).getRobDate());
                        orderStore.get(i).setRobDateId(djRobSingles.get(i).getId());
                    }
                }

                logger.info("orderStore==================================="+orderStore);
                logger.info("orderStore.size()==================================="+orderStore.size());
                for (OrderStoreDTO orderStoreDTO : orderStore) {
                    if(!CommonUtil.isEmpty(orderStoreDTO.getRobDate())) {
                        if (((System.currentTimeMillis() - grabSheetDTO.getModifyDate().getTime()) / 60 / 1000)
                                > (Integer.parseInt(orderStoreDTO.getRobDate())-1)) {
                            logger.info("11111111111111111111===================================" + orderStoreDTO.getStoreId());
                            logger.info("11111111111111111111===================================" + grabSheetDTO.getMemberId());
                            DjOrderSurface djOrderSurface = new DjOrderSurface();
                            djOrderSurface.setDataStatus(0);
                            djOrderSurface.setStoreId(orderStoreDTO.getStoreId());
                            djOrderSurface.setClueId(grabSheetDTO.getClueId());
                            djOrderSurface.setRobDateId(orderStoreDTO.getRobDateId());
                            djOrderSurfaceMapper.insert(djOrderSurface);
                            clueMapper.setDistribution(orderStoreDTO.getStoreId(), grabSheetDTO.getMemberId(),new Date());
                            break;
                        }
                    }
                }
            }
        }
        return ServerResponse.createBySuccessMessage("分配成功");
    }


    /**
     * 新增配置时间
     * @param djRobSingle
     * @return
     */
    public ServerResponse addDjRobSingle(DjRobSingle djRobSingle) {
        try {
            if (!CommonUtil.isEmpty(djRobSingle)) {
                djRobSingleMapper.insert(djRobSingle);
                return ServerResponse.createBySuccessMessage("新增成功");
            }
            return ServerResponse.createByErrorMessage("新增失败");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("新增失败");
        }

    }

    /**
     * 修改配置时间
     * @param djRobSingle
     * @return
     */
    public ServerResponse upDateDjRobSingle(DjRobSingle djRobSingle) {
        try {
            if (!CommonUtil.isEmpty(djRobSingle)) {
                djRobSingle.setCreateDate(null);
                djRobSingleMapper.updateByPrimaryKeySelective(djRobSingle);
                return ServerResponse.createBySuccessMessage("修改成功");
            }
            return ServerResponse.createByErrorMessage("修改失败");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("修改失败");
        }

    }

    /**
     * 删除配置时间
     * @param djRobSingle
     * @return
     */
    public ServerResponse deleteDjRobSingle(DjRobSingle djRobSingle) {
        try {
            if (!CommonUtil.isEmpty(djRobSingle)) {
                djRobSingleMapper.deleteByPrimaryKey(djRobSingle);
                return ServerResponse.createBySuccessMessage("删除成功");
            }
            return ServerResponse.createByErrorMessage("删除失败");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("删除失败");
        }

    }

    /**
     * 查询配置时间
     * @param pageDTO
     * @return
     */
    public ServerResponse queryDjRobSingle(PageDTO pageDTO){
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        Example example=new Example(DjRobSingle.class);
        example.orderBy(DjRobSingle.CREATE_DATE).asc();
        List<DjRobSingle> djRobSingles = djRobSingleMapper.selectByExample(example);
        if (djRobSingles.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        return ServerResponse.createBySuccess("查询提成列表", new PageInfo(djRobSingles));

    }


}
