package com.dangjia.acg.mapper.core;

import com.dangjia.acg.dto.product.BasicsGoodDTO;
import com.dangjia.acg.modle.product.BasicsGoods;
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
public interface IMasterBasicsGoodsMapper extends Mapper<BasicsGoods> {

    List<BasicsGoodDTO> queryGoodsByType(@Param("categoryId") String categoryId,@Param("sourceType") Integer sourceType,
                                         @Param("storefrontId") String storefrontId,@Param("searchKey") String searchKey,@Param("cityId") String cityId);
}
