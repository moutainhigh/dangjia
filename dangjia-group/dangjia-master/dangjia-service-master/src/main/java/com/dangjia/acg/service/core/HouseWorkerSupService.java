package com.dangjia.acg.service.core;

import com.dangjia.acg.api.repair.MendMaterielAPI;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.mapper.core.IHouseFlowApplyMapper;
import com.dangjia.acg.mapper.core.IHouseFlowMapper;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.core.HouseFlowApply;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.service.house.HouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author: Ronalcheng
 * Date: 2019/3/27 0027
 * Time: 9:55
 * 1.31业务补充
 */
@Service
public class HouseWorkerSupService {
    @Autowired
    private CraftsmanConstructionService constructionService;
    @Autowired
    private IHouseFlowMapper houseFlowMapper;
    @Autowired
    private IHouseFlowApplyMapper houseFlowApplyMapper;
    @Autowired
    private IMemberMapper memberMapper;
    @Autowired
    private IWorkerTypeMapper workerTypeMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private MendMaterielAPI mendMaterielAPI;
    @Autowired
    private HouseService houseService;

    /**
     * 管家审核验收申请h
     * 材料审查
     * 剩余材料列表
     */
    public ServerResponse surplusList(String houseFlowApplyId) {
        HouseFlowApply houseFlowApply = houseFlowApplyMapper.selectByPrimaryKey(houseFlowApplyId);
        return mendMaterielAPI.surplusList(houseFlowApply.getWorkerTypeId(), houseFlowApply.getHouseId());
    }

//    /**
//     * 审核停工
//     */
//    public ServerResponse auditApply(String houseFlowApplyId, Integer memberCheck) {
//        HouseFlowApply houseFlowApply = houseFlowApplyMapper.selectByPrimaryKey(houseFlowApplyId);
//        houseFlowApply.setMemberCheck(memberCheck);
//        houseFlowApply.setModifyDate(new Date());
//        houseFlowApplyMapper.updateByPrimaryKeySelective(houseFlowApply);
//        return ServerResponse.createBySuccessMessage("操作成功");
//    }

    /**
     * 审核停工页面内容
     */
    public ServerResponse tingGongPage(String houseFlowApplyId) {
        HouseFlowApply houseFlowApply = houseFlowApplyMapper.selectByPrimaryKey(houseFlowApplyId);
        Member worker = memberMapper.selectByPrimaryKey(houseFlowApply.getWorkerId());
        WorkerType workerType = workerTypeMapper.selectByPrimaryKey(worker.getWorkerTypeId());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Map<String, Object> map = new HashMap();
        map.put("head", configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class) + worker.getHead());
        map.put("workName", workerType.getName());
        map.put("name", worker.getName());
        map.put("praiseRate", worker.getPraiseRate() == null ? "100%" : worker.getPraiseRate().multiply(new BigDecimal(100)) + "%");
        map.put("mobile", worker.getMobile());
        map.put("memberId", worker.getId());
        map.put("applyDec", houseFlowApply.getApplyDec());
        map.put("startDate", sdf.format(houseFlowApply.getStartDate()));
        map.put("endDate", sdf.format(houseFlowApply.getEndDate()));
        map.put("createDate", houseFlowApply.getCreateDate());
        return ServerResponse.createBySuccess("获取成功", map);
    }

    /**
     * 工匠申请停工
     */
    public ServerResponse applyShutdown(String userToken, String houseFlowId, String applyDec, String startDate, String endDate) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member worker = (Member) object;
            HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(houseFlowId);

            Example example = new Example(HouseFlowApply.class);
            example.createCriteria().andEqualTo(HouseFlowApply.HOUSE_FLOW_ID, houseFlowId).andEqualTo(HouseFlowApply.APPLY_TYPE, 3)
                    .andCondition(" member_check in (1,3) ").andEqualTo(HouseFlowApply.PAY_STATE, 1);
            List<HouseFlowApply> houseFlowApplyList = houseFlowApplyMapper.selectByExample(example);
            if (houseFlowApplyList.size() > 0) {
                HouseFlowApply houseFlowApply = houseFlowApplyList.get(0);
                if(houseFlowApply.getEndDate().getTime()>new Date().getTime()){
                    return ServerResponse.createByErrorMessage("工序处于停工期间!");
                }
            }
            if (houseFlow.getPause() == 1) {
                return ServerResponse.createByErrorMessage("工序已暂停施工,请勿重复申请");
            }
            if (houseFlow.getWorkSteta() == 3) {
                return ServerResponse.createByErrorMessage("工序待交底请勿发起停工申请");
            }
            Date start = DateUtil.toDate(startDate);
            HouseFlowApply todayStart = houseFlowApplyMapper.getTodayStart(houseFlow.getHouseId(), worker.getId(), new Date());//查询今日开工记录
            if (todayStart != null &&  DateUtil.daysofTwo(new Date(), start)==0) {
                return ServerResponse.createByErrorMessage("工序今日已开工，请勿选择今日时间！");
            }
            Date end =  DateUtil.toDate(endDate);


            HouseFlowApply hfa = new HouseFlowApply();//发起申请任务
            hfa.setHouseFlowId(houseFlowId);//工序id
            hfa.setWorkerId(worker.getId());//工人id
            hfa.setWorkerTypeId(worker.getWorkerTypeId());//工种id
            hfa.setWorkerType(worker.getWorkerType());//工种类型
            hfa.setHouseId(houseFlow.getHouseId());//房子id
            hfa.setApplyType(3);//申请类型0每日完工申请，1阶段完工申请，2整体完工申请,3停工申请，4：每日开工,5巡查,6无人巡查
            hfa.setApplyDec(applyDec);//描述
            hfa.setApplyMoney(new BigDecimal(0));//申请得钱
            hfa.setSupervisorMoney(new BigDecimal(0));
            hfa.setOtherMoney(new BigDecimal(0));
            hfa.setMemberCheck(0);//业主审核状态0未审核，1审核通过，2审核不通过，3自动审核
            hfa.setPayState(1);//标记为新停工申请
            hfa.setMemberCheck(1);//默认业主审核状态通过
            hfa.setSupervisorCheck(1);//默认大管家审核状态通过
            hfa.setSuspendDay(DateUtil.daysofTwo(start, end));//申请停工天数 计算
            hfa.setStartDate(start);
            hfa.setEndDate(end);
            houseFlowApplyMapper.insert(hfa);
//            houseService.insertConstructionRecord(hfa);
            return ServerResponse.createBySuccessMessage("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("申请失败");
        }
    }


}
