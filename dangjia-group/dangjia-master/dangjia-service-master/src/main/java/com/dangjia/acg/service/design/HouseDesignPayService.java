package com.dangjia.acg.service.design;

import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.BaseException;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.design.DesignPayDTO;
import com.dangjia.acg.mapper.core.IHouseFlowMapper;
import com.dangjia.acg.mapper.core.IHouseWorkerOrderMapper;
import com.dangjia.acg.mapper.design.IDesignBusinessOrderMapper;
import com.dangjia.acg.mapper.design.IPayConfigurationMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.pay.IBusinessOrderMapper;
import com.dangjia.acg.mapper.worker.IWorkerDetailMapper;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.core.HouseWorkerOrder;
import com.dangjia.acg.modle.design.DesignBusinessOrder;
import com.dangjia.acg.modle.design.PayConfiguration;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.pay.BusinessOrder;
import com.dangjia.acg.modle.worker.WorkerDetail;
import com.dangjia.acg.service.config.ConfigMessageService;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Ruking.Cheng
 * @descrilbe 处理设计费需要添加
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/5/30 5:01 PM
 */
@Service
public class HouseDesignPayService {
    @Autowired
    private ConfigMessageService configMessageService;
    @Autowired
    private IHouseMapper houseMapper;
    @Autowired
    private IPayConfigurationMapper payConfigurationMapper;
    @Autowired
    private IDesignBusinessOrderMapper designBusinessOrderMapper;
    @Autowired
    private IBusinessOrderMapper businessOrderMapper;
    @Autowired
    private CraftsmanConstructionService constructionService;
    @Autowired
    private IHouseFlowMapper houseFlowMapper;
    @Autowired
    private IHouseWorkerOrderMapper houseWorkerOrderMapper;
    @Autowired
    private IMemberMapper memberMapper;
    @Autowired
    private IWorkerDetailMapper workerDetailMapper;
    @Autowired
    private ConfigUtil configUtil;

    /**
     * 判断次数后确认是否需要支付
     *
     * @param type 1:平面图，2：施工图
     */
    public ServerResponse checkPass(House house, HouseWorkerOrder hwo, Member worker, int type) {
        Example example = new Example(PayConfiguration.class);
        Example.Criteria criteria = example.createCriteria()
                .andEqualTo(PayConfiguration.DATA_STATUS, 0);
        criteria.andEqualTo(PayConfiguration.TYPE, type);
        List<PayConfiguration> payConfigurations = payConfigurationMapper.selectByExample(example);
        PayConfiguration payConfiguration = null;
        if (payConfigurations != null && payConfigurations.size() > 0) {
            payConfiguration = payConfigurations.get(0);
        }
        if (payConfiguration == null) {
            payConfiguration = new PayConfiguration();
            payConfiguration.setFrequency(-1);
        }
        if (type == 1) {
            Integer planeFrequency = house.getPlaneFrequency();
            if (planeFrequency == null) {
                planeFrequency = 0;
            }
            if (payConfiguration.getFrequency() != -1 && planeFrequency >= payConfiguration.getFrequency()) {
                return insertBusinessOrder(house, payConfiguration, worker);
            } else {
                house.setPlaneFrequency(planeFrequency + 1);
                house.setDesignerOk(6);
                if (hwo != null) {
                    configMessageService.addConfigMessage(null, "gj", hwo.getWorkerId(), "0", "平面图未通过", String.format(DjConstants.PushMessage.PLANE_ERROR, house.getHouseName()), "");
                }
                houseMapper.updateByPrimaryKeySelective(house);
                return ServerResponse.createBySuccessMessage("操作成功");
            }
        } else {
            Integer constructionFrequency = house.getConstructionFrequency();
            if (constructionFrequency == null) {
                constructionFrequency = 0;
            }
            if (payConfiguration.getFrequency() != -1 && constructionFrequency >= payConfiguration.getFrequency()) {
                return insertBusinessOrder(house, payConfiguration, worker);
            } else {
                house.setConstructionFrequency(constructionFrequency + 1);
                house.setDesignerOk(8);
                if (hwo != null) {
                    configMessageService.addConfigMessage(null, "gj", hwo.getWorkerId(), "0", "施工图未通过", String.format(DjConstants.PushMessage.CONSTRUCTION_ERROR, house.getHouseName()), "");
                }
                houseMapper.updateByPrimaryKeySelective(house);
                return ServerResponse.createBySuccessMessage("操作成功");
            }
        }
    }

