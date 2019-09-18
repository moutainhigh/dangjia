package com.dangjia.acg.service.complain;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.data.ForMasterAPI;
import com.dangjia.acg.api.sup.SupplierProductAPI;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.complain.ComPlainStopDTO;
import com.dangjia.acg.dto.complain.ComplainDTO;
import com.dangjia.acg.dto.deliver.SplitDeliverDTO;
import com.dangjia.acg.dto.deliver.SplitDeliverItemDTO;
import com.dangjia.acg.dto.worker.RewardPunishRecordDTO;
import com.dangjia.acg.mapper.complain.IComplainMapper;
import com.dangjia.acg.mapper.core.IHouseFlowApplyMapper;
import com.dangjia.acg.mapper.core.IHouseFlowMapper;
import com.dangjia.acg.mapper.core.IHouseWorkerOrderMapper;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.deliver.IOrderSplitItemMapper;
import com.dangjia.acg.mapper.deliver.ISplitDeliverMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.matter.ITechnologyRecordMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.safe.IWorkerTypeSafeOrderMapper;
import com.dangjia.acg.mapper.user.UserMapper;
import com.dangjia.acg.mapper.worker.IRewardPunishConditionMapper;
import com.dangjia.acg.mapper.worker.IRewardPunishRecordMapper;
import com.dangjia.acg.mapper.worker.IWorkIntegralMapper;
import com.dangjia.acg.mapper.worker.IWorkerDetailMapper;
import com.dangjia.acg.modle.basics.Product;
import com.dangjia.acg.modle.complain.Complain;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.core.HouseFlowApply;
import com.dangjia.acg.modle.core.HouseWorkerOrder;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.deliver.OrderSplitItem;
import com.dangjia.acg.modle.deliver.SplitDeliver;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.safe.WorkerTypeSafeOrder;
import com.dangjia.acg.modle.sup.Supplier;
import com.dangjia.acg.modle.user.MainUser;
import com.dangjia.acg.modle.worker.RewardPunishCondition;
import com.dangjia.acg.modle.worker.RewardPunishRecord;
import com.dangjia.acg.modle.worker.WorkIntegral;
import com.dangjia.acg.modle.worker.WorkerDetail;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.dangjia.acg.service.deliver.SplitDeliverService;
import com.dangjia.acg.service.house.HouseService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.*;


@Service
public class ComplainService {
    @Autowired
    private IMemberMapper memberMapper;
    @Autowired
    private IComplainMapper complainMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IRewardPunishRecordMapper rewardPunishRecordMapper;
    @Autowired
    private ITechnologyRecordMapper technologyRecordMapper;
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
    private CraftsmanConstructionService constructionService;
    @Autowired
    private IHouseWorkerOrderMapper houseWorkerOrderMapper;
    @Autowired
    private IWorkerTypeSafeOrderMapper workerTypeSafeOrderMapper;
    @Autowired
    private HouseService houseService;

    @Autowired
    private ForMasterAPI forMasterAPI;
    /**
     * 添加申诉
     *
     * @param userToken    用户Token
     * @param complainType 申诉类型 1:工匠被处罚后不服.2：业主要求整改.3：大管家（开工后）要求换人.4:部分收货申诉.5.提前结束装修，6.业主申请换人
     * @param businessId   对应业务ID
     *                     complain_type==1:对应处罚的rewardPunishRecordId,
     *                     complain_type==2:对应房子任务进程/申请表的houseFlowApplyId,
     *                     complain_type==3:对应工人订单表的houseWokerId,
     *                     complain_type==4:发货单splitDeliverId,
     *                     complain_type==5:对应工人订单表的houseWokerId,
     * @param houseId      对应房子ID
     * @return
     */

