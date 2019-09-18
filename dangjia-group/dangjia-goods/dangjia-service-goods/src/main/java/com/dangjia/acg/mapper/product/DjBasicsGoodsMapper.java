package com.dangjia.acg.mapper.product;

import com.dangjia.acg.modle.product.DjBasicsGoods;
import com.dangjia.acg.modle.product.DjBasicsProduct;
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
public interface DjBasicsGoodsMapper extends Mapper<DjBasicsGoods> {

    //查询某个分类的商品 模糊name（如果categoryId 为null，查询全部材料商品 ）
    List<DjBasicsGoods> queryGoodsListByCategoryLikeName(@Param("categoryId") String categoryId, @Param("name")String name);

    List<DjBasicsGoods> queryGoodsListByCategoryId(@Param("categoryId") String categoryId);

    DjBasicsGoods queryById(String id);

    //根据商品Id查货品
    List<DjBasicsProduct> queryByGoodsId(@Param("goodsId") String goodsId);

    List<DjBasicsGoods> queryByCategoryId(@Param("categoryId")String categoryId);
}
