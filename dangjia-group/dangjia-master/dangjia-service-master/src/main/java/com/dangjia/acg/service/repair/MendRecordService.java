package com.dangjia.acg.service.repair;

import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.repair.MendOrderDetail;
import com.dangjia.acg.mapper.deliver.IOrderSplitItemMapper;
import com.dangjia.acg.mapper.deliver.IOrderSplitMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.house.IWarehouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.repair.*;
import com.dangjia.acg.modle.deliver.OrderSplit;
import com.dangjia.acg.modle.deliver.OrderSplitItem;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.house.Warehouse;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.repair.*;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                map.put("shopCount", mendMateriel.getShopCount());//申请数量
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
     * 0:补材料;1:补人工;2:退材料(剩余材料登记);3:退人工,4:业主退材料, 5 要货
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
            if (type == 5) {
                OrderSplit orderSplit = orderSplitMapper.selectByPrimaryKey(mendOrderId);
                if (worker != null && worker.getWorkerTypeId().equals(orderSplit.getWorkerTypeId())) {
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
                if (worker != null && worker.getWorkerTypeId().equals(mendOrder.getWorkerTypeId())) {
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
     * 0:补材料;1:补人工;2:退材料(剩余材料登记);3:退人工,4:业主退材料, 5 要货
     */
    public ServerResponse recordList(String houseId, Integer type) {
        try {
            List<Map<String, Object>> returnMap = new ArrayList<>();

            if (type == 5) {
                Example example = new Example(OrderSplit.class);
                example.createCriteria().andEqualTo(OrderSplit.HOUSE_ID, houseId).andNotEqualTo(OrderSplit.APPLY_STATUS, 0);
                example.orderBy(OrderSplit.CREATE_DATE).desc();
                List<OrderSplit> orderSplitList = orderSplitMapper.selectByExample(example);
                for (OrderSplit orderSplit : orderSplitList) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("mendOrderId", orderSplit.getId());
                    map.put("number", orderSplit.getNumber());
                    map.put("state", orderSplit.getApplyStatus());
                    map.put("createDate", orderSplit.getCreateDate());
                    map.put("type", type);
                    returnMap.add(map);
                }
            } else {
                Example example = new Example(MendOrder.class);
                example.createCriteria().andEqualTo(MendOrder.HOUSE_ID, houseId).andEqualTo(MendOrder.TYPE, type)
                        .andNotEqualTo(MendOrder.STATE, 0);
                example.orderBy(MendOrder.CREATE_DATE).desc();
                List<MendOrder> mendOrderList = mendOrderMapper.selectByExample(example);
                for (MendOrder mendOrder : mendOrderList) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("mendOrderId", mendOrder.getId());
                    map.put("number", mendOrder.getNumber());
                    map.put("state", mendOrder.getState());
                    map.put("totalAmount", "¥" + String.format("%.2f", +mendOrder.getTotalAmount()));
                    map.put("createDate", mendOrder.getCreateDate());
                    map.put("type", type);
                    returnMap.add(map);
                }
            }


            return ServerResponse.createBySuccess("查询成功", returnMap);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 要补退记录
     * 0:补材料;1:补人工;2:退材料(剩余材料登记);3:退人工,4:业主退材料
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
//            Example example = new Example(MendOrder.class);
//            example.createCriteria().andEqualTo(MendOrder.HOUSE_ID, houseId).andEqualTo(MendOrder.TYPE,0)
//            .andNotEqualTo(MendOrder.STATE,0);
//            List<MendOrder> mendOrderList = mendOrderMapper.selectByExample(example);
//            if(mendOrderList.size() > 0){
//                Map<String,Object> map = new HashMap<>();
//                map.put("houseId", houseId);
//                map.put("type", 0);
//                map.put("image", address + "iconWork/zero.png");
//                map.put("name", "补材料/服务记录");
//                map.put("size", "共"+mendOrderList.size()+"条");
//                returnMap.add(map);
//            }

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
            example.createCriteria().andEqualTo(MendOrder.HOUSE_ID, houseId).andEqualTo(MendOrder.TYPE, 4)
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
            example.createCriteria().andEqualTo(OrderSplit.HOUSE_ID, houseId).andGreaterThan(OrderSplit.APPLY_STATUS, 0);
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
                    return ServerResponse.createByErrorMessage("撤回失败");
                }
            } else {
                return ServerResponse.createByErrorMessage("该货单已发送给供应商发货，无法撤回");
            }
        }
        //工匠退材料//工匠补人工//业主申请退人工
        else {
            Example example = new Example(MendOrderCheck.class);
            example.createCriteria().andEqualTo(MendOrderCheck.MEND_ORDER_ID, mendOrderId);
            List<MendOrderCheck> mendOrderChecks = mendOrderCheckMapper.selectByExample(example);
            boolean flag = false;
            for (MendOrderCheck m : mendOrderChecks) {
                //0处理中,1未通过,2已通过 3已撤回
                if (m.getState() == 0) {
                    flag = true;
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
                return ServerResponse.createByErrorMessage("撤回失败");
            }
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
