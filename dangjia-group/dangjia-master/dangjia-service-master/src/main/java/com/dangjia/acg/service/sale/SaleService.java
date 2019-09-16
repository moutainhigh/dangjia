package com.dangjia.acg.service.sale;

import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.auth.config.RedisSessionDAO;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.mapper.store.IStoreMapper;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.store.Store;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class SaleService {
    @Autowired
    private IStoreMapper iStoreMapper;
    @Autowired
    private CraftsmanConstructionService constructionService;
    @Autowired
    private RedisClient redisClient;
    private static Logger logger = LoggerFactory.getLogger(RedisSessionDAO.class);

    public ServerResponse getUserStoreList(String userToken, PageDTO pageDTO) {
        Object object = constructionService.getAccessToken(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        AccessToken accessToken = (AccessToken) object;
        if (CommonUtil.isEmpty(accessToken.getUserId())) {
            return ServerResponse.createbyUserTokenError();
        }
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        Example example = new Example(Store.class);
        example.createCriteria().andEqualTo(Store.USER_ID, accessToken.getUserId())
                .andEqualTo(Store.DATA_STATUS, 0);
        List<Store> storeList = iStoreMapper.selectByExample(example);
        if (storeList.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        PageInfo pageResult = new PageInfo(storeList);
        return ServerResponse.createBySuccess("查询成功", pageResult);
    }

    public ServerResponse getUserStore(String userToken) {
        Object object = constructionService.getAccessToken(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        AccessToken accessToken = (AccessToken) object;
        if (CommonUtil.isEmpty(accessToken.getUserId())) {
            return ServerResponse.createbyUserTokenError();
        }
        object = getStore(accessToken.getUserId());
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        return ServerResponse.createBySuccess("查询成功", object);
    }

    /**
     * 获取用户当前选择的门店
     *
     * @param userId 用户ID
     * @return
     */
    public Object getStore(String userId) {
        String storeId = redisClient.getCache("storeId" + userId, String.class);
        if (!CommonUtil.isEmpty(storeId)) {
            Store store = iStoreMapper.selectByPrimaryKey(storeId);
            if (store != null) {
                if (store.getUserId().equals(userId)) {
                    return store;
                } else {
                    redisClient.deleteCache("storeId" + userId);
                }
            }
        }
        PageHelper.startPage(0, 1);
        Example example = new Example(Store.class);
        example.createCriteria().andEqualTo(Store.USER_ID, userId)
                .andEqualTo(Store.DATA_STATUS, 0);
        List<Store> storeList = iStoreMapper.selectByExample(example);
        if (storeList.size() <= 0) {
            return ServerResponse.createbyUserTokenError();
        }
        Store store = storeList.get(0);
        redisClient.put("storeId" + userId, store.getId());
        return store;
    }

    public ServerResponse setUserStore(String userToken, String storeId) {
        Object object = constructionService.getAccessToken(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        AccessToken accessToken = (AccessToken) object;
        if (CommonUtil.isEmpty(accessToken.getUserId())) {
            return ServerResponse.createbyUserTokenError();
        }
        redisClient.put("storeId" + accessToken.getUserId(), storeId);
        return ServerResponse.createBySuccessMessage("操作成功");
    }
}
