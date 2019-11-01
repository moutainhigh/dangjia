package com.dangjia.acg.service.storefront;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.mapper.storefront.IStorefrontRuleConfigMapper;
import com.dangjia.acg.modle.storefront.StorefrontRuleConfig;
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
     * 根据店铺id和关键值查询运费配置
     *
     * @param userId
     * @param storefrontKey
     * @return
     */
    public ServerResponse queryStorefrontRuleConfigByIdAndKey(String userId, String storefrontKey) {
        try {
            Example example = new Example(StorefrontRuleConfig.class);
            example.createCriteria().andEqualTo(StorefrontRuleConfig.USER_ID, userId)
                    .andEqualTo(StorefrontRuleConfig.STOREFRONT_KEY, storefrontKey);
            List<StorefrontRuleConfig> list = istorefrontRuleConfigMapper.selectByExample(example);
            return ServerResponse.createBySuccess("查询成功", list);
        } catch (Exception e) {
            logger.error("根据店铺id和关键值查询运费配置异常：", e);
            return ServerResponse.createByErrorMessage("根据店铺id和关键值查询运费配置异常");
        }
    }

}
