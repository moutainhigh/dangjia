package com.dangjia.acg.service.home;

import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.home.HomeCollocationDTO;
import com.dangjia.acg.dto.home.HomeMasterplateDTO;
import com.dangjia.acg.dto.home.HomeTemplateDTO;
import com.dangjia.acg.mapper.home.IHomeCollocationMapper;
import com.dangjia.acg.mapper.home.IHomeMasterplateMapper;
import com.dangjia.acg.mapper.home.IHomeTemplateMapper;
import com.dangjia.acg.mapper.user.UserMapper;
import com.dangjia.acg.modle.home.HomeCollocation;
import com.dangjia.acg.modle.home.HomeMasterplate;
import com.dangjia.acg.modle.home.HomeTemplate;
import com.dangjia.acg.modle.user.MainUser;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Ruking.Cheng
 * @descrilbe 首页配置实现
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/6/13 3:22 PM
 */
@Service
public class HomeService {
    @Autowired
    private IHomeCollocationMapper iHomeCollocationMapper;
    @Autowired
    private IHomeMasterplateMapper iHomeMasterplateMapper;
    @Autowired
    private IHomeTemplateMapper iHomeTemplateMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private UserMapper userMapper;

    //-------------------模版---------------------//

    public ServerResponse addHomeTemplate(String userId, String name) {
        if (CommonUtil.isEmpty(userId)) {
            return ServerResponse.createbyUserTokenError();
        }
        if (CommonUtil.isEmpty(name)) {
            return ServerResponse.createByErrorMessage("请输入名称");
        }
        HomeTemplate homeTemplate = new HomeTemplate();
        homeTemplate.setUserId(userId);
        homeTemplate.setName(name);
        homeTemplate.setEnable(0);
        iHomeTemplateMapper.insert(homeTemplate);
        return ServerResponse.createBySuccessMessage("添加成功");
    }

