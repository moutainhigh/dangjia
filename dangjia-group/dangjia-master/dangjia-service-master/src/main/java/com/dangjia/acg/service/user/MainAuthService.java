package com.dangjia.acg.service.user;

import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.user.PermissionVO;
import com.dangjia.acg.dto.user.RoleVO;
import com.dangjia.acg.mapper.user.*;
import com.dangjia.acg.modle.user.Permission;
import com.dangjia.acg.modle.user.Role;
import com.dangjia.acg.modle.user.RolePermission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
@Service
public class MainAuthService {
	private static final Logger logger = LoggerFactory
			.getLogger(MainAuthService.class);
	@Autowired
	private PermissionMapper permissionMapper;
	@Autowired
	private RoleMapper roleMapper;
	@Autowired
	private RolePermissionMapper rolePermissionMapper;

	/****
	 * 注入配置
	 */
	@Autowired
	private RedisClient redisClient;
	@Autowired
	private SysMapper sysMapper;


	@Autowired
	private DomainMapper domainMapper;

	public ServerResponse selectSysAll() {
		List list= this.sysMapper.selectAll();
		return ServerResponse.createBySuccess("ok",list);
	}

	public ServerResponse selectDomainAll() {
		List list= this.domainMapper.selectAll();
		return ServerResponse.createBySuccess("ok",list);
	}

	public ServerResponse addPermission(Permission permission) {
		this.permissionMapper.insert(permission);
		return ServerResponse.createBySuccess("添加成功",permission.getId());
	}

	
	public ServerResponse permList() {
		List<PermissionVO> list= this.permissionMapper.findAll();
		return ServerResponse.createBySuccess("ok",list);
	}

	 public ServerResponse updatePerm(Permission permission) {
		  this.permissionMapper.updateByPrimaryKeySelective(permission);
		 return ServerResponse.createBySuccess("更新成功",permission.getId());
	}

	 public ServerResponse getPermission(String id) {
		 Permission permission= this.permissionMapper.selectByPrimaryKey(id);
		 return ServerResponse.createBySuccess("ok",permission);
	}

	 public ServerResponse delPermission(String id) {
		//查看该权限是否有子节点，如果有，先删除子节点
		List<PermissionVO> childPerm = this.permissionMapper.findChildPerm(id);
		if(null != childPerm && childPerm.size()>0){
			return ServerResponse.createByErrorMessage("删除失败，请您先删除该权限的子节点");
		}
		if(this.permissionMapper.deleteByPrimaryKey(String.valueOf(id))>0){
			return ServerResponse.createBySuccessMessage("删除成功");
		}else{
			return ServerResponse.createByErrorMessage("删除失败，请您稍后再试");
		}
	}

	
	public ServerResponse roleList() {
		List<Role> list=this.roleMapper.findList();
		return ServerResponse.createBySuccess("ok",list);
	}

