package com.dangjia.acg.api;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.model.Config;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @Author QiYuXiang
 * @Description 类说明
 * @Date 2018/4/3
 * @Time 上午9:57
 * @Version V1.0.0
 */
@FeignClient("dangjia-service-master")
@Api(value = "Basic管理端接口", description = "Basic管理端接口")
public interface BasicManageAPI {

  /**
   * @author QiYuXiang
   * @description 查询基础配置信息
   * @date 2018/4/3 上午10:02
   */
  @RequestMapping(value = "queryBasicConfig", method = RequestMethod.POST)
  @ApiOperation(value = "查询基础配置信息", notes = "查询基础配置信息")
  public Object queryBasicConfig(PageDTO pageDTO, @RequestBody Config config);

  /**
   * @author QiYuXiang
   * @description 修改基础配置信息
   * @date 2018/4/3 上午10:02
   */
  @RequestMapping(value = "modifyBasicConfig", method = RequestMethod.POST)
  @ApiOperation(value = "修改基础配置信息", notes = "修改基础配置信息")
  public void modifyBasicConfig(@RequestBody Config config);

  /**
   * @author QiYuXiang
   * @description 新增基础配置信息
   * @date 2018/4/3 上午10:02
   */
  @RequestMapping(value = "addBasicConfig", method = RequestMethod.POST)
  @ApiOperation(value = "新增基础配置信息", notes = "新增基础配置信息")
  public void addBasicConfig(@RequestBody Config config);

  /**
   * @author QiYuXiang
   * @description 删除基础配置信息
   * @date 2018/4/3 上午10:02
   */
  @RequestMapping(value = "delBasicConfig", method = RequestMethod.POST)
  @ApiOperation(value = "删除基础配置信息", notes = "删除基础配置信息")
  public void delBasicConfig(@RequestBody Config config);

}
