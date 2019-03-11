package com.dangjia.acg.service.repair;

import com.dangjia.acg.api.RedisClient;
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
import com.dangjia.acg.service.actuary.ActuaryOperationService;
import com.dangjia.acg.service.data.ForMasterService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

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


    /**
     * 要退查询仓库
     * 结合 精算记录+补记录
     */


    /**
     * 选择货品
     */
    public ServerResponse selectProduct(String goodsId, String brandId, String brandSeriesId, String attributeIdArr) {
        return actuaryOperationService.selectProduct(goodsId, brandId, brandSeriesId, attributeIdArr, "");
    }


    /**
     * 补货查询商品库
     */
    public ServerResponse repairLibraryMaterial(String categoryId, String name, Integer pageNum, Integer pageSize) {
        try {
            if (pageNum == null) {
                pageNum = 1;
            }
            if (pageSize == null) {
                pageSize = 5;
            }
            List<GoodsDTO> goodsDTOList = new ArrayList<>();
            PageHelper.startPage(pageNum, pageSize);
            List<Product> productList;
            if (StringUtil.isEmpty(categoryId)) {
                Example example = new Example(Product.class);
                example.createCriteria().andLike(Product.NAME, "%" + name + "%");
                productList = iProductMapper.selectByExample(example);
            } else {
                Example example = new Example(Product.class);
                example.createCriteria().andEqualTo(Product.CATEGORY_ID, categoryId).andLike(Product.NAME, "%" + name + "%");
                productList = iProductMapper.selectByExample(example);
            }
            PageInfo pageResult = new PageInfo(productList);
            if (productList.size() > 0) {
                for (Product product : productList) {
                    GoodsDTO goodsDTO = actuaryOperationService.goodsDetail(product, null, 1);
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
            List<BudgetMaterial> budgetMaterialList = budgetMaterialMapper.repairBudgetMaterial(worker.getWorkerTypeId(), houseId, categoryId, name);
            PageInfo pageResult = new PageInfo(budgetMaterialList);
            List<WarehouseDTO> warehouseDTOS = new ArrayList<>();
            for (BudgetMaterial budgetMaterial : budgetMaterialList) {
                Warehouse warehouse = technologyRecordAPI.getByProductId(budgetMaterial.getProductId(), houseId);
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


    public ServerResponse repairBudgetMaterial(String workerTypeId, String categoryId, String houseId, String productName,
                                               Integer pageNum, Integer pageSize) {
        try {
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            PageHelper.startPage(pageNum, pageSize);
            List<BudgetMaterial> budgetMaterialList = budgetMaterialMapper.repairBudgetMaterial(workerTypeId, houseId, categoryId, productName);
            PageInfo pageResult = new PageInfo(budgetMaterialList);
            List<BudgetMaterialDTO> budgetMaterialDTOS = new ArrayList<>();
            for (BudgetMaterial budgetMaterial : budgetMaterialList) {
                BudgetMaterialDTO budgetMaterialDTO = new BudgetMaterialDTO();
                budgetMaterialDTO.setId(budgetMaterial.getId());
                budgetMaterialDTO.setProductId(budgetMaterial.getProductId());
                budgetMaterialDTO.setProductSn(budgetMaterial.getProductSn());
                budgetMaterialDTO.setProductName(budgetMaterial.getProductName());
                budgetMaterialDTO.setProductNickName(budgetMaterial.getProductNickName());
                budgetMaterialDTO.setPrice(budgetMaterial.getPrice());
                budgetMaterialDTO.setCost(budgetMaterial.getCost());
                budgetMaterialDTO.setShopCount(budgetMaterial.getShopCount());//购买数量
                budgetMaterialDTO.setConvertCount(budgetMaterial.getConvertCount());//转换后的购买总数
                budgetMaterialDTO.setUnitName(budgetMaterial.getUnitName());
                budgetMaterialDTO.setProductType(budgetMaterial.getProductType());
                budgetMaterialDTO.setCategoryId(budgetMaterial.getCategoryId());
                budgetMaterialDTO.setImage(budgetMaterial.getImage() == null ? "" : address + budgetMaterial.getImage());
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
