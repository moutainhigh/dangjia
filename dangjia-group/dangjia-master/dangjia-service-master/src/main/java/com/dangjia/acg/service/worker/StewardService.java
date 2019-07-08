package com.dangjia.acg.service.worker;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.api.actuary.BudgetWorkerAPI;
import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.worker.CourseDTO;
import com.dangjia.acg.dto.worker.WorkerDetailDTO;
import com.dangjia.acg.mapper.complain.IComplainMapper;
import com.dangjia.acg.mapper.core.IHouseFlowApplyMapper;
import com.dangjia.acg.mapper.core.IHouseFlowMapper;
import com.dangjia.acg.mapper.core.IHouseWorkerMapper;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.matter.IWorkerDisclosureHouseFlowMapper;
import com.dangjia.acg.mapper.matter.IWorkerDisclosureMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.modle.complain.Complain;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.core.HouseFlowApply;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.matter.WorkerDisclosure;
import com.dangjia.acg.modle.matter.WorkerDisclosureHouseFlow;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.service.config.ConfigMessageService;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.dangjia.acg.util.LocationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * author: Ronalcheng
 * Date: 2018/11/28 0028
 * Time: 14:16
 * <p>
 * 工匠端 管家 交底 巡查 扫码功能
 */
@Service
public class StewardService {

    @Autowired
    private IHouseFlowMapper houseFlowMapper;
    @Autowired
    private IWorkerDisclosureMapper workerDisclosureMapper;
    @Autowired
    private IWorkerDisclosureHouseFlowMapper workerDisclosureHouseFlowMapper;
    @Autowired
    private IWorkerTypeMapper workerTypeMapper;
    @Autowired
    private IHouseFlowApplyMapper houseFlowApplyMapper;
    @Autowired
    private IHouseWorkerMapper houseWorkerMapper;
    @Autowired
    private IMemberMapper memberMapper;
    @Autowired
    private IHouseMapper houseMapper;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private BudgetWorkerAPI budgetWorkerAPI;
    @Autowired
    private IComplainMapper complainMapper;
    @Autowired
    private ConfigMessageService configMessageService;
    @Autowired
    private CraftsmanConstructionService constructionService;

    /**
     * 管家巡查扫验证二维码
     */
    public ServerResponse scanCode(String userToken, String code, String latitude, String longitude) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member member = (Member) object;
        if (CommonUtil.isEmpty(code)) {
            return ServerResponse.createByErrorMessage("二维码信息有误");
        }
        if (CommonUtil.isEmpty(latitude) || CommonUtil.isEmpty(longitude)) {
            return ServerResponse.createByErrorMessage("经纬度信息不正确");
        }
        String[] str = code.split("=");
        if (str.length < 2) {
            return ServerResponse.createByErrorMessage("二维码内容不正确");
        }
        String houseFlowId = str[1];
        HouseFlow hf = houseFlowMapper.selectByPrimaryKey(houseFlowId);//工匠houseFlow
        if (hf == null) {
            return ServerResponse.createByErrorMessage("工匠工序不存在");
        }
        if (hf.getWorkType() != 4) {
            return ServerResponse.createByErrorMessage("该工匠未被业主支付");
        }
        if (hf.getPast() == null) {
            return ServerResponse.createByErrorMessage("二维码信息有误，请重新生成");
        }
        switch (hf.getWorkSteta()) {
            case 0:
                return ServerResponse.createByErrorMessage("该工匠未被业主支付");
            case 2:
                return ServerResponse.createByErrorMessage("该工序已整体完工，无法巡查");
            case 3:
                return ServerResponse.createByErrorMessage("该工匠未交底，去交底");
        }
        //根据房子id找出该房子大管家
        Member stewardHouse = memberMapper.getSupervisor(hf.getHouseId());
        if (!stewardHouse.getId().equals(member.getId())) {
            return ServerResponse.createByErrorMessage("工人与大管家不是同一个工地");
        }
        double longitude1;
        double latitude1;
        double longitude2;
        double latitude2;
        try {
            longitude1 = Double.valueOf(longitude);
            latitude1 = Double.valueOf(latitude);
            longitude2 = Double.valueOf(hf.getLongitude());
            latitude2 = Double.valueOf(hf.getLatitude());
        } catch (NumberFormatException e) {
            return ServerResponse.createByErrorMessage("位置信息有误");
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(hf.getPast());//二维码生成时间
        cal.add(Calendar.MINUTE, 30);
        Calendar now = Calendar.getInstance();
        if (cal.before(now)) {
            return ServerResponse.createByErrorMessage("二维码失效,请重新生成");
        }
        double distance = LocationUtils.getDistance(latitude1, longitude1, latitude2, longitude2);//计算距离
        if (distance > 1000) {
            return ServerResponse.createByErrorMessage("大管家与工匠不在一起");
        }
        return ServerResponse.createBySuccess("巡查成功", houseFlowId);
    }

