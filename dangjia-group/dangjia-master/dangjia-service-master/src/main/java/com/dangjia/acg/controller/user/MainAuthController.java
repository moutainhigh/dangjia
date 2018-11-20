package com.dangjia.acg.controller.user;

import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.api.user.MainAuthAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.exception.BaseException;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.user.MainUser;
import com.dangjia.acg.modle.user.Permission;
import com.dangjia.acg.modle.user.Role;
import com.dangjia.acg.service.user.MainAuthService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@RestController
public class MainAuthController implements MainAuthAPI {
	private static final Logger logger = LoggerFactory
			.getLogger(MainAuthController.class);
	@Autowired
    private MainAuthService mainAuthService;
	/****
	 * 注入配置
	 */
	@Autowired
	private RedisClient redisClient;

	@Override
	@ApiMethod
	public ServerResponse selectSysAll(HttpServletRequest request) {
		return mainAuthService.selectSysAll();
	}
	@Override
	@ApiMethod
	public ServerResponse selectDomainAll(HttpServletRequest request) {
		return mainAuthService.selectDomainAll();
	}
	/**
	 * 添加权限
	 * @param permission
	 * @return ok/fail
	 */

	@Override
	@ApiMethod
	public ServerResponse addPermission(HttpServletRequest request,Permission permission) {
		logger.debug("新增权限--permission-" + permission);
		try {
			if (null != permission) {
				permission.setCreateDate(new Date());

				mainAuthService.addPermission(permission);
				logger.debug("新增权限成功！-permission-" + permission);
			}
			return ServerResponse.createBySuccessMessage("ok");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("新增权限异常！", e);
			return ServerResponse.createByErrorMessage("新增权限异常！");
		}
	}

	/**
	 * 权限列表
	 * @return ok/fail
	 */
	@Override
	@ApiMethod
	public ServerResponse permList(HttpServletRequest request) {
		logger.debug("权限列表！");
		try {
			ServerResponse permList = mainAuthService.permList();
			logger.debug("权限列表查询=permList:" + permList);
			return permList;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("权限查询异常！", e);
			return ServerResponse.createByErrorMessage("权限查询异常！");
		}
	}

