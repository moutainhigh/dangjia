package com.dangjia.acg.service.store;

import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.GaoDeUtils;
import com.dangjia.acg.common.util.JsmsUtil;
import com.dangjia.acg.dto.house.DesignDTO;
import com.dangjia.acg.dto.repair.HouseProfitSummaryDTO;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.house.IModelingVillageMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.store.IStoreMapper;
import com.dangjia.acg.mapper.store.IStoreSubscribeMapper;
import com.dangjia.acg.mapper.system.IDepartmentMapper;
import com.dangjia.acg.modle.house.ModelingVillage;
import com.dangjia.acg.modle.other.City;
import com.dangjia.acg.modle.store.Store;
import com.dangjia.acg.modle.store.StoreSubscribe;
import com.dangjia.acg.modle.system.Department;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;


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
    private IHouseMapper iHouseMapper;
    @Autowired
    private IStoreSubscribeMapper iStoreSubscribeMapper;
    @Autowired
    private IMemberMapper memberMapper;
    @Autowired
    private CraftsmanConstructionService constructionService;
    @Autowired
    private IModelingVillageMapper modelingVillageMapper;//小区
    @Autowired
    private IDepartmentMapper departmentMapper;
    /**
     * 根据门店ID,得到设置的管辖范围，得到所有范围内的小区
     * @param request
     * @param storeId 门店ID
     * @return
     */
    public ServerResponse getStorePrecinctVillage(HttpServletRequest request,String storeId) {
        Store store = iStoreMapper.selectByPrimaryKey(storeId);
        if(store==null||CommonUtil.isEmpty(store.getScopeItude())||CommonUtil.isEmpty(store.getVillages())){
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        Example example = new Example(ModelingVillage.class);
        example.createCriteria().andIn(ModelingVillage.ID,Arrays.asList(store.getVillages().split(",")));
        List<ModelingVillage> modelingVillages = modelingVillageMapper.selectByExample(example);
        return ServerResponse.createBySuccess("查询列表成功", modelingVillages);
    }

    /**
     * 创建门店
     * @param store
     * @return
     */
    public ServerResponse addStore(Store store) {
        try {
            Example example = new Example(Store.class);
            example.createCriteria().andEqualTo(Store.STORE_NAME, store.getStoreName())
                    .andEqualTo(Store.DATA_STATUS,0);
            if (iStoreMapper.selectByExample(example).size() > 0) {
                return ServerResponse.createByErrorMessage("门店已存在");
            }
            if(!CommonUtil.isEmpty(store.getDepartmentId())) {
                Department department = departmentMapper.selectByPrimaryKey(store.getDepartmentId());
                store.setCityName(department.getCityName());
                store.setDepartmentName(department.getName());
            }
            getStoreVillages(store);
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
            if(stores.size()<=0){
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
            Store oldStore = iStoreMapper.selectByPrimaryKey(store.getId());
            if(!oldStore.getStoreName().equals(store.getStoreName())){
                Example example = new Example(Store.class);
                example.createCriteria().andEqualTo(Store.STORE_NAME, store.getStoreName())
                        .andEqualTo(Store.DATA_STATUS,0);;
                if (iStoreMapper.selectByExample(example).size() > 0) {
                    return ServerResponse.createByErrorMessage("门店已存在");
                }
            }
            if(!CommonUtil.isEmpty(store.getDepartmentId())) {
                Department department = departmentMapper.selectByPrimaryKey(store.getDepartmentId());
                store.setCityName(department.getCityName());
                store.setDepartmentName(department.getName());
            }
            getStoreVillages(store);
            store.setCreateDate(null);
            store.setModifyDate(new Date());
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
    public ServerResponse queryStoreSubscribe(String searchKey, PageDTO pageDTO, String state) {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<StoreSubscribe> storeSubscribes = iStoreSubscribeMapper.queryStoreSubscribe(searchKey,state);
            if(storeSubscribes.size()<=0){
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
            storeSubscribe.setState(0);
            storeSubscribe.setStoreId(storeId);
            storeSubscribe.setStoreName(storeName);
            storeSubscribe.setCustomerName(customerName);
            storeSubscribe.setCustomerPhone(customerPhone);
            storeSubscribe.setModifyDate(modifyDate);
            if(iStoreSubscribeMapper.insert(storeSubscribe)>0) {
                Map<String, String> temp_para = new HashMap();
//                temp_para.put("time", new SimpleDateFormat("yyyy-MM-dd").format(modifyDate));
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
            if(stores.size()<=0){
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

    /**
     *
     * @param storeSubscribeId
     * @param info
     * @return
     */
    public ServerResponse callback(String storeSubscribeId, String info) {
        StoreSubscribe storeSubscribe=new StoreSubscribe();
        storeSubscribe.setId(storeSubscribeId);
        storeSubscribe.setInfo(info);
        storeSubscribe.setState(1);
        if(iStoreSubscribeMapper.updateByPrimaryKeySelective(storeSubscribe)>0) {
            return ServerResponse.createBySuccessMessage("处理成功");
        }else{
            return ServerResponse.createByErrorMessage("处理失败");
        }
    }

    /**
     * 根据本店设置的管辖范围，得到所有范围内的小区
     * @param store
     */
    public void getStoreVillages(Store store){
        if(!CommonUtil.isEmpty(store.getScopeItude())){
            Example example = new Example(ModelingVillage.class);
            example.createCriteria().andIsNotNull(ModelingVillage.LOCATIONX);
            List<ModelingVillage> modelingVillages = modelingVillageMapper.selectByExample(example);
            List<String> villageIds = new ArrayList<>();
            for (ModelingVillage modelingVillage : modelingVillages) {
                if(GaoDeUtils.isInPolygon(modelingVillage.getLocationx()+","+modelingVillage.getLocationy(),store.getScopeItude())){
                    villageIds.add(modelingVillage.getId());
                }
            }
            if(villageIds.size()>0){
                store.setVillages(StringUtils.join(villageIds,","));
            }else{
                store.setVillages("");
            }
        }
    }

    /**
     * 门店利润列表（利润统计）
     */
    public ServerResponse getStoreProfitList(HttpServletRequest request,PageDTO pageDTO, String searchKey) {
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<Store> stores = iStoreMapper.queryStore(null, searchKey);
        List<Map> storemaps =new ArrayList<>();
        if(stores.size()<0){
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        PageInfo pageResult=new PageInfo(stores);
        for (Store store : stores) {
            Double profit = 0d;
            if(!CommonUtil.isEmpty(store.getVillages())) {
                List<DesignDTO> houseList = iHouseMapper.getHouseProfitList(store.getVillages(), null, searchKey);
                if (houseList.size() <= 0) {
                    continue;
                }
                for (DesignDTO houseListDTO : houseList) {
                    List<HouseProfitSummaryDTO> list = iHouseMapper.getHouseProfitSummary(houseListDTO.getHouseId());
                    for (HouseProfitSummaryDTO houseProfitSummaryDTO : list) {
                        if ("0".equals(houseProfitSummaryDTO.getPlus())) {
                            profit = profit + houseProfitSummaryDTO.getMoney();
                        }
                        if ("1".equals(houseProfitSummaryDTO.getPlus())) {
                            profit = profit - houseProfitSummaryDTO.getMoney();
                        }
                    }
                }
            }
            Map map= BeanUtils.beanToMap(store);
            map.put("profit",profit);
            storemaps.add(map);
        }
        pageResult.setList(storemaps);
        return ServerResponse.createBySuccess("查询成功",pageResult);
    }
}
