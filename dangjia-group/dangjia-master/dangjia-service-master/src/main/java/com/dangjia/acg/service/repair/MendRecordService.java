package com.dangjia.acg.service.repair;

import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.refund.OrderProgressDTO;
import com.dangjia.acg.dto.repair.MendOrderDetail;
import com.dangjia.acg.mapper.core.IHouseFlowApplyMapper;
import com.dangjia.acg.mapper.core.IHouseFlowMapper;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.delivery.IMasterOrderProgressMapper;
import com.dangjia.acg.mapper.delivery.IOrderSplitItemMapper;
import com.dangjia.acg.mapper.delivery.IOrderSplitMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.house.IWarehouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.repair.*;
import com.dangjia.acg.mapper.worker.IEvaluateMapper;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.core.HouseFlowApply;
import com.dangjia.acg.modle.core.HouseWorker;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.deliver.OrderSplit;
import com.dangjia.acg.modle.deliver.OrderSplitItem;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.house.Warehouse;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.order.OrderProgress;
import com.dangjia.acg.modle.repair.*;
import com.dangjia.acg.modle.worker.Evaluate;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.dangjia.acg.service.core.HouseFlowApplyService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import java.math.BigDecimal;
import java.util.*;

/**
 * author: Ronalcheng
 * Date: 2018/12/24 0024
 * Time: 14:02
 * 补退记录
 */
@Service
public class MendRecordService {
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IMendOrderMapper mendOrderMapper;
    @Autowired
    private IMendMaterialMapper mendMaterialMapper;
    @Autowired
    private IMendWorkerMapper mendWorkerMapper;
    @Autowired
    private IOrderSplitMapper orderSplitMapper;
    @Autowired
    private IOrderSplitItemMapper orderSplitItemMapper;
    @Autowired
    private IChangeOrderMapper changeOrderMapper;
    @Autowired
    private IMendOrderCheckMapper mendOrderCheckMapper;
    @Autowired
    private IWarehouseMapper warehouseMapper;

    @Autowired
    private IHouseFlowMapper houseFlowMapper;
    @Autowired
    private IMendDeliverMapper mendDeliverMapper;
    @Autowired
    private IHouseMapper houseMapper;
    @Autowired
    private IMemberMapper memberMapper;
    @Autowired
    private CraftsmanConstructionService constructionService;
    @Autowired
    private IWorkerTypeMapper workerTypeMapper;
    @Autowired
    private IHouseFlowApplyMapper houseFlowApplyMapper;

    @Autowired
    private IEvaluateMapper evaluateMapper;
    @Autowired
    private IMasterOrderProgressMapper iMasterOrderProgressMapper;

