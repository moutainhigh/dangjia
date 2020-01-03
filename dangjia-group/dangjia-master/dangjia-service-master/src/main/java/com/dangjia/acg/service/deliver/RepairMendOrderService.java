package com.dangjia.acg.service.deliver;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.enums.AppType;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.MathUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.product.ProductWorkerDTO;
import com.dangjia.acg.dto.refund.RefundOrderItemDTO;
import com.dangjia.acg.dto.refund.RefundRepairOrderDTO;
import com.dangjia.acg.mapper.core.IHouseFlowMapper;
import com.dangjia.acg.mapper.delivery.*;
import com.dangjia.acg.mapper.design.IQuantityRoomMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.house.IWarehouseDetailMapper;
import com.dangjia.acg.mapper.house.IWarehouseMapper;
import com.dangjia.acg.mapper.member.IMasterMemberAddressMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.pay.IBusinessOrderMapper;
import com.dangjia.acg.mapper.product.IMasterStorefrontProductMapper;
import com.dangjia.acg.mapper.repair.IMendMaterialMapper;
import com.dangjia.acg.mapper.repair.IMendOrderMapper;
import com.dangjia.acg.mapper.repair.IMendWorkerMapper;
import com.dangjia.acg.mapper.worker.IWorkerDetailMapper;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.deliver.*;
import com.dangjia.acg.modle.design.QuantityRoom;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.house.Warehouse;
import com.dangjia.acg.modle.house.WarehouseDetail;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.member.MemberAddress;
import com.dangjia.acg.modle.order.DeliverOrderAddedProduct;
import com.dangjia.acg.modle.order.OrderProgress;
import com.dangjia.acg.modle.pay.BusinessOrder;
import com.dangjia.acg.modle.repair.MendMateriel;
import com.dangjia.acg.modle.repair.MendOrder;
import com.dangjia.acg.modle.repair.MendWorker;
import com.dangjia.acg.modle.storefront.StorefrontProduct;
import com.dangjia.acg.modle.worker.WorkerDetail;
import com.dangjia.acg.service.config.ConfigMessageService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 退款记录
 */
@Service
public class RepairMendOrderService {

    protected static final Logger logger = LoggerFactory.getLogger(RepairMendOrderService.class);
    @Autowired
    private IMendOrderMapper iMendOrderMapper;
    @Autowired
    private IQuantityRoomMapper iQuantityRoomMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IHouseMapper iHouseMapper;
    @Autowired
    private IOrderItemMapper iOrderItemMapper;
    @Autowired
    private IMendMaterialMapper iMendMaterialMapper;
    @Autowired
    private IMendWorkerMapper iMendWorkerMapper;
    @Autowired
    private IOrderMapper iOrderMapper;
    @Autowired
    private ConfigMessageService configMessageService;
    @Autowired
    private IMasterStorefrontProductMapper iMasterStorefrontProductMapper;
    @Autowired
    private IMasterDeliverOrderAddedProductMapper masterDeliverOrderAddedProductMapper;
    @Autowired
    private IWarehouseDetailMapper iWarehouseDetailMapper;
    @Autowired
    private IWarehouseMapper iWarehouseMapper;
    @Autowired
    private IMemberMapper iMemberMapper;
    @Autowired
    private IWorkerDetailMapper iWorkerDetailMapper;
    @Autowired
    private IMasterOrderProgressMapper iMasterOrderProgressMapper;
    @Autowired
    private IHouseFlowMapper iHouseFlowMapper;
    @Autowired
    private IOrderSplitItemMapper iOrderSplitItemMapper;
    @Autowired
    private IBusinessOrderMapper iBusinessOrderMapper;
    @Autowired
    private ICartMapper iCartMapper;
    @Autowired
    private IMasterMemberAddressMapper iMasterMemberAddressMapper;

