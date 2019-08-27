package com.dangjia.acg.mapper.sale;

import com.dangjia.acg.dto.sale.royalty.DjAreaMatchDTO;
import com.dangjia.acg.modle.sale.royalty.DjAreaMatch;
import com.dangjia.acg.modle.sale.royalty.DjAreaMatchSetup;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 提成楼栋配置模块
 * Created with IntelliJ IDEA.
 * author: ljl
 * Date: 2019/7/26
 * Time: 16:16
 */
@Repository
public interface DjAreaMatchMapper extends Mapper<DjAreaMatch> {


    List<DjAreaMatchDTO> commissionAllocation(Map<String,Object> map);


    DjAreaMatchDTO maxCommissionAllocation();

}
