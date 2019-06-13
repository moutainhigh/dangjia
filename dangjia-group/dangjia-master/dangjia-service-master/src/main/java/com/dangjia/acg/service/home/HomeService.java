package com.dangjia.acg.service.home;

import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.home.HomeCollocationDTO;
import com.dangjia.acg.dto.home.HomeMasterplateDTO;
import com.dangjia.acg.mapper.home.IHomeCollocationMapper;
import com.dangjia.acg.mapper.home.IHomeMasterplateMapper;
import com.dangjia.acg.mapper.user.UserMapper;
import com.dangjia.acg.modle.home.HomeCollocation;
import com.dangjia.acg.modle.home.HomeMasterplate;
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
    private ConfigUtil configUtil;
    @Autowired
    private UserMapper userMapper;


    public ServerResponse getAppHomeCollocation() {
        Example example = new Example(HomeCollocation.class);
        example.createCriteria()
                .andEqualTo(HomeCollocation.DATA_STATUS, 0);
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
            HomeMasterplateDTO homeMasterplateDTO = getHomeMasterplateDTO(homeMasterplate);
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

    private HomeMasterplateDTO getHomeMasterplateDTO(HomeMasterplate homeMasterplate) {
        if (homeMasterplate == null) {
            return null;
        }
        String imageAddress = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
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

    public ServerResponse setAppHomeCollocation(String userId, String masterpieceIds) {
        if (CommonUtil.isEmpty(userId)) {
            return ServerResponse.createbyUserTokenError();
        }
        HomeCollocation homeCollocation = new HomeCollocation();
        homeCollocation.setUserId(userId);
        homeCollocation.setMasterpieceIds(masterpieceIds);
        iHomeCollocationMapper.insert(homeCollocation);
        return ServerResponse.createBySuccessMessage("操作成功");
    }

    public ServerResponse getAppHomeCollocationHistory(PageDTO pageDTO) {
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        Example example = new Example(HomeCollocation.class);
        example.createCriteria()
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
                HomeMasterplateDTO homeMasterplateDTO = getHomeMasterplateDTO(homeMasterplate);
                if (homeMasterplateDTO != null) {
                    homeMasterplateDTO.setSort(i);
                    homeMasterplateDTOS.add(homeMasterplateDTO);
                }
            }
            homeCollocationDTO.setMasterplateList(homeMasterplateDTOS);
            homeCollocationDTOS.add(homeCollocationDTO);
        }
        pageResult.setList(homeCollocationDTOS);
        return ServerResponse.createBySuccess("查询用户列表成功", pageResult);
    }

    public ServerResponse getHomeMasterplateList(PageDTO pageDTO) {
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
            homeMasterplateDTOS.add(getHomeMasterplateDTO(homeMasterplate));
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
}
