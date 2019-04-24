package com.dangjia.acg.service.menu;

import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.enums.EventStatus;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.mapper.menu.IMenuConfigurationMapper;
import com.dangjia.acg.modle.menu.MenuConfiguration;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Ruking.Cheng
 * @descrilbe web端菜单编辑接口服务
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/4/23 6:01 PM
 */
@Service
public class MenuConfigurationService {
    @Autowired
    private IMenuConfigurationMapper iMenuConfigurationMapper;
    @Autowired
    private ConfigUtil configUtil;


    public ServerResponse setMenuConfiguration(MenuConfiguration menuConfiguration) {
        if (CommonUtil.isEmpty(menuConfiguration.getName())) {
            return ServerResponse.createByErrorMessage("请传入菜单名称");
        }
        if (CommonUtil.isEmpty(menuConfiguration.getMenuType())) {
            return ServerResponse.createByErrorMessage("请传入菜单类别");
        }
        if (CommonUtil.isEmpty(menuConfiguration.getSort())) {
            menuConfiguration.setSort(99);
        }
        if (CommonUtil.isEmpty(menuConfiguration.getId())) {
            iMenuConfigurationMapper.insert(menuConfiguration);
        } else {
            iMenuConfigurationMapper.updateByPrimaryKeySelective(menuConfiguration);
        }
        return ServerResponse.createBySuccessMessage("提交成功");
    }

    public ServerResponse delMenuConfiguration(String menuConfigurationId) {
        MenuConfiguration menuConfiguration = iMenuConfigurationMapper.selectByPrimaryKey(menuConfigurationId);
        if (menuConfiguration == null) {
            return ServerResponse.createByErrorMessage("此菜单不存在");
        }
        menuConfiguration.setDataStatus(1);
        iMenuConfigurationMapper.updateByPrimaryKeySelective(menuConfiguration);
        return ServerResponse.createBySuccessMessage("删除成功");
    }

    public ServerResponse getMenuConfigurations(PageDTO pageDTO, MenuConfiguration menuConfiguration) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            Example example = new Example(MenuConfiguration.class);
            Example.Criteria criteria = example.createCriteria()
                    .andEqualTo(MenuConfiguration.DATA_STATUS, 0);
            if (menuConfiguration.getParentId().equals("-1")) {
                criteria.andIsNull(MenuConfiguration.PARENT_ID);
            } else if (!CommonUtil.isEmpty(menuConfiguration.getParentId())) {
                criteria.andEqualTo(MenuConfiguration.PARENT_ID, menuConfiguration.getParentId());
            }
            if (!CommonUtil.isEmpty(menuConfiguration.getName())) {
                criteria.andEqualTo(MenuConfiguration.NAME, menuConfiguration.getName());
            }
            if (!CommonUtil.isEmpty(menuConfiguration.getType())) {
                criteria.andEqualTo(MenuConfiguration.TYPE, menuConfiguration.getType());
            }
            if (!CommonUtil.isEmpty(menuConfiguration.getMenuType())) {
                criteria.andEqualTo(MenuConfiguration.MENU_TYPE, menuConfiguration.getMenuType());
            }
            if (!CommonUtil.isEmpty(menuConfiguration.getShowDesigner())) {
                criteria.andEqualTo(MenuConfiguration.SHOW_DESIGNER, menuConfiguration.getShowDesigner());
            }
            if (!CommonUtil.isEmpty(menuConfiguration.getShowActuaries())) {
                criteria.andEqualTo(MenuConfiguration.SHOW_ACTUARIES, menuConfiguration.getShowActuaries());
            }
            if (!CommonUtil.isEmpty(menuConfiguration.getShowHousekeeper())) {
                criteria.andEqualTo(MenuConfiguration.SHOW_HOUSEKEEPER, menuConfiguration.getShowHousekeeper());
            }
            if (!CommonUtil.isEmpty(menuConfiguration.getShowCraftsman())) {
                criteria.andEqualTo(MenuConfiguration.SHOW_CRAFTSMAN, menuConfiguration.getShowCraftsman());
            }
            if (!CommonUtil.isEmpty(menuConfiguration.getShowType())) {
                criteria.andEqualTo(MenuConfiguration.SHOW_TYPE, menuConfiguration.getShowType());
            }
            if (!CommonUtil.isEmpty(menuConfiguration.getShowProprietor())) {
                criteria.andEqualTo(MenuConfiguration.SHOW_PROPRIETOR, menuConfiguration.getShowProprietor());
            }
            example.orderBy(MenuConfiguration.SORT).asc();
            List<MenuConfiguration> menuConfigurations = iMenuConfigurationMapper.selectByExample(example);
            if (menuConfigurations.size() <= 0) {
                return ServerResponse.createByErrorCodeMessage(EventStatus.NO_DATA.getCode()
                        , "查无数据");
            }
            PageInfo pageResult = new PageInfo(menuConfigurations);
            List<Map<String, Object>> datas = new ArrayList<>();
            String imageAddress = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            String webAddress = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class);
            for (MenuConfiguration configuration : menuConfigurations) {
                Map<String, Object> mapSeries = BeanUtils.beanToMap(configuration);
                configuration.initPath(imageAddress, webAddress);
                String imageUrl = configuration.getImage();
                String webUrl = configuration.getUrl();
                mapSeries.put("imageUrl", imageUrl);
                mapSeries.put("webUrl", webUrl);
                datas.add(mapSeries);
            }
            pageResult.setList(datas);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }
}
