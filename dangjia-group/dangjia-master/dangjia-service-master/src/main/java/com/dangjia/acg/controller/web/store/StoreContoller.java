package com.dangjia.acg.controller.web.store;


import com.dangjia.acg.api.web.store.StoreAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.store.Store;
import com.dangjia.acg.service.store.StoreServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/6/14
 * Time: 16:23
 */
@RestController
public class StoreContoller implements StoreAPI {
    @Autowired
    private StoreServices storeServices;


    @Override
    @ApiMethod
    public ServerResponse addStore(Store store) {
        return storeServices.addStore(store);
    }

    @Override
    @ApiMethod
    public ServerResponse queryStore(String cityId, String storeName) {
        return storeServices.queryStore(cityId,storeName);
    }

    @Override
    @ApiMethod
    public ServerResponse updateStore(Store store) {
        return storeServices.updateStore(store);
    }

    @Override
    @ApiMethod
    public ServerResponse delStore(String id) {
        return storeServices.delStore(id);
    }

    @Override
    @ApiMethod
    public ServerResponse queryStoreSubscribe(String searchKey) {
        return storeServices.queryStoreSubscribe(searchKey);
    }
}
