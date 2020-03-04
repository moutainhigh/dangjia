package com.dangjia.acg.service.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.basics.TechnologyDTO;
import com.dangjia.acg.dto.core.ButtonListBean;
import com.dangjia.acg.dto.core.ConstructionByWorkerIdBean;
import com.dangjia.acg.dto.core.NodeDTO;
import com.dangjia.acg.dto.core.Task;
import com.dangjia.acg.dto.house.HouseOrderDetailDTO;
import com.dangjia.acg.dto.worker.WorkerComprehensiveDTO;
import com.dangjia.acg.mapper.core.*;
import com.dangjia.acg.mapper.delivery.IOrderMapper;
import com.dangjia.acg.mapper.design.IDesignBusinessOrderMapper;
import com.dangjia.acg.mapper.engineer.DjMaintenanceRecordMapper;
import com.dangjia.acg.mapper.engineer.DjMaintenanceRecordProductMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.matter.IMasterTechnologyMapper;
import com.dangjia.acg.mapper.matter.IWorkerEverydayMapper;
import com.dangjia.acg.mapper.member.IMasterMemberAddressMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.menu.IMenuConfigurationMapper;
import com.dangjia.acg.mapper.pay.IBusinessOrderMapper;
import com.dangjia.acg.mapper.product.IMasterProductTemplateMapper;
import com.dangjia.acg.mapper.product.IMasterStorefrontProductMapper;
import com.dangjia.acg.mapper.safe.IWorkerTypeSafeMapper;
import com.dangjia.acg.mapper.safe.IWorkerTypeSafeOrderMapper;
import com.dangjia.acg.mapper.supervisor.ISiteMemoMapper;
import com.dangjia.acg.mapper.worker.IInsuranceMapper;
import com.dangjia.acg.mapper.worker.IWorkIntegralMapper;
import com.dangjia.acg.model.config.DjConfigRuleItemTwo;
import com.dangjia.acg.modle.basics.Technology;
import com.dangjia.acg.modle.brand.Unit;
import com.dangjia.acg.modle.core.*;
import com.dangjia.acg.modle.deliver.Order;
import com.dangjia.acg.modle.design.DesignBusinessOrder;
import com.dangjia.acg.modle.engineer.DjMaintenanceRecord;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.matter.WorkerEveryday;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.member.MemberAddress;
import com.dangjia.acg.modle.pay.BusinessOrder;
import com.dangjia.acg.modle.product.BasicsGoods;
import com.dangjia.acg.modle.product.DjBasicsProductTemplate;
import com.dangjia.acg.modle.safe.WorkerTypeSafe;
import com.dangjia.acg.modle.safe.WorkerTypeSafeOrder;
import com.dangjia.acg.modle.storefront.StorefrontProduct;
import com.dangjia.acg.modle.supervisor.SiteMemo;
import com.dangjia.acg.modle.worker.Insurance;
import com.dangjia.acg.service.configRule.ConfigRuleUtilService;
import com.dangjia.acg.service.design.QuantityRoomService;
import com.dangjia.acg.service.product.MasterProductTemplateService;
import com.dangjia.acg.service.safe.WorkerTypeSafeOrderService;
import com.dangjia.acg.util.HouseUtil;
import com.dangjia.acg.util.StringTool;
import com.dangjia.acg.util.Utils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author Ruking.Cheng
 * @descrilbe 新版工匠施工相关实现
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/4/22 2:34 PM
 */
@Service
public class CraftsmanConstructionService {
    private static Logger logger = Logger.getLogger(CraftsmanConstructionService.class);

    @Autowired
    private ISiteMemoMapper iSiteMemoMapper;
    @Autowired
    private IMenuConfigurationMapper iMenuConfigurationMapper;
    @Autowired
    private RedisClient redisClient;//缓存
    @Autowired
    private IHouseWorkerMapper houseWorkerMapper;
    @Autowired
    private IHouseFlowMapper houseFlowMapper;
    @Autowired
    private IHouseMapper houseMapper;

    @Autowired
    private DjMaintenanceRecordMapper djMaintenanceRecordMapper;
    @Autowired
    private DjMaintenanceRecordProductMapper djMaintenanceRecordProductMapper;

    @Autowired
    private IWorkerTypeSafeMapper workerTypeSafeMapper;
    @Autowired
    private IWorkerTypeSafeOrderMapper workerTypeSafeOrderMapper;
    @Autowired
    private IMemberMapper memberMapper;
    @Autowired
    private IHouseFlowApplyMapper houseFlowApplyMapper;
    @Autowired
    private IHouseWorkerOrderMapper houseWorkerOrderMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IWorkerTypeMapper workerTypeMapper;
    @Autowired
    private IWorkerEverydayMapper workerEverydayMapper;
    @Autowired
    private IDesignBusinessOrderMapper designBusinessOrderMapper;

    @Autowired
    private IWorkIntegralMapper workIntegralMapper;
    @Autowired
    private ConfigRuleUtilService configRuleUtilService;
    @Autowired
    private IInsuranceMapper insuranceMapper;
    @Autowired
    private QuantityRoomService quantityRoomService;
    @Autowired
    private IOrderMapper iOrderMapper;
    @Autowired
    private IBusinessOrderMapper iBusinessOrderMapper;
    @Autowired
    private TaskStackService taskStackService;
    @Autowired
    private IMasterProductTemplateMapper iMasterProductTemplateMapper;
    @Autowired
    private IMasterTechnologyMapper masterTechnologyMapper;
    @Autowired
    private IMasterBasicsGoodsMapper iMasterBasicsGoodsMapper;
    @Autowired
    private IMasterStorefrontProductMapper iMasterStorefrontProductMapper;
    @Autowired
    private IMasterUnitMapper iMasterUnitMapper;
    @Autowired
    private MasterProductTemplateService masterProductTemplateService;
    @Autowired
    private IMasterMemberAddressMapper iMasterMemberAddressMapper;

    @Value("${spring.profiles.active}")
    private String active;