    @Autowired
    private RedisClient redisClient;//缓存
    /**
     * 要补退明细
     * 0:补材料;1:补人工;2:退材料(剩余材料登记);3:退人工,4:业主退材料, 5 要货
     */
    public ServerResponse mendDeliverDetail(String userToken, String mendDeliverId) {
        try {
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            MendOrderDetail mendOrderDetail = new MendOrderDetail();
            mendOrderDetail.setIsShow(0);
            MendDeliver mendDeliver = mendDeliverMapper.selectByPrimaryKey(mendDeliverId);
            MendOrder mendOrder = mendOrderMapper.selectByPrimaryKey(mendDeliver.getMendOrderId());
            mendOrderDetail.setMendOrderId(mendDeliver.getMendOrderId());
            mendOrderDetail.setNumber(mendOrder.getNumber());
            mendOrderDetail.setType(mendOrder.getType());
            mendOrderDetail.setState(mendOrder.getState());//0生成中,1材料员待审核,2不通过取消,3，材料员已审核，大管家待核对,4已结算
            mendOrderDetail.setTotalAmount(mendOrder.getTotalAmount());
            mendOrderDetail.setCreateDate(mendOrder.getCreateDate());
            mendOrderDetail.setModifyDate(mendOrder.getModifyDate());
            mendOrderDetail.setApplicantId(mendOrder.getApplyMemberId());
            mendOrderDetail.setHouseId(mendOrder.getHouseId());
            mendOrderDetail.setApplicantName(mendDeliver.getShipName());
            mendOrderDetail.setApplicantMobile(mendDeliver.getShipMobile());
            List<Map<String, Object>> mapList = new ArrayList<>();
            Example example = new Example(MendMateriel.class);
            example.createCriteria().andEqualTo(MendMateriel.MEND_ORDER_ID, mendOrder.getId())
                    .andEqualTo(MendMateriel.REPAIR_MEND_DELIVER_ID, mendDeliver.getId());
            List<MendMateriel> mendMaterielList = mendMaterialMapper.selectByExample(example);
            for (MendMateriel mendMateriel : mendMaterielList) {
                /*统计收货数量*/
                Warehouse warehouse = warehouseMapper.getByProductId(mendMateriel.getProductId(), mendOrder.getHouseId());
                Map<String, Object> map = BeanUtils.beanToMap(mendMateriel);
                map.put("image", address + mendMateriel.getImage());
                if (mendMateriel.getProductType() == 0) {
                    map.put("productType", "材料");
                } else {
                    map.put("productType", "包工包料");
                }
                map.put("supplierTelephone", mendMateriel.getSupplierTelephone());
                map.put("productId", mendMateriel.getProductId());
                map.put("name", mendMateriel.getProductName());
                map.put("price", "¥" + String.format("%.2f", mendMateriel.getPrice()) + "/" + mendMateriel.getUnitName());
                map.put("shopCount", mendMateriel.getShopCount());//申请数量
                map.put("receive", warehouse.getReceive() - (warehouse.getWorkBack() == null ? 0D : warehouse.getWorkBack()));//申请数量
                map.put("actualCount", mendMateriel.getActualCount());//实际修改数量
                map.put("totalPrice", mendMateriel.getTotalPrice());
                mapList.add(map);
            }
            mendOrderDetail.setMapList(mapList);
            //得到房子名称及业主信息
            setMendOrder(mendOrderDetail);
            return ServerResponse.createBySuccess("查询成功", mendOrderDetail);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 审/要/补/退明细
     * 0:补材料;1:补人工;2:退材料(剩余材料登记);3:退人工,4:业主退材料, 5 要货, 6 审核进程
     */
    public ServerResponse mendOrderDetail(String userToken, String mendOrderId, Integer type) {
        try {
            Member worker = null;
            if (!CommonUtil.isEmpty(userToken)) {
                Object object = constructionService.getMember(userToken);
                if (object instanceof Member) {
                    worker = (Member) object;
                }
            }
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            MendOrderDetail mendOrderDetail = new MendOrderDetail();
            mendOrderDetail.setIsShow(0);
            mendOrderDetail.setIsAuditor(0);
            if (type == 6) {
                HouseFlowApply houseFlowApply = houseFlowApplyMapper.selectByPrimaryKey(mendOrderId);
                Member member = memberMapper.selectByPrimaryKey(houseFlowApply.getWorkerId());
                mendOrderDetail.setHouseId(houseFlowApply.getHouseId());
                if(houseFlowApply.getApplyType()!=10){//被动验收
                    mendOrderDetail.setApplicantId(houseFlowApply.getWorkerId());
                    mendOrderDetail.setApplicantName(CommonUtil.isEmpty(member.getName()) ? member.getNickName() : member.getName());
                    mendOrderDetail.setApplicantMobile(member.getMobile());
                    mendOrderDetail.setWorkerType(worker.getWorkerType());
                    WorkerType workerType = workerTypeMapper.selectByPrimaryKey(worker.getWorkerTypeId());
                    mendOrderDetail.setWorkerTypeColor(workerType.getColor());
                    mendOrderDetail.setWorkerTypeName(workerType.getName());
                }
                mendOrderDetail.setHouseFlowApplyType(houseFlowApply.getType());
                mendOrderDetail.setNumber(houseFlowApply.getId());
                mendOrderDetail.setType(6);
                mendOrderDetail.setState(houseFlowApply.getApplyType());
                mendOrderDetail.setCreateDate(houseFlowApply.getCreateDate());
                //查是否评价
                Evaluate evaluate = evaluateMapper.getForCountMoneySup(houseFlowApply.getHouseFlowId(), houseFlowApply.getApplyType(), worker.getId());
                if (evaluate == null) {
                    List<Map<String, Object>> listMap = new ArrayList<>();//返回通讯录list
                    if(houseFlowApply.getApplyType()==10){
                        House house = houseMapper.selectByPrimaryKey(houseFlowApply.getHouseId());
                        Member worker2 = memberMapper.selectByPrimaryKey(house.getMemberId());
                        Map<String, Object> map2 = new HashMap<>();
                        map2.put("workerTypeName", "业主");
                        map2.put("workerTypeColor", "#D67DAE");
                        map2.put("workerName", worker2.getNickName() == null ? worker2.getName() : worker2.getNickName());
                        map2.put("workerPhone", worker2.getMobile());
                        map2.put("workerId", worker2.getId());
                        listMap.add(map2);
                    }else{
                        House house = houseMapper.selectByPrimaryKey(houseFlowApply.getHouseId());
                        Member worker1 = memberMapper.selectByPrimaryKey(house.getMemberId());
                        Map<String, Object> map2 = new HashMap<>();
                        map2.put("workerTypeName", "业主");
                        map2.put("workerTypeColor", "#D67DAE");
                        map2.put("workerName", worker1.getNickName() == null ? worker1.getName() : worker1.getNickName());
                        map2.put("workerPhone", worker1.getMobile());
                        map2.put("workerId", worker1.getId());
                        listMap.add(map2);

                        HouseFlow houseFlow = houseFlowMapper.getByWorkerTypeId(houseFlowApply.getHouseId(),"3");
                        if(!worker.getId().equals(houseFlow.getWorkerId())) {
                            Member worker2 = memberMapper.selectByPrimaryKey(houseFlow.getWorkerId());
                            if (worker2 != null) {
                                Map<String, Object> map = new HashMap<>();
                                WorkerType workerType = workerTypeMapper.selectByPrimaryKey(worker2.getWorkerTypeId());
                                map.put("workerTypeName", workerType.getName());
                                map.put("workerTypeColor", workerType.getColor());
                                map.put("workerName", worker2.getName());
                                map.put("workerPhone", worker2.getMobile());
                                map.put("workerId", worker2.getId());
                                listMap.add(map);
                            }
                        }

                        //工匠
                        if(!worker.getId().equals(member.getId())) {
                            Map<String, Object> map = new HashMap<>();
                            WorkerType workerType = workerTypeMapper.selectByPrimaryKey(member.getWorkerTypeId());
                            map.put("workerTypeName", workerType.getName());
                            map.put("workerTypeColor", workerType.getColor());
                            map.put("workerName", member.getName());
                            map.put("workerPhone", member.getMobile());
                            map.put("workerId", member.getId());
                            listMap.add(map);
                        }
                    }
                    mendOrderDetail.setWorkerList(listMap);
                }
                mendOrderDetail.setMapList(getFlowInfo(houseFlowApply));

            } else if (type == 5) {
                OrderSplit orderSplit = orderSplitMapper.selectByPrimaryKey(mendOrderId);
                if (worker != null && worker.getWorkerTypeId() != null && worker.getWorkerTypeId().equals(orderSplit.getWorkerTypeId())) {
                    mendOrderDetail.setIsShow(1);
                }
                mendOrderDetail.setHouseId(orderSplit.getHouseId());
                mendOrderDetail.setApplicantId(orderSplit.getMemberId());
                mendOrderDetail.setApplicantName(orderSplit.getMemberName());
                mendOrderDetail.setApplicantMobile(orderSplit.getMobile());
                mendOrderDetail.setNumber(orderSplit.getNumber());
                mendOrderDetail.setMendOrderId(orderSplit.getMendNumber());
                mendOrderDetail.setType(5);
                mendOrderDetail.setState(orderSplit.getApplyStatus());
                switch (orderSplit.getApplyStatus()) {
                    case 0:
                        mendOrderDetail.setStateName("申请中");
                        break;
                    case 1:
                    case 2:
                        mendOrderDetail.setStateName("待发货");
                        break;
                    case 3:
                        mendOrderDetail.setStateName("已拒绝");
                        break;
                    case 4:
                        mendOrderDetail.setStateName("审核中");
                        break;
                    case 5:
                        mendOrderDetail.setStateName("已撤回");
                        break;
                }
                mendOrderDetail.setCreateDate(orderSplit.getCreateDate());
                /*
                计算要货单钱
                 */
                mendOrderDetail.setTotalAmount(orderSplit.getTotalAmount()!=null?orderSplit.getTotalAmount().doubleValue():0d);

                List<Map<String, Object>> mapList = new ArrayList<>();
                /*Example example = new Example(OrderSplitItem.class);
                example.createCriteria().andEqualTo(OrderSplitItem.ORDER_SPLIT_ID, orderSplit.getId());*/
                List<OrderSplitItem> orderSplitItemList = orderSplitItemMapper.selectSplitItemList(orderSplit.getId(),null);

                for (OrderSplitItem orderSplitItem : orderSplitItemList) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("image", address + orderSplitItem.getImage());
                    if (orderSplitItem.getProductType() == 0) {
                        map.put("productType", "材料");
                    } else {
                        map.put("productType", "包工包料");
                    }
                    map.put("name", orderSplitItem.getProductName());
                    map.put("productId", orderSplitItem.getProductId());
                    map.put("price", "¥" + String.format("%.2f", orderSplitItem.getPrice()) + "/" + orderSplitItem.getUnitName());
                    map.put("shopCount", orderSplitItem.getNum());//本次数量
                    map.put("repairCount", "0");
                    map.put("totalPrice", orderSplitItem.getTotalPrice());
                    if (!CommonUtil.isEmpty(orderSplit.getMendNumber())) {
                        MendMateriel mendMateriel = mendMaterialMapper.getMendOrderGoods(orderSplit.getMendNumber(), orderSplitItem.getProductId());
                        if (mendMateriel != null) {
                            map.put("repairCount", mendMateriel.getShopCount());
                        }

                    }
                    mapList.add(map);
                }
                List<Map> button = new ArrayList<>();
                if(orderSplit.getApplyStatus()==0||orderSplit.getApplyStatus()==4||
                        orderSplit.getApplyStatus()==1){
                    Map map=new HashMap();
                    map.put("buttonText","撤回货单");
                    map.put("buttonType",1);
                    map.put("buttonColour","#3B444D");
                    map.put("mendOrderId",orderSplit.getMendNumber());
                    button.add(map);
                }
                if(StringUtils.isNotBlank(orderSplit.getMendNumber())){
                    Map map=new HashMap();
                    map.put("buttonText","查看补货单");
                    map.put("buttonType",2);
                    map.put("buttonColour","#F57341");
                    map.put("mendOrderId",orderSplit.getMendNumber());
                    button.add(map);
                }
                mendOrderDetail.setButton(button);
                mendOrderDetail.setMapList(mapList);

            } else {
                MendOrder mendOrder = mendOrderMapper.selectByPrimaryKey(mendOrderId);
                House house = houseMapper.selectByPrimaryKey(mendOrder.getHouseId());
                if (mendOrder.getType() == 0 || mendOrder.getType() == 1) {
                    if (worker != null && worker.getId().equals(house.getMemberId())) {
                        mendOrderDetail.setIsAuditor(1);
                    }
                }
                if (mendOrder.getType() == 3) {
                    if (worker != null && worker.getWorkerTypeId() != null && worker.getWorkerTypeId().equals(mendOrder.getWorkerTypeId())) {
                        mendOrderDetail.setIsAuditor(1);
                    }
                }
                if (worker != null && worker.getWorkerTypeId() != null && worker.getWorkerTypeId().equals(mendOrder.getWorkerTypeId())) {
                    mendOrderDetail.setIsShow(1);
                }
                mendOrderDetail.setMendOrderId(mendOrderId);
                mendOrderDetail.setNumber(mendOrder.getNumber());
                mendOrderDetail.setType(mendOrder.getType());
                mendOrderDetail.setState(mendOrder.getState());//0生成中,1材料员待审核,2不通过取消,3，材料员已审核，大管家待核对,4已结算
                mendOrderDetail.setTotalAmount(mendOrder.getTotalAmount());
                mendOrderDetail.setCreateDate(mendOrder.getCreateDate());
                mendOrderDetail.setModifyDate(mendOrder.getModifyDate());
                mendOrderDetail.setApplicantId(mendOrder.getApplyMemberId());
                mendOrderDetail.setHouseId(mendOrder.getHouseId());
                if (!CommonUtil.isEmpty(mendOrder.getApplyMemberId())) {
                    Member member = memberMapper.selectByPrimaryKey(mendOrder.getApplyMemberId());
                    if (member != null) {
                        mendOrderDetail.setApplicantName(member.getNickName());
                        mendOrderDetail.setApplicantMobile(member.getMobile());
                    }
                }

                List<Map<String, Object>> mapList = new ArrayList<>();
                if (mendOrder.getType() == 0 || mendOrder.getType() == 2 || mendOrder.getType() == 4) {
                    List<MendMateriel> mendMaterielList = mendMaterialMapper.byMendOrderId(mendOrderId);
                    for (MendMateriel mendMateriel : mendMaterielList) {
                        Map<String, Object> map = BeanUtils.beanToMap(mendMateriel);
                        map.put("image", address + mendMateriel.getImage());
                        if (mendMateriel.getProductType() == 0) {
                            map.put("productType", "材料");
                        } else {
                            map.put("productType", "包工包料");
                        }
                        map.put("productId", mendMateriel.getProductId());
                        map.put("name", mendMateriel.getProductName());
                        map.put("price", "¥" + String.format("%.2f", mendMateriel.getPrice()) + "/" + mendMateriel.getUnitName());
                        map.put("shopCount", mendMateriel.getShopCount());
                        map.put("totalPrice", mendMateriel.getTotalPrice());
                        mapList.add(map);
                    }
                } else if (mendOrder.getType() == 1 || mendOrder.getType() == 3) {
                    List<MendWorker> mendWorkerList = mendWorkerMapper.byMendOrderId(mendOrderId);
                    for (MendWorker mendWorker : mendWorkerList) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("image", address + mendWorker.getImage());
                        map.put("productType", "人工");
                        map.put("name", mendWorker.getWorkerGoodsName());
                        map.put("price", "¥" + String.format("%.2f", mendWorker.getPrice()) + "/" + mendWorker.getUnitName());
                        map.put("shopCount", mendWorker.getShopCount());
                        map.put("totalPrice", String.format("%.2f", mendWorker.getTotalPrice()));
                        mapList.add(map);
                    }
                }
                mendOrderDetail.setMapList(mapList);
                if (mendOrder.getType() == 2 && StringUtil.isNotEmpty(mendOrder.getImageArr())) {
                    String[] imageArr = mendOrder.getImageArr().split(",");
                    if (imageArr.length > 0) {
                        List<String> imageList = new ArrayList<>();
                        for (String anImageArr : imageArr) {
                            imageList.add(address + anImageArr);
                        }
                        mendOrderDetail.setImageList(imageList);
                    }
                }
                if (mendOrder.getType() == 1 || mendOrder.getType() == 3) {//补退人工
                    ChangeOrder changeOrder = changeOrderMapper.selectByPrimaryKey(mendOrder.getChangeOrderId());
                    mendOrderDetail.setChangeOrder(changeOrder);
                }
            }
            //得到房子名称及业主信息
            setMendOrder(mendOrderDetail);
            return ServerResponse.createBySuccess("查询成功", mendOrderDetail);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    private List getFlowInfo(HouseFlowApply houseFlowApply) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        WorkerType workerType = workerTypeMapper.selectByPrimaryKey(houseFlowApply.getWorkerTypeId());
        Map<String, Object> map;
        String info;
        if (workerType.getType() == 3) {
            info = "我是" + workerType.getName() + ",我已申请了整体竣工";
            map = new HashMap<>();
            map.put("roleType", "管家");
            map.put("createDate", houseFlowApply.getCreateDate());
            map.put("info", info);//描述
            map.put("type", "1");//1=达到  0=未达到
            mapList.add(map);
        } else {
            info = "我是" + workerType.getName() + ",我已申请了" + (houseFlowApply.getApplyType() == 1 ? "阶段完工" : "整体完工");
            //工匠
            map = new HashMap<>();
            map.put("roleType", "工匠");
            map.put("createDate", houseFlowApply.getCreateDate());
            map.put("info", info);//描述
            map.put("type", "1");//1=达到  0=未达到
            mapList.add(map);
            //管家 0未审核，1审核通过，2审核不通过
            map = new HashMap<>();
            map.put("roleType", "管家");
            if (houseFlowApply.getSupervisorCheck() == 0) {
                map.put("type", "0");
                map.put("info", "未审核");
            }
            if (houseFlowApply.getSupervisorCheck() > 0) {
                map.put("createDate", houseFlowApply.getCreateDate());//默认赋值
                map.put("type", "1");
                if (houseFlowApply.getSupervisorCheck() == 1) {
                    map.put("info", "审核通过");
                    //查工匠被管家的评价
                    Evaluate evaluate = evaluateMapper.getForCountMoneySup(houseFlowApply.getHouseFlowId(), houseFlowApply.getApplyType(), houseFlowApply.getWorkerId());
                    if (evaluate != null) {
                        map.put("createDate", evaluate.getCreateDate());
                        map.put("content", evaluate.getContent());
                    }
                }
                if (houseFlowApply.getSupervisorCheck() == 2) {
                    map.put("info", "拒绝通过");
                    map.put("type", "0");
                    map.put("createDate", houseFlowApply.getModifyDate());
                    if (!info.equals(houseFlowApply.getApplyDec())) {
                        map.put("content", houseFlowApply.getApplyDec());
                    }
                }
            }
            mapList.add(map);
        }

        //业主 ,0未审核，1审核通过，2审核不通过，3自动审核，4申述中
        map = new HashMap<>();
        map.put("roleType", "业主");
        if (houseFlowApply.getMemberCheck() == 0) {
            map.put("type", "0");
            map.put("info", "未审核");
        }
        if (houseFlowApply.getMemberCheck() > 0) {
            map.put("createDate", houseFlowApply.getCreateDate());
            map.put("type", "1");

            if (houseFlowApply.getMemberCheck() == 1) {
                //查工匠被管家的评价
                Evaluate evaluate = evaluateMapper.getForCountMoney(houseFlowApply.getHouseFlowId(), houseFlowApply.getApplyType(), houseFlowApply.getWorkerId());
                if (evaluate != null) {
                    map.put("createDate", evaluate.getCreateDate());
                    map.put("content", evaluate.getContent());
                }
                map.put("info", "审核通过");
            }
            if (houseFlowApply.getMemberCheck() == 2) {
                map.put("info", "拒绝通过");
                map.put("type", "0");
                map.put("createDate", houseFlowApply.getModifyDate());
                if (!info.equals(houseFlowApply.getApplyDec())) {
                    map.put("content", houseFlowApply.getApplyDec());
                }
            }
            if (houseFlowApply.getMemberCheck() == 3) {
                map.put("info", "自动审核通过");
            }
            if (houseFlowApply.getMemberCheck() == 4) {
                map.put("info", "申述中");
            }
        }

        mapList.add(map);
        return mapList;
    }

    private void setMendOrder(MendOrderDetail mendOrderDetail) {
        if (!CommonUtil.isEmpty(mendOrderDetail.getHouseId())) {
            House house = houseMapper.selectByPrimaryKey(mendOrderDetail.getHouseId());
            if (house != null) {
                mendOrderDetail.setHouseName(house.getHouseName());//房名
                mendOrderDetail.setMemberId(house.getMemberId());//业主ID
                Member member = memberMapper.selectByPrimaryKey(house.getMemberId());
                if (member != null) {
                    mendOrderDetail.setMemberName(member.getNickName());//业主名称
                    mendOrderDetail.setMemberMobile(member.getMobile());//业主手机号
                }
            }
        }
    }

    /**
     * 记录列表
     * 1:补人工;2:退材料(剩余材料登记);3:退人工,4:业主退材料, 5 要货
     */
    public ServerResponse recordList(PageDTO pageDTO, String userToken, Integer roleType, String houseId, String queryId, Integer type) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member worker = (Member) object;
        if(worker.getWorkerType()!=null&&worker.getWorkerType()==3){
            roleType=2;
        }
        List<Map<String, Object>> returnMap = new ArrayList<>();
        PageInfo pageInfo;
        if (type == 5) {
            pageInfo=getOrderSplitList(pageDTO,worker, roleType,houseId, type,returnMap);
        } else if (type == 1||type == 3) {
            pageInfo=getChangeOrderList(pageDTO,worker, roleType,houseId, type,returnMap);
        } else {
            pageInfo=getMendOrderList(pageDTO,worker, roleType, houseId, type,returnMap);
        }
        return ServerResponse.createBySuccess("查询成功", pageInfo);
    }

