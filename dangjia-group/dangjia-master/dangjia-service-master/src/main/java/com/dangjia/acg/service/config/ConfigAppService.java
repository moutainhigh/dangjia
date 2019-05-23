package com.dangjia.acg.service.config;

import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.enums.EventStatus;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.mapper.config.IConfigAppHistoryMapper;
import com.dangjia.acg.mapper.config.IConfigAppMapper;
import com.dangjia.acg.modle.config.ConfigApp;
import com.dangjia.acg.modle.config.ConfigAppHistory;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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


    /**
     * 获取所有版本应用
     *
     * @param configApp
     * @return
     */
    public ServerResponse getConfigApps(HttpServletRequest request, ConfigApp configApp) {
        Example example = new Example(ConfigApp.class);
        Example.Criteria criteria = example.createCriteria();
        if (!CommonUtil.isEmpty(configApp.getAppType())) {
            criteria.andEqualTo(ConfigApp.APP_TYPE, configApp.getAppType());
        }
        if (!CommonUtil.isEmpty(configApp.getName())) {
            criteria.andLike(ConfigApp.NAME, "%" + configApp.getName() + "%");
        }
        criteria.andEqualTo(ConfigApp.DATA_STATUS, 0);
        example.orderBy(ConfigApp.CREATE_DATE).desc();
        Integer pageNum = Integer.parseInt(request.getParameter("pageNum"));
        Integer pageSize = Integer.parseInt(request.getParameter("pageSize"));
        PageHelper.startPage(pageNum, pageSize);
        List<ConfigApp> list = configAppMapper.selectByExample(example);
        List listh = new ArrayList();
        PageInfo pageResult = new PageInfo(list);
        for (ConfigApp app : list) {
            Map map = BeanUtils.beanToMap(app);
            Example exampleHistory = new Example(ConfigAppHistory.class);
            Example.Criteria criteriaHistory = exampleHistory.createCriteria();
            criteriaHistory.andEqualTo("appId", app.getId());
            List<ConfigAppHistory> historyList = configAppHistoryMapper.selectByExample(exampleHistory);
            map.put("historyList", historyList);
            listh.add(map);
        }
        pageResult.setList(listh);
        return ServerResponse.createBySuccess("ok", pageResult);
    }

    /**
     * 版本检测
     *
     * @param configApp
     * @return
     */
    public ServerResponse checkConfigApp(HttpServletRequest request, ConfigApp configApp) {
        Example example = new Example(ConfigApp.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo(ConfigApp.APP_TYPE, configApp.getAppType());
        criteria.andEqualTo(ConfigApp.DATA_STATUS, 0);
        if (!CommonUtil.isEmpty(configApp.getVersionCode()) && configApp.getVersionCode() > 0) {
            criteria.andGreaterThan(ConfigApp.VERSION_CODE, configApp.getVersionCode());
        }
        example.orderBy(ConfigApp.CREATE_DATE).desc();
        PageHelper.startPage(0, 1);
        List<ConfigApp> list = configAppMapper.selectByExample(example);
        if (list.size() > 0) {
            configApp = list.get(0);
            Example exampleHistory = new Example(ConfigAppHistory.class);
            Example.Criteria criteriaHistory = exampleHistory.createCriteria();
            criteriaHistory.andEqualTo(ConfigAppHistory.APP_ID, configApp.getId());
            criteriaHistory.andEqualTo(ConfigAppHistory.VERSION_CODE, configApp.getVersionCode());
            List<ConfigAppHistory> historyList = configAppHistoryMapper.selectByExample(exampleHistory);
            if (historyList.size() > 0) {
                configApp.setIsForced(historyList.get(0).getIsForced());
            }
            configApp.initPath(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class));
            return ServerResponse.createBySuccess("ok", configApp);
        } else {
            return ServerResponse.createByErrorCodeMessage(EventStatus.NO_DATA.getCode(), "暂无更新版本");
        }

    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    public ServerResponse delConfigApp(HttpServletRequest request, String id) {
        //查看该权限是否有子节点，如果有，先删除子节点
        Example example = new Example(ConfigAppHistory.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("appId", id);
        configAppHistoryMapper.deleteByExample(criteria);
        if (this.configAppMapper.deleteByPrimaryKey(String.valueOf(id)) > 0) {
            return ServerResponse.createBySuccessMessage("ok");
        } else {
            return ServerResponse.createByErrorMessage("删除失败，请您稍后再试");
        }
    }

    /**
     * 修改
     *
     * @param configApp
     * @return
     */
    public ServerResponse editConfigApp(HttpServletRequest request, ConfigApp configApp, String[] isForceds, String[] versionCode, String[] historyId) {
        Example example = new Example(ConfigAppHistory.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("appId", configApp.getId());
        configAppHistoryMapper.deleteByExample(example);
        if (this.configAppMapper.updateByPrimaryKeySelective(configApp) > 0) {
            for (int i = 0; i < historyId.length; i++) {
                ConfigAppHistory configAppHistory = new ConfigAppHistory();
                if (!CommonUtil.isEmpty(historyId[i])) {
                    configAppHistory.setAppId(configApp.getId());
                    configAppHistory.setHistoryId(historyId[i]);
                    configAppHistory.setIsForced(true);
                    if ("0".equals(isForceds[i])) {
                        configAppHistory.setIsForced(false);
                    }

                    configAppHistory.setVersionCode(versionCode[i]);
                    configAppHistoryMapper.insert(configAppHistory);
                }
            }
            return ServerResponse.createBySuccessMessage("ok");
        } else {
            return ServerResponse.createByErrorMessage("修改失败，请您稍后再试");
        }
    }

    /**
     * 新增
     *
     * @param configApp
     * @return
     */
    public ServerResponse addConfigApp(HttpServletRequest request, ConfigApp configApp, String[] isForceds, String[] versionCode, String[] historyId) {
        configApp.setId((int) (Math.random() * 50000000) + 50000000 + "" + System.currentTimeMillis());
        if (this.configAppMapper.insertSelective(configApp) > 0) {
            if (isForceds != null) {
                for (int i = 0; i < historyId.length; i++) {
                    ConfigAppHistory configAppHistory = new ConfigAppHistory();
                    if (!CommonUtil.isEmpty(historyId[i])) {
                        configAppHistory.setAppId(configApp.getId());
                        configAppHistory.setIsForced(true);
                        if ("0".equals(isForceds[i])) {
                            configAppHistory.setIsForced(false);
                        }
                        configAppHistory.setIsForced(Boolean.parseBoolean(isForceds[i]));
                        configAppHistory.setVersionCode(versionCode[i]);
                        configAppHistoryMapper.insert(configAppHistory);
                    }
                }
            }
            return ServerResponse.createBySuccessMessage("ok");
        } else {
            return ServerResponse.createByErrorMessage("新增失败，请您稍后再试");
        }
    }
}
