package com.dangjia.acg.service.storefront;

import com.dangjia.acg.mapper.storefront.IStoreConfigMapper;
import com.dangjia.acg.model.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

@Service
public class StoreConfigService {
    @Autowired
    private IStoreConfigMapper iStoreConfigMapper;

    /**
     * 查询对应的配置信息
     * @param paramKey
     * @return
     */
    public Config selectConfigInfoByParamKey(String paramKey){
        Example example=new Example(Config.class);
        example.createCriteria().andEqualTo(Config.PARAM_KEY,paramKey);
        return iStoreConfigMapper.selectOneByExample(example);

    }

}
