package com.dangjia.acg.service.activity;

import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.api.basics.ProductAPI;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.activity.ActivityRedPackDTO;
import com.dangjia.acg.dto.activity.ActivityRedPackRecordDTO;
import com.dangjia.acg.mapper.activity.IActivityRedPackMapper;
import com.dangjia.acg.mapper.activity.IActivityRedPackRecordMapper;
import com.dangjia.acg.mapper.activity.IActivityRedPackRuleMapper;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.modle.activity.ActivityRedPack;
import com.dangjia.acg.modle.activity.ActivityRedPackRecord;
import com.dangjia.acg.modle.activity.ActivityRedPackRule;
import com.dangjia.acg.modle.basics.Product;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.member.Member;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * 优惠券管理
 * author: qiyuxiang
 * Date: 2018/10/31 0031
 * Time: 20:18
 */
@Service
public class RedPackService {

    @Autowired
    private IActivityRedPackMapper activityRedPackMapper;
    @Autowired
    private IActivityRedPackRecordMapper activityRedPackRecordMapper;

    @Autowired
    private IActivityRedPackRuleMapper activityRedPackRuleMapper;

    @Autowired
    private IWorkerTypeMapper workerTypeMapper;
    @Autowired
    private ProductAPI productAPI;
    @Autowired
    private IMemberMapper memberMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private RedisClient redisClient;
    /**
     * 获取所有优惠券
     * @param activityRedPack
     * @return
     */
    public ServerResponse queryActivityRedPacks(HttpServletRequest request, PageDTO pageDTO,ActivityRedPack activityRedPack,String isEndTime) {
        Example example = new Example(ActivityRedPack.class);
        Example.Criteria criteria=example.createCriteria();
        if (!CommonUtil.isEmpty(activityRedPack.getName())) {
            criteria.andLike("name", "%" + activityRedPack.getName() + "%");
        }
        if(!CommonUtil.isEmpty(activityRedPack.getCityId())) {
            criteria.andEqualTo("cityId",activityRedPack.getCityId());
        }
        if(!CommonUtil.isEmpty(isEndTime)&&"0".equals(isEndTime)) {
            criteria.andLessThanOrEqualTo("endDate",new Date());
        }
        if(!CommonUtil.isEmpty(activityRedPack.getDeleteState())) {
            criteria.andEqualTo("deleteState",activityRedPack.getDeleteState());
        }
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        example.orderBy("modifyDate").desc();
        List<ActivityRedPack> list = activityRedPackMapper.selectByExample(example);
        PageInfo pageResult = new PageInfo(list);
        return ServerResponse.createBySuccess("ok",pageResult);
    }

