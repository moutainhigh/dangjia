package com.dangjia.acg.controller.web.store;


import com.dangjia.acg.api.web.store.StoreUserAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.store.StoreUser;
import com.dangjia.acg.service.store.StoreUserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class StoreUserController implements StoreUserAPI {
    @Autowired
    private StoreUserServices storeUserServices;

    @Override
    @ApiMethod
    public ServerResponse addStoreUser(HttpServletRequest request, StoreUser storeUser) {
        return storeUserServices.addStoreUser(storeUser);
    }

    @Override
    @ApiMethod
    public ServerResponse queryStoreUser(String storeId, String userName, PageDTO pageDTO) {
        return storeUserServices.queryStoreUser(storeId, userName, pageDTO);
    }

    @Override
    @ApiMethod
    public ServerResponse updateStoreUser(HttpServletRequest request, StoreUser storeUser) {
        return storeUserServices.updateStoreUser(storeUser);
    }

    @Override
    @ApiMethod
    public ServerResponse delStoreUser(String storeUserId) {
        return storeUserServices.delStoreUser(storeUserId);
    }
}
