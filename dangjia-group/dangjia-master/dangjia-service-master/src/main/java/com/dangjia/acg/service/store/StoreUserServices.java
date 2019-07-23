package com.dangjia.acg.service.store;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.mapper.store.IStoreMapper;
import com.dangjia.acg.modle.store.StoreUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class StoreUserServices {
    @Autowired
    private IStoreMapper iStoreMapper;

    public ServerResponse addStoreUser(StoreUser storeUser) {
        return null;
    }

    public ServerResponse queryStoreUser(String storeId, String userName, PageDTO pageDTO) {
        return null;
    }

    public ServerResponse updateStoreUser(StoreUser storeUser) {
        return null;
    }

    public ServerResponse delStoreUser(String storeUserId) {
        return null;
    }
}
