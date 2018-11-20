package com.dangjia.acg.mapper.member;

import com.dangjia.acg.modle.member.Customer;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/**
 * 客服表dao层
 * @Description: TODO
 * @author: qyx
 * @date: 2018-11-9 10:22:23
 */
@Repository
public interface ICustomerMapper extends Mapper<Customer> {

}

