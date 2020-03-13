package com.dangjia.acg.service.delivery;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.app.house.HouseAPI;
import com.dangjia.acg.api.app.member.MemberAPI;
import com.dangjia.acg.api.data.ForMasterAPI;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.common.util.MathUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.delivery.*;
import com.dangjia.acg.dto.design.CollectDataDTO;
import com.dangjia.acg.dto.design.QuantityRoomDTO;
import com.dangjia.acg.dto.design.WorkChartListDTO;
import com.dangjia.acg.dto.order.*;
import com.dangjia.acg.mapper.delivery.*;
import com.dangjia.acg.mapper.member.IBillMemberAddressMapper;
import com.dangjia.acg.mapper.order.*;
import com.dangjia.acg.mapper.pay.IBillBusinessOrderMapper;
import com.dangjia.acg.mapper.sale.IBillMemberMapper;
import com.dangjia.acg.mapper.shoppingCart.IBillShoppingCartMapper;
import com.dangjia.acg.mapper.storeFront.IBillStorefrontMapper;
import com.dangjia.acg.modle.activity.ActivityRedPack;
import com.dangjia.acg.modle.activity.ActivityRedPackRecord;
import com.dangjia.acg.modle.activity.DjStoreActivity;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.core.HouseFlowApply;
import com.dangjia.acg.modle.core.HouseWorker;
import com.dangjia.acg.modle.deliver.*;
import com.dangjia.acg.modle.design.QuantityRoom;
import com.dangjia.acg.modle.design.QuantityRoomImages;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.house.Warehouse;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.member.MemberAddress;
import com.dangjia.acg.modle.pay.BusinessOrder;
import com.dangjia.acg.modle.product.ShoppingCart;
import com.dangjia.acg.modle.storefront.Storefront;
import com.dangjia.acg.service.product.BillProductTemplateService;
import com.dangjia.acg.util.StringTool;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.dangjia.acg.common.model.PageDTO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DjDeliverOrderService {


    @Value("${spring.profiles.active}")
    private String active;
    @Autowired
    private IBillDjDeliverOrderMapper iBillDjDeliverOrderMapper;
    @Autowired
    private IBillMemberMapper iBillMemberMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IBillHouseFlowMapper iBillHouseFlowMapper;
    @Autowired
    private IBillDjDeliverOrderItemMapper iBillDjDeliverOrderItemMapper;
    @Autowired
    private IBillHouseMapper iBillHouseMapper;
    @Autowired
    private MemberAPI memberAPI;
    @Autowired
    private IBillQuantityRoomMapper iBillQuantityRoomMapper;
    @Autowired
    private IBillQuantityRoomImagesMapper iBillQuantityRoomImagesMapper;
    private static Logger logger = LoggerFactory.getLogger(DjDeliverOrderService.class);
    @Autowired
    private IBillActivityRedPackRecordMapper billActivityRedPackRecordMapper;
    @Autowired
    private HouseAPI houseAPI;
    @Autowired
    private IBillStorefrontMapper iBillStorefrontMapper;
    @Autowired
    private ForMasterAPI forMasterAPI;
    @Autowired
    private BillDjDeliverOrderSplitItemMapper billDjDeliverOrderSplitItemMapper;
    @Autowired
    private BillDjDeliverSplitDeliverMapper billDjDeliverSplitDeliverMapper;
    @Autowired
    private BillProductTemplateService billProductTemplateService;
    @Autowired
    private IBillWarehouseMapper iBillWarehouseMapper;
    @Autowired
    private BillDjDeliverOrderSplitMapper billDjDeliverOrderSplitMapper;
    @Autowired
    private IBillShoppingCartMapper iBillShoppingCartMapper;
    @Autowired
    private IBillQuantityRoomMapper iQuantityRoomMapper;
    @Autowired
    private IBillMemberAddressMapper iBillMemberAddressMapper;
    @Autowired
    private IBillHouseMapper houseMapper;
    @Autowired
    private IBillActivityRedPackMapper billActivityRedPackMapper;
    @Autowired
    private IBillBusinessOrderMapper billBusinessOrderMapper;
    @Autowired
    private IBillDjStoreActivityMapper iBillDjStoreActivityMapper;

    /**
     * 获取我的房产的查询条件
     *
     * @param memberId 用户ID
     * @return 条件实体
     */
    public Example getHouseExample(String memberId) {
        Example example = new Example(House.class);
        example.createCriteria()
                .andEqualTo(House.MEMBER_ID, memberId)
                .andNotEqualTo(House.VISIT_STATE, 2);
        return example;
    }




    /**
     * "获取设计图
     *
     * @param houseId
     * @return
     */
    public ServerResponse getDesignImag(String houseId, Integer type) {
        //0:量房，，
        List<WorkChartListDTO> list = new ArrayList<>();
        if (type == null || type == 0) {
            QuantityRoom quantityRoom = iBillQuantityRoomMapper.getBillQuantityRoom(houseId, 0);
            List<QuantityRoomImages> quantityRoomImages = getQuantityRoom(quantityRoom);
            if (quantityRoomImages != null && !quantityRoomImages.isEmpty()) {
                WorkChartListDTO workChartListDTO = new WorkChartListDTO();
                workChartListDTO.setDate(quantityRoomImages.get(0).getCreateDate());
                workChartListDTO.setName("量房");
                workChartListDTO.setList(quantityRoomImages);
                workChartListDTO.setType(0);
                list.add(workChartListDTO);
            }
        }
        //1平面图
        QuantityRoom quantityRoom1 = iBillQuantityRoomMapper.getBillQuantityRoom(houseId, 1);
        List<QuantityRoomImages> quantityRoomImages1 = getQuantityRoom(quantityRoom1);
        if (quantityRoomImages1 != null && !quantityRoomImages1.isEmpty()) {
            WorkChartListDTO workChartListDTO = new WorkChartListDTO();
            workChartListDTO.setDate(quantityRoomImages1.get(0).getCreateDate());
            workChartListDTO.setName("平面图");
            workChartListDTO.setList(quantityRoomImages1);
            list.add(workChartListDTO);
        }
        //2施工图
        QuantityRoom quantityRoom2 = iBillQuantityRoomMapper.getBillQuantityRoom(houseId, 2);
        List<QuantityRoomImages> quantityRoomImages2 = getQuantityRoom(quantityRoom2);
        if (quantityRoomImages2 != null && !quantityRoomImages2.isEmpty()) {
            WorkChartListDTO workChartListDTO = new WorkChartListDTO();
            workChartListDTO.setDate(quantityRoomImages2.get(0).getCreateDate());
            workChartListDTO.setName("施工图");
            workChartListDTO.setList(quantityRoomImages2);
            list.add(workChartListDTO);
        }
        if (list.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        return ServerResponse.createBySuccess("查询成功", list);
    }

    private List<QuantityRoomImages> getQuantityRoom(QuantityRoom quantityRoom) {
        if (quantityRoom == null) {
            return null;
        }
        Example example = new Example(QuantityRoomImages.class);
        example.createCriteria()
                .andEqualTo(QuantityRoomImages.QUANTITY_ROOM_ID, quantityRoom.getId())
                .andEqualTo(QuantityRoomImages.DATA_STATUS, 0);
        example.orderBy(QuantityRoomImages.SORT).asc();
        List<QuantityRoomImages> quantityRoomImages = iBillQuantityRoomImagesMapper.selectByExample(example);
        if (quantityRoomImages == null || quantityRoomImages.size() <= 0) {
            return null;
        }
        String imageAddress = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        for (QuantityRoomImages quantityRoomImage : quantityRoomImages) {
            quantityRoomImage.initPath(imageAddress);
        }
        return quantityRoomImages;
    }

    /**
     * 获取设计验收过程
     *
     * @param houseId
     * @return
     */
    public ServerResponse getDesignInfo(String houseId) {
        List<QuantityRoomDTO> quantityRoomDTOS = iBillQuantityRoomMapper.getQuantityRoomList(houseId);
        if (!quantityRoomDTOS.isEmpty()) {
            quantityRoomDTOS.forEach(quantityRoomDTO -> {
                if (quantityRoomDTO.getType() == 0) {
                    quantityRoomDTO.setName("量房");
                } else if (quantityRoomDTO.getType() == 1) {
                    if (quantityRoomDTO.getFlag() == 0) {
                        quantityRoomDTO.setName("平面图审核通过");
                    } else if (quantityRoomDTO.getFlag() == 1) {
                        quantityRoomDTO.setName("平面图审核未通过");
                    } else {
                        quantityRoomDTO.setName("上传平面图");
                    }
                } else if (quantityRoomDTO.getType() == 2) {
                    if (quantityRoomDTO.getFlag() == 0) {
                        quantityRoomDTO.setName("施工图审核通过");
                    } else if (quantityRoomDTO.getFlag() == 1) {
                        quantityRoomDTO.setName("施工图审核未通过");
                    } else {
                        quantityRoomDTO.setName("上传施工图");
                    }
                }
            });

        }

        return ServerResponse.createBySuccess("查询成功", quantityRoomDTOS);
    }


    /**
     * 查询精算信息
     *
     * @param houseId
     * @return
     */
    public ServerResponse getActuaryInfo(String houseId) {

        Example example = new Example(HouseFlow.class);
        example.createCriteria().andEqualTo(HouseFlow.HOUSE_ID, houseId)
                .andEqualTo(HouseFlow.DATA_STATUS, 0)
                .andEqualTo(HouseFlow.WORKER_TYPE, 2);
        List<HouseFlow> houseFlows = iBillHouseFlowMapper.selectByExample(example);

        List<Map<String, Object>> list = new ArrayList<>();
        if (!houseFlows.isEmpty()) {
            houseFlows.forEach(houseFlow -> {
                Map<String, Object> map = new HashMap<>();
                map.put("name", "精算确认");
                map.put("date", houseFlow.getCreateDate());
                list.add(map);
                if (houseFlow.getStartDate() != null) {
                    map = new HashMap<>();
                    map.put("name", "开始精算");
                    map.put("date", houseFlow.getStartDate());
                    list.add(map);
                }
            });
        }

        return ServerResponse.createBySuccess("查询成功", list);
    }


    /**
     * 查询验收过程
     *
     * @param houseId
     * @return
     */
    public ServerResponse getCollectInfo(String houseId) {
        CollectDataDTO collectDataDTO = new CollectDataDTO();
        //查询预计完工时间
        Date date = iBillQuantityRoomMapper.selectMaxEndDate(houseId);

        //查询验收过程时间
        List<HouseFlowApply> houseFlowApplies = iBillQuantityRoomMapper.selectApplyInfo(houseId);

        //查询工人进场时间
        List<HouseWorker> houseWorkers = iBillQuantityRoomMapper.selectWorkerInfo(houseId);

        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map;


        for (HouseFlowApply houseFlowApply : houseFlowApplies) {
            for (HouseWorker houseWorker : houseWorkers) {
                if (houseFlowApply.getWorkerType() == 4 && houseWorker.getWorkerType() == 4) {
                    map = new HashMap<>();
                    map.put("name", "拆除进场");
                    map.put("date", houseWorker.getModifyDate());
                    list.add(map);
                    if (houseFlowApply.getApplyType() == 1) {
                        map = new HashMap<>();
                        map.put("name", "拆除申请阶段完工");
                        map.put("date", houseFlowApply.getCreateDate());
                        list.add(map);
                    }
                    if (houseFlowApply.getMemberCheck() == 1) {
                        map = new HashMap<>();
                        map.put("name", "拆除申请阶段通过");
                        map.put("date", houseFlowApply.getModifyDate());
                        list.add(map);
                    } else {
                        map = new HashMap<>();
                        map.put("name", "拆除申请阶段未通过");
                        map.put("date", houseFlowApply.getModifyDate());
                        list.add(map);
                    }
                } else if (houseFlowApply.getWorkerType() == 6 && houseWorker.getWorkerType() == 6) {
                    map = new HashMap<>();
                    map.put("name", "水电工进场");
                    map.put("date", houseWorker.getModifyDate());
                    list.add(map);
                    if (houseFlowApply.getApplyType() == 1) {
                        map = new HashMap<>();
                        map.put("name", "水电工申请阶段完工");
                        map.put("date", houseFlowApply.getCreateDate());
                        list.add(map);
                    }
                    if (houseFlowApply.getMemberCheck() == 1) {
                        map = new HashMap<>();
                        map.put("name", "水电工申请阶段通过");
                        map.put("date", houseFlowApply.getModifyDate());
                        list.add(map);
                    } else {
                        map = new HashMap<>();
                        map.put("name", "水电工申请阶段未通过");
                        map.put("date", houseFlowApply.getModifyDate());
                        list.add(map);
                    }
                } else if (houseFlowApply.getWorkerType() == 7 && houseWorker.getWorkerType() == 7) {
                    map = new HashMap<>();
                    map.put("name", "防水进场");
                    map.put("date", houseWorker.getModifyDate());
                    list.add(map);
                    if (houseFlowApply.getApplyType() == 1) {
                        map = new HashMap<>();
                        map.put("name", "防水申请阶段完工");
                        map.put("date", houseFlowApply.getCreateDate());
                        list.add(map);
                    }
                    if (houseFlowApply.getMemberCheck() == 1) {
                        map = new HashMap<>();
                        map.put("name", "防水申请阶段通过");
                        map.put("date", houseFlowApply.getModifyDate());
                        list.add(map);
                    } else {
                        map = new HashMap<>();
                        map.put("name", "防水申请阶段未通过");
                        map.put("date", houseFlowApply.getModifyDate());
                        list.add(map);
                    }
                } else if (houseFlowApply.getWorkerType() == 8 && houseWorker.getWorkerType() == 8) {
                    map = new HashMap<>();
                    map.put("name", "泥工进场");
                    map.put("date", houseWorker.getModifyDate());
                    list.add(map);
                    if (houseFlowApply.getApplyType() == 1) {
                        map = new HashMap<>();
                        map.put("name", "泥工申请阶段完工");
                        map.put("date", houseFlowApply.getCreateDate());
                        list.add(map);
                    }
                    if (houseFlowApply.getMemberCheck() == 1) {
                        map = new HashMap<>();
                        map.put("name", "泥工申请阶段通过");
                        map.put("date", houseFlowApply.getModifyDate());
                        list.add(map);
                    } else {
                        map = new HashMap<>();
                        map.put("name", "泥工申请阶段未通过");
                        map.put("date", houseFlowApply.getModifyDate());
                        list.add(map);
                    }
                } else if (houseFlowApply.getWorkerType() == 9 && houseWorker.getWorkerType() == 9) {
                    map = new HashMap<>();
                    map.put("name", "木工进场");
                    map.put("date", houseWorker.getModifyDate());
                    list.add(map);
                    if (houseFlowApply.getApplyType() == 1) {
                        map = new HashMap<>();
                        map.put("name", "木工申请阶段完工");
                        map.put("date", houseFlowApply.getCreateDate());
                        list.add(map);
                    }
                    if (houseFlowApply.getMemberCheck() == 1) {
                        map = new HashMap<>();
                        map.put("name", "木工申请阶段通过");
                        map.put("date", houseFlowApply.getModifyDate());
                        list.add(map);
                    } else {
                        map = new HashMap<>();
                        map.put("name", "木工申请阶段未通过");
                        map.put("date", houseFlowApply.getModifyDate());
                        list.add(map);
                    }
                } else if (houseFlowApply.getWorkerType() == 10 && houseWorker.getWorkerType() == 10) {
                    map = new HashMap<>();
                    map.put("name", "油漆工进场");
                    map.put("date", houseWorker.getModifyDate());
                    list.add(map);
                    if (houseFlowApply.getApplyType() == 1) {
                        map = new HashMap<>();
                        map.put("name", "油漆工申请阶段完工");
                        map.put("date", houseFlowApply.getCreateDate());
                        list.add(map);
                    }
                    if (houseFlowApply.getMemberCheck() == 1) {
                        map = new HashMap<>();
                        map.put("name", "油漆工申请阶段通过");
                        map.put("date", houseFlowApply.getModifyDate());
                        list.add(map);
                    } else {
                        map = new HashMap<>();
                        map.put("name", "油漆工申请阶段未通过");
                        map.put("date", houseFlowApply.getModifyDate());
                        list.add(map);
                    }
                }
            }
        }
        for (HouseWorker houseWorker1 : houseWorkers) {
            if (houseWorker1.getWorkerType() == 3) {
                map = new HashMap<>();
                map.put("name", "大管家进场");
                map.put("date", houseWorker1.getModifyDate());
                list.add(map);
            }
        }
        collectDataDTO.setName("预计完工");
        collectDataDTO.setDate(date);
        collectDataDTO.setList(list);
        return ServerResponse.createBySuccess("查询成功", collectDataDTO);
    }

    /**
     * 订单列表（待收货、已经完成） --发货单
     *
     * @param pageDTO
     * @param userToken
     * @param houseId
     * @param cityId
     * @param orderStatus
     * @return
     */
    public ServerResponse queryDeliverOrderDsdListByStatus(PageDTO pageDTO, String userToken, String houseId, String cityId, String orderStatus) {
        try {
            Object object = memberAPI.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            JSONObject job = (JSONObject) object;
            Member member = job.toJavaObject(Member.class);
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());//初始化分页插获取用户信息件
            List<DjSplitDeliverOrderDTO> list = iBillDjDeliverOrderMapper.querySplitDeliverByHouse(cityId, houseId, orderStatus);
            if (list != null && list.size() > 0) {
                for (DjSplitDeliverOrderDTO djSplitDeliverOrderDTO : list) {
                    if (djSplitDeliverOrderDTO.getShippingState().equals("1") || djSplitDeliverOrderDTO.getShippingState().equals("7")) {
                        djSplitDeliverOrderDTO.setShippingType("10");
                    } else if (djSplitDeliverOrderDTO.getShippingState().equals("2") || djSplitDeliverOrderDTO.getShippingState().equals("8")) {
                        djSplitDeliverOrderDTO.setShippingType("11");
                        djSplitDeliverOrderDTO.setShippingState("1004");
                    }

                    String number = djSplitDeliverOrderDTO.getOrderNumber();//要货单号
                    Example example = new Example(OrderSplitItem.class);
                    example.createCriteria().andEqualTo(OrderSplitItem.ORDER_SPLIT_ID, number);
                    List<OrderSplitItem> orderSplitItemlist = billDjDeliverOrderSplitItemMapper.selectByExample(example);
                    if (orderSplitItemlist != null) {
                        String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
                        for (OrderSplitItem orderSplitItem : orderSplitItemlist) {
                            if (orderSplitItem != null)
                                orderSplitItem.setImage(address + orderSplitItem.getImage());
                        }
                        djSplitDeliverOrderDTO.setOrderSplitItemlist(orderSplitItemlist);
                        djSplitDeliverOrderDTO.setProductCount(orderSplitItemlist.size());//要货数大小
                    }

                }
            }

            PageInfo pageResult = new PageInfo(list);
            return ServerResponse.createBySuccess("查询所有订单", pageResult);
        } catch (Exception e) {
            logger.error("订单列表（待收货、已经完成）异常", e);
            return ServerResponse.createByErrorMessage("订单列表（待收货、已经完成）异常" + e);
        }
    }

    /**
     * 订单列表（全部订单、待付款、待发货） -- 订单列表
     *
     * @param pageDTO
     * @param userToken
     * @param houseId
     * @param cityId
     * @param orderStatus
     * @return
     */
    public ServerResponse queryDeliverOrderListByStatus(PageDTO pageDTO, String userToken, String houseId, String cityId, String orderStatus) {
        try {
            Object object = memberAPI.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            JSONObject job = (JSONObject) object;
            Member member = job.toJavaObject(Member.class);

            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());//初始化分页插获取用户信息件
            List<DjDeliverOrderDTO> list = iBillDjDeliverOrderMapper.selectDeliverOrderByHouse(cityId, houseId, orderStatus);
            for (DjDeliverOrderDTO jDeliverOrderDTO : list) {
                String orderId = jDeliverOrderDTO.getId();
                List<DjDeliverOrderItemDTO> djDeliverOrderItemDTOList = iBillDjDeliverOrderItemMapper.orderItemList(houseId, orderId);
                if (djDeliverOrderItemDTOList == null) {
                    jDeliverOrderDTO.setOrderItemlist(null);
                    jDeliverOrderDTO.setTotalSize(0);
                }
                Integer i = 0;// 是否预约计数
                for (DjDeliverOrderItemDTO djDeliverOrderItemDTO : djDeliverOrderItemDTOList) {
                    String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
                    djDeliverOrderItemDTO.setImageDetail(address + djDeliverOrderItemDTO.getImage());
                    String isReservationDeliver = djDeliverOrderItemDTO.getIsReservationDeliver();
                    if (isReservationDeliver != null && isReservationDeliver.equals("1")) {
                        i++;
                    }
                }
                String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
                jDeliverOrderDTO.setStorefrontIcon(imageAddress + jDeliverOrderDTO.getStorefrontIcon());//店铺图标
                jDeliverOrderDTO.setOrderItemlist(djDeliverOrderItemDTOList);
                jDeliverOrderDTO.setTotalSize(djDeliverOrderItemDTOList.size());

                Integer orderSource = jDeliverOrderDTO.getOrderSource();//订单来源(1,精算制作，2业主自购，3购物车）
                String dborderStatus = jDeliverOrderDTO.getOrderStatus();//订单状态

                if (dborderStatus != null && dborderStatus.equals("5")) {//已取消
                    List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
                    Map<String, Object> resultMap = new HashMap<>();
                    resultMap.put("name", "再次购买");
                    resultMap.put("type", 2);
                    rows.add(resultMap);
                    if (orderSource == 2 || orderSource == 3) {
                        Map<String, Object> resultMap2 = new HashMap<>();
                        resultMap2.put("name", "取消订单");
                        resultMap2.put("type", 1);
                        rows.add(resultMap2);
                    }
                    jDeliverOrderDTO.setButtonList(rows);
                }
                if (dborderStatus != null && dborderStatus.equals("1")) {
                    //1待付款
                    List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
                    Map<String, Object> resultMap = new HashMap<>();
                    resultMap.put("name", "付款");
                    resultMap.put("type", 4);
                    rows.add(resultMap);
                    if (orderSource == 2 || orderSource == 3) {
                        Map<String, Object> resultMap2 = new HashMap<>();
                        resultMap2.put("name", "取消订单");
                        resultMap2.put("type", 1);
                    }
                    jDeliverOrderDTO.setButtonList(rows);
                }
                //2已付款就是待发货
                if (dborderStatus != null && dborderStatus.equals("2")) {
                    if (i > 0) {
                        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
                        Map<String, Object> resultMap = new HashMap<>();
                        resultMap.put("name", "预约发货");
                        resultMap.put("type", 5);
                        rows.add(resultMap);
                        jDeliverOrderDTO.setButtonList(rows);
                    }
                }
            }
            PageInfo pageResult = new PageInfo(list);
            return ServerResponse.createBySuccess("查询所有订单", pageResult);
        } catch (Exception e) {
            logger.error("查询所有订单异常", e);
            return ServerResponse.createByErrorMessage("查询所有订单异常" + e);
        }
    }


    /**
     * 订单详情明细
     *
     * @param orderId
     * @return
     */
    public ServerResponse deliverOrderItemDetail(String orderId, Integer orderStatus) {
        try {

            Order order = iBillDjDeliverOrderMapper.selectByPrimaryKey(orderId);
            if (order == null) {
                return ServerResponse.createByErrorMessage("该订单不存在");
            }
            House house = houseAPI.selectHouseById(order.getHouseId());
            if (house == null) {
                return ServerResponse.createByErrorMessage("该房产不存在");
            }
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class); //图片地址
            AppOrderDetailDTO appOrderDetailDTO = iBillDjDeliverOrderMapper.selectOrderDetailById(order.getHouseId(), order.getId());
            if (appOrderDetailDTO != null) {
                List<AppOrderItemDetailDTO> list = iBillDjDeliverOrderMapper.selectOrderItemDetailById(appOrderDetailDTO.getOrderId(), orderStatus);
                for (AppOrderItemDetailDTO appOrderItemDetailDTO : list) {
                    if (appOrderItemDetailDTO != null) {
                        String productId = appOrderItemDetailDTO.getProductId();
                        String brandName = forMasterAPI.brandName("", productId);  //通过商品id去关联，然后组合商品名称
                        appOrderItemDetailDTO.setBrandName(brandName);//组合后的商品名称
                        appOrderItemDetailDTO.setImageDetail(address + appOrderItemDetailDTO.getImage());//商品图片详情
                    }
                    //待发货：增加退款按钮
                    if (orderStatus == 2) {
                        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
                        Map<String, Object> resultMap = new HashMap<>();
                        resultMap.put("name", "退款");
                        resultMap.put("type", 1);
                        rows.add(resultMap);
                        appOrderItemDetailDTO.setDetailMaplist(rows);
                    }
                    //已经取消：增加加入购物车按钮
                    if (orderStatus == 5) {
                        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
                        Map<String, Object> resultMap = new HashMap<>();
                        resultMap.put("name", "加入购物车");
                        resultMap.put("type", 2);
                        rows.add(resultMap);
                        appOrderItemDetailDTO.setDetailMaplist(rows);
                    }
                    //已经完成：增加退款和加入购物车按钮
                    if (orderStatus == 4) {
                        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
                        Map<String, Object> resultMap = new HashMap<>();
                        resultMap.put("name", "退款");
                        resultMap.put("type", 1);
                        rows.add(resultMap);
                        Map<String, Object> resultMap2 = new HashMap<>();
                        resultMap2.put("name", "加入购物车");
                        resultMap2.put("type", 2);
                        rows.add(resultMap2);
                        appOrderItemDetailDTO.setDetailMaplist(rows);
                    }
                }
                appOrderDetailDTO.setDetaillist(list);
                String houseName = house.getResidential() + house.getBuilding() + "栋" + house.getUnit() + "单元" + house.getNumber() + "号";
                appOrderDetailDTO.setShipAddress(houseName);//房子地址
            }
            return ServerResponse.createBySuccess("查询成功", appOrderDetailDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询订单明细异常");
        }

    }

    /**
     * 店铺--订单管理--列表
     * @param pageDTO 查询分页
     * @param userId 用户ID
     * @param cityId 城市ID
     * @param orderKey 查询
     * @param state 状态：3已支付
     * @return
     */
    public ServerResponse queryOrderInfo(PageDTO pageDTO, String userId, String cityId,
                                         String orderKey, Integer state) {
        try{
            Example example = new Example(Storefront.class);
            example.createCriteria().andEqualTo(Storefront.USER_ID, userId).
                    andEqualTo(Storefront.CITY_ID, cityId);
            List<Storefront> storefrontList = iBillStorefrontMapper.selectByExample(example);
            if (storefrontList == null && storefrontList.size() == 0) {
                return ServerResponse.createByErrorMessage("未找到符合条件的店铺，请先维护店铺信息");
            }
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            Map<String, Object> map = new HashMap<>();
            map.put("orderKey", orderKey);
            map.put("state", 3);//只查已支付的
            map.put("storefontId", storefrontList.get(0).getId());
            List<DOrderInfoDTO> orderInfoDTOS = iBillDjDeliverOrderMapper.queryOrderInfo(map);
            if(orderInfoDTOS!=null){
                for(DOrderInfoDTO orderInfoDTO:orderInfoDTOS){
                    String discountId=orderInfoDTO.getDiscountId();
                    if(orderInfoDTO.getTotalDiscountPrice()!=null&&orderInfoDTO.getTotalDiscountPrice()>0){
                        ActivityRedPackRecord redPackRecord=billActivityRedPackRecordMapper.selectByPrimaryKey(discountId);
                        ActivityRedPack activityRedPack=billActivityRedPackMapper.selectByPrimaryKey(redPackRecord.getRedPackId());
                        if(activityRedPack.getSourceType()==2){
                            orderInfoDTO.setDiscountType("1");
                        }else{
                            orderInfoDTO.setDiscountType("2");
                        }
                        orderInfoDTO.setDiscountNumber(redPackRecord.getPackNum());
                    }
                }
            }
            PageInfo orderInfoDTOSs = new PageInfo(orderInfoDTOS);
            /*map = new HashMap<>();
            map.put("storefontId", storefrontList.get(0).getId());
            List<DOrderInfoDTO> arrOrderInfoDTOS = iBillDjDeliverOrderMapper.queryOrderInfo(map);*/

            return ServerResponse.createBySuccess("查询成功", orderInfoDTOSs);
        }catch (Exception e){
            logger.error("查询失败",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 店铺--订单管理--查看详情
     *
     * @param orderId
     * @return
     */
    public ServerResponse queryOrderFineInfo(PageDTO pageDTO, String orderId) {
        try{
            if (CommonUtil.isEmpty(orderId)) {
                return ServerResponse.createByErrorMessage("订单id不能为空");
            }
            Order order=iBillDjDeliverOrderMapper.selectByPrimaryKey(orderId);
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<DOrderFineInfoDTO> dOrderArrInfoDTO = iBillDjDeliverOrderMapper.queryOrderFineInfo(orderId);
            Double totalPrice=0d;//商品总额（单价*数量）
            String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            for (DOrderFineInfoDTO dOrderFineInfoDTO : dOrderArrInfoDTO) {
                dOrderFineInfoDTO.setImage(StringTool.getImage(dOrderFineInfoDTO.getImage(),imageAddress));
                totalPrice=MathUtil.add(totalPrice,dOrderFineInfoDTO.getTotalPrice());
            }
            PageInfo orderInfoDTOSs = new PageInfo(dOrderArrInfoDTO);
            Map<String, Object> map =iBillDjDeliverOrderMapper.selectDeliverOrderInfoById(orderId);
            //查询房子信息，获取房子对应的楼层
            QuantityRoom quantityRoom=iQuantityRoomMapper.getBillQuantityRoom(order.getHouseId(),0);
            if(quantityRoom!=null&& StringUtils.isNotBlank(quantityRoom.getId())){
                Integer elevator=quantityRoom.getElevator();//是否电梯房
                String floor=quantityRoom.getFloor();//楼层
                map.put("elevator", elevator);//是否电梯房
                map.put("floor", floor);//楼层
            }
            //查询优惠卷信息
            map.put("totalPrice",totalPrice);//商品小计（单价*数量）

            if(order.getTotalDiscountPrice()!=null&&order.getTotalDiscountPrice().doubleValue()>0){
                Example example=new Example(BusinessOrder.class);
                example.createCriteria().andEqualTo(BusinessOrder.NUMBER,order.getBusinessOrderNumber());
                BusinessOrder businessOrder=billBusinessOrderMapper.selectOneByExample(example);
                String discountId=businessOrder.getRedPacketPayMoneyId();
                ActivityRedPackRecord redPackRecord=billActivityRedPackRecordMapper.selectByPrimaryKey(discountId);
                ActivityRedPack activityRedPack=billActivityRedPackMapper.selectByPrimaryKey(redPackRecord.getRedPackId());
                if(activityRedPack.getSourceType()==2){
                    map.put("discountType", "1");//店铺券
                }else{
                    map.put("discountType", "2");//平台券
                }
                map.put("discountNumber", redPackRecord.getPackNum());//优惠卷编号
                if(activityRedPack.getType()==0){
                    map.put("discountName", "满"+activityRedPack.getSatisfyMoney()+"减"+activityRedPack.getMoney());//优惠卷方式
                    map.put("discountPrice", activityRedPack.getMoney()+"元");//优惠卷金额
                }else if(activityRedPack.getType()==1){
                    if(activityRedPack.getSatisfyMoney()!=null&&activityRedPack.getSatisfyMoney().doubleValue()>0){
                        map.put("discountName", "满"+activityRedPack.getSatisfyMoney()+"打"+activityRedPack.getMoney()+"折");//优惠卷方式
                        map.put("discountPrice", activityRedPack.getMoney()+"元");//优惠卷金额
                    }else{
                        map.put("discountName", "无门槛");//优惠卷方式
                        map.put("discountPrice", activityRedPack.getMoney()+"折");//优惠卷金额
                    }
                }else{
                    map.put("discountName", "无门槛");//优惠卷方式
                    map.put("discountPrice", activityRedPack.getMoney()+"元");//优惠卷金额
                }
            }
            map.put("list",orderInfoDTOSs);//订单商品明细
            return ServerResponse.createBySuccess("查询成功", map);
        }catch (Exception e){
            logger.error("查询失败",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }

    }



    /**
     * 发货详情
     *
     * @param orderId
     * @param orderStatus
     * @return
     */
    public ServerResponse shippingDetail(String orderId, Integer orderStatus) {
        try {
            return null;
        } catch (Exception e) {
            logger.error("发货详情异常", e);
            return ServerResponse.createByErrorMessage("发货详情异常" + e);
        }
    }

    /**
     * 搬运费详情
     *
     * @param orderId
     * @param orderStatus
     * @return
     */
    public ServerResponse stevedorageCostDetail(PageDTO pageDTO, String orderId, Integer orderStatus) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());//初始化分页插获取用户信息件
            List<CostDetailDTO> list = iBillDjDeliverOrderMapper.queryStevedorage(orderId);
            PageInfo pageResult = new PageInfo(list);
            return ServerResponse.createBySuccess("查询所有订单", pageResult);
        } catch (Exception e) {
            logger.error("搬运费详情异常", e);
            return ServerResponse.createByErrorMessage("搬运费详情异常" + e);
        }
    }


    /**
     * 运费详情
     *
     * @param orderId
     * @param orderStatus
     * @return
     */
    public ServerResponse transportationCostDetail(PageDTO pageDTO, String orderId, Integer orderStatus) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());//初始化分页插获取用户信息件
            List<CostDetailDTO> list = iBillDjDeliverOrderMapper.queryTransportationCost(orderId);
            PageInfo pageResult = new PageInfo(list);
            return ServerResponse.createBySuccess("查询所有订单", pageResult);
        } catch (Exception e) {
            logger.error("运费详情异常", e);
            return ServerResponse.createByErrorMessage("运费详情异常" + e);
        }
    }


    /**
     * App订单列表（待收货、已完成、待评价） --发货单
     *
     * @param pageDTO
     * @param houseId
     * @param cityId
     * @param orderStatus
     * @return
     */
    public ServerResponse queryAppOrderList(PageDTO pageDTO,
                                            String userToken,
                                            String houseId,
                                            String cityId,
                                            Integer orderStatus,
                                            String idList) {
        try {
            Object object = memberAPI.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            JSONObject job = (JSONObject) object;
            Member member = job.toJavaObject(Member.class);
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());//初始化分页插获取用户信息件
            String[] arr;
            if (!CommonUtil.isEmpty(idList)) {
                arr = idList.split(",");
            } else {
                arr = null;
            }

            List<DjSplitDeliverOrderDTO> list = iBillDjDeliverOrderMapper.queryAppOrderList(cityId,member.getId(), houseId, orderStatus, arr);
            if (list != null && list.size() > 0) {
                for (DjSplitDeliverOrderDTO djSplitDeliverOrderDTO : list) {

                    if (djSplitDeliverOrderDTO.getShippingState().equals("1") || djSplitDeliverOrderDTO.getShippingState().equals("7")) {
                        //待收货
                        djSplitDeliverOrderDTO.setShippingType("10");
                    } else if (djSplitDeliverOrderDTO.getShippingState().equals("8") ||
                            djSplitDeliverOrderDTO.getShippingState().equals("2") || djSplitDeliverOrderDTO.getShippingState().equals("5")) {
                        // 已完成
                        djSplitDeliverOrderDTO.setShippingType("11");
                        djSplitDeliverOrderDTO.setShippingState("1004");
                    } else if (djSplitDeliverOrderDTO.getShippingState().equals("10")) {
                        //待评价
                        djSplitDeliverOrderDTO.setShippingType("13");
                        djSplitDeliverOrderDTO.setShippingState("10");
                    }

                    String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
                    djSplitDeliverOrderDTO.setStorefrontIcon(address + djSplitDeliverOrderDTO.getStorefrontIcon());
                    Example example = new Example(OrderSplitItem.class);
                    example.createCriteria().andEqualTo(OrderSplitItem.ORDER_SPLIT_ID, djSplitDeliverOrderDTO.getId());
                    List<OrderSplitItem> orderSplitItemlist = billDjDeliverOrderSplitItemMapper.selectByExample(example);
                    if (orderSplitItemlist != null) {
                        djSplitDeliverOrderDTO.setProductImageArr(getStartTwoImage1(orderSplitItemlist, address));
                        djSplitDeliverOrderDTO.setProductCount(orderSplitItemlist.size());//要货数大小
                        djSplitDeliverOrderDTO.setProductName(orderSplitItemlist.get(0).getProductName());
                    }

                    OrderSplit orderSplit = billDjDeliverOrderSplitMapper.selectByPrimaryKey(djSplitDeliverOrderDTO.getOrderSplitId());
                    if (orderSplit != null) {
                        Member member1 = queryWorker(orderSplit.getHouseId(), orderSplit.getWorkerTypeId());
                        if (member1 != null) {
                            //人工姓名
                            djSplitDeliverOrderDTO.setName(member1.getName());
                            djSplitDeliverOrderDTO.setWorkerId(member1.getId());
                        }
                    }
                }
            }
            if (list.size() <= 0) {
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            }
            PageInfo pageResult = new PageInfo(list);
            return ServerResponse.createBySuccess("查询所有订单", pageResult);
        } catch (Exception e) {
            logger.error("订单列表（待收货、已经完成）异常", e);
            return ServerResponse.createByErrorMessage("订单列表（待收货、已经完成）异常" + e);
        }
    }


    /**
     * App订单列表（待发货）
     *
     * @param pageDTO
     * @param houseId
     * @param cityId
     * @return
     */
    public ServerResponse queryAppHairOrderList(String userToken,
                                                PageDTO pageDTO,
                                                String houseId,
                                                String cityId) {
        try {
            Object object = memberAPI.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            JSONObject job = (JSONObject) object;
            Member member = job.toJavaObject(Member.class);
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());//初始化分页插获取用户信息件
            List<DjSplitDeliverOrderDTO> list = iBillDjDeliverOrderMapper.queryAppHairOrderList(cityId, houseId,member.getId());
            if (list != null && list.size() > 0) {
                for (DjSplitDeliverOrderDTO djSplitDeliverOrderDTO : list) {
                    //待发货
                    djSplitDeliverOrderDTO.setShippingType("12");
                    djSplitDeliverOrderDTO.setShippingState("1004");

                    String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
                    djSplitDeliverOrderDTO.setStorefrontIcon(address + djSplitDeliverOrderDTO.getStorefrontIcon());

                    List<String> strList = new ArrayList<>();

                    List<String> result = Arrays.asList(djSplitDeliverOrderDTO.getImage().split(","));
                    for (int i = 0; i < result.size(); i++) {
                        String str = address + result.get(i);
                        strList.add(str);
                    }
                    if (strList.size() > 2) {
                        String join = String.join(",", strList.subList(0, 2));
                        djSplitDeliverOrderDTO.setProductImageArr(join);
                    } else {
                        String join = String.join(",", strList.subList(0, 1));
                        djSplitDeliverOrderDTO.setProductImageArr(join);
                    }
                    djSplitDeliverOrderDTO.setProductCount(list.size());//要货数大小

                    OrderSplit orderSplit = billDjDeliverOrderSplitMapper.selectByPrimaryKey(djSplitDeliverOrderDTO.getOrderSplitId());
                    if (orderSplit != null) {
                        Member member1 = queryWorker(orderSplit.getHouseId(), orderSplit.getWorkerTypeId());
                        if (member1 != null) {
                            //人工姓名
                            djSplitDeliverOrderDTO.setName(member1.getName());
                            djSplitDeliverOrderDTO.setWorkerId(member1.getId());
                        }
                    }
                }
            }
            if (list.size() <= 0) {
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            }
            PageInfo pageResult = new PageInfo(list);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            logger.error("查询异常", e);
            return ServerResponse.createByErrorMessage("查询异常" + e);
        }
    }


    /**
     * 获取前两个商品的图片
     *
     * @return
     */
    String getStartTwoImage1(List<OrderSplitItem> os, String address) {
        String imageUrl = "";
        if (os != null && os.size() > 0) {
            for (OrderSplitItem ap : os) {
                String image = ap.getImage();
                //添加图片详情地址字段
                if (StringUtils.isNotBlank(image)) {
                    String[] imgArr = image.split(",");
                    if (StringUtils.isBlank(imageUrl)) {
                        imageUrl = address + imgArr[0];
                    } else {
                        imageUrl = imageUrl + "," + address + imgArr[0];
                        break;
                    }
                }
            }
        }
        return imageUrl;
    }

    /**
     * App 详情确定收货
     *
     * @param
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse updateAppOrderStats(String lists, String id) {
        try {
            if (CommonUtil.isEmpty(lists)) {
                return ServerResponse.createByErrorMessage("lists不能为空");
            }
            if (CommonUtil.isEmpty(id)) {
                return ServerResponse.createByErrorMessage("id不能为空");
            }

            JSONArray list = JSON.parseArray(lists);
            if (list.size() == 0) {
                return ServerResponse.createByErrorMessage("参数错误");
            }
            boolean isFlag = false;
            for (int i = 0; i < list.size(); i++) {
                JSONObject JS = list.getJSONObject(i);
                OrderSplitItem orderSplit = new OrderSplitItem();
                Double receive = JS.getDouble("receive");//收货数量
                Double num = JS.getDouble("shopCount");//发货数量(总数)
                String productId = JS.getString("productId");//要货货品id
                String houseId = JS.getString("houseId");//要货房子id
                Example example = new Example(OrderItem.class);
                example.createCriteria().andEqualTo(OrderItem.HOUSE_ID, houseId)
                        .andEqualTo(OrderItem.PRODUCT_ID, productId)
                        .andEqualTo(OrderItem.ORDER_ID, id);
                example.orderBy(OrderItem.CREATE_DATE).desc();
                /*OrderItem item = iBillDjDeliverOrderItemMapper.selectOneByExample(example);
                if (receive < item.getShopCount()) {
                    //部分收货
                    isFlag = true;
                    Double retur = num - receive;//发货数量 减去 收货数量 =  退货数量
                    if (item.getAskCount() > retur) {
                        //订单数量大于退货数量
                        retur = item.getAskCount() - retur;
                        item.setAskCount(retur);
                        item.setModifyDate(new Date());
                        //修改订单明细数量
                        iBillDjDeliverOrderItemMapper.updateByPrimaryKeySelective(item);
                    } else if (item.getAskCount() == retur) {
                        retur = item.getAskCount() - retur;
                        item.setAskCount(retur);
                        item.setModifyDate(new Date());
                        //修改订单明细数量
                        iBillDjDeliverOrderItemMapper.updateByPrimaryKeySelective(item);
                    } else if (item.getAskCount() < retur) {
                        retur = retur - item.getAskCount();
                        item.setAskCount(retur);
                        item.setModifyDate(new Date());
                        //修改订单明细数量
                        iBillDjDeliverOrderItemMapper.updateByPrimaryKeySelective(item);
                    }

                    //修改业主仓库数量
                    example.createCriteria().andEqualTo(Warehouse.HOUSE_ID, houseId)
                            .andEqualTo(Warehouse.PRODUCT_ID, productId)
                            .andEqualTo(Warehouse.DATA_STATUS, 0);
                    List<Warehouse> warehouses = iBillWarehouseMapper.selectByExample(example);
                    if (warehouses != null && warehouses.size() > 0) {
                        double ss = num - receive;//发货数量 减去 收货数量 =  退货数量
                        Warehouse warehouse = new Warehouse();
                        warehouse.setAskCount(warehouses.get(0).getAskCount() - ss);
                        warehouse.setModifyDate(new Date());
                        iBillWarehouseMapper.updateByPrimaryKeySelective(warehouse);
                    }
                }*/
                orderSplit.setId(JS.getString("id"));
                orderSplit.setReceive(JS.getDouble("receive"));
                //修改要货单详情数量
                billDjDeliverOrderSplitItemMapper.updateByPrimaryKeySelective(orderSplit);
            }

            SplitDeliver splitDeliver = new SplitDeliver();
            if (!isFlag) {
                //正常收货
                Example example = new Example(OrderSplitItem.class);
                example.createCriteria().andEqualTo(OrderSplitItem.SPLIT_DELIVER_ID, id);
                List<OrderSplitItem> orderSplitItem = billDjDeliverOrderSplitItemMapper.selectByExample(example);
                for (OrderSplitItem splitItem : orderSplitItem) {
                    if (CommonUtil.isEmpty(splitItem.getIsDeliveryInstall()) || splitItem.getIsDeliveryInstall().equals("0")) {
                        //isDeliveryInstall == null 默认为 0-否 不安装
                        //待评价
                        splitDeliver.setShippingState(10);
                    } else {
                        //安装
                        if (splitItem.getIsDeliveryInstall().equals("1")) {
                            //有需要安装商品，状态改为待安装
                            splitDeliver.setShippingState(9);
                            break;
                        } else {
                            // 待评价
                            splitDeliver.setShippingState(10);
                        }
                    }
                }
            } else {
                //部分收货
                splitDeliver.setShippingState(4);
            }
            splitDeliver.setId(id);
            splitDeliver.setRecTime(new Date());
            //修改发货单状态
            billDjDeliverSplitDeliverMapper.updateByPrimaryKeySelective(splitDeliver);

            return ServerResponse.createBySuccessMessage("收货成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("收货失败");
        }
    }

    /**
     * App 详情拒绝收货
     *
     * @param
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse refuseAppOrderStats(String id) {
        try {

            if (CommonUtil.isEmpty(id)) {
                return ServerResponse.createByErrorMessage("id不能为空");
            }

            Example example = new Example(OrderSplitItem.class);
            example.createCriteria().andEqualTo(OrderSplitItem.SPLIT_DELIVER_ID, id)
                    .andEqualTo(OrderSplitItem.DATA_STATUS, 0);
            List<OrderSplitItem> orderSplitItem = billDjDeliverOrderSplitItemMapper.selectByExample(example);


            SplitDeliver splitDeliver;
            splitDeliver = billDjDeliverSplitDeliverMapper.selectByPrimaryKey(id);
            if (DateUtil.daysofTwo(new Date(), splitDeliver.getRecTime()) > 7) {
                return ServerResponse.createByErrorMessage("发货已超过7天不能拒绝收货");
            }

            if (orderSplitItem != null && orderSplitItem.size() > 0) {
                for (OrderSplitItem splitItem : orderSplitItem) {
                    example = new Example(OrderItem.class);
                    example.createCriteria().andEqualTo(OrderItem.HOUSE_ID, splitItem.getHouseId())
                            .andEqualTo(OrderItem.PRODUCT_ID, splitItem.getProductId());
                    example.orderBy(OrderItem.CREATE_DATE).desc();
                    List<OrderItem> orderItem = iBillDjDeliverOrderItemMapper.selectByExample(example);
                    Double num = splitItem.getNum();
                    if (orderItem != null && orderItem.size() > 0) {
                        for (OrderItem item : orderItem) {
                            if (item.getAskCount() > num) {
                                //订单数量大于退货数量
                                num = item.getAskCount() - num;
                                item.setAskCount(num);
                                item.setModifyDate(new Date());
                                //修改订单明细数量
                                iBillDjDeliverOrderItemMapper.updateByPrimaryKeySelective(item);
                                break;
                            } else if (item.getAskCount() == num) {
                                //订单数量等于退货数量
                                num = item.getAskCount() - num;
                                item.setAskCount(num);
                                item.setModifyDate(new Date());
                                //修改订单明细数量
                                iBillDjDeliverOrderItemMapper.updateByPrimaryKeySelective(item);
                                break;
                            } else if (item.getAskCount() < num) {
                                //订单数量小于退货数量
                                num = num - item.getAskCount();
                                item.setAskCount(num);
                                item.setModifyDate(new Date());
                                //修改订单明细数量
                                iBillDjDeliverOrderItemMapper.updateByPrimaryKeySelective(item);
                            }
                        }
                    }

                    //修改业主仓库数量
                    example.createCriteria().andEqualTo(Warehouse.HOUSE_ID, splitItem.getHouseId())
                            .andEqualTo(Warehouse.PRODUCT_ID, splitItem.getProductId())
                            .andEqualTo(Warehouse.DATA_STATUS, 0);
                    List<Warehouse> warehouses = iBillWarehouseMapper.selectByExample(example);
                    if (warehouses != null && warehouses.size() > 0) {
                        Warehouse warehouse = new Warehouse();
                        warehouse.setAskCount(num);
                        warehouse.setModifyDate(new Date());
                        iBillWarehouseMapper.updateByPrimaryKeySelective(warehouse);
                    }

                    //修改要货单明细数量
                    splitItem.setReceive((double) 0);
                    splitItem.setModifyDate(new Date());
                    billDjDeliverOrderSplitItemMapper.updateByPrimaryKeySelective(splitItem);
                }
            }

            splitDeliver = new SplitDeliver();
            //9-拒绝收货
            splitDeliver.setShippingState(9);
            splitDeliver.setId(id);
            splitDeliver.setRecTime(new Date());
            //修改发货单状态
            billDjDeliverSplitDeliverMapper.updateByPrimaryKeySelective(splitDeliver);
            return ServerResponse.createBySuccessMessage("拒绝成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("拒绝失败");
        }
    }

    /**
     * App 确定安装
     *
     * @param
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse installAppOrderStats(String id) {
        try {

            if (CommonUtil.isEmpty(id)) {
                return ServerResponse.createByErrorMessage("id不能为空");
            }

            SplitDeliver splitDeliver = new SplitDeliver();
            //8-已完成
            splitDeliver.setShippingState(8);
            splitDeliver.setId(id);
            splitDeliver.setRecTime(new Date());
            //修改发货单状态
            billDjDeliverSplitDeliverMapper.updateByPrimaryKeySelective(splitDeliver);
            return ServerResponse.createBySuccessMessage("收货成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("收货失败");
        }
    }

    /**
     * app订单详情查询（待收货，待安装，已完成）
     *
     * @param id
     * @return
     */
    public ServerResponse queryAppOrderInFoList(String id, String shippingState) {
        if (CommonUtil.isEmpty(id)) {
            return ServerResponse.createByErrorMessage("id不能为空");
        }
        if (CommonUtil.isEmpty(shippingState)) {
            return ServerResponse.createByErrorMessage("shippingState不能为空");
        }

        if (shippingState.equals("1004")) {
            //已完成
            shippingState = "8";
        }
        Example example = new Example(SplitDeliver.class);
        example.createCriteria().andEqualTo(SplitDeliver.SHIPPING_STATE, Integer.parseInt(shippingState))
                .andEqualTo(SplitDeliver.ID, id)
                .andEqualTo(SplitDeliver.DATA_STATUS, 0);
        SplitDeliver splitDeliver = billDjDeliverSplitDeliverMapper.selectOneByExample(example);

        if (splitDeliver == null) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        Storefront storefront = iBillStorefrontMapper.selectByPrimaryKey(splitDeliver.getStorefrontId());
        if (storefront == null) {
            return ServerResponse.createByErrorMessage("门店不存在");
        }

        String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        OrderCollectInFoDTO orderCollectInFoDTO = new OrderCollectInFoDTO();
        MemberAddress memberAddress=iBillMemberAddressMapper.selectByPrimaryKey(splitDeliver.getAddressId());
        House house = null;
        if(memberAddress!=null){
            orderCollectInFoDTO.setHouseId(memberAddress.getHouseId());
            orderCollectInFoDTO.setHouseName( memberAddress.getAddress());

        }else{
            house=houseMapper.selectByPrimaryKey(splitDeliver.getHouseId());
            if(house!=null){
                orderCollectInFoDTO.setHouseId(house.getHouseId());
                orderCollectInFoDTO.setHouseName( house.getResidential() + house.getBuilding() + "栋 " + house.getUnit() + "单元" + house.getNumber() + "号");
            }
        }
        OrderSplit orderSplit = billDjDeliverOrderSplitMapper.selectByPrimaryKey(splitDeliver.getOrderSplitId());
        Order order = iBillDjDeliverOrderMapper.selectByPrimaryKey(orderSplit.getOrderId());
        if("6".equals(order.getOrderSource())){
            example=new Example(Order.class);
            example.createCriteria().andEqualTo(Order.DATA_STATUS, 0)
                    .andEqualTo(Order.ORDER_STATUS, 2)
                    .orEqualTo(Order.PARENT_ORDER_ID,order.getParentOrderId())
                    .orEqualTo(Order.ID,order.getId());
            example.orderBy(Order.ORDER_GENERATION_TIME).asc();
            List<Order> orders = iBillDjDeliverOrderMapper.selectByExample(example);
            List<String> list=new ArrayList<>();
            orders.forEach(order1 -> {
                Member member = iBillMemberMapper.selectByPrimaryKey(order1.getMemberId());
                list.add(address+member.getHead());
            });
            orderCollectInFoDTO.setHeadList(list);
        }
        orderCollectInFoDTO.setCreateDate(splitDeliver.getCreateDate());
        orderCollectInFoDTO.setRecTime(splitDeliver.getRecTime());
        orderCollectInFoDTO.setSendTime(splitDeliver.getRecTime());
        orderCollectInFoDTO.setNumber(splitDeliver.getNumber());
        orderCollectInFoDTO.setActualPaymentPrice(splitDeliver.getTotalAmount());
        orderCollectInFoDTO.setInstallName(splitDeliver.getInstallName());//安装人姓名
        orderCollectInFoDTO.setInstallMobile(splitDeliver.getInstallMobile());//安装人号码
        orderCollectInFoDTO.setDeliveryMobile(splitDeliver.getDeliveryMobile());
        orderCollectInFoDTO.setTotalAmount(splitDeliver.getTotalAmount());
        //送货人号码
        orderCollectInFoDTO.setDeliveryName(splitDeliver.getDeliveryName());//送货人姓名
        orderCollectInFoDTO.setId(splitDeliver.getId());


        example = new Example(OrderSplitItem.class);
        example.createCriteria().andEqualTo(OrderSplitItem.SPLIT_DELIVER_ID, splitDeliver.getId())
                .andEqualTo(OrderSplitItem.DATA_STATUS, 0);
        List<OrderSplitItem> orderSplitItem = billDjDeliverOrderSplitItemMapper.selectByExample(example);

        Map<String, Object> map;
        List<Map<String, Object>> list = new ArrayList<>();
        if (orderSplitItem != null && orderSplitItem.size() > 0) {
            for (OrderSplitItem splitItem : orderSplitItem) {
                Integer type = iBillDjDeliverOrderMapper.queryTypeArr(splitItem.getId());
                String str = iBillDjDeliverOrderMapper.queryValueIdArr(splitItem.getId());
                map = new HashMap<>();
                if (!CommonUtil.isEmpty(str)) {
                    String valueNameArr = billProductTemplateService.getNewValueNameArr(str);
                    map.put("valueNameArr", valueNameArr);
                } else {
                    map.put("valueNameArr", "");
                }
                map.put("type", type);
                map.put("id", splitItem.getId());
                map.put("productName", splitItem.getProductName());
                map.put("productId", splitItem.getProductId());
                map.put("houseId", splitItem.getHouseId());
                map.put("shopCount", splitItem.getNum());
                map.put("price", splitItem.getPrice());
                map.put("unitName", splitItem.getUnitName());
                List<String> result = Arrays.asList(splitItem.getImage().split(","));
                map.put("image", address + result.get(0));
                map.put("isDeliveryInstall", splitItem.getIsDeliveryInstall());
                map.put("storefrontId", splitItem.getStorefrontId());//店铺Id

                Integer sales = iBillDjDeliverOrderMapper.querySales(splitItem.getId());
                //该商品为 可退货 + 是预约发货 才能退货
                if (sales != null && sales == 0 && splitItem.getIsReservationDeliver() == 0) {
                    //flag 等于 true  可退货
                    map.put("flag", "true");
                } else {
                    //flag 等于 false  不可退货
                    map.put("flag", "false");
                }

                list.add(map);
            }
        }

        Map<String, Object> mapArr = new HashMap<>();
        mapArr.put("storefrontIcon", address + storefront.getSystemLogo());//店铺图标
        mapArr.put("storefrontName", storefront.getStorefrontName());//店铺名称
        mapArr.put("storefrontType", storefront.getStorefrontType());//店铺类型（实物商品：product，人工商品：worker)
        mapArr.put("storefrontId", storefront.getId());//店铺Id
        mapArr.put("mobile", storefront.getMobile());//店铺电话
        mapArr.put("id", splitDeliver.getId());//发货单id
        mapArr.put("appointmentDTOS", list);//商品详情
        List<Map<String, Object>> listArr = new ArrayList<>();
        listArr.add(mapArr);
        if (listArr.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }

        //查询购物车数量
        example = new Example(ShoppingCart.class);
        example.createCriteria().andEqualTo(ShoppingCart.MEMBER_ID, house!=null?house.getMemberId():memberAddress.getMemberId());
        orderCollectInFoDTO.setShoppingCartsCount(iBillShoppingCartMapper.selectCountByExample(example));

        orderCollectInFoDTO.setOrderStorefrontDTOS(listArr);
        return ServerResponse.createBySuccess("查询成功", orderCollectInFoDTO);
    }

    /**
     * App订单详情（待发货）
     *
     * @param id
     * @return
     */
    public ServerResponse queryAppHairOrderInFo(String id) {
        try {
            String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            if (CommonUtil.isEmpty(id)) {
                return ServerResponse.createByErrorMessage("id不能为空");
            }

            OrderSplit orderSplit = billDjDeliverOrderSplitMapper.selectByPrimaryKey(id);
            if (orderSplit == null) {
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            }
            Storefront storefront = iBillStorefrontMapper.selectByPrimaryKey(orderSplit.getStorefrontId());
            if (storefront == null) {
                return ServerResponse.createByErrorMessage("门店不存在");
            }

            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            OrderCollectInFoDTO orderCollectInFoDTO = new OrderCollectInFoDTO();
            MemberAddress memberAddress=iBillMemberAddressMapper.selectByPrimaryKey(orderSplit.getAddressId());
            if(memberAddress!=null){
                orderCollectInFoDTO.setHouseId(memberAddress.getHouseId());
                orderCollectInFoDTO.setHouseName( memberAddress.getAddress());

            }else{
                House house=houseMapper.selectByPrimaryKey(orderSplit.getHouseId());
                if(house!=null){
                    orderCollectInFoDTO.setHouseId(house.getHouseId());
                    orderCollectInFoDTO.setHouseName( house.getResidential() + house.getBuilding() + "栋" + house.getUnit() + " 单元" + house.getNumber() + "号");
                }
            }
            Order order = iBillDjDeliverOrderMapper.selectByPrimaryKey(orderSplit.getOrderId());
            if("6".equals(order.getOrderSource())){
                Example example=new Example(Order.class);
                example.createCriteria().andEqualTo(Order.DATA_STATUS, 0)
                        .andEqualTo(Order.ORDER_STATUS, 2)
                        .orEqualTo(Order.PARENT_ORDER_ID,order.getParentOrderId())
                        .orEqualTo(Order.ID,order.getId());
                example.orderBy(Order.ORDER_GENERATION_TIME).asc();
                List<Order> orders = iBillDjDeliverOrderMapper.selectByExample(example);
                List<String> list=new ArrayList<>();
                orders.forEach(order1 -> {
                    Member member = iBillMemberMapper.selectByPrimaryKey(order1.getMemberId());
                    list.add(imageAddress+member.getHead());
                });
                orderCollectInFoDTO.setHeadList(list);
            }
            orderCollectInFoDTO.setCreateDate(orderSplit.getCreateDate());
            orderCollectInFoDTO.setNumber(orderSplit.getNumber());
            orderCollectInFoDTO.setActualPaymentPrice(orderSplit.getTotalAmount()!=null?orderSplit.getTotalAmount().doubleValue():0);
            orderCollectInFoDTO.setTotalAmount(orderSplit.getTotalAmount()!=null?orderSplit.getTotalAmount().doubleValue():0);
            Example example = new Example(OrderSplitItem.class);
            example.createCriteria().andEqualTo(OrderSplitItem.ORDER_SPLIT_ID, orderSplit.getId())
                    .andEqualTo(OrderSplitItem.DATA_STATUS, 0);
            List<OrderSplitItem> orderSplitItem = billDjDeliverOrderSplitItemMapper.selectByExample(example);


            Map<String, Object> map;
            List<Map<String, Object>> list = new ArrayList<>();
            if (orderSplitItem != null && orderSplitItem.size() > 0) {
                for (OrderSplitItem splitItem : orderSplitItem) {
                    Integer type = iBillDjDeliverOrderMapper.queryTypeArr(splitItem.getId());
                    String str = iBillDjDeliverOrderMapper.queryValueIdArr(splitItem.getId());
                    map = new HashMap<>();
                    if (!CommonUtil.isEmpty(str)) {
                        String valueNameArr = billProductTemplateService.getNewValueNameArr(str);
                        map.put("valueNameArr", valueNameArr);
                    } else {
                        map.put("valueNameArr", "");
                    }
                    map.put("type", type);
                    map.put("id", splitItem.getId());
                    map.put("productName", splitItem.getProductName());
                    map.put("productId", splitItem.getProductId());
                    map.put("houseId", splitItem.getHouseId());
                    map.put("shopCount", splitItem.getNum());
                    map.put("price", splitItem.getPrice());
                    map.put("unitName", splitItem.getUnitName());
                    List<String> result = Arrays.asList(splitItem.getImage().split(","));
                    map.put("image", address + result.get(0));
                    map.put("isDeliveryInstall", splitItem.getIsDeliveryInstall());
                    map.put("storefrontId", splitItem.getStorefrontId());//店铺Id
                    map.put("isReservationDeliver", splitItem.getIsReservationDeliver());//是否需要预约(1是，0否）
                    map.put("reservationDeliverTime", splitItem.getReservationDeliverTime());//预约发货时间
                    list.add(map);
                }
            }

            Map<String, Object> mapArr = new HashMap<>();
            mapArr.put("storefrontIcon", address + storefront.getSystemLogo());//店铺图标
            mapArr.put("storefrontName", storefront.getStorefrontName());//店铺名称
            mapArr.put("storefrontType", storefront.getStorefrontType());//店铺类型（实物商品：product，人工商品：worker)
            mapArr.put("storefrontId", storefront.getId());//店铺Id
            mapArr.put("mobile", storefront.getMobile());//店铺电话
            mapArr.put("id", orderSplit.getId());//要货单id
            mapArr.put("appointmentDTOS", list);//商品详情
            List<Map<String, Object>> listArr = new ArrayList<>();
            listArr.add(mapArr);
            if (listArr.size() <= 0) {
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            }

            //查询购物车数量
            example = new Example(ShoppingCart.class);
            example.createCriteria().andEqualTo(ShoppingCart.MEMBER_ID, orderSplit.getMemberId());
            orderCollectInFoDTO.setShoppingCartsCount(iBillShoppingCartMapper.selectCountByExample(example));
            orderCollectInFoDTO.setOrderStorefrontDTOS(listArr);
            return ServerResponse.createBySuccess("查询成功", orderCollectInFoDTO);
        } catch (Exception e) {
            logger.error("查询异常", e);
            return ServerResponse.createByErrorMessage("查询异常" + e);
        }
    }

    /**
     * app订单详情查询（待收货- 人工）
     *
     * @param id
     * @return
     */
    public ServerResponse queryAppOrderWorkerInFoList(String id, String shippingState) {
        if (CommonUtil.isEmpty(id)) {
            return ServerResponse.createByErrorMessage("id不能为空");
        }

        Example example = new Example(SplitDeliver.class);
        example.createCriteria().andEqualTo(SplitDeliver.SHIPPING_STATE, Integer.parseInt(shippingState))
                .andEqualTo(SplitDeliver.ID, id)
                .andEqualTo(SplitDeliver.DATA_STATUS, 0);
        SplitDeliver splitDeliver = billDjDeliverSplitDeliverMapper.selectOneByExample(example);

        if (splitDeliver == null) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }

        Storefront storefront = iBillStorefrontMapper.selectByPrimaryKey(splitDeliver.getStorefrontId());
        if (storefront == null) {
            return ServerResponse.createByErrorMessage("门店不存在");
        }

        String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        OrderCollectInFoDTO orderCollectInFoDTO = new OrderCollectInFoDTO();
        MemberAddress memberAddress=iBillMemberAddressMapper.selectByPrimaryKey(splitDeliver.getAddressId());
        if(memberAddress!=null){
            orderCollectInFoDTO.setHouseId(memberAddress.getHouseId());
            orderCollectInFoDTO.setHouseName( memberAddress.getAddress());

        }else{
            House house=houseMapper.selectByPrimaryKey(splitDeliver.getHouseId());
            if(house!=null){
                orderCollectInFoDTO.setHouseId(house.getHouseId());
                orderCollectInFoDTO.setHouseName( house.getResidential() + house.getBuilding() + " 栋" + house.getUnit() + "单元" + house.getNumber() + "号");
            }
        }
        orderCollectInFoDTO.setCreateDate(splitDeliver.getCreateDate());
        orderCollectInFoDTO.setSendTime(splitDeliver.getRecTime());
        orderCollectInFoDTO.setNumber(splitDeliver.getNumber());
        orderCollectInFoDTO.setActualPaymentPrice(splitDeliver.getTotalAmount());
        orderCollectInFoDTO.setId(splitDeliver.getId());


        example = new Example(OrderSplitItem.class);
        example.createCriteria().andEqualTo(OrderSplitItem.ORDER_SPLIT_ID, splitDeliver.getId());
        List<OrderSplitItem> orderSplitItem = billDjDeliverOrderSplitItemMapper.selectByExample(example);

        Map<String, Object> map;
        List<Map<String, Object>> list = new ArrayList<>();
        if (orderSplitItem != null && orderSplitItem.size() > 0) {
            for (OrderSplitItem splitItem : orderSplitItem) {
                map = new HashMap<>();
                String str = iBillDjDeliverOrderMapper.queryValueIdArr(splitItem.getId());
                if (!CommonUtil.isEmpty(str)) {
                    String valueNameArr = billProductTemplateService.getNewValueNameArr(str);
                    map.put("valueNameArr", valueNameArr);
                } else {
                    map.put("valueNameArr", "");
                }
                map.put("id", splitItem.getId());
                map.put("productName", splitItem.getProductName());
                map.put("productId", splitItem.getProductId());
                map.put("houseId", splitItem.getHouseId());
                map.put("shopCount", splitItem.getNum());
                map.put("price", splitItem.getPrice());
                map.put("unitName", splitItem.getUnitName());
                List<String> result = Arrays.asList(splitItem.getImage().split(","));
                map.put("image", address + result.get(0));
                map.put("isDeliveryInstall", splitItem.getIsDeliveryInstall());
                list.add(map);
            }
        }

        Map<String, Object> mapArr = new HashMap<>();
        OrderSplit orderSplit = billDjDeliverOrderSplitMapper.selectByPrimaryKey(splitDeliver.getOrderSplitId());
        if (orderSplit != null) {
            Member member = queryWorker(orderSplit.getHouseId(), orderSplit.getWorkerTypeId());
            if (member != null) {
                //查询人工姓名
                mapArr.put("name", member.getName());
                mapArr.put("workerId", member.getId());
            }
        }

        mapArr.put("mobile", storefront.getMobile());//店铺电话
        mapArr.put("appointmentDTOS", list);//商品详情
