package com.dangjia.acg.service.activity;

import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dto.activity.ActivityDTO;
import com.dangjia.acg.dto.activity.ActivityRedPackDTO;
import com.dangjia.acg.mapper.activity.IActivityUserTemplateMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.modle.activity.Activity;
import com.dangjia.acg.modle.activity.ActivityRedPackRule;
import com.dangjia.acg.modle.activity.ActivityUserTemplate;
import com.dangjia.acg.modle.member.Member;
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
import java.util.Map;

/**
 * 活动推送目标模板
 * author: qiyuxiang
 * Date: 2018/10/31 0031
 * Time: 20:18
 */
@Service
public class ActivityUserTemplateService {

    @Autowired
    private IActivityUserTemplateMapper activityUserTemplateMapper;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private IMemberMapper memberMapper;
    @Autowired
    private RedPackService redPackService;

    /**
     * 获取所有推送模板
     *
     * @return
     */
    public ServerResponse queryActivityUserTemplate(PageDTO pageDTO) {
        Example example = new Example(ActivityUserTemplate.class);
        example.createCriteria().andEqualTo(ActivityUserTemplate.DATA_STATUS, 0);
        example.orderBy(Activity.MODIFY_DATE).desc();
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<ActivityUserTemplate> list = activityUserTemplateMapper.selectByExample(example);
        if (list.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        List<Map> mapList = new ArrayList<>();
        PageInfo pageResult = new PageInfo(list);
        for (ActivityUserTemplate activityUserTemplate1 : list) {
            Map map = BeanUtils.beanToMap(activityUserTemplate1);
            example = new Example(Member.class);
            if (!CommonUtil.isEmpty(activityUserTemplate1.getMembers())) {
                Example.Criteria criteria = example.createCriteria();
                String[] members = StringUtils.split(activityUserTemplate1.getMembers(), ",");
                for (String memberid : members) {
                    criteria.orEqualTo(Member.ID, memberid);
                }
            }
            example.orderBy(Activity.MODIFY_DATE).desc();
            map.put("memberList", memberMapper.selectByExample(example));
            mapList.add(map);
        }
        pageResult.setList(mapList);
        return ServerResponse.createBySuccess("ok", pageResult);
    }


    /**
     * 新增
     *
     * @param templateId           修改时，模板ID
     * @param activityUserTemplate
     * @return
     */
    public ServerResponse addActivityUserTemplate(HttpServletRequest request, String templateId, ActivityUserTemplate activityUserTemplate) {
        try {
            if (!CommonUtil.isEmpty(activityUserTemplate.getMembers())) {
                activityUserTemplate.setNum(StringUtils.split(activityUserTemplate.getMembers(), ",").length);
            }
            if (CommonUtil.isEmpty(templateId)) {
                this.activityUserTemplateMapper.insertSelective(activityUserTemplate);
                return ServerResponse.createBySuccessMessage("新增成功");
            } else {
                activityUserTemplate.setId(templateId);
                this.activityUserTemplateMapper.updateByPrimaryKeySelective(activityUserTemplate);
                return ServerResponse.createBySuccessMessage("修改成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败，请您稍后再试");
        }
    }

    /**
     * 可用优惠券数据
     *
     * @param request
     * @param members    成员组
     * @param activityId
     * @return
     */
    public ServerResponse sendActivityPadPack(HttpServletRequest request, String members, String activityId) {
        //检查是否存在有效活动
        ActivityDTO activity = validActivity(request, activityId);
        //检查优惠券是否有效
        if (activity != null && activity.getDiscounts() != null && activity.getDiscounts().size() > 0) {
            List<ActivityRedPackDTO> discounts = activity.getDiscounts();
            for (ActivityRedPackDTO red : discounts) {
                //判断优惠券是否过期，或优惠券未关闭
                if (red.getDeleteState() == 0 && red.getEndDate().getTime() > new Date().getTime()) {
                    List<String> redPackRuleIds = new ArrayList<>();
                    for (ActivityRedPackRule rule : red.getRedPackRule()) {
                        redPackRuleIds.add(rule.getId());
                    }
                    //开始发送红包
                    if (redPackRuleIds.size() > 0) {
                        ServerResponse serverResponse = redPackService.sendMemberPadPackBatch(members, red.getId(), StringUtils.join(redPackRuleIds, ","));
                        if (!serverResponse.isSuccess()) {
                            return serverResponse;
                        }
                    }
                } else {
                    return ServerResponse.createByErrorMessage("推送失败，请检查优惠券（" + red.getName() + "）是否过期！");
                }
            }
        } else {
            return ServerResponse.createByErrorMessage("推送失败，请检查活动时间是否有效！");
        }
        return ServerResponse.createBySuccessMessage("推送完成");
    }

    public ActivityDTO validActivity(HttpServletRequest request, String activityId) {
        ActivityDTO activity = new ActivityDTO();
        activity.setId(activityId);
        ServerResponse response = activityService.getActivity(request, activity);
        activity = (ActivityDTO) response.getResultObj();
        return activity;
    }
}
