package com.dangjia.acg.service.repair;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.app.member.MemberAPI;
import com.dangjia.acg.api.data.GetForBudgetAPI;
import com.dangjia.acg.api.data.TechnologyRecordAPI;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.actuary.GoodsDTO;
import com.dangjia.acg.dto.house.WarehouseDTO;
import com.dangjia.acg.dto.repair.BudgetMaterialDTO;
import com.dangjia.acg.mapper.actuary.IBudgetMaterialMapper;
import com.dangjia.acg.mapper.basics.IGoodsMapper;
import com.dangjia.acg.mapper.basics.IProductMapper;
import com.dangjia.acg.modle.actuary.BudgetMaterial;
import com.dangjia.acg.modle.basics.Goods;
import com.dangjia.acg.modle.basics.Product;
import com.dangjia.acg.modle.house.Warehouse;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.repair.MendMateriel;
import com.dangjia.acg.service.actuary.ActuaryOperationService;
import com.dangjia.acg.service.data.ForMasterService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author: Ronalcheng
 * Date: 2018/12/7 0007
 * Time: 10:42
 */
@Service
public class FillMaterielService {
    @Autowired
    private IProductMapper iProductMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IBudgetMaterialMapper budgetMaterialMapper;
    @Autowired
    private ActuaryOperationService actuaryOperationService;
    @Autowired
    private TechnologyRecordAPI technologyRecordAPI;
    @Autowired
    private ForMasterService forMasterService;
    @Autowired
    private GetForBudgetAPI getForBudgetAPI;
    @Autowired
    private IGoodsMapper goodsMapper;
    @Autowired
    private MemberAPI memberAPI;

