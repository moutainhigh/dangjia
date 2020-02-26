package com.dangjia.acg.service.worker;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.enums.AppType;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.worker.*;
import com.dangjia.acg.mapper.complain.IComplainMapper;
import com.dangjia.acg.mapper.core.IHouseWorkerMapper;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.user.UserMapper;
import com.dangjia.acg.mapper.worker.*;
import com.dangjia.acg.modle.complain.Complain;
import com.dangjia.acg.modle.core.HouseWorker;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.user.MainUser;
import com.dangjia.acg.modle.worker.*;
import com.dangjia.acg.service.config.ConfigMessageService;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.dangjia.acg.service.supervisor.PatrolRecordServices;
import com.dangjia.acg.util.StringTool;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 奖罚管理
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
    private CraftsmanConstructionService constructionService;
    @Autowired
    private ConfigMessageService configMessageService;
    @Autowired
    private IHouseMapper houseMapper;

    @Autowired
    private IComplainMapper complainMapper;
    @Autowired
    private IWorkIntegralMapper iWorkIntegralMapper;
    @Autowired
    private IWorkerDetailMapper iWorkerDetailMapper;

    @Autowired
    private IHouseWorkerMapper houseWorkerMapper;
    @Autowired
    private IMemberMapper memberMapper;

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private IWorkerTypeMapper workerTypeMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private PatrolRecordServices patrolRecordServices;

    /**
     * 保存奖罚条件及条件明细
     *
     * @return
     */
    public ServerResponse addRewardPunishCorrelation(String id, String name, String content, Integer type, Integer state, String conditionArr, BigDecimal quantity) {
        try {
            RewardPunishCorrelation rewardPunishCorrelation = new RewardPunishCorrelation();
            rewardPunishCorrelation.setName(name);
            rewardPunishCorrelation.setContent(content);
            rewardPunishCorrelation.setType(type);//0:奖励;1:处罚
            rewardPunishCorrelation.setState(state);
            if (id != null && !"".equals(id)) {
                rewardPunishCorrelation.setId(id);
                rewardPunishCorrelationMapper.updateByPrimaryKeySelective(rewardPunishCorrelation);//修改奖罚条件
                Example example = new Example(RewardPunishCondition.class);
                example.createCriteria().andEqualTo("rewardPunishCorrelationId", id);
                rewardPunishConditionMapper.deleteByExample(example);//删除关联条件明细
            } else {
                rewardPunishCorrelationMapper.insertSelective(rewardPunishCorrelation);//保存奖罚条件
            }
            JSONArray jsonArray = JSONArray.parseArray(conditionArr);
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                RewardPunishCondition rewardPunishCondition = new RewardPunishCondition();
                rewardPunishCondition.setRewardPunishCorrelationId(rewardPunishCorrelation.getId());
                String conditionName = "";
                if (type == 0) {//奖励
                    conditionName = "奖励" + object.getString("quantity") + object.getString("units");
                } else {//处罚
                    if (object.getInteger("type") == 3) {
                        conditionName = "处罚" + object.getString("quantity") + object.getString("units") + "不能接单";
                    } else if (object.getInteger("type") == 4) {
                        conditionName = "处罚" + object.getString("quantity") + object.getString("units") + "不能提现";
                    } else {
                        conditionName = "扣除" + object.getString("quantity") + object.getString("units");
                    }
                }
                int types = Integer.parseInt(object.getString("type"));
                rewardPunishCondition.setName(conditionName);
                rewardPunishCondition.setType(types);
                rewardPunishCondition.setQuantity(object.getBigDecimal("quantity"));
                rewardPunishCondition.setUnits(object.getString("units"));
//                if (types == 4) {
//                    String startTime = object.getString("startTime");
//                    String endTime = object.getString("endTime");
//                    rewardPunishCondition.setStartTime(DateUtil.toDate(startTime));
//                    rewardPunishCondition.setEndTime(DateUtil.toDate(endTime));
//                    rewardPunishCondition.setQuantity(quantity);
//                }
                rewardPunishConditionMapper.insertSelective(rewardPunishCondition);//保存奖罚条件明细
            }
            return ServerResponse.createBySuccessMessage("保存奖罚条件成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("保存奖罚条件失败");
        }
    }

    /**
     * 删除奖罚条件及条件明细
     *
     * @return
     */
    public ServerResponse deleteRewardPunishCorrelation(String id) {
        try {
            if (id != null && !"".equals(id)) {
                rewardPunishCorrelationMapper.deleteByPrimaryKey(id);
                Example example = new Example(RewardPunishCondition.class);
                example.createCriteria().andEqualTo("rewardPunishCorrelationId", id);
                rewardPunishConditionMapper.deleteByExample(example);//删除关联条件明细
            } else {
                return ServerResponse.createByErrorMessage("删除奖罚条件失败");
            }
            return ServerResponse.createBySuccessMessage("删除奖罚条件成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("删除奖罚条件失败");
        }
    }

    /**
     * 查询所有奖罚条件及条件明细
     *
     * @return
     */
    public ServerResponse queryCorrelation(String name, Integer type) {
        try {
            List<RewardPunishCorrelationDTO> correlationList = rewardPunishCorrelationMapper.queryCorrelation(name, type);
            List<Map<String, Object>> listMap = new ArrayList<>();
            for (RewardPunishCorrelationDTO correlation : correlationList) {
                Map<String, Object> correlationMap = BeanUtils.beanToMap(correlation);
                StringBuilder conditionArr = new StringBuilder();
                List<RewardPunishCondition> conditionList = correlation.getConditionList();
                for (int i = 0; i < conditionList.size(); i++) {
                    RewardPunishCondition condition = conditionList.get(i);
                    if (conditionList.size() - 1 == i) {
                        conditionArr.append(condition.getName());
                    } else {
                        conditionArr.append(condition.getName()).append(",");
                    }
                }
                correlationMap.put("conditionArr", conditionArr.toString());
                listMap.add(correlationMap);
            }
            return ServerResponse.createBySuccess("查询奖罚条件成功", listMap);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询奖罚条件失败");
        }
    }

    /**
     * 根据id查询奖罚条件及明细
     *
     * @param id
     * @return
     */
    public ServerResponse queryCorrelationById(String id) {
        try {
            RewardPunishCorrelation correlation = rewardPunishCorrelationMapper.selectByPrimaryKey(id);
            Map<String, Object> correlationMap = new HashMap<>();
            if (correlation != null) {
                correlationMap = BeanUtils.beanToMap(correlation);
                correlationMap.put("createDate", correlation.getCreateDate().getTime());
                Example example = new Example(RewardPunishCondition.class);
                example.createCriteria().andEqualTo("rewardPunishCorrelationId", correlation.getId());
                List<RewardPunishCondition> conditionList = rewardPunishConditionMapper.selectByExample(example);
                List<Map<String, Object>> conditionArr = new ArrayList<>();
                for (RewardPunishCondition condition : conditionList) {
                    Map<String, Object> conditionMap = new HashMap<>();
                    conditionMap.put("conditionId", condition.getId());
                    conditionMap.put("rewardPunishCorrelationId", condition.getRewardPunishCorrelationId());
                    conditionMap.put("conditionName", condition.getName());
                    conditionMap.put("conditionType", condition.getType());
                    conditionMap.put("conditionQuantity", condition.getQuantity());
                    conditionMap.put("conditionUnits", condition.getUnits());
                    if (condition.getStartTime() != null) {
                        conditionMap.put("startTime", condition.getStartTime().getTime());
                    }
                    if (condition.getEndTime() != null) {
                        conditionMap.put("endTime", condition.getEndTime().getTime());
                    }
                    conditionMap.put("conditionCreateDate", condition.getCreateDate().getTime());
                    conditionArr.add(conditionMap);
                }
                correlationMap.put("conditionArr", conditionArr);
            }
            return ServerResponse.createBySuccess("查询奖罚条件成功", correlationMap);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询奖罚条件失败");
        }
    }

    /**
     * 添加奖罚记录
     *
     * @param userToken
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse addRewardPunishRecord(String userToken, String userId, RewardPunishRecord rewardPunishRecord) {
        try {
            if (!CommonUtil.isEmpty(userToken)) {
                Object object = constructionService.getMember(userToken);
                if (object instanceof ServerResponse) {
                    return (ServerResponse) object;
                }
                Member member = (Member) object;
                rewardPunishRecord.setOperatorId(member.getId());
            }
            if (!CommonUtil.isEmpty(userId)) {
                rewardPunishRecord.setOperatorId(userId);
            }
            if (!CommonUtil.isEmpty(rewardPunishRecord.getRewardPunishCorrelationId())) {
                RewardPunishCorrelation rewardPunishCorrelation = rewardPunishCorrelationMapper.selectByPrimaryKey(rewardPunishRecord.getRewardPunishCorrelationId());
                rewardPunishRecord.setType(rewardPunishCorrelation.getType());
            }
            rewardPunishRecord.setState(0);//0:启用;1:不启用
            rewardPunishRecordMapper.insert(rewardPunishRecord);
            //工人ID账户奖罚积分和金额变更
            updateWorkerInfo(rewardPunishRecord.getId());
            if (!CommonUtil.isEmpty(rewardPunishRecord.getHouseId()) && rewardPunishRecord.getHouseId() != null && rewardPunishRecord.getMemberId() != null) {
                House house = houseMapper.selectByPrimaryKey(rewardPunishRecord.getHouseId());
                configMessageService.addConfigMessage(null, AppType.GONGJIANG, rewardPunishRecord.getMemberId(),
                        "0", "奖罚提醒", String.format(DjConstants.PushMessage.RECORD_OF_REWARDS_AND_PENALTIES, house.getHouseName()), "7");
            }
            //添加记录到督导工作记录中
            patrolRecordServices.addPatrolRecord(rewardPunishRecord.getOperatorId(),
                    rewardPunishRecord.getHouseId(), rewardPunishRecord.getRemarks(),
                    rewardPunishRecord.getImages(), rewardPunishRecord.getType(),
                    rewardPunishRecord.getId());
            return ServerResponse.createBySuccessMessage("新增奖罚记录成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("新增奖罚记录失败");
        }
    }

    public void updateWorkerInfo(String rewardPunishRecordId) {
        RewardPunishRecord rewardPunishRecord = rewardPunishRecordMapper.selectByPrimaryKey(rewardPunishRecordId);
        //1.获取被罚的工人id
        //2.获取奖罚条例的类型来判断是奖励还是处罚
        //3.获取条例的明细列表
        if (rewardPunishRecord.getType() == null) {
            return;
        }
        Member member = memberMapper.selectByPrimaryKey(rewardPunishRecord.getMemberId());
        Example example = new Example(RewardPunishCondition.class);
        example.createCriteria().andEqualTo(RewardPunishCondition.REWARD_PUNISH_CORRELATION_ID, rewardPunishRecord.getRewardPunishCorrelationId());
        List<RewardPunishCondition> rewardPunishConditionList = rewardPunishConditionMapper.selectByExample(example);
        for (RewardPunishCondition rewardPunishCondition : rewardPunishConditionList) {
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
                //1积分;2钱;3限制接单;
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

                    workerDetail.setWalletMoney(bigDecimal2);
                    workerDetail.setHaveMoney(haveMoney);
                    //加流水记录
                    workerDetail.setMoney(bigDecimal);
                    workerDetail.setState(12);
                    iWorkerDetailMapper.insert(workerDetail);
                }

                //罚
            } else if (rewardPunishRecord.getType() == 1) {
                workIntegral.setBriefed("处罚，积分扣除");
                workerDetail.setName("处罚，资金扣除");
                if (rewardPunishCondition.getType() == 1) {
                    bigDecimal2 = member.getEvaluationScore().subtract(bigDecimal);
                    member.setEvaluationScore(bigDecimal2);
                    //加积分流水
                    workIntegral.setIntegral(new BigDecimal("-" + bigDecimal.doubleValue()));
                    iWorkIntegralMapper.insert(workIntegral);
                }
                if (rewardPunishCondition.getType() == 2) {
                    bigDecimal2 = member.getSurplusMoney().subtract(bigDecimal);
                    BigDecimal haveMoney = member.getHaveMoney().subtract(bigDecimal);
                    member.setSurplusMoney(bigDecimal2);
                    member.setHaveMoney(haveMoney);

                    workerDetail.setWalletMoney(bigDecimal2);
                    workerDetail.setHaveMoney(haveMoney);
                    //加流水记录
                    workerDetail.setMoney(bigDecimal);
                    workerDetail.setState(13);
                    iWorkerDetailMapper.insert(workerDetail);
                }
            }
            memberMapper.updateByPrimaryKeySelective(member);
        }
    }


    public ServerResponse getMyRewardPunishRecord(String userToken,String houseId, PageDTO pageDTO) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member member = (Member) object;
        return queryRewardPunishRecord(null,member.getId(),houseId,pageDTO);
    }
    /**
     * 根据userToken查询奖罚记录
     *
     * @param userToken
     * @return
     */
    public ServerResponse queryRewardPunishRecord(String userToken, String workerId,String houseId, PageDTO pageDTO) {
        try {

            Example example = new Example(RewardPunishRecord.class);
            Example.Criteria criteria = example.createCriteria();
            if (!CommonUtil.isEmpty(userToken)) {
                Object object = constructionService.getMember(userToken);
                if (object instanceof ServerResponse) {
                    return (ServerResponse) object;
                }
                Member member = (Member) object;
                criteria.andEqualTo(RewardPunishRecord.OPERATOR_ID, member.getId());
            }
            if (!CommonUtil.isEmpty(workerId)) {
                criteria.andEqualTo(RewardPunishRecord.MEMBER_ID, workerId);
            }
            if (!CommonUtil.isEmpty(houseId)) {
                criteria.andEqualTo(RewardPunishRecord.HOUSE_ID, houseId);
            }
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            example.orderBy(RewardPunishRecord.CREATE_DATE).desc();
            List<RewardPunishRecord> recordList = rewardPunishRecordMapper.selectByExample(example);
            PageInfo pageResult = new PageInfo(recordList);
            if (recordList != null && recordList.size() > 0) {
                List<RewardPunishRecordDTO> recordDTOS = new ArrayList<>();
                for (RewardPunishRecord record : recordList) {
                    RewardPunishRecordDTO rewardPunishRecordDTO = new RewardPunishRecordDTO();
                    rewardPunishRecordDTO.setId(record.getId());
                    recordDTOS.addAll(rewardPunishRecordMapper.queryRewardPunishRecord(rewardPunishRecordDTO));
                }
                for (RewardPunishRecordDTO recordDTO : recordDTOS) {
                    Member member=memberMapper.selectByPrimaryKey(recordDTO.getOperatorId());
                    if(member!=null){
                        recordDTO.setOperatorName(CommonUtil.isEmpty(member.getName())?member.getNickName():member.getName());
                        recordDTO.setOperatorTypeName(workerTypeMapper.getName(member.getWorkerType()));
                    }else{
                        MainUser user=userMapper.selectByPrimaryKey(recordDTO.getOperatorId());
                        recordDTO.setOperatorName(CommonUtil.isEmpty(user.getUsername())?user.getMobile():user.getUsername());
                    }
                }
                pageResult.setList(recordDTOS);
                return ServerResponse.createBySuccess("ok", pageResult);
            } else {
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("根据userToken查询奖罚记录失败");
        }
    }

    /**
     * 奖罚详情
     *
     * @param recordId
     * @return
     */
    public ServerResponse getRewardPunishRecord(String recordId) {
        try {

            String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            RewardPunishRecordDTO example = new RewardPunishRecordDTO();
            example.setId(recordId);
            List<RewardPunishRecordDTO> recordList = rewardPunishRecordMapper.queryRewardPunishRecord(example);
            if (recordList != null && recordList.size() > 0) {
                RewardPunishRecordDTO rewardPunishRecordDTO=recordList.get(0);
                if(!CommonUtil.isEmpty(rewardPunishRecordDTO.getImages())){
                    rewardPunishRecordDTO.setImages(imageAddress+rewardPunishRecordDTO.getImages());
                }
                Member member=memberMapper.selectByPrimaryKey(rewardPunishRecordDTO.getOperatorId());
                if(member!=null){
                    rewardPunishRecordDTO.setOperatorName(CommonUtil.isEmpty(member.getName())?member.getNickName():member.getName());
                    rewardPunishRecordDTO.setOperatorTypeName(workerTypeMapper.getName(member.getWorkerType()));
                }else{
                    MainUser user=userMapper.selectByPrimaryKey(rewardPunishRecordDTO.getOperatorId());
                    rewardPunishRecordDTO.setOperatorName(CommonUtil.isEmpty(user.getUsername())?user.getMobile():user.getUsername());
                    rewardPunishRecordDTO.setOperatorTypeName("");
                }
                rewardPunishRecordDTO.setIsComplain(-1);//未投诉
                Example examples = new Example(Complain.class);
                examples.createCriteria().andEqualTo(Complain.BUSINESS_ID,recordId)
                            .andEqualTo(Complain.BUSINESS_ID, recordId)
                            .andEqualTo(Complain.DATA_STATUS, 0);
                List<Complain> complains = complainMapper.selectByExample(examples);
                if(complains.size()>0){
                    rewardPunishRecordDTO.setIsComplain(complains.get(0).getStatus());//-1：未投诉 0:待处理。1.驳回。2.接受
                    rewardPunishRecordDTO.setComplainId(complains.get(0).getId());
                }
                return ServerResponse.createBySuccess("ok", rewardPunishRecordDTO);
            } else {
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("根据userToken查询奖罚记录失败");
        }
    }



    /**
     * 奖罚-选择工匠列表
     *
     * @param houseId
     * @return
     */
    public ServerResponse queryCraftsmenList( String houseId) {
        try {
            //获取图片url
            String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            List<CraftsmenListDTO> listMap = new ArrayList<>();//返回工匠list
            //管家和工匠都能联系精算和设计
            Example example = new Example(HouseWorker.class);
            example.createCriteria()
                    .andEqualTo(HouseWorker.HOUSE_ID, houseId)
                    .andCondition(" work_type in(6,8) ")
                    .andCondition(" worker_type > 2 ");
            List<HouseWorker> houseWorkerList = houseWorkerMapper.selectByExample(example);
            for (HouseWorker houseWorker : houseWorkerList) {
                CraftsmenListDTO map = new CraftsmenListDTO();
                Member worker2 = memberMapper.selectByPrimaryKey(houseWorker.getWorkerId());
                if (worker2 == null) {
                    continue;
                }
                WorkerType workerType = workerTypeMapper.selectByPrimaryKey(worker2.getWorkerTypeId());
                map.setWorkerTypeName(workerType.getName());
                map.setWorkerTypeColor(workerType.getColor());
                map.setName(worker2.getName());
                map.setMobile(worker2.getMobile());
                map.setHead(imageAddress+worker2.getHead());
                map.setWorkerId( worker2.getId());
                listMap.add(map);
            }
            return ServerResponse.createBySuccess("查询成功", listMap);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("奖罚-选择工匠列表异常");
        }
    }
}
