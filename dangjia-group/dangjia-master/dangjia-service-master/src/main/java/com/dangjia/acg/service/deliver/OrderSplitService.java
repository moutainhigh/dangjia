package com.dangjia.acg.service.deliver;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.enums.AppType;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.MathUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.deliver.DeliverHouseDTO;
import com.dangjia.acg.dto.deliver.OrderSplitDTO;
import com.dangjia.acg.dto.deliver.OrderSplitItemDTO;
import com.dangjia.acg.dto.deliver.SplitDeliverDetailDTO;
import com.dangjia.acg.mapper.complain.IComplainMapper;
import com.dangjia.acg.mapper.delivery.IOrderItemMapper;
import com.dangjia.acg.mapper.delivery.IOrderSplitItemMapper;
import com.dangjia.acg.mapper.delivery.IOrderSplitMapper;
import com.dangjia.acg.mapper.delivery.ISplitDeliverMapper;
import com.dangjia.acg.mapper.design.IQuantityRoomMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.house.IWarehouseMapper;
import com.dangjia.acg.mapper.member.IMasterMemberAddressMapper;
import com.dangjia.acg.mapper.repair.IMendOrderMapper;
import com.dangjia.acg.mapper.supplier.IMasterSupplierMapper;
import com.dangjia.acg.mapper.user.UserMapper;
import com.dangjia.acg.modle.brand.Unit;
import com.dangjia.acg.modle.complain.Complain;
import com.dangjia.acg.modle.deliver.OrderItem;
import com.dangjia.acg.modle.deliver.OrderSplit;
import com.dangjia.acg.modle.deliver.OrderSplitItem;
import com.dangjia.acg.modle.deliver.SplitDeliver;
import com.dangjia.acg.modle.design.QuantityRoom;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.house.Warehouse;
import com.dangjia.acg.modle.member.MemberAddress;
import com.dangjia.acg.modle.repair.MendOrder;
import com.dangjia.acg.modle.storefront.Storefront;
import com.dangjia.acg.modle.sup.SupplierProduct;
import com.dangjia.acg.modle.supplier.DjSupplier;
import com.dangjia.acg.modle.user.MainUser;
import com.dangjia.acg.service.account.MasterAccountFlowRecordService;
import com.dangjia.acg.service.acquisition.MasterCostAcquisitionService;
import com.dangjia.acg.service.complain.ComplainService;
import com.dangjia.acg.service.config.ConfigMessageService;
import com.dangjia.acg.service.product.MasterProductTemplateService;
import com.dangjia.acg.service.product.MasterStorefrontService;
import com.dangjia.acg.util.StringTool;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.*;

/**
 * author: Ronalcheng
 * Date: 2018/12/5 0005
 * Time: 14:30
 * <p>
 * 生成发货单 发货操作类
 */
@Service
public class OrderSplitService {
    private static Logger logger = LoggerFactory.getLogger(OrderSplitService.class);
    @Autowired
    private ISplitDeliverMapper splitDeliverMapper;
    @Autowired
    private IOrderSplitMapper orderSplitMapper;
    @Autowired
    private IOrderSplitItemMapper orderSplitItemMapper;
    @Autowired
    private IHouseMapper houseMapper;
    @Autowired
    private ComplainService complainService;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IWarehouseMapper warehouseMapper;
    @Autowired
    private MasterProductTemplateService  masterProductTemplateService;
    @Autowired
    private IMendOrderMapper mendOrderMapper;
    @Autowired
    private IComplainMapper complainMapper;
    @Autowired
    private ConfigMessageService configMessageService;
    @Autowired
    private MasterAccountFlowRecordService masterAccountFlowRecordService ;
    @Autowired
    private MasterStorefrontService masterStorefrontService;
    @Autowired
    private IWarehouseMapper iWarehouseMapper;
    @Autowired
    private IOrderItemMapper iOrderItemMapper;
    @Autowired
    private MasterCostAcquisitionService masterCostAcquisitionService;
    @Autowired
    private IMasterMemberAddressMapper iMasterMemberAddressMapper;
    @Autowired
    private IQuantityRoomMapper iQuantityRoomMapper;
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private IMasterSupplierMapper iMasterSupplierMapper;

