package com.dangjia.acg.service.activity;

import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.mapper.activity.*;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.other.ICityMapper;
import com.dangjia.acg.modle.activity.*;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.member.Member;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * 新注册用户领取礼品活动管理
 * author: qiyuxiang
 * Date: 2019/10/15 0031
 * Time: 20:18
 */
@Service
public class ActivityParticipantService {

    @Autowired
    private IActivityParticipantMapper activityParticipantMapper;

    @Autowired
    private RedisClient redisClient;
    @Autowired
    private IMemberMapper memberMapper;
    @Autowired
    private ICityMapper cityMapper;

    /**
     * 获取所有活动
     * @param activityParticipant
     * @return
     */
    public ServerResponse queryParticipant(HttpServletRequest request, PageDTO pageDTO, ActivityParticipant activityParticipant, Date startTime, Date endTime) {
        Example example = new Example(ActivityParticipant.class);
        Example.Criteria criteria=example.createCriteria();
        if (!CommonUtil.isEmpty(activityParticipant.getNickName())) {
            criteria.andCondition(" CONCAT(nick_name,phone) like CONCAT('%','" + activityParticipant.getNickName() + "','%')");
        }
        if (activityParticipant.getState()!=null&&activityParticipant.getState()!=2) {
            criteria.andEqualTo(ActivityParticipant.STATE,activityParticipant.getState());
            criteria.andNotEqualTo(ActivityParticipant.STATE,2);
        }
        if (activityParticipant.getState()!=null&&activityParticipant.getState()==2) {
            criteria.andCondition(" (data_status=1 or state=2 ) ");
        }else{
            criteria.andEqualTo(ActivityParticipant.DATA_STATUS,0);
        }
        if(!CommonUtil.isEmpty(activityParticipant.getCityId())) {
            criteria.andEqualTo(ActivityParticipant.CITY_ID,activityParticipant.getCityId());
        }
        if(startTime!=null&&endTime!=null) {
            criteria.andBetween(ActivityParticipant.CREATE_DATE,startTime,endTime);
        }

        example.orderBy(ActivityParticipant.DATA_STATUS).desc();
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<ActivityParticipant> list = activityParticipantMapper.selectByExample(example);
        if (list.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        PageInfo pageResult = new PageInfo(list);
        List<Map> mapList=new ArrayList<>();
        for (ActivityParticipant participant : list) {
            Map map = BeanUtils.beanToMap(participant);
            map.put("cityName", cityMapper.selectByPrimaryKey(participant.getCityId()).getName());
            if (activityParticipant.getState()==1) {
                Member user = memberMapper.selectByPrimaryKey(participant.getMemberId());
                map.put(ActivityParticipant.CREATE_DATE, user.getCreateDate());
            }
            mapList.add(map);
        }
        pageResult.setList(mapList);
        return ServerResponse.createBySuccess("ok",pageResult);
    }
    /**
     * 获取当前用户是否已领取
     * @param userToken
     * @return
     */
    public ServerResponse getParticipant(HttpServletRequest request,String userToken) {
        AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
        if (accessToken == null) {//无效的token
            return ServerResponse.createbyUserTokenError();
        }
        Member user = memberMapper.selectByPrimaryKey(accessToken.getMemberId());
        Example example = new Example(ActivityParticipant.class);
        example.createCriteria().andEqualTo(ActivityParticipant.MEMBER_ID,user.getId());
        List list =activityParticipantMapper.selectByExample(example);
        if(list.size()>0){
            return ServerResponse.createBySuccess("ok",list.get(0));
        }else{
            return ServerResponse.createBySuccessMessage("ok");
        }
    }

    /**
     * 修改
     * @param activityParticipant
     * @return
     */
    public ServerResponse editParticipant(HttpServletRequest request, ActivityParticipant activityParticipant) {
        if(activityParticipant.getState()==1){
            activityParticipant.setReceiveDate(new Date());
        }
        activityParticipant.setModifyDate(new Date());
        if(this.activityParticipantMapper.updateByPrimaryKeySelective(activityParticipant)>0){
            return ServerResponse.createBySuccessMessage("ok");
        }else{
            return ServerResponse.createByErrorMessage("修改失败，请您稍后再试");
        }
    }

    /**
     * 清空
     * @param activityParticipant
     * @return
     */
    public ServerResponse cleanParticipant(HttpServletRequest request, ActivityParticipant activityParticipant) {
        activityParticipant.setId(null);
        activityParticipant.setDataStatus(1);
        Example example = new Example(ActivityParticipant.class);
        example.createCriteria().andEqualTo(ActivityParticipant.DATA_STATUS,0);
        this.activityParticipantMapper.updateByExampleSelective(activityParticipant,example);
        return ServerResponse.createBySuccessMessage("清理成功！");
    }

    /**
     * 添加排除用户
     * @param activityParticipant
     * @return
     */
    public ServerResponse addCleanParticipant(HttpServletRequest request, ActivityParticipant activityParticipant) {
        if(!CommonUtil.isEmpty(activityParticipant.getMemberId())) {
            String[] memberIds = activityParticipant.getMemberId().split(",");
            for (String memberId : memberIds) {
                ActivityParticipant activityParticipantnew=new ActivityParticipant();
                activityParticipantnew.setMemberId(memberId);
                activityParticipantnew.setState(2);
                activityParticipantnew.setCityId(activityParticipant.getCityId());
                addParticipant(request,null,activityParticipantnew);
            }
        }
        return ServerResponse.createBySuccessMessage("排除成功！");
    }
    /**
     * 报名
     * @param activityParticipant
     * @return
     */
    public ServerResponse addParticipant(HttpServletRequest request,String userToken,ActivityParticipant activityParticipant) {
        Member user =null;
        if(!CommonUtil.isEmpty(userToken)){
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            if (accessToken == null) {//无效的token
                return ServerResponse.createbyUserTokenError();
            }
             user = memberMapper.selectByPrimaryKey(accessToken.getMemberId());

        }
        if(!CommonUtil.isEmpty(activityParticipant.getMemberId())){
             user = memberMapper.selectByPrimaryKey(activityParticipant.getMemberId());
        }

        if(user!=null) {
            Example example = new Example(ActivityParticipant.class);
            example.createCriteria().andEqualTo(ActivityParticipant.MEMBER_ID,user.getId());
            if(activityParticipantMapper.selectByExample(example).size()>0){
                return ServerResponse.createBySuccessMessage("已领取！");
            }
            activityParticipant.setMemberId(user.getId());
            activityParticipant.setPhone(user.getMobile());
            activityParticipant.setNickName(CommonUtil.isEmpty(user.getName()) ? user.getNickName() : user.getName());
            activityParticipant.setActivityId("1");
            if(activityParticipant.getState()==null) {
                activityParticipant.setState(0);
            }
            this.activityParticipantMapper.insertSelective(activityParticipant);
            return ServerResponse.createBySuccessMessage("礼品数量有限，先到先得，请联系工作人员线下发放礼品！");
        }else{
            return ServerResponse.createByErrorMessage("未达到领取条件！");
        }
    }

}
