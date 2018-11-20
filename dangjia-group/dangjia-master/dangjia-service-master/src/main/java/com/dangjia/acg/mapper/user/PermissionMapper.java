package com.dangjia.acg.mapper.user;

import com.dangjia.acg.dto.user.PermissionVO;
import com.dangjia.acg.modle.user.Permission;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface PermissionMapper extends Mapper<Permission> {

    /**
     * 查找所有权限数据
     * @return
     */
	List<PermissionVO> findAll();

	/**
	 * 查找所有子节点
	 * @param pid
	 * @return
	 */
	List<PermissionVO> findChildPerm(@Param("pid")String pid);

	/**
	 * 查询权限树列表
	 * @return
	 */
	List<PermissionVO> findPerms();

	/**
	 * 根据角色id获取权限数据
	 * @param roleId
	 * @return
	 */
	List<Permission> findPermsByRole(@Param("roleId")String roleId);

	List<PermissionVO> getUserPerms(@Param("userId")String userId,@Param("source")Integer source);
}