    public ServerResponse addComplain(String userToken, String memberId, Integer complainType,
                                      String businessId, String houseId, String files) {
        if (CommonUtil.isEmpty(complainType) || CommonUtil.isEmpty(businessId)) {
            return ServerResponse.createByErrorMessage("参数错误");
        }
        House house = houseMapper.selectByPrimaryKey(houseId);
        if (complainType != 1 && complainType != 4) {
            if (CommonUtil.isEmpty(houseId)) {
                return ServerResponse.createByErrorMessage("参数错误");
            }
            if (house == null) {
                return ServerResponse.createByErrorMessage("没有查询到相关房子");
            }
            if (house.getVisitState() != 1) {
                return ServerResponse.createByErrorMessage("该房子不在装修中无法提交该申请");
            }
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
            Supplier supplier = supplierProductAPI.getSupplier(house.getCityId(),complain.getUserId());
            if (supplier != null) {
                complain.setUserMobile(supplier.getTelephone());
                complain.setUserName(supplier.getName());
                complain.setUserNickName("供应商-" + supplier.getCheckPeople());
            }
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
            houseFlowApply.setModifyDate(new Date());
            houseFlowApplyMapper.updateByPrimaryKeySelective(houseFlowApply);
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
    private String getUserID(Integer complainType, String businessId, String houseId) {
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
                    break;
                case 6:// 6:业主要求换人
                    House house2 = houseMapper.selectByPrimaryKey(houseId);
                    Member stewardHouse2 = memberMapper.selectByPrimaryKey(house2.getMemberId());
                    userid = stewardHouse2.getId();
                    break;
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
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), "查无数据");
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
    public ServerResponse updataComplain(String userId, String complainId, Integer state, String description,
                                         String files, String operateId, String operateName) {
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
        if (!CommonUtil.isEmpty(operateId)) {
            complain.setOperateId(operateId);
            complain.setOperateName(userMapper.selectByPrimaryKey(operateId).getUsername());
        }
        if (state == 2) {   // 申诉成功后要对对应的业务逻辑进行处理
            if (complain.getComplainType() != null) {
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
                                //1积分;2钱;3限制接单;
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
                                    //加积分流水
                                    workIntegral.setIntegral(bigDecimal);
                                    member.setEvaluationScore(bigDecimal2);
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
                        houseFlowApply.setModifyDate(new Date());
                        houseFlowApplyMapper.updateByPrimaryKeySelective(houseFlowApply);
                        //不通过停工申请
                        HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(houseFlowApply.getHouseFlowId());
                        houseFlow.setPause(0);
                        houseFlowMapper.updateByPrimaryKeySelective(houseFlow);
                        //不通过节点验收
                        technologyRecordMapper.passNoTecRecord(houseFlowApply.getHouseId(), houseFlowApply.getWorkerTypeId());

                        //业主不通过工匠发起阶段/整体完工申请驳回次数超过两次后将扣工人钱
                        List<HouseFlowApply> houseFlowApplyList = houseFlowApplyMapper.noPassList(houseFlow.getId());
                        if (houseFlowApplyList.size() > 2) {
                            BigDecimal money = new BigDecimal(100);
                            member = memberMapper.selectByPrimaryKey(houseFlowApply.getWorkerId());
                            WorkerDetail workerDetail = new WorkerDetail();
                            workerDetail.setName("阶段/整体完工第" + houseFlowApplyList.size() + "次驳回,次数超过两次，工钱扣除");
                            workerDetail.setWorkerId(member.getId());
                            workerDetail.setWorkerName(member.getName());
                            workerDetail.setHouseId(houseFlowApply.getHouseId());
                            workerDetail.setMoney(money);
                            workerDetail.setState(3);
                            iWorkerDetailMapper.insert(workerDetail);
                            BigDecimal surplusMoney = member.getSurplusMoney().subtract(money);
                            BigDecimal haveMoney = member.getHaveMoney().subtract(money);
                            member.setSurplusMoney(surplusMoney);
                            member.setHaveMoney(haveMoney);
                            memberMapper.updateByPrimaryKeySelective(member);
                        }
                        break;
                    case 4:// 4:部分收货申诉
                        ServerResponse response = splitDeliverService.splitDeliverDetail(complain.getBusinessId());
                        if (response.isSuccess()) {
                            SplitDeliverDTO json = (SplitDeliverDTO) response.getResultObj();
                            List<SplitDeliverItemDTO> list_Map = json.getSplitDeliverItemDTOList();
                            double applyMoney=0d;
                            for (SplitDeliverItemDTO tmp : list_Map) {
                                OrderSplitItem orderSplitItem = orderSplitItemMapper.selectByPrimaryKey(tmp.getId());
                                if (orderSplitItem.getReceive() == null || (orderSplitItem.getNum() > orderSplitItem.getReceive())) {
                                    orderSplitItem.setReceive(orderSplitItem.getNum());
                                    orderSplitItemMapper.updateByPrimaryKey(orderSplitItem);
                                }
                                applyMoney+=orderSplitItem.getSupCost()*orderSplitItem.getReceive();
                            }
                            SplitDeliver splitDeliver = splitDeliverMapper.selectByPrimaryKey(complain.getBusinessId());
                            splitDeliver.setApplyMoney(applyMoney);
                            splitDeliverMapper.updateByPrimaryKeySelective(splitDeliver);
                        } else {
                            return response;
                        }
                        break;
                    case 5://提前结束装修
                        House house = houseMapper.selectByPrimaryKey(complain.getHouseId());
                        if (house == null) {
                            return ServerResponse.createByErrorMessage("没有查询到相关房子");
                        }
                        if (house.getVisitState() != 1 && house.getVisitState() != 5) {
                            return ServerResponse.createByErrorMessage("该房子没有在装修，无法结束装修");
                        }
                        setHouseFlowsWorkSteta(house.getId());
                        house.setModifyDate(new Date());
                        house.setCompletedDate(new Date());
                        house.setVisitState(4);
                        house.setHaveComplete(1);
//                        house.setDesignerOk(3);
//                        house.setBudgetOk(3);
                        JSONArray brandSeriesLists = JSONArray.parseArray(operateName);
                        List<Map<String, Object>> maps = new ArrayList<>();
                        if (backMoneyJudge(brandSeriesLists, maps))
                            return ServerResponse.createByErrorMessage("传入退款金额有误，请确认无误后提交");
                        for (Map<String, Object> map : maps) {
                            commitStopBuild(map.get("workerId"),
                                    new BigDecimal((Double) map.get("backMoney")),
                                    new BigDecimal((Double) map.get("haveMoney")), complain.getHouseId(),
                                    "业主提前结束装修，原因为" + complain.getContent());
                        }
                        houseMapper.updateByPrimaryKeySelective(house);
                        break;
                }
            }
        } else {
            if (complain.getComplainType() != null && complain.getComplainType() == 2) {
                //将申请进程打回待审核。。
                HouseFlowApply houseFlowApply = houseFlowApplyMapper.selectByPrimaryKey(complain.getBusinessId());
                houseFlowApply.setMemberCheck(0);
                houseFlowApply.setModifyDate(new Date());
                houseFlowApplyMapper.updateByPrimaryKeySelective(houseFlowApply);
            }
            if (complain.getComplainType() != null && complain.getComplainType() == 5) {
                House house = houseMapper.selectByPrimaryKey(complain.getHouseId());
                house.setVisitState(1);
                house.setModifyDate(new Date());
                houseMapper.updateByPrimaryKeySelective(house);
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
            } else if (complain.getComplainType() == 4) {//收货
                SplitDeliver splitDeliver = splitDeliverMapper.selectByPrimaryKey(complain.getBusinessId());
                House house = houseMapper.selectByPrimaryKey(complain.getHouseId());
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
                    Product product=forMasterAPI.getProduct(house.getCityId(), orderSplitItem.getProductId());
                    SplitDeliverItemDTO splitDeliverItemDTO = new SplitDeliverItemDTO();
                    splitDeliverItemDTO.setImage(address + product.getImage());
                    splitDeliverItemDTO.setProductSn(product.getProductSn());
                    splitDeliverItemDTO.setProductName(product.getName());
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
            } else if (complain.getComplainType() == 5) {
                Object date = getDate(complain.getHouseId());
                complain.setData(date);
            }
        }
        return ServerResponse.createBySuccess("查询成功", complain);
    }

