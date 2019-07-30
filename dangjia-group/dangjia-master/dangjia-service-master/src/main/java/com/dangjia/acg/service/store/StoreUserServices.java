package com.dangjia.acg.service.store;

import com.dangjia.acg.api.MessageAPI;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.enums.AppType;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.sale.store.StoreUserDTO;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.store.IStoreMapper;
import com.dangjia.acg.mapper.store.IStoreUserMapper;
import com.dangjia.acg.mapper.user.UserMapper;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.store.Store;
import com.dangjia.acg.modle.store.StoreUser;
import com.dangjia.acg.modle.user.MainUser;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.dangjia.acg.service.sale.SaleService;
import com.dangjia.acg.service.sale.client.ClientService;
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
    private IStoreMapper iStoreMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private CraftsmanConstructionService constructionService;
    @Autowired
    private SaleService saleService;
    @Autowired
    private ClientService clientService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MessageAPI messageAPI;
    @Autowired
    private IMemberMapper memberMapper;

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

    public ServerResponse getStoreUser(String userToken, String userId) {
        Object object = constructionService.getAccessToken(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        AccessToken accessToken = (AccessToken) object;
        if (CommonUtil.isEmpty(accessToken.getUserId())) {
            return ServerResponse.createbyUserTokenError();
        }
        object = saleService.getStore(accessToken.getUserId());
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Store store = (Store) object;
        Example example = new Example(StoreUser.class);
        example.createCriteria()
                .andEqualTo(StoreUser.USER_ID, userId)
                .andEqualTo(StoreUser.STORE_ID, store.getId())
                .andEqualTo(StoreUser.DATA_STATUS, 0);
        List<StoreUser> storeUserList = iStoreUserMapper.selectByExample(example);
        if (storeUserList.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        StoreUser storeUser = storeUserList.get(0);
        StoreUserDTO storeUserDTO = new StoreUserDTO();
        storeUserDTO.setStoreUserId(storeUser.getId());//门店成员ID
        storeUserDTO.setUserId(storeUser.getUserId());//成员用户ID
        storeUserDTO.setStoreId(storeUser.getStoreId());//门店ID
        storeUserDTO.setType(storeUser.getType());//类别：0:场内销售，1:场外销售
        MainUser mainUser = userMapper.selectByPrimaryKey(store.getUserId());
        if (mainUser == null) {
            return ServerResponse.createByErrorMessage("目标用户不存在");
        }
        storeUserDTO.setUserName(mainUser.getUsername());//用户名
        storeUserDTO.setUserMobile(mainUser.getMobile());//手机
        Member member = memberMapper.selectByPrimaryKey(mainUser.getMemberId());
        if (member != null) {
            String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            String imageUrl = member.getHead();
            storeUserDTO.setUserHead(CommonUtil.isEmpty(imageUrl) ? null : (imageAddress + imageUrl));
        }
        storeUserDTO.setIsJob(mainUser.getIsJob());//是否在职（0：正常；1，离职）
        storeUserDTO.setCreateDate(storeUser.getCreateDate());// 创建日期
        storeUserDTO.setModifyDate(storeUser.getModifyDate());// 修改日期
        String storeName = store.getStoreName() + (storeUser.getType() == 0 ? "-场内销售" : "-场外销售");
        storeUserDTO.setStoreName(storeName);//门店——岗位名称
        storeUserDTO.setAppKey(messageAPI.getAppKey(AppType.SALE.getDesc()));//极光聊天的Key
        storeUserDTO.setOutField(clientService.getResidentialRangeDTOList(userId));
        storeUserDTO.setMonthlyTarget(clientService.getMonthlyTargetList(userId));
        return ServerResponse.createBySuccess("查询成功", storeUserDTO);
    }
}
