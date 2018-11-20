package com.dangjia.acg.service;

import com.github.pagehelper.PageHelper;
import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.model.PageBean;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.util.Validator;
import com.dangjia.acg.mapper.IConfigMapper;
import com.dangjia.acg.model.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author QiYuXiang
 * @Description 类说明
 * @Date 2018/4/3
 * @Time 上午11:03
 * @Version V1.0.0
 */
@Service
public class BasicManageService {

  @Autowired
  private IConfigMapper configMapper;
  @Autowired
  private RedisClient redisClient;

  /**
   * @author QiYuXiang
   * @description 查询公共配置
   * @date 2018/4/3 下午2:11
   */
  public PageBean<Config> queryBasicConfig(PageDTO pageDTO, Config config) {
    PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
    List<Config> configs = configMapper.queryBasicConfig(config);
    PageBean<Config> pages = new PageBean<Config>(configs);
    return pages;
  }

  /**
   * @author QiYuXiang
   * @description 修改配置
   * @date 2018/4/3 下午2:09
   */
  public void modifyBasicConfig(Config config) {
    Config config1 = new Config();
    config1.setParamKey(config.getParamKey());
    config1 = configMapper.selectOne(config1);
    Validator.notNull(config1, "参数有误,系统参数不存在.");
    configMapper.updateByPrimaryKeySelective(config);
    redisClient.put(Constants.CONFIG_KEY + config.getParamKey(), config.getParamValue());
  }

  /**
   * @author QiYuXiang
   * @description 添加配置
   * @date 2018/4/3 下午2:11
   */
  public void addBasicConfig(Config config) {
    configMapper.insert(config);
    redisClient.put(Constants.CONFIG_KEY + config.getParamKey(), config.getParamValue());
  }

  /**
   * @author QiYuXiang
   * @description 删除配置
   * @date 2018/4/3 下午2:11
   */
  public void delBasicConfig(Config config) {
    configMapper.delete(config);
    redisClient.deleteCache(Constants.CONFIG_KEY + config.getParamKey());
  }
}
