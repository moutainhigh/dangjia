package com.dangjia.acg.service.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.api.basics.WorkerGoodsAPI;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.enums.EventStatus;
import com.dangjia.acg.common.qrcode.QRCodeUtil;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.JsmsUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.core.WorkerFhowListResult;
import com.dangjia.acg.mapper.config.ISmsMapper;
import com.dangjia.acg.mapper.core.*;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.house.IModelingVillageMapper;
import com.dangjia.acg.mapper.matter.IWorkerDisclosureMapper;
import com.dangjia.acg.mapper.matter.IWorkerEverydayMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.other.IBankCardMapper;
import com.dangjia.acg.mapper.other.IWorkDepositMapper;
import com.dangjia.acg.mapper.worker.IRewardPunishRecordMapper;
import com.dangjia.acg.mapper.worker.IWithdrawDepositMapper;
import com.dangjia.acg.mapper.worker.IWorkerBankCardMapper;
import com.dangjia.acg.mapper.worker.IWorkerDetailMapper;
import com.dangjia.acg.modle.config.Sms;
import com.dangjia.acg.modle.core.*;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.house.ModelingVillage;
import com.dangjia.acg.modle.matter.WorkerDisclosure;
import com.dangjia.acg.modle.matter.WorkerEveryday;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.other.BankCard;
import com.dangjia.acg.modle.other.WorkDeposit;
import com.dangjia.acg.modle.worker.RewardPunishRecord;
import com.dangjia.acg.modle.worker.WithdrawDeposit;
import com.dangjia.acg.modle.worker.WorkerBankCard;
import com.dangjia.acg.modle.worker.WorkerDetail;
import com.dangjia.acg.util.StringTool;
import com.google.zxing.qrcode.encoder.QRCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2018/11/5 0005
 * Time: 18:58
 */
@Service
public class HouseWorkerService {

    @Autowired
    private IHouseWorkerMapper houseWorkerMapper;
    @Autowired
    private IHouseFlowMapper houseFlowMapper;
    @Autowired
    private IHouseMapper houseMapper;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private IMemberMapper memberMapper;
    @Autowired
    private IHouseFlowApplyMapper houseFlowApplyMapper;
    @Autowired
    private IHouseWorkerOrderMapper houseWorkerOrderMapper;
    @Autowired
    private IWorkerTypeMapper workerTypeMapper;
    @Autowired
    private IWorkerEverydayMapper workerEverydayMapper;
    @Autowired
    private IWorkerDetailMapper workerDetailMapper;
    @Autowired
    private IWorkerBankCardMapper workerBankCardMapper;
    @Autowired
    private IBankCardMapper bankCardMapper;
    @Autowired
    private IWithdrawDepositMapper withdrawDepositMapper;
    @Autowired
    private WorkerGoodsAPI workerGoodsAPI;
    @Autowired
    private IWorkDepositMapper workDepositMapper;
    @Autowired
    private IHouseFlowApplyImageMapper houseFlowApplyImageMapper;
    @Autowired
    private IModelingVillageMapper modelingVillageMapper;//小区
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IRewardPunishRecordMapper rewardPunishRecordMapper;
    @Autowired
    private IWorkerDisclosureMapper workerDisclosureMapper;


    @Autowired
    private ISmsMapper smsMapper;
    /*
    换人
     */
    public ServerResponse setChangeWorker(String userToken,String houseWorkerId){
        AccessToken accessToken = redisClient.getCache(userToken+ Constants.SESSIONUSERID,AccessToken.class);
        HouseWorker houseWorker = houseWorkerMapper.selectByPrimaryKey(houseWorkerId);
        if (houseWorker.getWorkType() == 6){
            return ServerResponse.createByErrorMessage("已支付不能换人,请联系当家装修");
        }
        houseWorker.setWorkType(2);//被换
        houseWorkerMapper.updateByPrimaryKeySelective(houseWorker);
        HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(houseWorker.getHouseFlowId());
        houseFlow.setWorkerId("");
        houseFlow.setWorkType(2);
        houseFlow.setReleaseTime(new Date());//重新发布
        houseFlow.setRefuseNumber(houseFlow.getRefuseNumber() + 1);
        houseFlowMapper.updateByPrimaryKeySelective(houseFlow);
        return ServerResponse.createBySuccessMessage("操作成功");
    }

    /*
     * 抢单
     */
    public ServerResponse setWorkerGrab(String userToken,String houseFlowId){
        AccessToken accessToken = redisClient.getCache(userToken+ Constants.SESSIONUSERID,AccessToken.class);
        Member worker = accessToken.getMember();
        HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(houseFlowId);
        if(houseFlow.getWorkType() == 3){
            return ServerResponse.createByErrorMessage("该订单已被抢");
        }
        House house = houseMapper.selectByPrimaryKey(houseFlow.getHouseId());
        houseFlow.setGrabNumber(houseFlow.getGrabNumber()+1);
        houseFlow.setWorkType(3);//等待支付
        houseFlowMapper.updateByPrimaryKeySelective(houseFlow);
        if(worker.getWorkerType() == 1){//设计师
            house.setDesignerOk(4);//有设计抢单待业主支付
            houseMapper.updateByPrimaryKeySelective(house);
        }
        houseWorkerMapper.doModifyAllByWorkerId(worker.getId());//将所有houseWorker的选中状态IsSelect改为0未选中
        HouseWorker houseWorker = new HouseWorker();
        houseWorker.setHouseId(house.getId());
        houseWorker.setWorkerId(worker.getId());
        houseWorker.setHouseFlowId(houseFlowId);
        houseWorker.setWorkerTypeId(houseFlow.getWorkerTypeId());
        houseWorker.setWorkerType(houseFlow.getWorkerType());
        houseWorker.setWorkType(1);//已抢单
        houseWorker.setWorkSteta(0);
        houseWorker.setEvaluateSteta(0);
        houseWorker.setHasEvaluate(0);
        houseWorker.setApply(0);
        houseWorker.setIsSelect(1);
        houseWorkerMapper.insert(houseWorker);
        return ServerResponse.createBySuccessMessage("抢单成功");
    }

