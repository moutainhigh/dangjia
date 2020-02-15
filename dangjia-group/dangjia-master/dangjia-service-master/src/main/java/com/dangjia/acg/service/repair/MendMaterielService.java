package com.dangjia.acg.service.repair;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.BasicsStorefrontAPI;
import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.api.data.ForMasterAPI;
import com.dangjia.acg.api.supplier.DjSupplierAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.MathUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.core.Task;
import com.dangjia.acg.dto.deliver.OrderSplitItemDTO;
import com.dangjia.acg.dto.refund.OrderProgressDTO;
import com.dangjia.acg.dto.repair.*;
import com.dangjia.acg.mapper.complain.IComplainMapper;
import com.dangjia.acg.mapper.delivery.IOrderSplitItemMapper;
import com.dangjia.acg.mapper.delivery.ISplitDeliverMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.house.IWarehouseMapper;
import com.dangjia.acg.mapper.member.IMasterMemberAddressMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.repair.*;
import com.dangjia.acg.mapper.supplier.IMasterSupplierMapper;
import com.dangjia.acg.modle.brand.Unit;
import com.dangjia.acg.modle.complain.Complain;
import com.dangjia.acg.modle.deliver.OrderItem;
import com.dangjia.acg.modle.deliver.OrderSplitItem;
import com.dangjia.acg.modle.deliver.SplitDeliver;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.house.TaskStack;
import com.dangjia.acg.modle.house.Warehouse;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.member.MemberAddress;
import com.dangjia.acg.modle.repair.*;
import com.dangjia.acg.modle.storefront.Storefront;
import com.dangjia.acg.modle.sup.SupplierProduct;
import com.dangjia.acg.modle.supplier.DjSupplier;
import com.dangjia.acg.service.acquisition.MasterCostAcquisitionService;
import com.dangjia.acg.service.complain.ComplainService;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.dangjia.acg.service.core.TaskStackService;
import com.dangjia.acg.service.deliver.OrderSplitService;
import com.dangjia.acg.service.product.MasterProductTemplateService;
import com.dangjia.acg.service.product.MasterStorefrontService;
import com.dangjia.acg.util.StringTool;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.JsonArray;
import com.netflix.discovery.converters.Auto;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

/**
 * author: Ronalcheng
 * Date: 2018/12/11 0011
 * Time: 9:41
 */
@Service
public class MendMaterielService {
    protected static final Logger logger = LoggerFactory.getLogger(MendMaterielService.class);
    @Autowired
    private IMendOrderMapper mendOrderMapper;
    @Autowired
    private IMendMaterialMapper mendMaterialMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IHouseMapper houseMapper;
    @Autowired
    private IMemberMapper memberMapper;
    @Autowired
    private IWarehouseMapper warehouseMapper;
    @Autowired
    private IMasterSupplierMapper iMasterSupplierMapper;
    @Autowired
    private ISplitDeliverMapper splitDeliverMapper;
    @Autowired
    private MasterCostAcquisitionService masterCostAcquisitionService;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private MasterStorefrontService masterStorefrontService;

