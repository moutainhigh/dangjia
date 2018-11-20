package com.dangjia.acg.service.config;

import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.enums.EventStatus;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.mapper.config.IConfigAppHistoryMapper;
import com.dangjia.acg.mapper.config.IConfigAppMapper;
import com.dangjia.acg.modle.config.ConfigAdvert;
import com.dangjia.acg.modle.config.ConfigApp;
import com.dangjia.acg.modle.config.ConfigAppHistory;
import com.github.pagehelper.PageHelper;
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
public class ConfigAppService {

    @Autowired
    private IConfigAppMapper configAppMapper;

    @Autowired
    private ConfigUtil configUtil;

    @Autowired
    private IConfigAppHistoryMapper configAppHistoryMapper;

    public List<ConfigApp>  queryConfigApps(HttpServletRequest request, ConfigApp configApp){
        Example example = new Example(ConfigApp.class);
        Example.Criteria criteria=example.createCriteria();
        if(!CommonUtil.isEmpty(configApp.getAppType())) {
            criteria.andEqualTo("appType", configApp.getAppType());
        }
        if (!CommonUtil.isEmpty(configApp.getName())) {
            criteria.andLike("name", "%" + configApp.getName() + "%");
        }
        example.orderBy("createDate").desc();
        if (!CommonUtil.isEmpty(request.getAttribute("pageNum"))) {
            Integer pageNum = (Integer) request.getAttribute("pageNum");
            Integer pageSize = (Integer) request.getAttribute("pageSize");
            PageHelper.startPage(pageNum, pageSize);
        }
        List<ConfigApp> list = configAppMapper.selectByExample(example);
        return list;
    }

    /**
     * 获取所有版本应用
     * @param configApp
     * @return
     */
    public ServerResponse getConfigApps(HttpServletRequest request, ConfigApp configApp) {
        Example example = new Example(ConfigApp.class);
        Example.Criteria criteria=example.createCriteria();
        if(!CommonUtil.isEmpty(configApp.getAppType())) {
            criteria.andEqualTo("appType", configApp.getAppType());
        }
        if (!CommonUtil.isEmpty(configApp.getName())) {
            criteria.andLike("name", "%" + configApp.getName() + "%");
        }
        example.orderBy("createDate").desc();
        if (!CommonUtil.isEmpty(request.getAttribute("pageNum"))) {
            Integer pageNum = (Integer) request.getAttribute("pageNum");
            Integer pageSize = (Integer) request.getAttribute("pageSize");
            PageHelper.startPage(pageNum, pageSize);
        }
        List<ConfigApp> list = configAppMapper.selectByExample(example);
        return ServerResponse.createBySuccess("ok",list);
    }

    /**
     * 版本检测
     * @param configApp
     * @return
     */
    public ServerResponse checkConfigApp(HttpServletRequest request, ConfigApp configApp) {
        Example example = new Example(ConfigApp.class);
        example.createCriteria()
                .andEqualTo("appType", configApp.getAppType())
                .andGreaterThan("versionCode", configApp.getVersionCode());
        example.orderBy("createDate").desc();
        PageHelper.startPage(0, 1);
        List<ConfigApp> list = configAppMapper.selectByExample(example);
        if(list.size()>0){
            configApp=list.get(0);
            Example exampleHistory = new Example(ConfigAppHistory.class);
            Example.Criteria criteriaHistory=exampleHistory.createCriteria();
            criteriaHistory.andEqualTo("appId",configApp.getId());
            criteriaHistory.andEqualTo("versionCode",configApp.getVersionCode());
            List<ConfigAppHistory> historyList =configAppHistoryMapper.selectByExample(exampleHistory);
            if(historyList.size()>0){
                configApp.setIsForced(historyList.get(0).getIsForced());
            }
            configApp.initPath(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class));
            return ServerResponse.createBySuccess("ok",configApp);
        }else{
            return ServerResponse.createByErrorCodeMessage(EventStatus.NO_DATA.getCode(),"暂无更新版本");
        }

    }
    /**
     * 删除
     * @param id
     * @return
     */
    public ServerResponse delConfigApp(HttpServletRequest request, String id) {
        //查看该权限是否有子节点，如果有，先删除子节点
        Example example = new Example(ConfigAppHistory.class);
        Example.Criteria criteria=example.createCriteria();
        criteria.andEqualTo("appId",id);
        configAppHistoryMapper.deleteByExample(criteria);
        if(this.configAppMapper.deleteByPrimaryKey(String.valueOf(id))>0){
            return ServerResponse.createBySuccessMessage("ok");
        }else{
            return ServerResponse.createByErrorMessage("删除失败，请您稍后再试");
        }
    }

    /**
     * 修改
     * @param configApp
     * @return
     */
    public ServerResponse editConfigApp(HttpServletRequest request, ConfigApp configApp) {
        Example example = new Example(ConfigAppHistory.class);
        Example.Criteria criteria=example.createCriteria();
        criteria.andEqualTo("appId",configApp.getId());
        configAppHistoryMapper.deleteByExample(criteria);
        if(this.configAppMapper.updateByPrimaryKeySelective(configApp)>0){
            Boolean[] isForceds=(Boolean[]) request.getAttribute("isForced");
            String[] cersionCode=(String[]) request.getAttribute("cersionCode");
            String[] historyIds=(String[]) request.getAttribute("historyId");
            for (int i = 0; i <historyIds.length ; i++) {
                ConfigAppHistory configAppHistory=new ConfigAppHistory();
                if(!CommonUtil.isEmpty(historyIds[i])){
                    configAppHistory.setAppId(configApp.getId());
                    configAppHistory.setHistoryId(historyIds[i]);
                    configAppHistory.setIsForced(isForceds[i]);
                    configAppHistory.setVersionCode(cersionCode[i]);
                    configAppHistoryMapper.insert(configAppHistory);
                }
            }
            return ServerResponse.createBySuccessMessage("ok");
        }else{
            return ServerResponse.createByErrorMessage("修改失败，请您稍后再试");
        }
    }
    /**
     * 新增
     * @param configApp
     * @return
     */
    public ServerResponse addConfigApp(HttpServletRequest request,ConfigApp configApp) {
        if(this.configAppMapper.insertSelective(configApp)>0){
            Boolean[] isForceds=(Boolean[]) request.getAttribute("isForceds");
            if(isForceds!=null){
                String[] vcersionCode=(String[]) request.getAttribute("vcersionCodes");
                String[] historyIds=(String[]) request.getAttribute("historyIds");
                for (int i = 0; i <historyIds.length ; i++) {
                    ConfigAppHistory configAppHistory=new ConfigAppHistory();
                    if(!CommonUtil.isEmpty(historyIds[i])){
                        configAppHistory.setAppId(configApp.getId());
                        configAppHistory.setHistoryId(historyIds[i]);
                        configAppHistory.setIsForced(isForceds[i]);
                        configAppHistory.setVersionCode(vcersionCode[i]);
                        configAppHistoryMapper.insert(configAppHistory);
                    }
                }
            }
            return ServerResponse.createBySuccessMessage("ok");
        }else{
            return ServerResponse.createByErrorMessage("新增失败，请您稍后再试");
        }
    }
}
