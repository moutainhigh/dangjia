package com.dangjia.acg.service.house;

import com.dangjia.acg.api.data.ForMasterAPI;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.house.WarehouseDTO;
import com.dangjia.acg.mapper.house.IWarehouseMapper;
import com.dangjia.acg.modle.house.Warehouse;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

@Service
public class WarehouseService {

    @Autowired
    private IWarehouseMapper warehouseMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private ForMasterAPI forMasterAPI;


    /**
     * 查询仓库材料
     */
    public ServerResponse warehouseList(Integer pageNum, Integer pageSize,String houseId,String categoryId, String name){
        try{
            if(StringUtil.isEmpty(houseId)){
                return ServerResponse.createByErrorMessage("houseId不能为空");
            }
            if(pageNum == null){
                pageNum = 1;
            }
            if(pageSize == null){
                pageSize = 5;
            }
            PageHelper.startPage(pageNum, pageSize);
            List<Warehouse> warehouseList = warehouseMapper.warehouseList(houseId,categoryId,name);
            PageInfo pageResult = new PageInfo(warehouseList);
            List<WarehouseDTO> warehouseDTOS = new ArrayList<WarehouseDTO>();
            for (Warehouse warehouse : warehouseList){
                WarehouseDTO warehouseDTO = new WarehouseDTO();
                warehouseDTO.setImage(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class) + warehouse.getImage());
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
                warehouseDTO.setBrandSeriesName(forMasterAPI.brandSeriesName(warehouse.getProductId()));
                warehouseDTO.setProductId(warehouse.getProductId());
                warehouseDTOS.add(warehouseDTO);
            }
            pageResult.setList(warehouseDTOS);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }
}
