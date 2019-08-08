package com.dangjia.acg.mapper.clue;


import com.dangjia.acg.dto.other.ClueDTO;
import com.dangjia.acg.dto.sale.achievement.UserAchievementDTO;
import com.dangjia.acg.dto.sale.client.CustomerIndexDTO;
import com.dangjia.acg.dto.sale.client.OrdersCustomerDTO;
import com.dangjia.acg.dto.sale.rob.RobDTO;
import com.dangjia.acg.dto.sale.rob.RobInfoDTO;
import com.dangjia.acg.dto.sale.rob.UserInfoDTO;
import com.dangjia.acg.dto.sale.store.StoreUserDTO;
import com.dangjia.acg.modle.clue.Clue;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;


@Repository
public interface ClueMapper extends Mapper<Clue> {
    List<Clue> getByStage(int stage);

    Clue getByPhone(String phone);

    List<Clue> getAll();

    List<Clue> getAllByCondition(String values);

    List<Clue> getGroupBy(@Param("phone") String phone,@Param("userId") String userId,@Param("storeId") String storeId);

    Clue getClue(@Param("phone") String phone,@Param("userId") String userId);

    List<ClueDTO>

    followList(@Param("label") String label,
                             @Param("time") String time,
                             @Param("stage") Integer stage,
                             @Param("searchKey") String searchKey,
                             @Param("userId") String userId,
                             @Param("storeId") String storeId);

    CustomerIndexDTO clientPage(@Param("type") Integer type,@Param("userId") String userId,@Param("storeUsers") List<StoreUserDTO> storeUsers);

    Integer Complete(@Param("userId") String userId,@Param("time") String time);

    List<OrdersCustomerDTO> ordersCustomer(@Param("userId") String userId,
                                           @Param("visitState") String visitState,
                                           @Param("searchKey") String searchKey,
                                           @Param("time") String time,
                                           @Param("storeId") String storeId,
                                           @Param("staff") String staff);

    List<CustomerIndexDTO> sleepingCustomer(@Param("storeId") String storeId,
                                            @Param("searchKey") String searchKey,
                                            @Param("time") String time,
                                            @Param("staff") String staff);

    /**
     * 查询现在表条数
     * @return
     */
    Integer queryTClue(@Param("mobile") String mobile);


    List<RobDTO> queryRobSingledata(Map<String,Object> map);


    List<RobInfoDTO> queryCustomerInfo(Map<String,Object> map);


    List<UserAchievementDTO> queryUserAchievementInFo(Map<String,Object> map);


    void upDateIsRobStats(Map<String,Object> map);


    UserInfoDTO queryTips(Map<String,Object> map);
    Integer getTips(@Param("type") Integer type,@Param("userId") String userId,@Param("storeUsers") List<StoreUserDTO> storeUsers);

    Integer getSleepingCustomerTips();

    int setFollow(@Param("clueId") String clueId,@Param("phaseStatus") Integer phaseStatus,@Param("mcId") String mcId);
}
