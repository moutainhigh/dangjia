package com.dangjia.acg.mapper.user;

import com.dangjia.acg.dto.user.RoleVO;
import com.dangjia.acg.modle.user.Role;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface RoleMapper   extends Mapper<Role> {

    /**
     * 分页查询所有的角色列表
     * @return
     */
	List<Role> findList();

	/**
	 * 获取角色相关的数据
	 * @param id
	 * @return
	 */
	RoleVO findRoleAndPerms(@Param("id")String id);

	/**
	 * 根据用户id获取角色数据
	 * @param userId
	 * @return
	 */
	List<Role> getRoleByUserId(@Param("userId")String userId);

	List<Role> getRoles();

}