package com.dangjia.acg.service.core;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.mapper.core.IHouseFlowApplyImageMapper;
import com.dangjia.acg.mapper.core.IHouseFlowApplyMapper;
import com.dangjia.acg.mapper.core.IHouseFlowMapper;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.repair.IChangeOrderMapper;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.core.HouseFlowApply;
import com.dangjia.acg.modle.core.HouseFlowApplyImage;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.repair.ChangeOrder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 * author: qiyuxiang
 * Date: 2019/05/27 0026
 * Time: 10:05
 */
@Service
public class HouseFlowScheduleService {

    @Autowired
    private IHouseFlowApplyMapper houseFlowApplyMapper;

    @Autowired
    private IHouseFlowApplyImageMapper houseFlowApplyImageMapper;
    @Autowired
    private IHouseFlowMapper houseFlowMapper;
    @Autowired
    private IMemberMapper memberMapper;
    @Autowired
    private IWorkerTypeMapper workerTypeMapper;
    @Autowired
    private IHouseMapper houseMapper;
    @Autowired
    private ConfigUtil configUtil;

    @Autowired
    private IChangeOrderMapper changeOrderMapper;

    /**
     * 工程日历工序列表
     * @param houseId 房子ID
     * @return
     */
    public ServerResponse getHouseFlows(String houseId){
        List<HouseFlow> houseFlowList=houseFlowMapper.getForCheckMoney(houseId);
        Map mapObj=new HashMap();
        List<Map> houseFlowMap=new ArrayList<>();
        Date startDate=null;
        Date endDate=null;
        for (HouseFlow houseFlow : houseFlowList) {
            Map map =new HashMap();
            WorkerType workerType = workerTypeMapper.selectByPrimaryKey(houseFlow.getWorkerTypeId());
            map.put(HouseFlow.ID,houseFlow.getId());
            map.put(WorkerType.NAME,workerType.getName());
            map.put(HouseFlow.START_DATE,houseFlow.getStartDate());
            map.put(HouseFlow.END_DATE,houseFlow.getEndDate());
            if(houseFlow.getStartDate()!=null){
                if(startDate==null||startDate.getTime()>houseFlow.getStartDate().getTime()){
                    startDate= houseFlow.getStartDate();
                }
                if(endDate==null||endDate.getTime()<houseFlow.getEndDate().getTime()){
                    startDate= houseFlow.getEndDate();
                }
                int num = 1 + DateUtil.daysofTwo(houseFlow.getStartDate(), houseFlow.getEndDate());//逾期工期天数
                map.put("num",num);
            }
            houseFlowMap.add(map);
            mapObj.put("houseId",houseId);
            int numall =0;
            if(startDate!=null) {
                numall = 1 + DateUtil.daysofTwo(startDate, endDate);//逾期工期天数
            }
            mapObj.put("totalNum",numall);
            mapObj.put("list",houseFlowMap);
        }
        return ServerResponse.createBySuccess("查询成功", mapObj);
    }


    /**
     * 设置指定工序的工期
     * @param houseFlowId 工序ID
     * @param startDate 工期开始时间
     * @param endDate   工期结束时间
     * @return
     */
    public ServerResponse setHouseFlowSchedule(String  houseFlowId, Date startDate,Date endDate){
        HouseFlow  houseFlow=houseFlowMapper.selectByPrimaryKey(houseFlowId);
        houseFlow.setStartDate(startDate);
        houseFlow.setEndDate(endDate);
        houseFlowMapper.updateByPrimaryKeySelective(houseFlow);
        return ServerResponse.createBySuccessMessage("保持成功");
    }

    /**
     * 延长或提前工序的工期
     * @param houseFlowId 工序ID
     * @param extend 延长天数 两者只能其一
     * @param advance 提前天数 两者只能其一
     * @return
     */
    public ServerResponse updateFlowSchedule(String  houseFlowId, Integer extend,Integer advance){
        HouseFlow  houseFlow=houseFlowMapper.selectByPrimaryKey(houseFlowId);
        if(extend!=null&&extend>0){
            houseFlow.setEndDate(DateUtil.addDateDays(houseFlow.getEndDate(),extend));
        }
        if(advance!=null&&advance>0){
            houseFlow.setEndDate(DateUtil.delDateDays(houseFlow.getEndDate(),advance));
        }
        if(houseFlow.getStartDate().getTime()<houseFlow.getEndDate().getTime()){
            houseFlow.setEndDate(houseFlow.getStartDate());
        }
        houseFlowMapper.updateByPrimaryKeySelective(houseFlow);
        return ServerResponse.createBySuccessMessage("保持成功");
    }

