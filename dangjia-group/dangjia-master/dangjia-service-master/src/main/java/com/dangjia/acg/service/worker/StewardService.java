package com.dangjia.acg.service.worker;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.api.actuary.BudgetWorkerAPI;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.worker.CourseDTO;
import com.dangjia.acg.dto.worker.WorkerDetailDTO;
import com.dangjia.acg.mapper.core.IHouseFlowApplyMapper;
import com.dangjia.acg.mapper.core.IHouseFlowMapper;
import com.dangjia.acg.mapper.core.IHouseWorkerMapper;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.house.IModelingVillageMapper;
import com.dangjia.acg.mapper.matter.IWorkerDisclosureHouseFlowMapper;
import com.dangjia.acg.mapper.matter.IWorkerDisclosureMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.core.HouseFlowApply;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.house.ModelingVillage;
import com.dangjia.acg.modle.matter.WorkerDisclosure;
import com.dangjia.acg.modle.matter.WorkerDisclosureHouseFlow;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.service.config.ConfigMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
    private IModelingVillageMapper modelingVillageMapper;
    @Autowired
    private BudgetWorkerAPI budgetWorkerAPI;

    @Autowired
    private ConfigMessageService configMessageService;

    /**
     * 管家巡查扫验证二维码
     */
    public ServerResponse scanCode(String userToken, String code, String latitude, String longitude) {
        try {
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            Member steward = accessToken.getMember();//管家
            String[] str = code.split("=");
            String houseFlowId = str[1];
            HouseFlow hf = houseFlowMapper.selectByPrimaryKey(houseFlowId);//工匠houseFlow
            if (hf.getWorkType() != 4) {
                return ServerResponse.createByErrorMessage("该工匠未被业主支付");
            }
            switch (hf.getWorkSteta()) {
                case 0:
                    return ServerResponse.createByErrorMessage("该工匠未被业主支付");
                case 2:
                    return ServerResponse.createByErrorMessage("该工序已整体完工，无法巡查");
                case 3:
                    return ServerResponse.createByErrorMessage("该工匠未交底，去交底");
            }
            //工匠的坐标
            String x = hf.getLatitude();
            String y = hf.getLongitude();
            System.out.println("工匠坐标:经度" + y + ",工匠纬度:" + x);
            Calendar cal = Calendar.getInstance();
            cal.setTime(hf.getPast());//二维码生成时间
            cal.add(Calendar.MINUTE, 30);
            Calendar now = Calendar.getInstance();
            if (cal.before(now)) {
                return ServerResponse.createByErrorMessage("二维码失效,请重新生成");
            }
            //根据房子id找出该房子大管家
            Member stewardHouse = memberMapper.getSupervisor(hf.getHouseId());
            if (!stewardHouse.getId().equals(steward.getId())) {
                return ServerResponse.createByErrorMessage("工人与大管家不是同一个工地");
            }
            House house = houseMapper.selectByPrimaryKey(hf.getHouseId());
            ModelingVillage village = modelingVillageMapper.selectByPrimaryKey(house.getVillageId());//小区
            Double locationx = Double.parseDouble(village.getLocationx() == null ? "0" : village.getLocationx());//小区经度
            Double locationy = Double.parseDouble(village.getLocationy() == null ? "0" : village.getLocationy());//小区维度
            System.out.println("小区坐标:经度" + locationx + ",小区纬度:" + locationy);
            /*if(locationx == 0 || locationy == 0){
               return ServerResponse.createByErrorMessage("请配置该房子所在小区地理位置");
            }*/
            double distance = GetShortDistance(locationx, locationy, Double.valueOf(y), Double.valueOf(x));//计算距离
            System.out.println("工匠与小区的距离:" + distance + "米*********************");
//		    if(distance > 3000){
//	   			return ServerResponse.createByErrorMessage("工匠未在工地范围！");
//		    }
            distance = GetShortDistance(Double.valueOf(longitude), Double.valueOf(latitude), locationx, locationy);//计算距离
            System.out.println("管家坐标:经度" + longitude + ",管家纬度:" + latitude);
            System.out.println("管家与小区的距离:" + distance + "米*********************");
//		    if(distance > 3000){
//		    	return ServerResponse.createByErrorMessage("大管家未在工地范围！");
//		    }
            distance = GetShortDistance(Double.valueOf(longitude), Double.valueOf(latitude), Double.valueOf(y), Double.valueOf(x));//计算距离
            System.out.println("管家与工匠的距离:" + distance + "米*********************");
            if (distance > 3000) {
                return ServerResponse.createByErrorMessage("大管家与工匠不在一起");
            }
            return ServerResponse.createBySuccess("巡查成功", houseFlowId);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("扫码失败");
        }
    }

    /**
     * 根据经纬度计算距离，经度1，纬度1，经度2，纬度2
     */
    private double GetShortDistance(double lon1, double lat1, double lon2, double lat2) {
        double DEF_PI = 3.14159265359; // PI
        double DEF_2PI = 6.28318530712; // 2*PI
        double DEF_PI180 = 0.01745329252; // PI/180.0
        double DEF_R = 6370693.5; // radius of earth

        double ew1, ns1, ew2, ns2;
        double dx, dy, dew;
        double distance;
        // 角度转换为弧度
        ew1 = lon1 * DEF_PI180;
        ns1 = lat1 * DEF_PI180;
        ew2 = lon2 * DEF_PI180;
        ns2 = lat2 * DEF_PI180;
        // 经度差
        dew = ew1 - ew2;
        // 若跨东经和西经180 度，进行调整
        if (dew > DEF_PI)
            dew = DEF_2PI - dew;
        else if (dew < -DEF_PI)
            dew = DEF_2PI + dew;
        dx = DEF_R * Math.cos(ns1) * dew; // 东西方向长度(在纬度圈上的投影长度)
        dy = DEF_R * (ns1 - ns2); // 南北方向长度(在经度圈上的投影长度)
        // 勾股定理求斜边长
        distance = Math.sqrt(dx * dx + dy * dy);
        return distance;
    }

    /**
     * 管家巡查工匠生成二维码内容
     */
    public ServerResponse workerQrcode(String houseFlowId, String latitude, String longitude) {
        try {
            if (StringUtil.isEmpty(latitude) || StringUtil.isEmpty(longitude)) {
                return ServerResponse.createByErrorMessage("获取定位失败");
            }
            HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(houseFlowId);
            houseFlow.setPast(new Date());//记录二维码生成时间
            houseFlow.setLatitude(latitude);
            houseFlow.setLongitude(longitude);
            houseFlowMapper.updateByPrimaryKeySelective(houseFlow);
            return ServerResponse.createBySuccess("操作成功", "http://weixin.fengjiangit.com/g.html?a=" + houseFlowId);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("返回失败");
        }

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
                houseFlowApplyMapper.updateByPrimaryKeySelective(houseFlowApply);
            } else {
                houseFlowApply.setMemberCheck(2);
                houseFlowApply.setSupervisorCheck(2);
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
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            Member worker = accessToken.getMember();
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
                example.createCriteria().andEqualTo("state", 1);
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

            return ServerResponse.createBySuccess("获取进程详情成功", courseDTO);
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
            Long countOrder = houseWorkerMapper.getCountOrderByWorkerId(worker.getId());
            workerDetailDTO.setCountOrder(countOrder == null ? 0 : countOrder);//总单数

            HouseFlowApply todayStart = houseFlowApplyMapper.getTodayStart(houseFlow.getHouseId(), worker.getId(), new Date());//查询今日开工记录
            if (todayStart == null) {//没有今日开工记录
                workerDetailDTO.setIsStart("否");
            } else {
                workerDetailDTO.setIsStart("是");//今日是否开工；
            }
            List<HouseFlowApply> earliestTime = houseFlowApplyMapper.getEarliestTimeHouseApply(houseFlow.getHouseId(), worker.getId());//查询最早的每日开工申请
            Long suspendDay = houseFlowApplyMapper.getSuspendApply(houseFlow.getHouseId(), worker.getId());//根据房子id和工人id查询暂停天数
            Long everyEndDay = houseFlowApplyMapper.getEveryDayApply(houseFlow.getHouseId(), worker.getId());//根据房子id和工人id查询每日完工申请天数
            if (earliestTime != null && earliestTime.size() > 0) {
                Date EarliestDay = earliestTime.get(0).getCreateDate();//最早开工时间
                Date newDate = new Date();
                int num = DateUtil.daysofTwo(EarliestDay, newDate);//计算当前时间隔最早开工时间相差多少天
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
            workerDetailDTO.setPatrolled(houseFlowApplyMapper.getCountValidPatrolByHouseId(houseFlow.getHouseId(), worker.getId()));//已巡查

            return workerDetailDTO;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
