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
import com.dangjia.acg.mapper.worker.IRewardPunishConditionMapper;
import com.dangjia.acg.mapper.worker.IRewardPunishCorrelationMapper;
import com.dangjia.acg.mapper.worker.IRewardPunishRecordMapper;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.worker.RewardPunishCondition;
import com.dangjia.acg.modle.worker.RewardPunishCorrelation;
import com.dangjia.acg.modle.worker.RewardPunishRecord;
import com.dangjia.acg.service.config.ConfigMessageService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

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
            rewardPunishRecord.setState(0);//0:启用;1:不启用
            rewardPunishRecordMapper.insertSelective(rewardPunishRecord);

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
