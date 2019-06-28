package com.dangjia.acg.service.config;

import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.mapper.config.IConfigAdvertMapper;
import com.dangjia.acg.modle.config.ConfigAdvert;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
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
    private CraftsmanConstructionService constructionService;

    /**
     * 获取所有广告
     *
     * @param configAdvert
     * @return
     */
    public ServerResponse getConfigAdverts(HttpServletRequest request, String userToken, ConfigAdvert configAdvert) {
        Example example = new Example(ConfigAdvert.class);
        Example.Criteria criteria = example.createCriteria();
        if (!CommonUtil.isEmpty(configAdvert.getAppType())) {//App端调用
            Object object = constructionService.getMember(userToken);
            if (object instanceof Member) {
                criteria.andNotEqualTo(ConfigAdvert.TYPE, 3);
            }
            criteria.andEqualTo(ConfigAdvert.APP_TYPE, configAdvert.getAppType());
            criteria.andCondition(" ( is_show = 0 or ( is_show = 2 and '" + DateUtil.format(new Date()) + "' BETWEEN show_time_start and show_time_end) )");
        }
        if (!CommonUtil.isEmpty(configAdvert.getCityId())) {
            criteria.andEqualTo(ConfigAdvert.CITY_ID, configAdvert.getCityId());
        }
        if (!CommonUtil.isEmpty(configAdvert.getAdvertType())) {
            criteria.andEqualTo(ConfigAdvert.ADVERT_TYPE, configAdvert.getAdvertType());
        }
        criteria.andEqualTo(ConfigAdvert.DATA_STATUS, 0);
        List<ConfigAdvert> list = configAdvertMapper.selectByExample(example);
        if (list.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode()
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
        ConfigAdvert configAdvert = configAdvertMapper.selectByPrimaryKey(id);
        if (configAdvert == null) {
            return ServerResponse.createByErrorMessage("广告不存在");
        }
        configAdvert.setDataStatus(1);
        if (configAdvertMapper.updateByPrimaryKeySelective(configAdvert) > 0) {
            return ServerResponse.createBySuccessMessage("删除成功");
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
        return setConfigAdvert(request, 1, configAdvert);
    }

    /**
     * 新增
     *
     * @param configAdvert
     * @return
     */
    public ServerResponse addConfigAdvert(HttpServletRequest request, ConfigAdvert configAdvert) {
        return setConfigAdvert(request, 0, configAdvert);
    }

    private ServerResponse setConfigAdvert(HttpServletRequest request, int type, ConfigAdvert configAdvert) {
        //查看该权限是否有子节点，如果有，先删除子节点
        if (configAdvert.getIsShow() == 3 && (
                CommonUtil.isEmpty(configAdvert.getShowTimeStart()) ||
                        CommonUtil.isEmpty(configAdvert.getShowTimeEnd()))) {
            return ServerResponse.createByErrorMessage("请选择开始和结束时间");
        }
        if ((configAdvert.getType() == 0 || configAdvert.getType() == 1) && CommonUtil.isEmpty(configAdvert.getData())) {
            return ServerResponse.createByErrorMessage(configAdvert.getType() == 0 ? "请输入跳转地址" : "请选择房子");
        }
        if (CommonUtil.isEmpty(configAdvert.getImage())) {
            return ServerResponse.createByErrorMessage("请上传图片");
        }
        if (CommonUtil.isEmpty(configAdvert.getCityId())) {
            return ServerResponse.createByErrorMessage("请选择城市");
        }
        if (type == 0) {//新增
            if (this.configAdvertMapper.insertSelective(configAdvert) <= 0) {
                return ServerResponse.createByErrorMessage("新增失败，请您稍后再试");
            }
        } else {//修改
            if (this.configAdvertMapper.updateByPrimaryKeySelective(configAdvert) <= 0) {
                return ServerResponse.createByErrorMessage("修改失败，请您稍后再试");
            }
        }
        return ServerResponse.createBySuccessMessage("操作成功");
    }

}
