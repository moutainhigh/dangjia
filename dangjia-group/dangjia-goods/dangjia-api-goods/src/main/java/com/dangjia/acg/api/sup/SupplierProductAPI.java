package com.dangjia.acg.api.sup;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 
 * @ClassName: SupplierProductController
 * @Description: 
 * @author: zmj
 * @date: 2018-9-18下午3:19:58
 */
@Api(description = "供应商管理接口")
@FeignClient("dangjia-service-goods")
public interface SupplierProductAPI {
    /**
     * 
     * @Title: insertSupplier
     * @Description:新增供应商
     * @param: @return   
     * @return: JsonResult   
     * @throws
     */
    @PostMapping("/sup/supplierProduct/insertSupplier")
    @ApiOperation(value = "新增供应商", notes = "新增供应商")
    public ServerResponse insertSupplier(String name, String address, String telephone, String checkPeople, Integer gender,
                                         String email, String notice, Integer supplierLevel, Integer state);
    
    /**
     * 
     * @Title: updateSupplier
     * @Description:修改供应商
     * @param: @return   
     * @return: JsonResult   
     * @throws
     */
    @PostMapping("/sup/supplierProduct/updateSupplier")
    @ApiOperation(value = "新增供应商", notes = "新增供应商")
    public ServerResponse updateSupplier(String id,String name,String address,String telephone,String checkPeople,Integer gender,
    		String email,String notice,Integer supplierLevel,Integer state);

    /**
     * 
     * @Title: querySupplierList
     * @Description:查询所有供应商
     * @param: @return   
     * @return: JsonResult   
     * @throws
     */
    @PostMapping("/sup/supplierProduct/querySupplierList")
    @ApiOperation(value = "查询所有供应商", notes = "查询所有供应商")
    public ServerResponse<PageInfo> querySupplierList(@RequestParam("pageDTO") PageDTO pageDTO);
    
    /**
     * 
     * @Title: querySupplierList
     * @Description:查询所有货品供应关系0:仅供应货品;1:所有货品
     * @param: @return   
     * @return: JsonResult   
     * @throws
     */
    @PostMapping("/sup/supplierProduct/querySupplierProduct")
    @ApiOperation(value = "查询所有货品供应关系", notes = "查询所有货品供应关系")
    public ServerResponse querySupplierProduct(int type,String supplierId,String categoryId);
    
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
     * @return: JsonResult   7
     * @throws
     */
    @PostMapping("/sup/supplierProduct/saveSupplierProduct")
    @ApiOperation(value = "保存供应商与货品供应关系", notes = "保存供应商与货品供应关系")
    public ServerResponse saveSupplierProduct(String productId,String supplierId,
			String attributeId,Double price,Double stock,Integer isSupply);
    
}
