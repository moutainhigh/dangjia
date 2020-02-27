package com.dangjia.acg.service.shell;

import com.ctc.wstx.sw.EncodingXmlWriter;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.common.util.MathUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.refund.OrderProgressDTO;
import com.dangjia.acg.dto.shell.HomeShellOrderDTO;
import com.dangjia.acg.dto.worker.WorkIntegralDTO;
import com.dangjia.acg.mapper.IConfigMapper;
import com.dangjia.acg.mapper.member.IMasterMemberAddressMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.pay.IBusinessOrderMapper;
import com.dangjia.acg.mapper.shell.IHomeShellOrderMapper;
import com.dangjia.acg.mapper.shell.IHomeShellProductMapper;
import com.dangjia.acg.mapper.shell.IHomeShellProductSpecMapper;
import com.dangjia.acg.mapper.shell.IMasterOrderNodeMapper;
import com.dangjia.acg.mapper.worker.IWorkIntegralMapper;
import com.dangjia.acg.mapper.worker.IWorkerDetailMapper;
import com.dangjia.acg.model.Config;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.member.MemberAddress;
import com.dangjia.acg.modle.order.OrderNode;
import com.dangjia.acg.modle.pay.BusinessOrder;
import com.dangjia.acg.modle.shell.HomeShellOrder;
import com.dangjia.acg.modle.shell.HomeShellProduct;
import com.dangjia.acg.modle.shell.HomeShellProductSpec;
import com.dangjia.acg.modle.worker.WorkIntegral;
import com.dangjia.acg.modle.worker.WorkerDetail;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.dangjia.acg.util.StringTool;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.weaver.ast.Or;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * author: fzh
 * Date: 25/02/2020
 * Time: 下午 3:31
 */
@Service
public class HomeShellOrderService {
    protected static final Logger logger = LoggerFactory.getLogger(HomeShellOrderService.class);

    @Autowired
    private IHomeShellOrderMapper homeShellOrderMapper;
    @Autowired
    private IHomeShellProductMapper homeShellProductMapper;
    @Autowired
    private IMasterMemberAddressMapper memberAddressMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IMemberMapper memberMapper;
    @Autowired
    private IWorkerDetailMapper workerDetailMapper;
    @Autowired
    private IWorkIntegralMapper workIntegralMapper;
    @Autowired
    private IHomeShellProductSpecMapper homeShellProductSpecMapper;
    @Autowired
    private CraftsmanConstructionService constructionService;
    @Autowired
    private IBusinessOrderMapper businessOrderMapper;
    @Autowired
    private HomeShellProductService homeShellProductService;
    @Autowired
    private IConfigMapper configMapper;
    @Autowired
    private IMasterOrderNodeMapper masterOrderNodeMapper;