    /**
     * 生成业务单
     */
    private ServerResponse insertBusinessOrder(House house, PayConfiguration payConfiguration, Member worker) {
        //查询or新增业务单
        DesignPayDTO designPayDTO = new DesignPayDTO();
        designPayDTO.setType(7);
        Example example = new Example(DesignBusinessOrder.class);
        example.createCriteria()
                .andEqualTo(DesignBusinessOrder.DATA_STATUS, 0)
                .andEqualTo(DesignBusinessOrder.HOUSE_ID, house.getId())
                .andEqualTo(DesignBusinessOrder.TYPE, payConfiguration.getType())
                .andNotEqualTo(DesignBusinessOrder.OPERATION_STATE, 2);
        List<DesignBusinessOrder> designBusinessOrders = designBusinessOrderMapper.selectByExample(example);
        DesignBusinessOrder order;
        if (designBusinessOrders != null && designBusinessOrders.size() > 0) {
            order = designBusinessOrders.get(0);
            if (order.getStatus() != 0) {
                return ServerResponse.createByErrorMessage("已经支付改订单");
            }
        } else {
            order = new DesignBusinessOrder();
            order.setFrequency(0);
            order.setType(payConfiguration.getType());
            order.setSumMoney(payConfiguration.getSumMoney());
            order.setOperationState(0);
            order.setStatus(0);
            order.setHouseId(house.getId());
            designBusinessOrderMapper.insert(order);
        }
        //查询or新增支付业务订单
        example = new Example(BusinessOrder.class);
        example.createCriteria().andEqualTo(BusinessOrder.TASK_ID, order.getId())
                .andEqualTo(BusinessOrder.STATE, 1);
        List<BusinessOrder> businessOrderList = businessOrderMapper.selectByExample(example);
        BusinessOrder businessOrder;
        if (businessOrderList != null && businessOrderList.size() > 0) {
            businessOrder = businessOrderList.get(0);
            if (businessOrder.getState() == 3) {
                return ServerResponse.createByErrorMessage("已经支付改订单");
            }
        } else {
            businessOrder = new BusinessOrder();
            businessOrder.setMemberId(worker.getId());
            businessOrder.setHouseId(house.getId());
            businessOrder.setNumber(System.currentTimeMillis() + "-" + (int) (Math.random() * 9000 + 1000));
            businessOrder.setState(1);//刚生成
            businessOrder.setTotalPrice(order.getSumMoney());
            businessOrder.setDiscountsPrice(new BigDecimal(0));
            businessOrder.setPayPrice(order.getSumMoney());
            businessOrder.setType(7);//记录支付类型任务类型
            businessOrder.setTaskId(order.getId());//保存任务ID
            businessOrderMapper.insert(businessOrder);
        }
        designPayDTO.setBusinessOrderNumber(businessOrder.getId());
        //编辑返回报文
        switch (order.getType()) {
            case 1:
            case 2:
                designPayDTO.setMessage("您当前修改设计的次数已超过" + payConfiguration.getFrequency() + "次，需要先支付改图费。");
                break;
            case 3:
                designPayDTO.setMessage("您当前已确认设计图需支付修改精算费用" + payConfiguration.getSumMoney().setScale(2, BigDecimal.ROUND_HALF_UP) + "元。");
                break;
            case 4:
                if (house.getBudgetOk() == 3) {
                    example = new Example(PayConfiguration.class);
                    Example.Criteria criteria = example.createCriteria()
                            .andEqualTo(PayConfiguration.DATA_STATUS, 0);
                    criteria.andEqualTo(PayConfiguration.TYPE, 3);
                    List<PayConfiguration> payConfigurations = payConfigurationMapper.selectByExample(example);
                    PayConfiguration payConfiguration2 = null;
                    if (payConfigurations != null && payConfigurations.size() > 0) {
                        payConfiguration2 = payConfigurations.get(0);
                    }
                    if (payConfiguration2 == null) {
                        payConfiguration2 = new PayConfiguration();
                        payConfiguration2.setFrequency(-1);
                    }
                    designPayDTO.setMessage("额外修改设计需要支付改图费"
                            + payConfiguration.getSumMoney().setScale(2, BigDecimal.ROUND_HALF_UP)
                            + "元和改精算费"
                            + payConfiguration2.getSumMoney().setScale(2, BigDecimal.ROUND_HALF_UP)
                            + "元");
                } else {
                    designPayDTO.setMessage("您当前已确认设计图，正在制作精算，若要修改设计需要先支付改图费。");
                }
                break;
        }
        String webAddress = configUtil.getValue(SysConfig.PUBLIC_APP_ADDRESS, String.class);
        if (order.getType() == 3) {
            designPayDTO.setButName("《精算服务须知》");
            designPayDTO.setButUrl(webAddress + "paymentAgreement?title=精算服务须知&protocolTpye=2");
            // protocolTpye==1 精算商品定义
            // protocolTpye=2 精算修改费用明细
            // protocolTpye=3 设计改图费用
            // protocolTpye=4 9.9设计商品定义
            // protocolTpye=5 18.8设计商品定义
            // protocolTpye=6 28.8设计商品定义
            designPayDTO.setMoneyMessage("精算费用:¥" + order.getSumMoney().setScale(2, BigDecimal.ROUND_HALF_UP));
        } else {
            designPayDTO.setButName("《设计服务须知》");
            designPayDTO.setButUrl(webAddress + "paymentAgreement?title=设计服务须知&protocolTpye=3");
            designPayDTO.setMoneyMessage("设计改图费用:¥" + order.getSumMoney().setScale(2, BigDecimal.ROUND_HALF_UP));
        }
        return ServerResponse.createByErrorNeedToPay(designPayDTO);
    }

