package com.dangjia.acg.mapper.actuary;

import com.dangjia.acg.dto.actuary.app.ActuarialProductAppDTO;
import com.dangjia.acg.modle.actuary.BudgetMaterial;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 
 * 
   * @类 名： IBillBudgetMapper.java
   * @功能描述：
 */
@Repository
public interface IBillBudgetMapper extends Mapper<BudgetMaterial> {


}
