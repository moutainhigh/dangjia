package com.dangjia.acg.service.sale.achievement;

import com.dangjia.acg.auth.config.RedisSessionDAO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.dto.sale.achievement.*;
import com.dangjia.acg.mapper.sale.AchievementMapper;
import com.dangjia.acg.mapper.sale.DjAreaMatchMapper;
import com.dangjia.acg.mapper.sale.ResidentialRangeMapper;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.sale.residential.ResidentialRange;
import com.dangjia.acg.modle.sale.royalty.DjAreaMatch;
import com.dangjia.acg.modle.store.Store;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.dangjia.acg.service.sale.SaleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 * 业绩 业务逻辑层
 * Created with IntelliJ IDEA.
 * author: ljl
 * Date: 2019/7/27
 * Time: 10:01
 */
@Service
public class AchievementService {

    @Autowired
    private AchievementMapper achievementMapper;
    @Autowired
    private CraftsmanConstructionService constructionService;
    @Autowired
    private SaleService saleService;
    @Autowired
    private ResidentialRangeMapper residentialRangeMapper;
    @Autowired
    private DjAreaMatchMapper djAreaMatchMapper;
    private static Logger logger = LoggerFactory.getLogger(RedisSessionDAO.class);
    /**
     * 根据月份 查询店长业绩
     * @param storeId
     * @param time
     * @return
     */
    public ServerResponse queryLeaderAchievementData(String userToken,String storeId ,Date time){
        logger.info("userToken==================="+ userToken);



        Object object = constructionService.getAccessToken(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        AccessToken accessToken = (AccessToken) object;
        if (CommonUtil.isEmpty(accessToken.getUserId())) {
            return ServerResponse.createbyUserTokenError();
        }
        object = saleService.getStore(accessToken.getUserId());
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Store store = (Store) object;

        Map<String,Object> map = new HashMap();
        if (!CommonUtil.isEmpty(time)) {
            map.put("time",DateUtil.dateToString(time, DateUtil.FORMAT));
        }


        logger.info("店长id   storeId==================="+storeId);

        logger.info("店长id   store.getId()==============="+ store.getId());

        map.put("storeId",store.getId());


        AchievementDataDTO achievementDataDTO = new AchievementDataDTO();

        List<AchievementInfoDTO> achievementInfoDTOS = achievementMapper.queryRoyaltyMatch(map);

        for (AchievementInfoDTO aa:achievementInfoDTOS) {
            int i = achievementMapper.Complete(aa.getUserId(), DateUtil.dateToString(time, DateUtil.FORMAT),null,null,null,null);
            aa.setSingleNumber(i);
        }

        Integer taskOrderNum = achievementInfoDTOS.stream().filter
                (a -> a.getMonthRoyalty()!=null).mapToInt
                (AchievementInfoDTO::getMonthRoyalty).sum();

        Integer s = achievementInfoDTOS.stream().filter
                (a -> a.getSingleNumber()!=null).mapToInt
                (AchievementInfoDTO::getSingleNumber).sum();

        achievementDataDTO.setDealNumber(s);
        achievementDataDTO.setStoreRoyalty(taskOrderNum);
        achievementDataDTO.setAchievementDataDTOS(achievementInfoDTOS);

        return ServerResponse.createBySuccess("查询成功", achievementDataDTO);

    }


    /**
     * 查询员工业绩
     * @param visitState
     * @param userId
     * @param time
     * @return
     */
    public ServerResponse queryUserAchievementData(String userToken, Integer visitState, String userId, Date time, String villageId,String building){

        Object object = constructionService.getAccessToken(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        AccessToken accessToken = (AccessToken) object;
        if (CommonUtil.isEmpty(accessToken.getUserId())) {
            return ServerResponse.createbyUserTokenError();
        }

        Map<String,Object> map = new HashMap();
        if (!CommonUtil.isEmpty(time)) {
            map.put("time",DateUtil.dateToString(time, DateUtil.FORMAT));
        }
        if (!CommonUtil.isEmpty(visitState)) {
            map.put("visitState",visitState);
        }
        if (!CommonUtil.isEmpty(userId)) {
            map.put("userId",userId);
        }else{
            map.put("userId",accessToken.getUserId());
            userId =accessToken.getUserId();
        }
        if (!CommonUtil.isEmpty(villageId)) {
            map.put("villageId",villageId);
        }
        if (!CommonUtil.isEmpty(building)) {
            map.put("building",building);
        }
        if(villageId.equals("0")){
            map.put("building",null);
            map.put("villageId",null);
        }
        List<DjAreaMatch> djAreaMatches=new ArrayList<>();
        if (villageId.equals("1")) {
            Example example=new Example(ResidentialRange.class);
            example.createCriteria().andEqualTo(ResidentialRange.USER_ID,userId);
            List<ResidentialRange> list = residentialRangeMapper.selectByExample(example);
            List<String> listIds=new ArrayList<>();
            for (ResidentialRange residentialRange : list) {
                String[] split = residentialRange.getBuildingId().split(",");
                listIds.addAll(Arrays.asList(split));
            }
            if(!listIds.isEmpty()) {
                example=new Example(DjAreaMatch.class);
                example.createCriteria().andIn(DjAreaMatch.BUILDING_ID,listIds);
                djAreaMatches = djAreaMatchMapper.selectByExample(example);
            }
            map.put("building",null);
            map.put("villageId",1);
            map.put("buildings",djAreaMatches);
        }
        UserAchievementDataDTO userAchievementDataDTO = new UserAchievementDataDTO();
        //查询员工业绩
        List<UserAchievementInfoDTO> list = achievementMapper.queryUserAchievementData(map);
        //求销售人员总提成
        Integer taskOrderNum = list.stream().filter
                (a -> a.getMonthRoyalty()!=null).mapToInt
                (UserAchievementInfoDTO::getMonthRoyalty).sum();

        userAchievementDataDTO.setUserAchievementInfoDTOS(list);
        userAchievementDataDTO.setArrMonthRoyalty(taskOrderNum);
        userAchievementDataDTO.setDealNumber(achievementMapper.Complete(userId, DateUtil.dateToString(time, DateUtil.FORMAT),building,villageId,visitState,djAreaMatches));

        return ServerResponse.createBySuccess("查询提成列表", userAchievementDataDTO);
    }


    /**
     * 查询员工业绩
     * @param visitState
     * @param userId
     * @param time
     * @return
     */
    public ServerResponse volume(String userToken, Integer visitState, String userId, Date time ,String building,String villageId){

        Object object = constructionService.getAccessToken(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        AccessToken accessToken = (AccessToken) object;
        if (CommonUtil.isEmpty(accessToken.getUserId())) {
            return ServerResponse.createbyUserTokenError();
        }

        Map<String,Object> map = new HashMap();
        if (!CommonUtil.isEmpty(time)) {
            map.put("time",DateUtil.dateToString(time, DateUtil.FORMAT));
        }
        if (!CommonUtil.isEmpty(visitState)) {
            map.put("visitState",visitState);
        }
        if (!CommonUtil.isEmpty(userId)) {
            map.put("userId",userId);
        }else{
            map.put("userId",accessToken.getUserId());
            userId =accessToken.getUserId();
        }
        if (!CommonUtil.isEmpty(building)) {
            map.put("building",building);
        }
        if (!CommonUtil.isEmpty(villageId)) {
            map.put("villageId",villageId);
        }
        if(villageId.equals("0")){
            map.put("building",null);
            map.put("villageId",null);
        }
        List<DjAreaMatch> djAreaMatches=new ArrayList<>();
        if (villageId.equals("1")) {
            Example example=new Example(ResidentialRange.class);
            example.createCriteria().andEqualTo(ResidentialRange.USER_ID,userId);
            List<ResidentialRange> list = residentialRangeMapper.selectByExample(example);
            List<String> listIds=new ArrayList<>();
            for (ResidentialRange residentialRange : list) {
                String[] split = residentialRange.getBuildingId().split(",");
                listIds.addAll(Arrays.asList(split));
            }
            if(!listIds.isEmpty()) {
                example=new Example(DjAreaMatch.class);
                example.createCriteria().andIn(DjAreaMatch.BUILDING_ID,listIds);
                djAreaMatches = djAreaMatchMapper.selectByExample(example);
            }
            map.put("building",null);
            map.put("villageId",1);
            map.put("buildings",djAreaMatches);
        }
        VolumeDTO volumeDTO=new VolumeDTO();
        //查询员工业绩
        List<UserAchievementInfoDTO> volumeDTOS = achievementMapper.queryVolumeDTO(map);
        volumeDTO.setUserAchievementInfoDTOS(volumeDTOS);
        volumeDTO.setDealNumber(achievementMapper.Complete(userId, DateUtil.dateToString(time, DateUtil.FORMAT),building,villageId,visitState,djAreaMatches));
        return ServerResponse.createBySuccess("查询成功", volumeDTO);
    }


    /**
     * 业绩页城市小区
     * @param userToken
     * @param userId
     * @return
     */
    public ServerResponse performanceQueryConditions(String userToken, String userId){

        Object object = constructionService.getAccessToken(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        AccessToken accessToken = (AccessToken) object;
        if (CommonUtil.isEmpty(accessToken.getUserId())) {
            return ServerResponse.createbyUserTokenError();
        }
        Map<String,Object> map = new HashMap();
        if (!CommonUtil.isEmpty(userId)) {
            map.put("userId",userId);
        }else{
            map.put("userId",accessToken.getUserId());
            userId =accessToken.getUserId();
        }
        Example example=new Example(ResidentialRange.class);
        example.createCriteria().andEqualTo(ResidentialRange.USER_ID,userId);
        List<ResidentialRange> list = residentialRangeMapper.selectByExample(example);
        List<String> listIds=new ArrayList<>();
        for (ResidentialRange residentialRange : list) {
            String[] split = residentialRange.getBuildingId().split(",");
            listIds.addAll(Arrays.asList(split));
        }
        List<DjAreaMatch> djAreaMatches=new ArrayList<>();
        if(!listIds.isEmpty()) {
            example=new Example(DjAreaMatch.class);
            example.createCriteria().andIn(DjAreaMatch.BUILDING_ID,listIds);
            djAreaMatches = djAreaMatchMapper.selectByExample(example);
        }
        return ServerResponse.createBySuccess("查询查询条件成功",djAreaMatches);
    }

}
