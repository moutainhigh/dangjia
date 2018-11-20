package com.dangjia.acg.controller.sup;

import com.dangjia.acg.api.sup.SupplierProductAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.sup.SupplierService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * 
 * @ClassName: SupplierProductController
 * @Description: 
 * @author: zmj
 * @date: 2018-9-18下午3:19:58
 */
@RestController
public class SupplierProductController  implements SupplierProductAPI {
    /**
     *service
     */
    @Autowired
    private SupplierService supplierService;
    /**
     * 
     * @Title: insertSupplier
     * @Description:新增供应商
     * @param: @return   
     * @return: JsonResult   
     * @throws
     */
    @Override
    @ApiMethod
    public ServerResponse insertSupplier(String name, String address, String telephone, String checkPeople, Integer gender,
                                         String email, String notice, Integer supplierLevel, Integer state){

        return supplierService.insertSupplier( name, address, telephone, checkPeople, gender,
        		 email, notice, supplierLevel, state);
    }
    
    /**
     * 
     * @Title: updateSupplier
     * @Description:修改供应商
     * @param: @return   
     * @return: JsonResult   
     * @throws
     */
    @Override
    @ApiMethod
    public ServerResponse updateSupplier(String id,String name,String address,String telephone,String checkPeople,Integer gender,
    		String email,String notice,Integer supplierLevel,Integer state){
        return supplierService.updateSupplier(id, name, address, telephone, checkPeople, gender,
        		 email, notice, supplierLevel, state);
    }

    /**
     * 查询所有供应商
     * @param pageDTO
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse<PageInfo> querySupplierList(PageDTO pageDTO){
        return supplierService.querySupplierList(pageDTO.getPageNum(),pageDTO.getPageSize());
    }
    
    /**
     * 
     * @Title: querySupplierList
     * @Description:查询所有货品供应关系0:仅供应货品;1:所有货品
     * @param: @return   
     * @return: JsonResult   
     * @throws
     */
    @Override
    @ApiMethod
    public ServerResponse querySupplierProduct(int type,String supplierId,String categoryId){
        return supplierService.querySupplierProduct(type,supplierId,categoryId);
    }
    
    /**
     * 保存供应商与货品供应关系
     * @Title: saveSupplierProduct
     * @Description: TODO
     * @param: @param product_id
     * @param: @param supplier_id
     * @param: @param attribute_id
     * @param: @param price
     * @param: @param stock
     * @param: @param is_supply
     * @param: @return   
     * @return: JsonResult   
     * @throws
     */
    @Override
    @ApiMethod
    public ServerResponse saveSupplierProduct(String productId,String supplierId,
			String attributeId,Double price,Double stock,Integer isSupply){
        return supplierService.saveSupplierProduct( productId, supplierId,
    			  price, stock, isSupply);
    }
    
}
