package com.dangjia.acg.service.house;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.actuary.BudgetWorkerAPI;
import com.dangjia.acg.api.data.ForMasterAPI;
import com.dangjia.acg.auth.config.RedisSessionDAO;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.enums.AppType;
import com.dangjia.acg.common.exception.BaseException;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.BaseEntity;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.common.util.JsmsUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.core.HouseFlowDTO;
import com.dangjia.acg.dto.design.QuantityRoomDTO;
import com.dangjia.acg.dto.house.*;
import com.dangjia.acg.dto.repair.HouseProfitSummaryDTO;
import com.dangjia.acg.mapper.clue.ClueMapper;
import com.dangjia.acg.mapper.core.*;
import com.dangjia.acg.mapper.house.*;
import com.dangjia.acg.mapper.matter.IRenovationManualMapper;
import com.dangjia.acg.mapper.matter.IRenovationManualMemberMapper;
import com.dangjia.acg.mapper.matter.ITechnologyRecordMapper;
import com.dangjia.acg.mapper.member.ICustomerMapper;
import com.dangjia.acg.mapper.member.IMemberCityMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.other.ICityMapper;
import com.dangjia.acg.mapper.other.IWorkDepositMapper;
import com.dangjia.acg.mapper.repair.IMendOrderMapper;
import com.dangjia.acg.mapper.sale.*;
import com.dangjia.acg.mapper.worker.IWorkerDetailMapper;
import com.dangjia.acg.modle.clue.Clue;
import com.dangjia.acg.modle.core.*;
import com.dangjia.acg.modle.design.QuantityRoomImages;
import com.dangjia.acg.modle.house.*;
import com.dangjia.acg.modle.matter.RenovationManual;
import com.dangjia.acg.modle.matter.RenovationManualMember;
import com.dangjia.acg.modle.matter.TechnologyRecord;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.member.Customer;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.member.MemberCity;
import com.dangjia.acg.modle.other.City;
import com.dangjia.acg.modle.other.WorkDeposit;
import com.dangjia.acg.modle.repair.ChangeOrder;
import com.dangjia.acg.modle.repair.MendOrder;
import com.dangjia.acg.modle.sale.residential.ResidentialBuilding;
import com.dangjia.acg.modle.sale.residential.ResidentialRange;
import com.dangjia.acg.modle.sale.royalty.DjAlreadyRobSingle;
import com.dangjia.acg.modle.sale.royalty.DjRoyaltyDetailsSurface;
import com.dangjia.acg.modle.sale.royalty.DjRoyaltyMatch;
import com.dangjia.acg.modle.worker.WorkerDetail;
import com.dangjia.acg.service.config.ConfigMessageService;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.dangjia.acg.service.core.HouseFlowService;
import com.dangjia.acg.service.design.DesignDataService;
import com.dangjia.acg.util.Utils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

/**
 * author: Ronalcheng
 * Date: 2018/11/1 0001
 * Time: 17:56
 */
@Service
public class HouseService {
    @Autowired
    private IHouseMapper iHouseMapper;
    @Autowired
    private ICityMapper iCityMapper;
    @Autowired
    private IMemberCityMapper memberCityMapper;
    @Autowired
    private IHouseFlowMapper houseFlowMapper;
    @Autowired
    private IWorkerTypeMapper workerTypeMapper;
    @Autowired
    private IModelingLayoutMapper modelingLayoutMapper;
    @Autowired
    private IModelingVillageMapper modelingVillageMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IRenovationManualMapper renovationManualMapper;
    @Autowired
    private IHouseAddressMapper iHouseAddressMapper;
    @Autowired
    private IRenovationManualMemberMapper renovationManualMemberMapper;
    @Autowired
    private IHouseFlowApplyMapper houseFlowApplyMapper;
    @Autowired
    private IMemberMapper memberMapper;
    @Autowired
    private IHouseWorkerMapper houseWorkerMapper;
    @Autowired
    private IHouseFlowApplyImageMapper houseFlowApplyImageMapper;
    @Autowired
    private ForMasterAPI forMasterAPI;
    @Autowired
    private IHouseWorkerOrderMapper houseWorkerOrderMapper;
    @Autowired
    private IWorkerDetailMapper workerDetailMapper;
    @Autowired
    private BudgetWorkerAPI budgetWorkerAPI;
    @Autowired
    private ConfigMessageService configMessageService;
    @Autowired
    private IHouseExpendMapper houseExpendMapper;
    @Autowired
    private ITechnologyRecordMapper technologyRecordMapper;
    @Autowired
    private ICustomerMapper iCustomerMapper;
    @Autowired
    private HouseChoiceCaseService houseChoiceCaseService;
    @Autowired
    private HouseFlowService houseFlowService;
    @Autowired
    private IMendOrderMapper mendOrderMapper;
    @Autowired
    private DesignDataService designDataService;
    @Autowired
    private HouseConstructionRecordMapper houseConstructionRecordMapper;
    @Autowired
    private CraftsmanConstructionService constructionService;
    @Autowired
    private IHouseChoiceCaseMapper iHouseChoiceCaseMapper;
    @Autowired
    private IWorkDepositMapper workDepositMapper;
    @Autowired
    private MyHouseService myHouseService;
    @Autowired
    private ClueMapper clueMapper;
    @Autowired
    private IModelingVillageMapper iModelingVillageMapper;
    @Autowired
    private ResidentialRangeMapper residentialRangeMapper;
    @Autowired
    private ResidentialBuildingMapper residentialBuildingMapper;
    @Autowired
    private DjAlreadyRobSingleMapper djAlreadyRobSingleMapper;
    @Autowired
    private RoyaltyMapper royaltyMapper;
    @Autowired
    private DjRoyaltyMatchMapper djRoyaltyMatchMapper;

    protected static final Logger LOG = LoggerFactory.getLogger(HouseService.class);

