package com.dangjia.acg.mapper.user;

import com.dangjia.acg.modle.user.MainUser;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;


@Repository
public interface IStoreUserMapper extends Mapper<MainUser> {
}