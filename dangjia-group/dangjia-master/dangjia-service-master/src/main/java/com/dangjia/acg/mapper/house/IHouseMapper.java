package com.dangjia.acg.mapper.house;

import com.alipay.api.domain.OrderDetail;
import com.dangjia.acg.dto.house.DesignDTO;
import com.dangjia.acg.dto.house.HouseDTO;
import com.dangjia.acg.dto.house.HouseListDTO;
import com.dangjia.acg.dto.repair.HouseProfitSummaryDTO;
import com.dangjia.acg.dto.repair.RepairMendDTO;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.order.DjOrder;
import com.dangjia.acg.modle.order.DjOrderDetail;
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

    /***
     * 根据房子装修状态查询所有的房子
     * @param visitState 0待确认开工,1装修中,2休眠中,3已完工
     * @return
     */
    List<House> getAllHouseByVisitState(@Param("visitState") Integer visitState);

    List<House> getByLikeAddress(@Param("storefrontId") String storefrontId,@Param("cityId") String cityId,@Param("likeAddress") String likeAddress,@Param("startDate") String startDate, @Param("endDate") String endDate);

    List<DesignDTO> getDesignList(@Param("designerType") int designerType, @Param("cityKey") String cityKey,@Param("searchKey") String searchKey,@Param("workerKey") String workerKey, @Param("dataStatus") String dataStatus);

    List<House> getSameLayout(@Param("cityId") String cityId, @Param("villageId") String villageId,
                              @Param("minSquare") Double minSquare, @Param("maxSquare") Double maxSquare, @Param("houseType") Integer houseType);

    List<House> getSameLayoutDistance(@Param("cityId") String cityId, @Param("locationx") String locationx, @Param("locationy") String locationy,
                              @Param("minSquare") Double minSquare, @Param("maxSquare") Double maxSquare, @Param("villageId") String villageId);

    List<House> getReferenceBudget(@Param("cityId") String cityId, @Param("villageId") String villageId, @Param("houseType") String houseType,
                                   @Param("minSquare") Double minSquare, @Param("maxSquare") Double maxSquare);


    List<HouseListDTO> getActuaryAll(@Param("cityId")String cityId ,@Param("budgetOk") String budgetOk, @Param("searchKey") String searchKey,@Param("workerKey") String workerKey, @Param("dataStatus") String dataStatus);

    List<HouseListDTO> getHouseList(@Param("cityKey")  String cityKey,@Param("userKey") String userKey,@Param("memberId") String memberId, @Param("visitState") Integer visitState, @Param("startDate") String startDate, @Param("endDate") String endDate, @Param("orderBy") String orderBy, @Param("searchKey") String searchKey);

    List<House> getHouseListLikeSearchKey(@Param("cityKey")  String cityKey,@Param("visitState") Integer visitState, @Param("searchKey") String searchKey,@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("supKey") String supKey);

    Date getHouseDateByMemberId(@Param("memberId") String memberId);

    List<RepairMendDTO> getRepairMend(@Param("houseId") String houseId, @Param("productId") String productId);

    int getBuildDay(@Param("houseId") String houseId);


    List<DesignDTO> getHouseProfitList(@Param("cityId")String cityId ,@Param("villageId")  String villageId,@Param("visitState") String visitState, @Param("searchKey") String searchKey);

    List<HouseProfitSummaryDTO> getHouseProfitSummary(@Param("houseId") String houseId);

    List<House> getRecommended(@Param("latitude")  String latitude,@Param("longitude") String longitude, @Param("limit") Integer limit);


    House queryPromotionListHouse(@Param("memberId") String memberId);

    /**
     * 根据房屋ID查对应的精算订单信息
     * @return
     */
    DjOrder getOrderInfo(@Param("houseId") String houseId);

    //根据房子ID查询对应的订单详情
    List<DjOrderDetail> getOrderDetailInfoList(@Param("houseId") String houseId);

    //修改商品订单对应的信息
    void updateOrderDetail(DjOrderDetail orderDetail);

    //修改商品订单表是否可付款状态
    void updateOrder(@Param("orderId") String orderId);
}