    public void setPaySuccess(BusinessOrder businessOrder) {
        DesignBusinessOrder designBusinessOrder = designBusinessOrderMapper.selectByPrimaryKey(businessOrder.getTaskId());
        if (designBusinessOrder == null) {
            throw new BaseException(ServerCode.ERROR, "未找到对应的业务单");
        }
        House house = houseMapper.selectByPrimaryKey(designBusinessOrder.getHouseId());
        if (house == null) {
            throw new BaseException(ServerCode.ERROR, "没有查询到相关房子");
        }
        designBusinessOrder.setStatus(1);
        Example examples = new Example(HouseFlow.class);
        examples.createCriteria().andEqualTo(HouseFlow.HOUSE_ID, house.getId())
                .andEqualTo(HouseFlow.WORKER_TYPE, house.getDecorationType() == 2 ? "2" : "1");
        List<HouseFlow> houseFlows = houseFlowMapper.selectByExample(examples);
        HouseWorkerOrder hwo = null;
        if (houseFlows.size() > 0) {
            hwo = houseWorkerOrderMapper.getByHouseIdAndWorkerTypeId(houseFlows.get(0).getHouseId(), houseFlows.get(0).getWorkerTypeId());
        }
        switch (designBusinessOrder.getType()) {//配置类型：1:平面图审核，2，施工图审核，3，精算修改费用，4:设计图施工过程中修改
            case 1:
                designBusinessOrder.setOperationState(2);
                Integer planeFrequency = house.getPlaneFrequency();
                if (planeFrequency == null) {
                    planeFrequency = 0;
                }
                house.setPlaneFrequency(planeFrequency + 1);
                house.setDesignerOk(6);
                if (hwo != null) {
                    configMessageService.addConfigMessage(null, "gj", hwo.getWorkerId(), "0", "平面图未通过", String.format(DjConstants.PushMessage.PLANE_ERROR, house.getHouseName()), "");
                }
                break;
            case 2:
                designBusinessOrder.setOperationState(2);
                Integer constructionFrequency = house.getConstructionFrequency();
                if (constructionFrequency == null) {
                    constructionFrequency = 0;
                }
                house.setConstructionFrequency(constructionFrequency + 1);
                house.setDesignerOk(8);
                if (hwo != null) {
                    configMessageService.addConfigMessage(null, "gj", hwo.getWorkerId(), "0", "施工图未通过", String.format(DjConstants.PushMessage.CONSTRUCTION_ERROR, house.getHouseName()), "");
                }
                break;
            case 3:
                Example example = new Example(DesignBusinessOrder.class);
                example.createCriteria()
                        .andEqualTo(DesignBusinessOrder.DATA_STATUS, 0)
                        .andEqualTo(DesignBusinessOrder.HOUSE_ID, house.getId())
                        .andEqualTo(DesignBusinessOrder.STATUS, 1)
                        .andEqualTo(DesignBusinessOrder.TYPE, 4)
                        .andNotEqualTo(DesignBusinessOrder.OPERATION_STATE, 2);
                List<DesignBusinessOrder> designBusinessOrders = designBusinessOrderMapper.selectByExample(example);
                if (designBusinessOrders != null && designBusinessOrders.size() > 0) {
                    designBusinessOrder.setOperationState(2);
                    DesignBusinessOrder order = designBusinessOrders.get(0);
                    order.setOperationState(2);
                    if (hwo != null) {
                        configMessageService.addConfigMessage(null, "gj", hwo.getWorkerId(), "0", "设计图已通过", String.format("恭喜！您设计的【%s】设计图已通过。", house.getHouseName()), "");
                    }
                    designBusinessOrderMapper.updateByPrimaryKeySelective(order);
                } else {
                    if (hwo != null) {
                        configMessageService.addConfigMessage(null, "gj", hwo.getWorkerId(), "0", "业主要求修改设计图", String.format("【%s】业主要求修改设计图。", house.getHouseName()), "");
                    }
                }
                break;
            case 4:
                if (hwo != null) {
                    configMessageService.addConfigMessage(null, "gj", hwo.getWorkerId(), "0", "业主要求修改设计图", String.format("【%s】业主要求修改设计图。", house.getHouseName()), "");
                }
                break;
        }
        houseMapper.updateByPrimaryKeySelective(house);
        designBusinessOrderMapper.updateByPrimaryKeySelective(designBusinessOrder);
        if (designBusinessOrder.getType() == 3) {
            HouseWorkerOrder hwo2 = houseWorkerOrderMapper.getByHouseIdAndWorkerTypeId(house.getId(), "2");
            setMoney(hwo2, house, designBusinessOrder, "精算费");
        } else {
            HouseWorkerOrder hwo2 = houseWorkerOrderMapper.getByHouseIdAndWorkerTypeId(house.getId(), "1");
            setMoney(hwo2, house, designBusinessOrder, "设计费");
        }
    }

