package com.dangjia.acg.service.configRule;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.mapper.configRule.*;
import com.dangjia.acg.mapper.core.IHouseFlowMapper;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.operation.IOperationFlowMapper;
import com.dangjia.acg.mapper.user.UserMapper;
import com.dangjia.acg.model.config.*;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.member.Member;
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.*;

/**
 * author: qiyuxiang
 * Date: 2019-12-11
 */
@Service
public class ConfigRuleUtilService {

    @Autowired
    private IConfigRuleItemLadderMapper configRuleItemLadderMapper;
    @Autowired
    private IConfigRuleItemOneMapper configRuleItemOneMapper;
    @Autowired
    private IConfigRuleItemThreeMapper configRuleItemThreeMapper;
    @Autowired
    private IConfigRuleItemTwoMapper configRuleItemTwoMapper;
    @Autowired
    private IConfigRuleModuleMapper configRuleModuleMapper;
    @Autowired
    private IConfigRuleRankMapper configRuleRankMapper;
    @Autowired
    private IConfigRuleTypeMapper configRuleTypeMapper;
    @Autowired
    private IOperationFlowMapper operationFlowMapper;

    @Autowired
    private IMemberMapper memberMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private IWorkerTypeMapper workerTypeMapper;
    @Autowired
    private ConfigRuleService configRuleService;

    @Autowired
    private IHouseFlowMapper houseFlowMapper;
    @Autowired
    private IHouseMapper houseMapper;

    @Value("${spring.profiles.active}")
    private String active;

    /**
     * 声明日志
     */
    private static Logger logger = LoggerFactory.getLogger(ConfigRuleUtilService.class);

    /**
     * 获取用户等级
     * @return
     */
    public String  getMemberRank(String memberId) {
        try {
            Member member = memberMapper.selectByPrimaryKey(memberId);
            Example example=new Example(DjConfigRuleRank.class);
            example.createCriteria().andCondition(" score_start >= "+member.getEvaluationScore()+"  and  score_end<= "+member.getEvaluationScore());
            List<DjConfigRuleRank> configRuleRanks = configRuleRankMapper.selectByExample(example);
            if(configRuleRanks.size()>0){
                return configRuleRanks.get(0).getName();
            }else{
                return "";
            }
        } catch (Exception e) {
            logger.error("ConfigRuleUtilService:",e);
            return "";
        }
    }
    /**
     * 抢单排队时间
     *
     * @param evaluationScore 积分
     * @return 等待时间
     */
    public Date getCountDownTime(BigDecimal evaluationScore) {
        if (active != null && active.equals("pre")) {
            Example example=new Example(DjConfigRuleRank.class);
            example.createCriteria().andCondition(" score_start >= "+evaluationScore.doubleValue()+"  and  score_end<= "+evaluationScore);
            List<DjConfigRuleRank> configRuleRanks = configRuleRankMapper.selectByExample(example);
            Integer amount=0;
            if(configRuleRanks.size()>0){
                DjConfigRuleRank configRuleRank=configRuleRanks.get(0);
                example=new Example(DjConfigRuleModule.class);
                example.createCriteria().andEqualTo(DjConfigRuleModule.TYPE_ID,ConfigRuleService.MK012);
                DjConfigRuleModule configRuleModule=configRuleModuleMapper.selectOneByExample(example);
                ServerResponse serverResponse=configRuleService.getConfigRuleModule(configRuleModule.getId(),configRuleModule.getTypeId(),null);
                if(serverResponse.isSuccess()){
                    List<Map> returnData = (List<Map>) serverResponse.getResultObj();
                    if(returnData.size()>0){
                        for (Map returnDatum : returnData) {
                            if(configRuleRank.getId().equals(returnDatum.get(DjConfigRuleItemOne.RANK_ID))){
                                amount=(Integer) returnDatum.get("integral");
                                break;
                            }
                        }
                    }
                }
            }else{
                return new Date();
            }
            Calendar now = Calendar.getInstance();
            now.add(Calendar.MINUTE, amount);//当前时间加N分钟
            String dateStr = DateUtil.getDateString(now.getTimeInMillis());
            return DateUtil.toDate(dateStr);
        } else {
            return new Date();
        }
    }

