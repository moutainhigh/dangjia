package com.dangjia.acg.service.activity;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.dto.activity.DjStoreActivityDTO;
import com.dangjia.acg.dto.activity.DjStoreActivityProductDTO;
import com.dangjia.acg.mapper.activity.DjActivitySessionMapper;
import com.dangjia.acg.mapper.activity.DjStoreActivityMapper;
import com.dangjia.acg.mapper.activity.DjStoreActivityProductMapper;
import com.dangjia.acg.mapper.activity.DjStoreParticipateActivitiesMapper;
import com.dangjia.acg.mapper.other.ICityMapper;
import com.dangjia.acg.modle.activity.DjActivitySession;
import com.dangjia.acg.modle.activity.DjStoreActivity;
import com.dangjia.acg.modle.activity.DjStoreActivityProduct;
import com.dangjia.acg.modle.activity.DjStoreParticipateActivities;
import com.dangjia.acg.modle.other.City;
import com.dangjia.acg.modle.storefront.Storefront;
import com.dangjia.acg.service.product.MasterStorefrontService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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

    /**
     * 添加活动
     *
     * @param djStoreActivity
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse addActivities(DjStoreActivity djStoreActivity)throws Exception {
        if (djStoreActivity.getActivityType() == 1) {
            if (commonality(djStoreActivity)==false){
                throw new Exception("配置失败");
            }
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

    private boolean commonality(DjStoreActivity djStoreActivity){
        if (DateUtil.getHourDays(djStoreActivity.getEndTime(),
                djStoreActivity.getActivityStartTime()) <= djStoreActivity.getCyclePurchasing()) {
            DjActivitySession djActivitySession=new DjActivitySession();
            djActivitySession.setSessionStartTime(djStoreActivity.getActivityStartTime());
            djActivitySession.setEndSession(djStoreActivity.getEndTime());
            djActivitySession.setSession(1);
            djActivitySession.setStoreActivityId(djStoreActivity.getId());
            djActivitySessionMapper.insert(djActivitySession);
            return true;
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
                return true;
            }
        }
        return false;
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
    public ServerResponse setActivities(DjStoreActivity djStoreActivity)throws Exception {
        if(djStoreActivity.getActivityType()==1){
            Example example=new Example(DjActivitySession.class);
            example.createCriteria().andNotEqualTo(DjActivitySession.STORE_ACTIVITY_ID,djStoreActivity.getId());
            djActivitySessionMapper.deleteByExample(example);
            if (commonality(djStoreActivity)==false){
                throw new Exception("编辑失败");
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
                List<DjActivitySession> djActivitySessions = djActivitySessionMapper.selectByExample(example);
                List<String> list=new ArrayList<>();
                for (int i = 0; i < djActivitySessions.size(); i++) {
                    list.add("第"+(i+1)+"场："+djActivitySessions.get(i).getSessionStartTime()
                            +" 至 "+djActivitySessions.get(i+1).getEndSession());
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
            example.createCriteria().andEqualTo(DjStoreParticipateActivities.ACTIVITY_SESSION_ID,activitySessionId)
                    .andEqualTo(DjStoreParticipateActivities.STORE_ACTIVITY_ID,storeActivityId)
                    .andEqualTo(DjStoreParticipateActivities.STOREFRONT_ID,storefront.getId())
                    .andEqualTo(DjStoreParticipateActivities.DATA_STATUS,0)
                    .andCondition("registration_status in(1,3)");
            if(djStoreParticipateActivitiesMapper.selectCountByExample(example)>0){
                return ServerResponse.createByErrorMessage("请勿重复申请");
            }
            DjStoreParticipateActivities djStoreParticipateActivities=new DjStoreParticipateActivities();
            djStoreParticipateActivities.setActivityType(activityType);
            djStoreParticipateActivities.setStoreActivityId(storeActivityId);
            djStoreParticipateActivities.setActivitySessionId(activitySessionId);
            djStoreParticipateActivities.setStorefrontId(storefront.getId());
            djStoreParticipateActivitiesMapper.insert(djStoreParticipateActivities);
            return ServerResponse.createBySuccessMessage("添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("添加失败");
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
            Storefront storefront = masterStorefrontService.getStorefrontByUserId(userId, cityId);
            PageHelper.startPage(pageDTO.getPageNum(),pageDTO.getPageSize());
            List<DjStoreActivityProductDTO> djStoreActivityProductDTOS =
                    djStoreActivityProductMapper.queryWaitingSelectionProduct(storefront.getId(),storeActivityId,activitySessionId);
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
            map.put("selected",djStoreActivityProductDTOS.size());
            List<DjStoreActivityProductDTO> djStoreActivityProductDTOS1 =
                    djStoreActivityProductMapper.querySelectedProduct(storefront.getId(), storeActivityId, activitySessionId);
            map.put("waiting",djStoreActivityProductDTOS1.size());
            return ServerResponse.createBySuccess("查询成功",map);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

}