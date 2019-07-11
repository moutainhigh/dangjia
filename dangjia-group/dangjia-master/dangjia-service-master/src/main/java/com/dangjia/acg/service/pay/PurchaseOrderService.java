package com.dangjia.acg.service.pay;

import com.dangjia.acg.api.actuary.ServerPortAPI;
import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.actuary.FlowActuaryDTO;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.pay.IPurchaseOrderMapper;
import com.dangjia.acg.modle.actuary.BudgetMaterial;
import com.dangjia.acg.modle.basics.Product;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.pay.PurchaseOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 * @author Ruking.Cheng
 * @descrilbe 购买单
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/6/29 11:36 AM
 */
@Service
public class PurchaseOrderService {
    @Autowired
    private IPurchaseOrderMapper purchaseOrderMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IHouseMapper iHouseMapper;
    @Autowired
    private ServerPortAPI serverPortAPI;

    public ServerResponse getBudgetMaterialList(String houseId) {
        House house = iHouseMapper.selectByPrimaryKey(houseId);
        if (house == null)
            return ServerResponse.createByErrorMessage("没有该房子");
        List<BudgetMaterial> budgetMaterialList = serverPortAPI.getBudgetMaterialList(house.getCityId(), houseId);
        if (budgetMaterialList.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        String appAddress = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class);
        Map<String, Object> datas = new HashMap<>();
        PurchaseOrder purchaseOrder = getPurchaseOrderExample(house.getCityId(), houseId, true);
        String[] ids = purchaseOrder.getBudgetIds().split(",");
        List<FlowActuaryDTO> flowActuaryDTOList = new ArrayList<>();
        for (BudgetMaterial bm : budgetMaterialList) {
            FlowActuaryDTO flowActuaryDTO = getFlowActuaryDTO(house.getCityId(), address, appAddress, bm);
            boolean flag = Arrays.asList(ids).contains(bm.getId());
            flowActuaryDTO.setSelection(flag ? 1 : 0);
            flowActuaryDTOList.add(flowActuaryDTO);
        }
        datas.put("datas", flowActuaryDTOList);
        datas.put("string", purchaseOrder.getId());
        datas.put("price", String.format("%.2f", purchaseOrder.getPrice()));
        return ServerResponse.createBySuccess("查询成功", datas);
    }

    private FlowActuaryDTO getFlowActuaryDTO(String cityId, String address, String appAddress, BudgetMaterial bm) {
        FlowActuaryDTO flowActuaryDTO = new FlowActuaryDTO();
        flowActuaryDTO.setTypeName("材料");
        flowActuaryDTO.setType(2);
        String convertUnitName = bm.getUnitName();
        flowActuaryDTO.setId(bm.getProductId());
        flowActuaryDTO.setImage(address + bm.getImage());
        String url = appAddress + String.format(DjConstants.YZPageAddress.COMMODITY, "", "", flowActuaryDTO.getTypeName() + "商品详情") + "&gId=" + bm.getId() + "&type=" + 2;
        flowActuaryDTO.setUrl(url);
        flowActuaryDTO.setAttribute(serverPortAPI.getAttributes(cityId, bm.getProductId()));//拼接属性品牌
        flowActuaryDTO.setPrice("¥" + String.format("%.2f", bm.getPrice()) + "/" + bm.getUnitName());
        flowActuaryDTO.setTotalPrice(bm.getTotalPrice());
        flowActuaryDTO.setShopCount(bm.getShopCount());
        flowActuaryDTO.setConvertCount(bm.getConvertCount());
        flowActuaryDTO.setBudgetMaterialId(bm.getId());
        flowActuaryDTO.setName(bm.getGoodsName());
        if (CommonUtil.isEmpty(flowActuaryDTO.getName())) {
            flowActuaryDTO.setName(bm.getProductName());
        }
        flowActuaryDTO.setUnitName(convertUnitName);
        flowActuaryDTO.setBuy(1);
        return flowActuaryDTO;
    }

    private PurchaseOrder getPurchaseOrderExample(String cityId, String houseId, Boolean selPrice) {
        Example example = new Example(PurchaseOrder.class);
        example.createCriteria()
                .andEqualTo(PurchaseOrder.HOUSE_ID, houseId)
                .andEqualTo(PurchaseOrder.TYPE, 0);
        List<PurchaseOrder> purchaseOrders = purchaseOrderMapper.selectByExample(example);
        PurchaseOrder purchaseOrder;
        if (purchaseOrders.size() <= 0) {
            purchaseOrder = new PurchaseOrder();
            purchaseOrder.setPrice(0d);
            purchaseOrder.setHouseId(houseId);
            purchaseOrder.setType(0);
            purchaseOrder.setBudgetIds("");
            purchaseOrderMapper.insert(purchaseOrder);
        } else {
            purchaseOrder = purchaseOrders.get(0);
        }
        if (selPrice != null && selPrice) {
            purchaseOrder.setPrice(getTotalPrice(cityId, purchaseOrder.getBudgetIds()));
        }
        return purchaseOrder;
    }

