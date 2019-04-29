package com.dangjia.acg.controller.basics;

import com.dangjia.acg.api.basics.BrandAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.brand.Brand;
import com.dangjia.acg.service.basics.BrandService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @类 名： BrandController
 */
@RestController
public class BrandController implements BrandAPI {

    @Autowired
    private BrandService brandService;

    /**
     * 根据id找到品牌
     *
     * @param brandId
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse<Brand> selectBrandById(HttpServletRequest request, String brandId) {
        return brandService.select(brandId);
    }

    /**
     * 根据名称找到品牌
     *
     * @param pageDTO
     * @param name
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse<PageInfo> selectBrandByName(HttpServletRequest request, PageDTO pageDTO, String name) {
        return brandService.getBrandByName(pageDTO, name);
    }

    /**
     * 查找到所有品牌
     *
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse<PageInfo> getAllBrand(HttpServletRequest request, PageDTO pageDTO) {
        return brandService.getAllBrand(pageDTO);

    }

    /**
     * 修改品牌信息
     *
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse updateBrand(HttpServletRequest request, String id, String name, String brandSeriesList) {
        return brandService.update(id, name, brandSeriesList);

    }

    /**
     * 添加品牌
     *
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse insertBrand(HttpServletRequest request, String brandSeriesList, String name) {
        return brandService.insert(brandSeriesList, name);

    }

    /**
     * 删除品牌
     *
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse deleteBrand(HttpServletRequest request, String id) {
        return brandService.deleteBrand(id);
    }
}
