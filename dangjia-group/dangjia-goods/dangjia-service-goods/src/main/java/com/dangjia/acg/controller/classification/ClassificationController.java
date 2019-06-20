package com.dangjia.acg.controller.classification;

import com.dangjia.acg.api.classification.ClassificationAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.classification.ClassificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Ruking.Cheng
 * @descrilbe 分类模块
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/6/19 4:37 PM
 */
@RestController
public class ClassificationController implements ClassificationAPI {
    /**
     * service
     */
    @Autowired
    private ClassificationService classificationService;

    @Override
    @ApiMethod
    public ServerResponse getGoodsCategoryList(HttpServletRequest request) {
        return classificationService.getGoodsCategoryList();
    }

    @Override
    @ApiMethod
    public ServerResponse getProductList(HttpServletRequest request, PageDTO pageDTO, String categoryId) {
        return classificationService.getProductList(pageDTO, categoryId);
    }

    @Override
    @ApiMethod
    public ServerResponse getWorkerGoodsList(HttpServletRequest request, PageDTO pageDTO, String workerTypeId) {
        return classificationService.getWorkerGoodsList(pageDTO, workerTypeId);
    }
}