    /**
     * 获取所有有效的优惠券
     * @param cityId
     * @param memberId 指定用户是否已经领取该优惠券
     * @return
     */
    public ServerResponse getActivityRedPacks(HttpServletRequest request, PageDTO pageDTO,String cityId,String memberId) {
        Example example = new Example(ActivityRedPack.class);
        Example.Criteria criteria=example.createCriteria();
        criteria.andEqualTo(ActivityRedPack.CITY_ID,cityId);
        criteria.andGreaterThan(ActivityRedPack.END_DATE,new Date());
        criteria.andGreaterThan(ActivityRedPack.SURPLUS_NUMS,"0");
        criteria.andEqualTo(ActivityRedPack.DELETE_STATE,"0");
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        example.orderBy(ActivityRedPack.MODIFY_DATE).desc();
        List<ActivityRedPack> list = activityRedPackMapper.selectByExample(example);
        List<Map> listMap = new ArrayList<>();
        PageInfo pageResult = new PageInfo(list);

        for (ActivityRedPack redPack:list) {
            Map map=BeanUtils.beanToMap(redPack);
            Example exampleRecord = new Example(ActivityRedPackRecord.class);
            exampleRecord.createCriteria().andEqualTo(ActivityRedPackRecord.RED_PACK_ID,redPack.getId()).andEqualTo(ActivityRedPackRecord.MEMBER_ID,memberId);
            map.put("receiveNum", activityRedPackRecordMapper.selectCountByExample(exampleRecord));

            Example exampleRule = new Example(ActivityRedPackRule.class);
            exampleRule.createCriteria().andEqualTo(ActivityRedPackRule.ACTIVITY_RED_PACK_ID,redPack.getId());
            List<ActivityRedPackRule> red= activityRedPackRuleMapper.selectByExample(exampleRule);
            map.put("redPackRules",red);
            listMap.add(map);
        }
        pageResult.setList(listMap);
        return ServerResponse.createBySuccess("ok",pageResult);
    }
    /**
     * 获取当前优惠券客户使用记录总数目
     * @param activityRedPackRecord
     * @return
     */
    public ServerResponse queryRedPackRecordCount(HttpServletRequest request,ActivityRedPackRecord activityRedPackRecord) {
        String userToken = request.getParameter(Constants.USER_TOKEY);
        AccessToken accessToken=redisClient.getCache(userToken+ Constants.SESSIONUSERID,AccessToken.class);
        activityRedPackRecord.setMemberId(accessToken.getMemberId());
        String[] haveReceives=new String[]{"0","1","2,3"};
        String[] counts=new String[]{"0","0","0"};
        for (int i = 0; i < haveReceives.length; i++) {
            Example example = new Example(ActivityRedPackRecord.class);
            Example.Criteria criteria=example.createCriteria();
            if(!CommonUtil.isEmpty(activityRedPackRecord.getMemberId())) {
                criteria.andEqualTo("memberId",activityRedPackRecord.getMemberId());
            }
            if(haveReceives[i].length()>2){
                criteria.andNotEqualTo("haveReceive", "0");
                criteria.andNotEqualTo("haveReceive", "1");
            }else {
                criteria.andEqualTo("haveReceive", haveReceives[i]);
            }
            int count = activityRedPackRecordMapper.selectCountByExample(example);
            counts[i]=String.valueOf(count);
        }
        return ServerResponse.createBySuccess("ok",StringUtils.join(counts,","));
    }
    /**
     * 获取当前优惠券客户使用记录
     * @param activityRedPackRecord
     * @return
     */
    public PageInfo queryActivityRedPackRecords(HttpServletRequest request,ActivityRedPackRecord activityRedPackRecord,PageDTO pageDTO) {
        String userToken = request.getParameter(Constants.USER_TOKEY);
        AccessToken accessToken=redisClient.getCache(userToken+ Constants.SESSIONUSERID,AccessToken.class);
        activityRedPackRecord.setMemberId(accessToken.getMemberId());
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<ActivityRedPackRecordDTO> list = activityRedPackRecordMapper.queryActivityRedPackRecords(activityRedPackRecord);
        PageInfo pageResult = new PageInfo(list);
        //格式化有效优惠券集合
        for (ActivityRedPackRecordDTO redPacetResult:list) {
            redPacetResult.toConvert();
        }
        pageResult.setList(list);
        return pageResult;
    }

    public ServerResponse getActivityRedPack(HttpServletRequest request, ActivityRedPackDTO activityRedPackDTO) {
        ActivityRedPack activityRedPack = activityRedPackMapper.selectByPrimaryKey(activityRedPackDTO.getId());
        request.setAttribute(Constants.CITY_ID,activityRedPack.getCityId());
        BeanUtils.beanToBean(activityRedPack,activityRedPackDTO);
        Example example = new Example(ActivityRedPackRule.class);
        example.createCriteria().andEqualTo("activityRedPackId",activityRedPackDTO.getId());
        List<ActivityRedPackRule> redPackRule=activityRedPackRuleMapper.selectByExample(example);
        activityRedPackDTO.setRedPackRule(redPackRule);
        if(activityRedPackDTO.getFromObjectType()==0){
            WorkerType workerType= workerTypeMapper.selectByPrimaryKey(activityRedPackDTO.getFromObject());
            if(workerType!=null){
                activityRedPackDTO.setFromObjectName(workerType.getName());
            }
        }
        if(activityRedPackDTO.getFromObjectType()==1){

            ServerResponse serverResponse=productAPI.getGoodsByGid(request,activityRedPackDTO.getFromObject());
            if(serverResponse!=null&&serverResponse.getResultObj()!=null){
                if(serverResponse.getResultObj() instanceof JSONObject){
                    JSONObject goods= (JSONObject)serverResponse.getResultObj();
                    activityRedPackDTO.setFromObjectName(goods.getString("name"));
                }
            }
        }
        if(activityRedPackDTO.getFromObjectType()==2){
            ServerResponse serverResponse=productAPI.getProductById(request,activityRedPackDTO.getFromObject());
            if(serverResponse!=null&&serverResponse.getResultObj()!=null){
                if(serverResponse.getResultObj() instanceof Product){
                    Product product= (Product)serverResponse.getResultObj();
                    activityRedPackDTO.setFromObjectName(product.getName());
                }
            }
        }
        return ServerResponse.createBySuccess("ok",activityRedPackDTO);
    }
    /**
     * 修改
     * @param activityRedPack
     * @return
     */
    public ServerResponse editActivityRedPack(HttpServletRequest request, ActivityRedPack activityRedPack,int[] num, BigDecimal[] money,  BigDecimal[] satisfyMoney) {
        activityRedPack.setModifyDate(new Date());
        if(activityRedPack.getNum()!=null&&activityRedPack.getNum()>0){
            activityRedPack.setSurplusNums(activityRedPack.getNum());
        }
        if(this.activityRedPackMapper.updateByPrimaryKeySelective(activityRedPack)>0){
            if(!CommonUtil.isEmpty(num)&&num.length>0) {
                addActivityRedPackRule(activityRedPack,num,money,satisfyMoney);
            }
            return ServerResponse.createBySuccessMessage("ok");
        }else{
            return ServerResponse.createByErrorMessage("修改失败，请您稍后再试");
        }
    }
    /**
     * 关闭
     * @param id
     * @return
     */
    public ServerResponse closeActivityRedPack(String id) {
        ActivityRedPack activity=new ActivityRedPack();
        activity.setId(id);
        activity.setDeleteState(1);
        if(this.activityRedPackMapper.updateByPrimaryKeySelective(activity)>0){
            return ServerResponse.createBySuccessMessage("ok");
        }else{
            return ServerResponse.createByErrorMessage("关闭失败，请您稍后再试");
        }
    }

