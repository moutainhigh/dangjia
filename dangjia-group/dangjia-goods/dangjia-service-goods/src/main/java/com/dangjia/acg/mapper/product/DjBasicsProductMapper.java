package com.dangjia.acg.mapper.product;

import com.dangjia.acg.modle.basics.Product;
import com.dangjia.acg.modle.product.DjBasicsProduct;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 产品dao
 * author: LJL
 * Date: 2019/7/25
 * Time: 13:56
 */
@Repository
public interface DjBasicsProductMapper extends Mapper<DjBasicsProduct> {

    /**
     * 查询是否添加过同属性的商品
     * @param valueIdArr
     * @return
     */
    List<DjBasicsProduct> getPListByValueIdArr(@Param("valueIdArr") String valueIdArr);

    /**
     * 根据名称查询商品
     * @param name
     * @return
     */
    List<DjBasicsProduct> queryByName(@Param("name") String name);

    /**
     * 根据编号查询商品
     * @param productSn
     * @return
     */
    List<DjBasicsProduct> queryByProductSn(@Param("productSn") String productSn);

}
