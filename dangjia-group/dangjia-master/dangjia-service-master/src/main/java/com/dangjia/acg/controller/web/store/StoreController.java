package com.dangjia.acg.controller.web.store;


import com.dangjia.acg.api.web.store.StoreAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.store.Store;
import com.dangjia.acg.service.store.StoreServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/6/14
 * Time: 16:23
 */
@RestController
public class StoreController implements StoreAPI {
    @Autowired
    private StoreServices storeServices;

    @Override
    @ApiMethod
    public ServerResponse getStorePrecinctVillage(HttpServletRequest request, String storeId) {
        return storeServices.getStorePrecinctVillage(request, storeId);
    }

    @Override
    @ApiMethod
    public ServerResponse addStore(Store store) {
        return storeServices.addStore(store);
    }

    @Override
    @ApiMethod
    public ServerResponse queryStore(HttpServletRequest request, String cityId, String storeName, PageDTO pageDTO) {
        return storeServices.queryStore(request, cityId, storeName, pageDTO);
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
    public ServerResponse queryStoreSubscribe(String searchKey, PageDTO pageDTO, String state) {
        return storeServices.queryStoreSubscribe(searchKey, pageDTO, state);
    }

    /**
     * 门店预约插入
     *
     * @param storeId       门店ID
     * @param storeName     门店名称
     * @param customerName  客户名称
     * @param customerPhone 客户电话
     * @param modifyDate    预约时间
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse storeSubscribe(String storeId, String storeName, String customerName, String customerPhone, Date modifyDate) {
        return storeServices.storeSubscribe(storeId, storeName, customerName, customerPhone, modifyDate);
    }

    @Override
    @ApiMethod
    public ServerResponse queryStoreDistance(PageDTO pageDTO, String cityId, String storeName) {
        return storeServices.queryStoreDistance(pageDTO, cityId, storeName);
    }

    @Override
    @ApiMethod
    public ServerResponse IndexqueryStore(String cityId, String latitude, String longitude) {
        return storeServices.IndexqueryStore(cityId, latitude, longitude);
    }

    /**
     * 门店利润列表（利润统计）
     */
    @Override
    @ApiMethod
    public ServerResponse getStoreProfitList(HttpServletRequest request, PageDTO pageDTO, String searchKey) {
        return storeServices.getStoreProfitList(request, pageDTO, searchKey);
    }

    @Override
    @ApiMethod
    public ServerResponse callback(String storeSubscribeId, String info) {
        return storeServices.callback(storeSubscribeId, info);
    }
}
