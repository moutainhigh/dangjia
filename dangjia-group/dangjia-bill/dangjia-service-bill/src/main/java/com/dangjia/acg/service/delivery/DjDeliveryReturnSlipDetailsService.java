package com.dangjia.acg.service.delivery;

import com.dangjia.acg.api.StorefrontProductAPI;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.delivery.DjDeliveryReturnSlipDetailsDTO;
import com.dangjia.acg.dto.delivery.DjDeliveryReturnSlipDetailsListDTO;
import com.dangjia.acg.mapper.delivery.DjDeliveryReturnSlipDetailsMapper;
import com.dangjia.acg.mapper.delivery.DjDeliveryReturnSlipMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

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
    @Autowired
    private ConfigUtil configUtil;

//
//    /**
//     * 任务详情
//     *
//     * @param id
//     * @return
//     */
//    public ServerResponse queryTaskDetails(String id) {
//        try {
//            List<DjDeliveryReturnSlipDetailsDTO> djDeliveryReturnSlipDetailsDTOS = djDeliveryReturnSlipDetailsMapper.queryOrderInformation(id);
//            Example example = new Example(DjDeliveryReturnSlipDetails.class);
//            djDeliveryReturnSlipDetailsDTOS.forEach(djDeliveryReturnSlipDetailsDTO -> {
//                DjDeliveryReturnSlipDetailsDTO djDeliveryReturnSlipDetailsDTO1 = djDeliveryReturnSlipDetailsMapper.queryWorkerInfByHouseId(djDeliveryReturnSlipDetailsDTO.getHouseId());
//                djDeliveryReturnSlipDetailsDTO.setWorkerName(djDeliveryReturnSlipDetailsDTO1.getWorkerName());
//                djDeliveryReturnSlipDetailsDTO.setWorkerMobile(djDeliveryReturnSlipDetailsDTO1.getWorkerMobile());
//                example.createCriteria().andEqualTo(DjDeliveryReturnSlipDetails.DELIVERY_RETURN_SLIP_ID, djDeliveryReturnSlipDetailsDTO.getId());
//                List<DjDeliveryReturnSlipDetails> djDeliveryReturnSlipDetails = djDeliveryReturnSlipDetailsMapper.selectByExample(example);
//                List<DjDeliveryReturnSlipDetailsProductDTO> djDeliveryReturnSlipDetailsProductDTOS = new ArrayList<>();
//                djDeliveryReturnSlipDetails.forEach(djDeliveryReturnSlipDetails1 -> {
//                    DjDeliveryReturnSlipDetailsProductDTO djDeliveryReturnSlipDetailsProductDTO = new DjDeliveryReturnSlipDetailsProductDTO();
//                    djDeliveryReturnSlipDetailsProductDTO.setQuantity(djDeliveryReturnSlipDetails1.getQuantity());
//                    djDeliveryReturnSlipDetailsProductDTO.setTotalPrices(djDeliveryReturnSlipDetails1.getTotalPrices());
//                    djDeliveryReturnSlipDetailsProductDTO.setUnitPrice(djDeliveryReturnSlipDetails1.getUnitPrice());
//                    StorefrontProductListDTO storefrontProductDTO = StorefrontProductAPI.querySingleStorefrontProductById(djDeliveryReturnSlipDetails1.getStorefrontProductId());
//                    djDeliveryReturnSlipDetailsProductDTO.setProductName(storefrontProductDTO.getProductName());
//                    djDeliveryReturnSlipDetailsProductDTO.setProductSn(storefrontProductDTO.getProductSn());
//                    djDeliveryReturnSlipDetailsProductDTO.setImage(storefrontProductDTO.getImage());
//                    djDeliveryReturnSlipDetailsProductDTOS.add(djDeliveryReturnSlipDetailsProductDTO);
//                });
//                djDeliveryReturnSlipDetailsDTO.setDjDeliveryReturnSlipDetailsProductDTOS(djDeliveryReturnSlipDetailsProductDTOS);
//            });
//            if (djDeliveryReturnSlipDetailsDTOS.size() <= 0) {
//                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
//            }
//            return ServerResponse.createBySuccess("查询成功", djDeliveryReturnSlipDetailsDTOS);
//        } catch (Exception e) {
//            logger.error("查询失败", e);
//            return ServerResponse.createByErrorMessage("查询失败" + e);
//        }
//    }


    public ServerResponse queryTaskDetails(String houseId,String number,String splitId, Integer invoiceType) {
        try {
            String imageaddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            List<DjDeliveryReturnSlipDetailsDTO> djDeliveryReturnSlipDetailsDTOS=null;
            if(invoiceType==1) {
                djDeliveryReturnSlipDetailsDTOS = djDeliveryReturnSlipDetailsMapper.queryDeliverOrderInformation(splitId);
            }else {
                djDeliveryReturnSlipDetailsDTOS = djDeliveryReturnSlipDetailsMapper.queryRepairOrderInformation(splitId);
            }
            DjDeliveryReturnSlipDetailsListDTO djDeliveryReturnSlipDetailsListDTO=new DjDeliveryReturnSlipDetailsListDTO();
            DjDeliveryReturnSlipDetailsListDTO djDeliveryReturnSlipDetailsListDTO1 = djDeliveryReturnSlipDetailsMapper.queryWorkerInfByHouseId(houseId);
            if(djDeliveryReturnSlipDetailsListDTO1==null)
                return ServerResponse.createByErrorMessage("房子信息不存在");
            djDeliveryReturnSlipDetailsListDTO.setWorkerName(djDeliveryReturnSlipDetailsListDTO1.getWorkerName());
            djDeliveryReturnSlipDetailsListDTO.setWorkerMobile(djDeliveryReturnSlipDetailsListDTO1.getWorkerMobile());
            DjDeliveryReturnSlipDetailsListDTO djDeliveryReturnSlipDetailsListDTO2 = djDeliveryReturnSlipDetailsMapper.queryMemberInfByHouseId(houseId);
            if(djDeliveryReturnSlipDetailsListDTO2==null)
                return ServerResponse.createByErrorMessage("房子信息不存在");
            djDeliveryReturnSlipDetailsListDTO.setName(djDeliveryReturnSlipDetailsListDTO2.getName());
            djDeliveryReturnSlipDetailsListDTO.setMobile(djDeliveryReturnSlipDetailsListDTO2.getMobile());
            djDeliveryReturnSlipDetailsDTOS.forEach(djDeliveryReturnSlipDetailsDTO -> {
                djDeliveryReturnSlipDetailsDTO.setImage(imageaddress+djDeliveryReturnSlipDetailsDTO.getImage());
                djDeliveryReturnSlipDetailsListDTO.setShipAddress(djDeliveryReturnSlipDetailsDTO.getShipAddress());
            });
            Double sumPrice = djDeliveryReturnSlipDetailsDTOS.stream().filter
                    (a -> a.getTotalPrice()!=null).mapToDouble
                    (DjDeliveryReturnSlipDetailsDTO::getTotalPrice).sum();
            djDeliveryReturnSlipDetailsListDTO.setNumber(number);
            djDeliveryReturnSlipDetailsListDTO.setSumPrice(sumPrice);
            djDeliveryReturnSlipDetailsListDTO.setDjDeliveryReturnSlipDetailsDTOS(djDeliveryReturnSlipDetailsDTOS);
            return ServerResponse.createBySuccess("查询成功", djDeliveryReturnSlipDetailsListDTO);
        } catch (Exception e) {
            logger.error("查询失败", e);
            return ServerResponse.createByErrorMessage("查询失败" + e);
        }
    }


}
