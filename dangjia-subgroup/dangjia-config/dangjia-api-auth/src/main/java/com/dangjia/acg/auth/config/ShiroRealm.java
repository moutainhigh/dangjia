package com.dangjia.acg.auth.config;

import com.dangjia.acg.common.constants.Constants;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 该类仅作为授权管理过程，不论何时都为成功
 */
public class ShiroRealm extends AuthorizingRealm {

    private Logger logger = LoggerFactory.getLogger(ShiroRealm.class);

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {

        SimpleAuthorizationInfo info = ShiroUtil.getShiroSession(Constants.AUTH_SESSION, SimpleAuthorizationInfo.class);
        if (null == info) {
            throw new UnauthenticatedException("授权失败");
//            info = new SimpleAuthorizationInfo();
//
//            List<Resource> menu = dataFilterServiceAPI.filterUsersMenu();
//
//            for (Resource r: menu) {
//                if(StringUtils.isNotBlank(r.getPermission())){
//
//                    info.addStringPermission(r.getPermission());
//                }
//
//            }
//
//            this.setSession(Constants.AUTH_SESSION, info);
//
//            info.addStringPermission("base:view");
//            return info;
        } else {
            return info;
        }


    }

    @Override
    protected org.apache.shiro.authc.AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {

        UsernamePasswordToken t = (UsernamePasswordToken) authenticationToken;
        AuthenticationInfo authcInfo = null;


        t.setRememberMe(false);

        return authcInfo;


    }


}
