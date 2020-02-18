package com.dangjia.acg.mapper.storefront;


import com.dangjia.acg.modle.deliver.Order;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

@Repository
public interface IStoreOrderMapper extends Mapper<Order> {

}
