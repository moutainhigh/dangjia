package com.dangjia.acg.service.refund;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.refund.RefundOrderDTO;
import com.dangjia.acg.dto.refund.RefundOrderItemDTO;
import com.dangjia.acg.mapper.order.IBillOrderProgressMapper;
import com.dangjia.acg.mapper.refund.*;
import com.dangjia.acg.modle.attribute.AttributeValue;
import com.dangjia.acg.modle.brand.Brand;
import com.dangjia.acg.modle.brand.Unit;
import com.dangjia.acg.modle.deliver.OrderItem;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.order.OrderProgress;
import com.dangjia.acg.modle.product.BasicsGoods;
import com.dangjia.acg.modle.product.DjBasicsProductTemplate;
import com.dangjia.acg.modle.repair.MendMateriel;
import com.dangjia.acg.modle.repair.MendOrder;
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
import java.util.Date;
import java.util.List;


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
    private IBillAttributeValueMapper iBillAttributeValueMapper;

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
    private IBillOrderItemMapper iBillOrderItemMapper;

    @Autowired
    private IBillOrderProgressMapper iBillOrderProgressMapper;
    /**
     * 查询可退款的商品
     * @param cityId 城市ID
     * @param houseId 房屋ID
     * @return
     */
    public ServerResponse<PageInfo> queryRefundOnlyOrderList(PageDTO pageDTO, String cityId, String houseId, String searchKey){
        try{
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            logger.info("queryRefundOrderList查询可退款的商品：city={},houseId{}",cityId,houseId);
            List<RefundOrderDTO> orderlist = refundAfterSalesMapper.queryRefundOrderList(houseId,searchKey);
            if(orderlist!=null&&orderlist.size()>0){
                String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
                for(RefundOrderDTO order:orderlist){
                    String orderId=order.getOrderId();
                    List<RefundOrderItemDTO> orderItemList=refundAfterSalesMapper.queryRefundOrderItemList(orderId,searchKey);
                    getProductList(orderItemList,address);
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
                String productTemplateId=ap.getProductTemplateId();
                DjBasicsProductTemplate pt=iBillProductTemplateMapper.selectByPrimaryKey(productTemplateId);
                if(pt==null||StringUtils.isBlank(pt.getId())){
                    continue;
                }
                String image=ap.getImage();
                if (image == null) {
                    image=pt.getImage();
                }
                ap.setConvertUnit(pt.getConvertUnit());
                ap.setCost(pt.getCost());
                ap.setCategoryId(pt.getCategoryId());
                //添加图片详情地址字段
                String[] imgArr = image.split(",");
                StringBuilder imgStr = new StringBuilder();
                StringBuilder imgUrlStr = new StringBuilder();
                StringTool.getImages(address, imgArr, imgStr, imgUrlStr);
                ap.setImageUrl(imgStr.toString());//图片详情地址设置
                //查询单位
                if(pt.getUnitId()!=null&& StringUtils.isNotBlank(pt.getUnitId())){
                    Unit unit= iBillUnitMapper.selectByPrimaryKey(pt.getUnitId());
                    ap.setUnitId(pt.getUnitId());
                    ap.setUnitName(unit!=null?unit.getName():"");
                }
                //查询规格名称
                if (StringUtils.isNotBlank(pt.getValueIdArr())) {
                    ap.setValueIdArr(pt.getValueIdArr());
                    ap.setValueNameArr(getNewValueNameArr(pt.getValueIdArr()));
                }
                BasicsGoods goods=iBillBasicsGoodsMapper.selectByPrimaryKey(pt.getGoodsId());
                if(StringUtils.isNotBlank(goods.getBrandId())){
                    Brand brand=iBillBrandMapper.selectByPrimaryKey(goods.getBrandId());
                    ap.setBrandId(goods.getId());
                    ap.setBrandName(brand!=null?brand.getName():"");
                }
            }
        }
    }
    /**
     * 获取对应的属性值信息
     * @param valueIdArr
     * @return
     */
    private String getNewValueNameArr(String valueIdArr){
        String strNewValueNameArr = "";
        String[] newValueNameArr = valueIdArr.split(",");
        for (int i = 0; i < newValueNameArr.length; i++) {
            String valueId = newValueNameArr[i];
            if (StringUtils.isNotBlank(valueId)) {
                AttributeValue attributeValue = iBillAttributeValueMapper.selectByPrimaryKey(valueId);
                if(attributeValue!=null&&StringUtils.isNotBlank(attributeValue.getName())){
                    if (i == 0) {
                        strNewValueNameArr = attributeValue.getName();
                    } else {
                        strNewValueNameArr = strNewValueNameArr + "," + attributeValue.getName();
                    }
                }

            }
        }
        return strNewValueNameArr;
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
        Member member = (Member) object;
        MendOrder mendOrder;
        Example example;
        JSONArray orderArrayList=JSONArray.parseArray(orderProductAttr);
        if(orderArrayList!=null&&orderArrayList.size()>0) {
            for (int i = 0; i < orderArrayList.size(); i++) {
                JSONObject obj = (JSONObject) orderArrayList.get(i);
                String orderId = (String) obj.get("orderId");
                String storefrontId = (String) obj.get("storefrontId");
                example = new Example(MendOrder.class);
                mendOrder = new MendOrder();
                mendOrder.setNumber("DJZX" + 40000 + iBillMendOrderMapper.selectCountByExample(example));//订单号
                mendOrder.setHouseId(houseId);
                mendOrder.setApplyMemberId(member.getId());
                mendOrder.setType(4);//业主退材料
                mendOrder.setOrderName("业主退材料");
                mendOrder.setState(0);//生成中
                mendOrder.setStorefrontId(storefrontId);
                mendOrder.setOrderId(orderId);
                mendOrder.setTotalAmount(0.0);
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
                    Double surplusCount=refundOrderItemDTO.getSurplusCount();
                    logger.info("退货量{}，可退量{}",returnCount,surplusCount);
                    if((new BigDecimal(surplusCount).compareTo(new BigDecimal(returnCount)))<0){
                        return ServerResponse.createByErrorMessage("退货量大于可退货量，不能退。");
                    }
                    refundOrderItemDTO.setReturnCount(returnCount);
                    //修改订单中的退货量为当前退货的量
                    OrderItem orderItem=new OrderItem();
                    orderItem.setId(refundOrderItemDTO.getOrderItemId());
                    orderItem.setReturnCount(returnCount);
                    iBillOrderItemMapper.updateByPrimaryKeySelective(orderItem);
                    //添回退款申请明细信息
                    MendMateriel mendMateriel = saveBillMendMaterial(mendOrder,cityId,refundOrderItemDTO,productId,surplusCount);
                    mendOrder.setTotalAmount(mendOrder.getTotalAmount() + mendMateriel.getTotalPrice());//修改总价
                }
                mendOrder.setModifyDate(new Date());
                mendOrder.setState(1);
                //添加对应的申请退货单信息
                iBillMendOrderMapper.insert(mendOrder);
                //添加对应的流水记录节点信息
                updateOrderProgressInfo(mendOrder.getId(),"2","REFUND_AFTER_SALES","RA_001",member.getId());
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
        mendMateriel.setTotalPrice(returnCount.doubleValue() * refundOrderItemDTO.getPrice());
        mendMateriel.setProductType(Integer.parseInt(refundOrderItemDTO.getProductType()));//0：材料；1：包工包料
        mendMateriel.setCategoryId(refundOrderItemDTO.getCategoryId());
        mendMateriel.setImage(refundOrderItemDTO.getImage());
        mendMateriel.setStorefrontId(refundOrderItemDTO.getStorefrontId());
        mendMateriel.setOrderItemId(refundOrderItemDTO.getOrderItemId());
        mendMateriel.setShopCount(refundOrderItemDTO.getShopCount());
        Unit unit = iBillUnitMapper.selectByPrimaryKey(refundOrderItemDTO.getConvertUnit());
        if (unit!=null&&unit.getType() == 1) {
            mendMateriel.setShopCount(Math.ceil(refundOrderItemDTO.getShopCount()));
        }
        mendMateriel.setMendOrderId(mendOrder.getId());
        mendMateriel.setProductId(productId);
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

}
