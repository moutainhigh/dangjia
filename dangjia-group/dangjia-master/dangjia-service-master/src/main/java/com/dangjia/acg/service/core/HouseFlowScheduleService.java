package com.dangjia.acg.service.core;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.mapper.core.IHouseFlowApplyMapper;
import com.dangjia.acg.mapper.core.IHouseFlowMapper;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.house.House;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private IHouseFlowMapper houseFlowMapper;
    @Autowired
    private IMemberMapper memberMapper;
    @Autowired
    private IWorkerTypeMapper workerTypeMapper;
    @Autowired
    private IHouseMapper houseMapper;
    @Autowired
    private ConfigUtil configUtil;


    /**
     * 工程日历工序列表
     * @param houseId 房子ID
     * @return
     */
    public ServerResponse getHouseFlows(String houseId){
        List<HouseFlow> houseFlowList=houseFlowMapper.getForCheckMoney(houseId);
        Map mapObj=new HashMap();
        int numall=0;
        List<Map> houseFlowMap=new ArrayList<>();
        for (HouseFlow houseFlow : houseFlowList) {
            Map map =new HashMap();
            WorkerType workerType = workerTypeMapper.selectByPrimaryKey(houseFlow.getWorkerTypeId());
            map.put(HouseFlow.ID,houseFlow.getId());
            map.put(WorkerType.NAME,workerType.getName());
            map.put(HouseFlow.START_DATE,houseFlow.getStartDate());
            map.put(HouseFlow.END_DATE,houseFlow.getEndDate());
            if(houseFlow.getStartDate()!=null){
                int num = 1 + DateUtil.daysofTwo(houseFlow.getStartDate(), houseFlow.getEndDate());//逾期工期天数
                map.put("num",num);
                numall=numall+num;
            }
            houseFlowMap.add(map);
            mapObj.put("houseId",houseId);
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
            Date od = DateUtil.toDate(o);
            Map map =new HashMap();
            map.put("type",0);
            List<String> plans=new ArrayList<>();//计划记录
            List<String> actuals=new ArrayList<>();//实际记录
            for (HouseFlow houseFlow : houseFlowList) {
                WorkerType workerType = workerTypeMapper.selectByPrimaryKey(houseFlow.getWorkerTypeId());
                if(houseFlow.getStartDate()!=null&&houseFlow.getEndDate()!=null) {
                    String s = DateUtil.dateToString(houseFlow.getStartDate(), null);
                    String e = DateUtil.dateToString(houseFlow.getEndDate(), null);
                    if(s.equals(o)){
                        plans.add("当前为"+workerType.getName()+"进场日期");
                        continue;
                    }
                    if(e.equals(o)){
                        if(houseFlow.getWorkerType()==4) {
                            plans.add("当前为"+workerType.getName()+"整体完工日期");
                        }else{
                            plans.add("当前为"+workerType.getName()+"阶段完工日期");
                        }
                        continue;
                    }
                    if(houseFlow.getStartDate().getTime()>od.getTime()&&houseFlow.getEndDate().getTime()<od.getTime()){
                        plans.add("当前为"+workerType.getName()+"正常施工日期");
                        continue;
                    }
                }
            }

            map.put("date",o);
            map.put("plans",plans);
            map.put("actuals",actuals);
            mapList.add(map);
        }
        return ServerResponse.createBySuccess("查询成功",mapList);
    }
}













