package com.dangjia.acg.mapper.product;

import com.dangjia.acg.modle.activity.DjStoreActivityProduct;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;


/**
 * Created with IntelliJ IDEA.
 * author: wkpp/feedback/addFeedbackInFopp/feedback/addFeedbackInFo
 * Date: 2020/2/19
 * Time: 10:59
 */
@Repository
public interface IGoodsDjStoreActivityProductMapper extends Mapper<DjStoreActivityProduct> {

    DjStoreActivityProduct queryDjStoreActivityProductByProductId(@Param("productId") String productId);

    DjStoreActivityProduct queryDjStoreActivityProductByProductId1(@Param("productId") String productId);
}
