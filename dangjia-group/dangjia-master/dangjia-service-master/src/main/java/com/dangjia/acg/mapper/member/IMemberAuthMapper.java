package com.dangjia.acg.mapper.member;

import com.dangjia.acg.modle.member.MemberAuth;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/**
 * 当家用户第三方认证表dao层
 */
@Repository
public interface IMemberAuthMapper extends Mapper<MemberAuth> {

    /**
     * 获取用户app应用角色
     * @param memberId
     * @return
     */
    MemberAuth queryUserRole(String memberId);

}

