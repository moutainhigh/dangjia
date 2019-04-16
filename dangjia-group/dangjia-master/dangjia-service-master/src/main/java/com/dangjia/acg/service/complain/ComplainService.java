package com.dangjia.acg.service.complain;

import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.api.sup.SupplierProductAPI;
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
import com.dangjia.acg.mapper.core.*;
import com.dangjia.acg.mapper.deliver.IOrderSplitItemMapper;
import com.dangjia.acg.mapper.deliver.ISplitDeliverMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.user.UserMapper;
import com.dangjia.acg.mapper.worker.IRewardPunishConditionMapper;
import com.dangjia.acg.mapper.worker.IRewardPunishRecordMapper;
import com.dangjia.acg.mapper.worker.IWorkIntegralMapper;
import com.dangjia.acg.mapper.worker.IWorkerDetailMapper;
import com.dangjia.acg.modle.complain.Complain;
import com.dangjia.acg.modle.core.HouseConstructionRecord;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.core.HouseFlowApply;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.deliver.OrderSplitItem;
import com.dangjia.acg.modle.deliver.SplitDeliver;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.sup.Supplier;
import com.dangjia.acg.modle.user.MainUser;
import com.dangjia.acg.modle.worker.RewardPunishCondition;
import com.dangjia.acg.modle.worker.RewardPunishRecord;
import com.dangjia.acg.modle.worker.WorkIntegral;
import com.dangjia.acg.modle.worker.WorkerDetail;
import com.dangjia.acg.service.core.HouseWorkerSupService;
import com.dangjia.acg.service.deliver.SplitDeliverService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


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
    private IHouseMapper houseMapper;
    @Autowired
    private IRewardPunishConditionMapper iRewardPunishConditionMapper;
    @Autowired
    private IWorkIntegralMapper iWorkIntegralMapper;
    @Autowired
    private SplitDeliverService splitDeliverService;
    @Autowired
    private IWorkerDetailMapper iWorkerDetailMapper;
    @Autowired
    private IHouseConstructionRecordMapper houseConstructionRecordMapper;
    @Autowired
    private HouseWorkerSupService houseWorkerSupService;

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

    public ServerResponse addComplain(String userToken, String memberId, Integer complainType, String businessId, String houseId, String files) {
        if (CommonUtil.isEmpty(complainType) || CommonUtil.isEmpty(businessId)) {
            return ServerResponse.createByErrorMessage("参数错误");
        }
        if ((complainType == 2 || complainType == 3) && CommonUtil.isEmpty(houseId)) {
            return ServerResponse.createByErrorMessage("参数错误");
        }
        Example example = new Example(Complain.class);
        example.createCriteria()
                .andEqualTo(Complain.MEMBER_ID, memberId)
                .andEqualTo(Complain.COMPLAIN_TYPE, complainType)
                .andEqualTo(Complain.BUSINESS_ID, businessId)
                .andEqualTo(Complain.STATUS, 0);
        List list = complainMapper.selectByExample(example);
        if (list.size() > 0) {
            return ServerResponse.createByErrorMessage("请勿重复提交申请！");
        }
        Complain complain = new Complain();
        complain.setMemberId(memberId);
        complain.setComplainType(complainType);
        complain.setBusinessId(businessId);
        complain.setUserId(getUserID(complainType, businessId, houseId));
        complain.setHouseId(houseId);
        complain.setFiles(files);


//        1:工匠被处罚后不服.2：业主要求整改.3：要求换人.4:部分收货申诉.
        if (complainType == 4) {
            Supplier supplier = supplierProductAPI.getSupplier(complain.getUserId());
            complain.setUserMobile(supplier.getTelephone());
            complain.setUserName(supplier.getName());
            complain.setUserNickName("供应商-" + supplier.getCheckPeople());
        } else {
            String field = "业主-";
            Member member = memberMapper.selectByPrimaryKey(complain.getUserId());
            if (member != null) {
                if (!CommonUtil.isEmpty(member.getWorkerTypeId())) {
                    WorkerType workerType = iWorkerTypeMapper.selectByPrimaryKey(member.getWorkerTypeId());
                    if (workerType != null) {
                        field = workerType.getName() + "-";
                    }
                }
                complain.setUserMobile(member.getMobile());
                complain.setUserName(CommonUtil.isEmpty(member.getName()) ? member.getNickName() : member.getName());
                complain.setUserNickName(field + member.getNickName());
            }
        }
        complain.setContent(getUserName(complain.getComplainType(), complain.getMemberId(), complain.getHouseId()));
        complainMapper.insertSelective(complain);

        if (complain.getComplainType() != null && complain.getComplainType() == 2) {
            //将申请进程更新为申述中。。
            HouseFlowApply houseFlowApply = houseFlowApplyMapper.selectByPrimaryKey(complain.getBusinessId());
            houseFlowApply.setMemberCheck(4);
            houseFlowApplyMapper.updateByPrimaryKeySelective(houseFlowApply);

            HouseConstructionRecord hcr = houseConstructionRecordMapper.selectHcrByHouseFlowApplyId(houseFlowApply.getId());
            houseWorkerSupService.saveHouseConstructionRecord(houseFlowApply, hcr);
        }
        return ServerResponse.createBySuccessMessage("提交成功");
    }

    /**
     * 根据类型和业务ID加房子ID获得发起人的用户ID
     *
     * @param complainType 业务类型
     * @param businessId   业务ID
     * @param houseId      房子ID
     * @return
     */
    public String getUserID(Integer complainType, String businessId, String houseId) {
        String userid = "";
        if (complainType != null)
            switch (complainType) {
                case 1://奖罚
                    RewardPunishRecord rewardPunishRecord = rewardPunishRecordMapper.selectByPrimaryKey(businessId);
                    userid = rewardPunishRecord.getMemberId();
                    break;
                case 2://2：业主要求整改.
                    House house = houseMapper.selectByPrimaryKey(houseId);
                    Member stewardHouse = memberMapper.selectByPrimaryKey(house.getMemberId());
                    userid = stewardHouse.getId();
                    break;
                case 3:// 3：大管家（开工后）要求换人.
                    stewardHouse = memberMapper.getSupervisor(houseId);
                    userid = stewardHouse.getId();
                    break;
                case 4:// 4:部分收货申诉
                    SplitDeliver response = splitDeliverMapper.selectByPrimaryKey(businessId);
                    userid = response.getSupplierId();
            }
        return userid;
    }

    /**
     * 根据用户ID获取对象名称
     *
     * @param memberId
     * @return
     */
    public String getUserName(Integer complainType, String memberId, String houseid) {
        if (complainType == 4) {
            House house = houseMapper.selectByPrimaryKey(houseid);
            return house.getHouseName();
        } else {
            String field = "业主-";
            String userName = "";
            Member member = memberMapper.selectByPrimaryKey(memberId);
            if (member != null) {
                if (!CommonUtil.isEmpty(member.getWorkerTypeId())) {
                    WorkerType workerType = iWorkerTypeMapper.selectByPrimaryKey(member.getWorkerTypeId());
                    field = workerType.getName() + "-";
                }
                userName = field + (CommonUtil.isEmpty(member.getName()) ? member.getUserName() : member.getName());
                return userName;
            }
            MainUser user = userMapper.selectByPrimaryKey(memberId);
            if (user != null) {
                field = "客服-";
                userName = field + user.getUsername();
                return userName;
            }
            return userName;
        }
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

//                u.username as userName,
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
    public ServerResponse updataComplain(String userId, String complainId, Integer state, String description, String files, String operateId, String operateName) {
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
        complain.setOperateId(operateId);
        complain.setOperateName(userMapper.selectByPrimaryKey(userId).getUsername());

        if (state == 2) {   // 申诉成功后要对对应的业务逻辑进行处理
            if (complain.getComplainType() != null)
                switch (complain.getComplainType()) {
                    case 1:// 1:工匠被处罚后不服.
                        RewardPunishRecord rewardPunishRecord = rewardPunishRecordMapper.selectByPrimaryKey(complain.getBusinessId());
                        //1.获取被罚的工人id
                        //2.获取奖罚条例的类型来判断是奖励还是处罚
                        //3.获取条例的明细列表
                        if (rewardPunishRecord.getType() == null) {
                            break;
                        }
                        Member member = memberMapper.selectByPrimaryKey(rewardPunishRecord.getMemberId());
                        Example example = new Example(RewardPunishCondition.class);
                        example.createCriteria().andEqualTo(RewardPunishCondition.REWARD_PUNISH_CORRELATION_ID, rewardPunishRecord.getRewardPunishCorrelationId());
                        List<RewardPunishCondition> rewardPunishConditionList = iRewardPunishConditionMapper.selectByExample(example);
                        for (RewardPunishCondition rewardPunishCondition : rewardPunishConditionList) {
                            BigDecimal bigDecimal = rewardPunishCondition.getQuantity();
                            String strBigDecimal = "-" + bigDecimal.doubleValue();
                            BigDecimal b = new BigDecimal(strBigDecimal);
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
                            }
                            if (rewardPunishRecord.getType() == 1) {
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
                        HouseFlowApply houseFlowApply = houseFlowApplyMapper.selectByPrimaryKey(complain.getBusinessId());
                        houseFlowApply.setMemberCheck(2);
                        houseFlowApply.setSupervisorCheck(2);
                        houseFlowApplyMapper.updateByPrimaryKeySelective(houseFlowApply);

                        HouseConstructionRecord hcr = houseConstructionRecordMapper.selectHcrByHouseFlowApplyId(houseFlowApply.getId());
                        hcr.setMemberCheck(2);
                        hcr.setSupervisorCheck(2);
                        houseConstructionRecordMapper.updateByPrimaryKeySelective(hcr);
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
                            SplitDeliverDTO json = (SplitDeliverDTO) response.getResultObj();
                            List<SplitDeliverItemDTO> list_Map = json.getSplitDeliverItemDTOList();
                            for (SplitDeliverItemDTO tmp : list_Map) {
                                OrderSplitItem orderSplitItem = orderSplitItemMapper.selectByPrimaryKey(tmp.getId());
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
        } else {
            if (complain.getComplainType() != null && complain.getComplainType() == 2) {
                //将申请进程打回待审核。。
                HouseFlowApply houseFlowApply = houseFlowApplyMapper.selectByPrimaryKey(complain.getBusinessId());
                houseFlowApply.setMemberCheck(0);
                houseFlowApplyMapper.updateByPrimaryKeySelective(houseFlowApply);
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

        //添加返回体
        if (complain.getComplainType() != null) {
            if (complain.getComplainType() == 1) {//奖罚
                RewardPunishRecordDTO rewardPunishRecordDTO = new RewardPunishRecordDTO();
                rewardPunishRecordDTO.setId(complain.getBusinessId());
                complain.setData(rewardPunishRecordMapper.queryRewardPunishRecord(rewardPunishRecordDTO));
            }
            if (complain.getComplainType() == 4) {//收货
                SplitDeliver splitDeliver = splitDeliverMapper.selectByPrimaryKey(complain.getBusinessId());
                SplitDeliverDTO splitDeliverDTO = new SplitDeliverDTO();
                splitDeliverDTO.setShipState(splitDeliver.getShippingState());//发货状态
                splitDeliverDTO.setNumber(splitDeliver.getNumber());
                splitDeliverDTO.setCreateDate(splitDeliver.getCreateDate());
                splitDeliverDTO.setSendTime(splitDeliver.getSendTime());
                splitDeliverDTO.setSubmitTime(splitDeliver.getSubmitTime());
                splitDeliverDTO.setRecTime(splitDeliver.getRecTime() == null ? splitDeliver.getModifyDate() : splitDeliver.getRecTime());//收货时间
                splitDeliverDTO.setTotalAmount(splitDeliver.getTotalAmount());
                splitDeliverDTO.setSupState(splitDeliver.getSupState());//大管家收货状态
                splitDeliverDTO.setSupName(splitDeliver.getSupplierName());
                splitDeliverDTO.setSupId(splitDeliver.getSupervisorId());
                splitDeliverDTO.setSupMobile(splitDeliver.getShipMobile());
                Example example = new Example(OrderSplitItem.class);
                example.createCriteria().andEqualTo(OrderSplitItem.SPLIT_DELIVER_ID, splitDeliver.getId());
                List<OrderSplitItem> orderSplitItemList = orderSplitItemMapper.selectByExample(example);
                List<SplitDeliverItemDTO> splitDeliverItemDTOList = new ArrayList<>();
                for (OrderSplitItem orderSplitItem : orderSplitItemList) {
                    SplitDeliverItemDTO splitDeliverItemDTO = new SplitDeliverItemDTO();
                    splitDeliverItemDTO.setImage(address + orderSplitItem.getImage());
                    splitDeliverItemDTO.setProductSn(orderSplitItem.getProductSn());
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
