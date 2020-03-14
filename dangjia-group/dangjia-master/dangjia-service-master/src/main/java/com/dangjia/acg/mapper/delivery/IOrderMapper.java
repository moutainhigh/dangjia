package com.dangjia.acg.mapper.delivery;

import com.dangjia.acg.dto.deliver.BudgetOrderDTO;
import com.dangjia.acg.dto.deliver.BudgetOrderItemDTO;
import com.dangjia.acg.dto.delivery.HouseFlowDataDTO;
import com.dangjia.acg.dto.delivery.OrderStorefrontDTO;
import com.dangjia.acg.dto.house.HouseOrderDetailDTO;
import com.dangjia.acg.dto.member.MemberDTO;
import com.dangjia.acg.dto.member.WorkerTypeDTO;
import com.dangjia.acg.modle.deliver.Order;
import com.dangjia.acg.modle.deliver.OrderItem;
import com.dangjia.acg.modle.member.Member;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

/**
 * author: zmj
 * Date: 2018/11/9 0009
 * Time: 13:59
 */
@Repository
public interface IOrderMapper extends Mapper<Order> {
    /**订单号*/
    List<Order> byBusinessOrderNumber(@Param("businessOrderNumber")String businessOrderNumber);

    List<Order> byBusinessOrderNumberAndOrderStatus(@Param("businessOrderNumber")String businessOrderNumber,@Param("orderStatus")String orderStatus);

    /**查询人工订单*/
    Order getWorkerOrder(@Param("houseId")String houseId,@Param("workerTypeId")String workerTypeId);
    /**查询人工订单*/
    Order getStorefontOrder(@Param("storefontId")String storefontId,@Param("parentOrderId")String parentOrderId,@Param("productType")Integer productType);


    /**
     * 查询房子设计、精算的订单信息
     * @param houseId 房子信息
     * @param orderSource 1工序订单，4补差价订单
     * @param state 处理状态  1刚生成(可编辑),2去支付(不修改),3已支付,4已取消"
     * @return
     */
    BudgetOrderDTO getOrderInfoByHouseId(@Param("houseId") String houseId, @Param("orderSource") String orderSource,@Param("state") String state);

    /**
     * 查询房子设计、精算的订单信息
     * @param orderId 房子信息
     * @return
     */
    List<BudgetOrderItemDTO> getOrderInfoItemList(@Param("orderId") String orderId);

    /**
     * 查询所有的设计费用
     * @param orderId
     * @return
     */
    Double getDesgionTotalMoney(@Param("orderId") String orderId);

    Order getGroupBooking(@Param("orderId") String orderId);
    //未支付完成的订单
    Integer selectCountOrderByMemberId(@Param("memberId") String memberId);

    //查询已支付但未要货的单
    Integer selectOrderItemByMemberId(@Param("memberId") String memberId);
    //判断是否有待发货的单
    Integer selectOrderSplitItemByMemberId(@Param("memberId") String memberId);
    //判断是否有待收货的单
    Integer selectOrderSplitDeliverByMemberId(@Param("memberId") String memberId);
    //判断是否存在申请退款，待处理的单
    Integer selectMendOrderByMemberId(@Param("memberId") String memberId);
    //判断是否有正在处理中的退款单
    Integer selectMendDeliverByMemberId(@Param("memberId") String memberId);

    /**
     * 待付款 数量
     */
    Integer queryDeliverOrderObligation(@Param("memberId") String memberId,
                                        @Param("houseId") String houseId);

    /**
     * 待发货数量
     * @param cityId
     * @param houseId
     * @param memberId
     * @return
     */
    Integer queryAppHairOrderList(@Param("cityId") String cityId,
                                  @Param("houseId") String houseId,
                                  @Param("memberId") String memberId);

    Integer queryAppOrderList(@Param("cityId") String cityId,
                              @Param("memberId") String memberId,
                              @Param("houseId") String houseId);


    List<MemberDTO> queryActivityPurchaseRotation(@Param("activityType") Integer activityType,
                                                  @Param("storeActivityProductId") String storeActivityProductId);

    List<HouseFlowDataDTO> queryApplyDec();

    List<HouseFlowDataDTO> queryApplyPayState(@Param("houseId") String houseId);

    /**
     * 查询装修状态
     * @param houseId
     * @return
     */
    List<WorkerTypeDTO> queryType(@Param("houseId") String houseId);

}

