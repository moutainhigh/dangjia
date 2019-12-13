package com.dangjia.acg.service.config;

import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.mapper.config.IDataCacheMapper;
import com.dangjia.acg.modle.config.DataCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author Ruking.Cheng
 * @descrilbe 前端数据临时缓存
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/12/12 9:20 PM
 */
@Service
public class DataCacheService {
    @Autowired
    private IDataCacheMapper iDataCacheMapper;

    public ServerResponse addDataCache(String publicKey, Integer type, String dataJson) {
        if (CommonUtil.isEmpty(publicKey)) {
            return ServerResponse.createByErrorMessage("键值不能为空");
        }
        if (CommonUtil.isEmpty(type)) {
            return ServerResponse.createByErrorMessage("缓存类型不能为空");
        }
        if (CommonUtil.isEmpty(dataJson)) {
            return ServerResponse.createByErrorMessage("缓存数据不能为空");
        }
        Example example = new Example(DataCache.class);
        example.createCriteria()
                .andEqualTo(DataCache.PUBLIC_KEY, publicKey)
                .andEqualTo(DataCache.TYPE, type);
        iDataCacheMapper.deleteByExample(example);
        DataCache dataCache = new DataCache();
        dataCache.setPublicKey(publicKey);
        dataCache.setType(type);
        dataCache.setDataJson(dataJson);
        iDataCacheMapper.insertSelective(dataCache);
        return ServerResponse.createBySuccessMessage("保存成功");
    }

    public ServerResponse getDataCache(String publicKey, Integer type) {
        Example example = new Example(DataCache.class);
        example.createCriteria()
                .andEqualTo(DataCache.PUBLIC_KEY, publicKey)
                .andEqualTo(DataCache.TYPE, type);
        List<DataCache> dataCaches = iDataCacheMapper.selectByExample(example);
        if (dataCaches.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        return ServerResponse.createBySuccess("查询成功", dataCaches.get(0));
    }
}
