package redis;

import redis.clients.jedis.Jedis;

public class LoginAndCookie {

    public String checkToken(Jedis conn,String token)

    {
        return  conn.hget("login:", token);
    }


}
