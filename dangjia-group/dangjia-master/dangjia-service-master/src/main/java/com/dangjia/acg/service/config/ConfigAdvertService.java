package com.dangjia.acg.service.config;

import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.mapper.config.IConfigAdvertMapper;
import com.dangjia.acg.modle.config.ConfigAdvert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2018/10/31 0031
 * Time: 20:18
 */
@Service
public class ConfigAdvertService {

    @Autowired
    private IConfigAdvertMapper configAdvertMapper;

    @Autowired
    private ConfigUtil configUtil;
    /**
     * 获取所有广告
     * @param configAdvert
     * @return
     */
    public ServerResponse getConfigAdverts(HttpServletRequest request, ConfigAdvert configAdvert) {
        Example example = new Example(ConfigAdvert.class);
        Example.Criteria criteria=example.createCriteria();
        if(!CommonUtil.isEmpty(configAdvert.getAppType())) {
            criteria.andEqualTo("appType", configAdvert.getAppType());
        }
        if(!CommonUtil.isEmpty(configAdvert.getCityId())) {
            criteria.andEqualTo("cityId", configAdvert.getCityId());
        }
        if(!CommonUtil.isEmpty(configAdvert.getAdvertType())) {
            criteria.andEqualTo("advertType", configAdvert.getAdvertType());
        }
        List<ConfigAdvert> list = configAdvertMapper.selectByExample(example);
        for (ConfigAdvert v:list){
            v.initPath(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class));
        }
        return ServerResponse.createBySuccess("ok",list);
    }
    /**
     * 删除
     * @param id
     * @return
     */
    public ServerResponse delConfigAdvert(HttpServletRequest request, String id) {
        if(this.configAdvertMapper.deleteByPrimaryKey(String.valueOf(id))>0){
            return ServerResponse.createBySuccessMessage("ok");
        }else{
            return ServerResponse.createByErrorMessage("删除失败，请您稍后再试");
        }
    }

    /**
     * 修改
     * @param configAdvert
     * @return
     */
    public ServerResponse editConfigAdvert(HttpServletRequest request, ConfigAdvert configAdvert) {
        //查看该权限是否有子节点，如果有，先删除子节点
        if(this.configAdvertMapper.updateByPrimaryKeySelective(configAdvert)>0){
            return ServerResponse.createBySuccessMessage("ok");
        }else{
            return ServerResponse.createByErrorMessage("修改失败，请您稍后再试");
        }
    }
    /**
     * 新增
     * @param configAdvert
     * @return
     */
    public ServerResponse addConfigAdvert(HttpServletRequest request,ConfigAdvert configAdvert) {
        //查看该权限是否有子节点，如果有，先删除子节点
        if(this.configAdvertMapper.insertSelective(configAdvert)>0){
            return ServerResponse.createBySuccessMessage("ok");
        }else{
            return ServerResponse.createByErrorMessage("新增失败，请您稍后再试");
        }
    }
}
