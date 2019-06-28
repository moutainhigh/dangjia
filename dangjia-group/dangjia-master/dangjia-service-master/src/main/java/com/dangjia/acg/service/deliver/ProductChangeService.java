package com.dangjia.acg.service.deliver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.basics.ProductAPI;
import com.dangjia.acg.api.basics.UnitAPI;
import com.dangjia.acg.api.data.ForMasterAPI;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.BaseException;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.MathUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.deliver.ProductChangeDTO;
import com.dangjia.acg.dto.deliver.ProductChangeItemDTO;
import com.dangjia.acg.dto.deliver.ProductChangeOrderDTO;
import com.dangjia.acg.dto.deliver.ProductOrderDTO;
import com.dangjia.acg.mapper.deliver.IProductChangeMapper;
import com.dangjia.acg.mapper.deliver.IProductChangeOrderMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.house.IWarehouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.pay.IBusinessOrderMapper;
import com.dangjia.acg.mapper.worker.IWorkerDetailMapper;
import com.dangjia.acg.modle.basics.Goods;
import com.dangjia.acg.modle.basics.Product;
import com.dangjia.acg.modle.brand.Unit;
import com.dangjia.acg.modle.deliver.ProductChange;
import com.dangjia.acg.modle.deliver.ProductChangeOrder;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.house.Warehouse;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.pay.BusinessOrder;
import com.dangjia.acg.modle.worker.WorkerDetail;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.dangjia.acg.util.Utils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * author: Yinjianbo
 * Date: 2019-5-11
 */
@Service
public class ProductChangeService {

    @Autowired
    private CraftsmanConstructionService constructionService;
    @Autowired
    private IProductChangeMapper productChangeMapper;
    @Autowired
    private IProductChangeOrderMapper productChangeOrderMapper;
    @Autowired
    private ProductAPI productAPI;
    @Autowired
    private IWarehouseMapper warehouseMapper;
    @Autowired
    private IHouseMapper houseMapper;
    @Autowired
    private IMemberMapper memberMapper;
    @Autowired
    private IWorkerDetailMapper workerDetailMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IBusinessOrderMapper businessOrderMapper;
    @Autowired
    private UnitAPI unitAPI;

    @Autowired
    private ForMasterAPI forMasterAPI;
    private static Logger LOG = LoggerFactory.getLogger(ProductChangeService.class);

