package com.dangjia.acg.controller.product;

import com.dangjia.acg.api.product.DjBasicsProductAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.product.BasicsGoodsDTO;
import com.dangjia.acg.mapper.product.DjBasicsProductMapper;
import com.dangjia.acg.mapper.product.IBasicsGoodsMapper;
import com.dangjia.acg.modle.basics.Goods;
import com.dangjia.acg.modle.product.BasicsGoods;
import com.dangjia.acg.modle.product.DjBasicsProduct;
import com.dangjia.acg.service.product.DjBasicsProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    private DjBasicsProductMapper djBasicsProductMapper ;

    @Autowired
    private IBasicsGoodsMapper iBasicsGoodsMapper ;

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
    public ServerResponse queryDataByProductId(HttpServletRequest request, String productSn) {
        return djBasicsProductService.queryDataByProductId(request,productSn);
    }

    /**
     * 新增货品
     */
    @Override
    @ApiMethod
    public ServerResponse saveBasicsGoods(HttpServletRequest request, BasicsGoodsDTO basicsGoodsDTO) {
        return djBasicsProductService.saveBasicsGoods(basicsGoodsDTO);

    }
    /**
     * 新增商品
     */
    @Override
    @ApiMethod
    public ServerResponse insertProduct(HttpServletRequest request, String productArr) {
        try{
            return djBasicsProductService.insertProduct(productArr);
        }catch (Exception e){
            logger.error("新增商品信息失败：",e);
            return ServerResponse.createBySuccessMessage("新增失败");
        }

    }
}
