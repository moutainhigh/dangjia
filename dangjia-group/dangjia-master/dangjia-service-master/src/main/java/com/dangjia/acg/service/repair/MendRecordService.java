package com.dangjia.acg.service.repair;

import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.repair.MendOrderDetail;
import com.dangjia.acg.mapper.complain.IComplainMapper;
import com.dangjia.acg.mapper.core.IHouseFlowApplyMapper;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.deliver.IOrderSplitItemMapper;
import com.dangjia.acg.mapper.deliver.IOrderSplitMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.house.IWarehouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.repair.*;
import com.dangjia.acg.mapper.worker.IEvaluateMapper;
import com.dangjia.acg.modle.core.HouseFlowApply;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.deliver.OrderSplit;
import com.dangjia.acg.modle.deliver.OrderSplitItem;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.house.Warehouse;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.repair.*;
import com.dangjia.acg.modle.worker.Evaluate;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

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
    private IComplainMapper complainMapper;

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
                    map.put("goodsType", "材料");
                } else {
                    map.put("goodsType", "服务");
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
     * 要补退明细
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
                mendOrderDetail.setApplicantId(houseFlowApply.getWorkerId());
                mendOrderDetail.setApplicantName(CommonUtil.isEmpty(member.getName()) ? member.getNickName() : member.getName());
                mendOrderDetail.setApplicantMobile(member.getMobile());
                mendOrderDetail.setNumber(houseFlowApply.getId());
                mendOrderDetail.setType(6);
                mendOrderDetail.setState(houseFlowApply.getApplyType());
                mendOrderDetail.setCreateDate(houseFlowApply.getCreateDate());
                mendOrderDetail.setMapList(getFlowInfo(houseFlowApply));
            } else if (type == 5) {
                OrderSplit orderSplit = orderSplitMapper.selectByPrimaryKey(mendOrderId);
                if (worker != null && worker.getWorkerTypeId() != null && worker.getWorkerTypeId().equals(orderSplit.getWorkerTypeId())) {
                    mendOrderDetail.setIsShow(1);
                }
                mendOrderDetail.setHouseId(orderSplit.getHouseId());
                mendOrderDetail.setApplicantId(orderSplit.getSupervisorId());
                mendOrderDetail.setApplicantName(orderSplit.getSupervisorName());
                mendOrderDetail.setApplicantMobile(orderSplit.getSupervisorTel());
                mendOrderDetail.setApplicantId(orderSplit.getSupervisorId());
                mendOrderDetail.setNumber(orderSplit.getNumber());
                mendOrderDetail.setMendOrderId(orderSplit.getMendNumber());
                mendOrderDetail.setType(5);
                mendOrderDetail.setState(orderSplit.getApplyStatus());
                mendOrderDetail.setCreateDate(orderSplit.getCreateDate());
                /*
                计算要货单钱
                 */
                mendOrderDetail.setTotalAmount(orderSplitItemMapper.getOrderSplitPrice(mendOrderId));

                List<Map<String, Object>> mapList = new ArrayList<>();
                Example example = new Example(OrderSplitItem.class);
                example.createCriteria().andEqualTo(OrderSplitItem.ORDER_SPLIT_ID, orderSplit.getId());
                List<OrderSplitItem> orderSplitItemList = orderSplitItemMapper.selectByExample(example);

                for (OrderSplitItem orderSplitItem : orderSplitItemList) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("image", address + orderSplitItem.getImage());
                    if (orderSplitItem.getProductType() == 0) {
                        map.put("goodsType", "材料");
                    } else {
                        map.put("goodsType", "服务");
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
                            map.put("goodsType", "材料");
                        } else {
                            map.put("goodsType", "服务");
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
                        map.put("goodsType", "人工");
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
        String info = "我是" + workerType.getName() + ",我已申请了" + (houseFlowApply.getApplyType() == 1 ? "阶段完工" : "整体完工");
        //工匠
        Map<String, Object> map = new HashMap<>();
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
     * 0:补材料;1:补人工;2:退材料(剩余材料登记);3:退人工,4:业主退材料, 5 要货,6，审核记录
     */
    public ServerResponse recordList(String userToken, int roleType, String houseId, String queryId, Integer type) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member worker = (Member) object;
        List<Map<String, Object>> returnMap = new ArrayList<>();
        if (CommonUtil.isEmpty(type)) {
            getHouseFlowApplies(worker, roleType, houseId, 6, returnMap);
            getOrderSplitList(houseId, 5, queryId, returnMap);
            getMendOrderList(worker, roleType, houseId, null, queryId, returnMap);
            sortMax(returnMap);
        } else if (type == 6) {
            getHouseFlowApplies(worker, roleType, houseId, type, returnMap);
        } else if (type == 5) {
            getOrderSplitList(houseId, type, queryId, returnMap);
        } else {
            getMendOrderList(worker, roleType, houseId, type, queryId, returnMap);
        }
        return ServerResponse.createBySuccess("查询成功", returnMap);
    }

    private void sortMax(List<Map<String, Object>> arr) {
        //让左边是最大的值，右边是最小的
        for (int i = 0; i < arr.size() - 1; i++) {
            for (int j = 1; j < arr.size() - i; j++) {
                Map<String, Object> a;
                Date date = (Date) arr.get(j - 1).get("createDate");
                Date date2 = (Date) arr.get(j).get("createDate");
                if (date.getTime() < date2.getTime()) { // 比较两个整数的大小
                    a = arr.get(j - 1);
                    arr.set((j - 1), arr.get(j));
                    arr.set(j, a);
                }
            }
        }
    }

    private void getMendOrderList(Member worker, int roleType, String houseId, Integer type, String queryId,
                                  List<Map<String, Object>> returnMap) {
        Example example = new Example(MendOrder.class);
        Example.Criteria criteria;
        //补退人工按工种区分
        if (roleType == 3 && (type == 1 || type == 3)) {//工匠
            criteria = example.createCriteria();
            criteria.andEqualTo(MendOrder.HOUSE_ID, houseId);
            criteria.andEqualTo(MendOrder.WORKER_TYPE_ID, worker.getWorkerTypeId());
            criteria.andNotEqualTo(MendOrder.STATE, 0);
        } else {
            criteria = example.createCriteria();
            criteria.andEqualTo(MendOrder.HOUSE_ID, houseId);
            criteria.andNotEqualTo(MendOrder.STATE, 0);
        }
        if (!CommonUtil.isEmpty(type)) {
            criteria.andEqualTo(MendOrder.TYPE, type);
        }
        example.orderBy(MendOrder.CREATE_DATE).desc();
        List<MendOrder> mendOrderList = mendOrderMapper.selectByExample(example);
        for (MendOrder mendOrder : mendOrderList) {
            Map<String, Object> map = new HashMap<>();
            map.put("mendOrderId", mendOrder.getId());
            map.put("number", mendOrder.getNumber());
            map.put("state", mendOrder.getState());
            map.put("totalAmount", "¥" + String.format("%.2f", +mendOrder.getTotalAmount()));
            map.put("createDate", mendOrder.getCreateDate());
            map.put("type", mendOrder.getType());
            switch (mendOrder.getType()) {
                //0:补材料;1:补人工;2:退材料(剩余材料登记);3:退人工,4:业主退材料
                case 0:
                    map.put("name", "补材料记录");
                    break;
                case 1:
                    map.put("name", "补人工记录");
                    break;
                case 2:
                    map.put("name", "退材料记录");
                    break;
                case 3:
                    map.put("name", "退人工记录");
                    break;
                case 4:
                    map.put("name", "业主退材料记录");
                    break;
            }
            if (!CommonUtil.isEmpty(queryId)) {
                if (mendOrder.getType() == 1 || mendOrder.getType() == 3) {
                    example = new Example(MendWorker.class);
                    example.createCriteria()
                            .andEqualTo(MendWorker.MEND_ORDER_ID, mendOrder.getId())
                            .andEqualTo(MendWorker.WORKER_GOODS_ID, queryId);
                    List<MendWorker> mendWorkerList = mendWorkerMapper.selectByExample(example);
                    if (mendWorkerList.size() > 0) {
                        returnMap.add(map);
                    }
                } else {
                    example = new Example(MendMateriel.class);
                    example.createCriteria()
                            .andEqualTo(MendMateriel.MEND_ORDER_ID, mendOrder.getId())
                            .andEqualTo(MendMateriel.PRODUCT_ID, queryId);
                    List<MendMateriel> mendMaterielList = mendMaterialMapper.selectByExample(example);
                    if (mendMaterielList.size() > 0) {
                        returnMap.add(map);
                    }
                }
            } else {
                returnMap.add(map);
            }
        }
    }

    private void getOrderSplitList(String houseId, Integer type, String queryId, List<Map<String, Object>> returnMap) {
        Example example = new Example(OrderSplit.class);
        example.createCriteria().andEqualTo(OrderSplit.HOUSE_ID, houseId)
                .andNotEqualTo(OrderSplit.APPLY_STATUS, 0);
        example.orderBy(OrderSplit.CREATE_DATE).desc();
        List<OrderSplit> orderSplitList = orderSplitMapper.selectByExample(example);
        for (OrderSplit orderSplit : orderSplitList) {
            Map<String, Object> map = new HashMap<>();
            map.put("mendOrderId", orderSplit.getId());
            map.put("number", orderSplit.getNumber());
            map.put("name", "要货记录");
            map.put("state", orderSplit.getApplyStatus());
            map.put("createDate", orderSplit.getCreateDate());
            map.put("type", type);
            if (!CommonUtil.isEmpty(queryId)) {
                example = new Example(OrderSplitItem.class);
                example.createCriteria()
                        .andEqualTo(OrderSplitItem.ORDER_SPLIT_ID, orderSplit.getId())
                        .andEqualTo(OrderSplitItem.PRODUCT_ID, queryId);
                List<OrderSplitItem> orderSplitItems = orderSplitItemMapper.selectByExample(example);
                if (orderSplitItems.size() > 0) {
                    returnMap.add(map);
                }
            } else {
                returnMap.add(map);
            }
        }
    }

    private void getHouseFlowApplies(Member worker, int roleType, String houseId, Integer type, List<Map<String, Object>> returnMap) {
        Example example = new Example(HouseFlowApply.class);
        /*审核记录*/
        if (roleType == 3) {//工匠
            example.createCriteria().andEqualTo(HouseFlowApply.HOUSE_ID, houseId)
                    .andCondition(" apply_type <3 and apply_type!=0 ")
                    .andEqualTo(HouseFlowApply.WORKER_TYPE_ID, worker.getWorkerTypeId());
        } else {
            example.createCriteria().andEqualTo(HouseFlowApply.HOUSE_ID, houseId)
                    .andCondition(" apply_type <3 and apply_type!=0 ");
        }
        example.orderBy(HouseFlowApply.CREATE_DATE).desc();
        List<HouseFlowApply> houseFlowApplies = houseFlowApplyMapper.selectByExample(example);
        for (HouseFlowApply houseFlowApply : houseFlowApplies) {
            WorkerType workerType = workerTypeMapper.selectByPrimaryKey(houseFlowApply.getWorkerTypeId());
            Map<String, Object> map = new HashMap<>();
            map.put("mendOrderId", houseFlowApply.getId());
            if (houseFlowApply.getApplyType() == 0) {
                map.put("number", workerType.getName() + "每日完工审核");
                map.put("name", workerType.getName() + "每日完工审核");
            }
            if (houseFlowApply.getApplyType() == 1) {
                map.put("number", workerType.getName() + "阶段完工审核");
                map.put("name", workerType.getName() + "阶段完工审核");
            }
            if (houseFlowApply.getApplyType() == 2) {
                map.put("number", workerType.getName() + "整体完工审核");
                map.put("name", workerType.getName() + "整体完工审核");
            }
            map.put("state", houseFlowApply.getApplyType());
            map.put("createDate", houseFlowApply.getCreateDate());
            map.put("type", type);
            returnMap.add(map);
        }
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
            List<MendOrder> mendOrderList;
            if (roleType == 3) {//工匠
                mendOrderList = mendOrderMapper.workerMendOrder(houseId, 1, worker.getWorkerTypeId());
            } else {
                mendOrderList = mendOrderMapper.workerMendOrder(houseId, 1, "");
            }
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
            example.createCriteria().andEqualTo(MendOrder.HOUSE_ID, houseId).andEqualTo(MendOrder.TYPE, 2)
                    .andNotEqualTo(MendOrder.STATE, 0);
            mendOrderList = mendOrderMapper.selectByExample(example);
            if (mendOrderList.size() > 0) {
                Map<String, Object> map = new HashMap<>();
                map.put("houseId", houseId);
                map.put("type", 2);
                map.put("image", address + "iconWork/two.png");
                map.put("name", "退材料/服务记录");
                map.put("size", "共" + mendOrderList.size() + "条");
                returnMap.add(map);
            }
            if (roleType == 3) {//工匠
                mendOrderList = mendOrderMapper.workerMendOrder(houseId, 3, worker.getWorkerTypeId());
            } else {
                mendOrderList = mendOrderMapper.workerMendOrder(houseId, 3, "");
            }
            if (mendOrderList.size() > 0) {
                Map<String, Object> map = new HashMap<>();
                map.put("houseId", houseId);
                map.put("type", 3);
                map.put("image", address + "iconWork/three.png");
                map.put("name", "退人工记录");
                map.put("size", "共" + mendOrderList.size() + "条");
                returnMap.add(map);
            }
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
                map.put("name", "业主退材料/服务记录");
                map.put("size", "共" + mendOrderList.size() + "条");
                returnMap.add(map);
            }
            /*要货单记录*/
            example = new Example(OrderSplit.class);
            example.createCriteria().andEqualTo(OrderSplit.HOUSE_ID, houseId)
                    .andGreaterThan(OrderSplit.APPLY_STATUS, 0);
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
            example = new Example(HouseFlowApply.class);
            /*审核记录*/
            if (roleType == 3) {//工匠
                example.createCriteria().andEqualTo(HouseFlowApply.HOUSE_ID, houseId)
                        .andCondition(" apply_type <3 and apply_type!=0  ")
                        .andEqualTo(HouseFlowApply.WORKER_TYPE_ID, worker.getWorkerTypeId());
            } else {
                example.createCriteria().andEqualTo(HouseFlowApply.HOUSE_ID, houseId)
                        .andCondition(" apply_type <3 and apply_type!=0  ");
            }
            List<HouseFlowApply> houseFlowApplies = houseFlowApplyMapper.selectByExample(example);
            if (houseFlowApplies.size() > 0) {
                Map<String, Object> map = new HashMap<>();
                map.put("houseId", houseId);
                map.put("type", 6);
                map.put("image", address + "iconWork/zero.png");
                map.put("name", "审核记录");
                map.put("size", "共" + houseFlowApplies.size() + "条");
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
            Example example = new Example(OrderSplitItem.class);
            example.createCriteria().andEqualTo(OrderSplitItem.ORDER_SPLIT_ID, orderSplit.getId());
            List<OrderSplitItem> orderSplitItemList = orderSplitItemMapper.selectByExample(example);
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
        for (OrderSplitItem orderSplitItem : orderSplitItemList) {
            Warehouse warehouse = warehouseMapper.getByProductId(orderSplitItem.getProductId(), orderSplit.getHouseId());
            warehouse.setAskCount(warehouse.getAskCount() - orderSplitItem.getNum());//更新仓库已要总数
            warehouse.setAskTime(warehouse.getAskTime() - 1);//更新该货品被要次数
            warehouseMapper.updateByPrimaryKeySelective(warehouse);
        }
    }
}
