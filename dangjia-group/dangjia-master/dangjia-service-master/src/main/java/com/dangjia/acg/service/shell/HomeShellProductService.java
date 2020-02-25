package com.dangjia.acg.service.shell;

import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.shell.HomeShellProductDTO;
import com.dangjia.acg.mapper.shell.IHomeShellProductMapper;
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
    private IHomeShellProductMapper billHomeShellProductMapper;

    @Autowired
    private ConfigUtil configUtil;

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
            List<HomeShellProductDTO> homeShellProductDTOList=billHomeShellProductMapper.queryHomeShellProductList(productType,searchKey);
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
            HomeShellProduct homeShellProduct=billHomeShellProductMapper.selectByPrimaryKey(shellProductId);
            if(homeShellProduct==null){
                return  ServerResponse.createBySuccessMessage("未找到符合条件的数据");
            }
            String address = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            HomeShellProductDTO homeShellProductDTO= new HomeShellProductDTO();
            BeanUtils.beanToBean(homeShellProduct,homeShellProductDTO);
            homeShellProductDTO.setShellProductId(homeShellProduct.getId());
            homeShellProductDTO.setImageUrl(StringTool.getImage(homeShellProductDTO.getImage(),address));
            homeShellProductDTO.setDetailImageUrl(StringTool.getImage(homeShellProductDTO.getDetailImageUrl(),address));
            return ServerResponse.createBySuccess("查询成功",homeShellProductDTO);
        }catch(Exception e){
            logger.error("查询失败");
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

        if(StringUtils.isNotBlank(shellProductId)){//修改
            HomeShellProduct homeShellProduct=billHomeShellProductMapper.selectByPrimaryKey(shellProductId);
            BeanUtils.beanToBean(homeShellProductDTO,homeShellProduct);
            homeShellProduct.setCityId(cityId);
            billHomeShellProductMapper.updateByPrimaryKey(homeShellProduct);
        }else{
            HomeShellProduct homeShellProduct=new HomeShellProduct();
            BeanUtils.beanToBean(homeShellProductDTO,homeShellProduct);
            homeShellProduct.setCityId(cityId);
            billHomeShellProductMapper.insert(homeShellProduct);
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
        HomeShellProduct homeShellProduct=billHomeShellProductMapper.selectByPrimaryKey(shellProductId);
        homeShellProduct.setDataStatus(1);//删除当条数据
        homeShellProduct.setModifyDate(new Date());
        billHomeShellProductMapper.updateByPrimaryKey(homeShellProduct);
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
            List<HomeShellProductDTO> homeShellProductDTOList=billHomeShellProductMapper.serachShellProductList(productType);
            if(homeShellProductDTOList==null){
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            }
            String address = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            for(HomeShellProductDTO homeShellProductDTO:homeShellProductDTOList){
                homeShellProductDTO.setImageUrl(StringTool.getImage(homeShellProductDTO.getImage(),address));
            }
            PageInfo pageInfo=new PageInfo(homeShellProductDTOList);
            return ServerResponse.createBySuccess("查询成功",pageInfo);
        }catch(Exception e){
            logger.error("查询失败",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


}
