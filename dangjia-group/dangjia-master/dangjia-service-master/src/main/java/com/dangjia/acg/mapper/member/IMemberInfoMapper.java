package com.dangjia.acg.mapper.member;

import com.dangjia.acg.modle.member.MemberInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 用户表dao层
 * @Description: TODO
 * @author: qyx
 * @date: 2018-9-19下午4:22:23
 */
@Repository
public interface IMemberInfoMapper extends Mapper<MemberInfo> {


    /**
     * 获取用户app应用角色
     * @param memberId
     * @return
     */
    List<MemberInfo> queryUserRole(@Param("memberId") String memberId);
}

