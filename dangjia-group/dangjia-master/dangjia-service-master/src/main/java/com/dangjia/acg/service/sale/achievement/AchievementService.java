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
import com.dangjia.acg.modle.store.Store;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.dangjia.acg.service.sale.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        object = saleService.getStore(accessToken.getUserId());
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Store store = (Store) object;


        Map<String,Object> map = new HashMap();
        if (!CommonUtil.isEmpty(time)) {
            map.put("time",DateUtil.dateToString(time, DateUtil.FORMAT));
        }
        if (!CommonUtil.isEmpty(store.getId())) {
            map.put("store",store.getId());
        }

        AchievementDataDTO achievementDataDTO = new AchievementDataDTO();

        List<AchievementInfoDTO> achievementInfoDTOS = achievementMapper.queryRoyaltyMatch(map);

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
    public ServerResponse queryUserAchievementData(String userToken, Integer visitState, String userId, Date time){

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
        }

        UserAchievementDataDTO userAchievementDataDTO = new UserAchievementDataDTO();
        //查询员工业绩
        List<UserAchievementInfoDTO> list = achievementMapper.queryUserAchievementData(map);

//        //全部提成数量
//        int arrRoyalty = 1000;
//        int s = 0;
//
//        //每条数据当月提成
//        if(!list.isEmpty()){
//            for (UserAchievementInfoDTO to:list) {
//                if(to.getVisitState() == 1){
//                    s = (int) (arrRoyalty*0.75);
//                    to.setMonthRoyalty(s);
//                    to.setMeterRoyalty(s);
//                }
//                if(to.getVisitState() == 3){
//                    s = (int) (arrRoyalty*0.25);
//                    to.setMonthRoyalty(s);
//                    to.setMeterRoyalty(arrRoyalty);
//                }
//                to.setArrRoyalty(arrRoyalty);
//            }
//        }


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
