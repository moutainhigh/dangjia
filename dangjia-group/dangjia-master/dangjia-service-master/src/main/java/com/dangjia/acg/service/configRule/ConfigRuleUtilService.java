package com.dangjia.acg.service.configRule;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.mapper.configRule.*;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.operation.IOperationFlowMapper;
import com.dangjia.acg.mapper.user.UserMapper;
import com.dangjia.acg.model.config.DjConfigRuleRank;
import com.dangjia.acg.modle.member.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

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

}
