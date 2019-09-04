package com.dangjia.acg.mapper.sale;

import com.dangjia.acg.modle.sale.residential.ResidentialRange;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/7/23
 * Time: 16:02
 */
@Repository
public interface ResidentialRangeMapper extends Mapper<ResidentialRange> {


   ResidentialRange selectSingleResidentialRange(@Param("buildingId") String buildingId);

}
