package com.dangjia.acg.mapper.product;

import com.dangjia.acg.modle.product.DjBasicsProductMaterial;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 材料商品扩展表
 * @author fzh
 */
@Repository
public interface DjBasicsProductMaterialMapper extends Mapper<DjBasicsProductMaterial> {


    /**
     * 根据商品ID查询商品扩展表信息
     * @param productId
     * @return
     */
    DjBasicsProductMaterial queryProductMaterialByProductId(@Param("productId") String productId);

}
