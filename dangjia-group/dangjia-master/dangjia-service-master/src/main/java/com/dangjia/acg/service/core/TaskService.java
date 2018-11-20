package com.dangjia.acg.service.core;

import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.core.ButtonDTO;
import com.dangjia.acg.dto.core.Task;
import com.dangjia.acg.mapper.core.IHouseFlowMapper;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.member.Member;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2018/11/5 0005
 * Time: 20:45
 */
@Service
public class TaskService {

    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IHouseFlowMapper houseFlowMapper;
    @Autowired
    private IWorkerTypeMapper workerTypeMapper;
    @Autowired
    private IHouseMapper houseMapper;
    @Autowired
    private RedisClient redisClient;


    /*
     * 任务列表
     */
    public ServerResponse getTaskList(String userToken){
        ButtonDTO buttonDTO = new ButtonDTO();
        if(StringUtils.isEmpty(userToken)){
            buttonDTO.setState(0);
            return ServerResponse.createBySuccess("查询成功", buttonDTO);
        }
        AccessToken accessToken = redisClient.getCache(userToken+ Constants.SESSIONUSERID,AccessToken.class);
        Member member = accessToken.getMember();

        //该城市该用户所有房产
        Example example = new Example(House.class);
        example.createCriteria().andEqualTo("memberId", member.getId());
        List<House> houseList = houseMapper.selectByExample(example);
        String houseId = null;
        if (houseList.size() > 1){
            buttonDTO.setState(2);
            for (House house : houseList){
                if (house.getVisitState() != 1){
                    buttonDTO.setState(3);
                    buttonDTO.setHouseType(house.getHouseType());
                    buttonDTO.setDrawings(house.getDrawings());
                }
                if(house.getIsSelect() == 1 && house.getVisitState() == 1){//当前选中且开工
                    houseId = house.getId();
                }
            }
            if(houseId == null){//有很多房子但是没有isSelect为1的
                houseId = houseList.get(0).getId();
            }
            buttonDTO.setHouseId(houseId);
            buttonDTO.setTaskList(getTask(houseId,userToken));
        }else if(houseList.size() == 1){
            buttonDTO = getButton(houseList.get(0).getId(),userToken);
        }else{
            buttonDTO.setState(0);
        }
        return ServerResponse.createBySuccess("查询成功", buttonDTO);
    }

    private ButtonDTO getButton(String houseId,String userToken){
        ButtonDTO button = new ButtonDTO();
        House house = houseMapper.selectByPrimaryKey(houseId);
        if(house.getVisitState() != 1){//处于回访阶段
            button.setState(1);
            button.setHouseType(house.getHouseType());
            button.setDrawings(house.getDrawings());
        }else{//开工状态
            button.setState(2);
            button.setHouseType(house.getHouseType());
            button.setDrawings(house.getDrawings());
            button.setHouseId(houseId);
            button.setTaskList(getTask(houseId,userToken));
        }
        return button;
    }

    /*
     * 任务列表 需加上补货补人工任务  设计审核任务 精算审核任务 验收任务
     */
    private List<Task> getTask(String houseId,String userToken){
        House house = houseMapper.selectByPrimaryKey(houseId);
        List<Task> taskList = new ArrayList<Task>();
        //查询待支付工序
        Example example = new Example(HouseFlow.class);
        example.createCriteria().andEqualTo("workType", 3).andEqualTo("houseId", houseId);
        List<HouseFlow> houseFlowList = houseFlowMapper.selectByExample(example);
        for(HouseFlow houseFlow : houseFlowList){
            WorkerType workerType = workerTypeMapper.selectByPrimaryKey(houseFlow.getWorkerTypeId());
            Task task = new Task();
            task.setDate(DateUtil.dateToString(houseFlow.getModifyDate(),"yyyy-MM-dd HH:mm"));
            task.setName(workerType.getName()+"待支付");
            task.setImage("");
            task.setHtmlUrl("");
            task.setType(1);
            task.setTaskId(houseFlow.getId());
            taskList.add(task);
        }
        //设计审核任务
        if (house.getDesignerOk() == 5){
            Task task = new Task();
            task.setDate(DateUtil.dateToString(house.getModifyDate(),"yyyy-MM-dd HH:mm"));
            task.setName("平面图审核");
            task.setImage("");
            task.setHtmlUrl(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_APP_ADDRESS, String.class)
                    +"designList?userToken="+userToken+"&houseId="+houseId+"&title=平面图审核");
            task.setType(3);
            task.setTaskId("");
            taskList.add(task);
        }
        if (house.getDesignerOk() == 2){
            Task task = new Task();
            task.setDate(DateUtil.dateToString(house.getModifyDate(),"yyyy-MM-dd HH:mm"));
            task.setName("施工图审核");
            task.setImage("");
            task.setHtmlUrl(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_APP_ADDRESS, String.class)
                    +"designList?userToken="+userToken+"&houseId="+houseId+"&title=施工图审核");
            task.setType(3);
            task.setTaskId("");
            taskList.add(task);
        }
        //精算审核任务
        if (house.getBudgetOk() == 2){
            Task task = new Task();
            task.setDate(DateUtil.dateToString(house.getModifyDate(),"yyyy-MM-dd HH:mm"));
            task.setName("精算审核");
            task.setImage("");
            task.setHtmlUrl(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_APP_ADDRESS, String.class)
                    +"confirmActuary?userToken="+userToken+"&cityId="+house.getCityId()+"&houseId="+houseId+"&title=精算审核");
            task.setType(3);
            task.setTaskId("");
            taskList.add(task);
        }
        //验收任务
        //补材料补服务补人工任务
        return taskList;
    }
}



