    /**
     * 持单上线(默认一个单)
     *
     * @param evaluationScore 积分
     * @return 等待时间
     */
    public Integer getMethodsCount(String workerTypeId,BigDecimal evaluationScore) {
        if (active != null && active.equals("pre")) {
            Example example=new Example(DjConfigRuleRank.class);
            example.createCriteria().andCondition(" score_start >= "+evaluationScore.doubleValue()+"  and  score_end<= "+evaluationScore);
            List<DjConfigRuleRank> configRuleRanks = configRuleRankMapper.selectByExample(example);
            Integer amount=1;
            if(configRuleRanks.size()>0){
                DjConfigRuleRank configRuleRank=configRuleRanks.get(0);
                example=new Example(DjConfigRuleModule.class);
                example.createCriteria().andEqualTo(DjConfigRuleModule.TYPE_ID,ConfigRuleService.MK011);
                DjConfigRuleModule configRuleModule=configRuleModuleMapper.selectOneByExample(example);
                ServerResponse serverResponse=configRuleService.getConfigRuleModule(configRuleModule.getId(),configRuleModule.getTypeId(),null);
                if(serverResponse.isSuccess()){
                    List<Map> returnData = (List<Map>) serverResponse.getResultObj();
                    if(returnData.size()>0){
                        for (Map returnDatum : returnData) {
                            if(configRuleRank.getId().equals(returnDatum.get(DjConfigRuleItemOne.RANK_ID))&&workerTypeId.equals(returnDatum.get(DjConfigRuleItemOne.TYPE_ID))){
                                amount=(Integer) returnDatum.get("integral");
                                break;
                            }
                        }
                    }
                }
            }
            return amount;
        } else {
            return 1000;
        }
    }

    /**
     * 新手保护单
     *
     * @return 保护数量
     */
    public Integer getProtectMethodsCount() {
        if (active != null && active.equals("pre")) {
            Integer amount=1;
            Example example=new Example(DjConfigRuleModule.class);
            example.createCriteria().andEqualTo(DjConfigRuleModule.TYPE_ID,ConfigRuleService.MK013);
            DjConfigRuleModule configRuleModule=configRuleModuleMapper.selectOneByExample(example);

            example=new Example(DjConfigRuleItemTwo.class);
            example.createCriteria().andEqualTo(DjConfigRuleItemTwo.MODULE_ID,configRuleModule.getId());
            example.orderBy(DjConfigRuleItemTwo.CREATE_DATE).desc();
            PageHelper.startPage(1, 1);
            List<DjConfigRuleItemTwo> configRuleItemTwos=configRuleItemTwoMapper.selectByExample(example);
            if (configRuleItemTwos.size() > 0) {
                for (DjConfigRuleItemTwo configRuleItemTwo : configRuleItemTwos) {
                    amount=Integer.parseInt(configRuleItemTwo.getFieldValue());
                }
            }
            return amount;
        } else {
            return 1;
        }
    }

    /**
     * 抢单限制（天数）
     *
     * @return 天数
     */
    public Integer getGrabLimitDay() {
        if (active != null && active.equals("pre")) {
            Integer amount=1;
            Example example=new Example(DjConfigRuleModule.class);
            example.createCriteria().andEqualTo(DjConfigRuleModule.TYPE_ID,ConfigRuleService.MK018);
            DjConfigRuleModule configRuleModule=configRuleModuleMapper.selectOneByExample(example);

            example=new Example(DjConfigRuleItemTwo.class);
            example.createCriteria().andEqualTo(DjConfigRuleItemTwo.MODULE_ID,configRuleModule.getId());
            example.orderBy(DjConfigRuleItemTwo.CREATE_DATE).desc();
            PageHelper.startPage(1, 1);
            List<DjConfigRuleItemTwo> configRuleItemTwos=configRuleItemTwoMapper.selectByExample(example);
            if (configRuleItemTwos.size() > 0) {
                for (DjConfigRuleItemTwo configRuleItemTwo : configRuleItemTwos) {
                    amount=Integer.parseInt(configRuleItemTwo.getFieldValue());
                }
            }
            return amount;
        } else {
            return 90;
        }
    }


