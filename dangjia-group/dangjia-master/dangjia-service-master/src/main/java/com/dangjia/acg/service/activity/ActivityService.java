package com.dangjia.acg.service.activity;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.activity.ActivityDTO;
import com.dangjia.acg.dto.activity.ActivityRedPackDTO;
import com.dangjia.acg.mapper.activity.IActivityDiscountMapper;
import com.dangjia.acg.mapper.activity.IActivityMapper;
import com.dangjia.acg.mapper.activity.IActivityRedPackMapper;
import com.dangjia.acg.mapper.activity.IActivityRedPackRuleMapper;
import com.dangjia.acg.modle.activity.Activity;
import com.dangjia.acg.modle.activity.ActivityDiscount;
import com.dangjia.acg.modle.activity.ActivityRedPack;
import com.dangjia.acg.modle.activity.ActivityRedPackRule;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * 活动管理
 * author: qiyuxiang
 * Date: 2018/10/31 0031
 * Time: 20:18
 */
@Service
public class ActivityService {

    @Autowired
    private IActivityMapper activityMapper;
    @Autowired
    private IActivityDiscountMapper activityDiscountMapper;
    @Autowired
    private IActivityRedPackMapper activityRedPackMapper;

    @Autowired
    private IActivityRedPackRuleMapper activityRedPackRuleMapper;

    @Autowired
    private ConfigUtil configUtil;
    /**
     * 获取所有活动
     * @param activity
     * @return
     */
    public ServerResponse queryActivitys(HttpServletRequest request, PageDTO pageDTO, Activity activity,String isEndTime) {
        Example example = new Example(Activity.class);
        Example.Criteria criteria=example.createCriteria();
        if(!CommonUtil.isEmpty(activity.getActivityType())) {
            criteria.andEqualTo(Activity.ACTIVITY_TYPE,activity.getActivityType());
        }
        if (!CommonUtil.isEmpty(activity.getName())) {
            criteria.andLike(Activity.NAME, "%" + activity.getName() + "%");
        }
        if(!CommonUtil.isEmpty(activity.getCityId())) {
            criteria.andEqualTo(Activity.CITY_ID,activity.getCityId());
        }
        if(!CommonUtil.isEmpty(isEndTime)&&"0".equals(isEndTime)) {
            criteria.andLessThanOrEqualTo(Activity.END_DATE,new Date());
        }
        if(!CommonUtil.isEmpty(activity.getDeleteState())) {
            criteria.andEqualTo(Activity.DELETE_STATE,activity.getDeleteState());
        }
        example.orderBy(Activity.MODIFY_DATE).desc();
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<Activity> list = activityMapper.selectByExample(example);
        PageInfo pageResult = new PageInfo(list);
        return ServerResponse.createBySuccess("ok",pageResult);
    }

    public ServerResponse getActivity(HttpServletRequest request, ActivityDTO activityDTO) {

        Activity activity = activityMapper.selectByPrimaryKey(activityDTO.getId());
        if(!CommonUtil.isEmpty(request.getParameter("isCheck"))){
            if(activity.getDeleteState()==1||activity.getEndDate().getTime() < new Date().getTime()){
                return ServerResponse.createByErrorMessage("活动已结束！");
            }
        }
        BeanUtils.beanToBean(activity,activityDTO);
        Example example = new Example(ActivityDiscount.class);
        example.createCriteria().andEqualTo(ActivityDiscount.ACTIVITY_ID,activity.getId());
        List<ActivityDiscount> discounts=activityDiscountMapper.selectByExample(example);
        List<ActivityRedPackDTO> redPacks=new ArrayList<>();
        for (ActivityDiscount discount:discounts) {
            ActivityRedPack activityRedPack= activityRedPackMapper.selectByPrimaryKey(discount.getActivityRedPackId());
           if(activityRedPack!=null) {
               ActivityRedPackDTO activityRedPackDTO=new  ActivityRedPackDTO();
               BeanUtils.beanToBean(activityRedPack,activityRedPackDTO);
               Example exampleRule = new Example(ActivityRedPackRule.class);
               exampleRule.createCriteria().andEqualTo(ActivityRedPackRule.ACTIVITY_RED_PACK_ID,activityRedPackDTO.getId());
               List<ActivityRedPackRule> redPackRule=activityRedPackRuleMapper.selectByExample(exampleRule);
               activityRedPackDTO.setRedPackRule(redPackRule);
               redPacks.add(activityRedPackDTO);
           }
        }
        activityDTO.setDiscounts(redPacks);
        return ServerResponse.createBySuccess("ok",activityDTO);
    }

    /**
     * 修改
     * @param activity
     * @return
     */
    public ServerResponse editActivity(HttpServletRequest request, Activity activity,String discount) {
        activity.setModifyDate(new Date());
        if(this.activityMapper.updateByPrimaryKeySelective(activity)>0){
            if(!CommonUtil.isEmpty(discount)) {
                addActivityDiscounts(activity, discount);
            }
            return ServerResponse.createBySuccessMessage("ok");
        }else{
            return ServerResponse.createByErrorMessage("修改失败，请您稍后再试");
        }
    }
    /**
     * 关闭活动
     * @param id
     * @return
     */
    public ServerResponse closeActivity(String id) {
        Activity activity=new Activity();
        activity.setId(id);
        activity.setDeleteState(1);
        if(this.activityMapper.updateByPrimaryKeySelective(activity)>0){
            return ServerResponse.createBySuccessMessage("ok");
        }else{
            return ServerResponse.createByErrorMessage("关闭失败，请您稍后再试");
        }
    }
    /**
     * 新增
     * @param activity
     * @return
     */
    public ServerResponse addActivity(HttpServletRequest request,Activity activity,String discount) {
        activity.setDeleteState(1);
        if(this.activityMapper.insertSelective(activity)>0){
            addActivityDiscounts(activity,discount);
            return ServerResponse.createBySuccessMessage("ok");
        }else{
            return ServerResponse.createByErrorMessage("新增失败，请您稍后再试");
        }
    }

    /**
     * 设置活动优惠券
     * @param activity
     * @return
     */
    public void addActivityDiscounts(Activity activity,String discount) {
        Example example = new Example(ActivityDiscount.class);
        Example.Criteria criteria=example.createCriteria();
        criteria.andEqualTo("activityId",activity.getId());
        activityDiscountMapper.deleteByExample(example);
        if(!CommonUtil.isEmpty(discount)){
            String[] discounts= StringUtils.split(discount,",");
            for (String v:discounts) {
                ActivityDiscount activityDiscount=new ActivityDiscount();
                activityDiscount.setActivityRedPackId(v);
                activityDiscount.setActivityId(activity.getId());
                activityDiscountMapper.insert(activityDiscount);
            }
        }
    }
}
