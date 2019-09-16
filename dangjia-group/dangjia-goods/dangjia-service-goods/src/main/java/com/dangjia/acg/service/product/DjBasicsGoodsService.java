package com.dangjia.acg.service.product;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.mapper.product.DjBasicsGoodsMapper;
import com.dangjia.acg.mapper.product.DjBasicsProductMapper;
import com.dangjia.acg.modle.product.DjBasicsGoods;
import com.dangjia.acg.modle.product.DjBasicsProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;


/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/9/12
 * Time: 9:54
 */
@Service
public class DjBasicsGoodsService {
    @Autowired
    private DjBasicsGoodsMapper djBasicsGoodsMapper;
    @Autowired
    private DjBasicsProductMapper djBasicsProductMapper;

    /**
     * 货品打标签
     * @param goodsId
     * @param labels
     * @return
     */
    public ServerResponse addLabels(String goodsId, String labels) {
        DjBasicsGoods djBasicsGoods=new DjBasicsGoods();
        djBasicsGoods.setId(goodsId);
        djBasicsGoods.setLabelIds(labels);
        djBasicsGoodsMapper.updateByPrimaryKeySelective(djBasicsGoods);
        Example example=new Example(DjBasicsProduct.class);
        example.createCriteria().andEqualTo(DjBasicsProduct.GOODS_ID,goodsId);
        DjBasicsProduct djBasicsProduct=new DjBasicsProduct();
        djBasicsProduct.setLabelId(labels);
        djBasicsProductMapper.updateByExampleSelective(djBasicsProduct,example);
        return ServerResponse.createBySuccessMessage("货品打标签成功");
    }
}
