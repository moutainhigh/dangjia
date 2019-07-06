package com.dangjia.acg.service.store;

import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.JsmsUtil;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.store.IStoreMapper;
import com.dangjia.acg.mapper.store.IStoreSubscribeMapper;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.store.Store;
import com.dangjia.acg.modle.store.StoreSubscribe;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.util.StringUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
    private IMemberMapper memberMapper;
    @Autowired
    private CraftsmanConstructionService constructionService;

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
    public ServerResponse queryStore(String cityId, String storeName,PageDTO pageDTO) {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<Store> stores = iStoreMapper.queryStore(cityId, storeName);
            if(stores.size()<0){
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            }
            PageInfo pageResult=new PageInfo(stores);
            return ServerResponse.createBySuccess("查询成功",pageResult);
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
            return ServerResponse.createByErrorMessage("编辑成功");
        }
    }

    /**
     * 删除门店
     * @param id
     * @return
     */
    public ServerResponse delStore(String id) {
        try {
            Store store=new Store();
            store.setId(id);
            store.setDataStatus(1);
            iStoreMapper.updateByPrimaryKeySelective(store);
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
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<StoreSubscribe> storeSubscribes = iStoreSubscribeMapper.queryStoreSubscribe(searchKey);
            if(storeSubscribes.size()<0){
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            }
            PageInfo pageResult=new PageInfo(storeSubscribes);
            return ServerResponse.createBySuccess("查询成功",pageResult);
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
            if(iStoreSubscribeMapper.insert(storeSubscribe)>0) {
                Map<String, String> temp_para = new HashMap();
                temp_para.put("time", new SimpleDateFormat("yyyy-MM-dd").format(modifyDate));
                temp_para.put("name", storeName);
                Store store = iStoreMapper.selectByPrimaryKey(storeId);
                temp_para.put("address",store.getStoreAddress());
                temp_para.put("phone",store.getReservationNumber());
                //给预约客户发送短信
                JsmsUtil.sendSMS(customerPhone, "166800", temp_para);
            }
            return ServerResponse.createBySuccessMessage("预约成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("预约失败");
        }
    }

    /**
     * 查询门店
     * @param cityId
     * @param storeName
     * @return
     */
    public ServerResponse queryStoreDistance(PageDTO pageDTO,String cityId, String storeName) {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<Store> stores = iStoreMapper.queryStoreDistance(cityId, storeName);
            if(stores.size()<0){
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            }
            PageInfo pageResult = new PageInfo(stores);
            return ServerResponse.createBySuccess("查询成功",pageResult);
    }


    /**
     * 首页查询门店
     * @param latitude
     * @param longitude
     * @return
     */
    public ServerResponse IndexqueryStore(String cityId,String latitude, String longitude) {
            if (CommonUtil.isEmpty(latitude)) {
                latitude = "28.228259";
            }
            if (CommonUtil.isEmpty(longitude)) {
                longitude = "112.938904";
            }
            List<Store> stores = iStoreMapper.IndexqueryStore(cityId,latitude, longitude);
            if(stores.size()<=0){
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            }
            return ServerResponse.createBySuccess("查询成功",stores);
    }
}