    /**
     * 关闭
     * @param id
     * @return
     */
    public ServerResponse closeActivityRedPackRecord(String id) {
        ActivityRedPackRecord activity=new ActivityRedPackRecord();
        activity.setId(id);
        activity.setHaveReceive(2);
        if(this.activityRedPackRecordMapper.updateByPrimaryKeySelective(activity)>0){
            return ServerResponse.createBySuccessMessage("ok");
        }else{
            return ServerResponse.createByErrorMessage("关闭失败，请您稍后再试");
        }
    }
    /**
     * 新增
     * @param activityRedPack
     * @return
     */
    public ServerResponse addActivityRedPack(HttpServletRequest request,ActivityRedPack activityRedPack,int[] num, BigDecimal[] money,  BigDecimal[] satisfyMoney) {
        activityRedPack.setDeleteState(0);
        if(activityRedPack.getNum()!=null&&activityRedPack.getNum()>0){
            activityRedPack.setSurplusNums(activityRedPack.getNum());
        }
        if(this.activityRedPackMapper.insertSelective(activityRedPack)>0){
            addActivityRedPackRule(activityRedPack,num,money,satisfyMoney);
            return ServerResponse.createBySuccessMessage("ok");
        }else{
            return ServerResponse.createByErrorMessage("新增失败，请您稍后再试");
        }
    }

    /**
     * 设置优惠券优惠券
     * @param activityRedPack
     * @return
     */
    public void addActivityRedPackRule(ActivityRedPack activityRedPack,int[] num, BigDecimal[] money,  BigDecimal[] satisfyMoney) {
        Example example = new Example(ActivityRedPackRule.class);
        Example.Criteria criteria=example.createCriteria();
        criteria.andEqualTo("activityRedPackId",activityRedPack.getId());
        activityRedPackRuleMapper.deleteByExample(example);
        if(!CommonUtil.isEmpty(num)){
            for (int i = 0; i < num.length; i++) {
                ActivityRedPackRule activityRedPackRule=new ActivityRedPackRule();
                activityRedPackRule.setActivityRedPackId(activityRedPack.getId());
                activityRedPackRule.setNum(num[i]);
                activityRedPackRule.setMoney(money[i]);
                activityRedPackRule.setSatisfyMoney(satisfyMoney[i]==null?new BigDecimal(0):satisfyMoney[i]);
                activityRedPackRuleMapper.insert(activityRedPackRule);
            }
        }
    }