    /**
     * 修改 供应商结算状态
     * id 供应商结算id
     * deliveryFee 配送费用
     * applyMoney   供应商申请结算的价格
     */
    public ServerResponse setSplitDeliver(SplitDeliver splitDeliver) {
        try {
            if (!StringUtils.isNoneBlank(splitDeliver.getId()))
                return ServerResponse.createByErrorMessage("id 不能为null");
            SplitDeliver srcSplitDeliver = splitDeliverMapper.selectByPrimaryKey(splitDeliver.getId());
            if (srcSplitDeliver == null)
                return ServerResponse.createByErrorMessage("无供应商结算单");
            //配送状态（0待发货,1已发待收货,2已收货,3取消,4部分收）
            if (!(srcSplitDeliver.getShippingState() == 2 || srcSplitDeliver.getShippingState() == 4 || srcSplitDeliver.getShippingState() == 6))
                return ServerResponse.createByErrorMessage("当前为未收货状态，不能申请结算");
            Example example = new Example(Complain.class);
            example.createCriteria()
                    .andEqualTo(Complain.COMPLAIN_TYPE, 4)
                    .andEqualTo(Complain.BUSINESS_ID, splitDeliver.getId())
                    .andEqualTo(Complain.STATUS, 0);
            List list = complainMapper.selectByExample(example);
            if (list.size() > 0) {
                return ServerResponse.createByErrorMessage("请勿重复提交申请！");
            }
            srcSplitDeliver.setDeliveryFee(splitDeliver.getDeliveryFee());
//            srcSplitDeliver.setApplyMoney(splitDeliver.getApplyMoney());
            srcSplitDeliver.setApplyState(0);//供应商申请结算的状态0申请中；1不通过；2通过
            splitDeliverMapper.updateByPrimaryKeySelective(srcSplitDeliver);
            return ServerResponse.createBySuccessMessage("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }


    /**
     * 供应商发货
     */
    public ServerResponse sentSplitDeliver(String splitDeliverId) {
        try {
            SplitDeliver splitDeliver = splitDeliverMapper.selectByPrimaryKey(splitDeliverId);
            System.out.println(splitDeliver);
            if (splitDeliver.getShippingState() == 6) {
                return ServerResponse.createBySuccessMessage("材料员已撤回！");
            }
            splitDeliver.setSendTime(new Date());
            splitDeliver.setShippingState(1);//已发待收
            splitDeliverMapper.updateByPrimaryKeySelective(splitDeliver);
//            House house = houseMapper.selectByPrimaryKey(splitDeliver.getHouseId());
//            //业主
//            configMessageService.addConfigMessage(null, AppType.ZHUANGXIU, house.getMemberId(), "0", "供应商发货", String.format
//                    (DjConstants.PushMessage.YZ_F_001, house.getHouseName()), "");
            return ServerResponse.createBySuccessMessage("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }

    /**
     * 工匠拒绝收货，打回供应商待发货
     */
    public ServerResponse rejectionSplitDeliver(String splitDeliverId) {
        try {
            SplitDeliver splitDeliver = splitDeliverMapper.selectByPrimaryKey(splitDeliverId);
            if (splitDeliver.getShippingState() == 0) {
                return ServerResponse.createBySuccessMessage("您已经拒收！");
            }
            splitDeliver.setSendTime(null);
            splitDeliver.setShippingState(0);//已发待收
            splitDeliverMapper.updateByPrimaryKeySelective(splitDeliver);
            return ServerResponse.createBySuccessMessage("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }

    /**
     * 发货任务--货单详情--清单
     * @param splitDeliverId 发货单ID
     */
    public ServerResponse splitDeliverDetail(String splitDeliverId) {
        try {
            String address = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            SplitDeliver splitDeliver = splitDeliverMapper.selectByPrimaryKey(splitDeliverId);
            if(splitDeliver==null){
                return ServerResponse.createByErrorMessage("未找到符合条件的订单");
            }
            //1.获取对应的发货单信息
            SplitDeliverDetailDTO detailDTO = new SplitDeliverDetailDTO();
            BeanUtils.beanToBean(splitDeliver,detailDTO);//数据转抽赋值
            detailDTO.setSplitDeliverId(splitDeliverId);//发货单ID
            detailDTO.setSupMobile(splitDeliver.getSupplierTelephone());
            detailDTO.setSupName(splitDeliver.getSupplierName());
            detailDTO.setImage(splitDeliver.getImage());
            detailDTO.setImageUrl(StringTool.getImage(splitDeliver.getImage(),address));
            QuantityRoom quantityRoom=iQuantityRoomMapper.getQuantityRoom(splitDeliver.getHouseId(),0);
            if(quantityRoom!=null&& StringUtils.isNotBlank(quantityRoom.getFloor())){
                detailDTO.setFloor(quantityRoom.getFloor());//楼层
                detailDTO.setElevator(quantityRoom.getElevator());//是否电梯房1是，0否
            }

            //2.获取对应的发货单明细信息
            List<OrderSplitItemDTO> orderSplitItemList=orderSplitItemMapper.getSplitOrderItemBySplitOrderId(splitDeliver.getOrderSplitId(),splitDeliverId);
            if(orderSplitItemList!=null&&orderSplitItemList.size()>0){
                for (OrderSplitItemDTO sd:orderSplitItemList){
                    //2.1查询当前订单对应的购买总量，已要货量
                   sd.setShopCount(sd.getNum());//购买量
                    if(StringUtils.isNotBlank(sd.getImage())){
                        sd.setImageUrl(StringTool.getImageSingle(sd.getImage(),address));
                    }
                    if(detailDTO.getShippingState()==4&&(detailDTO.getComplainStatus()!=null&&detailDTO.getComplainStatus()==3)){
                        sd.setSupTotalPrice(MathUtil.mul(sd.getSupCost(),sd.getNum()));//供应商品总价
                        sd.setTotalPrice(MathUtil.mul(sd.getPrice(),sd.getNum()));//销售商品总价
                    }else if(sd.getReceive()!=null){
                        sd.setSupTotalPrice(MathUtil.mul(sd.getSupCost(),sd.getReceive()));//供应商品总价
                        sd.setTotalPrice(MathUtil.mul(sd.getPrice(),sd.getReceive()));//销售商品总价
                    }else{
                        sd.setSupTotalPrice(MathUtil.mul(sd.getSupCost(),sd.getNum()));//供应商品总价
                        sd.setTotalPrice(MathUtil.mul(sd.getPrice(),sd.getNum()));//销售商品总价
                    }
                    //查询供应商的搬运费
                    Double porterage=orderSplitItemMapper.getSupPorterage(splitDeliver.getStorefrontId(),splitDeliver.getSupplierId(),sd.getProductId());
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
            detailDTO.setSize(orderSplitItemList.size());
            detailDTO.setApplyMoney(splitDeliver.getApplyMoney());
            detailDTO.setTotalAmount(splitDeliver.getTotalAmount());
            detailDTO.setTotalPrice(splitDeliver.getTotalPrice());
            detailDTO.setStevedorageCost(splitDeliver.getStevedorageCost());
            detailDTO.setDeliveryFee(splitDeliver.getDeliveryFee());
            DjSupplier djSupplier=iMasterSupplierMapper.selectByPrimaryKey(splitDeliver.getSupplierId());
            if(djSupplier!=null){
                detailDTO.setIsNonPlatformSupplier(djSupplier.getIsNonPlatformSupperlier().toString());//是否非平台供应商
            }else{
                detailDTO.setIsNonPlatformSupplier("0");//是否非平台供应商
            }

            detailDTO.setOrderSplitItemList(orderSplitItemList);
            return ServerResponse.createBySuccess("查询成功", detailDTO);
        } catch (Exception e) {
            logger.error("查询失败",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 供应商端发货列表
     * shipState  0待发货,1已发待收货,2已收货,3取消,4部分收,5已结算
     */
    public ServerResponse splitDeliverList(String supplierId, int shipState) {
        Example example = new Example(SplitDeliver.class);
        if (shipState == 2) {
            example.createCriteria().andEqualTo(SplitDeliver.SUPPLIER_ID, supplierId)
                    .andCondition(" shipping_state in(2,4) ");
            example.orderBy(SplitDeliver.CREATE_DATE).desc();
            example.orderBy(SplitDeliver.APPLY_STATE).asc();
        } else {
            example.createCriteria().andEqualTo(SplitDeliver.SUPPLIER_ID, supplierId)
                    .andEqualTo(SplitDeliver.SHIPPING_STATE, shipState);
            example.orderBy(SplitDeliver.CREATE_DATE).desc();
            example.orderBy(SplitDeliver.APPLY_STATE).asc();
        }
        List<SplitDeliver> splitDeliverList = splitDeliverMapper.selectByExample(example);
        for (SplitDeliver splitDeliver : splitDeliverList) {
            System.out.println(splitDeliver);
        }
        return ServerResponse.createBySuccess("查询成功", splitDeliverList);
    }


    /**
     * 撤回供应商待发货的订单（整单撤回）
     */
    public ServerResponse withdrawSupplier(String orderSplitId) {
        try {
            //将发货单设置为撤回状态
            SplitDeliver splitDeliver=splitDeliverMapper.selectByPrimaryKey(orderSplitId);
            if (splitDeliver.getShippingState()==1) {
                return ServerResponse.createBySuccessMessage("供应商已发货！");
            }
            splitDeliver.setShippingState(6);
            splitDeliverMapper.updateByPrimaryKeySelective(splitDeliver);
            return ServerResponse.createBySuccessMessage("撤回成功");
        } catch (Exception e) {
            logger.error("撤回失败!",e);
            return ServerResponse.createByErrorMessage("撤回失败");
        }
    }

    /**
     *发货任务--分给给不同的供应商
     * @param orderSplitId 要货单ID
     * @param splitDeliverId 发货单ID(重新发货时为必填）
     * @param splitItemList [{id:”aa”,supplierId:”xx”},{id:”bb”,supplierId:”xx”}] 分发明细 id要货单明细ID，supplierId 供应商ID
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse sentSupplier(String orderSplitId,String splitDeliverId, String splitItemList) {
            OrderSplit orderSplit = orderSplitMapper.selectByPrimaryKey(orderSplitId);
            //查发货单号为空，则不能重复分开
            if(StringUtils.isBlank(splitDeliverId)&&orderSplit!=null&&orderSplit.getApplyStatus()==2){
                return ServerResponse.createByErrorMessage("此要货单已分发过供应商，请勿重复操作");
            }
            if(splitItemList==null){
                return ServerResponse.createByErrorMessage("发货商品列表不能为空");
            }
            //1.修改供应商，将供应商ID保存到要货单详情中去
            JSONArray itemList = JSONArray.parseArray(splitItemList);
            for (int i = 0; i < itemList.size(); i++) {
                JSONObject obj = itemList.getJSONObject(i);
                String id = obj.getString("id");//发货单详情ID
                String supplierId = obj.getString("supplierId");
                OrderSplitItem orderSplitItem=orderSplitItemMapper.selectByPrimaryKey(id);
                //查询当前商品的供应价格
                SupplierProduct supplierProduct=orderSplitItemMapper.getsupplierProductById(orderSplit.getStorefrontId(),supplierId,orderSplitItem.getProductId());
                if(supplierProduct!=null&&supplierProduct.getPrice()!=null){
                    orderSplitItem.setSupCost(supplierProduct.getPrice());//供应单价费用
                    if(supplierProduct.getPorterage()!=null&&supplierProduct.getPorterage()>0){//需要收取搬运费(供应商的搬运费)
                       Double supTransCost= masterCostAcquisitionService.getSupStevedorageCost(orderSplit.getHouseId(),supplierProduct.getIsCartagePrice(),supplierProduct.getPorterage(),orderSplitItem.getNum());
                       orderSplitItem.setSupStevedorageCost(supTransCost);//供应商的搬运费
                    }else{
                        orderSplitItem.setSupStevedorageCost(0d);
                    }
                }else{
                    orderSplitItem.setSupCost(0d);//如果是非平台供应商，则供应价设为0
                    orderSplitItem.setSupStevedorageCost(0d);//供应商搬动费为0
                }
                String orderItemId=orderSplitItem.getOrderItemId();
                if(orderItemId!=null){//计算运费，搬运费
                    OrderItem orderItem=iOrderItemMapper.selectByPrimaryKey(orderItemId);
                    Double transportationCost=orderItem.getTransportationCost();//运费
                    Double stevedorageCost=orderItem.getStevedorageCost();//搬运费
                    //计算运费
                    if(transportationCost>0.0) {//（运费/总数量）*收货量
                        orderSplitItem.setTransportationCost(MathUtil.mul(MathUtil.div(transportationCost,orderItem.getShopCount()),orderSplitItem.getNum()));
                    }else{
                        orderSplitItem.setTransportationCost(0d);
                    }
                    //计算搬运费
                    if(stevedorageCost>0.0){//（搬运费/总数量）*收货量
                        orderSplitItem.setStevedorageCost(MathUtil.mul(MathUtil.div(stevedorageCost,orderItem.getShopCount()),orderSplitItem.getNum()));
                    }else{
                        orderSplitItem.setStevedorageCost(0d);
                    }
                    //计算搬运费
                    if(orderItem.getDiscountPrice()>0.0){//优惠价
                        orderSplitItem.setDiscountPrice(MathUtil.mul(MathUtil.div(orderItem.getDiscountPrice(),orderItem.getShopCount()),orderSplitItem.getNum()));
                    }else{
                        orderSplitItem.setDiscountPrice(0d);
                    }
                }
                orderSplitItem.setSupplierId(supplierId);
                orderSplitItem.setModifyDate(new Date());
                orderSplitItemMapper.updateByPrimaryKeySelective(orderSplitItem);
            }
            Map resultMap=new HashMap();
            Double sumTotalAmount=0d;//销售总额
            Double sumApplyMoney=0d;//成本总额
            //2.根据发货单ID，查询符合条件需生成发货单的列表(若为重新发货，则splitDeliverId不为空)
            List<Map<String,Object>> supItemList=orderSplitItemMapper.selectSupListBySplitId(orderSplitId,splitDeliverId);
            if(supItemList!=null&&supItemList.size()>0){
               for(Map<String,Object> param:supItemList){
                   Double totalAmount=(Double)param.get("totalAmount");
                   Double applyMoney=(Double)param.get("applyMoney");
                   sumTotalAmount=MathUtil.add(sumTotalAmount,totalAmount);
                   sumApplyMoney=MathUtil.add(sumApplyMoney,applyMoney);
               }
            }
            resultMap.put("sumTotalAmount",sumTotalAmount);
            resultMap.put("sumApplyMoney",sumApplyMoney);
            resultMap.put("supItemList",supItemList);
            return ServerResponse.createBySuccess("查询成功",resultMap);

    }

    /**
     *发货任务--生成发货单
     * @param orderSplitId 要货单ID
     * @param splitDeliverId 发货单ID(重新发货时为必填）
     * @param cityId 城市ID
     * @param userId 用户ID
     * @param installName 安装人姓名
     * @param installMobile 安装人电话
     * @param deliveryName
     * @param deliveryMobile 送货人电话
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse saveSentSupplier(String orderSplitId,String splitDeliverId, String cityId,String userId,String installName,
                                       String installMobile, String deliveryName, String deliveryMobile) {
        OrderSplit orderSplit = orderSplitMapper.selectByPrimaryKey(orderSplitId);
        //查发货单号为空，则不能重复分开
        if(StringUtils.isBlank(splitDeliverId)&&orderSplit!=null&&orderSplit.getApplyStatus()==2){
            return ServerResponse.createByErrorMessage("此要货单已分发过供应商，请勿重复操作");
        }
        //2.根据发货单ID，查询符合条件需生成发货单的列表(若为重新发货，则splitDeliverId不为空)
        List<Map<String,Object>> supItemList=orderSplitItemMapper.selectSupListBySplitId(orderSplitId,splitDeliverId);
        if(supItemList!=null){
            MemberAddress memberAddress=iMasterMemberAddressMapper.selectByPrimaryKey(orderSplit.getAddressId());
            SplitDeliver splitDeliver;
            Example example;
            //3.生成发货单信息
            for(Map<String,Object> supMap:supItemList){
                String supplierId=(String)supMap.get("supplierId");//供应商ID
                String isNonPlatformSupplier=(String)supMap.get("isNonPlatformSupplier");//是否非平台供应商，1是，0否
                Double supTotalPrice=(Double)supMap.get("supTotalPrice");//供应商品总额
                Double supStevedorageCost=(Double)supMap.get("supStevedorageCost");//搬运费
                String supName=(String)supMap.get("supName");//供应商名称
                String telephone=(String)supMap.get("telephone");//电话
                String isDeliveryInstall=(String)supMap.get("isDeliveryInstall");//发货与安装/施工分开(1是，0否）
                /*Double supTransportationCost=((BigDecimal)supMap.get("supTransportationCost")).doubleValue();//每单收取运费,供应商
                Double totalPrice=(Double)supMap.get("totalPrice");//销售商品总额
                Double totalTransportationCost=(Double)supMap.get("totalTransportationCost");//销售商品运费
                Double totalStevedorageCost=(Double)supMap.get("totalStevedorageCost");//销售商品搬运费*/
                Double totalAmount=(Double)supMap.get("totalAmount");//销售总额
                Double applyMoney=(Double)supMap.get("applyMoney");//销售总额
                //生成发货单信息
                example = new Example(SplitDeliver.class);
                splitDeliver = new SplitDeliver();
                splitDeliver.setNumber(orderSplit.getNumber() + "00" + splitDeliverMapper.selectCountByExample(example));//发货单号
                splitDeliver.setHouseId(orderSplit.getHouseId());
                splitDeliver.setOrderSplitId(orderSplitId);
                splitDeliver.setTotalAmount(totalAmount);//订单销售总额，包含运费搬运费-优惠价
                splitDeliver.setApplyMoney(applyMoney);//供应商供应总额，包含运费搬运费
                splitDeliver.setTotalPrice(supTotalPrice);//供应商供应商品总额
                splitDeliver.setStevedorageCost(supStevedorageCost);//供应商总搬运费
                splitDeliver.setDeliveryFee(supStevedorageCost);//供应商总运费
                splitDeliver.setShipName(orderSplit.getMemberName());
                splitDeliver.setShipMobile(orderSplit.getMobile());
                splitDeliver.setAddressId(orderSplit.getAddressId());
                if(memberAddress!=null){
                    splitDeliver.setShipAddress(memberAddress.getName());
                }
                splitDeliver.setSupplierId(supplierId);//供应商id
                splitDeliver.setSupplierTelephone(telephone);//供应商联系电话
                splitDeliver.setSupplierName(supName);//供应商供应商名称
                splitDeliver.setSubmitTime(new Date());
                splitDeliver.setShippingState(0);//待发货状态
                splitDeliver.setCityId(cityId);//城市id
                splitDeliver.setStorefrontId(orderSplit.getStorefrontId());//店铺id
                //判断是非平台供应商
                if (isNonPlatformSupplier!=null&&"1".equals(isNonPlatformSupplier)) {
                    splitDeliver.setDeliveryName(deliveryName);//送货人姓名
                    splitDeliver.setDeliveryMobile(deliveryMobile);//送货人号码
                    splitDeliver.setShippingState(1);//已发货
                    splitDeliver.setSendTime(new Date());//发货时间
                }
                //判断是当前供应商下是否有发货与安装/施工分开的商品，如果是，则添加安装人员
                if(isDeliveryInstall!=null&&"1".equals(isDeliveryInstall)){
                    splitDeliver.setInstallMobile(installName);
                    splitDeliver.setInstallName(installMobile);
                }
                splitDeliverMapper.insert(splitDeliver);//生成发货单
                //4.维护对应的发货单号到发货单明细中去
                orderSplitItemMapper.updateSplitDeliverIdByInfo(splitDeliver.getId(),orderSplitId,supplierId,splitDeliverId);
            }
        }
        //5.修改要货单的发货状态为已发送给供应商
        orderSplit.setApplyStatus(2);//2通过(发给供应商)
        orderSplit.setModifyDate(new Date());
        orderSplitMapper.updateByPrimaryKeySelective(orderSplit);//修改要货单信息

        OrderSplit orderSplitParent=orderSplitMapper.selectByPrimaryKey(orderSplit.getSplitParentId());
        if(orderSplitParent!=null&&orderSplitParent.getApplyStatus()==1){
            orderSplitParent.setApplyStatus(2);
            orderSplitParent.setModifyDate(new Date());
            orderSplitMapper.updateByPrimaryKeySelective(orderSplitParent);
        }

        if(StringUtils.isNotBlank(splitDeliverId)){//如果发货单ID不为空，则删除旧的发货单
            splitDeliverMapper.deleteByPrimaryKey(splitDeliverId);
        }
        return ServerResponse.createBySuccessMessage("发货成功");
    }

    /**
     * 上门安装
     * @param splitDeliverId 发货单ID
     * @param installName 安装人姓名
     * @param installMobile 安装人电话
     * @return
     */
    public ServerResponse saveInstallInfo(String splitDeliverId,String installName,String installMobile){
        try{
            SplitDeliver splitDeliver=splitDeliverMapper.selectByPrimaryKey(splitDeliverId);
            if(splitDeliver==null){
                return ServerResponse.createByErrorMessage("未找到符合条件的发货单信息");
            }
            splitDeliver.setInstallName(installName);
            splitDeliver.setInstallMobile(installMobile);
            splitDeliver.setModifyDate(new Date());
            splitDeliverMapper.updateByPrimaryKey(splitDeliver);
            return ServerResponse.createBySuccessMessage("保存成功");
        }catch(Exception e){
            logger.error("保存失败",e);
            return ServerResponse.createByErrorMessage("保存失败");
        }
    }

    /**
     * 修改对应的订单总额
     * @param splitDeliver
     * @param type 1按发货算，2按部分收货算
     */
    private  Double updateSplitOrderInfo(SplitDeliver splitDeliver,Integer type){
        Double totalAmount=0d;//计算店铺收货时可得钱
        Double applyMoney=0d;//供应商可得钱
        Double totalPrice=0d;//供应商品总额
        Double totalStevedorageCost=0d;//供应搬运费
        Example example=new Example(OrderSplitItem.class);
        example.createCriteria().andEqualTo(OrderSplitItem.ORDER_SPLIT_ID,splitDeliver.getOrderSplitId())
                .andEqualTo(OrderSplitItem.SPLIT_DELIVER_ID,splitDeliver.getId());
        List<OrderSplitItem> itemList=orderSplitItemMapper.selectByExample(example);//获取对应的发货单明细信息

        Double number=0d;
        Double totalReceiverNum=orderSplitItemMapper.getOrderSplitReceiverNum(splitDeliver.getOrderSplitId(),type);//查询当前收货单下的总收货量
        for(OrderSplitItem orderSplitItem:itemList){
            Double countNum=MathUtil.sub(orderSplitItem.getNum(),orderSplitItem.getReceive());
            //若发货数据大于你好货数据，则为部分收货
            number=orderSplitItem.getNum();//发货量
            if(type==2){
                number=orderSplitItem.getReceive();//收货量
            }
            //1.还原业主仓库，当前已要货数-未要货数
            //业主仓库数量加减
            example = new Example(Warehouse.class);
            example.createCriteria()
                    .andEqualTo(Warehouse.PRODUCT_ID, orderSplitItem.getProductId())
                    .andEqualTo(Warehouse.HOUSE_ID, orderSplitItem.getHouseId());
            Warehouse warehouse = iWarehouseMapper.selectOneByExample(example);
            if(type==2&&countNum>0){//按收货量算
                warehouse.setAskCount(MathUtil.sub(warehouse.getAskCount(),countNum));//还原未收货的数量
            }
            warehouse.setReceive(warehouse.getReceive() + number);
            warehouse.setAskTime(warehouse.getAskTime() + 1);//更新要货次数
            warehouse.setModifyDate(new Date());
            iWarehouseMapper.updateByPrimaryKeySelective(warehouse);
            //2.将对应订单的已要货量-未收货的量
            String orderItemId=orderSplitItem.getOrderItemId();
            if(orderItemId!=null){
                OrderItem orderItem=iOrderItemMapper.selectByPrimaryKey(orderItemId);
                if(orderItem!=null){
                    orderItem.setAskCount(MathUtil.sub(orderItem.getAskCount(),countNum));//还原未要货量
                    orderItem.setModifyDate(new Date());
                    if(orderItem.getReturnCount()==null)
                    orderItem.setReceiveCount(0d);
                    if(orderItem.getReturnCount()<orderItem.getAskCount()){
                        orderItem.setReceiveCount(MathUtil.add(orderItem.getReceiveCount(),number));
                    }
                    iOrderItemMapper.updateByPrimaryKeySelective(orderItem);//修改订单明细的要货量
                    //重新计算店铺的运费，搬运费
                    Double transportationCost=orderItem.getTransportationCost();//运费
                    Double stevedorageCost=orderItem.getStevedorageCost();//搬运费
                    //计算运费
                    if(transportationCost>0.0) {//（运费/总数量）*收货量
                        orderSplitItem.setTransportationCost(MathUtil.mul(MathUtil.div(transportationCost,orderItem.getShopCount()),number));
                    }else{
                        orderSplitItem.setTransportationCost(0d);
                    }
                    //计算搬运费
                    if(stevedorageCost>0.0){//（搬运费/总数量）*收货量
                        orderSplitItem.setStevedorageCost(MathUtil.mul(MathUtil.div(stevedorageCost,orderItem.getShopCount()),number));
                    }else{
                        orderSplitItem.setStevedorageCost(0d);
                    }
                    //计算搬运费
                    if(orderItem.getDiscountPrice()>0.0){//（搬运费/总数量）*收货量
                        orderItem.setDiscountPrice(MathUtil.mul(MathUtil.div(orderItem.getDiscountPrice(),orderItem.getShopCount()),number));
                    }else{
                        orderItem.setDiscountPrice(0d);
                    }
                }
            }

            //重新计算供应商的运费，搬运费
            orderSplitItem.setStevedorageCost(getSupProductStevedorageCost(splitDeliver.getStorefrontId(),splitDeliver.getSupplierId(),orderSplitItem.getProductId(),splitDeliver.getHouseId(),number));
            if(totalReceiverNum>0){
                orderSplitItem.setSupTransportationCost(MathUtil.mul(MathUtil.div(splitDeliver.getDeliveryFee(),totalReceiverNum),number));
            }
            orderSplitItemMapper.updateByPrimaryKeySelective(orderSplitItem);//修改对应的运费，搬运费

            totalAmount=MathUtil.add(totalAmount, MathUtil.add(MathUtil.add(MathUtil.mul(orderSplitItem.getPrice(),orderSplitItem.getReceive()),orderSplitItem.getTransportationCost()),orderSplitItem.getStevedorageCost()));
            totalAmount=MathUtil.sub(totalAmount,orderSplitItem.getDiscountPrice());
            applyMoney=MathUtil.add(applyMoney,MathUtil.add(MathUtil.mul(orderSplitItem.getSupCost(),number),orderSplitItem.getSupStevedorageCost()));
            totalPrice=MathUtil.add(totalPrice,MathUtil.mul(orderSplitItem.getSupCost(),number));
            totalStevedorageCost=MathUtil.add(totalStevedorageCost,orderSplitItem.getSupStevedorageCost());
        }
        applyMoney=MathUtil.add(applyMoney,splitDeliver.getDeliveryFee());
        splitDeliver.setTotalAmount(totalAmount);
        splitDeliver.setApplyMoney(applyMoney);
        splitDeliver.setTotalPrice(totalPrice);
        splitDeliver.setStevedorageCost(totalStevedorageCost);
        splitDeliver.setApplyState(0);
        splitDeliverMapper.updateByPrimaryKeySelective(splitDeliver);//修改对应的订单总额
        return totalAmount;
    }
    /**
     * 部分收货申诉接口
     * @param splitDeliverId 发货单ID
     * @param splitItemList 发货单明细列表
     * @param type 类型：1.认可部分收货，2申请平台申诉,3平台申诉通过,4平台申诉驳回
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse platformComplaint(String splitDeliverId,String splitItemList,Integer type,String userId,Integer applicationStatus){
        SplitDeliver splitDeliver=splitDeliverMapper.selectByPrimaryKey(splitDeliverId);
        if(splitDeliver==null){
            return ServerResponse.createByErrorMessage("未找到对应的订单信息");
        }
        if(splitDeliver.getShippingState()!=4){
            return ServerResponse.createByErrorMessage("只有部分收货的订单才能进行操作");
        }
        if(splitDeliver.getComplainStatus()!=null&&splitDeliver.getComplainStatus()==2){
            return ServerResponse.createByErrorMessage("平台处理中，请勿重复申诉");
        }else if(splitDeliver.getComplainStatus()!=null&&splitDeliver.getComplainStatus()==1&&splitDeliver.getComplainStatus()==3){
            return ServerResponse.createByErrorMessage("该单已处理完成，请勿重复操作");
        }
        MainUser user=userMapper.selectByPrimaryKey(userId);
        //Storefront storefront=masterStorefrontService.getStorefrontById(splitDeliver.getStorefrontId());
        //判断是否为平台申诉
        if(type==2){//2申请平台申诉,添加申诉信息
            if(splitItemList==null){
                return ServerResponse.createByErrorMessage("请选择需要申诉的商品信息");
            }
            //先将旧的申诉商品改为未申诉，重新申诉
            Example example=new Example(OrderSplitItem.class);
            example.createCriteria().andEqualTo(OrderSplitItem.SPLIT_DELIVER_ID,splitDeliverId);
            OrderSplitItem sItem=new OrderSplitItem();
            sItem.setShippingState(0);
            sItem.setId(null);
            sItem.setCreateDate(null);
            orderSplitItemMapper.updateByExampleSelective(sItem,example);
            JSONArray itemList = JSONArray.parseArray(splitItemList);
            for (int i = 0; i < itemList.size(); i++) {
                JSONObject obj = itemList.getJSONObject(i);
                String id = obj.getString("id");//发货单详情ID
                OrderSplitItem orderSplitItem=orderSplitItemMapper.selectByPrimaryKey(id);
                if(orderSplitItem!=null){
                    orderSplitItem.setShippingState(1);//部分收货申诉商品
                    orderSplitItem.setModifyDate(new Date());
                    orderSplitItemMapper.updateByPrimaryKeySelective(orderSplitItem);//修改商品的申诉状态
                }
            }

            //添加申诉记录
            complainService.insertUserComplain(user.getUsername(),user.getMobile(),userId
            ,splitDeliverId,splitDeliver.getHouseId(),4,"部分收货申诉",null,applicationStatus);//供应商申请
            //修改申诉次数及记录
            splitDeliver.setComplainCount(splitDeliver.getComplainCount()+1);//申诉资数

        }else if(type==1){//1.认可部分收货,将未收货数量还原到业主仓库上，及对应的订单明细上去
            Double totalAmount=updateSplitOrderInfo(splitDeliver,2);//按部分收货算
            //3.将当前订单所得钱给到对应的店铺
            if(StringUtils.isNotBlank(splitDeliver.getStorefrontId())){
                masterAccountFlowRecordService.updateStoreAccountMoney(splitDeliver.getStorefrontId(), splitDeliver.getHouseId(),
                        0, splitDeliver.getId(), totalAmount,"认可部分收货流水记录", userId);
            }

        }else if(type==3){//平台审核通过
            Double totalAmount=updateSplitOrderInfo(splitDeliver,1);//按全部收货算
            if(StringUtils.isNotBlank(splitDeliver.getStorefrontId())){
                //3.将当前订单所得钱给到对应的店铺
                masterAccountFlowRecordService.updateStoreAccountMoney(splitDeliver.getStorefrontId(), splitDeliver.getHouseId(),
                        0, splitDeliver.getId(), totalAmount,"申诉部分收货审核通过流水记录", userId);
            }
        }
        //修改申诉状态
        splitDeliver.setComplainStatus(type);
        splitDeliver.setModifyDate(new Date());
        splitDeliverMapper.updateByPrimaryKeySelective(splitDeliver);//修改申诉状态
        return ServerResponse.createBySuccessMessage("保存成功");
    }

    //获取供应商的搬运费
    public Double getSupProductStevedorageCost(String storefrontId,String supId,String productId,String houseId,Double number){
        Double supTransCost=0d;
        SupplierProduct supplierProduct=orderSplitItemMapper.getsupplierProductById(storefrontId,supId,productId);
        if(supplierProduct!=null&&supplierProduct.getPrice()!=null){
            if(supplierProduct.getPorterage()!=null&&supplierProduct.getPorterage()>0){//需要收取搬运费(供应商的搬运费)
               supTransCost= masterCostAcquisitionService.getSupStevedorageCost(houseId,supplierProduct.getIsCartagePrice(),supplierProduct.getPorterage(),number);
            }
        }
        return supTransCost;
    }

    /**
     * 取消(打回)
     * 返回数量
     */
    public ServerResponse cancelOrderSplit(String orderSplitId) {
        try {
            OrderSplit orderSplit = orderSplitMapper.selectByPrimaryKey(orderSplitId);
            Example example = new Example(OrderSplitItem.class);
            example.createCriteria().andEqualTo(OrderSplitItem.ORDER_SPLIT_ID, orderSplit.getId());
            List<OrderSplitItem> orderSplitItemList = orderSplitItemMapper.selectByExample(example);
            for (OrderSplitItem orderSplitItem : orderSplitItemList) {
                Warehouse warehouse = warehouseMapper.getByProductId(orderSplitItem.getProductId(), orderSplit.getHouseId());
                if (warehouse != null) {
                    warehouse.setAskCount(warehouse.getAskCount() - orderSplitItem.getNum());
                    warehouseMapper.updateByPrimaryKeySelective(warehouse);
                }
            }

            orderSplit.setApplyStatus(3);
            orderSplitMapper.updateByPrimaryKey(orderSplit);

            if (!CommonUtil.isEmpty(orderSplit.getMendNumber())) {
                MendOrder mendOrder = mendOrderMapper.selectByPrimaryKey(orderSplit.getMendNumber());
                mendOrder.setState(2);//不通过取消
                mendOrderMapper.updateByPrimaryKeySelective(mendOrder);
            }
            return ServerResponse.createBySuccessMessage("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }

    /**
     * 撤回发货单
     */
    public ServerResponse cancelSplitDeliver(String splitDeliverId) {
        try {
            //将发货单设置为撤回状态
            SplitDeliver splitDeliver=splitDeliverMapper.selectByPrimaryKey(splitDeliverId);
            if(splitDeliver==null){
                return ServerResponse.createByErrorMessage("未找到对应的发货单信息");
            }
            if (splitDeliver!=null&&splitDeliver.getShippingState()==0) {
                splitDeliver.setShippingState(6);//店铺撤回发货单
                splitDeliver.setModifyDate(new Date());
                splitDeliverMapper.updateByPrimaryKeySelective(splitDeliver);
            }else{
                return ServerResponse.createByErrorMessage("只有待发货状态才可撤回");
            }
            return ServerResponse.createBySuccessMessage("撤回成功");
        } catch (Exception e) {
            logger.error("撤回失败",e);
            return ServerResponse.createByErrorMessage("撤回失败");
        }
    }

    /**
     * 发货任务--货单列表--货单详情列表
     */
    public ServerResponse getOrderSplitDeliverList(String orderSplitId){

        try{
            Map<String,Object> resultMap=new HashMap();
            //1.查询对应的要货单号，判断是否已分发过供应商，若已分发，则返回给提示
            OrderSplit orderSplit=orderSplitMapper.selectByPrimaryKey(orderSplitId);
            resultMap.put("memberId",orderSplit.getMemberId());//要货人ID
            resultMap.put("memberName",orderSplit.getMemberName());//要货人姓名
            resultMap.put("mobile",orderSplit.getMobile());//要货人联系方式
            resultMap.put("orderSplitId",orderSplitId);//要货单ID
            resultMap.put("isReservationDeliver",orderSplit.getIsReservationDeliver());//是否需要预约发货（1是，0否）
            resultMap.put("reservationDeliverTime",orderSplit.getReservationDeliverTime());//预约发货时间

            List list=new ArrayList();
            Example example=new Example(SplitDeliver.class);
            example.createCriteria().andEqualTo(SplitDeliver.ORDER_SPLIT_ID,orderSplitId);
            List<SplitDeliver> splitDeliverList=splitDeliverMapper.selectByExample(example);
            if(splitDeliverList!=null){
                for(SplitDeliver sd:splitDeliverList){
                    //增加是否发货与安装分开的字段
                    String isDeliveryInstall=orderSplitItemMapper.selectIsDeliveryInstall(sd.getId());
                    sd.setIsDeliveryInstall(isDeliveryInstall);
                    DjSupplier supplier=iMasterSupplierMapper.selectByPrimaryKey(sd.getSupplierId());
                    if(supplier!=null){
                        sd.setIsNonPlatformSupperlier(supplier.getIsNonPlatformSupperlier());//是否非平台供应商 1是，0否
                    }
                    list.add(sd);
                }
            }
            resultMap.put("splitDeliverList",list);

            return ServerResponse.createBySuccess("查询成功",resultMap);

        } catch (Exception e) {
            logger.error("查询失败",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }
    /**
     * 发货任务--货单列表--分发任务、重新发货页面
     * @param orderSplitId 要货单ID
     * @param splitDeliverId 发货单ID
     */
    public ServerResponse orderSplitItemList(String orderSplitId,String splitDeliverId) {
        try {
            Map<String,Object> resultMap=new HashMap();
            //1.查询对应的要货单号，判断是否已分发过供应商，若已分发，则返回给提示
            OrderSplit orderSplit=orderSplitMapper.selectByPrimaryKey(orderSplitId);
            //查发货单号为空，则不能重复分开
            if(StringUtils.isBlank(splitDeliverId)&&orderSplit!=null&&orderSplit.getApplyStatus()==2){
                return ServerResponse.createByErrorMessage("此要货单已分发过供应商，请勿重复操作");
            }
            resultMap.put("memberId",orderSplit.getMemberId());//要货人ID
            resultMap.put("memberName",orderSplit.getMemberName());//要货人姓名
            resultMap.put("mobile",orderSplit.getMobile());//要货人联系方式
            resultMap.put("orderSplitId",orderSplitId);//要货单ID
            resultMap.put("number",orderSplit.getNumber());//要货单号
            resultMap.put("splitDeliverId",splitDeliverId);//发货单ID
            resultMap.put("isReservationDeliver",orderSplit.getIsReservationDeliver());//是否需要预约发货（1是，0否）
            resultMap.put("reservationDeliverTime",orderSplit.getReservationDeliverTime());//预约发货时间

            String isDeliveryInstall="0";//发货与安装/施工分开，1是，0否
            //2.查询对应的需要分发的要货单明细
            List<OrderSplitItemDTO> orderSplitItemList=orderSplitItemMapper.getSplitOrderItemBySplitOrderId(orderSplitId,splitDeliverId);
            if(orderSplitItemList!=null&&orderSplitItemList.size()>0){
                String address = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
                for (OrderSplitItemDTO sd:orderSplitItemList){
                  //2.1查询当前订单对应的购买总量，已要货量
                    Map<String,Object> countItemMap=iOrderItemMapper.searchCountItemByInfo(orderSplit.getStorefrontId(),orderSplit.getAddressId(),orderSplit.getHouseId(),sd.getProductId());
                    sd.setShopCount((Double)countItemMap.get("shopCount"));//购买总数
                    sd.setAskCount((Double)countItemMap.get("askCount"));//已要货总数
                    sd.setImageUrl(StringTool.getImageSingle(sd.getImage(),address));
                    //2.2查询当前商品对应的供应商，及销售总价(对此店铺供过货的供应商)
                    List<Map<String,Object>> supplierIdlist = splitDeliverMapper.getsupplierByProduct(orderSplit.getStorefrontId(),sd.getProductId());
                    if(supplierIdlist==null||supplierIdlist.size()<=0){
                        //若未查到线上可发货的供应商，则查询非平台供应商给到页面选择
                        supplierIdlist=splitDeliverMapper.queryNonPlatformSupplier();
                    }
                    sd.setSupplierIdlist(supplierIdlist);
                    if(sd.getIsDeliveryInstall()!=null&&"1".equals(sd.getIsDeliveryInstall())){//如果有施式与安装分开的商品，则为是
                        isDeliveryInstall="1";
                    }
                }
            }
            resultMap.put("isDeliveryInstall",isDeliveryInstall);//发货与安装/施工分开
            resultMap.put("orderSplitItemList",orderSplitItemList);//要货单明细表
            //查询房子信息，获取房子对应的楼层
            QuantityRoom quantityRoom=iQuantityRoomMapper.getQuantityRoom(orderSplit.getHouseId(),0);
            if(quantityRoom!=null&& StringUtils.isNotBlank(quantityRoom.getFloor())){
                resultMap.put("floor",quantityRoom.getFloor());//楼层
                resultMap.put("elevator",quantityRoom.getElevator());//是否电梯房
            }
            return ServerResponse.createBySuccess("查询成功", resultMap);
        } catch (Exception e) {
           logger.error("查询失败",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 材料员看房子列表
     */
    public ServerResponse getHouseList(String userId,String cityId,PageDTO pageDTO, String likeAddress,String startDate, String endDate) {
        try {
            //通过缓存查询店铺信息
           Storefront storefront= masterStorefrontService.getStorefrontByUserId(userId,cityId);
            if(storefront==null)
            {
                return ServerResponse.createByErrorMessage("不存在店铺信息，请先维护店铺信息");
            }
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
//        List<House> houseList = houseMapper.selectAll();
            List<DeliverHouseDTO> deliverHouseDTOList = houseMapper.getHouseAddrssByAddress(storefront.getId(),cityId,likeAddress,startDate,endDate);
            PageInfo pageResult = new PageInfo(deliverHouseDTOList);
            pageResult.setList(deliverHouseDTOList);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            logger.error("查询失败：",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 根据收货地址，查询对应的要货单列表
     */
    public ServerResponse getOrderSplitList(String userId,String cityId,PageDTO pageDTO,String addressId,String houseId,String storefrontId) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<OrderSplitDTO> orderSplistList=orderSplitMapper.searchOrderSplistByAddressId(addressId,houseId,storefrontId);
            PageInfo pageResult = new PageInfo(orderSplistList);
            pageResult.setList(orderSplistList);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            logger.error("查询失败",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }
}
