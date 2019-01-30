package com.dangjia.acg.mapper.deliver;

import com.dangjia.acg.modle.deliver.OrderSplit;
import com.dangjia.acg.modle.deliver.SplitDeliver;
import com.dangjia.acg.modle.pay.BusinessOrder;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2018/12/5 0005
 * Time: 14:25
 */
@Repository
public interface ISplitDeliverMapper extends Mapper<SplitDeliver> {

    /**授权大管家收货*/
    void supState(@Param("splitDeliverId") String splitDeliverId);
    /**
     *查询供应商发货订单
     * @param applyState 0申请中；1不通过；2通过
     * @return
     */
    List<SplitDeliver> getAllSplitDeliver(@Param("applyState")Integer applyState,@Param("beginDate")String beginDate,@Param("endDate")String endDate);

    /**
     *  本周发货单
     * @return
     */
    List<SplitDeliver> getAllCurWeek(@Param("applyState")Integer applyState,@Param("beginDate")String beginDate,@Param("endDate")String endDate);
}
