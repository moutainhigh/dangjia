package com.dangjia.acg.controller.supplier;

import com.dangjia.acg.api.supplier.DjRegisterApplicationAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.supplier.DjRegisterApplication;
import com.dangjia.acg.service.supplier.DjRegisterApplicationServices;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 8/10/2019
 * Time: 下午 2:19
 */
@RestController
public class DjRegisterApplicationController implements DjRegisterApplicationAPI {

    @Autowired
    private DjRegisterApplicationServices djSupplierServices;

    @Override
    @ApiMethod
    public ServerResponse registerSupAndStorefront(HttpServletRequest request, DjRegisterApplication djRegisterApplication) {
        return djSupplierServices.registerSupAndStorefront(djRegisterApplication);
    }

    @Override
    @ApiMethod
    public ServerResponse querySupAndStorefront(HttpServletRequest request, String mobile, String cityId) {
        return djSupplierServices.querySupAndStorefront(mobile, cityId);
    }
    @Override
    @ApiMethod
    public ServerResponse checkSupAndStorefront(HttpServletRequest request, String registerId, Integer isAdopt, String departmentId, String jobId,String failReason){
        return djSupplierServices.checkSupAndStorefront(request, registerId,isAdopt,departmentId,jobId,failReason);
    }

    /**
     * 查询已申请的供应商列表
     * @param request
     * @param pageDTO
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse<PageInfo> getRegisterList(HttpServletRequest request,PageDTO pageDTO,
                                                    String applicationStatus,String searchKey){
        return djSupplierServices.getRegisterList(pageDTO,applicationStatus,searchKey);
    }

    /**
     * 根据申请ID查询对应的申请信息
     * @param request
     * @param id
     * @return
     */

    @Override
    @ApiMethod
    public ServerResponse getRegisterInfoById(HttpServletRequest request,String id){
        return djSupplierServices.getRegisterInfoById(id);
    }

    @Override
    @ApiMethod
    public ServerResponse insertApplyNewStatus(HttpServletRequest request, String userId, DjRegisterApplication djRegisterApplication) {
        return djSupplierServices.insertApplyNewStatus(userId,djRegisterApplication);
    }


}
