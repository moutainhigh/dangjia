package com.dangjia.acg.service;

import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.util.ProtoStuffSerializerUtil;
import com.dangjia.acg.common.util.Validator;
import com.dangjia.acg.mapper.IConfigMapper;
import com.dangjia.acg.model.Config;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 系统配置service
 *
 * @author QiYuXiang
 * @date: 2017/8/3.
 */
@Service
public class ConfigService {

  /****
   * 注入配置mapper
   */
  @Autowired
  private IConfigMapper configMapper;

  /****
   * 注入配置
   */
  @Autowired
  private RedisClient redisClient;

  /****
   * 获取参数值
   *
   * @param name    参数名
   * @param appType 应用
   * @return
   */
  public byte[] getValue(String name, Integer appType) {

    Config config = new Config();
    config.setParamKey(name);
    config.setAppType(appType);
    config = configMapper.selectOne(config);
    String res = null != config ? config.getParamValue() : null;
    if (StringUtils.isEmpty(res)) {
      return null;
    } else {

      redisClient.put(Constants.CONFIG_KEY + config.getParamKey(), config.getParamValue());
      byte[] b = ProtoStuffSerializerUtil.serialize(res);
      return b;
    }



  }

  /****
   * 更新系统参数值
   *
   * @param name  参数名
   * @param value 参数值
   */
  public void updateValue(String name, Integer appType, String value) {
    Validator.hasText(name, "系统参数名不能为空.");
    Validator.notNull(appType, "应用不能为空.");
    Validator.hasText(value, "系统参数名不能为空.");

    Config config = new Config();
    config.setParamKey(name);
    config.setAppType(appType);
    config = configMapper.selectOne(config);
    Validator.notNull(config, "参数有误,系统参数不存在.");
    config.setParamValue(value);
    configMapper.updateByPrimaryKeySelective(config);

    redisClient.put(Constants.CONFIG_KEY + config.getParamKey(), config.getParamValue());
  }

  /**
   * 缓存配置信息
   */
  public void cacheConfig() {
    //循环缓存配置信息
    List<Config> configList = configMapper.selectAll();
    for (Config config : configList) {
      redisClient.put(Constants.CONFIG_KEY + config.getParamKey(), config.getParamValue());
    }
  }

}