    /**
     * 切换房产
     */
    public ServerResponse setSelectHouse(String userToken, String houseId) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member member = (Member) object;
        Example example = new Example(House.class);
        example.createCriteria()
                .andEqualTo(House.MEMBER_ID, member.getId())
                .andEqualTo(House.DATA_STATUS, 0);
        List<House> houseList = iHouseMapper.selectByExample(example);
        for (House house : houseList) {
            if (house.getId().equals(houseId)) {
                house.setIsSelect(1);//改为选择
            } else {
                house.setIsSelect(0);
            }
            iHouseMapper.updateByPrimaryKeySelective(house);
        }
        return ServerResponse.createBySuccessMessage("操作成功");
    }

    /**
     * 获取我的房产
     *
     * @param pageDTO
     * @param userToken
     * @return
     */
    public ServerResponse getMyHouseList(PageDTO pageDTO, String userToken) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member worker = (Member) object;
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<House> houseList = iHouseMapper.selectByExample(myHouseService.getHouseExample(worker.getId()));
        if (houseList.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), "暂无房产");
        }
        PageInfo pageResult = new PageInfo(houseList);
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (House house : houseList) {
            Map<String, Object> map = new HashMap<>();
            map.put("houseId", house.getId());
            map.put("houseName", house.getHouseName());
            map.put("visitState", house.getVisitState());
            map.put("task", this.getTask(house.getId()));
            String webAddress = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class);
            switch (house.getVisitState()) {
                case 0:
                    map.put("btName", "待确认开工");
                case 1:
                    map.put("btName", "申请结束装修");
                    map.put("onclick", webAddress + "ownerEnd?title=填写原因&houseId=" + house.getId());
                    break;
                case 2:
                    map.put("btName", "休眠中");
                    break;
                case 3:
                    map.put("btName", "已竣工");
                    break;
                case 4:
                    map.put("btName", "提前结束装修");
                    break;
                case 5:
                    map.put("btName", "审核中");
                    break;
            }
            mapList.add(map);
        }
        pageResult.setList(mapList);
        return ServerResponse.createBySuccess("查询成功", pageResult);
    }

    /**
     * 待处理任务
     */
    private int getTask(String houseId) {
        int task;
        //查询待支付工序
        Example example = new Example(HouseFlow.class);
        example.createCriteria().andEqualTo("workType", 3).andEqualTo("houseId", houseId);
        List<HouseFlow> houseFlowList = houseFlowMapper.selectByExample(example);
        task = houseFlowList.size();

        House house = iHouseMapper.selectByPrimaryKey(houseId);

        example = new Example(MendOrder.class);
        example.createCriteria().andEqualTo(MendOrder.HOUSE_ID, houseId).andEqualTo(MendOrder.TYPE, 0)
                .andEqualTo(MendOrder.STATE, 3);//补材料审核状态全通过
        List<MendOrder> mendOrderList = mendOrderMapper.selectByExample(example);
        task += mendOrderList.size();

        example = new Example(MendOrder.class);
        example.createCriteria().andEqualTo(MendOrder.HOUSE_ID, houseId).andEqualTo(MendOrder.TYPE, 1)
                .andEqualTo(MendOrder.STATE, 3);//审核状态
        mendOrderList = mendOrderMapper.selectByExample(example);
        task += mendOrderList.size();
        if (house.getDesignerOk() == 5 || house.getDesignerOk() == 2) {
            task++;
        }
        if (house.getBudgetOk() == 2) {
            task++;
        }
        //验收任务
        List<HouseFlowApply> houseFlowApplyList = houseFlowApplyMapper.getMemberCheckList(houseId);
        task += houseFlowApplyList.size();

        return task;
    }

    /**
     * 我的房子
     */
    public ServerResponse queryMyHouse(String userToken) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member member = (Member) object;
        Example example = new Example(House.class);
        example.createCriteria()
                .andEqualTo(House.MEMBER_ID, member.getId())
                .andNotEqualTo(House.VISIT_STATE, 0).andNotEqualTo(House.VISIT_STATE, 2)
                .andEqualTo(House.DATA_STATUS, 0);
        List<House> houseList = iHouseMapper.selectByExample(example);
        List<Map> mapList = new ArrayList<>();
        for (House house : houseList) {
            Map map = BeanUtils.beanToMap(house);
            map.put("houseName", house.getHouseName());
            mapList.add(map);
        }
        return ServerResponse.createBySuccess("查询成功", mapList);
    }


    /**
     * 开工页面
     */
    public ServerResponse startWorkPage(HttpServletRequest request, String houseId) {
        HouseDTO houseDTO = iHouseMapper.startWorkPage(houseId);
        if (StringUtil.isNotEmpty(houseDTO.getReferHouseId())) {
            House house = iHouseMapper.selectByPrimaryKey(houseDTO.getReferHouseId());
            if (house != null)
                houseDTO.setReferHouseName(house.getHouseName());
        }
        return ServerResponse.createBySuccess("查询成功", houseDTO);
    }

    /**
     * 修改房子工序顺序以及选配标签
     */
    public ServerResponse setHouseInfo(House house) {
        try {
            House srcHouse = iHouseMapper.selectByPrimaryKey(house.getId());
            if (srcHouse == null) {
                srcHouse = iHouseMapper.selectByPrimaryKey(house.getHouseId());
            }
            if (!CommonUtil.isEmpty(house.getCustomSort()) && !house.getCustomSort().equals("ignore")) {
                LOG.info("setHouseInfo getCustomSort:" + house.getCustomSort());
                if (StringUtils.isNoneBlank(house.getCustomSort())
                        && StringUtils.isNoneBlank(srcHouse.getCustomSort())) {//如果不问null ，说明已经排序过，就是修改顺序
                    String[] oldWorkerTypeArr = srcHouse.getCustomSort().split(",");
                    String[] newWorkerTypeArr = house.getCustomSort().split(",");
                    LOG.info("setHouseInfo old getCustomSort:" + srcHouse.getCustomSort());
                    Set<String> setNew = new HashSet<>(Arrays.asList(newWorkerTypeArr));//新修改的工序类型 的集合
                    Set<String> setDelete = new HashSet<>();//找出被删除的 工序类型 的集合
                    Set<String> setUpdate = new HashSet<>();//找出要修改的 工序类型 的集合
                    for (String oldWorkerType : oldWorkerTypeArr) {//找出老的，要么有修改，要么有删除的
                        if (setNew.contains(oldWorkerType)) {
                            setUpdate.add(oldWorkerType);//找出要修改的 工序类型
                        } else
                            setDelete.add(oldWorkerType);//找出被删除的 工序类型
                    }
                    LOG.info("删除前:" + setDelete + "  " + setDelete.size());
                    for (String deleteWorkerType : setDelete) {//删除工序
                        HouseFlow oldHouseFlow = houseFlowMapper.getHouseFlowByHidAndWty(house.getId(), Integer.parseInt(deleteWorkerType));
//                        LOG.info("oldHouseFlow 删除前:" + oldHouseFlow + "  " + deleteWorkerType);
                        if (oldHouseFlow != null) {
                            int ret = houseFlowMapper.deleteByPrimaryKey(oldHouseFlow.getId());
                            if (ret >= 1) {
                                LOG.info("setDelete 删除成功:" + deleteWorkerType);
                            }
                        }
                    }
                    for (String updateWorkerType : setUpdate) {//删除工序
                        HouseFlow oldHouseFlow = houseFlowMapper.getHouseFlowByHidAndWty(house.getId(), Integer.parseInt(updateWorkerType));
                        LOG.info("setHouseInfo updateWorkerType：：" + updateWorkerType + " " + oldHouseFlow);
                        if (oldHouseFlow != null) {
                            int sortIndex = houseFlowService.getCustomSortIndex(house.getCustomSort(), updateWorkerType);
//                            LOG.info("新排序：" + house.getCustomSort() + " oldWorkerType：" + updateWorkerType + " sortIndex:" + sortIndex);
//                            LOG.info("oldHouseFlow 修改前:" + oldHouseFlow);
                            if (sortIndex != -1 && oldHouseFlow.getSort() != sortIndex) {
                                oldHouseFlow.setSort(sortIndex);
                                oldHouseFlow.setModifyDate(new Date());
                                int ret = houseFlowMapper.updateByPrimaryKeySelective(oldHouseFlow);
                                if (ret >= 1) {
                                    LOG.info("oldHouseFlow 修改成功:" + oldHouseFlow);
                                }
                            }
                        }

                    }
                }
                srcHouse.setCustomSort(house.getCustomSort());
            }
            srcHouse.setOptionalLabel(house.getOptionalLabel());
            srcHouse.setCustomEdit(house.getCustomEdit());
            iHouseMapper.updateByPrimaryKeySelective(srcHouse);
            return ServerResponse.createBySuccessMessage("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }


    /**
     * 修改房子状态
     *
     * @param house
     * @return
     */
    public ServerResponse setHouseState(House house) {
        try {
            House srcHouse = iHouseMapper.selectByPrimaryKey(house.getId());
            if (!CommonUtil.isEmpty(house.getSiteDisplay())) {
                srcHouse.setSiteDisplay(house.getSiteDisplay());
            }
            if (!CommonUtil.isEmpty(house.getShowHouse())) {
                srcHouse.setShowHouse(house.getShowHouse());
                if (house.getShowHouse() == 1) {
                    HouseChoiceCase houseChoiceCase = new HouseChoiceCase();
                    houseChoiceCase.setDataStatus(0);
                    houseChoiceCase.setCityId(srcHouse.getCityId());
                    houseChoiceCase.setHouseId(srcHouse.getId());
                    houseChoiceCase.setMoney(srcHouse.getMoney());
                    houseChoiceCase.setTitle(srcHouse.getNoNumberHouseName());
                    houseChoiceCase.setLabel(srcHouse.getStyle());
                    houseChoiceCase.setSource("房源来自当家装修精选推荐");
                    houseChoiceCaseService.addHouseChoiceCase(houseChoiceCase);
                } else {
                    houseChoiceCaseService.delHouseChoiceCase(house.getId());
                }
            }
            if (!CommonUtil.isEmpty(house.getVisitState())) {
                srcHouse.setVisitState(house.getVisitState());
            }
            iHouseMapper.updateByPrimaryKeySelective(srcHouse);
            return ServerResponse.createBySuccessMessage("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }


    /**
     * WEB确认开工
     */
    public ServerResponse startWork(HttpServletRequest request, HouseDTO houseDTO,String userToken) {

        if (houseDTO.getDecorationType() >= 3 || houseDTO.getDecorationType() == 0) {
            return ServerResponse.createByErrorMessage("装修类型参数错误");
        }
        if (StringUtils.isEmpty(houseDTO.getHouseId()) || StringUtils.isEmpty(houseDTO.getVillageId())) {
            return ServerResponse.createByErrorMessage("参数为空");
        }
        if (houseDTO.getSquare() <= 0) {
            return ServerResponse.createByErrorMessage("面积错误");
        }
        House house = iHouseMapper.selectByPrimaryKey(houseDTO.getHouseId());
        if (house == null) {
            return ServerResponse.createByErrorMessage("该房产不存在");
        }
        house.setBuildSquare(new BigDecimal(houseDTO.getBuildSquare()));//建筑面积
        if (StringUtils.isEmpty(houseDTO.getCityId())) {
            house.setCityId(iModelingVillageMapper.selectByPrimaryKey(houseDTO.getVillageId()).getCityId());
        } else {
            house.setCityId(houseDTO.getCityId());
        }
        house.setCityName(houseDTO.getCityName());
        house.setVillageId(houseDTO.getVillageId());
        house.setResidential(houseDTO.getResidential());
        house.setBuilding(houseDTO.getBuilding());
        house.setUnit(houseDTO.getUnit());
        house.setNumber(houseDTO.getNumber());
        house.setSquare(new BigDecimal(houseDTO.getSquare()));
        house.setReferHouseId(houseDTO.getReferHouseId());
        house.setStyle(houseDTO.getStyle());
        house.setStyleId(houseDTO.getStyleId());
        house.setHouseType(houseDTO.getHouseType());
        house.setDrawings(houseDTO.getDrawings());
        house.setDecorationType(houseDTO.getDecorationType());
        HouseFlow houseFlow;
        try {
            //修改-自带设计和远程设计都需要进行抢单
            if (house.getDecorationType() == 2) {//自带设计,上传施工图先
                WorkerType workerType = workerTypeMapper.selectByPrimaryKey("2");
                Example example = new Example(HouseFlow.class);
                example.createCriteria().andEqualTo(HouseFlow.HOUSE_ID, house.getId()).andEqualTo(HouseFlow.WORKER_TYPE_ID, workerType.getId());
                List<HouseFlow> houseFlowList = houseFlowMapper.selectByExample(example);
                if (houseFlowList.size() > 0) {
//                    return ServerResponse.createByErrorMessage("生成精算houseFlow异常");
                } else {
                    houseFlow = new HouseFlow(true);
                    houseFlow.setCityId(house.getCityId());
                    houseFlow.setWorkerTypeId(workerType.getId());
                    houseFlow.setWorkerType(workerType.getType());
                    houseFlow.setHouseId(house.getId());
                    houseFlow.setState(workerType.getState());
                    houseFlow.setSort(workerType.getSort());
                    houseFlow.setWorkType(2);//设置可抢单
                    //这里算出精算费
                    WorkDeposit workDeposit = workDepositMapper.selectByPrimaryKey(house.getWorkDepositId());//结算比例表
                    houseFlow.setWorkPrice(house.getSquare().multiply(workDeposit.getBudgetCost()));
                    houseFlowMapper.insert(houseFlow);
                }
                house.setDesignerOk(1);
            } else {//远程设计
                WorkerType workerType = workerTypeMapper.selectByPrimaryKey("1");
                Example example = new Example(HouseFlow.class);
                example.createCriteria()
                        .andEqualTo(HouseFlow.HOUSE_ID, houseDTO.getHouseId())
                        .andEqualTo(HouseFlow.WORKER_TYPE_ID, workerType.getId());
                List<HouseFlow> houseFlowList = houseFlowMapper.selectByExample(example);
                if (houseFlowList.size() > 1) {
                    return ServerResponse.createByErrorMessage("设计异常,请联系平台部");
                } else if (houseFlowList.size() == 1) {
                    houseFlow = houseFlowList.get(0);
                    houseFlow.setReleaseTime(new Date());//发布时间
                    houseFlow.setState(workerType.getState());
                    houseFlow.setSort(workerType.getSort());
                    houseFlow.setWorkType(2);//开始设计等待被抢
                    houseFlow.setCityId(house.getCityId());
                    houseFlowMapper.updateByPrimaryKeySelective(houseFlow);
                } else {
                    houseFlow = new HouseFlow(true);
                    houseFlow.setCityId(house.getCityId());
                    houseFlow.setReleaseTime(new Date());//发布时间
                    houseFlow.setWorkerTypeId(workerType.getId());
                    houseFlow.setWorkerType(workerType.getType());
                    houseFlow.setHouseId(house.getId());
                    houseFlow.setState(workerType.getState());
                    houseFlow.setSort(workerType.getSort());
                    houseFlow.setWorkType(2);//开始设计等待被抢
                    houseFlow.setCityId(house.getCityId());
                    houseFlowMapper.insert(houseFlow);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
        house.setVisitState(1);//开工成单
        house.setIsRobStats(1);
        iHouseMapper.updateByPrimaryKeySelective(house);

        //确认开工后，要修改 业主客服阶段 为已下单
        Customer customer = iCustomerMapper.getCustomerByMemberId(house.getMemberId());
        customer.setStage(4);//阶段: 0未跟进,1继续跟进,2放弃跟进,3黑名单,4已下单
        customer.setPhaseStatus(1);
        iCustomerMapper.updateByPrimaryKeySelective(customer);
        Map<String, Object> map = new HashedMap();
        map.put("memberId", customer.getMemberId());
        map.put("userId", customer.getUserId());
        map.put("stage", 4);
        map.put("tips", 1);
        clueMapper.setStage(map);//修改线索的阶段



        //结算提成
        Object object = constructionService.getAccessToken(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        AccessToken accessToken = (AccessToken) object;
        if (CommonUtil.isEmpty(accessToken.getUserId())) {
            return ServerResponse.createbyUserTokenError();
        }

        //修改以抢单列表信息
        Map<String,Object> mm = new HashMap<>();
        mm.put("dataStatus",1);
        mm.put("houseId",houseDTO.getHouseId());
        djAlreadyRobSingleMapper.upDateDataStatus(mm);

        /**
         * 结算下单提成
         */
        endRoyalty(houseDTO,accessToken.getUserId());

        try {
            List<Customer> ms = iCustomerMapper.getCustomerMemberIdList(house.getMemberId());
            if (ms != null) {
                String url = configUtil.getValue(SysConfig.PUBLIC_SALE_APP_ADDRESS, String.class);
                for (Customer m : ms) {
                    configMessageService.addConfigMessage(AppType.SALE, m.getMemberId(), "开工提醒",
                            "您有已确认开工的客户【" + house.getHouseName() + "】", 0, url
                                    + Utils.getCustomerDetails(house.getMemberId(), "", 1, "4"));
                }
            }
            //通知业主确认开工
            configMessageService.addConfigMessage(request, AppType.ZHUANGXIU, house.getMemberId(), "0", "装修提醒",
                    String.format(DjConstants.PushMessage.START_FITTING_UP, house.getHouseName()), "");
            //通知设计师/精算师/大管家 抢单
            Example example = new Example(WorkerType.class);
            example.createCriteria().andCondition(WorkerType.TYPE + " in(1,2) ");
            List<WorkerType> workerTypeList = workerTypeMapper.selectByExample(example);
            for (WorkerType workerType : workerTypeList) {
                List<String> workerTypes = new ArrayList<>();
                workerTypes.add("wtId" + workerType.getId());
//                workerTypes.add(house.getId());
                configMessageService.addConfigMessage(request, AppType.GONGJIANG, StringUtils.join(workerTypes, ","), "0",
                        "新的装修订单", DjConstants.PushMessage.SNAP_UP_ORDER, "4");
            }

        } catch (Exception e) {
            System.out.println("建群失败，异常：" + e.getMessage());
        }
        return ServerResponse.createBySuccessMessage("操作成功");
    }

    private static Logger logger = LoggerFactory.getLogger(RedisSessionDAO.class);

    /**
     * 结算下单提成
     */
    public void endRoyalty(HouseDTO houseDTO,String userId){
        House house = iHouseMapper.selectByPrimaryKey(houseDTO.getHouseId());

        Example example = new Example(Clue.class);
        example.createCriteria().andEqualTo(Clue.MEMBER_ID, house.getMemberId())
                .andEqualTo(Clue.DATA_STATUS, 0);

        List<Clue> clueList = clueMapper.selectByExample(example);
        //查询提成配置表
        List<DjRoyaltyDetailsSurface> list = null;
        List<BaseEntity> baseEntityList = royaltyMapper.queryRoyaltySurface();
        if(!baseEntityList.isEmpty()){
            Example example1 = new Example(DjRoyaltyDetailsSurface.class);
            example1.createCriteria().andEqualTo(
                    DjRoyaltyDetailsSurface.VILLAGE_ID, baseEntityList.get(0).getId())
                    .andEqualTo(Clue.DATA_STATUS, 0);
             list = royaltyMapper.selectByExample(example1);
        }

        if(clueList.size() == 1){

            logger.info("一个销售人员录入==================="+userId);
            logger.info("一个销售人员录入==================="+houseDTO.getHouseId());
            logger.info("一个销售人员录入==================="+list);
            //一个销售人员录入
            djrHouse(userId,houseDTO.getHouseId(),list);

        }else{
            logger.info("//多个销售人员录入获取未抢到单的销售人员id==================="+userId);
            //多个销售人员录入获取未抢到单的销售人员id
            String userId2=null;
            for (Clue clue : clueList) {
                if(!clue.getCusService().equals(userId)){
                    userId2=clue.getCusService();
                    break;
                }
            }
            //多个销售人员录入
            //判断是否在未抢单的销售楼栋范围内
            ResidentialBuilding residentialBuilding = residentialBuildingMapper.selectSingleResidentialBuilding(null, house.getBuilding(), house.getVillageId());
                if (null != residentialBuilding) {
                    ResidentialRange residentialRange = residentialRangeMapper.selectSingleResidentialRange(residentialBuilding.getId());
                    if (null != residentialRange) {
                        //该单在未抢到单的销售的楼栋范围内
                        if(residentialRange.getUserId().equals(userId2)){
                            //两销售一起分配提成
                            logger.info("两销售一起分配提成==================="+userId2);
                            logger.info("两销售一起分配提成==================="+userId);
                            Map<String,Object> map = new HashMap<>();
                            map.put("userId",userId);
                            map.put("createDate",DateUtil.dateToString(new Date(), DateUtil.FORMAT));
                            List<DjAlreadyRobSingle> darList = djAlreadyRobSingleMapper.selectArr(map);
                            for (DjRoyaltyDetailsSurface ss : list) {
                                //判断当月
                                logger.info("判断当月==================="+ss.getStartSingle());
                                logger.info("判断当月==================="+darList.size());
                                logger.info("判断当月==================="+ss.getOverSingle());

                                if (ss.getStartSingle() <= darList.size() && darList.size() <= ss.getOverSingle()) {
                                    logger.info("两销售一起分配提成---------------------");
                                    DjRoyaltyMatch djRoyaltyMatch1 = new DjRoyaltyMatch();
                                    djRoyaltyMatch1.setDataStatus(0);
                                    djRoyaltyMatch1.setUserId(userId);
                                    djRoyaltyMatch1.setOrderStatus(0);
                                    djRoyaltyMatch1.setHouseId(houseDTO.getHouseId());
                                    djRoyaltyMatch1.setMonthRoyalty((int) (ss.getRoyalty() * 0.4));
                                    djRoyaltyMatch1.setMeterRoyalty((int) (ss.getRoyalty() * 0.4));
                                    djRoyaltyMatch1.setArrRoyalty(ss.getRoyalty());
                                    djRoyaltyMatchMapper.insert(djRoyaltyMatch1);

                                    djRoyaltyMatch1 = new DjRoyaltyMatch();
                                    djRoyaltyMatch1.setDataStatus(0);
                                    djRoyaltyMatch1.setUserId(userId2);
                                    djRoyaltyMatch1.setOrderStatus(0);
                                    djRoyaltyMatch1.setHouseId(houseDTO.getHouseId());
                                    djRoyaltyMatch1.setMonthRoyalty((int) (ss.getRoyalty() * 0.4));
                                    djRoyaltyMatch1.setMeterRoyalty((int) (ss.getRoyalty() * 0.4));
                                    djRoyaltyMatch1.setArrRoyalty(ss.getRoyalty());
                                    djRoyaltyMatchMapper.insert(djRoyaltyMatch1);
                                }
                            }
                        }else{
                            //抢单的销售单独分配提成
                            logger.info("抢单的销售单独分配提成==================="+userId);
                            logger.info("抢单的销售单独分配提成==================="+houseDTO);
                            logger.info("抢单的销售单独分配提成==================="+list);
                            djrHouse(userId,houseDTO.getHouseId(),list);
                        }
                    }
                }
        }
    }

    public void djrHouse(String userId,String houseId,List<DjRoyaltyDetailsSurface> list){
        logger.info("111111111111111111111==================="+userId);
        Map<String,Object> map = new HashMap<>();
        map.put("userId",userId);
        map.put("createDate",DateUtil.dateToString(new Date(), DateUtil.FORMAT));
        List<DjAlreadyRobSingle> darList = djAlreadyRobSingleMapper.selectArr(map);
        for (DjRoyaltyDetailsSurface ss : list) {
            //判断当月
            if(ss.getStartSingle() <= darList.size() && darList.size() <= ss.getOverSingle()){
                logger.info("222222222222222222222==================="+ss.getStartSingle());
                logger.info("333333333333333333333==================="+darList.size());
                logger.info("444444444444444444444==================="+ss.getOverSingle());
                DjRoyaltyMatch djRoyaltyMatch1 = new DjRoyaltyMatch();
                djRoyaltyMatch1.setDataStatus(0);
                djRoyaltyMatch1.setUserId(userId);
                djRoyaltyMatch1.setHouseId(houseId);
                djRoyaltyMatch1.setOrderStatus(0);
                djRoyaltyMatch1.setMonthRoyalty((int) (ss.getRoyalty()*0.75));
                djRoyaltyMatch1.setMeterRoyalty((int) (ss.getRoyalty()*0.75));
                djRoyaltyMatch1.setArrRoyalty(ss.getRoyalty());
                djRoyaltyMatchMapper.insert(djRoyaltyMatch1);
            }
        }
    }

    public ServerResponse revokeHouse(String userToken) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member member = (Member) object;
        Example example = new Example(House.class);
        example.createCriteria()
                .andEqualTo(House.MEMBER_ID, member.getId())
                .andEqualTo(House.DATA_STATUS, 0);
        List<House> houseList = iHouseMapper.selectByExample(example);
        if (houseList.size() > 0) {
            for (House house : houseList) {
                if (house.getVisitState() == 0) { //0待确认开工,1装修中,2休眠中,3已完工
                    //iHouseMapper.deleteByPrimaryKey(house);
                    house.setDataStatus(1);
                    iHouseMapper.updateByPrimaryKeySelective(house);
                    Customer customer = new Customer();
                    customer.setId(null);
                    customer.setStage(1);
                    example = new Example(Customer.class);
                    example.createCriteria().andEqualTo(Customer.MEMBER_ID, house.getMemberId());
                    iCustomerMapper.updateByExampleSelective(customer, example);
                    Clue clue = new Clue();
                    clue.setId(null);
                    clue.setStage(1);
                    example = new Example(Clue.class);
                    example.createCriteria().andEqualTo(Clue.MEMBER_ID, house.getMemberId());
                    clueMapper.updateByExampleSelective(clue, example);
                    return ServerResponse.createBySuccessMessage("操作成功");
                }
            }
        }
        return ServerResponse.createByErrorMessage("操作失败，无待开工的房子");
    }

    /**
     * APP开始装修
     */
    public ServerResponse setStartHouse(String userToken, String cityId, Integer houseType, Integer drawings,
                                        String latitude, String longitude, String address, String name) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member member = (Member) object;
        Example example = new Example(House.class);
        example.createCriteria()
                .andEqualTo(House.MEMBER_ID, member.getId())
                .andEqualTo(House.DATA_STATUS, 0);
        //获取结算比例对象
        Example workDepositExample = new Example(WorkDeposit.class);
        workDepositExample.orderBy(WorkDeposit.CREATE_DATE).desc();
        List<WorkDeposit> workDeposits = workDepositMapper.selectByExample(workDepositExample);
        List<House> houseList = iHouseMapper.selectByExample(example);
        int again = 1;
        if (houseList.size() > 0) {
            again += houseList.size();
            for (House house : houseList) {
                if (house.getVisitState() == 0) { //0待确认开工,1装修中,2休眠中,3已完工
                    //默认切换至未确认开工的房子
                    setSelectHouse(userToken, house.getId());
                    return ServerResponse.createByErrorMessage("有房子未确认开工,不能再装");
                }
            }
        }
        Map<String, Object> map = new HashedMap();
        map.put("memberId", member.getId());
        map.put("stage", 5);
        map.put("tips", 1);
        clueMapper.setStage(map);//修改线索的阶段
        example = new Example(Customer.class);
        example.createCriteria().andEqualTo(Customer.MEMBER_ID, member.getId());
        Customer customer = new Customer();
        customer.setId(null);
        customer.setStage(5);
        iCustomerMapper.updateByExampleSelective(customer, example);
        Integer type = iCustomerMapper.queryType(member.getId());
        Integer result = clueMapper.queryTClue(member.getMobile());
        City city = iCityMapper.selectByPrimaryKey(cityId);
        House house = new House(true);//新增房产信息
        //0:场内录入，1:场外录入
        if (null != type) {
            if (type == 0) {
                house.setIsType(0);
            } else {
                house.setIsType(1);
            }
        } else {
            house.setIsType(1);
        }
        if (null != result) {
            if (result == 1) {
                //一个销售人员下单
                house.setAbroadStats(0);
            } else if (result > 1) {
                //两个销售人员同时下单
                house.setAbroadStats(1);
            }
        } else {
            house.setAbroadStats(0);
        }
        List<Customer> ms = iCustomerMapper.getCustomerMemberIdList(house.getMemberId());
        if (ms != null) {
            for (Customer m : ms) {
                configMessageService.addConfigMessage(AppType.SALE, m.getMemberId(), "待抢单客户提醒",
                        "您有一个新的待抢单客户，请及时查看。", 4, null, "您有新的待抢单客户快去查看吧！");
            }
        }
        house.setIsRobStats(0);
        house.setMemberId(member.getId());//用户id
        if (city != null)
            house.setCityName(city.getName());//城市名
        house.setCityId(cityId);
        house.setAgain(again);//第几套房产
        house.setHouseType(houseType);//装修的房子类型0：新房；1：老房
        house.setDrawings(drawings);//有无图纸0：无图纸；1：有图纸
        house.setWorkDepositId(workDeposits.get(0).getId());
        iHouseMapper.insert(house);
        //保存业主选择的地理位置
        HouseAddress houseAddress = new HouseAddress();
        houseAddress.setHouseId(house.getId());
        houseAddress.setLatitude(latitude);
        houseAddress.setLongitude(longitude);
        houseAddress.setAddress(address);
        houseAddress.setName(name);
        houseAddress.setHouseType(houseType);//装修的房子类型0：新房；1：老房
        iHouseAddressMapper.insert(houseAddress);
        example = new Example(MemberCity.class);
        example.createCriteria()
                .andEqualTo(MemberCity.MEMBER_ID, member.getId())
                .andEqualTo(MemberCity.CITY_ID, cityId);
        List list = memberCityMapper.selectByExample(example);
        if (list.size() == 0) {
            MemberCity userCity = new MemberCity();
            userCity.setMemberId(member.getId());
            userCity.setCityId(cityId);
            if (city != null)
                userCity.setCityName(city.getName());
            memberCityMapper.insert(userCity);
        }

        //房子花费
        HouseExpend houseExpend = new HouseExpend(true);
        houseExpend.setHouseId(house.getId());
        houseExpendMapper.insert(houseExpend);
        //默认切换至未确认开工的房子
        setSelectHouse(userToken, house.getId());
        return ServerResponse.createBySuccessMessage("操作成功");
    }

    public ServerResponse getHouseAddress(String houseId) {
        Example example = new Example(HouseAddress.class);
        example.createCriteria()
                .andEqualTo(HouseAddress.HOUSE_ID, houseId)
                .andEqualTo(HouseAddress.DATA_STATUS, 0);
        example.orderBy(HouseAddress.CREATE_DATE).desc();
        List<HouseAddress> houseAddresses = iHouseAddressMapper.selectByExample(example);
        if (houseAddresses.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        return ServerResponse.createBySuccess("查询成功", houseAddresses.get(0));
    }

    /**
     * 房子装修列表
     */
    public ServerResponse getList(PageDTO pageDTO, String cityKey, Integer visitState, String startDate, String endDate, String searchKey, String orderBy, String memberId) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            if (!CommonUtil.isEmpty(startDate) && !CommonUtil.isEmpty(endDate)) {
                if (startDate.equals(endDate)) {
                    startDate = startDate + " " + "00:00:00";
                    endDate = endDate + " " + "23:59:59";
                }
            }
            List<HouseListDTO> houseList = iHouseMapper.getHouseList(cityKey, memberId, visitState, startDate, endDate, orderBy, searchKey);
            if (houseList.size() <= 0) {
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode()
                        , "查无数据");
            }
            PageInfo pageResult = new PageInfo(houseList);
            for (HouseListDTO houseListDTO : houseList) {
                houseListDTO.setAddress(houseListDTO.getHouseName());
            }
            pageResult.setList(houseList);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 修改房子精算状态
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse setHouseBudgetOk(String houseId, Integer budgetOk) {
        try {
            House house = iHouseMapper.selectByPrimaryKey(houseId);
            if (house == null) {
                return ServerResponse.createByErrorMessage("修改房子精算状态失败");
            }
            if (house.getDecorationType() == 2 && house.getDesignerOk() != 3 && budgetOk == 2) {
                return ServerResponse.createByErrorMessage("请先上传设计图！");
            }
            WorkDeposit workDeposit = workDepositMapper.selectByPrimaryKey(house.getWorkDepositId());//结算比例表
            if (budgetOk == 2) {//打算发送给业主,验证精算完整性
                Double price = forMasterAPI.getBudgetWorkerPrice(houseId, "3", house.getCityId());
                if (price == 0) {
                    return ServerResponse.createByErrorMessage("大管家没有精算人工费,请重新添加");
                }
            }
            if (house.getBudgetOk() == 2 && budgetOk == 2) {
                return ServerResponse.createByErrorMessage("该精算任务已发送给业主审核！");
            }
            if (house.getBudgetOk() == 3) {
                return ServerResponse.createBySuccessMessage("精算已审核通过");
            }
            if (budgetOk == 3) {//精算审核通过，调用此方法查询所有验收节点并保存
                //订单拿钱更新
                HouseWorkerOrder hwo = houseWorkerOrderMapper.getByHouseIdAndWorkerTypeId(houseId, "2");
                if (hwo != null) {
                    hwo.setHaveMoney(hwo.getWorkPrice());
                    houseWorkerOrderMapper.updateByPrimaryKeySelective(hwo);
                    //用户入账
                    Member member = memberMapper.selectByPrimaryKey(hwo.getWorkerId());
                    if (member != null) {
                        BigDecimal haveMoney = member.getHaveMoney().add(hwo.getWorkPrice());
                        BigDecimal surplusMoney = member.getSurplusMoney().add(hwo.getWorkPrice());
                        member.setHaveMoney(haveMoney);//添加已获总钱
                        member.setSurplusMoney(surplusMoney);//添加余额
                        memberMapper.updateByPrimaryKeySelective(member);
                        //添加流水
                        //处理精算工钱
                        WorkerDetail workerDetail = new WorkerDetail();
                        workerDetail.setName("精算通过");
                        workerDetail.setWorkerId(hwo.getWorkerId());
                        workerDetail.setWorkerName(memberMapper.selectByPrimaryKey(hwo.getWorkerId()).getName());
                        workerDetail.setHouseId(hwo.getHouseId());
                        workerDetail.setMoney(hwo.getWorkPrice());
                        workerDetail.setState(0);//进工钱
                        workerDetail.setHaveMoney(hwo.getHaveMoney());
                        workerDetail.setWalletMoney(surplusMoney);//更新后的余额
                        workerDetail.setHouseWorkerOrderId(hwo.getId());
                        workerDetail.setApplyMoney(hwo.getWorkPrice());
                        workerDetailMapper.insert(workerDetail);
                    }
                }
                //通知大管家抢单
                HouseFlow houseFlow = houseFlowMapper.getHouseFlowByHidAndWty(houseId, 3);
                houseFlow.setWorkType(2);//待抢单
                houseFlow.setReleaseTime(new Date());//发布时间
                houseFlowMapper.updateByPrimaryKeySelective(houseFlow);
                configMessageService.addConfigMessage(null, AppType.GONGJIANG, "wtId3" + houseFlow.getCityId(),
                        "0", "新的装修订单", DjConstants.PushMessage.SNAP_UP_ORDER, "4");
                //推送消息给业主等待大管家抢单
                configMessageService.addConfigMessage(null, AppType.ZHUANGXIU, house.getMemberId(),
                        "0", "等待大管家抢单", String.format(DjConstants.PushMessage.ACTUARIAL_COMPLETION,
                                house.getHouseName()), "");
                //告知工程部精算已通过
                Map<String, String> temp_para = new HashMap();
                temp_para.put("house_name", house.getHouseName());
                JsmsUtil.sendSMS("13574147081", "165204", temp_para);

                //在这里算出大管家每次巡查拿的钱 和 每次验收拿的钱 记录到大管家的 houseflow里 houseflow,新增两个字段.
                List<HouseFlow> houseFlowList = houseFlowMapper.getForCheckMoney(houseId);
                int check = 0;//累计大管家总巡查次数
                int time = 0;//累计管家总阶段验收和完工验收次数
                for (HouseFlow hf : houseFlowList) {
                    //查出该工种工钱
                    Double workerTotal = forMasterAPI.getBudgetWorkerPrice(houseId, hf.getWorkerTypeId(), house.getCityId());
                    int inspectNumber = workerTypeMapper.selectByPrimaryKey(hf.getWorkerTypeId()).getInspectNumber();//该工种配置默认巡查次数
                    int thisCheck = (int) (workerTotal / workDeposit.getPatrolPrice().intValue());//该工种钱算出来的巡查次数
                    if (thisCheck > inspectNumber) {
                        thisCheck = inspectNumber;
                    }
                    hf.setPatrol(thisCheck);//保存巡查次数
                    houseFlowMapper.updateByPrimaryKeySelective(hf);
                    //累计总巡查
                    check += thisCheck;
                    //累计总验收
                    if (hf.getWorkerType() == 4) {
                        time++;
                    } else {
                        time += 2;
                    }
                }
                //拿到这个大管家工钱
                Double moneySup = forMasterAPI.getBudgetWorkerPrice(houseId, "3", house.getCityId());
                //算管家每次巡查钱
                double patrolMoney = 0;
                if (check > 0) {
                    patrolMoney = moneySup * 0.2 / check;
                }
                //算管家每次验收钱
                double checkMoney = 0;
                if (time > 0) {
                    checkMoney = moneySup * 0.3 / time;
                }
                //保存到大管家的houseFlow
                houseFlow.setPatrolMoney(new BigDecimal(patrolMoney));
                houseFlow.setCheckMoney(new BigDecimal(checkMoney));
                houseFlowMapper.updateByPrimaryKeySelective(houseFlow);
            }
            if (budgetOk == 2) {
                HouseFlow houseFlow = houseFlowMapper.getByWorkerTypeId(house.getId(), "2");
                //添加一条记录
                HouseFlowApply hfa = new HouseFlowApply();//发起申请任务
                hfa.setHouseFlowId(houseFlow.getId());//工序id
                hfa.setWorkerId(houseFlow.getWorkerId());//工人id
                hfa.setOperator(houseFlow.getWorkerId());
                hfa.setWorkerTypeId(houseFlow.getWorkerTypeId());//工种id
                hfa.setWorkerType(houseFlow.getWorkerType());//工种类型
                hfa.setHouseId(houseFlow.getHouseId());//房子id
                hfa.setApplyType(16);//申请得钱
                hfa.setSupervisorMoney(new BigDecimal(0));
                hfa.setOtherMoney(new BigDecimal(0));
                hfa.setMemberCheck(1);//业主审核状态0未审核，1审核通过，2审核不通过，3自动审核
                hfa.setSupervisorCheck(1);//大管家审核状态0未审核，1审核通过，2审核不通过
                hfa.setPayState(0);//是否付款
                hfa.setApplyDec("我是精算师，我已经精算完成！ ");//描述
                houseFlowApplyMapper.insert(hfa);
                insertConstructionRecord(hfa);
            }
            house.setBudgetOk(budgetOk);//精算状态:-1已精算没有发给业主,默认0未开始,1已开始精算,2已发给业主,3审核通过,4审核不通过
            iHouseMapper.updateByPrimaryKeySelective(house);
            return ServerResponse.createBySuccessMessage("修改房子精算状态成功");
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ServerResponse.createByErrorMessage("修改房子精算状态失败");
        }
    }

    /**
     * 根据城市，小区，最小最大面积查询房子
     */
    public ServerResponse queryHouseByCity(String userToken, String cityId, String villageId,
                                           Double minSquare, Double maxSquare, Integer houseType, PageDTO pageDTO) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            Object object = constructionService.getMember(userToken);
            Member member = null;
            if (object instanceof Member) {
                member = (Member) object;
            }
            boolean isReferenceBudget = false;
            if (villageId != null && villageId.contains("#")) {
                isReferenceBudget = true;
                villageId = villageId.replaceAll("#", "");
            }

            List<House> houseList = iHouseMapper.getSameLayout(cityId, villageId, minSquare, maxSquare, houseType);
            PageInfo pageResult = new PageInfo(houseList);
            List<ShareDTO> srlist = new ArrayList<>();
            if (houseList.size() > 0) {//根据条件查询所选小区总价最少的房子
                for (House house : houseList) {
                    srlist.add(convertHouse(house, member));
                }
            } else {
                if (isReferenceBudget) {
                    houseList = iHouseMapper.getSameLayout(cityId, null, minSquare, maxSquare, houseType);
                    pageResult = new PageInfo(houseList);
                    if (houseList.size() > 0) {//根据条件查询所选小区总价最少的房子
                        for (House house : houseList) {
                            srlist.add(convertHouse(house, member));
                        }
                    } else {
                        return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), "查无数据");
                    }
                } else {
                    return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), "查无数据");
                }
            }
            pageResult.setList(srlist);
            return ServerResponse.createBySuccess(null, pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("系统出错,获取数据失败");
        }
    }

    private ShareDTO convertHouse(House house, Member member) {
        ModelingLayout ml = modelingLayoutMapper.selectByPrimaryKey(house.getModelingLayoutId());
        ShareDTO shareDTO = new ShareDTO();
        shareDTO.setType("1");
        if (house.getShowHouse() == 0) {
//            if (accessToken != null) {
//                shareDTO.setName(house.getHouseName());
//            } else {
            shareDTO.setHouseName(house.getHouseName());
            shareDTO.setNoNumberHouseName(house.getResidential() + "**" + "栋" + (CommonUtil.isEmpty(house.getUnit()) ? "" : house.getUnit() + "单元") + house.getNumber() + "房");
//            }
        } else {
            shareDTO.setHouseName(house.getHouseName());
            shareDTO.setNoNumberHouseName("*栋*单元*号");
        }
        shareDTO.setJianzhumianji("建筑面积:" + (house.getBuildSquare() == null ? "0" : house.getBuildSquare()) + "m²");//建筑面积
        shareDTO.setJvillageacreage("计算面积:" + (house.getSquare() == null ? "0" : house.getSquare()) + "m²");//计算面积
        String biaoqian = house.getLiangDian();//标签
        List<String> biaoqians = new ArrayList<>();
        if (!CommonUtil.isEmpty(biaoqian)) {
            for (String s1 : biaoqian.split(",")) {
                if (!CommonUtil.isEmpty(s1)) {
                    biaoqians.add(s1);
                }
            }
        }
        biaoqians.add((house.getBuildSquare() == null ? "0" : house.getBuildSquare()) + "m²");
        shareDTO.setBiaoqian(biaoqians);//亮点标签
        BigDecimal money = house.getMoney();
        shareDTO.setPrice("***" + (member != null && money != null && money.toString().length() > 2 ?
                money.toString().substring(money.toString().length() - 2) : "00"));//精算总价
        shareDTO.setShowHouse(house.getShowHouse());
        shareDTO.setHouseId(house.getId());
        shareDTO.setVisitState(house.getVisitState());
        shareDTO.setVillageId(house.getVillageId());//小区id
        shareDTO.setVillageName(house.getResidential());//小区名
        shareDTO.setLayoutId(house.getModelingLayoutId());//户型id
        shareDTO.setLayoutleft(ml == null ? "" : ml.getName());//户型名称
        String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        String jobLocationDetail = address + String.format(DjConstants.YZPageAddress.JOBLOCATIONDETAIL, "", house.getCityId(), "施工现场") + "&houseId=" + house.getId();
        shareDTO.setUrl(jobLocationDetail);
        shareDTO.setImageNum(0 + "张图片");
        shareDTO.setImage(address + houseFlowApplyImageMapper.getHouseFlowApplyImage(house.getId(), null));//户型图片
//        ServerResponse serverResponse = designDataService.getPlaneMap(house.getId());
//        if (serverResponse.isSuccess()) {
//            QuantityRoomDTO quantityRoomDTO = (QuantityRoomDTO) serverResponse.getResultObj();
//            List<QuantityRoomImages> images = quantityRoomDTO.getImages();
//            if (images != null && images.size() > 0) {
//                shareDTO.setImage(images.get(0).getImage());
//                serverResponse = designDataService.getConstructionPlans(house.getId());
//                if (serverResponse.isSuccess()) {
//                    quantityRoomDTO = (QuantityRoomDTO) serverResponse.getResultObj();
//                    if (quantityRoomDTO.getImages() != null) {
//                        shareDTO.setImageNum(quantityRoomDTO.getImages().size() + "张图片");
//                    }
//                }
//            }
//        }
        ServerResponse serverResponse = designDataService.getConstructionPlans(house.getId());
        if (serverResponse.isSuccess()) {
            QuantityRoomDTO quantityRoomDTO = (QuantityRoomDTO) serverResponse.getResultObj();
            List<QuantityRoomImages> images = quantityRoomDTO.getImages();
            if (images != null && images.size() > 0) {
//                shareDTO.setImage(images.get(0).getImage() + "?x-image-process=image/resize,w_500,h_500/quality,q_80");
                shareDTO.setImageNum(quantityRoomDTO.getImages().size() + "张图片");
            }
        }
        return shareDTO;
    }

    /**
     * 装修指南明细
     *
     * @param id
     * @return
     */
    public ServerResponse getRenovationManualinfo(String id) {
        try {
            RenovationManual renovationManual = renovationManualMapper.selectByPrimaryKey(id);
            return ServerResponse.createBySuccess("ok", renovationManual);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("系统出错,获取装修指南失败");
        }
    }

    /**
     * 保存装修指南
     *
     * @param userToken
     * @param saveList
     * @return
     */
    public ServerResponse saveRenovationManual(String userToken, String saveList) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = (Member) object;
            if (saveList != null) {
                Example example = new Example(RenovationManualMember.class);
                example.createCriteria().andEqualTo("memberId", member.getId());
                renovationManualMemberMapper.deleteByExample(example);
                JSONArray jsonArr = JSONArray.parseArray(saveList);//格式化jsonArr
                for (int i = 0; i < jsonArr.size(); i++) {
                    JSONObject obj = jsonArr.getJSONObject(i);
                    if (obj.getInteger("state") == 1) {
                        RenovationManualMember rm = new RenovationManualMember();
                        rm.setMemberId(member.getId());
                        rm.setRenovationManualId(obj.getString("id"));
                        rm.setState(1);
                        renovationManualMemberMapper.insertSelective(rm);
                    }
                }
            }
            return ServerResponse.createBySuccessMessage("保存装修指南成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("系统出错,保存装修指南失败");
        }
    }

    /**
     * 施工记录（首页滚动）
     */
    public ServerResponse queryHomeConstruction() {
        try {
            Map<Integer, String> applyTypeMap = new HashMap<>();
            applyTypeMap.put(DjConstants.ApplyType.MEIRI_WANGGONG, "今日已完工");
            applyTypeMap.put(DjConstants.ApplyType.JIEDUAN_WANGONG, "今日阶段完工");
            applyTypeMap.put(DjConstants.ApplyType.ZHENGTI_WANGONG, "今日整体完工");
            applyTypeMap.put(DjConstants.ApplyType.TINGGONG, "今日已停工");
            applyTypeMap.put(DjConstants.ApplyType.MEIRI_KAIGONG, "已开工");
            applyTypeMap.put(DjConstants.ApplyType.YOUXIAO_XUNCHA, "今日已巡查");
            applyTypeMap.put(DjConstants.ApplyType.WUREN_XUNCHA, "今日已巡查");
            applyTypeMap.put(DjConstants.ApplyType.ZUIJIA_XUNCHA, "今日已巡查");
            PageHelper.startPage(1, 20);
            Example example = new Example(HouseFlowApply.class);
            example.orderBy(HouseFlowApply.CREATE_DATE).desc();
            List<HouseFlowApply> hfaList = houseFlowApplyMapper.selectByExample(example);
            List listMap = new ArrayList<>();
            for (HouseFlowApply hfa : hfaList) {
                StringBuffer name = new StringBuffer();
                House house = iHouseMapper.selectByPrimaryKey(hfa.getHouseId());
                if (house != null) {
                    name.append(house.getNoNumberHouseName());
                }
                Member member = memberMapper.selectByPrimaryKey(hfa.getWorkerId());
                if (null != member) {
                    WorkerType workerType = workerTypeMapper.selectByPrimaryKey(member.getWorkerTypeId());
                    if (null != workerType) {
                        name.append(" " + workerType.getName());
                    }
                    name.append(applyTypeMap.get(hfa.getApplyType()));
                    listMap.add(name.toString());
                }
            }
            return ServerResponse.createBySuccess("ok", listMap);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("系统出错,查询施工记录失败");
        }
    }

    /**
     * 施工记录
     */
    public ServerResponse queryConstructionRecord(String houseId, PageDTO pageDTO, String workerTypeId) {
        // 施工记录的内容需要更改
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<HouseFlowApply> hfaList = houseFlowApplyMapper.queryAllHfaByHouseId(houseId, workerTypeId);
        PageInfo pageResult = new PageInfo(hfaList);
        List<Map<String, Object>> listMap = this.houseFlowApplyDetail(hfaList);
        if (listMap == null) {
            return ServerResponse.createByErrorMessage("系统出错,查询施工记录失败");
        }
        pageResult.setList(listMap);
        return ServerResponse.createBySuccess("查询施工记录成功", pageResult);
    }
    /**
     * 施工记录（分类型）
     */
    public ServerResponse queryConstructionRecordType(String houseId) {
        List<HouseConstructionRecordTypeDTO> hfaList = houseConstructionRecordMapper.getHouseConstructionRecordTypeDTO(houseId);
        return ServerResponse.createBySuccess("查询施工记录成功", hfaList);
    }
    /**
     * 施工记录
     */
    public ServerResponse queryConstructionRecordAll(String houseId, String ids, String day, String workerType, PageDTO pageDTO) {
        // 施工记录的内容需要更改
        String address = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
        Example example = new Example(HouseConstructionRecord.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo(HouseConstructionRecord.HOUSE_ID, houseId);
        if (!CommonUtil.isEmpty(day)) {
            criteria.andCondition(" to_days(create_date) = to_days('" + day + "') ");
        }
        if (!CommonUtil.isEmpty(ids)) {
            String[] id = ids.split(",");
            criteria.andIn(HouseConstructionRecord.ID,  Arrays.asList(id));
        }
        if (!CommonUtil.isEmpty(workerType)) {
            criteria.andEqualTo(HouseConstructionRecord.WORKER_TYPE, workerType);
        }
        example.orderBy(HouseConstructionRecord.CREATE_DATE).desc();
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<HouseConstructionRecord> hfaList = houseConstructionRecordMapper.selectByExample(example);
        if (hfaList.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), "无相关施工记录");
        }
        PageInfo pageResult = new PageInfo(hfaList);
        List<Map<String, Object>> listMap = new ArrayList<>();
        for (HouseConstructionRecord houseConstructionRecord : hfaList) {
            listMap.add(getHouseConstructionRecordMap(houseConstructionRecord, address));
        }
        pageResult.setList(listMap);
        return ServerResponse.createBySuccess("查询施工记录成功", pageResult);
    }

    private Map<String, Object> getHouseConstructionRecordMap(HouseConstructionRecord hfa, String address) {
        // 0每日完工申请，1阶段完工申请，2整体完工申请,3停工申请，
        // 4：每日开工,5有效巡查,6无人巡查,7追加巡查,
        // 8补人工,9退人工,10补材料,11退材料,12业主退材料
        Map<Integer, String> applyTypeMap = DjConstants.RecordType.getRecordTypeMap();
        Map<String, Object> map = new HashMap<>();
        map.put("id", hfa.getSourceId());
        Member member = memberMapper.selectByPrimaryKey(hfa.getWorkerId());
        if (member != null) {
            member.initPath(address);
            map.put("workerHead", member.getHead());//工人头像
            if (member.getWorkerType() != null && member.getWorkerType() >= 1) {
                map.put("workerTypeName", workerTypeMapper.selectByPrimaryKey(member.getWorkerTypeId()).getName());//工匠类型
            } else {
                map.put("workerTypeName", "业主");//工匠类型
            }
            map.put("workerName", member.getName());//工人名称
        }
        map.put("content", hfa.getContent());
        map.put("sourceType", hfa.getApplyType());
        Example example = new Example(HouseFlowApplyImage.class);
        if (hfa.getSourceType() == 0) {
            HouseFlowApply houseFlowApply = houseFlowApplyMapper.selectByPrimaryKey(hfa.getSourceId());
            example.createCriteria().andEqualTo(HouseFlowApplyImage.HOUSE_FLOW_APPLY_ID, hfa.getSourceId());
            List<HouseFlowApplyImage> hfaiList = houseFlowApplyImageMapper.selectByExample(example);
            String[] imgArr = new String[hfaiList.size()];
            for (int i = 0; i < hfaiList.size(); i++) {
                HouseFlowApplyImage hfai = hfaiList.get(i);
                String string = hfai.getImageUrl();
                imgArr[i] = address + string;
            }
            map.put("imgArr", imgArr);
            if (houseFlowApply != null) {
                map.put("startDate", houseFlowApply.getStartDate());
                map.put("endDate", houseFlowApply.getEndDate());
            }
        }
        if (hfa.getSourceType() == 1) {
            MendOrder mendOrder = mendOrderMapper.selectByPrimaryKey(hfa.getSourceId());
            if (null != mendOrder) {
                map.put("type", mendOrder.getType());
                map.put("number", mendOrder.getNumber());
                if (mendOrder.getType() == 2 && StringUtil.isNotEmpty(mendOrder.getImageArr())) {
                    String[] imageArr = mendOrder.getImageArr().split(",");
                    if (imageArr.length > 0) {
                        List<String> imageList = new ArrayList<>();
                        for (String anImageArr : imageArr) {
                            imageList.add(address + anImageArr);
                        }
                        map.put("imgArr", imageList);
                    }
                }
            }
        }
        map.put("applyType", applyTypeMap.get(hfa.getApplyType()));
        map.put("createDate", hfa.getCreateDate());
        example = new Example(TechnologyRecord.class);
        example.createCriteria().andEqualTo(TechnologyRecord.HOUSE_FLOW_APPLY_ID, hfa.getSourceId());
        example.orderBy(TechnologyRecord.CREATE_DATE).desc();
        //已验收节点
        List<TechnologyRecord> recordList = technologyRecordMapper.selectByExample(example);
        List<Map<String, Object>> nodeMap = new ArrayList<>();
        for (TechnologyRecord technologyRecord : recordList) {
            Map<String, Object> map1 = new HashMap<>();
            map1.put("time", technologyRecord.getModifyDate());
            map1.put("name", technologyRecord.getName());
            String[] imgArr = technologyRecord.getImage().split(",");
            for (int i = 0; i < imgArr.length; i++) {
                imgArr[i] = address + imgArr[i];
            }
            map1.put("imgArr", imgArr);
            nodeMap.add(map1);
        }
        map.put("recordList", nodeMap);
        return map;
    }

    /**
     * 工序记录
     */
    public ServerResponse queryFlowRecord(String houseFlowId) {
        try {
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            FlowRecordDTO flowRecordDTO = new FlowRecordDTO();
            List<HouseFlowApply> hfaList = houseFlowApplyMapper.queryFlowRecord(houseFlowId);
            List<Map<String, Object>> listMap = this.houseFlowApplyDetail(hfaList);
            flowRecordDTO.setFlowApplyMap(listMap);

            HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(houseFlowId);
            Example example = new Example(HouseWorker.class);
            example.createCriteria().andEqualTo(HouseWorker.HOUSE_ID, houseFlow.getHouseId())
                    .andEqualTo(HouseWorker.WORKER_TYPE_ID, houseFlow.getWorkerTypeId()).andNotEqualTo(HouseWorker.WORK_TYPE, 5);
            List<HouseWorker> houseWorkerList = houseWorkerMapper.selectByExample(example);
            List<Map<String, Object>> houseWorkerMap = new ArrayList<>();
            for (HouseWorker houseWorker : houseWorkerList) {
                Map<String, Object> map = new HashMap<>();
                Member member = memberMapper.selectByPrimaryKey(houseWorker.getWorkerId());
                map.put("workerHead", address + member.getHead());//工人头像
                map.put("workerTypeName", workerTypeMapper.selectByPrimaryKey(member.getWorkerTypeId()).getName());//工匠类型
                map.put("mobile", member.getMobile());//工人电话
                map.put("workerId", member.getId());//工人电话
                if (houseWorker.getWorkType() == 1) {
                    map.put("workerName", member.getName() + "(待支付)");//工人名称
                } else if (houseWorker.getWorkType() == 6) {
                    map.put("workerName", member.getName());//工人名称
                } else if (houseWorker.getWorkType() == 7) {
                    map.put("workerName", member.getName() + "(已放弃)");//工人名称
                } else {
                    map.put("workerName", member.getName() + "(已更换)");//2被换人,4已开工被换人,7抢单后放弃
                }
                map.put("workType", houseWorker.getWorkType());
                houseWorkerMap.add(map);
            }
            flowRecordDTO.setHouseWorkerMap(houseWorkerMap);

            //已验收节点
            List<TechnologyRecord> checkList = technologyRecordMapper.allChecked(houseFlow.getHouseId(), houseFlow.getWorkerTypeId());
            List<Map<String, Object>> nodeMap = new ArrayList<>();
            for (TechnologyRecord technologyRecord : checkList) {
                Map<String, Object> map = new HashMap<>();
                map.put("name", technologyRecord.getName());
                map.put("time", technologyRecord.getModifyDate());
                String[] imgArr = technologyRecord.getImage().split(",");
                for (int i = 0; i < imgArr.length; i++) {
                    imgArr[i] = address + imgArr[i];
                }
                map.put("imgArr", imgArr);
                nodeMap.add(map);
            }
            flowRecordDTO.setNodeMap(nodeMap);

            return ServerResponse.createBySuccess("查询工序记录成功", flowRecordDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询工序记录失败");
        }
    }

    /**
     * 记录
     */
    private List<Map<String, Object>> houseFlowApplyDetail(List<HouseFlowApply> hfaList) {
        try {
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            List<Map<String, Object>> listMap = new ArrayList<>();
            for (HouseFlowApply hfa : hfaList) {
                listMap.add(getHouseFlowApplyMap(hfa, address));
            }
            return listMap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取施工记录详情
     *
     * @param houseFlowApplyId 施工记录ID
     * @return
     */
    public ServerResponse getHouseFlowApply(String houseFlowApplyId) {
        if (CommonUtil.isEmpty(houseFlowApplyId)) {
            return ServerResponse.createByErrorMessage("请传入施工记录ID");
        }
        try {
            HouseFlowApply houseFlowApply = houseFlowApplyMapper.selectByPrimaryKey(houseFlowApplyId);
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            return ServerResponse.createBySuccess("查询成功", getHouseFlowApplyMap(houseFlowApply, address));
        } catch (Exception e) {
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    private Map<String, Object> getHouseFlowApplyMap(HouseFlowApply hfa, String address) {
        Map<Integer, String> applyTypeMap = new HashMap<>();
        applyTypeMap.put(DjConstants.ApplyType.MEIRI_WANGGONG, "每日完工申请");
        applyTypeMap.put(DjConstants.ApplyType.JIEDUAN_WANGONG, "阶段完工申请");
        applyTypeMap.put(DjConstants.ApplyType.ZHENGTI_WANGONG, "整体完工申请");
        applyTypeMap.put(DjConstants.ApplyType.TINGGONG, "停工申请");
        applyTypeMap.put(DjConstants.ApplyType.MEIRI_KAIGONG, "每日开工");
        applyTypeMap.put(DjConstants.ApplyType.YOUXIAO_XUNCHA, "有人巡查");
        applyTypeMap.put(DjConstants.ApplyType.WUREN_XUNCHA, "无人巡查");
        applyTypeMap.put(DjConstants.ApplyType.ZUIJIA_XUNCHA, "追加巡查");
        Map<String, Object> map = new HashMap<>();
        map.put("id", hfa.getId());
        Member member = memberMapper.selectByPrimaryKey(hfa.getWorkerId());
        map.put("workerHead", address + member.getHead());//工人头像
        map.put("workerTypeName", workerTypeMapper.selectByPrimaryKey(member.getWorkerTypeId()).getName());//工匠类型
        map.put("workerName", member.getName());//工人名称
        Example example = new Example(HouseWorker.class);
        example.createCriteria().andEqualTo("houseId", hfa.getHouseId()).andEqualTo("workerId", hfa.getWorkerId());
        List<HouseWorker> listHw = houseWorkerMapper.selectByExample(example);
        if (listHw.size() > 0) {
            HouseWorker houseWorker = listHw.get(0);
            if (houseWorker.getWorkType() == 4) {
                map.put("isNormal", "已更换");//施工状态
            } else {
                map.put("isNormal", "正常施工");
            }
        } else {
            map.put("isNormal", "正常施工");
        }
        map.put("content", hfa.getApplyDec());
        example = new Example(HouseFlowApplyImage.class);
        example.createCriteria().andEqualTo(HouseFlowApplyImage.HOUSE_FLOW_APPLY_ID, hfa.getId());
        List<HouseFlowApplyImage> hfaiList = houseFlowApplyImageMapper.selectByExample(example);
        String[] imgArr = new String[hfaiList.size()];
        for (int i = 0; i < hfaiList.size(); i++) {
            HouseFlowApplyImage hfai = hfaiList.get(i);
            String string = hfai.getImageUrl();
            imgArr[i] = address + string;
        }
        map.put("imgArr", imgArr);
        HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(hfa.getHouseFlowId());
        if (hfa.getWorkerType() == 3 && houseFlow != null && hfa.getWorkerType() != houseFlow.getWorkerType()) {
            map.put("applyType", "大管家验收(" + workerTypeMapper.selectByPrimaryKey(houseFlow.getWorkerTypeId()).getName() + ")的" + applyTypeMap.get(hfa.getApplyType()));
        } else {
            map.put("applyType", applyTypeMap.get(hfa.getApplyType()));
        }
        map.put("createDate", hfa.getCreateDate().getTime());
        return map;
    }

    /**
     * 根据id查询房子信息
     *
     * @return
     */
    public House getHouseById(String houseId) {
        try {
            return iHouseMapper.selectByPrimaryKey(houseId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 参考报价
     *
     * @return
     */
    public ServerResponse getReferenceBudget(String cityId, String villageId, Double minSquare, Double maxSquare, Integer houseType) {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            House house = null;
            List<House> listHouse = iHouseMapper.getReferenceBudget(cityId, villageId, houseType, minSquare, maxSquare);
            if (listHouse.size() > 0) {//根据条件查询所选小区总价最少的房子
                house = listHouse.get(0);
            } else {
                listHouse = iHouseMapper.getReferenceBudget(cityId, null, houseType, minSquare, maxSquare);
                if (listHouse.size() > 0) {//根据条件查询所选小区总价最少的房子
                    house = listHouse.get(0);
                }
            }
            if (house != null) {
                request.setAttribute(Constants.CITY_ID, house.getCityId());
                return budgetWorkerAPI.gatEstimateBudgetByHId(house.getCityId(), house.getId());
            }
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), "暂无所需报价");
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("系统出错，查询参考报价失败", e);
            return ServerResponse.createByErrorMessage("系统出错,查询参考报价失败");
        }
    }

    /**
     * 根据房子装修状态查询所有的房子
     *
     * @param visitState 0待确认开工,1装修中,2休眠中,3已完工
     * @return
     */
    public ServerResponse getAllHouseByVisitState(Integer visitState) {
        List<House> houseList = iHouseMapper.getAllHouseByVisitState(visitState);//0待确认开工,1装修中,2休眠中,3已完工
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (House house : houseList) {
            Map<String, Object> map = new HashMap<>();
            map.put("houseId", house.getId());
            map.put("address", house.getHouseName());
            map.put("visitState", house.getVisitState());
            mapList.add(map);
        }
        return ServerResponse.createBySuccess("查询成功", mapList);
    }

    /**
     * 业主装修的房子可修改
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse updateByHouseId(String building, String unit, String number,
                                          String houseId, String villageId, String cityId, Double buildSquare) {
        House house = iHouseMapper.selectByPrimaryKey(houseId);
        if (house == null) {
            return ServerResponse.createByErrorMessage("该房产不存在");
        }
        house.setBuilding(building);                 //楼栋
        house.setUnit(unit);                         //单元号
        house.setNumber(number);                     //房间号
        house.setVillageId(villageId);                //小区Id
        house.setCityId(cityId);                      //城市Id
        if (!CommonUtil.isEmpty(buildSquare))
            house.setBuildSquare(new BigDecimal(buildSquare));
        ModelingVillage modelingVillage = modelingVillageMapper.selectByPrimaryKey(villageId);
        if (modelingVillage != null)
            house.setResidential(modelingVillage.getName());
        iHouseMapper.updateByPrimaryKeySelective(house);
        return ServerResponse.createBySuccessMessage("更新成功");

    }

    /**
     * 房子申请修改未进场的工序还原
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse updateCustomEdit(String houseId) {
        try {
            House house = iHouseMapper.selectByPrimaryKey(houseId);
            house.setCustomEdit(null);
            iHouseMapper.updateByPrimaryKey(house);
            return ServerResponse.createBySuccessMessage("更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("更新失败");
        }

    }

    public ServerResponse getHistoryWorker(String houseId, String workerTypeId, String workId, PageDTO pageDTO) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            Example example = new Example(HouseWorker.class);
            example.createCriteria().andEqualTo(HouseWorker.HOUSE_ID, houseId)
                    .andEqualTo(HouseWorker.WORKER_TYPE_ID, workerTypeId)
                    .andNotEqualTo(HouseWorker.WORK_TYPE, 6)
                    .andNotEqualTo(HouseWorker.WORK_TYPE, 1);
            example.orderBy(HouseWorker.MODIFY_DATE).desc();
            List<HouseWorker> houseWorkers = houseWorkerMapper.selectByExample(example);
            PageInfo pageResult = new PageInfo(houseWorkers);
            List<HouseWorkDTO> houseWorkDTOS = new ArrayList<>();
            for (HouseWorker h : houseWorkers) {
                HouseWorkDTO houseWorkDTO = new HouseWorkDTO();
                Member member = memberMapper.selectByPrimaryKey(h.getWorkerId());
                Example example1 = new Example(WorkerDetail.class);
                example1.createCriteria().andEqualTo(WorkerDetail.HOUSE_ID, houseId)
                        .andEqualTo(WorkerDetail.WORKER_ID, h.getWorkerId());
                List<WorkerDetail> workerDetails = workerDetailMapper.selectByExample(example1);
                double money = 0;
                for (WorkerDetail w : workerDetails) {
                    money += w.getMoney().doubleValue();
                }
                houseWorkDTO.setWorkName(member.getName());
                houseWorkDTO.setPhone(member.getMobile());
                houseWorkDTO.setModifyDate(h.getModifyDate());
                houseWorkDTO.setWorkerId(h.getWorkerId());
                houseWorkDTO.setHaveMoney(new BigDecimal(money));
                houseWorkDTOS.add(houseWorkDTO);
            }
            pageResult.setList(houseWorkDTOS);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    //    0每日完工申请，1阶段完工申请，2整体完工申请,4：每日开工,5巡查,8补人工,9退人工,10补材料,11退材料,12业主退材料
    public void insertConstructionRecord(Object sourceOjb) {
        insertConstructionRecordAll(sourceOjb, null);
    }

    public void insertConstructionRecordAll(Object sourceOjb, ChangeOrder changeOrder) {
        try {
            // 施工记录的内容
            HouseConstructionRecord houseConstructionRecord = new HouseConstructionRecord();
            if (sourceOjb instanceof HouseFlowApply) {
                HouseFlowApply houseFlowApply = (HouseFlowApply) sourceOjb;
                houseConstructionRecord.setHouseId(houseFlowApply.getHouseId());
                houseConstructionRecord.setSourceId(houseFlowApply.getId());
                houseConstructionRecord.setContent(houseFlowApply.getApplyDec());

                if (houseFlowApply.getApplyType() == 3) {
                    WorkerType workerType = workerTypeMapper.selectByPrimaryKey(houseFlowApply.getWorkerTypeId());
                    String dayse = "(" + DateUtil.dateToString(houseFlowApply.getStartDate(), DateUtil.FORMAT1) + "至" + DateUtil.dateToString(houseFlowApply.getEndDate(), DateUtil.FORMAT1) + ")";
                    if (houseFlowApply.getWorkerId() != null && (CommonUtil.isEmpty(houseFlowApply.getOperator()) || houseFlowApply.getWorkerId().equals(houseFlowApply.getOperator()))) {
                        houseConstructionRecord.setContent("申请停工" + houseFlowApply.getSuspendDay() + "天" + dayse + (CommonUtil.isEmpty(houseFlowApply.getApplyDec()) ? "" : ",理由：" + houseFlowApply.getApplyDec()));
                    } else {
                        houseConstructionRecord.setContent("申请(" + workerType.getName() + ")停工" + houseFlowApply.getSuspendDay() + "天" + dayse + (CommonUtil.isEmpty(houseFlowApply.getApplyDec()) ? "" : ",理由：" + houseFlowApply.getApplyDec()));
                    }
                }
                houseConstructionRecord.setWorkerId(houseFlowApply.getOperator());
                houseConstructionRecord.setWorkerType(houseFlowApply.getWorkerType());
                houseConstructionRecord.setApplyType(houseFlowApply.getApplyType());
                if (houseFlowApply.getApplyType() == 8) {
                    houseConstructionRecord.setApplyType(13);
                }
                if (houseFlowApply.getApplyType() == 5 || houseFlowApply.getApplyType() == 6 || houseFlowApply.getApplyType() == 7) {
                    HouseFlow supervisorHF = houseFlowMapper.getHouseFlowByHidAndWty(houseFlowApply.getHouseId(), 3);//大管家的hf
                    houseConstructionRecord.setWorkerId(supervisorHF.getWorkerId());
                    houseConstructionRecord.setWorkerType(supervisorHF.getWorkerType());
                }
                houseConstructionRecord.setSourceType(0);
            }
            if (sourceOjb instanceof MendOrder) {
                MendOrder mendOrder = (MendOrder) sourceOjb;
                houseConstructionRecord.setHouseId(mendOrder.getHouseId());
                houseConstructionRecord.setSourceId(mendOrder.getId());
                houseConstructionRecord.setContent("发起了" + mendOrder.getOrderName());

                if (mendOrder.getType() == 1 || mendOrder.getType() == 3) {
                    if (changeOrder != null && !CommonUtil.isEmpty(changeOrder.getMemberId())) {
                        houseConstructionRecord.setWorkerId(changeOrder.getMemberId());
                        houseConstructionRecord.setWorkerType(null);
                    }
                    if (changeOrder != null && !CommonUtil.isEmpty(changeOrder.getWorkerId())) {
                        houseConstructionRecord.setWorkerId(changeOrder.getWorkerId());
                        if (!CommonUtil.isEmpty(changeOrder.getWorkerTypeId())) {
                            WorkerType workerType = workerTypeMapper.selectByPrimaryKey(changeOrder.getWorkerTypeId());
                            houseConstructionRecord.setWorkerType(workerType.getType());
                        }
                    }
                } else {
                    houseConstructionRecord.setWorkerId(mendOrder.getApplyMemberId());
                    if (!CommonUtil.isEmpty(mendOrder.getWorkerTypeId())) {
                        WorkerType workerType = workerTypeMapper.selectByPrimaryKey(mendOrder.getWorkerTypeId());
                        houseConstructionRecord.setWorkerType(workerType.getType());
                    }
                }
                if (mendOrder.getType() == 0) {
                    houseConstructionRecord.setApplyType(10);
                } else if (mendOrder.getType() == 1) {
                    houseConstructionRecord.setApplyType(8);
                } else if (mendOrder.getType() == 2) {
                    houseConstructionRecord.setApplyType(11);
                } else if (mendOrder.getType() == 3) {
                    houseConstructionRecord.setApplyType(9);
                } else if (mendOrder.getType() == 4) {
                    houseConstructionRecord.setApplyType(12);
                } else {
                    houseConstructionRecord.setApplyType(-1);//未知类型
                }
                houseConstructionRecord.setSourceType(1);
            }
            if (!CommonUtil.isEmpty(houseConstructionRecord.getSourceId())) {
                houseConstructionRecordMapper.insert(houseConstructionRecord);
            }
        } catch (BaseException e) {
            LOG.error("施工记录保存异常：" + e.getMessage(), e);
        }
    }

    /**
     * 房子利润列表（利润统计）
     */
    public ServerResponse getHouseProfitList(HttpServletRequest request, PageDTO pageDTO, String villageId, String visitState, String searchKey) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<DesignDTO> houseList = iHouseMapper.getHouseProfitList(villageId, visitState, searchKey);
            if (houseList.size() <= 0) {
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode()
                        , "查无数据");
            }
            PageInfo pageResult = new PageInfo(houseList);
            for (DesignDTO houseListDTO : houseList) {
                Double profit = 0d;
                List<HouseProfitSummaryDTO> list = iHouseMapper.getHouseProfitSummary(houseListDTO.getHouseId());
                houseListDTO.setProfitSummarys(list);
                for (HouseProfitSummaryDTO houseProfitSummaryDTO : list) {
                    if ("0".equals(houseProfitSummaryDTO.getPlus())) {
                        profit = profit + houseProfitSummaryDTO.getMoney();
                    }
                    if ("1".equals(houseProfitSummaryDTO.getPlus())) {
                        profit = profit - houseProfitSummaryDTO.getMoney();
                    }
                }
                houseListDTO.setProfit(profit);
            }
            pageResult.setList(houseList);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 获取房屋精选案例详情
     */
    public ServerResponse getHouseChoiceCases(String id) {
        try {
            String jdAddress = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            HouseChoiceCase houseChoiceCase = iHouseChoiceCaseMapper.selectByPrimaryKey(id);
            JSONArray itemObjArr = JSON.parseArray(houseChoiceCase.getTextContent());
            HouseChoiceCaseDTO houseChoiceCaseDTO = new HouseChoiceCaseDTO();
            for (int i = 0; i < itemObjArr.size(); i++) {
                JSONObject jsonObject = itemObjArr.getJSONObject(i);
                String headline = jsonObject.getString("headline");
                String[] image = jsonObject.getString("image").split(",");
                for (int j = 0; j < image.length; j++) {
                    image[j] = jdAddress + image[j];

                }
                String describe = jsonObject.getString("describe");
                TextContentDTO textContentDTO = new TextContentDTO();
                textContentDTO.setHeadline(headline);
                textContentDTO.setDescribe(describe);
                textContentDTO.setImage(image);
                houseChoiceCaseDTO.getTextContentDTO().add(textContentDTO);
            }
            houseChoiceCaseDTO.setImage(jdAddress + houseChoiceCase.getImage());
            houseChoiceCaseDTO.setTitle(houseChoiceCase.getTitle());
            houseChoiceCaseDTO.setBuildingNames(houseChoiceCase.getBuildingNames());
            houseChoiceCaseDTO.setArea(houseChoiceCase.getArea());
            houseChoiceCaseDTO.setCost(houseChoiceCase.getCost());
            houseChoiceCaseDTO.setStyle(houseChoiceCase.getStyle());
            return ServerResponse.createBySuccess("查询成功", houseChoiceCaseDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    public ServerResponse getStageProgress(String houseFlowId) {
        HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(houseFlowId);
        if (houseFlow == null) {
            return ServerResponse.createByErrorMessage("该工序不存在");
        }
        if (houseFlow.getWorkerType() == 1 || houseFlow.getWorkerType() == 2) {
            return ServerResponse.createByErrorMessage("该工序不支持查询");
        }
        WorkerType workerType = workerTypeMapper.selectByPrimaryKey(houseFlow.getWorkerTypeId());
        if (workerType == null) {
            return ServerResponse.createByErrorMessage("该工序不存在");
        }
        House house = iHouseMapper.selectByPrimaryKey(houseFlow.getHouseId());
        if (house == null) {
            return ServerResponse.createByErrorMessage("没有查询到相关房子");
        }
        Map<String, Object> dataMap = new HashMap<>();
        String[] titles;
        int rank = -1;
        String des = "";
        if (workerType.getType() == 3) {//管家
            List<HouseFlowDTO> houseFlowList = houseFlowMapper.getHouseScheduleFlow(houseFlow.getHouseId());
            Date startDate = null;
            Date endDate = null;
            for (HouseFlowDTO houseFlow1 : houseFlowList) {
                if (houseFlow1.getStartDate() != null) {
                    if (endDate == null || endDate.getTime() < houseFlow1.getEndDate().getTime()) {
                        endDate = houseFlow1.getEndDate();
                    }
                    if (startDate == null || startDate.getTime() > houseFlow1.getStartDate().getTime()) {
                        startDate = houseFlow1.getStartDate();
                    }
                }
            }
            int totalDuration = 0;
            if (startDate != null) {
                totalDuration = 1 + DateUtil.daysofTwo(startDate, endDate);//逾期工期天数
            }
            dataMap.put("totalDuration", totalDuration);//总工期/天
            Example example1 = new Example(HouseFlowApply.class);
            example1.createCriteria().andEqualTo(HouseFlowApply.HOUSE_ID, house.getId())
                    .andEqualTo(HouseFlowApply.MEMBER_CHECK, 1)
                    .andEqualTo(HouseFlowApply.APPLY_TYPE, 3);
            List<HouseFlowApply> houseFlowss = houseFlowApplyMapper.selectByExample(example1);
            int downtime = 0;//停工天数
            for (HouseFlowApply flowss : houseFlowss) {
                downtime += flowss.getSuspendDay();
            }
            dataMap.put("downtime", downtime);//停工天数/天
            titles = new String[]{workerType.getName() + "抢单", "支付" + workerType.getName() + "费用", "工程排期", "确认开工", "监管工地"};
            if (houseFlow.getWorkType() == 2) {
                rank = 0;
                des = "待抢单";
            } else if (houseFlow.getWorkType() == 3) {
                rank = 1;
                des = "进行中";
            } else if (houseFlow.getWorkType() == 4) {
                if ("0".equals(house.getSchedule())) {
                    rank = 2;
                    des = "进行中";
                } else if (houseFlow.getSupervisorStart() == 0) {
                    rank = 3;
                    des = "待开工";
                } else if (houseFlow.getWorkSteta() == 2) {
                    rank = 4;
                    des = "整体完工";
                } else if (houseFlow.getWorkSteta() == 6) {
                    rank = 4;
                    des = "提前结束装修";
                } else {
                    rank = 4;
                    des = "监工中";
                }
            }
        } else {
            Date startDate = houseFlow.getStartDate();
            Date endDate = houseFlow.getEndDate();
            int numall = 0;
            if (startDate != null) {
                numall = 1 + DateUtil.daysofTwo(startDate, endDate);//逾期工期天数
            }
            dataMap.put("totalDuration", numall);//总工期/天
            Example example1 = new Example(HouseFlowApply.class);
            example1.createCriteria().andEqualTo(HouseFlowApply.HOUSE_ID, house.getId())
                    .andEqualTo(HouseFlowApply.WORKER_TYPE_ID, houseFlow.getWorkerTypeId())
                    .andEqualTo(HouseFlowApply.MEMBER_CHECK, 1)
                    .andEqualTo(HouseFlowApply.APPLY_TYPE, 3);
            List<HouseFlowApply> houseFlowss = houseFlowApplyMapper.selectByExample(example1);
            int downtime = 0;//停工天数
            for (HouseFlowApply flowss : houseFlowss) {
                downtime += flowss.getSuspendDay();
            }
            dataMap.put("downtime", downtime);//停工天数/天
            example1 = new Example(HouseFlowApply.class);
            example1.createCriteria().andEqualTo(HouseFlowApply.HOUSE_ID, house.getId())
                    .andEqualTo(HouseFlowApply.WORKER_TYPE_ID, houseFlow.getWorkerTypeId())
                    .andEqualTo(HouseFlowApply.MEMBER_CHECK, 1)
                    .andEqualTo(HouseFlowApply.APPLY_TYPE, 2);
            example1.orderBy(HouseFlowApply.MODIFY_DATE).desc();
            List<HouseFlowApply> houseFlowss2 = houseFlowApplyMapper.selectByExample(example1);
            int advanceTime = 0;
            if (houseFlowss2.size() > 0) {
                advanceTime = DateUtil.daysofTwo(houseFlowss2.get(0).getModifyDate(), endDate);
            }
            if (advanceTime < 0) {
                advanceTime = 0;
            }
            dataMap.put("advanceTime", advanceTime);//提前完工时间/天
            titles = new String[]{workerType.getName() + "抢单", "支付" + workerType.getName() + "费用", "施工交底", "施工中", "验收环节"};
            if (houseFlow.getWorkType() == 2) {
                rank = 0;
                des = "待抢单";
            } else if (houseFlow.getWorkType() == 3) {
                rank = 1;
                des = "进行中";
            } else if (houseFlow.getWorkType() == 4) {//已支付
                if (houseFlow.getWorkSteta() == 3) {
                    rank = 2;
                    des = "待交底";
                } else if (houseFlow.getWorkSteta() == 4) {
                    rank = 3;
                    des = "施工中";
                } else if (houseFlow.getWorkerType() != 4
                        && houseFlow.getWorkSteta() == 1) {
                    rank = 3;
                    des = "阶段完工";
                } else if (houseFlow.getWorkSteta() == 2) {
                    rank = 4;
                    des = "整体完工";
                } else if (houseFlow.getWorkSteta() == 6) {
                    rank = 4;
                    des = "提前结束装修";
                }
            }
        }
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", titles[i]);
            if (rank < i) {
                map.put("state", 0);//0：未选中，1：当前阶段，2，已过阶段
                map.put("msg", "");
            } else if (rank == i) {
                map.put("state", 1);
                map.put("msg", des);
            } else {
                map.put("state", 2);
                map.put("msg", "已完成");
            }
            mapList.add(map);
        }
        dataMap.put("stageData", mapList);
        return ServerResponse.createBySuccess("查询成功", dataMap);
    }
}