    /**
     * 管家巡查工匠生成二维码内容
     */
    public ServerResponse workerQrcode(String houseFlowId, String latitude, String longitude) {
        if (CommonUtil.isEmpty(latitude) || CommonUtil.isEmpty(longitude)) {
            return ServerResponse.createByErrorMessage("获取定位失败");
        }
        HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(houseFlowId);
        if (houseFlow == null) {
            return ServerResponse.createByErrorMessage("工序不存在");
        }
        houseFlow.setPast(new Date());//记录二维码生成时间
        houseFlow.setLatitude(latitude);
        houseFlow.setLongitude(longitude);
        houseFlowMapper.updateByPrimaryKeySelective(houseFlow);
        return ServerResponse.createBySuccess("操作成功", "http://weixin.fengjiangit.com/g.html?a=" + houseFlowId);
    }

    /**
     * 管家审核停工申请
     * state 1通过 0不通过
     */
    public ServerResponse passShutWork(String houseFlowApplyId, String content, int state) {
        try {
            HouseFlowApply houseFlowApply = houseFlowApplyMapper.selectByPrimaryKey(houseFlowApplyId);
            houseFlowApply.setApplyDec(content);
            if (state == 1) {
                houseFlowApply.setMemberCheck(1);
                houseFlowApply.setSupervisorCheck(1);
                houseFlowApply.setModifyDate(new Date());
                houseFlowApplyMapper.updateByPrimaryKeySelective(houseFlowApply);
            } else {
                houseFlowApply.setMemberCheck(2);
                houseFlowApply.setSupervisorCheck(2);
                houseFlowApply.setModifyDate(new Date());
                houseFlowApplyMapper.updateByPrimaryKeySelective(houseFlowApply);
                //不通过停工申请
                HouseFlow hf = houseFlowMapper.selectByPrimaryKey(houseFlowApply.getHouseFlowId());
                hf.setPause(0);
                houseFlowMapper.updateByPrimaryKeySelective(hf);
            }
            return ServerResponse.createBySuccessMessage("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }


    /**
     * 成功返回交底内容
     */
    public ServerResponse readProjectInfo(String houseFlowId) {
        try {
            List<WorkerDisclosure> wdList = workerDisclosureMapper.getWorkerDisclosureList(houseFlowId);
            HouseFlow hf = houseFlowMapper.selectByPrimaryKey(houseFlowId);
            JSONArray jsonArray = budgetWorkerAPI.getTecByHouseFlowId(hf.getHouseId(), hf.getId());
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String technologyName = object.getString("technologyName");
                String content = object.getString("content");
                WorkerDisclosure workerDisclosure = new WorkerDisclosure();
                workerDisclosure.setName(technologyName);
                workerDisclosure.setDetails(content);
                wdList.add(workerDisclosure);
            }
            String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            for (WorkerDisclosure w : wdList) {
                if (w.getImg() != null) {
                    w.setImg(imageAddress + w.getImg());
                }
            }
            return ServerResponse.createBySuccess("查询成功", wdList);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 提交完成交底
     */
    public ServerResponse confirmProjectInfo(String houseFlowId) {
        try {
            HouseFlow hf = houseFlowMapper.selectByPrimaryKey(houseFlowId);
            hf.setWorkSteta(4);//施工中
            houseFlowMapper.updateByPrimaryKeySelective(hf);
            House house = houseMapper.selectByPrimaryKey(hf.getHouseId());
            WorkerType workerType = workerTypeMapper.selectByPrimaryKey(hf.getWorkerTypeId());
            configMessageService.addConfigMessage(null, "zx", house.getMemberId(), "0", "大管家交底",
                    String.format(DjConstants.PushMessage.STEWARD_CRAFTSMAN_FINISHED, house.getHouseName(), workerType.getName()), "");
            return ServerResponse.createBySuccessMessage("交底成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 交底工匠扫二维码调用
     * 工匠扫二维码 成功返回交底内容url
     */
    public ServerResponse tellCode(String userToken, String code) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member worker = (Member) object;
            String[] str = code.split("=");
            String houseFlowId = str[1];
            HouseFlow hf = houseFlowMapper.selectByPrimaryKey(houseFlowId);//查询houseFlow
            if (!hf.getWorkerId().equals(worker.getId())) {
                return ServerResponse.createByErrorMessage("交底人不匹配");
            }
            String url = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class) +
                    String.format(DjConstants.GJPageAddress.READPROJECTINFO, userToken, hf.getCityId(), "交底详情") + "&houseFlowId=" + houseFlowId;
            return ServerResponse.createBySuccess("交底成功", url);
        } catch (Exception e) {
            return ServerResponse.createByErrorMessage("扫码失败");
        }
    }

    /**
     * 管家交底生成二维码内容
     */
    public ServerResponse stewardQrcode(String houseFlowId, String disclosureIds) {
        try {
            //删除之前选中的
            Example example = new Example(WorkerDisclosureHouseFlow.class);
            example.createCriteria().andEqualTo(WorkerDisclosureHouseFlow.HOUSE_FLOW_ID, houseFlowId);
            workerDisclosureHouseFlowMapper.deleteByExample(example);
            String[] tellList = disclosureIds.split(",");
            for (String tell : tellList) {
                WorkerDisclosureHouseFlow wdh = new WorkerDisclosureHouseFlow();
                wdh.setWorkerDiscloId(tell);
                wdh.setHouseFlowId(houseFlowId);
                wdh.setState(1);
                workerDisclosureHouseFlowMapper.insert(wdh);
            }
            return ServerResponse.createBySuccess("操作成功", "http://weixin.fengjiangit.com/g.html?a=" + houseFlowId);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("保存交底事项失败");
        }
    }


    /**
     * 管家查看工序进程
     */
    public ServerResponse getCourse(String houseFlowId) {
        try {
            HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(houseFlowId);
            House house = houseMapper.selectByPrimaryKey(houseFlow.getHouseId());
            CourseDTO courseDTO = new CourseDTO();
            courseDTO.setHouseName(house.getHouseName());
            courseDTO.setWorkerDetailDTO(getWorkerDetail(houseFlow));
            courseDTO.setWorkType(houseFlow.getWorkType());
            courseDTO.setWorkSteta(houseFlow.getWorkSteta());
            courseDTO.setHouseFlowId(houseFlowId);
            courseDTO.setHouseFlowApplyId("");
            String userToken = redisClient.getCache("role2:" + houseFlow.getWorkerId(), String.class);
            courseDTO.setRewardUrl(configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class) +
                    String.format(DjConstants.GJPageAddress.JFREGULATIONS, userToken, houseFlow.getCityId(), "选择奖罚条例"));//奖罚页面
            if (houseFlow.getWorkType() == 4 && houseFlow.getWorkSteta() == 3) {//待交底
                Example example = new Example(WorkerDisclosure.class);
                example.createCriteria().andEqualTo("state", 1).andEqualTo("type", 0);
                List<WorkerDisclosure> wdList = workerDisclosureMapper.selectByExample(example);
                courseDTO.setWorkerDisclosureList(wdList);
                courseDTO.setApplyType(0);//没有申请
            } else {//施工中
                HouseFlowApply hfa = houseFlowApplyMapper.getSupervisorCheck(houseFlow.getId(), houseFlow.getWorkerId());
                if (hfa != null) {
                    courseDTO.setApplyType(hfa.getApplyType());
                    courseDTO.setHouseFlowApplyId(hfa.getId());
                } else {
                    courseDTO.setApplyType(0);//没有申请
                }
            }
            if (houseFlow.getWorkType() != 4) {//如果是已抢单待支付。则提醒业主支付
                courseDTO.setIfBackOut(0);//0可放弃；1：申请停工；2：已停工 3 审核中
            } else if (houseFlow.getPause() == 1) {//已暂停  停工有两种情况需要处理
                courseDTO.setIfBackOut(2);
            } else {
                List<HouseFlowApply> allAppList = houseFlowApplyMapper.getTodayHouseFlowApply(houseFlow.getId(), 2, houseFlow.getWorkerId(), new Date());//查询今天是否已提交整体完工
                List<HouseFlowApply> stageAppList = houseFlowApplyMapper.getTodayHouseFlowApply(houseFlow.getId(), 1, houseFlow.getWorkerId(), new Date());//查询今天是否已提交阶段完工
                if (allAppList.size() > 0) {
                    courseDTO.setIfBackOut(2);
                } else if (stageAppList.size() > 0) {
                    courseDTO.setIfBackOut(2);
                } else {
                    courseDTO.setIfBackOut(1);
                }
            }
            Example example = new Example(Complain.class);
            example.createCriteria().andEqualTo(Complain.MEMBER_ID, houseFlow.getWorkerId())
                    .andEqualTo(Complain.HOUSE_ID, houseFlow.getHouseId())
                    .andEqualTo(Complain.STATUS, 0)
                    .andEqualTo(Complain.COMPLAIN_TYPE, 3);
            List<Complain> complains = complainMapper.selectByExample(example);
            if (complains.size() > 0) {
                Map map = BeanUtils.beanToMap(courseDTO);
                map.put(Complain.STATUS, 0);
                return ServerResponse.createBySuccess("获取进程详情成功", map);
            } else {
                return ServerResponse.createBySuccess("获取进程详情成功", courseDTO);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 工匠详情
     */
    private WorkerDetailDTO getWorkerDetail(HouseFlow houseFlow) {
        try {
            WorkerDetailDTO workerDetailDTO = new WorkerDetailDTO();
            Member worker = memberMapper.selectByPrimaryKey(houseFlow.getWorkerId());
            WorkerType workerType = workerTypeMapper.selectByPrimaryKey(worker.getWorkerTypeId());//查询工种
            workerDetailDTO.setWorkerTypeName(workerType.getName());
            workerDetailDTO.setWorkerId(worker.getId());
            workerDetailDTO.setHead(configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class) + worker.getHead());
            workerDetailDTO.setName(worker.getName());
            workerDetailDTO.setMobile(worker.getMobile());
            workerDetailDTO.setPraiseRate(worker.getPraiseRate().multiply(new BigDecimal(100)) + "%");//好评率
            workerDetailDTO.setEvaluationScore(worker.getEvaluationScore());//积分
            Long countOrder = houseWorkerMapper.getCountOrderByWorkerId(houseFlow.getWorkerId());
            workerDetailDTO.setCountOrder(countOrder == null ? 0 : countOrder);//总单数
            HouseFlowApply todayStart = houseFlowApplyMapper.getTodayStart(houseFlow.getHouseId(), houseFlow.getWorkerId(), new Date());//查询今日开工记录
            if (todayStart == null) {//没有今日开工记录
                workerDetailDTO.setIsStart("否");
            } else {
                workerDetailDTO.setIsStart("是");//今日是否开工；
            }
            List<HouseFlowApply> earliestTime = houseFlowApplyMapper.getEarliestTimeHouseApply(houseFlow.getHouseId(), houseFlow.getWorkerId());//查询最早的每日开工申请
            Long suspendDay = houseFlowApplyMapper.getSuspendApply(houseFlow.getHouseId(), houseFlow.getWorkerId());//根据房子id和工人id查询暂停天数
            Long everyEndDay = houseFlowApplyMapper.getEveryDayApply(houseFlow.getHouseId(), houseFlow.getWorkerId());//根据房子id和工人id查询每日完工申请天数
            if (earliestTime != null && earliestTime.size() > 0) {
                Date EarliestDay = earliestTime.get(0).getCreateDate();//最早开工时间
                Date newDate = new Date();
                int num = 1 + DateUtil.daysofTwo(EarliestDay, newDate);//计算当前时间隔最早开工时间相差多少天
                if (suspendDay == null) {
                    workerDetailDTO.setTotalDay(num);//总开工天数
                } else {
                    long aa = num - suspendDay;
                    if (aa >= 0) {
                        workerDetailDTO.setTotalDay(aa);
                    } else {
                        workerDetailDTO.setTotalDay(0);
                    }
                }
            } else {
                workerDetailDTO.setTotalDay(0);//总开工天数
            }
            workerDetailDTO.setEveryDay(everyEndDay == null ? 0 : everyEndDay);
            workerDetailDTO.setSuspendDay(suspendDay == null ? 0 : suspendDay);//暂停天数
            workerDetailDTO.setPatrol(houseFlow.getPatrol());//巡查标准次数
            workerDetailDTO.setPatrolled(houseFlowApplyMapper.getCountValidPatrolByHouseId(houseFlow.getHouseId(), houseFlow.getWorkerId()));//已巡查
            return workerDetailDTO;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
