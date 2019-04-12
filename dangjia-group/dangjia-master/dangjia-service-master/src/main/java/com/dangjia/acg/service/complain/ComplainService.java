package com.dangjia.acg.service.complain;

import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.api.sup.SupplierProductAPI;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.enums.EventStatus;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.complain.ComplainDTO;
import com.dangjia.acg.dto.deliver.SplitDeliverDTO;
import com.dangjia.acg.dto.deliver.SplitDeliverItemDTO;
import com.dangjia.acg.dto.worker.RewardPunishRecordDTO;
import com.dangjia.acg.mapper.complain.IComplainMapper;
import com.dangjia.acg.mapper.core.IHouseFlowApplyMapper;
import com.dangjia.acg.mapper.core.IHouseFlowMapper;
import com.dangjia.acg.mapper.core.IHouseWorkerMapper;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.deliver.IOrderSplitItemMapper;
import com.dangjia.acg.mapper.deliver.ISplitDeliverMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.user.UserMapper;
import com.dangjia.acg.mapper.worker.IRewardPunishConditionMapper;
import com.dangjia.acg.mapper.worker.IRewardPunishRecordMapper;
import com.dangjia.acg.mapper.worker.IWorkIntegralMapper;
import com.dangjia.acg.mapper.worker.IWorkerDetailMapper;
import com.dangjia.acg.modle.complain.Complain;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.core.HouseFlowApply;
import com.dangjia.acg.modle.deliver.OrderSplitItem;
import com.dangjia.acg.modle.deliver.SplitDeliver;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.sup.Supplier;
import com.dangjia.acg.modle.user.MainUser;
import com.dangjia.acg.modle.worker.RewardPunishCondition;
import com.dangjia.acg.modle.worker.RewardPunishRecord;
import com.dangjia.acg.modle.worker.WorkIntegral;
import com.dangjia.acg.modle.worker.WorkerDetail;
import com.dangjia.acg.service.deliver.SplitDeliverService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.ArrayList;
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
    private IRewardPunishRecordMapper rewardPunishRecordMapper;
    @Autowired
    private IHouseWorkerMapper houseWorkerMapper;
    @Autowired
    private IHouseFlowMapper houseFlowMapper;
    @Autowired
    private IOrderSplitItemMapper orderSplitItemMapper;
    @Autowired
    private IWorkerTypeMapper iWorkerTypeMapper;
    @Autowired
    private SupplierProductAPI supplierProductAPI;
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ISplitDeliverMapper splitDeliverMapper;

    @Autowired
    private IHouseFlowApplyMapper houseFlowApplyMapper;

    @Autowired
    private IRewardPunishConditionMapper iRewardPunishConditionMapper;

    @Autowired
    private IWorkIntegralMapper iWorkIntegralMapper;
    @Autowired
    private SplitDeliverService splitDeliverService;
    @Autowired
    private IWorkerDetailMapper iWorkerDetailMapper;
    /**
     * 添加申诉
     *
     * @param userToken    用户Token
     * @param complainType 申诉类型 1:工匠被处罚后不服.2：业主要求整改.3：大管家（开工后）要求换人.4:部分收货申诉.
     * @param businessId   对应业务ID
     *                     complain_type==1:对应处罚的rewardPunishRecordId,
     *                     complain_type==2:对应房子任务进程/申请表的houseFlowApplyId,
     *                     complain_type==3:对应工人订单表的houseWokerId,
     *                     complain_type==4:发货单splitDeliverId,
     * @param houseId      对应房子ID
     * @return
     */


    public ServerResponse addComplain(String userToken, Integer complainType, String businessId, String houseId, String files) {
        if (CommonUtil.isEmpty(complainType) || CommonUtil.isEmpty(businessId)) {
            return ServerResponse.createByErrorMessage("参数错误");
        }
        if((complainType==2||complainType==3)&&CommonUtil.isEmpty(houseId)){
            return ServerResponse.createByErrorMessage("参数错误");
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
        complain.setUserId(getUserID(complainType,businessId,houseId));
        complain.setHouseId(houseId);
        complain.setFiles(files);
        complainMapper.insertSelective(complain);
        return ServerResponse.createBySuccessMessage("提交成功");
    }

    /**
     * 根据类型和业务ID加房子ID获得发起人的用户ID
     * @param complainType 业务类型
     * @param businessId 业务ID
     * @param houseId 房子ID
     * @return
     */
    public String getUserID(Integer complainType, String businessId , String houseId ){
        String userid="";
        if (complainType != null)
            switch (complainType) {
                case 1://奖罚
                    RewardPunishRecord rewardPunishRecord = rewardPunishRecordMapper.selectByPrimaryKey(businessId);
                    userid=rewardPunishRecord.getOperatorId();
                    break;
                case 2://2：业主要求整改.
                    Member stewardHouse = memberMapper.getSupervisor(houseId);
                    userid=stewardHouse.getId();
                    break;
                case 3:// 3：大管家（开工后）要求换人.
                    stewardHouse = memberMapper.getSupervisor(houseId);
                    userid=stewardHouse.getId();
                    break;
                case 4:// 4:部分收货申诉
                    SplitDeliver response = splitDeliverMapper.selectByPrimaryKey(businessId);
                    userid=response.getSupplierId();
            }
        return userid;
    }

    /**
     * 根据用户ID返回用户名称
     * @param userId
     * @return
     */
    public String getUserName(Integer complainType, String userId){
        String userName="";
        if (complainType != null&&!CommonUtil.isEmpty(userId))
            switch (complainType) {
                case 1://奖罚
                    MainUser user=userMapper.selectByPrimaryKey(userId);
                    if(user==null){
                        Member member=memberMapper.selectByPrimaryKey(userId);
                        userName=iWorkerTypeMapper.selectByPrimaryKey(member.getWorkerTypeId()).getName()+"-"+(CommonUtil.isEmpty(member.getName())?member.getUserName():member.getName());
                    }else {
                        userName="客服-"+user.getUsername();
                    }
                    break;
                case 2://2：业主要求整改.
                    Member member=memberMapper.selectByPrimaryKey(userId);
                    userName=iWorkerTypeMapper.selectByPrimaryKey(member.getWorkerTypeId()).getName()+"-"+(CommonUtil.isEmpty(member.getName())?member.getUserName():member.getName());
                    break;
                case 3:// 3：大管家（开工后）要求换人.
                    member=memberMapper.selectByPrimaryKey(userId);
                    userName=iWorkerTypeMapper.selectByPrimaryKey(member.getWorkerTypeId()).getName()+"-"+(CommonUtil.isEmpty(member.getName())?member.getUserName():member.getName());
                    break;
                case 4:// 4:部分收货申诉
                    Supplier supplier=supplierProductAPI.getSupplier(userId);
                    userName="供应商-"+supplier.getName();
                    break;
            }
        return userName;
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
                        RewardPunishRecord rewardPunishRecord = rewardPunishRecordMapper.selectByPrimaryKey(complain.getBusinessId());
                        //1.获取被罚的工人id
                        //2.获取奖罚条例的类型来判断是奖励还是处罚
                        //3.获取条例的明细列表
                        if(rewardPunishRecord.getType()==null){
                            break;
                        }
                        Member member= memberMapper.selectByPrimaryKey(rewardPunishRecord.getMemberId());
                        Example example =new Example(RewardPunishCondition.class);
                        example.createCriteria().andEqualTo(RewardPunishCondition.REWARD_PUNISH_CORRELATION_ID,rewardPunishRecord.getRewardPunishCorrelationId());
                        List<RewardPunishCondition> rewardPunishConditionList =  iRewardPunishConditionMapper.selectByExample(example);
                        for(RewardPunishCondition rewardPunishCondition:rewardPunishConditionList) {
                            BigDecimal bigDecimal = rewardPunishCondition.getQuantity();
                            String strBigDecimal="-"+bigDecimal.doubleValue();
                            BigDecimal b=new BigDecimal(strBigDecimal);
                            BigDecimal bigDecimal2;
                            WorkIntegral workIntegral = new WorkIntegral();
                            WorkerDetail workerDetail = new WorkerDetail();

                            workIntegral.setHouseId(rewardPunishRecord.getHouseId());
                            workIntegral.setStatus(0);
                            workIntegral.setWorkerId(member.getId());
                            workIntegral.setBriefed("申述成功，积分还原");
                            workerDetail.setName("申述成功，资金还原");
                            workerDetail.setWorkerId(member.getId());
                            workerDetail.setWorkerName(member.getName());
                            workerDetail.setHouseId(rewardPunishRecord.getHouseId());
                            if (rewardPunishRecord.getType() == 0) {
                                //奖励
                                //4.根据每个条例的明细类型（奖或罚）来判断，该工人是否扣除或者增加（只对账户余额和积分进行增减）
                                //1积分;2钱;3限制接单;4冻结账号
                                //5.对工人原有的基础之上重新set账户余额或者积分并进行更新（update）
                                if (rewardPunishCondition.getType() == 1) {
                                    bigDecimal2 = member.getEvaluationScore().subtract(bigDecimal);
                                    member.setEvaluationScore(bigDecimal2);
                                    //加积分流水
                                    workIntegral.setIntegral(b);
                                    iWorkIntegralMapper.insert(workIntegral);
                                }
                                if (rewardPunishCondition.getType() == 2) {
                                    bigDecimal2 = member.getSurplusMoney().subtract(bigDecimal);
                                    BigDecimal haveMoney = member.getHaveMoney().subtract(bigDecimal);
                                    member.setSurplusMoney(bigDecimal2);
                                    member.setHaveMoney(haveMoney);
                                    //加流水记录
                                    workerDetail.setMoney(b);
                                    workerDetail.setState(1);
                                    iWorkerDetailMapper.insert(workerDetail);
                                }

                                //罚
                            }  if (rewardPunishRecord.getType() == 1) {
                                if (rewardPunishCondition.getType() == 1) {
                                    bigDecimal2 = member.getEvaluationScore().add(bigDecimal);
                                    member.setEvaluationScore(bigDecimal2);
                                    //加积分流水
                                    workIntegral.setIntegral(bigDecimal);
                                    iWorkIntegralMapper.insert(workIntegral);
                                }
                                if (rewardPunishCondition.getType() == 2) {
                                    bigDecimal2 = member.getSurplusMoney().add(bigDecimal);
                                    BigDecimal haveMoney = member.getHaveMoney().add(bigDecimal);
                                    member.setSurplusMoney(bigDecimal2);
                                    member.setHaveMoney(haveMoney);
                                    //加流水记录
                                    workerDetail.setMoney(bigDecimal);
                                    workerDetail.setState(0);
                                    iWorkerDetailMapper.insert(workerDetail);
                                }
                            }
                            memberMapper.updateByPrimaryKeySelective(member);
                        }
                        //7.将奖罚设置为禁用
                        rewardPunishRecord.setState(1);
                        rewardPunishRecordMapper.updateByPrimaryKeySelective(rewardPunishRecord);
                        break;
                    case 2://2：业主要求整改.
                        HouseFlowApply houseFlowApply=houseFlowApplyMapper.selectByPrimaryKey(complain.getBusinessId());
                        houseFlowApply.setMemberCheck(2);
                        houseFlowApply.setSupervisorCheck(2);
                        houseFlowApplyMapper.updateByPrimaryKeySelective(houseFlowApply);
                        //不通过停工申请
                        HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(houseFlowApply.getHouseFlowId());
                        houseFlow.setPause(0);
                        houseFlowMapper.updateByPrimaryKeySelective(houseFlow);
                        break;

                    case 3:// 3：大管家（开工后）要求换人.
//                        HouseWorker houseWorker = houseWorkerMapper.selectByPrimaryKey(complain.getBusinessId());
//                        if (houseWorker.getWorkType() == 6) {
//                            return ServerResponse.createByErrorMessage("已支付,更换换人方式");
//                        }
//                        houseWorker.setWorkType(3);//被平台换
//                        houseWorkerMapper.updateByPrimaryKeySelective(houseWorker);
//                        houseFlow = houseFlowMapper.getByWorkerTypeId(houseWorker.getHouseId(), houseWorker.getWorkerTypeId());
//                        houseFlow.setWorkerId("");
//                        houseFlow.setWorkType(2);
//                        houseFlow.setReleaseTime(new Date());//重新发布
//                        houseFlow.setRefuseNumber(houseFlow.getRefuseNumber() + 1);
//                        houseFlowMapper.updateByPrimaryKeySelective(houseFlow);
                        break;
                    case 4:// 4:部分收货申诉
                        ServerResponse response = splitDeliverService.splitDeliverDetail(complain.getBusinessId());
                        if (response.isSuccess()) {
                            JSONObject json = (JSONObject)response.getResultObj();
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
        String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        if (CommonUtil.isEmpty(complainId)) {
            return ServerResponse.createByErrorMessage("参数不正确");
        }
        ComplainDTO complain = complainMapper.getComplain(complainId);
        if (complain == null) {
            return ServerResponse.createByErrorMessage("未找到对应申诉");
        }
        String files = complain.getFiles();
        if (!CommonUtil.isEmpty(files)) {
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
        complain.setContent(getUserName(complain.getComplainType(),complain.getUserId()));

        //添加返回体
        if (complain.getComplainType() != null){
            if(complain.getComplainType()==1){//奖罚
                RewardPunishRecordDTO rewardPunishRecordDTO=new RewardPunishRecordDTO();
                rewardPunishRecordDTO.setId(complain.getBusinessId());
                complain.setData(rewardPunishRecordMapper.queryRewardPunishRecord(rewardPunishRecordDTO));
            }
            if(complain.getComplainType()==4){//收货
                SplitDeliver splitDeliver = splitDeliverMapper.selectByPrimaryKey(complain.getBusinessId());
                SplitDeliverDTO splitDeliverDTO = new SplitDeliverDTO();
                splitDeliverDTO.setShipState(splitDeliver.getShippingState());//发货状态
                splitDeliverDTO.setNumber(splitDeliver.getNumber());
                splitDeliverDTO.setCreateDate(splitDeliver.getCreateDate());
                splitDeliverDTO.setSendTime(splitDeliver.getSendTime());
                splitDeliverDTO.setSubmitTime(splitDeliver.getSubmitTime());
                splitDeliverDTO.setModifyDate(splitDeliver.getModifyDate());//收货时间
                splitDeliverDTO.setTotalAmount(splitDeliver.getTotalAmount());
                splitDeliverDTO.setSupState(splitDeliver.getSupState());//大管家收货状态
                splitDeliverDTO.setSupName(splitDeliver.getSupplierName());
                splitDeliverDTO.setSupId(splitDeliver.getSupervisorId());
                splitDeliverDTO.setSupMobile(splitDeliver.getShipMobile());
                Example example = new Example(OrderSplitItem.class);
                example.createCriteria().andEqualTo(OrderSplitItem.SPLIT_DELIVER_ID, splitDeliver.getId());
                List<OrderSplitItem> orderSplitItemList = orderSplitItemMapper.selectByExample(example);
                List<SplitDeliverItemDTO> splitDeliverItemDTOList = new ArrayList<>();
                for (OrderSplitItem orderSplitItem : orderSplitItemList){
                    SplitDeliverItemDTO splitDeliverItemDTO = new SplitDeliverItemDTO();
                    splitDeliverItemDTO.setImage(address + orderSplitItem.getImage());
                    splitDeliverItemDTO.setProductName(orderSplitItem.getProductName());
                    splitDeliverItemDTO.setTotalPrice(orderSplitItem.getTotalPrice());
                    splitDeliverItemDTO.setShopCount(orderSplitItem.getShopCount());
                    splitDeliverItemDTO.setNum(orderSplitItem.getNum());
                    splitDeliverItemDTO.setUnitName(orderSplitItem.getUnitName());
                    splitDeliverItemDTO.setPrice(orderSplitItem.getPrice());
                    splitDeliverItemDTO.setCost(orderSplitItem.getCost());
                    splitDeliverItemDTO.setId(orderSplitItem.getId());
                    splitDeliverItemDTO.setReceive(orderSplitItem.getReceive());//收货数量
                    splitDeliverItemDTO.setSupCost(orderSplitItem.getSupCost());
                    splitDeliverItemDTO.setAskCount(orderSplitItem.getAskCount());
                    splitDeliverItemDTOList.add(splitDeliverItemDTO);
                }
                splitDeliverDTO.setSplitDeliverItemDTOList(splitDeliverItemDTOList);//明细
                complain.setData(splitDeliverDTO);
            }
        }
        ServerResponse serverResponse = ServerResponse.createBySuccess("查询成功", complain);
        return serverResponse;
    }
}
