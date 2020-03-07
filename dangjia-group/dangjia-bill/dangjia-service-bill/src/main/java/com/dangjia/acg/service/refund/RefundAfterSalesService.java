package com.dangjia.acg.service.refund;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.api.app.member.MemberAPI;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.common.util.MathUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.refund.*;
import com.dangjia.acg.mapper.config.IBillComplainMapper;
import com.dangjia.acg.mapper.config.IBillConfigMapper;
import com.dangjia.acg.mapper.delivery.BillDjDeliverOrderSplitItemMapper;
import com.dangjia.acg.mapper.delivery.BillDjDeliverOrderSplitMapper;
import com.dangjia.acg.mapper.delivery.IBillDjDeliverOrderItemMapper;
import com.dangjia.acg.mapper.order.IBillChangeOrderMapper;
import com.dangjia.acg.mapper.order.IBillMendWorkerMapper;
import com.dangjia.acg.mapper.order.IBillOrderProgressMapper;
import com.dangjia.acg.mapper.order.IBillQuantityRoomMapper;
import com.dangjia.acg.mapper.pay.IBillBusinessOrderMapper;
import com.dangjia.acg.mapper.refund.*;
import com.dangjia.acg.mapper.repair.IBillMendDeliverMapper;
import com.dangjia.acg.mapper.sale.IBillMemberMapper;
import com.dangjia.acg.mapper.task.IBillTaskStackMapper;
import com.dangjia.acg.model.Config;
import com.dangjia.acg.modle.brand.Brand;
import com.dangjia.acg.modle.brand.Unit;
import com.dangjia.acg.modle.complain.Complain;
import com.dangjia.acg.modle.deliver.Order;
import com.dangjia.acg.modle.deliver.OrderItem;
import com.dangjia.acg.modle.deliver.OrderSplitItem;
import com.dangjia.acg.modle.design.QuantityRoom;
import com.dangjia.acg.modle.house.TaskStack;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.order.OrderProgress;
import com.dangjia.acg.modle.product.BasicsGoods;
import com.dangjia.acg.modle.product.DjBasicsProductTemplate;
import com.dangjia.acg.modle.repair.ChangeOrder;
import com.dangjia.acg.modle.repair.MendDeliver;
import com.dangjia.acg.modle.repair.MendMateriel;
import com.dangjia.acg.modle.repair.MendOrder;
import com.dangjia.acg.service.product.BillProductTemplateService;
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
 * Created with IntelliJ IDEA.
 * author: fzh
 * Date: 25/10/2019
 * Time: 下午 3:56
 */
@Service
public class RefundAfterSalesService {
    protected static final Logger logger = LoggerFactory.getLogger(RefundAfterSalesService.class);
    @Autowired
    private RefundAfterSalesMapper refundAfterSalesMapper;
    @Autowired
    private IBillUnitMapper iBillUnitMapper;
    @Autowired
    private IBillBrandMapper iBillBrandMapper;
    @Autowired
    private IBillProductTemplateMapper iBillProductTemplateMapper;
    @Autowired
    private IBillBasicsGoodsMapper iBillBasicsGoodsMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private RedisClient redisClient;//缓存
    @Autowired
    private IBillMendOrderMapper iBillMendOrderMapper;
    @Autowired
    private IBillMendMaterialMapper iBillMendMaterialMapper;
    @Autowired
    private IBillMendWorkerMapper iBillMendWorkerMapper;
    @Autowired
    private IBillChangeOrderMapper iBillChangeOrderMapper;
    @Autowired
    private IBillDjDeliverOrderItemMapper iBillDjDeliverOrderItemMapper;
    @Autowired
    private IBillMendDeliverMapper billMendDeliverMapper;
    @Autowired
    private IBillOrderProgressMapper iBillOrderProgressMapper;
    @Autowired
    private IBillConfigMapper iBillConfigMapper;
    @Autowired
    private IBillComplainMapper iBillComplainMapper;
    @Autowired
    private IBillQuantityRoomMapper iBillQuantityRoomMapper;
    @Autowired
    private BillDjDeliverOrderSplitMapper billDjDeliverOrderSplitMapper;
    @Autowired
    private BillDjDeliverOrderSplitItemMapper billDjDeliverOrderSplitItemMapper;
   // @Autowired
  //  private MendRecordAPI mendRecordAPI;
    @Autowired
    private IBillMemberMapper iBillMemberMapper;
    @Autowired
    private BillProductTemplateService billProductTemplateService;
    @Autowired
    private IBillTaskStackMapper iBillTaskStackMapper;
    @Autowired
    private IBillBusinessOrderMapper iBillBusinessOrderMapper;
    @Autowired
    private MemberAPI memberAPI;

