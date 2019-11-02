package com.dangjia.acg.mapper.sale;

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
public interface IBillMemberMapper extends Mapper<Member> {

}

