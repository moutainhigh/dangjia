package com.dangjia.acg.service.core;

import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.core.ButtonDTO;
import com.dangjia.acg.dto.core.Task;
import com.dangjia.acg.mapper.core.IHouseFlowApplyMapper;
import com.dangjia.acg.mapper.core.IHouseFlowMapper;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.house.IHouseExpendMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.repair.IMendOrderMapper;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.core.HouseFlowApply;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.house.HouseExpend;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.repair.MendOrder;
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
    @Autowired
    private IHouseFlowApplyMapper houseFlowApplyMapper;
    @Autowired
    private IMendOrderMapper mendOrderMapper;
    @Autowired
    private IHouseExpendMapper houseExpendMapper;


    /**
     * 任务列表
     */
    public ServerResponse getTaskList(String userToken) {
        ButtonDTO buttonDTO = new ButtonDTO();
        if (StringUtils.isEmpty(userToken)) {
            buttonDTO.setState(0);
            return ServerResponse.createBySuccess("查询成功", buttonDTO);
        }
        AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
        Member member = accessToken.getMember();

        //该城市该用户所有房产
        Example example = new Example(House.class);
        example.createCriteria().andEqualTo(House.MEMBER_ID, member.getId());
        List<House> houseList = houseMapper.selectByExample(example);
        for(House house : houseList){
            //if(house.getType() == 2){//老用户
                HouseExpend houseExpend = houseExpendMapper.getByHouseId(house.getId());
                if (houseExpend == null){
                    houseExpend = new HouseExpend(true);
                    houseExpend.setHouseId(house.getId());
                    houseExpendMapper.insert(houseExpend);
                }
            //}
        }
        String houseId = null;
        if (houseList.size() > 1) {
            buttonDTO.setState(2);
            for (House house : houseList) {
                if (house.getVisitState() != 1) {
                    buttonDTO.setState(3);
                    buttonDTO.setHouseType(house.getHouseType());
                    buttonDTO.setDrawings(house.getDrawings());
                }
                if (house.getIsSelect() == 1 && house.getVisitState() == 1) {//当前选中且开工
                    houseId = house.getId();
                }
            }
            if (houseId == null) {//有很多房子但是没有isSelect为1的
                houseId = houseList.get(0).getId();
            }
            buttonDTO.setHouseId(houseId);
            buttonDTO.setTaskList(getTask(houseId, userToken));
        } else if (houseList.size() == 1) {
            buttonDTO = getButton(houseList.get(0).getId(), userToken);
        } else {
            buttonDTO.setState(0);
        }
        return ServerResponse.createBySuccess("查询成功", buttonDTO);
    }

    /**
     * 回访状态
     */
    private ButtonDTO getButton(String houseId, String userToken) {
        ButtonDTO button = new ButtonDTO();
        House house = houseMapper.selectByPrimaryKey(houseId);
        if (house.getVisitState() != 1) {//处于回访阶段
            button.setState(1);
            button.setHouseType(house.getHouseType());
            button.setDrawings(house.getDrawings());
        } else {//开工状态
            button.setState(2);
            button.setHouseType(house.getHouseType());
            button.setDrawings(house.getDrawings());
            button.setHouseId(houseId);
            button.setTaskList(getTask(houseId, userToken));
        }
        return button;
    }

    /**
     * 任务列表 需加上补货补人工任务
     * type 1支付任务,2补货补人工,3其它任务
     */
    private List<Task> getTask(String houseId, String userToken) {
        House house = houseMapper.selectByPrimaryKey(houseId);
        List<Task> taskList = new ArrayList<Task>();
        //查询待支付工序
        Example example = new Example(HouseFlow.class);
        example.createCriteria().andEqualTo(HouseFlow.WORK_TYPE, 3).andEqualTo(HouseFlow.HOUSE_ID, houseId);
        List<HouseFlow> houseFlowList = houseFlowMapper.selectByExample(example);
        for (HouseFlow houseFlow : houseFlowList) {
            WorkerType workerType = workerTypeMapper.selectByPrimaryKey(houseFlow.getWorkerTypeId());
            Task task = new Task();
            task.setDate(DateUtil.dateToString(houseFlow.getModifyDate(), "yyyy-MM-dd HH:mm"));
            task.setName(workerType.getName() + "待支付");
            task.setImage(configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class) + "icon/chaichu.png");
            task.setHtmlUrl("");
            task.setType(1);
            task.setTaskId(houseFlow.getId());
            taskList.add(task);
        }
        //补材料补服务
        example = new Example(MendOrder.class);
        example.createCriteria().andEqualTo(MendOrder.HOUSE_ID, houseId).andEqualTo(MendOrder.TYPE, 0)
                .andEqualTo(MendOrder.STATE, 3);//补材料审核状态全通过
        List<MendOrder> mendOrderList = mendOrderMapper.selectByExample(example);
        for (MendOrder mendOrder : mendOrderList){
            WorkerType workerType = workerTypeMapper.selectByPrimaryKey(mendOrder.getWorkerTypeId());
            Task task = new Task();
            task.setDate(DateUtil.dateToString(mendOrder.getModifyDate(), "yyyy-MM-dd HH:mm"));
            task.setName(workerType.getName() + "补材料");
            task.setImage(configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class) + "icon/buchailiao.png");
            task.setHtmlUrl("");
            task.setType(2);
            task.setTaskId(mendOrder.getId());
            taskList.add(task);
        }
        //补人工任务
        example = new Example(MendOrder.class);
        example.createCriteria().andEqualTo(MendOrder.HOUSE_ID, houseId).andEqualTo(MendOrder.TYPE, 1)
                .andEqualTo(MendOrder.STATE, 3);//审核状态
        mendOrderList = mendOrderMapper.selectByExample(example);
        for (MendOrder mendOrder : mendOrderList){
            WorkerType workerType = workerTypeMapper.selectByPrimaryKey(mendOrder.getWorkerTypeId());
            Task task = new Task();
            task.setDate(DateUtil.dateToString(mendOrder.getModifyDate(), "yyyy-MM-dd HH:mm"));
            task.setName(workerType.getName() + "补人工");
            task.setImage(configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class) + "icon/burengong.png");
            task.setHtmlUrl("");
            task.setType(2);
            task.setTaskId(mendOrder.getId());
            taskList.add(task);
        }

        //设计审核任务
        if (house.getDesignerOk() == 5) {
            Task task = new Task();
            task.setDate(DateUtil.dateToString(house.getModifyDate(), "yyyy-MM-dd HH:mm"));
            task.setName("平面图审核");
            task.setImage(configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class) + "icon/sheji.png");
            String url =configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class) +  String.format(DjConstants.YZPageAddress.DESIGNLIST, userToken, house.getCityId(), "平面图审核") + "&houseId=" + house.getId();
            task.setHtmlUrl(url);
            task.setType(3);
            task.setTaskId("");
            taskList.add(task);
        }
        if (house.getDesignerOk() == 2) {
            Task task = new Task();
            task.setDate(DateUtil.dateToString(house.getModifyDate(), "yyyy-MM-dd HH:mm"));
            task.setName("施工图审核");
            task.setImage(configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class) + "icon/sheji.png");
            String url = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class) + String.format(DjConstants.YZPageAddress.DESIGNLIST, userToken, house.getCityId(), "施工图审核") + "&houseId=" + house.getId();
            task.setHtmlUrl(url);
            task.setType(3);
            task.setTaskId("");
            taskList.add(task);
        }
        //精算审核任务
        if (house.getBudgetOk() == 2) {
            Task task = new Task();
            task.setDate(DateUtil.dateToString(house.getModifyDate(), "yyyy-MM-dd HH:mm"));
            task.setName("精算审核");
            task.setImage(configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class) + "icon/jingsuan.png");
            String url = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class) + String.format(DjConstants.YZPageAddress.CONFIRMACTUARY, userToken, house.getCityId(), "精算审核") + "&houseId=" + house.getId();
            task.setHtmlUrl(url);
            task.setType(3);
            task.setTaskId("");
            taskList.add(task);
        }
        //验收任务
        List<HouseFlowApply> houseFlowApplyList = houseFlowApplyMapper.getMemberCheckList(houseId);
        for (HouseFlowApply houseFlowApply : houseFlowApplyList) {
            WorkerType workerType = workerTypeMapper.selectByPrimaryKey(houseFlowApply.getWorkerTypeId());
            Task task = new Task();
            task.setDate(DateUtil.dateToString(houseFlowApply.getModifyDate(), "yyyy-MM-dd HH:mm"));
            if (houseFlowApply.getApplyType() == 0) {
                task.setName(workerType.getName() + "每日完工待验收");
            } else if (houseFlowApply.getApplyType() == 1) {
                task.setName(workerType.getName() + "阶段完工待验收");
            } else if (houseFlowApply.getApplyType() == 2) {
                task.setName(workerType.getName() + "整体完工待验收");
            }
            task.setImage(configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class) + "icon/chaichu.png");
            task.setHtmlUrl(configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class) + String.format(DjConstants.YZPageAddress.CONFIRMAPPLY + "&houseFlowApplyId=%s",
                    userToken, house.getCityId(), "验收工匠完工申请", houseFlowApply.getId()));
            task.setType(3);
            task.setTaskId("");
            taskList.add(task);
        }

        return taskList;
    }
}



















