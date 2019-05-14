package com.dangjia.acg.mapper.deliver;

import com.dangjia.acg.modle.deliver.ProductChange;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * author: Yinjianbo
 * Date: 2019-5-11
 */
@Repository
public interface IProductChangeMapper extends Mapper<ProductChange> {

    /**根据houseId查询商品换货对象*/
    List<ProductChange> queryByHouseId(@Param("houseId") String houseId, @Param("type") String type);
}