    /**
     * 费用要给到设计师和精算师
     */
    private void setMoney(HouseWorkerOrder hwo2, House house, DesignBusinessOrder designBusinessOrder, String name) {
        if (hwo2 != null) {
            //用户入账
            Member member = memberMapper.selectByPrimaryKey(hwo2.getWorkerId());
            if (member != null) {
                BigDecimal haveMoney = member.getHaveMoney().add(designBusinessOrder.getSumMoney());
                BigDecimal surplusMoney = member.getSurplusMoney().add(designBusinessOrder.getSumMoney());
                member.setHaveMoney(haveMoney);//添加已获总钱
                member.setSurplusMoney(surplusMoney);//添加余额
                memberMapper.updateByPrimaryKeySelective(member);
                //添加流水
                WorkerDetail workerDetail = new WorkerDetail();
                workerDetail.setName(name);
                workerDetail.setWorkerId(member.getId());
                workerDetail.setWorkerName(member.getName());
                workerDetail.setHouseId(house.getId());
                workerDetail.setMoney(designBusinessOrder.getSumMoney());
                workerDetail.setState(0);//进工钱
                workerDetail.setWalletMoney(surplusMoney);//更新后的余额
                workerDetail.setHaveMoney(designBusinessOrder.getSumMoney());
                workerDetail.setHouseWorkerOrderId(hwo2.getId());
                workerDetail.setApplyMoney(designBusinessOrder.getSumMoney());
                workerDetailMapper.insert(workerDetail);
            }
        }
    }