    /**
     * 质保抢单时间配置（小时数）
     *
     * @return 小时数
     */
    public Integer getGuaranteedQualityTime() {
        if (active != null && active.equals("pre")) {
            Integer amount=1;
            Example example=new Example(DjConfigRuleModule.class);
            example.createCriteria().andEqualTo(DjConfigRuleModule.TYPE_ID,ConfigRuleService.MK019);
            DjConfigRuleModule configRuleModule=configRuleModuleMapper.selectOneByExample(example);

            example=new Example(DjConfigRuleItemTwo.class);
            example.createCriteria().andEqualTo(DjConfigRuleItemTwo.MODULE_ID,configRuleModule.getId());
            example.orderBy(DjConfigRuleItemTwo.CREATE_DATE).desc();
            PageHelper.startPage(1, 1);
            List<DjConfigRuleItemTwo> configRuleItemTwos=configRuleItemTwoMapper.selectByExample(example);
            if (configRuleItemTwos.size() > 0) {
                for (DjConfigRuleItemTwo configRuleItemTwo : configRuleItemTwos) {
                    amount=Integer.parseInt(configRuleItemTwo.getFieldValue());
                }
            }
            return amount;
        } else {
            return 90;
        }
    }

    /**
     *  大管家自动派单
     * @param juli 距离
     * @param evaluationScore  积分
     * @param methods 持单量
     * @return
     */
    public Double getautoDistributeHandleConfig(Double juli,Double evaluationScore,Integer methods) {
        Double amount = 0d;
        Example example = new Example(DjConfigRuleModule.class);
        example.createCriteria().andEqualTo(DjConfigRuleModule.TYPE_ID, ConfigRuleService.MK017);
        DjConfigRuleModule configRuleModule = configRuleModuleMapper.selectOneByExample(example);

        Double a=0d;//距离参考分
        Double b=0d;//积分参考分
        Double c=0d;//距离参考分
        example = new Example(DjConfigRuleItemThree.class);
        example.createCriteria().andEqualTo(DjConfigRuleItemThree.MODULE_ID, configRuleModule.getId());
        example.orderBy(DjConfigRuleItemThree.CREATE_DATE).desc();
        PageHelper.startPage(1, 1);
        List<DjConfigRuleItemThree> configRuleItemThrees = configRuleItemThreeMapper.selectByExample(example);
        if (configRuleItemThrees.size() > 0) {
            for (DjConfigRuleItemThree configRuleItemThree : configRuleItemThrees) {
                example = new Example(DjConfigRuleItemLadder.class);
                example.createCriteria().andEqualTo(DjConfigRuleItemLadder.ITEM_THREE_ID, configRuleItemThree.getId());
                List<DjConfigRuleItemLadder> configRuleItemLadders = configRuleItemLadderMapper.selectByExample(example);
                //地域距离
                if(configRuleItemThree.getParamType().equals(ConfigRuleService.CS006)){
                    for (DjConfigRuleItemLadder configRuleItemLadder : configRuleItemLadders) {
                        if(configRuleItemLadder.getPhaseStart()<=juli&&configRuleItemLadder.getPhaseEnd()>=juli){
                            a=configRuleItemLadder.getFraction()*(configRuleItemThree.getParamWeight()/100);
                            break;
                        }
                    }
                    if(a==0){
                        for (DjConfigRuleItemLadder configRuleItemLadder : configRuleItemLadders) {
                            if(configRuleItemLadder.getPhaseStart()>juli){
                                a=configRuleItemLadder.getFraction()*(configRuleItemThree.getParamWeight()/100);
                            }
                            if(configRuleItemLadder.getPhaseEnd()<juli){
                                a=configRuleItemLadder.getFraction()*(configRuleItemThree.getParamWeight()/100);
                            }
                        }
                    }
                }
                //积分
                if(configRuleItemThree.getParamType().equals(ConfigRuleService.CS007)){
                    for (DjConfigRuleItemLadder configRuleItemLadder : configRuleItemLadders) {
                        if(configRuleItemLadder.getPhaseStart()<=evaluationScore&&configRuleItemLadder.getPhaseEnd()>=evaluationScore){
                            b=configRuleItemLadder.getFraction()*(configRuleItemThree.getParamWeight()/100);
                            break;
                        }
                    }
                    if(a==0){
                        for (DjConfigRuleItemLadder configRuleItemLadder : configRuleItemLadders) {
                            if(configRuleItemLadder.getPhaseStart()>evaluationScore){
                                a=configRuleItemLadder.getFraction()*(configRuleItemThree.getParamWeight()/100);
                            }
                            if(configRuleItemLadder.getPhaseEnd()<evaluationScore){
                                a=configRuleItemLadder.getFraction()*(configRuleItemThree.getParamWeight()/100);
                            }
                        }
                    }
                }
                //持单上限
                if(configRuleItemThree.getParamType().equals(ConfigRuleService.CS008)){
                    for (DjConfigRuleItemLadder configRuleItemLadder : configRuleItemLadders) {
                        if(configRuleItemLadder.getPhaseStart()<=methods&&configRuleItemLadder.getPhaseEnd()>=methods){
                            c=configRuleItemLadder.getFraction()*(configRuleItemThree.getParamWeight()/100);
                            break;
                        }
                    }
                    if(a==0){
                        for (DjConfigRuleItemLadder configRuleItemLadder : configRuleItemLadders) {
                            if(configRuleItemLadder.getPhaseStart()>methods){
                                a=configRuleItemLadder.getFraction()*(configRuleItemThree.getParamWeight()/100);
                            }
                            if(configRuleItemLadder.getPhaseEnd()<methods){
                                a=configRuleItemLadder.getFraction()*(configRuleItemThree.getParamWeight()/100);
                            }
                        }
                    }
                }
            }
        }
        amount=a+b+c;
        return amount;
    }


