package com.dangjia.acg.service.core;

import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.api.actuary.BudgetWorkerAPI;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.mapper.core.IHouseFlowCountDownTimeMapper;
import com.dangjia.acg.mapper.core.IHouseFlowMapper;
import com.dangjia.acg.mapper.core.IHouseWorkerMapper;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.worker.IRewardPunishRecordMapper;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.core.HouseFlowCountDownTime;
import com.dangjia.acg.modle.core.HouseWorker;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.worker.RewardPunishRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * author: Ronalcheng
 * Date: 2018/10/31 0031
 * Time: 17:00
 */
@Service
public class HouseFlowService {

    @Autowired
    private IHouseFlowMapper houseFlowMapper;
    @Autowired
    private IHouseMapper houseMapper;
    @Autowired
    private IWorkerTypeMapper workerTypeMapper;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private IHouseWorkerMapper houseWorkerMapper;
    @Autowired
    private IHouseFlowCountDownTimeMapper houseFlowCountDownTimeMapper;
    @Autowired
    private IMemberMapper memberMapper;
    @Autowired
    private BudgetWorkerAPI budgetWorkerAPI;
    @Autowired
    private IRewardPunishRecordMapper rewardPunishRecordMapper;



    /**
     * 抢单列表
     */
    public ServerResponse getGrabList(String userToken,String cityId){
        try {
            List<Map<String, Object>> grabList = new ArrayList<Map<String, Object>>();//返回的任务list
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            Member member = accessToken.getMember();
            //待设计师抢单列表
            if (member.getWorkerType() == 1) {
                Example example = new Example(HouseFlow.class);
                example.createCriteria().andEqualTo("workType", 2).andEqualTo("workerTypeId", "1")
                        .andEqualTo("cityId", cityId);
                List<HouseFlow> houseFlowList = houseFlowMapper.selectByExample(example);
                return ServerResponse.createBySuccess("查询成功", houseFlowList);
            } else if (member.getWorkerType() == 3) {//如果是大管家
                Example example = new Example(HouseFlow.class);
                example.createCriteria().andEqualTo("workType", 2).andEqualTo("workerTypeId", "3")
                        .andEqualTo("cityId", cityId);
                List<HouseFlow> hfList = houseFlowMapper.selectByExample(example);
                Example example2 = new Example(HouseWorker.class);
                example2.createCriteria().andEqualTo("workerId", member.getId());
                List<HouseWorker> hwList = houseWorkerMapper.selectByExample(example2);//查出自己的所有已抢单
                //移除自己抢过的包括被拒的
                for (HouseWorker hw : hwList) {
                    for (HouseFlow hf : hfList) {
                        if (hf.getId().equals(hw.getHouseFlowId())) {
                            hfList.remove(hf);
                            break;
                        }
                    }
                }
                for (HouseFlow hf : hfList) {
                    House house = houseMapper.selectByPrimaryKey(hf.getHouseId());
                    /************拼接返回抢单列表************/
                    Map<String, Object> map = new HashMap<String, Object>();
                    Example example3 = new Example(HouseFlowCountDownTime.class);
                    example3.createCriteria().andEqualTo("workerId", member.getId()).andEqualTo("houseFlowId", hf.getId());
                    List<HouseFlowCountDownTime> houseFlowDownTimeList = houseFlowCountDownTimeMapper.selectByExample(example3);
                    HouseFlowCountDownTime houseFlowCountDownTime = new HouseFlowCountDownTime();
                    if (houseFlowDownTimeList == null || houseFlowDownTimeList.size() == 0) {//如果这个单没有存在倒计时，说明是新单没有被该工匠刷到过
                        houseFlowCountDownTime.setWorkerId(member.getId());//工匠id
                        houseFlowCountDownTime.setHouseFlowId(hf.getId());//houseFlowId
                        BigDecimal evaluation = member.getEvaluationScore();
                        if (evaluation == null) {
                            member.setEvaluationScore(new BigDecimal(60));
                            memberMapper.updateByPrimaryKeySelective(member);
                        }
                        //抢单列表根据积分设置排队时间
                        Date date = this.getCountDownTime(member.getEvaluationScore());
                        houseFlowCountDownTime.setCountDownTime(date);//可抢单时间
                        List<HouseFlowCountDownTime> houseFlowDownTimeList2 = houseFlowCountDownTimeMapper.selectByExample(example3);
                        if (houseFlowDownTimeList2 == null || houseFlowDownTimeList2.size() == 0) {//新增此数据前查询是否已存在，避免重复插入
                            houseFlowCountDownTimeMapper.insert(houseFlowCountDownTime);
                        }
                    } else {
                        houseFlowCountDownTime = houseFlowDownTimeList.get(0);
                    }
                    map.put("houseFlowId", hf.getId());
                    map.put("houseName", (house.getResidential() == null ? "*" : house.getResidential())
                            + (house.getBuilding() == null ? "*" : house.getBuilding()) + "栋"
                            + (house.getUnit() == null ? "*" : house.getUnit()) + "单元" + (house.getNumber() == null ? "*" : house.getNumber()) + "号");
                    map.put("square", "面积 " + house.getSquare() + "m²");//面积
                    map.put("houseMember", "业主 " + member.getName());//业主名称
                    ServerResponse serverResponse = budgetWorkerAPI.getWorkerTotalPrice(hf.getId(), hf.getWorkerTypeId());
                    Double totalPrice = 0.00;
                    if (serverResponse.isSuccess()) {
                        JSONObject obj = JSONObject.parseObject(serverResponse.getResultObj().toString());
                        totalPrice = Double.parseDouble(obj.getString("totalPrice"));
                    }
                    map.put("workertotal", "￥" + totalPrice);//工钱
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    map.put("releaseTime", "时间 " + (hf.getReleaseTime() == null ? "" : sdf.format(hf.getReleaseTime())));//发布时间
                    Long countDownTime = houseFlowCountDownTime.getCountDownTime().getTime() - new Date().getTime();//获取倒计时
                    map.put("countDownTime", countDownTime);//可接单时间
                    grabList.add(map);
                }
                return ServerResponse.createBySuccess("查询成功", grabList);
            } else {
                List<HouseFlow> hfList = new ArrayList<HouseFlow>();//所有其他工匠可抢单
                if (member.getWorkerTypeId() == null) {//工匠类别为空，默认查询拆除的抢单
                    Example example = new Example(HouseFlow.class);
                    example.createCriteria().andEqualTo("workType", 2).andEqualTo("workerTypeId", 4)
                            .andEqualTo("cityId", cityId);
                    hfList = houseFlowMapper.selectByExample(example);
                } else {
                    Example example = new Example(HouseFlow.class);
                    example.createCriteria().andEqualTo("workType", 2).andEqualTo("workerTypeId", member.getWorkerTypeId())
                            .andEqualTo("cityId", cityId);
                    hfList = houseFlowMapper.selectByExample(example);
                }
                Example example2 = new Example(HouseWorker.class);
                example2.createCriteria().andEqualTo("workerId", member.getId());
                List<HouseWorker> hwList = houseWorkerMapper.selectByExample(example2);//查出自己的所有已抢单
                //移除自己抢过的包括被拒的
                for (HouseWorker hw : hwList) {
                    for (HouseFlow hf : hfList) {
                        if (hf.getId().equals(hw.getHouseFlowId())) {
                            hfList.remove(hf);
                            break;
                        }
                    }
                }
                for (HouseFlow hf : hfList) {
                    House house = houseMapper.selectByPrimaryKey(hf.getHouseId());
                    /************拼接返回抢单列表************/
                    Example example3 = new Example(HouseFlowCountDownTime.class);
                    example3.createCriteria().andEqualTo("workerId", member.getId()).andEqualTo("houseFlowId", hf.getId());
                    List<HouseFlowCountDownTime> houseFlowDownTimeList = houseFlowCountDownTimeMapper.selectByExample(example3);
                    Map<String, Object> map = new HashMap<String, Object>();
                    HouseFlowCountDownTime houseFlowCountDownTime = new HouseFlowCountDownTime();
                    if (houseFlowDownTimeList == null || houseFlowDownTimeList.size() == 0) {//如果这个单没有存在倒计时，说明是新单没有被该工匠刷到过
                        houseFlowCountDownTime.setWorkerId(member.getId());//工匠id
                        houseFlowCountDownTime.setHouseFlowId(hf.getId());//houseFlowId
                        BigDecimal evaluation = member.getEvaluationScore();
                        if (evaluation == null) {
                            member.setEvaluationScore(new BigDecimal(60));
                            memberMapper.updateByPrimaryKeySelective(member);
                        }
                        //抢单列表根据积分设置排队时间
                        Date date = this.getCountDownTime(member.getEvaluationScore());
                        houseFlowCountDownTime.setCountDownTime(date);//可抢单时间
                        example3.createCriteria().andEqualTo("workerId", member.getId()).andEqualTo("houseFlowId", hf.getId());
                        List<HouseFlowCountDownTime> houseFlowDownTimeList2 = houseFlowCountDownTimeMapper.selectByExample(example3);
                        if (houseFlowDownTimeList2 == null || houseFlowDownTimeList2.size() == 0) {//新增此数据前查询是否已存在，避免重复插入
                            houseFlowCountDownTimeMapper.insert(houseFlowCountDownTime);
                        }
                    } else {
                        houseFlowCountDownTime = houseFlowDownTimeList.get(0);
                    }
                    map.put("houseFlowId", hf.getId());
                    map.put("houseName", (house.getResidential() == null ? "*" : house.getResidential())
                            + (house.getBuilding() == null ? "*" : house.getBuilding()) + "栋"
                            + (house.getUnit() == null ? "*" : house.getUnit()) + "单元" + (house.getNumber() == null ? "*" : house.getNumber()) + "号");
                    map.put("square", "面积 " + house.getSquare() + "m²");//面积
                    map.put("houseMember", "业主 " + member.getName());//业主名称
                    ServerResponse serverResponse = budgetWorkerAPI.getWorkerTotalPrice(hf.getHouseId(), hf.getWorkerTypeId());
                    Double totalPrice = 0.00;
                    if (serverResponse.isSuccess()) {
                        if (serverResponse.getResultObj() != null) {
                            JSONObject obj = JSONObject.parseObject(serverResponse.getResultObj().toString());
                            totalPrice = Double.parseDouble(obj.getString("totalPrice"));
                        }
                    }
                    map.put("workertotal", "￥" + totalPrice);//工钱
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    map.put("releaseTime", "时间 " + (hf.getReleaseTime() == null ? "" : sdf.format(hf.getReleaseTime())));//发布时间
                    Long countDownTime = houseFlowCountDownTime.getCountDownTime().getTime() - new Date().getTime();//获取倒计时
                    map.put("countDownTime", countDownTime);//可接单时间
                    grabList.add(map);
                }
                return ServerResponse.createBySuccess("查询成功", grabList);
            }
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("系统出错,查询失败");
        }
    }

