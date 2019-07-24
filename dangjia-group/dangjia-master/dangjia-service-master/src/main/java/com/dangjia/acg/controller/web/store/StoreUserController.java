package com.dangjia.acg.controller.web.store;


import com.dangjia.acg.api.web.store.StoreUserAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.store.StoreUserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class StoreUserController implements StoreUserAPI {
    @Autowired
    private StoreUserServices storeUserServices;

    @Override
    public ServerResponse addStoreUser(HttpServletRequest request, String userId, String storeId, Integer type) {
        return storeUserServices.addStoreUser(userId, storeId, type);
    }

    @Override
    @ApiMethod
    public ServerResponse queryStoreUser(String storeId, String searchKey, PageDTO pageDTO) {
        return storeUserServices.queryStoreUser(storeId, searchKey, pageDTO);
    }

    @Override
    public ServerResponse updateStoreUser(HttpServletRequest request, String storeUserId, Integer type) {
        return storeUserServices.updateStoreUser(storeUserId, type);
    }

    @Override
    @ApiMethod
    public ServerResponse delStoreUser(String storeUserId) {
        return storeUserServices.delStoreUser(storeUserId);
    }
}
