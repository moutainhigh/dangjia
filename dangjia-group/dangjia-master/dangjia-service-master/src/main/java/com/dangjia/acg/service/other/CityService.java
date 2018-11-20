package com.dangjia.acg.service.other;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.mapper.other.ICityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * author: Ronalcheng
 * Date: 2018/11/1 0001
 * Time: 16:16
 */
@Service
public class CityService {

    @Autowired
    private ICityMapper iCityMapper;

    public ServerResponse getAllCity(){
        return ServerResponse.createBySuccess("查询城市列表成功", iCityMapper.getAllCity());
    }

}