    //生成退款的流水记录(直接退款的）
    public String saveRefundInfoRecord(String cityId,String houseId,String orderId,String orderProductAttr,BigDecimal roomCharge){

        String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        //查询房子信息，获取房子对应的楼层
        QuantityRoom quantityRoom=iQuantityRoomMapper.getQuantityRoom(houseId,0);
        Integer elevator= 1;//是否电梯房
        String floor="1";
        if(quantityRoom!=null&& StringUtils.isNotBlank(quantityRoom.getId())){
            elevator=quantityRoom.getElevator();//是否电梯房
            floor=quantityRoom.getFloor();//楼层
        }
        House house=iHouseMapper.selectByPrimaryKey(houseId);
        String memberId=house.getMemberId();
        MendOrder mendOrder = new MendOrder();;
        Example example;
        if(orderId!=null&&StringUtils.isNotBlank(orderId)) {
                Order order=iOrderMapper.selectByPrimaryKey(orderId);
                String storefrontId = order.getStorefontId();
                example = new Example(MendOrder.class);
                mendOrder.setNumber("DJZX" + 40000 + iMendOrderMapper.selectCountByExample(example));//订单号
                mendOrder.setHouseId(houseId);
                mendOrder.setApplyMemberId(memberId);
                mendOrder.setType(6);//系统自动退款
                mendOrder.setOrderName("系统自动退款");
                if(order.getOrderSource()==4){
                    mendOrder.setOrderName("系统自动退款，退差价订单");
                }
                mendOrder.setState(0);//生成中
                mendOrder.setStorefrontId(storefrontId);
                mendOrder.setOrderId(orderId);
                Double totalRransportationCost = 0.0;//可退运费
                Double totalStevedorageCost = 0.0;//可退搬运费
                Double actualTotalAmount=0.0;//退货总额
                //获取商品信息
                JSONArray orderItemProductList = JSONArray.parseArray(orderProductAttr);
                for (int j = 0; j < orderItemProductList.size(); j++) {
                    JSONObject productObj = (JSONObject) orderItemProductList.get(j);
                    String orderItemId=(String)productObj.get("orderItemId");//订单详情号
                    String productId=(String)productObj.get("productId");//产品ID
                    Double returnCount=productObj.getDouble("returnCount");//退货量
                    String addedProductIds=(String)productObj.get("addedProductIds");//增值商品ID，多个用逗号分隔
                    //修改订单中的退货量为当前退货的量
                    OrderItem orderItem=iOrderItemMapper.selectByPrimaryKey(orderItemId);
                    if(orderItem.getReturnCount()==null){
                        orderItem.setReturnCount(Double.valueOf(0L));
                    }
                    if(MathUtil.sub(MathUtil.sub(orderItem.getShopCount(),orderItem.getReturnCount()),returnCount)<0){
                         returnCount=MathUtil.sub(orderItem.getShopCount(),orderItem.getReturnCount());
                    }
                    orderItem.setReturnCount(MathUtil.add(orderItem.getReturnCount(),returnCount));
                    iOrderItemMapper.updateByPrimaryKeySelective(orderItem);
                    Double price = orderItem.getPrice();//购买单价
                    Double shopCount=orderItem.getShopCount();//购买数据
                    Double transportationCost=orderItem.getTransportationCost();//运费
                    Double stevedorageCost=orderItem.getStevedorageCost();//搬运费

                    Double returnRransportationCost=0.0;
                    //计算可退运费
                    if(transportationCost>0.0) {
                        returnRransportationCost = CommonUtil.getReturnRransportationCost(price, shopCount, returnCount,transportationCost);
                        totalRransportationCost=MathUtil.add(totalRransportationCost,returnRransportationCost);
                    }
                    //计算可退搬费
                    Double returnStevedorageCost=0.0;
                    if(stevedorageCost>0.0){
                        StorefrontProduct storefrontProduct=iMasterStorefrontProductMapper.selectByPrimaryKey(productId);
                        String isUpstairsCost=storefrontProduct.getIsUpstairsCost();//是否按1层收取上楼费
                        Double moveCost=storefrontProduct.getMoveCost().doubleValue();//每层搬费
                        returnStevedorageCost= CommonUtil.getReturnStevedorageCost(elevator,floor,isUpstairsCost,moveCost,returnCount);
                        totalStevedorageCost=MathUtil.add(totalStevedorageCost,returnStevedorageCost);
                    }
                    orderItem.setStorefontId(order.getStorefontId());
                    //添回退款申请明细信息
                    MendMateriel mendMateriel=saveMasterMendMaterial(mendOrder,cityId,orderItem,returnStevedorageCost,returnRransportationCost,productId,returnCount);
                    setAddedProduct(mendMateriel.getId(), addedProductIds, "3");
                    //获取增值商品信息
                    Example example1=new Example(DeliverOrderAddedProduct.class);
                    example1.createCriteria().andEqualTo(DeliverOrderAddedProduct.ANY_ORDER_ID,mendMateriel.getId())
                            .andEqualTo(DeliverOrderAddedProduct.SOURCE,3);
                    List<DeliverOrderAddedProduct> deliverOrderAddedProducts = masterDeliverOrderAddedProductMapper.selectByExample(example1);
                    for (DeliverOrderAddedProduct deliverOrderAddedProduct : deliverOrderAddedProducts) {
                        Double totalPrice =MathUtil.mul(deliverOrderAddedProduct.getPrice(),returnCount);
                        actualTotalAmount = MathUtil.add(actualTotalAmount,totalPrice);
                    }
                    //增加增值商品数据
                    actualTotalAmount=MathUtil.add(actualTotalAmount,MathUtil.mul(price,returnCount));

                }
                mendOrder.setModifyDate(new Date());
                mendOrder.setState(1);
                mendOrder.setCarriage(totalRransportationCost);//运费
                mendOrder.setTotalStevedorageCost(totalStevedorageCost);//搬运费
                mendOrder.setActualTotalAmount(actualTotalAmount);//退货总额

                mendOrder.setTotalAmount(MathUtil.add(MathUtil.add(actualTotalAmount,totalRransportationCost),totalStevedorageCost));//实退款，含运费
                if(order.getOrderSource()==1){
                    //增加退款单流水(退原订单)
                   // updateOrderProgressInfo(mendOrder.getId(),"2","REFUND_AFTER_SALES","RA_001",memberId);//您的退款申请已提交
                    //减除扣减的量房费用
                    mendOrder.setTotalAmount(MathUtil.sub(mendOrder.getTotalAmount(),roomCharge.doubleValue()));//扣减量房后的费用
                    mendOrder.setRoomCharge(roomCharge.doubleValue());//量房收费
                }
               //添加对应的申请退货单信息
                iMendOrderMapper.insert(mendOrder);

                agreeRepairApplication(mendOrder.getId(),order.getOrderSource(),memberId);//自动同意退款申诉
        }
        return  mendOrder.getId();
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
    /**
     * 添加退款信息
     * @param mendOrder
     * @param cityId
     * @param productId
     * @param returnCount
     * @return
     */
    public MendMateriel saveMasterMendMaterial(MendOrder mendOrder, String cityId,OrderItem orderItem,Double stevedorageCost,Double transportationCost, String productId, Double returnCount) {
        MendMateriel mendMateriel = new MendMateriel();//退材料明细
        mendMateriel.setCityId(cityId);
        mendMateriel.setProductSn(orderItem.getProductSn());
        mendMateriel.setProductName(orderItem.getProductName());
        mendMateriel.setPrice(orderItem.getPrice());
        mendMateriel.setCost(orderItem.getCost());
        mendMateriel.setUnitName(orderItem.getUnitName());
        mendMateriel.setTotalPrice(MathUtil.mul(returnCount.doubleValue() ,orderItem.getPrice()));
        mendMateriel.setProductType(orderItem.getProductType());//0：材料；1：包工包料
        mendMateriel.setCategoryId(orderItem.getCategoryId());
        mendMateriel.setImage(orderItem.getImage());
        mendMateriel.setStorefrontId(orderItem.getStorefontId());
        mendMateriel.setOrderItemId(orderItem.getId());
        mendMateriel.setShopCount(returnCount.doubleValue());
        mendMateriel.setStevedorageCost(stevedorageCost);
        mendMateriel.setTransportationCost(transportationCost);
        mendMateriel.setMendOrderId(mendOrder.getId());
        mendMateriel.setProductId(productId);
        mendMateriel.setCategoryId(orderItem.getCategoryId());
        mendMateriel.setActualCount(mendMateriel.getShopCount());
        mendMateriel.setActualPrice(mendMateriel.getTotalPrice());
        iMendMaterialMapper.insertSelective(mendMateriel);
        return mendMateriel;
    }
    /**
     *  更新/设置增值商品
     * @param orderId 来源订单ID
     * @param addedProductIds 增值商品 多个以逗号分隔
     * @param source 来源类型
     */
    private void setAddedProduct(String orderId,String addedProductIds,String source){
        if(!CommonUtil.isEmpty(orderId)) {
            Example example=new Example(DeliverOrderAddedProduct.class);
            example.createCriteria().andEqualTo(DeliverOrderAddedProduct.ANY_ORDER_ID,orderId).andEqualTo(DeliverOrderAddedProduct.SOURCE,source);
            masterDeliverOrderAddedProductMapper.deleteByExample(example);
            if(!CommonUtil.isEmpty(addedProductIds)) {
                String[] addedProductIdList=addedProductIds.split(",");
                for (String addedProductId : addedProductIdList) {
                    StorefrontProduct product = iMasterStorefrontProductMapper.selectByPrimaryKey(addedProductId);
                    DeliverOrderAddedProduct deliverOrderAddedProduct1 = new DeliverOrderAddedProduct();
                    deliverOrderAddedProduct1.setAnyOrderId(orderId);
                    deliverOrderAddedProduct1.setAddedProductId(addedProductId);
                    deliverOrderAddedProduct1.setPrice(product.getSellPrice());
                    deliverOrderAddedProduct1.setProductName(product.getProductName());
                    deliverOrderAddedProduct1.setSource(source);
                    masterDeliverOrderAddedProductMapper.insert(deliverOrderAddedProduct1);
                }
            }
        }
    }

    /**
     * 同意退款申诉（退货申请）
     * @param repairMendOrderId
     */
    public ServerResponse agreeRepairApplication(String repairMendOrderId,Integer orderSource,String memberId){
        MendOrder mendOrder=iMendOrderMapper.selectByPrimaryKey(repairMendOrderId);//退款订单详情查询
        //修改退款申诉的状态
        mendOrder.setId(repairMendOrderId);
        mendOrder.setState(3);
        mendOrder.setModifyDate(new Date());
        iMendOrderMapper.updateByPrimaryKeySelective(mendOrder);//修改退款申请单的状态同意
        //增加退款流水记录
        /*if(orderSource==1){
            //增加退款单流水(退原订单)
            updateOrderProgressInfo(mendOrder.getId(),"2","REFUND_AFTER_SALES","RA_006",memberId);//平台同意退款
            updateOrderProgressInfo(mendOrder.getId(),"2","REFUND_AFTER_SALES","RA_008",memberId);//平台同意退款
        }*/

        settleMendOrder(repairMendOrderId,orderSource);//退钱给业主
        return ServerResponse.createBySuccess("操作成功");
    }

    /**
     * 系统自动退款退钱
     * @param repairMendOrderId
     * @return
     */
    public ServerResponse settleMendOrder(String repairMendOrderId,Integer orderSource){
        MendOrder mendOrder=iMendOrderMapper.selectByPrimaryKey(repairMendOrderId);//查询对应的退货申请单信息
        if(mendOrder.getType()==6) {//系统自动退款
            /*审核通过修改仓库数量,记录流水*/
            Example example=new Example(MendMateriel.class);
            example.createCriteria().andEqualTo(MendMateriel.MEND_ORDER_ID,repairMendOrderId);
            List<MendMateriel> repairMaterialList = iMendMaterialMapper.selectByExample(example);//退款商品列表查询
            if (repairMaterialList != null && repairMaterialList.size() > 0) {
                for (MendMateriel mendMateriel : repairMaterialList) {
                    example = new Example(Warehouse.class);
                    example.createCriteria()
                            .andEqualTo(Warehouse.PRODUCT_ID, mendMateriel.getProductId())
                            .andEqualTo(Warehouse.HOUSE_ID, mendOrder.getHouseId());
                    Warehouse warehouse = iWarehouseMapper.selectOneByExample(example);
                    if(warehouse!=null&&StringUtils.isNotBlank(warehouse.getId())){
                        warehouse.setBackCount(warehouse.getBackCount() + mendMateriel.getShopCount());//更新退数量
                        warehouse.setBackTime(warehouse.getBackTime() + 1);//更新退次数
                        warehouse.setOwnerBack(warehouse.getOwnerBack() == null ? mendMateriel.getShopCount() : (warehouse.getOwnerBack() + mendMateriel.getShopCount())); //购买数量+业主退数量
                        iWarehouseMapper.updateByPrimaryKeySelective(warehouse);
                    }

                }

                WarehouseDetail warehouseDetail = new WarehouseDetail();
                warehouseDetail.setHouseId(mendOrder.getHouseId());
                warehouseDetail.setRelationId(mendOrder.getId());
                warehouseDetail.setRecordType(4);//业主退
                iWarehouseDetailMapper.insert(warehouseDetail);
                /*退钱给业主*/
                Member member = iMemberMapper.selectByPrimaryKey(iHouseMapper.selectByPrimaryKey(mendOrder.getHouseId()).getMemberId());
                BigDecimal haveMoney = member.getHaveMoney().add(new BigDecimal(mendOrder.getTotalAmount()));
                BigDecimal surplusMoney = member.getSurplusMoney().add(new BigDecimal(mendOrder.getTotalAmount()));
                //记录流水
                WorkerDetail workerDetail = new WorkerDetail();
                if(orderSource==4){
                    workerDetail.setName("系统自动退款,退差价订单");
                }else{
                    workerDetail.setName("系统自动退款,退原订单");
                }
                workerDetail.setWorkerId(member.getId());
                workerDetail.setWorkerName(CommonUtil.isEmpty(member.getName()) ? member.getNickName() : member.getName());
                workerDetail.setHouseId(mendOrder.getHouseId());
                workerDetail.setMoney(new BigDecimal(mendOrder.getTotalAmount()));
                workerDetail.setApplyMoney(new BigDecimal(mendOrder.getTotalAmount()));
                workerDetail.setWalletMoney(surplusMoney);
                workerDetail.setState(6);//进钱//业主退
                iWorkerDetailMapper.insert(workerDetail);

                member.setHaveMoney(haveMoney);
                member.setSurplusMoney(surplusMoney);
                iMemberMapper.updateByPrimaryKeySelective(member);

                mendOrder.setState(4);
                iMendOrderMapper.updateByPrimaryKeySelective(mendOrder);
                if(orderSource==4){
                    //推送消息给业主退货退款通知
                    configMessageService.addConfigMessage( AppType.ZHUANGXIU, member.getId(),
                            "0", "有退款到账啦", String.format(DjConstants.PushMessage.YEZHUTUIHUO),7, "量房后发现，支付面积大于施工面积");

                }
            }
        }
        return ServerResponse.createBySuccessMessage("流程全部通过");
    }

    /**
     * 工匠加钱(设计师加上量房的钱）
     * @param houseId 房子ID
     * @param roomCharge 量房钱
     * @return
     */
    public ServerResponse settleMemberMoney(String houseId,BigDecimal roomCharge){

           //查询设计师的信息
            HouseFlow houseFlow=iHouseFlowMapper.getByWorkerTypeId(houseId,"1");
            if(houseFlow==null||StringUtils.isBlank(houseFlow.getId())){
                logger.info("未找到对应的工匠信息:"+houseId);
                return ServerResponse.createByErrorMessage("未找到对应的工匠信息");
            }
            /*退钱给工匠,设计师*/
            Member member = iMemberMapper.selectByPrimaryKey(houseFlow.getWorkerId());
            BigDecimal haveMoney = member.getHaveMoney().add(roomCharge);
            BigDecimal surplusMoney = member.getSurplusMoney().add(roomCharge);
            //记录流水
            WorkerDetail workerDetail = new WorkerDetail();
            workerDetail.setName("量房费用");
            workerDetail.setWorkerId(member.getId());
            workerDetail.setWorkerName(CommonUtil.isEmpty(member.getName()) ? member.getNickName() : member.getName());
            workerDetail.setHouseId(houseId);
            workerDetail.setMoney(roomCharge);
            workerDetail.setApplyMoney(roomCharge);
            workerDetail.setWalletMoney(surplusMoney);
            workerDetail.setState(2);//进钱//业主退
            iWorkerDetailMapper.insert(workerDetail);

            member.setHaveMoney(haveMoney);
            member.setSurplusMoney(surplusMoney);
            iMemberMapper.updateByPrimaryKeySelective(member);

        return ServerResponse.createBySuccessMessage("流程全部通过");
    }


    /**
     * 工匠申请退货退款提交
     * @param worker
     * @param cityId
     * @param houseId
     * @param orderProductAttr
     * @return
     */
    public String workerApplyReturnMaterial(Member worker,String cityId,String houseId,String orderProductAttr){

        String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        //查询房子信息，获取房子对应的楼层
        QuantityRoom quantityRoom=iQuantityRoomMapper.getQuantityRoom(houseId,0);
        Integer elevator= 1;//是否电梯房
        String floor="1";
        if(quantityRoom!=null&&StringUtils.isNotBlank(quantityRoom.getId())){
            elevator=quantityRoom.getElevator();//是否电梯房
            floor=quantityRoom.getFloor();//楼层
        }
        MendOrder mendOrder;
        Example example;
        example = new Example(MendOrder.class);
        mendOrder = new MendOrder();
        mendOrder.setNumber("DJZX" + 40000 + iMendOrderMapper.selectCountByExample(example));//订单号
        mendOrder.setHouseId(houseId);
        mendOrder.setApplyMemberId(worker.getId());
        mendOrder.setType(2);//工匠退材料
        mendOrder.setOrderName("工匠退材料申请");
        mendOrder.setState(0);//生成中
        Double totalRransportationCost = 0.0;//可退运费
        Double totalStevedorageCost = 0.0;//可退搬运费
        Double actualTotalAmount=0.0;//退货总额
        //获取商品信息
        JSONArray orderItemProductList=JSONObject.parseArray(orderProductAttr);
        for (int j = 0; j < orderItemProductList.size(); j++) {
            JSONObject productObj = (JSONObject) orderItemProductList.get(j);
            String orderSplitItemId=(String)productObj.get("orderSplitItemId");//要货单详情Id
            String productId=(String)productObj.get("productId");//产品ID
            Double returnCount=productObj.getDouble("returnCount");//退货量
            logger.info("退货量{}",returnCount);
            OrderSplitItem orderSplitItem=iOrderSplitItemMapper.selectByPrimaryKey(orderSplitItemId);
           // RefundOrderItemDTO refundOrderItemDTO=iOrderSplitItemMapper.queryReturnRefundOrderItemInfo(orderSplitItemId);
            if(orderSplitItem==null){
                continue;
            }
            if(orderSplitItem.getReturnCount()==null){
                orderSplitItem.setReturnCount(Double.valueOf(0L));
            }
            if(MathUtil.sub(MathUtil.sub(orderSplitItem.getReceive(),orderSplitItem.getReturnCount()),returnCount)<0){
                returnCount=MathUtil.sub(orderSplitItem.getReceive(),orderSplitItem.getReturnCount());
            }
           // setProductInfo(refundOrderItemDTO,address);
            logger.info("可货量{}",returnCount);
            //orderSplitItem.setReturnCount(returnCount);
            //修改要货订单中的退货量为最新的退货量
            //OrderSplitItem orderSplitItem=billDjDeliverOrderSplitItemMapper.selectByPrimaryKey(refundOrderItemDTO.getOrderSplitItemId());
            //orderSplitItem.setId(refundOrderItemDTO.getOrderSplitItemId());
            Double price = orderSplitItem.getPrice();//购买单价
            Double shopCount=orderSplitItem.getShopCount();//购买数据
            OrderItem orderItem=iOrderItemMapper.selectByPrimaryKey(orderSplitItem.getOrderItemId());//原订单信息
            Double transportationCost=orderItem.getTransportationCost();//运费
            Double stevedorageCost=orderItem.getStevedorageCost();//搬运费
            //计算可退运费
            if(transportationCost>0.0) {
                Double returnRransportationCost = CommonUtil.getReturnRransportationCost(price, shopCount, returnCount,transportationCost);
                totalRransportationCost=MathUtil.add(totalRransportationCost,returnRransportationCost);
                //refundOrderItemDTO.setTransportationCost(returnRransportationCost);
            }
            //计算可退搬费
            if(stevedorageCost>0.0){
                StorefrontProduct storefrontProduct=iMasterStorefrontProductMapper.selectByPrimaryKey(productId);
                String isUpstairsCost=storefrontProduct.getIsUpstairsCost();//是否按1层收取上楼费
                Double moveCost=storefrontProduct.getMoveCost().doubleValue();//每层搬运费
                Double returnStevedorageCost=CommonUtil.getReturnStevedorageCost(elevator,floor,isUpstairsCost,moveCost,returnCount);
                totalStevedorageCost=MathUtil.add(totalStevedorageCost,returnStevedorageCost);
                //refundOrderItemDTO.setStevedorageCost(totalStevedorageCost);
            }
           // refundOrderItemDTO.setOrderItemId(refundOrderItemDTO.getOrderSplitItemId());//退货单详情ID
            //添回退款申请明细信息
            MendMateriel mendMateriel = saveMasterMendSplitMaterial(mendOrder,  cityId, orderSplitItem, stevedorageCost, transportationCost, productId, returnCount) ;
            actualTotalAmount=MathUtil.add(actualTotalAmount,MathUtil.mul(price,returnCount));

        }
        mendOrder.setModifyDate(new Date());
        mendOrder.setState(0);//状态为生成中，需要业主审核后，重新生成单
        mendOrder.setCarriage(totalRransportationCost);//运费
        mendOrder.setTotalStevedorageCost(totalStevedorageCost);//搬运费
        mendOrder.setActualTotalAmount(actualTotalAmount);//退货总额
        mendOrder.setTotalAmount(MathUtil.sub(MathUtil.sub(actualTotalAmount,totalRransportationCost),totalStevedorageCost));//实退款，含运费(去掉运费搬运费后的可得钱)
        //添加对应的申请退货单信息
        iMendOrderMapper.insert(mendOrder);
        return  mendOrder.getId();
    }

    /**
     * 添加退货退款信息
     * @param mendOrder
     * @param cityId
     * @param productId
     * @param returnCount
     * @return
     */
    public MendMateriel saveMasterMendSplitMaterial(MendOrder mendOrder, String cityId,OrderSplitItem orderSplitItem,Double stevedorageCost,Double transportationCost, String productId, Double returnCount) {
        MendMateriel mendMateriel = new MendMateriel();//退材料明细
        mendMateriel.setCityId(cityId);
        mendMateriel.setProductSn(orderSplitItem.getProductSn());
        mendMateriel.setProductName(orderSplitItem.getProductName());
        mendMateriel.setPrice(orderSplitItem.getPrice());
        mendMateriel.setCost(orderSplitItem.getCost());
        mendMateriel.setUnitName(orderSplitItem.getUnitName());
        mendMateriel.setTotalPrice(MathUtil.mul(returnCount.doubleValue() ,orderSplitItem.getPrice()));
        mendMateriel.setProductType(orderSplitItem.getProductType());//0：材料；1：包工包料
        mendMateriel.setCategoryId(orderSplitItem.getCategoryId());
        mendMateriel.setImage(orderSplitItem.getImage());
        mendMateriel.setOrderItemId(orderSplitItem.getId());
        mendMateriel.setShopCount(returnCount.doubleValue());
        mendMateriel.setStevedorageCost(stevedorageCost);
        mendMateriel.setTransportationCost(transportationCost);
        mendMateriel.setMendOrderId(mendOrder.getId());
        mendMateriel.setProductId(productId);
        mendMateriel.setActualCount(mendMateriel.getShopCount());
        mendMateriel.setActualPrice(mendMateriel.getTotalPrice());
        iMendMaterialMapper.insertSelective(mendMateriel);
        return mendMateriel;
    }


    /**
     * 生成补人工订单
     * @param houseId  房子ID
     * @param workerId 工匠ID
     * @param workerTypeId 工种ID
     * @param changeOrderId 变更申请单ID
     * @return
     */
    public MendOrder insertSupplementLaborOrder(String houseId,String workerId,String workerTypeId,String changeOrderId){
        /**
         * 查询选中的人工商品
         */
        House house=iHouseMapper.selectByPrimaryKey(houseId);
        Example example=new Example(MemberAddress.class);
        example.createCriteria().andEqualTo(MemberAddress.HOUSE_ID,houseId);
        MemberAddress memberAddress=iMasterMemberAddressMapper.selectOneByExample(example);
        String addressId="";
        if(memberAddress!=null&&StringUtils.isNotBlank(memberAddress.getId())){
            addressId=memberAddress.getId();
        }
        String memberId=house.getId();//业主信息

        example=new Example(Cart.class);
        example.createCriteria().andEqualTo(Cart.HOUSE_ID,houseId)
                .andEqualTo(Cart.MEMBER_ID,workerId);
        List<Cart> cartList=iCartMapper.selectByExample(example);
        if(cartList!=null&&cartList.size()>0){
            Cart cart=cartList.get(0);
            StorefrontProduct storefrontProduct=iMasterStorefrontProductMapper.selectByPrimaryKey(cart.getProductId());//获取商品信息
            //生成补货单
            MendOrder mendOrder;
            example = new Example(MendOrder.class);
            mendOrder = new MendOrder();
            mendOrder.setChangeOrderId(changeOrderId);
            mendOrder.setNumber("DJZX" + 20000 + iMendOrderMapper.selectCountByExample(example));//订单号
            mendOrder.setHouseId(houseId);
            mendOrder.setApplyMemberId(workerId);
            mendOrder.setType(1);//补人工
            mendOrder.setOrderName("工匠补人工");
            mendOrder.setWorkerTypeId(workerTypeId);
            mendOrder.setState(0);
            mendOrder.setStorefrontId(storefrontProduct.getStorefrontId());
            mendOrder.setAddressId(addressId);
            Double totalAmount=0d;
            //补货单明细
            for(Cart ct:cartList){
                storefrontProduct=iMasterStorefrontProductMapper.selectByPrimaryKey(ct.getProductId());//获取商品信息
                MendWorker mendWorker = new MendWorker();//补退人工
                mendWorker.setMendOrderId(mendOrder.getId());
                mendWorker.setWorkerGoodsId(ct.getProductId());
                mendWorker.setWorkerGoodsName(ct.getProductName());
                mendWorker.setWorkerGoodsSn(ct.getProductSn());
                mendWorker.setUnitName(ct.getUnitName());
                mendWorker.setPrice(ct.getPrice());
                mendWorker.setImage(storefrontProduct.getImage());
                mendWorker.setShopCount(ct.getShopCount());
                Double totalPrice=MathUtil.mul(ct.getPrice(),ct.getShopCount());
                totalAmount=MathUtil.add(totalAmount,totalPrice);
                mendWorker.setTotalPrice(totalPrice);
                iMendWorkerMapper.insertSelective(mendWorker);
            }
            mendOrder.setTotalAmount(totalAmount);
            iMendOrderMapper.insert(mendOrder);//添加补人工单


            //删除已生成补货单的购物车
            example=new Example(Cart.class);
            example.createCriteria().andEqualTo(Cart.HOUSE_ID,houseId)
                    .andEqualTo(Cart.MEMBER_ID,workerId);
            iCartMapper.deleteByExample(example);
            return mendOrder;//申请补货单号
        }
        return null;
    }

    /**
     * 生成退人工订单
     */
    public MendOrder insertRefundOrder(String houseId,String memberId,String workerTypeId,String changeOrderId,String productArr){

        JSONArray orderItemProductList= JSONArray.parseArray(productArr);
        Example example;
        if(orderItemProductList!=null&&orderItemProductList.size()>0) {
            example=new Example(MemberAddress.class);
            example.createCriteria().andEqualTo(MemberAddress.HOUSE_ID,houseId);
            MemberAddress memberAddress=iMasterMemberAddressMapper.selectOneByExample(example);
            String addressId="";
            if(memberAddress!=null&&StringUtils.isNotBlank(memberAddress.getId())){
                addressId=memberAddress.getId();
            }
            //生成退货单（退人工）
            MendOrder mendOrder;
            example = new Example(MendOrder.class);
            mendOrder = new MendOrder();
            mendOrder.setChangeOrderId(changeOrderId);
            mendOrder.setNumber("DJZX" + 20000 + iMendOrderMapper.selectCountByExample(example));//订单号
            mendOrder.setHouseId(houseId);
            mendOrder.setApplyMemberId(memberId);
            mendOrder.setType(3);//退人工
            mendOrder.setOrderName("业主退人工");
            mendOrder.setWorkerTypeId(workerTypeId);
            mendOrder.setState(0);
            Double totalAmount=0d;
            String storefrontId="";
            for (int i = 0; i < orderItemProductList.size(); i++) {
                JSONObject productObj = (JSONObject) orderItemProductList.get(i);
                String orderItemId = (String) productObj.get("orderItemId");//订单详情号
                Double returnCount = productObj.getDouble("returnCount");//退货量
                OrderItem orderItem=iOrderItemMapper.selectByPrimaryKey(orderItemId);
                if(orderItem.getReturnCount()==null){
                    orderItem.setReturnCount(0d);
                }
                if(MathUtil.sub(MathUtil.sub(orderItem.getShopCount(),orderItem.getReturnCount()),returnCount)<0){
                    returnCount=MathUtil.sub(orderItem.getShopCount(),orderItem.getReturnCount());
                }
                MendWorker mendWorker = new MendWorker();//补退人工
                mendWorker.setMendOrderId(mendOrder.getId());
                mendWorker.setWorkerGoodsId(orderItem.getProductId());
                mendWorker.setWorkerGoodsName(orderItem.getProductName());
                mendWorker.setWorkerGoodsSn(orderItem.getProductSn());
                mendWorker.setUnitName(orderItem.getUnitName());
                mendWorker.setPrice(orderItem.getPrice());
                mendWorker.setImage(orderItem.getImage());
                mendWorker.setShopCount(returnCount);
                Double totalPrice= MathUtil.mul(orderItem.getPrice(),returnCount);
                totalAmount=MathUtil.add(totalAmount,totalPrice);//汇总总退款金额
                if(i==0){
                    Order order=iOrderMapper.selectByPrimaryKey(orderItem.getOrderId());
                    storefrontId=order.getStorefontId();
                }
                mendWorker.setTotalPrice(totalPrice);
                mendWorker.setOrderItemId(orderItem.getId());
                iMendWorkerMapper.insertSelective(mendWorker);
                //修改原订单中的退人工数据
                orderItem.setReturnCount(MathUtil.add(orderItem.getReturnCount(),returnCount));//添加退款量
                orderItem.setModifyDate(new Date());
                iOrderItemMapper.updateByPrimaryKeySelective(orderItem);

            }
            mendOrder.setStorefrontId(storefrontId);
            mendOrder.setAddressId(addressId);
            mendOrder.setTotalAmount(totalAmount);
            iMendOrderMapper.insert(mendOrder);//添加退人工单
            return mendOrder;
        }
        return null;

    }


    /**
     * 业主补人工，不通过，生成退人工单，并退钱给业主
     * @return
     */
    public MendOrder refundWorkerInfo(Order order){
        //生成退货单（退人工）
        MendOrder mendOrder;
        Example example = new Example(MendOrder.class);
        mendOrder = new MendOrder();
        //mendOrder.setChangeOrderId(changeOrderId);
        mendOrder.setNumber("DJZX" + 20000 + iMendOrderMapper.selectCountByExample(example));//订单号
        mendOrder.setHouseId(order.getHouseId());
        mendOrder.setApplyMemberId(order.getMemberId());
        mendOrder.setType(6);//工匠不同意，系统自动退货退款
        mendOrder.setOrderName("系统自动退人工退款");
        mendOrder.setWorkerTypeId(order.getWorkerTypeId());
        mendOrder.setState(4);
        mendOrder.setStorefrontId(order.getStorefontId());
        mendOrder.setAddressId(order.getAddressId());
        mendOrder.setTotalAmount(order.getTotalAmount().doubleValue());
        mendOrder.setOrderId(order.getId());
        iMendOrderMapper.insert(mendOrder);//添加系统退人工单

        example =new Example(OrderItem.class);
        example.createCriteria().andEqualTo(OrderItem.ORDER_ID,order.getId());
        List<OrderItem> orderItemList=iOrderItemMapper.selectByExample(example);
        if(orderItemList!=null&&orderItemList.size()>0){

            for(OrderItem orderItem:orderItemList){
                MendWorker mendWorker = new MendWorker();//补退人工
                mendWorker.setMendOrderId(mendOrder.getId());
                mendWorker.setWorkerGoodsId(orderItem.getProductId());
                mendWorker.setWorkerGoodsName(orderItem.getProductName());
                mendWorker.setWorkerGoodsSn(orderItem.getProductSn());
                mendWorker.setUnitName(orderItem.getUnitName());
                mendWorker.setPrice(orderItem.getPrice());
                mendWorker.setImage(orderItem.getImage());
                mendWorker.setShopCount(orderItem.getShopCount());
                mendWorker.setTotalPrice(orderItem.getTotalPrice());
                mendWorker.setOrderItemId(orderItem.getId());
                iMendWorkerMapper.insertSelective(mendWorker);
            }
        }
        return mendOrder;
    }

}