    /**
     * 添加更换商品
     *
     * @param request
     * @param userToken
     * @param houseId
     * @param srcProductId
     * @param destProductId
     * @param srcSurCount
     * @param productType   0:材料 1：服务
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse insertProductChange(HttpServletRequest request, String userToken, String houseId, String srcProductId, String destProductId, Double srcSurCount, Integer productType) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member operator = (Member) object;
            // 原商品仓库
            Warehouse oldWareHouse = warehouseMapper.getByProductId(srcProductId, houseId);
            // 新商品仓库
            Warehouse wareHouse = warehouseMapper.getByProductId(destProductId, houseId);
            // 原商品
            ServerResponse srcResponse = productAPI.getProductById(request, srcProductId);
            // 更换后的商品
            ServerResponse destResponse = productAPI.getProductById(request, destProductId);
            boolean flag = (srcResponse != null && srcResponse.getResultObj() != null && destResponse != null && destResponse.getResultObj() != null);
            Product srcProduct = null;
            Product destProduct = null;
            Unit srcUnit = null;
            Unit destUnit = null;
            if (flag) {
                srcProduct = JSON.parseObject(JSON.toJSONString(srcResponse.getResultObj()), Product.class);
                destProduct = JSON.parseObject(JSON.toJSONString(destResponse.getResultObj()), Product.class);
                Goods goods = forMasterAPI.getGoods(request.getParameter(Constants.CITY_ID), destProduct.getGoodsId());
                if (goods != null) {
                    productType = goods.getType();
                }
                // 查询转换单位
                ServerResponse srcUnitResponse = unitAPI.getUnitById(request, srcProduct.getConvertUnit());
                ServerResponse destUnitResponse = unitAPI.getUnitById(request, destProduct.getConvertUnit());
                boolean flagB = (srcUnitResponse != null && srcUnitResponse.getResultObj() != null && destUnitResponse != null && destUnitResponse.getResultObj() != null);
                if (flagB) {
                    srcUnit = JSON.parseObject(JSON.toJSONString(srcUnitResponse.getResultObj()), Unit.class);
                    destUnit = JSON.parseObject(JSON.toJSONString(destUnitResponse.getResultObj()), Unit.class);
                }
            }
            // 查询
            Example example = new Example(ProductChange.class);
            example.createCriteria()
                    .andEqualTo(ProductChange.HOUSE_ID, houseId)
                    .andEqualTo(ProductChange.MEMBER_ID, operator.getId())
                    .andEqualTo(ProductChange.SRC_PRODUCT_ID, srcProductId)
                    .andEqualTo(ProductChange.TYPE, 0);
            List<ProductChange> list = productChangeMapper.selectByExample(example);
            if (!CommonUtil.isEmpty(list)) {
                ProductChange change = list.get(0);
                change.setDestProductId(destProduct.getId());
                change.setDestProductSn(destProduct.getProductSn());
                change.setDestProductName(destProduct.getName());
                change.setDestUnitName(null == wareHouse ? destUnit.getName() : wareHouse.getUnitName());
                change.setDestPrice(destProduct.getPrice());
                change.setDestImage(destProduct.getImage());
                change.setDestSurCount(srcSurCount);
                // 类型 0 材料 1 服务
                change.setProductType(productType);
                // 差额单价
                BigDecimal price = BigDecimal.valueOf(MathUtil.sub(change.getDestPrice(), change.getSrcPrice()));
                // 差价= 更换数*差额单价
                BigDecimal differPrice = price.multiply(BigDecimal.valueOf(srcSurCount));
                // 差价默认为可以更换数量的最大值
                change.setDifferencePrice(differPrice);
                change.setModifyDate(new Date());
                productChangeMapper.updateByPrimaryKey(change);
            } else {
                ProductChange productChange = new ProductChange();
                productChange.setMemberId(operator.getId());
                productChange.setHouseId(houseId);
                productChange.setCategoryId(StringUtils.isNotBlank(srcProduct.getCategoryId()) ? srcProduct.getCategoryId() : "");
                // src
                productChange.setSrcProductId(srcProduct.getId());
                productChange.setSrcProductSn(srcProduct.getProductSn());
                productChange.setSrcProductName(srcProduct.getName());
                productChange.setSrcPrice(srcProduct.getPrice());
                productChange.setSrcSurCount(srcSurCount);
                // 仓库商品有单位，则取仓库单位，反之取商品转换单位
                productChange.setSrcUnitName(null == oldWareHouse ? srcUnit.getName() : oldWareHouse.getUnitName());
                productChange.setSrcImage(srcProduct.getImage());
                // dest
                productChange.setDestProductId(destProduct.getId());
                productChange.setDestProductSn(destProduct.getProductSn());
                productChange.setDestProductName(destProduct.getName());
                productChange.setDestPrice(destProduct.getPrice());
                productChange.setDestUnitName(null == wareHouse ? destUnit.getName() : wareHouse.getUnitName());
                productChange.setDestImage(destProduct.getImage());
                // 更换数默认为0
                productChange.setDestSurCount(srcSurCount);
                // 未处理
                productChange.setType(0);
                // 类型 0 材料 1 服务
                productChange.setProductType(productType);
                // 差额单价
                BigDecimal price = BigDecimal.valueOf(MathUtil.sub(productChange.getDestPrice(), productChange.getSrcPrice()));
                // 差价= 更换数*差额单价
                BigDecimal differPrice = price.multiply(BigDecimal.valueOf(srcSurCount));
                // 差价默认为可以更换数量的最大值
                productChange.setDifferencePrice(differPrice);
                productChangeMapper.insert(productChange);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
        return ServerResponse.createBySuccessMessage("操作成功");
    }

    /**
     * 根据houseId查询商品更换列表
     *
     * @param request
     * @param userToken
     * @param houseId
     * @return
     */
    public ServerResponse queryChangeByHouseId(HttpServletRequest request, String userToken, String houseId) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member operator = (Member) object;
        Example example = new Example(ProductChange.class);
        example.createCriteria()
                .andEqualTo(ProductChange.HOUSE_ID, houseId)
                .andEqualTo(ProductChange.MEMBER_ID, operator.getId())
                .andEqualTo(ProductChange.TYPE, 0);
        List<ProductChange> list = productChangeMapper.selectByExample(example);
        return ServerResponse.createBySuccess("操作成功", list);
    }

    /**
     * 申请换货
     *
     * @param request
     * @param houseId
     * @return
     */
    public ServerResponse applyProductChange(HttpServletRequest request, String houseId) {
        try {
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            List<ProductChangeDTO> list = new ArrayList<>();
            ProductChangeOrderDTO productChangeOrderDTO = new ProductChangeOrderDTO();
            ProductChangeOrder order = insertProductChangeOrder(houseId);
            // 查询商品换货列表
            List<ProductChange> changeList = productChangeMapper.queryByHouseId(houseId, "0");
            for (ProductChange change : changeList) {
                ProductChangeDTO productChangeDTO = new ProductChangeDTO();
                productChangeDTO.setId(change.getId());
                productChangeDTO.setHouseId(change.getHouseId());
                productChangeDTO.setMemberId(StringUtils.isNotBlank(change.getMemberId()) ? change.getMemberId() : "");
                productChangeDTO.setCategoryId(StringUtils.isNotBlank(change.getCategoryId()) ? change.getCategoryId() : "");
                productChangeDTO.setSrcProductId(change.getSrcProductId());
                productChangeDTO.setSrcProductSn(change.getSrcProductSn());
                productChangeDTO.setSrcProductName(change.getSrcProductName());
                productChangeDTO.setSrcPrice(change.getSrcPrice());
                productChangeDTO.setSrcSurCount(change.getSrcSurCount());
                productChangeDTO.setSrcUnitName(change.getSrcUnitName());
                productChangeDTO.setSrcImage(address + change.getSrcImage());
                productChangeDTO.setDestProductId(change.getDestProductId());
                productChangeDTO.setDestProductSn(change.getDestProductSn());
                productChangeDTO.setDestProductName(change.getDestProductName());
                productChangeDTO.setDestPrice(change.getDestPrice());
                productChangeDTO.setDestSurCount(change.getDestSurCount());
                productChangeDTO.setDestUnitName(change.getDestUnitName());
                productChangeDTO.setDestImage(address + change.getDestImage());
                // 差额
                productChangeDTO.setDifferencePrice(change.getDifferencePrice());
                productChangeDTO.setType(change.getType());
                productChangeDTO.setCreateDate(change.getCreateDate());
                productChangeDTO.setModifyDate(change.getModifyDate());
                list.add(productChangeDTO);
            }
            if (null != order) {
                productChangeOrderDTO.setId(order.getId());
                productChangeOrderDTO.setNumber(order.getNumber());
                productChangeOrderDTO.setDifferencePrice(order.getDifferencePrice());
                productChangeOrderDTO.setType(order.getType());
                productChangeOrderDTO.setCreateDate(order.getCreateDate());
                productChangeOrderDTO.setModifyDate(order.getModifyDate());
            }
            productChangeOrderDTO.setProductChangeDTOList(list);
            return ServerResponse.createBySuccess("操作成功", productChangeOrderDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }

    /**
     * 设置商品更换数
     *
     * @param request
     * @param id
     * @param destSurCount
     * @param orderId
     * @return
     */
    public ServerResponse setDestSurCount(HttpServletRequest request, String id, Double destSurCount, String orderId) {
        try {

            ProductChange productChange = productChangeMapper.selectByPrimaryKey(id);
            // 查询订单表
            ProductChangeOrder order = productChangeOrderMapper.selectByPrimaryKey(orderId);
            House house = houseMapper.selectByPrimaryKey(productChange.getHouseId());
            request.setAttribute(Constants.CITY_ID, house.getCityId());
            if (null != productChange) {
                // 剩余数
                BigDecimal srcCount = BigDecimal.valueOf(productChange.getSrcSurCount());
                // 更换数
                BigDecimal destCount = BigDecimal.valueOf(destSurCount);
                if (destCount.compareTo(srcCount) == 1) {
                    return ServerResponse.createByErrorMessage("不能大于商品剩余数");
                }
                Unit unit;
                Product product = forMasterAPI.getProduct(house.getCityId(), productChange.getDestProductId());
                ServerResponse serverResponse = unitAPI.getUnitById(request, product.getConvertUnit());
                if (serverResponse.getResultObj() instanceof JSONObject) {
                    unit = JSON.parseObject(JSON.toJSONString(serverResponse.getResultObj()), Unit.class);
                } else {
                    unit = (Unit) serverResponse.getResultObj();
                }
                if (unit.getType() == 1) {
                    destSurCount = Math.ceil(destSurCount);
                }
                productChange.setDestSurCount(destSurCount);
                // 差额单价
                BigDecimal price = BigDecimal.valueOf(MathUtil.sub(productChange.getDestPrice(), productChange.getSrcPrice()));
                // 差价= 更换数*差额单价
                BigDecimal differPrice = price.multiply(BigDecimal.valueOf(destSurCount));
                productChange.setDifferencePrice(differPrice);
                productChange.setModifyDate(new Date());
                productChange.setOrderId(orderId);
                // 修改商品更换表
                productChangeMapper.updateByPrimaryKey(productChange);
                // 修改商品更换订单表
                // 计算总价差额
                order.setDifferencePrice(calcDifferPrice(productChange.getHouseId()));
                order.setModifyDate(new Date());
                productChangeOrderMapper.updateByPrimaryKey(order);

            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
        return ServerResponse.createBySuccessMessage("操作成功");
    }

    /**
     * 确定
     *
     * @param request
     * @param changeItemList
     * @param orderId
     * @return
     */
    public ServerResponse productSure(HttpServletRequest request, String changeItemList, String orderId) {
        try {
            // 查询订单表
            ProductChangeOrder order = productChangeOrderMapper.selectByPrimaryKey(orderId);
            // 房子信息
            House house = houseMapper.selectByPrimaryKey(order.getHouseId());
            request.setAttribute(Constants.CITY_ID, house.getCityId());
            if (StringUtil.isNotEmpty(changeItemList)) {
                JSONArray itemObjArr = JSON.parseArray(changeItemList);
                BigDecimal differencePrice = BigDecimal.ZERO;
                for (int i = 0; i < itemObjArr.size(); i++) {
                    JSONObject productObj = itemObjArr.getJSONObject(i);
                    String id = productObj.getString("id");
                    Double destSurCount = productObj.getDouble("destSurCount");
                    ProductChange productChange = productChangeMapper.selectByPrimaryKey(id);
                    // 剩余数
                    BigDecimal srcCount = BigDecimal.valueOf(productChange.getSrcSurCount());
                    // 更换数
                    BigDecimal destCount = BigDecimal.valueOf(destSurCount);
                    if (destCount.compareTo(srcCount) == 1) {
                        return ServerResponse.createByErrorMessage("不能大于商品剩余数");
                    }
                    Unit unit;
                    Product product = forMasterAPI.getProduct(house.getCityId(), productChange.getDestProductId());
                    ServerResponse serverResponse = unitAPI.getUnitById(request, product.getConvertUnit());
                    if (serverResponse.getResultObj() instanceof JSONObject) {
                        unit = JSON.parseObject(JSON.toJSONString(serverResponse.getResultObj()), Unit.class);
                    } else {
                        unit = (Unit) serverResponse.getResultObj();
                    }
                    if (unit.getType() == 1) {
                        destSurCount = Math.ceil(destSurCount);
                    }
                    productChange.setDestSurCount(destSurCount);
                    // 差额单价
                    BigDecimal price = BigDecimal.valueOf(MathUtil.sub(productChange.getDestPrice(), productChange.getSrcPrice()));
                    // 差价= 更换数*差额单价
                    BigDecimal differPrice = price.multiply(BigDecimal.valueOf(destSurCount));
                    productChange.setDifferencePrice(differPrice);
                    productChange.setModifyDate(new Date());
                    productChange.setOrderId(orderId);
                    // 修改商品更换表
                    productChangeMapper.updateByPrimaryKey(productChange);
                    // 累加总差价
                    differencePrice.add(differPrice);
                }
                // 计算总价差额
                order.setDifferencePrice(differencePrice);
                order.setModifyDate(new Date());
                productChangeOrderMapper.updateByPrimaryKey(order);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
        return ServerResponse.createBySuccessMessage("操作成功");

    }

    /**
     * 添加更换商品订单
     *
     * @param houseId
     * @return
     */
    public ProductChangeOrder insertProductChangeOrder(String houseId) {
        ProductChangeOrder order = null;
        try {
            // 查询db中是否有该房子的换货订单
            List<ProductChangeOrder> list = productChangeOrderMapper.queryOrderByHouseId(houseId, "0");
            // 当前房子有更换的商品时
            int count = productChangeMapper.queryProductChangeExist(houseId, null, "0");
            // 计算总价差额
            BigDecimal differPrice = calcDifferPrice(houseId);
            if (null != list && list.size() > 0) {
                order = list.get(0);
                order.setDifferencePrice(differPrice);
                order.setModifyDate(new Date());
                productChangeOrderMapper.updateByPrimaryKey(order);
            } else if (count > 0) {
                order = new ProductChangeOrder();
                order.setHouseId(houseId);
                // 默认未支付
                order.setType(0);
                order.setNumber(System.currentTimeMillis() + "-" + Utils.generateWord());
                order.setDifferencePrice(differPrice);
                productChangeOrderMapper.insert(order);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return order;
    }

    /**
     * 根据houseId查询更换商品订单
     *
     * @param houseId
     * @return
     */
    public ServerResponse queryOrderByHouseId(String houseId) {
        Example example = new Example(ProductChangeOrder.class);
        example.createCriteria()
                .andEqualTo(ProductChangeOrder.HOUSE_ID, houseId);
        List<ProductChangeOrder> list = productChangeOrderMapper.selectByExample(example);
        return ServerResponse.createBySuccess("操作成功", list);
    }

    /**
     * 补退差价回调
     *
     * @param request
     * @param id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse orderBackFun(HttpServletRequest request, String id) {
        String msg = "";
        try {
            // 查询
            ProductChangeOrder order = productChangeOrderMapper.selectByPrimaryKey(id);
            if (order != null) {
                String houseId = order.getHouseId();
                // 计算总价差额
                BigDecimal totalDifferPrice = calcDifferPrice(houseId);
                // 判断总价差额是否小于等于0
                if (totalDifferPrice.compareTo(BigDecimal.ZERO) == -1 || totalDifferPrice.compareTo(BigDecimal.ZERO) == 0) {
                    // 0未支付 1已支付 2已退款
                    order.setType(2);
                    // 总价差额不等于 0 时，退钱到业主钱包
                    if (totalDifferPrice.compareTo(BigDecimal.ZERO) != 0) {
                        // 取绝对值 -12 = 12
                        BigDecimal toDifferPrice = BigDecimal.valueOf(Math.abs(totalDifferPrice.doubleValue()));
                        /*退钱给业主*/
                        Member member = memberMapper.selectByPrimaryKey(houseMapper.selectByPrimaryKey(houseId).getMemberId());
                        BigDecimal haveMoney = member.getHaveMoney().add(toDifferPrice);
                        BigDecimal surplusMoney = member.getSurplusMoney().add(toDifferPrice);
                        //记录流水
                        WorkerDetail workerDetail = new WorkerDetail();
                        workerDetail.setName("业主换材料退款");
                        workerDetail.setWorkerId(member.getId());
                        workerDetail.setWorkerName(CommonUtil.isEmpty(member.getName()) ? member.getNickName() : member.getName());
                        workerDetail.setHouseId(houseId);
                        workerDetail.setMoney(toDifferPrice);
                        workerDetail.setApplyMoney(toDifferPrice);
                        workerDetail.setWalletMoney(surplusMoney);
                        workerDetail.setState(4);//进钱//业主退
                        workerDetailMapper.insert(workerDetail);

                        member.setHaveMoney(haveMoney);
                        member.setSurplusMoney(surplusMoney);
                        memberMapper.updateByPrimaryKeySelective(member);
                        msg = "您已成功更换商品，系统将在24小时内退差价进您的钱包。";
                    }
                } else {
                    order.setType(1);
                }
                order.setDifferencePrice(totalDifferPrice);
                productChangeOrderMapper.updateByPrimaryKey(order);
                // 更换已购买商品，没有新增，有则修改
                if (!changeGmProduct(request, houseId, order.getId())) {
                    return ServerResponse.createByErrorMessage("不能大于商品剩余数");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(ServerCode.DATA_SAVE_FAIL, "修改失败");
        }
        return ServerResponse.createBySuccessMessage(StringUtils.isNotBlank(msg) ? msg : "操作成功");
    }

    /**
     * 计算总价差额
     *
     * @param houseId
     * @return
     */
    private BigDecimal calcDifferPrice(String houseId) {
        // 查询商品换货列表
        List<ProductChange> changeList = productChangeMapper.queryByHouseId(houseId, "0");
        BigDecimal differencePrice = BigDecimal.ZERO;
        if (null != changeList && changeList.size() > 0) {
            // 计算总价差额
            for (ProductChange change : changeList) {
                differencePrice = differencePrice.add(change.getDifferencePrice());
            }
        }
        return differencePrice;
    }

    /**
     * 更换已购买商品
     *
     * @param request
     * @param houseId
     */
    private boolean changeGmProduct(HttpServletRequest request, String houseId, String orderId) {
        // 查询
        List<ProductChange> list = productChangeMapper.queryByHouseId(houseId, "0");
        Product destProduct = null;
        Unit destUnit = null;
        if (null != list && list.size() > 0) {
            int num = 0;
            for (ProductChange change : list) {
                // 更换数大于0的商品，才做处理
                if (change.getDestSurCount().compareTo(0.0) == 1) {
                    // 原商品仓库
                    Warehouse oldWareHouse = warehouseMapper.getByProductId(change.getSrcProductId(), houseId);
                    // 新商品仓库
                    Warehouse wareHouse = warehouseMapper.getByProductId(change.getDestProductId(), houseId);
                    // 更换后的商品
                    ServerResponse destResponse = productAPI.getProductById(request, change.getDestProductId());
                    if (destResponse != null && destResponse.getResultObj() != null) {
                        destProduct = JSON.parseObject(JSON.toJSONString(destResponse.getResultObj()), Product.class);
                        ServerResponse destUnitResponse = unitAPI.getUnitById(request, destProduct.getConvertUnit());
                        boolean flagB = destUnitResponse != null && destUnitResponse.getResultObj() != null;
                        if (flagB) {
                            destUnit = JSON.parseObject(JSON.toJSONString(destUnitResponse.getResultObj()), Unit.class);
                        }
                    }
                    // 处理新商品------begin
                    if (null == wareHouse) {
                        Goods goods = forMasterAPI.getGoods(request.getParameter(Constants.CITY_ID), destProduct.getGoodsId());
                        // 新商品没有则添加
                        Warehouse newWareHouse = new Warehouse();
                        newWareHouse.setHouseId(houseId);
                        newWareHouse.setShopCount(change.getDestSurCount());
                        newWareHouse.setRepairCount(0.0);
                        newWareHouse.setStayCount(0.0);
                        newWareHouse.setRobCount(0.0);
                        newWareHouse.setAskCount(0.0);//已要数量
                        newWareHouse.setBackCount(0.0);//退总数
                        newWareHouse.setReceive(0.0);
                        newWareHouse.setProductId(destProduct.getId());
                        newWareHouse.setProductSn(destProduct.getProductSn());
                        newWareHouse.setProductName(destProduct.getName());
                        newWareHouse.setPrice(destProduct.getPrice());
                        newWareHouse.setCost(destProduct.getCost());
                        // 取商品转换单位
                        newWareHouse.setUnitName(destUnit.getName());
                        newWareHouse.setProductType(goods.getType());
                        newWareHouse.setCategoryId(destProduct.getCategoryId());
                        newWareHouse.setImage(destProduct.getImage());
                        newWareHouse.setPayTime(0);
                        newWareHouse.setAskTime(0);
                        newWareHouse.setRepTime(0);//补次数
                        newWareHouse.setBackTime(0);
                        warehouseMapper.insert(newWareHouse);
                    } else {
                        // 原商品剩余数
                        // 商品剩余数 剩余数量 所有买的数量 - 业主退货 - 要的
                        double surCount = oldWareHouse.getShopCount() - (oldWareHouse.getOwnerBack() == null ? 0D : oldWareHouse.getOwnerBack()) - oldWareHouse.getAskCount();
                        if (BigDecimal.valueOf(change.getDestSurCount()).compareTo(BigDecimal.valueOf(surCount)) == 1) {
                            return false;
                        }
                        wareHouse.setModifyDate(new Date());
                        // 计算所有买的数量 买的数量+更换数
                        BigDecimal shopCount = BigDecimal.valueOf(wareHouse.getShopCount()).add(BigDecimal.valueOf(change.getDestSurCount()));
                        wareHouse.setShopCount(shopCount.doubleValue());
                        warehouseMapper.updateByPrimaryKey(wareHouse);
                    }
                    // 修改原仓库商品 买的数量 原买的数量-更换数
                    BigDecimal oldShopCount = BigDecimal.valueOf(oldWareHouse.getShopCount()).subtract(BigDecimal.valueOf(change.getDestSurCount()));
                    oldWareHouse.setShopCount(oldShopCount.doubleValue());
                    oldWareHouse.setModifyDate(new Date());
                    warehouseMapper.updateByPrimaryKey(oldWareHouse);
                    // 处理新商品------end
                    change.setType(1);
                    productChangeMapper.updateByPrimaryKey(change);
                } else {
                    // 为0的，则删除
                    productChangeMapper.deleteByPrimaryKey(change.getId());
                    num += 1;
                }
            }
            // 如果存在一个或多个更换数为 0 的商品，则不生成订单
            if (num == list.size()) {
                productChangeOrderMapper.deleteByPrimaryKey(orderId);
            }
        }
        return true;
    }

    /**
     * 查询业主退货列表
     * materialOrderState
     */
    public ServerResponse changeOrderState(String houseId, PageDTO pageDTO, String beginDate, String endDate, String likeAddress) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            if (!CommonUtil.isEmpty(beginDate) && !CommonUtil.isEmpty(endDate)) {
                if (beginDate.equals(endDate)) {
                    beginDate = beginDate + " " + "00:00:00";
                    endDate = endDate + " " + "23:59:59";
                }
            }
            List<ProductChangeOrder> productChangeOrderList = productChangeOrderMapper.queryOrderByStateAndLikeAddress(houseId, beginDate, endDate, likeAddress);
            PageInfo pageResult = new PageInfo(productChangeOrderList);
            List<ProductOrderDTO> productOrderDTOList = getProductOrderDTOList(productChangeOrderList);
            pageResult.setList(productOrderDTOList);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 处理dto
     *
     * @param productChangeOrderList
     * @return
     */
    private List<ProductOrderDTO> getProductOrderDTOList(List<ProductChangeOrder> productChangeOrderList) {
        List<ProductOrderDTO> productOrderDTOS = new ArrayList<ProductOrderDTO>();
        for (ProductChangeOrder order : productChangeOrderList) {
            ProductOrderDTO orderDTO = new ProductOrderDTO();
            orderDTO.setId(order.getId());
            orderDTO.setNumber(order.getNumber());
            orderDTO.setCreateDate(order.getCreateDate());
            orderDTO.setHouseId(order.getHouseId());
            House house = houseMapper.selectByPrimaryKey(order.getHouseId());
            if (house != null) {
                if (house.getVisitState() != 0) {
                    orderDTO.setAddress(house.getHouseName());
                    Member member = memberMapper.selectByPrimaryKey(house.getMemberId());
                    orderDTO.setMemberName(member.getNickName() == null ? member.getName() : member.getNickName());
                    orderDTO.setMemberId(member.getId());
                    orderDTO.setMemberMobile(member.getMobile());
                    orderDTO.setType("业主换货");
                    productOrderDTOS.add(orderDTO);
                }
            }
        }
        return productOrderDTOS;
    }

    /**
     * 根据OrderIdhouseId查询商品更换明细
     */
    public ServerResponse queryChangeDetail(String orderId, String houseId) {
        ProductOrderDTO productOrderDTO = new ProductOrderDTO();
        Example example = new Example(ProductChange.class);
        example.createCriteria()
                .andEqualTo(ProductChange.HOUSE_ID, houseId)
                .andEqualTo(ProductChange.ORDER_ID, orderId)
                .andEqualTo(ProductChange.TYPE, "1");
        List<ProductChange> productChangeList = productChangeMapper.selectByExample(example);
        House house = houseMapper.selectByPrimaryKey(houseId);
        Member member = memberMapper.selectByPrimaryKey(house.getMemberId());
        // 房子地址
        productOrderDTO.setAddress(house.getHouseName());
        // 业主
        productOrderDTO.setMemberName(member.getNickName() == null ? member.getName() : member.getNickName());
        // 手机号码
        productOrderDTO.setMemberMobile(member.getMobile());
        // 明细list
        List<ProductChangeItemDTO> itemDTOList = new ArrayList<>();
        String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        BigDecimal totalDifferPrice = BigDecimal.ZERO;
        for (ProductChange change : productChangeList) {
            // 合计退差价
            totalDifferPrice = totalDifferPrice.add(change.getDifferencePrice());
            // 处理itemDTo
            ProductChangeItemDTO itemDTO = getItemDTO(change, address, 0);
            itemDTOList.add(itemDTO);
            productOrderDTO.setProductChangeItemDTOList(itemDTOList);
        }
        productOrderDTO.setTotalDifferPrice(totalDifferPrice);
        return ServerResponse.createBySuccess("查询成功", productOrderDTO);
    }

    /**
     * 根据taskId查询商品支付流水
     */
    public ServerResponse queryPayChangeDetail(String number, String taskId) {
        // 查询支付信息
        Example example = new Example(BusinessOrder.class);
        example.createCriteria().andEqualTo(BusinessOrder.NUMBER, number);
        List<BusinessOrder> businessOrderList = businessOrderMapper.selectByExample(example);
        BusinessOrder businessOrder = businessOrderList.get(0);
        // 查询商品更换订单表
        Example productExample = new Example(ProductChange.class);
        productExample.createCriteria()
                .andEqualTo(ProductChange.HOUSE_ID, businessOrder.getHouseId())
                .andEqualTo(ProductChange.ORDER_ID, taskId)
                .andEqualTo(ProductChange.TYPE, "1");
        List<ProductChange> productChangeList = productChangeMapper.selectByExample(productExample);
        // 房子信息
        House house = houseMapper.selectByPrimaryKey(businessOrder.getHouseId());
        ProductOrderDTO productOrderDTO = new ProductOrderDTO();
        // 房子地址
        productOrderDTO.setAddress(house.getHouseName());
        // 订单号
        productOrderDTO.setNumber(businessOrder.getNumber());
        List<ProductChangeItemDTO> itemDTOList = new ArrayList<>();
        String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        // 总补差价
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (ProductChange change : productChangeList) {
            // 合计补差价
            totalPrice = totalPrice.add(change.getDifferencePrice());
            ProductChangeItemDTO itemDTO = getItemDTO(change, address, 1);
            itemDTOList.add(itemDTO);
            productOrderDTO.setProductChangeItemDTOList(itemDTOList);
        }
        productOrderDTO.setTotalDifferPrice(totalPrice);
        return ServerResponse.createBySuccess("查询成功", productOrderDTO);
    }


    /**
     * 转换ITEMDTO
     *
     * @param change
     * @return
     */
    private ProductChangeItemDTO getItemDTO(ProductChange change, String address, int temp) {
        ProductChangeItemDTO itemDTO = new ProductChangeItemDTO();
        if (temp == 0) {
            // src
            itemDTO.setSrcImage(address + change.getSrcImage());
            itemDTO.setSrcProductId(change.getSrcProductId());
            itemDTO.setSrcProductSn(change.getSrcProductSn());
            itemDTO.setSrcProductName(change.getSrcProductName());
            itemDTO.setSrcPrice(change.getSrcPrice());
            itemDTO.setSrcUnitName(change.getSrcUnitName());
            itemDTO.setSrcBeforeCount(change.getSrcSurCount());
            itemDTO.setSrcAfterCount(MathUtil.sub(change.getSrcSurCount(), change.getDestSurCount()));
            // dest
            itemDTO.setDestImage(address + change.getDestImage());
            itemDTO.setDestProductId(change.getDestProductId());
            itemDTO.setDestProductSn(change.getDestProductSn());
            itemDTO.setDestProductName(change.getDestProductName());
            itemDTO.setDestPrice(change.getDestPrice());
            itemDTO.setDestUnitName(change.getDestUnitName());
            double destDifferPrice = MathUtil.sub(change.getDestPrice(), change.getSrcPrice());
            itemDTO.setDestDifferPrice((destDifferPrice > 0 ? "+" + destDifferPrice : destDifferPrice) + "元/" + change.getDestUnitName());
            itemDTO.setDestBeforeCount(0.0);
            itemDTO.setDestAfterCount(change.getDestSurCount());
            itemDTO.setDestTotalMoney(change.getDifferencePrice());
        } else if (temp == 1) {
            // 支付单换货详情
            itemDTO.setImage(address + change.getDestImage());
            itemDTO.setProductSn(change.getDestProductSn());
            itemDTO.setProductName(change.getDestProductName());
            itemDTO.setPrice(BigDecimal.valueOf(change.getDestPrice()));
            itemDTO.setUnitName(change.getDestUnitName());
            double destDifferPrice = MathUtil.sub(change.getDestPrice(), change.getSrcPrice());
            itemDTO.setDifferPrice(BigDecimal.valueOf(destDifferPrice));
            itemDTO.setShopCount(change.getDestSurCount());
            itemDTO.setTotalPrice(change.getDifferencePrice());
        }

        return itemDTO;
    }

}
