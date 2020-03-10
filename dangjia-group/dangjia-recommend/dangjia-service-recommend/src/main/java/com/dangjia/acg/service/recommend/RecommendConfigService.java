package com.dangjia.acg.service.recommend;

import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.mapper.recommend.IRecommendConfigMapper;
import com.dangjia.acg.modle.recommend.RecommendConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @Description: 推荐配置服务类
 * @author: luof
 * @date: 2020-3-9
 */
@Service
public class RecommendConfigService {

    /** 声明日志 */
    private static Logger logger = LoggerFactory.getLogger(RecommendConfigService.class);

    @Autowired
    private IRecommendConfigMapper recommendConfigMapper;

    /**
     * @Description: 查询推荐目标列表
     * @author: luof
     * @date: 2020-3-9
     */
    public ServerResponse queryList(){

        List<RecommendConfig> recommendConfigList = recommendConfigMapper.queryList();

        if ( recommendConfigList == null || recommendConfigList.size() == 0 ) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), "查无数据");
        }
        return ServerResponse.createBySuccess("查询成功", recommendConfigList);
    }

    /**
     * @Description: 设置单个推荐配置参数
     * @author: luof
     * @date: 2020-3-9
     */
    public ServerResponse setSingle(String id, String configCode, Integer configValue){

        if( null == id || id.equals("") ){
            return ServerResponse.createByErrorMessage("参数[id]为空!");
        }
        if( null == configCode || configCode.equals("") ){
            return ServerResponse.createByErrorMessage("参数[configCode]为空!");
        }
        if( null == configValue ){
            return ServerResponse.createByErrorMessage("参数[configValue]为空!");
        }

        Date now = new Date();
        int rows = recommendConfigMapper.updateSingle(id, now, configCode, configValue);

        if( rows != 1 ){
            return ServerResponse.createByErrorMessage("设置失败");
        }
        return ServerResponse.createBySuccessMessage("设置成功");
    }
}