    /**
     * 自动排期配置-工地户型设置
     * @param square 面积
     * @return
     */
    public DjConfigRuleItemTwo getApartmentConfig(BigDecimal square) {
        Example example=new Example(DjConfigRuleModule.class);
        example.createCriteria().andEqualTo(DjConfigRuleModule.TYPE_ID,ConfigRuleService.PQ101);
        DjConfigRuleModule configRuleModule=configRuleModuleMapper.selectOneByExample(example);
        DjConfigRuleItemTwo configRuleItemTwoNew=null;
        example=new Example(DjConfigRuleItemTwo.class);
        example.createCriteria().andEqualTo(DjConfigRuleItemTwo.MODULE_ID,configRuleModule.getId());
        example.orderBy(DjConfigRuleItemTwo.CREATE_DATE).desc();
        PageHelper.startPage(1, 3);
        List<DjConfigRuleItemTwo> configRuleItemTwos=configRuleItemTwoMapper.selectByExample(example);
        if (configRuleItemTwos.size() > 0) {
            for (DjConfigRuleItemTwo configRuleItemTwo : configRuleItemTwos) {
                String[] fieldValues=configRuleItemTwo.getFieldValue().split(",");
                if(Double.parseDouble(fieldValues[0])<=square.doubleValue()&&Double.parseDouble(fieldValues[1])>=square.doubleValue()){
                    configRuleItemTwoNew= configRuleItemTwo;
                    break;
                }
            }
        }
        return configRuleItemTwoNew;
    }

