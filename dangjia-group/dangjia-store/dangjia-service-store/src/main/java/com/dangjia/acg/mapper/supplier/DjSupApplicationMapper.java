package com.dangjia.acg.mapper.supplier;

import com.dangjia.acg.modle.supplier.DjSupApplication;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 10/10/2019
 * Time: 下午 3:44
 */
@Repository
public interface DjSupApplicationMapper extends Mapper<DjSupApplication> {


    List<DjSupApplication> queryDjSupApplicationList(@Param("cityId") String cityId,
                                                     @Param("shopId") String shopId);
}
