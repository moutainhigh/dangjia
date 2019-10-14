package com.dangjia.acg.service.delivery;

import com.dangjia.acg.api.StorefrontProductAPI;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.delivery.DjDeliveryReturnSlipDetailsDTO;
import com.dangjia.acg.dto.delivery.DjDeliveryReturnSlipDetailsProductDTO;
import com.dangjia.acg.dto.storefront.StorefrontProductDTO;
import com.dangjia.acg.mapper.delivery.DjDeliveryReturnSlipDetailsMapper;
import com.dangjia.acg.mapper.delivery.DjDeliveryReturnSlipMapper;
import com.dangjia.acg.modle.delivery.DjDeliveryReturnSlipDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 14/10/2019
 * Time: 下午 3:58
 */
@Service
public class DjDeliveryReturnSlipDetailsService {

    @Autowired
    private DjDeliveryReturnSlipDetailsMapper djDeliveryReturnSlipDetailsMapper;
    @Autowired
    private DjDeliveryReturnSlipMapper djDeliveryReturnSlipMapper;
    @Autowired
    private StorefrontProductAPI StorefrontProductAPI;
    private static Logger logger = LoggerFactory.getLogger(DjDeliveryReturnSlipDetailsService.class);


    /**
     * 任务详情
     * @param id
     * @return
     */
    public ServerResponse queryTaskDetails(String id) {
        try {
            List<DjDeliveryReturnSlipDetailsDTO> djDeliveryReturnSlipDetailsDTOS = djDeliveryReturnSlipDetailsMapper.queryOrderInformation(id);
            Example example=new Example(DjDeliveryReturnSlipDetails.class);
            djDeliveryReturnSlipDetailsDTOS.forEach(djDeliveryReturnSlipDetailsDTO -> {
                 djDeliveryReturnSlipDetailsDTO = djDeliveryReturnSlipDetailsMapper.queryWorkerInfByHouseId(djDeliveryReturnSlipDetailsDTO.getHouseId());
                 example.createCriteria().andEqualTo(DjDeliveryReturnSlipDetails.DELIVERY_RETURN_SLIP_ID,djDeliveryReturnSlipDetailsDTO.getId());
                 List<DjDeliveryReturnSlipDetails> djDeliveryReturnSlipDetails = djDeliveryReturnSlipDetailsMapper.selectByExample(example);
                 List<DjDeliveryReturnSlipDetailsProductDTO> djDeliveryReturnSlipDetailsProductDTOS=new ArrayList<>();
                 djDeliveryReturnSlipDetails.forEach(djDeliveryReturnSlipDetails1 -> {
                     DjDeliveryReturnSlipDetailsProductDTO djDeliveryReturnSlipDetailsProductDTO=new DjDeliveryReturnSlipDetailsProductDTO();
                     djDeliveryReturnSlipDetailsProductDTO.setQuantity(djDeliveryReturnSlipDetails1.getQuantity());
                     djDeliveryReturnSlipDetailsProductDTO.setTotalPrices(djDeliveryReturnSlipDetails1.getTotalPrices());
                     djDeliveryReturnSlipDetailsProductDTO.setUnitPrice(djDeliveryReturnSlipDetails1.getUnitPrice());
                     StorefrontProductDTO storefrontProductDTO = StorefrontProductAPI.querySingleStorefrontProductById(djDeliveryReturnSlipDetails1.getStorefrontProductId());
                     djDeliveryReturnSlipDetailsProductDTO.setProductName(storefrontProductDTO.getProductName());
                     djDeliveryReturnSlipDetailsProductDTO.setProductSn(storefrontProductDTO.getProductSn());
                     djDeliveryReturnSlipDetailsProductDTOS.add(djDeliveryReturnSlipDetailsProductDTO);
                 });
                djDeliveryReturnSlipDetailsDTO.setDjDeliveryReturnSlipDetailsProductDTOS(djDeliveryReturnSlipDetailsProductDTOS);
            });
            if(djDeliveryReturnSlipDetailsDTOS.size()<=0)
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(),ServerCode.NO_DATA.getDesc());
            return ServerResponse.createBySuccess("查询成功",djDeliveryReturnSlipDetailsDTOS);
        } catch (Exception e) {
            logger.error("查询失败",e);
            return ServerResponse.createByErrorMessage("查询失败"+e);
        }
    }


}
