package com.dangjia.acg.controller;

import com.dangjia.acg.api.BasicManageAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.model.Config;
import com.dangjia.acg.service.BasicManageService;
import com.dangjia.acg.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Author QiYuXiang
 * @Description 管理端相关服务
 * @Date 2018/4/3
 * @Time 上午9:56
 * @Version V1.0.0
 */
@RestController
public class BasicManageController implements BasicManageAPI {

  @Resource
  private BasicManageService manageService;
  @Autowired
  private ConfigService configService;

  /**
   * @author QiYuXiang
   * @description 查询基础配置信息
   * @date 2018/4/3 上午10:02
   */
  @Override
  @ApiMethod
  public Object queryBasicConfig(PageDTO pageDTO, Config config) {
    return manageService.queryBasicConfig(pageDTO,config);
  }

  /**
   * @author QiYuXiang
   * @description 修改基础配置信息
   * @date 2018/4/3 上午10:02
   */
  @Override
  @ApiMethod
  public void modifyBasicConfig(Config config) {
    manageService.modifyBasicConfig(config);
  }

  /**
   * @author QiYuXiang
   * @description 新增基础配置信息
   * @date 2018/4/3 上午10:02
   */
  @Override
  @ApiMethod
  public void addBasicConfig(Config config) {
    manageService.addBasicConfig(config);
  }

  /**
   * @author QiYuXiang
   * @description 删除基础配置信息
   * @date 2018/4/3 上午10:02
   */
  @Override
  @ApiMethod
  public void delBasicConfig(Config config) {
    manageService.delBasicConfig(config);
  }
}
