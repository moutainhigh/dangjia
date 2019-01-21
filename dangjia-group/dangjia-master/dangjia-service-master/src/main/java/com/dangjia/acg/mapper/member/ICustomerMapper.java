package com.dangjia.acg.mapper.member;

import com.dangjia.acg.modle.member.Customer;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 客服表dao层
 *
 * @Description: TODO
 * @author: qyx
 * @date: 2018-11-9 10:22:23
 */
@Repository
public interface ICustomerMapper extends Mapper<Customer> {

    /**
     * 根据业主 查所有
     * @return
     */
    List<Customer> getAllCustomer();


    Customer getCustomerByMemberId(@Param("memberId") String memberId);

    /**
     * 根据业主id查询
     * @param memberId
     * @param stage -1 表示 忽略该字段
     * @return
     */
    Customer getCustomerByMemberIdAndStage(@Param("memberId") String memberId,@Param("stage") Integer stage);
}

