package com.dangjia.acg.mapper.pay;

import com.dangjia.acg.modle.deliver.Order;
import com.dangjia.acg.modle.pay.BusinessOrder;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2018/11/8 0008
 * Time: 15:59
 */
@Repository
public interface IBusinessOrderMapper extends Mapper<BusinessOrder> {

    List<BusinessOrder> byMemberId(@Param("memberId")String memberId);

    BusinessOrder byNumber(@Param("number")String number);

    /**查询所有订单*/
    List<BusinessOrder> getAllBusinessOrder();
}