    public ServerResponse modifyDesign(String userToken, String houseId) {
        House house = houseMapper.selectByPrimaryKey(houseId);
        if (house == null) {
            return ServerResponse.createByErrorMessage("没有查询到相关房子");
        }
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member worker = (Member) object;
        if (!worker.getId().equals(house.getMemberId())) {
            return ServerResponse.createByErrorMessage("您无权操作此房产");
        }
        Example example = new Example(PayConfiguration.class);
        Example.Criteria criteria = example.createCriteria()
                .andEqualTo(PayConfiguration.DATA_STATUS, 0);
        if (house.getDecorationType() == 2) {
            criteria.andEqualTo(PayConfiguration.TYPE, 3);
        } else {
            criteria.andEqualTo(PayConfiguration.TYPE, 4);
        }
        List<PayConfiguration> payConfigurations = payConfigurationMapper.selectByExample(example);
        PayConfiguration payConfiguration = null;
        if (payConfigurations != null && payConfigurations.size() > 0) {
            payConfiguration = payConfigurations.get(0);
        }
        if (payConfiguration == null) {
            return ServerResponse.createByErrorMessage("未找到支付配置，请联系客服");
        }
        return insertBusinessOrder(house, payConfiguration, worker);
    }

    public ServerResponse confirmDesign(String userToken, String houseId, int type) {
        House house = houseMapper.selectByPrimaryKey(houseId);
        if (house == null) {
            return ServerResponse.createByErrorMessage("没有查询到相关房子");
        }
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member worker = (Member) object;
        if (!worker.getId().equals(house.getMemberId())) {
            return ServerResponse.createByErrorMessage("您无权操作此房产");
        }
        Example examples = new Example(HouseFlow.class);
        examples.createCriteria().andEqualTo(HouseFlow.HOUSE_ID, house.getId()).andEqualTo(HouseFlow.WORKER_TYPE, "1");
        List<HouseFlow> houseFlows = houseFlowMapper.selectByExample(examples);
        HouseWorkerOrder hwo = null;
        if (houseFlows.size() > 0) {
            hwo = houseWorkerOrderMapper.getByHouseIdAndWorkerTypeId(houseFlows.get(0).getHouseId(), houseFlows.get(0).getWorkerTypeId());
        }
        //确认设计图
        Example example = new Example(DesignBusinessOrder.class);
        example.createCriteria()
                .andEqualTo(DesignBusinessOrder.DATA_STATUS, 0)
                .andEqualTo(DesignBusinessOrder.HOUSE_ID, house.getId())
                .andEqualTo(DesignBusinessOrder.STATUS, 1)
                .andEqualTo(DesignBusinessOrder.TYPE, 4)
                .andNotEqualTo(DesignBusinessOrder.OPERATION_STATE, 2);
        List<DesignBusinessOrder> designBusinessOrders = designBusinessOrderMapper.selectByExample(example);
        if (designBusinessOrders != null && designBusinessOrders.size() > 0) {
            DesignBusinessOrder order = designBusinessOrders.get(0);
            if (order.getOperationState() == 1) {
                if (type == 1) {
                    if (house.getBudgetOk() == 3) {
                        example = new Example(PayConfiguration.class);
                        Example.Criteria criteria = example.createCriteria()
                                .andEqualTo(PayConfiguration.DATA_STATUS, 0);
                        criteria.andEqualTo(PayConfiguration.TYPE, 3);
                        List<PayConfiguration> payConfigurations = payConfigurationMapper.selectByExample(example);
                        PayConfiguration payConfiguration = null;
                        if (payConfigurations != null && payConfigurations.size() > 0) {
                            payConfiguration = payConfigurations.get(0);
                        }
                        if (payConfiguration == null) {
                            return ServerResponse.createByErrorMessage("未找到支付配置，请联系客服");
                        }
                        return insertBusinessOrder(house, payConfiguration, worker);
                    }
                    order.setOperationState(2);
                    if (hwo != null) {
                        configMessageService.addConfigMessage(null, "gj", hwo.getWorkerId(), "0", "设计图已通过", String.format("恭喜！您设计的【%s】设计图已通过。", house.getHouseName()), "");
                    }
                } else {
                    Integer frequency = order.getFrequency();
                    if (frequency == null) {
                        frequency = 0;
                    }
                    order.setFrequency(frequency + 1);
                    order.setOperationState(0);
                    if (hwo != null) {
                        configMessageService.addConfigMessage(null, "gj", hwo.getWorkerId(), "0", "设计图未通过", String.format("抱歉！您设计的【%s】设计图未通过。", house.getHouseName()), "");
                    }
                }
                designBusinessOrderMapper.updateByPrimaryKeySelective(order);
                return ServerResponse.createBySuccessMessage("操作成功");
            }
        }
        return ServerResponse.createByErrorMessage("流程出现问题，无法操作，请联系客服");
    }

}