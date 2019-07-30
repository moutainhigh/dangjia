package com.dangjia.acg.service.store;

import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.BaseException;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.sale.store.StoreUserDTO;
import com.dangjia.acg.mapper.store.IStoreMapper;
import com.dangjia.acg.mapper.store.IStoreUserMapper;
import com.dangjia.acg.modle.store.Store;
import com.dangjia.acg.modle.store.StoreUser;
import com.dangjia.acg.modle.user.MainUser;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections.ArrayStack;
import org.apache.commons.lang3.StringUtils;
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
    private IStoreMapper iStoreMapper;
    @Autowired
    private ConfigUtil configUtil;

    @Autowired
    private RedisClient redisClient;

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
        Example example = new Example(Store.class);
        example.createCriteria().andEqualTo(Store.USER_ID, userId)
                .andEqualTo(Store.DATA_STATUS, 0);
        List<Store> stores = iStoreMapper.selectByExample(example);
        if (stores.size() > 0) {
            return ServerResponse.createByErrorMessage("该用户已被设置为店长，请勿添加");
        }
        example = new Example(StoreUser.class);
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
        List<StoreUserDTO> storeUserDTOS = iStoreUserMapper.getStoreUsers(storeId, searchKey, null);
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

    /**
     * 获取当前登陆人是否为城市管理者/门店店长/销售人员
     * @param userId 当前登陆人ID
     * @return
     */
    public String getStoreUser(String userId){
        MainUser existUser = redisClient.getCache(Constants.USER_KEY + userId, MainUser.class);
        if (null == existUser) {
            throw new BaseException(ServerCode.THE_LANDING_TIME_PLEASE_LAND_AGAIN, ServerCode.THE_LANDING_TIME_PLEASE_LAND_AGAIN.getDesc());
        }
        List<String> users=new ArrayStack();
        //获取是否为店长，可看门店所有销售的客户
        Example example = new Example(Store.class);
        example.createCriteria().andEqualTo(Store.USER_ID, userId)
                .andEqualTo(Store.DATA_STATUS, 0);
        List<Store> stores = iStoreMapper.selectByExample(example);
        if(stores.size()>0){
            for (Store store : stores) {
                example = new Example(StoreUser.class);
                example.createCriteria().andEqualTo(StoreUser.STORE_ID, store.getId())
                        .andEqualTo(StoreUser.DATA_STATUS, 0);
                List<StoreUser> storeUserList = iStoreUserMapper.selectByExample(example);
                for (StoreUser storeUser : storeUserList) {
                    users.add(storeUser.getUserId());
                }
            }
            if(users.size()>0){
                return StringUtils.join(users,",");
            }
        }else{
            //判断是否为销售，只能看自己的客户
            example = new Example(StoreUser.class);
            example.createCriteria().andEqualTo(StoreUser.USER_ID, userId)
                    .andEqualTo(StoreUser.DATA_STATUS, 0);
            List<StoreUser> storeUserList = iStoreUserMapper.selectByExample(example);
            if(storeUserList.size()>0){
                return userId;
            }
        }
        //总店，根据组织架构设置的城市控制查所有用户
        //城市管理者，根据组织架构设置的城市控制查所有用户（设置指定城市来控制）
        //既不是店长又不是销售，默认定为空，查所有
        return "";
    }

}
