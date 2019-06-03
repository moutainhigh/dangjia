package com.dangjia.acg.service.core;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.core.HouseFlowDTO;
import com.dangjia.acg.mapper.core.IHouseFlowApplyImageMapper;
import com.dangjia.acg.mapper.core.IHouseFlowApplyMapper;
import com.dangjia.acg.mapper.core.IHouseFlowMapper;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.repair.IChangeOrderMapper;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.core.HouseFlowApply;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.repair.ChangeOrder;
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
                    endDate= houseFlow.getEndDate();
                }
                int num = 1 + DateUtil.daysofTwo(houseFlow.getStartDate(), houseFlow.getEndDate());//逾期工期天数
                map.put("num",num);
            }
            houseFlowMap.add(map);
            mapObj.put("houseId",houseId);
        }
        int numall =0;
        if(startDate!=null) {
            numall = 1 + DateUtil.daysofTwo(startDate, endDate);//逾期工期天数
        }
        mapObj.put("totalNum",numall);
        mapObj.put("list",houseFlowMap);
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
     * @param houseId 房子ID
     * @param workerTypeId 工序ID
     * @param extend 延长天数 两者只能其一
     * @param advance 提前天数 两者只能其一
     * @return
     */
    public ServerResponse updateFlowSchedule(String houseId,String workerTypeId, Integer extend,Integer advance){
        HouseFlow  houseFlow=houseFlowMapper.getByWorkerTypeId(houseId,workerTypeId);
        if(houseFlow.getEndDate()!=null){
            if (extend != null && extend > 0) {
                houseFlow.setEndDate(DateUtil.addDateDays(houseFlow.getEndDate(), extend));
            }
            if (advance != null && advance > 0) {
                houseFlow.setEndDate(DateUtil.delDateDays(houseFlow.getEndDate(), advance));
            }
            if (houseFlow.getStartDate().getTime() > houseFlow.getEndDate().getTime()) {
                houseFlow.setEndDate(houseFlow.getStartDate());
            }
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
     * @return   {date:'2019-05-01',type:1} type: 1,正常;2,特殊;3,其他;4,正常+特殊;5,其他+特殊
     */
    public ServerResponse viewCalendar(String houseId,Date day){
        if(day==null){
            day=new Date();
        }
        Map mapObj=new HashMap();
        Calendar c = Calendar.getInstance();
        c.setTime(day);
        int year = c.get(Calendar.YEAR);
        int modth = c.get(Calendar.MONTH)+1;
        List<String> list=DateUtil.dayReportAll(year,modth);
        List<HouseFlowDTO> houseFlowList=houseFlowMapper.getHouseScheduleFlow(houseId);

        List<HouseFlowApply>  houseFlowApplies=houseFlowApplyMapper.getHouseScheduleFlowApply(houseId,DateUtil.dateToString(day,DateUtil.FORMAT2));

        Example example=new Example(ChangeOrder.class);
        example.createCriteria().andEqualTo(ChangeOrder.HOUSE_ID,houseId)
                .andCondition("  (state = 4 or  state = 6) ");
        List<ChangeOrder>  changeOrders=changeOrderMapper.selectByExample(example);

        List<Map> mapList=new ArrayList<>();
        for (String o : list) {
            Map map =new HashMap();
            List plans=new ArrayList<>();//计划记录
            List actuals=new ArrayList<>();//实际记录
            int type=getPlans(o,houseFlowList,plans,actuals,houseFlowApplies,changeOrders);
            map.put("type",type);
            map.put("date",o);
            map.put("plans",plans);
            map.put("actuals",actuals);
            mapList.add(map);
        }
        Date startDate=null;
        Date endDate=null;
        for (HouseFlowDTO houseFlow : houseFlowList) {
            if(houseFlow.getStartDate()!=null){
                if(startDate==null||startDate.getTime()>houseFlow.getStartDate().getTime()){
                    startDate= houseFlow.getStartDate();
                }
                if(endDate==null||endDate.getTime()<houseFlow.getEndDate().getTime()){
                    endDate= houseFlow.getEndDate();
                }
            }
        }
        int numall =0;
        if(startDate!=null) {
            numall = 1 + DateUtil.daysofTwo(startDate, endDate);//逾期工期天数
        }
        mapObj.put("totalNum",numall);
        mapObj.put("list",mapList);
        return ServerResponse.createBySuccess("查询成功",mapObj);
    }
//    type: 1,正常;2,特殊;3,其他;4,正常+特殊;5,其他+特殊
    public int getPlans(String o , List<HouseFlowDTO> houseFlowList,List<Map> plans,List<Map> actuals,List<HouseFlowApply>  houseFlowApplies, List<ChangeOrder>  changeOrders){
        int type=0;
        Date od = DateUtil.toDate(o);
        for (HouseFlowDTO houseFlow : houseFlowList) {
            if(houseFlow.getStartDate()!=null&&houseFlow.getEndDate()!=null) {
                String s = DateUtil.dateToString(houseFlow.getStartDate(), null);
                String e = DateUtil.dateToString(houseFlow.getEndDate(), null);
                if(s.equals(o)){
                    Map map =new HashMap<>();
                    map.put("info","当前为"+houseFlow.getWorkerTypeName()+"进场日期");
                    map.put("date",DateUtil.dateToString(houseFlow.getCreateDate(),DateUtil.FORMAT2));
                    map.put("type",1);
                    plans.add(map);
                }
                if(e.equals(o)){
                    Map map =new HashMap<>();
                    if(houseFlow.getWorkerType()==4) {
                        map.put("info","当前为"+houseFlow.getWorkerTypeName()+"整体完工日期");
                    }else{
                        map.put("info","当前为"+houseFlow.getWorkerTypeName()+"阶段完工日期");
                    }

                    map.put("type",2);
                    map.put("date",DateUtil.dateToString(houseFlow.getCreateDate(),DateUtil.FORMAT2));
                    plans.add(map);
                }
                if(houseFlow.getStartDate().getTime()>od.getTime()&&houseFlow.getEndDate().getTime()<od.getTime()){
                    Map map =new HashMap<>();
                    map.put("date",DateUtil.dateToString(houseFlow.getCreateDate(),DateUtil.FORMAT2));
                    map.put("info","当前为"+houseFlow.getWorkerTypeName()+"正常施工日期");
                    map.put("type",1);
                    plans.add(map);
                }
            }

            for (HouseFlowApply houseFlowApply : houseFlowApplies) {
                if(!houseFlowApply.getHouseFlowId().equals(houseFlow.getId())){
                    continue;
                }
                String sc = DateUtil.dateToString(houseFlowApply.getCreateDate(), null);
                if(o.equals(sc)){
                    //0每日完工申请
                    if(houseFlowApply.getApplyType()==0){
                        Map map =new HashMap<>();
                        map.put("date",DateUtil.dateToString(houseFlowApply.getCreateDate(),DateUtil.FORMAT2));
                        map.put("info",houseFlow.getWorkerTypeName()+"今日完工，完成节点:"+houseFlowApply.getApplyDec());
                        map.put("type",1);
                        actuals.add(map);
                    }
                    //1阶段完工申请
                    if(houseFlowApply.getApplyType()==1){
                        Map map =new HashMap<>();
                        map.put("date",DateUtil.dateToString(houseFlowApply.getCreateDate(),DateUtil.FORMAT2));
                        map.put("info",houseFlow.getWorkerTypeName()+"已阶段完工，完成节点:"+houseFlowApply.getApplyDec());
                        map.put("type",2);
                        actuals.add(map);
                    }
                    //2整体完工申请
                    if(houseFlowApply.getApplyType()==2){
                        Map map =new HashMap<>();
                        map.put("date",DateUtil.dateToString(houseFlowApply.getCreateDate(),DateUtil.FORMAT2));
                        map.put("info",houseFlow.getWorkerTypeName()+"已整体完工，完成节点:"+houseFlowApply.getApplyDec());
                        map.put("type",2);
                        actuals.add(map);
                    }
                    //3停工申请
                    if(houseFlowApply.getApplyType()==3){
                        int numall = 1 + DateUtil.daysofTwo(houseFlowApply.getStartDate(), houseFlowApply.getEndDate());//请假天数
                        Map map =new HashMap<>();
                        map.put("date",DateUtil.dateToString(houseFlowApply.getCreateDate(),DateUtil.FORMAT2));
                        map.put("info",houseFlow.getWorkerTypeName()+"申请"+numall+"天停工"+ (CommonUtil.isEmpty(houseFlowApply.getApplyDec())?"":",理由："+houseFlowApply.getApplyDec()));
                        map.put("type",3);
                        actuals.add(map);
                    }
                    //4每日开工申请
                    if(houseFlowApply.getApplyType()==4){
                        Map map =new HashMap<>();
                        map.put("date",DateUtil.dateToString(houseFlowApply.getCreateDate(),DateUtil.FORMAT2));
                        map.put("info",houseFlow.getWorkerTypeName()+"今日开工");
                        map.put("type",1);
                        actuals.add(map);
                    }

                }
            }
            for (ChangeOrder changeOrder : changeOrders) {
                if(!changeOrder.getWorkerTypeId().equals(houseFlow.getWorkerTypeId())){
                    continue;
                }
                String sc = DateUtil.dateToString(changeOrder.getCreateDate(), null);
                if(o.equals(sc)){
                    //补人工
                    if(changeOrder.getType()==1){
                        Map map =new HashMap<>();
                        map.put("date",DateUtil.dateToString(changeOrder.getCreateDate(),DateUtil.FORMAT2));
                        if(changeOrder.getScheduleDay()!=null&&changeOrder.getScheduleDay()>0) {
                            map.put("info",houseFlow.getWorkerTypeName()+"补人工成功，工期延期" + changeOrder.getScheduleDay() + "天");
                        }else{
                            map.put("info",houseFlow.getWorkerTypeName()+"补人工成功，工期延期");
                        }
                        map.put("type",3);
                        actuals.add(map);
                    }
                    //退人工
                    if(changeOrder.getType()==2){
                        Map map =new HashMap<>();
                        map.put("date",DateUtil.dateToString(changeOrder.getCreateDate(),DateUtil.FORMAT2));
                        if(changeOrder.getScheduleDay()!=null&&changeOrder.getScheduleDay()>0) {
                            map.put("info","业主退人工成功，工期提前" + changeOrder.getScheduleDay() + "天");
                        }else{
                            map.put("info","业主退人工成功，工期提前");
                        }
                        map.put("type",3);
                        actuals.add(map);
                    }
                }
            }
        }
//        type: 1,正常;2,特殊;3,其他;4,正常+特殊;5,其他+特殊；6,无记录
        if(actuals.size()==0&&plans.size()==0){
            type=6;
        }else if(actuals.size()==0||plans.size()==0){
            type=3;
        }else if(actuals.size()>0&&plans.size()>0){
            type=3;
            for (Map plan : plans) {
                Integer planlType=(Integer) plan.get("type");
                for (Map actual : actuals) {
                    Integer actualType=(Integer) actual.get("type");
                    if(actualType==planlType){
                        type=1;
                        break;
                    }
                }
            }
            for (Map actual : actuals) {
                Integer actualType=(Integer) actual.get("type");
                if(actualType==3&&type==1){
                    type=4;
                }else if(actualType==3&&type==3){
                    type=5;
                }else{
                    type=2;
                }
            }

        }
        return type;
    }
}













