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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
            List<Warehouse> warehouseList;

            PageHelper.startPage(pageNum, pageSize);
            if (type == 0) {//材料
                warehouseList = warehouseMapper.materialsList(houseId, categoryId, name);
            } else if (type == 1) {//服务
                PageHelper.startPage(pageNum, pageSize);
                warehouseList = warehouseMapper.serverList(houseId, categoryId, name);
            } else {
                PageHelper.startPage(pageNum, pageSize);
                warehouseList = warehouseMapper.warehouseList(houseId, categoryId, name);
            }
            LOG.info(" warehouseList size:" + warehouseList.size());
            PageInfo pageResult = new PageInfo(warehouseList);
            List<WarehouseDTO> warehouseDTOS = new ArrayList<>();
            for (Warehouse warehouse : warehouseList) {
                WarehouseDTO warehouseDTO = new WarehouseDTO();
                warehouseDTO.setImage(address + warehouse.getImage());
                warehouseDTO.setShopCount(warehouse.getShopCount());
                warehouseDTO.setAskCount(warehouse.getAskCount());
                warehouseDTO.setBackCount(warehouse.getBackCount());
                warehouseDTO.setReceive(warehouse.getReceive());//收货数
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
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }
}
