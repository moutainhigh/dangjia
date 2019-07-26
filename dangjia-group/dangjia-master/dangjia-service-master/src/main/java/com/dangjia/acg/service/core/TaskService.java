package com.dangjia.acg.service.core;

import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.core.ButtonDTO;
import com.dangjia.acg.dto.core.Task;
import com.dangjia.acg.mapper.core.IHouseFlowApplyMapper;
import com.dangjia.acg.mapper.core.IHouseFlowMapper;
import com.dangjia.acg.mapper.core.IHouseWorkerMapper;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.design.IDesignBusinessOrderMapper;
import com.dangjia.acg.mapper.house.IHouseExpendMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.repair.IChangeOrderMapper;
import com.dangjia.acg.mapper.repair.IMendDeliverMapper;
import com.dangjia.acg.mapper.repair.IMendOrderMapper;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.core.HouseFlowApply;
import com.dangjia.acg.modle.core.HouseWorker;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.design.DesignBusinessOrder;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.house.HouseExpend;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.repair.ChangeOrder;
import com.dangjia.acg.modle.repair.MendDeliver;
import com.dangjia.acg.modle.repair.MendOrder;
import com.dangjia.acg.service.house.MyHouseService;
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
    private IHouseFlowApplyMapper houseFlowApplyMapper;
    @Autowired
    private IMendOrderMapper mendOrderMapper;
    @Autowired
    private IHouseExpendMapper houseExpendMapper;
    @Autowired
    private HouseFlowApplyService houseFlowApplyService;
    @Autowired
    private IMendDeliverMapper mendDeliverMapper;
    @Autowired
    private CraftsmanConstructionService constructionService;
    @Autowired
    private IChangeOrderMapper changeOrderMapper;
    @Autowired
    private IDesignBusinessOrderMapper designBusinessOrderMapper;
    @Autowired
    private MyHouseService myHouseService;

    @Autowired
    private IHouseWorkerMapper houseWorkerMapper;
    /**
     * 任务列表
     */
    public ServerResponse getTaskList(String userToken, Integer userRole) {
        if (userRole == null) {
            userRole = 1;
        }
        ButtonDTO buttonDTO = new ButtonDTO();
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            buttonDTO.setState(0);
            return ServerResponse.createBySuccess("查询成功", buttonDTO);
        }
        Member member = (Member) object;
        String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
        String address = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class);
        String houseId = null;
        //大管家
        if (userRole == 2) {
            object = constructionService.getHouseWorker(null, member.getId());
            if (object instanceof HouseWorker) {
                HouseWorker hw = (HouseWorker) object;
                houseId = hw.getHouseId();
                buttonDTO.setHouseId(houseId);
                buttonDTO.setTaskList(getWorkerTask(houseId, userToken, member, imageAddress, address));
            }
        } else {
            List<House> houseList = houseMapper.selectByExample(myHouseService.getHouseExample(member.getId()));
            //初始化花费
            for (House house : houseList) {
                HouseExpend houseExpend = houseExpendMapper.getByHouseId(house.getId());
                if (houseExpend == null) {
                    houseExpend = new HouseExpend(true);
                    houseExpend.setHouseId(house.getId());
                    houseExpendMapper.insert(houseExpend);
                }
            }
            //业主待处理任务
            if (houseList.size() > 1) {
                buttonDTO.setState(2);
                for (House house : houseList) {
                    if (house.getVisitState() == 0) {//0待确认开工,1装修中,2休眠中,3已完工
                        buttonDTO.setState(3);
                        buttonDTO.setHouseType(house.getHouseType());
                        buttonDTO.setDrawings(house.getDrawings());
                    }
                    if (house.getIsSelect() == 1) {//当前选中且开工
                        houseId = house.getId();
                    }
                }
                if (houseId == null) {//有很多房子但是没有isSelect为1的
                    houseId = houseList.get(0).getId();
                }
                buttonDTO.setHouseId(houseId);
                //业主
                buttonDTO.setTaskList(getTask(houseId, userToken, imageAddress, address));
            } else if (houseList.size() == 1) {
                buttonDTO = this.getButton(houseList.get(0).getId(), userToken, imageAddress, address);
            } else {
                buttonDTO.setState(0);
            }
        }
        return ServerResponse.createBySuccess("查询成功", buttonDTO);
    }

    /**
     * 回访状态
     */
    private ButtonDTO getButton(String houseId, String userToken, String imageAddress, String address) {
        ButtonDTO button = new ButtonDTO();
        House house = houseMapper.selectByPrimaryKey(houseId);
        if (house.getVisitState() == 0) {//处于回访阶段
            button.setState(1);
            button.setHouseType(house.getHouseType());
            button.setDrawings(house.getDrawings());
        } else {//开工状态
            button.setState(2);
            button.setHouseType(house.getHouseType());
            button.setDrawings(house.getDrawings());
            button.setHouseId(houseId);
            button.setTaskList(getTask(houseId, userToken, imageAddress, address));
        }
        return button;
    }

    /**
     * 工匠任务列表 需加上补货补人工任务
     * type 1支付任务,2补货补人工,3其它任务
     */
    private List<Task> getWorkerTask(String houseId, String userToken, Member worker, String imageAddress, String address) {
        House house = houseMapper.selectByPrimaryKey(houseId);
        List<Task> taskList = new ArrayList<>();
        if (worker.getWorkerType() == null || worker.getWorkerType() < 3) {
            return taskList;
        }
        if (worker.getWorkerType() == 3) {
            //退材料退服务
            Example example = new Example(MendDeliver.class);
            example.createCriteria().andEqualTo(MendDeliver.HOUSE_ID, houseId)
                    .andEqualTo(MendDeliver.SHIPPING_STATE, 0);
            List<MendDeliver> mendDeliverList = mendDeliverMapper.selectByExample(example);
            for (MendDeliver mendDeliver : mendDeliverList) {
                String productType = "0";
                MendOrder mendOrder = mendOrderMapper.selectByPrimaryKey(mendDeliver.getMendOrderId());
                if (mendOrder == null)
                    continue;
                WorkerType workerType = workerTypeMapper.selectByPrimaryKey(mendOrder.getWorkerTypeId());
                if (workerType == null)
                    continue;
                Task task = new Task();
                task.setDate(DateUtil.dateToString(mendOrder.getModifyDate(), DateUtil.FORMAT11));
                task.setName("退材料待审核处理");
                if (workerType.getType() == 3) {
                    productType = "1";
                    task.setName("退服务待审核处理");
                }
                task.setImage(imageAddress + "icon/buchailiao.png");
                String url = address + String.format(DjConstants.YZPageAddress.TUIPRODUCTEXAMINE, userToken, house.getCityId(), task.getName()) + "&mendDeliverId=" + mendDeliver.getId() + "&productType=" + productType + "&houseId=" + mendOrder.getHouseId();
                task.setHtmlUrl(url);
                task.setType(4);
                task.setTaskId(mendDeliver.getId());
                taskList.add(task);
            }
            //验收任务
            List<HouseFlowApply> houseFlowApplyList = houseFlowApplyMapper.getSupervisorCheckList(houseId);
            for (HouseFlowApply houseFlowApply : houseFlowApplyList) {
                WorkerType workerType = workerTypeMapper.selectByPrimaryKey(houseFlowApply.getWorkerTypeId());
                if (workerType == null)
                    continue;
                Task task = new Task();
                task.setDate(DateUtil.dateToString(houseFlowApply.getModifyDate(), DateUtil.FORMAT11));
                if (houseFlowApply.getApplyType() == 1) {
                    task.setName(workerType.getName() + "阶段完工待验收");
                }
                if (houseFlowApply.getApplyType() == 2) {
                    task.setName(workerType.getName() + "整体完工待验收");
                }
                task.setImage(imageAddress + "icon/chaichu.png");
                task.setHtmlUrl(address + String.format(DjConstants.GJPageAddress.COMFIRMAPPLY + "&houseFlowApplyId=%s",
                        userToken, house.getCityId(), "验收工匠完工申请", houseFlowApply.getId()));
                task.setType(3);
                task.setTaskId("");
                taskList.add(task);
            }

            //补退人工任务
            example = new Example(ChangeOrder.class);
            example.createCriteria().andEqualTo(ChangeOrder.HOUSE_ID, houseId).andEqualTo(ChangeOrder.STATE, 0);
            List<ChangeOrder> changeOrders = changeOrderMapper.selectByExample(example);
            for (ChangeOrder changeOrder : changeOrders) {
                WorkerType workerType = workerTypeMapper.selectByPrimaryKey(changeOrder.getWorkerTypeId());
                if (workerType == null)
                    continue;
                Task task = new Task();
                task.setDate(DateUtil.dateToString(changeOrder.getModifyDate(), DateUtil.FORMAT11));
                String reMark = "4";
                if (changeOrder.getType() == 1) {
                    task.setName(workerType.getName() + "补人工");
                    reMark = "4";
                }
                if (changeOrder.getType() == 2) {
                    task.setName(workerType.getName() + "退人工");
                    reMark = "5";
                }
                task.setImage(imageAddress + "icon/burengong.png");
                task.setHtmlUrl(address + String.format(DjConstants.GJPageAddress.BTPEOPLE + "&workerTypeId=%s&changeOrderId=%s&reMark=%s&houseId=%s",
                        userToken, house.getCityId(), "填写变更数量", changeOrder.getWorkerTypeId(), changeOrder.getId(), reMark, changeOrder.getHouseId()));
                task.setType(2);
                task.setTaskId("");
                taskList.add(task);
            }
        } else {
            //退人工任务
            Example example = new Example(ChangeOrder.class);
            example.createCriteria().andEqualTo(ChangeOrder.HOUSE_ID, houseId)
                    .andEqualTo(ChangeOrder.STATE, 2)
                    .andEqualTo(ChangeOrder.TYPE, 2)
                    .andEqualTo(ChangeOrder.WORKER_TYPE_ID, worker.getWorkerTypeId());
            List<ChangeOrder> changeOrders = changeOrderMapper.selectByExample(example);
            for (ChangeOrder changeOrder : changeOrders) {
                example = new Example(MendOrder.class);
                example.createCriteria().andEqualTo(MendOrder.HOUSE_ID, changeOrder.getHouseId()).andEqualTo(MendOrder.TYPE, 3)
                        .andEqualTo(MendOrder.CHANGE_ORDER_ID, changeOrder.getId());
                List<MendOrder> mendOrderList = mendOrderMapper.selectByExample(example);
                if (mendOrderList.size() > 0) {
                    MendOrder mendOrder = mendOrderList.get(0);
                    WorkerType workerType = workerTypeMapper.selectByPrimaryKey(changeOrder.getWorkerTypeId());
                    if (workerType == null)
                        continue;
                    Task task = new Task();
                    task.setDate(DateUtil.dateToString(changeOrder.getModifyDate(), DateUtil.FORMAT11));
                    task.setName(workerType.getName() + "退人工");
                    task.setImage(imageAddress + "icon/burengong.png");
                    String url = address + String.format(DjConstants.GJPageAddress.REFUNDITEMDETAIL, userToken, house.getCityId(), task.getName()) + "&type=" + mendOrder.getType() + "&mendOrderId=" + mendOrder.getId() + "&roleType=3&state=" + mendOrder.getState();
                    task.setHtmlUrl(url);
                    task.setType(2);
                    task.setTaskId("");
                    taskList.add(task);
                }
            }
        }
        return taskList;
    }

    /**
     * 任务列表 需加上补货补人工任务
     * type 1支付任务,2补货补人工,3其它任务
     */
    private List<Task> getTask(String houseId, String userToken, String imageAddress, String address) {
        House house = houseMapper.selectByPrimaryKey(houseId);
        List<Task> taskList = new ArrayList<>();
        if (house.getVisitState() == 4) {
            return taskList;
        }
        //查询待支付工序
        Example example = new Example(HouseFlow.class);
        example.createCriteria().andEqualTo(HouseFlow.WORK_TYPE, 3).andEqualTo(HouseFlow.HOUSE_ID, houseId)
                .andNotEqualTo(HouseFlow.STATE, 2);
        List<HouseFlow> houseFlowList = houseFlowMapper.selectByExample(example);
        for (HouseFlow houseFlow : houseFlowList) {
            WorkerType workerType = workerTypeMapper.selectByPrimaryKey(houseFlow.getWorkerTypeId());
            HouseWorker hw = houseWorkerMapper.getByWorkerTypeId(houseFlow.getHouseId(), houseFlow.getWorkerTypeId(),1);
            Task task = new Task();
            task.setDate(DateUtil.dateToString(hw.getModifyDate(), DateUtil.FORMAT11));
            task.setName(workerType.getName() + "待支付");
            task.setImage(imageAddress + "icon/chaichu.png");
            task.setHtmlUrl("");
            task.setType(1);
            task.setTaskId(houseFlow.getId());
            taskList.add(task);
        }
        //补材料补服务
        example = new Example(MendOrder.class);
        example.createCriteria().andEqualTo(MendOrder.HOUSE_ID, houseId).andEqualTo(MendOrder.TYPE, 0)
                .andEqualTo(MendOrder.STATE, 1);//补材料审核状态全通过
        List<MendOrder> mendOrderList = mendOrderMapper.selectByExample(example);
        for (MendOrder mendOrder : mendOrderList) {
            String productType = "0";
            WorkerType workerType = workerTypeMapper.selectByPrimaryKey(mendOrder.getWorkerTypeId());
            Task task = new Task();
            task.setDate(DateUtil.dateToString(mendOrder.getModifyDate(), DateUtil.FORMAT11));
            task.setName(workerType.getName() + "补材料审核");
            if (workerType.getType() == 3) {
                task.setName(workerType.getName() + "补服务审核");
                productType = "1";
            }
            task.setImage(imageAddress + "icon/buchailiao.png");
            String url = address + String.format(DjConstants.GJPageAddress.REFUNDITEMDETAIL, userToken, house.getCityId(), task.getName()) + "&type=0&mendOrderId=" + mendOrder.getId() + "&productType=" + productType + "&roleType=1&state=" + mendOrder.getState();
            task.setHtmlUrl(url);
            task.setType(3);
            task.setTaskId(mendOrder.getId());
            taskList.add(task);
        }
        //补人工任务
        example = new Example(MendOrder.class);
        example.createCriteria().andEqualTo(MendOrder.HOUSE_ID, houseId).andEqualTo(MendOrder.TYPE, 1)
                .andEqualTo(MendOrder.STATE, 1);//审核状态
        mendOrderList = mendOrderMapper.selectByExample(example);
        for (MendOrder mendOrder : mendOrderList) {
            ChangeOrder changeOrder = changeOrderMapper.selectByPrimaryKey(mendOrder.getChangeOrderId());
            if (changeOrder.getState() == 2) {//大管家已经同意
                WorkerType workerType = workerTypeMapper.selectByPrimaryKey(mendOrder.getWorkerTypeId());
                Task task = new Task();
                task.setDate(DateUtil.dateToString(mendOrder.getModifyDate(), DateUtil.FORMAT11));
                task.setName(workerType.getName() + "补人工审核");
                task.setImage(imageAddress + "icon/burengong.png");
                String url = address + String.format(DjConstants.GJPageAddress.REFUNDITEMDETAIL, userToken, house.getCityId(), task.getName()) + "&type=0&mendOrderId=" + mendOrder.getId() + "&roleType=1&state=" + mendOrder.getState();
                task.setHtmlUrl(url);
                task.setType(3);
                task.setTaskId(mendOrder.getId());
                taskList.add(task);
            }
        }
        //设计审核任务
        boolean isDesigner = false;
        if (house.getDesignerOk() == 3) {
            Example example1 = new Example(DesignBusinessOrder.class);
            example1.createCriteria()
                    .andEqualTo(DesignBusinessOrder.DATA_STATUS, 0)
                    .andEqualTo(DesignBusinessOrder.HOUSE_ID, house.getId())
                    .andEqualTo(DesignBusinessOrder.STATUS, 1)
                    .andNotEqualTo(DesignBusinessOrder.OPERATION_STATE, 2)
                    .andEqualTo(DesignBusinessOrder.TYPE, 4);
            List<DesignBusinessOrder> designBusinessOrders = designBusinessOrderMapper.selectByExample(example1);
            if (designBusinessOrders != null && designBusinessOrders.size() > 0) {
                DesignBusinessOrder order = designBusinessOrders.get(0);
                if (order.getOperationState() == 1) {
                    isDesigner = true;
                }
            }
        }
        if (isDesigner || house.getDesignerOk() == 5 || house.getDesignerOk() == 2) {
            Task task = new Task();
            task.setDate(DateUtil.dateToString(house.getModifyDate(), DateUtil.FORMAT11));
            task.setName(house.getDesignerOk() == 5 ? "平面图审核" : "施工图审核");
            task.setImage(imageAddress + "icon/sheji.png");
            String url = address + String.format(DjConstants.YZPageAddress.DESIGNLIST, userToken, house.getCityId(), task.getName()) + "&houseId=" + house.getId();
            task.setHtmlUrl(url);
            task.setType(3);
            task.setTaskId("");
            taskList.add(task);
        }
        //精算审核任务
        if (house.getBudgetOk() == 2) {
            Task task = new Task();
            task.setDate(DateUtil.dateToString(house.getModifyDate(), DateUtil.FORMAT11));
            task.setName("精算审核");
            task.setImage(imageAddress + "icon/jingsuan.png");
            String url = address + String.format(DjConstants.YZPageAddress.CONFIRMACTUARY, userToken, house.getCityId(), "精算审核") + "&houseId=" + house.getId();
            task.setHtmlUrl(url);
            task.setType(3);
            task.setTaskId("");
            taskList.add(task);
        }
        //验收任务
        List<HouseFlowApply> houseFlowApplyList = houseFlowApplyMapper.getMemberCheckList(houseId);
        for (HouseFlowApply houseFlowApply : houseFlowApplyList) {
            if (houseFlowApply.getApplyType() == 0) {
                houseFlowApplyService.checkWorker(houseFlowApply.getId(), false);
                continue;
            }
            WorkerType workerType = workerTypeMapper.selectByPrimaryKey(houseFlowApply.getWorkerTypeId());
            Task task = new Task();
            task.setDate(DateUtil.dateToString(houseFlowApply.getModifyDate(), DateUtil.FORMAT11));
            if (houseFlowApply.getApplyType() == 0) {
                task.setName(workerType.getName() + "每日完工待验收");
            } else if (houseFlowApply.getApplyType() == 1) {
                task.setName(workerType.getName() + "阶段完工待验收");
            } else if (houseFlowApply.getApplyType() == 2) {
                task.setName(workerType.getName() + "整体完工待验收");
            }
            task.setImage(imageAddress + "icon/chaichu.png");
            task.setHtmlUrl(address + String.format(DjConstants.YZPageAddress.CONFIRMAPPLY + "&houseFlowApplyId=%s",
                    userToken, house.getCityId(), "验收工匠完工申请", houseFlowApply.getId()));
            task.setType(3);
            task.setTaskId("");
            taskList.add(task);
        }
        return taskList;
    }
}



















