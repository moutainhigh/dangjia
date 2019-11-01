package com.dangjia.acg.service.storefront;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.mapper.storefront.IStorefrontRuleConfigMapper;
import com.dangjia.acg.modle.storefront.StorefrontRuleConfig;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class StorefrontRuleConfigService {


    @Autowired
    private IStorefrontRuleConfigMapper istorefrontRuleConfigMapper;
    /**
     * 声明日志
     */
    private static Logger logger = LoggerFactory.getLogger(StorefrontRuleConfigService.class);

    /**
     * 通过店铺id和总价，查询返回运费
     *
     * @param storefrontId
     * @param belowUnitPrice
     * @return
     */
    public ServerResponse queryStorefrontRuleConfigByIdAndprice(String storefrontId, String belowUnitPrice) {
        try {
            if (StringUtils.isEmpty(storefrontId)) {
                return ServerResponse.createBySuccess("查询成功", 0);
            }
            Example example = new Example(StorefrontRuleConfig.class);
            example.createCriteria().andEqualTo(StorefrontRuleConfig.STOREFRONT_ID, storefrontId);
            List<StorefrontRuleConfig> list = istorefrontRuleConfigMapper.selectByExample(example);
            if(list.size()<=0)
            {
                return ServerResponse.createBySuccess("查询成功", 0);
            }
            StorefrontRuleConfig storefrontRuleConfig=list.get(0);
            Double freight=Double.parseDouble(storefrontRuleConfig.getFreight()!=null?storefrontRuleConfig.getFreight():"0");

            if(Double.parseDouble(belowUnitPrice)<freight)
                return ServerResponse.createBySuccess("查询成功", freight);
            return ServerResponse.createBySuccess("查询成功", 0);
        } catch (Exception e) {
            logger.error("根据店铺id和关键值查询运费配置异常：", e);
            return ServerResponse.createByErrorMessage("根据店铺id和关键值查询运费配置异常");
        }
    }

}
