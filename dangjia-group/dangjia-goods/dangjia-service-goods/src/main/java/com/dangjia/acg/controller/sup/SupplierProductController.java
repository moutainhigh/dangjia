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
    /**
     * 查询指定供应商
     */
    public Supplier getSupplier(String productId) {
        return supplierService.getSupplier(productId);
    }
    /**
     * 供应商登录
     */
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

    /**
     * @Description:新增供应商
     */
    @Override
    @ApiMethod
    public ServerResponse insertSupplier(HttpServletRequest request, String name, String address, String telephone, String checkPeople, Integer gender,
                                         String email, String notice, Integer supplierLevel, Integer state) {

        return supplierService.insertSupplier(name, address, telephone, checkPeople, gender,
                email, notice, supplierLevel, state);
    }

    /**
     * @throws
     * @Title: updateSupplier
     * @Description:修改供应商
     * @param: @return
     * @return: JsonResult
     */
    @Override
    @ApiMethod
    public ServerResponse updateSupplier(HttpServletRequest request, String id, String name, String address, String telephone, String checkPeople, Integer gender,
                                         String email, String notice, Integer supplierLevel, Integer state) {
        return supplierService.updateSupplier(id, name, address, telephone, checkPeople, gender,
                email, notice, supplierLevel, state);
    }

    /**
     * 查询所有供应商
     *
     * @param pageDTO
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse<PageInfo> querySupplierList(HttpServletRequest request, PageDTO pageDTO) {
        return supplierService.querySupplierList(pageDTO);
    }

    /**
     * 按照名字模糊查询所有供应商
     *
     * @param pageDTO
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse<PageInfo> querySupplierListLikeByName(HttpServletRequest request, PageDTO pageDTO, String name) {
        return supplierService.querySupplierListLikeByName(pageDTO, name);
    }

    /**
     * @throws
     * @Title: querySupplierList
     * @Description:查询所有货品供应关系0:仅供应货品;1:所有货品
     * @param: @return
     * @return: JsonResult
     */
    @Override
    @ApiMethod
    public ServerResponse querySupplierProduct(HttpServletRequest request, int type, String supplierId, String categoryId,String likeProductName, PageDTO pageDTO) {
        return supplierService.querySupplierProduct(type, supplierId, categoryId,likeProductName, pageDTO.getPageNum(), pageDTO.getPageSize());
    }

    /**
     * 保存供应商与货品供应关系
     *
     * @throws
     * @Title: saveSupplierProduct
     * @Description: TODO
     * @param: @param arrString
     * @param: @return
     * @return: JsonResult
     */
    @Override
    @ApiMethod
    public ServerResponse saveSupplierProduct(String arrString) {
        return supplierService.saveSupplierProduct(arrString);
    }

//    /**
//     * 根据货品查询相应供应商
//     *
//     * @param productId
//     * @return
//     */
//    @Override
//    @ApiMethod
//    public List<Map<String, Object>> querySupplierProductByPid(PageDTO pageDTO,String productId) {
//        return supplierProductService.querySupplierProductByPid(pageDTO,productId);
//    }

    /**
     * 根据货品查询相应供应商
     *
     * @param productId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse querySupplierProductByPid(PageDTO pageDTO,String productId) {
        return supplierProductService.querySupplierProductByPid(pageDTO,productId);
    }
}
