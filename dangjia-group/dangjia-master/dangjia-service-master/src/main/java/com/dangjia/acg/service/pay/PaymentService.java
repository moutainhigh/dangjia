package com.dangjia.acg.service.pay;

import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.enums.EventStatus;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.pay.*;
import com.dangjia.acg.mapper.core.IHouseFlowMapper;
import com.dangjia.acg.mapper.core.IHouseWorkerMapper;
import com.dangjia.acg.mapper.core.IHouseWorkerOrderMapper;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.design.IDesignImageTypeMapper;
import com.dangjia.acg.mapper.design.IHouseDesignImageMapper;
import com.dangjia.acg.mapper.design.IHouseStyleTypeMapper;
import com.dangjia.acg.mapper.house.IHouseAccountsMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.pay.IBusinessOrderMapper;
import com.dangjia.acg.mapper.pay.IPayOrderMapper;
import com.dangjia.acg.mapper.safe.IWorkerTypeSafeMapper;
import com.dangjia.acg.mapper.safe.IWorkerTypeSafeOrderMapper;
import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.core.HouseWorker;
import com.dangjia.acg.modle.core.HouseWorkerOrder;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.design.DesignImageType;
import com.dangjia.acg.modle.design.HouseDesignImage;
import com.dangjia.acg.modle.design.HouseStyleType;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.house.HouseAccounts;
import com.dangjia.acg.modle.member.AccessToken;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.pay.BusinessOrder;
import com.dangjia.acg.modle.pay.PayOrder;
import com.dangjia.acg.modle.safe.WorkerTypeSafe;
import com.dangjia.acg.modle.safe.WorkerTypeSafeOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import java.math.BigDecimal;
import java.util.*;

/**
 * author: Ronalcheng
 * Date: 2018/11/7 0007
 * Time: 14:38
 */
@Service
public class PaymentService {
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private IHouseFlowMapper houseFlowMapper;
    @Autowired
    private IWorkerTypeMapper workerTypeMapper;
    @Autowired
    private IMemberMapper memberMapper;
    @Autowired
    private IHouseWorkerMapper houseWorkerMapper;
    @Autowired
    private IHouseMapper houseMapper;
    @Autowired
    private IHouseStyleTypeMapper houseStyleTypeMapper;
    @Autowired
    private IHouseDesignImageMapper houseDesignImageMapper;
    @Autowired
    private IDesignImageTypeMapper designImageTypeMapper;
    @Autowired
    private IWorkerTypeSafeMapper workerTypeSafeMapper;
    @Autowired
    private IWorkerTypeSafeOrderMapper workerTypeSafeOrderMapper;
    @Autowired
    private IBusinessOrderMapper businessOrderMapper;
    @Autowired
    private IPayOrderMapper payOrderMapper;
    @Autowired
    private IHouseWorkerOrderMapper houseWorkerOrderMapper;
    @Autowired
    private IHouseAccountsMapper houseAccountsMapper;