    private PageInfo getChangeOrderList(PageDTO pageDTO,Member worker, Integer roleType, String houseId, Integer type,
                                      List<Map<String, Object>> returnMap) {
        Example example = new Example(ChangeOrder.class);
        Example.Criteria  criteria = example.createCriteria();
        criteria.andEqualTo(ChangeOrder.HOUSE_ID, houseId);
        //补退人工按工种区分
        if (type == 1) {//补人工
            criteria.andIn(ChangeOrder.TYPE,  Arrays.asList(new Integer[]{1,3}));
        } else {//退人工
            criteria.andEqualTo(ChangeOrder.TYPE, 2);
        }
        if(roleType==3){
            criteria.andEqualTo(ChangeOrder.WORKER_TYPE_ID, worker.getWorkerTypeId());
        }
        example.orderBy(MendOrder.CREATE_DATE).desc();
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<ChangeOrder> mendOrderList = changeOrderMapper.selectByExample(example);
        PageInfo pageResult = new PageInfo(mendOrderList);
        for (ChangeOrder mendOrder : mendOrderList) {
            List<OrderProgressDTO> orderProgressDTOList=iMasterOrderProgressMapper.queryProgressListByOrderId(mendOrder.getId());//退款历史记录

            WorkerType workerType = workerTypeMapper.selectByPrimaryKey(mendOrder.getWorkerTypeId());
            Map<String, Object> map = new HashMap<>();
            map.put("mendOrderId", mendOrder.getId());
            map.put("number", "");
            if(orderProgressDTOList!=null&&orderProgressDTOList.size()>0){//判断最后节点，及剩余处理时间
                OrderProgressDTO orderProgressDTO=orderProgressDTOList.get(orderProgressDTOList.size()-1);
                map.put("applyStatus",CommonUtil.getStateWorkerName(orderProgressDTO.getNodeCode()));
            }else{
                map.put("applyStatus",CommonUtil.getChangeStateName(String.valueOf(mendOrder.getState()),String.valueOf(mendOrder.getType())));
            }
            map.put("applyStatus","申请中");
            map.put("state", mendOrder.getState());
            map.put("createDate", mendOrder.getCreateDate());
            map.put("type", mendOrder.getType());
            switch (mendOrder.getType()) {
                case 1:
                    map.put("name", workerType.getName()+"申请补人工");
                    break;
                case 2:
                    map.put("name", "业主申请退"+workerType.getName());
                    break;
                case 3:
                    map.put("name", "业主申请补"+workerType.getName());
                    break;
            }
            returnMap.add(map);
        }
        pageResult.setList(returnMap);
        return pageResult;
    }
    private PageInfo getMendOrderList(PageDTO pageDTO,Member worker, Integer roleType, String houseId, Integer type,
                                  List<Map<String, Object>> returnMap) {
        Example example = new Example(MendOrder.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo(MendOrder.HOUSE_ID, houseId);
        //补退人工按工种区分
        if (roleType == 3) {//工匠
            criteria.andEqualTo(MendOrder.WORKER_TYPE_ID, worker.getWorkerTypeId());
        }
        if (type == 4) {
            criteria.andIn(ChangeOrder.TYPE,  Arrays.asList(new Integer[]{4,5}));
        } else {
            criteria.andEqualTo(ChangeOrder.TYPE, type);
        }
        if (!CommonUtil.isEmpty(type)) {
            criteria.andEqualTo(MendOrder.TYPE, type);
        }
        example.orderBy(MendOrder.CREATE_DATE).desc();
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<MendOrder> mendOrderList = mendOrderMapper.selectByExample(example);
        PageInfo pageResult = new PageInfo(mendOrderList);
        for (MendOrder mendOrder : mendOrderList) {
            Map<String, Object> map = new HashMap<>();
            map.put("mendOrderId", mendOrder.getId());
            map.put("number", mendOrder.getNumber());
            map.put("state", mendOrder.getState());
            map.put("createDate", mendOrder.getCreateDate());
            map.put("type", mendOrder.getType());

            example=new Example(MendDeliver.class);
            example.createCriteria().andEqualTo(MendDeliver.MEND_ORDER_ID,mendOrder.getId());
            List<MendDeliver> mendDeliverList=mendDeliverMapper.selectByExample(example);
            switch (mendOrder.getType()) {
                case 2:
                    map.put("name", "申请退货");
                    break;
                case 4:
                case 5:
                    map.put("name", "申请退库存");
                    break;
            }
            //因发货状态存在多条，则取其中一条做状态参考
            if(mendOrder.getState()!=4&&mendDeliverList.size()>0){
                map.put("applyStatus", CommonUtil.getDeliverStateName(mendDeliverList.get(0).getShippingState()));
            }else {
                switch (mendOrder.getState()) {
                    case 0:
                        map.put("applyStatus", "待申请");
                        break;
                    case 1:
                        map.put("applyStatus", "处理中");
                        break;
                    case 2:
                        map.put("applyStatus", "已拒绝");
                        break;
                    case 3:
                        map.put("applyStatus", "已通过");
                        break;
                    case 4:
                        map.put("applyStatus", "已结算");
                        break;
                    case 5:
                        map.put("applyStatus", "已撤回");
                        break;
                    case 6:
                        map.put("applyStatus", "已关闭");
                        break;
                }
            }
            returnMap.add(map);
        }
        pageResult.setList(returnMap);
        return pageResult;
    }

    private PageInfo getOrderSplitList(PageDTO pageDTO,Member worker, Integer roleType,String houseId, Integer type,  List<Map<String, Object>> returnMap) {
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<OrderSplit> orderSplitList = orderSplitMapper.selectOrderSplitList(houseId,roleType == 3?worker.getWorkerTypeId():null);
        PageInfo pageResult = new PageInfo(orderSplitList);
        for (OrderSplit orderSplit : orderSplitList) {
            WorkerType workerType = workerTypeMapper.selectByPrimaryKey(orderSplit.getWorkerTypeId());
            Map<String, Object> map = new HashMap<>();
            map.put("mendOrderId", orderSplit.getId());
            map.put("number", orderSplit.getNumber());
            map.put("name", workerType.getName()+"申请要货");
            map.put("state", orderSplit.getApplyStatus());
            map.put("createDate", orderSplit.getCreateDate());
            map.put("type", type);
            switch (orderSplit.getApplyStatus()) {
                case 0:
                    map.put("applyStatus","申请中");
                    break;
                case 1:
                case 2:
                    map.put("applyStatus","待发货");
                    break;
                case 3:
                    map.put("applyStatus","已拒绝");
                    break;
                case 4:
                    map.put("applyStatus","审核中");
                    break;
                case 5:
                    map.put("applyStatus","已撤回");
                    break;
            }
            returnMap.add(map);
        }
        pageResult.setList(returnMap);
        return pageResult;
    }


    /**
     * 要补退记录
     * 0:补材料;1:补人工;2:退材料(剩余材料登记);3:退人工,4:业主退材料5 要货,6，审核记录
     * roleType 1业主 2管家 3工匠
     */
    public ServerResponse mendList(String userToken, String houseId, int roleType) {
        try {
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            List<Map<String, Object>> returnMap = new ArrayList<>();
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member worker = (Member) object;
            if(worker.getWorkerType()!=null&&worker.getWorkerType()==3){
                roleType=2;
            }
            String workerTypeId="";
            if (roleType == 3) {//工匠
                workerTypeId= worker.getWorkerTypeId();
            }
            List<MendOrder> mendOrderList = mendOrderMapper.workerMendOrder(houseId, 1, workerTypeId);
            if (mendOrderList.size() > 0) {
                Map<String, Object> map = new HashMap<>();
                map.put("houseId", houseId);
                map.put("type", 1);
                map.put("image", address + "iconWork/one.png");
                map.put("name", "补人工记录");
                map.put("size", "共" + mendOrderList.size() + "条");
                returnMap.add(map);
            }

            Example example = new Example(MendOrder.class);
            Example.Criteria criteria= example.createCriteria();
            criteria.andEqualTo(MendOrder.HOUSE_ID, houseId);
            criteria.andEqualTo(MendOrder.TYPE, 2);
            criteria.andNotEqualTo(MendOrder.STATE, 0);
            if(!CommonUtil.isEmpty(workerTypeId)){
                criteria.andEqualTo(MendOrder.WORKER_TYPE_ID, workerTypeId);
            }
            mendOrderList = mendOrderMapper.selectByExample(example);
            if (mendOrderList.size() > 0) {
                Map<String, Object> map = new HashMap<>();
                map.put("houseId", houseId);
                map.put("type", 2);
                map.put("image", address + "iconWork/two.png");
                map.put("name", "退货记录");
                map.put("size", "共" + mendOrderList.size() + "条");
                returnMap.add(map);
            }

            mendOrderList = mendOrderMapper.workerMendOrder(houseId, 3, workerTypeId);
            if (mendOrderList.size() > 0) {
                Map<String, Object> map = new HashMap<>();
                map.put("houseId", houseId);
                map.put("type", 3);
                map.put("image", address + "iconWork/three.png");
                map.put("name", "退人工记录");
                map.put("size", "共" + mendOrderList.size() + "条");
                returnMap.add(map);
            }
            if (roleType != 3) {//工匠
                example = new Example(MendOrder.class);
                example.createCriteria().andEqualTo(MendOrder.HOUSE_ID, houseId)
                        .andEqualTo(MendOrder.TYPE, 4)
                        .andNotEqualTo(MendOrder.STATE, 0);
                mendOrderList = mendOrderMapper.selectByExample(example);
                if (mendOrderList.size() > 0) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("houseId", houseId);
                    map.put("type", 4);
                    map.put("image", address + "iconWork/four.png");
                    map.put("name", "退库存记录");
                    map.put("size", "共" + mendOrderList.size() + "条");
                    returnMap.add(map);
                }
            }
            /*要货单记录*/
            example = new Example(OrderSplit.class);
            Example.Criteria criterias= example.createCriteria();
            criterias.andEqualTo(OrderSplit.HOUSE_ID, houseId);
            criterias.andGreaterThan(OrderSplit.APPLY_STATUS, 0);
            if(!CommonUtil.isEmpty(workerTypeId)){
                criterias.andEqualTo(OrderSplit.WORKER_TYPE_ID, workerTypeId);
            }
            List<OrderSplit> orderSplitList = orderSplitMapper.selectByExample(example);
            if (orderSplitList.size() > 0) {
                Map<String, Object> map = new HashMap<>();
                map.put("houseId", houseId);
                map.put("type", 5);
                map.put("image", address + "iconWork/five.png");
                map.put("name", "要货记录");
                map.put("size", "共" + orderSplitList.size() + "条");
                returnMap.add(map);
            }
            return ServerResponse.createBySuccess("查询成功", returnMap);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 撤回补货要货订单
     *
     * @param mendOrderId
     * @return
     */
    public ServerResponse backOrder(String mendOrderId, Integer type) {
        //工匠/大管家要货撤回
        if (type != null && type == 1) {
            OrderSplit orderSplit = orderSplitMapper.selectByPrimaryKey(mendOrderId);
            //后台审核状态：0生成中, 1申请中, 2通过(发给供应商), 3不通过, 4待业主支付,5已撤回
            Integer applyStatus = orderSplit.getApplyStatus();
            /*Example example = new Example(OrderSplitItem.class);
            example.createCriteria().andEqualTo(OrderSplitItem.ORDER_SPLIT_ID, orderSplit.getId());*/
            List<OrderSplitItem> orderSplitItemList = orderSplitItemMapper.selectSplitItemList(orderSplit.getId(),null);
            //无补货且在申请中
            if (orderSplit.getMendNumber() == null && applyStatus == 1) {
                updateWarehouseList(orderSplit, orderSplitItemList);
                orderSplit.setApplyStatus(5);
                orderSplitMapper.updateByPrimaryKeySelective(orderSplit);
                return ServerResponse.createBySuccessMessage("撤回成功");
            } else if (orderSplit.getMendNumber() != null) {
                MendOrder mendOrder = mendOrderMapper.selectByPrimaryKey(orderSplit.getMendNumber());
                //未支付
                //0生成中,1处理中,2不通过取消,3已通过,4已全部结算,5已撤回
                if (mendOrder.getState() == 1) {
                    orderSplit.setApplyStatus(5);
                    orderSplitMapper.updateByPrimaryKeySelective(orderSplit);
                    //业主的补货支付被撤回
                    mendOrder.setState(5);
                    mendOrderMapper.updateByPrimaryKeySelective(mendOrder);
                    return ServerResponse.createBySuccessMessage("撤回成功");
                }
                //已支付
                else if (mendOrder.getState() == 4) {
                    updateWarehouseList(orderSplit, orderSplitItemList);
                    orderSplit.setApplyStatus(5);
                    orderSplitMapper.updateByPrimaryKeySelective(orderSplit);
                    return ServerResponse.createBySuccessMessage("撤回成功");
                } else {
                    return getServerResponse(mendOrder);
                }
            } else {
                switch (applyStatus) {
                    case 2:
                        return ServerResponse.createByErrorMessage("该货单已发送给供应商发货，无法撤回");
                    case 3:
                        return ServerResponse.createByErrorMessage("该货单已被打回，无法撤回");
                    case 5:
                        return ServerResponse.createByErrorMessage("该货单已撤回，请误重复操作");
                    default:
                        return ServerResponse.createByErrorMessage("撤回失败");
                }
            }
        }
        //工匠退材料//工匠补人工//业主申请退人工
        else {
            Example example = new Example(MendOrderCheck.class);
            example.createCriteria().andEqualTo(MendOrderCheck.MEND_ORDER_ID, mendOrderId);
            List<MendOrderCheck> mendOrderChecks = mendOrderCheckMapper.selectByExample(example);
            boolean flag = false;
            if (mendOrderChecks.size() <= 0) {
                flag = true;
            } else {
                for (MendOrderCheck m : mendOrderChecks) {
                    //0处理中,1未通过,2已通过 3已撤回
                    if (m.getState() == 0) {
                        flag = true;
                    }
                }
            }
            MendOrder mendOrder = mendOrderMapper.selectByPrimaryKey(mendOrderId);
            //"0生成中,1处理中,2不通过取消,3已通过,4已全部结算,5已撤回"
            if (flag && mendOrder.getState() != 5) {
                mendOrder.setState(5);
                mendOrderMapper.updateByPrimaryKeySelective(mendOrder);
                if (mendOrder.getType() == 1 || mendOrder.getType() == 3) {
                    ChangeOrder changeOrder = changeOrderMapper.selectByPrimaryKey(mendOrder.getChangeOrderId());
                    changeOrder.setState(7);
                    changeOrderMapper.updateByPrimaryKeySelective(changeOrder);
                    //退人工后，记录流水
                    if(mendOrder.getType()==3){//撤销退人工申请
                        updateOrderProgressInfo(changeOrder.getId(),"2","REFUND_AFTER_SALES","RA_019",changeOrder.getMemberId());//撤销退人工申请
                    }
                }
                return ServerResponse.createBySuccessMessage("撤回成功");
            } else {
                if (!flag) {
                    return ServerResponse.createBySuccessMessage("无可撤回的单");
                }
                return getServerResponse(mendOrder);
            }
        }
    }
    /**
     * //添加进度信息
     * @param orderId 订单ID
     * @param progressType 订单类型
     * @param nodeType 节点类型
     * @param nodeCode 节点编码
     * @param userId 用户id
     */
    private void updateOrderProgressInfo(String orderId,String progressType,String nodeType,String nodeCode,String userId){
        OrderProgress orderProgress=new OrderProgress();
        orderProgress.setProgressOrderId(orderId);
        orderProgress.setProgressType(progressType);
        orderProgress.setNodeType(nodeType);
        orderProgress.setNodeCode(nodeCode);
        orderProgress.setCreateBy(userId);
        orderProgress.setUpdateBy(userId);
        orderProgress.setCreateDate(new Date());
        orderProgress.setModifyDate(new Date());
        iMasterOrderProgressMapper.insert(orderProgress);
    }
    private ServerResponse getServerResponse(MendOrder mendOrder) {
        switch (mendOrder.getState()) {
            case 3:
                return ServerResponse.createByErrorMessage("该货单已发送给供应商发货，无法撤回");
            case 2:
                return ServerResponse.createByErrorMessage("该货单已被打回，无法撤回");
            case 5:
                return ServerResponse.createByErrorMessage("该货单已撤回，请误重复操作");
            default:
                return ServerResponse.createByErrorMessage("撤回失败");
        }
    }

    private void updateWarehouseList(OrderSplit orderSplit, List<OrderSplitItem> orderSplitItemList) {
        //修改要货子单的状态也为已撤回
        orderSplitMapper.updateOrderSplitStatus(orderSplit.getId());
        for (OrderSplitItem orderSplitItem : orderSplitItemList) {
            Warehouse warehouse = warehouseMapper.getByProductId(orderSplitItem.getProductId(), orderSplit.getHouseId());
            warehouse.setAskCount(warehouse.getAskCount() - orderSplitItem.getNum());//更新仓库已要总数
            warehouse.setAskTime(warehouse.getAskTime() - 1);//更新该货品被要次数
            warehouseMapper.updateByPrimaryKeySelective(warehouse);
        }
    }
}
