package com.dangjia.acg.mapper.user;

import com.dangjia.acg.dto.user.UserRoleDTO;
import com.dangjia.acg.dto.user.UserRolesVO;
import com.dangjia.acg.dto.user.UserSearchDTO;
import com.dangjia.acg.modle.user.MainUser;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface UserMapper extends Mapper<MainUser> {

    /**
     * 分页查询用户数据
     *
     * @return
     */
    List<UserRoleDTO> getUsers(@Param("cityKey") String cityKey,@Param("userKey") String userKey,@Param("userSearch") UserSearchDTO userSearch, @Param("isJob") Integer isJob);

    /**
     * 删除用户
     *
     * @param id
     * @param isDel
     * @return
     */
    int setDelUser(@Param("id") String id, @Param("isDel") boolean isDel,
                   @Param("insertUid") String insertUid);

    /**
     * 查询当前坐席的用户
     *
     * @return
     */
    MainUser getUserByReceive(@Param("cityKey") String cityKey,@Param("type") Integer type);

    /**
     * 查询用户及对应的角色
     *
     * @param id
     * @return
     */
    UserRolesVO getUserAndRoles(@Param("id") String id);

    /**
     * 根据用户名和密码查找用户
     *
     * @param username
     * @param password
     * @return
     */
    MainUser findUser(@Param("username") String username,
                      @Param("password") String password);

    /**
     * 根据手机号获取用户数据
     *
     * @param mobile
     * @return
     */
    MainUser findUserByMobile(@Param("mobile") String mobile);

    /**
     * 根据用户名获取用户数据
     *
     * @param username
     * @return
     */
    MainUser findUserByName(@Param("username") String username);

    /**
     * 修改用户密码
     *
     * @param id
     * @param password
     * @return
     */
    int updatePwd(@Param("id") String id, @Param("password") String password);

    MainUser getNameById(@Param("id") String id);

    int insertMemberId(@Param("mobile") String mobile);
}