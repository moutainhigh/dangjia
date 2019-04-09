package com.dangjia.acg.service.house;

import com.dangjia.acg.api.actuary.ActuaryOpeAPI;
import com.dangjia.acg.api.basics.GoodsCategoryAPI;
import com.dangjia.acg.api.data.ForMasterAPI;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.budget.BudgetItemDTO;
import com.dangjia.acg.dto.house.WarehouseDTO;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.house.IWarehouseMapper;
import com.dangjia.acg.modle.attribute.GoodsCategory;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.house.Warehouse;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WarehouseService {

    @Autowired
    private IWarehouseMapper warehouseMapper;
    @Autowired
    private GoodsCategoryAPI goodsCategoryAPI;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private ForMasterAPI forMasterAPI;
    @Autowired
    private ActuaryOpeAPI actuaryOpeAPI;
    @Autowired
    private IHouseMapper houseMapper;
    private static Logger LOG = LoggerFactory.getLogger(WarehouseService.class);


    /**
     * 查询仓库材料
     * type 0材料 1服务 2所有
     */
    public ServerResponse warehouseList(Integer pageNum, Integer pageSize, String houseId, String categoryId, String name, Integer type) {
        try {
            if (StringUtil.isEmpty(houseId)) {
                return ServerResponse.createByErrorMessage("houseId不能为空");
            }
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            Example example=new Example(Warehouse.class);
            Example.Criteria criteria=example.createCriteria();
            criteria.andEqualTo(Warehouse.HOUSE_ID,houseId);
            if(type!=null&&type<2){
                criteria.andEqualTo(Warehouse.PRODUCT_TYPE,type);
            }
            if(!CommonUtil.isEmpty(categoryId)){
                criteria.andEqualTo(Warehouse.CATEGORY_ID,categoryId);
            }
            if(!CommonUtil.isEmpty(name)){
                criteria.andLike(Warehouse.PRODUCT_NAME,"%"+name+"%");
            }
            PageHelper.startPage(pageNum, pageSize);
            List<Warehouse> warehouseList=warehouseMapper.selectByExample(example);
            LOG.info(" warehouseList size:" + warehouseList.size());
            PageInfo pageResult = new PageInfo(warehouseList);
            List<WarehouseDTO> warehouseDTOS = new ArrayList<>();
            House house = houseMapper.selectByPrimaryKey(houseId);
            for (Warehouse warehouse : warehouseList) {
                WarehouseDTO warehouseDTO = new WarehouseDTO();
                BeanUtils.beanToBean(warehouse,warehouseDTO);
                warehouseDTO.setImage(address + warehouse.getImage());
                warehouseDTO.setRealCount(warehouse.getShopCount() - warehouse.getBackCount());
                warehouseDTO.setSurCount(warehouse.getShopCount()  - warehouse.getBackCount() - warehouse.getAskCount());//剩余数量 所有买的数量 - 退货 - 收的
                warehouseDTO.setTolPrice(warehouseDTO.getRealCount() * warehouse.getPrice());
                warehouseDTO.setBrandSeriesName(forMasterAPI.brandSeriesName(house.getCityId(), warehouse.getProductId()));
                warehouseDTO.setRepairCount(warehouse.getRepairCount());
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
     * 查询仓库材料（已购买）
     * type 0材料 1服务 2人工
     */
    public ServerResponse warehouseGmList(HttpServletRequest request, String houseId, String name, Integer type) {
        try {
            String cityId = request.getParameter(Constants.CITY_ID);
            if (StringUtil.isEmpty(houseId)) {
                return ServerResponse.createByErrorMessage("houseId不能为空");
            }
            Map<String, Map> maps = new HashMap<>();
            Map map=new HashMap();
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            if(type!=null&&type==2){
                List<BudgetItemDTO> budgetItemDTOS= actuaryOpeAPI.getHouseWorkerInfo(cityId,"3",houseId,address);
                map.put("goodsItemDTOList",budgetItemDTOS);
            }else {
                Example example = new Example(Warehouse.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo(Warehouse.HOUSE_ID, houseId);
                if (type != null && type < 2) {
                    criteria.andEqualTo(Warehouse.PRODUCT_TYPE, type);
                }
                if (!CommonUtil.isEmpty(name)) {
                    criteria.andLike(Warehouse.PRODUCT_NAME, "%" + name + "%");
                }
                List<Warehouse> warehouseList = warehouseMapper.selectByExample(example);
                LOG.info(" warehouseList size:" + warehouseList.size());

                List<String> categoryIdList = warehouseMapper.categoryIdList(houseId);
                for (String categoryId : categoryIdList) {
                    //获取低级类别
                    GoodsCategory goodsCategoryNext = goodsCategoryAPI.getGoodsCategory(request, categoryId);
                    if (goodsCategoryNext == null) {
                        continue;
                    }
                    //获取顶级类别
                    GoodsCategory goodsCategoryParentTop = goodsCategoryAPI.getGoodsCategory(request, goodsCategoryNext.getParentTop());
                    GoodsCategory goodsCategory;
                    if (goodsCategoryParentTop == null) {
                        goodsCategory = goodsCategoryNext;
                    } else {
                        goodsCategory = goodsCategoryParentTop;
                    }
                    //重临时缓存maps中取出BudgetItemDTO
                    Map budgetItemDTO = maps.get(goodsCategory.getId());
                    BigDecimal rowPrice = new BigDecimal(0);
                    List<WarehouseDTO> warehouseDTOS = new ArrayList<>();
                    if (budgetItemDTO == null) {
                        //如果没有将BudgetItemDTO初始化
                        budgetItemDTO = new HashMap();
                        budgetItemDTO.put("rowImage", address + goodsCategory.getImage());
                        budgetItemDTO.put("rowName", goodsCategory.getName());
                        budgetItemDTO.put("rowPrice", rowPrice);
                    }else{
                        rowPrice=(BigDecimal)budgetItemDTO.get("rowPrice");
                        warehouseDTOS=(List<WarehouseDTO>)budgetItemDTO.get("goodsItems");
                    }
                    for (Warehouse warehouse : warehouseList) {
                        if (!categoryId.equals(warehouse.getCategoryId())) continue;
                        WarehouseDTO warehouseDTO = new WarehouseDTO();
                        BeanUtils.beanToBean(warehouse, warehouseDTO);
                        warehouseDTO.setImage(address + warehouse.getImage());
                        warehouseDTO.setRealCount(warehouse.getShopCount() - warehouse.getBackCount());
                        warehouseDTO.setSurCount(warehouse.getShopCount() - warehouse.getAskCount() - warehouse.getBackCount());
                        warehouseDTO.setTolPrice(warehouseDTO.getRealCount() * warehouse.getPrice());
                        warehouseDTO.setBrandSeriesName(forMasterAPI.brandSeriesName(cityId,warehouse.getProductId()));
                        warehouseDTOS.add(warehouseDTO);
                        rowPrice = rowPrice.add(new BigDecimal(warehouseDTO.getTolPrice()));
                    }
                    budgetItemDTO.put("rowPrice", rowPrice);
                    budgetItemDTO.put("goodsItems", warehouseDTOS);
                    maps.put(goodsCategory.getId(), budgetItemDTO);
                }
                List<Map> budgetItemDTOList = new ArrayList<>();
                for (Map.Entry<String, Map> entry : maps.entrySet()) {
                    budgetItemDTOList.add(entry.getValue());
                }
                map.put("goodsItemDTOList", budgetItemDTOList);
            }
            Double workerPrice=actuaryOpeAPI.getHouseWorkerPrice(cityId,"3",houseId);
            Double caiPrice=warehouseMapper.getHouseGoodsPrice(houseId,name);
            Double totalPrice=workerPrice+caiPrice;
            map.put("workerPrice", workerPrice);
            map.put("caiPrice", caiPrice);
            map.put("totalPrice", totalPrice);
            return ServerResponse.createBySuccess("查询成功", map);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }
}
