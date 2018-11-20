package com.dangjia.acg.mapper.user;

import com.dangjia.acg.modle.user.MainDomain;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

@Repository
public interface DomainMapper extends Mapper<MainDomain> {

}