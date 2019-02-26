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
    public ServerResponse selectProduct(HttpServletRequest request,String goodsId,String brandId,String brandSeriesId,String attributeIdArr){
        return fillMaterielService.selectProduct(goodsId,brandId,brandSeriesId,attributeIdArr);
    }

    @Override
    @ApiMethod
    public ServerResponse repairLibraryMaterial(HttpServletRequest request, String categoryId,String name,PageDTO pageDTO){
        return fillMaterielService.repairLibraryMaterial(categoryId,name,pageDTO.getPageNum(),pageDTO.getPageSize());
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