    /**
     * 移动端支付成功回调
     */
    public ServerResponse setPaySuccess(String userToken,String businessOrderNumber){
        AccessToken accessToken = redisClient.getCache(userToken+ Constants.SESSIONUSERID,AccessToken.class);
        if(accessToken == null){//无效的token
            return ServerResponse.createByErrorCodeMessage(EventStatus.USER_TOKEN_ERROR.getCode(),"请重新登录或注册!");
        }
        Map<String, Object> returnMap = new HashMap<String, Object>();
        try{
            Example example = new Example(BusinessOrder.class);
            example.createCriteria().andEqualTo("number", businessOrderNumber);
            List<BusinessOrder> businessOrderList = businessOrderMapper.selectByExample(example);
            if (businessOrderList.size() == 0){
                return ServerResponse.createByErrorMessage("订单不存在");
            }
            BusinessOrder businessOrder = businessOrderList.get(0);
            if(businessOrder.getState() == 3){
                returnMap.put("name", "当家装修担保平台");
                returnMap.put("businessOrderNumber", businessOrderNumber);
                returnMap.put("price", businessOrder.getPayPrice());
                return ServerResponse.createBySuccess("支付成功",returnMap);
            }
            House house = houseMapper.selectByPrimaryKey(businessOrder.getHouseId());
            example = new Example(PayOrder.class);
            example.createCriteria().andEqualTo("businessOrderNumber", businessOrderNumber).andEqualTo("state",0);
            List<PayOrder> payOrderList = payOrderMapper.selectByExample(example);
            if (payOrderList.size() == 0){
                return ServerResponse.createByErrorMessage("支付订单不存在");
            }
            PayOrder payOrder = payOrderList.get(0);
            businessOrder.setPayOrderNumber(payOrder.getNumber());
            businessOrder.setState(3);//已支付
            businessOrderMapper.updateByPrimaryKeySelective(businessOrder);
            payOrder.setState(2);//已支付
            payOrderMapper.updateByPrimaryKeySelective(payOrder);
            String payState = payOrder.getPayState();

            if (StringUtil.isNotEmpty(businessOrder.getHouseflowIds())){//工序支付
                payWorkerType(businessOrder.getHouseflowIds(),payState);
            }
            /*//处理补货 同时生成补货订单  ronalcheng
            buhuo(businessOrderNumber,houseid,paystate);
            //处理自购 同时生成自购订单  ronalcheng 20180827
            zigou(businessOrderNumber,houseid,paystate);
            //处理补人工
            burengong(businessOrderNumber,houseid);*/

            returnMap.put("name", "当家装修担保平台");
            returnMap.put("businessOrderNumber", businessOrderNumber);
            returnMap.put("price", businessOrder.getPayPrice());
            return ServerResponse.createBySuccess("支付成功",returnMap);
        }catch (Exception e){
            e.printStackTrace();
            returnMap.put("name", "当家装修担保平台");
            returnMap.put("businessOrderNumber", businessOrderNumber);
            returnMap.put("price", 0);
            return ServerResponse.createBySuccess("支付回调异常",returnMap);
        }
    }
    /*
     支付工序
     */
    private void payWorkerType(String houseFlowId,String payState){
        HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(houseFlowId);
        House house = houseMapper.selectByPrimaryKey(houseFlow.getHouseId());
        if(house.getMoney() == null){
            house.setMoney(new BigDecimal(0));
        }
        HouseWorkerOrder hwo = houseWorkerOrderMapper.selectByPrimaryKey(houseFlow.getHouseWorkerOrderId());
        hwo.setPayState(1);
        hwo.setPayment(payState);//统计支付方式
        houseWorkerOrderMapper.updateByPrimaryKeySelective(hwo);
        HouseWorker houseWorker = houseWorkerMapper.selectByPrimaryKey(hwo.getHouseWorkerId());
        houseWorker.setWorkType(6);
        houseWorkerMapper.updateByPrimaryKeySelective(houseWorker);

        houseFlow.setWorkerId(hwo.getWorkerId());
        houseFlow.setWorkType(4);
        houseFlow.setHouseWorkerId(hwo.getHouseWorkerId());
        houseFlow.setMaterialPrice(hwo.getMaterialPrice());
        houseFlow.setWorkPrice(hwo.getWorkPrice());
        houseFlow.setTotalPrice(hwo.getTotalPrice());
        houseFlow.setWorkSteta(3);//待交底
        if(StringUtil.isNotEmpty(hwo.getWorkerTypeSafeId())){
            houseFlow.setSafeId(hwo.getWorkerTypeSafeId());
        }
        houseFlow.setModifyDate(new Date());
        houseFlowMapper.updateByPrimaryKeySelective(houseFlow);

        if(hwo.getWorkerType() == 1){ //设计费用处理
            house.setDesignerOk(1);
            houseMapper.updateByPrimaryKeySelective(house);
        } else if(hwo.getWorkerType() == 2){//精算费用处理
            house.setBudgetOk(1);//房间工种表里标记开始精算
            houseMapper.updateByPrimaryKeySelective(house);
        }else{//处理其它工人
            //人工和取消的材料
            /*rengong(businessOrderNumber,hwo,houseFlow.getId());
            cailiao(businessOrderNumber,hwo,paystate);*/
            //处理保险订单
            insurance(hwo,payState);
        }
       /* //记录工种工钱 项目流水
        liushui(businessOrderNumber,house,hwo,paystate);
        //app推送和发送短信给工匠
        sendMessge(businessOrderNumber,worker,house);*/
    }
    /*
    保险订单
     */
    private void insurance(HouseWorkerOrder hwo,String payState){
        if(StringUtil.isNotEmpty(hwo.getWorkerTypeSafeOrderId())){
            HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(hwo.getHouseFlowId());
            House house = houseMapper.selectByPrimaryKey(hwo.getHouseId());
            WorkerTypeSafeOrder wtso = workerTypeSafeOrderMapper.selectByPrimaryKey(hwo.getWorkerTypeSafeOrderId());
            wtso.setWorkerTypeId(hwo.getWorkerTypeId()); // 工种id
            wtso.setWorkerType(hwo.getWorkerType());
            wtso.setHouseFlowId(houseFlow.getId());
            wtso.setState(1);  //已支付
            wtso.setShopDate(new Date());  //设置购买时间
            workerTypeSafeOrderMapper.insert(wtso);
            hwo.setSafePrice(wtso.getPrice());
            houseWorkerOrderMapper.updateByPrimaryKeySelective(hwo);//记录保险费

            WorkerTypeSafe wts = workerTypeSafeMapper.selectByPrimaryKey(wtso.getWorkerTypeSafeId());
            if(house.getMoney() == null){
                house.setMoney(new BigDecimal(0));
            }
            //记录项目流水 保险
            HouseAccounts ha = new HouseAccounts();
            ha.setReason("收入"+wts.getName()+"费用");
            ha.setMoney(house.getMoney().add(hwo.getSafePrice()));//项目总钱
            ha.setState(0);//进
            ha.setPayMoney(hwo.getSafePrice());//本次数额
            ha.setHouseId(house.getId());
            ha.setHouseName(house.getResidential()+house.getBuilding()+"栋"+house.getUnit()+"单元"+house.getNumber()+"号");
            ha.setMemberId(hwo.getMemberId());
            ha.setName("业主支付");
            ha.setPayment(payState);//统计支付方式
            houseAccountsMapper.insert(ha);
            house.setMoney(house.getMoney().add(hwo.getSafePrice()));//累计项目钱
            houseMapper.updateByPrimaryKeySelective(house);
        }
    }