    /**
     * 生产工程日历
     * @param houseId 房子ID
     * @return
     */
    public ServerResponse makeCalendar(String houseId){
        House house = houseMapper.selectByPrimaryKey(houseId);
        house.setSchedule("1");

        houseMapper.updateByPrimaryKeySelective(house);
        return ServerResponse.createBySuccessMessage("生成成功");
    }

    /**
     * 查看日历
     * @param houseId 房子ID
     * @param day 指定哪天
     * @return   {date:'2019-05-01',type:1}
     *   type: 1,正常;2,特殊;3,其他;4,正常+特殊;5,其他+特殊
     */
    public ServerResponse viewCalendar(String houseId,Date day){
        if(day==null){
            day=new Date();
        }
        Calendar c = Calendar.getInstance();
        c.setTime(day);
        int year = c.get(Calendar.YEAR);
        int modth = c.get(Calendar.MONTH);
        List<String> list=DateUtil.dayReportAll(year,modth);
        List<HouseFlow> houseFlowList=houseFlowMapper.getForCheckMoney(houseId);
        List<Map> mapList=new ArrayList<>();
        for (String o : list) {
            Map map =new HashMap();
            List<String> plans=new ArrayList<>();//计划记录
            List<String> actuals=new ArrayList<>();//实际记录
            int type=getPlans(o,houseFlowList,plans,actuals);
            map.put("type",type);
            map.put("date",o);
            map.put("plans",plans);
            map.put("actuals",actuals);
            mapList.add(map);
        }
        return ServerResponse.createBySuccess("查询成功",mapList);
    }
//    type: 1,正常;2,特殊;3,其他;4,正常+特殊;5,其他+特殊
    public int getPlans(String o , List<HouseFlow> houseFlowList,List<String> plans,List<String> actuals){
        int type=0;
        Date od = DateUtil.toDate(o);
        for (HouseFlow houseFlow : houseFlowList) {
            int mr=0;
            WorkerType workerType = workerTypeMapper.selectByPrimaryKey(houseFlow.getWorkerTypeId());
            Example example=new Example(HouseFlowApply.class);
            example.createCriteria().andEqualTo(HouseFlowApply.HOUSE_FLOW_ID,houseFlow.getId())
                    .andEqualTo(HouseFlowApply.MEMBER_CHECK,1)
                    .andCondition(" apply_type in (0,1,2,3,4) ")
                    .andEqualTo(HouseFlowApply.WORKER_TYPE,houseFlow.getWorkerType());
            List<HouseFlowApply>  houseFlowApplies=houseFlowApplyMapper.selectByExample(example);
            if(houseFlow.getStartDate()!=null&&houseFlow.getEndDate()!=null) {
                String s = DateUtil.dateToString(houseFlow.getStartDate(), null);
                String e = DateUtil.dateToString(houseFlow.getEndDate(), null);
                if(s.equals(o)){
                    plans.add("当前为"+workerType.getName()+"进场日期");
                    mr=1;
                }
                if(e.equals(o)){
                    mr=1;
                    if(houseFlow.getWorkerType()==4) {
                        plans.add("当前为"+workerType.getName()+"整体完工日期");
                    }else{
                        plans.add("当前为"+workerType.getName()+"阶段完工日期");
                    }
                }
                if(houseFlow.getStartDate().getTime()>od.getTime()&&houseFlow.getEndDate().getTime()<od.getTime()){
                    plans.add("当前为"+workerType.getName()+"正常施工日期");
                    mr=1;
                }
            }
            for (HouseFlowApply houseFlowApply : houseFlowApplies) {
                String jieDian="";
                if(houseFlowApply.getApplyType()<=2){
                    example = new Example(HouseFlowApplyImage.class);
                    example.createCriteria().andEqualTo(HouseFlowApplyImage.HOUSE_FLOW_APPLY_ID, houseFlowApply.getId());
                    List<HouseFlowApplyImage> houseFlowApplyImageList = houseFlowApplyImageMapper.selectByExample(example);
                    List<String> imageName = new ArrayList<String>();
                    for (HouseFlowApplyImage houseFlowApplyImage : houseFlowApplyImageList){
                        if(houseFlowApplyImage.getImageType()>1) {
                            imageName.add(houseFlowApplyImage.getImageTypeName());
                        }
                    }
                    jieDian= StringUtils.join(imageName,",");
                }
                String sc = DateUtil.dateToString(houseFlowApply.getCreateDate(), null);
                if(o.equals(sc)){
                    //0每日完工申请
                    if(houseFlowApply.getApplyType()==0){
                        if(type!=4&&type!=5) {
                            if (mr == 1 && type == 2) {
                                type = 4;
                            } else if (mr == 1 && type == 3) {
                                type = 5;
                            } else if (mr == 1) {
                                type = 1;
                            }
                        }
                        actuals.add(workerType.getName()+"今日完工，完成节点:"+jieDian);
                    }
                    //1阶段完工申请
                    if(houseFlowApply.getApplyType()==1){
                        if(type!=4&&type!=5) {
                            if (mr == 1 && type == 2) {
                                type = 4;
                            } else if (mr == 1 && type == 3) {
                                type = 5;
                            } else if (mr == 1) {
                                type = 1;
                            }
                        }
                        actuals.add(workerType.getName()+"已阶段完工，完成节点:"+jieDian);
                    }
                    //2整体完工申请
                    if(houseFlowApply.getApplyType()==2){
                        if(type!=4&&type!=5) {
                            if (mr == 1 && type == 2) {
                                type = 4;
                            } else if (mr == 1 && type == 3) {
                                type = 5;
                            } else if (mr == 1) {
                                type = 1;
                            }
                        }
                        actuals.add(workerType.getName()+"已整体完工，完成节点:"+jieDian);
                    }
                    //3停工申请
                    if(houseFlowApply.getApplyType()==3){
                        if(type!=4&&type!=5) {
                            if(type==1){
                                type=4;
                            }else if(type==3){
                                type=5;
                            }else {
                                type=2;
                            }
                        }

                        int numall = 1 + DateUtil.daysofTwo(houseFlowApply.getStartDate(), houseFlowApply.getEndDate());//请假天数
                        actuals.add(workerType.getName()+"申请"+numall+"天停工，工期延期"+numall+"天"+ (CommonUtil.isEmpty(houseFlowApply.getApplyDec())?"":",理由："+houseFlowApply.getApplyDec()));
                    }
                    //4每日开工申请
                    if(houseFlowApply.getApplyType()==4){
                        if(type!=4&&type!=5) {
                            if (mr == 1 && type == 2) {
                                type = 4;
                            } else if (mr == 1 && type == 3) {
                                type = 5;
                            } else if (mr == 1) {
                                type = 1;
                            }
                        }
                        actuals.add(workerType.getName()+"今日开工");
                    }

                    if(mr==1&&type!=1&&type!=4&&type!=5){
                        type=3;
                    }
                }
            }
            example=new Example(ChangeOrder.class);
            example.createCriteria().andEqualTo(ChangeOrder.HOUSE_ID,houseFlow.getHouseId())
                    .andEqualTo(ChangeOrder.WORKER_TYPE_ID,houseFlow.getWorkerTypeId())
                    .andCondition("  state not in (4,6) ");
            List<ChangeOrder>  changeOrders=changeOrderMapper.selectByExample(example);
            for (ChangeOrder changeOrder : changeOrders) {
                String sc = DateUtil.dateToString(changeOrder.getCreateDate(), null);
                if(o.equals(sc)){
                    //补人工
                    if(changeOrder.getType()==1){
                        if(type!=4&&type!=5) {
                            if(type==1){
                                type=4;
                            }else if(type==3){
                                type=5;
                            }else {
                                type=2;
                            }
                        }
                        if(changeOrder.getScheduleDay()!=null&&changeOrder.getScheduleDay()>0) {
                            actuals.add(workerType.getName()+"补人工成功，工期延期" + changeOrder.getScheduleDay() + "天");
                        }else{
                            actuals.add(workerType.getName()+"补人工成功，工期延期");
                        }
                    }
                    //退人工
                    if(changeOrder.getType()==2){
                        if(type!=4&&type!=5) {
                            if(type==1){
                                type=4;
                            }else if(type==3){
                                type=5;
                            }else {
                                type=2;
                            }
                        }
                        if(changeOrder.getScheduleDay()!=null&&changeOrder.getScheduleDay()>0) {
                            actuals.add("业主退人工成功，工期提前" + changeOrder.getScheduleDay() + "天");
                        }else{
                            actuals.add("业主退人工成功，工期提前");
                        }
                    }
                }
            }
        }
        return type;
    }
}