	 public ServerResponse findPerms() {
		 List<PermissionVO> list=this.permissionMapper.findPerms();
		 return ServerResponse.createBySuccess("ok",list);
	}

	
	@Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.DEFAULT,timeout=30000,rollbackFor={RuntimeException.class, Exception.class})
	public ServerResponse addRole(Role role, String permIds) {
		Example example =new Example(Role.class);
		example.createCriteria().andEqualTo("roleName",role.getRoleName());
		List list=roleMapper.selectByExample(example);
		if(list.size()>0){
			return ServerResponse.createByErrorMessage("角色名称不能重复");
		}
		this.roleMapper.insert(role);
		String roleId=role.getId();
		String[] arrays=permIds.split(",");
		logger.debug("权限id =arrays="+arrays.toString());
		setRolePerms(roleId, arrays);
		return ServerResponse.createBySuccessMessage("新增成功");
	}

	 public ServerResponse findRoleAndPerms(String id) {
		 RoleVO roleVO= this.roleMapper.findRoleAndPerms(id);
		 //角色下的权限
//		 List<RolePermission> rpks=roleVO.getRolePerms();
		 //获取全部权限数据
//		 List<PermissionVO> pvos=this.permissionMapper.findPerms();
//		 for (RolePermission rpk : rpks) {
//			 //设置角色下的权限checked状态为：true
//			 for (PermissionVO pvo : pvos) {
//				 if(String.valueOf(rpk.getPermitId()).equals(String.valueOf(pvo.getId()))){
//					 pvo.setChecked(true);
//				 }
//			 }
//		 }
//		 roleVO.setPvos(pvos);
		 return ServerResponse.createBySuccess("ok",roleVO);
	}

	
	@Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.DEFAULT,timeout=30000,rollbackFor={RuntimeException.class, Exception.class})
	public ServerResponse updateRole(Role role, String permIds) {
		String roleId=role.getId();
		Example example =new Example(Role.class);
		example.createCriteria().andEqualTo("roleName",role.getRoleName()).andNotEqualTo(Role.ID,roleId);
		List list=roleMapper.selectByExample(example);
		if(list.size()>0){
			return ServerResponse.createByErrorMessage("角色名称不能重复");
		}
		String[] arrays=permIds.split(",");
		logger.debug("权限id =arrays="+arrays.toString());
		//1，更新角色表数据；
		int num=this.roleMapper.updateByPrimaryKeySelective(role);
		if(num<1){
			//事务回滚
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return ServerResponse.createByErrorMessage("操作失败");
		}
		//2，删除原角色权限；
		batchDelRolePerms(roleId);
		//3，添加新的角色权限数据；
		setRolePerms(roleId, arrays);
		return ServerResponse.createBySuccessMessage("ok");
	}



	
	@Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.DEFAULT,timeout=30000,rollbackFor={RuntimeException.class, Exception.class})
	public ServerResponse delRole(String id) {
		//1.删除角色对应的权限
		batchDelRolePerms(id);
		//2.删除角色
		int num=this.roleMapper.deleteByPrimaryKey(id);
		if(num<1){
			//事务回滚
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return ServerResponse.createByErrorMessage("操作失败");
		}
		return ServerResponse.createBySuccessMessage("ok");
	}

	
	public ServerResponse getRoles() {
		List<Role> list= this.roleMapper.getRoles();
		return ServerResponse.createBySuccess("ok",list);
	}

	
	public List<Role> getRoleByUser(String userId) {
		List<Role> list= this.roleMapper.getRoleByUserId(userId);
		return list;
	}

	
	public List<Permission> findPermsByRoleId(String id) {
		List<Permission> list= this.permissionMapper.findPermsByRole(id);
		return list;
	}

	
	public ServerResponse getUserPerms(String userID,String id) {
//		Integer source= redisClient.getCache("sysSource:"+userID,Integer.class);
//		if(source==null){
//			source=1;
//		}
		Integer source=null;
		List<PermissionVO> list= this.permissionMapper.getUserPerms(id,source);
		return ServerResponse.createBySuccess("ok",list);
	}

	/**
	 * 批量删除角色权限中间表数据
	 * @param roleId
	 */
	private void batchDelRolePerms(String roleId) {
		List<RolePermission> rpks=this.rolePermissionMapper.findByRole(roleId);
		if(null!=rpks && rpks.size()>0){
			for (RolePermission rpk : rpks) {
				this.rolePermissionMapper.deleteByPrimaryKey(rpk);
			}
		}
	}

	/**
	 * 给当前角色设置权限
	 * @param roleId
	 * @param arrays
	 */
	private void setRolePerms(String roleId, String[] arrays) {
		for (String permid : arrays) {
			RolePermission rpk=new RolePermission();
			rpk.setRoleId(roleId);
			rpk.setPermitId(permid);
			this.rolePermissionMapper.insert(rpk);
		}
	}
}