    /**
     * 支付页面
     */
    public ServerResponse getPaymentOrder(String userToken, String houseId, String taskId, int type){
        AccessToken accessToken = redisClient.getCache(userToken+ Constants.SESSIONUSERID,AccessToken.class);
        if(accessToken == null){//无效的token
            return ServerResponse.createByErrorCodeMessage(EventStatus.USER_TOKEN_ERROR.getCode(),"无效的token,请重新登录或注册!");
        }
        House house = houseMapper.selectByPrimaryKey(houseId);
        PaymentDTO paymentDTO = new PaymentDTO();
        BigDecimal paymentPrice = new BigDecimal(0);//总共钱
        BigDecimal workPrice = new BigDecimal(0);//工钱
        BigDecimal materialPrice = new BigDecimal(0);//材料钱

        Example example = new Example(BusinessOrder.class);
        example.createCriteria().andEqualTo("houseId", houseId).andEqualTo("state", 1);
        List<BusinessOrder> businessOrderList = businessOrderMapper.selectByExample(example);
        BusinessOrder businessOrder;
        if(businessOrderList.size() == 0){
            businessOrder = new BusinessOrder();
            businessOrder.setMemberId(house.getMemberId());
            businessOrder.setHouseId(houseId);
            businessOrder.setNumber(System.currentTimeMillis()+"-"+(int)(Math.random()*9000+1000));
            businessOrder.setState(1);//刚生成
            businessOrder.setTotalPrice(new BigDecimal(0.0));
            businessOrder.setDiscountsPrice(new BigDecimal(0));
            businessOrder.setPayPrice(new BigDecimal(0.0));
            businessOrderMapper.insert(businessOrder);
        }else{
            businessOrder = businessOrderList.get(0);
        }

        if(type == 1){//支付工序
            HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(taskId);
            if(houseFlow.getWorkType() != 3){
                return ServerResponse.createByErrorMessage("该工序订单异常");
            }
            businessOrder.setHouseflowIds(houseFlow.getId());//保存houseFlowId
            example = new Example(HouseWorker.class);
            example.createCriteria().andEqualTo("houseFlowId", houseFlow.getId()).andEqualTo("workType", 1);
            List<HouseWorker> houseWorkerList = houseWorkerMapper.selectByExample(example);
            HouseWorker houseWorker = houseWorkerList.get(0);
            Member worker = memberMapper.selectByPrimaryKey(houseWorker.getWorkerId()); //查工匠
            WorkerType workerType = workerTypeMapper.selectByPrimaryKey(worker.getWorkerTypeId());
            WorkerDTO workerDTO = new WorkerDTO();
            workerDTO.setHouseWorkerId(houseWorkerList.get(0).getId());//换人参数
            workerDTO.setHead(worker.getHead());
            workerDTO.setWorkerTypeName(workerType.getName());
            workerDTO.setName(worker.getName());
            workerDTO.setMobile(worker.getMobile());
            workerDTO.setChange(0);//不能换人
            paymentDTO.setWorkerDTO(workerDTO);//工匠信息
            /*
             * 生成工匠订单
             */
            HouseWorkerOrder hwo = null;
            example = new Example(HouseWorkerOrder.class);
            example.createCriteria().andEqualTo("houseFlowId", houseFlow.getId()).andEqualTo("workerType", houseFlow.getWorkerType());
            List<HouseWorkerOrder> houseWorkerOrderList = houseWorkerOrderMapper.selectByExample(example);
            if (houseWorkerOrderList.size() == 1){
                hwo = houseWorkerOrderList.get(0);
            }else if(houseWorkerOrderList.size() > 1) {
                return ServerResponse.createByErrorMessage("抢单异常,联系平台部");
            }
            if(hwo == null){
                hwo = new HouseWorkerOrder();
                hwo.setMemberId(houseFlow.getMemberId());
                hwo.setHouseId(houseFlow.getHouseId());
                hwo.setHouseFlowId(houseFlow.getId());
                hwo.setHouseWorkerId(houseWorker.getId());
                hwo.setWorkerId(worker.getId());
                hwo.setWorkerTypeId(worker.getWorkerTypeId());
                hwo.setWorkerType(worker.getWorkerType());
                hwo.setBusinessOrderNumber(businessOrder.getNumber());//业务订单号
                houseWorkerOrderMapper.insert(hwo);
                houseFlow.setHouseWorkerOrderId(hwo.getId());
                houseFlowMapper.updateByPrimaryKeySelective(houseFlow);
                houseWorker.setHouseWorkerOrderId(hwo.getId());
                houseWorkerMapper.updateByPrimaryKeySelective(houseWorker);
            }else{
                hwo.setMemberId(houseFlow.getMemberId());
                hwo.setHouseId(houseFlow.getHouseId());
                hwo.setHouseFlowId(houseFlow.getId());
                hwo.setHouseWorkerId(houseWorker.getId());
                hwo.setWorkerId(worker.getId());
                hwo.setWorkerTypeId(worker.getWorkerTypeId());
                hwo.setWorkerType(worker.getWorkerType());
                hwo.setBusinessOrderNumber(businessOrder.getNumber());//业务订单号
                houseWorkerOrderMapper.updateByPrimaryKeySelective(hwo);
            }

            if (houseFlow.getWorkerType() == 1){//设计师
                HouseStyleType houseStyleType = houseStyleTypeMapper.getStyleByName(house.getStyle());
                workPrice = house.getSquare().multiply(houseStyleType.getPrice());//设计工钱
                hwo.setWorkPrice(workPrice);
                hwo.setTotalPrice(workPrice);
                houseWorkerOrderMapper.updateByPrimaryKeySelective(hwo);
                paymentPrice = paymentPrice.add(workPrice);

                example = new Example(HouseDesignImage.class);
                example.createCriteria().andEqualTo("houseId", houseId);
                List<HouseDesignImage> houseDesignImageList = houseDesignImageMapper.selectByExample(example);
                UpgradeDesignDTO upgradeDesignDTO = new UpgradeDesignDTO();//升级设计
                List<DesignImageDTO> designImageDTOList = new ArrayList<DesignImageDTO>();
                upgradeDesignDTO.setTitle("升级设计");
                upgradeDesignDTO.setType(1);//多选
                for (HouseDesignImage hdi : houseDesignImageList){
                    hdi.setBusinessOrderNumber(businessOrder.getNumber());
                    houseDesignImageMapper.updateByPrimaryKeySelective(hdi);
                    DesignImageType designImageType = designImageTypeMapper.selectByPrimaryKey(hdi.getDesignImageTypeId());
                    DesignImageDTO designImageDTO = new DesignImageDTO();
                    //升级用设计图
                    designImageDTO.setName(designImageType.getName());
                    designImageDTO.setPrice("¥"+designImageType.getPrice());
                    designImageDTO.setDesignImageTypeId(designImageType.getId());
                    designImageDTO.setSelected(1);//已选
                    designImageDTOList.add(designImageDTO);
                    paymentPrice = paymentPrice.add(designImageType.getPrice());//加上升级钱
                }
                upgradeDesignDTO.setDesignImageDTOList(designImageDTOList);
                paymentDTO.setUpgradeDesignDTO(upgradeDesignDTO);
            }else if (houseFlow.getWorkerType() == 2){//精算
                //待完成
                workPrice = houseFlow.getWorkPrice();
                paymentPrice = paymentPrice.add(workPrice);
            }else {//其它工序
                //查工钱待完成 还有材料人工钱
                //TODO workPrice =

                //查出有没有生成保险订单
                example = new Example(WorkerTypeSafeOrder.class);
                example.createCriteria().andEqualTo("houseFlowId",houseFlow.getId());
                List<WorkerTypeSafeOrder> wtsoList = workerTypeSafeOrderMapper.selectByExample(example);
                if(wtsoList.size() == 1){
                    WorkerTypeSafeOrder workerTypeSafeOrder = wtsoList.get(0);
                    workerTypeSafeOrder.setBusinessOrderNumber(businessOrder.getNumber());
                    //保存业务订单号
                    workerTypeSafeOrderMapper.updateByPrimaryKey(workerTypeSafeOrder);
                    UpgradeSafeDTO upgradeSafeDTO = new UpgradeSafeDTO();//保险服务
                    upgradeSafeDTO.setTitle("保险服务");
                    upgradeSafeDTO.setType(0);//单选
                    WorkerTypeSafe wts = workerTypeSafeMapper.selectByPrimaryKey(workerTypeSafeOrder.getWorkerTypeSafeId());
                    List<SafeTypeDTO> safeTypeDTOList = new ArrayList<SafeTypeDTO>();
                    SafeTypeDTO safeTypeDTO = new SafeTypeDTO();
                    safeTypeDTO.setName(wts.getName());
                    safeTypeDTO.setPrice("¥"+wts.getPrice().multiply(house.getSquare()));
                    safeTypeDTO.setSelected(1);//勾
                    safeTypeDTOList.add(safeTypeDTO);
                    upgradeSafeDTO.setSafeTypeDTOList(safeTypeDTOList);
                    paymentDTO.setUpgradeSafeDTO(upgradeSafeDTO);//保险
                    paymentPrice = paymentPrice.add(wts.getPrice().multiply(house.getSquare()));//钱加上
                }else if(wtsoList.size() > 1){
                    return ServerResponse.createByErrorMessage("保险订单错误,联系平台部");
                }
            }
        }else if(type == 2){//补人工补材料

        }else{
            return ServerResponse.createByErrorMessage("参数错误");
        }
        //保存总价
        businessOrder.setTotalPrice(paymentPrice);
        businessOrder.setPayPrice(paymentPrice);
        businessOrderMapper.updateByPrimaryKeySelective(businessOrder);

        /*//查看优惠
        List<RedPacketRecord> rprList = redPacketRecordDao.getNotUsedRecord(house.getMemberid());
        if(rprList.size() > 0){
            paymentPageResult.setDiscounts("1");//有优惠
        }else{
            paymentPageResult.setDiscounts("0");//
        }*/
        paymentDTO.setTotalPrice(paymentPrice);
        paymentDTO.setDiscounts(0);
        paymentDTO.setDiscountsPrice(new BigDecimal(0));
        paymentDTO.setPayPrice(paymentPrice);//实付
        paymentDTO.setBusinessOrderNumber(businessOrder.getNumber());
        return  ServerResponse.createBySuccess("查询成功", paymentDTO);
    }