    /**
     * 业主申请提前结束
     */
    public ServerResponse userStop(String houseId, String userToken, String content) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member member = (Member) object;
        House house = houseMapper.selectByPrimaryKey(houseId);
        if (house == null) {
            return ServerResponse.createByErrorMessage("没有查询到相关房子");
        }
        if (house.getVisitState() != 1 && house.getVisitState() != 5) {
            return ServerResponse.createByErrorMessage("该房子没有在装修，无法结束装修");
        }
        if (!member.getId().equals(house.getMemberId())) {
            return ServerResponse.createByErrorMessage("您无权操作此房产");
        }
        Complain complain = new Complain();
        complain.setHouseId(houseId);
        complain.setMemberId(member.getId());
        complain.setComplainType(5);
        complain.setContent(content);
        complain.setStatus(0);
        complain.setUserNickName(member.getNickName());
        complain.setUserName(member.getName());
        complain.setUserMobile(member.getMobile());
        complain.setUserId(member.getId());
        complainMapper.insert(complain);
        house.setVisitState(5);
        house.setModifyDate(new Date());
        houseMapper.updateByPrimaryKeySelective(house);

//        //添加一条记录
//        HouseFlowApply hfa = new HouseFlowApply();//发起申请任务
//        hfa.setWorkerId(member.getId());//工人id
//        hfa.setHouseId(houseId);//房子id
//        hfa.setApplyType(9);//申请类型0每日完工申请，1阶段完工申请，2整体完工申请,3停工申请，4：每日开工,5巡查,6无人巡查
//        hfa.setApplyMoney(new BigDecimal(0));//申请得钱
//        hfa.setSupervisorMoney(new BigDecimal(0));
//        hfa.setOtherMoney(new BigDecimal(0));
//        hfa.setMemberCheck(1);//业主审核状态0未审核，1审核通过，2审核不通过，3自动审核
//        hfa.setSupervisorCheck(1);//大管家审核状态0未审核，1审核通过，2审核不通过
//        hfa.setPayState(1);//是否付款
//        hfa.setApplyDec(content);//描述
//        houseFlowApplyMapper.insert(hfa);
//        houseService.insertConstructionRecord(hfa);
        return ServerResponse.createBySuccessMessage("申请成功");
    }

    /**
     * 中台提前停止装修页面
     */
    public ServerResponse adminStop(String houseId) {
        ComplainDTO complain = new ComplainDTO();
        Object date = getDate(houseId);
        House house = houseMapper.selectByPrimaryKey(houseId);
        Member member = memberMapper.selectByPrimaryKey(house.getMemberId());
        complain.setHouseName(house.getHouseName());
        complain.setMemberName(member.getName());
        complain.setMemberMobile(member.getMobile());
        complain.setData(date);
        return ServerResponse.createBySuccess("成功", complain);
    }

    private Object getDate(String houseId) {
        House house = houseMapper.selectByPrimaryKey(houseId);
        if (house == null) {
            return ServerResponse.createByErrorMessage("没有查询到相关房子");
        }
        List<ComPlainStopDTO> comPlainStopDTOList = new ArrayList<>();
        if (house.getDecorationType() != 2 && house.getDesignerOk() != 3) {
            Example examples = new Example(HouseFlow.class);
            examples.createCriteria().andEqualTo(HouseFlow.HOUSE_ID, house.getId())
                    .andEqualTo(HouseFlow.WORKER_TYPE, "1");
            List<HouseFlow> houseFlows = houseFlowMapper.selectByExample(examples);
            getComPlainStop(comPlainStopDTOList, houseFlows);
        }
        if (house.getBudgetOk() != 3) {
            Example examples = new Example(HouseFlow.class);
            examples.createCriteria().andEqualTo(HouseFlow.HOUSE_ID, house.getId())
                    .andEqualTo(HouseFlow.WORKER_TYPE, "2");
            List<HouseFlow> houseFlows = houseFlowMapper.selectByExample(examples);
            getComPlainStop(comPlainStopDTOList, houseFlows);
        }
        Example example = new Example(HouseFlow.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo(HouseFlow.HOUSE_ID, houseId);
        criteria.andGreaterThanOrEqualTo(HouseFlow.WORKER_TYPE, 3);
        criteria.andCondition(" work_steta not IN(0,2,6)");
        List<HouseFlow> houseFlows = houseFlowMapper.selectByExample(example);
        for (HouseFlow houseFlow : houseFlows) {
            WorkerType workerType = iWorkerTypeMapper.selectByPrimaryKey(houseFlow.getWorkerTypeId());
            HouseWorkerOrder hwo = houseWorkerOrderMapper.getByHouseIdAndWorkerTypeId(houseFlow.getHouseId(), houseFlow.getWorkerTypeId());
            BigDecimal workPrice = new BigDecimal(0);
            BigDecimal haveMoney = new BigDecimal(0);
            BigDecimal retentionMoney = new BigDecimal(0);
            BigDecimal deductPrice = new BigDecimal(0);
            if (hwo!=null) {
                workPrice = hwo.getWorkPrice();//工钱
                haveMoney = hwo.getHaveMoney();
                retentionMoney = hwo.getRetentionMoney() == null ? new BigDecimal(0) : hwo.getRetentionMoney();//滞留金
                deductPrice = hwo.getDeductPrice() == null ? new BigDecimal(0) : hwo.getDeductPrice();//评价积分扣除的钱
            }
            BigDecimal alsoMoney = new BigDecimal(workPrice.doubleValue() - haveMoney.doubleValue() - retentionMoney.doubleValue() - deductPrice.doubleValue());
            ComPlainStopDTO comPlainStopDTO = new ComPlainStopDTO();
            comPlainStopDTO.setHaveMoney(alsoMoney);
            comPlainStopDTO.setWorkerId(hwo.getWorkerId());
            comPlainStopDTO.setWorkerTypeId(houseFlow.getWorkerTypeId());
            comPlainStopDTO.setWorkerTypeName(workerType.getName());
            comPlainStopDTOList.add(comPlainStopDTO);
        }
        return comPlainStopDTOList;
    }

    private void getComPlainStop(List<ComPlainStopDTO> comPlainStopDTOList, List<HouseFlow> houseFlows) {
        if (houseFlows.size() > 0) {
            HouseFlow houseFlow = houseFlows.get(0);
            WorkerType workerType = iWorkerTypeMapper.selectByPrimaryKey(houseFlow.getWorkerTypeId());
            HouseWorkerOrder hwo = houseWorkerOrderMapper.getByHouseIdAndWorkerTypeId(houseFlow.getHouseId(), houseFlow.getWorkerTypeId());
            if (houseFlow.getWorkType() == 4 && hwo != null && workerType != null) {
                BigDecimal haveMoney = hwo.getHaveMoney();
                BigDecimal workPrice = hwo.getWorkPrice();
                BigDecimal subtract = workPrice.subtract(haveMoney);
                String workerId = hwo.getWorkerId();
                ComPlainStopDTO comPlainStopDTO = new ComPlainStopDTO();
                comPlainStopDTO.setHaveMoney(subtract);
                comPlainStopDTO.setWorkerId(workerId);
                comPlainStopDTO.setWorkerTypeId(houseFlow.getWorkerTypeId());
                comPlainStopDTO.setWorkerTypeName(workerType.getName());
                comPlainStopDTOList.add(comPlainStopDTO);
            }
        }
    }

    /**
     * 中台提前停止装修提交
     *
     * @return
     */
    public ServerResponse updateAdminStop(String jsonStr, String content, String houseId) {
        //讲房子状态改为提前竣工
        House house = houseMapper.selectByPrimaryKey(houseId);
        if (house == null) {
            return ServerResponse.createByErrorMessage("没有查询到相关房子");
        }
        if (house.getVisitState() != 1 && house.getVisitState() != 5) {
            return ServerResponse.createByErrorMessage("该房子没有在装修，无法结束装修");
        }
        JSONArray brandSeriesLists = JSONArray.parseArray(jsonStr);
        List<Map<String, Object>> maps = new ArrayList<>();
        if (backMoneyJudge(brandSeriesLists, maps))
            return ServerResponse.createByErrorMessage("传入退款金额有误，请确认无误后提交");
        for (Map<String, Object> map : maps) {
            commitStopBuild(map.get("workerId"),
                    new BigDecimal((Double) map.get("backMoney")),
                    new BigDecimal((Double) map.get("haveMoney")), houseId,
                    "中台提前结束装修，原因为" + content);
        }
        setHouseFlowsWorkSteta(house.getId());
        house.setModifyDate(new Date());
        house.setCompletedDate(new Date());
        house.setVisitState(4);
        house.setHaveComplete(1);
//        house.setDesignerOk(3);
//        house.setBudgetOk(3);
        houseMapper.updateByPrimaryKeySelective(house);
        return ServerResponse.createBySuccessMessage("ok");
    }

    /**
     * 精算设计提前结束
     *
     * @return
     */
    public ServerResponse commitStop(String backMoney, String content, String userToken, String userId, String houseId) {
        House house = houseMapper.selectByPrimaryKey(houseId);
        if (house == null) {
            return ServerResponse.createByErrorMessage("没有查询到相关房子");
        }
        if (house.getVisitState() != 1 && house.getVisitState() != 5) {
            return ServerResponse.createByErrorMessage("该房子没有在装修，无法结束装修");
        }
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse && CommonUtil.isEmpty(userId)) {
            return ServerResponse.createbyUserTokenError();
        }
        HouseWorkerOrder hwo2 = null;
        if (house.getDecorationType() != 2 && house.getDesignerOk() != 3) {
            Example examples = new Example(HouseFlow.class);
            examples.createCriteria().andEqualTo(HouseFlow.HOUSE_ID, house.getId())
                    .andEqualTo(HouseFlow.WORKER_TYPE, "1");
            List<HouseFlow> houseFlows = houseFlowMapper.selectByExample(examples);
            if (houseFlows.size() > 0) {
                HouseFlow houseFlow = houseFlows.get(0);
                if (houseFlow.getWorkType() == 4) {
                    hwo2 = houseWorkerOrderMapper.getByHouseIdAndWorkerTypeId(houseFlow.getHouseId(), houseFlow.getWorkerTypeId());
                }
            }
        } else if (house.getBudgetOk() != 3) {
            Example examples = new Example(HouseFlow.class);
            examples.createCriteria().andEqualTo(HouseFlow.HOUSE_ID, house.getId())
                    .andEqualTo(HouseFlow.WORKER_TYPE, "2");
            List<HouseFlow> houseFlows = houseFlowMapper.selectByExample(examples);
            if (houseFlows.size() > 0) {
                HouseFlow houseFlow = houseFlows.get(0);
                if (houseFlow.getWorkType() == 4) {
                    hwo2 = houseWorkerOrderMapper.getByHouseIdAndWorkerTypeId(houseFlow.getHouseId(), houseFlow.getWorkerTypeId());
                }
            }
        }
        if (hwo2 == null) {
            return ServerResponse.createByErrorMessage("暂无工匠");
        }
        BigDecimal subtract = hwo2.getWorkPrice().subtract(hwo2.getHaveMoney());
        if (Double.parseDouble(backMoney) > subtract.doubleValue() || Double.parseDouble(backMoney) <= 0) {
            return ServerResponse.createByErrorMessage("退款金额有误，请重新输入!");
        }
        setHouseFlowsWorkSteta(house.getId());
        commitStopBuild(hwo2.getWorkerId(), new BigDecimal(backMoney), subtract, houseId, content);
        house.setModifyDate(new Date());
        house.setCompletedDate(new Date());
        house.setVisitState(4);
        house.setHaveComplete(1);
//        house.setDesignerOk(3);
//        house.setBudgetOk(3);
        houseMapper.updateByPrimaryKeySelective(house);
        return ServerResponse.createBySuccessMessage("操作成功");
    }

    private boolean backMoneyJudge(JSONArray brandSeriesLists, List<Map<String, Object>> maps) {
        for (int i = 0; i < brandSeriesLists.size(); i++) {
            JSONObject jsonObject = brandSeriesLists.getJSONObject(i);
            // 施工工人ID
            Object workerId = jsonObject.get("id");
            //获取退多少钱
            Object backMoney1 = jsonObject.get("backMoney");
            //可还得工钱
            Object haveMoney1 = jsonObject.get("haveMoney");
            Double backMoney;
            try {
                backMoney = Double.valueOf(backMoney1.toString());
            } catch (Exception e) {
                backMoney = 0d;
            }
            Double haveMoney;
            try {
                haveMoney = Double.valueOf(haveMoney1.toString());
            } catch (Exception e) {
                haveMoney = 0d;
            }
            if (backMoney < 0 || backMoney > haveMoney) {
                return true;
            }
            Map<String, Object> map = new HashMap<>();
            map.put("workerId", workerId);
            map.put("backMoney", backMoney);
            map.put("haveMoney", haveMoney);
            maps.add(map);
        }
        return false;
    }

    private void setHouseFlowsWorkSteta(String houseId) {
        Example example = new Example(HouseFlow.class);
        example.createCriteria()
                .andEqualTo(HouseFlow.HOUSE_ID, houseId)
                .andEqualTo(HouseFlow.WORK_STETA, 0);
        List<HouseFlow> houseFlows = houseFlowMapper.selectByExample(example);
        for (HouseFlow houseFlow : houseFlows) {
            houseFlow.setWorkType(1);//把下一个工种弄成未发布
            houseFlow.setReleaseTime(new Date());
            houseFlow.setWorkSteta(6);
            houseFlowMapper.updateByPrimaryKeySelective(houseFlow);
            //删除质保卡
            WorkerTypeSafeOrder wtso = workerTypeSafeOrderMapper.getByWorkerTypeId(houseFlow.getWorkerTypeId(), houseFlow.getHouseId());
            if (wtso != null) {
                wtso.setDataStatus(1);
                workerTypeSafeOrderMapper.updateByPrimaryKey(wtso);
            }
            if (!CommonUtil.isEmpty(houseFlow.getWorkerId())) {
                HouseFlowApply hfa = new HouseFlowApply();//发起申请任务
                hfa.setHouseFlowId(houseFlow.getId());//工序id
                hfa.setWorkerId(houseFlow.getWorkerId());//工人id
                hfa.setOperator(houseFlow.getWorkerId());
                hfa.setWorkerTypeId(houseFlow.getWorkerTypeId());//工种id
                hfa.setWorkerType(houseFlow.getWorkerType());//工种类型
                hfa.setHouseId(houseFlow.getHouseId());//房子id
                hfa.setApplyType(8);//申请类型0每日完工申请，1阶段完工申请，2整体完工申请,3停工申请，4：每日开工,5巡查,6无人巡查
                hfa.setApplyMoney(new BigDecimal(0));//申请得钱
                hfa.setSupervisorMoney(new BigDecimal(0));
                hfa.setOtherMoney(new BigDecimal(0));
                hfa.setMemberCheck(1);//业主审核状态0未审核，1审核通过，2审核不通过，3自动审核
                hfa.setSupervisorCheck(1);//大管家审核状态0未审核，1审核通过，2审核不通过
                hfa.setPayState(1);//是否付款
                hfa.setApplyDec("提前结束装修");//描述
                houseFlowApplyMapper.insert(hfa);
                houseService.insertConstructionRecord(hfa);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void commitStopBuild(Object workerId, BigDecimal backMoney, BigDecimal haveMoney, String houseId, String content) {
        Example example1 = new Example(HouseWorkerOrder.class);
        example1.createCriteria()
                .andEqualTo(HouseWorkerOrder.HOUSE_ID, houseId)
                .andEqualTo(HouseWorkerOrder.WORKER_ID, workerId);
        List<HouseWorkerOrder> houseWorkerOrderList = houseWorkerOrderMapper.selectByExample(example1);
        //退钱了工匠还可得
        BigDecimal subtract1 = haveMoney.subtract(backMoney);
        BigDecimal add = houseWorkerOrderList.get(0).getHaveMoney().add(subtract1);
        BigDecimal subtract = houseWorkerOrderList.get(0).getWorkPrice().subtract(backMoney);
        houseWorkerOrderList.get(0).setHaveMoney(add);
        houseWorkerOrderList.get(0).setWorkPrice(subtract);
        houseWorkerOrderMapper.updateByPrimaryKeySelective(houseWorkerOrderList.get(0));
        Example example2 = new Example(HouseFlow.class);
        example2.createCriteria()
                .andEqualTo(HouseFlow.HOUSE_ID, houseId)
                .andEqualTo(HouseFlow.WORKER_ID, workerId);
        List<HouseFlow> houseFlows = houseFlowMapper.selectByExample(example2);
        if (houseFlows.size() > 0) {
            //设置当前工序为提前竣工
            houseFlows.get(0).setWorkSteta(6);
            houseFlows.get(0).setWorkPrice(subtract);
            houseFlowMapper.updateByPrimaryKeySelective(houseFlows.get(0));

            //删除质保卡
            WorkerTypeSafeOrder wtso = workerTypeSafeOrderMapper.getByWorkerTypeId(houseFlows.get(0).getWorkerTypeId(), houseFlows.get(0).getHouseId());
            if (wtso != null) {
                wtso.setDataStatus(1);
                workerTypeSafeOrderMapper.updateByPrimaryKey(wtso);
            }
            //添加一条记录
            HouseFlow houseFlow = houseFlows.get(0);
            HouseFlowApply hfa = new HouseFlowApply();//发起申请任务
            hfa.setHouseFlowId(houseFlow.getId());//工序id
            hfa.setWorkerId(houseFlow.getWorkerId());//工人id
            hfa.setOperator(houseFlow.getWorkerId());
            hfa.setWorkerTypeId(houseFlow.getWorkerTypeId());//工种id
            hfa.setWorkerType(houseFlow.getWorkerType());//工种类型
            hfa.setHouseId(houseFlow.getHouseId());//房子id
            hfa.setApplyType(8);//申请类型0每日完工申请，1阶段完工申请，2整体完工申请,3停工申请，4：每日开工,5巡查,6无人巡查
            hfa.setApplyMoney(subtract1);//申请得钱
            hfa.setSupervisorMoney(new BigDecimal(0));
            hfa.setOtherMoney(new BigDecimal(0));
            hfa.setMemberCheck(1);//业主审核状态0未审核，1审核通过，2审核不通过，3自动审核
            hfa.setSupervisorCheck(1);//大管家审核状态0未审核，1审核通过，2审核不通过
            hfa.setPayState(1);//是否付款
            hfa.setApplyDec(content);//描述
            houseFlowApplyMapper.insert(hfa);
            houseService.insertConstructionRecord(hfa);
        }
        //工匠加流水记录
        Member member1 = memberMapper.selectByPrimaryKey(workerId);
        WorkerDetail workerDetail = new WorkerDetail();
        workerDetail.setName(content);
        workerDetail.setWorkerId(workerId.toString());
        workerDetail.setWorkerName(member1.getName());
        workerDetail.setHouseId(houseId);
        workerDetail.setMoney(subtract1);
        workerDetail.setState(0);
        workerDetail.setHaveMoney(houseWorkerOrderList.get(0).getHaveMoney());
        workerDetail.setHouseWorkerOrderId(houseWorkerOrderList.get(0).getId());
        workerDetail.setApplyMoney(subtract1);
        workerDetail.setWalletMoney(member1.getHaveMoney());
        iWorkerDetailMapper.insert(workerDetail);
        //工匠钱包更新
        member1.setWorkerPrice(member1.getWorkerPrice().add(subtract1));
        member1.setHaveMoney(member1.getHaveMoney().add(subtract1));
        member1.setSurplusMoney(member1.getSurplusMoney().add(subtract1));
        member1.setModifyDate(new Date());
        memberMapper.updateByPrimaryKeySelective(member1);
        //业主钱包更新
        House house = houseMapper.selectByPrimaryKey(houseId);
        Member member = memberMapper.selectByPrimaryKey(house.getMemberId());
        member.setHaveMoney(member.getHaveMoney().add(backMoney));
        member.setSurplusMoney(member.getSurplusMoney().add(backMoney));
        member.setModifyDate(new Date());
        memberMapper.updateByPrimaryKeySelective(member);
        //业主加流水记录
        WorkerDetail workerDetail1 = new WorkerDetail();
        workerDetail1.setName(content);
        workerDetail1.setWorkerId(member.getId());
        workerDetail1.setWorkerName(member.getNickName());
        workerDetail1.setHouseId(houseId);
        workerDetail1.setMoney(backMoney);
        workerDetail1.setState(9);
        workerDetail1.setHaveMoney(new BigDecimal(0));
        workerDetail1.setHouseWorkerOrderId(houseWorkerOrderList.get(0).getId());
        workerDetail1.setApplyMoney(backMoney);
        workerDetail1.setWalletMoney(member.getHaveMoney());
        iWorkerDetailMapper.insert(workerDetail1);
    }
}

