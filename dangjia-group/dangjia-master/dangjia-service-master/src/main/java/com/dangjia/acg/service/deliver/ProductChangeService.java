package com.dangjia.acg.service.deliver;

import com.alibaba.fastjson.JSON;
import com.dangjia.acg.api.basics.ProductAPI;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.BaseException;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.MathUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.deliver.ProductChangeDTO;
import com.dangjia.acg.dto.deliver.ProductChangeOrderDTO;
import com.dangjia.acg.mapper.deliver.IProductChangeMapper;
import com.dangjia.acg.mapper.deliver.IProductChangeOrderMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.house.IWarehouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.worker.IWorkerDetailMapper;
import com.dangjia.acg.modle.basics.Product;
import com.dangjia.acg.modle.deliver.ProductChange;
import com.dangjia.acg.modle.deliver.ProductChangeOrder;
import com.dangjia.acg.modle.house.Warehouse;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.worker.WorkerDetail;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import org.apache.commons.lang3.StringUtils;
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
    private static Logger LOG = LoggerFactory.getLogger(ProductChangeService.class);

    /**
     * 添加更换商品
     * @param request
     * @param userToken
     * @param houseId
     * @param srcProductId
     * @param destProductId
     * @param srcSurCount
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse insertProductChange(HttpServletRequest request, String userToken, String houseId, String srcProductId, String destProductId, Double srcSurCount){
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member operator = (Member) object;
            // 原商品
            ServerResponse srcResponse = productAPI.getProductById(request, srcProductId);
            // 更换后的商品
            ServerResponse destResponse = productAPI.getProductById(request, destProductId);
            boolean flag = (srcResponse!=null&&srcResponse.getResultObj()!=null && destResponse!=null&&destResponse.getResultObj()!=null);
            Product srcProduct = null;
            Product destProduct = null;
            if(flag) {
                srcProduct = JSON.parseObject(JSON.toJSONString(srcResponse.getResultObj()), Product.class);
                destProduct = JSON.parseObject(JSON.toJSONString(destResponse.getResultObj()), Product.class);
            }
            // 查询
            Example example = new Example(ProductChange.class);
            example.createCriteria()
                    .andEqualTo(ProductChange.HOUSE_ID, houseId)
                    .andEqualTo(ProductChange.MEMBER_ID, operator.getId())
                    .andEqualTo(ProductChange.SRC_PRODUCT_ID, srcProductId);
            List<ProductChange> list = productChangeMapper.selectByExample(example);
            if(!CommonUtil.isEmpty(list)) {
                ProductChange change = list.get(0);
                change.setDestProductId(destProduct.getId());
                change.setDestProductSn(destProduct.getProductSn());
                change.setDestProductName(destProduct.getName());
                change.setDestUnitName(destProduct.getUnitName());
                change.setDestPrice(destProduct.getPrice());
                change.setDestImage(destProduct.getImage());
                change.setDestSurCount(0.0);
                change.setModifyDate(new Date());
                productChangeMapper.updateByPrimaryKeySelective(change);
            }else {
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
                productChange.setSrcUnitName(srcProduct.getUnitName());
                productChange.setSrcImage(srcProduct.getImage());
                // dest
                productChange.setDestProductId(destProduct.getId());
                productChange.setDestProductSn(destProduct.getProductSn());
                productChange.setDestProductName(destProduct.getName());
                productChange.setDestPrice(destProduct.getPrice());
                productChange.setDestUnitName(destProduct.getUnitName());
                productChange.setDestImage(destProduct.getImage());
                // 更换数默认为0
                productChange.setDestSurCount(0.0);
                // 未处理
                productChange.setType(0);
                // 差价默认为0
                productChange.setDifferencePrice(BigDecimal.ZERO);
                productChangeMapper.insert(productChange);
            }
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
        return ServerResponse.createBySuccessMessage("操作成功");
    }

    /**
     * 根据houseId查询商品更换列表
     * @param request
     * @param userToken
     * @param houseId
     * @return
     */
    public ServerResponse queryChangeByHouseId(HttpServletRequest request, String userToken, String houseId){
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member operator = (Member) object;
        Example example = new Example(ProductChange.class);
        example.createCriteria()
                .andEqualTo(ProductChange.HOUSE_ID,houseId)
                .andEqualTo(ProductChange.MEMBER_ID,operator.getId())
                .andEqualTo(ProductChange.TYPE,0);
        List<ProductChange> list = productChangeMapper.selectByExample(example);
        return ServerResponse.createBySuccess("操作成功",list);
    }

    /**
     * 申请换货
     * @param request
     * @param houseId
     * @return
     */
    public ServerResponse applyProductChange(HttpServletRequest request,String houseId) {
        try {
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            List<ProductChangeDTO> list = new ArrayList<>();
            ProductChangeOrderDTO productChangeOrderDTO = new ProductChangeOrderDTO();
            ProductChangeOrder order = insertProductChangeOrder(houseId);
            // 查询商品换货列表
            List<ProductChange> changeList = productChangeMapper.queryByHouseId(houseId, "0");
            for(ProductChange change : changeList){
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
            if(null != order){
                productChangeOrderDTO.setId(order.getId());
                productChangeOrderDTO.setNumber(order.getNumber());
                productChangeOrderDTO.setDifferencePrice(order.getDifferencePrice());
                productChangeOrderDTO.setType(order.getType());
                productChangeOrderDTO.setCreateDate(order.getCreateDate());
                productChangeOrderDTO.setModifyDate(order.getModifyDate());
            }
            productChangeOrderDTO.setProductChangeDTOList(list);
            return ServerResponse.createBySuccess("操作成功", productChangeOrderDTO);
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }

    /**
     * 设置商品更换数
     * @param request
     * @param id
     * @param destSurCount
     * @return
     */
    public ServerResponse setDestSurCount(HttpServletRequest request, String id, Double destSurCount){
        try {
            ProductChange productChange = productChangeMapper.selectByPrimaryKey(id);
            if(null != productChange){
                // 剩余数
                BigDecimal srcCount = BigDecimal.valueOf(productChange.getSrcSurCount());
                // 更换数
                BigDecimal destCount = BigDecimal.valueOf(destSurCount);
                if(destCount.compareTo(srcCount) == 1){
                    return ServerResponse.createByErrorMessage("不能大于商品剩余数");
                }
                productChange.setDestSurCount(destSurCount);
                // 差额单价
                BigDecimal price = BigDecimal.valueOf(MathUtil.sub(productChange .getDestPrice(), productChange.getSrcPrice()));
                BigDecimal differPrice = BigDecimal.ZERO;
                if(price.compareTo(BigDecimal.ZERO) == 0){
                    // 两个商品价格相等 差价=更换数*商品价格
                    differPrice = BigDecimal.valueOf(productChange.getDestPrice()).multiply(BigDecimal.valueOf(destSurCount));
                } else {
                    // 不相等 差价=更换数*差额单价
                    differPrice = price.multiply(BigDecimal.valueOf(destSurCount));
                }
                productChange.setDifferencePrice(differPrice);
                productChange.setModifyDate(new Date());
                productChangeMapper.updateByPrimaryKeySelective(productChange);
            }
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
        return ServerResponse.createBySuccessMessage("操作成功");
    }

    /**
     * 添加更换商品订单
     * @param houseId
     * @return
     */
    public ProductChangeOrder insertProductChangeOrder(String houseId){
        ProductChangeOrder order = null;
        try {
            // 查询db中是否有该房子的换货订单
            List<ProductChangeOrder> list = productChangeOrderMapper.queryOrderByHouseId(houseId, "0");
            // 计算总价差额
            BigDecimal differPrice = calcDifferPrice(houseId);
            if(null != list && list.size() > 0){
                order = list.get(0);
                order.setDifferencePrice(differPrice);
                order.setModifyDate(new Date());
                productChangeOrderMapper.updateByPrimaryKey(order);
            } else {
                order = new ProductChangeOrder();
                order.setHouseId(houseId);
                // 默认未支付
                order.setType(0);
                order.setNumber(System.currentTimeMillis()+"-"+generateWord());
                order.setDifferencePrice(differPrice);
                productChangeOrderMapper.insert(order);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return order;
    }

    private String generateWord() {
        String[] beforeShuffle = new String[] {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
                "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
                "W", "X", "Y", "Z" };
        List<String> list = Arrays.asList(beforeShuffle);
        Collections.shuffle(list);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
        }
        String afterShuffle = sb.toString();
        String result = afterShuffle.substring(5, 9);
        return result;
    }

    /**
     * 根据houseId查询更换商品订单
     * @param houseId
     * @return
     */
    public ServerResponse queryOrderByHouseId(String houseId){
        Example example = new Example(ProductChangeOrder.class);
        example.createCriteria()
                .andEqualTo(ProductChangeOrder.HOUSE_ID,houseId);
        List<ProductChangeOrder> list = productChangeOrderMapper.selectByExample(example);
        return ServerResponse.createBySuccess("操作成功",list);
    }

    /**
     * 补退差价回调
     * @param request
     * @param id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse orderBackFun(HttpServletRequest request, String id){
        String msg = "";
        try {
            // 查询
            ProductChangeOrder order = productChangeOrderMapper.selectByPrimaryKey(id);
            if(order!=null){
                String houseId = order.getHouseId();
                // 计算总价差额
                BigDecimal totalDifferPrice = calcDifferPrice(houseId);
                // 判断总价差额是否小于等于0
                if(totalDifferPrice.compareTo(BigDecimal.ZERO) == -1 || totalDifferPrice.compareTo(BigDecimal.ZERO) == 0){
                    // 0未支付 1已支付 2已退款
                    order.setType(2);
                    // 总价差额不等于 0 时，退钱到业主钱包
                    if(totalDifferPrice.compareTo(BigDecimal.ZERO) != 0) {
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
                }else {
                    order.setType(1);
                }
                order.setDifferencePrice(totalDifferPrice);
                productChangeOrderMapper.updateByPrimaryKey(order);
                // 更换已购买商品，没有新增，有则修改
                if(!changeGmProduct(request, houseId)){
                    return ServerResponse.createByErrorMessage("操作失败");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new BaseException(ServerCode.DATA_SAVE_FAIL, "修改失败");
        }
        return ServerResponse.createBySuccessMessage(StringUtils.isNotBlank(msg) ? msg : "操作成功");
    }

    /**
     * 计算总价差额
     * @param houseId
     * @return
     */
    private BigDecimal calcDifferPrice(String houseId){
        // 查询商品换货列表
        List<ProductChange> changeList = productChangeMapper.queryByHouseId(houseId, "0");
        BigDecimal differencePrice = BigDecimal.ZERO;
        if(null != changeList && changeList.size() > 0 ){
            // 计算总价差额
            for (ProductChange change : changeList){
                differencePrice = differencePrice.add(change.getDifferencePrice());
            }
        }
        return differencePrice;
    }

    /**
     * 更换已购买商品
     * @param request
     * @param houseId
     */
    private boolean changeGmProduct(HttpServletRequest request, String houseId){
        // 查询
        List<ProductChange> list = productChangeMapper.queryByHouseId(houseId, "0");
        Product destProduct = null;
        if(null != list && list.size() > 0){
            for (ProductChange change : list){
                // 更换数大于0的商品，才做处理
                if(change.getDestSurCount().compareTo(0.0) == 1) {
                    // 原商品仓库
                    Warehouse oldWareHouse = warehouseMapper.getByProductId(change.getSrcProductId(), houseId);
                    // 新商品仓库
                    Warehouse wareHouse = warehouseMapper.getByProductId(change.getDestProductId(), houseId);
                    // 更换后的商品
                    ServerResponse destResponse = productAPI.getProductById(request, change.getDestProductId());
                    if (destResponse != null && destResponse.getResultObj() != null) {
                        destProduct = JSON.parseObject(JSON.toJSONString(destResponse.getResultObj()), Product.class);
                    }
                    // 处理新商品------begin
                    if (null == wareHouse) {
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
                        newWareHouse.setUnitName(destProduct.getUnitName());
                        newWareHouse.setProductType(0);
                        newWareHouse.setCategoryId(destProduct.getCategoryId());
                        newWareHouse.setImage(destProduct.getImage());
                        newWareHouse.setPayTime(0);
                        newWareHouse.setAskTime(0);
                        newWareHouse.setRepTime(0);//补次数
                        newWareHouse.setBackTime(0);
                        warehouseMapper.insert(newWareHouse);
                    } else {
                        // 新商品有则修改
                        // 商品剩余数 剩余数量 所有买的数量 - 业主退货 - 要的
                        double surCount = wareHouse.getShopCount() - (wareHouse.getOwnerBack() == null ? 0D : wareHouse.getOwnerBack()) - wareHouse.getAskCount();
                        if (BigDecimal.valueOf(change.getDestSurCount()).compareTo(BigDecimal.valueOf(surCount)) == 1) {
                            return false;
                        }
                        wareHouse.setModifyDate(new Date());
                        // 计算所有买的数量 买的数量+更换数
                        BigDecimal shopCount = BigDecimal.valueOf(wareHouse.getShopCount()).add(BigDecimal.valueOf(change.getDestSurCount()));
                        wareHouse.setShopCount(shopCount.doubleValue());
                        warehouseMapper.updateByPrimaryKey(wareHouse);
                        // 修改原仓库商品 买的数量 原买的数量-更换数
                        BigDecimal oldShopCount = BigDecimal.valueOf(oldWareHouse.getShopCount()).subtract(BigDecimal.valueOf(change.getDestSurCount()));
                        oldWareHouse.setShopCount(oldShopCount.doubleValue());
                        oldWareHouse.setModifyDate(new Date());
                        warehouseMapper.updateByPrimaryKey(oldWareHouse);
                    }
                    // 处理新商品------end
                    change.setType(1);
                    productChangeMapper.updateByPrimaryKey(change);
                }else {
                    // 为0的，则删除
                    productChangeMapper.deleteByPrimaryKey(change.getId());
                }
            }
        }
        return true;
    }
}
