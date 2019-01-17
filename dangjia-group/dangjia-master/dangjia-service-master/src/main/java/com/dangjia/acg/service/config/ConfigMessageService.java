package com.dangjia.acg.service.config;

import com.dangjia.acg.api.MessageAPI;
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
    @Autowired
    private MessageAPI messageAPI;
    /****
     * 注入配置
     */
    @Autowired
    private RedisClient redisClient;
    /**
     * 获取所有公共消息(web端列表)
     * @param configMessage
     * @return
     */
    public ServerResponse queryConfigMessages(HttpServletRequest request, PageDTO pageDTO, ConfigMessage configMessage) {
        Example example = new Example(ConfigMessage.class);
        example.orderBy("createDate").desc();
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<ConfigMessage> list = configMessageMapper.selectByExample(example);
        PageInfo pageResult = new PageInfo(list);
        return ServerResponse.createBySuccess("ok",pageResult);
    }
    /**
     * 获取所有公共消息
     * @param configMessage
     * @return
     */
    public ServerResponse getConfigMessages(HttpServletRequest request, PageDTO pageDTO, ConfigMessage configMessage) {
        String userToken = request.getParameter(Constants.USER_TOKEY);
        String cityId = request.getParameter(Constants.CITY_ID);
        Example example = new Example(ConfigMessage.class);
        Example.Criteria criteria=example.createCriteria();
        criteria.andEqualTo("appType", configMessage.getAppType());
        if(!CommonUtil.isEmpty(userToken)) {
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            if(accessToken == null){//无效的token
                return ServerResponse.createByErrorCodeMessage(EventStatus.USER_TOKEN_ERROR.getCode(),"无效的token,请重新登录或注册！");
            }
            criteria.andCondition("(target_uid='"+accessToken.getMemberId()+"' or target_type=1  or target_uid='workerTypeId"+accessToken.getMember().getWorkerTypeId()+","+cityId+"')");
//            criteria.andEqualTo("targetUid", accessToken.getMember().getId()).orEqualTo("targetType","1");
        }else {
            criteria.andEqualTo("targetType", "1");
        }
        example.orderBy("createDate").desc();
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<ConfigMessage> list = configMessageMapper.selectByExample(example);
        for (ConfigMessage msg:list) {
            msg.initPath(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class));
        }
        PageInfo pageResult = new PageInfo(list);
        return ServerResponse.createBySuccess("ok",pageResult);
    }

    /**
     * 推送至个人消息
     * @param request
     * @param appType
     * @param memberId 接收人
     * @param targetType 消息类型 0=个人推送  1=全推
     * @param title 推送标题
     * @param alert 推送内容
     * @param speak 推送语音文字（选填）
     * @return
     */
    public ServerResponse addConfigMessage(HttpServletRequest request,String appType,String memberId,String targetType,String title,String alert,String speak){
        ConfigMessage configMessage=new ConfigMessage();
        appType=(!CommonUtil.isEmpty(appType)&&appType.equals("zx"))?"1":"2";
        configMessage.setAppType(appType);
        configMessage.setTargetUid(memberId);
        configMessage.setTargetType(targetType);
        configMessage.setName(title);
        configMessage.setSpeak(speak);
        configMessage.setText(alert);
        configMessage.setType(2);
        return addConfigMessage(request,configMessage);
    }
    /**
     * 新增
     * @param configMessage
     * @return
     */
    public ServerResponse addConfigMessage(HttpServletRequest request,ConfigMessage configMessage) {
        try {

            if(CommonUtil.isEmpty(configMessage.getIcon())){
                //设置默认图标
                configMessage.setIcon("qrcode/push.png");
            }
            if(this.configMessageMapper.insertSelective(configMessage)>0){
                //发送推送消息
                String appType=(!CommonUtil.isEmpty(configMessage.getAppType())&&configMessage.getAppType().equals("1"))?"zx":"gj";
                if(!CommonUtil.isEmpty(configMessage.getTargetUid())&&configMessage.getTargetType().equals("0")){
                    messageAPI.sendMemberIdPush(appType,new String[]{configMessage.getTargetUid()},configMessage.getName(),configMessage.getText(),configMessage.getSpeak());
                }
                if(configMessage.getTargetType().equals("1")){
                    messageAPI.sendSysPush(appType,configMessage.getName(),configMessage.getText(),configMessage.getSpeak());
                }
                return ServerResponse.createBySuccessMessage("ok");
            }else{
                return ServerResponse.createByErrorMessage("推送失败，请您稍后再试");
            }
        }catch (Exception e){
            return ServerResponse.createByErrorMessage("推送失败；原因："+e.getMessage());
        }
    }
}
