package com.dangjia.acg.controller.shell;

import com.dangjia.acg.api.delivery.BillAppointmentAPI;
import com.dangjia.acg.api.shell.BillHomeShellProductAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.controller.refund.RefundAfterSalesController;
import com.dangjia.acg.dto.shell.HomeShellProductDTO;
import com.dangjia.acg.service.delivery.BillAppointmentService;
import com.dangjia.acg.service.shell.BillHomeShellProductService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * fzh
 * 2020-02-25
 */
@RestController
public class BillHomeShellProductController implements BillHomeShellProductAPI {
    protected static final Logger logger = LoggerFactory.getLogger(BillHomeShellProductController.class);

    @Autowired
    private BillHomeShellProductService billHomeShellProductService;
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

}
