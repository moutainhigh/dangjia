package com.dangjia.acg.service.shell;

import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.shell.HomeShellOrderDTO;
import com.dangjia.acg.dto.shell.HomeShellProductDTO;
import com.dangjia.acg.dto.shell.HomeShellProductSpecDTO;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.shell.IHomeShellOrderMapper;
import com.dangjia.acg.mapper.shell.IHomeShellProductMapper;
import com.dangjia.acg.mapper.shell.IHomeShellProductSpecMapper;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.shell.HomeShellOrder;
import com.dangjia.acg.modle.shell.HomeShellProduct;
import com.dangjia.acg.modle.shell.HomeShellProductSpec;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.dangjia.acg.util.StringTool;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mysql.fabric.Server;
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
public class HomeShellProductService {
    protected static final Logger logger = LoggerFactory.getLogger(HomeShellProductService.class);

    @Autowired
    private IHomeShellProductMapper homeShellProductMapper;
    @Autowired
    private IHomeShellProductSpecMapper productSpecMapper;
    @Autowired
    private IHomeShellOrderMapper shellOrderMapper;

    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private CraftsmanConstructionService constructionService;
    @Autowired
    private IMemberMapper memberMapper;

    /**
     * 当家贝商品列表
     * @param pageDTO 分页
     * @param productType 商品类型：1实物商品 2虚拟商品
     * @param searchKey 商品名称/编码
     * @return
     */
    public ServerResponse queryHomeShellProductList( PageDTO pageDTO, String productType, String searchKey){
        try{
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());//初始化分页插获取用户信息件
            List<HomeShellProductDTO> homeShellProductDTOList=homeShellProductMapper.queryHomeShellProductList(productType,searchKey);
            PageInfo pageInfo=new PageInfo(homeShellProductDTOList);
            return ServerResponse.createBySuccess("查询成功",pageInfo);
        }catch(Exception e){
            logger.error("查询失败",e);
            return ServerResponse.createBySuccessMessage("查询失败");
        }
    }

    /**
     * 商品详情
     * @param shellProductId 当家贝商品ID
     * @return
     */
    public ServerResponse queryHomeShellProductInfo(String shellProductId){
        try{
            if(shellProductId==null){
                return ServerResponse.createBySuccessMessage("商品ID不能为空");
            }
            HomeShellProduct homeShellProduct=homeShellProductMapper.selectByPrimaryKey(shellProductId);
            if(homeShellProduct==null){
                return  ServerResponse.createBySuccessMessage("未找到符合条件的数据");
            }
            String address = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            HomeShellProductDTO homeShellProductDTO= new HomeShellProductDTO();
            BeanUtils.beanToBean(homeShellProduct,homeShellProductDTO);
            homeShellProductDTO.setShellProductId(homeShellProduct.getId());
            homeShellProductDTO.setImageUrl(StringTool.getImage(homeShellProductDTO.getImage(),address));
            homeShellProductDTO.setDetailImageUrl(StringTool.getImage(homeShellProductDTO.getDetailImageUrl(),address));
            List<HomeShellProductSpecDTO> productSpecDTO=productSpecMapper.selectProductSpecByProductId(shellProductId);
            homeShellProductDTO.setProductSpecList(productSpecDTO);
            return ServerResponse.createBySuccess("查询成功",homeShellProductDTO);
        }catch(Exception e){
            logger.error("查询失败",e);
            return ServerResponse.createBySuccessMessage("查询失败");
        }
    }

    /**
     * 添加修改商品
     * @param homeShellProductDTO 商品内容
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse editHomeShellProductInfo( HomeShellProductDTO homeShellProductDTO,String cityId){
        String shellProductId=homeShellProductDTO.getShellProductId();
        //修改商品信息
        if(StringUtils.isNotBlank(shellProductId)){//修改
            HomeShellProduct homeShellProduct=homeShellProductMapper.selectByPrimaryKey(shellProductId);
            BeanUtils.beanToBean(homeShellProductDTO,homeShellProduct);
            homeShellProduct.setCityId(cityId);
            homeShellProductMapper.updateByPrimaryKeySelective(homeShellProduct);
        }else{
            HomeShellProduct homeShellProduct=new HomeShellProduct();
            BeanUtils.beanToBean(homeShellProductDTO,homeShellProduct);
            homeShellProduct.setCityId(cityId);
            homeShellProductMapper.insertSelective(homeShellProduct);
            shellProductId=homeShellProduct.getId();
        }
        //修改商品规格信息
        List<HomeShellProductSpecDTO> productSpecList=homeShellProductDTO.getProductSpecList();
        if(productSpecList!=null){
            for(HomeShellProductSpecDTO productSpecDTO:productSpecList){
                if(homeShellProductDTO.getPayType()==1){//如果支付类型选择了积分支付，则金钱自动改为0
                    productSpecDTO.setMoney(0d);
                }
                if(StringUtils.isNotBlank(productSpecDTO.getProductSpecId())){
                    //修改
                    HomeShellProductSpec productSpec=productSpecMapper.selectByPrimaryKey(productSpecDTO.getProductSpecId());
                    BeanUtils.beanToBean(productSpecDTO,productSpec);
                    productSpec.setProductId(shellProductId);
                    productSpecMapper.updateByPrimaryKeySelective(productSpec);
                }else{
                    //添加
                    HomeShellProductSpec productSpec=new HomeShellProductSpec();
                    BeanUtils.beanToBean(productSpecDTO,productSpec);
                    productSpec.setProductId(shellProductId);
                    productSpecMapper.insertSelective(productSpec);
                }
            }
        }
        return ServerResponse.createBySuccessMessage("保存成功");
    }

    /**
     * 删除商品(修改商品的状态为删除状态）
     * @param shellProductId 当家贝商品ID
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse deleteHomeShellProduct(String shellProductId){
        HomeShellProduct homeShellProduct=homeShellProductMapper.selectByPrimaryKey(shellProductId);
        homeShellProduct.setDataStatus(1);//删除当条数据
        homeShellProduct.setModifyDate(new Date());
        homeShellProductMapper.updateByPrimaryKey(homeShellProduct);
        return ServerResponse.createBySuccessMessage("删除成功");
    }

    /**
     * 当家贝商城
     * @param userToken
     * @param pageDTO 分页
     * @param productType 商品类型：1实物商品 2虚拟商品
     * @return
     */
    public ServerResponse serachShellProductList(String userToken,PageDTO pageDTO,String productType){
        try{
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());//初始化分页
            List<HomeShellProductDTO> homeShellProductDTOList=homeShellProductMapper.serachShellProductList(productType);
            if(homeShellProductDTOList==null){
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            }
            String address = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            for(HomeShellProductDTO homeShellProductDTO:homeShellProductDTOList){
                homeShellProductDTO.setImageUrl(StringTool.getImage(homeShellProductDTO.getImage(),address));
                HomeShellProductSpecDTO productSpecDTO=productSpecMapper.selectProductSpecInfo(homeShellProductDTO.getShellProductId());
                if(productSpecDTO!=null){
                    homeShellProductDTO.setProductSpecId(productSpecDTO.getProductSpecId());
                    homeShellProductDTO.setIntegral(productSpecDTO.getIntegral());
                    homeShellProductDTO.setMoney(productSpecDTO.getMoney());
                }
            }
            PageInfo pageInfo=new PageInfo(homeShellProductDTOList);
            return ServerResponse.createBySuccess("查询成功",pageInfo);
        }catch(Exception e){
            logger.error("查询失败",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 商品详情
     * @param userToken
     * @param shellProductId 当家贝商品ID
     * @param productSpecId 商品规格Id
     * @return
     */
    public ServerResponse searchShellProductInfo(String userToken,String shellProductId,String productSpecId){
        try{
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = (Member) object;
            if(shellProductId==null){
                return ServerResponse.createBySuccessMessage("商品ID不能为空");
            }
            HomeShellProduct homeShellProduct=homeShellProductMapper.selectByPrimaryKey(shellProductId);
            if(homeShellProduct==null){
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            }
            String address = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            HomeShellProductDTO homeShellProductDTO= new HomeShellProductDTO();
            BeanUtils.beanToBean(homeShellProduct,homeShellProductDTO);
            homeShellProductDTO.setShellProductId(homeShellProduct.getId());
            homeShellProductDTO.setImageUrl(StringTool.getImage(homeShellProductDTO.getImage(),address));
            homeShellProductDTO.setDetailImageUrl(StringTool.getImage(homeShellProductDTO.getDetailImage(),address));
            List<HomeShellProductSpecDTO> productSpecDTO=productSpecMapper.selectProductSpecByProductId(shellProductId);
            if(productSpecDTO!=null){
                for(HomeShellProductSpecDTO proSpec:productSpecDTO){
                    proSpec.setShowButton(getShowButton(homeShellProduct,member,proSpec.getStockNum(),proSpec.getIntegral()));
                }
            }
            homeShellProductDTO.setProductSpecList(productSpecDTO);//规格列表
            HomeShellProductSpec productSpec=productSpecMapper.selectByPrimaryKey(productSpecId);//当前选中规格信息
            if(productSpec!=null){
                homeShellProductDTO.setIntegral(productSpec.getIntegral());
                homeShellProductDTO.setMoney(productSpec.getMoney());
                homeShellProductDTO.setProductSpecId(productSpec.getId());
                homeShellProductDTO.setStockNum(productSpec.getStockNum());
                homeShellProductDTO.setShowButton(getShowButton(homeShellProduct,member,productSpec.getStockNum(),productSpec.getIntegral()));
            }
            //获取剩余时间
            if(homeShellProductDTO.getOpeningTimeLimit()==1) {
                Date newDate = DateUtil.addDateHours(homeShellProductDTO.getCreateDate(), homeShellProductDTO.getLimithours().intValue());
                Date nowDate = new Date(System.currentTimeMillis());
                homeShellProductDTO.setRemainingTime(DateUtil.daysBetweenTime(nowDate,newDate));
            }
            return ServerResponse.createBySuccess("查询成功",homeShellProductDTO);
        }catch (Exception e){
            logger.error("查询失败",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 显示按钮：1兑换按钮，2库存不足，3贝币不足，4超过兑换次数 5超过购买时间
     * @param shellProductDTO 商品信息
     * @param member 用户信息
     * @param stockNum 库存数量
     * @param integral 贝币
     * @return
     */
    public Integer getShowButton(HomeShellProduct shellProductDTO,Member member,Double stockNum,Double integral){
        Integer showButton=1;
        member=memberMapper.selectByPrimaryKey(member.getId());
        if(member.getShellMoney()==null){
            member.setShellMoney(new BigDecimal(0));
        }
        if(stockNum<=0){
            showButton=2;//库存不足
        }else if(member.getShellMoney().subtract(BigDecimal.valueOf(integral)).doubleValue()<=0){
            showButton=3;//贝币不足
        }else if(shellProductDTO.getLimitExchangeVolume()==1){
            //查询当前用户的已兑换量
            Integer num=shellOrderMapper.selectExchangeCount(member.getId(),shellProductDTO.getId());
            if(num>=shellProductDTO.getLimitExchangeVolume()){
                showButton=4;//超过兑换次数
            }
        }else if(shellProductDTO.getOpeningTimeLimit()==1){
            Date newDate=DateUtil.addDateHours(shellProductDTO.getCreateDate(),shellProductDTO.getLimithours().intValue());
            Date nowDate = new Date(System.currentTimeMillis());
            //判断是否超过购买时间
            if(nowDate.compareTo(newDate)>=0){
                showButton=5;//超过兑换次数
            }
        }
        return  showButton;
    }



}