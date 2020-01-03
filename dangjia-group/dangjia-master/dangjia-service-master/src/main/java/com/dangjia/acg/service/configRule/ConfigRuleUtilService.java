package com.dangjia.acg.service.configRule;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.mapper.configRule.*;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.operation.IOperationFlowMapper;
import com.dangjia.acg.mapper.user.UserMapper;
import com.dangjia.acg.model.config.DjConfigRuleItemOne;
import com.dangjia.acg.model.config.DjConfigRuleItemTwo;
import com.dangjia.acg.model.config.DjConfigRuleModule;
import com.dangjia.acg.model.config.DjConfigRuleRank;
import com.dangjia.acg.modle.member.Member;
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
}
