package com.dangjia.acg.service.store;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.mapper.store.IStoreMapper;
import com.dangjia.acg.mapper.store.IStoreSubscribeMapper;
import com.dangjia.acg.modle.store.Store;
import com.dangjia.acg.modle.store.StoreSubscribe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/6/14
 * Time: 16:25
 */
@Service
public class StoreServices {
    @Autowired
    private IStoreMapper iStoreMapper;
    @Autowired
    private IStoreSubscribeMapper iStoreSubscribeMapper;

    /**
     * 创建门店
     * @param store
     * @return
     */
    public ServerResponse addStore(Store store) {
        try {
            iStoreMapper.insert(store);
            return ServerResponse.createBySuccessMessage("创建成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("创建失败");
        }
    }

    /**
     * 查询门店
     * @param cityId
     * @param storeName
     * @return
     */
    public ServerResponse queryStore(String cityId, String storeName) {
        try {
            List<Store> stores = iStoreMapper.queryStore(cityId, storeName);
            return ServerResponse.createBySuccess("查询成功",stores);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 编辑门店
     * @param store
     * @return
     */
    public ServerResponse updateStore(Store store) {
        try {
            store.setCreateDate(null);
            iStoreMapper.updateByPrimaryKeySelective(store);
            return ServerResponse.createByErrorMessage("编辑成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("编辑失败");
        }
    }

    /**
     * 删除门店
     * @param id
     * @return
     */
    public ServerResponse delStore(String id) {
        try {
            iStoreMapper.deleteByPrimaryKey(id);
            return ServerResponse.createByErrorMessage("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("删除失败");
        }
    }

    /**
     * 门店预约查询
     * @param searchKey
     * @return
     */
    public ServerResponse queryStoreSubscribe(String searchKey) {
        try {
            List<StoreSubscribe> storeSubscribes = iStoreSubscribeMapper.queryStoreSubscribe(searchKey);
            return ServerResponse.createBySuccess("查询成功",storeSubscribes);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

}
