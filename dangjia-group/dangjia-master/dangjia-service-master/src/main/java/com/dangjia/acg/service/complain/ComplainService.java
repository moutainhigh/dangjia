package com.dangjia.acg.service.complain;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.enums.EventStatus;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.JsmsUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.complain.ComplainDTO;
import com.dangjia.acg.dto.deliver.SplitDeliverItemDTO;
import com.dangjia.acg.dto.worker.RewardPunishRecordDTO;
import com.dangjia.acg.mapper.complain.IComplainMapper;
import com.dangjia.acg.mapper.core.IHouseFlowMapper;
import com.dangjia.acg.mapper.core.IHouseWorkerMapper;
import com.dangjia.acg.mapper.deliver.IOrderSplitItemMapper;
import com.dangjia.acg.mapper.deliver.ISplitDeliverMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.worker.IRewardPunishRecordMapper;
import com.dangjia.acg.modle.complain.Complain;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.deliver.OrderSplit;
import com.dangjia.acg.modle.deliver.OrderSplitItem;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.worker.RewardPunishCorrelation;
import com.dangjia.acg.modle.worker.RewardPunishRecord;
import com.dangjia.acg.service.deliver.SplitDeliverService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.JsonObject;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Service
public class ComplainService {
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private IMemberMapper memberMapper;
    @Autowired
    private IComplainMapper complainMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private SplitDeliverService splitDeliverService;
    @Autowired
    private IRewardPunishRecordMapper rewardPunishRecordMapper;
    @Autowired
    private IHouseWorkerMapper houseWorkerMapper;
    @Autowired
    private IHouseFlowMapper houseFlowMapper;
    @Autowired
    private IOrderSplitItemMapper orderSplitItemMapper;


    /**
     * 添加申诉
     *
     * @param userToken    用户Token
     * @param complainType 申诉类型 1:工匠被处罚后不服.2：业主要求整改.3：大管家（开工后）要求换人.4:部分收货申诉.
     * @param businessId   对应业务ID
     *                     complain_type==1:对应处罚的rewardPunishRecordId,
     *                     complain_type==2:对应工匠memberId,
     *                     complain_type==3:对应工匠memberId,
     *                     complain_type==4:发货单splitDeliverId,
     * @param houseId      对应房子ID
     * @return
     */

