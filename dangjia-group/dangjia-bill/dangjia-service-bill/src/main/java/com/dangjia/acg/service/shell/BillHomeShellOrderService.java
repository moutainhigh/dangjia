package com.dangjia.acg.service.shell;

import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.shell.HomeShellOrderDTO;
import com.dangjia.acg.dto.shell.HomeShellProductDTO;
import com.dangjia.acg.mapper.shell.IBillHomeShellOrderMapper;
import com.dangjia.acg.mapper.shell.IBillHomeShellProductMapper;
import com.dangjia.acg.modle.shell.HomeShellOrder;
import com.dangjia.acg.modle.shell.HomeShellProduct;
import com.dangjia.acg.util.StringTool;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: fzh
 * Date: 25/02/2020
 * Time: 下午 3:31
 */
@Service
public class BillHomeShellOrderService {
    protected static final Logger logger = LoggerFactory.getLogger(BillHomeShellOrderService.class);

    @Autowired
    private IBillHomeShellOrderMapper billHomeShellOrderMapper;
    @Autowired
    private IBillHomeShellProductMapper billHomeShellProductMapper;
    @Autowired
    private ConfigUtil configUtil;

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
            List<HomeShellOrderDTO>  shellOrderDTOList=billHomeShellOrderMapper.selectShellOrderList(exchangeClient,status,startTime,endTime,searchKey);
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
            HomeShellOrder homeShellOrder=billHomeShellOrderMapper.selectByPrimaryKey(homeOrderId);
            HomeShellOrderDTO homeShellOrderDTO=new HomeShellOrderDTO();
            BeanUtils.beanToBean(homeShellOrder,homeShellOrderDTO);
            homeShellOrderDTO.setShellOrderId(homeShellOrder.getId());
            String productId=homeShellOrder.getProuctId();
            HomeShellProduct homeShellProduct=billHomeShellProductMapper.selectByPrimaryKey(productId);
            homeShellOrderDTO.setProductName(homeShellProduct.getName());
            homeShellOrderDTO.setProductType(homeShellProduct.getProductType());
            homeShellOrderDTO.setProductSn(homeShellProduct.getProductSn());
            if(StringUtils.isNotBlank(homeShellOrder.getImage())){
                String address = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
                homeShellOrderDTO.setImageUrl(StringTool.getImage(homeShellOrder.getImage(),address));
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
     * @param status 2发货，5退货
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse updateOrderInfo(String homeOrderId,Integer status){
        if(StringUtils.isBlank(homeOrderId)){
            return ServerResponse.createByErrorMessage("兑换流水ID不能为空");
        }
        //修改对应的货单状态
        HomeShellOrder homeShellOrder=billHomeShellOrderMapper.selectByPrimaryKey(homeOrderId);
        homeShellOrder.setStatus(status);
        if(status==2){//发货
            homeShellOrder.setDeliverTime(new Date());
            billHomeShellOrderMapper.updateByPrimaryKeySelective(homeShellOrder);
        }else if(status==5){//退货
            homeShellOrder.setRefundTime(new Date());
            billHomeShellOrderMapper.updateByPrimaryKeySelective(homeShellOrder);
            //还原对应的金额及贝币给到用户或工匠
            //记录流水

        }
        return ServerResponse.createBySuccessMessage("提交成功");
    }



}
