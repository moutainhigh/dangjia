package com.dangjia.acg.mapper.repair;

import com.dangjia.acg.modle.repair.MendTypeRole;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/**
 * author: Ronalcheng
 * Date: 2019/1/16 0016
 * Time: 15:11
 */
@Repository
public interface IMendTypeRoleMapper extends Mapper<MendTypeRole> {

    MendTypeRole getByType(@Param("type")Integer type);
}
