package com.dangjia.acg.mapper.recommend;


import com.dangjia.acg.modle.member.Member;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/**
 * 用户表dao层
 * luof
 */
@Repository
public interface MemberMapper extends Mapper<Member> {
}
