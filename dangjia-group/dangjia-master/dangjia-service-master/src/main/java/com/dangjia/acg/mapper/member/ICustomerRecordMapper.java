package com.dangjia.acg.mapper.member;

import com.dangjia.acg.modle.member.CustomerRecord;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 客服图片表dao层
 *
 * @Description: TODO
 * @author: qyx
 * @date: 2018-9-19下午4:22:23
 */
@Repository
public interface ICustomerRecordMapper extends Mapper<CustomerRecord> {

    /**
     * 根据业主id查询 ，业主id 为 null 查所有
     *
     * @param memberId
     * @return
     */
    List<CustomerRecord> getCustomerRecordByMemberId(@Param("memberId") String memberId);
}

