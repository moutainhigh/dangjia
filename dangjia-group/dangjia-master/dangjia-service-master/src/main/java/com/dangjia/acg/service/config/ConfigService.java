package com.dangjia.acg.service.config;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.mapper.config.IConfigMapper;
import com.dangjia.acg.model.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/6/13
 * Time: 15:49
 */
@Service
public class ConfigService {
    @Autowired
    private IConfigMapper iConfigMapper;

    @Transactional(rollbackFor = Exception.class)
    public ServerResponse editDistance(double distance,double radius) throws RuntimeException{
        try {
            Example example=new Example(Config.class);
            example.createCriteria().andCondition("param_key ='CONSTRUCTION_SITE_DISTANCE'");
            List<Config> configs = iConfigMapper.selectByExample(example);
            Config config = new Config();
            if(configs.size()>0) {
                example = new Example(Config.class);
                example.createCriteria().andCondition("param_key ='CONSTRUCTION_SITE_DISTANCE'");
                config.setParamValue(distance + "");
                iConfigMapper.updateByExampleSelective(config, example);
            }else{
                example = new Example(Config.class);
                config.setParamKey("CONSTRUCTION_SITE_DISTANCE");
                config.setParamValue(distance+"");
                config.setParamDesc("施工现场距离");
                iConfigMapper.insert(config);
            }
            example=new Example(Config.class);
            example.createCriteria().andCondition("param_key ='EXPAND_THE_RADIUS'");
            List<Config> configs1 = iConfigMapper.selectByExample(example);
            if(configs1.size()>0) {
                example.createCriteria().andCondition("param_key ='EXPAND_THE_RADIUS'");
                config = new Config();
                config.setParamValue(radius + "");
                iConfigMapper.updateByExampleSelective(config, example);
            }else{
                example = new Example(Config.class);
                config=new Config();
                config.setParamKey("EXPAND_THE_RADIUS");
                config.setParamValue(radius+"");
                config.setParamDesc("不满足条件的扩大半径");
                iConfigMapper.insert(config);
            }
            return ServerResponse.createBySuccessMessage("配置成功");
        } catch (Exception e) {
            return ServerResponse.createByErrorMessage("配置失败");
        }
    }


    public ServerResponse selectDistance() {
        try {
            Example example=new Example(Config.class);
            example.createCriteria().andCondition(" param_key IN('CONSTRUCTION_SITE_DISTANCE','EXPAND_THE_RADIUS')");
            List<Config> configs = iConfigMapper.selectByExample(example);
            return ServerResponse.createBySuccess("查询成功",configs);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }
}
