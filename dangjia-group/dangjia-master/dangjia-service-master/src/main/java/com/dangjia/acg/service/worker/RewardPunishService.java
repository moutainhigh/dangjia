package com.dangjia.acg.service.worker;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.enums.EventStatus;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.dto.worker.RewardPunishCorrelationDTO;
import com.dangjia.acg.dto.worker.RewardPunishRecordDTO;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.worker.*;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.worker.*;
import com.dangjia.acg.service.config.ConfigMessageService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**奖罚管理
 * zmj
 */
@Service
public class RewardPunishService {
    @Autowired
    private IRewardPunishConditionMapper rewardPunishConditionMapper;
    @Autowired
    private IRewardPunishCorrelationMapper rewardPunishCorrelationMapper;
    @Autowired
    private IRewardPunishRecordMapper rewardPunishRecordMapper;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private ConfigMessageService configMessageService;
    @Autowired
    private IHouseMapper houseMapper;

    @Autowired
    private IWorkIntegralMapper iWorkIntegralMapper;
    @Autowired
    private IWorkerDetailMapper iWorkerDetailMapper;

    @Autowired
    private IMemberMapper memberMapper;
    /**
     * 保存奖罚条件及条件明细
     * @return
     */
    public ServerResponse addRewardPunishCorrelation(String id,String name,String content,Integer type,Integer state,String conditionArr){
        try{
            RewardPunishCorrelation rewardPunishCorrelation=new RewardPunishCorrelation();
            rewardPunishCorrelation.setName(name);
            rewardPunishCorrelation.setContent(content);
            rewardPunishCorrelation.setType(type);//0:奖励;1:处罚
            rewardPunishCorrelation.setState(state);
            if(id!=null&&!"".equals(id)){
                rewardPunishCorrelation.setId(id);
                rewardPunishCorrelationMapper.updateByPrimaryKeySelective(rewardPunishCorrelation);//修改奖罚条件
                Example example=new Example(RewardPunishCondition.class);
                example.createCriteria().andEqualTo("rewardPunishCorrelationId",id);
                rewardPunishConditionMapper.deleteByExample(example);//删除关联条件明细
            }else{
                rewardPunishCorrelationMapper.insertSelective(rewardPunishCorrelation);//保存奖罚条件
            }
            JSONArray jsonArray=JSONArray.parseArray(conditionArr);
            for(int i=0;i<jsonArray.size();i++){
                JSONObject object=jsonArray.getJSONObject(i);
                RewardPunishCondition rewardPunishCondition=new RewardPunishCondition();
                rewardPunishCondition.setRewardPunishCorrelationId(rewardPunishCorrelation.getId());
                String conditionName="";
                if(type==0){//奖励
                        conditionName="奖励"+object.getString("quantity")+object.getString("units");
                }else{//处罚
                    if(object.getInteger("type")==3){
                        conditionName="处罚"+object.getString("quantity")+object.getString("units")+"不能接单";
                    }else if(object.getInteger("type")==4){
                        conditionName="处罚"+object.getString("quantity")+object.getString("units")+"不能提现";
                    }else{
                        conditionName="扣除"+object.getString("quantity")+object.getString("units");
                    }
                }
                int types=Integer.parseInt(object.getString("type"));
                rewardPunishCondition.setName(conditionName);
                rewardPunishCondition.setType(types);
                rewardPunishCondition.setQuantity(object.getBigDecimal("quantity"));
                rewardPunishCondition.setUnits(object.getString("units"));
                if(types==3||types==4){
                    String startTime = object.getString("startTime");
                    String endTime = object.getString("endTime");
                    rewardPunishCondition.setStartTime(DateUtil.toDate(startTime));
                    rewardPunishCondition.setEndTime(DateUtil.toDate(endTime));
                }
                rewardPunishConditionMapper.insertSelective(rewardPunishCondition);//保存奖罚条件明细
            }
            return ServerResponse.createBySuccessMessage("保存奖罚条件成功");
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("保存奖罚条件失败");
        }
    }

