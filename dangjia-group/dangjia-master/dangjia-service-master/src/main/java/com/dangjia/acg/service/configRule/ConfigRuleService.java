package com.dangjia.acg.service.configRule;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.mapper.configRule.*;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.operation.IOperationFlowMapper;
import com.dangjia.acg.mapper.user.UserMapper;
import com.dangjia.acg.model.config.*;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.operation.OperationFlow;
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
public class ConfigRuleService {

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
    private UserMapper userMapper;
    @Autowired
    private IWorkerTypeMapper workerTypeMapper;
    /**
     * 声明日志
     */
    private static Logger logger = LoggerFactory.getLogger(ConfigRuleService.class);

    /**
     * 等级明细列表
     * @return
     */
    public ServerResponse searchConfigRuleRank() {
        try {
            Example example=new Example(DjConfigRuleRank.class);
            List<DjConfigRuleRank> configRuleRanks = configRuleRankMapper.selectByExample(example);
            return ServerResponse.createBySuccess("查询成功", configRuleRanks);
        } catch (Exception e) {
            logger.error("searchConfigRuleRank:",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 更新等级明细列表
     * @return
     */
    public ServerResponse editConfigRuleRank(String rankIds,String scoreStarts,String scoreEnds) {
        try {
            if(!CommonUtil.isEmpty(rankIds)){
                String[] rankIdlist=rankIds.split(",");
                String[] scoreStartlist=scoreStarts.split(",");
                String[] scoreEndslist=scoreEnds.split(",");
                DjConfigRuleRank configRuleRank=new DjConfigRuleRank();
                configRuleRank.setId(null);
                configRuleRank.setCreateDate(null);
                for (int i = 0; i < rankIdlist.length; i++) {
                    configRuleRank.setModifyDate(new Date());
                    configRuleRank.setScoreStart(Double.parseDouble(scoreStartlist[i]));
                    configRuleRank.setScoreEnd(Double.parseDouble(scoreEndslist[i]));
                    Example example=new Example(DjConfigRuleRank.class);
                    example.createCriteria().andEqualTo(DjConfigRuleRank.ID,rankIdlist[i]);
                    configRuleRankMapper.updateByExampleSelective(configRuleRank,example);
                }
            }
            return ServerResponse.createBySuccess("更新成功");
        } catch (Exception e) {
            logger.error("editConfigRuleRank:",e);
            return ServerResponse.createByErrorMessage("更新失败");
        }
    }

    /**
     * 查询模块配置列表
     * @param  type 规则模块类型： 1=积分规则 2=拿钱规则  3=抢单规则 4=其他规则
     * @return
     */
    public ServerResponse searchConfigRuleModule(String type) {
        try {
            Example example=new Example(DjConfigRuleModule.class);
            example.createCriteria().andEqualTo(DjConfigRuleModule.TYPE,type);
            List<DjConfigRuleModule> configRuleRanks = configRuleModuleMapper.selectByExample(example);
            for (DjConfigRuleModule configRuleModule : configRuleRanks) {
                //获取明细头部分类
                if(configRuleModule.getItemType()==1){
                    List<Map> types=new ArrayList<>();
                    String[] paramtype = new String[0];
                    //施工流程分类获取
                    if(MK003.equals(configRuleModule.getTypeId())||MK004.equals(configRuleModule.getTypeId())) {
                        if (MK003.equals(configRuleModule.getTypeId())) {
                            paramtype = new String[]{SG001, SG002, SG003, SG004};
                        }
                        if (MK004.equals(configRuleModule.getTypeId())) {
                            paramtype = new String[]{SG005, SG006};
                        }
                        example = new Example(DjConfigRuleType.class);
                        example.createCriteria().andEqualTo(DjConfigRuleType.SOURCE, 2).andIn(DjConfigRuleType.ID, Arrays.asList(paramtype));
                        List<DjConfigRuleType> configRuleTypes = configRuleTypeMapper.selectByExample(example);
                        for (DjConfigRuleType configRuleType : configRuleTypes) {
                            Map map =new HashMap();
                            map.put(DjConfigRuleModule.TYPE_NAME,configRuleType.getName());
                            map.put(DjConfigRuleModule.TYPE_ID,configRuleType.getId());
                            types.add(map);
                        }
                    }
                    //工种分类获取
                    if(MK008.equals(configRuleModule.getTypeId())||MK009.equals(configRuleModule.getTypeId())) {
                        example = new Example(WorkerType.class);
                        if(MK008.equals(configRuleModule.getTypeId())){//工匠拿钱规则
                            example.createCriteria().andGreaterThan(WorkerType.TYPE ,3);
                        }
                        if(MK009.equals(configRuleModule.getTypeId())){//滞留金上限
                            example.createCriteria().andGreaterThan(WorkerType.TYPE ,2);
                        }
                        List<WorkerType> workerTypeList = workerTypeMapper.selectByExample(example);
                        for (WorkerType configRuleType : workerTypeList) {
                            Map map =new HashMap();
                            map.put(DjConfigRuleModule.TYPE_NAME,configRuleType.getName());
                            map.put(DjConfigRuleModule.TYPE_ID,configRuleType.getId());
                            types.add(map);
                        }
                    }
                    configRuleModule.setTypes(types);
                }
            }
            return ServerResponse.createBySuccess("查询成功", configRuleRanks);
        } catch (Exception e) {
            logger.error("searchConfigRuleModule:",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 规则配置明细信息
     * @param moduleId 规则模版ID
     * @param typeId 明细类别,有分类时必传，无分类时可不穿
     * @return
     */
    public ServerResponse getConfigRuleModule(String moduleId,String typeId,String batchCode) {
        try {
            DjConfigRuleModule configRuleModule=configRuleModuleMapper.selectByPrimaryKey(moduleId);
            if(configRuleModule==null){
                return ServerResponse.createByErrorMessage("获取配置错误，请检查初始化参数！");
            }
            if(CommonUtil.isEmpty(batchCode)) {
                batchCode = CommonUtil.getUniqueId();
            }
            Map<String,String> field= setConfigRuleItemField(configRuleModule,typeId);
            if(configRuleModule.getItemType()==1){
                List<DjConfigRuleItemOne> ruleItemOneData =new ArrayList<>();
                Example example=new Example(DjConfigRuleRank.class);
                List<DjConfigRuleRank> configRuleRanks = configRuleRankMapper.selectByExample(example);
                if(field.isEmpty()){
                    return ServerResponse.createByErrorMessage("获取配置错误，配置参数字段错误！");
                }
                for (String key : field.keySet()) {
                    ruleItemOneData.addAll(configRuleItemOneMapper.getRuleItemOneData(moduleId,typeId,batchCode,field.get(key),key,configRuleRanks.size()));
                }
                if (ruleItemOneData.size() <= 0) {
                    return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
                }
                List<Map> returnData=new ArrayList<>();
                for (DjConfigRuleRank configRuleRank : configRuleRanks) {
                    Map map =new HashMap();
                    map.put(DjConfigRuleItemOne.RANK_ID,configRuleRank.getId());
                    map.put(DjConfigRuleItemOne.RANK_NAME,configRuleRank.getName());
                    map.put(DjConfigRuleItemOne.TYPE_ID,typeId);
                    map.put(DjConfigRuleItemOne.BATCH_CODE,batchCode);
                    map.put(DjConfigRuleItemOne.MODULE_ID,moduleId);
                    for (DjConfigRuleItemOne ruleItemOneDatum : ruleItemOneData) {
                        if(ruleItemOneDatum.getRankId().equals(configRuleRank.getId())){
                            map.put(ruleItemOneDatum.getRuleFieldCode(),ruleItemOneDatum.getRuleFieldValue());
                        }
                    }
                    returnData.add(map);
                }
                return ServerResponse.createBySuccess("查询成功",returnData);
            }
            if(configRuleModule.getItemType()==2){
                Example example=new Example(DjConfigRuleItemTwo.class);
                example.createCriteria().andEqualTo(DjConfigRuleItemTwo.MODULE_ID,moduleId);
                example.orderBy(DjConfigRuleItemTwo.CREATE_DATE).desc();
                PageHelper.startPage(field.size(), 1);
                List<DjConfigRuleItemTwo> configRuleItemTwos=configRuleItemTwoMapper.selectByExample(example);
                if (configRuleItemTwos.size() > 0) {
                    return ServerResponse.createBySuccess("查询成功",configRuleItemTwos);
                }

                configRuleItemTwos=new ArrayList<>();
                for (String key : field.keySet()) {
                    DjConfigRuleItemTwo configRuleItemTwo=new DjConfigRuleItemTwo();
                    configRuleItemTwo.setBatchCode(batchCode);
                    configRuleItemTwo.setFieldCode(key);
                    configRuleItemTwo.setFieldName(field.get(key));
                    configRuleItemTwo.setFieldValue("0");
                    configRuleItemTwo.setModuleId(moduleId);
                    configRuleItemTwos.add(configRuleItemTwo);
                }
                return ServerResponse.createBySuccess("查询成功",configRuleItemTwos);
            }
            if(configRuleModule.getItemType()==3){
                String[] paramtype = new String[0];
                if(MK015.equals(configRuleModule.getTypeId())) {
                    paramtype = new String[]{CS001, CS002, CS003, CS004};
                }
                if(MK016.equals(configRuleModule.getTypeId())) {
                    paramtype = new String[]{CS001, CS002, CS003, CS004, CS005};
                }
                if(MK017.equals(configRuleModule.getTypeId())) {
                    paramtype = new String[]{CS006, CS007, CS008};
                }
                Example example=new Example(DjConfigRuleItemThree.class);
                example.createCriteria().andEqualTo(DjConfigRuleItemTwo.MODULE_ID,moduleId);
                example.orderBy(DjConfigRuleItemThree.CREATE_DATE).desc();
                PageHelper.startPage(paramtype.length, 1);
                List<DjConfigRuleItemThree> configRuleItemThrees=configRuleItemThreeMapper.selectByExample(example);
                if (configRuleItemThrees.size() ==0) {

                    example=new Example(DjConfigRuleType.class);
                    example.createCriteria().andEqualTo(DjConfigRuleType.SOURCE,2).andIn(DjConfigRuleType.ID,Arrays.asList(paramtype));
                    List<DjConfigRuleType> configRuleTypes= configRuleTypeMapper.selectByExample(example);
                    configRuleItemThrees=new ArrayList<>();
                    for (DjConfigRuleType configRuleType : configRuleTypes) {
                        DjConfigRuleItemThree configRuleItemThree=new DjConfigRuleItemThree();
                        configRuleItemThree.setBatchCode(batchCode);
                        configRuleItemThree.setParamType(configRuleType.getId());
                        configRuleItemThree.setParamWeight(0d);
                        configRuleItemThree.setParamName(configRuleType.getName());
                        configRuleItemThree.setModuleId(moduleId);
                        configRuleItemThrees.add(configRuleItemThree);
                    }
                }
                for (DjConfigRuleItemThree configRuleItemThree : configRuleItemThrees) {
                    example=new Example(DjConfigRuleItemLadder.class);
                    example.createCriteria().andEqualTo(DjConfigRuleItemLadder.ITEM_THREE_ID,configRuleItemThree.getId());
                    List<DjConfigRuleItemLadder> configRuleItemLadders = configRuleItemLadderMapper.selectByExample(example);
                    configRuleItemThree.setConfigRuleItemLadders(configRuleItemLadders);
                }
                return ServerResponse.createBySuccess("查询成功",configRuleItemThrees);
            }
            return ServerResponse.createBySuccess("查询成功");
        } catch (Exception e) {
            logger.error("searchConfigRuleModule:",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 新增规则配置明细
     * @return
     */
    public ServerResponse setConfigRuleItem(HttpServletRequest request, String moduleId, String itemDataJson) {
        try {
            DjConfigRuleModule configRuleModule=configRuleModuleMapper.selectByPrimaryKey(moduleId);
            JSONArray productArray= JSON.parseArray(itemDataJson);
            if(productArray.size()==0){
                return ServerResponse.createByErrorMessage("参数错误");
            }
            String batchCode="";
            if(configRuleModule.getItemType()==1){
                List<Map> returnData=productArray.toJavaList(Map.class);
                Example example=new Example(DjConfigRuleRank.class);
                List<DjConfigRuleRank> configRuleRanks = configRuleRankMapper.selectByExample(example);
                for (DjConfigRuleRank configRuleRank : configRuleRanks) {
                    for (Map<String,String> field : returnData) {
                        if (configRuleRank.getId().equals(field.get(DjConfigRuleItemOne.RANK_ID))) {
                            Map<String, String> fieldName = setConfigRuleItemField(configRuleModule, field.get(DjConfigRuleItemOne.TYPE_ID));
                            for (String key : field.keySet()) {
                                if (!CommonUtil.isEmpty(fieldName.get(key))) {
                                    DjConfigRuleItemOne configRuleItemOne = new DjConfigRuleItemOne();
                                    configRuleItemOne.setBatchCode(field.get(DjConfigRuleItemOne.BATCH_CODE));
                                    configRuleItemOne.setModuleId(CommonUtil.isEmpty(moduleId) ? field.get(DjConfigRuleItemOne.MODULE_ID) : moduleId);
                                    configRuleItemOne.setRankId(configRuleRank.getId());
                                    configRuleItemOne.setTypeId(field.get(DjConfigRuleItemOne.TYPE_ID));
                                    configRuleItemOne.setRuleFieldCode(key);
                                    configRuleItemOne.setRuleFieldName(fieldName.get(key));
                                    configRuleItemOne.setRuleFieldValue(field.get(key));
                                    configRuleItemOneMapper.insert(configRuleItemOne);
                                    batchCode=configRuleItemOne.getBatchCode();
                                }
                            }
                        }
                    }
                }
            }
            if(configRuleModule.getItemType()==2){
                List<Map> returnData=productArray.toJavaList(Map.class);
                for (Map<String,String> field : returnData) {
                    DjConfigRuleItemTwo configRuleItemTwo = new DjConfigRuleItemTwo();
                    configRuleItemTwo.setBatchCode(field.get(DjConfigRuleItemTwo.BATCH_CODE));
                    configRuleItemTwo.setFieldCode(field.get(DjConfigRuleItemTwo.FIELD_CODE));
                    configRuleItemTwo.setFieldName(field.get(DjConfigRuleItemTwo.FIELD_NAME));
                    configRuleItemTwo.setFieldValue(field.get(DjConfigRuleItemTwo.FIELD_VALUE));
                    configRuleItemTwo.setModuleId(CommonUtil.isEmpty(moduleId) ? field.get(DjConfigRuleItemTwo.MODULE_ID) : moduleId);
                    configRuleItemTwoMapper.insert(configRuleItemTwo);
                    batchCode=configRuleItemTwo.getBatchCode();
                }
            }
            if(configRuleModule.getItemType()==3){
                List<DjConfigRuleItemThree> returnData=productArray.toJavaList(DjConfigRuleItemThree.class);
                for (DjConfigRuleItemThree field : returnData) {
                    List<DjConfigRuleItemLadder> configRuleItemLadders=field.getConfigRuleItemLadders();
                    if(configRuleItemLadders!=null&&configRuleItemLadders.size()>0){
                        DjConfigRuleItemThree configRuleItemThree=new DjConfigRuleItemThree();
                        configRuleItemThree.setBatchCode(field.getBatchCode());
                        configRuleItemThree.setParamType(field.getParamType());
                        configRuleItemThree.setParamWeight(field.getParamWeight());
                        configRuleItemThree.setParamName(field.getParamName());
                        configRuleItemThree.setModuleId(CommonUtil.isEmpty(moduleId) ? field.getModuleId() : moduleId);
                        configRuleItemThreeMapper.insert(configRuleItemThree);
                        for (DjConfigRuleItemLadder configRuleItemLadder : configRuleItemLadders) {
                            DjConfigRuleItemLadder configRuleItemLadderNew=new DjConfigRuleItemLadder();
                            configRuleItemLadderNew.setFraction(configRuleItemLadder.getFraction());
                            configRuleItemLadderNew.setItemThreeId(configRuleItemThree.getId());
                            configRuleItemLadderNew.setPhaseEnd(configRuleItemLadder.getPhaseEnd());
                            configRuleItemLadderNew.setPhaseStart(configRuleItemLadder.getPhaseStart());
                            configRuleItemLadderMapper.insert(configRuleItemLadderNew);
                        }
                    }

                    batchCode=field.getBatchCode();
                }
            }
            //新增操作流水
            String userID = request.getParameter(Constants.USERID);
            OperationFlow operationFlow=new OperationFlow();
            operationFlow.setName(configRuleModule.getTypeName()+"配置更新");
            operationFlow.setOperationId(moduleId);
            operationFlow.setOperationType("configRule_"+configRuleModule.getTypeId());
            operationFlow.setRemarks(batchCode);
            operationFlow.setUserId(userID);
            operationFlow.setUserType(0);
            operationFlowMapper.insert(operationFlow);
            return ServerResponse.createBySuccess("更新成功");
        } catch (Exception e) {
            logger.error("setConfigRuleItem:",e);
            return ServerResponse.createByErrorMessage("更新失败");
        }
    }

    /**
     * 等级明细列表
     * @return
     */
    public ServerResponse searchConfigRuleFlow(String moduleId) {
        try {
            DjConfigRuleModule configRuleModule=configRuleModuleMapper.selectByPrimaryKey(moduleId);
            Example example=new Example(OperationFlow.class);
            example.createCriteria().andEqualTo(OperationFlow.OPERATION_ID,moduleId)
                    .andEqualTo(OperationFlow.USER_TYPE,0)
                    .andEqualTo(OperationFlow.OPERATION_TYPE,"configRule_"+configRuleModule.getTypeId());
            List<OperationFlow> operationFlows = operationFlowMapper.selectByExample(example);
            for (OperationFlow configRuleRank : operationFlows) {
                configRuleRank.setUserName(userMapper.selectByPrimaryKey(configRuleRank.getUserId()).getUsername());
            }
            return ServerResponse.createBySuccess("查询成功", operationFlows);
        } catch (Exception e) {
            logger.error("searchConfigRuleRank:",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 新增规则配置明细
     * @return
     */
    public Map<String,String> setConfigRuleItemField(DjConfigRuleModule configRuleModule,String typeId) {
        Map<String,String> field= new HashMap();
        if(configRuleModule.getItemType()==1){
            if(MK001.equals(configRuleModule.getTypeId())){//旷工扣积分
                field.put("integral","配置积分(分)");
            }
            if(MK003.equals(configRuleModule.getTypeId())||MK004.equals(configRuleModule.getTypeId())){//大管家获取积分/工匠获取积分
                if(SG001.equals(typeId)||SG002.equals(typeId)||SG005.equals(typeId)){//周计划/巡查
                    field.put("integral","配置积分");
                }
                if(SG003.equals(typeId)||SG004.equals(typeId)||SG006.equals(typeId)){//竣工/验收
                    field.put("starOne","1星");
                    field.put("starTwo","2星");
                    field.put("starThree","3星");
                    field.put("starFour","4星");
                    field.put("starFive","5星");
                }
            }
            if(MK010.equals(configRuleModule.getTypeId())) {//大管家拿钱规则
                field.put("weekPlan","周计划");
                field.put("patrol","巡查");
                field.put("tested","验收");
                field.put("completed","竣工");
            }

            if(MK008.equals(configRuleModule.getTypeId())) {
                field.put("integral","配置上线(元)");
            }
            if(MK007.equals(configRuleModule.getTypeId())) {
                field.put("integral","配置比例(百分比)");
            }
            if(MK006.equals(configRuleModule.getTypeId())) {
                field.put("integral","配置次数(次)");
            }
            if(MK012.equals(configRuleModule.getTypeId())) {
                field.put("integral","配置时间(分钟)");
            }
            if(MK011.equals(configRuleModule.getTypeId())) {
                field.put("integral","配置持单(个)");
            }
            if(MK009.equals(configRuleModule.getTypeId())) {//工匠拿钱规则
                field.put("daily","每日完工拿钱(/天)");
                field.put("unlimited","每日完工拿钱上限");
                if(!"4".equals(typeId)) {//除拆除外
                    field.put("stageRatio", "阶段完工拿钱比例");
                }
                field.put("completedRatio","整体完工拿钱比例");
            }
        }
        if(configRuleModule.getItemType()==2){
            if(MK002.equals(configRuleModule.getTypeId())){//延期完工扣积分
                field.put("workerDelay","工匠延期扣分(每天)");
                field.put("stewardDelay","大管家延期扣分(每天)");
            }
            if(MK005.equals(configRuleModule.getTypeId())){//延期完工扣积分
                field.put("worker","工匠");
                field.put("steward","大管家");
            }
            if(MK013.equals(configRuleModule.getTypeId())){//新手保护单
                field.put("protect","新手保护单");
            }
            if(MK018.equals(configRuleModule.getTypeId())){//抢单限制
                field.put("protect","抢单限制");
            }
            if(MK014.equals(configRuleModule.getTypeId())){//积分转化当家贝
                field.put("integral","积分");
                field.put("mark","比例分");
                field.put("currency","比例贝");
            }
        }
        return field;
    }
    public static String   MK001 = "MK001";//旷工扣积分
    public static String   MK002 = "MK002";//延期完工扣积分
    public static String   MK003 = "MK003";//大管家获取积分
    public static String   MK004 = "MK004";//工匠获取积分
    public static String   MK005 = "MK005";//放弃(派)单扣分
    public static String   MK010 = "MK010";//大管家拿钱规则
    public static String   MK009 = "MK009";//工匠拿钱规则
    public static String   MK008 = "MK008";//滞留金上限
    public static String   MK007 = "MK007";//滞留金每单比例
    public static String   MK006 = "MK006";//月提现次数上限
    public static String   MK018 = "MK018";//抢单限制
    public static String   MK013 = "MK013";//新手保护单
    public static String   MK012 = "MK012";//抢单排队时间
    public static String   MK011 = "MK011";//持单上限
    public static String   MK015 = "MK015";//精算匹配店铺商品
    public static String   MK016 = "MK016";//搜索结果排序算法
    public static String   MK017 = "MK017";//管家派单算法
    public static String   MK014 = "MK014";//积分转化当家贝

    public static String   SG001 = "SG001";//周计划
    public static String   SG002 = "SG002";//巡查
    public static String   SG003 = "SG003";//验收
    public static String   SG004 = "SG004";//竣工
    public static String   SG005 = "SG005";//每日完工
    public static String   SG006 = "SG006";//被评价

    public static String   CS001 = "CS001";//店铺总销量
    public static String   CS002 = "CS002";//店铺上货数
    public static String   CS003 = "CS003";//商品点击率
    public static String   CS004 = "CS004";//商品收藏数
    public static String   CS005 = "CS005";//店铺的订单量
    public static String   CS006 = "CS006";//地域
    public static String   CS007 = "CS007";//评分
    public static String   CS008 = "CS008";//持单上线
}