    /**
     * 购物车
     * @param taskId houseFlowId,mendOrderId,houseFlowApplyId
     * @param type 1工序支付任务,2补货补人工,3审核任务,4待付款进来只付材料
     */
    public ServerResponse getPaymentPage(String userToken, String houseId, String taskId, int type){
        House house = houseMapper.selectByPrimaryKey(houseId);
        PaymentDTO paymentDTO = new PaymentDTO();

        BigDecimal totalPrice = new BigDecimal(0);
        BigDecimal workPrice = new BigDecimal(0);//工钱
        Example example;
        if(type == 1){//支付工序
            HouseFlow houseFlow = houseFlowMapper.selectByPrimaryKey(taskId);
            if(houseFlow.getWorkType() == 2){
                return ServerResponse.createByErrorMessage("等待工匠抢单");
            }
            if(houseFlow.getWorkType() == 4){
                return ServerResponse.createByErrorMessage("该订单已支付");
            }
            example = new Example(HouseWorker.class);
            example.createCriteria().andEqualTo("houseFlowId", houseFlow.getId()).andEqualTo("workType", 1);
            List<HouseWorker> houseWorkerList = houseWorkerMapper.selectByExample(example);
            if(houseWorkerList.size() != 1){
                return ServerResponse.createByErrorMessage("抢单异常,联系平台部");
            }
            Member worker = memberMapper.selectByPrimaryKey(houseWorkerList.get(0).getWorkerId()); //查工匠
            WorkerType workerType = workerTypeMapper.selectByPrimaryKey(worker.getWorkerTypeId());
            WorkerDTO workerDTO = new WorkerDTO();
            workerDTO.setHouseWorkerId(houseWorkerList.get(0).getId());//换人参数
            workerDTO.setHead(worker.getHead());
            workerDTO.setWorkerTypeName(workerType.getName());
            workerDTO.setName(worker.getName());
            workerDTO.setMobile(worker.getMobile());
            if(houseFlow.getWorkerType() > 2){//精算之后才能换人
                workerDTO.setChange(1);
            }else {
                workerDTO.setChange(0);
            }
            paymentDTO.setWorkerDTO(workerDTO);//工匠信息
            if (houseFlow.getWorkerType() == 1){//设计师
                HouseStyleType houseStyleType = houseStyleTypeMapper.getStyleByName(house.getStyle());
                workPrice = house.getSquare().multiply(houseStyleType.getPrice());//设计工钱
                example = new Example(HouseDesignImage.class);
                example.createCriteria().andEqualTo("houseId", houseId);
                List<HouseDesignImage> houseDesignImageList = houseDesignImageMapper.selectByExample(example);
                UpgradeDesignDTO upgradeDesignDTO = new UpgradeDesignDTO();//升级设计
                upgradeDesignDTO.setTitle("升级设计");
                upgradeDesignDTO.setType(1);//多选
                //所有升级设计图
                example = new Example(DesignImageType.class);
                example.createCriteria().andEqualTo("sell", 1);
                List<DesignImageType> designImageTypeList = designImageTypeMapper.selectByExample(example);
                List<DesignImageDTO> designImageDTOList = new ArrayList<DesignImageDTO>();
                for (DesignImageType designImageType : designImageTypeList){
                    DesignImageDTO designImageDTO = new DesignImageDTO();
                    designImageDTO.setName(designImageType.getName());
                    designImageDTO.setDesignImageTypeId(designImageType.getId());
                    designImageDTO.setPrice("¥"+designImageType.getPrice());
                    designImageDTO.setSelected(0);//未选
                    //匹配该房子图
                    for (HouseDesignImage hdi : houseDesignImageList){
                        if(hdi.getDesignImageTypeId().equals(designImageType.getId())){
                            designImageDTO.setSelected(1);//已选
                            totalPrice = totalPrice.add(designImageType.getPrice());
                            break;
                        }
                    }
                    designImageDTOList.add(designImageDTO);//所有升级图
                }
                upgradeDesignDTO.setDesignImageDTOList(designImageDTOList);
                paymentDTO.setUpgradeDesignDTO(upgradeDesignDTO);

                /*
                展示数据结构
                 */
                UpgradeSafeDTO upgradeSafeDTO = new UpgradeSafeDTO();//升级保险
                List<SafeTypeDTO> safeTypeDTOList = new ArrayList<SafeTypeDTO>();
                SafeTypeDTO safeTypeDTO = new SafeTypeDTO();
                safeTypeDTO.setName("保险名1");
                safeTypeDTO.setPrice("¥1000");
                safeTypeDTO.setSelected(0);//未勾
                safeTypeDTO.setWorkerTypeSafeId("");//保险类型id
                safeTypeDTO.setHouseFlowId("");
                safeTypeDTOList.add(safeTypeDTO);
                safeTypeDTO = new SafeTypeDTO();
                safeTypeDTO.setName("保险名2");
                safeTypeDTO.setPrice("¥1005");
                safeTypeDTO.setSelected(1);//已勾
                safeTypeDTO.setWorkerTypeSafeId("");//保险类型id
                safeTypeDTO.setHouseFlowId("");
                safeTypeDTOList.add(safeTypeDTO);
                upgradeSafeDTO.setTitle("保险升级");
                upgradeSafeDTO.setType(0);
                upgradeSafeDTO.setSafeTypeDTOList(safeTypeDTOList);
                paymentDTO.setUpgradeSafeDTO(upgradeSafeDTO);

                List<ActuaryDTO> actuaryDTOList = new ArrayList<ActuaryDTO>();//商品
                ActuaryDTO actuaryDTO = new ActuaryDTO();
                actuaryDTO.setImage("");
                actuaryDTO.setKind("人工");
                actuaryDTO.setName("水电阶段人工花费");
                actuaryDTO.setPrice("¥100");
                actuaryDTO.setButton("材料明细");
                actuaryDTO.setUrl("");
                actuaryDTO.setType(1);
                actuaryDTOList.add(actuaryDTO);
                actuaryDTO = new ActuaryDTO();
                actuaryDTO.setImage("");
                actuaryDTO.setKind("材料");
                actuaryDTO.setName("水电阶段材料花费");
                actuaryDTO.setPrice("¥100");
                actuaryDTO.setButton("材料明细");
                actuaryDTO.setUrl("");
                actuaryDTO.setType(2);
                actuaryDTOList.add(actuaryDTO);
                actuaryDTO = new ActuaryDTO();
                actuaryDTO.setImage("");
                actuaryDTO.setKind("服务");
                actuaryDTO.setName("水电阶段服务花费");
                actuaryDTO.setPrice("¥100");
                actuaryDTO.setButton("服务明细");
                actuaryDTO.setUrl("");
                actuaryDTO.setType(3);
                actuaryDTOList.add(actuaryDTO);
                actuaryDTO = new ActuaryDTO();
                actuaryDTO.setImage("");
                actuaryDTO.setKind("补人工");
                actuaryDTO.setName("补人工花费");
                actuaryDTO.setPrice("¥100");
                actuaryDTO.setButton("补人工明细");
                actuaryDTO.setUrl("");
                actuaryDTO.setType(4);
                actuaryDTOList.add(actuaryDTO);
                paymentDTO.setActuaryDTOList(actuaryDTOList);

            }else if (houseFlow.getWorkerType() == 2){//精算
                workPrice = houseFlow.getWorkPrice();
            }else {//其它工序
                //查工钱待完成 还有材料人工钱
                //workPrice =

                //该工钟所有保险
                example = new Example(WorkerTypeSafe.class);
                example.createCriteria().andEqualTo("worker_type_id",houseFlow.getWorkerTypeId());
                List<WorkerTypeSafe> wtsList = workerTypeSafeMapper.selectByExample(example);
                //有保险服务
                if(wtsList.size() > 0){
                    //查出有没有生成保险订单
                    example = new Example(WorkerTypeSafeOrder.class);
                    example.createCriteria().andEqualTo("houseFlowId",houseFlow.getId());
                    List<WorkerTypeSafeOrder> wtsoList = workerTypeSafeOrderMapper.selectByExample(example);
                    UpgradeSafeDTO upgradeSafeDTO = new UpgradeSafeDTO();//保险服务
                    upgradeSafeDTO.setTitle("保险服务");
                    upgradeSafeDTO.setType(0);//单选
                    List<SafeTypeDTO> safeTypeDTOList = new ArrayList<SafeTypeDTO>();
                    for(WorkerTypeSafe wts : wtsList){
                        SafeTypeDTO safeTypeDTO = new SafeTypeDTO();
                        safeTypeDTO.setName(wts.getName());
                        safeTypeDTO.setPrice("¥"+wts.getPrice().multiply(house.getSquare()));
                        safeTypeDTO.setSelected(0);//未勾
                        safeTypeDTO.setWorkerTypeSafeId(wts.getId());//保险类型id
                        safeTypeDTO.setHouseFlowId(houseFlow.getId());
                        for(WorkerTypeSafeOrder wtso : wtsoList){
                            if(wts.getId().equals(wtso.getWorkerTypeSafeId())){
                                safeTypeDTO.setSelected(0);
                                totalPrice = totalPrice.add(wts.getPrice().multiply(house.getSquare()));
                                break;
                            }
                        }
                        safeTypeDTOList.add(safeTypeDTO);
                    }
                    upgradeSafeDTO.setSafeTypeDTOList(safeTypeDTOList);
                    paymentDTO.setUpgradeSafeDTO(upgradeSafeDTO);//保险
                }
            }
            totalPrice = totalPrice.add(workPrice);//工钱

        }else if(type == 2){//补人工补材料

        }else{
            return ServerResponse.createByErrorMessage("参数错误");
        }
        paymentDTO.setTotalPrice(totalPrice);
        paymentDTO.setDiscounts(0);
        paymentDTO.setHouseId(houseId);
        paymentDTO.setTaskId(taskId);
        paymentDTO.setType(type);
        return  ServerResponse.createBySuccess("查询成功", paymentDTO);
    }

    /**
     * 待付款 管家后
     */
    public ServerResponse setPaying(String houseId){
        Example example = new Example(HouseFlow.class);
        example.createCriteria().andEqualTo("houseId", houseId).andEqualTo("state", 0)
                .andGreaterThan("workerType",2).andEqualTo("workType",3);
        List<HouseFlow> houseFlowList = houseFlowMapper.selectByExample(example);
        List<Map<String,Object>> mapList = new ArrayList<Map<String,Object>>();
        for (HouseFlow hf : houseFlowList){
            Map<String,Object> map = new HashMap<String, Object>();
            map.put("name" , workerTypeMapper.selectByPrimaryKey(hf.getWorkerTypeId()).getName());
            map.put("image" , "");
            map.put("houseId" , hf.getWorkerTypeId());
            map.put("taskId", hf.getId());
            map.put("type", 4);
            mapList.add(map);
        }
        return ServerResponse.createBySuccess("查询成功", mapList);
    }
}