    /**
     * 删除奖罚条件及条件明细
     * @return
     */
    public ServerResponse deleteRewardPunishCorrelation(String id){
        try{
            if(id!=null&&!"".equals(id)){
                rewardPunishCorrelationMapper.deleteByPrimaryKey(id);
                Example example=new Example(RewardPunishCondition.class);
                example.createCriteria().andEqualTo("rewardPunishCorrelationId",id);
                rewardPunishConditionMapper.deleteByExample(example);//删除关联条件明细
            }else{
                return ServerResponse.createByErrorMessage("删除奖罚条件失败");
            }
            return ServerResponse.createBySuccessMessage("删除奖罚条件成功");
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("删除奖罚条件失败");
        }
    }

    /**
     * 查询所有奖罚条件及条件明细
     * @return
     */
    public ServerResponse queryCorrelation(Integer pageNum, Integer pageSize,String name,Integer type){
        try{
            PageHelper.startPage(pageNum, pageSize);
            List<RewardPunishCorrelationDTO> correlationList=rewardPunishCorrelationMapper.queryCorrelation(name,type);
            PageInfo pageResult = new PageInfo(correlationList);
            List<Map<String,Object>> listMap=new ArrayList<>();
            for(RewardPunishCorrelationDTO correlation:correlationList){
                Map<String,Object> correlationMap= BeanUtils.beanToMap(correlation);
                String conditionArr="";
                List<RewardPunishCondition> conditionList=correlation.getConditionList();
                for(int i = 0;i<conditionList.size();i++){
                    RewardPunishCondition condition=conditionList.get(i);
                    if(conditionList.size()-1==i){
                        conditionArr+=condition.getName();
                    }else{
                        conditionArr+=condition.getName()+",";
                    }
                }
                correlationMap.put("conditionArr",conditionArr);
                listMap.add(correlationMap);
            }
            pageResult.setList(listMap);
            return ServerResponse.createBySuccess("查询奖罚条件成功",pageResult);
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询奖罚条件失败");
        }
    }

    /**
     * 根据id查询奖罚条件及明细
     * @param id
     * @return
     */
    public ServerResponse queryCorrelationById(String id){
        try {
            RewardPunishCorrelation correlation = rewardPunishCorrelationMapper.selectByPrimaryKey(id);
            Map<String,Object> correlationMap=new HashMap<>();
            if(correlation!=null){
                correlationMap= BeanUtils.beanToMap(correlation);
                correlationMap.put("createDate",correlation.getCreateDate().getTime());
                Example example=new Example(RewardPunishCondition.class);
                example.createCriteria().andEqualTo("rewardPunishCorrelationId",correlation.getId());
                List<RewardPunishCondition> conditionList=rewardPunishConditionMapper.selectByExample(example);
                List<Map<String,Object>> conditionArr=new ArrayList<>();
                for(RewardPunishCondition condition:conditionList){
                    Map<String,Object> conditionMap=new HashMap<>();
                    conditionMap.put("conditionId",condition.getId());
                    conditionMap.put("rewardPunishCorrelationId",condition.getRewardPunishCorrelationId());
                    conditionMap.put("conditionName",condition.getName());
                    conditionMap.put("conditionType",condition.getType());
                    conditionMap.put("conditionQuantity",condition.getQuantity());
                    conditionMap.put("conditionUnits",condition.getUnits());
                    if(condition.getStartTime()!=null){
                        conditionMap.put("startTime",condition.getStartTime().getTime());
                    }
                    if(condition.getEndTime()!=null){
                        conditionMap.put("endTime",condition.getEndTime().getTime());
                    }
                    conditionMap.put("conditionCreateDate",condition.getCreateDate().getTime());
                    conditionArr.add(conditionMap);
                }
                correlationMap.put("conditionArr",conditionArr);
            }
            return ServerResponse.createBySuccess("查询奖罚条件成功",correlationMap);
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询奖罚条件失败");
        }
    }

