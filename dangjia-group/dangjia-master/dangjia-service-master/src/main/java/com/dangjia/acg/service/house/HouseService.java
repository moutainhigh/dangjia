package com.dangjia.acg.service.house;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.api.actuary.BudgetWorkerAPI;
import com.dangjia.acg.api.data.ForMasterAPI;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.enums.EventStatus;
import com.dangjia.acg.common.exception.BaseException;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.core.HouseResult;
import com.dangjia.acg.dto.core.NodeDTO;
import com.dangjia.acg.dto.design.QuantityRoomDTO;
import com.dangjia.acg.dto.house.*;
import com.dangjia.acg.mapper.core.*;
import com.dangjia.acg.mapper.house.*;
import com.dangjia.acg.mapper.matter.IRenovationManualMapper;
import com.dangjia.acg.mapper.matter.IRenovationManualMemberMapper;
import com.dangjia.acg.mapper.matter.IRenovationStageMapper;
import com.dangjia.acg.mapper.matter.ITechnologyRecordMapper;
import com.dangjia.acg.mapper.member.ICustomerMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.other.ICityMapper;
import com.dangjia.acg.mapper.other.IWorkDepositMapper;
import com.dangjia.acg.mapper.repair.IMendOrderMapper;
import com.dangjia.acg.mapper.worker.IWorkerDetailMapper;
import com.dangjia.acg.modle.core.*;
import com.dangjia.acg.modle.design.QuantityRoomImages;
import com.dangjia.acg.modle.group.Group;
import com.dangjia.acg.modle.house.*;
import com.dangjia.acg.modle.matter.RenovationManual;
import com.dangjia.acg.modle.matter.RenovationManualMember;
import com.dangjia.acg.modle.matter.RenovationStage;
import com.dangjia.acg.modle.matter.TechnologyRecord;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.member.Customer;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.other.City;
import com.dangjia.acg.modle.other.WorkDeposit;
import com.dangjia.acg.modle.repair.ChangeOrder;
import com.dangjia.acg.modle.repair.MendOrder;
import com.dangjia.acg.modle.worker.WorkerDetail;
import com.dangjia.acg.service.config.ConfigMessageService;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.dangjia.acg.service.core.HouseFlowService;
import com.dangjia.acg.service.design.DesignDataService;
import com.dangjia.acg.service.member.GroupInfoService;
import com.dangjia.acg.util.HouseUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.TextUtils;
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
    private GroupInfoService groupInfoService;
    @Autowired
    private ICityMapper iCityMapper;
    @Autowired
    private IHouseFlowMapper houseFlowMapper;
    @Autowired
    private IWorkerTypeMapper workerTypeMapper;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private IModelingLayoutMapper modelingLayoutMapper;
    @Autowired
    private IModelingVillageMapper modelingVillageMapper;
    //    @Autowired
