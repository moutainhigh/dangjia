package com.dangjia.acg.service.activity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.activity.DjStoreActivityDTO;
import com.dangjia.acg.dto.activity.DjStoreActivityProductDTO;
import com.dangjia.acg.dto.activity.DjStoreParticipateActivitiesDTO;
import com.dangjia.acg.dto.activity.HomeLimitedPurchaseActivitieDTO;
import com.dangjia.acg.dto.product.StorefrontProductDTO;
import com.dangjia.acg.mapper.activity.DjActivitySessionMapper;
import com.dangjia.acg.mapper.activity.DjStoreActivityMapper;
import com.dangjia.acg.mapper.activity.DjStoreActivityProductMapper;
import com.dangjia.acg.mapper.activity.DjStoreParticipateActivitiesMapper;
import com.dangjia.acg.mapper.other.ICityMapper;
import com.dangjia.acg.mapper.product.IMasterStorefrontProductMapper;
import com.dangjia.acg.modle.activity.DjActivitySession;
import com.dangjia.acg.modle.activity.DjStoreActivity;
import com.dangjia.acg.modle.activity.DjStoreActivityProduct;
import com.dangjia.acg.modle.activity.DjStoreParticipateActivities;
import com.dangjia.acg.modle.other.City;
import com.dangjia.acg.modle.storefront.Storefront;
import com.dangjia.acg.modle.storefront.StorefrontProduct;
import com.dangjia.acg.service.product.MasterStorefrontService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2020/2/15
 * Time: 14:24
 */
@Service
public class DjStoreActivityService {

    @Autowired
    private DjStoreActivityMapper djStoreActivityMapper;
    @Autowired
    private DjActivitySessionMapper djActivitySessionMapper;
    @Autowired
    private ICityMapper iCityMapper;
    @Autowired
    private MasterStorefrontService masterStorefrontService;
    @Autowired
    private DjStoreParticipateActivitiesMapper djStoreParticipateActivitiesMapper;
    @Autowired
    private DjStoreActivityProductMapper djStoreActivityProductMapper;
    @Autowired
    private IMasterStorefrontProductMapper iMasterStorefrontProductMapper;
    @Autowired
    private ConfigUtil configUtil;

    /**
     * 添加活动
     *
     * @param djStoreActivity
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse addActivities(DjStoreActivity djStoreActivity){
        if (djStoreActivity.getActivityType() == 1) {
            commonality(djStoreActivity);
        }
        djStoreActivityMapper.insert(djStoreActivity);
        return ServerResponse.createBySuccessMessage("配置成功");
    }


    /**
     * 场次
     * @param djStoreActivity
     * @return
     */
    public ServerResponse getSession(DjStoreActivity djStoreActivity) {
        List<String> list=new ArrayList<>();
        //限时购
        if(djStoreActivity.getActivityType() == 1){
            //场次开始时间跟活动结束时间小于等于限购周期则只有一场
            if (DateUtil.getHourDays(djStoreActivity.getEndTime(),
                    djStoreActivity.getActivityStartTime()) <= djStoreActivity.getCyclePurchasing()) {
                list.add("第1场："+convertDate2String("yyyy-MM-dd HH:mm:ss",djStoreActivity.getActivityStartTime())
                        +" 至 "+convertDate2String("yyyy-MM-dd HH:mm:ss",djStoreActivity.getEndTime()));
            }else{
                List<String> intervalTimeList = getIntervalTimeList(convertDate2String("yyyy-MM-dd HH:mm:ss", djStoreActivity.getActivityStartTime())
                        , convertDate2String("yyyy-MM-dd HH:mm:ss", djStoreActivity.getEndTime()),
                        djStoreActivity.getCyclePurchasing());
                for (int i = 0; i <intervalTimeList.size()/2+1; i++) {
                    list.add("第"+(i+1)+"场："+intervalTimeList.get(i)
                            +" 至 "+intervalTimeList.get(i+1));
                }
            }
        }
        return ServerResponse.createBySuccess("获取成功",list);
    }