	/**
	 * 添加权限
	 * @param type [0：编辑；1：新增子节点权限]
	 * @param permission
	 * @return ModelAndView ok/fail
	 */
    @Override
	@ApiMethod
	public ServerResponse setPerm(HttpServletRequest request,int type, Permission permission) {
		logger.debug("设置权限--区分type-" + type + "【0：编辑；1：新增子节点权限】，权限--permission-"
				+ permission);
		try {
			if (null != permission) {
				Date date = new Date();
				if (0 == type) {
					permission.setModifyDate(date);
					//编辑权限
					this.mainAuthService.updatePerm(permission);
				} else if (1 == type) {
					permission.setCreateDate(date);
					//增加子节点权限
					this.mainAuthService.addPermission(permission);
				}
				logger.debug("设置权限成功！-permission-" + permission);
				return ServerResponse.createBySuccessMessage("ok");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("设置权限异常！", e);
		}
		return ServerResponse.createByErrorMessage("设置权限出错，请您稍后再试");
	}

	/**
	 * 获取权限
	 * @param id
	 * @return
	 */
	@Override
	@ApiMethod
	public ServerResponse getPerm(HttpServletRequest request,String id) {
		logger.debug("获取权限--id-" + id);
		try {
			if (!StringUtils.isEmpty(id)) {
				ServerResponse perm = this.mainAuthService.getPermission(id);
				logger.debug("获取权限成功！-permission-" + perm);
				return perm;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("获取权限异常！", e);
		}
		return ServerResponse.createByErrorMessage("获取权限异常");
	}

	/**
	 * 删除权限
	 * @param id
	 * @return
	 */
	@Override
	@ApiMethod
	public ServerResponse del(HttpServletRequest request,String id) {
		logger.debug("删除权限--id-" + id);
		try {
			if (!StringUtils.isEmpty(id)) {
				return this.mainAuthService.delPermission(id);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("删除权限异常！", e);
		}
		return ServerResponse.createByErrorMessage("删除权限出错，请您稍后再试");
	}


	/**
	 * 角色列表
	 * @return ok/fail
	 */
	@Override
	@ApiMethod
	public ServerResponse getRoleList(HttpServletRequest request) {
		logger.debug("角色列表！");
		ServerResponse roleList=null;
		try {
			roleList = mainAuthService.roleList();
			logger.debug("角色列表查询=roleList:" + roleList);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("角色查询异常！", e);
			return ServerResponse.createByErrorMessage("角色查询异常");
		}
		return roleList;
	}

	/**
	 * 查询权限树数据
	 * @return PermTreeDTO
	 */
	@Override
	@ApiMethod
	public ServerResponse findPerms(HttpServletRequest request) {
		logger.debug("权限树列表！");
		ServerResponse pvo = null;
		try {
			pvo = mainAuthService.findPerms();
			//生成页面需要的json格式
			logger.debug("权限树列表查询=pvo:" + pvo);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("权限树列表查询异常！", e);
			return ServerResponse.createByErrorMessage("权限树列表查询异常");
		}
		return pvo;
	}

	/**
	 * 添加角色并授权
	 * @return PermTreeDTO
	 */
	@Override
	@ApiMethod
	public ServerResponse addRole(HttpServletRequest request,String permIds, Role role) {
		logger.debug("添加角色并授权！角色数据role："+role+"，权限数据permIds："+permIds);
		try {
			if(StringUtils.isEmpty(permIds)){
				return ServerResponse.createByErrorMessage("未授权，请您给该角色授权");
			}
			if(null == role){
				return ServerResponse.createByErrorMessage("请您填写完整的角色数据");
			}
			role.setCreateDate(new Date());
			return mainAuthService.addRole(role,permIds);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("添加角色并授权！异常！", e);
		}
		return ServerResponse.createByErrorMessage("操作错误，请您稍后再试");
	}
	/**
	 * 根据id查询角色
	 * @return PermTreeDTO
	 */
	@Override
	@ApiMethod
	public ServerResponse updateRole(HttpServletRequest request,String id) {
		logger.debug("根据id查询角色id："+id);
		try {
			if(null==id){
				return ServerResponse.createByErrorMessage("请求参数有误，请您稍后再试");
			}
			ServerResponse rvo=this.mainAuthService.findRoleAndPerms(id);
			return rvo;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("添加角色并授权！异常！", e);
			return ServerResponse.createByErrorMessage("添加角色并授权！异常！");
		}
	}

	/**
	 * 更新角色并授权
	 * @return PermTreeDTO
	 */
	@Override
	@ApiMethod
	public ServerResponse setRole(HttpServletRequest request,String permIds, Role role) {
		logger.debug("更新角色并授权！角色数据role："+role+"，权限数据permIds："+permIds);
		try {
			if(StringUtils.isEmpty(permIds)){
				return ServerResponse.createByErrorMessage("未授权，请您给该角色授权");
			}
			if(null == role){
				return ServerResponse.createByErrorMessage("请您填写完整的角色数据");
			}
			role.setModifyDate(new Date());
			return mainAuthService.updateRole(role,permIds);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("更新角色并授权！异常！", e);
		}
		return ServerResponse.createByErrorMessage("更新角色并授权！异常！");
	}

	/**
	 * 删除角色以及它对应的权限
	 * @param id
	 * @return
	 */
	@Override
	@ApiMethod
	public ServerResponse delRole(HttpServletRequest request,String id) {
		logger.debug("删除角色以及它对应的权限--id-" + id);
		try {
			if (!StringUtils.isEmpty(id)) {
				return this.mainAuthService.delRole(id);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("删除角色异常！", e);
		}
		return ServerResponse.createByErrorMessage("删除角色出错，请您稍后再试");
	}

	/**
	 * 查找所有角色
	 * @return
	 */
	@Override
	@ApiMethod
	public ServerResponse getRoles(HttpServletRequest request) {
		logger.debug("查找所有角色!");
		try {
			return this.mainAuthService.getRoles();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("查找所有角色异常！", e);
		}
		return ServerResponse.createByErrorMessage("查找所有角色异常！");
	}


	/**
	 * 根据用户id查询权限树数据
	 * @return PermTreeDTO
	 */
	@Override
	@ApiMethod
	public ServerResponse getUserPerms(HttpServletRequest request) {
		logger.debug("根据用户id查询限树列表！");
		ServerResponse pvo = null;
		String userID = request.getParameter(Constants.USERID);
		MainUser existUser= redisClient.getCache(Constants.USER_KEY+userID,MainUser.class);
		if(null==existUser){
			throw new BaseException(ServerCode.THE_LANDING_TIME_PLEASE_LAND_AGAIN, ServerCode.THE_LANDING_TIME_PLEASE_LAND_AGAIN.getDesc());
		}
		try {
			pvo = mainAuthService.getUserPerms(existUser.getId());
			//生成页面需要的json格式
			logger.debug("根据用户id查询限树列表查询=pvo:" + pvo);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("根据用户id查询权限树列表查询异常！", e);
			return ServerResponse.createByErrorMessage("根据用户id查询权限树列表查询异常！");
		}
		return pvo;
	}
}