    /**
     * 获取施工页面
     *
     * @param userToken userToken
     * @return 施工页面信息
     */
    public ServerResponse getConstructionView(HttpServletRequest request, String userToken, String houseWorkerId) {
        ConstructionByWorkerIdBean bean = new ConstructionByWorkerIdBean();//公用返回体
        Object object = getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member worker = (Member) object;
        if (worker.getWorkerType() == null) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), "请上传资料");
        }
        HouseWorker hw = null;
        if (CommonUtil.isEmpty(houseWorkerId)) {
            object = getHouseWorker(bean, worker.getId());
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            hw = (HouseWorker) object;
        } else {
            hw = houseWorkerMapper.selectByPrimaryKey(houseWorkerId);
        }
        if (hw == null) {
            return ServerResponse.createByErrorMessage("订单不存在");
        }
        bean.setHouseWorkerId(hw.getId());
        bean.setHouseWorkerType(hw.getType());
        if (hw.getType() == 0) {
            House house = houseMapper.selectByPrimaryKey(hw.getHouseId());//查询房产信息
            if (house == null) {
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), "房产信息不存在");
            }
            HouseFlow hf = houseFlowMapper.getByWorkerTypeId(hw.getHouseId(), hw.getWorkerTypeId());//查询自己的任务状态
            if (hf == null) {
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), "没有查到该任务");
            }
            bean.setHouseId(house.getId());
            bean.setHouseName(house.getHouseName());
            bean.setHouseSquare(house.getSquare().doubleValue());
            Example example = new Example(MemberAddress.class);
            example.createCriteria().andEqualTo(MemberAddress.HOUSE_ID, house.getId());
            MemberAddress memberAddress = iMasterMemberAddressMapper.selectOneByExample(example);
            if (memberAddress != null && StringUtils.isNotBlank(memberAddress.getAddress())) {
                bean.setHouseName(memberAddress.getAddress());
            }

            if (house.getSquare() != null) {
                DjConfigRuleItemTwo configRuleItemTwo = configRuleUtilService.getApartmentConfig(house.getSquare());
                if (configRuleItemTwo != null) {
                    bean.setApartmentName(configRuleItemTwo.getFieldName());
                }
            }
            switch (worker.getWorkerType()) {
                case 1://设计师
                    return getDesignerBean(request, bean, hw, house, hf);
                case 2://精算师
                    return getActuariesBean(request, bean, hw, worker, house, hf);
                case 3://大管家
                    return getHousekeeperBean(request, bean, hw, worker, house, hf, hw.getType());
                default://工匠
                    return getCraftsmanBean(request, bean, hw, worker, house, hf, hw.getType());
            }
        }

        if (hw.getType() == 1) {

            List<ButtonListBean> buttonList = new ArrayList<>();
            Order order = iOrderMapper.selectByPrimaryKey(hw.getBusinessId());//查询房产信息
            if (order == null) {
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), "体验单不存在");
            }
            bean.setHouseId(order.getId());
            MemberAddress memberAddress = iMasterMemberAddressMapper.selectByPrimaryKey(order.getAddressId());
            if (memberAddress != null && StringUtils.isNotBlank(memberAddress.getAddress())) {
                bean.setHouseName(memberAddress.getAddress());
            }
            bean.setWorkerType(worker.getWorkerType());//0:大管家；1：工匠；2：设计师；3：精算师
            Example example = new Example(HouseWorker.class);
            example.createCriteria().andCondition(" work_type in(6,8) ").andEqualTo(HouseWorker.TYPE, hw.getType()).andEqualTo(HouseWorker.BUSINESS_ID, order.getId());
            List<HouseWorker> houseWorkers = houseWorkerMapper.selectByExample(example);
            HouseWorker houseWorker = null;
            if (houseWorkers.size() > 0) {
                houseWorker = houseWorkers.get(0);
                bean.setHouseFlowId(houseWorker.getId());
                if (houseWorker.getWorkType() == 6) {
                    bean.setAlreadyMoney(new BigDecimal(0));//已得钱
                    bean.setAlsoMoney(houseWorker.getPrice());//已得钱
                } else {
                    bean.setAlreadyMoney(houseWorker.getPrice());//已得钱
                    bean.setAlsoMoney(new BigDecimal(0));//还可得钱
                }
            }
            Member houseMember = memberMapper.selectByPrimaryKey(order.getMemberId());//业主
            if (houseMember != null) {
                bean.setHouseMemberName(houseMember.getNickName());//业主名称
                bean.setHouseMemberPhone(houseMember.getMobile());//业主电话
                bean.setUserId(houseMember.getId());//
            }
            //查询我的单
            List<HouseOrderDetailDTO> houseOrderDetailDTOList = houseMapper.getBudgetOrderDetailByInFo(order.getId(), null, hw.getType());
            List<Map<String, Object>> mapDataList = new ArrayList<>();
            if (houseOrderDetailDTOList != null && houseOrderDetailDTOList.size() > 0) {
                String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
                for (HouseOrderDetailDTO houseOrderDetailDTO : houseOrderDetailDTOList) {
                    setProductInfo(houseOrderDetailDTO, address);
                    Map<String, Object> dataMap = BeanUtils.beanToMap(houseOrderDetailDTO);
                    dataMap.put("totalNodeNumber", 1);//总节点数
                    if (houseWorker != null && houseWorker.getWorkType() == 6) {
                        dataMap.put("completedNodeNumber", 0);//已完成节点数(已完成)
                        dataMap.put("labelName", "待验房");//节点名称


                        JSONObject paramVal = new JSONObject();
                        paramVal.put("orderItemId", houseOrderDetailDTO.getOrderItemId());
                        paramVal.put("orderStatus", 4);
                        buttonList.add(Utils.getButton("上传验房结果", 7001));
                    } else {
                        dataMap.put("completedNodeNumber", 1);//已完成节点数(已完成)
                        dataMap.put("labelName", "已完成");//节点名称
                    }
                    mapDataList.add(dataMap);
                }
                //获取维保id
                bean.setBusinessId(houseOrderDetailDTOList.get(0).getBusinessId());
            }
            bean.setDataList(mapDataList);
            bean.setButtonList(buttonList);
            return ServerResponse.createBySuccess("获取施工列表成功", bean);
        }

        if (hw.getType() == 2) {
            String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            List<ButtonListBean> buttonList = new ArrayList<>();
            DjMaintenanceRecord record = djMaintenanceRecordMapper.selectByPrimaryKey(hw.getBusinessId());
            //查询保险订单对应的工种

            WorkerType workerType = workerTypeMapper.selectByPrimaryKey(hw.getWorkerTypeId());
            House house = houseMapper.selectByPrimaryKey(record.getHouseId());
            bean.setHouseId(house.getId());
            bean.setHouseName(house.getHouseName());
            bean.setWorkerType(worker.getWorkerType());//0:大管家；1：工匠；2：设计师；3：精算师
            bean.setWorkerTypeColor(workerType.getColor());
            bean.setWorkerTypeName(workerType.getName());
            bean.setWorkerTypeImage(imageAddress+workerType.getImage());

            if(record.getOverProtection()==1){
                bean.setWorkerTypeSlogan(workerType.getName()+"已过保");
            }else {
                bean.setWorkerTypeSlogan(workerType.getName()+"免费质保");
            }
            bean.setWorkerTypeColor(workerType.getColor());
            bean.setHouseFlowId(hw.getId());
            if (hw.getWorkType() == 6) {
                bean.setAlreadyMoney(new BigDecimal(0));//已得钱
                bean.setAlsoMoney(hw.getPrice());//已得钱
            } else {
                bean.setAlreadyMoney(hw.getPrice());//已得钱
                bean.setAlsoMoney(new BigDecimal(0));//还可得钱
            }
            Member houseMember = memberMapper.selectByPrimaryKey(house.getMemberId());//业主
            if (houseMember != null) {
                bean.setHouseMemberName(houseMember.getNickName());//业主名称
                bean.setHouseMemberPhone(houseMember.getMobile());//业主电话
                bean.setUserId(houseMember.getId());//
            }
            List<Map<String, Object>> workerTypeList = djMaintenanceRecordProductMapper.selectWorkerTypeListById(record.getId(), 2);//查询是否已添加勘查费用的商品
            if (record.getState() == null && workerTypeList.size() > 0 && worker.getWorkerType() == 3) {//大管家勘查
                buttonList.add(Utils.getButton("上传勘查结果", 6001));
            }
            if (record.getState() == null) {//工匠开工/结束
                buttonList.add(Utils.getButton("提前结束", 6002));
                buttonList.add(Utils.getButton("已确认可开工", 6003));
            }
            if (record.getState() == 5) {//工匠验收、报销
                buttonList.add(Utils.getButton("申请报销", 6004));
                buttonList.add(Utils.getButton("申请验收", 6005));
            }
            //查询我的单
            List<HouseOrderDetailDTO> houseOrderDetailDTOList = djMaintenanceRecordProductMapper.getBudgetOrderDetailByInFo(record.getId());
            List<Map<String, Object>> mapDataList = new ArrayList<>();
            if (houseOrderDetailDTOList != null && houseOrderDetailDTOList.size() > 0) {
                String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
                for (HouseOrderDetailDTO houseOrderDetailDTO : houseOrderDetailDTOList) {
                    setProductInfo(houseOrderDetailDTO, address);
                    Map<String, Object> dataMap = BeanUtils.beanToMap(houseOrderDetailDTO);
                    dataMap.put("totalNodeNumber", 1);//总节点数
                    if (hw != null && hw.getWorkType() == 6) {
                        dataMap.put("completedNodeNumber", 0);//已完成节点数(已完成)
                        dataMap.put("labelName", "未维修");//节点名称
                    } else {
                        dataMap.put("completedNodeNumber", 1);//已完成节点数(已完成)
                        dataMap.put("labelName", "已完成");//节点名称
                    }
                    mapDataList.add(dataMap);
                }
                //获取维保id
                bean.setBusinessId(hw.getBusinessId());
            }
            setMenus(bean, house, -1);

            bean.setDataList(mapDataList);
            bean.setButtonList(buttonList);
            return ServerResponse.createBySuccess("获取施工列表成功", bean);
        }
        return ServerResponse.createBySuccessMessage("OK");
    }

    /**
     * 设计师
     */
    private ServerResponse getDesignerBean(HttpServletRequest request, ConstructionByWorkerIdBean bean, HouseWorker hw, House house, HouseFlow hf) {
        bean.setWorkerType(2);//0:大管家；1：工匠；2：设计师；3：精算师
        bean.setHouseFlowId(hf.getId());
        bean.setDecorationType(house.getDecorationType());
        bean.setDesignerOk(house.getDesignerOk());
        setMoney(bean, hw);
        Member houseMember = memberMapper.selectByPrimaryKey(house.getMemberId());//业主
        if (houseMember != null) {
            bean.setHouseMemberName(houseMember.getNickName());//业主名称
            bean.setHouseMemberPhone(houseMember.getMobile());//业主电话
            bean.setUserId(houseMember.getId());//
        }
        setMenus(bean, house, hf.getWorkerType());
//        Map<String, Object> dataMap = HouseUtil.getDesignDatas(house);
//        bean.setDataList((List<Map<String, Object>>) dataMap.get("dataList"));
        //查询设计师的订单数据
        List<HouseOrderDetailDTO> houseOrderDetailDTOList = houseMapper.getBudgetOrderNewInfo(house.getId(), "1");
        bean.setDataList(getBudgetDataList(houseOrderDetailDTOList, house, 1));//查询已购买的设计师的商品
        List<ButtonListBean> buttonList = new ArrayList<>();
//        if (house.getVisitState() == 1 && house.getDesignerState() != 0 && house.getDesignerState() != 4 && house.getDesignerState() != 3) {
//            String webAddress = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class);
//            String data = "&houseId=" + house.getId() + "&houseFlowId=" + hf.getId();
//            buttonList.add(Utils.getButton("提前结束", webAddress + "construction?title=填写原因" + data, 88));
//        }
        if (house.getVisitState() == 1) {
            if (house.getDecorationType() != 2 && house.getDesignerState() == 1) {
                buttonList.add(Utils.getButton("去量房", 1001));
            } else {
                switch (house.getDesignerState()) {
                    case 1://1已支付-设计师待量房
                    case 9://9量房图发给业主
                        buttonList.add(Utils.getButton("上传平面图", 1002));
                        break;
                    case 6://6平面图审核不通过（NG，可编辑平面图）
                        buttonList.add(Utils.getButton("修改平面图", 1003));
                        break;
                    case 7://7通过平面图待发施工图（OK，可编辑施工图）
                        buttonList.add(Utils.getButton("上传施工图", 1004));
                        break;
                    case 8://8施工图片审核不通过（NG，可编辑施工图）
                        buttonList.add(Utils.getButton("修改施工图", 1005));
                        break;
                    case 3://3设计图完成后有需要改设计的
                        Example example = new Example(DesignBusinessOrder.class);
                        Example.Criteria criteria = example.createCriteria()
                                .andEqualTo(DesignBusinessOrder.DATA_STATUS, 0)
                                .andEqualTo(DesignBusinessOrder.HOUSE_ID, house.getId())
                                .andEqualTo(DesignBusinessOrder.STATUS, 1)
                                .andNotEqualTo(DesignBusinessOrder.OPERATION_STATE, 2);
                        if (house.getDecorationType() != 2) {
                            criteria.andEqualTo(DesignBusinessOrder.TYPE, 4);
                        } else {
                            criteria.andEqualTo(DesignBusinessOrder.TYPE, 3);
                        }
                        List<DesignBusinessOrder> designBusinessOrders = designBusinessOrderMapper.selectByExample(example);
                        if (designBusinessOrders != null && designBusinessOrders.size() > 0) {
                            DesignBusinessOrder order = designBusinessOrders.get(0);
                            if (order.getOperationState() == 0) {
                                buttonList.add(Utils.getButton("上传设计图", 1006));
                            }
                        }
                        break;
                }
            }
        }
        bean.setButtonList(buttonList);
        return ServerResponse.createBySuccess("获取施工列表成功！", bean);
    }


    /**
     * 精算师
     */
    private ServerResponse getActuariesBean(HttpServletRequest request, ConstructionByWorkerIdBean bean, HouseWorker hw, Member worker, House house, HouseFlow hf) {
        bean.setWorkerType(3);//0:大管家；1：工匠；2：设计师；3：精算师
        bean.setHouseFlowId(hf.getId());
        setMoney(bean, hw);
        Member houseMember = memberMapper.selectByPrimaryKey(house.getMemberId());//业主
        if (houseMember != null) {
            bean.setHouseMemberName(houseMember.getNickName());//业主名称
            bean.setHouseMemberPhone(houseMember.getMobile());//业主电话
            bean.setUserId(houseMember.getId());//
        }
        setMenus(bean, house, hf.getWorkerType());
        //查询精算师的订单数据
        List<HouseOrderDetailDTO> houseOrderDetailDTOList = houseMapper.getBudgetOrderDetailByInFo(house.getId(), "2", null);
        bean.setDataList(getBudgetDataList(houseOrderDetailDTOList, house, 2));//查询已购买的精算师的商品
        List<ButtonListBean> buttonList = showActuaryButton(house.getId());//按钮显示
        bean.setButtonList(buttonList);
        return ServerResponse.createBySuccess("获取施工列表成功！", bean);
    }

    /**
     * 转换成符合条件的订单数据
     *
     * @param houseOrderDetailDTOList
     * @param house
     * @return
     */
    List<Map<String, Object>> getBudgetDataList(List<HouseOrderDetailDTO> houseOrderDetailDTOList, House house, int workertype) {
        List<Map<String, Object>> mapDataList = new ArrayList<>();
        if (houseOrderDetailDTOList != null && houseOrderDetailDTOList.size() > 0) {
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            for (HouseOrderDetailDTO houseOrderDetailDTO : houseOrderDetailDTOList) {
                setProductInfo(houseOrderDetailDTO, address);
                Map<String, Object> dataMap = BeanUtils.beanToMap(houseOrderDetailDTO);
                NodeDTO nodeDTO = new NodeDTO();
                if (workertype == 1) {
                    nodeDTO = HouseUtil.getDesignDatas(house, nodeDTO);
                } else {
                    nodeDTO = HouseUtil.getBudgetDatas(house, nodeDTO);
                }
                dataMap.put("completedNodeNumber", nodeDTO.getRank());//已完成节点数(已完成)
                dataMap.put("totalNodeNumber", nodeDTO.getTotal());//精算总节点数
                dataMap.put("labelName", nodeDTO.getNameB());//节点名称
                mapDataList.add(dataMap);
            }
        }
        return mapDataList;
    }

    /**
     * 替换对应的信息
     *
     * @param ap
     * @param address
     */
    private void setProductInfo(HouseOrderDetailDTO ap, String address) {
        String productTemplateId = ap.getProductTemplateId();
        DjBasicsProductTemplate pt = iMasterProductTemplateMapper.selectByPrimaryKey(productTemplateId);
        if (pt != null && StringUtils.isNotBlank(pt.getId())) {
            String image = ap.getImage();
            if (image != null) {
                //添加图片详情地址字段
                String[] imgArr = image.split(",");
                if (imgArr != null && imgArr.length > 0) {
                    ap.setImageUrl(address + imgArr[0]);//图片详情地址设置
                }
            }

            String unitId = pt.getUnitId();
            //查询单位
            if (pt.getConvertQuality() != null && pt.getConvertQuality() > 0) {
                unitId = pt.getConvertUnit();
            }
            if (unitId != null && StringUtils.isNotBlank(unitId)) {
                Unit unit = iMasterUnitMapper.selectByPrimaryKey(unitId);
                ap.setUnitId(unitId);
                ap.setUnitName(unit != null ? unit.getName() : "");
            }
            //查询规格名称
            if (StringUtils.isNotBlank(pt.getValueIdArr())) {
                ap.setValueIdArr(pt.getValueIdArr());
                ap.setValueNameArr(masterProductTemplateService.getNewValueNameArr(pt.getValueIdArr()).replaceAll(",", " "));
            }
        }

    }

    /**
     * 判断移动端精算当前需显示的按钮
     *
     * @param houseId
     * @return showButtonType(2001显示装修信息按钮 2002审核图纸 2003请等待业主选择 2004请等待设计师制作完毕 2005已结束 2006上传图纸 2007上传图纸 ）
     */
    public List<ButtonListBean> showActuaryButton(String houseId) {

        List<ButtonListBean> buttonList = new ArrayList<>();
        try {
            //1.判断业主购买的精算单是否为已退款状态
            List<HouseOrderDetailDTO> orderInfo = houseMapper.getBudgetOrderNewInfo(houseId, "2");
            List<HouseOrderDetailDTO> houseOrderDetailDTOList = houseMapper.getBudgetOrderDetailByInFo(houseId, "2", null);//判断是否有精算订单
            if ((orderInfo == null || orderInfo.size() == 0) && (houseOrderDetailDTOList != null && houseOrderDetailDTOList.size() > 0)) {
                buttonList.add(Utils.getButton("已结束", 2005));
                return buttonList;
            }
            //2.判断是否有待付款的补差价订单
            orderInfo = houseMapper.getBudgetDifferenceOrder(houseId, "2");
            if (orderInfo != null && orderInfo.size() > 0) {
                buttonList.add(Utils.getButton("请等待业主选择", 2003));
                return buttonList;
            }
            //3.判断是否显示确认装修信息按钮
            ServerResponse serverResponse = quantityRoomService.isConfirmAddress(houseId);
            if (serverResponse.getResultObj() != null) {
                String obj = serverResponse.getResultObj().toString();//0:未确认地址，1：已经确认地址
                if ("0".equals(obj)) {//如果未确认过地址，则显示确认地址按钮，精算师上传地址
                    buttonList.add(Utils.getButton("确认装修信息", 2001));
                    return buttonList;
                }
            }
            //4.判断是否显示审核图纸按钮
            House house = houseMapper.selectByPrimaryKey(houseId);
            if (house.getBudgetOk() == 1 && house.getDesignerOk() == 0) {//如果图纸未审核通过，且未有设计师
                //4.1判断业主是否购买了当家平台设计，以及对应的支付状态
                houseOrderDetailDTOList = houseMapper.getBudgetOrderDetailByInFo(houseId, "1", null);
                if (houseOrderDetailDTOList != null && houseOrderDetailDTOList.size() > 0) {//购买了当家平台设计
                    //判断当前设计单的状态
                    HouseOrderDetailDTO houseOrderDetailDTO = houseOrderDetailDTOList.get(0);
                    Order order = iOrderMapper.selectByPrimaryKey(houseOrderDetailDTO.getOrderId());
                    Example example = new Example(BusinessOrder.class);
                    example.createCriteria().andEqualTo(BusinessOrder.NUMBER, order.getBusinessOrderNumber());
                    BusinessOrder businessOrder = iBusinessOrderMapper.selectOneByExample(example);
                    if (businessOrder.getState() == 3) {
                        buttonList.add(Utils.getButton("请等待设计师制作完毕", 2004));
                    } else {
                        buttonList.add(Utils.getButton("请等待业主选择", 2003));
                    }
                } else {//未购买当家平台设计
                    //判断业主是否已做了选择
                    List<Task> list = taskStackService.selectTaskStackInfoByType(houseId, "6");//是否有待审核图纸的任务
                    if (list != null && list.size() > 0) {
                        buttonList.add(Utils.getButton("请等待设计师制作完毕", 2004));
                    } else {
                        buttonList.add(Utils.getButton("审核图纸", 2002));
                    }
                }

            } else if (house.getBudgetOk() == 1 && house.getDesignerOk() == 3) {//如果精算师是已抢单，且设计师为已完成，则显示审核图纸按钮
                buttonList.add(Utils.getButton("审核图纸", 2002));
            } else if (house.getBudgetOk() == 1 && house.getDesignerOk() != 0 && house.getDesignerOk() != 3) {//若精算师为已他单，设计师为已抢单未完成状态
                buttonList.add(Utils.getButton("请等待设计师制作完毕", 2004));
            } else if (house.getBudgetOk() == 6 && house.getDesignerOk() != 7 && house.getDesignerOk() != 3) {//精算师为已审核完成，设计师为未审核,先上传平面图
                buttonList.add(Utils.getButton("上传平面图", 2006));
            } else if (house.getBudgetOk() == 6 && house.getDesignerOk() == 7) {//精算师为已审核完成，设计师为未审核,先上传施工图
                buttonList.add(Utils.getButton("上传施工图", 2007));
            }

        } catch (Exception e) {
            logger.error("查询失败", e);
        }
        return buttonList;
    }

    /**
     * 大管家
     */
    private ServerResponse getHousekeeperBean(HttpServletRequest request, ConstructionByWorkerIdBean bean, HouseWorker hw, Member worker, House house, HouseFlow hf, Integer type) {
        bean.setWorkerType(0);//0:大管家；1：工匠；2：设计师；3：精算师
        bean.setHouseFlowId(hf.getId());
        setMoney(bean, hw);
        Member houseMember = memberMapper.selectByPrimaryKey(house.getMemberId());//业主
        if (houseMember != null) {
            bean.setHouseMemberName(houseMember.getNickName());//业主名称
            bean.setHouseMemberPhone(houseMember.getMobile());//业主电话
            bean.setUserId(houseMember.getId());//
        }

        //查询我的单
        List<HouseOrderDetailDTO> houseOrderDetailDTOList = houseMapper.getBudgetOrderDetailByInFo(house.getId(), hw.getWorkerTypeId(), type);
        List<Map<String, Object>> mapDataList = new ArrayList<>();
        if (houseOrderDetailDTOList != null && houseOrderDetailDTOList.size() > 0) {
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            for (HouseOrderDetailDTO houseOrderDetailDTO : houseOrderDetailDTOList) {
                setProductInfo(houseOrderDetailDTO, address);
                Map<String, Object> dataMap = BeanUtils.beanToMap(houseOrderDetailDTO);
                dataMap.put("totalNodeNumber", houseMapper.queryArrNumber(houseOrderDetailDTO.getProductTemplateId()));//总节点数
                dataMap.put("completedNodeNumber", houseMapper.queryTestNumber(houseOrderDetailDTO.getProductTemplateId(), house.getId(), hw.getWorkerId()));//已完成节点数(已完成)
                dataMap.put("labelName", houseMapper.getNoTechnologyName(houseOrderDetailDTO.getProductTemplateId(), house.getId(), hw.getWorkerId()));//节点名称
                mapDataList.add(dataMap);
            }
            //获取维保id
            bean.setBusinessId(houseOrderDetailDTOList.get(0).getBusinessId());
        }
        bean.setDataList(mapDataList);

        Example example = new Example(HouseFlow.class);
        example.createCriteria()
                .andGreaterThan(HouseFlow.WORKER_TYPE, 2)
                .andEqualTo(HouseFlow.HOUSE_ID, house.getId());
        example.orderBy(HouseFlow.SORT).desc();
        List<HouseFlow> houseFlows = houseFlowMapper.selectByExample(example);
        int all = 0;
        for (HouseFlow flow : houseFlows) {
            if (flow.getWorkerType() != 3 && flow.getPatrol() != null) {
                all = all + flow.getPatrol();
            }
        }
        Long allPatrol = houseFlowApplyMapper.countPatrol(house.getId(), null);
        bean.setActualPatrol("实际巡查次数:" + (allPatrol == null ? 0 : allPatrol));
        bean.setAllPatrol("标准巡查次数:" + all);
        bean.setIfBackOut(1);//0可放弃；1：申请停工；2：已停工 3 审核中


        setMenus(bean, house, hf.getWorkerType());
        List<String> promptList = new ArrayList<>();//消息提示list
        List<ButtonListBean> buttonList = new ArrayList<>();
        List<ConstructionByWorkerIdBean.WokerFlowListBean> workerFlowList = new ArrayList<>();
        boolean houseIsStart = false;
        //当业主支付大管家费用并且确认开工之后之后才出现
        if (hf.getWorkerType() == 3 && hf.getWorkType() == 4 && hf.getSupervisorStart() == 1) {
            example = new Example(HouseFlow.class);
            example.createCriteria()
                    .andEqualTo(HouseFlow.STATE, 0)
                    .andEqualTo(HouseFlow.HOUSE_ID, hw.getHouseId())
                    .andNotEqualTo(HouseFlow.WORKER_TYPE, 1)//排除设计师
                    .andNotEqualTo(HouseFlow.WORKER_TYPE, 2)//排除精算师
                    .andNotEqualTo(HouseFlow.WORKER_TYPE, 3);//排除大管家
            example.orderBy(HouseFlow.SORT).asc();
            List<HouseFlow> hfList = houseFlowMapper.selectByExample(example);//查询该房产下的工序
            for (HouseFlow hfl : hfList) {
                ConstructionByWorkerIdBean.WokerFlowListBean wfr = new ConstructionByWorkerIdBean.WokerFlowListBean();
                example = new Example(HouseWorker.class);
                example.createCriteria()
                        .andEqualTo(HouseWorker.HOUSE_ID, house.getId())
                        .andEqualTo(HouseWorker.WORKER_TYPE_ID, hfl.getWorkerTypeId())
                        .andEqualTo(HouseWorker.WORK_TYPE, 6);
                List<HouseWorker> hwList = houseWorkerMapper.selectByExample(example);//根据房子id和工匠type查询房子对应的工人
                HouseWorker houseWorker = new HouseWorker();
                if (hwList.size() > 0) {
                    houseWorker = hwList.get(0);
                }
                Member worker2 = memberMapper.selectByPrimaryKey(houseWorker.getWorkerId());
                WorkerType workerType = workerTypeMapper.selectByPrimaryKey(hfl.getWorkerTypeId());
                wfr.setHouseFlowId(hfl.getId());//进程id
                wfr.setWorkerType(workerType.getType());//进程类型
                wfr.setWorkerTypeName(workerType == null ? "" : workerType.getName());//大进程名
                wfr.setWorkerTypeColor(workerType == null ? "" : workerType.getColor());//工人id

                if (worker2 != null) {
                    wfr.setWorkerName(worker2 == null ? "" : worker2.getName());//工人名称
                    wfr.setWorkerId(worker2 == null ? "" : worker2.getId());//工人id
                    wfr.setWorkerPhone(worker2 == null ? "" : worker2.getMobile());//工人手机
                    WorkerComprehensiveDTO workerComprehensive = workIntegralMapper.getComprehensiveWorker(worker.getId());
                    wfr.setOverall(workerComprehensive.getOverall());
                    wfr.setRaiseRate(worker.getPraiseRate() == null ? "0.00%" : worker.getPraiseRate().multiply(new BigDecimal(100)) + "%");
                    example = new Example(HouseWorker.class);
                    example.createCriteria()
                            .andEqualTo(HouseWorker.WORKER_ID, wfr.getWorkerId())
                            .andEqualTo(HouseWorker.TYPE, 0)
                            .andIn(HouseWorker.WORK_TYPE, Arrays.asList(1, 6, 8));
                    Integer orderTakingNum = houseWorkerMapper.selectCountByExample(example);
                    wfr.setOrderTakingNum(orderTakingNum);
                    HouseFlowApply todayStart = houseFlowApplyMapper.getTodayStart(house.getId(), worker2 == null ? "" : worker2.getId(), new Date());//查询今日开工记录
                    if (todayStart == null) {//没有今日开工记录
                        wfr.setIsStart(0);//今日是否开工0:否；1：是；
                    } else {
                        wfr.setIsStart(1);//今日是否开工0:否；1：是；
                    }
                }
                if (hfl.getWorkType() < 2) {//未发布工种抢单
                    wfr.setButtonTitle("未进场");//按钮提示
                    wfr.setState(0);
                } else if (hfl.getWorkType() < 4) {//待抢单和已抢单
                    wfr.setButtonTitle("正在进场");//按钮提示
                    wfr.setState(1);
                } else if (hfl.getWorkType() == 5) {//业主待支付
                    wfr.setButtonTitle("等待业主支付");//按钮提示
                    wfr.setState(1);
                } else if (hfl.getWorkSteta() == 3) {
                    wfr.setButtonTitle("去交底");
                    wfr.setState(2);
                } else if ((hfl.getWorkType() == 4 && hfl.getWorkSteta() == 0) || hfl.getWorkSteta() == 4) {
                    wfr.setButtonTitle("施工中");
                    wfr.setState(3);
                } else if (hfl.getWorkSteta() == 1) {
                    wfr.setButtonTitle("已阶段完工");
                    wfr.setState(4);
                } else if (hfl.getWorkSteta() == 5) {
                    wfr.setButtonTitle("收尾施工中");
                    wfr.setState(5);
                } else if (hfl.getWorkSteta() == 2 || hfl.getWorkSteta() == 6) {
                    if (hfl.getWorkSteta() == 2) {
                        wfr.setButtonTitle("已整体完工");
                    } else {
                        wfr.setButtonTitle("提前竣工");
                    }
                    wfr.setState(6);
                }
                wfr.setTotalNodeNumber(7);
                if (workerType.getType() == 4) {//拆除少无阶段完工，减去1节点数
                    wfr.setTotalNodeNumber(6);
                }
                wfr.setCompletedNodeNumber(wfr.getState());//当前完成的节点数
                workerFlowList.add(wfr);
            }
        }
        bean.setHouseIsStart(houseIsStart ? "今日已开工" : "今日未开工");
        bean.setWokerFlowList(workerFlowList);
        if (hf.getWorkType() == 3) {//如果是已抢单待支付。则提醒业主支付
            bean.setIfBackOut(0);
            promptList.add("请联系业主支付您的大管家费用");
        }

        //查询是否全部整体完工
        List<HouseFlow> checkFinishList = houseFlowMapper.checkAllFinish(hf.getHouseId(), hf.getId());
        //查询是否提前结束装修
        example = new Example(HouseFlow.class);
        example.createCriteria().andEqualTo(HouseFlow.HOUSE_ID, hf.getHouseId()).andGreaterThanOrEqualTo(HouseFlow.WORKER_TYPE, 3);
        for (HouseFlow h : houseFlows) {
            if (h.getWorkSteta() == 6) {
                checkFinishList.clear();
                break;
            }
        }
        //查询是否今天已经上传过巡查
        List<HouseFlowApply> houseFlowApplyList = houseFlowApplyMapper.getTodayPatrol(hf.getHouseId(), new Date());
        if (active != null && !active.equals("pre")) {
            if (!((houseFlowApplyList.size() & 1) == 1)) {
                houseFlowApplyList = new ArrayList<>();
            }
        }
        if (hf.getSupervisorStart() == 0) {//已开工之后都是巡查工地；1：巡查工地2：申请业主验收；3:确认开工
            List<HouseFlow> listStart = houseFlowMapper.getHouseIsStart(hf.getHouseId());
            if (listStart.size() > 0) {
                hf.setSupervisorStart(1);//改为开工状态(兼容老数据)
                houseFlowMapper.updateByPrimaryKeySelective(hf);
                buttonList.add(Utils.getButton("巡查工地", 3001));
            } else if (hf.getWorkType() == 4) {//支付之后显示按钮
                if ("0".equals(house.getSchedule())) {
                    buttonList.add(Utils.getButton("确认开工", 3002));//需要去生成日历
                } else {
                    buttonList.add(Utils.getButton("确认开工", 3003));//确认开工
                }
            }
        } else if (checkFinishList.size() == 0) {//所有工种都整体完工，申请业主验收
            if (house.getHaveComplete() == 1) {
                promptList.add("该房子已竣工!");
                String url = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class) +
                        "takeMoneyDetailed?title=拿钱明细&houseId=" + house.getId() +
                        "&houseFlowId=" + hf.getId() + "&houseName=" + house.getHouseName();
                buttonList.add(Utils.getButton("查看拿钱明细", url, 3005));
            } else {
                HouseFlowApply houseFlowApp = houseFlowApplyMapper.checkSupervisorApply(hf.getId(), worker.getId());//查询大管家是否有验收申请
                if (houseFlowApp == null) {//没有发验收申请
                    buttonList.add(Utils.getButton("申请业主验收", 3004));
                } else {
                    if (houseFlowApp.getMemberCheck() == 4) {
                        promptList.add("业主要求整改");
                    } else {
                        promptList.add("您已提交业主验收申请，请耐心等待业主审核！");
                    }

                }
            }
        }  else {
            buttonList.add(Utils.getButton("巡查工地", 3001));
        }
        bean.setPromptList(promptList);
        bean.setButtonList(buttonList);
        return ServerResponse.createBySuccess("获取施工列表成功", bean);
    }


    /**
     * 工匠
     */
    private ServerResponse getCraftsmanBean(HttpServletRequest request, ConstructionByWorkerIdBean bean, HouseWorker hw,
                                            Member worker, House house, HouseFlow hf, Integer type) {
        Example example = new Example(Insurance.class);
        example.createCriteria().andEqualTo(Insurance.WORKER_ID, hw.getWorkerId()).andIsNotNull(Insurance.END_DATE);
        example.orderBy(Insurance.END_DATE).desc();
        List<Insurance> insurances = insuranceMapper.selectByExample(example);
        //保险服务剩余天数小于等于60天
        int daynum = 0;
        if (insurances.size() > 0) {
            daynum = DateUtil.daysofTwo(new Date(), insurances.get(0).getEndDate());
        }
        boolean isBX = true;
        //工人未购买保险
        if (hw.getWorkerType() > 2 && (insurances.size() == 0 || daynum <= 60)) {
            isBX = false;
        }

        //查询我的单
        List<HouseOrderDetailDTO> houseOrderDetailDTOList = houseMapper.getBudgetOrderDetailByInFo(house.getId(), hw.getWorkerTypeId(), type);
        List<Map<String, Object>> mapDataList = new ArrayList<>();
        if (houseOrderDetailDTOList != null && houseOrderDetailDTOList.size() > 0) {
            //获取维保id
            bean.setBusinessId(houseOrderDetailDTOList.get(0).getBusinessId());
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            for (HouseOrderDetailDTO houseOrderDetailDTO : houseOrderDetailDTOList) {
                setProductInfo(houseOrderDetailDTO, address);
                Map<String, Object> dataMap = BeanUtils.beanToMap(houseOrderDetailDTO);
                dataMap.put("totalNodeNumber", houseMapper.queryArrNumber(houseOrderDetailDTO.getProductTemplateId()));//总节点数
                dataMap.put("completedNodeNumber", houseMapper.queryTestNumber(houseOrderDetailDTO.getProductTemplateId(), house.getId(), hw.getWorkerId()));//已完成节点数(已完成)
                dataMap.put("labelName", houseMapper.getNoTechnologyName(houseOrderDetailDTO.getProductTemplateId(), house.getId(), hw.getWorkerId()));//节点名称
                mapDataList.add(dataMap);
            }
        }
        bean.setDataList(mapDataList);

        //查询工序节点
        example = new Example(HouseFlowApply.class);
        example.createCriteria().andEqualTo(HouseFlowApply.HOUSE_FLOW_ID, hf.getId())
                .andCondition(" apply_type in(1,2)")
                .andNotEqualTo(HouseFlowApply.MEMBER_CHECK, 1);
        example.orderBy(HouseFlowApply.CREATE_DATE).desc();
        List<HouseFlowApply> houseFlowApplies = houseFlowApplyMapper.selectByExample(example);
        if (houseFlowApplies != null && houseFlowApplies.size() > 0) {
            int zhengTiNum = 0;
            int jieDuanNum = 0;
            for (HouseFlowApply houseFlowApply : houseFlowApplies) {
                if (houseFlowApply.getApplyType() == 1) {
                    jieDuanNum++;
                }
                if (houseFlowApply.getApplyType() == 2) {
                    zhengTiNum++;
                }
            }
            if (houseFlowApplies.get(0).getApplyType() == 1) {
                bean.setTrialNumber(jieDuanNum);
                bean.setNodeTitle("阶段完工审核(第" + CommonUtil.numberToChinese(jieDuanNum) + "次)");
            }
            if (houseFlowApplies.get(0).getApplyType() == 2) {
                bean.setTrialNumber(zhengTiNum);
                bean.setNodeTitle("阶段完工审核(第" + CommonUtil.numberToChinese(jieDuanNum) + "次)");
            }
            if (houseFlowApplies.get(0).getMemberCheck() != 1) {
                //node 1-工匠发起 2-大管家审核通过 3-业主审核通过
                bean.setNode(1);
                if (houseFlowApplies.get(0).getSupervisorCheck() == 1) {
                    bean.setNode(2);
                }
            }
        }

        //查询质保金是否为负数
        Member member = memberMapper.selectByPrimaryKey(hf.getWorkerId());
        if (member.getRetentionMoney().intValue() < 0) {
            bean.setRetentionType(0);
        } else {
            bean.setRetentionType(1);
        }

        bean.setWorkerType(1);//0:大管家；1：工匠；2：设计师；3：精算师
        bean.setHouseFlowId(hf.getId());
        setMoney(bean, hw);
        //房产信息
//        HouseWorker supervisorWorker = houseWorkerMapper.getHwByHidAndWtype(hf.getHouseId(), 3);//查询大管家的
//        if (supervisorWorker != null) {
//            Member workerSup = memberMapper.selectByPrimaryKey(supervisorWorker.getWorkerId());//查询大管家
//            if (workerSup != null) {
//                bean.setSupervisorName(workerSup.getName());//大管家名字
//                bean.setSupervisorPhone(workerSup.getMobile());
//                bean.setUserId(workerSup.getId());
//                bean.setSupervisorEvation("积分:" + workerSup.getEvaluationScore());//大管家积分
//                Long supervisorCountOrder = houseWorkerMapper.getCountOrderByWorkerId(workerSup.getId());
//                if (supervisorCountOrder != null)
//                    bean.setSupervisorCountOrder("总单数:" + supervisorCountOrder);//大管家总单数
//                bean.setSupervisorPraiseRate("好评率:" + workerSup.getPraiseRate().multiply(new BigDecimal(100)) + "%");//大管家好评率
//            }
//        }
        if (hf.getPause() == 1) {//已暂停  停工有两种情况需要处理
            bean.setIfBackOut(2);
        } else {
            bean.setIfBackOut(1);
        }
        setMenus(bean, house, hf.getWorkerType());
        List<String> promptList = new ArrayList<>();//消息提示list
        List<ButtonListBean> buttonList = new ArrayList<>();
        List<HouseFlowApply> earliestTimeList = houseFlowApplyMapper.getEarliestTimeHouseApply(house.getId(), worker.getId());
        HouseFlowApply earliestTime = null;
        if (earliestTimeList.size() > 0) {
            earliestTime = earliestTimeList.get(0);
        }
        HouseFlowApply checkFlowApp = houseFlowApplyMapper.checkHouseFlowApply(hf.getId(), worker.getId());//根据工种任务id和工人id查询此工人待审核
        Long suspendDay = houseFlowApplyMapper.getSuspendApply(house.getId(), worker.getId());//根据房子id和工人id查询暂停天数
        Long everyEndDay = houseFlowApplyMapper.getEveryDayApply(house.getId(), worker.getId());//根据房子id和工人id查询每日完工申请天数
        long totalDay = 0;
        if (earliestTime != null) {
            Date EarliestDay = earliestTime.getCreateDate();//最早开工时间
            Date newDate = new Date();
            totalDay = 1 + DateUtil.daysofTwo(EarliestDay, newDate);//计算当前时间隔最早开工时间相差多少天
            if (suspendDay != null) {
                totalDay = totalDay - suspendDay;
                if (totalDay <= 0) totalDay = 0;
            }
        }
        bean.setTotalDay("总开工天数" + totalDay);
        bean.setEveryDay("每日完工天数" + (everyEndDay == null ? "0" : everyEndDay));
        bean.setSuspendDay("暂停天数" + (suspendDay == null ? "0" : suspendDay));
        if (hw.getWorkType() == 1) {
            bean.setIfDisclose(0);
        } else if (hf.getWorkSteta() == 3) {
            bean.setIfDisclose(1);
        } else {
            bean.setIfDisclose(2);
        }
        if (hf.getWorkType() == 3) {//如果是已抢单待支付。则提醒业主支付
            if (isBX) {
                promptList.add("请联系业主支付您的工匠费用");
            } else {
                Date d = DateUtil.addDateMinutes(hw.getCreateDate(), 30);
                Date d2 = new Date();
                promptList.add("剩余支付保险时间：" + DateUtil.getDiffTime2(d.getTime(), d2.getTime()) + ",超过时间则自动放弃");
                buttonList.add(Utils.getButton("购买保险", 4001));
            }
            bean.setIfBackOut(0);//0可放弃；1：申请停工；2：已停工 3 审核中
        } else if (hf.getPause() == 1) {
            promptList.add("您已停工");
        } else if (hf.getWorkSteta() == 1) {
            promptList.add("您已阶段完工");
        }
        if (hf.getWorkSteta() == 2 || hf.getWorkSteta() == 6) {
            if (hf.getWorkSteta() == 2) {
                promptList.add("您已整体完工");
            } else {
                promptList.add("该房子已提前结束装修,您的工钱已自动入账！");
            }
            bean.setIfBackOut(2);
            buttonList.add(Utils.getButton("查看拿钱明细", 4002));
        } else if (hf.getWorkType() == 4) {
//            if(!isBX) {
//                buttonList.add(Utils.getButton("购买保险", 4001));
//            }else
            if (hf.getWorkSteta() == 3) {//待交底
                buttonList.add(Utils.getButton("找大管家交底", 4003));
//            } else if (worker.getWorkerType() == 4) {//如果是拆除，只有整体完工
//                setDisplayState(hf, promptList, buttonList, checkFlowApp, true);
            } else {//已交底
                bean.setFootMessageTitle("");//每日开工事项
                bean.setFootMessageDescribe("");//每日开工事项
                HouseFlowApply todayStart = houseFlowApplyMapper.getTodayStart(house.getId(), worker.getId(), new Date());//查询今日开工记录
                List<ConstructionByWorkerIdBean.BigListBean.ListMapBean> workerEverydayList = new ArrayList<>();
                if (todayStart == null) {//没有今日开工记录
                    buttonList.add(Utils.getButton("今日开工", 4004));
                    List<WorkerEveryday> listWorDay = workerEverydayMapper.getWorkerEverydayList(1);//事项类型  1 开工事项 2 完工事项
                    for (WorkerEveryday day : listWorDay) {
                        ConstructionByWorkerIdBean.BigListBean.ListMapBean listMapBean = new ConstructionByWorkerIdBean.BigListBean.ListMapBean();
                        listMapBean.setName(day.getName());
                        workerEverydayList.add(listMapBean);
                    }
                    bean.setFootMessageTitle("今日开工任务");//每日开工事项
                    bean.setFootMessageDescribe("（每日十二点前今日开工）");//每日开工事项
                } else {
                    List<HouseFlowApply> allAppList = houseFlowApplyMapper.getTodayHouseFlowApply(hf.getId(), 2, worker.getId(), new Date());//查询今天是否已提交整体完工
                    List<HouseFlowApply> stageAppList = houseFlowApplyMapper.getTodayHouseFlowApply(hf.getId(), 1, worker.getId(), new Date());//查询今天是否已提交阶段完工
                    List<HouseFlowApply> flowAppList = houseFlowApplyMapper.getTodayHouseFlowApply(hf.getId(), 0, worker.getId(), new Date());//查询是否已提交今日完工
                    if (allAppList.size() > 0) {
                        promptList.add("今日已申请整体完工");
                        bean.setIfBackOut(2);
                    } else if (stageAppList.size() > 0) {
                        promptList.add("今日已申请阶段完工");
                        bean.setIfBackOut(2);
                    } else if (flowAppList != null && flowAppList.size() > 0) {//已提交今日完工
                        promptList.add("今日已完工");
//                        bean.setIfBackOut(2);
                    } else {
                        buttonList.add(Utils.getButton("今日完工", 4005));
                        List<WorkerEveryday> listWorDay = workerEverydayMapper.getWorkerEverydayList(2);//事项类型  1 开工事项 2 完工事项
                        for (WorkerEveryday day : listWorDay) {
                            ConstructionByWorkerIdBean.BigListBean.ListMapBean listMapBean = new ConstructionByWorkerIdBean.BigListBean.ListMapBean();
                            listMapBean.setName(day.getName());
                            workerEverydayList.add(listMapBean);
                        }
                        bean.setFootMessageTitle("今日完工任务");//每日开工事项
                        bean.setFootMessageDescribe("");//每日开工事项
                        if (hf.getWorkSteta() == 1 || worker.getWorkerType() == 4) {
                            setDisplayState(hf, promptList, buttonList, checkFlowApp, true);
                        } else {
                            setDisplayState(hf, promptList, buttonList, checkFlowApp, false);
                        }
                    }
                }
//                if (buttonList.size() <= 0) {
//                    buttonList.add(Utils.getButton("今日开工", 4004));
//                    bean.setFootMessageTitle("今日开工任务");//每日开工事项
//                    bean.setFootMessageDescribe("（每日十二点前今日开工）");//每日开工事项
//                    List<WorkerEveryday> listWorDay = workerEverydayMapper.getWorkerEverydayList(1);//事项类型  1 开工事项 2 完工事项
//                    for (WorkerEveryday day : listWorDay) {
//                        ConstructionByWorkerIdBean.BigListBean.ListMapBean listMapBean = new ConstructionByWorkerIdBean.BigListBean.ListMapBean();
//                        listMapBean.setName(day.getName());
//                        workerEverydayList.add(listMapBean);
//                    }
//                }
                bean.setWorkerEverydayList(workerEverydayList);//每日完工事项
            }
        }

        bean.setPromptList(promptList);
        bean.setButtonList(buttonList);
        return ServerResponse.createBySuccess("获取施工列表成功", bean);
    }


    /**
     * 设置菜单
     */
    private void setMenus(ConstructionByWorkerIdBean bean, House house, Integer workerType) {
        String imageAddress = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        List<ConstructionByWorkerIdBean.BigListBean> bigList = new ArrayList<>();
        ConstructionByWorkerIdBean.BigListBean bigListBean = new ConstructionByWorkerIdBean.BigListBean();
        bigListBean.setName("我的工具");
        if (workerType == 1) {//设计师菜单
            bigListBean.setListMap(bigListBean.getMenus(imageAddress, bigListBean.sheJi));
            bigList.add(bigListBean);
        } else if (workerType == 2) {//精算师菜单
            if (house.getDecorationType() == 1) {//远程设计菜单
                bigListBean.setListMap(bigListBean.getMenus(imageAddress, bigListBean.jingSuanYc));
                bigList.add(bigListBean);
            }
            if (house.getDecorationType() == 2) {//自带设计菜单
                bigListBean.setListMap(bigListBean.getMenus(imageAddress, bigListBean.jingSuanZd));
                bigList.add(bigListBean);
            }
        } else if (workerType == 3) {//大管家菜单
            Example example = new Example(SiteMemo.class);
            example.createCriteria().andEqualTo(SiteMemo.TYPE, 1).andEqualTo(SiteMemo.HOUSE_ID, house.getId())
                    .andCondition(" DATE_FORMAT(create_date, '%x年-第%v周' ) = DATE_FORMAT( SYSDATE(), '%x年-第%v周' ) ");
            Integer num = iSiteMemoMapper.selectCountByExample(example);//本周是否有周计划
            if (num > 0) {//是否设置了本周计划
                bigListBean.setListMap(bigListBean.getMenus(imageAddress, bigListBean.daGuanJiaY));
                bigList.add(bigListBean);
            } else {
                bigListBean.setListMap(bigListBean.getMenus(imageAddress, bigListBean.daGuanJiaN));
                bigList.add(bigListBean);
            }
            //工地记录菜单
            ConstructionByWorkerIdBean.BigListBean bigListBean2 = new ConstructionByWorkerIdBean.BigListBean();
            bigListBean2.setName("工地记录");
            bigListBean2.setListMap(bigListBean2.getMenus(imageAddress, bigListBean2.daGuanJiaG));
            bigList.add(bigListBean2);
        } else {//工匠菜单
            bigListBean.setListMap(bigListBean.getMenus(imageAddress, bigListBean.gongJiang));
            bigList.add(bigListBean);
        }
        bean.setBigList(bigList);//添加菜单到返回体中
    }


    /**
     * 获取已得钱和还可得钱
     */
    private void setMoney(ConstructionByWorkerIdBean bean, HouseWorker hw) {
        HouseWorkerOrder hwo = houseWorkerOrderMapper.getHouseWorkerOrder(hw.getHouseId(), hw.getWorkerId(), hw.getWorkerTypeId());
        if (hwo == null) {
            bean.setAlreadyMoney(new BigDecimal(0));//已得钱
            bean.setAlsoMoney(new BigDecimal(0));//还可得钱
        } else {
            BigDecimal workPrice = hwo.getWorkPrice() == null ? new BigDecimal(0) : hwo.getWorkPrice();//总共钱
            BigDecimal haveMoney = hwo.getHaveMoney() == null ? new BigDecimal(0) : hwo.getHaveMoney();//已得到的钱
            BigDecimal repairPrice = hwo.getRepairPrice() == null ? new BigDecimal(0) : hwo.getRepairPrice();//当前阶段补人工钱
            BigDecimal repairTotalPrice = hwo.getRepairTotalPrice() == null ? new BigDecimal(0) : hwo.getRepairTotalPrice();//补人工总钱
            BigDecimal retentionMoney = hwo.getRetentionMoney() == null ? new BigDecimal(0) : hwo.getRetentionMoney();//滞留金
            BigDecimal deductPrice = hwo.getDeductPrice() == null ? new BigDecimal(0) : hwo.getDeductPrice();//评价积分扣除的钱
            //总共钱-已得到的钱+补人工钱-滞留金-评价扣的钱=还可得钱
            BigDecimal alsoMoney = new BigDecimal(workPrice.doubleValue() - haveMoney.doubleValue() + repairPrice.doubleValue() - retentionMoney.doubleValue() - deductPrice.doubleValue());
            if (alsoMoney.doubleValue() < 0) {
                alsoMoney = new BigDecimal(0);
            }
            bean.setAlsoMoney(alsoMoney);//还可得钱

            //已得到的钱+滞留金的钱+（补人工总钱-当前阶段补人工钱）=已得总钱
            BigDecimal alreadyMoney = new BigDecimal(haveMoney.doubleValue() + retentionMoney.doubleValue() + (repairTotalPrice.doubleValue() - repairPrice.doubleValue()));
            bean.setAlreadyMoney(alreadyMoney);//已得钱
        }
    }

    /**
     * 显示当前需要申请的状态
     *
     * @param hf           自己的任务状态
     * @param promptList   消息
     * @param buttonList   按钮
     * @param checkFlowApp 此工人待审核申请
     * @param isShow       true 整体完工 false 阶段完工
     */
    private void setDisplayState(HouseFlow hf, List<String> promptList, List<ButtonListBean> buttonList, HouseFlowApply checkFlowApp, boolean isShow) {
        if (isShow) {//整体完工
            if (checkFlowApp == null) {
                if (hf.getWorkSteta() != 2) {
                    buttonList.add(Utils.getButton("申请整体完工", 4006));
                }
            } else if (checkFlowApp.getSupervisorCheck() == 0) {
                promptList.add("已申请整体完工,等待大管家审核");
            } else if (checkFlowApp.getSupervisorCheck() == 1) {
                if (checkFlowApp.getMemberCheck() == 4) {
                    promptList.add("业主要求整改");
                } else {
                    promptList.add("大管家已审核您的整体完工,待业主审核");
                }
            }
        } else {//阶段完工申请
            if (checkFlowApp == null) {
                if (hf.getWorkSteta() != 1 && hf.getWorkSteta() != 2) {
                    buttonList.add(Utils.getButton("申请阶段完工", 4007));
                }
            } else if (checkFlowApp.getSupervisorCheck() == 0) {
                promptList.add("已申请阶段完工,等待大管家审核");
            } else if (checkFlowApp.getSupervisorCheck() == 1) {
                promptList.add("大管家已审核您的阶段完工,待业主审核");
            }
        }
    }


    /**
     * 获取用户信息
     *
     * @param userToken userToken
     * @return Member/ServerResponse
     */
    public Object getMember(String userToken) {
        if (CommonUtil.isEmpty(userToken)) {
            return ServerResponse.createbyUserTokenError();
        }
        AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
        if (accessToken == null) {
            Member worker = memberMapper.selectByPrimaryKey(userToken);
            if (worker != null) {
                return worker;
            }
            return ServerResponse.createbyUserTokenError();
        }
        Member worker = accessToken.getMember();
        if (worker == null) {
            return ServerResponse.createbyUserTokenError();
        }
        return worker;
    }

    public Object getAccessToken(String userToken) {
        if (CommonUtil.isEmpty(userToken)) {
            return ServerResponse.createbyUserTokenError();
        }
        AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
        if (accessToken == null) {
            return ServerResponse.createbyUserTokenError();
        }
        return accessToken;
    }

    /**
     * 获取工匠当前的施工单
     *
     * @param workerId 工匠ID
     * @return HouseWorker/ServerResponse
     */
    public Object getHouseWorker(ConstructionByWorkerIdBean bean, String workerId) {
        Example example = new Example(HouseWorker.class);
        example.createCriteria()
                .andEqualTo(HouseWorker.DATA_STATUS, 0)
                .andEqualTo(HouseWorker.WORKER_ID, workerId)
                .andCondition(" work_type in (1,6) ")
                .andEqualTo(HouseWorker.IS_SELECT, 1);
        example.orderBy(HouseWorker.MODIFY_DATE).desc();
        List<HouseWorker> selectList = houseWorkerMapper.selectByExample(example);//查询选中
        HouseWorker hw;
        if (selectList != null && selectList.size() > 0) {
            hw = selectList.get(0);
        } else {
            example = new Example(HouseWorker.class);
            example.createCriteria()
                    .andEqualTo(HouseWorker.DATA_STATUS, 0)
                    .andEqualTo(HouseWorker.WORKER_ID, workerId)
                    .andCondition(" work_type in (1,6) ");
            example.orderBy(HouseWorker.MODIFY_DATE).desc();
            List<HouseWorker> houseWorkerList = houseWorkerMapper.selectByExample(example);//查询选中
            if (houseWorkerList == null || houseWorkerList.size() <= 0) {
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), "您暂无施工中的记录,快去接单吧！");
            }
            if (bean != null) {
                int count = 0;
                for (HouseWorker houseWorker : houseWorkerList) {//循环所有订单任务
                    List<HouseFlowApply> supervisorCheckList = houseFlowApplyMapper.getSupervisorCheckList(houseWorker.getHouseId());//查询所有待大管家审核
                    count += supervisorCheckList.size();
                }
                bean.setTaskNumber(count);//总任务数量
            }
            hw = houseWorkerList.get(0);
            hw.setIsSelect(1);//设置成默认
            houseWorkerMapper.updateByPrimaryKeySelective(hw);
        }
        return hw;
    }

    /**
     * 工匠-工地进度详情
     */
    public ServerResponse getConstructionInfo(HttpServletRequest request, String userToken, String houseId, String houseFlowId) {
        Object object = getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member worker = (Member) object;
        if (worker.getWorkerType() == null || worker.getWorkerType() != 3) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), "非大管家，无权访问");
        }
        String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
        HouseFlow hfl = houseFlowMapper.selectByPrimaryKey(houseFlowId);//查询该房产下的工序
        ConstructionByWorkerIdBean.WokerFlowListBean wfr = new ConstructionByWorkerIdBean.WokerFlowListBean();
        Example example = new Example(HouseWorker.class);
        example.createCriteria()
                .andEqualTo(HouseWorker.HOUSE_ID, hfl.getHouseId())
                .andEqualTo(HouseWorker.WORKER_TYPE_ID, hfl.getWorkerTypeId())
                .andEqualTo(HouseWorker.WORK_TYPE, 6);
        List<HouseWorker> hwList = houseWorkerMapper.selectByExample(example);//根据房子id和工匠type查询房子对应的工人
        HouseWorker houseWorker = new HouseWorker();
        if (hwList.size() > 0) {
            houseWorker = hwList.get(0);
        }
        Member worker2 = memberMapper.selectByPrimaryKey(hfl.getWorkerId());
        WorkerType workerType = workerTypeMapper.selectByPrimaryKey(hfl.getWorkerTypeId());
        wfr.setHouseFlowId(hfl.getId());//进程id
        wfr.setWorkerType(workerType.getType());//进程类型
        wfr.setWorkerTypeId(workerType.getId());//进程ID
        wfr.setWorkerTypeName(workerType == null ? "" : workerType.getName());//大进程名
        wfr.setWorkerName(worker2 == null ? "" : worker2.getName());//工人名称
        wfr.setWorkerId(worker2 == null ? "" : worker2.getId());//工人id
        wfr.setWorkerHead(worker2 == null ? "" : imageAddress + worker2.getHead());
        wfr.setWorkerTypeColor(workerType == null ? "" : workerType.getColor());//工人id
        WorkerComprehensiveDTO workerComprehensive = workIntegralMapper.getComprehensiveWorker(worker.getId());
        wfr.setOverall(workerComprehensive.getOverall());
        wfr.setRaiseRate(worker.getPraiseRate() == null ? "0.00%" : worker.getPraiseRate().multiply(new BigDecimal(100)) + "%");
        wfr.setWorkerPhone(worker2 == null ? "" : worker2.getMobile());//工人手机
        wfr.setPatrolSecond("" + houseFlowApplyMapper.countPatrol(hfl.getHouseId(), worker2 == null ? "0" : worker2.getWorkerTypeId()));//工序巡查次数
        wfr.setPatrolStandard("" + (hfl.getPatrol() == null ? 0 : hfl.getPatrol()));//巡查标准
        example = new Example(HouseWorker.class);
        example.createCriteria()
                .andEqualTo(HouseWorker.WORKER_ID, wfr.getWorkerId())
                .andEqualTo(HouseWorker.TYPE, 0)
                .andIn(HouseWorker.WORK_TYPE, Arrays.asList(1, 6, 8));
        Integer orderTakingNum = houseWorkerMapper.selectCountByExample(example);
        wfr.setOrderTakingNum(orderTakingNum);
        HouseFlowApply todayStart = houseFlowApplyMapper.getTodayStart(hfl.getHouseId(), worker2 == null ? "" : worker2.getId(), new Date());//查询今日开工记录
        if (todayStart == null) {//没有今日开工记录
            wfr.setIsStart(0);//今日是否开工0:否；1：是；
        } else {
            wfr.setIsStart(1);//今日是否开工0:否；1：是；
        }
        List<ButtonListBean> topButton = new ArrayList<>();//头部按钮
        List<ButtonListBean> footButton = new ArrayList<>();//底部按钮
        JSONObject paramVal = new JSONObject();
        HouseFlowApply houseFlowApp = houseFlowApplyMapper.checkHouseFlowApply(hfl.getId(), worker2 == null ? "" : worker2.getId());//根据工种任务id和工人id查询此工人待审核
        if (houseFlowApp != null && houseFlowApp.getApplyType() == 1) {//阶段完工申请
            if (houseFlowApp.getSupervisorCheck() == 0) {
                paramVal.put("houseFlowApplyId", houseFlowApp.getId());
                footButton.add(Utils.getButton("审核阶段完工", JSON.toJSONString(paramVal), 3021));
            }
            topButton.add(Utils.getButton("奖罚", 3011));
            if (houseWorker != null && houseWorker.getWorkType() != null && (houseWorker.getWorkType() == 2 || houseWorker.getWorkType() == 3 || houseWorker.getWorkType() == 4)) {
                topButton.add(Utils.getButton("换人审核中", 3013));
            } else {
                topButton.add(Utils.getButton("更换工匠", 3012));
            }
            wfr.setState(4);//装修进度0：未进场；1：待审核工匠；2：待交底；3：施工中；4：阶段完工；5：收尾施工；6：整体完工
        } else if (houseFlowApp != null && houseFlowApp.getApplyType() == 2 && houseFlowApp.getSupervisorCheck() == 0) {
            paramVal.put("houseFlowApplyId", houseFlowApp.getId());
            footButton.add(Utils.getButton("审核整体完工", JSON.toJSONString(paramVal), 3022));
            wfr.setState(6);
        } else {
            topButton.add(Utils.getButton("申请停工", 3010));
            topButton.add(Utils.getButton("奖罚", 3011));
            if (houseWorker != null && houseWorker.getWorkType() != null && (houseWorker.getWorkType() == 2 || houseWorker.getWorkType() == 3 || houseWorker.getWorkType() == 4)) {
                topButton.add(Utils.getButton("换人审核中", 3013));
            } else {
                topButton.add(Utils.getButton("更换工匠", 3012));
            }
            if (hfl.getWorkType() < 2) {//未发布工种抢单
                footButton.add(Utils.getButton("提前进场", 3023));
                wfr.setButtonTitle("未进场");//按钮提示
                wfr.setState(0);
            } else if (hfl.getWorkType() < 4) {//待抢单和已抢单
                wfr.setButtonTitle("正在进场");//按钮提示
                wfr.setState(1);
            } else if (hfl.getWorkType() == 5) {//业主待支付
                wfr.setButtonTitle("等待业主支付");//按钮提示
                wfr.setState(1);
            } else if (hfl.getWorkSteta() == 3) {
                footButton.add(Utils.getButton("生成二维码", 3020));
                wfr.setButtonTitle("去交底");
                wfr.setState(2);
            } else if ((hfl.getWorkType() == 4 && hfl.getWorkSteta() == 0) || hfl.getWorkSteta() == 4) {
                wfr.setButtonTitle("施工中");
                wfr.setState(3);
            } else if (hfl.getWorkSteta() == 1) {
                wfr.setButtonTitle("已阶段完工");
                wfr.setState(4);
            } else if (hfl.getWorkSteta() == 5) {
                wfr.setButtonTitle("收尾施工中");
                wfr.setState(5);
            } else if (hfl.getWorkSteta() == 2 || hfl.getWorkSteta() == 6) {
                if (hfl.getWorkSteta() == 2) {
                    wfr.setButtonTitle("已整体完工");
                } else {
                    wfr.setButtonTitle("提前竣工");
                }
                wfr.setState(6);
            }
        }
        if (houseFlowApp != null && houseFlowApp.getApplyType() == 3) {
            wfr.setButtonTitle("停工申请");//按钮提示
        }
        if (hfl.getPause() == 1) {
            wfr.setButtonTitle("已停工");//按钮提示
        }
        wfr.setTotalNodeNumber(7);
        if (workerType.getType() == 4) {//拆除少无阶段完工，减去1节点数
            wfr.setTotalNodeNumber(6);
        }
        wfr.setCompletedNodeNumber(wfr.getState());//当前完成的节点数
        wfr.setTopButton(topButton);
        wfr.setFootButton(footButton);

        List<HouseFlowApply> earliestTimeList = houseFlowApplyMapper.getEarliestTimeHouseApply(hfl.getHouseId(), hfl.getWorkerId());
        HouseFlowApply earliestTime = null;
        if (earliestTimeList.size() > 0) {
            earliestTime = earliestTimeList.get(0);
        }
        Long suspendDay = houseFlowApplyMapper.getSuspendApply(hfl.getHouseId(), hfl.getWorkerId());//根据房子id和工人id查询暂停天数
        Long everyEndDay = houseFlowApplyMapper.getEveryDayApply(hfl.getHouseId(), hfl.getWorkerId());//根据房子id和工人id查询每日完工申请天数
        long totalDay = 0;
        if (earliestTime != null) {
            Date EarliestDay = earliestTime.getCreateDate();//最早开工时间
            Date newDate = new Date();
            totalDay = 1 + DateUtil.daysofTwo(EarliestDay, newDate);//计算当前时间隔最早开工时间相差多少天
            if (suspendDay != null) {
                totalDay = totalDay - suspendDay;
                if (totalDay <= 0) totalDay = 0;
            }
        }
        wfr.setStartDay(totalDay);
        wfr.setFinishedDay(everyEndDay == null ? 0 : everyEndDay);
        wfr.setSuspendDay((suspendDay == null ? 0 : suspendDay));
        return ServerResponse.createBySuccess("获取指定工程进度明细成功", wfr);
    }

    /**
     * 工匠-任务详情
     */
    public ServerResponse getJobsInfo(HttpServletRequest request, String userToken, String houseId, String productId) {
        Object object = getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }

        List<ButtonListBean> footButton = new ArrayList<>();//底部按钮
        Map map =new HashMap();
        String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
        List<TechnologyDTO> technologies = new ArrayList<>();
        StorefrontProduct storefrontProduct=iMasterStorefrontProductMapper.selectByPrimaryKey(productId);
        if(storefrontProduct!=null&& StringUtils.isNotBlank(storefrontProduct.getId())) {//店铺商品(补人工为店铺商品)
            DjBasicsProductTemplate djBasicsProductTemplate = iMasterProductTemplateMapper.selectByPrimaryKey(storefrontProduct.getProdTemplateId());
            if (!CommonUtil.isEmpty(djBasicsProductTemplate.getTechnologyIds())) {
                List<Technology> technologyList = masterTechnologyMapper.queryTechnologyList(djBasicsProductTemplate.getTechnologyIds());
                for (Technology technology : technologyList) {
                    TechnologyDTO technologyResult = new TechnologyDTO();
                    technologyResult.setId(technology.getId());
                    technologyResult.setName(technology.getName());
                    technologyResult.setImage(technology.getImage());
                    technologyResult.setImageUrl(StringTool.getImage(technology.getImage(), imageAddress));
                    technologyResult.setModifyDate(String.valueOf(technology.getModifyDate().getTime()));
                    technologies.add(technologyResult);
                }
            }
            BasicsGoods goods = iMasterBasicsGoodsMapper.selectByPrimaryKey(djBasicsProductTemplate.getGoodsId());
            map.put(BasicsGoods.HOUSEKEEPER_ACCEPTANCE,goods.getHousekeeperAcceptance());//是否需要管家验收（1是，0否）
            HouseFlow houseFlowDgj = houseFlowMapper.getHouseFlowByHidAndWty(houseId, 3);
            map.put("technologies",technologies);//节点列表
            map.put("productId",productId);//节点列表
            map.put("houseFlowId",houseFlowDgj.getId());//节点列表

            if(goods.getHousekeeperAcceptance()==1) {
                Example example = new Example(HouseFlowApply.class);
                example.createCriteria().andEqualTo(HouseFlowApply.HOUSE_ID, houseId)
                        .andEqualTo(HouseFlowApply.HOUSE_FLOW_APPLY_ID, productId)
                        .andNotIn(HouseFlowApply.MEMBER_CHECK, Arrays.asList(0, 1))
                        .andEqualTo(HouseFlowApply.TYPE, 1);
                Integer checkNum = houseFlowApplyMapper.selectCountByExample(example);
                if (checkNum == 0) {
                    footButton.add(Utils.getButton("发起验收", 3007));
                    map.put("footButton", footButton);//底部按钮
                }
            }

        }
        return ServerResponse.createBySuccess("获取指定工程进度明细成功",map);
    }
}