    /**
     * 多用户推送优惠券
     * @param phones 手机号,数组字符串，以逗号分隔
     * @param redPackId 优惠券主表ID
     * @param redPackRuleIds 优惠券规则ID,数组字符串，以逗号分隔
     * @return
     */
    public ServerResponse sendMemberPadPackBatch(String phones,String redPackId,String redPackRuleIds){
        if(!CommonUtil.isEmpty(phones)){
            String[] phone=StringUtils.split(phones,",");
            if(phone!=null&&phone.length>0) {
                for (String p :phone) {
                    Member user = new Member();
                    user.setMobile(phones);
                    user = memberMapper.getUser(user);
                    if(user==null){
                        user = memberMapper.selectByPrimaryKey(phones);
                    }
                    if(user==null){
                        return ServerResponse.createByErrorMessage("用户（"+p+"）未注册！");
                    }
                    //指定优惠券规则发放
                    if(!StringUtils.isEmpty(redPackRuleIds)) {
                        String[] redPackRuleId = StringUtils.split(redPackRuleIds, ",");
                        for (String rprid : redPackRuleId) {
                            String msg=sendMemberRadPack(user,redPackId,rprid);
                            if(!StringUtils.isEmpty(msg)){
                                return ServerResponse.createByErrorMessage(msg);
                            }
                        }
                    }
                }
            }
        }
        return ServerResponse.createBySuccessMessage("推送成功！");
    }

    /**
     * 发放优惠券到会员账户
     * @param member 会员
     * @param redPackId 优惠券ID
     * @param redPackRuleId 优惠券规则ID
     * @return
     */
    public String sendMemberRadPack( Member member,String redPackId,String redPackRuleId){
        StringBuffer msg=new StringBuffer();
        ActivityRedPackRecord activityRedPackRecord=new ActivityRedPackRecord();
        activityRedPackRecord.setMemberId(member.getId());
        activityRedPackRecord.setRedPackId(redPackId);
        activityRedPackRecord.setRedPackRuleId(redPackRuleId);
        List<ActivityRedPackRecordDTO> activityRedPackRecordList = activityRedPackRecordMapper.queryActivityRedPackRecords(activityRedPackRecord);
        //判断是否已领取同类优惠券
        if (activityRedPackRecordList != null&&activityRedPackRecordList.size()>0) {
            msg.append("已领取，无法重复领取！");
            return msg.toString();
        }
        //检验优惠券发放数量是否有剩余
        ActivityRedPack activityRedPack = activityRedPackMapper.selectByPrimaryKey(redPackId);
        if (activityRedPack.getEndDate().getTime() < new Date().getTime()) {
            msg.append("已失效，无法领取！");
            return msg.toString();
        }
        ActivityRedPackRule activityRedPackRule=activityRedPackRuleMapper.selectByPrimaryKey(redPackRuleId);
        if(activityRedPackRule!=null) {

            //得到指定红包规则发放的数量
            for (int i = 0; i < activityRedPackRule.getNum(); i++) {
                if (activityRedPack.getSurplusNums() > 0) {
                    activityRedPack.setSurplusNums(activityRedPack.getSurplusNums() - 1);
                } else {
                    //若无优惠券直推返回
                    msg.append("已无剩余优惠券！");
                    return msg.toString();
                }
                //更新剩余数量
                activityRedPackMapper.updateByPrimaryKeySelective(activityRedPack);
                //开始发放优惠券
                activityRedPackRecord=new ActivityRedPackRecord();
                activityRedPackRecord.setMemberId(member.getId());
                activityRedPackRecord.setRedPackId(redPackId);
                activityRedPackRecord.setRedPackRuleId(redPackRuleId);
                activityRedPackRecord.setHaveReceive(0);
                activityRedPackRecord.setPhone(member.getMobile());
                activityRedPackRecord.setCityId(activityRedPack.getCityId());
                activityRedPackRecord.setEndDate(activityRedPack.getEndDate());
                activityRedPackRecord.setStartDate(activityRedPack.getStartDate());
                activityRedPackRecordMapper.insert(activityRedPackRecord);
            }
        }
        return msg.toString();
    }
    /**
     * 获取所有优惠券客户未使用记录
     * @return
     */
    public List<ActivityRedPackRecord> queryRedPackRecord() {
        Example example = new Example(ActivityRedPackRecord.class);
        Example.Criteria criteria=example.createCriteria();
        criteria.andEqualTo("haveReceive", 0);
        criteria.andLessThanOrEqualTo("endDate",new Date());
        List<ActivityRedPackRecord> count = activityRedPackRecordMapper.selectByExample(example);
        return count;
    }

}
