package com.dangjia.acg.mapper.sale;

import com.dangjia.acg.dto.sale.achievement.AchievementInfoDTO;
import com.dangjia.acg.dto.sale.achievement.UserAchievementInfoDTO;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 业绩 DAO
 * Created with IntelliJ IDEA.
 * author: ljl
 * Date: 2019/7/27
 * Time: 10:01
 */
@Repository
public interface AchievementMapper {

    /**
     * 查询成交量
     * @param map
     * @return
     */
    Integer queryDealNumber(Map<String, Object> map);


    /**
     * 查詢当月订单状态
     * @param map
     * @return
     */
    List<AchievementInfoDTO> queryVisitState(Map<String, Object> map);

    /**
     * 全部订单状态
     * @param map
     * @return
     */
    List<AchievementInfoDTO> queryMeterVisitState(Map<String, Object> map);

    /**
     * 根据店长查询销售人员的下单数
     * @param map
     * @return
     */
    List<AchievementInfoDTO> querySingleNumber(Map<String, Object> map);

    /**
     * 查询店长下面员工
     */
    List<AchievementInfoDTO>queryUserId(Map<String, Object> map);

    /**
     *  查询员工业绩
     * @param map
     * @return
     */
    List<UserAchievementInfoDTO>queryUserAchievementData(Map<String, Object> map);



    /**
     *  查询门店下面员工业绩
     * @param map
     * @return
     */
    List<AchievementInfoDTO>queryRoyaltyMatch(Map<String, Object> map);




    int Complete(Map<String, Object> map1);


    /**
     *  查询成交量页面
     * @param map
     * @return
     */
    List<UserAchievementInfoDTO>queryVolumeDTO(Map<String, Object> map);



}
