package com.dangjia.acg.service.config;

import com.dangjia.acg.api.MessageAPI;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.enums.AppType;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.mapper.config.IConfigMessageMapper;
import com.dangjia.acg.modle.config.ConfigMessage;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
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
    @Autowired
    private CraftsmanConstructionService constructionService;

    /**
     * 获取所有公共消息(web端列表)
     *
     * @param configMessage
     * @return
     */
    public ServerResponse queryConfigMessages(HttpServletRequest request, PageDTO pageDTO) {
        Example example = new Example(ConfigMessage.class);
        example.createCriteria().andNotEqualTo(ConfigMessage.NAME, "");
        example.orderBy("createDate").desc();
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<ConfigMessage> list = configMessageMapper.selectByExample(example);
        if (list.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        PageInfo pageResult = new PageInfo(list);
        return ServerResponse.createBySuccess("ok", pageResult);
    }

    /**
     * 获取所有公共消息
     *
     * @param configMessage
     * @return
     */
    public ServerResponse getConfigMessages(HttpServletRequest request, PageDTO pageDTO, ConfigMessage configMessage) {
        String userToken = request.getParameter(Constants.USER_TOKEY);
        String cityId = request.getParameter(Constants.CITY_ID);
        Example example = new Example(ConfigMessage.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("appType", configMessage.getAppType());
        if (!CommonUtil.isEmpty(userToken)) {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = (Member) object;
            criteria.andCondition("(" +
                    "target_uid='" + member.getId() + "' or " +
                    "target_uid='" + cityId + "' or " +
                    "target_uid='wtId" + member.getWorkerTypeId() + "' or " +
                    "target_type=1  or " +
                    "target_uid='wtId" + member.getWorkerTypeId() + cityId + "'" +
                    ")");
//            criteria.andEqualTo("targetUid", accessToken.getMember().getId()).orEqualTo("targetType","1");
        } else {
            criteria.andEqualTo("targetType", "1");
        }
        example.orderBy("createDate").desc();
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<ConfigMessage> list = configMessageMapper.selectByExample(example);
        if (list.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        for (ConfigMessage msg : list) {
            msg.initPath(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class));
        }
        PageInfo pageResult = new PageInfo(list);
        return ServerResponse.createBySuccess("ok", pageResult);
    }

    /**
     * 推送至个人消息（即将废弃）
     */
    public ServerResponse addConfigMessage(HttpServletRequest request, AppType appType, String memberId,
                                           String targetType, String title, String alert, String typeText) {
        String data = null;
        int type = 0;
        typeText = (!CommonUtil.isEmpty(typeText)) ? typeText : "2";
        if (!CommonUtil.isEmpty(typeText) && "6".equals(typeText)) {
            data = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class) + String.format(DjConstants.GJPageAddress.JUGLELIST,
                    "", "", "评价记录");
        } else if (!CommonUtil.isEmpty(typeText) && "7".equals(typeText)) {
            data = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class) + String.format(DjConstants.GJPageAddress.JIANGFALIST,
                    "", "", "奖罚记录");
        } else if (!StringUtils.isNumeric(typeText)) {
            data = typeText;
        } else {
            type = Integer.parseInt(typeText);
        }
        return addConfigMessage(appType, memberId, targetType, title, alert, type, data);
    }

    /**
     * 推送至个人消息(无语音，type！=0）
     *
     * @param appType  应用端类别
     * @param memberId 接收人
     * @param title    推送标题
     * @param alert    推送内容
     * @param type     动作类型（0:直接跳转URL，1:跳转支付，2:只显示，3:登录，4:工匠端抢单界面/销售抢单页，5:工匠端施工界面/销售首页，6：销售业绩页）
     * @return
     */
    public ServerResponse addConfigMessage(AppType appType, String memberId, String title, String alert, int type) {
        return addConfigMessage(appType, memberId, title, alert, type, null);
    }

    /**
     * 推送至个人消息(无语音）
     *
     * @param appType  应用端类别
     * @param memberId 接收人
     * @param title    推送标题
     * @param alert    推送内容
     * @param type     动作类型（0:直接跳转URL，1:跳转支付，2:只显示，3:登录，4:工匠端抢单界面/销售抢单页，5:工匠端施工界面/销售首页，6：销售业绩页）
     * @param data     跳转地址
     * @return
     */
    public ServerResponse addConfigMessage(AppType appType, String memberId, String title, String alert, int type, String data) {
        return addConfigMessage(appType, memberId, title, alert, type, data, null);
    }

    /**
     * 推送至个人消息
     *
     * @param appType  应用端类别
     * @param memberId 接收人
     * @param title    推送标题
     * @param alert    推送内容
     * @param type     动作类型（0:直接跳转URL，1:跳转支付，2:只显示，3:登录，4:工匠端抢单界面/销售抢单页，5:工匠端施工界面/销售首页，6：销售业绩页）
     * @param data     跳转地址
     * @param speak    语音提示内容
     * @return
     */
    public ServerResponse addConfigMessage(AppType appType, String memberId, String title, String alert, int type, String data, String speak) {
        return addConfigMessage(appType, memberId, "0", title, alert, type, data, speak);
    }

    /**
     * 推送消息(无语音）
     *
     * @param appType    应用端类别
     * @param memberId   接收人
     * @param targetType 消息类型 0=个人推送  1=全推
     * @param title      推送标题
     * @param alert      推送内容
     * @param type       动作类型（0:直接跳转URL，1:跳转支付，2:只显示，3:登录，4:工匠端抢单界面/销售抢单页，5:工匠端施工界面/销售首页，6：销售业绩页）
     * @param data       跳转地址
     * @return
     */
    public ServerResponse addConfigMessage(AppType appType, String memberId, String targetType,
                                           String title, String alert, int type, String data) {
        return addConfigMessage(appType, memberId, targetType, title, alert, type, data, null);
    }

    /**
     * 推送消息
     *
     * @param appType    应用端类别
     * @param memberId   接收人
     * @param targetType 消息类型 0=个人推送  1=全推
     * @param title      推送标题
     * @param alert      推送内容
     * @param type       动作类型（0:直接跳转URL，1:跳转支付，2:只显示，3:登录，4:工匠端抢单界面/销售抢单页，5:工匠端施工界面/销售首页，6：销售业绩页）
     * @param data       跳转地址
     * @param speak      语音提示内容
     * @return
     */
    public ServerResponse addConfigMessage(AppType appType, String memberId, String targetType,
                                           String title, String alert, int type, String data, String speak) {
        ConfigMessage configMessage = new ConfigMessage();
        if (appType == null) {
            appType = AppType.ZHUANGXIU;
        }
        configMessage.setAppType(appType.getCode() + "");
        configMessage.setTargetUid(memberId);
        configMessage.setTargetType(targetType);
        configMessage.setName(title);
        configMessage.setText(alert);
        configMessage.setType(type);
        configMessage.setData(data);
        configMessage.setSpeak(speak);
        return addConfigMessage(configMessage);
    }


    public ServerResponse addConfigMessage(ConfigMessage configMessage) {
        try {
            if (this.configMessageMapper.insertSelective(configMessage) > 0) {
                new Thread(() -> {
                    if (CommonUtil.isEmpty(configMessage.getIcon())) {
                        //设置默认图标
                        configMessage.setIcon("qrcode/push.png");
                    }
                    AppType appType = AppType.ZHUANGXIU;
                    if (!CommonUtil.isEmpty(configMessage.getAppType())) {
                        switch (configMessage.getAppType()) {
                            case "1":
                                appType = AppType.ZHUANGXIU;
                                break;
                            case "2":
                                appType = AppType.GONGJIANG;
                                break;
                            case "3":
                                appType = AppType.SALE;
                                break;
                        }
                    }
                    if (!CommonUtil.isEmpty(configMessage.getTargetUid()) && configMessage.getTargetType().equals("0")) {
                        messageAPI.sendMemberIdPush(appType.getDesc(), new String[]{configMessage.getTargetUid()}, configMessage.getName(), configMessage.getText(), configMessage.getSpeak());
                    }
                    if (configMessage.getTargetType().equals("1")) {
                        messageAPI.sendSysPush(appType.getDesc(), configMessage.getName(), configMessage.getText(), configMessage.getSpeak());
                    }
                }).start();
            }
            return ServerResponse.createBySuccessMessage("ok");
        } catch (Exception e) {
            return ServerResponse.createByErrorMessage("推送失败；原因：" + e.getMessage());
        }
    }
}
