package com.dangjia.acg.api;

import com.dangjia.acg.api.config.FeignConfig;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Set;

/**
 * Created by QiYuXiang on 2018/3/20.
 */
@FeignClient(value = "dangjia-service-cache",configuration = FeignConfig.class)
public interface CacheServiceAPI {



    /***
     * put
     * @param key key
     * @param obj obj

     * @return
     */
    @RequestMapping(value = "put",method = RequestMethod.POST)
     boolean put(@RequestParam("key") String key, @RequestParam("obj") byte[] obj);

    /***
     * put
     * @param key
     * @param obj
     * @param expireTime
     */
    @RequestMapping(value = "putCacheWithExpireTime",method = RequestMethod.POST)
    void putCacheWithExpireTime(@RequestParam("key")String key,@RequestParam("obj") byte[] obj,@RequestParam("expireTime")  Long expireTime);

    /***
     * 集合put
     * @param key
     * @param objList
     * @return
     */
    @RequestMapping(value = "putListCache",method = RequestMethod.POST)
    boolean putListCache(@RequestParam("key")String key, @RequestParam("objList")byte[] objList);

    /***
     * 集合put
     * @param key
     * @param objList
     * @return
     */
     @RequestMapping(value = "putListCaches",method = RequestMethod.POST)
     boolean putListCaches(@RequestParam("key")String key, @RequestBody byte[] objList);

     @RequestMapping(value = "putListCacheStr",method = RequestMethod.POST)
     boolean putListCacheStr(@RequestParam("key")String key,@RequestBody List<String> objList);

        /***
         * put
         * @param key
         * @param objList
         * @param expireTime
         * @return
         */
    @RequestMapping(value = "putListCacheWithExpireTime",method = RequestMethod.POST)
    boolean putListCacheWithExpireTime(@RequestParam("key") String key,@RequestParam("objList") byte [] objList,@RequestParam("expireTime") Long expireTime);
//
//
    /***
     *
     * @param key
     * @return
     */
    @RequestMapping(value = "getCache",method = RequestMethod.POST)
    byte[] getCache(@RequestParam("key") String key);

    /***
     *
     * @param key
     * @return
     */
    @RequestMapping(value = "getListCache",method = RequestMethod.POST)
    byte[] getListCache(@RequestParam("key") String key);


    @RequestMapping(value = "getLikeListCache",method = RequestMethod.POST)
    byte[] getLikeListCache(@RequestParam("key") String key);

    /***
     * 删除
     * @param key
     */
    @RequestMapping(value = "deleteCache",method = RequestMethod.POST)
    void deleteCache(@RequestParam("key")String key);


    /***
     * 判断是否存在
     * @param key
     * @return
     */
    @RequestMapping(value = "exists",method = RequestMethod.POST)
    boolean exists(@RequestParam("key")String key);

    /***
     * 模糊匹配删除
     * @param pattern
     */
    @RequestMapping(value = "deleteCacheWithPattern",method = RequestMethod.POST)
    void deleteCacheWithPattern(@RequestParam("pattern")String pattern);


    /***
     * 时间更新
     * @param key
     * @param timeout
     * @return
     */
    @RequestMapping(value = "expire",method = RequestMethod.POST)
    boolean expire(@RequestParam("key")String key,@RequestParam("timeout")Long timeout);

    @RequestMapping(value = "keys",method = RequestMethod.POST)
    Set<String> keys(@RequestParam("pattern") String pattern);


    @RequestMapping(value = "putMap",method = RequestMethod.POST)
    void putMap(@RequestParam("key")String key,@RequestParam("object")Object object);


    @RequestMapping(value = "getMap",method = RequestMethod.POST)
    Object getMap(@RequestParam("key")String key);



    @RequestMapping(value = "sortPageList",method = RequestMethod.POST)
    <T> List<T> sortPageList(@RequestParam("key")String key,@RequestParam("isAlpha")boolean isAlpha,@RequestParam("off")Integer off,@RequestParam("num")Integer num);

}
