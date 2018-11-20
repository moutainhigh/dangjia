package com.dangjia.acg.mapper.user;

import com.dangjia.acg.modle.user.RolePermission;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface RolePermissionMapper  extends Mapper<RolePermission> {

	List<RolePermission> findByRole(@Param("roleId")String roleId);
}