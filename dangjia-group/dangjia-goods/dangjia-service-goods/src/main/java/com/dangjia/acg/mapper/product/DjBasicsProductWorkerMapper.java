package com.dangjia.acg.mapper.product;

import com.dangjia.acg.modle.product.DjBasicsProductWorker;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/**
 * 人工商品扩展表
 * @author fzh
 *
 */
@Repository
public interface DjBasicsProductWorkerMapper extends Mapper<DjBasicsProductWorker> {


    /**
     * 根据商品ID查询商品扩展表信息
     * @param productId
     * @return
     */
    DjBasicsProductWorker queryProductWorkerByProductId(@Param("productId") String productId);

}
