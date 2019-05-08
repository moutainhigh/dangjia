package com.dangjia.acg.service.house;

import com.dangjia.acg.api.data.ForMasterAPI;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.house.SurplusWareHouseProductDTO;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.house.ISurplusWareDivertMapper;
import com.dangjia.acg.mapper.house.ISurplusWareHouseItemMapper;
import com.dangjia.acg.mapper.house.ISurplusWareHouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.modle.house.SurplusWareDivert;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private IMemberMapper iMemberMapper;
    @Autowired
    private ISurplusWareHouseMapper iSurplusWareHouseMapper;
    @Autowired
    private ISurplusWareHouseItemMapper iSurplusWareHouseItemMapper;
    @Autowired
    private ISurplusWareDivertMapper iSurplusWareDivertMapper;
    @Autowired
    private IHouseMapper iHouseMapper;
    @Autowired
    private ForMasterAPI forMasterAPI;
    @Autowired
    private static Logger LOG = LoggerFactory.getLogger(SurplusWareHouseItemService.class);


    /**
     * 查询所有商品的库存 ，按照address 或 商品名字模糊查询
     *
     * @param pageDTO
     * @param address
     * @param productName
     * @return
     */
    public ServerResponse getAllProductsLikeAddressOrPName(PageDTO pageDTO, String address, String productName) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());

            List<SurplusWareHouseProductDTO> dtoList = iSurplusWareHouseItemMapper.getAllProductsLikeAddressOrPName(address, productName);
            LOG.info(" getAllProductsLikeAddressOrPName list:" + dtoList);
            PageInfo pageResult = new PageInfo(dtoList);
            for (SurplusWareHouseProductDTO pto : dtoList) {
                Integer count = 0;
                Integer allProductCount = 0;
                pto.setSurplusWareHouseAllCount(0);
                pto.setProductAllCount(0);
                List<Map<String, Object>> objList = iSurplusWareHouseItemMapper.getAllSurplusWareHouseListByPId(pto.getProductId());
                for (Map<String, Object> mapObj : objList) {
                    count++;
                    Integer productCount = (Integer) mapObj.get("productCount");
                    allProductCount = allProductCount + productCount;
                }
                pto.setProductAllCount(allProductCount);
                pto.setSurplusWareHouseAllCount(count);

                SurplusWareDivert surplusWareDivert = iSurplusWareDivertMapper.getDivertBySIdAndPidSortDate(pto.getProductId());
                if (surplusWareDivert != null)
                    pto.setMinDivertDate(surplusWareDivert.getDivertDate());
            }
            pageResult.setList(dtoList);
            return ServerResponse.createBySuccess("查询成功", pageResult);
//            return ServerResponse.createByErrorMessage("查询失败");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }

    }


    /**
     * 查询指定productId的所有仓库
     *
     * @param pageDTO
     * @param productId
     * @return
     */
    public ServerResponse getAllSurplusWareHouseListByPId(PageDTO pageDTO, String productId) {
        try {

            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<Map<String, Object>> mapList = iSurplusWareHouseItemMapper.getAllSurplusWareHouseListByPId(productId);
//            obj.put("goodsGroup", map);
//            obj.put("mapList", mapList);
//            list.add(obj);
            PageInfo pageResult = new PageInfo(mapList);
            pageResult.setList(mapList);
            return ServerResponse.createBySuccess("查询成功", pageResult);
//            iSurplusWareHouseItemMapper.getAllSurplusWareHouseListByPId(productId);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }

    }

}
