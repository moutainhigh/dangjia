package com.dangjia.acg.mapper.product;

import com.dangjia.acg.modle.product.DjBasicsMaintain;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/7/25
 * Time: 13:56
 */
@Repository
public interface DjBasicsMaintainMapper extends Mapper<DjBasicsMaintain> {

    List<DjBasicsMaintain> duplicateRemoval(@Param("id") String id,
                                            @Param("cityId") String cityId,
                                            @Param("searchItem") String searchItem);
}