//        orderCollectInFoDTO.setOrderStorefrontDTOS(mapArr);

        return ServerResponse.createBySuccess("查询成功", orderCollectInFoDTO);
    }


    /**
     * App 已完成 - 删除订单
     *
     * @param
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse deleteAppOrder(String id) {
        try {
            if (CommonUtil.isEmpty(id)) {
                return ServerResponse.createByErrorMessage("id不能为空");
            }
            SplitDeliver splitDeliver = new SplitDeliver();
            splitDeliver.setId(id);
            //1-删除订单
            splitDeliver.setDataStatus(1);
            billDjDeliverSplitDeliverMapper.updateByPrimaryKeySelective(splitDeliver);
            return ServerResponse.createBySuccessMessage("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("删除失败", e);
            return ServerResponse.createByErrorMessage("删除失败");
        }
    }

    /**
     * 我的订单,待发货
     *
     * @param pageDTO
     * @param houseId
     * @return
     */
    public ServerResponse queryDeliverOrderHump(PageDTO pageDTO, String houseId, String state, String userToken) {
        try {
            Object object = memberAPI.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            JSONObject job = (JSONObject) object;
            Member member = job.toJavaObject(Member.class);
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            String imageAddress = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            List<OrderStorefrontDTO> orderStorefrontDTOS;
            if (StringUtils.isBlank(state)) {
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            }
            if (state.equals("2") || state.equals("4")) {
                orderStorefrontDTOS = iBillDjDeliverOrderMapper.queryDeliverOrderObligation(member.getId(),houseId, state);
                orderStorefrontDTOS.forEach(orderStorefrontDTO -> {
                    List<AppointmentDTO> appointmentDTOS = iBillDjDeliverOrderMapper.queryDeliverOrderItemObligation(orderStorefrontDTO.getOrderId());
                    orderStorefrontDTO.setProductCount(appointmentDTOS.size());
                    orderStorefrontDTO.setProductImageArr(getStartTwoImage(appointmentDTOS, imageAddress));
                    orderStorefrontDTO.setStorefrontIcon(imageAddress + orderStorefrontDTO.getStorefrontIcon());
                    if (appointmentDTOS.size() > 0) {
                        AppointmentDTO appointmentDTO = appointmentDTOS.get(0);
                        orderStorefrontDTO.setProductName(appointmentDTO.getProductName());
                    }
                });
            } else {
                orderStorefrontDTOS = iBillDjDeliverOrderMapper.queryDeliverOrderHump(member.getId(),houseId);
                orderStorefrontDTOS.forEach(orderStorefrontDTO -> {
                    if("6".equals(orderStorefrontDTO.getOrderSource())){
                        Example example=new Example(Order.class);
                        Example.Criteria criteria = example.createCriteria().andEqualTo(Order.DATA_STATUS, 0)
                                .andEqualTo(Order.ORDER_STATUS, 9);
                        if(StringUtils.isNotBlank(orderStorefrontDTO.getParentOrderId())){
                            criteria.andEqualTo(Order.PARENT_ORDER_ID,orderStorefrontDTO.getParentOrderId());
                        }else{
                            criteria.andEqualTo(Order.PARENT_ORDER_ID,orderStorefrontDTO.getOrderId());
                        }
                        DjStoreActivity djStoreActivity =
                                iBillDjStoreActivityMapper.selectByPrimaryKey(orderStorefrontDTO.getStoreActivityId());
                        orderStorefrontDTO.setShortPeople(djStoreActivity.getSpellGroup()-iBillDjDeliverOrderMapper.selectCountByExample(example));
                    }
                    List<AppointmentDTO> appointmentDTOS = iBillDjDeliverOrderMapper.queryAppointmentHump(orderStorefrontDTO.getOrderId());
                    orderStorefrontDTO.setProductCount(appointmentDTOS.size());
                    orderStorefrontDTO.setProductImageArr(getStartTwoImage(appointmentDTOS, imageAddress));
                    orderStorefrontDTO.setStorefrontIcon(imageAddress + orderStorefrontDTO.getStorefrontIcon());
                    if (appointmentDTOS.size() > 0) {
                        AppointmentDTO appointmentDTO = appointmentDTOS.get(0);
                        orderStorefrontDTO.setProductName(appointmentDTO.getProductName());
                    }
                    if (orderStorefrontDTO.getStorefrontType().equals("worker")) {
                        Member worker = this.queryWorker(orderStorefrontDTO.getHouseId(), orderStorefrontDTO.getWorkerTypeId());
                        if (worker != null) {
                            orderStorefrontDTO.setWorkerId(worker.getId());
                            orderStorefrontDTO.setWorkerName(worker.getName());
                        }
                    }
                });
            }
            if (orderStorefrontDTOS.size() <= 0)
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            PageInfo pageResult = new PageInfo(orderStorefrontDTOS);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("查询失败：", e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 查询订单人工
     *
     * @param houseId
     * @param workerTypeId
     * @return
     */
    public Member queryWorker(String houseId, String workerTypeId) {
        try {
            Example example = new Example(HouseFlow.class);
            example.createCriteria().andEqualTo(HouseFlow.HOUSE_ID, houseId)
                    .andEqualTo(HouseFlow.WORKER_TYPE_ID, workerTypeId)
                    .andEqualTo(HouseFlow.DATA_STATUS, 0);
            HouseFlow houseFlow = iBillHouseFlowMapper.selectOneByExample(example);
            Member member = iBillMemberMapper.selectByPrimaryKey(houseFlow.getWorkerId());
            return member;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("查询失败：", e);
            return null;
        }
    }


    /**
     * 获取前两个商品的图片
     *
     * @return
     */
    String getStartTwoImage(List<AppointmentDTO> appointmentDTOS, String address) {
        String imageUrl = "";
        if (appointmentDTOS != null && appointmentDTOS.size() > 0) {
            for (AppointmentDTO ap : appointmentDTOS) {
                String image = ap.getImage();
                //添加图片详情地址字段
                if (StringUtils.isNotBlank(image)) {
                    String[] imgArr = image.split(",");
                    if (StringUtils.isBlank(imageUrl)) {
                        imageUrl = address + imgArr[0];
                    } else {
                        imageUrl = imageUrl + "," + address + imgArr[0];
                        break;
                    }
                }

            }
        }
        return imageUrl;
    }


    /**
     * 待收货列表-确认收货
     *
     * @param id
     * @return
     */
    public ServerResponse setConfirmReceipt(String id) {
        try {
            SplitDeliver splitDeliver = new SplitDeliver();
            splitDeliver.setId(id);
            splitDeliver.setShippingState(2);
            billDjDeliverSplitDeliverMapper.updateByPrimaryKeySelective(splitDeliver);
            Example example = new Example(OrderSplitItem.class);
            example.createCriteria().andEqualTo(OrderSplitItem.SPLIT_DELIVER_ID, id);
            List<OrderSplitItem> orderSplitItems = billDjDeliverOrderSplitItemMapper.selectByExample(example);
            orderSplitItems.forEach(orderSplitItem -> {
                orderSplitItem.setReceive(orderSplitItem.getNum());
                orderSplitItem.setModifyDate(new Date());
                billDjDeliverOrderSplitItemMapper.updateByPrimaryKeySelective(orderSplitItem);
                if(orderSplitItem.getOrderItemId()!=null){
                    OrderItem orderItem=iBillDjDeliverOrderItemMapper.selectByPrimaryKey(orderSplitItem.getOrderItemId());
                    if(orderItem!=null){
                        if(orderItem.getReturnCount()==null)
                            orderItem.setReceiveCount(0d);
                        if(orderItem.getReturnCount()<orderItem.getAskCount()){
                            orderItem.setReceiveCount(MathUtil.add(orderItem.getReceiveCount(),orderSplitItem.getReceive()));
                            orderItem.setModifyDate(new Date());
                            iBillDjDeliverOrderItemMapper.updateByPrimaryKeySelective(orderItem);
                        }
                    }
                }
            });
            return ServerResponse.createBySuccessMessage("操作成功");
        } catch (Exception e) {
            logger.error("操作失败：", e);
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }


    /**
     * 查询订单人工商品
     * orderStatus 3待收货，4已完成
     *
     * @param houseId
     * @return
     */
    public ServerResponse queryWorkerGoods(String houseId, Integer orderStatus) {
        try {

            List<WorkerGoodsInFoDTO> workerGoodsInFoDTOS = iBillDjDeliverOrderMapper.queryWorkerGoods(houseId, orderStatus);
            if (workerGoodsInFoDTOS != null && workerGoodsInFoDTOS.size() > 0) {
                String imageAddress = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
                workerGoodsInFoDTOS.forEach(a -> {
                    a.setImage(imageAddress + a.getImage());
                });
            }
            if (workerGoodsInFoDTOS.size() <= 0)
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());

            return ServerResponse.createBySuccess("查询成功", workerGoodsInFoDTOS);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("查询失败", e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 查询订单人工商品详情
     * orderStatus 3待收货，4已完成
     *
     * @param id
     * @return
     */
    public ServerResponse queryWorkerGoodsInFo(String id) {
        try {
            List<WorkerGoodsInFoDTO> workerGoodsInFoDTOS = iBillDjDeliverOrderMapper.queryWorkerGoodsInFo(id);
            List<Map<String, Object>> list = new ArrayList<>();
            Map<String, Object> map = new HashMap<>();
            if (workerGoodsInFoDTOS != null && workerGoodsInFoDTOS.size() > 0) {
                House house = iBillHouseMapper.selectByPrimaryKey(workerGoodsInFoDTOS.get(0).getHouseId());
                String houseName = house.getResidential() + house.getBuilding() + "栋" + house.getUnit() + "单元" + house.getNumber() + "号";
                map.put("houseName", houseName);
                map.put("createDate", workerGoodsInFoDTOS.get(0).getCreateDate());
                map.put("totalAmount", workerGoodsInFoDTOS.get(0).getTotalAmount());
                String imageAddress = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);

                workerGoodsInFoDTOS.forEach(a -> {
                    a.setTotalNodeNumber(iBillDjDeliverOrderMapper.queryArrNumber(a.getProductTemplateId()));//查询总节点数
                    a.setCompletedNodeNumber(iBillDjDeliverOrderMapper.queryTestNumber(a.getProductTemplateId(), a.getHouseId(), a.getWorkerId()));//已完成节点
                    a.setImage(imageAddress + a.getImage());
                });
            }
            map.put("workerGoodsDTOS", workerGoodsInFoDTOS);

            list.add(map);
            if (list.size() <= 0)
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());

            return ServerResponse.createBySuccess("查询成功", list);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("查询失败", e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 完工后-花费明细
     *
     * @param houseId
     * @return
     */
    public ServerResponse queryCostDetailsAfterCompletion(String houseId) {
        try {
            List<DecorationCostDTO> decorationCostDTOS = iBillDjDeliverOrderMapper.queryCostDetailsAfterCompletion(houseId);
            Map<String, List<DecorationCostDTO>> collect = decorationCostDTOS.stream()
                    .collect(Collectors.groupingBy(DecorationCostDTO::getWorkerTypeId));
            return ServerResponse.createBySuccess("查询成功", collect);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("查询失败", e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }
}
