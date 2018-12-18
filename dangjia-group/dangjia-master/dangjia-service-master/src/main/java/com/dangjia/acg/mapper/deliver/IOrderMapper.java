package com.dangjia.acg.mapper.deliver;

import com.dangjia.acg.dto.deliver.OrderDTO;
import com.dangjia.acg.modle.deliver.Order;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * author: zmj
 * Date: 2018/11/9 0009
 * Time: 13:59
 */
@Repository
public interface IOrderMapper extends Mapper<Order> {

    List<OrderDTO> orderList(@Param("houseId")String houseId);
}