    public ServerResponse getHomeTemplateList(PageDTO pageDTO) {
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        Example example = new Example(HomeTemplate.class);
        example.createCriteria().andEqualTo(HomeTemplate.DATA_STATUS, 0);
        List<HomeTemplate> homeTemplates = iHomeTemplateMapper.selectByExample(example);
        if (homeTemplates.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        PageInfo pageResult = new PageInfo(homeTemplates);
        List<HomeTemplateDTO> homeTemplateDTOS = new ArrayList<>();
        for (HomeTemplate homeTemplate : homeTemplates) {
            HomeTemplateDTO homeTemplateDTO = new HomeTemplateDTO();
            homeTemplateDTO.setId(homeTemplate.getId());
            homeTemplateDTO.setDataStatus(homeTemplate.getDataStatus());
            homeTemplateDTO.setCreateDate(homeTemplate.getCreateDate());
            homeTemplateDTO.setModifyDate(homeTemplate.getModifyDate());
            homeTemplateDTO.setName(homeTemplate.getName());
            homeTemplateDTO.setEnable(homeTemplate.getEnable());
            MainUser mainUser = userMapper.selectByPrimaryKey(homeTemplate.getUserId());
            if (mainUser != null) {
                homeTemplateDTO.setUserId(mainUser.getId());
                homeTemplateDTO.setUserName(mainUser.getUsername());
                homeTemplateDTO.setUserMobile(mainUser.getMobile());
            }
            homeTemplateDTOS.add(homeTemplateDTO);
        }
        pageResult.setList(homeTemplateDTOS);
        return ServerResponse.createBySuccess("查询模版列表成功", pageResult);
    }

    public ServerResponse upDataHomeTemplate(String userId, String name, String templateId) {
        if (CommonUtil.isEmpty(userId)) {
            return ServerResponse.createbyUserTokenError();
        }
        HomeTemplate homeTemplate = iHomeTemplateMapper.selectByPrimaryKey(templateId);
        if (homeTemplate == null) {
            return ServerResponse.createByErrorMessage("模版不存在");
        }
        if (CommonUtil.isEmpty(name)) {
            return ServerResponse.createByErrorMessage("请输入名称");
        }
        homeTemplate.setUserId(userId);
        homeTemplate.setName(name);
        homeTemplate.setModifyDate(new Date());
        iHomeTemplateMapper.updateByPrimaryKeySelective(homeTemplate);
        return ServerResponse.createBySuccessMessage("修改成功");
    }

    public ServerResponse setHomeTemplateEnable(String userId, String templateId) {
        if (CommonUtil.isEmpty(userId)) {
            return ServerResponse.createbyUserTokenError();
        }
        HomeTemplate homeTemplate = iHomeTemplateMapper.selectByPrimaryKey(templateId);
        if (homeTemplate == null) {
            return ServerResponse.createByErrorMessage("模版不存在");
        }
        Example example = new Example(HomeTemplate.class);
        example.createCriteria().andEqualTo(HomeTemplate.ENABLE, 1)
                .andEqualTo(HomeTemplate.DATA_STATUS, 0);
        List<HomeTemplate> homeTemplates = iHomeTemplateMapper.selectByExample(example);
        for (HomeTemplate template : homeTemplates) {
            template.setUserId(userId);
            template.setEnable(0);
            template.setModifyDate(new Date());
            iHomeTemplateMapper.updateByPrimaryKeySelective(template);
        }
        homeTemplate.setUserId(userId);
        homeTemplate.setEnable(1);
        homeTemplate.setModifyDate(new Date());
        iHomeTemplateMapper.updateByPrimaryKeySelective(homeTemplate);
        return ServerResponse.createBySuccessMessage("修改成功");
    }

    public ServerResponse delHomeTemplate(String userId, String templateId) {
        if (CommonUtil.isEmpty(userId)) {
            return ServerResponse.createbyUserTokenError();
        }
        HomeTemplate homeTemplate = iHomeTemplateMapper.selectByPrimaryKey(templateId);
        if (homeTemplate == null) {
            return ServerResponse.createByErrorMessage("模版不存在");
        }
        homeTemplate.setUserId(userId);
        homeTemplate.setDataStatus(1);
        homeTemplate.setModifyDate(new Date());
        iHomeTemplateMapper.updateByPrimaryKeySelective(homeTemplate);
        return ServerResponse.createBySuccessMessage("删除成功");
    }

    //-------------------模版详情---------------------//

    public ServerResponse getAppHomeCollocation(String templateId) {
        String imageAddress = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        Example example = new Example(HomeCollocation.class);
        Example.Criteria criteria = example.createCriteria()
                .andEqualTo(HomeCollocation.DATA_STATUS, 0);
        if (CommonUtil.isEmpty(templateId)) {
            Example example2 = new Example(HomeTemplate.class);
            example2.createCriteria()
                    .andEqualTo(HomeTemplate.ENABLE, 1)
                    .andEqualTo(HomeTemplate.DATA_STATUS, 0);
            List<HomeTemplate> homeTemplates = iHomeTemplateMapper.selectByExample(example2);
            if (homeTemplates.size() <= 0) {
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            }
            criteria.andEqualTo(HomeCollocation.TEMPLATE_ID, homeTemplates.get(0).getId());
        } else {
            criteria.andEqualTo(HomeCollocation.TEMPLATE_ID, templateId);
        }
        example.orderBy(HomeCollocation.MODIFY_DATE).desc();
        List<HomeCollocation> homeCollocationList = iHomeCollocationMapper.selectByExample(example);
        if (homeCollocationList.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        List<HomeMasterplateDTO> homeMasterplateDTOS = new ArrayList<>();
        HomeCollocation collocation = homeCollocationList.get(0);
        if (CommonUtil.isEmpty(collocation.getMasterpieceIds())) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        String[] masterpieceIds = collocation.getMasterpieceIds().split(",");
        int i = 0;
        for (String masterpieceId : masterpieceIds) {
            HomeMasterplate homeMasterplate = iHomeMasterplateMapper.selectByPrimaryKey(masterpieceId);
            HomeMasterplateDTO homeMasterplateDTO = getHomeMasterplateDTO(homeMasterplate, imageAddress);
            if (homeMasterplateDTO != null) {
                homeMasterplateDTO.setSort(i++);
                homeMasterplateDTOS.add(homeMasterplateDTO);
            }
        }
        if (homeMasterplateDTOS.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        return ServerResponse.createBySuccess("查询成功", homeMasterplateDTOS);
    }

    public ServerResponse setAppHomeCollocation(String templateId, String userId, String masterpieceIds) {
        if (CommonUtil.isEmpty(userId)) {
            return ServerResponse.createbyUserTokenError();
        }
        if (CommonUtil.isEmpty(templateId)) {
            return ServerResponse.createByErrorMessage("请传入模板ID");
        }
        HomeTemplate homeTemplate = iHomeTemplateMapper.selectByPrimaryKey(templateId);
        if (homeTemplate == null) {
            return ServerResponse.createByErrorMessage("模版不存在");
        }
        homeTemplate.setUserId(userId);
        homeTemplate.setModifyDate(new Date());
        iHomeTemplateMapper.updateByPrimaryKeySelective(homeTemplate);
        HomeCollocation homeCollocation = new HomeCollocation();
        homeCollocation.setUserId(userId);
        homeCollocation.setTemplateId(templateId);
        homeCollocation.setMasterpieceIds(masterpieceIds);
        iHomeCollocationMapper.insert(homeCollocation);
        return ServerResponse.createBySuccessMessage("操作成功");
    }

    public ServerResponse getAppHomeCollocationHistory(String templateId, PageDTO pageDTO) {
        if (CommonUtil.isEmpty(templateId)) {
            return ServerResponse.createByErrorMessage("请传入模板ID");
        }
        String imageAddress = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        Example example = new Example(HomeCollocation.class);
        example.createCriteria()
                .andEqualTo(HomeCollocation.TEMPLATE_ID, templateId)
                .andEqualTo(HomeCollocation.DATA_STATUS, 0);
        example.orderBy(HomeCollocation.MODIFY_DATE).desc();
        List<HomeCollocation> homeCollocationList = iHomeCollocationMapper.selectByExample(example);
        if (homeCollocationList.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        PageInfo pageResult = new PageInfo(homeCollocationList);
        List<HomeCollocationDTO> homeCollocationDTOS = new ArrayList<>();
        for (HomeCollocation homeCollocation : homeCollocationList) {
            HomeCollocationDTO homeCollocationDTO = new HomeCollocationDTO();
            homeCollocationDTO.setId(homeCollocation.getId());
            homeCollocationDTO.setDataStatus(homeCollocation.getDataStatus());
            homeCollocationDTO.setCreateDate(homeCollocation.getCreateDate());
            homeCollocationDTO.setModifyDate(homeCollocation.getModifyDate());
            homeCollocationDTO.setMasterpieceIds(homeCollocation.getMasterpieceIds());
            MainUser mainUser = userMapper.selectByPrimaryKey(homeCollocation.getUserId());
            if (mainUser != null) {
                homeCollocationDTO.setUserId(mainUser.getId());
                homeCollocationDTO.setUserName(mainUser.getUsername());
                homeCollocationDTO.setUserMobile(mainUser.getMobile());
            }
            String[] masterpieceIds = homeCollocation.getMasterpieceIds().split(",");
            List<HomeMasterplateDTO> homeMasterplateDTOS = new ArrayList<>();
            for (int i = 0; i < masterpieceIds.length; i++) {
                HomeMasterplate homeMasterplate = iHomeMasterplateMapper.selectByPrimaryKey(masterpieceIds[i]);
                HomeMasterplateDTO homeMasterplateDTO = getHomeMasterplateDTO(homeMasterplate, imageAddress);
                if (homeMasterplateDTO != null) {
                    homeMasterplateDTO.setSort(i);
                    homeMasterplateDTOS.add(homeMasterplateDTO);
                }
            }
            homeCollocationDTO.setMasterplateList(homeMasterplateDTOS);
            homeCollocationDTOS.add(homeCollocationDTO);
        }
        pageResult.setList(homeCollocationDTOS);
        return ServerResponse.createBySuccess("查询历史记录成功", pageResult);
    }

    //-------------------模块---------------------//

    public ServerResponse getHomeMasterplateList(PageDTO pageDTO) {
        String imageAddress = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        Example example = new Example(HomeMasterplate.class);
        example.createCriteria()
                .andEqualTo(HomeMasterplate.DATA_STATUS, 0);
        example.orderBy(HomeMasterplate.MODIFY_DATE).desc();
        List<HomeMasterplate> homeMasterplates = iHomeMasterplateMapper.selectByExample(example);
        if (homeMasterplates.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        PageInfo pageResult = new PageInfo(homeMasterplates);
        List<HomeMasterplateDTO> homeMasterplateDTOS = new ArrayList<>();
        for (HomeMasterplate homeMasterplate : homeMasterplates) {
            homeMasterplateDTOS.add(getHomeMasterplateDTO(homeMasterplate, imageAddress));
        }
        pageResult.setList(homeMasterplateDTOS);
        return ServerResponse.createBySuccess("查询用户列表成功", pageResult);
    }

    public ServerResponse addHomeMasterplate(String name, String image, String url, String userId) {
        if (CommonUtil.isEmpty(name)) {
            return ServerResponse.createByErrorMessage("请输入名称");
        }
        if (CommonUtil.isEmpty(image)) {
            return ServerResponse.createByErrorMessage("请选择图片");
        }
        if (CommonUtil.isEmpty(url)) {
            return ServerResponse.createByErrorMessage("请输入H5对应的组件名");
        }
        if (CommonUtil.isEmpty(userId)) {
            return ServerResponse.createbyUserTokenError();
        }
        HomeMasterplate homeMasterplate = new HomeMasterplate();
        homeMasterplate.setName(name);
        homeMasterplate.setImage(image);
        homeMasterplate.setUrl(url);
        homeMasterplate.setUserId(userId);
        iHomeMasterplateMapper.insert(homeMasterplate);
        return ServerResponse.createBySuccessMessage("添加成功");
    }

    public ServerResponse delHomeMasterplate(String id, String userId) {
        HomeMasterplate homeMasterplate = iHomeMasterplateMapper.selectByPrimaryKey(id);
        if (homeMasterplate == null) {
            return ServerResponse.createByErrorMessage("该模块不存在");
        }
        if (CommonUtil.isEmpty(userId)) {
            return ServerResponse.createbyUserTokenError();
        }
        homeMasterplate.setUserId(userId);
        homeMasterplate.setDataStatus(1);
        homeMasterplate.setModifyDate(new Date());
        iHomeMasterplateMapper.updateByPrimaryKeySelective(homeMasterplate);
        return ServerResponse.createBySuccessMessage("删除成功");
    }

    public ServerResponse upDataHomeMasterplate(String id, String name, String image, String url, String userId) {
        HomeMasterplate homeMasterplate = iHomeMasterplateMapper.selectByPrimaryKey(id);
        if (homeMasterplate == null) {
            return ServerResponse.createByErrorMessage("该模块不存在");
        }
        if (CommonUtil.isEmpty(userId)) {
            return ServerResponse.createbyUserTokenError();
        }
        if (!CommonUtil.isEmpty(name)) {
            homeMasterplate.setName(name);
        }
        if (!CommonUtil.isEmpty(image)) {
            homeMasterplate.setImage(image);
        }
        if (!CommonUtil.isEmpty(url)) {
            homeMasterplate.setUrl(url);
        }
        homeMasterplate.setUserId(userId);
        homeMasterplate.setModifyDate(new Date());
        iHomeMasterplateMapper.updateByPrimaryKeySelective(homeMasterplate);
        return ServerResponse.createBySuccessMessage("修改成功");
    }


    /**
     * 模块通过数据库实体转发为返回体
     *
     * @param homeMasterplate 数据库实体
     * @return 返回体
     */
    private HomeMasterplateDTO getHomeMasterplateDTO(HomeMasterplate homeMasterplate, String imageAddress) {
        if (homeMasterplate == null) {
            return null;
        }
        HomeMasterplateDTO homeMasterplateDTO = new HomeMasterplateDTO();
        homeMasterplateDTO.setId(homeMasterplate.getId());
        homeMasterplateDTO.setDataStatus(homeMasterplate.getDataStatus());
        homeMasterplateDTO.setCreateDate(homeMasterplate.getCreateDate());
        homeMasterplateDTO.setModifyDate(homeMasterplate.getModifyDate());
        homeMasterplateDTO.setName(homeMasterplate.getName());
        homeMasterplateDTO.setImage(homeMasterplate.getImage());
        homeMasterplateDTO.setImageAddress(CommonUtil.isEmpty(homeMasterplate.getImage()) ? null : imageAddress + homeMasterplate.getImage());
        homeMasterplateDTO.setUrl(homeMasterplate.getUrl());
        MainUser mainUser = userMapper.selectByPrimaryKey(homeMasterplate.getUserId());
        if (mainUser != null) {
            homeMasterplateDTO.setUserId(mainUser.getId());
            homeMasterplateDTO.setUserName(mainUser.getUsername());
            homeMasterplateDTO.setUserMobile(mainUser.getMobile());
        }
        return homeMasterplateDTO;
    }
}
