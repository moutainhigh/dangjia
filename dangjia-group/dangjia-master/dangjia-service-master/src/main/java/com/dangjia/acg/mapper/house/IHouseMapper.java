package com.dangjia.acg.mapper.house;

import com.dangjia.acg.dto.house.*;
import com.dangjia.acg.dto.repair.HouseProfitSummaryDTO;
import com.dangjia.acg.dto.repair.RepairMendDTO;
import com.dangjia.acg.modle.house.House;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: Ronalcheng
 * Date: 2018/10/31 0031
 * Time: 17:09
 */
@Repository
public interface IHouseMapper extends Mapper<House> {

    List<House> getStatisticsByDate(@Param("start") Date start, @Param("end") Date end);

    HouseDTO startWorkPage(@Param("houseId") String houseId);

    /**
     * 查询房子信息
     * @param houseId
     * @return
     */
    HouseDTO getHouseDetailByHouseId(@Param("houseId") String houseId);

    /**
     * 查询房子订单信息
     * @param houseId 房子信息
     * @param workerTypeId 订单类型（1设计，2精算）
     * @return
     */
    List<HouseOrderDetailDTO> getBudgetOrderDetailByHouseId(@Param("houseId") String houseId,
                                                            @Param("workerTypeId") String workerTypeId);


    /**
     * 查询房子订单信息
     * @param houseId 房子信息
     * @param workerTypeId 订单类型（1设计，2精算）
     * @return
     */
    List<HouseOrderDetailDTO> getBudgetOrderDetailByInFo(@Param("houseId") String houseId,
                                                            @Param("workerTypeId") String workerTypeId,
                                                            @Param("type") Integer type);




    int queryTestNumber(@Param("id") String id,
                        @Param("houseId") String houseId,
                        @Param("workerId") String workerId);

    int queryArrNumber(@Param("id") String id);
    /**
     * 判断当前订单是否为已退款状态
     * @param houseId
     * @param workerTypeId
     * @return
     */
    List<HouseOrderDetailDTO> getBudgetOrderNewInfo(@Param("houseId") String houseId,@Param("workerTypeId") String workerTypeId);

    /**
     * 查询订单是否退货退款
     * @param cityId
     * @param workerTypeId
     * @return
     */
    List<HouseOrderDetailDTO> selectDesignProductList(@Param("cityId") String cityId, @Param("workerTypeId") String workerTypeId,
                                                      @Param("serviceTypeId") String serviceTypeId);

    /**
     * 查询是否有待处理的补差价订单
     * @param houseId
     * @param workerTypeId
     * @return
     */
    List<HouseOrderDetailDTO> getBudgetDifferenceOrder(@Param("houseId") String houseId, @Param("workerTypeId") String workerTypeId);
    /***
     * 根据房子装修状态查询所有的房子
     * @param visitState 0待确认开工,1装修中,2休眠中,3已完工
     * @return
     */
    List<House> getAllHouseByVisitState(@Param("visitState") Integer visitState);

    List<House> getByLikeAddress(@Param("storefrontId") String storefrontId,@Param("cityId") String cityId,@Param("likeAddress") String likeAddress,@Param("startDate") String startDate, @Param("endDate") String endDate);

    List<DesignDTO> getDesignList(@Param("designerType") int designerType,
                                  @Param("cityKey") String cityKey,
                                  @Param("searchKey") String searchKey,
                                  @Param("workerKey") String workerKey,
                                  @Param("dataStatus") String dataStatus,
                                  @Param("flag") int flag,
                                  @Param("userId") String userId);

    List<House> getSameLayout(@Param("cityId") String cityId, @Param("villageId") String villageId,
                              @Param("minSquare") Double minSquare, @Param("maxSquare") Double maxSquare, @Param("houseType") Integer houseType);

    List<House> getSameLayoutDistance(@Param("cityId") String cityId, @Param("locationx") String locationx, @Param("locationy") String locationy,
                              @Param("minSquare") Double minSquare, @Param("maxSquare") Double maxSquare, @Param("villageId") String villageId);

    List<House> getReferenceBudget(@Param("cityId") String cityId, @Param("villageId") String villageId, @Param("houseType") String houseType,
                                   @Param("minSquare") Double minSquare, @Param("maxSquare") Double maxSquare);


    List<HouseListDTO> getActuaryAll(@Param("cityId") String cityId,
                                     @Param("budgetOk") String budgetOk,
                                     @Param("searchKey") String searchKey,
                                     @Param("workerKey") String workerKey,
                                     @Param("dataStatus") String dataStatus,
                                     @Param("userId") String userId,
                                     @Param("budgetStatus") String budgetStatus,
                                     @Param("decorationType") String  decorationType);

    List<HouseListDTO> getHouseList(@Param("cityKey")  String cityKey,@Param("userKey") String userKey,@Param("memberId") String memberId, @Param("visitState") Integer visitState, @Param("startDate") String startDate, @Param("endDate") String endDate, @Param("orderBy") String orderBy, @Param("searchKey") String searchKey);

    List<House> getHouseListLikeSearchKey(@Param("cityKey")  String cityKey,@Param("visitState") Integer visitState, @Param("searchKey") String searchKey,@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("supKey") String supKey);

    Date getHouseDateByMemberId(@Param("memberId") String memberId);

    List<RepairMendDTO> getRepairMend(@Param("houseId") String houseId, @Param("productId") String productId);

    int getBuildDay(@Param("houseId") String houseId);


    List<DesignDTO> getHouseProfitList(@Param("cityId")String cityId ,@Param("villageId")  String villageId,@Param("visitState") String visitState, @Param("searchKey") String searchKey);

    List<HouseProfitSummaryDTO> getHouseProfitSummary(@Param("houseId") String houseId);

    List<House> getRecommended(@Param("latitude")  String latitude,@Param("longitude") String longitude, @Param("limit") Integer limit);


    House queryPromotionListHouse(@Param("memberId") String memberId);


    UserInfoDateDTO getUserList(@Param("memberId") String memberId);

    UserInfoDateDTO getDesignListInfo(@Param("houseId") String houseId);

    Date getModifyDate(@Param("taskId") String taskId);

}
