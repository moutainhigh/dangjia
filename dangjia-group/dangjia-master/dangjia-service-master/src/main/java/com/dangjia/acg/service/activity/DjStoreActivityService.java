package com.dangjia.acg.service.activity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.enums.AppType;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.activity.DjStoreActivityDTO;
import com.dangjia.acg.dto.activity.DjStoreActivityProductDTO;
import com.dangjia.acg.dto.activity.DjStoreParticipateActivitiesDTO;
import com.dangjia.acg.dto.activity.HomeLimitedPurchaseActivitieDTO;
import com.dangjia.acg.dto.deliver.GroupBooking;
import com.dangjia.acg.dto.member.MemberDTO;
import com.dangjia.acg.dto.product.StorefrontProductDTO;
import com.dangjia.acg.mapper.activity.DjActivitySessionMapper;
import com.dangjia.acg.mapper.activity.DjStoreActivityMapper;
import com.dangjia.acg.mapper.activity.DjStoreActivityProductMapper;
import com.dangjia.acg.mapper.activity.DjStoreParticipateActivitiesMapper;
import com.dangjia.acg.mapper.delivery.IOrderMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.other.ICityMapper;
import com.dangjia.acg.mapper.product.IMasterProductTemplateMapper;
import com.dangjia.acg.mapper.product.IMasterStorefrontProductMapper;
import com.dangjia.acg.mapper.worker.IWorkerDetailMapper;
import com.dangjia.acg.modle.activity.DjActivitySession;
import com.dangjia.acg.modle.activity.DjStoreActivity;
import com.dangjia.acg.modle.activity.DjStoreActivityProduct;
import com.dangjia.acg.modle.activity.DjStoreParticipateActivities;
import com.dangjia.acg.modle.deliver.Order;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.member.MemberAddress;
import com.dangjia.acg.modle.other.City;
import com.dangjia.acg.modle.product.DjBasicsProductTemplate;
import com.dangjia.acg.modle.storefront.Storefront;
import com.dangjia.acg.modle.storefront.StorefrontProduct;
import com.dangjia.acg.modle.worker.WorkerDetail;
import com.dangjia.acg.service.config.ConfigMessageService;
import com.dangjia.acg.service.member.MemberAddressService;
import com.dangjia.acg.service.product.MasterStorefrontService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


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
    @Autowired
    private IOrderMapper orderMapper;
    @Autowired
    private IMemberMapper iMemberMapper;
    @Autowired
    private IMasterProductTemplateMapper iMasterProductTemplateMapper;
    @Autowired
    private IWorkerDetailMapper iWorkerDetailMapper;
    @Autowired
    private ConfigMessageService configMessageService;
    @Autowired
    private MemberAddressService memberAddressService;

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
            if (DateUtil.getHourDays(djStoreActivity.getActivityendTime(),
                    djStoreActivity.getActivityStartTime()) <= djStoreActivity.getCyclePurchasing()) {
                list.add("第1场："+convertDate2String("yyyy-MM-dd HH:mm:ss",djStoreActivity.getActivityStartTime())
                        +" 至 "+convertDate2String("yyyy-MM-dd HH:mm:ss",djStoreActivity.getActivityendTime()));
            }else{
                List<String> intervalTimeList = getIntervalTimeList(convertDate2String("yyyy-MM-dd HH:mm:ss", djStoreActivity.getActivityStartTime())
                        , convertDate2String("yyyy-MM-dd HH:mm:ss", djStoreActivity.getActivityendTime()),
                        djStoreActivity.getCyclePurchasing());
                for (int i = 0; i <intervalTimeList.size()-1; i++) {
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
        if (DateUtil.getHourDays(djStoreActivity.getActivityendTime(),
                djStoreActivity.getActivityStartTime()) <= djStoreActivity.getCyclePurchasing()) {
            DjActivitySession djActivitySession=new DjActivitySession();
            djActivitySession.setSessionStartTime(djStoreActivity.getActivityStartTime());
            djActivitySession.setSessionEndTime(djStoreActivity.getActivityendTime());
            djActivitySession.setSession(1);
            djActivitySession.setStoreActivityId(djStoreActivity.getId());
            djActivitySessionMapper.insert(djActivitySession);
        }else{
            List<String> intervalTimeList = getIntervalTimeList(convertDate2String("yyyy-MM-dd HH:mm:ss", djStoreActivity.getActivityStartTime())
                    , convertDate2String("yyyy-MM-dd HH:mm:ss", djStoreActivity.getActivityendTime()),
                    djStoreActivity.getCyclePurchasing());
            for (int i = 0; i <intervalTimeList.size()-1; i++) {
                DjActivitySession djActivitySession=new DjActivitySession();
                djActivitySession.setSessionStartTime(convertString2Date("yyyy-MM-dd HH:mm:ss",intervalTimeList.get(i)));
                djActivitySession.setSessionEndTime(convertString2Date("yyyy-MM-dd HH:mm:ss",intervalTimeList.get(i+1)));
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
            example.orderBy(DjStoreActivity.CREATE_DATE).desc();
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
                    djStoreActivity.getRegistrationEndTime().after(djStoreActivity.getActivityendTime())){
                throw new Exception("请正确填写报名时间和活动时间");
            }
            if(!djStoreActivity1.getActivityStartTime().equals(djStoreActivity.getActivityStartTime())&&!djStoreActivity1.getActivityendTime().equals(djStoreActivity.getActivityendTime())){
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
                            +" 至 "+convertDate2String("yyyy-MM-dd HH:mm:ss",djActivitySessions.get(i).getSessionEndTime()));
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
            example.createCriteria().andGreaterThanOrEqualTo(DjActivitySession.SESSION_END_TIME,new Date());
            List<DjActivitySession> djActivitySessions = djActivitySessionMapper.selectByExample(example);
            List<HomeLimitedPurchaseActivitieDTO> homeLimitedPurchaseActivitieDTOS=new ArrayList<>();
            djActivitySessions.forEach(djActivitySession -> {
                HomeLimitedPurchaseActivitieDTO homeLimitedPurchaseActivitieDTO=new HomeLimitedPurchaseActivitieDTO();
                homeLimitedPurchaseActivitieDTO.setId(djActivitySession.getId());
                homeLimitedPurchaseActivitieDTO.setSessionStartTime(djActivitySession.getSessionStartTime());
                homeLimitedPurchaseActivitieDTO.setEndSession(djActivitySession.getSessionEndTime());
                List<StorefrontProductDTO> storefrontProductDTOS =
                        djStoreActivityProductMapper.queryHomeGroupActivities(limit,djActivitySession.getId());
                if(storefrontProductDTOS.size()>0) {
                    homeLimitedPurchaseActivitieDTO.setStorefrontProductDTOS(storefrontProductDTOS);
                    homeLimitedPurchaseActivitieDTOS.add(homeLimitedPurchaseActivitieDTO);
                }
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
     * 限时购/拼团购(更多、超值抢购)
     * @param id
     * @return
     */
    public ServerResponse queryBuyMoreLimitedTime(String id) {
        try {
            String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            Map<String,Object> map=new HashMap<>();
            List<StorefrontProductDTO> storefrontProductDTOS =
                    djStoreActivityProductMapper.queryHomeGroupActivities(3,id);
            storefrontProductDTOS.forEach(storefrontProductDTO -> {
                storefrontProductDTO.setImage(imageAddress+storefrontProductDTO.getImage());
            });
            map.put("tabList",storefrontProductDTOS);
            List<StorefrontProductDTO> storefrontProductDTOS1 =
                    djStoreActivityProductMapper.queryHomeGroupActivities(null, id);
            storefrontProductDTOS1.forEach(storefrontProductDTO -> {
                storefrontProductDTO.setImage(imageAddress+storefrontProductDTO.getImage());
            });
            map.put("panicBuying",storefrontProductDTOS1);
            return ServerResponse.createBySuccess("查询成功 ",map);
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


    /**
     * 拼团列表
     * @param storeActivityProductId
     * @return
     */
    public ServerResponse querySpellGroupList(String storeActivityProductId) {
        try {
            String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            List<Map> list = djStoreActivityMapper.querySpellDeals(storeActivityProductId);
            list.forEach(a ->{
                SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String d = format.format(a.get("orderPayTime"));
                try {
                    Date date=format.parse(d);
                    a.put("orderPayTime", DateUtil.daysBetweenTime(new Date(),DateUtil.addDateHours(date,24)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                a.put("head",imageAddress+a.get("head"));
            });
            return ServerResponse.createBySuccess("查询成功",list);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 参与拼团(未满/已满)
     * @param orderId
     * @return
     */
    public ServerResponse setSpellGroup(String orderId) {
        try {
            Order order = orderMapper.selectByPrimaryKey(orderId);
            DjStoreActivity djStoreActivity = djStoreActivityMapper.selectByPrimaryKey(order.getStoreActivityId());
            String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            if(StringUtils.isNotBlank(order.getParentOrderId()))
                orderId=order.getParentOrderId();
            Example example=new Example(Order.class);
            example.createCriteria().andEqualTo(Order.PARENT_ORDER_ID,orderId)
                    .orEqualTo(Order.ID,orderId)
                    .andEqualTo(Order.DATA_STATUS,0)
                    .andEqualTo(Order.ORDER_STATUS,9);
            List<Order> orders = orderMapper.selectByExample(example);
            DjStoreActivityProduct djStoreActivityProduct
                    = djStoreActivityProductMapper.selectByPrimaryKey(order.getStoreActivityProductId());
            StorefrontProduct storefrontProduct = iMasterStorefrontProductMapper.selectByPrimaryKey(djStoreActivityProduct.getProductId());
            DjBasicsProductTemplate djBasicsProductTemplate = iMasterProductTemplateMapper.selectByPrimaryKey(storefrontProduct.getProdTemplateId());
            GroupBooking groupBooking=new GroupBooking();
            List<Map<String,Object>> list=new ArrayList<>();
            groupBooking.setProductId(storefrontProduct.getId());
            groupBooking.setImage(imageAddress+storefrontProduct.getImage());
            groupBooking.setProductName(storefrontProduct.getProductName());
            groupBooking.setRushPurchasePrice(djStoreActivityProduct.getRushPurchasePrice());
            groupBooking.setUnitName(djBasicsProductTemplate.getUnitName());
            groupBooking.setSellPrice(storefrontProduct.getSellPrice());
            groupBooking.setSpellGroup(djStoreActivity.getSpellGroup());
            orders.forEach(order1 -> {
                Map<String, Object> map = new HashMap<>();
                Member member = iMemberMapper.selectByPrimaryKey(order1.getMemberId());
                member.initPath(imageAddress);
                map.put("administrator", 0);
                if (cn.jiguang.common.utils.StringUtils.isEmpty(order1.getParentOrderId())) {
                    map.put("administrator", 1);
                }
                map.put("head", member.getHead());
                list.add(map);
            });
            groupBooking.setList(list);
            groupBooking.setShortProple(djStoreActivity.getSpellGroup() - orders.size());
            return ServerResponse.createBySuccess("查询成功",groupBooking);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 查询限时购/拼团购轮播
     * @param storeActivityProductId
     * @param activityType
     * @return
     */
    public ServerResponse queryActivityPurchaseRotation(String storeActivityProductId, Integer activityType) {
        try {
            String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            List<MemberDTO> memberDTOS = orderMapper.queryActivityPurchaseRotation(activityType, storeActivityProductId);
            memberDTOS.forEach(memberDTO ->{
                memberDTO.setHead(imageAddress+memberDTO.getHead());
                try {
                    memberDTO.setAirtime(DateUtil.daysBetweenTime(memberDTO.getOrderPayTime(),new Date()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            });
            return ServerResponse.createBySuccess("查询成功",memberDTOS);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 检测拼团购是否拼团失败
     * @return
     */
    @Transactional
    public void checkGroupPurchaseOrder() {
        Example example=new Example(Order.class);
        example.createCriteria().andEqualTo(Order.DATA_STATUS,0)
                .andEqualTo(Order.ORDER_STATUS,9)
                .andEqualTo(Order.ORDER_SOURCE,6)
                .andCondition("TIMESTAMPDIFF( HOUR, order_pay_time, SYSDATE()) >= 24");
        List<Order> orders = orderMapper.selectByExample(example);
        orders.forEach(order -> {
            //拼团失败
            order.setOrderStatus("10");
            orderMapper.updateByPrimaryKeySelective(order);
            //退款，记录流水
            //用户入账
            Member member = iMemberMapper.selectByPrimaryKey(order.getMemberId());
            if (member != null) {
                BigDecimal haveMoney = member.getHaveMoney().add(order.getTotalAmount());
                BigDecimal surplusMoney = member.getSurplusMoney().add(order.getTotalAmount());
                member.setHaveMoney(haveMoney);//添加已获总钱
                member.setSurplusMoney(surplusMoney);//添加余额
                iMemberMapper.updateByPrimaryKeySelective(member);
                //添加流水
                WorkerDetail workerDetail = new WorkerDetail();
                workerDetail.setName("业主拼团失败退款");
                workerDetail.setWorkerId(member.getId());
                workerDetail.setWorkerName(member.getName()!=null?member.getName():member.getNickName());
                workerDetail.setHouseId(order.getHouseId());
                workerDetail.setMoney(order.getTotalAmount());
                workerDetail.setState(2);//进工钱
                workerDetail.setWalletMoney(surplusMoney);//
                workerDetail.setHaveMoney(order.getTotalAmount());
                workerDetail.setHouseWorkerOrderId(order.getId());
                workerDetail.setApplyMoney(order.getTotalAmount());
                iWorkerDetailMapper.insert(workerDetail);
                MemberAddress memberAddress=memberAddressService.getMemberAddressInfo(order.getAddressId(),order.getHouseId());
                if(memberAddress!=null) {
                    configMessageService.addConfigMessage(AppType.ZHUANGXIU, order.getMemberId(), "0", "拼团失败", String.format
                            (DjConstants.PushMessage.YZ_PT_FAIL, memberAddress.getAddress()), 3, null, null);
                }
            }
        });
    }
}