package com.dangjia.acg.service.sale.achievement;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.dto.sale.achievement.AchievementDataDTO;
import com.dangjia.acg.dto.sale.achievement.AchievementInfoDTO;
import com.dangjia.acg.dto.sale.achievement.UserAchievementDataDTO;
import com.dangjia.acg.dto.sale.achievement.UserAchievementInfoDTO;
import com.dangjia.acg.mapper.sale.achievement.AchievementMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    /**
     * 根据月份 查询店长业绩
     * @param storeId
     * @param userId
     * @param time
     * @return
     */
    public ServerResponse queryLeaderAchievementData(String storeId , String userId, Date time){

        Map<String,Object> map = new HashMap();
        if (!CommonUtil.isEmpty(time)) {
            map.put("time",DateUtil.dateToString(time, DateUtil.FORMAT));
        }
        if (!CommonUtil.isEmpty(storeId)) {
            map.put("storeId",storeId);
        }
        if (!CommonUtil.isEmpty(userId)) {
            map.put("userId",userId);
        }
        Integer result = achievementMapper.queryDealNumber(map);
        AchievementDataDTO achievementDataDTO = new AchievementDataDTO();

        //查詢當月订单状态
        List<AchievementInfoDTO> achievementInfoDTOs = achievementMapper.queryVisitState(map);

        List<AchievementInfoDTO> copy = new ArrayList<>(achievementInfoDTOs);

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

        Integer taskOrderNum = achievementInfoDTOs.stream().filter
                (a -> a.getUserId().equals(a.getUserId())).mapToInt
                (AchievementInfoDTO::getZiduan).sum();


        //求每个销售人员当月提成总和
        if(!achievementInfoDTOs.isEmpty()){
            for (int j = 0; j < achievementInfoDTOs.size(); j++) {
                for (int k = 1; k < copy.size() ; k++) {
                    AchievementInfoDTO jj = achievementInfoDTOs.get(j);
                    AchievementInfoDTO kk = copy.get(k);
                    if(jj.getUserId().equals(kk.getUserId())){
                        jj.setMonthRoyalty(achievementInfoDTOs.stream().filter
                                (a -> a.getUserId().equals(a.getUserId())).mapToInt
                                (AchievementInfoDTO::getZiduan).sum());
                    }
                }
            }
        }

        //查詢全部订单状态
        List<AchievementInfoDTO> meterVisitState = achievementMapper.queryMeterVisitState(map);

        List<AchievementInfoDTO> copyMonthRoyalty = new ArrayList<>(meterVisitState);

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

        //求每个销售人员累计提成提成总和
        if(!meterVisitState.isEmpty()){
            for (int j = 0; j < meterVisitState.size(); j++) {
                for (int k = 1; k < copyMonthRoyalty.size() ; k++) {
                    AchievementInfoDTO jj = meterVisitState.get(j);
                    AchievementInfoDTO kk = copyMonthRoyalty.get(k);
                    if(jj.getUserId().equals(kk.getUserId())){
                        jj.setMeterRoyalty(jj.getZiduan() + kk.getZiduan());
                    }
                }
            }
        }

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

        if(!achievementInfoDTOs.isEmpty() && !queryUserId.isEmpty()){
            //销售人员当月提成  到销售人员列表里面
            for (AchievementInfoDTO to: queryUserId) {
                for (AchievementInfoDTO aa:achievementInfoDTOs) {
                    if(to.getUserId().equals(aa.getUserId())){
                        to.setMonthRoyalty(aa.getMonthRoyalty());
                    }
                }
                to.setArrRoyalty(arrRoyalty);
            }
        }

        achievementDataDTO.setAchievementDataDTOS(queryUserId);
        achievementDataDTO.setDealNumber(result);

        return ServerResponse.createBySuccess("查询成功",achievementDataDTO);
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
        return ServerResponse.createBySuccess("查询成功",userAchievementDataDTO);
    }


}