    public ServerResponse setPurchaseOrder(String houseId, String budgetIds) {
        House house = iHouseMapper.selectByPrimaryKey(houseId);
        if (house == null)
            return ServerResponse.createByErrorMessage("没有该房子");
        if (budgetIds == null) {
            budgetIds = "";
        }
        PurchaseOrder purchaseOrder = getPurchaseOrderExample(house.getCityId(), houseId, false);
        purchaseOrder.setBudgetIds(budgetIds);
        purchaseOrderMapper.updateByPrimaryKeySelective(purchaseOrder);
        return ServerResponse.createBySuccessMessage("操作成功");
    }

    Map<String, Object> getPurchaseOrder(String purchaseOrderId) {
        PurchaseOrder purchaseOrder = purchaseOrderMapper.selectByPrimaryKey(purchaseOrderId);
        if (purchaseOrder == null) {
            return null;
        }
        House house = iHouseMapper.selectByPrimaryKey(purchaseOrder.getHouseId());
        if (house == null)
            return null;
        String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        String appAddress = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class);
        List<FlowActuaryDTO> flowActuaryDTOList = new ArrayList<>();
        List<BudgetMaterial> budgetMaterialList = serverPortAPI.getInIdsBudgetMaterialList(house.getCityId(), purchaseOrder.getBudgetIds());
        double totalPrice = 0d;
        for (BudgetMaterial bm : budgetMaterialList) {
            if (bm != null) {
                totalPrice = totalPrice + bm.getTotalPrice();
                flowActuaryDTOList.add(getFlowActuaryDTO(house.getCityId(), address, appAddress, bm));
            }
        }
        if (flowActuaryDTOList.size() <= 0) {
            return null;
        }
        purchaseOrder.setPrice(totalPrice);
        Map<String, Object> map = new HashMap<>();
        map.put("purchaseOrder", purchaseOrder);
        map.put("list", flowActuaryDTOList);
        return map;
    }

    /**
     * 支付成功后回调
     *
     * @param purchaseOrderId purchaseOrderId
     * @return
     */
    List<BudgetMaterial> payPurchaseOrder(String purchaseOrderId) {
        PurchaseOrder purchaseOrder = purchaseOrderMapper.selectByPrimaryKey(purchaseOrderId);
        List<BudgetMaterial> budgetMaterialList = new ArrayList<>();
        if (purchaseOrder == null) {
            return budgetMaterialList;
        }
        House house = iHouseMapper.selectByPrimaryKey(purchaseOrder.getHouseId());
        if (house == null)
            return budgetMaterialList;
        List<BudgetMaterial> budgetMaterials = serverPortAPI.getInIdsBudgetMaterialList(house.getCityId(), purchaseOrder.getBudgetIds());
        for (BudgetMaterial budgetMaterial : budgetMaterials) {
            Product product = serverPortAPI.getProduct(house.getCityId(), budgetMaterial.getProductId());
            if (product != null) {
                budgetMaterial.setModifyDate(new Date());
                budgetMaterial.setDeleteState(1);//找不到商品标记删除
                serverPortAPI.updateBudgetMaterial(house.getCityId(), budgetMaterial);
            } else {
                //重新记录支付时精算价格
                budgetMaterial.setPrice(product.getPrice());
                budgetMaterial.setCost(product.getCost());
                budgetMaterial.setTotalPrice(budgetMaterial.getConvertCount() * product.getPrice());//已支付 记录总价
                budgetMaterial.setDeleteState(3);//已支付
                budgetMaterial.setModifyDate(new Date());
                serverPortAPI.updateBudgetMaterial(house.getCityId(), budgetMaterial);
                budgetMaterialList.add(budgetMaterial);
            }
        }
        purchaseOrder.setType(1);
        purchaseOrderMapper.updateByPrimaryKeySelective(purchaseOrder);
        return budgetMaterialList;
    }

    /**
     * 获取价格
     *
     * @param budgetIds BudgetMaterial id集合
     * @return
     */
    private double getTotalPrice(String cityId, String budgetIds) {
        double totalPrice = 0d;
        List<BudgetMaterial> budgetMaterials = serverPortAPI.getInIdsBudgetMaterialList(cityId, budgetIds);
        for (BudgetMaterial bm : budgetMaterials) {
            if (bm != null) {
                totalPrice = totalPrice + bm.getTotalPrice();
            }
        }
        return totalPrice;
    }
}
