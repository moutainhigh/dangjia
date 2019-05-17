package com.dangjia.acg.controller.repair;

import com.dangjia.acg.api.repair.MendMaterielAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.repair.FillMaterielService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * author: Ronalcheng
 * Date: 2018/12/7 0007
 * Time: 10:41
 */
@RestController
public class MendMaterielController implements MendMaterielAPI {

    @Autowired
    private FillMaterielService fillMaterielService;


    @Override
    @ApiMethod
    public ServerResponse surplusList(String workerTypeId,String houseId){
        return fillMaterielService.surplusList(workerTypeId,houseId);
    }
    /**
     * 要退查询仓库
     * 结合 精算记录+补记录
     */
    @Override
    @ApiMethod
    public ServerResponse askAndQuit(String userToken, String houseId, String categoryId, String name) {
        return fillMaterielService.askAndQuit(userToken,houseId,categoryId,name);
    }

    @Override
    @ApiMethod
    public ServerResponse selectProduct(HttpServletRequest request,String goodsId,String selectVal,String attributeIdArr){
        return fillMaterielService.selectProduct(goodsId,selectVal,attributeIdArr);
    }

    @Override
    @ApiMethod
    public ServerResponse repairLibraryMaterial(String userToken,HttpServletRequest request, String categoryId,String name,PageDTO pageDTO){
        return fillMaterielService.repairLibraryMaterial(userToken,categoryId,name,pageDTO.getPageNum(),pageDTO.getPageSize());
    }

    /**
     * 工匠补退要货查询精算内货品
     */
    @Override
    @ApiMethod
    public ServerResponse workerTypeBudget(String userToken, String houseId, String categoryId, String name, PageDTO pageDTO){
        return fillMaterielService.workerTypeBudget(userToken,houseId,categoryId,name,pageDTO.getPageNum(),pageDTO.getPageSize());
    }
}
