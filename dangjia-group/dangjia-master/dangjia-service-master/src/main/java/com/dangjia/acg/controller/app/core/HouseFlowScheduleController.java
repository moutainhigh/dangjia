package com.dangjia.acg.controller.app.core;

import com.dangjia.acg.api.app.core.HouseFlowScheduleAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.core.HouseFlowScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * author: Ronalcheng
 * Date: 2018/11/26 0026
 * Time: 20:18
 */
@RestController
public class HouseFlowScheduleController implements HouseFlowScheduleAPI {

    @Autowired
    private HouseFlowScheduleService houseFlowScheduleService;


    /**
     * 工程日历工序列表
     * @param houseId 房子ID
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getHouseFlows(String houseId){

        return houseFlowScheduleService.getHouseFlows(houseId);
    }


    /**
     * 设置指定工序的工期
     * @param houseFlowId 工序ID
     * @param startDate 工期开始时间
     * @param endDate   工期结束时间
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse setHouseFlowSchedule(String  houseFlowId, Date startDate, Date endDate){

        return houseFlowScheduleService.setHouseFlowSchedule(houseFlowId,startDate,endDate);
    }


    /**
     * 生产工程日历
     * @param houseId 房子ID
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse makeCalendar(String houseId){

        return houseFlowScheduleService.makeCalendar(houseId);
    }


    /**
     * 查看日历
     * @param houseId 房子ID
     * @param day 指定哪天
     * @return   {date:'2019-05-01',type:1}
     *   type: 1,正常;2,特殊;3,其他;4,正常+特殊;5,其他+特殊
     */
    @Override
    @ApiMethod
    public ServerResponse viewCalendar(String houseId,Date day){
        return houseFlowScheduleService.viewCalendar(houseId,day);
    }

}
