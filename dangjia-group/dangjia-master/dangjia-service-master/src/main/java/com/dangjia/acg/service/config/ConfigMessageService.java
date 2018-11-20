package com.dangjia.acg.service.config;

import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.enums.EventStatus;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.mapper.config.IConfigMessageMapper;
import com.dangjia.acg.modle.config.ConfigMessage;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.member.AccessToken;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2018/10/31 0031
 * Time: 20:18
 */
@Service
public class ConfigMessageService {


    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IConfigMessageMapper configMessageMapper;
    /****
     * 注入配置
     */
    @Autowired
    private RedisClient redisClient;
    /**
     * 获取所有公共消息
     * @param configMessage
     * @return
     */
    public ServerResponse getConfigMessages(HttpServletRequest request, PageDTO pageDTO, ConfigMessage configMessage) {
        String userToken = request.getParameter(Constants.USER_TOKEY);
        Example example = new Example(ConfigMessage.class);
        Example.Criteria criteria=example.createCriteria();
        criteria.andEqualTo("appType", configMessage.getAppType());
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        if(CommonUtil.isEmpty(userToken)) {
            criteria.andEqualTo("targetType","1");
            List<ConfigMessage> list = configMessageMapper.selectByExample(example);
            for (ConfigMessage msg:list) {
                msg.initPath(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class));
            }
            PageInfo pageResult = new PageInfo(list);
            return ServerResponse.createBySuccess("ok",pageResult);
        }else{
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            if(accessToken == null){//无效的token
                return ServerResponse.createByErrorCodeMessage(EventStatus.USER_TOKEN_ERROR.getCode(),"无效的token,请重新登录或注册！");
            }
            criteria.andEqualTo("fromUid", accessToken.getMember().getUserName()).orEqualTo("targetType","1");
            List<ConfigMessage> list = configMessageMapper.selectByExample(example);
            for (ConfigMessage msg:list) {
                msg.initPath(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class));
            }
            PageInfo pageResult = new PageInfo(list);
            return ServerResponse.createBySuccess("ok",pageResult);
        }

    }

    /**
     * 新增
     * @param configMessage
     * @return
     */
    public ServerResponse addConfigMessage(HttpServletRequest request,ConfigMessage configMessage) {
        if(this.configMessageMapper.insertSelective(configMessage)>0){
            return ServerResponse.createBySuccessMessage("ok");
        }else{
            return ServerResponse.createByErrorMessage("新增失败，请您稍后再试");
        }
    }
}
