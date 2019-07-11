package com.dangjia.acg.service.house;

import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.house.SurplusWareHouseProductDTO;
import com.dangjia.acg.mapper.house.ISurplusWareDivertMapper;
import com.dangjia.acg.mapper.house.ISurplusWareHouseItemMapper;
import com.dangjia.acg.modle.house.SurplusWareDivert;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * author: ysl
 * Date: 2019/1/24 0018
 * Time: 10:54
 */
@Service
public class SurplusWareHouseItemService {
    @Autowired
    private ISurplusWareHouseItemMapper iSurplusWareHouseItemMapper;
    @Autowired
    private ISurplusWareDivertMapper iSurplusWareDivertMapper;


    /**
     * 查询所有商品的库存 ，按照address 或 商品名字模糊查询
     *
     * @param pageDTO
     * @param address
     * @param productName
     * @return
     */
    public ServerResponse getAllProductsLikeAddressOrPName(PageDTO pageDTO, String address, String productName) {
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<SurplusWareHouseProductDTO> dtoList = iSurplusWareHouseItemMapper.getAllProductsLikeAddressOrPName(address, productName);
        if (dtoList.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        PageInfo pageResult = new PageInfo(dtoList);
        for (SurplusWareHouseProductDTO pto : dtoList) {
            Integer allProductCount = 0;
            List<Map<String, Object>> objList = iSurplusWareHouseItemMapper.getAllSurplusWareHouseListByPId(pto.getProductId());
            for (Map<String, Object> mapObj : objList) {
                Integer productCount = (Integer) mapObj.get("productCount");
                allProductCount = allProductCount + productCount;
            }
            pto.setProductAllCount(allProductCount);
            pto.setSurplusWareHouseAllCount(objList.size());
            SurplusWareDivert surplusWareDivert = iSurplusWareDivertMapper.getDivertBySIdAndPidSortDate(pto.getProductId());
            if (surplusWareDivert != null)
                pto.setMinDivertDate(surplusWareDivert.getDivertDate());
        }
        pageResult.setList(dtoList);
        return ServerResponse.createBySuccess("查询成功", pageResult);
    }


    /**
     * 查询指定productId的所有仓库
     *
     * @param pageDTO
     * @param productId
     * @return
     */
    public ServerResponse getAllSurplusWareHouseListByPId(PageDTO pageDTO, String productId) {
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<Map<String, Object>> mapList = iSurplusWareHouseItemMapper.getAllSurplusWareHouseListByPId(productId);
        if (mapList.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        PageInfo pageResult = new PageInfo(mapList);
        return ServerResponse.createBySuccess("查询成功", pageResult);
    }

}
