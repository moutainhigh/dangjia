package com.dangjia.acg.service.repair;

import com.alibaba.fastjson.JSON;
import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.api.data.GetForBudgetAPI;
import com.dangjia.acg.api.data.TechnologyRecordAPI;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.actuary.GoodsDTO;
import com.dangjia.acg.dto.house.WarehouseDTO;
import com.dangjia.acg.dto.repair.BudgetMaterialDTO;
import com.dangjia.acg.mapper.actuary.IBudgetMaterialMapper;
import com.dangjia.acg.mapper.basics.IProductMapper;
import com.dangjia.acg.modle.actuary.BudgetMaterial;
import com.dangjia.acg.modle.basics.Product;
import com.dangjia.acg.modle.house.Warehouse;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.repair.MendMateriel;
import com.dangjia.acg.service.actuary.ActuaryOperationService;
import com.dangjia.acg.service.data.ForMasterService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
    private RedisClient redisClient;
    @Autowired
    private TechnologyRecordAPI technologyRecordAPI;
    @Autowired
    private ForMasterService forMasterService;
    @Autowired
    private GetForBudgetAPI getForBudgetAPI;


    /**
     * 要退查询仓库
     * 结合 精算记录+补记录
     */
    public ServerResponse askAndQuit(String userToken, String houseId, String categoryId, String name) {
        try {
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            Member worker = accessToken.getMember();
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);

            //精算的
            List<BudgetMaterial> budgetMaterialList = budgetMaterialMapper.repairBudgetMaterial(worker.getWorkerTypeId(), houseId, categoryId, name,"0");
            //补材料的
            List<MendMateriel> mendMaterielList = getForBudgetAPI.askAndQuit(worker.getWorkerTypeId(), houseId,categoryId,name);
            List<WarehouseDTO> warehouseDTOS = new ArrayList<>();

            List<String> productIdList = new ArrayList<>();
            String productId;

            for(MendMateriel mendMateriel : mendMaterielList){
                boolean flag = true;
                productId = mendMateriel.getProductId();
                for(BudgetMaterial bm : budgetMaterialList){
                    if(productId.equals(bm.getProductId())){
                        flag = false;
                        continue;
                    }
                }
                if(flag){
                    productIdList.add(productId);
                }
            }
            for(BudgetMaterial bm : budgetMaterialList){
                productIdList.add(bm.getProductId());
            }

            for (String id : productIdList) {
                ServerResponse response = technologyRecordAPI.getByProductId(id, houseId);
                //if(!response.isSuccess()) continue;
                Object warehouseStr = response.getResultObj();
                Warehouse warehouse = JSON.parseObject(JSON.toJSONString(warehouseStr),Warehouse.class);
                if(warehouse == null) continue;
                WarehouseDTO warehouseDTO = new WarehouseDTO();
                warehouseDTO.setImage(address + warehouse.getImage());
                warehouseDTO.setShopCount(warehouse.getShopCount());
                warehouseDTO.setAskCount(warehouse.getAskCount());
                warehouseDTO.setBackCount(warehouse.getBackCount());
                warehouseDTO.setRealCount(warehouse.getShopCount() - warehouse.getBackCount());
                warehouseDTO.setSurCount(warehouse.getShopCount() - warehouse.getBackCount() - warehouse.getReceive());//所有买的数量 - 退货 - 收的
                warehouseDTO.setProductName(warehouse.getProductName());
                warehouseDTO.setPrice(warehouse.getPrice());
                warehouseDTO.setTolPrice(warehouseDTO.getRealCount() * warehouse.getPrice());
                warehouseDTO.setReceive(warehouse.getReceive());
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
    public ServerResponse selectProduct(String goodsId, String brandId, String brandSeriesId, String attributeIdArr) {
        return actuaryOperationService.selectProduct(goodsId, brandId, brandSeriesId, attributeIdArr, "");
    }


    /**
     * 工匠补货查询商品库普通材料
     * 是大管家就查询商品库服务材料
     */
    public ServerResponse repairLibraryMaterial(String userToken,String categoryId, String name, Integer pageNum, Integer pageSize) {
        try {
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            Member worker = accessToken.getMember();
            List<GoodsDTO> goodsDTOList = new ArrayList<>();
            String productType="0";
            if (worker.getWorkerType() == 3){//大管家
                productType="1";
            }
            PageHelper.startPage(pageNum, pageSize);
            List<Product>  productList = iProductMapper.queryProductData(name,categoryId,productType);
            PageInfo pageResult = new PageInfo(productList);
            if (productList.size() > 0) {
                for (int i = 0; i < productList.size(); i++) {
                    Product product=productList.get(i);
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
    public ServerResponse workerTypeBudget(String userToken, String houseId, String categoryId, String name, Integer pageNum, Integer pageSize) {
        try {
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            Member worker = accessToken.getMember();
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            PageHelper.startPage(pageNum, pageSize);
            List<BudgetMaterial> budgetMaterialList = budgetMaterialMapper.repairBudgetMaterial(worker.getWorkerTypeId(), houseId, categoryId, name,"0");
            PageInfo pageResult = new PageInfo(budgetMaterialList);
            List<WarehouseDTO> warehouseDTOS = new ArrayList<>();
            for (BudgetMaterial budgetMaterial : budgetMaterialList) {
                ServerResponse response = technologyRecordAPI.getByProductId(budgetMaterial.getProductId(), houseId);
                if(!response.isSuccess()) continue;
                Object warehouseStr = response.getResultObj();
                Warehouse warehouse = JSON.parseObject(JSON.toJSONString(warehouseStr),Warehouse.class);
                if(warehouse == null) continue;
                WarehouseDTO warehouseDTO = new WarehouseDTO();
                warehouseDTO.setImage(address + warehouse.getImage());
                warehouseDTO.setShopCount(warehouse.getShopCount());
                warehouseDTO.setAskCount(warehouse.getAskCount());
                warehouseDTO.setBackCount(warehouse.getBackCount());
                warehouseDTO.setRealCount(warehouse.getShopCount() - warehouse.getBackCount());
                warehouseDTO.setSurCount(warehouse.getShopCount() - warehouse.getAskCount() - warehouse.getBackCount());
                warehouseDTO.setProductName(warehouse.getProductName());
                warehouseDTO.setPrice(warehouse.getPrice());
                warehouseDTO.setTolPrice(warehouseDTO.getRealCount() * warehouse.getPrice());
                warehouseDTO.setReceive(warehouse.getReceive());
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
    public ServerResponse repairBudgetMaterial(String workerTypeId, String categoryId, String houseId, String productName,String productType,
                                               Integer pageNum, Integer pageSize) {
        try {
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            PageHelper.startPage(pageNum, pageSize);
            List<BudgetMaterial> budgetMaterialList = budgetMaterialMapper.repairBudgetMaterial(workerTypeId, houseId, categoryId, productName,productType);
            PageInfo pageResult = new PageInfo(budgetMaterialList);
            List<BudgetMaterialDTO> budgetMaterialDTOS = new ArrayList<>();
            for (BudgetMaterial budgetMaterial : budgetMaterialList) {
                Product product=iProductMapper.selectByPrimaryKey(budgetMaterial.getProductId());
                BudgetMaterialDTO budgetMaterialDTO = new BudgetMaterialDTO();
                budgetMaterialDTO.setId(budgetMaterial.getId());
                budgetMaterialDTO.setProductId(budgetMaterial.getProductId());
                budgetMaterialDTO.setProductSn(budgetMaterial.getProductSn());
                budgetMaterialDTO.setProductName(product.getName());
                budgetMaterialDTO.setProductNickName(budgetMaterial.getProductNickName());
                budgetMaterialDTO.setPrice(budgetMaterial.getPrice());
                budgetMaterialDTO.setCost(budgetMaterial.getCost());
                budgetMaterialDTO.setShopCount(budgetMaterial.getShopCount());//购买数量
                budgetMaterialDTO.setConvertCount(budgetMaterial.getConvertCount());//转换后的购买总数
                budgetMaterialDTO.setUnitName(budgetMaterial.getUnitName());
                budgetMaterialDTO.setProductType(budgetMaterial.getProductType());
                budgetMaterialDTO.setCategoryId(budgetMaterial.getCategoryId());
                budgetMaterialDTO.setImage(product.getImage() == null ? "" : address + product.getImage());
                budgetMaterialDTOS.add(budgetMaterialDTO);
            }
            pageResult.setList(budgetMaterialDTOS);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

}
