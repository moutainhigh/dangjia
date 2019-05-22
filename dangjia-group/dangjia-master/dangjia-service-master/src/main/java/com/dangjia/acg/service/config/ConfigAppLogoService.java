package com.dangjia.acg.service.config;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.mapper.config.IConfigAppLogoMapper;
import com.dangjia.acg.modle.config.ConfigAppLogo;
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
public class ConfigAppLogoService {

    @Autowired
    private IConfigAppLogoMapper configAppLogoMapper;

    /**
     * 获取所有版本应用
     * @param configAppLogo
     * @return
     */
    public ServerResponse getConfigAppLogos(HttpServletRequest request, ConfigAppLogo configAppLogo) {
        Example example = new Example(ConfigAppLogo.class);
        Example.Criteria criteria=example.createCriteria();
        if(!CommonUtil.isEmpty(configAppLogo.getAppType())) {
            criteria.andEqualTo("appType", configAppLogo.getAppType());
        }
        List<ConfigAppLogo> list = configAppLogoMapper.selectByExample(example);
        return ServerResponse.createBySuccess("ok",list);
    }
    /**
     * 获取当前配置logo
     * @param configAppLogo
     * @return
     */
    public ServerResponse getConfigAppLogo(HttpServletRequest request, ConfigAppLogo configAppLogo) {
        Example example = new Example(ConfigAppLogo.class);
        Example.Criteria criteria=example.createCriteria();
        if(!CommonUtil.isEmpty(configAppLogo.getAppType())) {
            criteria.andEqualTo("appType", configAppLogo.getAppType());
        }
        criteria.andEqualTo("isSwitch",true);
        List<ConfigAppLogo> list = configAppLogoMapper.selectByExample(example);
        if(list.size()>0){
            return ServerResponse.createBySuccess("ok",list.get(0).getType());
        }
        return ServerResponse.createBySuccess("ok","0");
    }
    /**
     * 删除
     * @param id
     * @return
     */
    public ServerResponse delConfigAppLogo(HttpServletRequest request, String id) {
        if(this.configAppLogoMapper.deleteByPrimaryKey(String.valueOf(id))>0){
            return ServerResponse.createBySuccessMessage("ok");
        }else{
            return ServerResponse.createByErrorMessage("删除失败，请您稍后再试");
        }
    }

    /**
     * 修改
     * @param configAppLogo
     * @return
     */
    public ServerResponse editConfigAppLogo(HttpServletRequest request, ConfigAppLogo configAppLogo) {
        ConfigAppLogo configAppLogoAll=new ConfigAppLogo();
        Boolean isSwitch = Boolean.parseBoolean(request.getParameter("isSwitch"));
        Example example = new Example(ConfigAppLogo.class);
        Example.Criteria criteria=example.createCriteria();
        criteria.andEqualTo("isSwitch",true).andEqualTo("appType",configAppLogo.getAppType());
        configAppLogoAll.setId(null);
        configAppLogoAll.setIsSwitch(false);
        configAppLogo.setIsSwitch(isSwitch);
        configAppLogoMapper.updateByExampleSelective(configAppLogoAll,example);
        if(this.configAppLogoMapper.updateByPrimaryKeySelective(configAppLogo)>0){
            return ServerResponse.createBySuccessMessage("ok");
        }else{
            return ServerResponse.createByErrorMessage("修改失败，请您稍后再试");
        }
    }
    /**
     * 新增
     * @param configAppLogo
     * @return
     */
    public ServerResponse addConfigAppLogo(HttpServletRequest request,ConfigAppLogo configAppLogo) {
        //查看该权限是否有子节点，如果有，先删除子节点
        configAppLogo.setId((int)(Math.random() * 50000000) + 50000000 + "" + System.currentTimeMillis());
        ConfigAppLogo configAppLogoAll=new ConfigAppLogo();
        Example example = new Example(ConfigAppLogo.class);
        Example.Criteria criteria=example.createCriteria();
        criteria.andEqualTo("isSwitch",true).andEqualTo("appType",configAppLogo.getAppType());
        configAppLogoAll.setId(null);
        configAppLogoAll.setIsSwitch(false);
        configAppLogoMapper.updateByExampleSelective(configAppLogoAll,example);
        Boolean isSwitch = Boolean.parseBoolean(request.getParameter("isSwitch"));
        configAppLogo.setIsSwitch(isSwitch);
        if(this.configAppLogoMapper.insertSelective(configAppLogo)>0){
            return ServerResponse.createBySuccessMessage("ok");
        }else{
            return ServerResponse.createByErrorMessage("新增失败，请您稍后再试");
        }
    }
}