    /**
     * 获取固定间隔时刻集合
     * @param start 开始时间
     * @param end 结束时间
     * @param interval 时间间隔(单位：分钟)
     * @return
     */
    public static List<String> getIntervalTimeList(String start,String end,int interval){
        Date startDate = convertString2Date("yyyy-MM-dd HH:mm:ss",start);
        Date endDate = convertString2Date("yyyy-MM-dd HH:mm:ss",end);
        List<String> list = new ArrayList<>();
        while(startDate.getTime()<=endDate.getTime()){
            list.add(convertDate2String("yyyy-MM-dd HH:mm:ss",startDate));
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);
            calendar.add(Calendar.HOUR,interval);
            if(calendar.getTime().getTime()>endDate.getTime()){
                if(!startDate.equals(endDate)){
                    list.add(convertDate2String("yyyy-MM-dd HH:mm:ss",endDate));
                }
                startDate = calendar.getTime();
            }else{
                startDate = calendar.getTime();
            }

        }
        return list;
    }

    public static Date convertString2Date(String format, String dateStr) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        try {
            Date date = simpleDateFormat.parse(dateStr);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String convertDate2String(String format,Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

    private void commonality(DjStoreActivity djStoreActivity){
        if (DateUtil.getHourDays(djStoreActivity.getEndTime(),
                djStoreActivity.getActivityStartTime()) <= djStoreActivity.getCyclePurchasing()) {
            DjActivitySession djActivitySession=new DjActivitySession();
            djActivitySession.setSessionStartTime(djStoreActivity.getActivityStartTime());
            djActivitySession.setEndSession(djStoreActivity.getEndTime());
            djActivitySession.setSession(1);
            djActivitySession.setStoreActivityId(djStoreActivity.getId());
            djActivitySessionMapper.insert(djActivitySession);
        }else{
            List<String> intervalTimeList = getIntervalTimeList(convertDate2String("yyyy-MM-dd HH:mm:ss", djStoreActivity.getActivityStartTime())
                    , convertDate2String("yyyy-MM-dd HH:mm:ss", djStoreActivity.getEndTime()),
                    djStoreActivity.getCyclePurchasing());
            for (int i = 0; i <intervalTimeList.size()/2+1; i++) {
                DjActivitySession djActivitySession=new DjActivitySession();
                djActivitySession.setSessionStartTime(convertString2Date("yyyy-MM-dd HH:mm:ss",intervalTimeList.get(i)));
                djActivitySession.setEndSession(convertString2Date("yyyy-MM-dd HH:mm:ss",intervalTimeList.get(i+1)));
                djActivitySession.setSession(i+1);
                djActivitySession.setStoreActivityId(djStoreActivity.getId());
                djActivitySessionMapper.insert(djActivitySession);
            }
        }
    }


    /**
     * 查询活动
     * @param pageDTO
     * @return
     */
    public ServerResponse queryActivities(PageDTO pageDTO) {
        try {
            Example example=new Example(DjStoreActivity.class);
            example.createCriteria().andEqualTo(DjStoreActivity.DATA_STATUS,0);
            PageHelper.startPage(pageDTO.getPageNum(),pageDTO.getPageSize());
            List<DjStoreActivity> djStoreActivities = djStoreActivityMapper.selectByExample(example);
            djStoreActivities.forEach(djStoreActivity -> {
                City city = iCityMapper.selectByPrimaryKey(djStoreActivity.getCityId());
                djStoreActivity.setCity(city.getName());
            });
            if(djStoreActivities.size()<=0){
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(),ServerCode.NO_DATA.getDesc());
            }
            PageInfo pageInfo=new PageInfo(djStoreActivities);
            return ServerResponse.createBySuccess("查询成功",pageInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 编辑活动
     * @param djStoreActivity
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse setActivities(DjStoreActivity djStoreActivity) throws Exception {
        if(djStoreActivity.getActivityType()==1){
            DjStoreActivity djStoreActivity1 = djStoreActivityMapper.selectByPrimaryKey(djStoreActivity.getId());
            if(djStoreActivity.getRegistrationStartTime().before(djStoreActivity.getActivityStartTime())||
                    djStoreActivity.getEndTimeRegistration().after(djStoreActivity.getEndTime())){
                throw new Exception("请正确填写报名时间和活动时间");
            }
            if(!djStoreActivity1.equals(djStoreActivity.getActivityStartTime())&&!djStoreActivity1.getEndTime().equals(djStoreActivity.getEndTime())){
                Example example=new Example(DjActivitySession.class);
                example.createCriteria().andEqualTo(DjActivitySession.STORE_ACTIVITY_ID,djStoreActivity.getId());
                djActivitySessionMapper.deleteByExample(example);
                commonality(djStoreActivity);
            }
        }
        djStoreActivityMapper.updateByPrimaryKeySelective(djStoreActivity);
        return ServerResponse.createBySuccessMessage("编辑成功");
    }


    /**
     * 查询活动详情
     * @param id
     * @return
     */
    public ServerResponse queryActivitiesById(String id) {
        try {
            DjStoreActivity djStoreActivity = djStoreActivityMapper.selectByPrimaryKey(id);
            if(djStoreActivity.getActivityType()==1){
                Example example=new Example(DjActivitySession.class);
                example.createCriteria().andEqualTo(DjActivitySession.STORE_ACTIVITY_ID,id)
                        .andEqualTo(DjActivitySession.DATA_STATUS,0);
                example.orderBy(DjActivitySession.SESSION).asc();
                List<DjActivitySession> djActivitySessions = djActivitySessionMapper.selectByExample(example);
                List<String> list=new ArrayList<>();
                for (int i = 0; i < djActivitySessions.size(); i++) {
                    list.add("第"+(djActivitySessions.get(i).getSession())+"场："+convertDate2String("yyyy-MM-dd HH:mm:ss",djActivitySessions.get(i).getSessionStartTime())
                            +" 至 "+convertDate2String("yyyy-MM-dd HH:mm:ss",djActivitySessions.get(i).getEndSession()));
                }
                djStoreActivity.setList(list);
            }
            return ServerResponse.createBySuccess("查询成功",djStoreActivity);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 查询活动/场次详情
     * @param id
     * @param activityType
     * @return
     */
    public ServerResponse queryActivitiesOrSessionById(String id, Integer activityType) {
        try {
            List<DjStoreActivityDTO> djStoreActivityDTOS =
                    djStoreActivityMapper.queryActivitiesOrSessionById(id, activityType);
            return ServerResponse.createBySuccess("查询成功",djStoreActivityDTOS);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 查询活动(店铺参加)
     * @param userId
     * @param cityId
     * @return
     */
    public ServerResponse queryActivitiesByStorefront(String userId, String cityId, Integer activityType) {
        try {
            Storefront storefront = masterStorefrontService.getStorefrontByUserId(userId, cityId);
            List<DjStoreActivityDTO> djStoreActivityDTOS =
                    djStoreActivityMapper.queryActivitiesByStorefront(activityType, storefront.getId());
            if(djStoreActivityDTOS.size()<=0){
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(),ServerCode.NO_DATA.getDesc());
            }
            return ServerResponse.createBySuccess("查询成功",djStoreActivityDTOS);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 查询活动场次(店铺参加)
     * @param userId
     * @param cityId
     * @param id
     * @return
     */
    public ServerResponse queryActivitiesSessionByStorefront(String userId, String cityId, String id) {
        try {
            Storefront storefront = masterStorefrontService.getStorefrontByUserId(userId, cityId);
            List<DjStoreActivityDTO> djStoreActivityDTOS =
                    djStoreActivityMapper.queryActivitiesSessionByStorefront(id, storefront.getId());
            if(djStoreActivityDTOS.size()<=0){
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(),ServerCode.NO_DATA.getDesc());
            }
            return ServerResponse.createBySuccess("查询成功",djStoreActivityDTOS);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 店参加活动
     * @param userId
     * @param cityId
     * @param storeActivityId
     * @param activitySessionId
     * @param activityType
     * @return
     */
    public ServerResponse setStoreParticipateActivities(String userId, String cityId,
                                                        String storeActivityId, String activitySessionId,
                                                        Integer activityType) {
        try {
            Storefront storefront = masterStorefrontService.getStorefrontByUserId(userId, cityId);
            Example example=new Example(DjStoreParticipateActivities.class);
            Example.Criteria criteria = example.createCriteria().andEqualTo(DjStoreParticipateActivities.STORE_ACTIVITY_ID, storeActivityId)
                    .andEqualTo(DjStoreParticipateActivities.STOREFRONT_ID, storefront.getId())
                    .andEqualTo(DjStoreParticipateActivities.DATA_STATUS, 0)
                    .andCondition("registration_status in(1,3)");
            if(StringUtils.isNotBlank(activitySessionId)){
                criteria.andEqualTo(DjStoreParticipateActivities.ACTIVITY_SESSION_ID,activitySessionId);
            }
            if(djStoreParticipateActivitiesMapper.selectCountByExample(example)>0){
                return ServerResponse.createByErrorMessage("请勿重复申请");
            }
            example=new Example(DjStoreParticipateActivities.class);
            Example.Criteria criteria1 = example.createCriteria().andEqualTo(DjStoreParticipateActivities.STORE_ACTIVITY_ID, storeActivityId)
                    .andEqualTo(DjStoreParticipateActivities.STOREFRONT_ID, storefront.getId())
                    .andEqualTo(DjStoreParticipateActivities.DATA_STATUS, 0)
                    .andEqualTo(DjStoreParticipateActivities.REGISTRATION_STATUS, 4);
            if(StringUtils.isNotBlank(activitySessionId)){
                criteria1.andEqualTo(DjStoreParticipateActivities.ACTIVITY_SESSION_ID,activitySessionId);
            }
            DjStoreParticipateActivities djStoreParticipateActivities1 =
                    djStoreParticipateActivitiesMapper.selectOneByExample(example);
            if(djStoreParticipateActivities1!=null){
                djStoreParticipateActivities1.setRegistrationStatus(2);
                djStoreParticipateActivitiesMapper.updateByPrimaryKeySelective(djStoreParticipateActivities1);
                return ServerResponse.createBySuccessMessage("操作成功");
            }
            DjStoreParticipateActivities djStoreParticipateActivities=new DjStoreParticipateActivities();
            djStoreParticipateActivities.setActivityType(activityType);
            djStoreParticipateActivities.setStoreActivityId(storeActivityId);
            djStoreParticipateActivities.setActivitySessionId(activitySessionId);
            djStoreParticipateActivities.setStorefrontId(storefront.getId());
            djStoreParticipateActivities.setRegistrationStatus(2);
            djStoreParticipateActivitiesMapper.insert(djStoreParticipateActivities);
            return ServerResponse.createBySuccess("申请成功",djStoreParticipateActivities.getId());
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("申请失败");
        }
    }


    /**
     * 店铺活动商品待选列表
     * @param userId
     * @param cityId
     * @return
     */
    public ServerResponse queryWaitingSelectionProduct(String userId, String cityId, PageDTO pageDTO, String storeActivityId, String activitySessionId) {
        try {
            String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            Storefront storefront = masterStorefrontService.getStorefrontByUserId(userId, cityId);
            PageHelper.startPage(pageDTO.getPageNum(),pageDTO.getPageSize());
            List<DjStoreActivityProductDTO> djStoreActivityProductDTOS =
                    djStoreActivityProductMapper.queryWaitingSelectionProduct(storefront.getId(),storeActivityId,activitySessionId);
            if(djStoreActivityProductDTOS.size()<=0){
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(),ServerCode.NO_DATA.getDesc());
            }
            djStoreActivityProductDTOS.forEach(djStoreActivityProductDTO -> {
                djStoreActivityProductDTO.setImage(imageAddress+djStoreActivityProductDTO.getImage());
            });
            PageInfo pageInfo=new PageInfo(djStoreActivityProductDTOS);
            return ServerResponse.createBySuccess("查询成功",pageInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 已选/待选 数量
     * @param userId
     * @param cityId
     * @param storeActivityId
     * @param activitySessionId
     * @return
     */
    public ServerResponse querySelectedWaitingSelectionCount(String userId, String cityId, String storeActivityId, String activitySessionId) {
        try {
            Storefront storefront = masterStorefrontService.getStorefrontByUserId(userId, cityId);
            List<DjStoreActivityProductDTO> djStoreActivityProductDTOS =
                    djStoreActivityProductMapper.queryWaitingSelectionProduct(storefront.getId(),storeActivityId,activitySessionId);
            Map<String,Object> map=new HashMap<>();
            map.put("waiting",djStoreActivityProductDTOS.size());
            List<DjStoreActivityProductDTO> djStoreActivityProductDTOS1 =
                    djStoreActivityProductMapper.querySelectedProduct(storefront.getId(), storeActivityId, activitySessionId);
            map.put("selected",djStoreActivityProductDTOS1.size());
            return ServerResponse.createBySuccess("查询成功",map);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 店铺活动商品已选列表
     * @param userId
     * @param cityId
     * @param storeActivityId
     * @param activitySessionId
     * @return
     */
    public ServerResponse querySelectedProduct(String userId, String cityId, PageDTO pageDTO, String storeActivityId, String activitySessionId) {
        try {
            String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            Storefront storefront = masterStorefrontService.getStorefrontByUserId(userId, cityId);
            PageHelper.startPage(pageDTO.getPageNum(),pageDTO.getPageSize());
            List<DjStoreActivityProductDTO> djStoreActivityProductDTOS =
                    djStoreActivityProductMapper.querySelectedProduct(storefront.getId(), storeActivityId, activitySessionId);
            if(djStoreActivityProductDTOS.size()<=0){
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(),ServerCode.NO_DATA.getDesc());
            }
            djStoreActivityProductDTOS.forEach(djStoreActivityProductDTO -> {
                djStoreActivityProductDTO.setImage(imageAddress+djStoreActivityProductDTO.getImage());
            });
            PageInfo pageInfo=new PageInfo(djStoreActivityProductDTOS);
            return ServerResponse.createBySuccess("查询成功",pageInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 店铺选择活动商品
     * @param userId
     * @param cityId
     * @param storeActivityId
     * @param activitySessionId
     * @param productId
     * @param activityType
     * @param storeParticipateActivitiesId
     * @return
     */
    public ServerResponse setSelectActiveProduct(String userId, String cityId, String storeActivityId,
                                                 String activitySessionId, String productId,
                                                 Integer activityType,String storeParticipateActivitiesId) {
        try {
            Storefront storefront = masterStorefrontService.getStorefrontByUserId(userId, cityId);
            List<DjStoreParticipateActivities> djStoreParticipateActivities =
                    djStoreActivityProductMapper.queryHaveAttendProduct(productId, storefront.getId());
            StorefrontProduct storefrontProduct = iMasterStorefrontProductMapper.selectByPrimaryKey(productId);
            if(djStoreParticipateActivities.size()<=0){
                DjStoreActivityProduct djStoreActivityProduct=new DjStoreActivityProduct();
                djStoreActivityProduct.setActivityType(activityType);
                djStoreActivityProduct.setProductId(productId);
                djStoreActivityProduct.setInventory(storefrontProduct.getSuppliedNum().intValue());
                djStoreActivityProduct.setRushPurchasePrice(storefrontProduct.getSellPrice());
                djStoreActivityProduct.setStoreParticipateActivitiesId(storeParticipateActivitiesId);
                djStoreActivityProductMapper.insert(djStoreActivityProduct);
                return ServerResponse.createBySuccessMessage("添加成功");
            }else{
                for (DjStoreParticipateActivities djStoreParticipateActivity : djStoreParticipateActivities) {
                    Integer integer;
                    if(djStoreParticipateActivity.getActivityType()==1) {
                        integer = djStoreActivityProductMapper.queryWhetherOverlap(djStoreParticipateActivity.getActivitySessionId(), activitySessionId, storeActivityId);
                    }else{
                        integer = djStoreActivityProductMapper.queryWhetherOverlap1(djStoreParticipateActivity.getStoreActivityId(), storeActivityId,activitySessionId);
                    }
                    if(integer>0) {
                        if (djStoreParticipateActivity.getRegistrationStatus() == 2 ||
                                djStoreParticipateActivity.getRegistrationStatus() == 4)
                            djStoreActivityProductMapper.deleteByPrimaryKey(djStoreParticipateActivity.getId());
                        else
                            return ServerResponse.createByErrorMessage("该商品在该时间段已存在活动");
                    }
                }
                DjStoreActivityProduct djStoreActivityProduct = new DjStoreActivityProduct();
                djStoreActivityProduct.setActivityType(activityType);
                djStoreActivityProduct.setProductId(productId);
                djStoreActivityProduct.setInventory(storefrontProduct.getSuppliedNum().intValue());
                djStoreActivityProduct.setRushPurchasePrice(storefrontProduct.getSellPrice());
                djStoreActivityProduct.setStoreParticipateActivitiesId(storeParticipateActivitiesId);
                djStoreActivityProductMapper.insert(djStoreActivityProduct);
            }
            return ServerResponse.createBySuccessMessage("添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("添加失败");
        }
    }


    /**
     * 提交
     * @param jsonStr
     * @param storeParticipateActivitiesId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse setCommit(String jsonStr, String storeParticipateActivitiesId) {
        JSONArray jsonArr = JSONArray.parseArray(jsonStr);
        jsonArr.forEach(str -> {
            JSONObject obj = (JSONObject) str;
            String id = obj.getString("id");
            Integer inventory = obj.getInteger("inventory");
            Double rushPurchasePrice = obj.getDouble("rushPurchasePrice");
            DjStoreActivityProduct djStoreActivityProduct=new DjStoreActivityProduct();
            djStoreActivityProduct.setId(id);
            djStoreActivityProduct.setInventory(inventory);
            djStoreActivityProduct.setRushPurchasePrice(rushPurchasePrice);
            djStoreActivityProductMapper.updateByPrimaryKeySelective(djStoreActivityProduct);
        });
        DjStoreParticipateActivities djStoreParticipateActivities=new DjStoreParticipateActivities();
        djStoreParticipateActivities.setId(storeParticipateActivitiesId);
        djStoreParticipateActivities.setRegistrationStatus(3);
        djStoreParticipateActivitiesMapper.updateByPrimaryKeySelective(djStoreParticipateActivities);
        return ServerResponse.createBySuccessMessage("提交成功");
    }


    /**
     * 审核店铺参与活动列表
     * @return
     */
    public ServerResponse queryAuditstoresParticipateActivities(PageDTO pageDTO) {
        try {
            Example example=new Example(DjStoreActivity.class);
            example.createCriteria().andEqualTo(DjStoreActivity.DATA_STATUS,0);
            PageHelper.startPage(pageDTO.getPageNum(),pageDTO.getPageSize());
            List<DjStoreActivity> djStoreActivities = djStoreActivityMapper.selectByExample(example);
            List<DjStoreParticipateActivitiesDTO> djStoreParticipateActivitiesDTOS=new ArrayList<>();
            djStoreActivities.forEach(djStoreActivity -> {
                DjStoreParticipateActivitiesDTO djStoreParticipateActivitiesDTO=
                        new DjStoreParticipateActivitiesDTO();
                Integer integer = djStoreParticipateActivitiesMapper.queryRegistrationNumber(djStoreActivity.getId());
                djStoreParticipateActivitiesDTO.setRegistrationNumber(null!=integer?integer:0);
                djStoreParticipateActivitiesDTO.setActivityType(djStoreActivity.getActivityType());
                Example example1=new Example(DjStoreParticipateActivities.class);
                example1.createCriteria().andEqualTo(DjStoreParticipateActivities.DATA_STATUS,0)
                        .andEqualTo(DjStoreParticipateActivities.STORE_ACTIVITY_ID,djStoreActivity.getId())
                        .andEqualTo(DjStoreParticipateActivities.REGISTRATION_STATUS,3);
                djStoreParticipateActivitiesDTO.setPendingCount(djStoreParticipateActivitiesMapper.selectCountByExample(example1));
                djStoreParticipateActivitiesDTO.setStoreActivityId(djStoreActivity.getId());
                djStoreParticipateActivitiesDTOS.add(djStoreParticipateActivitiesDTO);
            });
            if(djStoreParticipateActivitiesDTOS.size()<=0){
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(),ServerCode.NO_DATA.getDesc());
            }
            PageInfo pageInfo=new PageInfo(djStoreParticipateActivitiesDTOS);
            return ServerResponse.createBySuccess("查询成功",pageInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 参与活动店铺列表
     * @param pageDTO
     * @param activityType
     * @param storeActivityId
     * @return
     */
    public ServerResponse queryParticipatingShopsList(PageDTO pageDTO, Integer activityType, String storeActivityId,
                                                      String activitySessionId) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(),pageDTO.getPageSize());
            List<DjStoreParticipateActivitiesDTO> djStoreParticipateActivitiesDTOS =
                    djStoreParticipateActivitiesMapper.queryParticipatingShopsList(storeActivityId,activityType,activitySessionId);
            djStoreParticipateActivitiesDTOS.forEach(djStoreParticipateActivitiesDTO -> {
                djStoreParticipateActivitiesDTO.setRegistrationNumber(djStoreParticipateActivitiesDTOS.size());
            });
            if(djStoreParticipateActivitiesDTOS.size()<=0){
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(),ServerCode.NO_DATA.getDesc());
            }
            PageInfo pageInfo=new PageInfo(djStoreParticipateActivitiesDTOS);
            return ServerResponse.createBySuccess("查询成功",pageInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 货品清单
     * @param pageDTO
     * @param id
     * @return
     */
    public ServerResponse queryBillGoods(PageDTO pageDTO, String id) {
        try {
            String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            PageHelper.startPage(pageDTO.getPageNum(),pageDTO.getPageSize());
            List<DjStoreActivityProductDTO> djStoreActivityProductDTOS = djStoreActivityProductMapper.queryBillGoods(id);
            if(djStoreActivityProductDTOS.size()<=0){
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(),ServerCode.NO_DATA.getDesc());
            }
            djStoreActivityProductDTOS.forEach(djStoreActivityProductDTO -> {
                djStoreActivityProductDTO.setImage(imageAddress+djStoreActivityProductDTO.getImage());
            });
            PageInfo pageInfo=new PageInfo(djStoreActivityProductDTOS);
            return ServerResponse.createBySuccess("查询成功",pageInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 审核货品清单
     * @param id
     * @param registrationStatus
     * @return
     */
    public ServerResponse setBillGoods(String id, Integer registrationStatus, String backReason) {
        try {
            DjStoreParticipateActivities djStoreParticipateActivities=new DjStoreParticipateActivities();
            djStoreParticipateActivities.setId(id);
            djStoreParticipateActivities.setRegistrationStatus(registrationStatus);
            djStoreParticipateActivities.setBackReason(backReason);
            djStoreParticipateActivitiesMapper.updateByPrimaryKeySelective(djStoreParticipateActivities);
            return ServerResponse.createBySuccessMessage("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }


    /**
     * 删除店铺活动商品已选列表
     * @param id
     * @return
     */
    public ServerResponse deleteSelectedProduct(String id) {
        try {
            djStoreActivityProductMapper.deleteByPrimaryKey(id);
            return ServerResponse.createBySuccessMessage("删除成功 ");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("删除失败");
        }
    }


    /**
     * 首页拼团活动
     * @return
     */
    public ServerResponse queryHomeGroupActivities(Integer limit) {
        try {
            List<StorefrontProductDTO> storefrontProductDTOS =
                    djStoreActivityProductMapper.queryHomeGroupActivities(limit,null);
            return ServerResponse.createBySuccess("查询成功",storefrontProductDTOS);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 首页限时购活动
     * @param limit
     * @return
     */
    public ServerResponse queryHomeLimitedPurchaseActivities(Integer limit) {
        try {
            Example example=new Example(DjActivitySession.class);
            example.createCriteria().andLessThanOrEqualTo(DjActivitySession.SESSION_START_TIME,new Date())
                    .andGreaterThanOrEqualTo(DjActivitySession.END_SESSION,new Date());
            List<DjActivitySession> djActivitySessions = djActivitySessionMapper.selectByExample(example);
            List<HomeLimitedPurchaseActivitieDTO> homeLimitedPurchaseActivitieDTOS=new ArrayList<>();
            djActivitySessions.forEach(djActivitySession -> {
                HomeLimitedPurchaseActivitieDTO homeLimitedPurchaseActivitieDTO=new HomeLimitedPurchaseActivitieDTO();
                homeLimitedPurchaseActivitieDTO.setId(djActivitySession.getId());
                homeLimitedPurchaseActivitieDTO.setSessionStartTime(djActivitySession.getSessionStartTime());
                homeLimitedPurchaseActivitieDTO.setEndSession(djActivitySession.getEndSession());
                List<StorefrontProductDTO> storefrontProductDTOS =
                        djStoreActivityProductMapper.queryHomeGroupActivities(limit,djActivitySession.getId());
                homeLimitedPurchaseActivitieDTO.setStorefrontProductDTOS(storefrontProductDTOS);
                homeLimitedPurchaseActivitieDTOS.add(homeLimitedPurchaseActivitieDTO);
            });
            if(homeLimitedPurchaseActivitieDTOS.size()<=0){
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(),ServerCode.NO_DATA.getDesc());
            }
            return ServerResponse.createBySuccess("查询成功",homeLimitedPurchaseActivitieDTOS);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 限时购(更多)
     * @param id
     * @return
     */
    public ServerResponse queryBuyMoreLimitedTime(String id) {
        try {
            List<StorefrontProductDTO> storefrontProductDTOS =
                    djStoreActivityProductMapper.queryHomeGroupActivities(null,id);
            return ServerResponse.createBySuccess("查询成功 ",storefrontProductDTOS);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 撤回
     * @param storeParticipateActivitiesId
     * @return
     */
    public ServerResponse setwithdraw(String storeParticipateActivitiesId) {
        try {
            DjStoreParticipateActivities djStoreParticipateActivities =
                    djStoreParticipateActivitiesMapper.selectByPrimaryKey(storeParticipateActivitiesId);
            djStoreParticipateActivities.setRegistrationStatus(2);
            djStoreParticipateActivitiesMapper.updateByPrimaryKeySelective(djStoreParticipateActivities);
            return ServerResponse.createBySuccessMessage("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }

}