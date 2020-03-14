package com.dangjia.acg.service.storefront;

import com.dangjia.acg.mapper.storefront.IStorefrontConfigMapper;
import com.dangjia.acg.modle.storefront.StorefrontConfig;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StorefrontConfigService {


    @Autowired
    private IStorefrontConfigMapper istorefrontConfigMapper;
    /**
     * 声明日志
     */
    private static Logger logger = LoggerFactory.getLogger(StorefrontConfigService.class);

    /**
     * 通过店铺id和总价  获取运费金额
     *
     * @param storefrontId 店铺ID
     * @param totalPrice 订单内实物商品总价
     * @return
     */
    public Double getFreightPrice(String storefrontId, Double totalPrice) {
        try {
            if (StringUtils.isEmpty(storefrontId)) {
                return 0d;
            }
            if (totalPrice<=0) {
                return 0d;
            }
            StorefrontConfig  freight = istorefrontConfigMapper.getConfig(storefrontId,StorefrontConfig.FREIGHT);
            StorefrontConfig  freightTerms = istorefrontConfigMapper.getConfig(storefrontId,StorefrontConfig.FREIGHT_TERMS);
            if(freight==null||freightTerms==null){
                return 0d;
            }
            Double freightPrice=Double.parseDouble(freight.getParamValue());
            Double freightTermsPrice=Double.parseDouble(freightTerms.getParamValue());
            if(totalPrice>freightTermsPrice) {
                freightPrice=0d;
            }
            return freightPrice;
        } catch (Exception e) {
            logger.error("根据店铺id和关键值查询运费配置异常：", e);
            return 0d;
        }
    }

}