    /**
     * 精算生成houseFlow
     */
    public ServerResponse makeOfBudget(String houseId,String workerTypeId) {
        Map<String, Object> map = new HashMap<String, Object>();
        House house = houseMapper.selectByPrimaryKey(houseId);
        WorkerType workerType = workerTypeMapper.selectByPrimaryKey(workerTypeId);
        if(house == null){
            return ServerResponse.createByErrorMessage("根据houseId查询房产失败");
        }
        if(workerType == null){
            return ServerResponse.createByErrorMessage("根据workerTypeId查询失败");
        }
        Example example = new Example(HouseFlow.class);
        example.createCriteria().andEqualTo("houseId", houseId).andEqualTo("workerTypeId", workerTypeId);
        List<HouseFlow> houseFlowList = houseFlowMapper.selectByExample(example);
        if(houseFlowList.size() > 1){
            return ServerResponse.createByErrorMessage("精算异常,请联系平台部");
        }else if(houseFlowList.size() == 1){
            HouseFlow houseFlow = houseFlowList.get(0);
            map.put("houseFlowId", houseFlow.getId());
            return ServerResponse.createBySuccess("查询houseFlow成功", map);
        }else{
            HouseFlow houseFlow = new HouseFlow();
            houseFlow.setWorkerTypeId(workerTypeId);
            houseFlow.setWorkerType(workerType.getType());
            houseFlow.setMemberId(house.getMemberId());
            houseFlow.setHouseId(house.getId());
            houseFlow.setState(workerType.getState());
            houseFlow.setSort(workerType.getSort());
            houseFlow.setSafe(workerType.getSafeState());
            houseFlow.setWorkType(1);//生成默认房产，工匠还不能抢
            houseFlow.setCityId(house.getCityId());
            houseFlowMapper.insert(houseFlow);
            map.put("houseFlowId", houseFlow.getId());
            return ServerResponse.createBySuccess("创建houseFlow成功", map);
        }
    }
    /**
     * 抢单验证
     */
    public ServerResponse setGrabVerification(String userToken,String houseFlowId) {
            return this.commonValidate(userToken,houseFlowId);
    }
    /**
     * 放弃此单
     */
    public ServerResponse setGiveUpOrder(String userToken,String houseFlowId){
        try{
            AccessToken accessToken = redisClient.getCache(userToken+ Constants.SESSIONUSERID,AccessToken.class);
            Member member=accessToken.getMember();
            HouseFlow hf = houseFlowMapper.selectByPrimaryKey(houseFlowId);
            Example example = new Example(HouseWorker.class);
            example.createCriteria().andEqualTo("workerId", member.getId()).andEqualTo("houseFlowId", houseFlowId);
            List<HouseWorker> hwList = houseWorkerMapper.selectByExample(example);//查出自己的
            HouseWorker houseWorker= hwList.get(0);
            if(member.getWorkerType()==3){//大管家
                if(hf.getWorkType()==3&&hf.getSupervisorStart()==0){//已抢单待支付，并且未开工(无责取消)
                    hf.setWorkType(2);//抢s单状态更改为待抢单
                    hf.setReleaseTime(new Date());//set发布时间
                    houseFlowMapper.updateByPrimaryKeySelective(hf);
                    houseWorker.setWorkType(7);//抢单状态改为（7抢单后放弃）
                    houseWorker.setWorkSteta(4);//修改此单为放弃
                    houseWorkerMapper.updateByPrimaryKeySelective(houseWorker);
                }else{
                    if(hf.getSupervisorStart()!=0){//已开工的状态不可放弃
                        return ServerResponse.createBySuccessMessage("您已确认开工，不可放弃！");
                    }else{
                        if(hf.getWorkType()==4){//已支付（有责取消）
                            hf.setWorkType(2);//抢单状态更改为待抢单
                            hf.setReleaseTime(new Date());//set发布时间
                            houseFlowMapper.updateByPrimaryKeySelective(hf);
                            BigDecimal evaluation =member.getEvaluationScore();
                            if(evaluation==null){//如果积分为空，默认60分
                                member.setEvaluationScore(new BigDecimal(60));
                            }
                            BigDecimal evaluation2=member.getEvaluationScore().subtract(new BigDecimal(2));//积分减2分
                            member.setEvaluationScore(evaluation2);
                            memberMapper.updateByPrimaryKeySelective(member);
                            //修改此单为放弃
                            houseWorker.setWorkType(7);//抢单状态改为（7抢单后放弃）
                            houseWorker.setWorkSteta(4);
                            houseWorkerMapper.updateByPrimaryKeySelective(houseWorker);
                        }
                    }
                }
            }else{//普通工匠
                if(hf.getWorkType()==3){//已抢单待支付(无责取消)
                    hf.setWorkType(2);//抢单状态更改为待抢单
                    hf.setReleaseTime(new Date());//set发布时间
                    houseFlowMapper.updateByPrimaryKeySelective(hf);
                }else{
                    if(hf.getWorkSteta()!=3||hf.getWorkSteta()!=0){//已开工的状态不可放弃
                        return ServerResponse.createByErrorMessage("您已在施工，不可放弃！");
                    }else{
                        if(hf.getWorkType()==4){//已支付（有责取消）
                            hf.setWorkType(2);//抢单状态更改为待抢单
                            hf.setReleaseTime(new Date());//set发布时间
                            houseFlowMapper.updateByPrimaryKeySelective(hf);
                            BigDecimal evaluation =member.getEvaluationScore();
                            if(evaluation==null){//如果积分为空，默认60分
                                member.setEvaluationScore(new BigDecimal(60));
                            }
                            BigDecimal evaluation2=member.getEvaluationScore().subtract(new BigDecimal(2));//积分减2分
                            member.setEvaluationScore(evaluation2);
                            memberMapper.updateByPrimaryKeySelective(member);
                        }
                    }
                }
                //修改此单为放弃
                houseWorker.setWorkType(7);//抢单状态改为（7抢单后放弃）
                houseWorker.setWorkSteta(4);
                houseWorkerMapper.updateByPrimaryKeySelective(houseWorker);
            }
            return ServerResponse.createBySuccessMessage("放弃成功！");
        }catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("系统出错，放弃失败！");
        }
    }

    //拒单
    public ServerResponse setRefuse(String userToken,String houseFlowId){
        try{
            AccessToken accessToken = redisClient.getCache(userToken+ Constants.SESSIONUSERID,AccessToken.class);
            Member member=accessToken.getMember();
            HouseFlow hf = houseFlowMapper.selectByPrimaryKey(houseFlowId);
            //查询排队时间,并修改重排
            Example example = new Example(HouseFlowCountDownTime.class);
            example.createCriteria().andEqualTo("workerId", member.getId()).andEqualTo("houseFlowId", hf.getId());
            List<HouseFlowCountDownTime> houseFlowDownTimeList= houseFlowCountDownTimeMapper.selectByExample(example);
            for(HouseFlowCountDownTime h:houseFlowDownTimeList){
                BigDecimal evaluation= member.getEvaluationScore();
                if(evaluation==null){
                    member.setEvaluationScore(new BigDecimal(60));
                    memberMapper.updateByPrimaryKeySelective(member);
                }
                if(Double.parseDouble(member.getEvaluationScore().toString())<70){//积分小于70分，加20分钟
                    Calendar now=Calendar.getInstance();
                    now.add(Calendar.MINUTE,20);//当前时间加20分钟
                    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String dateStr=sdf.format(now.getTimeInMillis());
                    Date date=sdf.parse(dateStr);
                    h.setCountDownTime(date);//可抢单时间
                }else if(Double.parseDouble(member.getEvaluationScore().toString())>=70&&Double.parseDouble(member.getEvaluationScore().toString())<80){
                    Calendar now=Calendar.getInstance();
                    now.add(Calendar.MINUTE,10);//当前时间加10分钟
                    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String dateStr=sdf.format(now.getTimeInMillis());
                    Date date=sdf.parse(dateStr);
                    h.setCountDownTime(date);//可抢单时间
                }else if(Double.parseDouble(member.getEvaluationScore().toString())>=80&&Double.parseDouble(member.getEvaluationScore().toString())<90){
                    Calendar now=Calendar.getInstance();
                    now.add(Calendar.MINUTE,5);//当前时间加5分钟
                    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String dateStr=sdf.format(now.getTimeInMillis());
                    Date date=sdf.parse(dateStr);
                    h.setCountDownTime(date);//可抢单时间
                }else{
                    Calendar now=Calendar.getInstance();
                    now.add(Calendar.MINUTE,1);//当前时间加1分钟
                    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String dateStr=sdf.format(now.getTimeInMillis());
                    Date date=sdf.parse(dateStr);
                    h.setCountDownTime(date);//可抢单时间
                }
                houseFlowCountDownTimeMapper.updateByPrimaryKeySelective(h);
            }
            return ServerResponse.createBySuccessMessage("拒单成功！");
        }catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("系统出错，拒单失败！");
        }
    }

    //公用验证方法
    public ServerResponse commonValidate(String userToken,String houseFlowId){
        try{
            AccessToken accessToken = redisClient.getCache(userToken+ Constants.SESSIONUSERID,AccessToken.class);
            Member member=accessToken.getMember();
            HouseFlow hf = houseFlowMapper.selectByPrimaryKey(houseFlowId);
            Example example3 = new Example(HouseFlowCountDownTime.class);
            example3.createCriteria().andEqualTo("workerId", member.getId()).andEqualTo("houseFlowId", hf.getId());
            List<HouseFlowCountDownTime> houseFlowDownTimeList= houseFlowCountDownTimeMapper.selectByExample(example3);
            Example example4 = new Example(RewardPunishRecord.class);
            example4.createCriteria().andEqualTo("workerId", member.getId());
            List<RewardPunishRecord> wraprList=rewardPunishRecordMapper.selectByExample(example4);
            if(hf != null && hf.getGrablock()==2){
                return ServerResponse.createByErrorMessage("项目已经被抢了！");
            }
            if(member.getCheckType()==0){
                //审核中的人不能抢单
                return ServerResponse.createByErrorMessage("您的账户正在审核中！");
            }
            if(member.getCheckType()==1){
                //审核未通过 的人不能抢单
                return ServerResponse.createByErrorMessage("您的帐户审核未通过！");
            }
            if(member.getCheckType()==3){
                //被禁用的帐户不能抢单
                return ServerResponse.createByErrorMessage("您的帐户已经被禁用！");
            }
            if(member.getCheckType()==4){
                //冻结的帐户不能抢单
                return ServerResponse.createByErrorMessage("您的帐户已冻结");
            }
            if(member.getCheckType()==5){
                return ServerResponse.createByErrorMessage("您未提交资料审核,请点击【我的】→【我的资料】→完善资料并提交审核！");
            }
            if (wraprList != null) {
                //通过查看奖罚限制抢单时间限制抢单
                for (RewardPunishRecord wrapr : wraprList) {
                    if (wrapr.getGrab() == 2 || wrapr.getDeposit() == 2) {
                        Date wraprDate = wrapr.getGrabExpire();
                        DateFormat longDateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);
                        Date date = new Date();
                        if (date.getTime() < wraprDate.getTime()) {
                            return ServerResponse.createByErrorMessage("您处于平台处罚期内，"+longDateFormat.format(wraprDate)+"以后才能抢单,如有疑问请致电400-168-1231！");
                        }
                    }
                }
            }
            //通过查看奖罚限制抢单时间限制抢单
            if(houseFlowDownTimeList!=null||houseFlowDownTimeList.size()>0){
                HouseFlowCountDownTime houseFlowCountDownTime=houseFlowDownTimeList.get(0);
                Long countDownTime=houseFlowCountDownTime.getCountDownTime().getTime()-new Date().getTime();//获取倒计时
                if(countDownTime>0){//未到时间不能抢单
                    return ServerResponse.createByErrorMessage("您还在排队时间内，请稍后抢单！");
                }
            }
            if(member.getWorkerType()>3){//其他工人
                if(hf.getPause() == 1){
                    return ServerResponse.createByErrorMessage("该房子已暂停施工！");
                }
                List<HouseWorker> hwList=houseWorkerMapper.grabControl(member.getId());//查询未完工工地
                WorkerType wt=workerTypeMapper.selectByPrimaryKey(member.getWorkerTypeId());
                if(member.getWorkerType() != 7&& hwList.size() >= wt.getMethods()){
                    return ServerResponse.createByErrorMessage("您有工地还未完工,暂不能抢单！");
                }
                if (wraprList != null) {//通过查看奖罚限制抢单时间限制抢单
                    for (RewardPunishRecord wrapr : wraprList) {
                        if (wrapr.getGrab() == 2 || wrapr.getDeposit() == 2) {
                            Date wraprDate = wrapr.getGrabExpire();
                            Date date = new Date();
                            if (date.getTime() < wraprDate.getTime()) {
                                return ServerResponse.createByErrorMessage("您的限制抢单处罚时间还未结束！");
                            }
                        }
                    }
                }
                List<HouseWorker> hwlist=houseWorkerMapper.grabOneDayOneTime(member.getId());
                if(hwlist.size() > 0){
                    return ServerResponse.createByErrorMessage("每天只能抢一单哦！");
                }
            }
            return ServerResponse.createBySuccess("通过验证","www.baidu.com");
        }catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("验证出错！");
        }
    }
    /**
     * 抢单列表根据积分设置排队时间
     */
    public Date getCountDownTime(BigDecimal evaluationScore){
        if(Double.parseDouble(evaluationScore.toString())<70){//积分小于70分，加20分钟
            Calendar now=Calendar.getInstance();
            now.add(Calendar.MINUTE,20);//当前时间加20分钟
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateStr=sdf.format(now.getTimeInMillis());
            Date date=null;
            try {
                date=sdf.parse(dateStr);
            }catch (Exception e){
                e.printStackTrace();
            }
           return date;
        }else if(Double.parseDouble(evaluationScore.toString())>=70&&Double.parseDouble(evaluationScore.toString())<80){
            Calendar now=Calendar.getInstance();
            now.add(Calendar.MINUTE,10);//当前时间加10分钟
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateStr=sdf.format(now.getTimeInMillis());
            Date date=null;
            try {
                date=sdf.parse(dateStr);
            }catch (Exception e){
                e.printStackTrace();
            }
            return date;
        }else if(Double.parseDouble(evaluationScore.toString())>=80&&Double.parseDouble(evaluationScore.toString())<90){
            Calendar now=Calendar.getInstance();
            now.add(Calendar.MINUTE,5);//当前时间加5分钟
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateStr=sdf.format(now.getTimeInMillis());
            Date date=null;
            try {
                date=sdf.parse(dateStr);
            }catch (Exception e){
                e.printStackTrace();
            }
            return date;
        }else{
            Calendar now=Calendar.getInstance();
            now.add(Calendar.MINUTE,1);//当前时间加1分钟
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateStr=sdf.format(now.getTimeInMillis());
            Date date=null;
            try {
                date=sdf.parse(dateStr);
            }catch (Exception e){
                e.printStackTrace();
            }
            return date;
        }
    }

    /**
     * 确认开工
     */
    public ServerResponse setConfirmStart(String userToken,String houseFlowId){
        try{
            AccessToken accessToken = redisClient.getCache(userToken+ Constants.SESSIONUSERID,AccessToken.class);
            Member member=accessToken.getMember();
            HouseFlow houseFlow=houseFlowMapper.selectByPrimaryKey(houseFlowId);//查询大管家houseFlow
            houseFlow.setSupervisorStart(1);//大管家进度改为已开工
            houseFlowMapper.updateByPrimaryKeySelective(houseFlow);
            HouseFlow houseFlow2= houseFlowMapper.getNextTopHouseFlow(houseFlow.getHouseId()).get(0);//根据当前工序查下一工序
            if(houseFlow2!=null) {
                if (houseFlow2.getWorkType() == null || houseFlow2.getWorkType() == 1) {
                    houseFlow2.setWorkType(2);//把下一个工种弄成待抢单
                    houseFlow2.setReleaseTime(new Date());//发布时间
                    System.out.println("HouseWorkerOrderServiceImpl 281 改为了2，houseFlowId" + houseFlow2.getId());
                    houseFlowMapper.updateByPrimaryKeySelective(houseFlow2);
                }
            }
            return ServerResponse.createBySuccessMessage("确认开工成功");
        }catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("系统出错，确认开工失败");
        }
    }

    /**
     * 精算详情需要
     */
    public List<Map<String,String>> getFlowList(String houseId){
        Example example = new Example(HouseFlow.class);
        example.createCriteria().andEqualTo("houseId", houseId).andEqualTo("state", 0).andGreaterThan("workerType",2);
        List<HouseFlow> houseFlowList = houseFlowMapper.selectByExample(example);
        List<Map<String,String>> mapList = new ArrayList<Map<String,String>>();
        for (HouseFlow hf : houseFlowList){
            Map<String,String> map = new HashMap<String, String>();
            map.put("workerTypeId" , hf.getWorkerTypeId());
            map.put("name" , workerTypeMapper.selectByPrimaryKey(hf.getWorkerTypeId()).getName());
            mapList.add(map);
        }
        return mapList;
    }

    /**
     * 根据houseId查询除设计精算外的可用工序
     */
    public List<HouseFlow>  getFlowByhouseIdNot12(String houseId){
      try{
          List<HouseFlow> houseFlowList= houseFlowMapper.getFlowByhouseIdNot12(houseId);
          return houseFlowList;
      }catch (Exception e){
          e.printStackTrace();
          return null;
      }
    }

    /**
     * 根据houseId和工种类型查询HouseFlow
     */
    public HouseFlow  getHouseFlowByHidAndWty(String houseId,Integer workerType){
        try{
           HouseFlow houseFlow= houseFlowMapper.getHouseFlowByHidAndWty(houseId,workerType);
            return houseFlow;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
