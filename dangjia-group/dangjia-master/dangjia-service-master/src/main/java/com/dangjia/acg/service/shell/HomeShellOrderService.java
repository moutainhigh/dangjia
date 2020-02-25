package com.dangjia.acg.service.shell;

import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.shell.HomeShellOrderDTO;
import com.dangjia.acg.mapper.member.IMasterMemberAddressMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.shell.IHomeShellOrderMapper;
import com.dangjia.acg.mapper.shell.IHomeShellProductMapper;
import com.dangjia.acg.mapper.shell.IHomeShellProductSpecMapper;
import com.dangjia.acg.mapper.worker.IWorkIntegralMapper;
import com.dangjia.acg.mapper.worker.IWorkerDetailMapper;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.member.MemberAddress;
import com.dangjia.acg.modle.shell.HomeShellOrder;
import com.dangjia.acg.modle.shell.HomeShellProduct;
import com.dangjia.acg.modle.shell.HomeShellProductSpec;
import com.dangjia.acg.modle.worker.WorkIntegral;
import com.dangjia.acg.modle.worker.WorkerDetail;
import com.dangjia.acg.util.StringTool;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

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
            return ServerResponse.createBySuccess("查询成功",homeShellOrderDTO);
        }catch (Exception e){
            logger.error("查询失败",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }

    }

    /**
     * 修改订单状态
     * @param homeOrderId 兑换记录ID
     * @param status 2发货，5退货
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse updateOrderInfo(String homeOrderId,Integer status){
        if(StringUtils.isBlank(homeOrderId)){
            return ServerResponse.createByErrorMessage("兑换流水ID不能为空");
        }
        //修改对应的货单状态
        HomeShellOrder homeShellOrder=homeShellOrderMapper.selectByPrimaryKey(homeOrderId);
        if(homeShellOrder.getStatus()!=1&&homeShellOrder.getStatus()!=4){
            return ServerResponse.createBySuccess("此单处理，请勿重复操作");
        }
        homeShellOrder.setStatus(status);
        if(status==2){//发货
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
                settleIntegral(member,homeShellOrder.getId(),homeShellProduct.getName(),homeShellOrder.getMoney(),2);
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
        workIntegral.setStatus(0);
        workIntegral.setWorkerId(member.getId());
        workIntegral.setBriefed(remark);
        //加积分流水
        workIntegral.setIntegral(BigDecimal.valueOf(money));
        workIntegral.setIntegralType(type);
        workIntegralMapper.insert(workIntegral);
        if(type==2){
            member.setShellMoney(member.getSurplusMoney().add(BigDecimal.valueOf(money)));
            member.setModifyDate(new Date());
        }
        memberMapper.updateByPrimaryKeySelective(member);
    }


}
