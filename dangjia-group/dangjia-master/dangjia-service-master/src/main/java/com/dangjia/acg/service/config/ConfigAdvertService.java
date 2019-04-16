package com.dangjia.acg.service.config;

import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.enums.EventStatus;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.mapper.config.IConfigAdvertMapper;
import com.dangjia.acg.modle.config.ConfigAdvert;
import com.dangjia.acg.modle.member.AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * author: Ronalcheng
 * Date: 2018/10/31 0031
 * Time: 20:18
 */
@Service
public class ConfigAdvertService {

    @Autowired
    private IConfigAdvertMapper configAdvertMapper;

    @Autowired
    private ConfigUtil configUtil;

    @Autowired
    private RedisClient redisClient;

    /**
     * 获取所有广告
     *
     * @param configAdvert
     * @return
     */
    public ServerResponse getConfigAdverts(HttpServletRequest request, ConfigAdvert configAdvert) {
//        Example example = new Example(ConfigAdvert.class);
//        Example.Criteria criteria = example.createCriteria();
//        AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
//        if (accessToken != null) {//有效的token
//            criteria.andNotEqualTo(ConfigAdvert.TYPE, 3);
//        }
//        if (!CommonUtil.isEmpty(configAdvert.getAppType())) {
//            criteria.andEqualTo(ConfigAdvert.APP_TYPE, configAdvert.getAppType());
//            criteria.andEqualTo(ConfigAdvert.SHOW, true);
//        }
//        if (!CommonUtil.isEmpty(configAdvert.getCityId())) {
//            criteria.andEqualTo(ConfigAdvert.CITY_ID, configAdvert.getCityId());
//        }
//        if (!CommonUtil.isEmpty(configAdvert.getAdvertType())) {
//            criteria.andEqualTo(ConfigAdvert.ADVERT_TYPE, configAdvert.getAdvertType());
//        }
        List<ConfigAdvert> list = configAdvertMapper.selectConfigAdvertByDataStatus();
        if (list.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(EventStatus.NO_DATA.getCode()
                    , "查无数据");
        }
        List<Map> listMap = new ArrayList<>();
        for (ConfigAdvert v : list) {
            Map map = BeanUtils.beanToMap(v);
            map.put(ConfigAdvert.IMAGE + "Url", v.getImage());
            v.initPath(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class));
            map.put(ConfigAdvert.IMAGE, v.getImage());
            listMap.add(map);
        }
        return ServerResponse.createBySuccess("查询成功", listMap);
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    public ServerResponse delConfigAdvert(HttpServletRequest request, String id) {
        if (this.configAdvertMapper.deleteByPrimaryKey(String.valueOf(id)) > 0) {
            return ServerResponse.createBySuccessMessage("ok");
        } else {
            return ServerResponse.createByErrorMessage("删除失败，请您稍后再试");
        }
    }

    /**
     * 修改
     *
     * @param configAdvert
     * @return
     */
    public ServerResponse editConfigAdvert(HttpServletRequest request, ConfigAdvert configAdvert) {
        //查看该权限是否有子节点，如果有，先删除子节点
        if (this.configAdvertMapper.updateByPrimaryKeySelective(configAdvert) > 0) {
            return ServerResponse.createBySuccessMessage("ok");
        } else {
            return ServerResponse.createByErrorMessage("修改失败，请您稍后再试");
        }
    }

    /**
     * 新增
     *
     * @param configAdvert
     * @return
     */
    public ServerResponse addConfigAdvert(HttpServletRequest request, ConfigAdvert configAdvert) {
        //查看该权限是否有子节点，如果有，先删除子节点
        if (this.configAdvertMapper.insertSelective(configAdvert) > 0) {
            return ServerResponse.createBySuccessMessage("ok");
        } else {
            return ServerResponse.createByErrorMessage("新增失败，请您稍后再试");
        }
    }
}
