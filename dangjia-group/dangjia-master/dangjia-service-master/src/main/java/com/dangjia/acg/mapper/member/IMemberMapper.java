package com.dangjia.acg.mapper.member;

import com.dangjia.acg.modle.member.Member;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 用户表dao层
 * @Description: TODO
 * @author: qyx
 * @date: 2018-9-19下午4:22:23
 */
@Repository
public interface IMemberMapper extends Mapper<Member> {
    /**通过评价表的houseId获得大管家*/
    Member getSupervisor(@Param("houseId") String houseId);

    Member getUser(Member member);

    List<Map<String,Object>> getMemberList();
}

