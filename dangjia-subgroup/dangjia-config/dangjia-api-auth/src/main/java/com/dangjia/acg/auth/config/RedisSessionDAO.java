package com.dangjia.acg.auth.config;

import com.dangjia.acg.api.CacheServiceAPI;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.util.SerializeUtils;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.Collection;

/**
 * redis实现共享session
 */
public class RedisSessionDAO extends AbstractSessionDAO {

    private static Logger logger = LoggerFactory.getLogger(RedisSessionDAO.class);


    private static String SUFFIX = Constants.REDIS_SHIRO_CACHE;

    @Autowired
    private CacheServiceAPI redisClient;

    // 创建session，保存到数据库
    @Override
    protected Serializable doCreate(Session session) {

        Serializable sessionId = this.generateSessionId(session);
        this.assignSessionId(session, sessionId);

        logger.info("创建session:{}", session.getId());
        try {
            redisClient.put(SUFFIX + sessionId.toString(), SerializeUtils.serialize(session));
        } catch (Exception e) {
            throw e;
        }

        return sessionId;
    }

    // 获取session
    @Override
    protected Session doReadSession(Serializable sessionId) {
        logger.info("获取session:{}", sessionId);

        byte[] bytes = redisClient.getCache(SUFFIX + sessionId.toString());
        Session session = (Session) SerializeUtils.deserialize(bytes);

        //对象转map
        //
        return session;
    }


    @Override
    public void update(Session session) throws UnknownSessionException {
        //logger.info("更新session:{}", session.getId()+"expired"+session.getLastAccessTime());

        String key = Constants.REDIS_SHIRO_CACHE + session.getId().toString();

        //redisClient.putCacheWithExpireTime(key, SerializeUtils.serialize(session), Constants.SESSION_EXPIRE_TIME);

    }

    @Override
    public void delete(Session session) {
        logger.info("删除session:{}", session.getId());
        redisClient.deleteCache(SUFFIX + session.getId().toString());
    }

    @Override
    public Collection<Session> getActiveSessions() {
        return null;
    }
}
