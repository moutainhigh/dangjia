package com.dangjia.acg.controller.repair;

import com.dangjia.acg.api.repair.MendMaterielAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
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
    private FillMaterielService mendMaterielService;

    @Override
    @ApiMethod
    public ServerResponse selectProduct(HttpServletRequest request,String goodsId,String brandSeriesId,String attributeIdArr){
        return mendMaterielService.selectProduct(goodsId,brandSeriesId,attributeIdArr);
    }

    @Override
    @ApiMethod
    public ServerResponse repairLibraryMaterial(HttpServletRequest request, String categoryId,String name,Integer pageNum,Integer pageSize){
        return mendMaterielService.repairLibraryMaterial(categoryId,name,pageNum,pageSize);
    }
}
