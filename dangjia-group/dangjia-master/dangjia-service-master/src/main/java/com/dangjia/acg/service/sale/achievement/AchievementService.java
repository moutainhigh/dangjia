package com.dangjia.acg.service.sale.achievement;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.dto.sale.achievement.AchievementDataDTO;
import com.dangjia.acg.dto.sale.achievement.AchievementInfoDTO;
import com.dangjia.acg.dto.sale.achievement.UserAchievementDataDTO;
import com.dangjia.acg.dto.sale.achievement.UserAchievementInfoDTO;
import com.dangjia.acg.mapper.sale.AchievementMapper;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    /**
     * 根据月份 查询店长业绩
     * @param storeId
     * @param time
     * @return
     */
    public ServerResponse queryLeaderAchievementData(String userToken,String storeId ,Date time){
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
        if (!CommonUtil.isEmpty(storeId)) {
            map.put("storeId",storeId);
        }
        map.put("userId",accessToken.getUserId());

        Integer result = achievementMapper.queryDealNumber(map);
        AchievementDataDTO achievementDataDTO = new AchievementDataDTO();

        //查詢當月订单状态
        List<AchievementInfoDTO> achievementInfoDTOs = achievementMapper.queryVisitState(map);

        //全部提成数量
        int arrRoyalty = 1000;

        if(!achievementInfoDTOs.isEmpty()){
            int TheSum = 0;
            int s = 0;
            //每单提成比列
            for (AchievementInfoDTO dto: achievementInfoDTOs) {
                if(dto.getVisitState() == 1){
                    s = (int) (arrRoyalty*0.75);
                }
                if(dto.getVisitState() == 3){
                    s = (int) (arrRoyalty*0.25);
                }
                dto.setZiduan(s);
                TheSum += s;
            }
            //总提成
            achievementDataDTO.setStoreRoyalty(TheSum);
        }

        //求一个销售人员当月提成总和
        Map<String,Integer> royMap = achievementInfoDTOs.stream().collect(
                Collectors.toMap(
                        item -> item.getUserId(),
                        item -> item.getZiduan(),
                        Integer::sum));

        //查詢全部订单状态
        List<AchievementInfoDTO> meterVisitState = achievementMapper.queryMeterVisitState(map);

        if(!meterVisitState.isEmpty()){
            int s = 0;
            //每单提成比列
            for (AchievementInfoDTO dto: meterVisitState) {
                if(dto.getVisitState() == 1){
                    s = (int) (arrRoyalty*0.75);
                    dto.setMeterRoyalty(s);
                }
                if(dto.getVisitState() == 3){
                    s = (int) (arrRoyalty*0.25);
                    dto.setMeterRoyalty(arrRoyalty);
                }
                dto.setZiduan(s);
            }
        }

        //求一个销售人员累计提成总和
        Map<String,Integer> mroMap = meterVisitState.stream().collect(
                Collectors.toMap(
                        item -> item.getUserId(),
                        item -> item.getZiduan(),
                        Integer::sum));


        //根据店长查询销 售人员的下单数
        List<AchievementInfoDTO> singleNumber = achievementMapper.querySingleNumber(map);

        //查询店长下面员工信息列表
        List<AchievementInfoDTO> queryUserId = achievementMapper.queryUserId(map);

        //销售人员下单数 把值房到销售人员列表里面
        if(!singleNumber.isEmpty() && !queryUserId.isEmpty()){
            for (AchievementInfoDTO to: queryUserId) {
                for (AchievementInfoDTO vo:singleNumber) {
                    if(to.getUserId().equals(vo.getUserId())){
                        to.setSingleNumber(vo.getCount());
                        to.setVisitState(vo.getVisitState());
                    }
                }
            }
        }

        if(!queryUserId.isEmpty()){
            //销售人员当月提成  到销售人员列表里面
            for (AchievementInfoDTO to: queryUserId) {
                for (String key : royMap.keySet()) {
                    if(to.getUserId().equals(key)){
                        to.setMonthRoyalty(royMap.get(key));
                    }
                }
                to.setArrRoyalty(arrRoyalty);
            }
        }

        //销售人员累计提成  到销售人员列表里面
        for (AchievementInfoDTO to: queryUserId) {
            for (String key : mroMap.keySet()) {
                if(to.getUserId().equals(key)){
                    to.setMeterRoyalty(mroMap.get(key));
                }
            }
//            to.setArrRoyalty(arrRoyalty);
        }

        achievementDataDTO.setAchievementDataDTOS(queryUserId);
        achievementDataDTO.setDealNumber(result);


        return ServerResponse.createBySuccess("查询提成列表", achievementDataDTO);
    }


    /**
     * 查询员工业绩
     * @param visitState
     * @param userId
     * @param time
     * @return
     */
    public ServerResponse queryUserAchievementData(Integer visitState, String userId, Date time){

        Map<String,Object> map = new HashMap();
        if (!CommonUtil.isEmpty(time)) {
            map.put("time",DateUtil.dateToString(time, DateUtil.FORMAT));
        }
        if (!CommonUtil.isEmpty(visitState)) {
            map.put("visitState",visitState);
        }
        if (!CommonUtil.isEmpty(userId)) {
            map.put("userId",userId);
        }

        UserAchievementDataDTO userAchievementDataDTO = new UserAchievementDataDTO();
        //查询员工业绩
        List<UserAchievementInfoDTO> list = achievementMapper.queryUserAchievementData(map);

        //全部提成数量
        int arrRoyalty = 1000;
        int s = 0;

        //每条数据当月提成
        if(!list.isEmpty()){
            for (UserAchievementInfoDTO to:list) {
                if(to.getVisitState() == 1){
                    s = (int) (arrRoyalty*0.75);
                    to.setMonthRoyalty(s);
                    to.setMeterRoyalty(s);
                }
                if(to.getVisitState() == 3){
                    s = (int) (arrRoyalty*0.25);
                    to.setMonthRoyalty(s);
                    to.setMeterRoyalty(arrRoyalty);
                }
                to.setArrRoyalty(arrRoyalty);
            }
        }


        //求销售人员总提成
        Integer taskOrderNum = list.stream().filter
                (a -> a.getMonthRoyalty()!=null).mapToInt
                (UserAchievementInfoDTO::getMonthRoyalty).sum();

        userAchievementDataDTO.setUserAchievementInfoDTOS(list);
        userAchievementDataDTO.setArrMonthRoyalty(taskOrderNum);
        userAchievementDataDTO.setDealNumber(list.size());

        return ServerResponse.createBySuccess("查询提成列表", userAchievementDataDTO);
    }


}
