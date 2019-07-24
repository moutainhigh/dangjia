package com.dangjia.acg.service.store;

import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.sale.store.StoreUserDTO;
import com.dangjia.acg.mapper.store.IStoreUserMapper;
import com.dangjia.acg.modle.store.StoreUser;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;


@Service
public class StoreUserServices {
    @Autowired
    private IStoreUserMapper iStoreUserMapper;
    @Autowired
    private ConfigUtil configUtil;

    public ServerResponse addStoreUser(String userId, String storeId, Integer type) {
        if (CommonUtil.isEmpty(userId)) {
            return ServerResponse.createByErrorMessage("请选择用户");
        }
        if (CommonUtil.isEmpty(storeId)) {
            return ServerResponse.createByErrorMessage("请选择门店");
        }
        if (CommonUtil.isEmpty(type)) {
            return ServerResponse.createByErrorMessage("请选择岗位");
        }
        Example example = new Example(StoreUser.class);
        example.createCriteria().andEqualTo(StoreUser.USER_ID, userId)
                .andEqualTo(StoreUser.DATA_STATUS, 0);
        List<StoreUser> storeUserList = iStoreUserMapper.selectByExample(example);
        if (storeUserList.size() > 0) {
            if (userId.equals(storeUserList.get(0).getUserId())) {
                return ServerResponse.createByErrorMessage("该用户已在本门店存在，请勿重复添加");
            } else {
                return ServerResponse.createByErrorMessage("该用户已在其他门店存在，请勿重复添加");
            }
        }
        StoreUser storeUser = new StoreUser();
        storeUser.setStoreId(storeId);
        storeUser.setUserId(userId);
        storeUser.setType(type);
        if (iStoreUserMapper.insertSelective(storeUser) > 0) {
            return ServerResponse.createBySuccessMessage("添加成功");
        } else {
            return ServerResponse.createByErrorMessage("添加失败");
        }
    }

    public ServerResponse queryStoreUser(String storeId, String searchKey, PageDTO pageDTO) {
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<StoreUserDTO> storeUserDTOS = iStoreUserMapper.getStoreUsers(storeId, searchKey);
        if (storeUserDTOS.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        PageInfo pageResult = new PageInfo(storeUserDTOS);
        String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
        for (StoreUserDTO storeUserDTO : storeUserDTOS) {
            String imageUrl = storeUserDTO.getUserHead();
            storeUserDTO.setUserHead(CommonUtil.isEmpty(imageUrl) ? null : (imageAddress + imageUrl));
        }
        pageResult.setList(storeUserDTOS);
        return ServerResponse.createBySuccess("查询成功", pageResult);
    }

    public ServerResponse updateStoreUser(String storeUserId, Integer type) {
        StoreUser storeUser = iStoreUserMapper.selectByPrimaryKey(storeUserId);
        if (storeUser == null) {
            return ServerResponse.createByErrorMessage("该用户不存在");
        }
        storeUser.setType(type);
        storeUser.setModifyDate(new Date());
        if (iStoreUserMapper.updateByPrimaryKeySelective(storeUser) > 0) {
            return ServerResponse.createBySuccessMessage("修改成功");
        } else {
            return ServerResponse.createByErrorMessage("修改失败");
        }
    }

    public ServerResponse delStoreUser(String storeUserId) {
        StoreUser storeUser = iStoreUserMapper.selectByPrimaryKey(storeUserId);
        if (storeUser == null) {
            return ServerResponse.createByErrorMessage("该用户不存在");
        }
        storeUser.setDataStatus(1);
        storeUser.setModifyDate(new Date());
        if (iStoreUserMapper.updateByPrimaryKeySelective(storeUser) > 0) {
            return ServerResponse.createBySuccessMessage("删除成功");
        } else {
            return ServerResponse.createByErrorMessage("删除失败");
        }
    }
}
