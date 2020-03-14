package com.dangjia.acg.controller.shell;

import com.dangjia.acg.api.shell.HomeShellProductAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.shell.HomeShellProductDTO;
import com.dangjia.acg.service.shell.HomeShellProductService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * fzh
 * 2020-02-25
 */
@RestController
public class HomeShellProductController implements HomeShellProductAPI {
    protected static final Logger logger = LoggerFactory.getLogger(HomeShellProductController.class);

    @Autowired
    private HomeShellProductService billHomeShellProductService;
    /**
     * 当家贝商品列表
     * @param request
     * @param pageDTO 分页
     * @param productType 商品类型：1实物商品 2虚拟商品
     * @param searchKey 商品名称/编码
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse queryHomeShellProductList(HttpServletRequest request,PageDTO pageDTO,String productType,String searchKey){
        return billHomeShellProductService.queryHomeShellProductList(pageDTO,productType,searchKey);
    }

    /**
     * 商品详情
     * @param request
     * @param shellProductId 当家贝商品ID
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse queryHomeShellProductInfo(HttpServletRequest request,String shellProductId){
        return billHomeShellProductService.queryHomeShellProductInfo(shellProductId);
    }
    /**
     * 商品上下架
     * @param request
     * @param shellProductId 商品上下架
     * @param shelfStatus   上下架状态 1：上架  0:下架
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse updateHomeShellProductStatus( HttpServletRequest request,String shellProductId,String shelfStatus){
        return billHomeShellProductService.updateHomeShellProductStatus(shellProductId,shelfStatus);
    }
    /**
     * 添加修改商品
     * @param request
     * @param homeShellProductDTO 商品内容
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse editHomeShellProductInfo(HttpServletRequest request,HomeShellProductDTO homeShellProductDTO,String cityId){
        try{
            return billHomeShellProductService.editHomeShellProductInfo(homeShellProductDTO,cityId);
        }catch(Exception e){
            logger.error("保存失败");
            return ServerResponse.createBySuccessMessage("保存失败");
        }
    }

    /**
     * 删除商品(修改商品的状态为删除状态）
     * @param request
     * @param shellProductId 当家贝商品ID
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse deleteHomeShellProduct(HttpServletRequest request,String shellProductId){
        try{
            return billHomeShellProductService.deleteHomeShellProduct(shellProductId);
        }catch(Exception e){
            logger.error("删除失败");
            return ServerResponse.createBySuccessMessage("删除失败");
        }
    }

    /**
     * 当家贝商城
     * @param userToken
     * @param pageDTO 分页
     * @param productType 商品类型：1实物商品 2虚拟商品
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse serachShellProductList(String userToken,PageDTO pageDTO,String productType){
        return billHomeShellProductService.serachShellProductList(userToken,pageDTO,productType);
    }

    /**
     * 商品详情
     * @param userToken
     * @param shellProductId 当家贝商品ID
     * @param productSpecId 商品规格Id
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse searchShellProductInfo(String userToken,String shellProductId,String productSpecId){
        return billHomeShellProductService.searchShellProductInfo(userToken,shellProductId,productSpecId);
    }

}