//    private IHouseDesignImageMapper houseDesignImageMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IRenovationManualMapper renovationManualMapper;
    @Autowired
    private IRenovationStageMapper renovationStageMapper;
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
    private IWorkDepositMapper workDepositMapper;
    protected static final Logger LOG = LoggerFactory.getLogger(HouseService.class);

    /**
     * 切换房产
     */
    public ServerResponse setSelectHouse(String userToken, String cityId, String houseId) {
        AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
        Example example = new Example(House.class);
        example.createCriteria()
                .andEqualTo(House.MEMBER_ID, accessToken.getMember().getId())
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
     * 房产列表
     */
    public ServerResponse getHouseList(String userToken, String cityId) {
        AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
        Example example = new Example(House.class);
        example.createCriteria()
                .andEqualTo(House.MEMBER_ID, accessToken.getMember().getId())
                .andNotEqualTo(House.VISIT_STATE, 0).andNotEqualTo(House.VISIT_STATE, 2)
                .andEqualTo(House.DATA_STATUS, 0);
        List<House> houseList = iHouseMapper.selectByExample(example);
        List<Map<String, String>> mapList = new ArrayList<>();
        for (House house : houseList) {
            Map<String, String> map = new HashMap<>();
            map.put("houseId", house.getId());
            map.put("houseName", house.getHouseName());
            map.put("task", this.getTask(house.getId()) + "");
            mapList.add(map);
        }
        return ServerResponse.createBySuccess("查询成功", mapList);
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
        AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
        Example example = new Example(House.class);
        example.createCriteria()
                .andEqualTo(House.MEMBER_ID, accessToken.getMember().getId())
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
     * APP我的房产
     */
    public ServerResponse getMyHouse(String userToken, String cityId) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member member = (Member) object;
        //该城市该用户所有开工房产
        Example example = new Example(House.class);
        example.createCriteria()
                .andEqualTo(House.MEMBER_ID, member.getId())
                .andNotEqualTo(House.VISIT_STATE, 0).andNotEqualTo(House.VISIT_STATE, 2)
                .andEqualTo(House.DATA_STATUS, 0);
        List<House> houseList = iHouseMapper.selectByExample(example);
        String houseId = null;
        if (houseList.size() > 1) {
            for (House house : houseList) {
                if (house.getIsSelect() == 1) {//当前选中
                    houseId = house.getId();
                    break;
                }
            }
            if (houseId == null) {//有很多房子但是没有isSelect为1的
                houseId = houseList.get(0).getId();
            }
        } else if (houseList.size() == 1) {
            houseId = houseList.get(0).getId();
        } else {
            return ServerResponse.createByErrorCodeMessage(EventStatus.NO_DATA.getCode(), "暂无房产");
        }

        House house = iHouseMapper.selectByPrimaryKey(houseId);
        //统计几套房
        int again = houseList.size();
        HouseResult houseResult = new HouseResult();
        houseResult.setHouseName(house.getHouseName());
        houseResult.setAgain(again);
        houseResult.setHouseId(houseId);
        /*其它房产待处理任务列表状态*/
        int task = 0;
        for (House elseHouse : houseList) {
            if (!elseHouse.getId().equals(houseId)) {
                task += this.getTask(elseHouse.getId());
            }
        }
        houseResult.setTask(task);
        houseResult.setState("00000");
        /**展示各种进度*/
        List<HouseFlow> houseFlowList = houseFlowMapper.getAllFlowByHouseId(houseId);
        List<NodeDTO> courseList = new ArrayList<>();
        if (house.getDecorationType() == 2) {//兼容以前自带设计没有发单的情况
            NodeDTO nodeDTO = new NodeDTO();
            nodeDTO.setNameA("设计师");
            nodeDTO.setColor("#F0643C");
            nodeDTO.setNameC("自带设计");
            nodeDTO.setState(0);
            Map<String, Object> dataMap = HouseUtil.getDesignDatas(house.getDecorationType(), house.getDesignerOk());
            nodeDTO.setTotal((Integer) dataMap.get("total"));
            nodeDTO.setRank((Integer) dataMap.get("rank"));
            nodeDTO.setNameB((String) dataMap.get("nameB"));
            courseList.add(nodeDTO);
        }
        for (HouseFlow houseFlow : houseFlowList) {
            NodeDTO nodeDTO = new NodeDTO();
            WorkerType workerType = workerTypeMapper.selectByPrimaryKey(houseFlow.getWorkerTypeId());
            nodeDTO.setNameA(workerType.getName());
            nodeDTO.setColor(workerType.getColor());
            nodeDTO.setNameC("工序详情");
            String url = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class) +
                    String.format(DjConstants.YZPageAddress.WORKINGDETAILS, userToken, cityId, "工序详情") + "&houseFlowId=" + houseFlow.getId();
            nodeDTO.setUrl(url);
            //0待抢单,4待支付,1已支付待发平面图,5平面图发给业主,6平面图审核不通过,7通过平面图待发施工图,2已发给业主施工图,8施工图片审核不通过,3施工图(全部图)审核通过
            if (workerType.getType() == 1) {//设计
                if (courseList.size() > 0) {//兼容以前自带设计没有发单的情况
                    courseList.remove(0);
                }
                nodeDTO.setState(0);
                Map<String, Object> dataMap = HouseUtil.getDesignDatas(house.getDecorationType(), house.getDesignerOk());
                nodeDTO.setTotal((Integer) dataMap.get("total"));
                nodeDTO.setRank((Integer) dataMap.get("rank"));
                nodeDTO.setNameB((String) dataMap.get("nameB"));
            } else if (workerType.getType() == 2) {//精算
                //默认0未开始,1已开始精算,-1已精算没有发给业主,2已发给业主,3审核通过,4审核不通过
                nodeDTO.setState(0);
                Map<String, Object> dataMap = HouseUtil.getBudgetDatas(house.getBudgetOk());
                nodeDTO.setTotal((Integer) dataMap.get("total"));
                nodeDTO.setRank((Integer) dataMap.get("rank"));
                nodeDTO.setNameB((String) dataMap.get("nameB"));
            } else if (workerType.getType() == 3) {//管家
                //管家状态1未发布,2待抢单,3待支付,4已支付,5完工
                nodeDTO.setState(0);
                nodeDTO.setTotal(5);
                if (houseFlow.getWorkType() == 1) {
                    nodeDTO.setRank(1);
                    nodeDTO.setNameB("未发布");
                } else if (houseFlow.getWorkType() == 2) {
                    nodeDTO.setRank(2);
                    nodeDTO.setNameB("待抢单");
                } else if (houseFlow.getWorkType() == 3) {
                    nodeDTO.setRank(3);
                    nodeDTO.setNameB("待支付");
                } else if (houseFlow.getWorkSteta() == 2) {
                    nodeDTO.setRank(5);
                    nodeDTO.setNameB("整体完工");
                } else if (houseFlow.getWorkType() == 4) {
                    nodeDTO.setRank(4);
                    nodeDTO.setNameB("监工中");
                }
            } else {
                //workType 1还没有发布,2等待被抢，3有工匠抢单,
                //workSteta  3已支付待交底 4施工中 1阶段完工通过,+ 2整体完工通过
                nodeDTO.setState(0);
                if (houseFlow.getWorkerType() == 4) {//拆除没有阶段完工
                    nodeDTO.setTotal(6);//总共点
                } else {
                    nodeDTO.setTotal(7);//总共点
                }
                if (houseFlow.getWorkType() == 1) {
                    nodeDTO.setRank(1);
                    nodeDTO.setNameB("未发布");
                } else if (houseFlow.getWorkType() == 2) {
                    nodeDTO.setRank(2);
                    nodeDTO.setNameB("待抢单");
                } else if (houseFlow.getWorkType() == 3) {
                    nodeDTO.setRank(3);
                    nodeDTO.setNameB("待支付");
                } else if (houseFlow.getWorkType() == 4) {//已支付
                    if (houseFlow.getWorkSteta() == 3) {
                        nodeDTO.setRank(4);
                        nodeDTO.setNameB("待交底");
                    } else if (houseFlow.getWorkSteta() == 4) {
                        nodeDTO.setRank(5);
                        nodeDTO.setNameB("施工中");
                    } else {
                        if (houseFlow.getWorkerType() == 4) {//拆除
                            if (houseFlow.getWorkSteta() == 2) {
                                nodeDTO.setRank(6);
                                nodeDTO.setNameB("整体完工");
                            }
                        } else {
                            if (houseFlow.getWorkSteta() == 1) {
                                nodeDTO.setRank(6);
                                nodeDTO.setNameB("阶段完工");
                            } else if (houseFlow.getWorkSteta() == 2) {
                                nodeDTO.setRank(7);
                                nodeDTO.setNameB("整体完工");
                            }
                        }
                    }
                }
            }
            courseList.add(nodeDTO);
        }
        houseResult.setCourseList(courseList);
        return ServerResponse.createBySuccess("查询成功", houseResult);
    }

    /**
     * 开工页面
     */
    public ServerResponse startWorkPage(HttpServletRequest request, String houseId) {
        HouseDTO houseDTO = iHouseMapper.startWorkPage(houseId);
        if (StringUtil.isNotEmpty(houseDTO.getReferHouseId())) {
            House house = iHouseMapper.selectByPrimaryKey(houseDTO.getReferHouseId());
            houseDTO.setReferHouseName(house.getHouseName());
        }
        return ServerResponse.createBySuccess("查询成功", houseDTO);
    }

    /**
     * 修改房子信息
     */
    public ServerResponse setHouseInfo(House house) {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                    .getRequest();
            House srcHouse = iHouseMapper.selectByPrimaryKey(house.getId());
            if (srcHouse == null)
                return ServerResponse.createByErrorMessage("没有该房子");
            if (!CommonUtil.isEmpty(house.getSiteDisplay())) {
                srcHouse.setSiteDisplay(house.getSiteDisplay());
            }
            if (house.getShowHouse() != -1) {
                srcHouse.setShowHouse(house.getShowHouse());
                if (house.getShowHouse() == 1) {
                    HouseChoiceCase houseChoiceCase = new HouseChoiceCase();
                    houseChoiceCase.setDataStatus(1);
                    houseChoiceCase.setCityId(srcHouse.getCityId());
                    houseChoiceCase.setHouseId(srcHouse.getId());
                    houseChoiceCase.setImage(srcHouse.getImage());
                    houseChoiceCase.setMoney(srcHouse.getMoney());
                    houseChoiceCase.setTitle(srcHouse.getNoNumberHouseName());
                    houseChoiceCase.setLabel(StringUtils.replaceAll(srcHouse.getStyle(), "、", "|"));
                    houseChoiceCase.setSource("房源来自当家装修精选推荐");
                    houseChoiceCaseService.addHouseChoiceCase(request, houseChoiceCase);
                } else {
                    houseChoiceCaseService.delHouseChoiceCase(request, house.getId());
                }
            }
            if (house.getVisitState() != -1) {
                srcHouse.setVisitState(house.getVisitState());
            }
            if (!house.getCustomSort().equals("ignore")) {
                LOG.info("setHouseInfo getCustomSort:" + house.getCustomSort());
                if (StringUtils.isNoneBlank(house.getCustomSort())
                        && StringUtils.isNoneBlank(srcHouse.getCustomSort())) {//如果不问null ，说明已经排序过，就是修改顺序
                    String[] oldWorkerTypeArr = srcHouse.getCustomSort().split(",");
                    String[] newWorkerTypeArr = house.getCustomSort().split(",");
                    LOG.info("setHouseInfo old getCustomSort:" + srcHouse.getCustomSort());

                    Set<String> setNew = new HashSet<>();//新修改的工序类型 的集合
                    Set<String> setDelete = new HashSet<>();//找出被删除的 工序类型 的集合
                    Set<String> setUpdate = new HashSet<>();//找出要修改的 工序类型 的集合
                    for (String newWorkerType : newWorkerTypeArr) {//找出老的，要么有修改，要么有删除的
                        setNew.add(newWorkerType);
                    }
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
            iHouseMapper.updateByPrimaryKey(srcHouse);
            return ServerResponse.createBySuccessMessage("保存成功");
        } catch (
                Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }

    /**
     * WEB确认开工
     */
    public ServerResponse startWork(HttpServletRequest request, HouseDTO houseDTO, String members, String prefixs) {
        if (houseDTO.getDecorationType() >= 3 || houseDTO.getDecorationType() == 0) {
            return ServerResponse.createByErrorMessage("装修类型参数错误");
        }
        if (StringUtils.isEmpty(houseDTO.getHouseId()) || StringUtils.isEmpty(houseDTO.getCityId())
                || StringUtils.isEmpty(houseDTO.getStyle()) || StringUtils.isEmpty(houseDTO.getVillageId())) {
            return ServerResponse.createByErrorMessage("参数为空");
        }
        if (houseDTO.getSquare() <= 0) {
            return ServerResponse.createByErrorMessage("面积错误");
        }
        ModelingLayout modelingLayout = modelingLayoutMapper.selectByPrimaryKey(houseDTO.getModelingLayoutId());

        House house = iHouseMapper.selectByPrimaryKey(houseDTO.getHouseId());
        house.setBuildSquare(new BigDecimal(modelingLayout.getBuildSquare()));//建筑面积
        house.setCityId(houseDTO.getCityId());
        house.setCityName(houseDTO.getCityName());
        house.setVillageId(houseDTO.getVillageId());
        house.setResidential(houseDTO.getResidential());
        house.setModelingLayoutId(houseDTO.getModelingLayoutId());
        house.setBuilding(houseDTO.getBuilding());
        house.setUnit(houseDTO.getUnit());
        house.setNumber(houseDTO.getNumber());
        house.setSquare(new BigDecimal(houseDTO.getSquare()));
        house.setReferHouseId(houseDTO.getReferHouseId());
        house.setStyle(houseDTO.getStyle());
        house.setHouseType(houseDTO.getHouseType());
        house.setDrawings(houseDTO.getDrawings());
        house.setDecorationType(houseDTO.getDecorationType());
        HouseFlow houseFlow;
        try {
            //修改-自带设计和远程设计都需要进行抢单
            //if (houseDTO.getDecorationType() == 1) {//远程设计
            WorkerType workerType = workerTypeMapper.selectByPrimaryKey("1");
            Example example = new Example(HouseFlow.class);
            example.createCriteria()
                    .andEqualTo("houseId", houseDTO.getHouseId())
                    .andEqualTo("workerTypeId", workerType.getId());
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
//            } else if (house.getDecorationType() == 2) {//自带设计,上传施工图先
//                house.setDesignerOk(1);
//            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }

        house.setVisitState(1);//开工成单
        //house.setModifyDate(new Date());
        iHouseMapper.updateByPrimaryKeySelective(house);

        try {
            //开始建群
//            Group group = new Group();
//            group.setHouseId(house.getId());
//            group.setUserId(house.getMemberId());
//            String members = "";//创建群前，配置的成员userid组，多个以逗号分隔，不包含业主的userid组
//            String prefixs = "";//创建群前，配置的成员userid组的前缀，多个以逗号分隔，不包含业主的前缀组
//            groupInfoService.addGroup(request, group, members, prefixs);

            //通知业主确认开工
            configMessageService.addConfigMessage(request, "zx", house.getMemberId(), "0", "装修提醒",
                    String.format(DjConstants.PushMessage.START_FITTING_UP, house.getHouseName()), "");
            //通知设计师/精算师/大管家 抢单
            Example example = new Example(WorkerType.class);
            example.createCriteria().andCondition(WorkerType.TYPE + " in(1,2) ");
            List<WorkerType> workerTypeList = workerTypeMapper.selectByExample(example);
            for (WorkerType workerType : workerTypeList) {
                List<String> workerTypes = new ArrayList<>();
                workerTypes.add("wtId" + workerType.getId());
//                workerTypes.add(house.getId());
                configMessageService.addConfigMessage(request, "gj", StringUtils.join(workerTypes, ","), "0",
                        "新的装修订单", DjConstants.PushMessage.SNAP_UP_ORDER, "4");

            }

            //确认开工后，要修改 业主客服阶段 为已下单
            Customer customer = iCustomerMapper.getCustomerByMemberId(house.getMemberId());
            customer.setStage(4);//阶段: 0未跟进,1继续跟进,2放弃跟进,3黑名单,4已下单
            iCustomerMapper.updateByPrimaryKeySelective(customer);
        } catch (Exception e) {
            System.out.println("建群失败，异常：" + e.getMessage());
        }
        return ServerResponse.createBySuccessMessage("操作成功");
    }

    public ServerResponse revokeHouse(String userToken) {
        AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
        String memberId = accessToken.getMemberId();
        if (StringUtils.isEmpty(memberId)) {
            return ServerResponse.createByErrorMessage("用户id不存在");
        }
        Example example = new Example(House.class);
        example.createCriteria()
                .andEqualTo(House.MEMBER_ID, memberId)
                .andEqualTo(House.DATA_STATUS, 0);
        List<House> houseList = iHouseMapper.selectByExample(example);
        if (houseList.size() > 0) {
            for (House house : houseList) {
                if (house.getVisitState() == 0) { //0待确认开工,1装修中,2休眠中,3已完工
                    //iHouseMapper.deleteByPrimaryKey(house);
                    house.setDataStatus(1);
                    iHouseMapper.updateByPrimaryKeySelective(house);
                    return ServerResponse.createBySuccessMessage("操作成功");
                }
            }
        }
        return ServerResponse.createByErrorMessage("操作失败，无待开工的房子");
    }

    /**
     * APP开始装修
     */
    public ServerResponse setStartHouse(String userToken, String cityId, int houseType, int drawings) {
        AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
        String memberId = accessToken.getMemberId();
        if (StringUtils.isEmpty(memberId)) {
            return ServerResponse.createByErrorMessage("用户id不存在");
        }
        Example example = new Example(House.class);
        example.createCriteria()
                .andEqualTo(House.MEMBER_ID, memberId)
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
                    return ServerResponse.createByErrorMessage("有房子未确认开工,不能再装");
                }
            }
        }
        City city = iCityMapper.selectByPrimaryKey(cityId);
        House house = new House(true);//新增房产信息
        if (houseList.size() > 0) {
            house.setIsSelect(0);
        }
        house.setMemberId(memberId);//用户id
        house.setCityName(city.getName());//城市名
        house.setCityId(cityId);
        house.setAgain(again);//第几套房产
        house.setHouseType(houseType);//装修的房子类型0：新房；1：老房
        house.setDrawings(drawings);//有无图纸0：无图纸；1：有图纸
        house.setWorkDepositId(workDeposits.get(0).getId());
        iHouseMapper.insert(house);
        //房子花费
        HouseExpend houseExpend = new HouseExpend(true);
        houseExpend.setHouseId(house.getId());
        houseExpendMapper.insert(houseExpend);
        return ServerResponse.createBySuccessMessage("操作成功");
    }

    /**
     * 房子装修列表
     */
    public ServerResponse getList(PageDTO pageDTO, Integer visitState, String startDate, String endDate, String searchKey, String orderBy, String memberId) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            if (startDate != null && startDate != "" && endDate != null && endDate != "") {
                if (startDate.equals(endDate)) {
                    startDate = startDate + " " + "00:00:00";
                    endDate = endDate + " " + "23:59:59";
                }
            }
            List<HouseListDTO> houseList = iHouseMapper.getHouseList(memberId, visitState, startDate, endDate, orderBy, searchKey);
            if (houseList.size() <= 0) {
                return ServerResponse.createByErrorCodeMessage(EventStatus.NO_DATA.getCode()
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
                HouseWorkerOrder hwo = houseWorkerOrderMapper.getByHouseIdAndWorkerTypeId(houseId, "2");
                hwo.setHaveMoney(hwo.getWorkPrice());
                houseWorkerOrderMapper.updateByPrimaryKeySelective(hwo);
                //处理精算工钱
                WorkerDetail workerDetail = new WorkerDetail();
                workerDetail.setName("精算通过");
                workerDetail.setWorkerId(hwo.getWorkerId());
                workerDetail.setWorkerName(memberMapper.selectByPrimaryKey(hwo.getWorkerId()).getName());
                workerDetail.setHouseId(hwo.getHouseId());
                workerDetail.setMoney(hwo.getWorkPrice());
                workerDetail.setState(0);//进工钱
                workerDetail.setHaveMoney(hwo.getHaveMoney());
                workerDetail.setHouseWorkerOrderId(hwo.getId());
                workerDetail.setApplyMoney(hwo.getWorkPrice());
                workerDetailMapper.insert(workerDetail);

                //通知大管家抢单
                HouseFlow houseFlow = houseFlowMapper.getHouseFlowByHidAndWty(houseId, 3);
                houseFlow.setWorkType(2);//待抢单
                houseFlow.setReleaseTime(new Date());//发布时间
                houseFlowMapper.updateByPrimaryKeySelective(houseFlow);
                configMessageService.addConfigMessage(null, "gj", "wtId3" + houseFlow.getCityId(), "0", "新的装修订单", DjConstants.PushMessage.SNAP_UP_ORDER, "4");
                //推送消息给业主等待大管家抢单
                configMessageService.addConfigMessage(null, "zx", house.getMemberId(), "0", "等待大管家抢单", String.format(DjConstants.PushMessage.ACTUARIAL_COMPLETION, house.getHouseName()), "");

                /**
                 * 在这里算出大管家每次巡查拿的钱 和 每次验收拿的钱 记录到大管家的 houseflow里 houseflow,新增两个字段.
                 */
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
                Double patrolMoney = 0d;
                if (check > 0) {
                    patrolMoney = moneySup * 0.2 / check;
                }
                //算管家每次验收钱
                Double checkMoney = 0d;
                if (time > 0) {
                    checkMoney = moneySup * 0.3 / time;
                }

                //保存到大管家的houseFlow
                houseFlow.setPatrolMoney(new BigDecimal(patrolMoney));
                houseFlow.setCheckMoney(new BigDecimal(checkMoney));
                houseFlowMapper.updateByPrimaryKeySelective(houseFlow);
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
    public ServerResponse queryHouseByCity(String userToken, String cityId, String villageId, Double minSquare, Double maxSquare, Integer houseType, PageDTO pageDTO) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
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
                    srlist.add(convertHouse(house, accessToken));
                }
            } else {
                if (isReferenceBudget) {
                    houseList = iHouseMapper.getSameLayout(cityId, null, minSquare, maxSquare, houseType);
                    pageResult = new PageInfo(houseList);
                    if (houseList.size() > 0) {//根据条件查询所选小区总价最少的房子
                        for (House house : houseList) {
                            srlist.add(convertHouse(house, accessToken));
                        }
                    } else {
                        return ServerResponse.createByErrorCodeMessage(EventStatus.NO_DATA.getCode(), "查无数据");
                    }
                } else {
                    return ServerResponse.createByErrorCodeMessage(EventStatus.NO_DATA.getCode(), "查无数据");
                }
            }
            pageResult.setList(srlist);
            return ServerResponse.createBySuccess(null, pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("系统出错,获取数据失败");
        }
    }

    private ShareDTO convertHouse(House house, AccessToken accessToken) {
        ModelingLayout ml = modelingLayoutMapper.selectByPrimaryKey(house.getModelingLayoutId());
        ShareDTO shareDTO = new ShareDTO();
        shareDTO.setType("1");
        if (house.getShowHouse() == 0) {
//            if (accessToken != null) {
//                shareDTO.setName(house.getHouseName());
//            } else {
            shareDTO.setName(house.getResidential() + "**" + "栋" + (TextUtils.isEmpty(house.getUnit()) ? "" : house.getUnit() + "单元") + house.getNumber() + "房");
//            }
        } else {
            shareDTO.setName("*栋*单元*号");
        }
        shareDTO.setJianzhumianji("建筑面积:" + (house.getBuildSquare() == null ? "0" : house.getBuildSquare()) + "m²");//建筑面积
        shareDTO.setJvillageacreage("计算面积:" + (house.getSquare() == null ? "0" : house.getSquare()) + "m²");//计算面积
        String biaoqian = house.getLiangDian();//标签
        List<String> biaoqians = new ArrayList<>();
        if (!TextUtils.isEmpty(biaoqian)) {
            for (String s1 : biaoqian.split(",")) {
                if (!TextUtils.isEmpty(s1)) {
                    biaoqians.add(s1);
                }
            }
        }
        biaoqians.add((house.getBuildSquare() == null ? "0" : house.getBuildSquare()) + "m²");
        shareDTO.setBiaoqian(biaoqians);//亮点标签
        BigDecimal money = house.getMoney();
        shareDTO.setPrice("***" + (accessToken != null && money != null && money.toString().length() > 2 ?
                money.toString().substring(money.toString().length() - 2) : "00"));//精算总价
        shareDTO.setShowHouse(house.getShowHouse());
        shareDTO.setHouseId(house.getId());
        shareDTO.setVillageId(house.getVillageId());//小区id
        shareDTO.setVillageName(house.getResidential());//小区名
        shareDTO.setLayoutId(house.getModelingLayoutId());//户型id
        shareDTO.setLayoutleft(ml == null ? "" : ml.getName());//户型名称
        String jobLocationDetail = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class) +
                String.format(DjConstants.YZPageAddress.JOBLOCATIONDETAIL, accessToken != null ? accessToken.getUserToken() : "", house.getCityId(), "施工现场") + "&houseId=" + house.getId();
        shareDTO.setUrl(jobLocationDetail);
        shareDTO.setImageNum(0 + "张图片");
        shareDTO.setImage("");//户型图片
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
                shareDTO.setImage(images.get(0).getImage() + "?x-image-process=image/resize,w_500,h_500/quality,q_80");
                shareDTO.setImageNum(quantityRoomDTO.getImages().size() + "张图片");
            }
        }
        return shareDTO;
    }

    //装修指南
    public ServerResponse getRenovationManual(String userToken, Integer type) {
        try {
            String imgUrl = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            Member member = null;
            if (!CommonUtil.isEmpty(userToken)) {
                Object object = constructionService.getMember(userToken);
                if (object instanceof Member) {
                    member = (Member) object;
                }
            }
            Map<String, Object> returnMap = new HashMap<>();//返回对象
            List<Map<String, Object>> workerTypeList = new ArrayList<>();
            List<RenovationStage> wtList = renovationStageMapper.selectAll();
            for (RenovationStage wt : wtList) {
                List<RenovationManual> listR = renovationManualMapper.getRenovationManualByWorkertyId(wt.getId());
                Map<String, Object> wMap = new HashMap<>();
                wMap.put("workerTypeName", wt.getName());
                wMap.put("image", imgUrl + wt.getImage());
                List<Map<String, Object>> listMap = new ArrayList<>();
                for (RenovationManual r : listR) {
                    Map<String, Object> map = BeanUtils.beanToMap(r);
                    if (member != null) {
                        Example example = new Example(RenovationManualMember.class);
                        example.createCriteria().andEqualTo("renovationManualId", r.getId()).andEqualTo("memberId", member.getId());
                        List<RenovationManualMember> rmList = renovationManualMemberMapper.selectByExample(example);
                        if (rmList.size() > 0) {
                            map.put("isSelect", 1);//选中
                        } else {
                            map.put("isSelect", 0);//未选中
                        }
                    }
                    listMap.add(map);
                }
                wMap.put("rList", listMap);
                workerTypeList.add(wMap);
            }
            returnMap.put("list", workerTypeList);//大list
            return ServerResponse.createBySuccess("获取装修指南成功", returnMap);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("系统出错,获取装修指南失败");
        }
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
                name.append(" " + workerTypeMapper.selectByPrimaryKey(member.getWorkerTypeId()).getName());
                name.append(applyTypeMap.get(hfa.getApplyType()));
                listMap.add(name.toString());
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
     * 施工记录
     */
    public ServerResponse queryConstructionRecordAll(String houseId, PageDTO pageDTO) {
        // 施工记录的内容需要更改
        String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        Example example = new Example(HouseConstructionRecord.class);
        example.createCriteria().andEqualTo(HouseConstructionRecord.HOUSE_ID, houseId);
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        example.orderBy(HouseConstructionRecord.CREATE_DATE).desc();
        List<HouseConstructionRecord> hfaList = houseConstructionRecordMapper.selectByExample(example);
        PageInfo pageResult = new PageInfo(hfaList);
        List<Map<String, Object>> listMap = new ArrayList<>();
        for (HouseConstructionRecord houseConstructionRecord : hfaList) {
            listMap.add(getHouseConstructionRecordMap(houseConstructionRecord, address));
        }
        if (listMap == null) {
            return ServerResponse.createByErrorMessage("系统出错,查询施工记录失败");
        }
        pageResult.setList(listMap);
        return ServerResponse.createBySuccess("查询施工记录成功", pageResult);
    }

    private Map<String, Object> getHouseConstructionRecordMap(HouseConstructionRecord hfa, String address) {

        // 0每日完工申请，1阶段完工申请，2整体完工申请,3停工申请，
        // 4：每日开工,5巡查,6同意停工,7拒绝停工,
        // 8补人工,9退人工,10补材料,11退材料,12业主退材料
        Map<Integer, String> applyTypeMap = DjConstants.RecordType.getRecordTypeMap();

        Map<String, Object> map = new HashMap<>();
        map.put("id", hfa.getSourceId());
        Member member = memberMapper.selectByPrimaryKey(hfa.getWorkerId());
        map.put("workerHead", address + member.getHead());//工人头像
        if (hfa.getWorkerType() != null) {
            map.put("workerTypeName", workerTypeMapper.selectByPrimaryKey(member.getWorkerTypeId()).getName());//工匠类型
        } else {
            map.put("workerTypeName", "业主");//工匠类型
        }
        map.put("workerName", member.getName());//工人名称
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
            map.put("startDate", houseFlowApply.getStartDate());
            map.put("endDate", houseFlowApply.getEndDate());
        }
        if (hfa.getSourceType() == 1) {
            MendOrder mendOrder = mendOrderMapper.selectByPrimaryKey(hfa.getSourceId());
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
        map.put("applyType", applyTypeMap.get(hfa.getApplyType()));
        map.put("createDate", hfa.getCreateDate());
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
        applyTypeMap.put(DjConstants.ApplyType.YOUXIAO_XUNCHA, "巡查");
        applyTypeMap.put(DjConstants.ApplyType.WUREN_XUNCHA, "巡查");
        applyTypeMap.put(DjConstants.ApplyType.ZUIJIA_XUNCHA, "巡查");
//        applyTypeMap.put(DjConstants.ApplyType.JIEDUAN_WANGONG_SUCCESS, "阶段完工审核");
//        applyTypeMap.put(DjConstants.ApplyType.ZHENGTI_WANGONG_SUCCESS, "整体完工审核");
//        applyTypeMap.put(DjConstants.ApplyType.NO_PASS, "审核未通过");
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
        map.put("applyType", applyTypeMap.get(hfa.getApplyType()));
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
                return budgetWorkerAPI.gatEstimateBudgetByHId(request, house.getId());
            }
            return ServerResponse.createByErrorCodeMessage(EventStatus.NO_DATA.getCode(), "暂无所需报价");
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
    public ServerResponse updateByHouseId(String building, String unit, String number, String houseId, String villageId, String cityId, String modelingLayoutId) {
        try {
            House house = iHouseMapper.selectByPrimaryKey(houseId);
            house.setBuilding(building);                 //楼栋
            house.setUnit(unit);                         //单元号
            house.setNumber(number);                     //房间号
            house.setVillageId(villageId);                //小区Id
            house.setCityId(cityId);                      //城市Id
            house.setModelingLayoutId(modelingLayoutId);  //户型Id
            ModelingVillage modelingVillage = modelingVillageMapper.selectByPrimaryKey(villageId);
            house.setResidential(modelingVillage.getName());
            iHouseMapper.updateByPrimaryKeySelective(house);
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
            example.createCriteria().andEqualTo(HouseWorker.HOUSE_ID, houseId).andEqualTo(HouseWorker.WORKER_TYPE_ID, workerTypeId).andNotEqualTo(HouseWorker.WORK_TYPE, 6).andNotEqualTo(HouseWorker.WORK_TYPE, 1);
            example.orderBy(HouseWorker.MODIFY_DATE).desc();
            List<HouseWorker> houseWorkers = houseWorkerMapper.selectByExample(example);
            PageInfo pageResult = new PageInfo(houseWorkers);
            List<HouseWorkDTO> houseWorkDTOS = new ArrayList<>();
            for (HouseWorker h : houseWorkers) {
                HouseWorkDTO houseWorkDTO = new HouseWorkDTO();
                Member member = memberMapper.selectByPrimaryKey(h.getWorkerId());
                Example example1 = new Example(WorkerDetail.class);
                example1.createCriteria().andEqualTo(WorkerDetail.HOUSE_ID, houseId).andEqualTo(WorkerDetail.WORKER_ID, h.getWorkerId());
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
                houseConstructionRecord.setWorkerId(houseFlowApply.getWorkerId());
                houseConstructionRecord.setWorkerType(houseFlowApply.getWorkerType());
                if (houseFlowApply.getApplyType() == 6 || houseFlowApply.getApplyType() == 7) {
                    houseConstructionRecord.setApplyType(5);
                } else {
                    houseConstructionRecord.setApplyType(houseFlowApply.getApplyType());
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
                        if(!CommonUtil.isEmpty(changeOrder.getWorkerTypeId())) {
                            WorkerType workerType = workerTypeMapper.selectByPrimaryKey(changeOrder.getWorkerTypeId());
                            houseConstructionRecord.setWorkerType(workerType.getType());
                        }
                    }
                } else {
                    houseConstructionRecord.setWorkerId(mendOrder.getApplyMemberId());
                    if(!CommonUtil.isEmpty(mendOrder.getWorkerTypeId())) {
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
}

