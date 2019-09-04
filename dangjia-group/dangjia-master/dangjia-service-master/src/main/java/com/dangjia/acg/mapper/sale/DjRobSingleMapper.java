package com.dangjia.acg.mapper.sale;

import com.dangjia.acg.modle.sale.royalty.DjOrderSurface;
import com.dangjia.acg.modle.sale.royalty.DjRobSingle;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 抢单配置 DAO
 * Created with IntelliJ IDEA.
 * author: ljl
 * Date: 2019/8/14
 * Time: 10:01
 */
@Repository
public interface DjRobSingleMapper extends Mapper<DjRobSingle> {

    List<DjRobSingle> getRobDate(@Param("djOrderSurfaces") List<DjOrderSurface> djOrderSurfaces);

}