    /**
     * 添加奖罚记录
     * @param userToken
     * @return
     */
    public ServerResponse addRewardPunishRecord(String userToken,String userId,RewardPunishRecord rewardPunishRecord){
        try {
            if(!CommonUtil.isEmpty(userToken)) {
                AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
                Member member = accessToken.getMember();
                rewardPunishRecord.setOperatorId(member.getId());
            }
            if(!CommonUtil.isEmpty(userId)) {
                rewardPunishRecord.setOperatorId(userId);
            }
            if(!CommonUtil.isEmpty(rewardPunishRecord.getRewardPunishCorrelationId())){
                RewardPunishCorrelation rewardPunishCorrelation=rewardPunishCorrelationMapper.selectByPrimaryKey(rewardPunishRecord.getRewardPunishCorrelationId());
                rewardPunishRecord.setType(rewardPunishCorrelation.getType());
            }
            rewardPunishRecord.setState(0);//0:启用;1:不启用
            rewardPunishRecordMapper.insertSelective(rewardPunishRecord);

            //工人ID账户奖罚积分和金额变更
            updateWorkerInfo(rewardPunishRecord.getId());


            if(!CommonUtil.isEmpty(rewardPunishRecord.getHouseId())&&rewardPunishRecord.getHouseId()!=null&&rewardPunishRecord.getMemberId()!=null) {
                House house = houseMapper.selectByPrimaryKey(rewardPunishRecord.getHouseId());
                configMessageService.addConfigMessage(null, "gj", rewardPunishRecord.getMemberId(), "0", "奖罚提醒", String.format(DjConstants.PushMessage.RECORD_OF_REWARDS_AND_PENALTIES, house.getHouseName()), "7");
            }
            return ServerResponse.createBySuccessMessage("新增奖罚记录成功");
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("新增奖罚记录失败");
        }
    }

    public void updateWorkerInfo(String rewardPunishRecordId){
        RewardPunishRecord rewardPunishRecord = rewardPunishRecordMapper.selectByPrimaryKey(rewardPunishRecordId);
        //1.获取被罚的工人id
        //2.获取奖罚条例的类型来判断是奖励还是处罚
        //3.获取条例的明细列表
        if(rewardPunishRecord.getType()==null){
            return;
        }
        Member member= memberMapper.selectByPrimaryKey(rewardPunishRecord.getMemberId());
        Example example =new Example(RewardPunishCondition.class);
        example.createCriteria().andEqualTo(RewardPunishCondition.REWARD_PUNISH_CORRELATION_ID,rewardPunishRecord.getRewardPunishCorrelationId());
        List<RewardPunishCondition> rewardPunishConditionList =  rewardPunishConditionMapper.selectByExample(example);
        for(RewardPunishCondition rewardPunishCondition:rewardPunishConditionList) {
            BigDecimal bigDecimal = rewardPunishCondition.getQuantity();
            BigDecimal bigDecimal2;
            WorkIntegral workIntegral = new WorkIntegral();
            WorkerDetail workerDetail = new WorkerDetail();

            workIntegral.setHouseId(rewardPunishRecord.getHouseId());
            workIntegral.setStatus(0);
            workIntegral.setWorkerId(member.getId());
            workerDetail.setWorkerId(member.getId());
            workerDetail.setWorkerName(member.getName());
            workerDetail.setHouseId(rewardPunishRecord.getHouseId());
            if (rewardPunishRecord.getType() == 0) {
                workIntegral.setBriefed("奖励，积分增加");
                workerDetail.setName("奖罚，资金增加");
                //奖励
                //4.根据每个条例的明细类型（奖或罚）来判断，该工人是否扣除或者增加（只对账户余额和积分进行增减）
                //1积分;2钱;3限制接单;4冻结账号
                //5.对工人原有的基础之上重新set账户余额或者积分并进行更新（update）
                if (rewardPunishCondition.getType() == 1) {
                    bigDecimal2 = member.getEvaluationScore().add(bigDecimal);
                    member.setEvaluationScore(bigDecimal2);
                    //加积分流水
                    workIntegral.setIntegral(bigDecimal);
                    iWorkIntegralMapper.insert(workIntegral);
                }
                if (rewardPunishCondition.getType() == 2) {
                    bigDecimal2 = member.getSurplusMoney().add(bigDecimal);
                    BigDecimal haveMoney = member.getHaveMoney().add(bigDecimal);
                    member.setSurplusMoney(bigDecimal2);
                    member.setHaveMoney(haveMoney);
                    //加流水记录
                    workerDetail.setMoney(bigDecimal);
                    workerDetail.setState(1);
                    iWorkerDetailMapper.insert(workerDetail);
                }

                //罚
            }  if (rewardPunishRecord.getType() == 1) {
                workIntegral.setBriefed("处罚，积分扣除");
                workerDetail.setName("处罚，资金扣除");
                if (rewardPunishCondition.getType() == 1) {
                    bigDecimal2 = member.getEvaluationScore().subtract(bigDecimal);
                    member.setEvaluationScore(bigDecimal2);
                    //加积分流水
                    workIntegral.setIntegral(new BigDecimal("-"+bigDecimal.doubleValue()));
                    iWorkIntegralMapper.insert(workIntegral);
                }
                if (rewardPunishCondition.getType() == 2) {
                    bigDecimal2 = member.getSurplusMoney().subtract(bigDecimal);
                    BigDecimal haveMoney = member.getHaveMoney().subtract(bigDecimal);
                    member.setSurplusMoney(bigDecimal2);
                    member.setHaveMoney(haveMoney);
                    //加流水记录
                    workerDetail.setMoney(new BigDecimal("-"+bigDecimal.doubleValue()));
                    workerDetail.setState(0);
                    iWorkerDetailMapper.insert(workerDetail);
                }
            }
            memberMapper.updateByPrimaryKeySelective(member);
        }
    }