    /**
     * 查询可退款的商品
     * @param cityId 城市ID
     * @param houseId 房屋ID
     * @return
     */
    public ServerResponse<PageInfo> queryRefundOnlyOrderList(PageDTO pageDTO,String userToken, String cityId, String houseId, String searchKey){
        try{
            Object object = memberAPI.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            JSONObject job = (JSONObject) object;
            Member member = job.toJavaObject(Member.class);
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            logger.info("queryRefundOrderList查询可退款的商品：city={},houseId{}",cityId,houseId);
            List<RefundOrderDTO> orderlist = refundAfterSalesMapper.queryRefundOrderList(member.getId(),houseId,searchKey);
            if(orderlist!=null&&orderlist.size()>0){
                String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
                for(RefundOrderDTO order:orderlist){
                    String orderId=order.getOrderId();
                    List<RefundOrderItemDTO> orderItemList=refundAfterSalesMapper.queryRefundOrderItemList(orderId,searchKey);
                    getProductList(orderItemList,address);
                    order.setStorefrontIcon(address+order.getStorefrontIcon());
                    order.setOrderDetailList(orderItemList);
                }
            }
            PageInfo pageResult = new PageInfo(orderlist);
            return  ServerResponse.createBySuccess("查询成功",pageResult);
        }catch (Exception e){
            logger.error("queryRefundOrderList查询失败",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }

    }
    /**
     * 查询商品对应的规格详情，品牌，单位信息
     * @param productList
     * @param address
     */
    private  void getProductList(List<RefundOrderItemDTO> productList, String address){
        if(productList!=null&&productList.size()>0){
            for(RefundOrderItemDTO ap:productList){
                setProductInfo(ap,address);
            }
        }
    }
    /**
     * 替换对应的信息
     * @param ap
     * @param address
     */
    private  void setProductInfo(RefundOrderItemDTO ap,String address){
        String productTemplateId=ap.getProductTemplateId();
        DjBasicsProductTemplate pt=iBillProductTemplateMapper.selectByPrimaryKey(productTemplateId);
        if(pt!=null&&StringUtils.isNotBlank(pt.getId())){
            String image=ap.getImage();
            if (image == null) {
                image=pt.getImage();
            }
            ap.setConvertUnit(pt.getConvertUnit());
            ap.setCost(pt.getCost());
            ap.setCategoryId(pt.getCategoryId());
            if(ap.getStorefrontIcon()!=null&&StringUtils.isNotBlank(ap.getStorefrontIcon())){
                ap.setStorefrontIcon(address+ap.getStorefrontIcon());
            }
            //添加图片详情地址字段
            String[] imgArr = image.split(",");
            //StringBuilder imgStr = new StringBuilder();
           // StringBuilder imgUrlStr = new StringBuilder();
           // StringTool.get.getImages(address, imgArr, imgStr, imgUrlStr);
            if(imgArr!=null&&imgArr.length>0){
                ap.setImageUrl(address+imgArr[0]);//图片详情地址设置
            }

            String unitId=pt.getUnitId();
            //查询单位
            if(pt.getConvertQuality()!=null&&pt.getConvertQuality()>0){
                unitId=pt.getConvertUnit();
            }
            if(unitId!=null&& StringUtils.isNotBlank(unitId)){
                Unit unit= iBillUnitMapper.selectByPrimaryKey(unitId);
                ap.setUnitId(unitId);
                ap.setUnitName(unit!=null?unit.getName():"");
                ap.setUnitType(unit!=null?unit.getType():2);
            }

            BasicsGoods goods=iBillBasicsGoodsMapper.selectByPrimaryKey(pt.getGoodsId());
            ap.setProductType(goods.getType().toString());
            if(StringUtils.isNotBlank(goods.getBrandId())){
                Brand brand=iBillBrandMapper.selectByPrimaryKey(goods.getBrandId());
                ap.setBrandId(goods.getId());
                ap.setBrandName(brand!=null?brand.getName():"");
            }
            //查询规格名称
            if (StringUtils.isNotBlank(pt.getValueIdArr())) {
                ap.setValueIdArr(pt.getValueIdArr());
                ap.setValueNameArr(billProductTemplateService.getNewValueNameArr(pt.getValueIdArr()).replaceAll(",", " "));
            }
        }

    }

    /**
     * 申请退款页面，列表展示
     * @param userToken        用户token
     * @param cityId           城市ID
     * @param houseId          房屋ID
     * @param orderProductAttr 需退款商品列表
     * @return
     */
    public ServerResponse queryRefundonlyInfoList(String userToken,String cityId,String houseId,String orderProductAttr){
        try{
            Map<String,Object> map=new HashMap<String,Object>();

            Double actualTotalAmountT=0.0;//退货总额
            Double totalRransportationCostT = 0.0;//可退运费
            Double totalStevedorageCostT = 0.0;//可退搬运费
            Double totalAmountT=0.0;//实退款
            //获取退款商品列表
            List<RefundOrderDTO> orderlist=new ArrayList<>();
            JSONArray orderArrayList=JSONArray.parseArray(orderProductAttr);
            if(orderArrayList!=null&&orderArrayList.size()>0) {
                String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
                for (int i = 0; i < orderArrayList.size(); i++) {
                    JSONObject obj = (JSONObject) orderArrayList.get(i);
                    String orderId = (String) obj.get("orderId");
                    String houseIdNew = (String) obj.get("houseId");
                    if(houseIdNew!=null){
                        houseId=houseIdNew;
                    }
                    //查询房子信息，获取房子对应的楼层
                    QuantityRoom quantityRoom=iBillQuantityRoomMapper.getBillQuantityRoom(houseId,0);
                    Integer elevator= 1;//是否电梯房
                    String floor="1";
                    if(quantityRoom!=null&&StringUtils.isNotBlank(quantityRoom.getId())){
                        elevator=quantityRoom.getElevator();//是否电梯房
                        floor=quantityRoom.getFloor();//楼层
                    }
                    RefundOrderDTO orderInfo = refundAfterSalesMapper.queryRefundOrderInfoById(orderId);
                    Double totalRransportationCost = 0.0;//可退运费
                    Double totalStevedorageCost = 0.0;//可退搬运费
                    Double actualTotalAmount=0.0;//退货总额
                    List<RefundOrderItemDTO> orderItemList=new ArrayList<>();
                    //获取商品信息
                    JSONArray orderItemProductList = obj.getJSONArray("orderItemProductList");
                    for (int j = 0; j < orderItemProductList.size(); j++) {
                        JSONObject productObj = (JSONObject) orderItemProductList.get(j);
                        String orderItemId = (String) productObj.get("orderItemId");//订单详情号
                       // String productId = (String) productObj.get("productId");//产品ID
                        Double returnCount = productObj.getDouble("returnCount");//退货量
                        RefundOrderItemDTO refundOrderItemDTO = refundAfterSalesMapper.queryRefundOrderItemInfo(orderItemId);
                        if (refundOrderItemDTO == null) {
                            return ServerResponse.createByErrorMessage("未找到对应的退货单信息");
                        }
                        refundOrderItemDTO.setSurplusCount(returnCount);
                        setProductInfo(refundOrderItemDTO,address);
                        Double price = refundOrderItemDTO.getPrice();//购买单价
                        Double shopCount=refundOrderItemDTO.getShopCount();//购买数据
                        Double transportationCost=refundOrderItemDTO.getTransportationCost();//运费
                        Double stevedorageCost=refundOrderItemDTO.getStevedorageCost();//搬运费
                        //计算可退运费
                        if(transportationCost>0.0) {
                            Double returnRransportationCost = CommonUtil.getReturnRransportationCost(price, shopCount, returnCount,transportationCost);
                            totalRransportationCost=MathUtil.add(totalRransportationCost,returnRransportationCost);
                        }
                        //计算可退搬费
                        if(stevedorageCost>0.0){
                            String isUpstairsCost=refundOrderItemDTO.getIsUpstairsCost();//是否按1层收取上楼费
                            Double moveCost=refundOrderItemDTO.getMoveCost();//每层搬超级赛亚人费
                            Double returnStevedorageCost=CommonUtil.getReturnStevedorageCost(elevator,floor,isUpstairsCost,moveCost,returnCount);
                            totalStevedorageCost=MathUtil.add(totalStevedorageCost,returnStevedorageCost);
                        }
                        //优惠金额
                        Double discountPrice=0d;
                        if(refundOrderItemDTO.getDiscountPrice()!=null&&refundOrderItemDTO.getDiscountPrice()>0.0){
                            discountPrice=MathUtil.mul(MathUtil.div(refundOrderItemDTO.getDiscountPrice(),refundOrderItemDTO.getShopCount()),returnCount);
                        }
                        actualTotalAmount=MathUtil.add(actualTotalAmount,MathUtil.mul(price,returnCount));
                        actualTotalAmount=MathUtil.sub(actualTotalAmount,discountPrice);
                        orderItemList.add(refundOrderItemDTO);
                    }

                    orderInfo.setTotalRransportationCost(totalRransportationCost);//可退运费
                    orderInfo.setTotalStevedorageCost(totalStevedorageCost);//可退搬运费
                    orderInfo.setOrderDetailList(orderItemList);
                    orderInfo.setActualTotalAmount(actualTotalAmount);//退货总额
                    orderInfo.setTotalAmount(MathUtil.add(MathUtil.add(actualTotalAmount,totalRransportationCost),totalStevedorageCost));//实退款
                    orderInfo.setTotalRransportationCostRemark("运费："+orderInfo.getTotalRransportationCost());//运费描述
                    orderInfo.setTotalStevedorageCostRemark("搬运费："+orderInfo.getTotalStevedorageCost());//搬运费描述
                    //添加对应的信息
                    orderlist.add(orderInfo);
                    actualTotalAmountT=MathUtil.add(actualTotalAmountT,orderInfo.getActualTotalAmount());
                    totalRransportationCostT= MathUtil.add(totalRransportationCostT,orderInfo.getTotalRransportationCost());
                    totalStevedorageCostT=MathUtil.add(totalStevedorageCostT,orderInfo.getTotalStevedorageCost());
                    totalAmountT=MathUtil.add(totalAmountT,orderInfo.getTotalAmount());

                    orderInfo.setStorefrontIcon(address+orderInfo.getStorefrontIcon());
                }

                map.put("actualTotalAmount",actualTotalAmountT);
                map.put("totalRransportationCost",totalRransportationCostT);
                map.put("totalStevedorageCost",totalStevedorageCostT);
                map.put("totalAmount",totalAmountT);
                map.put("orderlist",orderlist);
            }
            return ServerResponse.createBySuccess("查询成功",map);
        }catch (Exception e){
            logger.error("查询失败",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }

    }
    /**
     * 仅退款提交
     * @param userToken  用户token
     * @param cityId 城市ID
     * @param houseId 房屋ID
     * @param orderProductAttr  需退款商品列表
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse saveRefundonlyInfo(String userToken,String cityId,String houseId,String orderProductAttr){
        Object object = getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        Member member = (Member) object;

        MendOrder mendOrder;
        Example example;
        JSONArray orderArrayList=JSONArray.parseArray(orderProductAttr);
        if(orderArrayList!=null&&orderArrayList.size()>0) {
            for (int i = 0; i < orderArrayList.size(); i++) {
                JSONObject obj = (JSONObject) orderArrayList.get(i);
                String orderId = (String) obj.get("orderId");
                String storefrontId = (String) obj.get("storefrontId");
                String houseIdNew = (String) obj.get("houseId");
                if(houseIdNew!=null){
                    houseId=houseIdNew;
                }
                RefundOrderDTO orderInfo = refundAfterSalesMapper.queryRefundOrderInfoById(orderId);
                //查询房子信息，获取房子对应的楼层
                QuantityRoom quantityRoom=iBillQuantityRoomMapper.getBillQuantityRoom(houseId,0);
                Integer elevator= 1;//是否电梯房
                String floor="1";
                if(quantityRoom!=null&&StringUtils.isNotBlank(quantityRoom.getId())){
                    elevator=quantityRoom.getElevator();//是否电梯房
                    floor=quantityRoom.getFloor();//楼层
                }
                example = new Example(MendOrder.class);
                mendOrder = new MendOrder();
                mendOrder.setNumber("DJZX" + 40000 + iBillMendOrderMapper.selectCountByExample(example));//订单号
                mendOrder.setHouseId(orderInfo.getHouseId());
                mendOrder.setAddressId(orderInfo.getAddressId());
                mendOrder.setApplyMemberId(member.getId());
                mendOrder.setType(4);//业主退材料
                mendOrder.setOrderName("业主退材料退款");
                mendOrder.setState(0);//生成中
                mendOrder.setStorefrontId(orderInfo.getStorefrontId());
                mendOrder.setOrderId(orderId);
                Double totalRransportationCost = 0.0;//可退运费
                Double totalStevedorageCost = 0.0;//可退搬运费
                Double actualTotalAmount=0.0;//退货总额
                //获取商品信息
                JSONArray orderItemProductList = obj.getJSONArray("orderItemProductList");
                for (int j = 0; j < orderItemProductList.size(); j++) {
                    JSONObject productObj = (JSONObject) orderItemProductList.get(j);
                    String orderItemId=(String)productObj.get("orderItemId");//订单详情号
                    String productId=(String)productObj.get("productId");//产品ID
                    Double returnCount=productObj.getDouble("returnCount");//退货量
                    RefundOrderItemDTO refundOrderItemDTO=refundAfterSalesMapper.queryRefundOrderItemInfo(orderItemId);
                    if(refundOrderItemDTO==null){
                        return ServerResponse.createByErrorMessage("未找到对应的退货单信息");
                    }
                    setProductInfo(refundOrderItemDTO,address);
                    Double surplusCount=refundOrderItemDTO.getSurplusCount();
                    logger.info("退货量{}，可退量{}",returnCount,surplusCount);
                    if(MathUtil.sub(surplusCount,returnCount)<0){
                        return ServerResponse.createByErrorMessage("退货量大于可退货量，不能退。");
                    }
                    refundOrderItemDTO.setReturnCount(returnCount);
                    //修改订单中的退货量为当前退货的量
                    OrderItem orderItem=iBillDjDeliverOrderItemMapper.selectByPrimaryKey(refundOrderItemDTO.getOrderItemId());
                    if(orderItem.getReturnCount()==null){
                        orderItem.setReturnCount(0d);
                    }
                    orderItem.setId(refundOrderItemDTO.getOrderItemId());
                    orderItem.setReturnCount(MathUtil.add(orderItem.getReturnCount(),returnCount));
                    iBillDjDeliverOrderItemMapper.updateByPrimaryKeySelective(orderItem);
                    Double price = refundOrderItemDTO.getPrice();//购买单价
                    Double shopCount=refundOrderItemDTO.getShopCount();//购买数据
                    Double transportationCost=refundOrderItemDTO.getTransportationCost();//运费
                    Double stevedorageCost=refundOrderItemDTO.getStevedorageCost();//搬运费
                    //计算可退运费
                    if(transportationCost>0.0) {
                        Double returnRransportationCost = CommonUtil.getReturnRransportationCost(price, shopCount, returnCount,transportationCost);
                        totalRransportationCost=MathUtil.add(totalRransportationCost,returnRransportationCost);
                        refundOrderItemDTO.setTransportationCost(returnRransportationCost);
                    }
                    //计算可退搬费
                    if(stevedorageCost>0.0){
                        String isUpstairsCost=refundOrderItemDTO.getIsUpstairsCost();//是否按1层收取上楼费
                        Double moveCost=refundOrderItemDTO.getMoveCost();//每层搬超级赛亚人费
                        Double returnStevedorageCost=CommonUtil.getReturnStevedorageCost(elevator,floor,isUpstairsCost,moveCost,returnCount);
                        totalStevedorageCost=MathUtil.add(totalStevedorageCost,returnStevedorageCost);
                        refundOrderItemDTO.setStevedorageCost(totalStevedorageCost);
                    }
                    //优惠金额
                    Double discountPrice=0d;
                    if(refundOrderItemDTO.getDiscountPrice()!=null&&refundOrderItemDTO.getDiscountPrice()>0.0){
                        discountPrice=MathUtil.mul(MathUtil.div(refundOrderItemDTO.getDiscountPrice(),refundOrderItemDTO.getShopCount()),returnCount);
                        refundOrderItemDTO.setDiscountPrice(discountPrice);
                    }
                    //添回退款申请明细信息
                    MendMateriel mendMateriel = saveBillMendMaterial(mendOrder,cityId,refundOrderItemDTO,productId,returnCount);
                    actualTotalAmount=MathUtil.add(actualTotalAmount,MathUtil.mul(price,returnCount));
                    actualTotalAmount=MathUtil.sub(actualTotalAmount,discountPrice);

                }
                mendOrder.setModifyDate(new Date());
                mendOrder.setState(1);
                mendOrder.setCarriage(totalRransportationCost);//运费
                mendOrder.setTotalStevedorageCost(totalStevedorageCost);//搬运费
                mendOrder.setActualTotalAmount(actualTotalAmount);//退货总额
                mendOrder.setTotalAmount(MathUtil.add(MathUtil.add(actualTotalAmount,totalRransportationCost),totalStevedorageCost));//实退款，含运费
                //添加对应的申请退货单信息
                iBillMendOrderMapper.insert(mendOrder);
                //添加对应的流水记录节点信息
                updateOrderProgressInfo(mendOrder.getId(),"2","REFUND_AFTER_SALES","RA_001",member.getId());
                updateOrderProgressInfo(mendOrder.getId(),"2","REFUND_AFTER_SALES","RA_002",member.getId());
            }
        }
        return  ServerResponse.createBySuccessMessage("提交成功");
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
        iBillOrderProgressMapper.insert(orderProgress);
    }
    /**
     * 添加退款信息
     * @param mendOrder
     * @param cityId
     * @param refundOrderItemDTO
     * @param productId
     * @param returnCount
     * @return
     */
    public MendMateriel saveBillMendMaterial(MendOrder mendOrder, String cityId,RefundOrderItemDTO refundOrderItemDTO, String productId, Double returnCount) {

        MendMateriel mendMateriel = new MendMateriel();//退材料明细
        mendMateriel.setCityId(cityId);
        mendMateriel.setProductSn(refundOrderItemDTO.getProductSn());
        mendMateriel.setProductName(refundOrderItemDTO.getProductName());
        mendMateriel.setPrice(refundOrderItemDTO.getPrice());
        mendMateriel.setCost(refundOrderItemDTO.getCost());
        mendMateriel.setUnitName(refundOrderItemDTO.getUnitName());
        mendMateriel.setTotalPrice(MathUtil.mul(returnCount.doubleValue() ,refundOrderItemDTO.getPrice()));
        mendMateriel.setProductType(Integer.parseInt(refundOrderItemDTO.getProductType()));//0：材料；1：包工包料
        mendMateriel.setCategoryId(refundOrderItemDTO.getCategoryId());
        mendMateriel.setImage(refundOrderItemDTO.getImage());
        mendMateriel.setStorefrontId(refundOrderItemDTO.getStorefrontId());
        mendMateriel.setOrderItemId(refundOrderItemDTO.getOrderItemId());
        mendMateriel.setShopCount(returnCount.doubleValue());
        /*Unit unit = iBillUnitMapper.selectByPrimaryKey(refundOrderItemDTO.getConvertUnit());
        if (unit!=null&&unit.getType() == 1) {
            mendMateriel.setShopCount(Math.ceil(returnCount.doubleValue()));
        }*/
        mendMateriel.setStevedorageCost(refundOrderItemDTO.getStevedorageCost());
        mendMateriel.setTransportationCost(refundOrderItemDTO.getTransportationCost());
        mendMateriel.setMendOrderId(mendOrder.getId());
        mendMateriel.setProductId(productId);
        mendMateriel.setCategoryId(refundOrderItemDTO.getCategoryId());
        mendMateriel.setActualCount(mendMateriel.getShopCount());
        mendMateriel.setActualPrice(mendMateriel.getTotalPrice());
        mendMateriel.setDiscountPrice(refundOrderItemDTO.getDiscountPrice());
        iBillMendMaterialMapper.insertSelective(mendMateriel);
        return mendMateriel;
    }
    /**
     * 获取用户信息
     *
     * @param userToken userToken
     * @return Member/ServerResponse
     */
    public Object getMember(String userToken) {
        if (CommonUtil.isEmpty(userToken)) {
            return ServerResponse.createbyUserTokenError();
        }
        AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
        if (accessToken == null) {
            return ServerResponse.createbyUserTokenError();
        }
        Member worker = accessToken.getMember();
        if (worker == null) {
            return ServerResponse.createbyUserTokenError();
        }
        return worker;
    }

    /**
     *查询仅退款信息的历史退款记录
     * @param pageDTO 分页
     * @param cityId 城市ID
     * @param houseId 房子ID
     * @param searchKey
     * @param type (0:补材料;1:补人工;2:退材料(剩余材料登记);3:退人工,4:业主退材料;5业主退货退款,6系统自动退款)
     * @return
     */
    public ServerResponse<PageInfo> queryRefundOnlyHistoryOrderList(PageDTO pageDTO,String userToken,String cityId,String houseId,String searchKey,Integer type){
        try{
            Object object = getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = (Member) object;
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            logger.info("queryRefundOrderList查询可退款的商品：city={},houseId={}",cityId,houseId);
            List<RefundRepairOrderDTO> repairOrderDTOList=refundAfterSalesMapper.queryRefundOnlyHistoryOrderList(member.getId(),houseId,type);

            if(repairOrderDTOList!=null&&repairOrderDTOList.size()>0){
                String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
                for(RefundRepairOrderDTO refundRepairOrderDTO:repairOrderDTOList){
                   String  repairMendOrderId=refundRepairOrderDTO.getRepairMendOrderId();
                    List<RefundRepairOrderMaterialDTO> repairMaterialList=refundAfterSalesMapper.queryRefundOnlyHistoryOrderMaterialList(repairMendOrderId,null);
                    //getRepairOrderProductList(repairMaterialList,address);
                   // refundRepairOrderDTO.setRepairOrderMaterialDTO(repairMaterialList);
                    refundRepairOrderDTO.setRepairProductCount(repairMaterialList.size());
                    refundRepairOrderDTO.setStateName(CommonUtil.getStateName(refundRepairOrderDTO.getState()));
                    refundRepairOrderDTO.setRepairProductImageArr(getStartTwoImage(repairMaterialList,address));
                    if(repairMaterialList!=null&&repairMaterialList.size()>0){
                        RefundRepairOrderMaterialDTO rm=repairMaterialList.get(0);
                        refundRepairOrderDTO.setRepairProductName(rm.getProductName());
                    }
                    refundRepairOrderDTO.setStorefrontIcon(address+refundRepairOrderDTO.getStorefrontIcon());
                }
            }
            PageInfo pageResult = new PageInfo(repairOrderDTOList);
            return  ServerResponse.createBySuccess("查询成功",pageResult);
        }catch (Exception e){
            logger.error("查询仅退款商品历史记录异常：",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 获取前两个商品的图片
     * @return
     */
    String getStartTwoImage(List<RefundRepairOrderMaterialDTO> repairMaterialList,String address){
        String imageUrl="";
        if(repairMaterialList!=null&&repairMaterialList.size()>0){
            for(RefundRepairOrderMaterialDTO ap:repairMaterialList){
                String image=ap.getImage();
                //添加图片详情地址字段
                if(StringUtils.isNotBlank(image)){
                    String[] imgArr = image.split(",");
                    if(StringUtils.isBlank(imageUrl)){
                       imageUrl=address+imgArr[0];
                    }else{
                        imageUrl=imageUrl+","+address+imgArr[0];
                        break;
                    }
                }

            }
        }
        return imageUrl;
    }

    //加载商品规格等数据
    void getRepairOrderProductList(List<RefundRepairOrderMaterialDTO> repairMaterialList,String address){
        if(repairMaterialList!=null&&repairMaterialList.size()>0){
            for(RefundRepairOrderMaterialDTO ap:repairMaterialList){
                setRepairOrderProductInfo(ap,address);
            }
        }
    }
    /**
     * 替换对应的信息
     * @param ap
     * @param address
     */
    private  void setRepairOrderProductInfo(RefundRepairOrderMaterialDTO ap,String address){
        String productTemplateId=ap.getProductTemplateId();
        DjBasicsProductTemplate pt=iBillProductTemplateMapper.selectByPrimaryKey(productTemplateId);
        if(pt!=null&&StringUtils.isNotBlank(pt.getId())){
            String image=ap.getImage();
            if (image == null) {
                image=pt.getImage();
            }
            ap.setConvertUnit(pt.getConvertUnit());
            ap.setCost(pt.getCost());
            ap.setCategoryId(pt.getCategoryId());
            if(ap.getStorefrontIcon()!=null&&StringUtils.isNotBlank(ap.getStorefrontIcon())){
                ap.setStorefrontIcon(address+ap.getStorefrontIcon());
            }
            //添加图片详情地址字段
            String[] imgArr = image.split(",");
            //StringBuilder imgStr = new StringBuilder();
            //StringBuilder imgUrlStr = new StringBuilder();
            //StringTool.getImages(address, imgArr, imgStr, imgUrlStr);
            if(imgArr!=null&&imgArr.length>0){
                ap.setImageUrl(address+imgArr[0]);//图片详情地址设置
            }
           // ap.setImageUrl(imgStr.toString());//图片详情地址设置
            //查询单位
            String unitId=pt.getUnitId();
            if(pt.getConvertQuality()!=null&&pt.getConvertQuality()>0){
                unitId=pt.getConvertUnit();
            }
            if(unitId!=null&& StringUtils.isNotBlank(unitId)){
                Unit unit= iBillUnitMapper.selectByPrimaryKey(unitId);
                ap.setUnitId(unitId);
                ap.setUnitName(unit!=null?unit.getName():"");
                ap.setUnitType(unit!=null?unit.getType():2);
            }
            BasicsGoods goods=iBillBasicsGoodsMapper.selectByPrimaryKey(pt.getGoodsId());
            ap.setProductType(goods.getType().toString());
            if(StringUtils.isNotBlank(goods.getBrandId())){
                Brand brand=iBillBrandMapper.selectByPrimaryKey(goods.getBrandId());
                ap.setBrandId(goods.getId());
                ap.setBrandName(brand!=null?brand.getName():"");
            }
            //查询规格名称
            if (StringUtils.isNotBlank(pt.getValueIdArr())) {
                ap.setValueIdArr(pt.getValueIdArr());
                ap.setValueNameArr(billProductTemplateService.getNewValueNameArr(pt.getValueIdArr().replaceAll(",", " ")));
            }
        }

    }
    /**
     * 查询退货单详情数据
     * @param cityId 城市ID
     * @param repairMendOrderId 退货单ID
     * @return
     */
    public ServerResponse queryRefundOnlyHistoryOrderInfo(String cityId,String repairMendOrderId){
        logger.info("queryRefundOrderList查询可退款的商品：city={},repairMendOrderId={}",cityId,repairMendOrderId);
        try{
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            RefundRepairOrderDTO refundRepairOrderDTO=refundAfterSalesMapper.queryRefundOnlyHistoryOrderInfo(repairMendOrderId);//退款订单详情查询
            List<RefundRepairOrderMaterialDTO> repairMaterialList=refundAfterSalesMapper.queryRefundOnlyHistoryOrderMaterialList(repairMendOrderId,null);//退款商品列表查询
            getRepairOrderProductList(repairMaterialList,address);
            refundRepairOrderDTO.setOrderMaterialList(repairMaterialList);//将退款材料明细放入对象中
            //查询对应的流水节点信息(根据订单ID）
            List<OrderProgressDTO> orderProgressDTOList=iBillOrderProgressMapper.queryOrderProgressListByOrderId(repairMendOrderId,null);//仅退款
            refundRepairOrderDTO.setShowRepairDateType(1);//显示时间判断（1订单剩余时间，2最新处理时间）
           if(orderProgressDTOList!=null&&orderProgressDTOList.size()>0){//判断最后节点，及剩余处理时间
               OrderProgressDTO orderProgressDTO=orderProgressDTOList.get(orderProgressDTOList.size()-1);
               refundRepairOrderDTO.setRepairNewNode(orderProgressDTO.getNodeName());
               refundRepairOrderDTO.setReparirRemainingTime(getRemainingTime(orderProgressDTO,orderProgressDTO.getCreateDate()));
               refundRepairOrderDTO.setAssociatedOperation(orderProgressDTO.getAssociatedOperation());
               refundRepairOrderDTO.setAssociatedOperationName(orderProgressDTO.getAssociatedOperationName());
               refundRepairOrderDTO.setRepairNewDate(orderProgressDTO.getCreateDate());
               //前端节点显示
               refundRepairOrderDTO.setOrderProgressList(setOrderProgressList(orderProgressDTOList,orderProgressDTO,1));//返回显示，节点优化
               if("RA_008".equals(orderProgressDTO.getNodeCode())||"RA_009".equals(orderProgressDTO.getNodeCode())||"RA_010".equals(orderProgressDTO.getNodeCode())){
                   refundRepairOrderDTO.setShowRepairDateType(2);
               }
           }
           if(refundRepairOrderDTO.getType()==6){
               refundRepairOrderDTO.setRepairNewNode("退款成功");
           }
            if("5".equals(refundRepairOrderDTO.getState())){
                refundRepairOrderDTO.setShowRepairDateType(2);
            }
            refundRepairOrderDTO.setMobile(refundRepairOrderDTO.getStorefrontMobile());//拨打电话
            refundRepairOrderDTO.setStorefrontIcon(address+refundRepairOrderDTO.getStorefrontIcon());
            //相关凭证图片地址存储
            refundRepairOrderDTO.setImageArrUrl(StringTool.getImage(refundRepairOrderDTO.getImageArr(),address));//图片详情地址设置

            return ServerResponse.createBySuccess("查询成功",refundRepairOrderDTO);
        }catch (Exception e){
            logger.error("queryRefundOnlyHistoryOrderInfo查询退款详情失败：",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 查询退货退款--退款订单
     *
     * @param cityId
     * @param repairMendOrderId
     * @return
     */
    public ServerResponse queryRefundHistoryOrderInfo(String cityId,String repairMendOrderId){
        try{
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            RefundRepairOrderDTO refundRepairOrderDTO=refundAfterSalesMapper.queryRefundOnlyHistoryOrderInfo(repairMendOrderId);//退款订单详情查询
            if("3".equals(refundRepairOrderDTO.getState())){//已分配供应商
                List<Map<String,Object>> deliverList=new ArrayList<>();
                Example example=new Example(MendDeliver.class);
                example.createCriteria().andEqualTo(MendDeliver.MEND_ORDER_ID,refundRepairOrderDTO.getRepairMendOrderId());
                List<MendDeliver> mendDeliverList=billMendDeliverMapper.selectByExample(example);
                if(mendDeliverList!=null){
                    Map<String,Object> param;
                    for(MendDeliver mendDeliver:mendDeliverList){
                        List<RefundRepairOrderMaterialDTO> repairMaterialList=refundAfterSalesMapper.queryRefundOnlyHistoryOrderMaterialList(repairMendOrderId,mendDeliver.getId());
                        param=new HashMap();
                        param.put("mendDeliverId",mendDeliver.getId());
                        param.put("number",mendDeliver.getNumber());
                        param.put("state",mendDeliver.getShippingState());//0供应商待确认,1已确认,2已结算,3取消，4部分退货，5业主申诉部分退货，6业主认可部分退货，7平台同意（按业主申请退），8平台驳回（按供应商同意退）
                        param.put("stateName",CommonUtil.getDeliverStateName(mendDeliver.getShippingState()));
                        param.put("totalAmount",mendDeliver.getTotalAmount());//退货总额
                        param.put("count",repairMaterialList.size());//总件数
                        if(repairMaterialList!=null&&repairMaterialList.size()>0){
                            RefundRepairOrderMaterialDTO rm=repairMaterialList.get(0);
                            param.put("productName",rm.getProductName());//商品名称
                        }
                        param.put("image",getStartTwoImage(repairMaterialList,address));//图片
                        deliverList.add(param);
                        refundRepairOrderDTO.setRepairNewDate(mendDeliver.getCreateDate());//处理时间
                    }
                }
                refundRepairOrderDTO.setMendDeliverList(deliverList);
            }else{//未分配供应商（1待处理（可撤销），5已撤销）
                List<RefundRepairOrderMaterialDTO> repairMaterialList=refundAfterSalesMapper.queryRefundOnlyHistoryOrderMaterialList(repairMendOrderId,null);//退款商品列表查询
                getRepairOrderProductList(repairMaterialList,address);
                refundRepairOrderDTO.setOrderMaterialList(repairMaterialList);//将退款材料明细放入对象中
            }
            //相关凭证图片地址存储
            refundRepairOrderDTO.setImageArrUrl(StringTool.getImage(refundRepairOrderDTO.getImageArr(),address));//图片详情地址设置
            return ServerResponse.createBySuccess("查询成功",refundRepairOrderDTO);
        }catch (Exception e){
            logger.error("查询失败",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }
    /**
     * 查询退货退款--退款详情
     *
     * @param cityId
     * @param mendDeliverId 退货单ID
     * @return
     */
    public ServerResponse queryMendDeliverInfo(String cityId,String mendDeliverId){
        logger.info("queryMendDeliverInfo查询可退货退款的商品：city={},mendDeliverId={}",cityId,mendDeliverId);
        try{
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            RefundRepairOrderDTO refundRepairOrderDTO=refundAfterSalesMapper.queryMendSplitOrderInfo(mendDeliverId);//退款订单详情查询
            List<RefundRepairOrderMaterialDTO> repairMaterialList=refundAfterSalesMapper.queryRefundOnlyHistoryOrderMaterialList(refundRepairOrderDTO.getRepairMendOrderId(),mendDeliverId);//退款商品列表查询
            getRepairOrderProductList(repairMaterialList,address);
            refundRepairOrderDTO.setOrderMaterialList(repairMaterialList);//将退款材料明细放入对象中
            //查询订单总额，运费等信息
           Map<String,Object> totalMap=refundAfterSalesMapper.totalProductDimensionMendDetail(mendDeliverId);
           if(totalMap!=null){
               refundRepairOrderDTO.setTotalAmount(((BigDecimal)totalMap.get("totalAmount")).doubleValue());//实退款
               refundRepairOrderDTO.setActualTotalAmount(((BigDecimal)totalMap.get("totalPrice")).doubleValue());//退货金额
               refundRepairOrderDTO.setTotalStevedorageCost(((BigDecimal)totalMap.get("totalStevedorageCost")).doubleValue());//退货金额
               refundRepairOrderDTO.setCarriage(((BigDecimal)totalMap.get("carriage")).doubleValue());//退货金额
           }
            //查询对应的流水节点信息(根据订单ID）
            List<OrderProgressDTO> orderProgressDTOList=iBillOrderProgressMapper.queryOrderProgressListByOrderId(refundRepairOrderDTO.getRepairMendOrderId(),mendDeliverId);//仅退款
            refundRepairOrderDTO.setShowRepairDateType(1);//显示时间判断（1订单剩余时间，2最新处理时间）

            if(orderProgressDTOList!=null&&orderProgressDTOList.size()>0){//判断最后节点，及剩余处理时间
                OrderProgressDTO orderProgressDTO=orderProgressDTOList.get(orderProgressDTOList.size()-1);
                refundRepairOrderDTO.setRepairNewNode(orderProgressDTO.getNodeName());
                if("RA_001".equals(orderProgressDTO.getNodeCode())||"RA_002".equals(orderProgressDTO.getNodeCode())){
                    refundRepairOrderDTO.setReparirRemainingTime(getRemainingTime(orderProgressDTO,refundRepairOrderDTO.getCreateDate()));
                }else{
                    refundRepairOrderDTO.setReparirRemainingTime(getRemainingTime(orderProgressDTO,orderProgressDTO.getCreateDate()));
                }

                refundRepairOrderDTO.setAssociatedOperation(orderProgressDTO.getAssociatedOperation());
                refundRepairOrderDTO.setAssociatedOperationName(orderProgressDTO.getAssociatedOperationName());
                refundRepairOrderDTO.setRepairNewDate(orderProgressDTO.getCreateDate());
                //前端节点显示
                refundRepairOrderDTO.setOrderProgressList(setOrderProgressList(orderProgressDTOList,orderProgressDTO,3));//返回显示，节点优化
                if("RA_008".equals(orderProgressDTO.getNodeCode())||"RA_009".equals(orderProgressDTO.getNodeCode())||"RA_010".equals(orderProgressDTO.getNodeCode())){
                    refundRepairOrderDTO.setShowRepairDateType(2);
                }
            }
            if("5".equals(refundRepairOrderDTO.getState())){
                refundRepairOrderDTO.setShowRepairDateType(2);
            }
            refundRepairOrderDTO.setMobile(refundRepairOrderDTO.getStorefrontMobile());//拨打电话
            refundRepairOrderDTO.setStorefrontIcon(address+refundRepairOrderDTO.getStorefrontIcon());

            return ServerResponse.createBySuccess("查询成功",refundRepairOrderDTO);
        }catch (Exception e){
            logger.error("查询失败",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    //判断对就原按钮显不显示
    private List<OrderProgressDTO> setOrderProgressList(List<OrderProgressDTO> orderProgressDTOList,OrderProgressDTO orderProgressDTO,Integer type){
        List<OrderProgressDTO> list=new ArrayList<>();
        for(int i=0;i<orderProgressDTOList.size();i++){
            OrderProgressDTO op=orderProgressDTOList.get(i);
            op.setNodeStatus(1);//先打勾
            //如果为商家已拒绝或退款关闭，则打叉
            if("RA_004".equals(op.getNodeCode())||"RA_009".equals(op.getNodeCode())){//
                op.setNodeStatus(2);//打黑叉
            }
            //如果为商家已拒绝,最新节点也为已拒绝，则打红叉
            if("RA_004".equals(op.getNodeCode())&&"RA_004".equals(orderProgressDTO.getNodeCode())){//
                op.setNodeStatus(3);//打红叉
            }
            if(type==3&&"RA_001".equals(op.getNodeCode())){
                op.setAssociatedOperation("");//清空按钮数据
                op.setAssociatedOperationName("");//清空按钮数据
            }
            //如果最新节点为RA_001，RA_002等待商家审核 状态，且当前显示节点为001的话，则显示“撤销按钮”,否则，则不显示
            if(!(("RA_001".equals(orderProgressDTO.getNodeCode())||"RA_002".equals(orderProgressDTO.getNodeCode())))&&"RA_001".equals(op.getNodeCode())){
                op.setAssociatedOperation("");//清空按钮数据
                op.setAssociatedOperationName("");//清空按钮数据
            }
            if(!("RA_004".equals(orderProgressDTO.getNodeCode()))&&"RA_004".equals(op.getNodeCode())){//如果当前节点或最新节点都 为商家拒绝，则增加等待"平台介入"按钮,否则，则去掉按钮
                op.setAssociatedOperation("");//清空按钮数据
                op.setAssociatedOperationName("");//清空按钮数据
            }
            list.add(op);
        }
        if("RA_002".equals(orderProgressDTO.getNodeCode())||"RA_005".equals(orderProgressDTO.getNodeCode())){//如果最新节点为等待高家审核 或等待平如介入，则增加退款关闭，灰度显示
            OrderProgressDTO lastOrder=new OrderProgressDTO();
            lastOrder.setNodeCode("RA_008");
            lastOrder.setNodeName("退款成功");
            lastOrder.setNodeStatus(4);
            list.add(lastOrder);
        }

        return list;
     }
    /**
     * 获取当前阶段剩余可处理时间
     * @return
     */
    private long getRemainingTime(OrderProgressDTO orderProgressDTO,Date createDate){
        try{
            String nodeCode=orderProgressDTO.getNodeCode();
            String parayKey=CommonUtil.getParayKey(nodeCode);
            if(parayKey!=null&&StringUtils.isNotBlank(parayKey)){
                Config config=iBillConfigMapper.selectConfigInfoByParamKey(parayKey);//获取对应阶段需处理剩余时间
                if(config!=null&&StringUtils.isNotBlank(config.getId())){
                    String hour=config.getParamValue();
                    Date newDate=DateUtil.addDateHours(createDate,Integer.parseInt(hour));
                    return DateUtil.daysBetweenTime(new Date(),newDate);
                }
            }
        }catch (Exception e){
            logger.error("获取剩余时间异常:",e);
        }

        return 0;
    }
    /**
     * 撤销退款申请
     * @param cityId
     * @param repairMendOrderId 申请单ID
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse cancelRepairApplication(String  cityId,String repairMendOrderId){
        RefundRepairOrderDTO refundRepairOrderDTO=refundAfterSalesMapper.queryRefundOnlyHistoryOrderInfo(repairMendOrderId);//退款订单详情查询
        if("5".equals(refundRepairOrderDTO.getState())){
            return ServerResponse.createByErrorMessage("请勿重复撤销");
        }
        if(!"1".equals(refundRepairOrderDTO.getState())){
            return ServerResponse.createByErrorMessage("只能撤回状态为待处进的单");
        }
        updateRepairOrderInfo(refundRepairOrderDTO,repairMendOrderId, 5,refundRepairOrderDTO.getType());//已撤销（业主仅退款）
        //添加对应的流水记录节点信息
        updateOrderProgressInfo(repairMendOrderId,"2","REFUND_AFTER_SALES","RA_011",refundRepairOrderDTO.getApplyMemberId());
        return ServerResponse.createBySuccessMessage("撤销成功");
    }
    /**
     * 修改对应的退款申请单信息(退款/退货退款）
     * @param repairMendOrderId
     * @param state
     * @param type 4仅退款，5退货退款
     */
    void updateRepairOrderInfo(RefundRepairOrderDTO refundRepairOrderDTO,String repairMendOrderId,Integer state,Integer type){
        List<RefundRepairOrderMaterialDTO> repairMaterialList=refundAfterSalesMapper.queryRefundOnlyHistoryOrderMaterialList(repairMendOrderId,null);//退款商品列表查询
        if(repairMaterialList!=null&&repairMaterialList.size()>0){
            for(RefundRepairOrderMaterialDTO rm:repairMaterialList){
                Double returnCount=rm.getReturnCount();
                //修改订单详情表的退货字段
                if(type==4){//订单详情表的退货字段
                    OrderItem orderItem=iBillDjDeliverOrderItemMapper.selectByPrimaryKey(rm.getOrderItemId());
                    orderItem.setId(rm.getOrderItemId());
                    Double newReturnCount=MathUtil.sub(orderItem.getReturnCount(),returnCount);
                    orderItem.setReturnCount(newReturnCount<0?0:newReturnCount);
                    iBillDjDeliverOrderItemMapper.updateByPrimaryKeySelective(orderItem);
                }
               if(type==5){//要货单详情表的退货字段
                   //修改要货订单中的退货量为最新的退货量
                   OrderSplitItem orderSplitItem=billDjDeliverOrderSplitItemMapper.selectByPrimaryKey(rm.getOrderItemId());
                   orderSplitItem.setId(rm.getOrderItemId());
                   Double newReturnCount=MathUtil.sub(orderSplitItem.getReturnCount(),returnCount);
                   orderSplitItem.setReturnCount(newReturnCount<0?0:newReturnCount);
                   billDjDeliverOrderSplitItemMapper.updateByPrimaryKeySelective(orderSplitItem);
               }

            }
        }
        MendOrder mendOrder=new MendOrder();
        mendOrder.setId(refundRepairOrderDTO.getRepairMendOrderId());
        mendOrder.setState(state);
        mendOrder.setModifyDate(new Date());
        iBillMendOrderMapper.updateByPrimaryKeySelective(mendOrder);//修改退款申请单的状态为已撤销或已驳回
    }

    /**
     * 驳回申诉（退货申请）----废弃
     * @param repairMendOrderId
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse<Object> rejectRepairApplication(String repairMendOrderId, String userId){
        RefundRepairOrderDTO refundRepairOrderDTO=refundAfterSalesMapper.queryRefundOnlyHistoryOrderInfo(repairMendOrderId);//退款订单详情查询
        if(!("1".equals(refundRepairOrderDTO.getState())||"2".equals(refundRepairOrderDTO.getState()))){
            return ServerResponse.createByErrorMessage("此单已处理完成，请勿重复操作");
        }
        updateRepairOrderInfo(refundRepairOrderDTO,repairMendOrderId, 6,refundRepairOrderDTO.getType());//退款关闭
        //更新平台介入按钮的状态为已删除
        iBillOrderProgressMapper.updateOrderStatusByNodeCode(repairMendOrderId,"REFUND_AFTER_SALES","RA_005");
        //添加对应的流水记录节点信息,平台已拒绝
        updateOrderProgressInfo(repairMendOrderId,"2","REFUND_AFTER_SALES","RA_007",userId);
         //添加对应的流水记录节点信息，退款关闭
        updateOrderProgressInfo(repairMendOrderId,"2","REFUND_AFTER_SALES","RA_009",userId);
        return ServerResponse.createBySuccess("操作成功");
    }

    /**
     * 同意退款申诉（退货申请）----废弃
     * @param repairMendOrderId
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse agreeRepairApplication(String repairMendOrderId,String userId){
        RefundRepairOrderDTO refundRepairOrderDTO=refundAfterSalesMapper.queryRefundOnlyHistoryOrderInfo(repairMendOrderId);//退款订单详情查询
       if(!("1".equals(refundRepairOrderDTO.getState())||"2".equals(refundRepairOrderDTO.getState()))){
            return ServerResponse.createByErrorMessage("此单已处理完成，请勿重复操作");
        }
        //修改退款申诉的状态
        MendOrder mendOrder=new MendOrder();
        mendOrder.setId(repairMendOrderId);
        mendOrder.setState(3);
        mendOrder.setModifyDate(new Date());
        iBillMendOrderMapper.updateByPrimaryKeySelective(mendOrder);//修改退款申请单的状态同意
        //更新平台介入按钮的状态为已删除
        iBillOrderProgressMapper.updateOrderStatusByNodeCode(repairMendOrderId,"REFUND_AFTER_SALES","RA_005");
        //添加对应的流水记录节点信息,平台已同意
        updateOrderProgressInfo(repairMendOrderId,"2","REFUND_AFTER_SALES","RA_006",userId);
        //添加对应的流水记录节点信息，退款关闭
        updateOrderProgressInfo(repairMendOrderId,"2","REFUND_AFTER_SALES","RA_008",userId);
       // billMendOrderCheckService.settleMendOrder(repairMendOrderId,null);//退钱给业主
        return ServerResponse.createBySuccess("操作成功");
    }

    /**
     * 业主申诉退货
     * @param userToken
     * @param content  申诉内容
     * @param houseId  房子ID
     * @param mendDeliverId  申诉单号
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse addRepairComplain(String userToken,String content,String houseId,String mendDeliverId){
        Object object = getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member member = (Member) object;
        Example example = new Example(Complain.class);
        example.createCriteria()
                .andEqualTo(Complain.MEMBER_ID, member.getId())
                .andEqualTo(Complain.COMPLAIN_TYPE, "7")
                .andEqualTo(Complain.BUSINESS_ID, mendDeliverId)
                .andEqualTo(Complain.STATUS, 0);
        List list = iBillComplainMapper.selectByExample(example);
        if (list.size() > 0) {
            return ServerResponse.createByErrorMessage("请勿重复提交申请！");
        }
        RefundRepairOrderDTO refundRepairOrderDTO=refundAfterSalesMapper.queryRefundOnlyHistoryOrderInfo(mendDeliverId);//退款订单详情查询
        if(!("1".equals(refundRepairOrderDTO.getState())||"2".equals(refundRepairOrderDTO.getState()))){
            return ServerResponse.createByErrorMessage("不是处理中的单，不能申请平台申诉");
        }
        //添加申诉信息
        Complain complain = new Complain();
        complain.setHouseId(houseId);
        complain.setMemberId(member.getId());
        complain.setComplainType(7);
        complain.setContent(content);
        complain.setStatus(0);
        complain.setUserNickName(member.getNickName());
        complain.setUserName(member.getName());
        complain.setUserMobile(member.getMobile());
        complain.setUserId(member.getId());
        complain.setBusinessId(mendDeliverId);
        iBillComplainMapper.insert(complain);
        //添加对应的申诉流水信息(增加平台介入记录信息）
        updateOrderProgressInfo(mendDeliverId,"2","REFUND_AFTER_SALES","RA_005",member.getId());
        return ServerResponse.createBySuccessMessage("申诉提交成功");
    }

    /**
     * 查询退货退款列表
     * @param pageDTO
     * @param cityId
     * @param houseId
     * @param searchKey 订单号或商品名称
     * @return
     */
    public ServerResponse queryReturnRefundOrderList(PageDTO pageDTO,String userToken,String cityId,String houseId,String searchKey){
        try{
            Object object = getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = (Member) object;
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            logger.info("queryReturnRefundOrderList查询可退货退款的商品：city={},houseId={}",cityId,houseId);
            List<RefundOrderDTO> orderlist = billDjDeliverOrderSplitMapper.queryReturnRefundOrderList(member.getId(),houseId,searchKey);
            if(orderlist!=null&&orderlist.size()>0){
                String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
                for(RefundOrderDTO order:orderlist){
                    String orderSplitId=order.getOrderSplitId();
                    List<RefundOrderItemDTO> orderItemList=billDjDeliverOrderSplitItemMapper.queryReturnRefundOrderItemList(orderSplitId,searchKey);
                    getProductList(orderItemList,address);
                    order.setOrderDetailList(orderItemList);
                    order.setStorefrontIcon(address+order.getStorefrontIcon());
                }
            }
            PageInfo pageResult = new PageInfo(orderlist);
            return  ServerResponse.createBySuccess("查询成功",pageResult);
        }catch (Exception e){
            logger.error("queryReturnRefundOrderList查询失败",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }
    /**
     * 申请退货退款列表展示
     * @param userToken
     * @param cityId
     * @param houseId
     * @param orderProductAttr
     * @return
     */
    public ServerResponse queryReturnRefundInfoList(String userToken,String cityId,String houseId,String orderProductAttr){
        try{
            Map<String,Object> map=new HashMap<String,Object>();
            Double actualTotalAmountT=0.0;//退货总额
            Double totalRransportationCostT = 0.0;//可退运费
            Double totalStevedorageCostT = 0.0;//可退搬运费
            Double totalAmountT=0.0;//实退款
            //获取退款商品列表
            List<RefundOrderDTO> orderlist=new ArrayList<>();
            JSONArray orderArrayList=JSONArray.parseArray(orderProductAttr);
            if(orderArrayList!=null&&orderArrayList.size()>0) {
                String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
                for (int i = 0; i < orderArrayList.size(); i++) {
                    JSONObject obj = (JSONObject) orderArrayList.get(i);
                    String orderSplitId = (String) obj.get("orderSplitId");//退货退款订单号
                    String houseIdNew = (String) obj.get("houseId");
                    if(houseIdNew!=null){
                        houseId=houseIdNew;
                    }
                    //查询房子信息，获取房子对应的楼层
                    QuantityRoom quantityRoom=iBillQuantityRoomMapper.getBillQuantityRoom(houseId,0);
                    Integer elevator= 1;//是否电梯房
                    String floor="1";
                    if(quantityRoom!=null&&StringUtils.isNotBlank(quantityRoom.getId())){
                        elevator=quantityRoom.getElevator();//是否电梯房
                        floor=quantityRoom.getFloor();//楼层
                    }
                    RefundOrderDTO orderInfo = billDjDeliverOrderSplitMapper.queryReturnRefundOrderInfo(orderSplitId);
                    Double totalRransportationCost = 0.0;//可退运费
                    Double totalStevedorageCost = 0.0;//可退搬运费
                    Double actualTotalAmount=0.0;//退货总额
                    List<RefundOrderItemDTO> orderItemList=new ArrayList<>();
                    //获取商品信息
                    JSONArray orderItemProductList = obj.getJSONArray("orderItemProductList");
                    for (int j = 0; j < orderItemProductList.size(); j++) {
                        JSONObject productObj = (JSONObject) orderItemProductList.get(j);
                        String orderSplitItemId = (String) productObj.get("orderSplitItemId");//退货退款详情订单号
                        // String productId = (String) productObj.get("productId");//产品ID
                        Double returnCount = productObj.getDouble("returnCount");//退货量
                        RefundOrderItemDTO refundOrderItemDTO = billDjDeliverOrderSplitItemMapper.queryReturnRefundOrderItemInfo(orderSplitItemId);
                        if (refundOrderItemDTO == null) {
                            return ServerResponse.createByErrorMessage("未找到对应的退货单信息");
                        }
                        refundOrderItemDTO.setSurplusCount(returnCount);
                        setProductInfo(refundOrderItemDTO,address);
                        Double price = refundOrderItemDTO.getPrice();//购买单价
                        Double shopCount=refundOrderItemDTO.getShopCount();//购买数据
                        Double transportationCost=refundOrderItemDTO.getTransportationCost();//运费
                        Double stevedorageCost=refundOrderItemDTO.getStevedorageCost();//搬运费
                        //计算可退运费
                        if(transportationCost>0.0) {
                            Double returnRransportationCost = CommonUtil.getReturnRransportationCost(price, shopCount, returnCount,transportationCost);
                            totalRransportationCost=MathUtil.add(totalRransportationCost,returnRransportationCost);
                        }
                        //计算可退搬费
                        if(stevedorageCost>0.0){
                            String isUpstairsCost=refundOrderItemDTO.getIsUpstairsCost();//是否按1层收取上楼费
                            Double moveCost=refundOrderItemDTO.getMoveCost();//每层搬运费
                            Double returnStevedorageCost=CommonUtil.getReturnStevedorageCost(elevator,floor,isUpstairsCost,moveCost,returnCount);
                            totalStevedorageCost=MathUtil.add(totalStevedorageCost,returnStevedorageCost);
                        }
                        //优惠金额
                        Double discountPrice=0d;
                        if(refundOrderItemDTO.getDiscountPrice()!=null&&refundOrderItemDTO.getDiscountPrice()>0.0){
                            discountPrice=MathUtil.mul(MathUtil.div(refundOrderItemDTO.getDiscountPrice(),refundOrderItemDTO.getShopCount()),returnCount);
                        }
                        actualTotalAmount=MathUtil.add(actualTotalAmount,MathUtil.mul(price,returnCount));
                        actualTotalAmount=MathUtil.sub(actualTotalAmount,discountPrice);
                        orderItemList.add(refundOrderItemDTO);
                    }
                    orderInfo.setTotalRransportationCost(totalRransportationCost);//可退运费
                    orderInfo.setTotalStevedorageCost(totalStevedorageCost);//可退搬运费
                    orderInfo.setOrderDetailList(orderItemList);
                    orderInfo.setActualTotalAmount(actualTotalAmount);//退货总额
                    orderInfo.setTotalAmount(MathUtil.sub(MathUtil.sub(actualTotalAmount,totalRransportationCost),totalStevedorageCost));//实退款

                    orderInfo.setTotalRransportationCostRemark("运费："+orderInfo.getTotalRransportationCost());//运费描述
                    orderInfo.setTotalStevedorageCostRemark("搬运费："+orderInfo.getTotalStevedorageCost());//搬运费描述
                    orderInfo.setStorefrontIcon(address+orderInfo.getStorefrontIcon());
                    //添加对应的信息
                    orderlist.add(orderInfo);
                    actualTotalAmountT=MathUtil.add(actualTotalAmountT,orderInfo.getActualTotalAmount());
                    totalRransportationCostT= MathUtil.add(totalRransportationCostT,orderInfo.getTotalRransportationCost());
                    totalStevedorageCostT=MathUtil.add(totalStevedorageCostT,orderInfo.getTotalStevedorageCost());
                    totalAmountT=MathUtil.add(totalAmountT,orderInfo.getTotalAmount());
                }

                map.put("actualTotalAmount",actualTotalAmountT);
                map.put("totalRransportationCost",MathUtil.sub(0,totalRransportationCostT));
                map.put("totalStevedorageCost",MathUtil.sub(0,totalStevedorageCostT));
                map.put("totalAmount",totalAmountT);
                map.put("orderlist",orderlist);
            }
            return ServerResponse.createBySuccess("查询成功",map);
        }catch (Exception e){
            logger.error("查询失败",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }
    /**
     * 退货退款提交
     * @param userToken
     * @param cityId
     * @param houseId
     * @param orderProductAttr
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse saveReturnRefundInfo(String userToken,String cityId,String houseId,String orderProductAttr){
        Object object = getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member member = (Member) object;
        String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        MendOrder mendOrder;
        Example example;
        JSONArray orderArrayList=JSONArray.parseArray(orderProductAttr);
        if(orderArrayList!=null&&orderArrayList.size()>0) {
            for (int i = 0; i < orderArrayList.size(); i++) {
                JSONObject obj = (JSONObject) orderArrayList.get(i);
                String orderSplitId = (String) obj.get("orderSplitId");
                String storefrontId = (String) obj.get("storefrontId");
                String imageArr = (String)obj.get("imageArr");
                String houseIdNew = (String) obj.get("houseId");
                if(houseIdNew!=null){
                    houseId=houseIdNew;
                }
                //查询房子信息，获取房子对应的楼层
                QuantityRoom quantityRoom=iBillQuantityRoomMapper.getBillQuantityRoom(houseId,0);
                Integer elevator= 1;//是否电梯房
                String floor="1";
                if(quantityRoom!=null&&StringUtils.isNotBlank(quantityRoom.getId())){
                    elevator=quantityRoom.getElevator();//是否电梯房
                    floor=quantityRoom.getFloor();//楼层
                }
                RefundOrderDTO orderInfo = billDjDeliverOrderSplitMapper.queryReturnRefundOrderInfo(orderSplitId);
                example = new Example(MendOrder.class);
                mendOrder = new MendOrder();
                mendOrder.setNumber("DJZX" + 40000 + iBillMendOrderMapper.selectCountByExample(example));//订单号
                mendOrder.setHouseId(orderInfo.getHouseId());
                mendOrder.setAddressId(orderInfo.getAddressId());
                mendOrder.setApplyMemberId(member.getId());
                mendOrder.setType(5);//业主退材料
                mendOrder.setOrderName("业主退材料退货退款");
                mendOrder.setState(0);//生成中
                mendOrder.setStorefrontId(orderInfo.getStorefrontId());
                mendOrder.setImageArr(imageArr);//相关凭证
                mendOrder.setOrderId(orderSplitId);//订单申请ID(要货表中的订单ID）
                Double totalRransportationCost = 0.0;//可退运费
                Double totalStevedorageCost = 0.0;//可退搬运费
                Double actualTotalAmount=0.0;//退货总额
                //获取商品信息
                JSONArray orderItemProductList = obj.getJSONArray("orderItemProductList");
                for (int j = 0; j < orderItemProductList.size(); j++) {
                    JSONObject productObj = (JSONObject) orderItemProductList.get(j);
                    String orderSplitItemId=(String)productObj.get("orderSplitItemId");//要货单详情Id
                    String productId=(String)productObj.get("productId");//产品ID
                    Double returnCount=productObj.getDouble("returnCount");//退货量
                    RefundOrderItemDTO refundOrderItemDTO=billDjDeliverOrderSplitItemMapper.queryReturnRefundOrderItemInfo(orderSplitItemId);
                    if(refundOrderItemDTO==null){
                        return ServerResponse.createByErrorMessage("未找到对应的退货单信息");
                    }
                    setProductInfo(refundOrderItemDTO,address);
                    Double surplusCount=refundOrderItemDTO.getSurplusCount();
                    logger.info("退货量{}，可退量{}",returnCount,surplusCount);
                    if(MathUtil.sub(surplusCount,returnCount)<0){
                        return ServerResponse.createByErrorMessage("退货量大于可退货量，不能退。");
                    }
                    refundOrderItemDTO.setReturnCount(returnCount);
                    //修改要货订单中的退货量为最新的退货量
                    OrderSplitItem orderSplitItem=billDjDeliverOrderSplitItemMapper.selectByPrimaryKey(refundOrderItemDTO.getOrderSplitItemId());
                    orderSplitItem.setId(refundOrderItemDTO.getOrderSplitItemId());
                    orderSplitItem.setReturnCount(MathUtil.add(orderSplitItem.getReturnCount(),returnCount));
                    billDjDeliverOrderSplitItemMapper.updateByPrimaryKeySelective(orderSplitItem);
                    Double price = refundOrderItemDTO.getPrice();//购买单价
                    Double shopCount=refundOrderItemDTO.getShopCount();//购买数据
                    Double transportationCost=refundOrderItemDTO.getTransportationCost();//运费
                    Double stevedorageCost=refundOrderItemDTO.getStevedorageCost();//搬运费
                    //计算可退运费
                    if(transportationCost>0.0) {
                        Double returnRransportationCost = CommonUtil.getReturnRransportationCost(price, shopCount, returnCount,transportationCost);
                        totalRransportationCost=MathUtil.add(totalRransportationCost,returnRransportationCost);
                        refundOrderItemDTO.setTransportationCost(returnRransportationCost);
                    }
                    //计算可退搬费
                    if(stevedorageCost>0.0){
                        String isUpstairsCost=refundOrderItemDTO.getIsUpstairsCost();//是否按1层收取上楼费
                        Double moveCost=refundOrderItemDTO.getMoveCost();//每层搬超级赛亚人费
                        Double returnStevedorageCost=CommonUtil.getReturnStevedorageCost(elevator,floor,isUpstairsCost,moveCost,returnCount);
                        totalStevedorageCost=MathUtil.add(totalStevedorageCost,returnStevedorageCost);
                        refundOrderItemDTO.setStevedorageCost(totalStevedorageCost);
                    }
                    //优惠金额
                    Double discountPrice=0d;
                    if(refundOrderItemDTO.getDiscountPrice()!=null&&refundOrderItemDTO.getDiscountPrice()>0.0){
                        discountPrice=MathUtil.mul(MathUtil.div(refundOrderItemDTO.getDiscountPrice(),refundOrderItemDTO.getShopCount()),returnCount);
                        refundOrderItemDTO.setDiscountPrice(discountPrice);
                    }

                    refundOrderItemDTO.setOrderItemId(refundOrderItemDTO.getOrderSplitItemId());//传退货单详情ID
                    //添回退款申请明细信息
                    MendMateriel mendMateriel = saveBillMendMaterial(mendOrder,cityId,refundOrderItemDTO,productId,returnCount);
                    actualTotalAmount=MathUtil.add(actualTotalAmount,MathUtil.mul(price,returnCount));

                }
                mendOrder.setModifyDate(new Date());
                mendOrder.setState(1);
                mendOrder.setCarriage(totalRransportationCost);//运费
                mendOrder.setTotalStevedorageCost(totalStevedorageCost);//搬运费
                mendOrder.setActualTotalAmount(actualTotalAmount);//退货总额
                mendOrder.setTotalAmount(MathUtil.sub(MathUtil.sub(actualTotalAmount,totalRransportationCost),totalStevedorageCost));//实退款，含运费(去掉运费搬运费后的可得钱)
                //添加对应的申请退货单信息
                iBillMendOrderMapper.insert(mendOrder);
                //添加对应的流水记录节点信息
                updateOrderProgressInfo(mendOrder.getId(),"2","REFUND_AFTER_SALES","RA_001",member.getId());
                updateOrderProgressInfo(mendOrder.getId(),"2","REFUND_AFTER_SALES","RA_002",member.getId());
            }
        }
        return  ServerResponse.createBySuccessMessage("提交成功");
    }



    /**
     * 退货退款提交
     * @param userToken
     * @param cityId
     * @param houseId
     * @param orderProductAttr
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse clearSurplusGoods(String userToken,String cityId,String houseId,String orderProductAttr){

        Object object = getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        Member member = (Member) object;
        //查询房子信息，获取房子对应的楼层
        QuantityRoom quantityRoom=iBillQuantityRoomMapper.getBillQuantityRoom(houseId,0);
        Integer elevator= 1;//是否电梯房
        String floor="1";
        if(quantityRoom!=null&&StringUtils.isNotBlank(quantityRoom.getId())){
            elevator=quantityRoom.getElevator();//是否电梯房
            floor=quantityRoom.getFloor();//楼层
        }
        MendOrder mendOrder;
        Example example;

        //去重 orderProductAttr
        JSONArray orderArrayList=JSONArray.parseArray(orderProductAttr);
        Set set = new HashSet();
        if(orderArrayList!=null && orderArrayList.size() > 0) {
            for (int i = 0; i < orderArrayList.size(); i++) {
                set.add(orderArrayList.get(i));
            }
        }
        System.out.println(set.size());
        orderArrayList = JSONArray.parseArray(set.toString());
        if(orderArrayList!=null&&orderArrayList.size()>0) {
            for (int i = 0; i < orderArrayList.size(); i++) {
                JSONObject obj = (JSONObject) orderArrayList.get(i);
                String orderSplitId = (String) obj.get("orderSplitId");
                String storefrontId = (String) obj.get("storefrontId");
                String imageArr = (String)obj.get("imageArr");
                example = new Example(MendOrder.class);
                mendOrder = new MendOrder();
                mendOrder.setNumber("DJZX" + 40000 + iBillMendOrderMapper.selectCountByExample(example));//订单号
                mendOrder.setHouseId(houseId);
                mendOrder.setApplyMemberId(member.getId());
                mendOrder.setType(2);//2:退材料
                mendOrder.setOrderName("业主退材料退货退款");
                mendOrder.setState(0);//生成中
                mendOrder.setStorefrontId(storefrontId);
                mendOrder.setImageArr(imageArr);//相关凭证
                mendOrder.setOrderId(orderSplitId);//订单申请ID(要货表中的订单ID）
                Double totalRransportationCost = 0.0;//可退运费
                Double totalStevedorageCost = 0.0;//可退搬运费
                Double actualTotalAmount=0.0;//退货总额
                //获取商品信息
                JSONArray orderItemProductList = obj.getJSONArray("orderItemProductList");
                for (int j = 0; j < orderItemProductList.size(); j++) {
                    JSONObject productObj = (JSONObject) orderItemProductList.get(j);
                    String orderSplitItemId=(String)productObj.get("orderSplitItemId");//要货单详情Id
                    String productId=(String)productObj.get("productId");//产品ID
                    Double returnCount=productObj.getDouble("returnCount");//退货量
                    RefundOrderItemDTO refundOrderItemDTO=billDjDeliverOrderSplitItemMapper.queryReturnRefundOrderItemInfo(orderSplitItemId);
                    if(refundOrderItemDTO==null){
                        return ServerResponse.createByErrorMessage("未找到对应的退货单信息");
                    }
                    setProductInfo(refundOrderItemDTO,address);
                    Double surplusCount=refundOrderItemDTO.getSurplusCount();
                    logger.info("退货量{}，可退量{}",returnCount,surplusCount);
                    if(MathUtil.sub(surplusCount,returnCount)<0){
                        return ServerResponse.createByErrorMessage("退货量大于可退货量，不能退。");
                    }
                    refundOrderItemDTO.setReturnCount(returnCount);
                    //修改要货订单中的退货量为最新的退货量
                    OrderSplitItem orderSplitItem=billDjDeliverOrderSplitItemMapper.selectByPrimaryKey(refundOrderItemDTO.getOrderSplitItemId());
                    orderSplitItem.setId(refundOrderItemDTO.getOrderSplitItemId());
                    orderSplitItem.setReturnCount(MathUtil.add(orderSplitItem.getReturnCount(),returnCount));
                    billDjDeliverOrderSplitItemMapper.updateByPrimaryKeySelective(orderSplitItem);
                    Double price = refundOrderItemDTO.getPrice();//购买单价
                    Double shopCount=refundOrderItemDTO.getShopCount();//购买数据
                    Double transportationCost=refundOrderItemDTO.getTransportationCost();//运费
                    Double stevedorageCost=refundOrderItemDTO.getStevedorageCost();//搬运费
                    //计算可退运费
                    if(transportationCost>0.0) {
                        Double returnRransportationCost = CommonUtil.getReturnRransportationCost(price, shopCount, returnCount,transportationCost);
                        totalRransportationCost=MathUtil.add(totalRransportationCost,returnRransportationCost);
                        refundOrderItemDTO.setTransportationCost(returnRransportationCost);
                    }
                    //计算可退搬费
                    if(stevedorageCost>0.0){
                        String isUpstairsCost=refundOrderItemDTO.getIsUpstairsCost();//是否按1层收取上楼费
                        Double moveCost=refundOrderItemDTO.getMoveCost();//每层搬超级赛亚人费
                        Double returnStevedorageCost=CommonUtil.getReturnStevedorageCost(elevator,floor,isUpstairsCost,moveCost,returnCount);
                        totalStevedorageCost=MathUtil.add(totalStevedorageCost,returnStevedorageCost);
                        refundOrderItemDTO.setStevedorageCost(totalStevedorageCost);
                    }
                    //优惠金额
                    Double discountPrice=0d;
                    if(refundOrderItemDTO.getDiscountPrice()!=null&&refundOrderItemDTO.getDiscountPrice()>0.0){
                        discountPrice=MathUtil.mul(MathUtil.div(refundOrderItemDTO.getDiscountPrice(),refundOrderItemDTO.getShopCount()),returnCount);
                        refundOrderItemDTO.setDiscountPrice(discountPrice);
                    }
                    refundOrderItemDTO.setOrderItemId(refundOrderItemDTO.getOrderSplitItemId());//传退货单详情ID
                    //添回退款申请明细信息
                    MendMateriel mendMateriel = saveBillMendMaterial(mendOrder,cityId,refundOrderItemDTO,productId,returnCount);
                    actualTotalAmount=MathUtil.add(actualTotalAmount,MathUtil.mul(price,returnCount));
                    actualTotalAmount=MathUtil.sub(actualTotalAmount,discountPrice);

                }
                mendOrder.setModifyDate(new Date());
                mendOrder.setState(1);
                mendOrder.setCarriage(totalRransportationCost);//运费
                mendOrder.setTotalStevedorageCost(totalStevedorageCost);//搬运费
                mendOrder.setActualTotalAmount(actualTotalAmount);//退货总额
                mendOrder.setTotalAmount(MathUtil.sub(MathUtil.sub(actualTotalAmount,totalRransportationCost),totalStevedorageCost));//实退款，含运费(去掉运费搬运费后的可得钱)
                //添加对应的申请退货单信息
                iBillMendOrderMapper.insert(mendOrder);
                //添加对应的流水记录节点信息
                updateOrderProgressInfo(mendOrder.getId(),"2","REFUND_AFTER_SALES","RA_001",member.getId());
                updateOrderProgressInfo(mendOrder.getId(),"2","REFUND_AFTER_SALES","RA_002",member.getId());
            }
        }
        return  ServerResponse.createBySuccessMessage("提交成功");
    }


    /**
     * 查询补退人工历史记录列表
     * @param pageDTO
     * @param cityId
     * @param houseId
     * @param searchType 1.工匠补人工，2业主退人工，3业主补人工 4大管家查看补人工，5大管家查看退人工
     * @return
     */
    public ServerResponse<PageInfo> queryRetrunWorkerHistoryList(PageDTO pageDTO,String userToken,String cityId,String houseId,String searchType){
        try{
            Object object = getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = (Member) object;//对应的工匠或业主信息
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            logger.info("queryRetrunWorkerHistoryList查询退人工历史记录列表：city={},houseId={}",cityId,houseId);
            List<ReturnWorkOrderDTO> reuturnWokerList=iBillChangeOrderMapper.queryReturnWorkerList(houseId,member.getId(),searchType);
            if(reuturnWokerList!=null&&reuturnWokerList.size()>0){
                for(ReturnWorkOrderDTO returnWorkOrderDTO:reuturnWokerList){
                    String  repairWorkOrderId=returnWorkOrderDTO.getRepairWorkOrderId();
                    //查询对应的流水节点信息(根据订单ID）
                    List<OrderProgressDTO> orderProgressDTOList=iBillOrderProgressMapper.queryOrderProgressListByOrderId(repairWorkOrderId,null);//退款历史记录
                    if(orderProgressDTOList!=null&&orderProgressDTOList.size()>0){//判断最后节点，及剩余处理时间
                        OrderProgressDTO orderProgressDTO=orderProgressDTOList.get(orderProgressDTOList.size()-1);
                        returnWorkOrderDTO.setStateName(CommonUtil.getStateWorkerName(orderProgressDTO.getNodeCode()));
                    }else{
                        returnWorkOrderDTO.setStateName(CommonUtil.getChangeStateName(returnWorkOrderDTO.getState(),returnWorkOrderDTO.getType()));
                    }
                }
            }
            PageInfo pageResult = new PageInfo(reuturnWokerList);
            return  ServerResponse.createBySuccess("查询成功",pageResult);
        }catch (Exception e){
            logger.error("查询退人工历史记录列表异常：",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 退人工详情页面
     * @param cityId
     * @param repairWorkOrderId
     * @return
     */
    public ServerResponse queryRetrunWorkerHistoryDetail(String cityId,String repairWorkOrderId){
        try{
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            logger.info("退人工详情页面：repairWorkOrderId={}",repairWorkOrderId);
            ReturnWorkOrderDTO returnWorkOrderDTO=iBillChangeOrderMapper.queryReturnWorkerInfo(repairWorkOrderId);

            List<OrderProgressDTO> orderProgressDTOList=iBillOrderProgressMapper.queryOrderProgressListByOrderId(repairWorkOrderId,null);//退款历史记录
            if(orderProgressDTOList!=null&&orderProgressDTOList.size()>0){//判断最后节点，及剩余处理时间
                OrderProgressDTO orderProgressDTO=orderProgressDTOList.get(orderProgressDTOList.size()-1);
                returnWorkOrderDTO.setStateName(CommonUtil.getStateWorkerName(orderProgressDTO.getNodeCode()));
                returnWorkOrderDTO.setRepairNewNode(orderProgressDTO.getNodeName());
                returnWorkOrderDTO.setAssociatedOperation(orderProgressDTO.getAssociatedOperation());
                returnWorkOrderDTO.setAssociatedOperationName(orderProgressDTO.getAssociatedOperationName());

                //查询业主的电话
                String mobile="";
                Member member=iBillMemberMapper.selectByPrimaryKey(returnWorkOrderDTO.getMemberId());
                if(member!=null&&StringUtils.isNotBlank(member.getMobile())){
                    mobile=member.getMobile();
                }
                //流水节点放入
                returnWorkOrderDTO.setOrderProgressList(setOrderWorkerProgressList(orderProgressDTOList,orderProgressDTO,mobile));

            }else{
                //状态优化
                returnWorkOrderDTO.setStateName(CommonUtil.getChangeStateName(returnWorkOrderDTO.getState(),returnWorkOrderDTO.getType()));
            }

            returnWorkOrderDTO.setMobile(returnWorkOrderDTO.getWorkerMobile());//拨打电话
            returnWorkOrderDTO.setRepairWorkOrderNumber(returnWorkOrderDTO.getRepairWorkOrderId());
            //查询对应的需审核的商品信息(根据变列申请单ID）
            Example example = new Example(MendOrder.class);
            example.createCriteria()
                    .andEqualTo(MendOrder.CHANGE_ORDER_ID, repairWorkOrderId)
                    .andEqualTo(MendOrder.DATA_STATUS, 0);
            MendOrder mendOrder=iBillMendOrderMapper.selectOneByExample(example);
            if(mendOrder!=null&&StringUtils.isNotBlank(mendOrder.getId())){
                returnWorkOrderDTO.setRepairWorkOrderNumber(mendOrder.getNumber());//设置申请单号
                returnWorkOrderDTO.setActualTotalAmount(mendOrder.getActualTotalAmount());
                returnWorkOrderDTO.setTotalAmount(mendOrder.getTotalAmount());
                returnWorkOrderDTO.setMendOrderId(mendOrder.getId());
                Member member=iBillMemberMapper.selectByPrimaryKey(mendOrder.getApplyMemberId());
                returnWorkOrderDTO.setApplyMemberName(member.getName());//
                if("2".equals(returnWorkOrderDTO.getType())||"3".equals(returnWorkOrderDTO.getType())){//业主补、退人工
                    returnWorkOrderDTO.setApplyMemberTypeName("业主");//申请人类型名称
                    returnWorkOrderDTO.setWorkerTypeColor("#D67DAE");
                }else{
                    returnWorkOrderDTO.setApplyMemberTypeName(returnWorkOrderDTO.getWorkTypeName());//申请人类型名称
                    returnWorkOrderDTO.setWorkerTypeColor(returnWorkOrderDTO.getWorkerTypeColor());
                }
            }
            List<RefundRepairOrderMaterialDTO> repairWorkerList=iBillMendWorkerMapper.queryBillMendOrderId(mendOrder.getId());//退款商品列表查询
            getRepairOrderProductList(repairWorkerList,address);
            returnWorkOrderDTO.setOrderWorkerList(repairWorkerList);//设置人工商品信息
            return  ServerResponse.createBySuccess("查询成功",returnWorkOrderDTO);
        }catch (Exception e){
            logger.error("queryRetrunWorkerHistoryDetail查询退人工历史记录详情异常：",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    //判断对就原按钮显不显示
    private List<OrderProgressDTO> setOrderWorkerProgressList(List<OrderProgressDTO> orderProgressDTOList,OrderProgressDTO orderProgressDTO,String mobile){
        List<OrderProgressDTO> list=new ArrayList<>();
        for(int i=0;i<orderProgressDTOList.size();i++){
            OrderProgressDTO op=orderProgressDTOList.get(i);
            op.setNodeStatus(1);//先打勾
            //业主已拒绝
            if("RA_022".equals(op.getNodeCode())){
                op.setNodeStatus(3);//打红叉
                op.setMobile(mobile);//拨打电话，大管家审核 不通过
            }
            if("RA_017".equals(op.getNodeCode())){
                op.setNodeStatus(3);//工匠已拒绝，打红叉
            }
            //如果最新节点为RA_012，RA_016您的退人工申请已提交 ,为RA_020，RA_021您的补人人工申请已提交状态，，且当前显示节点为RA_012,RA_020的话，则显示“撤销按钮”,否则，则不显示
            if(!(("RA_012".equals(orderProgressDTO.getNodeCode())||"RA_016".equals(orderProgressDTO.getNodeCode())
                    ||"RA_020".equals(orderProgressDTO.getNodeCode())||"RA_021".equals(orderProgressDTO.getNodeCode())))
                    &&("RA_012".equals(op.getNodeCode())||"RA_020".equals(op.getNodeCode()))){
                op.setAssociatedOperation("");//清空按钮数据
                op.setAssociatedOperationName("");//清空按钮数据
            }

            list.add(op);
        }


        return list;
    }

    /**
     *
     * @param cityId
     * @param repairWorkOrderId
     * @return
     */
    public ServerResponse cancelWorkerApplication(String cityId,String repairWorkOrderId){

        //1.撤回变更申请单即可
        ChangeOrder changeOrder = iBillChangeOrderMapper.selectByPrimaryKey(repairWorkOrderId);
        if(changeOrder==null){
            return ServerResponse.createBySuccessMessage("未找到可撤销的变更单");
        }
        if(changeOrder!=null&&"7".equals(changeOrder.getState())){
            return ServerResponse.createBySuccess("变更申请单已撤销，请勿重复申请。");
        }
        changeOrder.setState(7);
        changeOrder.setModifyDate(new Date());
        iBillChangeOrderMapper.updateByPrimaryKeySelective(changeOrder);
        //2.查询商品申请单(改为撤回状态)
        Example example = new Example(MendOrder.class);
        example.createCriteria()
                .andEqualTo(MendOrder.CHANGE_ORDER_ID, repairWorkOrderId)
                .andEqualTo(MendOrder.DATA_STATUS, 0);
        MendOrder mendOrder=iBillMendOrderMapper.selectOneByExample(example);
        if(mendOrder!=null&&StringUtils.isNotBlank(mendOrder.getId())){
           // mendRecordAPI.backOrder(mendOrder.getId(),2);//有变更订单的撤销
            mendOrder.setState(5);//已撤回
            mendOrder.setModifyDate(new Date());
            iBillMendOrderMapper.updateByPrimaryKeySelective(mendOrder);
        }
        //3.判断是否有需要撤回的任务,修改状态为已处理
        example=new Example(TaskStack.class);
        example.createCriteria().andEqualTo(TaskStack.HOUSE_ID,changeOrder.getHouseId())
                .andEqualTo(TaskStack.STATE,0)
                .andEqualTo(TaskStack.DATA,repairWorkOrderId);
        TaskStack taskStack=iBillTaskStackMapper.selectOneByExample(example);
        if(taskStack!=null&&StringUtils.isNotBlank(taskStack.getId())){
            taskStack.setState(1);
            taskStack.setModifyDate(new Date());
            iBillTaskStackMapper.updateByPrimaryKeySelective(taskStack);
        }
        if(changeOrder.getType()==1){
            //补人工后，记录流水
            updateOrderProgressInfo(changeOrder.getId(),"3","REFUND_AFTER_SALES","RA_019",changeOrder.getMemberId());//撤销退人工申请

        }else{
            //退人工后，记录流水
            updateOrderProgressInfo(changeOrder.getId(),"2","REFUND_AFTER_SALES","RA_019",changeOrder.getMemberId());//撤销退人工申请

        }
        return ServerResponse.createBySuccess("撤销成功");
    }

    /**
     * 查询对应补、退单详情信息（大管家，或待审核页面）
     * @param cityId
     * @param repairWorkOrderId
     * @return
     */
    public ServerResponse searchWorkerHistoryInfobyChangeOrderId(String cityId,String repairWorkOrderId) {
        try{
            //根据变更单ID查询对应的信息
            ReturnWorkOrderDTO returnWorkOrderDTO=iBillChangeOrderMapper.queryReturnWorkerInfo(repairWorkOrderId);
            Example example = new Example(MendOrder.class);
            example.createCriteria()
                    .andEqualTo(MendOrder.CHANGE_ORDER_ID, repairWorkOrderId)
                    .andEqualTo(MendOrder.DATA_STATUS, 0);
            MendOrder mendOrder=iBillMendOrderMapper.selectOneByExample(example);
            if(mendOrder!=null&&StringUtils.isNotBlank(mendOrder.getId())){
                returnWorkOrderDTO.setRepairWorkOrderNumber(mendOrder.getNumber());//设置申请单号
                returnWorkOrderDTO.setTotalAmount(mendOrder.getTotalAmount());
                returnWorkOrderDTO.setApplyMemberId(mendOrder.getApplyMemberId());//申请人ID
                Member member=iBillMemberMapper.selectByPrimaryKey(mendOrder.getApplyMemberId());
                returnWorkOrderDTO.setApplyMemberName(member.getName());//
                if("2".equals(returnWorkOrderDTO.getType())||"3".equals(returnWorkOrderDTO.getType())){//业主补、退人工
                    returnWorkOrderDTO.setApplyMemberTypeName("业主");//申请人类型名称
                    returnWorkOrderDTO.setWorkerTypeColor("#D67DAE");
                }else{
                    returnWorkOrderDTO.setApplyMemberTypeName(returnWorkOrderDTO.getWorkTypeName());//申请人类型名称
                    returnWorkOrderDTO.setWorkerTypeColor(returnWorkOrderDTO.getWorkerTypeColor());
                }
                //查询对应的流水节点信息(根据订单ID）
                List<OrderProgressDTO> orderProgressDTOList=iBillOrderProgressMapper.queryOrderProgressListByOrderId(repairWorkOrderId,null);//退款历史记录
                if(orderProgressDTOList!=null&&orderProgressDTOList.size()>0){//判断最后节点，及剩余处理时间
                    OrderProgressDTO orderProgressDTO=orderProgressDTOList.get(orderProgressDTOList.size()-1);
                    returnWorkOrderDTO.setStateName(CommonUtil.getStateWorkerName(orderProgressDTO.getNodeCode()));
                }else{
                    returnWorkOrderDTO.setStateName(CommonUtil.getChangeStateName(returnWorkOrderDTO.getState(),returnWorkOrderDTO.getType()));
                }
                String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
                List<RefundRepairOrderMaterialDTO> repairWorkerList=iBillMendWorkerMapper.queryBillMendOrderId(mendOrder.getId());//退款商品列表查询
                getRepairOrderProductList(repairWorkerList,address);
                returnWorkOrderDTO.setOrderWorkerList(repairWorkerList);//设置人工商品信息
            }

            return ServerResponse.createBySuccess("查询成功",returnWorkOrderDTO);

        }catch (Exception e){
            logger.error("查询异常：",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 查询待审核的补人工订单ID
     * @param cityId
     * @param mendOrderId
     * @return
     */
    public ServerResponse searchAuditInfoByMendOrderId(String cityId,String mendOrderId){
        try{
            MendOrder mendOrder=iBillMendOrderMapper.selectByPrimaryKey(mendOrderId);
            return searchWorkerHistoryInfobyChangeOrderId(cityId,mendOrder.getChangeOrderId());
           /* //查询对应的需审核的商品信息(根据变列申请单ID
            ReturnWorkOrderDTO returnWorkOrderDTO=iBillChangeOrderMapper.queryReturnWorkerInfo(mendOrder.getChangeOrderId());
            if(mendOrder!=null&&StringUtils.isNotBlank(mendOrder.getId())){
                returnWorkOrderDTO.setRepairWorkOrderNumber(mendOrder.getNumber());//设置申请单号
                returnWorkOrderDTO.setTotalAmount(mendOrder.getTotalAmount());
                returnWorkOrderDTO.setApplyMemberId(mendOrder.getApplyMemberId());//申请人ID
                Member member=iBillMemberMapper.selectByPrimaryKey(mendOrder.getApplyMemberId());
                returnWorkOrderDTO.setApplyMemberName(member.getName());//
                if("2".equals(returnWorkOrderDTO.getType())||"3".equals(returnWorkOrderDTO.getType())){//业主补、退人工
                    returnWorkOrderDTO.setApplyMemberTypeName("业主");//申请人类型名称
                }else{
                    returnWorkOrderDTO.setApplyMemberTypeName(returnWorkOrderDTO.getWorkTypeName());//申请人类型名称
                }
                //查询对应的流水节点信息(根据订单ID）
                List<OrderProgressDTO> orderProgressDTOList=iBillOrderProgressMapper.queryOrderProgressListByOrderId(mendOrder.getChangeOrderId());//退款历史记录
                if(orderProgressDTOList!=null&&orderProgressDTOList.size()>0){//判断最后节点，及剩余处理时间
                    OrderProgressDTO orderProgressDTO=orderProgressDTOList.get(orderProgressDTOList.size()-1);
                    returnWorkOrderDTO.setStateName(CommonUtil.getStateWorkerName(orderProgressDTO.getNodeCode()));
                }else{
                    returnWorkOrderDTO.setStateName(CommonUtil.getChangeStateName(returnWorkOrderDTO.getState(),returnWorkOrderDTO.getType()));
                }

            }
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            List<RefundRepairOrderMaterialDTO> repairWorkerList=iBillMendWorkerMapper.queryBillMendOrderId(mendOrder.getId());//退款商品列表查询
            getRepairOrderProductList(repairWorkerList,address);
            returnWorkOrderDTO.setOrderWorkerList(repairWorkerList);//设置人工商品信息
            return ServerResponse.createBySuccess("查询成功",returnWorkOrderDTO);*/

        }catch (Exception e){
            logger.error("查询异常：",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }

    }



    /**
     * 退人工--查询符合条件的可退人工商品
     * @param userToken 用户token
     * @param cityId  城市ID
     * @param houseId 房子ID
     * @param workerTypeId 工种ID
     * @param searchKey 商品名称
     * @return
     */
    public ServerResponse queryWorkerProductList(String userToken,String cityId,String houseId,
                                                 String workerTypeId,String searchKey){
        try{
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            logger.info("退人工商品查询：userToken={}，cityId={},houseId={},workerTypeId={},searchKey={}",userToken,cityId,houseId,workerTypeId,searchKey);
            //PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<RefundOrderItemDTO> orderItemList=refundAfterSalesMapper.queryWorkerProductList(houseId,workerTypeId,searchKey);
            getProductList(orderItemList,address);

            //PageInfo pageResult = new PageInfo(orderItemList);
            return ServerResponse.createBySuccess("查询成功",orderItemList);
        }catch (Exception e){
            logger.error("查询可退人工商品异常：",e);
            return ServerResponse.createByErrorMessage("查询可退人工商品失败");
        }
    }



}
