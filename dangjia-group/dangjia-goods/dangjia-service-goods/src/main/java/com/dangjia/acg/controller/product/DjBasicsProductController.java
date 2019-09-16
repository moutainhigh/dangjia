package com.dangjia.acg.controller.product;

import com.dangjia.acg.api.product.DjBasicsProductAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.product.BasicsGoodsDTO;
import com.dangjia.acg.dto.product.BasicsProductDTO;
import com.dangjia.acg.modle.product.DjBasicsProduct;
import com.dangjia.acg.service.product.DjBasicsGoodsService;
import com.dangjia.acg.service.product.DjBasicsProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


/**
 * 产品控制层
 * author: fzh
 * Date: 2019/9/15
 */
@RestController
public class DjBasicsProductController implements DjBasicsProductAPI {
    private static Logger logger = LoggerFactory.getLogger(DjBasicsProductService.class);

    @Autowired
    private DjBasicsProductService djBasicsProductService;

    @Autowired
    private DjBasicsGoodsService djBasicsGoodsService;

    @Override
    @ApiMethod
    public ServerResponse queryProductData(HttpServletRequest request, String name) {
        return djBasicsProductService.queryProductData(name);

    }

    @Override
    @ApiMethod
    public DjBasicsProduct queryProductDataByID(HttpServletRequest request, String id) {
        return djBasicsProductService.queryProductDataByID(request,id);
    }
    @Override
    @ApiMethod
    public ServerResponse queryProductLabels(HttpServletRequest request, String productId) {
        return djBasicsProductService.queryProductLabels(productId);
    }

    @Override
    @ApiMethod
    public ServerResponse addLabelsValue(HttpServletRequest request, String jsonStr) {
        return djBasicsProductService.addLabelsValue(jsonStr);
    }

    @Override
    @ApiMethod
    public ServerResponse queryDataByProductId(HttpServletRequest request, String productSn) {
        return djBasicsProductService.queryDataByProductId(request,productSn);
    }

    /**
     * 新增货品
     */
    @Override
    @ApiMethod
    public ServerResponse saveBasicsGoods(HttpServletRequest request, BasicsGoodsDTO basicsGoodsDTO) {
        return djBasicsGoodsService.saveBasicsGoods(basicsGoodsDTO);

    }

    /**
     * 修改货品
     * @param request
     * @param basicsGoodsDTO
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse updateBasicsGoods(HttpServletRequest request,BasicsGoodsDTO basicsGoodsDTO){
        return djBasicsGoodsService.updateBasicsGoods(basicsGoodsDTO);
    }
    /**
     * 新增商品
     */
    @Override
    @ApiMethod
    public ServerResponse insertBatchProduct(HttpServletRequest request, String productArr) {
        try{
            return djBasicsProductService.insertBatchProduct(productArr);
        }catch (Exception e){
            logger.error("新增商品信息失败：",e);
            return ServerResponse.createBySuccessMessage("新增失败");
        }

    }
    /**
     * 单个新增修改商品信息
     * @param request
     * @param basicsProductDTO
     * @param technologyList  添加工艺列表
     * @param deleteTechnologyIds 删除工艺列表的D
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse editSingleProduct(@RequestParam("request") HttpServletRequest request,
                                     BasicsProductDTO basicsProductDTO,
                                     @RequestParam("technologyList") String technologyList,
                                     @RequestParam("deleteTechnologyIds") String  deleteTechnologyIds){
        try{
            return djBasicsProductService.saveProductTemporaryStorage(basicsProductDTO, technologyList, deleteTechnologyIds,0);
        }catch (Exception e){
            logger.error("保存单个商品信息失败：",e);
            return ServerResponse.createBySuccessMessage("保存单个商品失败");
        }
    }

    /**
     * 暂存商品
     * @param request
     * @param basicsProductDTO
     * @param technologyList  添加工艺列表
     * @param deleteTechnologyIds 删除工艺列表的D
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse saveProductTemporaryStorage(HttpServletRequest request,
                                                      BasicsProductDTO basicsProductDTO, String technologyList, String  deleteTechnologyIds){
        try{
            return djBasicsProductService.saveProductTemporaryStorage(basicsProductDTO, technologyList, deleteTechnologyIds,2);
        }catch (Exception e){
            logger.error("保存商品信息失败：",e);
            return ServerResponse.createBySuccessMessage("保存商品失败");
        }
    }
}