    @Autowired
    private TaskStackService taskStackService;
    @Autowired
    private MendOrderCheckService mendOrderCheckService;
    @Autowired
    private ComplainService complainService;
    @Autowired
    private IMendDeliverMapper mendDeliverMapper ;
    @Autowired
    private IOrderSplitItemMapper orderSplitItemMapper;
    @Autowired
    private IComplainMapper iComplainMapper;
    @Autowired
    private IMasterMemberAddressMapper iMasterMemberAddressMapper;
    @Autowired
    private MasterProductTemplateService masterProductTemplateService;
    @Autowired
    private OrderSplitService orderSplitService;
    /**
     * 售后管理--退货退款--分发供应商列表
     * @param request
     * @param cityId
     * @param userId
     * @param mendOrderId 退货申请单ID
     * @return
     */
    public ServerResponse searchReturnRefundMaterielList(HttpServletRequest request, String cityId, String userId, String mendOrderId) {
        try{
            Map<String,Object> resultMap=new HashMap();
            MendOrder mendOrder=mendOrderMapper.selectByPrimaryKey(mendOrderId);
            if(mendOrder==null){
                return ServerResponse.createByErrorMessage("未找到符合条件的退货单");
            }
            if(mendOrder.getType()!=2&&mendOrder.getType()!=5){
                return ServerResponse.createByErrorMessage("此单不属于退货退款单，不能分发");
            }
            if(mendOrder.getState()!=1){
                return ServerResponse.createByErrorMessage("不是处理中的单，不能操作分发");
            }
            String address = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class);
            //查询申请退货单明细
            List<OrderSplitItemDTO> mendMaterialList=mendMaterialMapper.searchReturnRefundMaterielList(mendOrderId,null);
            if(mendMaterialList!=null&&mendMaterialList.size()>0){
                for (OrderSplitItemDTO sd:mendMaterialList){
                    sd.setImageUrl(StringTool.getImageSingle(sd.getImage(),address));
                    //2.2给当前店铺当前房子供过此商品的供应商
                    List<Map<String,Object>> supplierIdlist = mendMaterialMapper.getsupplierInfoList(mendOrder.getStorefrontId(),sd.getProductId(),mendOrder.getHouseId());
                    if(supplierIdlist==null||supplierIdlist.size()<=0){
                        //若未查到线上可发货的供应商，则查询非平台供应商给到页面选择
                        supplierIdlist=splitDeliverMapper.queryNonPlatformSupplier();
                    }
                    sd.setSupplierIdlist(supplierIdlist);
                }
            }
            resultMap.put("createDate",mendOrder.getCreateDate());//申请时间
            resultMap.put("state",mendOrder.getState());//申请状态，1待分配
            resultMap.put("imageUrl",StringTool.getImage(mendOrder.getImageArr(),address));//退货图片，相关凭证
            resultMap.put("mendOrderId",mendOrderId);
            resultMap.put("mendMaterialList",mendMaterialList);//要货单明细表
            return ServerResponse.createBySuccess("查询成功", resultMap);
        } catch (Exception e) {
            logger.error("查询失败",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 店铺退货分发供应商
     * @param mendOrderId
     * @param userId
     * @param materielSupList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse saveReturnRefundMaterielSup(String mendOrderId, String userId,String cityId, String materielSupList) {
        MendOrder mendOrder=mendOrderMapper.selectByPrimaryKey(mendOrderId);//补退订单表
        if(mendOrder==null){
            return ServerResponse.createByErrorMessage("未找到符合条件的退货单");
        }
        if(mendOrder.getType()!=2&&mendOrder.getType()!=5){
            return ServerResponse.createByErrorMessage("此单不属于退货退款单，不能分发");
        }
        if(mendOrder.getState()!=1){
            return ServerResponse.createByErrorMessage("不是处理中的单，不能操作分发");
        }
        List<Map<String,Object>> list= JSON.parseObject(materielSupList, List.class);
        for (Map<String, Object> stringStringMap : list) {
            String mendMaterielId=(String)stringStringMap.get("mendMaterielId");//退货单明细ID
            String supplierId=stringStringMap.get("supplierId").toString();//供应商id
            DjSupplier supplier=iMasterSupplierMapper.selectByPrimaryKey(supplierId);
            MendMateriel mendMateriel=mendMaterialMapper.selectByPrimaryKey(mendMaterielId);
            mendMateriel.setSupplierId(supplierId);//供应商ID
            mendMateriel.setSupplierName(supplier.getName());
            mendMateriel.setSupplierTelephone(supplier.getTelephone());
            //查询当前商品的供应价格
            SupplierProduct supplierProduct=orderSplitItemMapper.getsupplierProductById(mendOrder.getStorefrontId(),supplierId,mendMateriel.getProductId());
            if(supplierProduct!=null&&supplierProduct.getPrice()!=null){
                mendMateriel.setCost(supplierProduct.getPrice());//供应单价费用
                if(supplierProduct.getPorterage()!=null&&supplierProduct.getPorterage()>0){//需要收取搬运费(供应商的搬运费)
                    Double supTransCost= masterCostAcquisitionService.getSupStevedorageCost(mendOrder.getHouseId(),supplierProduct.getIsCartagePrice(),supplierProduct.getPorterage(),mendMateriel.getShopCount());
                    mendMateriel.setSupStevedorageCost(supTransCost);//供应商的搬运费
                }else{
                    mendMateriel.setSupStevedorageCost(0d);
                }
            }else{
                mendMateriel.setCost(0d);//如果是非平台供应商，则供应价设为0
                mendMateriel.setSupStevedorageCost(0d);//供应商搬动费为0
            }
            String orderItemId=mendMateriel.getOrderItemId();
            if(orderItemId!=null){//计算运费，搬运费
                OrderSplitItem orderSplitItem=orderSplitItemMapper.selectByPrimaryKey(orderItemId);
                if(orderSplitItem!=null){
                    Double transportationCost=orderSplitItem.getTransportationCost();//运费
                    Double stevedorageCost=orderSplitItem.getStevedorageCost();//搬运费
                    if(orderSplitItem.getReceive()==null)
                        orderSplitItem.setReceive(orderSplitItem.getNum());
                    //计算运费(店铺）
                    if(transportationCost>0.0) {//（搬运费/收货量）*退货量
                        mendMateriel.setTransportationCost(MathUtil.mul(MathUtil.div(transportationCost,orderSplitItem.getReceive()),mendMateriel.getShopCount()));
                    }else{
                        mendMateriel.setTransportationCost(0d);
                    }
                    //计算搬运费（店铺）
                    if(stevedorageCost>0.0){//（搬运费/收货量）*退货量
                        mendMateriel.setStevedorageCost(MathUtil.mul(MathUtil.div(stevedorageCost,orderSplitItem.getReceive()),mendMateriel.getShopCount()));
                    }else{
                        mendMateriel.setStevedorageCost(0d);
                    }
                }else{
                    mendMateriel.setTransportationCost(0d);
                    mendMateriel.setStevedorageCost(0d);
                }


            }
            mendMateriel.setSupplierId(supplierId);
            mendMateriel.setModifyDate(new Date());
            mendMaterialMapper.updateByPrimaryKey(mendMateriel);
        }
        //2.根据退货单查询附合条件的退费汇总信息
        Map<String ,Object> resultMap=new HashMap<>();
        Double sumTotalAmount=0d;
        Double sumApplyMoney=0d;
        List<Map<String,Object>> supItemList=mendMaterialMapper.selectSupMaterialByMendId(mendOrderId,null);
        if(supItemList!=null&&supItemList.size()>0){
            for(Map<String,Object> param:supItemList){
                Double totalAmount=((BigDecimal)param.get("totalAmount")).doubleValue();
                Double applyMoney=(Double)param.get("applyMoney");
                sumTotalAmount=MathUtil.add(sumTotalAmount,totalAmount);
                sumApplyMoney=MathUtil.add(sumApplyMoney,applyMoney);
            }
        }
        resultMap.put("sumTotalAmount",sumTotalAmount);
        resultMap.put("sumApplyMoney",sumApplyMoney);
        resultMap.put("mendOrderId",mendOrderId);
        resultMap.put("supItemList",supItemList);
        return ServerResponse.createBySuccess("查询成功",resultMap);
    }

    /**
     * 退货退款--分发供应商--生成退货单
     * @param mendOrderId 退货申请单ID
     * @param userId 用户ID
     * @param cityId 城市ID
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse generateMendDeliverorder(String mendOrderId, String userId,String cityId){
        MendOrder mendOrder=mendOrderMapper.selectByPrimaryKey(mendOrderId);//补退订单表
        if(mendOrder==null){
            return ServerResponse.createByErrorMessage("未找到符合条件的退货单");
        }
        if(mendOrder.getType()!=2&&mendOrder.getType()!=5){
            return ServerResponse.createByErrorMessage("此单不属于退货退款单，不能分发");
        }
        if(mendOrder.getState()!=1){
            return ServerResponse.createByErrorMessage("不是处理中的单，不能操作分发");
        }
        //2.根据发货单ID，查询符合条件需生成发货单的列表(若为重新发货，则splitDeliverId不为空)
        List<Map<String,Object>> supItemList=mendMaterialMapper.selectSupMaterialByMendId(mendOrderId,null);
        if(supItemList!=null){
            MemberAddress memberAddress=iMasterMemberAddressMapper.selectByPrimaryKey(mendOrder.getAddressId());
            MendDeliver mendDeliver;
            Example example;
            //3.生成发货单信息
            for(Map<String,Object> supMap:supItemList){
                String supplierId=(String)supMap.get("supplierId");//供应商ID
                String isNonPlatformSupplier=(String)supMap.get("isNonPlatformSupplier");//是否非平台供应商，1是，0否
                Double supTotalPrice=((BigDecimal)supMap.get("supTotalPrice")).doubleValue();//供应商品总额
                Double supStevedorageCost=(Double)supMap.get("supStevedorageCost");//搬运费
                String supName=(String)supMap.get("supName");//供应商名称
                String telephone=(String)supMap.get("telephone");//电话
                Double supTransportationCost=((BigDecimal)supMap.get("supTransportationCost")).doubleValue();//每单收取运费,供应商
                Double totalAmount=((BigDecimal)supMap.get("totalAmount")).doubleValue();
                Double applyMoney=(Double)supMap.get("applyMoney");
                //生成发货单信息
                example = new Example(SplitDeliver.class);
                mendDeliver = new MendDeliver();
                mendDeliver.setNumber(mendOrder.getNumber() + "00" + mendDeliverMapper.selectCountByExample(example));//退货单号
                mendDeliver.setHouseId(mendOrder.getHouseId());
                mendDeliver.setMendOrderId(mendOrderId);
                mendDeliver.setTotalAmount(totalAmount);
                mendDeliver.setDeliveryFee(supTransportationCost);
                mendDeliver.setApplyMoney(applyMoney);
                mendDeliver.setTotalPrice(supTotalPrice);//供应商供应商品总额
                mendDeliver.setStevedorageCost(supStevedorageCost);//供应商总搬运费
                mendDeliver.setShipName(mendOrder.getApplyMemberId());
                if(memberAddress!=null){
                    mendDeliver.setShipMobile(memberAddress.getMobile());
                    mendDeliver.setShipAddress(memberAddress.getAddress());
                }
                mendDeliver.setAddressId(mendOrder.getAddressId());
                mendDeliver.setSupplierId(supplierId);//供应商id
                mendDeliver.setSupplierTelephone(telephone);//供应商联系电话
                mendDeliver.setSupplierName(supName);//供应商供应商名称
                mendDeliver.setSubmitTime(new Date());
                mendDeliver.setShippingState(0);//待退货
                mendDeliver.setApplyState(0);
                mendDeliver.setStorefrontId(mendOrder.getStorefrontId());//店铺id
                mendDeliverMapper.insert(mendDeliver);//生成发货单
                //4.维护对应的退货单号到退货单明细中去
                mendMaterialMapper.updateMendDeliverIdByInfo(mendDeliver.getId(),mendOrderId,supplierId);
            }
        }
        //5.修改要货单的发货状态为已发送给供应商
        mendOrder.setState(3);//3已通过或已分发供应商
        mendOrder.setModifyDate(new Date());
        mendOrderMapper.updateByPrimaryKeySelective(mendOrder);//修改要货单信息
        return ServerResponse.createBySuccessMessage("保存成功");
    }


    //维护实际退货数到对应的退货单明细中去
    private void updateMendMaterialList(String mendMaterialList) {
        JSONArray jsonArray = JSONArray.parseArray(mendMaterialList);
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            String mendMaterielId=(String)obj.get("mendMaterielId");
            Double actualCount=obj.getDouble("actualCount");
            MendMateriel mendMateriel=mendMaterialMapper.selectByPrimaryKey(mendMaterielId);
            if (mendMateriel.getShopCount() < actualCount) {
                actualCount = mendMateriel.getShopCount();
            }
            mendMateriel.setActualCount(actualCount);
            mendMateriel.setModifyDate(new Date());
            mendMaterialMapper.updateByPrimaryKeySelective(mendMateriel);
        }
    }

    /**
     * 重新计算运费，搬运费
     * @param mendDeliver 退货单信息
     * @param type 1按申请数量计算，2按退货数量计算
     */
    private void updateNewMendMaterialList(MendDeliver mendDeliver,Integer type) {
        Double totalAmount=0d;//计算店铺收货时可得钱
        Double applyMoney=0d;//供应商可得钱
        Double totalPrice=0d;//供应商品总额
        Double totalStevedorageCost=0d;//供应搬运费
        Example example=new Example(MendMateriel.class);
        example.createCriteria().andEqualTo(MendMateriel.MEND_ORDER_ID,mendDeliver.getMendOrderId())
                .andEqualTo(MendMateriel.REPAIR_MEND_DELIVER_ID,mendDeliver.getId());
        List<MendMateriel> mendMaterielList=mendMaterialMapper.selectByExample(example);//查询符合条件的信息
        Double totalReceiverNum=mendMaterialMapper.getMendMaterialReceiverNum(mendDeliver.getId(),type);//查询当前收货单下的总收货量
        for (MendMateriel mendMateriel:mendMaterielList) {
            Double actualCount=mendMateriel.getShopCount();
            if(type==2&&mendMateriel.getActualCount()!=null){
                //若为部分分货，则按部分退
                actualCount=mendMateriel.getActualCount();
            }
            mendMateriel.setActualCount(actualCount);
            mendMateriel.setModifyDate(new Date());
            //如果是部分退，则重新计算运费搬运费
            String orderItemId = mendMateriel.getOrderItemId();
            if (mendMateriel.getShopCount() > actualCount) {
                mendMateriel.setTransportationCost(0d);
                mendMateriel.setStevedorageCost(0d);
                mendMateriel.setSupStevedorageCost(0d);
                mendMateriel.setSupTransportationCost(0d);
                //重新计算店铺的运费，搬运费，供应商的运费，搬运费
                OrderSplitItem orderSplitItem = orderSplitItemMapper.selectByPrimaryKey(orderItemId);//获取发货单是的运费，搬运费
                if (StringUtils.isNotBlank(orderItemId) && orderSplitItem != null) {
                    Double transportationCost = orderSplitItem.getTransportationCost();//运费
                    Double stevedorageCost = orderSplitItem.getStevedorageCost();//搬运费
                    if (orderSplitItem.getReceive() == null)
                        orderSplitItem.setReceive(orderSplitItem.getNum());
                    //计算运费(店铺）
                    if (transportationCost > 0.0) {//（搬运费/收货量）*退货量
                        mendMateriel.setTransportationCost(MathUtil.mul(MathUtil.div(transportationCost, orderSplitItem.getReceive()), actualCount));
                    } else {
                        mendMateriel.setTransportationCost(0d);
                    }
                    //计算搬运费（店铺）
                    if (stevedorageCost > 0.0) {//（搬运费/收货量）*退货量
                        mendMateriel.setStevedorageCost(MathUtil.mul(MathUtil.div(stevedorageCost, orderSplitItem.getReceive()), actualCount));
                    } else {
                        mendMateriel.setStevedorageCost(0d);
                    }
                    //计算供应商的搬运费
                    mendMateriel.setSupStevedorageCost(orderSplitService.getSupProductStevedorageCost(mendDeliver.getStorefrontId(), mendDeliver.getSupplierId(), mendMateriel.getProductId(), mendDeliver.getHouseId(), actualCount));
                    if(totalReceiverNum>0){//运费
                        mendMateriel.setSupTransportationCost(MathUtil.mul(MathUtil.div(mendDeliver.getDeliveryFee(),totalReceiverNum),actualCount));
                    }

                }
                if(mendMateriel.getSupStevedorageCost()==null)
                    mendMateriel.setSupStevedorageCost(0d);
                totalAmount=MathUtil.add(totalAmount, MathUtil.sub(MathUtil.sub(MathUtil.mul(mendMateriel.getPrice(),actualCount),mendMateriel.getTransportationCost()),mendMateriel.getStevedorageCost()));
                applyMoney=MathUtil.add(applyMoney,MathUtil.sub(MathUtil.mul(mendMateriel.getCost(),actualCount),mendMateriel.getSupStevedorageCost()));
                totalPrice=MathUtil.add(totalPrice,MathUtil.mul(mendMateriel.getCost(),actualCount));
                totalStevedorageCost=MathUtil.add(totalStevedorageCost,mendMateriel.getSupStevedorageCost());
            }
            mendMaterialMapper.updateByPrimaryKeySelective(mendMateriel);
        }
        applyMoney=MathUtil.sub(applyMoney,mendDeliver.getDeliveryFee());
        mendDeliver.setTotalAmount(totalAmount);
        mendDeliver.setApplyMoney(applyMoney);
        mendDeliver.setStevedorageCost(totalStevedorageCost);
        mendDeliver.setTotalPrice(totalPrice);
        mendDeliverMapper.updateByPrimaryKeySelective(mendDeliver);
    }



    /**
     * 退货退款—确认退货/部分退货
     * @param mendDeliverId 退货单ID
     * @param userId 用户id
     * @param type 类型：1确认退货，2部分退货
     * @param mendMaterialList 退货详情列表 [{“mendMaterielId”:”退货明细ID”,”actualCount”:9（实退货量）}]
     * @param partialReturnReason 部分退货原因
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse confirmReturnMendMaterial(String mendDeliverId, String userId,Integer type,String mendMaterialList,String partialReturnReason) {
        MendDeliver mendDeliver=mendDeliverMapper.selectByPrimaryKey(mendDeliverId);
        if(mendDeliver==null){
            return ServerResponse.createByErrorMessage("未找到符合条件的发货单");
        }
        if(StringUtils.isBlank(mendMaterialList)){
            return ServerResponse.createByErrorMessage("确认退单明细商品不能为空");
        }
        if(type==2&&StringUtils.isBlank(partialReturnReason)){
            return ServerResponse.createByErrorMessage("确认退货时，收货原因不能为空");
        }
        if(mendDeliver.getShippingState()!=0){
            return ServerResponse.createByErrorMessage("已确认过退货，请勿重复确认");
        }
        //更新订单明细信息
        updateMendMaterialList( mendMaterialList);
        //汇总最新的价钱到对应的发货单上去(更新发货单信息）

        if (type == 1) {//1确认全部退货
            updateNewMendMaterialList(mendDeliver,1);
            mendDeliver.setShippingState(1);//已确认全部退货
            mendDeliver.setApplyState(0);//供应商结算状态
            //打钱给业主（扣店铺的总额和可提现余额),业主仓库中的退货量减少
            mendOrderCheckService.setMendMoneyOrder(mendDeliverId,userId);
        } else if (type == 2){//确认部分退货
            updateNewMendMaterialList(mendDeliver,2);
            mendDeliver.setShippingState(4);//部分退货
            mendDeliver.setReasons(partialReturnReason);//退货原因
            House house=houseMapper.selectByPrimaryKey(mendDeliver.getHouseId());
            //增加部分退货任务
            String image="";
            Storefront storefront=masterStorefrontService.getStorefrontById(mendDeliver.getStorefrontId());
            if(storefront!=null){
                image=storefront.getSystemLogo();
            }
            taskStackService.insertTaskStackInfo(mendDeliver.getHouseId(),house.getMemberId(),"部分退货",image,17,mendDeliverId);
        }
        mendDeliver.setModifyDate(new Date());
        mendDeliverMapper.updateByPrimaryKeySelective(mendDeliver);
        return ServerResponse.createBySuccessMessage("确认退货成功");
    }

    /**
     * 要货退货 查询补材料
     */
    public List<MendMateriel> askAndQuit(String workerTypeId, String houseId, String categoryId, String name) {
        List<MendMateriel> mendMaterielList = mendMaterialMapper.askAndQuit(workerTypeId, houseId, categoryId, name);
        return mendMaterielList;
    }

    /**
     * 售后管理--仅退款--退货单详情列表
     * @param cityId
     * @param userId
     * @param mendOrderId 退货申请单ID
     * @return
     */
    public ServerResponse searchRefundMaterielList(String cityId,String userId,String mendOrderId){
       try{
           Map<String,Object> resultMap=new HashMap();
           MendOrder mendOrder=mendOrderMapper.selectByPrimaryKey(mendOrderId);
           if(mendOrder==null){
               return ServerResponse.createByErrorMessage("未找到符合条件的退货单");
           }
           String address = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class);
           //查询申请退货单明细
           List<OrderSplitItemDTO> mendMaterialList=mendMaterialMapper.searchReturnRefundMaterielList(mendOrderId,null);
           if(mendMaterialList!=null&&mendMaterialList.size()>0){
               for (OrderSplitItemDTO sd:mendMaterialList){
                   sd.setImageUrl(StringTool.getImageSingle(sd.getImage(),address));
               }
           }
           resultMap.put("state",mendOrder.getState());
           resultMap.put("totalAmount",mendOrder.getTotalAmount());//总价（包含运费，搬运费）
           resultMap.put("actualTotalAmount",mendOrder.getActualTotalAmount());//商品总额
           resultMap.put("totalStevedorageCost",mendOrder.getTotalStevedorageCost());
           resultMap.put("carriage",mendOrder.getCarriage());
           resultMap.put("mendOrderId",mendOrderId);
           resultMap.put("mendMaterialList",mendMaterialList);//要货单明细表
           return ServerResponse.createBySuccess("查询成功", resultMap);
       }catch(Exception e){
           logger.error("查询失败");
           return ServerResponse.createByErrorMessage("查询失败");
       }
    }

    /**
     * 售后管理--仅退款--确认退款
     * @param cityId
     * @param userId
     * @param mendOrderId 退货申请单ID
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse saveRefundMaterielInfo(String cityId,String userId,String mendOrderId){

        MendOrder mendOrder=mendOrderMapper.selectByPrimaryKey(mendOrderId);
        if(mendOrder==null||mendOrder.getType()!=4){
            return ServerResponse.createByErrorMessage("未找到符合条件的退货申请单");
        }
        if(mendOrder.getState()!=1){
            return ServerResponse.createByErrorMessage("此订单已处理，请勿重复处理");
        }
        //1.退钱给业主，修改材料仓库
        mendOrderCheckService.settleMendOrder(mendOrder);
        //2.修改退款申请单信息
        mendOrder.setState(4);//已结算
        mendOrder.setModifyDate(new Date());
        mendOrderMapper.updateByPrimaryKeySelective(mendOrder);
        return ServerResponse.createByErrorMessage("确认成功");
    }

    /**
     *店铺--售后处理--获取统计数量
     * @param cityId 城市ID
     * @param userId 用户ID
     * @param type 查询类型：1退货退款，2仅退款
     * @return
     */
    public ServerResponse searchRefundCountNumber(String cityId,String userId,Integer type){
        try{
            Storefront storefront = masterStorefrontService.getStorefrontByUserId(userId, cityId);
            if (storefront == null) {
                return ServerResponse.createByErrorMessage("不存在店铺信息，请先维护店铺信息");
            }
            Map<String,Object> resultMap=new HashMap<>();
            if(type==1){
                resultMap.put("needNumber",mendOrderMapper.searchReturnRrefundCount(storefront.getId(), type, 1));//待分配
                resultMap.put("assignedNumber",mendDeliverMapper.searchReturnRefundSplitCount(storefront.getId(), 1));//已分配供应商
                resultMap.put("completeNumber",mendDeliverMapper.searchReturnRefundSplitCount(storefront.getId(), 2));//已完成
            }else if(type==2){
                resultMap.put("needNumber",mendOrderMapper.searchReturnRrefundCount(storefront.getId(), type, 1));//待处理
                resultMap.put("completeNumber",mendOrderMapper.searchReturnRrefundCount(storefront.getId(), type, 2));//已处理
            }

            return ServerResponse.createBySuccess("查询成功",resultMap);
        }catch (Exception e){
            logger.error("查询失败",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     *店铺--售后处理--待处理列表
     * @param request
     * @param cityId 城市ID
     * @param userId 用户ID
     * @param pageDTO
     * @param state 状态默认：1待处理，2已处理
     * @param likeAddress
     * @param type 查询类型：1退货退款，2仅退款
     * @return
     */
    public ServerResponse searchReturnRrefundList(HttpServletRequest request, String cityId, String userId, PageDTO pageDTO, Integer state, String likeAddress,Integer type) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            //判断是否有维护店铺信息，若未维护，则返回提示
            Storefront storefront = masterStorefrontService.getStorefrontByUserId(userId, cityId);
            if (storefront == null) {
                return ServerResponse.createByErrorMessage("不存在店铺信息，请先维护店铺信息");
            }
            List<MendOrderDTO> mendOrderList = mendOrderMapper.searchReturnRrefundList(storefront.getId(), type, state, likeAddress);
            PageInfo pageResult = new PageInfo(mendOrderList);
            pageResult.setList(mendOrderList);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
           logger.error("查询失败",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }
    /**
     *店铺--售后处理--退货退款--已处理列表
     * @param request
     * @param cityId 城市ID
     * @param userId 用户ID
     * @param pageDTO
     * @param state 状态默认：2.已分发供应商 3.已结束
     * @param likeAddress 单号或地址
     * @return
     */
    public ServerResponse searchReturnRefundSplitList(HttpServletRequest request, String cityId, String userId, PageDTO pageDTO, Integer state, String likeAddress) {
       try{
           //判断是否有维护店铺信息，若未维护，则返回提示
           Storefront storefront = masterStorefrontService.getStorefrontByUserId(userId, cityId);
           if (storefront == null) {
               return ServerResponse.createByErrorMessage("不存在店铺信息，请先维护店铺信息");
           }
           PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
           List<MendDeliverDTO> mendDeliverList = mendDeliverMapper.searchReturnRefundSplitList(storefront.getId(), state, likeAddress);
           PageInfo pageResult = new PageInfo(mendDeliverList);
           pageResult.setList(mendDeliverList);
           return ServerResponse.createBySuccess("查询成功",pageResult);
       }catch(Exception e){
           logger.error("查询失败",e);
           return ServerResponse.createByErrorMessage("查询失败");
       }
    }


    /**
     * 房子id查询退货单列表
     * material_back_state
     * 0生成中,1平台审核中，2平台审核不通过，3审核通过，4管家取消
     */
    public ServerResponse materialBackState(String userId, String cityId, PageDTO pageDTO, String state, String likeAddress) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            //通过缓存查询店铺信息
            Storefront storefront = masterStorefrontService.getStorefrontByUserId(userId, cityId);
            if (storefront == null) {
                return ServerResponse.createByErrorMessage("不存在店铺信息，请先维护店铺信息");
            }
            List<MendOrder> mendOrderList = mendOrderMapper.materialByStateAndLikeAddress(storefront.getId(), 2,  state, likeAddress);
            PageInfo pageResult = new PageInfo(mendOrderList);
            List<MendOrderDTO> mendOrderDTOS = getMendOrderDTOList(mendOrderList);
            pageResult.setList(mendOrderDTOS);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 退货退款—退货详情列表
     * @param mendDeliverId 退货单ID
     * @return
     */
    public ServerResponse queryMendMaterialList(String mendDeliverId) {
        try{
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            MendDeliver mendDeliver = mendDeliverMapper.selectByPrimaryKey(mendDeliverId);//补退订单表
            if (mendDeliver == null) {
                return ServerResponse.createByErrorMessage("未找到符合条件订单");
            }
            MendDeliverDTO mendDeliverDTO=new MendDeliverDTO();
            BeanUtils.beanToBean(mendDeliver,mendDeliverDTO);
            mendDeliverDTO.setMendDeliverId(mendDeliver.getId());
            mendDeliverDTO.setMendDeliverNumber(mendDeliver.getNumber());
            mendDeliverDTO.setState(mendDeliver.getShippingState());
            if(mendDeliver.getShippingState()==6){;//若状态为6，则合并为1返回给前端
                mendDeliverDTO.setState(1);
            }
            //2.获取对应的发货单明细信息
            List<OrderSplitItemDTO> mendMaterialList=mendMaterialMapper.searchReturnRefundMaterielList(mendDeliver.getMendOrderId(),mendDeliverId);
            if(mendMaterialList!=null&&mendMaterialList.size()>0){
                for (OrderSplitItemDTO sd:mendMaterialList){
                    if(sd.getActualCount()==null){
                        sd.setActualCount(sd.getShopCount());
                    }
                    //2.1查询当前订单对应的
                    if(StringUtils.isNotBlank(sd.getImage())){
                        sd.setImageUrl(StringTool.getImageSingle(sd.getImage(),address));
                    }

                    sd.setSupTotalPrice(MathUtil.mul(sd.getSupCost(),sd.getActualCount()));//供应商品总价
                    sd.setTotalPrice(MathUtil.mul(sd.getPrice(),sd.getActualCount()));//销售商品总价
                    //查询供应商的搬运费
                    Double porterage=orderSplitItemMapper.getSupPorterage(mendDeliver.getStorefrontId(),mendDeliver.getSupplierId(),sd.getProductId());
                    if(porterage!=null){
                        sd.setSupPorterage(porterage);
                    }else{
                        sd.setSupPorterage(0d);
                    }

                    //查询商品单位
                    Unit unit=masterProductTemplateService.getUnitInfoByTemplateId(sd.getProductTemplateId());
                    if(unit!=null){
                        sd.setUnitId(unit.getId());
                        sd.setUnitName(unit.getName());
                    }
                }
            }
            mendDeliverDTO.setApplyMoney(mendDeliver.getApplyMoney());//供应商总价
            mendDeliverDTO.setTotalAmount(mendDeliver.getTotalAmount());//店铺总价
            mendDeliverDTO.setTotalPrice(mendDeliver.getTotalPrice());//供应商品总价
            mendDeliverDTO.setStevedorageCost(mendDeliver.getStevedorageCost());//供应商搬运费
            mendDeliverDTO.setDeliveryFee(mendDeliver.getDeliveryFee());//供应商总运费
            DjSupplier djSupplier=iMasterSupplierMapper.selectByPrimaryKey(mendDeliver.getSupplierId());
            if(djSupplier!=null){
                mendDeliverDTO.setIsNonPlatformSupplier(djSupplier.getIsNonPlatformSupperlier().toString());//是否非平台供应商
            }else{
                mendDeliverDTO.setIsNonPlatformSupplier("0");//是否非平台供应商
            }
            mendDeliverDTO.setMendMaterielList(mendMaterialList);
            return ServerResponse.createBySuccess("查询成功", mendDeliverDTO);
        }catch (Exception e){
          logger.error("查询失败",e);
          return ServerResponse.createByErrorMessage("查询失败");
        }

    }

    /**
     * 根据mendOrderId查明细
     */
    public ServerResponse mendMaterialList(String mendOrderId, String userId) {
        MendOrder mendOrder = mendOrderMapper.selectByPrimaryKey(mendOrderId);
        House house = houseMapper.selectByPrimaryKey(mendOrder.getHouseId());
        List<MendMateriel> mendMaterielList = mendMaterialMapper.byMendOrderId(mendOrderId);
        List<Map> mendMaterielMaps = new ArrayList<>();
        for (MendMateriel mendMateriel : mendMaterielList) {
            mendMateriel.initPath(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class));
            Map map = BeanUtils.beanToMap(mendMateriel);
            Warehouse warehouse = warehouseMapper.getByProductId(mendMateriel.getProductId(), mendOrder.getHouseId());//材料仓库统计
            if (warehouse == null) {
                map.put(Warehouse.RECEIVE, "0");//收货总数
            } else {
                //工匠退材料新增已收货数量字段
                if (mendOrder.getType() == 2) {
                    map.put(Warehouse.RECEIVE, warehouse.getReceive() == null ? 0d : warehouse.getReceive());
                }
                //业主退材料增加未发货数量
                if (mendOrder.getType() == 4) {
                    //未发货数量=已要 - 已收
                    map.put(Warehouse.RECEIVE, warehouse.getShopCount() - (warehouse.getOwnerBack() == null ? 0D : warehouse.getOwnerBack()) - warehouse.getAskCount());
                }
            }
            //判断商品有哪些供应商供应
            List<Map<String,Object>> supplierIdList = splitDeliverMapper.getSupplierGoodsId(mendOrder.getHouseId(), mendMateriel.getProductSn());
            if (supplierIdList!=null)
                map.put("suppliers", supplierIdList);
            mendMaterielMaps.add(map);
        }
        return ServerResponse.createBySuccess("查询成功", mendMaterielMaps);
    }

    /**
     * 房子id查询补货单列表
     * materialOrderState
     * 0生成中,1平台审核中，2平台审核不通过，3平台审核通过待业主支付,4业主已支付，5业主不同意，6管家取消
     */
    public ServerResponse materialOrderState(String userId, String cityId,String houseId, PageDTO pageDTO, String beginDate, String endDate, String state, String likeAddress) {
        try {
            Storefront storefront = masterStorefrontService.getStorefrontByUserId(userId, cityId);
            if (storefront == null) {
                return ServerResponse.createByErrorMessage("不存在店铺信息，请先维护店铺信息");
            }
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            if (!CommonUtil.isEmpty(beginDate) && !CommonUtil.isEmpty(endDate)) {
                if (beginDate.equals(endDate)) {
                    beginDate = beginDate + " " + "00:00:00";
                    endDate = endDate + " " + "23:59:59";
                }
            }
//            List<MendOrder> mendOrderList = mendOrderMapper.materialOrderState(houseId);
            List<MendOrder> mendOrderList = mendOrderMapper.materialByStateAndLikeAddress(storefront.getId(), 0, state, likeAddress);
            PageInfo pageResult = new PageInfo(mendOrderList);
            List<MendOrderDTO> mendOrderDTOS = getMendOrderDTOList(mendOrderList);
            pageResult.setList(mendOrderDTOS);

            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    public List<MendOrderDTO> getMendOrderDTOList(List<MendOrder> mendOrderList) {

        List<MendOrderDTO> mendOrderDTOS = new ArrayList<MendOrderDTO>();
        for (MendOrder mendOrder : mendOrderList) {
            MendOrderDTO mendOrderDTO = new MendOrderDTO();
            mendOrderDTO.setMendOrderId(mendOrder.getId());
            mendOrderDTO.setNumber(mendOrder.getNumber());
            mendOrderDTO.setCreateDate(mendOrder.getCreateDate());
            House house = houseMapper.selectByPrimaryKey(mendOrder.getHouseId());
            if (house != null) {
                if (house.getVisitState() != 0) {
                    mendOrderDTO.setAddress(house.getHouseName());
                    Member member = memberMapper.selectByPrimaryKey(house.getMemberId());
                    mendOrderDTO.setMemberName(member.getNickName() == null ? member.getName() : member.getNickName());
                    mendOrderDTO.setMemberId(member.getId());
                    mendOrderDTO.setMemberMobile(member.getMobile());
                }
            }
            Member worker = memberMapper.selectByPrimaryKey(mendOrder.getApplyMemberId());//申请人id
            if (worker != null) {
                mendOrderDTO.setApplyMemberId(worker.getId());
                mendOrderDTO.setApplyName(CommonUtil.isEmpty(worker.getName()) ? worker.getNickName() : worker.getName());
                mendOrderDTO.setApplyMobile(worker.getMobile());
            }
            mendOrderDTO.setType(mendOrder.getType());
            mendOrderDTO.setState(mendOrder.getState());
            mendOrderDTO.setTotalAmount(mendOrder.getTotalAmount());

            mendOrderDTO.setDeliverNumber(mendOrder.getDeliverNumber());
            mendOrderDTO.setSupplierName(mendOrder.getSupplierName());

            mendOrderDTOS.add(mendOrderDTO);

        }

        return mendOrderDTOS;
    }


    /**
     * 业主清点剩余材料
     * @param data
     * @return
     */
    public ServerResponse querySurplusMaterial(String data) {
        try {
            ArrSurplusMaterialDTO arrSurplusMaterialDTO = new ArrSurplusMaterialDTO();
            List<SurplusMaterialDTO> surplusMaterialDTOS = mendOrderMapper.querySurplusMaterial(data);
            if(surplusMaterialDTOS != null && surplusMaterialDTOS.size() >0){
                String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
                for (SurplusMaterialDTO surplusMaterialDTO : surplusMaterialDTOS) {
                    surplusMaterialDTO.setImage(imageAddress + surplusMaterialDTO.getImage());
                }
                arrSurplusMaterialDTO.setList(surplusMaterialDTOS);
                arrSurplusMaterialDTO.setCreateDate(surplusMaterialDTOS.get(0).getCreateDate());
                arrSurplusMaterialDTO.setMobile(surplusMaterialDTOS.get(0).getMobile());
                arrSurplusMaterialDTO.setName(surplusMaterialDTOS.get(0).getName());
                arrSurplusMaterialDTO.setHouseId(surplusMaterialDTOS.get(0).getHouseId());
                arrSurplusMaterialDTO.setWorkerId(surplusMaterialDTOS.get(0).getWorkerId());
            }
            return ServerResponse.createBySuccess("查询成功", arrSurplusMaterialDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 业主审核部分退货
     * @param taskId
     * @return
     */
    public ServerResponse queryTrialRetreatMaterial(String taskId) {
        try {
            TaskStack taskStack=taskStackService.selectTaskStackById(taskId);
            if(taskStack==null){
                return ServerResponse.createByErrorMessage("未找到符合条件的信息");
            }
            if(taskStack.getState()==1){
                return ServerResponse.createByErrorMessage("当前任务已处理，请勿重复处理");
            }
            MendDeliver mendDeliver=mendDeliverMapper.selectByPrimaryKey(taskStack.getData());
            MendDeliverDTO mendDeliverDTO=new MendDeliverDTO();
            BeanUtils.beanToBean(mendDeliver,mendDeliverDTO);
            mendDeliverDTO.setMendDeliverId(mendDeliver.getId());
            mendDeliverDTO.setMendDeliverNumber(mendDeliver.getNumber());//退货单号
            mendDeliverDTO.setState(mendDeliver.getShippingState());//状态 4部分退货
            mendDeliverDTO.setReasons(mendDeliver.getReasons());//退货原因
            if(mendDeliver.getShippingState()==6){;//若状态为6，则合并为1返回给前端
                mendDeliverDTO.setState(1);
            }
            //查询店铺电话
            Storefront storefront=masterStorefrontService.getStorefrontById(mendDeliver.getStorefrontId());
            if(storefront!=null){
               mendDeliverDTO.setStorefrontId(storefront.getId());
               mendDeliverDTO.setStorefrontName(storefront.getStorefrontName());
               mendDeliverDTO.setStorefrontMobile(storefront.getMobile());
            }
            //2.获取对应的发货单明细信息
            List<OrderSplitItemDTO> mendMaterialList=mendMaterialMapper.searchReturnRefundMaterielList(mendDeliver.getMendOrderId(),mendDeliver.getId());
            if(mendMaterialList!=null&&mendMaterialList.size()>0){
                String address = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
                for (OrderSplitItemDTO sd:mendMaterialList){
                    if(sd.getActualCount()==null){
                        sd.setActualCount(sd.getShopCount());
                    }
                    //2.1查询当前订单对应的
                    if(StringUtils.isNotBlank(sd.getImage())){
                        sd.setImageUrl(StringTool.getImageSingle(sd.getImage(),address));
                    }
                    //查询商品单位
                    Unit unit=masterProductTemplateService.getUnitInfoByTemplateId(sd.getProductTemplateId());
                    if(unit!=null){
                        sd.setUnitId(unit.getId());
                        sd.setUnitName(unit.getName());
                    }
                }
            }
            mendDeliverDTO.setMendMaterielList(mendMaterialList);//退货详情列表
            return ServerResponse.createBySuccess("查询成功", mendDeliverDTO);
        } catch (Exception e) {
            logger.error("查询失败",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }



    /**
     * 业主审核部分退货--申请平台介入/接受商家退货
     * @param userToken
     * @param taskId 任务ID
     * @param description 平台介入原因
     * @param type 审核结果：1申请平台介入 2接受商家退货数
     * @return
     */
    public ServerResponse addPlatformComplain(String userToken,String taskId,String description,Integer type){

        TaskStack taskStack=taskStackService.selectTaskStackById(taskId);
        if(taskStack==null){
            return ServerResponse.createByErrorMessage("未找到符合条件的信息");
        }
        if(taskStack.getState()==1){
            return ServerResponse.createByErrorMessage("当前任务已处理，请勿重复处理");
        }
        MendDeliver mendDeliver = mendDeliverMapper.selectByPrimaryKey(taskStack.getData());
        if(mendDeliver ==null){
            return ServerResponse.createByErrorMessage("未找到符合条件的订单数据");
        }
        if(type==1){//申请平台介入
            Member member=memberMapper.selectByPrimaryKey(taskStack.getMemberId());
            complainService.insertUserComplain(member.getName(),member.getMobile(),member.getId(),mendDeliver.getId(),mendDeliver.getHouseId(),7,description,"",2);
            mendDeliver.setShippingState(5);//业主申诉部分退货
        }else if(type==2){//接受商家部分退货
            updateNewMendMaterialList(mendDeliver,2);
            //打钱给业主（扣店铺的总额和可提现余额),业主仓库中的退货量减少
            mendOrderCheckService.setMendMoneyOrder(mendDeliver.getId(),taskStack.getMemberId());
            mendDeliver.setShippingState(6);//业主认可部分退货
            mendDeliver.setApplyState(0);//供应商结算状态
        }
        mendDeliver.setModifyDate(new Date());
        mendDeliverMapper.updateByPrimaryKeySelective(mendDeliver);
        taskStack.setState(1);//修改状态为已处理
        taskStack.setModifyDate(new Date());
        taskStackService.updateTaskStackInfo(taskStack);
        return ServerResponse.createByErrorMessage("提交成功");
    }

    /**
     * 修改申诉结果处理
     * @param mendDeliverId
     * @param type 7平台同意业主申诉（按申请退） 8，平台驳回（按确认退）
     */
    public  void updatePlatformComplainInfo(String mendDeliverId,String userId,Integer type){
        MendDeliver mendDeliver=mendDeliverMapper.selectByPrimaryKey(mendDeliverId);
        if(type==7){
            updateNewMendMaterialList(mendDeliver,1);
            //打钱给业主（扣店铺的总额和可提现余额),业主仓库中的退货量减少
            mendOrderCheckService.setMendMoneyOrder(mendDeliver.getId(),userId);
            mendDeliver.setShippingState(7);//按业主申请退货
            mendDeliver.setApplyState(0);//供应商结算状态
        }else{
            updateNewMendMaterialList(mendDeliver,2);
            //打钱给业主（扣店铺的总额和可提现余额),业主仓库中的退货量减少
            mendOrderCheckService.setMendMoneyOrder(mendDeliver.getId(),userId);
            mendDeliver.setShippingState(8);//按平台同意退货
            mendDeliver.setApplyState(0);//供应商结算状态
        }
        mendDeliverMapper.updateByPrimaryKeySelective(mendDeliver);
    }

}
