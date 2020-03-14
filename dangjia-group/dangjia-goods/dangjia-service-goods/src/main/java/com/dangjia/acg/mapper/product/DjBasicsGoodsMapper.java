package com.dangjia.acg.mapper.product;

import com.dangjia.acg.modle.product.BasicsGoods;
import com.dangjia.acg.modle.product.DjBasicsProductTemplate;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/9/12
 * Time: 9:54
 */
@Repository
public interface DjBasicsGoodsMapper extends Mapper<BasicsGoods> {

    //查询某个分类的商品 模糊name（如果categoryId 为null，查询全部材料商品 ）
    List<BasicsGoods> queryGoodsListByCategoryLikeName(@Param("categoryId") String categoryId,
                                                         @Param("name")String name,
                                                         @Param("cityId")String cityId);

    BasicsGoods queryById(String id);

    //根据商品Id查货品
    List<DjBasicsProductTemplate> queryByGoodsId(@Param("goodsId") String goodsId);

    List<BasicsGoods> queryByCategoryId(@Param("categoryId")String categoryId,@Param("cityId")String cityId);

    String queryGoodsLabels(@Param("goodsId") String goodsId);
}