    public ServerResponse addComplain(String userToken, Integer complainType, String businessId, String houseId, String files) {
        if (CommonUtil.isEmpty(complainType) || CommonUtil.isEmpty(businessId) || CommonUtil.isEmpty(houseId)) {
            return ServerResponse.createByErrorMessage("参数不正确");
        }
        AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
        if (accessToken == null) {//无效的token
            return ServerResponse.createByErrorCodeMessage(EventStatus.USER_TOKEN_ERROR.getCode(), "无效的token,请重新登录或注册！");
        }
        Member user = memberMapper.selectByPrimaryKey(accessToken.getMember().getId());
        if (user == null) {
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        Complain complain = new Complain();
        complain.setMemberId(user.getId());
        complain.setComplainType(complainType);
        complain.setBusinessId(businessId);
        complain.setHouseId(houseId);
        complain.setFiles(files);
        complainMapper.insertSelective(complain);
        return ServerResponse.createBySuccessMessage("提交成功");
    }

    /**
     * 查询申诉
     *
     * @param pageDTO      分页实体
     * @param complainType 申诉类型 1:工匠被处罚后不服.2：业主要求整改.3：大管家（开工后）要求换人.4:部分收货申诉.
     * @param state        处理状态:0:待处理。1.驳回。2.接受。
     * @param searchKey    用户关键字查询，包含名称、手机号、昵称
     * @return
     */
    public ServerResponse getComplainList(PageDTO pageDTO, Integer complainType, Integer state, String searchKey) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            if (state != null && state == -1) state = null;
            List<ComplainDTO> complainDTOList = complainMapper.getComplainList(complainType, state, searchKey);
            if (complainDTOList.size() == 0) {
                return ServerResponse.createByErrorCodeMessage(EventStatus.NO_DATA.getCode(), "查无数据");
            }
            PageInfo pageResult = new PageInfo(complainDTOList);
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            for (ComplainDTO complainDTO : complainDTOList) {
                String files = complainDTO.getFiles();
                if (CommonUtil.isEmpty(files)) {
                    complainDTO.setFileList(null);
                    continue;
                }
                List<String> filesList = new ArrayList<>();
                String[] fs = files.split(",");
                for (String f : fs) {
                    filesList.add(address + f);
                }
                if (filesList.size() > 0) {
                    complainDTO.setFileList(filesList);
                }
            }
            pageResult.setList(complainDTOList);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 修改申诉
     *
     * @param userId      处理人ID
     * @param complainId  申诉ID
     * @param state       处理状态.0:待处理。1.驳回。2.接受。
     * @param description 处理描述
     * @param files       附件 以，分割 如：data/f.dow,data/f2.dow
     * @return
     */
    public ServerResponse updataComplain(String userId, String complainId, Integer state, String description, String files) {
        if (CommonUtil.isEmpty(complainId)) {
            return ServerResponse.createByErrorMessage("参数不正确");
        }
        Complain complain = complainMapper.selectByPrimaryKey(complainId);
        if (complain == null) {
            return ServerResponse.createByErrorMessage("未找到对应申诉");
        }
        complain.setStatus(state);
        complain.setUserId(userId);
        complain.setDescription(description);
        complain.setFiles(files);
        if (state == 2) {   //TODO 申诉成功后要对对应的业务逻辑进行处理
            if (complain.getComplainType() != null)
                switch (complain.getComplainType()) {
                    case 1://TODO 1:工匠被处罚后不服.
                       /* RewardPunishRecord rewardPunishRecord = rewardPunishRecordMapper.selectByPrimaryKey(businessId);
                        rewardPunishRecord.setComplainId(complain.getId());
                        rewardPunishRecordMapper.updateByPrimaryKeySelective(rewardPunishRecord);*/
                        break;
                    case 2://2：业主要求整改.
                        Member member = memberMapper.selectByPrimaryKey(complain.getBusinessId());
                        houseWorkerMapper.changeWorkerByHouseIdWorkerId(complain.getHouseId(), member.getId());
                        String houseFlowId = houseFlowMapper.selectHouseFlowId(complain.getHouseId(), member.getWorkerTypeId());
                        if (CommonUtil.isEmpty(houseFlowId)) {
                            return ServerResponse.createByErrorMessage("该工匠不在要求整改中");
                        }
                        HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(houseFlowId);
                        houseFlow.setWorkSteta(4);
                        houseFlowMapper.updateByPrimaryKey(houseFlow);
                        break;

                    case 3:// 3：大管家（开工后）要求换人.
                        member = memberMapper.selectByPrimaryKey(complain.getBusinessId());
                        houseWorkerMapper.changeWorkerByHouseIdWorkerId(complain.getHouseId(), member.getId());
                        houseFlowId = houseFlowMapper.selectHouseFlowId(complain.getHouseId(), member.getWorkerTypeId());
                        houseFlow = houseFlowMapper.selectByPrimaryKey(houseFlowId);
                        houseFlow.setGrabLock(0);
                        houseFlow.setWorkType(2);
                        houseFlowMapper.updateByPrimaryKey(houseFlow);
                        break;
                    case 4://TODO 4:部分收货申诉
                        ServerResponse response = splitDeliverService.splitDeliverDetail(complain.getBusinessId());
                        if (response.isSuccess()) {
                            String json_str = JSON.toJSON(response.getResultObj()).toString();
                            JSONObject json = JSONObject.parseObject(json_str);
                            Map<String, Object> json_map = json.getInnerMap();
                            List<Map<String, Object>> list_Map = (List<Map<String, Object>>) json_map.get("splitDeliverItemDTOList");
                            for (Map<String, Object> tmp : list_Map) {
                                String id = tmp.get("id").toString();
                                OrderSplitItem orderSplitItem = orderSplitItemMapper.selectByPrimaryKey(id);
                                if (orderSplitItem.getReceive() == null || (orderSplitItem.getNum() > orderSplitItem.getReceive())) {
                                    orderSplitItem.setReceive(orderSplitItem.getNum());
                                    orderSplitItemMapper.updateByPrimaryKey(orderSplitItem);
                                }
                            }
                        } else {
                            return response;
                        }
                        break;
                }
        }
        complainMapper.updateByPrimaryKeySelective(complain);
        return ServerResponse.createBySuccessMessage("提交成功");
    }

    /**
     * 获取申诉详情
     *
     * @param complainId 申诉ID
     * @return
     */
    public ServerResponse getComplain(String complainId) {
        if (CommonUtil.isEmpty(complainId)) {
            return ServerResponse.createByErrorMessage("参数不正确");
        }
        ComplainDTO complain = complainMapper.getComplain(complainId);
        if (complain == null) {
            return ServerResponse.createByErrorMessage("未找到对应申诉");
        }
        String files = complain.getFiles();
        if (!CommonUtil.isEmpty(files)) {
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            List<String> filesList = new ArrayList<>();
            String[] fs = files.split(",");
            for (String f : fs) {
                filesList.add(address + f);
            }
            if (filesList.size() > 0) {
                complain.setFileList(filesList);
            }
        } else {
            List<String> list = new ArrayList<>();
            complain.setFileList(list);
        }
        //添加返回体
        if (complain.getComplainType() != null)
            switch (complain.getComplainType()) {
                case 1://1:工匠被处罚后不服.
                    RewardPunishRecordDTO rewardPunishRecordDTO = rewardPunishRecordMapper.getRewardPunishRecord(complain.getBusinessId());
                    if (rewardPunishRecordDTO == null) {
                        return ServerResponse.createByErrorMessage("没有找到对应处罚单");
                    }
                    complain.setData(rewardPunishRecordDTO);
                    break;
                case 2://2：业主要求整改.
                case 3:// 3：大管家（开工后）要求换人.
                    Member member = memberMapper.selectByPrimaryKey(complain.getBusinessId());
                    if (member == null) {
                        return ServerResponse.createByErrorMessage("没有找到对应工匠");
                    }
                    member.initPath(configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class));
                    member.setPassword(null);
                    complain.setData(member);
                    break;
                case 4:// 4:部分收货申诉
                    ServerResponse response = splitDeliverService.splitDeliverDetail(complain.getBusinessId());
                    if (response.isSuccess()) {
                        complain.setData(response.getResultObj());
                    } else {
                        return response;
                    }
                    break;
            }
        ServerResponse serverResponse = ServerResponse.createBySuccess("查询成功", complain);
        return serverResponse;
    }
}
