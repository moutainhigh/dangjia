package com.dangjia.acg.controller.sup;

import com.dangjia.acg.api.sup.SupplierProductAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.sup.Supplier;
import com.dangjia.acg.service.sup.SupplierProductService;
import com.dangjia.acg.service.sup.SupplierService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName: SupplierProductController
 * @Description:
 * @author: zmj
 * @date: 2018-9-18下午3:19:58
 */
@RestController
public class SupplierProductController implements SupplierProductAPI {

    @Autowired
    private SupplierService supplierService;
    @Autowired
    private SupplierProductService supplierProductService;

    public Supplier getSupplier(String productId) {
        return supplierService.getSupplier(productId);
    }

    @Override
    @ApiMethod
    public ServerResponse byTelephone(String telephone) {
        return supplierService.byTelephone(telephone);
    }

    @Override
    @ApiMethod
    public ServerResponse supplierList(HttpServletRequest request, String productId) {
        return supplierProductService.supplierList(productId);
    }

    @Override
    @ApiMethod
    public ServerResponse insertSupplier(HttpServletRequest request, String name, String address,
                                         String telephone, String checkPeople, Integer gender,
                                         String email, String notice, Integer supplierLevel, Integer state) {

        return supplierService.insertSupplier(name, address, telephone, checkPeople, gender,
                email, notice, supplierLevel, state);
    }

    @Override
    @ApiMethod
    public ServerResponse updateSupplier(HttpServletRequest request, String id, String name, String address,
                                         String telephone, String checkPeople, Integer gender,
                                         String email, String notice, Integer supplierLevel, Integer state) {
        return supplierService.updateSupplier(id, name, address, telephone, checkPeople, gender,
                email, notice, supplierLevel, state);
    }

    @Override
    @ApiMethod
    public ServerResponse<PageInfo> querySupplierListLikeByName(HttpServletRequest request, PageDTO pageDTO, String name) {
        return supplierService.querySupplierListLikeByName(pageDTO, name);
    }

    @Override
    @ApiMethod
    public ServerResponse querySupplierProduct(HttpServletRequest request, int type, String supplierId,
                                               String likeProductName, PageDTO pageDTO) {
        return supplierService.querySupplierProduct(type, supplierId, likeProductName, pageDTO);
    }

    @Override
    @ApiMethod
    public ServerResponse saveSupplierProduct(String arrString) {
        return supplierService.saveSupplierProduct(arrString);
    }

    @Override
    @ApiMethod
    public ServerResponse querySupplierProductByPid(PageDTO pageDTO, String productId) {
        return supplierProductService.querySupplierProductByPid(pageDTO, productId);
    }
}