    /**
     * 管家审核验收申请
     * 材料审查
     * 剩余材料列表
     */
    public ServerResponse surplusList(String workerTypeId, String houseId) {
        try {
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            //精算的
            List<BudgetMaterial> budgetMaterialList = budgetMaterialMapper.repairBudgetMaterial(workerTypeId, houseId, "", "", "0");
            //补材料的
            List<MendMateriel> mendMaterielList = getForBudgetAPI.askAndQuit(workerTypeId, houseId, "", "");
            List<WarehouseDTO> warehouseDTOS = new ArrayList<>();
            List<String> productIdList = new ArrayList<>();
            String productId;
            for (MendMateriel mendMateriel : mendMaterielList) {
                boolean flag = true;
                productId = mendMateriel.getProductId();
                for (BudgetMaterial bm : budgetMaterialList) {
                    if (productId.equals(bm.getProductId())) {
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    productIdList.add(productId);
                }
            }
            for (BudgetMaterial bm : budgetMaterialList) {
                productIdList.add(bm.getProductId());
            }
            for (String id : productIdList) {
                ServerResponse response = technologyRecordAPI.getByProductId(id, houseId);
                //if(!response.isSuccess()) continue;
                Object warehouseStr = response.getResultObj();
                Warehouse warehouse = JSON.parseObject(JSON.toJSONString(warehouseStr), Warehouse.class);
                if (warehouse == null) continue;
                Product product = iProductMapper.selectByPrimaryKey(warehouse.getProductId());
                WarehouseDTO warehouseDTO = new WarehouseDTO();
                warehouseDTO.setImage(address + product.getImage());
                warehouseDTO.setShopCount(warehouse.getShopCount());
                warehouseDTO.setAskCount(warehouse.getAskCount());
                warehouseDTO.setBackCount((warehouse.getWorkBack() == null ? 0D : warehouse.getWorkBack()));
                warehouseDTO.setRealCount(warehouse.getShopCount() - warehouse.getBackCount());
                warehouseDTO.setSurCount(warehouse.getShopCount() - (warehouse.getOwnerBack() == null ? 0D : warehouse.getOwnerBack()) - warehouse.getReceive());//所有买的数量 - 退货 - 收的
                warehouseDTO.setProductName(product.getName());
                warehouseDTO.setPrice(warehouse.getPrice());
                warehouseDTO.setTolPrice(warehouseDTO.getRealCount() * warehouse.getPrice());
                warehouseDTO.setReceive(warehouse.getReceive() - (warehouse.getWorkBack() == null ? 0D : warehouse.getWorkBack()));
                warehouseDTO.setUnitName(warehouse.getUnitName());
                warehouseDTO.setProductType(warehouse.getProductType());
                warehouseDTO.setAskTime(warehouse.getAskTime());
                warehouseDTO.setRepTime(warehouse.getRepTime());
                warehouseDTO.setBackTime(warehouse.getBackTime());
                warehouseDTO.setBrandSeriesName(forMasterService.brandSeriesName(warehouse.getProductId()));
                warehouseDTO.setProductId(warehouse.getProductId());
                warehouseDTOS.add(warehouseDTO);
            }
            return ServerResponse.createBySuccess("查询成功", warehouseDTOS);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 要退查询仓库
     * 结合 精算记录+补记录
     */
    public ServerResponse askAndQuit(String userToken, String houseId, String categoryId, String name) {
        try {
            Object object = memberAPI.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            JSONObject job = (JSONObject)object;
            Member worker = job.toJavaObject(Member.class);
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            //精算的
            List<BudgetMaterial> budgetMaterialList = budgetMaterialMapper.repairBudgetMaterial(worker.getWorkerTypeId(), houseId, categoryId, name, "0");
            //补材料的
            List<MendMateriel> mendMaterielList = getForBudgetAPI.askAndQuit(worker.getWorkerTypeId(), houseId, categoryId, name);
            List<WarehouseDTO> warehouseDTOS = new ArrayList<>();

            List<String> productIdList = new ArrayList<>();

            Map map = new HashMap();
            for (BudgetMaterial bm : budgetMaterialList) {
                productIdList.add(bm.getProductId());
                map.put(bm.getProductId(), "0");
            }
            for (MendMateriel mendMateriel : mendMaterielList) {
                if (map.get(mendMateriel.getProductId()) == null) {
                    productIdList.add(mendMateriel.getProductId());
                    map.put(mendMateriel.getProductId(), "0");
                }
            }
            for (String id : productIdList) {
                ServerResponse response = technologyRecordAPI.getByProductId(id, houseId);
                Object warehouseStr = response.getResultObj();
                Warehouse warehouse = JSON.parseObject(JSON.toJSONString(warehouseStr), Warehouse.class);
                if (warehouse == null) continue;
                WarehouseDTO warehouseDTO = new WarehouseDTO();
                Product product = iProductMapper.selectByPrimaryKey(warehouse.getProductId());
                warehouseDTO.setMaket(1);
                if (product.getMaket() == 0 || product.getType() == 0) {
                    warehouseDTO.setMaket(0);
                }
                Goods goods = goodsMapper.selectByPrimaryKey(product.getGoodsId());
                if (goods != null) {
                    warehouseDTO.setSales(goods.getSales());
                }
                warehouseDTO.setImage(address + product.getImage());
                warehouseDTO.setShopCount(warehouse.getShopCount());
                warehouseDTO.setAskCount(warehouse.getAskCount());
                warehouseDTO.setBackCount((warehouse.getWorkBack() == null ? 0D : warehouse.getWorkBack()));
                warehouseDTO.setRealCount(warehouse.getShopCount() - warehouse.getBackCount());
                warehouseDTO.setSurCount(warehouse.getShopCount() - (warehouse.getOwnerBack() == null ? 0D : warehouse.getOwnerBack()) - warehouse.getAskCount());//所有买的数量 - 退货 - 收的=仓库剩余
                warehouseDTO.setProductName(product.getName());
                warehouseDTO.setPrice(warehouse.getPrice());
                warehouseDTO.setTolPrice(warehouseDTO.getRealCount() * warehouse.getPrice());
                warehouseDTO.setReceive(warehouse.getReceive() - (warehouse.getWorkBack() == null ? 0D : warehouse.getWorkBack()));
                warehouseDTO.setUnitName(warehouse.getUnitName());
                warehouseDTO.setProductType(warehouse.getProductType());
                warehouseDTO.setAskTime(warehouse.getAskTime());
                warehouseDTO.setRepTime(warehouse.getRepTime());
                warehouseDTO.setBackTime(warehouse.getBackTime());
                warehouseDTO.setBrandSeriesName(forMasterService.brandSeriesName(warehouse.getProductId()));
                warehouseDTO.setProductId(warehouse.getProductId());
                warehouseDTOS.add(warehouseDTO);
            }

            return ServerResponse.createBySuccess("查询成功", warehouseDTOS);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 选择货品
     */
    public ServerResponse selectProduct(String goodsId, String selectVal, String attributeIdArr) {
        return actuaryOperationService.selectProduct(goodsId, selectVal, attributeIdArr, "");
    }


    /**
     * 工匠补货查询商品库普通材料
     * 是大管家就查询商品库服务材料
     */
    public ServerResponse repairLibraryMaterial(String userToken, String categoryId, String name, PageDTO pageDTO) {
        try {
            Object object = memberAPI.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            JSONObject job = (JSONObject)object;
            Member worker = job.toJavaObject(Member.class);
            List<GoodsDTO> goodsDTOList = new ArrayList<>();
            String productType = "0";
            if (worker.getWorkerType() == 3) {//大管家
                productType = "1";
            }
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<Product> productList = iProductMapper.queryProductData(name, categoryId, productType, null);
            PageInfo pageResult = new PageInfo(productList);
            if (productList.size() > 0) {
                for (Product product : productList) {
                    GoodsDTO goodsDTO = actuaryOperationService.goodsDetail(product, null);
                    if (goodsDTO != null) {
                        goodsDTOList.add(goodsDTO);
                    }
                }
            }
            pageResult.setList(goodsDTOList);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 工匠补查询精算内货品
     */
    public ServerResponse workerTypeBudget(String userToken, String houseId, String categoryId, String name, PageDTO pageDTO) {
        try {
            Object object = memberAPI.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            JSONObject job = (JSONObject)object;
            Member worker = job.toJavaObject(Member.class);
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            List<BudgetMaterial> budgetMaterialList = budgetMaterialMapper.repairBudgetMaterial(worker.getWorkerTypeId(), houseId, categoryId, name, "0");
            PageInfo pageResult = new PageInfo(budgetMaterialList);
            List<WarehouseDTO> warehouseDTOS = new ArrayList<>();
            for (BudgetMaterial budgetMaterial : budgetMaterialList) {
                ServerResponse response = technologyRecordAPI.getByProductId(budgetMaterial.getProductId(), houseId);
                if (!response.isSuccess()) continue;
                Object warehouseStr = response.getResultObj();
                Warehouse warehouse = JSON.parseObject(JSON.toJSONString(warehouseStr), Warehouse.class);
                if (warehouse == null) continue;
                WarehouseDTO warehouseDTO = new WarehouseDTO();

                Product product = iProductMapper.selectByPrimaryKey(warehouse.getProductId());
                warehouseDTO.setImage(address + product.getImage());
                warehouseDTO.setShopCount(warehouse.getShopCount());
                warehouseDTO.setAskCount(warehouse.getAskCount());
                warehouseDTO.setBackCount((warehouse.getWorkBack() == null ? 0D : warehouse.getWorkBack()));
                warehouseDTO.setRealCount(warehouse.getShopCount() - warehouse.getBackCount());
                warehouseDTO.setSurCount(warehouse.getShopCount() - (warehouse.getOwnerBack() == null ? 0D : warehouse.getOwnerBack()) - warehouse.getAskCount());
                warehouseDTO.setProductName(product.getName());
                warehouseDTO.setPrice(warehouse.getPrice());
                warehouseDTO.setTolPrice(warehouseDTO.getRealCount() * warehouse.getPrice());
                warehouseDTO.setReceive(warehouse.getReceive() - (warehouse.getWorkBack() == null ? 0D : warehouse.getWorkBack()));
                warehouseDTO.setUnitName(warehouse.getUnitName());
                warehouseDTO.setProductType(warehouse.getProductType());
                warehouseDTO.setAskTime(warehouse.getAskTime());
                warehouseDTO.setRepTime(warehouse.getRepTime());
                warehouseDTO.setBackTime(warehouse.getBackTime());
                warehouseDTO.setBrandSeriesName(forMasterService.brandSeriesName(warehouse.getProductId()));
                warehouseDTO.setProductId(warehouse.getProductId());
                warehouseDTOS.add(warehouseDTO);
            }
            pageResult.setList(warehouseDTOS);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 查询工序材料
     */
    public ServerResponse repairBudgetMaterial(String workerTypeId, String categoryId, String houseId, String productName, String productType,
                                               PageDTO pageDTO) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            List<BudgetMaterial> budgetMaterialList = budgetMaterialMapper.repairBudgetMaterial(workerTypeId, houseId, categoryId, productName, productType);
            PageInfo pageResult = new PageInfo(budgetMaterialList);
            List<BudgetMaterialDTO> budgetMaterialDTOS = new ArrayList<>();
            for (BudgetMaterial budgetMaterial : budgetMaterialList) {
                Product product = iProductMapper.selectByPrimaryKey(budgetMaterial.getProductId());
                BudgetMaterialDTO budgetMaterialDTO = new BudgetMaterialDTO();
                budgetMaterialDTO.setId(budgetMaterial.getId());
                budgetMaterialDTO.setProductId(budgetMaterial.getProductId());
                budgetMaterialDTO.setProductSn(budgetMaterial.getProductSn());
                budgetMaterialDTO.setProductName(budgetMaterial.getProductName());
                budgetMaterialDTO.setImage(budgetMaterial.getImage() == null ? "" : address + budgetMaterial.getImage());
                budgetMaterialDTO.setProductNickName(budgetMaterial.getProductNickName());
                budgetMaterialDTO.setPrice(budgetMaterial.getPrice());
                budgetMaterialDTO.setCost(budgetMaterial.getCost());
                budgetMaterialDTO.setShopCount(budgetMaterial.getShopCount());//购买数量
                budgetMaterialDTO.setConvertCount(budgetMaterial.getConvertCount());//转换后的购买总数
                budgetMaterialDTO.setUnitName(budgetMaterial.getUnitName());
                budgetMaterialDTO.setProductType(budgetMaterial.getProductType());
                budgetMaterialDTO.setCategoryId(budgetMaterial.getCategoryId());
                if(product!=null) {
                    budgetMaterialDTO.setProductName(product.getName());
                    budgetMaterialDTO.setImage(product.getImage() == null ? "" : address + product.getImage());
                    budgetMaterialDTOS.add(budgetMaterialDTO);
                }
            }
            pageResult.setList(budgetMaterialDTOS);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

}