    /**
     * 查询兑换记录列表
     * @param pageDTO 分页
     * @param exchangeClient 客户端：-1全部，1业主端，2工匠端
     * @param status 查询状态：-1全部，1待发货，2待收货，3已收货，4待退款，5已退款
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param searchKey 兑换人姓名/电话/单号
     * @return
     */
    public ServerResponse queryOrderInfoList(PageDTO pageDTO,Integer exchangeClient,Integer status, Date startTime, Date endTime, String searchKey){
        try{
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());//初始化分页插获取用户信息件
            List<HomeShellOrderDTO>  shellOrderDTOList=homeShellOrderMapper.selectShellOrderList(exchangeClient,status,startTime,endTime,searchKey);
            PageInfo pageInfo=new PageInfo(shellOrderDTOList);
            return ServerResponse.createBySuccess("查询成功",pageInfo);
        }catch(Exception e){
            logger.error("查询失败");
            return ServerResponse.createBySuccessMessage("查询失败");
        }
    }

    /**
     * 查询兑换详情
     * @param homeOrderId 兑换记录ID
     * @return
     */
    public ServerResponse queryOrderInfoDetail( String homeOrderId){
        try{
            if(homeOrderId==null){
                return ServerResponse.createByErrorMessage("兑换记录ID为空");
            }
            HomeShellOrder homeShellOrder=homeShellOrderMapper.selectByPrimaryKey(homeOrderId);
            HomeShellOrderDTO homeShellOrderDTO=new HomeShellOrderDTO();
            BeanUtils.beanToBean(homeShellOrder,homeShellOrderDTO);
            homeShellOrderDTO.setShellOrderId(homeShellOrder.getId());
            String productId=homeShellOrder.getProuctId();
            HomeShellProduct homeShellProduct=homeShellProductMapper.selectByPrimaryKey(productId);
            homeShellOrderDTO.setProductName(homeShellProduct.getName());
            homeShellOrderDTO.setProductType(homeShellProduct.getProductType());
            homeShellOrderDTO.setProductSn(homeShellProduct.getProductSn());
            if(StringUtils.isNotBlank(homeShellOrder.getImage())){
                String address = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
                homeShellOrderDTO.setImageUrl(StringTool.getImage(homeShellOrder.getImage(),address));
            }
            MemberAddress memberAddress=memberAddressMapper.selectByPrimaryKey(homeShellOrder.getAddressId());
            homeShellOrderDTO.setReveiveMemberName(memberAddress.getName());
            homeShellOrderDTO.setReveiveMemberMobile(memberAddress.getMobile());
            HomeShellProductSpec productSpec=homeShellProductSpecMapper.selectByPrimaryKey(homeShellOrder.getProductSpecId());
            if(productSpec!=null){
                homeShellOrderDTO.setProductSpecName(productSpec.getName());
            }
            return ServerResponse.createBySuccess("查询成功",homeShellOrderDTO);
        }catch (Exception e){
            logger.error("查询失败",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }

    }

    /**
     * 修改订单状态
     * @param homeOrderId 兑换记录ID
     * @param status 类型：2发货，5退货，8确认发放
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse updateOrderInfo(String homeOrderId,Integer status){
        if(StringUtils.isBlank(homeOrderId)){
            return ServerResponse.createByErrorMessage("兑换流水ID不能为空");
        }
        //修改对应的货单状态
        HomeShellOrder homeShellOrder=homeShellOrderMapper.selectByPrimaryKey(homeOrderId);
        if(homeShellOrder.getStatus()!=1&&homeShellOrder.getStatus()!=4||homeShellOrder.getStatus()!=7){
            return ServerResponse.createBySuccess("此单处理，请勿重复操作");
        }
        homeShellOrder.setStatus(status);
        if(status==2||status==8){//发货/发放
            homeShellOrder.setDeliverTime(new Date());
            homeShellOrderMapper.updateByPrimaryKeySelective(homeShellOrder);
        }else if(status==5){//退货
            homeShellOrder.setRefundTime(new Date());
            homeShellOrderMapper.updateByPrimaryKeySelective(homeShellOrder);
            //还原贝币
            HomeShellProductSpec shellProductSpec=homeShellProductSpecMapper.selectByPrimaryKey(homeShellOrder.getProductSpecId());
            shellProductSpec.setStockNum(shellProductSpec.getStockNum()+1);
            homeShellProductSpecMapper.updateByPrimaryKey(shellProductSpec);
            HomeShellProduct homeShellProduct=homeShellProductMapper.selectByPrimaryKey(homeShellOrder.getProuctId());
            Member member=memberMapper.selectByPrimaryKey(homeShellOrder.getMemberId());
            //还原对应的金额及贝币给到用户或工匠
            if(homeShellOrder.getMoney()!=null&&homeShellOrder.getMoney()>0){
                //退钱给业主
                settleMemberMoney(member,homeShellOrder.getId(),homeShellOrder.getMoney());
            }else if(homeShellOrder.getIntegral()!=null&&homeShellOrder.getIntegral()>0){
                settleIntegral(member,homeShellOrder.getId(),"兑换退款："+homeShellProduct.getName(),homeShellOrder.getIntegral(),2);
            }
        }
        return ServerResponse.createBySuccessMessage("提交成功");
    }

    public void settleMemberMoney(Member member,String orderId,Double money){

        /*退钱给业主*/
        BigDecimal haveMoney = member.getHaveMoney().add(new BigDecimal(money));
        BigDecimal surplusMoney = member.getSurplusMoney().add(new BigDecimal(money));
        //记录流水
        WorkerDetail workerDetail = new WorkerDetail();
        workerDetail.setName("当家贝商品退货退款");
        workerDetail.setWorkerId(member.getId());
        workerDetail.setWorkerName(CommonUtil.isEmpty(member.getName()) ? member.getNickName() : member.getName());
        workerDetail.setDefinedWorkerId(orderId);
        workerDetail.setDefinedName("当家贝商品退货退款");
        workerDetail.setMoney(new BigDecimal(money));
        workerDetail.setApplyMoney(new BigDecimal(money));
        workerDetail.setWalletMoney(surplusMoney);
        workerDetail.setState(2);//进钱//业主退
        workerDetailMapper.insert(workerDetail);

        member.setHaveMoney(haveMoney);
        member.setSurplusMoney(surplusMoney);
        memberMapper.updateByPrimaryKeySelective(member);
    }

    /**
     *
     * @param member
     * @param orderId
     * @param money
     * @param type 获取类型：0评价获取，1充值获取，2兑换退款，3兑换商品
     */
    public void settleIntegral(Member member,String orderId,String remark,Double money,Integer type){
        WorkIntegral workIntegral = new WorkIntegral();
        workIntegral.setAnyBusinessId(orderId);
        workIntegral.setStatus(3);
        workIntegral.setWorkerId(member.getId());
        workIntegral.setBriefed(remark);
        //加/减积分流水
        workIntegral.setIntegral(BigDecimal.valueOf(money));
        workIntegral.setIntegralType(type);
        workIntegralMapper.insert(workIntegral);
        //用户当家币修改
        member.setShellMoney(member.getShellMoney().add(BigDecimal.valueOf(money)));
        member.setModifyDate(new Date());
        memberMapper.updateByPrimaryKeySelective(member);
    }


    /**
     * 当家贝商城--商品兑换提交
     * @param userToken 用户token
     * @param addressId 地址ID
     * @param productSpecId 商品规格ID
     * @param exchangeNum 商品数量
     * @param userRole 提交服务端：1为业主应用，2为工匠应用，3为销售应用
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse saveConvertedCommodities(String userToken,String addressId,String productSpecId,Integer exchangeNum,Integer userRole,String cityId){
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member member = (Member) object;
        if(productSpecId==null){
            return ServerResponse.createByErrorMessage("商品规格ID不能为空");
        }
        HomeShellProductSpec productSpec=homeShellProductSpecMapper.selectByPrimaryKey(productSpecId);
        if(productSpec==null){
            return ServerResponse.createByErrorMessage("请传入正确的商品规格ID");
        }
        HomeShellProduct product=homeShellProductMapper.selectByPrimaryKey(productSpec.getProductId());
        if("1".equals(product.getProductType())&&StringUtils.isBlank(addressId)){
            return ServerResponse.createByErrorMessage("实物商品兑换，地址不能为空");
        }
        if(exchangeNum==null)
            exchangeNum=0;
        //判断当前时间是否可兑换 1兑换按钮，2库存不足，3贝币不足，4超过兑换次数 5超过购买时间
        Integer showButton=homeShellProductService.getShowButton(product,member,productSpec.getStockNum(),productSpec.getIntegral());
        if(showButton==2){
            return ServerResponse.createByErrorMessage("库存不足");
        }else if(showButton==3){
            return ServerResponse.createByErrorMessage("贝币不足");
        }else if(showButton==4){
            return ServerResponse.createByErrorMessage("超过兑换次数");
        }else if(showButton==5){
            return ServerResponse.createByErrorMessage("超过兑换时间");
        }
        //生成兑换订单
        HomeShellOrder homeShellOrder=new HomeShellOrder();
        homeShellOrder.setNumber("DS"+System.currentTimeMillis()+ (int) (Math.random() * 9000 + 1000));
        //扣减库存
        productSpec.setStockNum(MathUtil.sub(productSpec.getStockNum(),1));
        productSpec.setConvertedNumber(MathUtil.add(productSpec.getConvertedNumber(),1));
        productSpec.setModifyDate(new Date());
        homeShellProductSpecMapper.updateByPrimaryKeySelective(productSpec);
        //已兑换数+1
        product.setConvertedNumber(MathUtil.add(product.getConvertedNumber(),1));
        product.setModifyDate(new Date());
        homeShellProductMapper.updateByPrimaryKeySelective(product);
        //扣减用户的当家贝币
        settleIntegral(member,homeShellOrder.getId(),"兑换商品："+product.getName(),productSpec.getIntegral()*(-1),3);//贝币流水记录
        String businessNum="";
        //判断是否需要生成支付订单
        if(product.getPayType()==2&&productSpec.getMoney()>0){//需要支付金钱的订单，则生成业务支付单号
            BusinessOrder businessOrder=new BusinessOrder();
            businessOrder.setMemberId(member.getId());
            businessOrder.setNumber(System.currentTimeMillis() + "-" + (int) (Math.random() * 9000 + 1000));
            businessOrder.setState(1);//刚生成
            businessOrder.setTotalPrice(BigDecimal.valueOf(productSpec.getMoney()));
            businessOrder.setDiscountsPrice(new BigDecimal(0));
            businessOrder.setPayPrice(BigDecimal.valueOf(productSpec.getMoney()));
            businessOrder.setType(11);//记录支付类型任务类型
            businessOrderMapper.insert(businessOrder);
            businessNum=businessOrder.getNumber();
            homeShellOrder.setStatus(0);//待支付
            homeShellOrder.setMoney(productSpec.getMoney());
            homeShellOrder.setBusinessOrderNumber(businessNum);
        }else{
            if("2".equals(product.getProductType())){//虚拟商品
                homeShellOrder.setStatus(7);//待发放
            }else{
                homeShellOrder.setStatus(1);//待发货
            }
            homeShellOrder.setMoney(0d);
        }
        homeShellOrder.setProductSpecId(productSpecId);
        homeShellOrder.setProuctId(product.getId());
        homeShellOrder.setExchangeClient(userRole);//1业主端，2工匠端
        homeShellOrder.setExchangeTime(new Date());
        homeShellOrder.setAddressId(addressId);
        MemberAddress memberAddress=memberAddressMapper.selectByPrimaryKey(addressId);
        homeShellOrder.setAddress(memberAddress.getAddress());
        homeShellOrder.setMemberId(member.getId());
        homeShellOrder.setMemberName(member.getName());
        homeShellOrder.setMemberMobile(member.getMobile());
        homeShellOrder.setExchangeNum(Double.valueOf(exchangeNum));
        homeShellOrder.setIntegral(productSpec.getIntegral());
        homeShellOrder.setCityId(cityId);
        homeShellOrderMapper.insertSelective(homeShellOrder);
        return ServerResponse.createBySuccess("兑换成功",businessNum);
    }

    //支付完成后
    public void updateShellOrderInfo(String businessOrderNum){
        Example example=new Example(HomeShellOrder.class);
        example.createCriteria().andEqualTo(HomeShellOrder.BUSINESS_ORDER_NUMBER,businessOrderNum);
        HomeShellOrder homeShellOrder=homeShellOrderMapper.selectOneByExample(example);
        homeShellOrder.setStatus(1);
        HomeShellProduct product=homeShellProductMapper.selectByPrimaryKey(homeShellOrder.getProuctId());
        if("2".equals(product.getProductType())){//虚拟商品
            homeShellOrder.setStatus(7);//待发放
        }else{
            homeShellOrder.setStatus(1);//待发货
        }
        homeShellOrder.setModifyDate(new Date());
        homeShellOrderMapper.updateByPrimaryKeySelective(homeShellOrder);
    }

    /**
     * 当家贝商城--兑换记录
     * @param userToken 用户token
     * @return
     */
    public ServerResponse searchShellProductInfo(String userToken,PageDTO pageDTO){
        try{
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = (Member) object;
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());//初始化分页插获取用户信息件
            List<HomeShellOrderDTO>  shellOrderDTOList=homeShellOrderMapper.searchShellProductInfo(member.getId());
            if(shellOrderDTOList==null){
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            }
            String address = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            for(HomeShellOrderDTO orderDTO:shellOrderDTOList){
                orderDTO.setImageUrl(StringTool.getImage(orderDTO.getImage(),address));
            }
            PageInfo pageInfo=new PageInfo(shellOrderDTOList);
            return ServerResponse.createBySuccess("查询成功",pageInfo);
        }catch (Exception e){
            logger.error("查询失败",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 当家贝商城--当家贝明细
     * @param userToken
     * @return
     */
    public ServerResponse searchShellMoneyList(String userToken,PageDTO pageDTO){
        try{
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = (Member) object;
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());//初始化分页插获取用户信息件
            List<WorkIntegralDTO> workIntegralList=workIntegralMapper.queryWorkerIntegerList(member.getId());
            PageInfo pageInfo=new PageInfo(workIntegralList);
            //查询所有汇总，收入支出的当家贝币
            Map<String,Object> map=workIntegralMapper.getTotalShellMoney(member.getId());
            if(map==null){
                map =new HashMap<>();
                map.put("incomeIntegral",0);
                map.put("expendIntegral",0);
            }
            //查询用户的当家贝币
            member=memberMapper.selectByPrimaryKey(member.getId());
            map.put("shellMoney",member.getShellMoney());
            map.put("workIntegralList",pageInfo);
            return ServerResponse.createBySuccess("查询成功",map);
        }catch (Exception e){
            logger.error("查询失败",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 当家贝商城--兑换详情
     * @param userToken
     * @param  shellOrderId 兑换记录ID
     * @return
     */
    public ServerResponse searchConvertedProductInfo(String userToken,String shellOrderId){
        try{
            if(shellOrderId==null){
                return ServerResponse.createByErrorMessage("兑换记录ID为空");
            }
            HomeShellOrder homeShellOrder=homeShellOrderMapper.selectByPrimaryKey(shellOrderId);
            HomeShellOrderDTO homeShellOrderDTO=new HomeShellOrderDTO();
            BeanUtils.beanToBean(homeShellOrder,homeShellOrderDTO);
            homeShellOrderDTO.setShellOrderId(homeShellOrder.getId());
            String productId=homeShellOrder.getProuctId();
            HomeShellProduct homeShellProduct=homeShellProductMapper.selectByPrimaryKey(productId);
            homeShellOrderDTO.setProductName(homeShellProduct.getName());
            homeShellOrderDTO.setProductType(homeShellProduct.getProductType());
            homeShellOrderDTO.setProductSn(homeShellProduct.getProductSn());
            if(StringUtils.isNotBlank(homeShellProduct.getImage())){
                String address = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
                homeShellOrderDTO.setImageUrl(StringTool.getImage(homeShellProduct.getImage(),address));
            }
            MemberAddress memberAddress=memberAddressMapper.selectByPrimaryKey(homeShellOrder.getAddressId());
            homeShellOrderDTO.setReveiveMemberName(memberAddress.getName());
            homeShellOrderDTO.setReveiveMemberMobile(memberAddress.getMobile());
            HomeShellProductSpec productSpec=homeShellProductSpecMapper.selectByPrimaryKey(homeShellOrder.getProductSpecId());
            if(productSpec!=null){
                homeShellOrderDTO.setProductSpecName(productSpec.getName());
            }
            homeShellOrderDTO.setRefundButton(0);//不显示退款按钮
            homeShellOrderDTO.setShowButton(0);//不显示
            if(homeShellOrderDTO.getProductType()!=null&&"1".equals(homeShellOrderDTO.getProductType())){//只有实物商品才显示下面的按钮
                //判断是否可退款
                if(homeShellOrderDTO.getStatus()==1){
                    homeShellOrderDTO.setRefundButton(1);//显示取消按钮
                }else if(homeShellOrderDTO.getStatus()==3){
                    homeShellOrderDTO.setRefundButton(2);//显示退款按钮
                }

                //判断前端按钮显示
                if(homeShellOrderDTO.getStatus()==0){
                    homeShellOrderDTO.setShowButton(1);//去支付
                }else if(homeShellOrder.getStatus()==2){
                    homeShellOrderDTO.setShowButton(2);//确认收货
                    //剩余处理时间（默认倒时计7天）
                    homeShellOrderDTO.setRemainingTime(getRemainingTime("SHELL_PRODUCT_RECIEVE_TIME",homeShellOrder.getDeliverTime()));
                }else if(homeShellOrder.getStatus()==4){//待退款
                    //剩余处理时间（默认倒时计7天）
                    homeShellOrderDTO.setRemainingTime(getRemainingTime("SHELL_PRODUCT_REFUND_TIME",homeShellOrder.getDeliverTime()));
                }else if(homeShellOrder.getStatus()==5){
                    //显示退款时间
                    homeShellOrderDTO.setRefundTime(homeShellOrder.getRefundTime());
                }
                homeShellOrderDTO.setStatusName(getStatusName(homeShellOrderDTO.getStatus()));//订单状态名称
            }else{//虚拟商品
                if(homeShellOrder.getStatus()==0){
                    homeShellOrderDTO.setStatusName("待付款");
                }else{
                    homeShellOrderDTO.setStatusName("兑换成功");
                }
            }

            return ServerResponse.createBySuccess("查询成功",homeShellOrderDTO);
        }catch(Exception e){
            logger.error("查询失败",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 获取当前阶段剩余可处理时间
     * @return
     */
    private long getRemainingTime(String paramKey, Date createDate){
        try{
            Config config=configMapper.selectConfigInfoByParamKey(paramKey);//获取对应阶段需处理剩余时间
            if(config!=null&&StringUtils.isNotBlank(config.getId())){
                String day=config.getParamValue();
                Date newDate= DateUtil.addDateDays(createDate,Integer.parseInt(day));
                return DateUtil.daysBetweenTime(new Date(),newDate);
            }
        }catch (Exception e){
            logger.error("获取剩余时间异常:",e);
        }

        return 0;
    }

    private String getStatusName(Integer status){
        String statusName="";
        switch (status){
            case 0 :
                statusName="待付款";
                break;
            case 1:
                statusName="待发货";
                break;
            case 2:
                statusName="待收货";
                break;
            case 3:
                statusName="兑换成功";
                break;
            case 4:
                statusName="等待商家处理";
                break;
            case 5:
            case 6:
                statusName="兑换关闭";
                break;
            default:
                break;
        }
        return statusName;
    }


    /**
     * 当家贝商城--确认收货/撤销退款申请
     * @param userToken
     * @param shellOrderId 兑换记录ID
     * @param type 类型：1确认收货，2撤销退款
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse updateConvertedProductInfo(String userToken,String shellOrderId,Integer type){
        if(shellOrderId==null){
            return ServerResponse.createByErrorMessage("兑换记录ID为空");
        }
        //修改对应的货单状态
        HomeShellOrder homeShellOrder=homeShellOrderMapper.selectByPrimaryKey(shellOrderId);
        if(type==1&&homeShellOrder.getStatus()!=2){
            return ServerResponse.createBySuccess("订单不符合确认收货条件，不能确认收货");
        }else if(type==2&&homeShellOrder.getStatus()!=4){
            return ServerResponse.createBySuccess("订单不符合确撤销条件，不能撤销退款申请");
        }
        if(type==1){//确认收货
            homeShellOrder.setStatus(3);
            homeShellOrder.setReveiveTime(new Date());//收货时间
            homeShellOrder.setModifyDate(new Date());
            homeShellOrderMapper.updateByPrimaryKeySelective(homeShellOrder);
        }else if(type==2){//撤销申请
            homeShellOrder.setStatus(3);
            homeShellOrder.setCancelApplicationTime(new Date());//撤销退款申请
            homeShellOrder.setModifyDate(new Date());
            homeShellOrderMapper.updateByPrimaryKeySelective(homeShellOrder);
        }
        return ServerResponse.createBySuccessMessage("提交成功");
    }

    /**
     * 当家贝商城--取消订单/申请退款
     * @param userToken
     * @param shellOrderId 兑换记录ID
     * @param image 相关凭证
     * @param type 申请类型：1取消订单，2申请退款
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse refundConvertedProductInfo(String userToken,String shellOrderId,String image,Integer type){
        if(shellOrderId==null){
            return ServerResponse.createByErrorMessage("兑换记录ID为空");
        }
        //修改对应的货单状态
        HomeShellOrder homeShellOrder=homeShellOrderMapper.selectByPrimaryKey(shellOrderId);
        if(type==2&&homeShellOrder.getStatus()!=3){
            return ServerResponse.createBySuccess("订单不符合退款条件，不能申请退款");
        }else if(type==1&&homeShellOrder.getStatus()!=1){
            return ServerResponse.createBySuccess("订单不符合取消条件，不能申请取消");
        }
        if(type==1){//取消订单
            homeShellOrder.setStatus(6);
            homeShellOrder.setCancelApplicationTime(new Date());//申请取消时间
            homeShellOrder.setImage(image);
            homeShellOrder.setModifyDate(new Date());
            homeShellOrderMapper.updateByPrimaryKeySelective(homeShellOrder) ;
            //退还当家贝币和金钱
            //还原贝币
            HomeShellProductSpec shellProductSpec=homeShellProductSpecMapper.selectByPrimaryKey(homeShellOrder.getProductSpecId());
            shellProductSpec.setStockNum(shellProductSpec.getStockNum()+1);
            homeShellProductSpecMapper.updateByPrimaryKey(shellProductSpec);
            HomeShellProduct homeShellProduct=homeShellProductMapper.selectByPrimaryKey(homeShellOrder.getProuctId());
            Member member=memberMapper.selectByPrimaryKey(homeShellOrder.getMemberId());
            //还原对应的金额及贝币给到用户或工匠
            if(homeShellOrder.getMoney()!=null&&homeShellOrder.getMoney()>0){
                //退钱给业主
                settleMemberMoney(member,homeShellOrder.getId(),homeShellOrder.getMoney());
            }else if(homeShellOrder.getIntegral()!=null&&homeShellOrder.getIntegral()>0){
                settleIntegral(member,homeShellOrder.getId(),"取消订单:"+homeShellProduct.getName(),homeShellOrder.getIntegral(),2);
            }
        }else if(type==2){//申请退款
            homeShellOrder.setStatus(4);
            homeShellOrder.setReturnApplicationTime(new Date());//申请退款时间
            homeShellOrder.setImage(image);
            homeShellOrder.setModifyDate(new Date());
            homeShellOrderMapper.updateByPrimaryKeySelective(homeShellOrder) ;
        }

        return ServerResponse.createBySuccessMessage("申请成功");
    }

    /**
     * 当家贝商城，充值页面
     * @param userToken
     * @return
     */
    public ServerResponse searchShellRechargeInfo(String userToken){
        try{
            Map<String,Object> paramMap=new HashMap<>();
            Config config=configMapper.selectConfigInfoByParamKey("SHELL_RECHARGE_REMARK");
            if(config!=null){
                paramMap.put("remark",config.getParamDesc());
            }else{
                paramMap.put("remark","1.当家贝可用于兑换相应的物品；\n" +
                        "\n" +
                        "2.当家贝获取规则：充值；特定方法获取；\n" +
                        "\n" +
                        "3.当家贝为虚拟币，不会过期，不可赠送、提现、转让。");
            }
            List rechargeList=new ArrayList();
            Map rechargeMap;
            Example example=new Example(OrderNode.class);
            example.createCriteria().andEqualTo(OrderNode.TYPE,"SHELL_MONEY_NODE");
            example.orderBy(OrderNode.SORT).asc();
            List<OrderNode> orderNodeList=masterOrderNodeMapper.selectByExample(example);
            if(orderNodeList!=null){
                for(OrderNode orderNode:orderNodeList){
                    rechargeMap=new HashMap();
                    rechargeMap.put("id",orderNode.getId());
                    rechargeMap.put("money",orderNode.getCode());
                    rechargeMap.put("integral",orderNode.getName());
                    rechargeList.add(rechargeMap);
                }
            }
            paramMap.put("rechargeList",rechargeList);

            return ServerResponse.createBySuccess("查询成功",paramMap);
        }catch (Exception e){
            logger.error("查询失败",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 当家贝商城--支付充值（生成支付单)
     * @param userToken
     * @param id 充值记录ID
     * @param cityId 城市ID
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse saveShellRechargeInfo(String userToken,String id,String cityId){
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member member = (Member) object;
        if(id==null){
            return ServerResponse.createByErrorMessage("请传入要充值的记录ID");
        }
        OrderNode orderNode=masterOrderNodeMapper.selectByPrimaryKey(id);
        if(orderNode==null){
            return ServerResponse.createByErrorMessage("未找到符合条件的充值记录信息");
        }
        //生成业务支付单
        BusinessOrder businessOrder=new BusinessOrder();
        businessOrder.setMemberId(member.getId());
        businessOrder.setNumber(System.currentTimeMillis() + "-" + (int) (Math.random() * 9000 + 1000));
        businessOrder.setState(1);//刚生成
        businessOrder.setTotalPrice(BigDecimal.valueOf(Double.parseDouble(orderNode.getCode())));
        businessOrder.setDiscountsPrice(new BigDecimal(0));
        businessOrder.setPayPrice(BigDecimal.valueOf(Double.parseDouble(orderNode.getCode())));
        businessOrder.setType(12);//记录支付类型任务类型
        businessOrder.setAnyOrderId(id);//充值单ID
        businessOrderMapper.insert(businessOrder);
        return  ServerResponse.createBySuccess("提交成功",businessOrder.getNumber());
    }

    /**
     * 充值成功，记录充值流水，将当家币汇总到用户帐号上去
     * @param businessOrder
     */
    public void saveShellMoney(BusinessOrder businessOrder){
        OrderNode orderNode=masterOrderNodeMapper.selectByPrimaryKey(businessOrder.getAnyOrderId());
        if(orderNode!=null){
            Double integral=Double.parseDouble(orderNode.getName());//当家贝币s
            String memberId=businessOrder.getMemberId();
            Member member=memberMapper.selectByPrimaryKey(memberId);
            //将当家币放到用户帐户上去
            settleIntegral(member,businessOrder.getId(),"充值获取",integral,1);
        }

    }

}