    /**
     * 根据工人id查询自己的施工界面
     * @return
     */
    public ServerResponse getConstructionByWorkerId(String userToken){
        try{
            HttpServletRequest request = ((ServletRequestAttributes )RequestContextHolder.getRequestAttributes()).getRequest();
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class) + configUtil.getValue(SysConfig.PUBLIC_TEMPORARY_FILE_ADDRESS, String.class);
            List<Map<String, Object>> grabList=new ArrayList<Map<String,Object>>();//返回的任务list
            AccessToken accessToken = redisClient.getCache(userToken+ Constants.SESSIONUSERID,AccessToken.class);
            Member worker=accessToken.getMember();
            Map<String, Object> returnMap=new HashMap<>();//返回的对象
            HouseWorker hw = houseWorkerMapper.getDetailHouseWorker(worker.getId());//根据工人id查询已支付未完工并默认的施工任务
            List<HouseWorker> houseList=houseWorkerMapper.getAllHouseWorker(worker.getId());//查询所有施工中的订单
            if(hw==null){//没有施工中的任务
                if(houseList.size()>0){
                    hw=houseList.get(0);
                    hw.setIsSelect(1);//设置成默认
                    houseWorkerMapper.updateByPrimaryKeySelective(hw);
                }else{
                    return ServerResponse.createByErrorCodeMessage(EventStatus.NO_DATA.getCode(),"您暂无施工中的记录,快去接单吧！");
                }
            }
            List<HouseFlow> hfList = houseFlowMapper.getAllFlowByHouseId(hw.getHouseId());
            House house=houseMapper.selectByPrimaryKey(hw.getHouseId());//查询房产信息
            HouseFlow hf = houseFlowMapper.selectByPrimaryKey(hw.getHouseFlowId());//查询自己的任务状态
            if(worker!=null&&worker.getWorkerType()==3){//如果是大管家
                returnMap.put("workerType", 0);//大管家
                returnMap.put("houseFlowId", hf.getId());//houseFlowId
                //房产信息
                returnMap.put("houseName", (house.getResidential()==null?"*":house.getResidential())
                        +(house.getBuilding()==null?"*":house.getBuilding())+"栋"
                        +(house.getUnit()==null?"*":house.getUnit())+"单元"+(house.getNumber()==null?"*":house.getNumber())+"号");//地址
                returnMap.put("houseMemberName", worker.getName()==null?"":worker.getName());//业主名称
                returnMap.put("houseMemberPhone", worker.getMobile()==null?"":worker.getMobile());//业主电话
                returnMap.put("allPatrol", "总巡查次数"+(houseFlowApplyMapper.getCountValidPatrolByHouseId(house.getId(),null)==null?"0":houseFlowApplyMapper.getCountValidPatrolByHouseId(house.getId(),null)));//总巡查次数
                HouseWorkerOrder hwo=houseWorkerOrderMapper.getHouseWorkerOrder(hf.getId(), worker.getId());
                if(hwo==null){
                    returnMap.put("alreadyMoney", 0);//已得钱
                    returnMap.put("alsoMoney", 0);//还可得钱
                }else{
                    BigDecimal alsoMoney =(hwo.getWorkPrice()==null?new BigDecimal(0):hwo.getWorkPrice()).subtract(hwo.getHaveMoney()==null?new BigDecimal(0):hwo.getHaveMoney());//还可得钱
                    returnMap.put("alreadyMoney",(hwo.getWorkPrice().setScale(2, BigDecimal.ROUND_HALF_UP)).toString());//已得钱
                    returnMap.put("alsoMoney", (alsoMoney.setScale(2, BigDecimal.ROUND_HALF_UP)).toString());//还可得钱
                }

                List<Map<String, Object>> returnlistMap=new ArrayList<Map<String,Object>>();
                //工程资料
                List<Map<String, Object>> listMap=new ArrayList<Map<String,Object>>();
                Map<String, Object> map1=new HashMap<String, Object>();
                map1.put("name", "施工图");
                map1.put("type", 0);
                map1.put("image", address+"/images/gongjiang/artisan_25.png");
                map1.put("url", StringTool.getUrl(request) + "/worker/worker!drawings.action?houseid="+house.getId()+"&title施工图title");
                Map<String, Object> map2=new HashMap<String, Object>();
                map2.put("name", "精算");
                map2.put("type", 0);
                map2.put("image", address+"/images/gongjiang/artisan_26.png");
                map2.put("url", StringTool.getUrl(request) + "/worker/worker!budget.action?houseid="+house.getId()+"&title精算title");
                Map<String, Object> map3=new HashMap<String, Object>();
                map3.put("name", "工艺标准");
                map3.put("type", 0);
                map3.put("image", address+"/images/gongjiang/artisan_27.png");
                map3.put("url",StringTool.getUrl(request) +  "/worker/worker!technological.action?houseid="+house.getId()+"&title工艺标准title");
                Map<String, Object> map4=new HashMap<String, Object>();
                map4.put("name", "验收标准");
                map4.put("type", 0);
                map4.put("image", address+"/images/gongjiang/artisan_28.png");
                map4.put("url", StringTool.getUrl(request) + "/worker/worker!standard.action?houseid="+house.getId()+"&title验收标准title");
                listMap.add(map1);
                listMap.add(map2);
                listMap.add(map3);
                listMap.add(map4);
                Map<String, Object> mapGon=new HashMap<String, Object>();
                mapGon.put("listMap", listMap);
                mapGon.put("name", "工程资料");
                returnlistMap.add(mapGon);//工程资料
                //材料人工
                List<Map<String, Object>> listMap2=new ArrayList<Map<String,Object>>();
                Map<String, Object> map5=new HashMap<String, Object>();
                map5.put("name", "要货");
                map5.put("type", 0);
                map5.put("image", address+"/images/gongjiang/artisan_22.png");
                map5.put("url", StringTool.getUrl(request) + "/worker/supervisor_split!rushOrders.action?houseid="+house.getId()+"&title要货title");
                Map<String, Object> map6=new HashMap<String, Object>();
                map6.put("name", "收货");
                map6.put("type", 0);
                map6.put("image", address+"/images/gongjiang/artisan_23.png");
                map6.put("url", StringTool.getUrl(request) + "/worker/supervisor_split!deliverGoods.action?houseid="+house.getId()+"&title收货title");
                Map<String, Object> map7=new HashMap<String, Object>();
                map7.put("name", "补材料");
                map7.put("type", 0);
                map7.put("image", address+"/images/gongjiang/artisan_24.png");
                map7.put("url", StringTool.getUrl(request) + "/worker/supervisor!materialsList.action?houseid="+house.getId()+"&title补材料title");
                Map<String, Object> map8=new HashMap<String, Object>();
                map8.put("name", "补人工");
                map8.put("type", 0);
                map8.put("image", address+"/images/gongjiang/artisan_31.png");
                map8.put("url", StringTool.getUrl(request) + "/worker/supervisor!fillWork.action?houseid="+house.getId()+"&title补人工title");
                Map<String, Object> map11=new HashMap<String, Object>();
                map11.put("name", "补材料记录");
                map11.put("type", 0);
                map11.put("image", address+"/images/gongjiang/artisan_24.png");
                map11.put("url", StringTool.getUrl(request) + "/worker/supervisor!deliverGoods.action?houseid="+house.getId()+"&title补材料记录title");
                Map<String, Object> map12=new HashMap<String, Object>();
                map12.put("name", "补人工记录");
                map12.put("type", 0);
                map12.put("image", address+"/images/gongjiang/artisan_31.png");
                map12.put("url", StringTool.getUrl(request) + "/worker/supervisor!deliverWorkerGoods.action?houseid="+house.getId()+"&title补人工记录title");
                listMap2.add(map5);
                listMap2.add(map6);
                if(hf.getWorkType()==4){//业主已支付
                    listMap2.add(map7);
                    listMap2.add(map11);
                    listMap2.add(map8);
                    listMap2.add(map12);
                }
                Map<String, Object> mapCai=new HashMap<String, Object>();
                mapCai.put("listMap", listMap2);
                mapCai.put("name", "材料人工");
                returnlistMap.add(mapCai);//工程资料
                //其他
                List<Map<String, Object>> listMap3=new ArrayList<Map<String,Object>>();
                Map<String, Object> map9=new HashMap<String, Object>();
                map9.put("name", "工地记录");
                map9.put("type", 0);
                map9.put("image", address+"/images/gongjiang/artisan_29.png");
                map9.put("url",StringTool.getUrl(request) +  "/app/modeling_reference_effect!checklist.action?houseid="+house.getId()+"&title工地记录title");
                Map<String, Object> map10=new HashMap<String, Object>();
                map10.put("name", "通讯录");
                map10.put("type",0);
                map10.put("image", address+"/images/gongjiang/artisan_30.png");
                map10.put("url", StringTool.getUrl(request) + "/worker/worker!phonelist.action?houseFlowId="+hf.getId()+"&workerId="+worker.getId()+"&title通讯录title");
                listMap3.add(map9);
                listMap3.add(map10);
                Map<String, Object> mapQi=new HashMap<String, Object>();
                mapQi.put("listMap", listMap3);
                mapQi.put("name", "其他");
                returnlistMap.add(mapQi);//其他
                returnMap.put("bigList", returnlistMap);

                //施工进程
                Integer c=new Integer(0);
                List<WorkerFhowListResult> listMap4=new ArrayList<WorkerFhowListResult>();//大进程list
                List<String> promptList=new ArrayList<String>();//消息提示list
                if(hf.getWorkerType()==3&&hf.getWorkType()==4&&hf.getSupervisorStart()==1){//当业主支付大管家费用并且确认开工之后之后才出现
                    for (HouseFlow hfl : hfList) {//大进程
                        WorkerFhowListResult wfr=new WorkerFhowListResult();//大进程进度
                        if((hfl.getWorkerType()==1||hfl.getWorkerType()==2||hfl.getWorkerType()==3)){
                            continue;
                        }else{
                            Example example = new Example(HouseWorker.class);
                            example.createCriteria().andEqualTo("houseId", house.getId()).andEqualTo("workerTypeId", hfl.getWorkerTypeId()).andEqualTo("workType", 6);
                            List<HouseWorker> hwList=houseWorkerMapper.selectByExample(example);//根据房子id和工匠type查询房子对应的工人
                            HouseWorker houseWorker=new HouseWorker();
                            if(hwList.size()>0){
                                houseWorker=hwList.get(0);
                            }
                            Member worker2=memberMapper.selectByPrimaryKey(houseWorker.getWorkerId());
                            WorkerType workerType=workerTypeMapper.selectByPrimaryKey(hfl.getWorkerTypeId());
                            wfr.setHouseFlowId(hfl.getId());//进程id
                            wfr.setHouseFlowtype(hfl.getWorkerType());//进程类型
                            wfr.setHouseFlowName(workerType==null?"":workerType.getName());//大进程名
                            wfr.setWorkerName(worker2==null?"":worker2.getName());//工人名称
                            wfr.setWorkerId(worker2==null?"":worker2.getId());//工人id
                            wfr.setWorkerPhone(worker2==null?"":worker2.getMobile());//工人手机
                            wfr.setPatrolSecond("巡查次数"+houseFlowApplyMapper.getCountValidPatrolByHouseId(house.getId(), worker2==null?"0":worker2.getId()));//巡查次数
                            wfr.setPatrolStandard("巡查标准"+hfl.getPatrol());//巡查标准
                            HouseFlowApply todayStart=houseFlowApplyMapper.getTodayStart(house.getId(),worker2==null?"":worker2.getId());//查询今日开工记录
                            if(todayStart==null){//没有今日开工记录
                                wfr.setIsStart("0");//今日是否开工0:否；1：是；
                            }else{
                                wfr.setIsStart("1");//今日是否开工0:否；1：是；
                                c++;
                            }
                            wfr.setDetailUrl(StringTool.getUrl(request) + "/worker/supervisor!course.action?houseFlowId="+hfl.getId()+"&workerid="+(worker2==null?"":worker2.getId())+"&title工序详情title");//进程详情链接
                            HouseFlowApply houseFlowApp=houseFlowApplyMapper.checkHouseFlowApply(hfl.getId(),worker2==null?"":worker2.getId());//根据工种任务id和工人id查询此工人待审核
                            String prompt="";//消息提示
                            if(houseFlowApp!=null&&houseFlowApp.getApplyType()==1){//阶段完工申请
                                wfr.setButtonTitle("阶段完工申请");//按钮提示
                                prompt="我是"+(workerType==null?"":workerType.getName())+"工"+(worker2==null?"":worker2.getName()+",我提交了阶段完工申请");//消息提示
                                promptList.add(prompt);
                                wfr.setState(4);//装修进度0：未进场；1：待业主支付；2：待交底；3：施工中；4：阶段完工；5：收尾施工；6：整体完工
                            }else if(houseFlowApp!=null&&houseFlowApp.getApplyType()==2){
                                wfr.setButtonTitle("整体完工申请");//按钮提示
                                wfr.setState(6);//装修进度0：未进场；1：待业主支付；2：待交底；3：施工中；4：阶段完工；5：收尾施工；6：整体完工
                                prompt="我是"+(workerType==null?"":workerType.getName())+"工"+(worker2==null?"":worker2.getName()+",我提交了整体完工申请");//消息提示
                                promptList.add(prompt);
                            }else if(hfl.getWorkType()<2){//未发布工种抢单
                                wfr.setButtonTitle("提前进场");//按钮提示
                                wfr.setState(0);//装修进度0：未进场；1：待业主支付；2：待交底；3：施工中；4：阶段完工；5：收尾施工；6：整体完工
                            }else if(hfl.getWorkType()<4){//待抢单和已抢单
                                wfr.setButtonTitle("正在进场");//按钮提示
                                wfr.setState(1);//装修进度0：未进场；1：待业主支付；2：待交底；3：施工中；4：阶段完工；5：收尾施工；6：整体完工
                            }else if(hfl.getWorkType()==3){
                                wfr.setButtonTitle("去交底");//按钮提示
                                wfr.setState(2);//装修进度0：未进场；1：待业主支付；2：待交底；3：施工中；4：阶段完工；5：收尾施工；6：整体完工
                            }else if((hfl.getWorkType()==4&&hfl.getWorkSteta()==0)||hfl.getWorkSteta()==4){
                                wfr.setButtonTitle("施工中");//按钮提示
                                wfr.setState(3);//装修进度0：未进场；1：待业主支付；2：待交底；3：施工中；4：阶段完工；5：收尾施工；6：整体完工
                            }else if(hfl.getWorkSteta()==1){
                                wfr.setButtonTitle("已阶段完工");//按钮提示
                                wfr.setState(4);//装修进度0：未进场；1：待业主支付；2：待交底；3：施工中；4：阶段完工；5：收尾施工；6：整体完工
                            }else if(hfl.getWorkSteta()==5){
                                wfr.setButtonTitle("收尾施工中");//按钮提示
                                wfr.setState(5);//装修进度0：未进场；1：待业主支付；2：待交底；3：施工中；4：阶段完工；5：收尾施工；6：整体完工
                            }else if(hfl.getWorkSteta()==2){
                                wfr.setButtonTitle("已整体完工");//按钮提示
                                wfr.setState(6);//装修进度0：未进场；1：待业主支付；2：待交底；3：施工中；4：阶段完工；5：收尾施工；6：整体完工
                            }

                            if(houseFlowApp!=null&&houseFlowApp.getApplyType()==3){
                                wfr.setButtonTitle("停工申请");//按钮提示
                                prompt="我是"+(workerType==null?"":workerType.getName())+"工"+(worker2==null?"":worker2.getName()+",我提交了停工申请");//消息提示
                                promptList.add(prompt);
                            }

                            if(hfl.getPause()==1){
                                wfr.setButtonTitle("已停工");//按钮提示
                            }
                            listMap4.add(wfr);
                        }
                    }
                }
                if(c!=0){
                    returnMap.put("houseIsStart", "今日已开工");//今日是否有开工记录
                }else{
                    returnMap.put("houseIsStart", "今日未开工");//
                }
                int count=0;
                for(HouseWorker houseWorker:houseList){//循环所有订单任务
                    List<HouseFlowApply> supervisorCheckList =houseFlowApplyMapper.getSupervisorCheckList(houseWorker.getHouseId());//查询所有待大管家审核
                    count+=supervisorCheckList.size();
                }
                returnMap.put("taskNumber", count);//总任务数量
                returnMap.put("wokerFlowList", listMap4);//添加大进程list
                returnMap.put("ifBackOut", 1);//大管家是否可放弃，0:可放弃；1:不可放弃
                returnMap.put("ifDisclose", 0);//ios支付前放弃(支付后放弃为1)
                //消息提示list
                if(hf!=null&&hf.getWorkType()==3){//如果是已抢单待支付。则提醒业主支付
                    returnMap.put("ifBackOut", 0);
                    String prompt="请联系业主支付您的大管家费用";
                    promptList.add(prompt);
                }
                //消息提示list
                returnMap.put("promptList", promptList);
                List<Map<String, Object>> buttonList=new ArrayList<Map<String, Object>>();//按钮list
                //查询是否全部整体完工
                List<HouseFlow> checkFinish = houseFlowMapper.checkAllFinish(hf.getHouseId(),hf.getId());
                //查询是否今天已经上传过巡查
                List<HouseFlowApply> hfalistApplies = houseFlowApplyMapper.getTodayHouseFlowApplyBy56(hf.getHouseId());
                if(hf.getSupervisorStart()==0){//已开工之后都是巡查工地；1：巡查工地2：申请业主验收；3:确认开工
                    List<HouseFlow> listStart=houseFlowMapper.getHouseIsStart(hf.getHouseId());
                    if(listStart.size()>0){
                        hf.setSupervisorStart(1);//改为开工状态(兼容老数据)
                        houseFlowMapper.updateByPrimaryKeySelective(hf);
                        Map<String, Object> map=new HashMap<String, Object>();
                        map.put("buttonType", "1");
                        map.put("buttonTypeName", "巡查工地");
                        buttonList.add(map);
                        returnMap.put("buttonList", buttonList);
                        returnMap.put("ifBackOut", 1);//大管家是否可放弃，0:可放弃；1:不可放弃
                    }else{
                        if(hf!=null&&hf.getWorkType()==4){//支付之后显示按钮
                            Map<String, Object> map=new HashMap<String, Object>();
                            map.put("buttonType", "3");
                            map.put("buttonTypeName", "确认开工");
                            buttonList.add(map);
                            returnMap.put("buttonList", buttonList);
                            returnMap.put("ifBackOut", 1);//大管家是否可放弃，0:可放弃；1:不可放弃
                        }
                    }
                }else if(checkFinish == null || checkFinish.size() == 0){//所有工种都整体完工，申请业主验收
                    HouseFlowApply houseFlowApp=houseFlowApplyMapper.checkSupervisorApply(hf.getId(),worker.getId());//查询大管家是否有验收申请
                    if(houseFlowApp==null){//没有发验收申请
                        Map<String, Object> map=new HashMap<String, Object>();
                        map.put("buttonType", "2");
                        map.put("buttonTypeName", "申请业主验收");
                        buttonList.add(map);
                        returnMap.put("buttonList", buttonList);
                    }else{
                        String prompt="您已提交业主验收申请，请耐心等待业主审核！";
                        promptList.add(prompt);
                        //消息提示list
                        returnMap.put("promptList", promptList);
                    }
                }else if(hfalistApplies != null && hfalistApplies.size() != 0){//今日已提交过巡查
                    List<HouseFlowApply> hfalistApp7 = houseFlowApplyMapper.getTodayHouseFlowApply(hf.getId(),7,worker.getId());
                    if(hfalistApp7==null||hfalistApp7.size()==0){
                        Map<String, Object> map=new HashMap<String, Object>();
                        map.put("buttonType", "4");
                        map.put("buttonTypeName", "追加巡查");
                        buttonList.add(map);
                        returnMap.put("buttonList", buttonList);
                    }else{
                        String prompt="今日已巡查";
                        promptList.add(prompt);
                        returnMap.put("promptList", promptList);
                    }
                }else{//巡查工地
                    Map<String, Object> map=new HashMap<String, Object>();
                    map.put("buttonType", "1");
                    map.put("buttonTypeName", "巡查工地");
                    buttonList.add(map);
                    returnMap.put("buttonList", buttonList);
                    returnMap.put("ifBackOut", 1);//大管家是否可放弃，0:可放弃；1:不可放弃
                }
                //工匠
            }else if(worker!=null&&(worker.getWorkerType()!=1||worker.getWorkerType()!=2|worker.getWorkerType()!=3)){
                //工匠个人统计信息
                returnMap.put("workerType", 1);//工匠
                returnMap.put("houseFlowId", hf.getId());//houseFlowId
                List<HouseFlowApply> earliestTimeList=houseFlowApplyMapper.getEarliestTimeHouseApply(house.getId(),worker.getId());
                HouseFlowApply earliestTime=null;
                if (earliestTimeList.size()>0){
                    earliestTime=earliestTimeList.get(1);
                }
                HouseFlowApply checkFlowApp=houseFlowApplyMapper.checkHouseFlowApply(hf.getId(),worker.getId());//根据工种任务id和工人id查询此工人待审核
                Long suspendDay=houseFlowApplyMapper.getSuspendApply(house.getId(),worker.getId());//根据房子id和工人id查询暂停天数
                Long everyEndDay=houseFlowApplyMapper.getEveryDayApply(house.getId(),worker.getId());//根据房子id和工人id查询每日完工申请天数
                if(earliestTime!=null){
                    Date EarliestDay= earliestTime.getCreateDate();//最早开工时间
                    Date newDate=new Date();
                    int a = daysOfTwo(EarliestDay,newDate);//计算当前时间隔最早开工时间相差多少天
                    if(suspendDay==null){
                        returnMap.put("totalDay", "总开工天数"+(a-0));//总开工天数
                    }else{
                        long aa= a-suspendDay;
                        if(aa>=0){
                            returnMap.put("totalDay", "总开工天数"+aa);//总开工天数
                        }else{
                            returnMap.put("totalDay", "总开工天数0");//总开工天数
                        }
                    }
                }else{
                    returnMap.put("totalDay","总开工天数0");//总开工天数
                }
                returnMap.put("everyDay", "每日完工天数"+(everyEndDay==null?"0":everyEndDay));//每日完工天数
                returnMap.put("suspendDay", "暂停天数"+(suspendDay==null?"0":suspendDay));//暂停天数
                HouseWorkerOrder houseWorkerOrder=houseWorkerOrderMapper.getHouseWorkerOrder(hf.getId(), worker.getId());
                if(houseWorkerOrder==null){
                    returnMap.put("alreadyMoney", 0);//已得钱
                    returnMap.put("alsoMoney", 0);//还可得钱
                }else{
                    BigDecimal alsoMoney =(houseWorkerOrder.getWorkPrice()==null?new BigDecimal(0):houseWorkerOrder.getWorkPrice()).subtract(houseWorkerOrder.getHaveMoney()==null?new BigDecimal(0):houseWorkerOrder.getHaveMoney());//还可得钱
                    returnMap.put("alreadyMoney",(houseWorkerOrder.getHaveMoney().setScale(2, BigDecimal.ROUND_HALF_UP)).toString());//已得钱
                    returnMap.put("alsoMoney", (alsoMoney.setScale(2, BigDecimal.ROUND_HALF_UP)).toString());//还可得钱
                }
                if(hw!=null&&hw.getWorkType()==1){
                    returnMap.put("ifDisclose", 0);//0:未支付；1：未交底；2:已交底
                }else if(hw!=null&&hw.getWorkSteta()==3){
                    returnMap.put("ifDisclose", 1);//0:未支付；1：未交底；2:已交底
                }else{
                    returnMap.put("ifDisclose", 2);//0:未支付；1：未交底；2:已交底
                }
                //房产信息
                returnMap.put("houseName", (house.getResidential()==null?"*":house.getResidential())
                        +(house.getBuilding()==null?"*":house.getBuilding())+"栋"
                        +(house.getUnit()==null?"*":house.getUnit())+"单元"+(house.getNumber()==null?"*":house.getNumber())+"号");
                HouseWorker supervisorWorker=houseWorkerMapper.getHwByHidAndWtype(hf.getHouseId(), 3);//查询大管家的
                Member wokerSup=memberMapper.selectByPrimaryKey(supervisorWorker.getWorkerId());//查询大管家
                returnMap.put("supervisorName", wokerSup==null?"无":wokerSup.getName());//大管家名字
                returnMap.put("supervisorPhone", wokerSup==null?"无":wokerSup.getMobile());//大管家电话
                returnMap.put("supervisorEvation", "积分 "+(wokerSup==null?"0.00":wokerSup.getEvaluationScore()));//大管家积分
                Long supervisorCountOrder=houseWorkerMapper.getCountOrderByWorkerId(wokerSup.getId());
                returnMap.put("supervisorCountOrder", "总单数 "+(supervisorCountOrder==null?"0":supervisorCountOrder));//大管家总单数
                returnMap.put("supervisorPraiseRate", "好评率 "+(wokerSup.getPraiseRate().multiply(new BigDecimal(100))+"%"));//大管家好评率

                List<Map<String, Object>> returnlistMap=new ArrayList<Map<String,Object>>();
                //工程资料
                List<Map<String, Object>> listMap=new ArrayList<Map<String,Object>>();
                Map<String, Object> map1=new HashMap<String, Object>();
                map1.put("name", "施工图");
                map1.put("type", 0);
                map1.put("image", address+"/images/gongjiang/artisan_25.png");
                map1.put("url", StringTool.getUrl(request) + "/worker/worker!drawings.action?houseid="+house.getId()+"&title施工图title");
                Map<String, Object> map2=new HashMap<String, Object>();
                map2.put("name", "精算");
                map2.put("type", 0);
                map2.put("image", address+"/images/gongjiang/artisan_26.png");
                map2.put("url", StringTool.getUrl(request) + "/worker/worker!budget.action?houseid="+house.getId()+"&title精算title");
                Map<String, Object> map3=new HashMap<String, Object>();
                map3.put("name", "工艺标准");
                map3.put("type", 0);
                map3.put("image", address+"/images/gongjiang/artisan_27.png");
                map3.put("url", StringTool.getUrl(request) + "/worker/worker!technological.action?houseid="+house.getId()+"&title工艺标准title");
                Map<String, Object> map4=new HashMap<String, Object>();
                map4.put("name", "验收标准");
                map4.put("type", 0);
                map4.put("image", address+"/images/gongjiang/artisan_28.png");
                map4.put("url", StringTool.getUrl(request) + "/worker/worker!standard.action?houseid="+house.getId()+"&title验收标准title");
                Map<String, Object> map12=new HashMap<String, Object>();
                map12.put("name", "补人工记录");
                map12.put("type", 0);
                map12.put("image", address+"/images/gongjiang/artisan_31.png");
                map12.put("url", StringTool.getUrl(request) + "/worker/supervisor!deliverWorkerGoods2.action?houseid="+house.getId()+"&houseFlowId="+hf.getId()+"&title补人工记录title");
                listMap.add(map1);
                listMap.add(map2);
                listMap.add(map3);
                listMap.add(map4);
                listMap.add(map12);
                Map<String, Object> mapGon=new HashMap<String, Object>();
                mapGon.put("listMap", listMap);
                mapGon.put("name", "工程资料");
                returnlistMap.add(mapGon);//工程资料

                //其他
                List<Map<String, Object>> listMap3=new ArrayList<Map<String,Object>>();
                Map<String, Object> map9=new HashMap<String, Object>();
                map9.put("name", "工地记录");
                map9.put("type",0);
                map9.put("image", address+"/images/gongjiang/artisan_29.png");
                map9.put("url", StringTool.getUrl(request) + "/app/modeling_reference_effect!checklist.action?houseid="+house.getId()+"&title工地记录title");
                Map<String, Object> map10=new HashMap<String, Object>();
                map10.put("name", "通讯录");
                map10.put("type", 0);
                map10.put("image",  address+"/images/gongjiang/artisan_30.png");
                map10.put("url", StringTool.getUrl(request) + "/worker/worker!phonelist.action?houseFlowId="+hf.getId()+"&workerId="+worker.getId()+"&title通讯录title");
                if(hf.getWorkType()==4){//业主已支付
                    Map<String, Object> map11=new HashMap<String, Object>();
                    map11.put("name", "生成二维码");
                    map11.put("type", 1);
                    map11.put("image",  address+"/images/gongjiang/artisan_42.png");
                    map11.put("url",StringTool.getUrl(request) +  "/app/verification!generateQrcode.action?houseFlowid="+hf.getId()+"&title二维码title");
                    listMap3.add(map11);
                }
                listMap3.add(map9);
                listMap3.add(map10);
                Map<String, Object> mapQi=new HashMap<String, Object>();
                mapQi.put("listMap", listMap3);
                mapQi.put("name", "其他");
                returnlistMap.add(mapQi);//工程资料
                returnMap.put("bigList", returnlistMap);
                List<String> promptList=new ArrayList<String>();//消息提示list
                if(hf!=null&&hf.getWorkType()==3){//如果是已抢单待支付。则提醒业主支付
                    String prompt="请联系业主支付您的工匠费用";
                    promptList.add(prompt);
                    returnMap.put("ifBackOut", 0);//0：可放弃；1：申请停工；2：已停工
                }else if(hf.getPause()==1){
                    returnMap.put("ifBackOut", 2);
                    String prompt="您已停工！";
                    promptList.add(prompt);
                }else if(hf.getWorkSteta()==1){
                    returnMap.put("ifBackOut", 2);
                    String prompt="您已阶段完工！";
                    promptList.add(prompt);
                }else if(hf.getWorkSteta()==2){
                    returnMap.put("ifBackOut", 2);
                    String prompt="您已整体完工！";
                    promptList.add(prompt);
                }else{
                    returnMap.put("ifBackOut", 1);//可申请停工
                }
                List<Map<String, Object>> buttonList=new ArrayList<Map<String, Object>>();//按钮list
                if(hf!=null&&hf.getWorkType()<4){//待支付,不显示按钮

                }else if(hf!=null&&hf.getWorkSteta()==3){//待交底
                    Map<String, Object> map=new HashMap<String, Object>();
                    map.put("buttonType", "1");//按钮提示 1:找大管家交底2:今日开工;3：今日完工;4阶段完工；5整体完工
                    map.put("buttonTypeName", "找大管家交底");
                    returnMap.put("ifBackOut", 1);//0：可放弃；1：申请停工；2：已停工
                    buttonList.add(map);
                }else if(worker.getWorkerType()==4){//如果是拆除，只有整体完工
                    if(checkFlowApp!=null&&checkFlowApp.getApplyType()==2&&checkFlowApp.getSupervisorCheck()==0){
                        String prompt="已申请整体完工";
                        promptList.add(prompt);
                    }else if(checkFlowApp!=null&&checkFlowApp.getApplyType()==2&&checkFlowApp.getSupervisorCheck()==1){
                        String prompt="大管家已审核您的整体完工,待业主审核";
                        promptList.add(prompt);
                    }else if(checkFlowApp!=null&&checkFlowApp.getApplyType()==2&&checkFlowApp.getSupervisorCheck()==2){
                        String prompt="大管家审核不通过,请重新提交整体完工申请";
                        promptList.add(prompt);
                        Map<String, Object> mapBu2=new HashMap<String, Object>();
                        mapBu2.put("buttonType", "5");//按钮提示 1:找大管家交底2:今日开工;3：今日完工;4阶段完工；5整体完工
                        mapBu2.put("buttonTypeName", "申请整体完工");
                        buttonList.add(mapBu2);
                    }else if(hf!=null&&hf.getWorkerType()!=2){
                        Map<String, Object> mapBu2=new HashMap<String, Object>();
                        mapBu2.put("buttonType", "5");//按钮提示 1:找大管家交底2:今日开工;3：今日完工;4阶段完工；5整体完工
                        mapBu2.put("buttonTypeName", "申请整体完工");
                        buttonList.add(mapBu2);
                    }
                }else{//已交底
                    returnMap.put("footMessageTitle", "");//每日开工事项
                    returnMap.put("footMessageDescribe", "");//每日开工事项
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
                    String startTime=sdf.format(new Date());
                    String endTime=sdf2.format(new Date());
                    HouseFlowApply todayStart=houseFlowApplyMapper.getTodayStart(house.getId(),worker.getId());//查询今日开工记录
                    List<Map<String, Object>> listDay=new ArrayList<Map<String,Object>>();
                    if(todayStart==null){//没有今日开工记录
                        Map<String, Object> map=new HashMap<String, Object>();
                        map.put("buttonType", "2");//按钮提示 1:找大管家交底2:今日开工;3：今日完工;4阶段完工；5整体完工
                        map.put("buttonTypeName", "今日开工");
                        buttonList.add(map);
                        List<WorkerEveryday> listWorDay=workerEverydayMapper.getWorkerEverydayList(1);//事项类型  1 开工事项 2 完工事项
                        for(WorkerEveryday day:listWorDay){
                            Map<String, Object> dayMap=new HashMap<String, Object>();
                            dayMap.put("name", day.getName());
                            listDay.add(dayMap);
                        }
                        returnMap.put("footMessageTitle", "今日开工任务");//每日开工事项
                        returnMap.put("footMessageDescribe", "（每日十二点前今日开工）");//每日开工事项
                    }else{
                        List<HouseFlowApply> flowAppList=houseFlowApplyMapper.getTodayHouseFlowApply(hf.getId(), 0,  worker.getId());//查询是否已提交今日完工
                        if(flowAppList!=null&&flowAppList.size()>0){//已提交今日完工
                            String prompt="今日已完工";
                            promptList.add(prompt);
                            returnMap.put("ifBackOut", 1);//可申请停工
                        }else{
                            Map<String, Object> mapBu=new HashMap<String, Object>();
                            mapBu.put("buttonType", "3");//按钮提示 1:找大管家交底2:今日开工;3：今日完工;4阶段完工；5整体完工
                            mapBu.put("buttonTypeName", "今日完工");
                            buttonList.add(mapBu);
                            returnMap.put("ifBackOut", 1);//可申请停工
                            List<WorkerEveryday> listWorDay=workerEverydayMapper.getWorkerEverydayList(2);//事项类型  1 开工事项 2 完工事项
                            for(WorkerEveryday day:listWorDay){
                                Map<String, Object> dayMap=new HashMap<String, Object>();
                                dayMap.put("name", day.getName());
                                listDay.add(dayMap);
                            }
                            returnMap.put("footMessageTitle", "今日完工任务");//每日开工事项
                            returnMap.put("footMessageDescribe", "");//每日开工事项
                        }

                        if(hf.getWorkSteta()==1){
                            if(checkFlowApp!=null&&checkFlowApp.getApplyType()==2&&checkFlowApp.getSupervisorCheck()==0){
                                String prompt="已申请整体完工";
                                promptList.add(prompt);
                            }else if(checkFlowApp!=null&&checkFlowApp.getApplyType()==2&&checkFlowApp.getSupervisorCheck()==1){
                                String prompt="大管家已审核您的整体完工,待业主审核";
                                promptList.add(prompt);
                            }else if(checkFlowApp!=null&&checkFlowApp.getApplyType()==2&&checkFlowApp.getSupervisorCheck()==2){
                                String prompt="大管家审核不通过,请重新提交整体完工申请";
                                promptList.add(prompt);
                                Map<String, Object> mapBu2=new HashMap<String, Object>();
                                mapBu2.put("buttonType", "5");//按钮提示 1:找大管家交底2:今日开工;3：今日完工;4阶段完工；5整体完工
                                mapBu2.put("buttonTypeName", "申请整体完工");
                                buttonList.add(mapBu2);
                            }else if(hf!=null&&hf.getWorkerType()!=2){
                                Map<String, Object> mapBu2=new HashMap<String, Object>();
                                mapBu2.put("buttonType", "5");//按钮提示 1:找大管家交底2:今日开工;3：今日完工;4阶段完工；5整体完工
                                mapBu2.put("buttonTypeName", "申请整体完工");
                                buttonList.add(mapBu2);
                            }
                        }else{
                            if(checkFlowApp!=null&&checkFlowApp.getApplyType()==1&&checkFlowApp.getSupervisorCheck()==0){
                                String prompt="已申请阶段完工";
                                promptList.add(prompt);
                            }else if(checkFlowApp!=null&&checkFlowApp.getApplyType()==1&&checkFlowApp.getSupervisorCheck()==1){
                                String prompt="大管家已审核您的阶段完工,待业主审核";
                                promptList.add(prompt);
                            }else if(checkFlowApp!=null&&checkFlowApp.getApplyType()==1&&checkFlowApp.getSupervisorCheck()==2){
                                String prompt="大管家审核不通过,请重新提交阶段完工申请";
                                promptList.add(prompt);
                                Map<String, Object> mapBu2=new HashMap<String, Object>();
                                mapBu2.put("buttonType", "4");//按钮提示 1:找大管家交底2:今日开工;3：今日完工;4阶段完工；5整体完工
                                mapBu2.put("buttonTypeName", "申请阶段完工");
                                buttonList.add(mapBu2);
                            }else if(hf!=null&&hf.getWorkerType()!=1){
                                Map<String, Object> mapBu2=new HashMap<String, Object>();
                                mapBu2.put("buttonType", "4");//按钮提示 1:找大管家交底2:今日开工;3：今日完工;4阶段完工；5整体完工
                                mapBu2.put("buttonTypeName", "申请阶段完工");
                                buttonList.add(mapBu2);
                            }
                        }
                    }
                    returnMap.put("workerEverydayList", listDay);//每日完工事项
                }
                //按钮list
                returnMap.put("buttonList", buttonList);
                //消息提示list
                returnMap.put("promptList", promptList);
            }else{
                return ServerResponse.createByErrorMessage("您的工匠信息不存在,请核对信息后重试！");
            }
            return ServerResponse.createBySuccess("获取施工列表成功！", returnMap);
        }catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询出错！");
        }
    }
    //根据开始时间和结束时间对比相差多少天
    public static int daysOfTwo(Date fDate, Date oDate) {

        Calendar aCalendar = Calendar.getInstance();

        aCalendar.setTime(fDate);

        int day1 = aCalendar.get(Calendar.DAY_OF_YEAR);

        aCalendar.setTime(oDate);

        int day2 = aCalendar.get(Calendar.DAY_OF_YEAR);

        return day2 - day1;
    }

    /**
     * 获取我的界面
     * @return
     */
    public ServerResponse getMyHomePage(String userToken){
        try{
            HttpServletRequest request = ((ServletRequestAttributes )RequestContextHolder.getRequestAttributes()).getRequest();
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class) + configUtil.getValue(SysConfig.PUBLIC_TEMPORARY_FILE_ADDRESS, String.class);
            AccessToken accessToken = redisClient.getCache(userToken+ Constants.SESSIONUSERID,AccessToken.class);
            Member worker=accessToken.getMember();
            if(worker!=null){
                Map<String, Object> returnMap=new HashMap<String, Object>();
                returnMap.put("workerId", worker.getId());//工匠id
                returnMap.put("ioflow", worker.getHead()==null?"":address+worker.getHead());//头像
                returnMap.put("workerName", worker.getName()==null?"":worker.getName());//工匠姓名
                returnMap.put("evaluation", worker.getEvaluationScore()==null?0.00:worker.getEvaluationScore());//工匠积分
                returnMap.put("favorable", worker.getPraiseRate()==null?"0.00%":worker.getPraiseRate().multiply(new BigDecimal(100))+"%");//工匠好评率
                if(worker.getWorkerType()==3){//大管家
                    if(worker.getIsCrowned()==1){
                        returnMap.put("gradeName", "皇冠大管家");//工匠评级昵称
                    }else if(Double.parseDouble(worker.getEvaluationScore().toString())>90){
                        returnMap.put("gradeName", "金牌大管家");//工匠评级昵称
                    }else if(Double.parseDouble(worker.getEvaluationScore().toString())>80){
                        returnMap.put("gradeName", "银牌大管家");//工匠评级昵称
                    }else if(Double.parseDouble(worker.getEvaluationScore().toString())>70){
                        returnMap.put("gradeName", "铜牌大管家");//工匠评级昵称
                    }else{
                        returnMap.put("gradeName", "普通大管家");//工匠评级昵称
                    }
                }else if(worker.getWorkerType()>3){//普通工匠
                    if(worker.getIsCrowned()==1){
                        returnMap.put("gradeName", "皇冠工匠");//工匠评级昵称
                    }else if(Double.parseDouble(worker.getEvaluationScore().toString())>90){
                        returnMap.put("gradeName", "金牌工匠");//工匠评级昵称
                    }else if(Double.parseDouble(worker.getEvaluationScore().toString())>80){
                        returnMap.put("gradeName", "银牌工匠");//工匠评级昵称
                    }else if(Double.parseDouble(worker.getEvaluationScore().toString())>70){
                        returnMap.put("gradeName", "铜牌工匠");//工匠评级昵称
                    }else{
                        returnMap.put("gradeName", "普通工匠");//工匠评级昵称
                    }
                }
                List<Map<String, Object>> listMap=new ArrayList<Map<String,Object>>();
                //工艺要求
                Map<String, Object> map1=new HashMap<String, Object>();
                map1.put("name", "工艺要求");
                map1.put("url", StringTool.getUrl(request) +"/worker/worker_personal!craftList.action?title工艺要求title");
                map1.put("type", "0");
                map1.put("imageUrl", address+"/images/gongjiang/artisan_35.png");
                listMap.add(map1);
                //工匠报价
                Map<String, Object> map2=new HashMap<String, Object>();
                map2.put("name", "工匠报价");
                map2.put("url",StringTool.getUrl(request) +"/worker/worker_goods_price!list.action?title工匠报价title");
                map2.put("type", "0");
                map2.put("imageUrl", address+"/images/gongjiang/artisan_36.png");
                listMap.add(map2);
                //完善资料
                Map<String, Object> map3=new HashMap<String, Object>();
                map3.put("name", "我的资料");
                map3.put("url", StringTool.getUrl(request) +"/worker/worker_personal!perfect.action?worker.id="+worker.getId()+"&title我的资料title");
                map3.put("type", "1");
                map3.put("imageUrl", address+"/images/gongjiang/artisan_37.png");
                listMap.add(map3);
                //提现记录
                Map<String, Object> map4=new HashMap<String, Object>();
                map4.put("name", "提现记录");
                map4.put("url", StringTool.getUrl(request) +"/worker/withdraw_deposit!listmoney.action?worker.id="+worker.getId()+"&title提现记录title");
                map4.put("type", "0");
                map4.put("imageUrl", address+"/images/gongjiang/artisan_39.png");
                listMap.add(map4);
                //我的任务
                Map<String, Object> map5=new HashMap<String, Object>();
                map5.put("name", "我的任务");
                map5.put("url", StringTool.getUrl(request) +"/worker/withdraw_deposit!list.action?worker.id="+worker.getId()+"&title我的任务title");
                map5.put("type", "0");
                map5.put("imageUrl", address+"/images/gongjiang/artisan_40.png");
                listMap.add(map5);
                //我的银行卡
                Map<String, Object> map6=new HashMap<String, Object>();
                map6.put("name", "我的银行卡");
                map6.put("url", StringTool.getUrl(request) +"/worker/withdraw_deposit!mybankcard.action?worker.id="+worker.getId()+"&title我的银行卡title");
                map6.put("type", "0");
                map6.put("imageUrl", address+"/images/gongjiang/artisan_41.png");
                listMap.add(map6);
                //二维码
                Map<String, Object> map7=new HashMap<String, Object>();
                map7.put("name", "二维码");
                map7.put("url", StringTool.getUrl(request) +"/worker/worker_personal!invite.action?worker.id="+worker.getId()+"&title二维码title");
                map7.put("type", "0");
                map7.put("imageUrl", address+"/images/gongjiang/artisan_42.png");
                listMap.add(map7);
                //邀请排行榜
                Map<String, Object> map8=new HashMap<String, Object>();
                map8.put("name", "邀请排行榜");
                map8.put("url", StringTool.getUrl(request) +"/worker/worker_personal!ranking.action?worker.id="+worker.getId()+"&title邀请排行榜title");
                map8.put("type", "0");
                map8.put("imageUrl", address+"/images/gongjiang/artisan_43.png");
                listMap.add(map8);
                //关于我们
                Map<String, Object> map9=new HashMap<String, Object>();
                map9.put("name", "关于我们");
                map9.put("url", StringTool.getUrl(request) +"/worker/worker_personal!about.action?worker.id="+worker.getId()+"&title关于我们title");
                map9.put("type", "0");
                map9.put("imageUrl",address+"/images/gongjiang/artisan_44.png");
                listMap.add(map9);
                //奖罚记录
                Map<String, Object> map10=new HashMap<String, Object>();
                map10.put("name", "奖罚记录");
                map10.put("url", StringTool.getUrl(request) +"/worker/worker_personal!reward.action?worker.id="+worker.getId()+"&title奖罚记录title");
                map10.put("type", "0");
                map10.put("imageUrl", address+"/images/gongjiang/artisan_69.png");
                listMap.add(map10);
                //帮助中心
                Map<String, Object> map11=new HashMap<String, Object>();
                map11.put("name", "帮助中心");
                map11.put("url", StringTool.getUrl(request) +"/worker/worker_personal!help.action?title帮助中心title");
                map11.put("type", "0");
                map11.put("imageUrl", address+"/images/gongjiang/artisan_60.png");
                listMap.add(map11);
                //接单记录
                Map<String, Object> map12=new HashMap<String, Object>();
                map12.put("name", "接单记录");
                map12.put("url", StringTool.getUrl(request) +"/worker/worker_personal!takeOrder.action?worker.id="+worker.getId()+"&title接单记录title");
                map12.put("type", "0");
                map12.put("imageUrl", address+"/images/gongjiang/artisan_61.png");
                listMap.add(map12);
                returnMap.put("list", listMap);
                return ServerResponse.createBySuccess("获取我的界面成功！", returnMap);
            }else{
                return ServerResponse.createByErrorCodeMessage(EventStatus.NO_DATA.getCode(),"查不到此工匠的信息！");
            }
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorCodeMessage(EventStatus.ERROR.getCode(),"获取我的界面信息失败！");
        }
    }

    /**
     * 提现列表
     * @param userToken
     * @return
     */
    public ServerResponse getExtractMoney(String userToken){
        try{
            AccessToken accessToken = redisClient.getCache(userToken+ Constants.SESSIONUSERID,AccessToken.class);
            Member worker=accessToken.getMember();
            Map<String, Object> returnMap=new HashMap<String, Object>();
            BigDecimal surplusMoney=worker.getSurplusMoney();
            BigDecimal surplusMoney2=new BigDecimal(0);
            if(surplusMoney==null){
                returnMap.put("surplusprice","0.00");
            }else if((surplusMoney.compareTo(surplusMoney2))==-1){
                returnMap.put("surplusprice","0.00");
            }else{
                returnMap.put("surplusprice",surplusMoney);
            }
            returnMap.put("retentionmoney", worker.getRetentionMoney()==null?"0.00":worker.getRetentionMoney());//滞留金
            Double allmoney = workerDetailMapper.getCountWorkerDetailByWid(worker.getId());
            returnMap.put("havemaoney", allmoney);//总金额
            Long countFinishOrder=houseWorkerMapper.getCountOrderByWorkerId(worker.getId());//查询个人所有已经完工的单
            returnMap.put("countOrder", countFinishOrder);//总单数
            /**************查询有记录的历史月****************/
            List<Map<String, Object>> monthBillList=new ArrayList<Map<String,Object>>();//月流水list
            List<String> monthList=workerDetailMapper.getHistoryMonth(worker.getId());//有流水月的list
            for(String month :monthList){//遍历有数据的月份,根据月份查询对应的流水
                Map<String, Object> monthMap=new HashMap<String, Object>();
                List<WorkerDetail> wdlist =workerDetailMapper.getHistoryMonthByWorkerId(worker.getId(),month);
                List<Map<String, Object>> thisMonList=new ArrayList<Map<String,Object>>();//本月流水
                Double allMoney=0.00;
                for(WorkerDetail wd:wdlist){
                    if("提取现金".equals(wd.getName())){//提取现金的记录不显示
                        continue;
                    }
                    Map<String, Object> wdMap=new HashMap<String, Object>();
                    wdMap.put("id", wd.getId());//流水详情id
                    if(wd.getId()!=null){
                        House house=houseMapper.selectByPrimaryKey(wd.getHouseId());
                        if(house!=null){
                            wdMap.put("name", (house.getResidential()==null?"*":house.getResidential())
                                    +(house.getBuilding()==null?"*":house.getBuilding())+"栋"
                                    +(house.getUnit()==null?"*":house.getUnit())+"单元"+(house.getNumber()==null?"*":house.getNumber())+"号");//流水描述
                        }else{
                            wdMap.put("name", "自定义流水");
                        }
                    }else{
                        wdMap.put("name", wd.getName());//流水描述
                    }
                    if(wd.getState()==0||wd.getState()==2){
                        wdMap.put("money", "+"+wd.getMoney());//流水金额(加)
                    }else{
                        wdMap.put("money", "-"+wd.getMoney());//流水金额(减)
                    }
                    allMoney+=Double.parseDouble(wd.getMoney().toString());//累加此月流水
                    thisMonList.add(wdMap);
                }

                monthMap.put("billList", thisMonList);//流水详细List
                monthMap.put("month", month);//流水月份
                monthMap.put("allMoney", allMoney);//此月总流水
                monthMap.put("number", thisMonList.size());//单数
                monthBillList.add(monthMap);
            }
            returnMap.put("monthBillList",monthBillList);
            return ServerResponse.createBySuccess("获取流水信息成功！", returnMap);
        }catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorCodeMessage(EventStatus.ERROR.getCode(),"获取流水信息失败！");
        }
    }

    /**
     * 提现详情
     */
    public ServerResponse getExtractMoneyDetail(String userToken,String workerDetailId){
        try{
            AccessToken accessToken = redisClient.getCache(userToken+ Constants.SESSIONUSERID,AccessToken.class);
            Member worker=accessToken.getMember();
            WorkerDetail workerDetail= workerDetailMapper.selectByPrimaryKey(workerDetailId);//根据流水id查询流水详情
            Map<String, Object> returnMap=new HashMap<String, Object>();
            returnMap.put("id", workerDetail.getId());//id
            returnMap.put("typeName", workerDetail.getName());//流水详情描述
            House house=houseMapper.selectByPrimaryKey(workerDetail.getHouseId());
            String houseName="自定义流水";
            if(house!=null){
                houseName=(house.getResidential()==null?"*":house.getResidential())
                        +(house.getBuilding()==null?"*":house.getBuilding())+"栋"
                        +(house.getUnit()==null?"*":house.getUnit())+"单元"+(house.getNumber()==null?"*":house.getNumber())+"号";//流水描述
            }
            returnMap.put("name", houseName);//流水来源
            if(workerDetail.getState()==0||workerDetail.getState()==2){
                returnMap.put("money", "+"+workerDetail.getMoney());//流水金额(加)
            }else{
                returnMap.put("money", "-"+workerDetail.getMoney());//流水金额(减)
            }
            String dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(workerDetail.getCreateDate());
            returnMap.put("time", dateStr);//流水时间
            return ServerResponse.createBySuccess("获取流水详情成功！", returnMap);
        }catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorCodeMessage(EventStatus.ERROR.getCode(),"获取流水详细失败！");
        }
    }

    /*
     * 提现验证码
     */
    public ServerResponse getPaycode(String userToken, String phone){
        try{
            AccessToken accessToken = redisClient.getCache(userToken+ Constants.SESSIONUSERID,AccessToken.class);
            Member worker=accessToken.getMember();
            int registerCode = (int) (Math.random() * 9000 + 1000);
            JsmsUtil.SMS(registerCode, phone);
            //记录短信发送
            Sms sms=new Sms();
            sms.setCode(String.valueOf(registerCode));
            sms.setMobile(phone);
            smsMapper.insert(sms);
            return ServerResponse.createBySuccessMessage("验证码已发送！");
        }catch(Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorCodeMessage(EventStatus.ERROR.getCode(),"系统出错，获取验证码失败！");
        }
    }

    /*
     * 完成验证提现
     */
    public ServerResponse checkFinish(String userToken, String smscode,String money){
        try {
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            if (StringUtil.isEmpty(money)) {
                return ServerResponse.createByErrorCodeMessage(EventStatus.ERROR.getCode(), "money不能为空，提现失败！");
            }
            Member worker = accessToken.getMember();
            if (worker.getSurplusMoney().compareTo(new BigDecimal(0)) <= 0) {
                return ServerResponse.createByErrorCodeMessage(EventStatus.ERROR.getCode(), "可取余额不足，提现失败！");
            }

            List<WorkerBankCard> wbcList = workerBankCardMapper.getByWorkerid(worker.getId());//查有么有填银行卡
            WorkerBankCard workerBankCard = null;
            if (wbcList.size() == 0) {
                return ServerResponse.createByErrorCodeMessage(EventStatus.ERROR.getCode(), "您还未绑定银行卡，提现失败！");
            } else {
                workerBankCard = wbcList.get(0);
            }
            if (!smscode.equals(worker.getPaycode())) {//验证码错误
                return ServerResponse.createByErrorCodeMessage(EventStatus.ERROR.getCode(), "验证码错误！");
            }
            //生成提现订单
            WithdrawDeposit wd = new WithdrawDeposit();
            wd.setName(worker.getName());
            wd.setWorkerId(worker.getId());
            wd.setWorkerTypeId(worker.getWorkerTypeId());
            wd.setMoney(new BigDecimal(money));
            wd.setBankName(bankCardMapper.selectByPrimaryKey(workerBankCard.getBankCardId()) == null ? "" : bankCardMapper.selectByPrimaryKey(workerBankCard.getBankCardId()).getBankName());
            wd.setCardNumber(workerBankCard.getBankCardNumber());
            wd.setState(0);//未处理
            withdrawDepositMapper.insert(wd);

            //记录流水
            WorkerDetail workerDetail = new WorkerDetail();
            workerDetail.setName("提取现金");
            workerDetail.setWorkerId(worker.getId());
            workerDetail.setWorkerName(worker.getName());
            workerDetail.setMoney(new BigDecimal(money));
            workerDetail.setState(1);//出
            workerDetailMapper.insert(workerDetail);

            worker.setHaveMoney(worker.getHaveMoney().subtract(new BigDecimal(money)));//更新已有钱
            worker.setSurplusMoney(worker.getSurplusMoney().subtract(new BigDecimal(money)));
            worker.setPaycode(0);//验证码置0
            memberMapper.updateByPrimaryKeySelective(worker);
            return ServerResponse.createBySuccessMessage("提现成功！");
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorCodeMessage(EventStatus.ERROR.getCode(),"系统出错，提现失败！");
        }
    }

    /**
     * 提交审核、停工
     * @return
     */
    public ServerResponse setHouseFlowApply(String userToken,Integer applyType,String houseFlowId,Integer  suspendDay,
             String applyDec ,String imageList ,String houseFlowId2){
        AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
        Member worker = accessToken.getMember();

        //暂停施工
        if(applyType!=4){//每日开工不需要验证
            HouseFlow hf = houseFlowMapper.selectByPrimaryKey(houseFlowId);
            House house = houseMapper.selectByPrimaryKey(hf.getHouseId());
            if(hf.getPause() == 1){
                return ServerResponse.createByErrorCodeMessage(EventStatus.ERROR.getCode(),"您已暂停在此户施工,请勿提交申请！");
            }
            if(house.getPause() != null){
                if(house.getPause() == 1){
                    return ServerResponse.createByErrorCodeMessage(EventStatus.ERROR.getCode(),"该房子已暂停施工,请勿提交申请！");
                }
            }
        }
        if(applyType==5){//大管家巡查
            HouseFlow hf2 =houseFlowMapper.selectByPrimaryKey(houseFlowId2);
            return this.setHouseFlowApply(applyType, houseFlowId2, hf2.getWorkerId(), suspendDay, applyDec,
                    imageList);
        }else{
            return this.setHouseFlowApply(applyType, houseFlowId, worker.getId(), suspendDay, applyDec,
                    imageList);
        }
    }

    /**
     *今日开工，今日完工，阶段完工，整体完工，停工申请，巡查,无人巡查
     */
    public ServerResponse setHouseFlowApply(Integer applyType,String houseFlowId,String workerId,Integer suspendDay,String applyDec,
                                    String imageList){
        try{
            HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(houseFlowId);//查询任务
            HouseFlow supervisorHF = houseFlowMapper.getHouseFlowByHidAndWty(houseFlow.getHouseId(),3);//大管家的hf
            //****针对老工地管家兼容巡查拿钱和验收拿钱***//
            if(supervisorHF.getPatrolMoney() == null || supervisorHF.getCheckMoney() == null ||
                    supervisorHF.getPatrolMoney().compareTo(new BigDecimal(0)) == 0
                    || supervisorHF.getCheckMoney().compareTo(new BigDecimal(0)) == 0){
                List<HouseFlow> houseFlowList = houseFlowMapper.getForCheckMoney(houseFlow.getHouseId());
                int check = 0;//累计大管家总巡查次数
                int time = 0;//累计管家总阶段验收和完工验收次数
                for(HouseFlow houseflow : houseFlowList){
                    //查出该工种工钱
                    Double workerTotal=0.0;
                    ServerResponse serverResponse = workerGoodsAPI.getWorkertoCheck(houseFlow.getHouseId(), houseflow.getId());
                    if(serverResponse.isSuccess()) {
                        JSONObject obj = JSONObject.parseObject(serverResponse.getResultObj().toString());
                        workerTotal = obj.getDouble("totalPrice");
                    }
                    int inspectNumber = workerTypeMapper.selectByPrimaryKey(houseflow.getWorkerTypeId()).getInspectNumber();//该工种配置默认巡查次数
                    int thisCheck = workerTotal.intValue()/600;//该工种钱算出来的巡查次数
                    if(thisCheck > inspectNumber){
                        thisCheck = inspectNumber;
                    }
                    houseflow.setPatrol(thisCheck);//保存巡查次数
                    houseFlowMapper.updateByPrimaryKeySelective(houseflow);
                    //累计总巡查
                    check = check + thisCheck;
                    //累计总验收
                    if(houseflow.getWorkerType() == 4){
                        time++;
                    }else{
                        time = time +2;
                    }
                }
                //拿到这个大管家已支付工钱
                BigDecimal moneySup = new BigDecimal(0);
                if(supervisorHF.getWorkPrice().compareTo(new BigDecimal(0)) == 0){
                    ServerResponse serverResponse = workerGoodsAPI.getWorkertoCheck(houseFlow.getHouseId(), supervisorHF.getId());
                    if(serverResponse.isSuccess()) {
                        JSONObject obj = JSONObject.parseObject(serverResponse.getResultObj().toString());
                        moneySup = BigDecimal.valueOf(obj.getDouble("totalPrice"));
                    }
                }else{
                    moneySup = supervisorHF.getWorkPrice();
                }
                //算管家每次巡查钱
                BigDecimal patrolMoney = moneySup.multiply(new BigDecimal(0.2)).divide(new BigDecimal(check),2,BigDecimal.ROUND_HALF_UP);
                //算管家每次验收钱
                BigDecimal checkMoney = moneySup.multiply(new BigDecimal(0.3)).divide(new BigDecimal(time),2,BigDecimal.ROUND_HALF_UP);
                //保存到大管家的houseflow
                supervisorHF.setPatrolMoney(patrolMoney);//巡查钱
                supervisorHF.setCheckMoney(checkMoney);//验收钱
                houseFlowMapper.updateByPrimaryKeySelective(supervisorHF);
            }

            List<HouseFlowApply> todayApply =houseFlowApplyMapper.getTodayHouseFlowApply2(houseFlowId,workerId);
            if(applyType!=5&&todayApply!=null&&todayApply.size()>0){
                return ServerResponse.createByErrorCodeMessage(EventStatus.ERROR.getCode(),"您今日已发过申请,请改天再发申请！");
            }
            //提交申请进行控制
            if(applyType!=0 && applyType!= 4 &&applyType!=5&&houseFlowApplyMapper.checkHouseFlowApply(houseFlowId, workerId) != null){
                //有未审核申请就修改申请
                return ServerResponse.createByErrorCodeMessage(EventStatus.ERROR.getCode(),"您有待审核的申请,请联系业主或大管家审核后再提交！");
            }
            if(applyType!=5&&houseFlowApplyMapper.waitHouseFlowApply(houseFlowId,workerId) != null){
                //监理已审核通过,等待业主审核
                return ServerResponse.createByErrorCodeMessage(EventStatus.ERROR.getCode(),"大管家已审核通过,等待业主审核！");
            }
            List<HouseFlowApply> hfalistApplies = houseFlowApplyMapper.getTodayHouseFlowApply(houseFlowId,applyType,workerId);
            if(hfalistApplies!=null&&hfalistApplies.size() != 0){//没有发过今天的每日开工,每日完工申请,每日巡查，或者没有提交过阶段完工和整体完工申请
                return ServerResponse.createByErrorCodeMessage(EventStatus.ERROR.getCode(),"您已提交过此申请,请勿重复提交！");
            }
            Member worker =memberMapper.selectByPrimaryKey(workerId);//查询对应的工人
            WorkerType workType= workerTypeMapper.selectByPrimaryKey(worker.getWorkerTypeId());//查询工种
            WorkDeposit workDeposit = workDepositMapper.selectAll().get(0);//结算比例表
            Example example = new Example(HouseFlow.class);
            example.createCriteria().andEqualTo("houseFlowId", houseFlowId).andEqualTo("workerId", workerId);
            HouseWorkerOrder hwo = houseWorkerOrderMapper.selectByExample(example).get(0);
            HouseFlowApply hfa =new HouseFlowApply();//发起申请任务
            hfa.setHouseFlowId(houseFlowId);//任务id
            hfa.setHouseWorkerOrderId(hwo==null?"":hwo.getId());//houseworkerorderid
            hfa.setWorkerId(workerId);//工人id
            hfa.setWorkerTypeId(worker.getWorkerTypeId());//工种id
            hfa.setWorkerType(worker.getWorkerType());//工种类型
            hfa.setHouseId(houseFlow.getHouseId());//房子id
            hfa.setMemberId(houseFlow.getMemberId());//房主id
            hfa.setApplyType(applyType);//申请类型0每日完工申请，1阶段完工申请，2整体完工申请,3停工申请，4：每日开工,5巡查,6无人巡查
            hfa.setSuspendDay(suspendDay);//申请停工天数
            hfa.setApplyDec(applyDec);//描述
            hfa.setApplyMoney(new BigDecimal(0));//申请得钱
            hfa.setSupervisorMoney(new BigDecimal(0));
            hfa.setOtherMoney(new BigDecimal(0));
            hfa.setMemberCheck(0);//业主审核状态0未审核，1审核通过，2审核不通过，3自动审核
            hfa.setSupervisorCheck(0);//大管家审核状态0未审核，1审核通过，2审核不通过
            hfa.setWorkEvaluat(0);//大管家是否评价0未评价，1已评价
            hfa.setPayState(0);//是否付款

            //********************发申请，计算可得钱和积分等*****************//
            BigDecimal paymoney=new BigDecimal(0);
            BigDecimal havemaoney=new BigDecimal(0);
            BigDecimal everydaypaymaoney=new BigDecimal(0);
            if(hwo != null){
                paymoney=hwo.getWorkPrice();
                havemaoney=hwo.getHaveMoney();
                everydaypaymaoney=hwo.getEveryMoney();
            }
            BigDecimal limitpay = paymoney.multiply(workDeposit.getLimitPay());//每日完工得到钱的上限
            System.out.println("每日完工得到钱的上限："+limitpay);
            System.out.println("每日完工已经拿到的钱："+everydaypaymaoney);
            //***每日完工申请***//
            if(applyType==0){//每日完工申请
                if(workDeposit.getEverydayPay().compareTo(limitpay.subtract(havemaoney)) <= 0){//每日上限减已获 大于等于 100
                    hfa.setApplyMoney(workDeposit.getEverydayPay());
                }
                hfa.setOtherMoney((paymoney).subtract(havemaoney).subtract(hfa.getApplyMoney()));
                hfa.setApplyDec("我是"+workType.getName()+",我今天已经完工了");//描述
                houseFlowApplyMapper.insert(hfa);
                //***阶段完工申请***//
            }else if(applyType==1){
                //算阶段完工所有的钱
                hfa.setApplyMoney(paymoney.multiply((workDeposit.getStagePay().add(workDeposit.getLimitPay()))).subtract(havemaoney)); //stagepay阶段完工比例百分比
                //剩下钱
                hfa.setOtherMoney(paymoney.subtract(havemaoney).subtract(hfa.getApplyMoney()));
                hfa.setApplyDec("我是"+workType.getName()+",我已经阶段完工了");//描述
                houseFlowApplyMapper.insert(hfa);
                //***整体完工申请***//
            }else if(applyType==2){
                hfa.setApplyMoney(new BigDecimal(0));
                if(hfa.getWorkerType()==4||hfa.getWorkerType()==5){//如果是拆除跟打孔(直接完工)
                    hfa.setApplyMoney(paymoney);
                }else{
                    hfa.setApplyMoney(paymoney.multiply(workDeposit.getWholePay()));
                    hfa.setOtherMoney(paymoney.subtract(havemaoney).subtract(hfa.getApplyMoney()));
                }
                hfa.setApplyDec("我是"+workType.getName()+",我已经整体完工了");//描述
                houseFlowApplyMapper.insert(hfa);
                //***停工申请***//*
            }else if(applyType==3){
                houseFlow.setPause(1);//0:正常；1暂停；
                houseFlowMapper.updateByPrimaryKeySelective(houseFlow);//发停工申请默认修改施工状态为暂停
                hfa.setMemberCheck(1);//业主审核状态0未审核，1审核通过，2审核不通过，3自动审核
                houseFlowApplyMapper.insert(hfa);
                return ServerResponse.createBySuccessMessage("操作成功");
                //***每日开工申请(不用审核，默认审核通过)***//
            }else if(applyType == 4){
                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat sdf2=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date s=new Date();
                String s2=sdf.format(s)+" 12:00:00";//当天12点
                Date lateDate=sdf2.parse(s2);

                String newDate=sdf2.format(new Date());//当前时间
                Date newDate2=sdf2.parse(newDate);//当前时间
                Long downTime=newDate2.getTime()-lateDate.getTime();//对比12点
                if(downTime>0){
                    return ServerResponse.createByErrorMessage("请在当天12点之前开工,您已超过开工时间！");
                }
                hfa.setApplyDec("我是"+workType.getName()+",我今天已经开工了");//描述
                hfa.setMemberCheck(1);//默认业主审核状态通过
                hfa.setSupervisorCheck(1);//默认大管家审核状态通过
                houseFlow.setPause(0);//0:正常；1暂停；
                houseFlowMapper.updateByPrimaryKeySelective(houseFlow);//发每日开工将暂停状态改为正常
                houseFlowApplyMapper.insert(hfa);
                return ServerResponse.createBySuccessMessage("操作成功！");
            }else if(applyType == 5){//巡查
                hfa.setApplyDec("业主您好,我是"+workType.getName()+",大管家已经巡查了");//描述
                hfa.setMemberCheck(1);//默认业主审核状态通过
                hfa.setSupervisorCheck(1);//默认大管家审核状态通过
                Example example2 = new Example(HouseFlow.class);
                example2.createCriteria().andEqualTo("houseId", houseFlow.getHouseId())
                        .andEqualTo("workerType", worker.getWorkerType()) .andEqualTo("applyType",5);
                List<HouseFlowApply> hfalist = houseFlowApplyMapper.selectByExample(example2);
                //工人houseflow
                if(hfalist.size() < houseFlow.getPatrol()){//该工种没有巡查够，每次要拿钱
                    Member supervisor=memberMapper.selectByPrimaryKey(supervisorHF.getWorkerId());//找出大管家
                    HouseWorkerOrder supervisorHWO = houseWorkerOrderMapper.getHouseWorkerOrder(supervisorHF.getId(), supervisor.getId());
                    if(supervisorHWO.getCheckMoney() == null){
                        supervisorHWO.setCheckMoney(new BigDecimal(0));
                    }
                    if(supervisorHWO.getHaveMoney() == null){
                        supervisorHWO.setHaveMoney(new BigDecimal(0));
                    }
                    //累计大管家拿到的钱
                    supervisorHWO.setHaveMoney(supervisorHWO.getHaveMoney().add(supervisorHF.getPatrolMoney()));
                    //累计大管家订单巡查得到的钱
                    supervisorHWO.setCheckMoney(supervisorHWO.getCheckMoney().add(supervisorHF.getPatrolMoney()));
                    houseWorkerOrderMapper.updateByPrimaryKeySelective(supervisorHWO);

                    //申请中记录大管家钱
                    hfa.setSupervisorMoney(supervisorHF.getPatrolMoney());//本次大管家得到的钱
                    //大管家剩余
                    hfa.setOtherMoney(supervisorHWO.getWorkPrice().subtract(supervisorHWO.getHaveMoney()));
                    houseFlowApplyMapper.insert(hfa);

                    if(supervisor.getHaveMoney() == null){
                        supervisor.setHaveMoney(new BigDecimal(0));
                    }
                    if(supervisor.getSurplusMoney() == null){
                        supervisor.setSurplusMoney(new BigDecimal(0));
                    }
                    supervisor.setHaveMoney(supervisor.getHaveMoney().add(supervisorHF.getPatrolMoney()));
                    supervisor.setSurplusMoney(supervisor.getSurplusMoney().add(supervisorHF.getPatrolMoney()));
                    memberMapper.updateByPrimaryKeySelective(supervisor);
                    //记录到管家流水
                    WorkerDetail workerDetail = new WorkerDetail();
                    workerDetail.setName("巡查收入");
                    workerDetail.setWorkerId(supervisor.getId());
                    workerDetail.setWorkerName(supervisor.getName());
                    workerDetail.setHouseId(hfa.getHouseId());
                    workerDetail.setMoney(supervisorHF.getPatrolMoney());
                    workerDetail.setState(0);//进钱
                    workerDetailMapper.insert(workerDetail);
                }else{
                    houseFlowApplyMapper.insert(hfa);
                    return ServerResponse.createBySuccessMessage("巡查成功");
                }

            }else if(applyType == 6){//无人巡查
                hfa.setApplyDec("业主您好，我已经巡查了工地，工地情况如图");//描述
                hfa.setMemberCheck(1);//默认业主审核状态通过
                hfa.setSupervisorCheck(1);//默认大管家审核状态通过
                houseFlowApplyMapper.insert(hfa);
            }else{
                hfa.setApplyDec("业主您好，我已经巡查了工地，工地情况如图");//描述
                hfa.setMemberCheck(1);//默认业主审核状态通过
                hfa.setSupervisorCheck(1);//默认大管家审核状态通过
                houseFlowApplyMapper.insert(hfa);
            }
            //**********保存图片等信息************//
            JSONArray imageArr= JSON.parseArray(imageList);
            for(int i=0; i<imageArr.size(); i++){//上传材料照片
                JSONObject imageObj =imageArr.getJSONObject(i);
                HouseFlowApplyImage hfai = new HouseFlowApplyImage();
                String[] imageUrlList = imageObj.getString("imageUrl").split(",");//材料照片
                for(int j=0;j<imageUrlList.length;j++){
                    int first4 = imageUrlList[j].indexOf("/20");
                    String imgStr="";
                    if (first4 >= 0) {
                        imgStr = imageUrlList[j].substring(first4);
                    }
                    hfai.setImageUrl(imgStr);
                    hfai.setImageType(imageObj.getInteger("imageType"));//图片类型 0：材料照片；1：进度照片；2:其他
                    hfai.setImageTypeId(imageObj.getString("imageTypeId"));//图片类型id 例如：关联验收节点的id
                    hfai.setImageTypeName(imageObj.getString("imageTypeName"));//图片类型名称 例如：材料照片；进度照片
                    hfai.setHouseFlowApplyId(hfa.getId());
                    houseFlowApplyImageMapper.insert(hfai);
                }
            }
            if(hfa.getApplyType() == 0){//0每日完工申请
                House house = houseMapper.selectByPrimaryKey(hfa.getHouseId());
                house.setTaskNumber(1);
                houseMapper.updateByPrimaryKeySelective(house);
            }
            return ServerResponse.createBySuccessMessage("操作成功");
        }catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }

    /*
     * 提前入场
     */
    public ServerResponse getAdvanceInAdvance(String userToken,String houseFlowId) {
        try {
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            Member worker = accessToken.getMember();
            HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(houseFlowId);
            if (houseFlow.getWorkType() > 1) {
                return ServerResponse.createByErrorMessage("提前进场失败");
            } else if (houseFlow.getWorkType() == null) {
                houseFlow.setWorkType(0);//
            }
            // 重新调整施工顺序
            int sort = 4;
            // 先确定已开工顺序
            List<HouseFlow> hflist = houseFlowMapper.getForCheckMoney(houseFlow.getHouseId());
            for (HouseFlow hf : hflist) {
                if (hf.getWorkType() > 1) {
                    hf.setSort(sort);
                    sort++;
                    houseFlowMapper.updateByPrimaryKeySelective(hf);
                }
            }
            houseFlow.setSort(sort);// 本次提前位置
            sort++;
            houseFlow.setWorkType(2);
            houseFlow.setReleaseTime(new Date());//发布时间
            houseFlowMapper.updateByPrimaryKeySelective(houseFlow);

            for (HouseFlow hf : hflist) {
                if (hf.getWorkType() < 2) {
                    hf.setSort(sort);
                    sort++;
                    houseFlowMapper.updateByPrimaryKeySelective(houseFlow);
                }
            }
            return ServerResponse.createBySuccessMessage("提前进场成功");
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("系统出错，提前进场失败");
        }
    }

    /**
     * 根据工匠id查询施工列表
     */
    public ServerResponse getHouseFlowList(String userToken){
        try{
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            Member worker = accessToken.getMember();
            List<HouseWorker> listHouseWorker=houseWorkerMapper.getAllHouseWorker(worker.getId());
            List<Map<String,Object>> listMap=new ArrayList<Map<String,Object>>();//返回通讯录list
            for(HouseWorker houseWorker:listHouseWorker){
                Map<String, Object> map=new HashMap<String, Object>();
                map.put("houseFlowId", houseWorker.getHouseFlowId());//任务id
                HouseFlow houseFlow=houseFlowMapper.selectByPrimaryKey(houseWorker.getHouseFlowId());
                House house=houseMapper.selectByPrimaryKey(houseWorker.getHouseId());//查询房产信息
                //房产信息
                map.put("houseName", (house.getResidential()==null?"*":house.getResidential())
                        +(house.getBuilding()==null?"*":house.getBuilding())+"栋"
                        +(house.getUnit()==null?"*":house.getUnit())+"单元"+(house.getNumber()==null?"*":house.getNumber())+"号");//地址
                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                map.put("releaseTime", houseFlow.getReleaseTime()==null?"":sdf.format(houseFlow.getReleaseTime()));//发布时间
                map.put("square", (house.getSquare()==null?"0":house.getSquare())+"m²");//面积
                map.put("memberName", memberMapper.selectByPrimaryKey(houseFlow.getMemberId()).getName());//业主姓名
                map.put("price", "￥"+(houseFlow.getWorkPrice()==null?"0":houseFlow.getWorkPrice()));//价格
                if(houseFlow.getPause()==0){//正常施工
                    map.put("isItNormal", "正常施工");
                }else{
                    map.put("isItNormal", "暂停施工");//暂停施工
                }
                List<HouseFlowApply> todayStartList=houseFlowApplyMapper.getTodayStartByHouseId(house.getId());//查询今日开工记录
                if(todayStartList==null||todayStartList.size()==0){//没有今日开工记录
                    map.put("houseIsStart", "今日未开工");//是否正常施工
                }else{
                    map.put("houseIsStart", "今日已开工");//是否正常施工
                }
                List<HouseFlowApply> supervisorCheckList =houseFlowApplyMapper.getSupervisorCheckList(house.getId());//查询所有待大管家审核
                map.put("taskNumber", supervisorCheckList.size());//任务数量
                listMap.add(map);
            }
            return ServerResponse.createBySuccess("获取施工列表成功",listMap);
        }catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("系统出错，获取施工列表失败！");
        }
    }

    /**
     * 切换工地
     */
    public ServerResponse setSwitchHouseFlow(String userToken,String houseFlowId){
        try{
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            Member worker = accessToken.getMember();
            List<HouseWorker> listHouseWorker=houseWorkerMapper.getAllHouseWorker(worker.getId());
            for(HouseWorker houseWorker:listHouseWorker){
                if(houseWorker.getHouseFlowId().equals(houseFlowId)){//选中的任务isSelect改为1
                    houseWorker.setIsSelect(1);
                    houseWorkerMapper.updateByPrimaryKeySelective(houseWorker);
                }else{//其他改为0
                    houseWorker.setIsSelect(0);
                    houseWorkerMapper.updateByPrimaryKeySelective(houseWorker);
                }
            }
            return ServerResponse.createBySuccessMessage("切换工地成功");
        }catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("系统出错，切换工地失败！");
        }
    }

    /*
     * 校验交底二维码（工人扫）
     */
    public ServerResponse telCode(String userToken,String code){
        try{
            HttpServletRequest request = ((ServletRequestAttributes )RequestContextHolder.getRequestAttributes()).getRequest();
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            Member worker = accessToken.getMember();
            String[] str = code.split("=");
            String houseFlowId = str[1];
            HouseFlow hf=houseFlowMapper.selectByPrimaryKey(houseFlowId);//查询houseFlow
            if(!hf.getWorkerId().equals(worker.getId())){
                return ServerResponse.createByErrorMessage("交底人不匹配！");
            }
            return ServerResponse.createBySuccess("交底成功！",StringTool.getUrl(request) + "/worker/worker!readDrawing.action?houseFlowId="+houseFlowId+"&start=1");
        }catch(Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("扫码失败！");
        }
    }

    /**
     * 校验巡查二维码
     * @return
     */
    public ServerResponse scanCode(String userToken,String code,String longitude,String latitude){
        try{
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            Member worker = accessToken.getMember();

            String[] str= code.split("=");
            String houseFlowId = str[1];
            HouseFlow hf=houseFlowMapper.selectByPrimaryKey(houseFlowId);//查询houseFlow
            //工匠的坐标
            String x = hf.getLatitude();
            String y = hf.getLongitude();
            System.out.println("工匠坐标:经度"+y+",工匠纬度:"+x);

            Calendar cal = Calendar.getInstance();
            cal.setTime(hf.getPast());//二维码生成时间
            cal.add(Calendar.MINUTE, 30);

            Calendar now = Calendar.getInstance();
            if(cal.before(now)){
                return ServerResponse.createByErrorMessage("二维码失效,请重新生成！");
            }
            //根据房子id找出该房子大管家
            HouseWorker supervisorWorker=houseWorkerMapper.getHwByHidAndWtype(hf.getHouseId(), 3);//查询大管家的
            Member steward=memberMapper.selectByPrimaryKey(supervisorWorker.getWorkerId());//查询大管家
            if(!steward.getId().equals(worker.getId())){
                return ServerResponse.createByErrorMessage("工人与大管家不是同一个工地！");
            }
            House house=houseMapper.selectByPrimaryKey(hf.getHouseId());//查询房产信息
            ModelingVillage village = modelingVillageMapper.selectByPrimaryKey(house.getVillageId());//小区
            Double locationx = Double.parseDouble(village.getLocationx()==null?"0":village.getLocationx());//小区经度
            Double locationy = Double.parseDouble(village.getLocationy()==null?"0":village.getLocationy());//小区维度
            System.out.println("小区坐标:经度"+locationx+",小区纬度:"+locationy);

            if(locationx == 0 || locationy == 0){
                return ServerResponse.createByErrorMessage("请配置该房子所在小区地理位置！");
            }

            double distance = GetShortDistance(locationx, locationy, Double.valueOf(y), Double.valueOf(x));//计算距离
            System.out.println("工匠与小区的距离:"+distance+"米*********************");

            distance = GetShortDistance(Double.valueOf(longitude), Double.valueOf(latitude), locationx, locationy);//计算距离
            System.out.println("管家坐标:经度"+longitude+",管家纬度:"+latitude);
            System.out.println("管家与小区的距离:"+distance+"米*********************");

            distance = GetShortDistance(Double.valueOf(longitude), Double.valueOf(latitude), Double.valueOf(y), Double.valueOf(x));//计算距离
            System.out.println("管家与工匠的距离:"+distance+"米*********************");
            if(distance > 3000){
                return ServerResponse.createByErrorMessage("大管家与工匠不在一起！");
            }
            return ServerResponse.createBySuccess("巡查校验二维码成功！",houseFlowId);
        }catch(Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("巡查校验二维码失败！");
        }
    }

    //根据经纬度计算距离，经度1，纬度1，经度2，纬度2
    public double GetShortDistance(double lon1,double lat1,double lon2,double lat2){
        double DEF_PI = 3.14159265359; // PI
        double DEF_2PI= 6.28318530712; // 2*PI
        double DEF_PI180= 0.01745329252; // PI/180.0
        double DEF_R =6370693.5; // radius of earth

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
     * 大管家申请验收
     */
    public ServerResponse setSupervisorApply(String userToken,String houseFlowId) {
        try {
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            Member worker = accessToken.getMember();
            HouseFlow houseFlow=houseFlowMapper.selectByPrimaryKey(houseFlowId);//查询houseFlow
            HouseWorker hw = houseWorkerMapper.getHwByHidAndWtype(houseFlow.getHouseId(),worker.getWorkerType());//这是查的大管家houseworker
            Member member = memberMapper.selectByPrimaryKey(houseFlow.getMemberId());//查询业主信息
            //新生成大管家hfa
            HouseWorkerOrder supervisor = houseWorkerOrderMapper.selectByPrimaryKey(hw.getHouseWorkerOrderId());
            HouseFlowApply hfa = new HouseFlowApply();
            hfa.setHouseFlowId(houseFlow.getId());
            hfa.setWorkerId(hw.getWorkerId());
            hfa.setWorkerTypeId(hw.getWorkerTypeId());
            hfa.setWorkerType(hw.getWorkerType());
            hfa.setHouseId(hw.getHouseId());
            hfa.setMemberId(houseFlow.getMemberId());
            hfa.setPayState(0);
            hfa.setApplyType(2);//大管家没有阶段完工，直接整体完工
            hfa.setApplyDec("亲爱的业主，您的房子已经全部完工，大吉大利!");
            hfa.setSupervisorCheck(1);
            hfa.setMemberCheck(0);
            hfa.setApplyMoney(supervisor.getWorkPrice().multiply(new BigDecimal(0.5)));//通过后拿剩下百分之50减押金
            hfa.setOtherMoney(new BigDecimal(0.0));
            houseFlowApplyMapper.insert(hfa);
            House house = houseMapper.selectByPrimaryKey(hfa.getHouseId());
            house.setTaskNumber(house.getTaskNumber() + 1);
            houseMapper.updateByPrimaryKeySelective(house);
            return ServerResponse.createBySuccessMessage("申请验收成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("系统出错，申请验收失败");
        }
    }

    /*
     * 进程详情
     */
    public ServerResponse getCourse(String userToken,String houseFlowId){
        try {
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            //工匠houseflow
            HouseFlow houseFlow=houseFlowMapper.selectByPrimaryKey(houseFlowId);//查询houseFlow
            Member worker = memberMapper.selectByPrimaryKey(houseFlow.getWorkerId());
            Map<String,Object> workerDetail=this.getWorkerDetail(houseFlow,worker);
            List<WorkerDisclosure> wdList=new ArrayList<>();
            String houseFlowApply="";
            if(houseFlow.getWorkType() == 4&&houseFlow.getWorkSteta() == 3){//待交底
                Example example = new Example(WorkerDisclosure.class);
                example.createCriteria().andEqualTo("state", 1);
                wdList=workerDisclosureMapper.selectByExample(example);
            }else{//施工中
                HouseFlowApply  hfa = houseFlowApplyMapper.getSupervisorCheck(houseFlow.getId(),houseFlow.getWorkerId());
                if(hfa!=null){
                    houseFlowApply=JSONObject.toJSONString(hfa);
                }
            }
            workerDetail.put("wdList",wdList);
            workerDetail.put("houseFlowApply",houseFlowApply);
            return ServerResponse.createBySuccess("获取进程详情成功",workerDetail);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("系统出错，获取进程详情失败");
        }
    }

    /**
     * 工人施工详情
     */
    public Map<String,Object> getWorkerDetail(HouseFlow houseFlow,Member worker) {
        try {
            Map<String, Object> map = new HashMap<String, Object>();
            if (worker != null && houseFlow != null) {
                WorkerType workType = workerTypeMapper.selectByPrimaryKey(worker.getWorkerTypeId());//查询工种
                map.put("workerHead", "" + worker.getHead());//工人头像
                map.put("workerName", worker.getName());//工人名称
                map.put("workerPhone", worker.getMobile());//工人电话
                map.put("praiseRate", worker.getPraiseRate().multiply(new BigDecimal(100)) + "%");//大管家好评率
                map.put("evation", worker.getEvaluationScore());//工人积分
                Long countOrder = houseWorkerMapper.getCountOrderByWorkerId(worker.getId());
                map.put("countOrder", countOrder == null ? "0" : countOrder);//工人总单数
                HouseFlowApply todayStart = houseFlowApplyMapper.getTodayStart(houseFlow.getHouseId(), worker.getId());//查询今日开工记录
                if (todayStart == null) {//没有今日开工记录
                    map.put("isStart", "否");
                } else {
                    map.put("isStart", "是");//今日是否开工；
                }
                List<HouseFlowApply> earliestTime = houseFlowApplyMapper.getEarliestTimeHouseApply(houseFlow.getHouseId(), worker.getId());//查询最早的每日开工申请
                Long suspendDay = houseFlowApplyMapper.getSuspendApply(houseFlow.getHouseId(), worker.getId());//根据房子id和工人id查询暂停天数
                Long everyEndDay = houseFlowApplyMapper.getEveryDayApply(houseFlow.getHouseId(), worker.getId());//根据房子id和工人id查询每日完工申请天数
                if (earliestTime != null && earliestTime.size()>0) {
                    Date EarliestDay = earliestTime.get(0).getCreateDate();//最早开工时间
                    Date newDate = new Date();
                    int a = daysOfTwo(EarliestDay, newDate);//计算当前时间隔最早开工时间相差多少天
                    if (suspendDay == null) {
                        map.put("totalDay", a - 0);//总开工天数
                    } else {
                        long aa = a - suspendDay;
                        if (aa >= 0) {
                            map.put("totalDay", aa);//总开工天数
                        } else {
                            map.put("totalDay", 0);//总开工天数
                        }
                    }
                } else {
                    map.put("totalDay", 0);//总开工天数
                }
                map.put("everyDay", everyEndDay == null ? "0" : everyEndDay);//每日完工天数
                map.put("suspendDay", suspendDay == null ? "0" : suspendDay);//暂停天数
                /*******正在进场；正常施工；已停工*********/
                if (todayStart != null) {//只要有今日开工,就是正常施工
                    map.put("isItNormal", "正常施工");
                } else if (houseFlow.getWorkType() == 3) {//如果是已抢单待支付
                    map.put("isItNormal", "正在进场");
                    map.put("message", "业主尚未支付" + workType.getName() + "费用,暂不能交底。请等待业主支付" + workType.getName() + "费用");
                } else if (houseFlow.getPause() == 1) {
                    map.put("isItNormal", "已停工");
                } else if (houseFlow.getWorkSteta() == 1) {
                    map.put("isItNormal", "已停工");
                } else if (houseFlow.getWorkSteta() == 2) {
                    map.put("isItNormal", "已停工");
                } else if (houseFlow.getWorkSteta() == 3) {//待交底
                    map.put("isItNormal", "正常施工");
                    map.put("message", "业主已支付" + workType.getName() + "费用,请联系工匠当面交底。长按底部按钮生成交底二维码支付");
                } else {
                    map.put("isItNormal", "正常施工");
                }
                map.put("patrol", houseFlow.getPatrol());//巡查标准次数
                map.put("patrolSecond", houseFlowApplyMapper.getCountValidPatrolByHouseId(houseFlow.getHouseId(), worker.getId()));//已巡查次数
            } else {
                return null;
            }
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
     * 获取提现信息
     */
    public ServerResponse getWithdrawalInformation(String userToken){
        try {
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class) + configUtil.getValue(SysConfig.PUBLIC_TEMPORARY_FILE_ADDRESS, String.class);
            Member worker = accessToken.getMember();
            Example example=new Example(RewardPunishRecord.class);
            example.createCriteria().andEqualTo("workerId", worker.getId());
            List<RewardPunishRecord> wraprList = rewardPunishRecordMapper.selectByExample(example);
            if (wraprList != null) {//通过查看奖罚限制提现时间限制提现
                for (RewardPunishRecord wrapr : wraprList) {
                    if (wrapr.getDeposit() == 2) {
                        Date wraprDate = wrapr.getDepositExpire();
                        DateFormat longDateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);
                        Date date = new Date();
                        if (date.getTime() < wraprDate.getTime()) {
                            return ServerResponse.createByErrorMessage("您处于平台处罚期内，" + longDateFormat.format(wraprDate) + "以后才能提现,如有疑问请致电400-168-1231！");
                        }
                    }
                }
            }
            //工匠关联银卡
            Example example2=new Example(RewardPunishRecord.class);
            example2.createCriteria().andEqualTo("workerId", worker.getId());
            List<WorkerBankCard> wbcList = workerBankCardMapper.selectByExample(example2);
            if (wbcList == null||wbcList.size()==0) {
                return ServerResponse.createByErrorMessage("请绑定银行卡!");
            }
            WorkerBankCard wbc=wbcList.get(0);
            //卡号
            String bankcardnumber = wbc.getBankCardNumber();

            //具体银卡名字图片
            BankCard bc = bankCardMapper.selectByPrimaryKey(wbc.getBankCardId());

            Map<String,Object> map = new HashMap<>();
            map.put("telphone",worker.getMobile());
            map.put("bankPictures",address+bc.getBankCardImage());//银行图标
            //名字+卡号后4位
            bankcardnumber = bankcardnumber.replaceAll("\\s*", "");
            map.put("bandDescribe",bc.getBankName() + bankcardnumber.substring(bankcardnumber.length() - 4, bankcardnumber.length()));
            map.put("surplusprice",worker.getSurplusMoney());
            if (bc.getBkMinAmt() == null || bc.getBkMaxAmt() == null) {
                return ServerResponse.createByErrorMessage("请设置" + bc.getBankName() + "的最大最小限额!");
            }
            map.put("min",new BigDecimal(bc.getBkMinAmt()));
            map.put("max",new BigDecimal(bc.getBkMaxAmt()));
            return ServerResponse.createBySuccess("获取成功",map);
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("获取失败");
        }
    }

    /**
     * 根据工匠id和houseFlowid查询通讯录
     */
    public ServerResponse getMailList(String userToken,String houseFlowId){
        try{
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            Member worker = accessToken.getMember();
            HouseFlow houseFlow=houseFlowMapper.selectByPrimaryKey(houseFlowId);//查询houseFlow
            List<Map<String,Object>> listMap=new ArrayList<Map<String,Object>>();//返回通讯录list
            if(houseFlow!=null&&worker!=null){
                if(worker.getWorkerType()==3){//大管家
                    List<HouseWorker> listHouseWorker=houseWorkerMapper.getWorktype6ByHouseid(houseFlow.getHouseId());
                    for(HouseWorker houseWorker:listHouseWorker){
                        Map<String, Object> map=new HashMap<String, Object>();
                        Member worker2=memberMapper.selectByPrimaryKey(houseWorker.getWorkerId());
                        if(worker2==null){
                            continue;
                        }
                        map.put("workerTypeName", workerTypeMapper.selectByPrimaryKey(worker2.getWorkerTypeId())==null?"":workerTypeMapper.selectByPrimaryKey(worker2.getWorkerTypeId()).getName());
                        map.put("workerName", worker2.getName());
                        map.put("workerPhone", worker2.getMobile());
                        listMap.add(map);
                    }
                }else{//普通工匠
                    HouseWorker houseWorker= houseWorkerMapper.getHwByHidAndWtype(houseFlow.getHouseId(),3);
                    Map<String, Object> map=new HashMap<String, Object>();
                    Member worker2=memberMapper.selectByPrimaryKey(houseWorker.getWorkerId());//根据工匠id查询工匠信息详情
                    map.put("workerTypeName", "大管家");
                    map.put("workerName", worker2.getName());//大管家
                    map.put("workerPhone", worker2.getMobile());
                    Member member=memberMapper.selectByPrimaryKey(houseFlow.getMemberId());//房主
                    Map<String, Object> map2=new HashMap<String, Object>();
                    map2.put("workerTypeName", "业主");
                    map2.put("workerName", member.getName());
                    map2.put("workerPhone", member.getMobile());
                    listMap.add(map);
                    listMap.add(map2);
                }
            }
            return ServerResponse.createBySuccess("获取成功",listMap);
        }catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("获取失败");
        }
    }

    /**
     * 获取验收节点ist
     */
    public ServerResponse getHouseFlowApplyImageList(String houseId){
        try{

            return ServerResponse.createBySuccess("获取成功",null);
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("获取失败");
        }
    }

}
