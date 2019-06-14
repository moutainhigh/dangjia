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
            example.createCriteria().andEqualTo(Config.ID,8);
            Config config=new Config();
            config.setParamValue(distance+"");
            iConfigMapper.updateByExampleSelective(config,example);
            example=new Example(Config.class);
            example.createCriteria().andEqualTo(Config.ID,9);
            config=new Config();
            config.setParamValue(radius+"");
            iConfigMapper.updateByExampleSelective(config,example);
            return ServerResponse.createBySuccessMessage("配置成功");
        } catch (Exception e) {
            return ServerResponse.createByErrorMessage("配置失败");
        }
    }


    public ServerResponse selectDistance() {
        try {
            Example example=new Example(Config.class);
            example.createCriteria().andCondition(" ID IN(8,9)");
            List<Config> configs = iConfigMapper.selectByExample(example);
            return ServerResponse.createBySuccess("查询成功",configs);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }
}