    /**
     * 修改奖罚记录
     * @param userToken
     * @param rewardPunishRecord
     * @return
     */
    public ServerResponse updateRewardPunishRecord(String userToken,String userId,RewardPunishRecord rewardPunishRecord){
        try {
            if(!CommonUtil.isEmpty(userToken)) {
                AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
                Member member = accessToken.getMember();
                rewardPunishRecord.setOperatorId(member.getId());
            }
            if(!CommonUtil.isEmpty(userId)) {
                rewardPunishRecord.setOperatorId(userId);
            }
            rewardPunishRecordMapper.updateByPrimaryKeySelective(rewardPunishRecord);
            return ServerResponse.createBySuccessMessage("修改奖罚记录成功");
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("修改奖罚记录失败");
        }
    }

    /**
     * 根据userToken查询奖罚记录
     * @param userToken
     * @return
     */
    public ServerResponse queryRewardPunishRecord(String userToken,String workerId, PageDTO pageDTO){
        try {

            Example example=new Example(RewardPunishRecord.class);
            Example.Criteria criteria=example.createCriteria();
            if(!CommonUtil.isEmpty(userToken)) {
                AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
                Member member = accessToken.getMember();
                criteria.andEqualTo(RewardPunishRecord.MEMBER_ID,member.getId());
            }
            if(!CommonUtil.isEmpty(workerId)) {
                criteria.andEqualTo(RewardPunishRecord.MEMBER_ID,workerId);
            }
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<RewardPunishRecord> recordList=rewardPunishRecordMapper.selectByExample(example);
            PageInfo pageResult = new PageInfo(recordList);
            if(recordList!=null&&recordList.size()>0){
                List<RewardPunishRecordDTO> recordDTOS=new ArrayList<>();
                for (RewardPunishRecord record:recordList) {
                    RewardPunishRecordDTO rewardPunishRecordDTO=new RewardPunishRecordDTO();
                    rewardPunishRecordDTO.setId(record.getId());
                    recordDTOS.addAll(rewardPunishRecordMapper.queryRewardPunishRecord(rewardPunishRecordDTO));
                }
                pageResult.setList(recordDTOS);
                return ServerResponse.createBySuccess("ok",pageResult);
            }else{
                return ServerResponse.createByErrorCodeMessage(EventStatus.NO_DATA.getCode(),EventStatus.NO_DATA.getDesc());
            }
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("根据userToken查询奖罚记录失败");
        }
    }

    /**
     * 奖罚详情
     * @param recordId
     * @return
     */
    public ServerResponse getRewardPunishRecord(String recordId){
        try {
            RewardPunishRecordDTO example=new RewardPunishRecordDTO();
            example.setId(recordId);
            List<RewardPunishRecordDTO> recordList=rewardPunishRecordMapper.queryRewardPunishRecord(example);
            if(recordList!=null&&recordList.size()>0){
                return ServerResponse.createBySuccess("ok",recordList.get(0));
            }else{
                return ServerResponse.createByErrorCodeMessage(EventStatus.NO_DATA.getCode(),EventStatus.NO_DATA.getDesc());
            }

        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("根据userToken查询奖罚记录失败");
        }
    }
}