    /**
     * 自动排期配置-其他
     * @param typeId 平均工价(PQ102)/默认人数(PQ103)/工序安装期配置(PQ104)
     */
    public List<Map> getAutoSchedulingConfig(String typeId) {
        Example example=new Example(DjConfigRuleModule.class);
        example.createCriteria().andEqualTo(DjConfigRuleModule.TYPE_ID,typeId);
        DjConfigRuleModule configRuleModule=configRuleModuleMapper.selectOneByExample(example);
        ServerResponse serverResponse=configRuleService.getConfigRuleModule(configRuleModule.getId(),configRuleModule.getTypeId(),null);
        if(serverResponse.isSuccess()){
            List<Map> returnData = (List<Map>) serverResponse.getResultObj();
            return returnData;
        }
        return new ArrayList();
    }

    /**
     * 自动排期
     * @param house
     * @param houseFlows
     */
    public void getAutoSchedulingConfig(Date constructionDate,Boolean isWeekend,House house, List<HouseFlow> houseFlows) {
        if(house.getSquare()==null){
            return;
        }
        Date startDate=null;
        house.setStartDate(constructionDate);
        house.setEndDate(constructionDate);
        DjConfigRuleItemTwo configRuleItemTwo= getApartmentConfig(house.getSquare());
        List<Map> averageLabourPrice = getAutoSchedulingConfig(ConfigRuleService.PQ102);//平均工价
        List<Map> defaultNumber = getAutoSchedulingConfig(ConfigRuleService.PQ103);//默认人数
        List<Map> installationPeriod = getAutoSchedulingConfig(ConfigRuleService.PQ104);//工序安装期配置
        for (HouseFlow houseFlow : houseFlows) {
            BigDecimal gongJia=new BigDecimal(0);
            Integer renShu=0;
            for (Map map : averageLabourPrice) {
                if(houseFlow.getWorkerTypeId().equals(map.get(DjConfigRuleItemOne.RANK_ID))){
                    gongJia=new BigDecimal(map.get(configRuleItemTwo.getFieldCode()).toString());
                }
            }
            for (Map map : defaultNumber) {
                if(houseFlow.getWorkerTypeId().equals(map.get(DjConfigRuleItemOne.RANK_ID))){
                    renShu=Integer.parseInt(map.get("number").toString());
                }
            }
            if(startDate==null){
                startDate=constructionDate;
            }else{
                startDate=DateUtil.addDateDays(startDate,1);
            }
            //工期 = 工序工价 / （户型平均工价（工序）* 默认工人人数）
            Integer dayNum=(houseFlow.getWorkPrice().intValue()/(gongJia.intValue()*renShu))-1;
            houseFlow.setStartDate(startDate);
            houseFlow.setEndDate(DateUtil.addDateDays(startDate,dayNum));
            //如果不包含周末，则加上周末的天数
            if(isWeekend){
                dayNum = dayNum + DateUtil.getWeekendDay(houseFlow.getStartDate(),houseFlow.getEndDate());
                houseFlow.setEndDate(DateUtil.addDateDays(startDate,dayNum));
            }
            houseFlowMapper.updateByPrimaryKeySelective(houseFlow);
            startDate=houseFlow.getEndDate();
        }
        for (HouseFlow houseFlow : houseFlows) {
            for (Map map : installationPeriod) {
                if(houseFlow.getWorkerTypeId().equals(map.get(DjConfigRuleItemOne.RANK_ID))){
                    Integer day=Integer.parseInt(map.get("number").toString());
                    startDate=DateUtil.addDateDays(startDate,day);
                }
            }
        }
        house.setEndDate(startDate);
        house.setSchedule("1");
        houseMapper.updateByPrimaryKeySelective(house);
    }

}
