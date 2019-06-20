package com.dangjia.acg.service.store;

import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.mapper.store.IStoreMapper;
import com.dangjia.acg.mapper.store.IStoreSubscribeMapper;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.store.Store;
import com.dangjia.acg.modle.store.StoreSubscribe;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
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
    @Autowired
    private RedisClient redisClient;

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
            return ServerResponse.createBySuccessMessage("编辑成功");
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
            return ServerResponse.createBySuccessMessage("删除成功");
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
    public ServerResponse queryStoreSubscribe(String searchKey, PageDTO pageDTO) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<StoreSubscribe> storeSubscribes = iStoreSubscribeMapper.queryStoreSubscribe(searchKey);
            PageInfo pageResult=new PageInfo(storeSubscribes);
            return ServerResponse.createBySuccess("查询成功",pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 门店预约插入
     * @param storeId 门店ID
     * @param storeName 门店名称
     * @param customerName 客户名称
     * @param customerPhone 客户电话
     * @param modifyDate 预约时间
     * @return
     */
    public ServerResponse storeSubscribe(String storeId, String storeName, String customerName, String customerPhone, Date modifyDate) {
        try {
            StoreSubscribe storeSubscribe=new StoreSubscribe();
            storeSubscribe.setStoreId(storeId);
            storeSubscribe.setStoreName(storeName);
            storeSubscribe.setCustomerName(customerName);
            storeSubscribe.setCustomerPhone(customerPhone);
            storeSubscribe.setModifyDate(modifyDate);
            iStoreSubscribeMapper.insert(storeSubscribe);
            return ServerResponse.createBySuccessMessage("预约成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("预约成功");
        }
    }

    /**
     * 查询门店
     * @param cityId
     * @param storeName
     * @return
     */
    public ServerResponse queryStoreDistance(PageDTO pageDTO,String cityId, String storeName) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<Store> stores = iStoreMapper.queryStoreDistance(cityId, storeName);
            PageInfo pageResult = new PageInfo(stores);
            return ServerResponse.createBySuccess("查询成功",pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 首页查询门店
     * @param userToken
     * @param latitude
     * @param longitude
     * @return
     */
    public ServerResponse IndexqueryStore(String userToken, String latitude, String longitude) {
        try {
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            List list=new ArrayList();
            Member member =null;
            if(null!=accessToken) {
                member = accessToken.getMember();
            }
            List<Store> stores = iStoreMapper.IndexqueryStore(latitude, longitude);
            list.add(member);
            list.add(stores);
            return ServerResponse.createBySuccess("查询成功",list);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }
}
